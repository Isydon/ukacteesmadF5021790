package com.example.ukacteesmadf5021790.screens

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.ukacteesmadf5021790.ReminderReceiver
import com.example.ukacteesmadf5021790.model.Task
import java.util.Calendar

// This function creates and displays a notification for a selected task.
fun showNotification(context: Context, title: String, message: String) {
    val channelId = "task_channel"

    // Android 8+ requires a notification channel.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Task Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    // This builds the notification content.
    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)

    // This checks notification permission before showing notification.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context)
                .notify((0..1000).random(), builder.build())
        }
    } else {
        NotificationManagerCompat.from(context)
            .notify((0..1000).random(), builder.build())
    }
}

// This function opens a time picker and schedules a reminder notification.
fun scheduleTaskReminder(context: Context, task: Task) {
    val calendar = Calendar.getInstance()

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->

            // Set selected reminder time.
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            // If selected time has already passed today, schedule for tomorrow.
            if (calendar.timeInMillis <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }

            // Intent sent to ReminderReceiver when alarm time arrives.
            val intent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra("title", task.title)
                putExtra("description", task.description)
            }

            // PendingIntent allows AlarmManager to trigger the receiver later.
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                task.id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // Schedule exact reminder notification.
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        false
    )

    timePickerDialog.show()
}

// This screen displays full task details and actions.
@Composable
fun TaskDetailsScreen(
    task: Task,
    onBack: () -> Unit,
    onDelete: (Int) -> Unit,
    onEdit: (Int) -> Unit
) {
    val context = LocalContext.current

    // Controls delete confirmation dialog.
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Requests notification permission on Android 13+.
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            scheduleTaskReminder(context, task)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F1F1))
            .padding(20.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Task Details",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "View, share, set reminder or delete this task.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Task details card.
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Text(
                    text = "Title",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Description",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Share task with other apps.
        Button(
            onClick = {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "Task: ${task.title}\nDescription: ${task.description}"
                    )
                }

                context.startActivity(
                    Intent.createChooser(shareIntent, "Share Task")
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0D6EFD)
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Share Task", color = Color.White)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Opens time picker and schedules reminder notification.
        Button(
            onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        scheduleTaskReminder(context, task)
                    } else {
                        notificationPermissionLauncher.launch(
                            Manifest.permission.POST_NOTIFICATIONS
                        )
                    }
                } else {
                    scheduleTaskReminder(context, task)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF198754)
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Set Reminder", color = Color.White)
        }

        Spacer(modifier = Modifier.height(12.dp))



        Button(
            onClick = {
                onEdit(task.id)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0D6EFD)
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Edit Task", color = Color.White)
        }
        Spacer(modifier = Modifier.height(12.dp))
        // Opens delete confirmation dialog.
        Button(
            onClick = {
                showDeleteDialog = true
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFDC3545)
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Delete Task", color = Color.White)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Returns to previous screen.
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Back")
        }
    }

    // Delete confirmation dialog.
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },
            title = {
                Text("Delete Task?")
            },
            text = {
                Text("Are you sure you want to delete this task? This action cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onDelete(task.id)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFDC3545)
                    )
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}