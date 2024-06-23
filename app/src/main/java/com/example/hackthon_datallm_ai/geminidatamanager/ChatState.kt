package com.example.hackthon_datallm_ai.geminidatamanager

data class ChatState(
    val chatList: MutableList<Chat> = mutableListOf(),
    val prompt: String = ""
)
