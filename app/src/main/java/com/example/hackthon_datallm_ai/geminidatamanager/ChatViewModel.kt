package com.example.hackthon_datallm_ai.geminidatamanager

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hackthon_datallm_ai.Database.DatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel (private val context: Context) : ViewModel() {
    private var _database = "" //table name

    lateinit var _attributes: List<Pair<String, String>>
    fun setattributes(attributes: List<Pair<String, String>>) {
        _attributes = attributes
    }
    fun setDatabase(database: String) {
        _database = database
    }

    fun getAllData(): Cursor? {

        return DatabaseHelper(context).getAllData(_database)
    }

    private val _chatState = MutableStateFlow(ChatState())
    val chatState = _chatState.asStateFlow()
    val isLoading = mutableStateOf(true)
    val responseMessage = mutableStateOf("")
    private val _livechat = MutableLiveData<Resource<String>>()
    val chatdata: LiveData<Resource<String>> = _livechat

    fun onEvent(event: ChatUIEvent) {
        when (event) {
            is ChatUIEvent.SendPrompt -> {
                if (event.prompt.isNotEmpty()) {
                    addPrompt(event.prompt)
                }
                getResponse(event.prompt)
            }

            is ChatUIEvent.UpdatePrompt -> {
                _chatState.update {
                    it.copy(prompt = event.newPrompt)
                }
            }
        }
    }

    fun searchInDatabase(input: String,   callback: (List<String>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val suggestions = mutableListOf<String>()

            getAllData()?.let {
                // Retrieve data from cursor based on attributes
                val dataList = DatabaseHelper(context).getDataFromCursor(
                    it,
                    _attributes
                )

                for (data in dataList) {
                    if (data.contains(input, ignoreCase = true)) {
                        suggestions.add(data)
                    }
                  }

            }

            callback(suggestions)
        }
    }
    private fun addPrompt(prompt: String) {
        _chatState.update {
            it.copy(
                chatList = it.chatList.toMutableList().apply {
                    add(0, Chat(prompt, true))
                },
                prompt = ""
            )
        }
    }

    private fun getResponse(prompt: String) {
        _livechat.value = Resource.Loading()
        viewModelScope.launch {
            var modifiedPrompt = "["+prompt +"]" +"now give me null if it has no meaning related to update , insert and delete query , if it has then give me query in format only "+
                    "INSERT INTO <tablename> (key1, key2) VALUES (value1, value2)" +
                    " or " +
                    "UPDATE <tablename> SET key1 = value1, key2 = value2 WHERE condition" +
                    " or " +
                    "DELETE FROM <tablename> WHERE condition" +
                    " or  null" + "here is the table attributes table name: ${_database} tabel attributes: ${_attributes}"
            val chat = ChatData.getResponse(modifiedPrompt, isLoading, responseMessage)
            println(chat)
            if(chat.prompt=="null" || chat.prompt=="Null" || chat.prompt=="NULL" ||chat.prompt=="'null'" || chat.prompt=="'Null'" || chat.prompt=="'NULL'"){
                _livechat.value = Resource.Success("Retry! you can either ask to delete , insert or update noting else till now :)")
                _livechat.value = Resource.Stop()
                _chatState.update {
                    it.copy(
                        chatList = it.chatList.toMutableList().apply {
                            add(0, chat)
                        }
                    )
                }
            }
            else{
                val dbHelper = DatabaseHelper(context)
                dbHelper.convertSqlToContentValues(chat.prompt,dbHelper,_database,_livechat,_chatState,chat)
            }
        }
    }
}
