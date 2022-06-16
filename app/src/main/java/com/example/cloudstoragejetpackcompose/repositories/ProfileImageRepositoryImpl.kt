package com.example.cloudstoragejetpackcompose.repositories

import android.net.Uri
import com.example.cloudstoragejetpackcompose.other.Constants.CREATED_AT
import com.example.cloudstoragejetpackcompose.other.Constants.PROFILE_IMAGE_NAME
import com.example.cloudstoragejetpackcompose.other.Constants.UID
import com.example.cloudstoragejetpackcompose.other.Constants.URL
import com.example.cloudstoragejetpackcompose.other.Response
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class ProfileImageRepositoryImpl(
    private val imagesStorageRed: StorageReference,
    private val imagesCollRef: CollectionReference
) : ProfileImageRepository {
    override suspend fun addImageToFirebaseStorage(imageUri: Uri): Flow<Response<Uri>> = flow {
        try {
            emit(Response.Loading)

            val downloadUrl = imagesStorageRed.child(PROFILE_IMAGE_NAME)
                .putFile(imageUri).await()
                .storage.downloadUrl.await()

            emit(Response.Success(downloadUrl))

        } catch (e: Exception) {
            emit(Response.Failure(e))
        }
    }

    override suspend fun addImageToFirestore(downloadUrl: Uri): Flow<Response<Boolean>> = flow {
        try {
            emit(Response.Loading)
            imagesCollRef.document(UID).set(
                mapOf(
                    URL to downloadUrl,
                    CREATED_AT to FieldValue.serverTimestamp()
                )
            ).await()

            emit(Response.Success(true))
        } catch (e: Exception) {
            emit(Response.Failure(e))
        }
    }

    override suspend fun getImageFromFirestore(): Flow<Response<String>> = flow {
        try {
            emit(Response.Loading)
            val url = imagesCollRef.document(UID).get().await().getString(URL)
            emit(Response.Success(url))
        } catch (e: Exception) {
            emit(Response.Failure(e))
        }
    }
}