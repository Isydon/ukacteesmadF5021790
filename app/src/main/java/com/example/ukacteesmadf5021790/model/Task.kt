package com.example.ukacteesmadf5021790.model

// This data class represents a task.
// Default values are required so Firebase Firestore can convert documents into Task objects.
data class Task(
    val id: Int = 0,
    val title: String = "",
    val description: String = ""
)