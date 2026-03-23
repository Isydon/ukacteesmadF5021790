package com.example.ukacteesmadf5021790.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ukacteesmadf5021790.model.Task

@Composable
fun TaskDetailsScreen(
    task: Task,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Task Details", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(8.dp))
        Text("Title: ${task.title}")

        Spacer(modifier = Modifier.height(8.dp))
        Text("Description: ${task.description}")

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onBack) {
            Text("Back")
        }
    }
}