package com.example.ukacteesmadf5021790.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ukacteesmadf5021790.screens.*

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

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
                onAddTask = { navController.navigate(Routes.AddTask) },
                onOpenTask = { taskId -> navController.navigate(Routes.taskDetails(taskId)) }
            )
        }

        composable(Routes.AddTask) {
            AddTaskScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = Routes.TaskDetails,
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { entry ->
            val taskId = entry.arguments?.getInt("taskId") ?: 0
            TaskDetailsScreen(taskId = taskId, onBack = { navController.popBackStack() })
        }
    }
}