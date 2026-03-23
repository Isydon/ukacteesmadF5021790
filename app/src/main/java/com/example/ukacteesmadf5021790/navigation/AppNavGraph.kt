package com.example.ukacteesmadf5021790.navigation

import androidx.compose.runtime.*
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

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    var tasks by remember {
        mutableStateOf(
            listOf(
                Task(1, "Sample Task", "This is a sample task")
            )
        )
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
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}