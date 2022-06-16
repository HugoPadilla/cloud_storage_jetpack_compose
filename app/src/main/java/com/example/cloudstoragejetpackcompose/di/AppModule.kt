package com.example.cloudstoragejetpackcompose.di

import com.example.cloudstoragejetpackcompose.repositories.ProfileImageRepository
import com.example.cloudstoragejetpackcompose.repositories.ProfileImageRepositoryImpl
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @Provides
    @Singleton
    fun provideImagesStorageRed(storage: FirebaseStorage) = storage.reference.child("images")

    @Provides
    @Singleton
    fun provideCollectionReference(fire_store: FirebaseFirestore) = fire_store.collection("images")

    @Provides
    @Singleton
    fun provideProfileImageRepository(
        imagesStorage: StorageReference,
        imagesCollReference: CollectionReference
    ): ProfileImageRepository =
        ProfileImageRepositoryImpl(imagesStorage, imagesCollReference)
}