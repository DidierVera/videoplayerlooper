package com.came.parkare.videoplayercompose.ui.views

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.ui.PlayerView
import com.came.parkare.videoplayercompose.ui.viewmodel.MainViewModel

@Composable
fun MediaPlayerScreen(
    modifier: Modifier = Modifier.background(Color.Black),
    _onPermissionGranted: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
){
    val state = viewModel.state
    _onPermissionGranted.let {
        showVideos(viewModel)
    }

    if(state.errorMessageId != null) {
        Toast.makeText(LocalContext.current, state.errorMessageId, Toast.LENGTH_LONG).show()
    }


    var lifecycle by remember {
        mutableStateOf(Lifecycle.Event.ON_CREATE)
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner){
        val observer = LifecycleEventObserver {_, event ->
            lifecycle = event
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AndroidView(factory = { context ->
            PlayerView(context).also {
                it.player = viewModel.player
            }
        },
            update = {
                when(lifecycle){
                    Lifecycle.Event.ON_PAUSE -> {
                        it.onPause()
                    }
                    Lifecycle.Event.ON_RESUME -> {
                        it.onResume()
                    }
                    else -> Unit
                }
            },
            modifier = Modifier.fillMaxSize().background(Color.Black)
        )
    }
}
fun showVideos(viewModel: MainViewModel){
    val videoUris = viewModel.getUriVideos()
    videoUris.forEach { uri ->
        viewModel.addVideoUri(uri)
    }
}
