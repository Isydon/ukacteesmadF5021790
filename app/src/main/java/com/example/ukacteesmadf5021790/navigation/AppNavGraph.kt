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
import com.example.ukacteesmadf5021790.storage.TaskStorage

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current

    var tasks by remember {
        mutableStateOf(TaskStorage.loadTasks(context))
    }

    if (tasks.isEmpty()) {
        tasks = listOf(
            Task(1, "Sample Task", "This is a sample task"),
            Task(2, "Finish Report", "Complete the sprint documentation"),
            Task(3, "Prepare Slides", "Create slides for the app presentation")
        )
    }

    LaunchedEffect(tasks) {
        TaskStorage.saveTasks(context, tasks)
    }

    NavHost(
        navController = navController,
        startDestination = Routes.Splash
    ) {
        composable(Routes.Splash) {
            SplashScreen(
                onFinished = {
                    navController.navigate(Routes.Home) {
                        popUpTo(Routes.Splash) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Home) {
            HomeScreen(
                tasks = tasks,
                onAddTask = { navController.navigate(Routes.AddTask) },
                onOpenTask = { taskId ->
                    navController.navigate(Routes.taskDetails(taskId))
                }
            )
        }

        composable(Routes.AddTask) {
            AddTaskScreen(
                onSave = { task ->
                    tasks = tasks + task
                    TaskStorage.saveTasks(context, tasks)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.TaskDetails,
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
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
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}