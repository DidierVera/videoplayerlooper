package com.came.parkare.videoplayercompose

import android.Manifest
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.came.parkare.videoplayercompose.ui.theme.VideoplayercomposeTheme
import com.came.parkare.videoplayercompose.ui.viewmodel.MainViewModel
import com.came.parkare.videoplayercompose.ui.views.MediaPlayerScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideStatusBar()
        setContent {
            VideoplayercomposeTheme {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_VIDEO)
                requestPermissionLauncher.launch(Manifest.permission.RECEIVE_BOOT_COMPLETED)
                MediaPlayerScreen(_onPermissionGranted = _onPermissionGranted)
            }
        }
    }

    private var permissionGranted = false

    private val _onPermissionGranted: () -> Unit = {
        permissionGranted = true
    }
    val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            _onPermissionGranted.invoke()
        } else {
            Toast.makeText(this, R.string.the_storage_permissions_is_necessary, Toast.LENGTH_LONG).show()
        }
    }

    private fun hideStatusBar() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
}