package com.example.hackthon_datallm_ai.Model

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hackthon_datallm_ai.geminidatamanager.ChatUIEvent
import com.example.hackthon_datallm_ai.geminidatamanager.ChatViewModel
import com.example.hackthon_datallm_ai.geminidatamanager.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewModelChat(private val chatViewModel: ChatViewModel) : ViewModel() {


    val conversation: StateFlow<List<ChatUiModel.Message>>
        get() = _conversation

    private val _conversation = MutableStateFlow(
        listOf(ChatUiModel.Message.initConv)
    )

    fun sendChat(msg: String) {
        val myChat = ChatUiModel.Message(msg, ChatUiModel.Author.me)
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                _conversation.emit(_conversation.value + myChat)
            }
            chatViewModel.onEvent(ChatUIEvent.UpdatePrompt(msg))
            chatViewModel.onEvent(ChatUIEvent.SendPrompt(msg))

            val initialValue = chatViewModel.chatdata.value
            var botReplyAdded = false
            var botReplyblank = false

            val observer = Observer<Resource<String>> { resource ->
                when (resource) {
                    is Resource.Success -> {
                        if (!botReplyAdded) {
                            viewModelScope.launch {
                                _conversation.emit(
                                    _conversation.value + ChatUiModel.Message(
                                        text = resource.data.toString(),
                                        author = ChatUiModel.Author.bot
                                    )
                                )
                            }
                            botReplyAdded = true
                        }
                    }

                    is Resource.Loading -> {

                        if(!botReplyblank){
                            viewModelScope.launch {
                                _conversation.emit(
                                    _conversation.value + ChatUiModel.Message(
                                        text = "",
                                        author = ChatUiModel.Author.bot
                                    )
                                )
                            }
                            botReplyblank=true
                        }
                    }

                    is Resource.Stop -> {
                        viewModelScope.launch {
                            _conversation.emit(
                                _conversation.value - ChatUiModel.Message(
                                    text = "",
                                    author = ChatUiModel.Author.bot
                                )
                            )
                        }
                    }
                }
            }

            chatViewModel.chatdata.observeForever(observer)

            if (initialValue != null) {
                observer.onChanged(initialValue)
            }
        }
    }
}