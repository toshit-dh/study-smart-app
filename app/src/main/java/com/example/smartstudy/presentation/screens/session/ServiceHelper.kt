package com.example.smartstudy.presentation.screens.session

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.example.smartstudy.MainActivity
import com.example.smartstudy.utils.Constants.CLICK_REQUEST_CODE

object ServiceHelper {
    fun triggerForegroundService(context: Context, action: String) {
        Intent(context, SessionTimerService::class.java).apply {
            this.action = action
            context.startService(this)
        }
    }

    fun clickPendingIntent(context: Context): PendingIntent {
        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW,
            "smart_study://dashboard/session".toUri(),
            context,
            MainActivity::class.java
        )
        return TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(deepLinkIntent)
            getPendingIntent(
                CLICK_REQUEST_CODE,
                PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}