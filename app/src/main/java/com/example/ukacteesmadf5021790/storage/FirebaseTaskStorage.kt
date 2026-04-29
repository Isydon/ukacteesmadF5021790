package com.example.ukacteesmadf5021790.storage

import com.example.ukacteesmadf5021790.model.Task
import com.google.firebase.firestore.FirebaseFirestore

// This object handles all Firebase Firestore operations for tasks.
object FirebaseTaskStorage {

    // Firestore database instance.
    private val db = FirebaseFirestore.getInstance()

    // Collection where tasks will be stored.
    private val taskCollection = db.collection("tasks")

    // Saves one task to Firebase Firestore.
    fun saveTask(
        task: Task,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        taskCollection
            .document(task.id.toString())
            .set(task)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    // Deletes one task from Firebase Firestore.
    fun deleteTask(
        taskId: Int,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        taskCollection
            .document(taskId.toString())
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    // Loads all tasks from Firebase Firestore.
    fun loadTasks(
        onSuccess: (List<Task>) -> Unit,
        onFailure: (Exception) -> Unit = {}
    ) {
        taskCollection
            .get()
            .addOnSuccessListener { result ->
                val tasks = result.documents.mapNotNull { document ->
                    document.toObject(Task::class.java)
                }
                onSuccess(tasks)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}