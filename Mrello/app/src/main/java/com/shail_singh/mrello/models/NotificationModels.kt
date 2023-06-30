package com.shail_singh.mrello.models

object NotificationModels {
    data class Notification(val title: String, val body: String)
    data class Message(val to: String, val notification: Notification)
}