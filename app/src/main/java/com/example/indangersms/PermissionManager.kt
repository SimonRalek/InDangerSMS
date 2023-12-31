package com.example.indangersms

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object RequestCodes {
    const val READ_CONTACTS = 1
    const val SEND_SMS = 2
    const val ACCESS_C_LOCATION = 3
    const val ACCESS_F_LOCATION = 4
    const val POST_NOTIFICATION = 5
}

class PermissionManager(private val context: Context) {

    private var permissionIndex = 0
    private val permissionsToCheck = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(
            Pair(android.Manifest.permission.READ_CONTACTS, RequestCodes.READ_CONTACTS),
            Pair(android.Manifest.permission.ACCESS_FINE_LOCATION, RequestCodes.ACCESS_F_LOCATION),
            Pair(android.Manifest.permission.SEND_SMS, RequestCodes.SEND_SMS),
            Pair(android.Manifest.permission.POST_NOTIFICATIONS, RequestCodes.POST_NOTIFICATION)
        )
    } else {
        listOf(
            Pair(android.Manifest.permission.READ_CONTACTS, RequestCodes.READ_CONTACTS),
            Pair(android.Manifest.permission.ACCESS_FINE_LOCATION, RequestCodes.ACCESS_F_LOCATION),
            Pair(android.Manifest.permission.SEND_SMS, RequestCodes.SEND_SMS)
        )
    }

    fun checkPermissions() {
        checkNextPermission()
    }

    private fun checkNextPermission() {
        if (permissionIndex < permissionsToCheck.size) {
            val (permission, requestCode) = permissionsToCheck[permissionIndex]
            checkPermission(
                permission,
                requestCode,
                "Permission",
                "For this application to function properly please provide $permission permission"
            )
        }
    }

    private fun checkPermission(
        permission: String,
        requestCode: Int,
        title: String,
        message: String
    ) {
        if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    context as? MainActivity ?: return, permission
                )
            ) {
                showExplanation(title, message, permission, requestCode, false)
            } else {
                ActivityCompat.requestPermissions(
                    context as? MainActivity ?: return,
                    arrayOf(permission),
                    requestCode
                )
            }
        } else {
            permissionIndex++
            checkNextPermission()
        }
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (isDenied(grantResults)) {
            val (permission, _) = permissionsToCheck[permissionIndex]
            showExplanation(
                "Permission Denied",
                "You need to grant $permission permission for the app to work.",
                permission,
                requestCode,
                true
            )
        } else {
            permissionIndex++
            checkNextPermission()
        }
    }

    private fun showExplanation(
        title: String,
        message: String,
        permission: String,
        permissionRequestCode: Int,
        settings: Boolean
    ) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle(title)
            .setMessage(message)
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                {
                }
            }
            .setPositiveButton(android.R.string.ok) { _, _ ->
                if (settings) {
                    goToSettings()
                    permissionIndex += 1
                    checkNextPermission()
                } else {
                    requestPermission(permission, permissionRequestCode)
                }
            }
        builder.create().show()
    }

    private fun requestPermission(permissionName: String, permissionRequestCode: Int) {
        ActivityCompat.requestPermissions(
            context as? MainActivity ?: return,
            arrayOf(permissionName),
            permissionRequestCode
        )
    }

    private fun goToSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        context.startActivity(intent)
    }

    private fun isDenied(grantResults: IntArray): Boolean {
        return grantResults.isEmpty() || grantResults[0] == PackageManager.PERMISSION_DENIED
    }

    fun checkOutsidePermission(
        permission: String,
        title: String = "",
        message: String = "",
        alert: Boolean = true
    ): Boolean {
        val checked = ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED

        if (!checked && alert) showSimpleExplanation(title, message)

        return checked
    }

    private fun showSimpleExplanation(
        title: String,
        message: String,
    ) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                goToSettings()
            }
        builder.create().show()
    }
}
