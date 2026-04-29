package com.example.ukacteesmadf5021790

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.ukacteesmadf5021790.screens.showNotification

// This receiver is triggered by AlarmManager when the reminder time arrives.
class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Task Reminder"
        val description = intent.getStringExtra("description") ?: "You have an unfinished task."

        showNotification(
            context,
            "Task Reminder",
            "$title: $description"
        )
    }
}