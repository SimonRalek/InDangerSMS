package com.example.indangersms

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.SmsManager
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices

class SenderProvider(private var context: Context, private var alert: Boolean = true) {

    fun sendSms(phoneNumber: String?, message: String?, callback: (Boolean) -> Unit) {
        val isPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PermissionManager(context).checkOutsidePermission(
                android.Manifest.permission.POST_NOTIFICATIONS,
                "Missing Notifications Permission",
                "To notify you about events within the app, we require permission to send notifications.",
                alert
            )
        } else {
            true
        }

        getLocation { result ->
            result?.let {
                val smsManager = context.getSystemService(SmsManager::class.java) as SmsManager
                smsManager.sendTextMessage(phoneNumber, null, message, null, null)
                smsManager.sendTextMessage(phoneNumber, null, result, null, null)
                if (isPermission) {
                    sendNotification("Message Sent", "Message was successfully sent!", context)
                } else {
                    Toast.makeText(context, "Message Sent", Toast.LENGTH_LONG).show()
                }
                callback(true)
            } ?: run {
                if (alert) {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                    builder.setTitle("Location Error")
                        .setMessage("Unable to retrieve location. Make sure location services are enabled.")
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                        }
                    builder.create().show()
                    callback(false)
                } else if (isPermission) {
                   sendNotification("Location Error", "Unable to retrieve location. Make sure location services are enabled.", context)
                }

            }
        }
    }

//    private fun getFullMessage(message: String?, callback: (String?) -> Unit) {
//        getLocation { result ->
//            result?.let {
//                val location = result
//                val fullMessage = "$message$location"
//                callback(fullMessage)
//            } ?: callback(null)
//        }
//    }

    fun getLocation(callback: (String?) -> Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val permManager = PermissionManager(context)

        if (permManager.checkOutsidePermission(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                "Missing Location Permission",
                "To send a SMS message with location, you need to permit the location permission",
                alert
            ) && permManager.checkOutsidePermission(
                android.Manifest.permission.SEND_SMS,
                "Missing Send SMS Permission",
                "To send a SMS message, you need to permit the SMS permission", alert
            )
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    location?.let {
                        val latitude = it.latitude
                        val longitude = it.longitude
                        val result = "https://www.google.com/maps/place/$latitude,$longitude"
                        callback(result)
                    }

                    if (location == null) {
                        callback(null)
                    }
                }
                .addOnFailureListener { exception ->
                    callback(null)
                }
        } else {
            callback(null)
        }
    }

    fun checkSettings(phoneNumber: String?, message: String?): Boolean {
        if (phoneNumber == null || phoneNumber == "") {
            alertBuilder(
                "Emergency contact isn't set",
                "Please set up the emergency contact before sending the message"
            )
            return false
        }

        if (message == null || message == "") {
            alertBuilder(
                "Message isn't set",
                "Please set up the message you want sent before sending the message"
            )
            return false
        }

        return true
    }

    fun alertBuilder(title: String, message: String) {
        if (alert) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK") { dialog, which ->
                    val intent = Intent(context, Settings::class.java)
                    context.startActivity(intent)
                }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }

    fun sendNotification(title: String, message: String, context: Context) {

        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "emergency_alerts"
            val channelName = "Emergency Alerts"
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "For notifications"
            }

            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java) // Replace YourMainActivity::class.java with the actual main activity of your app
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, "emergency_alerts")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.baseline_warning_amber_24)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(1, builder.build())

    }
}