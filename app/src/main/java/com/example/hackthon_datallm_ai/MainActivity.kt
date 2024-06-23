package com.example.hackthon_datallm_ai

import Showdatabase
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hackthon_datallm_ai.Database.DatabaseHelper
import com.example.hackthon_datallm_ai.Model.ViewModelChat
import com.example.hackthon_datallm_ai.Model.ViewModelChatFactory
import com.example.hackthon_datallm_ai.Model.ViewModelFactory
import com.example.hackthon_datallm_ai.geminidatamanager.ChatViewModel
import com.example.hackthon_datallm_ai.ui.theme.Hackthon_DataLLM_AITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Hackthon_DataLLM_AITheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val chatViewModel: ChatViewModel = viewModel(
                        factory = ViewModelFactory(applicationContext)
                    )
                    val viewModelChat: ViewModelChat = viewModel(
                        factory = ViewModelChatFactory(chatViewModel)
                    )
                    // Set up navigation graph
                    NavHost(
                        navController = navController,
                        startDestination = if (DatabaseHelper(applicationContext).getTableNames().isNotEmpty()) "main_screen" else "Start"                    ) {

                        composable("input") {
                            UIdatainput(applicationContext,navController).Databaseinput()
                        }
                        composable("main_screen"){
                            MainScreen().MainUi(context = applicationContext,navController,chatViewModel)
                        }
                        composable("chat"){
                            ChatScreen(navController = navController, viewmodel = viewModelChat,chatViewModel)
                        }
                        composable("datashow"){
                            Showdatabase().Showdata(context = applicationContext, chatViewModel = chatViewModel,navController)
                        }
                        composable("Start"){
                            StartScreen(navController)
                        }
                    }
                }
            }
        }
    }
}

