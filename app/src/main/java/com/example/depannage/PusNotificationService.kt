package com.example.depannage

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PusNotificationService: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
    }
}