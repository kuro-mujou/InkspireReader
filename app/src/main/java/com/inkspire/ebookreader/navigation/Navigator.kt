package com.inkspire.ebookreader.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

class Navigator(
    val backStack: NavBackStack<NavKey>,
    private val startDestination: NavKey
) {
    val currentScreen: NavKey
        get() = backStack.lastOrNull() ?: startDestination

    val currentTab: NavKey
        get() = backStack.firstOrNull() ?: startDestination

    private val tabHistories = mutableMapOf<NavKey, List<NavKey>>()

    init {
        if (backStack.isEmpty()) {
            backStack.add(startDestination)
        }
    }

    fun navigateTo(screen: NavKey) {
        backStack.add(screen)
    }

    fun switchTab(newTab: NavKey) {
        if (newTab == currentTab) return

        tabHistories[currentTab] = backStack.toList()

        backStack.clear()

        val savedHistory = tabHistories[newTab]
        if (savedHistory != null && savedHistory.isNotEmpty()) {
            backStack.addAll(savedHistory)
        } else {
            backStack.add(newTab)
        }
    }

    fun handleBack(): Boolean {
        return when {
            backStack.size > 1 -> {
                backStack.removeLastOrNull()
                true
            }
            currentTab != startDestination -> {
                switchTab(startDestination)
                true
            }
            else -> false
        }
    }
}