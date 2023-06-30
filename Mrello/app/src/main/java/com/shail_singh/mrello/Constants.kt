package com.shail_singh.mrello

object Constants {
    /* Collection */
    const val FIRESTORE_USER_COLLECTION_NAME: String = "users"
    const val FIRESTORE_BOARDS_COLLECTION_NAME: String = "boards"

    /* Hash Map keys */
    const val IMAGE: String = "image"
    const val NAME: String = "name"
    const val MOBILE: String = "mobile"
    const val EMAIL: String = "email"
    const val ID: String = "id"
    const val ASSIGNED_TO: String = "assignedTo"
    const val TASK_LIST: String = "taskList"
    const val BOARD_DETAIL: String = "boardDetail"
    const val TASK_LIST_ITEM_POSITION: String = "taskListItemPosition"
    const val CARD_LIST_ITEM_POSITION: String = "cardListItemPosition"
    const val BOARD_MEMBERS_LIST: String = "boardMembersList"
    const val FCM_TOKEN = "fcmToken"

    const val FCM_BASE_URL: String = "https://fcm.googleapis.com/"
    const val FCM_SERVER_END_POINT: String = "fcm/send"
    const val FCM_AUTHORIZATION: String = "authorization"
    const val FCM_SERVER_KEY: String = "com.googleapis.fcm.SERVER_PUBLIC_KEY"

    /* POST request properties*/
    const val CONTENT_TYPE: String = "Content-Type"
    const val APPLICATION_JSON: String = "application/json"

    /* Misc */
    const val MRELLO_SHARED_PREFERENCES = "mrello_shared_preferences"
    const val IS_FCM_TOKEN_UPDATED = "is_fcm_token_updated"

    /* Image */
    const val USER_IMAGE_PREFIX: String = "USER_IMAGE_"
    const val BOARD_IMAGE_PREFIX: String = "BOARD_IMAGE_"
}