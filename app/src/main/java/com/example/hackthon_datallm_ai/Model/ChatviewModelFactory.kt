package com.example.hackthon_datallm_ai.Model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hackthon_datallm_ai.geminidatamanager.ChatViewModel

class ViewModelChatFactory(private val chatViewModel: ChatViewModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewModelChat::class.java)) {
            return ViewModelChat(chatViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
