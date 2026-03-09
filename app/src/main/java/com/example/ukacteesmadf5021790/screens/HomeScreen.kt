package com.example.ukacteesmadf5021790.screens


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onAddTask: () -> Unit,
    onOpenTask: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Home", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        Button(onClick = onAddTask) {
            Text("Add Task")
        }

        Spacer(Modifier.height(12.dp))

        Button(onClick = { onOpenTask(1) }) {
            Text("Open Sample Task")
        }
    }
}