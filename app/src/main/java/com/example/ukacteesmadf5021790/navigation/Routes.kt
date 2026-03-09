package com.example.ukacteesmadf5021790.navigation

object Routes {
    const val Splash = "splash"
    const val Home = "home"
    const val AddTask = "add_task"

    // Task details route with argument
    const val TaskDetails = "task_details/{taskId}"

    fun taskDetails(taskId: Int): String = "task_details/$taskId"
}