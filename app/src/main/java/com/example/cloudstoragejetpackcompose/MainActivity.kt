package com.example.cloudstoragejetpackcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.cloudstoragejetpackcompose.other.Constants.ALL_IMAGES
import com.example.cloudstoragejetpackcompose.other.Response
import com.example.cloudstoragejetpackcompose.ui.theme.CloudStorageJetpackComposeTheme
import com.example.cloudstoragejetpackcompose.ui.viewModels.ProfileImageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProfileImageScreen()
        }
    }
}

@Composable
fun ProfileImageScreen(
    viewModel: ProfileImageViewModel = hiltViewModel()
) {
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { imageUrl ->
            imageUrl?.let {
                viewModel.addImageToStorage(imageUrl)
            }
        })

    Scaffold(scaffoldState = scaffoldState) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ProfileImageContent(
                openGallery = {
                    galleryLauncher.launch(ALL_IMAGES)
                }
            )
        }
    }

    when (val addImageToStorageResponse = viewModel.addImageToStorageState.value) {
        is Response.Failure -> LaunchedEffect(Unit) { print(addImageToStorageResponse.e) }
        is Response.Loading -> ProgressBar()
        is Response.Success -> {
            val isImageAddedToStorage = addImageToStorageResponse.data
            isImageAddedToStorage?.let { downloadUrl ->
                LaunchedEffect(key1 = Unit) {
                    viewModel.addImageToDatabaseState(downloadUrl)
                }
            }
        }
    }

    val message = LocalContext.current.getString(R.string.image_successfully_added_message)
    val action_label = LocalContext.current.getString(R.string.action_label)

    fun showSnackBar() = coroutineScope.launch {

        val result = scaffoldState.snackbarHostState.showSnackbar(
            message = message,
            actionLabel = action_label
        )
        if (result == SnackbarResult.ActionPerformed) {
            viewModel.getImageFromDatabase()
        }
    }

    when (val addImageToDatabaseResponse = viewModel.addImageToDatabaseState.value) {
        is Response.Failure -> LaunchedEffect(key1 = Unit) { print(addImageToDatabaseResponse.e) }
        is Response.Loading -> ProgressBar()
        is Response.Success -> {
            val isImageAddedToDatabase = addImageToDatabaseResponse.data
            isImageAddedToDatabase?.let {
                if (isImageAddedToDatabase) {
                    LaunchedEffect(key1 = isImageAddedToDatabase) {
                        showSnackBar()
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileImageContent(
    viewModel: ProfileImageViewModel = hiltViewModel(),
    openGallery: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 64.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Button(onClick = openGallery) {
            Text(
                text = LocalContext.current.getString(R.string.open_gallery),
                fontSize = 18.sp
            )
        }
    }

    when (val getImageFromDatabaseResponse = viewModel.getImageFromDatabaseState.value) {
        is Response.Failure -> LaunchedEffect(Unit) { print(getImageFromDatabaseResponse.e) }
        is Response.Loading -> ProgressBar()
        is Response.Success -> {
            val imageUrl = getImageFromDatabaseResponse.data
            imageUrl?.let {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(it)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .padding(top = 64.dp)
                            .clip(CircleShape)
                            .width(160.dp)
                            .height(160.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ProgressBar() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CloudStorageJetpackComposeTheme {
        ProfileImageScreen()
    }
}