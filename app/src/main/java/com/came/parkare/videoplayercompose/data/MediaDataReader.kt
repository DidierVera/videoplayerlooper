package com.came.parkare.videoplayercompose.data

import android.app.Application
import android.net.Uri
import android.provider.MediaStore
import javax.inject.Inject


data class MetaData(
    val fileName: String
)

interface MediaDataReader {
    fun getMetaDataFromUri(contentUri: Uri): MetaData?
}

class MetaDataReaderImpl @Inject constructor(
    private val app: Application
) : MediaDataReader {
    override fun getMetaDataFromUri(contentUri: Uri): MetaData? {
        if (contentUri.scheme != "content"){
            return null
        }
        val fileName = app.contentResolver
            .query(
                contentUri,
                arrayOf(MediaStore.Video.VideoColumns.DISPLAY_NAME),
                null,
                null,
                null
            )
            ?.use { cursor ->
                val index = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                cursor.getString(index)
            }
        return fileName?.let { fullFileName ->
            MetaData(fileName = Uri.parse(fullFileName).lastPathSegment ?: return null)
        }
    }
}