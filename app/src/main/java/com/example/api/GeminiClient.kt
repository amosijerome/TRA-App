package com.example.api

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiClient {
    private const val TAG = "GeminiClient"
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun generateTaxAdvice(userPrompt: String, systemInstruction: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY" || apiKey == "null") {
            Log.e(TAG, "API Key is missing or default placeholder!")
            return@withContext "API Key is not configured in AI Studio Secrets. Please use the following preloaded answer:\n\nTo file your taxes in Tanzania, ensure you have a Taxpayer Identification Number (TIN). Revenue officers can assist you in registering and recording payment receipts. For VAT, the standard rate is 18%. Income tax rates are tiered based on business turnover."
        }

        try {
            // Build the JSON Request using standard org.json classes (zero external dependency risk)
            val root = JSONObject()

            // contents array
            val contentsArray = JSONArray()
            val contentObj = JSONObject()
            contentObj.put("role", "user")
            
            val partsArray = JSONArray()
            val partObj = JSONObject()
            partObj.put("text", userPrompt)
            partsArray.put(partObj)
            
            contentObj.put("parts", partsArray)
            contentsArray.put(contentObj)
            root.put("contents", contentsArray)

            // systemInstruction (if provided)
            if (systemInstruction.isNotEmpty()) {
                val sysInstructionObj = JSONObject()
                val sysPartsArray = JSONArray()
                val sysPartObj = JSONObject()
                sysPartObj.put("text", systemInstruction)
                sysPartsArray.put(sysPartObj)
                sysInstructionObj.put("parts", sysPartsArray)
                root.put("systemInstruction", sysInstructionObj)
            }

            // generationConfig
            val generationConfig = JSONObject()
            generationConfig.put("temperature", 0.7)
            root.put("generationConfig", generationConfig)

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = root.toString().toRequestBody(mediaType)

            val urlWithKey = "$BASE_URL?key=$apiKey"
            val request = Request.Builder()
                .url(urlWithKey)
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errBody = response.body?.string() ?: ""
                    Log.e(TAG, "API call failed with code ${response.code}: $errBody")
                    return@withContext "API error occurred (Code: ${response.code}). Please verify your credentials or try again later."
                }

                val responseBody = response.body?.string() ?: return@withContext "Empty response from server."
                val responseJson = JSONObject(responseBody)
                
                val candidates = responseJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.optJSONObject("content")
                    if (content != null) {
                        val parts = content.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            return@withContext parts.getJSONObject(0).optString("text", "No text content found.")
                        }
                    }
                }
                "The AI assistant was unable to generate a response. Please try again."
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during Gemini API request", e)
            "Unable to connect to the AI Assistant. Please check your internet connection. Error: ${e.localizedMessage}"
        }
    }
}
