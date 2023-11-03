package com.came.parkare.videoplayercompose.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.came.parkare.videoplayercompose.data.MediaDataReader
import com.came.parkare.videoplayercompose.models.VideoItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val metaDataReader: MediaDataReader,
    val player: Player
) : ViewModel() {
    private val videoUris = savedStateHandle.getStateFlow("videoUris", emptyList<Uri>())

    val videoItems = videoUris.map { uris ->
        uris.map { uri ->
            VideoItem(
                contentUri = uri,
                mediaItem = MediaItem.fromUri(uri),
                name = metaDataReader.getMetaDataFromUri(uri)?.fileName ?: "No name"
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        player.prepare()
        player.playWhenReady = true
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    player.seekToDefaultPosition(0)
                    player.seekTo(0)
                }
            }
        })
    }

    fun addVideoUri(uri: Uri){
        savedStateHandle["videoUris"] = videoUris.value + uri
        player.addMediaItem(MediaItem.fromUri(uri))
    }
    override fun onCleared() {
        super.onCleared()
        player.release()
    }

    fun getUriVideos():List<Uri> {
        val folderPath = "/storage/emulated/0/camevideos"
        val videoUris = mutableListOf<Uri>()

        val folder = File(folderPath)
        if (folder.isDirectory) {
            val files = folder.listFiles() // List the files in the folder

            for (file in files) {
                if(file.extension == "mp4"){ // Check if the file is a video
                    val fileUri: Uri = Uri.fromFile(file) // Get the URI for each file
                    videoUris.add(fileUri)
                }
            }
            savedStateHandle["videoUris"] = videoUris
        }
        return videoUris
    }
}