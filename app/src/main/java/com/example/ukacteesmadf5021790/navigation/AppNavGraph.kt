package com.example.ukacteesmadf5021790.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ukacteesmadf5021790.model.Task
import com.example.ukacteesmadf5021790.screens.AddTaskScreen
import com.example.ukacteesmadf5021790.screens.HomeScreen
import com.example.ukacteesmadf5021790.screens.SplashScreen
import com.example.ukacteesmadf5021790.screens.TaskDetailsScreen
import com.example.ukacteesmadf5021790.storage.FirebaseTaskStorage
import com.example.ukacteesmadf5021790.storage.TaskStorage
import com.example.ukacteesmadf5021790.storage.AuthStorage

// This composable controls the navigation between all app screens.
@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Loads tasks first from local storage so the app can work offline.
    var tasks by remember {
        mutableStateOf(TaskStorage.loadTasks(context))
    }

    // Loads tasks from Firebase when the app starts.
    // If Firebase has data, it updates the app list and saves a local copy.
    LaunchedEffect(Unit) {
        FirebaseTaskStorage.loadTasks(
            onSuccess = { firebaseTasks ->
                if (firebaseTasks.isNotEmpty()) {
                    tasks = firebaseTasks
                    TaskStorage.saveTasks(context, firebaseTasks)
                } else if (tasks.isEmpty()) {
                    val defaultTasks = listOf(
                        Task(1, "Sample Task", "This is a sample task"),
                        Task(2, "Finish Report", "Complete the sprint documentation"),
                        Task(3, "Prepare Slides", "Create slides for the app presentation")
                    )
                    tasks = defaultTasks
                    TaskStorage.saveTasks(context, defaultTasks)

                    // Saves default tasks to Firebase as initial data.
                    defaultTasks.forEach { task ->
                        FirebaseTaskStorage.saveTask(task)
                    }
                }
            },
            onFailure = {
                // If Firebase fails, the app continues using local storage.
                if (tasks.isEmpty()) {
                    tasks = listOf(
                        Task(1, "Sample Task", "This is a sample task"),
                        Task(2, "Finish Report", "Complete the sprint documentation"),
                        Task(3, "Prepare Slides", "Create slides for the app presentation")
                    )
                    TaskStorage.saveTasks(context, tasks)
                }
            }
        )
    }

    NavHost(
        navController = navController,
        startDestination = Routes.Splash
    ) {
        // Splash screen route.
        composable(Routes.Splash) {
            SplashScreen(
                onAuthenticated = {
                    navController.navigate(Routes.Home) {
                        popUpTo(Routes.Splash) { inclusive = true }
                    }
                }
            )
        }

        // Home screen route.
        composable(Routes.Home) {
            HomeScreen(
                tasks = tasks,
                onAddClick = {
                    navController.navigate(Routes.AddTask)
                },
                onTaskClick = { task ->
                    navController.navigate(Routes.taskDetails(task.id))
                },
                onDeleteTask = { task ->
                    tasks = tasks.filter { it.id != task.id }

                    TaskStorage.saveTasks(context, tasks)

                    FirebaseTaskStorage.deleteTask(task.id)
                },
                onLogout = {
                    AuthStorage.logout()

                    navController.navigate(Routes.Splash) {
                        popUpTo(Routes.Home) { inclusive = true }
                    }
                }
            )
        }

        // Add task screen route.
        composable(Routes.AddTask) {
            AddTaskScreen(
                onSave = { task ->
                    // Adds the new task to the app list.
                    tasks = tasks + task

                    // Saves the new list locally.
                    TaskStorage.saveTasks(context, tasks)

                    // Saves the task online in Firebase.
                    FirebaseTaskStorage.saveTask(task)

                    // Returns to Home screen.
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Task details screen route with taskId argument.
        composable(
            route = Routes.TaskDetails,
            arguments = listOf(
                navArgument("taskId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val taskId = backStackEntry.arguments?.getInt("taskId") ?: 0
            val selectedTask = tasks.find { it.id == taskId }

            if (selectedTask != null) {
                TaskDetailsScreen(
                    task = selectedTask,
                    onBack = { navController.popBackStack() },
                    onDelete = { deleteTaskId ->
                        tasks = tasks.filter { it.id != deleteTaskId }
                        TaskStorage.saveTasks(context, tasks)
                        FirebaseTaskStorage.deleteTask(deleteTaskId)
                        navController.popBackStack()
                    },
                    onEdit = { taskId ->
                        navController.navigate(Routes.editTask(taskId))
                    }
                )

            }
        }
        composable(
            route = Routes.EditTask,
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId") ?: 0
            val selectedTask = tasks.find { it.id == taskId }

            if (selectedTask != null) {
                AddTaskScreen(
                    existingTask = selectedTask,
                    onSave = { updatedTask ->
                        tasks = tasks.map {
                            if (it.id == updatedTask.id) updatedTask else it
                        }

                        TaskStorage.saveTasks(context, tasks)
                        FirebaseTaskStorage.saveTask(updatedTask)

                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}