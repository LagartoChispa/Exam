package com.exam.me

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.exam.me.ui.main.AuthState
import com.exam.me.ui.main.MainViewModel
import com.exam.me.ui.navigation.AppNavigation
import com.exam.me.ui.navigation.Screen
import com.exam.me.ui.theme.ExamTheme

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExamTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val authState by mainViewModel.authState.collectAsState()

                    when (authState) {
                        AuthState.Unknown -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                        AuthState.Authenticated -> {
                            AppNavigation(startDestination = Screen.Home.route)
                        }
                        AuthState.Unauthenticated -> {
                            AppNavigation(startDestination = Screen.Login.route)
                        }
                    }
                }
            }
        }
    }
}