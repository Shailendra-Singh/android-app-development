package com.shail_singh.mrello.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class PermissionsManager(
    private val activity: Activity,
    permissionTypeName: String,
    private val permissions: List<String>
) {

    private var permissionsListener: IPermissionsCheckedListener? = null
    private val onPermissionsListener: OnPermissionsListener =
        OnPermissionsListener(permissionTypeName)

    interface IPermissionsCheckedListener {
        fun onPermissionsChecked()
    }

    fun requestPermissions(onPermissionsCheckedListener: IPermissionsCheckedListener) {
        this.permissionsListener = onPermissionsCheckedListener
        Dexter.withActivity(activity).withPermissions(this.permissions)
            .withListener(this.onPermissionsListener).onSameThread().check()
    }

    private inner class OnPermissionsListener(private val permissionName: String) :
        MultiplePermissionsListener {
        override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
            if (report!!.areAllPermissionsGranted()) {
                this@PermissionsManager.permissionsListener?.onPermissionsChecked()
            }

            if (report.isAnyPermissionPermanentlyDenied) {
                Toast.makeText(
                    activity,
                    "You have denied $permissionName permission. App cannot function without it.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        override fun onPermissionRationaleShouldBeShown(
            permissions: MutableList<PermissionRequest>?, token: PermissionToken?
        ) {
            this.showRationalDialogForPermissions(this.permissionName)
        }

        private fun showRationalDialogForPermissions(permissionName: String) {
            AlertDialog.Builder(activity).setMessage(
                "Unable to obtain $permissionName permissions.\n Grant required permissions from Settings"
            ).setPositiveButton("GO TO SETTINGS") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.fromParts("package", activity.packageName, null)
                    ContextCompat.startActivity(activity, intent, null)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }.setNegativeButton("CANCEL") { dialog, _ ->
                dialog.dismiss()
            }.show()
        }
    }
}