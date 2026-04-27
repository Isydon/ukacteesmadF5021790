package com.example.ukacteesmadf5021790.storage

import android.content.Context
import com.example.ukacteesmadf5021790.model.Task
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object TaskStorage {
    private const val PREF_NAME = "task_prefs"
    private const val TASKS_KEY = "tasks"

    fun saveTasks(context: Context, tasks: List<Task>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val json = Gson().toJson(tasks)
        editor.putString(TASKS_KEY, json)
        editor.apply()
    }

    fun loadTasks(context: Context): List<Task> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(TASKS_KEY, null) ?: return emptyList()

        val type = object : TypeToken<List<Task>>() {}.type
        return Gson().fromJson(json, type)
    }
}