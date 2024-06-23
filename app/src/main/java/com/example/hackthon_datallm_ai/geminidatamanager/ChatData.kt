package com.example.hackthon_datallm_ai.geminidatamanager

import androidx.compose.runtime.MutableState
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.ResponseStoppedException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ChatData {
    val api_key = "AIzaSyDwCa8LqBStecMFJON446ZHZ4SUmidDNn0"

    suspend fun getResponse(
        prompt: String,
        _isLoading: MutableState<Boolean>,
        responseMessage: MutableState<String>
    ): Chat {
        val generativeModel = GenerativeModel(
            modelName = "gemini-pro", apiKey = api_key
        )
        try {
            val response = withContext(Dispatchers.IO) {
                generativeModel.generateContent(prompt)
            }

            val generatedContent = response.text // Assuming there's a method getContent()
//            println(generatedContent)
            responseMessage.value=generatedContent.toString()
            if(response.text.toString().isNotEmpty()){
                _isLoading.value=false
            }

            return Chat(
                prompt = generatedContent.toString(),
                isFromUser = false
            )
        } catch (e: ResponseStoppedException) {
            // Handle ResponseStoppedException separately if needed
            return Chat(
                prompt = "Response stopped: ${e.message}",
                isFromUser = false
            )
        } catch (e: Exception) {
            return Chat(
                prompt = e.message ?: "Unknown error occurred",
                isFromUser = false
            )
        }
    }
}