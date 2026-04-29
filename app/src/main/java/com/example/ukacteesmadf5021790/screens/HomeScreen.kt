package com.example.ukacteesmadf5021790.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ukacteesmadf5021790.model.Task
import com.example.ukacteesmadf5021790.storage.AuthStorage

@Composable
fun HomeScreen(
    tasks: List<Task>,
    onAddClick: () -> Unit,
    onTaskClick: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    onLogout: () -> Unit
) {

    // State for delete confirmation dialog
    var taskToDelete by remember { mutableStateOf<Task?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // 🔵 HEADER SECTION
        Text(
            text = "My Tasks",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 📋 TASK LIST
        if (tasks.isEmpty()) {
            Text(
                text = "No tasks yet. Click Add to create one.",
                color = Color.Gray
            )
        } else {
            tasks.forEach { task ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { onTaskClick(task) }, // Open details
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEDEDED))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Text(
                            text = task.title,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = task.description,
                            color = Color.DarkGray
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // 🔴 DELETE BUTTON
                        Button(
                            onClick = {
                                taskToDelete = task // Trigger dialog
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Delete", color = Color.White)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // ➕ ADD TASK BUTTON (CENTERED + LONG)
        Button(
            onClick = onAddClick,
            modifier = Modifier
                .fillMaxWidth(0.6f) // makes it centered smaller width
                .align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0D6EFD)
            )
        ) {
            Text("Add Task", color = Color.White)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 🚪 LOGOUT BUTTON
        OutlinedButton(
            onClick = {
                AuthStorage.logout()
                onLogout()
            },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("Logout")
        }
    }

    // ⚠️ DELETE CONFIRMATION DIALOG
    if (taskToDelete != null) {
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
            title = { Text("Delete Task") },
            text = { Text("Are you sure you want to delete this task?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteTask(taskToDelete!!)
                        taskToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { taskToDelete = null }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}