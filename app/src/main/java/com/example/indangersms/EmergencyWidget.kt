package com.example.indangersms

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.preference.PreferenceManager

class EmergencyWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_layout)

        val intent = Intent(context, EmergencyWidget::class.java)
        intent.action = "buttonClick"
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        views.setOnClickPendingIntent(
            R.id.widgetButton,
            getPendingSelfIntent(context, "buttonClick", appWidgetId)
        )

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val prefManager = PreferenceManager.getDefaultSharedPreferences(context)
        val permManager = PermissionManager(context)

        if (intent.action == "buttonClick") {
            val message = prefManager.getString("message", null) + " - "
            val contactNumber = prefManager.getString("contactNumber", null)
            val testMode = prefManager.getBoolean("test_mode", false)
            val senderProvider = SenderProvider(context, false)

            val isPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permManager.checkOutsidePermission(
                    android.Manifest.permission.POST_NOTIFICATIONS,
                    "",
                    "",
                    false
                )
            } else {
                true
            }

            if (senderProvider.checkSettings(contactNumber, message)) {
                if (testMode) {
                    if (isPermission) {
                        senderProvider.sendNotification(
                            "Message Not Sent",
                            "Message was unsuccesfully sent, please disable Test Mode before using the widget.",
                            context
                        )
                    }
                } else {
                    senderProvider.sendSms(contactNumber, message) { success ->
                        if (isPermission) {
                            if (!success) senderProvider.sendNotification(
                                "Message Not Sent",
                                "Please check you permitted location permissions and you have location service enabled.",
                                context
                            )
                        }
                    }

                }
            } else if (isPermission) {
                senderProvider.sendNotification(
                    "Message Not Sent",
                    "Please first set your emergency contact and message content.",
                    context
                )
            }
        }

        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )
        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }


    private fun getPendingSelfIntent(
        context: Context,
        action: String,
        appWidgetId: Int
    ): PendingIntent {
        val intent = Intent(context, EmergencyWidget::class.java)
        intent.action = action
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }
}
