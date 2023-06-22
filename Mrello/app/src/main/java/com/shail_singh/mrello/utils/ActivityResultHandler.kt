package com.shail_singh.mrello.utils

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.shail_singh.mrello.activities.BaseActivity

class ActivityResultHandler(activity: BaseActivity) {
    interface OnActivityResultListener {
        fun onActivityResult(customActivityCode: Int, result: ActivityResult?)
    }

    private var activityCode: Int = -1
    private var resultsLauncher: ActivityResultLauncher<Intent>
    private var onActivityResultListener: OnActivityResultListener? = null

    fun launchIntent(
        intent: Intent, onActivityResultListener: OnActivityResultListener, activityCode: Int
    ) {
        this.activityCode = activityCode
        this.onActivityResultListener = onActivityResultListener
        this.resultsLauncher.launch(intent)
    }

    init {
        this.resultsLauncher =
            activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                this.onActivityResultListener?.onActivityResult(activityCode, it)
            }
    }
}