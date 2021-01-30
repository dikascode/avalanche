package com.decagon.avalanche.firebase

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class OnBootBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val intent = Intent("com.demo.FirebaseMessagingReceiveService")
        intent.setClass(context, FirebaseService::class.java)
        context.startService(intent)
    }
}