package com.came.parkare.videoplayercompose.data.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.came.parkare.videoplayercompose.MainActivity

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val activityIntent = Intent(context, MainActivity::class.java)
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(activityIntent)
    }
}