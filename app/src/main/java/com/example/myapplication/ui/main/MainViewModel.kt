package com.example.myapplication.ui.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.mock.MockLink
import com.example.myapplication.util.ViewModelFactory

class MainViewModel : ViewModel() {
    var selectedTab by mutableStateOf(0)
        private set
    var selectedLink by mutableStateOf<MockLink?>(null)
        private set

    fun selectTab(index: Int) {
        selectedTab = index
    }

    fun selectLink(link: MockLink) {
        selectedLink = link
        selectedTab = 1
    }

    fun openAnalytics() {
        selectedTab = 2
    }

    fun openSettings() {
        selectedTab = 3
    }

    fun goToShorten() {
        selectedTab = 0
    }

    companion object {
        fun factory() = ViewModelFactory { MainViewModel() }
    }
}
