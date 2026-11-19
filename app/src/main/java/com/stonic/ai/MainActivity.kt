
package com.stonic.ai

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import java.util.Locale

data class ChatMessage(val text: String, val user: Boolean)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StonicApp()
        }
    }
}

@Composable
fun StonicApp() {
    val context = LocalContext.current
    var input by remember { mutableStateOf("") }
    var messages by remember {
        mutableStateOf(
            listOf(ChatMessage("Hello! I am Stonic AI. How can I help you?", false))
        )
    }

    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(context) {
        val engine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                engineLanguage(tts, Locale("hi", "IN"))
            }
        }
        tts = engine
        onDispose {
            engine.stop()
            engine.shutdown()
        }
    }

    fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "stonic")
    }

    fun reply(question: String): String {
        val q = question.lowercase(Locale.ROOT)
        return when {
            q.contains("hello") || q.contains("hi") ||
                    q.contains("namaste") ->
                "Hello! Main Stonic AI hoon. Bataiye, main aapki kya help kar sakta hoon?"

            q.contains("name") || q.contains("naam") ->
                "Mera naam Stonic AI hai."

            q.contains("time") || q.contains("samay") ->
                "Aapke phone par current time check kar sakte hain."

            else ->
                "Main Stonic AI ka demo version hoon. Abhi mera online AI brain connect nahi hua hai."
        }
    }

    val speechIntent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to Stonic AI")
        }
    }

    val speechLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == ComponentActivity.RESULT_OK) {
                val spoken = result.data
                    ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    ?.firstOrNull()

                if (!spoken.isNullOrBlank()) {
                    input = spoken
                }
            }
        }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) speechLauncher.launch(speechIntent)
        }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF00D9FF),
            background = Color(0xFF080D18),
            surface = Color(0xFF111C2D)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF080D18))
                .padding(16.dp)
        ) {
            Text(
                "STONIC AI",
                color = Color(0xFF00D9FF),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                "YOUR PERSONAL AI ASSISTANT",
                color = Color.LightGray,
                fontSize = 11.sp
            )

            Spacer(Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        Color(0xFF111C2D),
                        RoundedCornerShape(18.dp)
                    )
                    .padding(12.dp)
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(messages) { message ->
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = if (message.user)
                                Alignment.End else Alignment.Start
                        ) {
                            Text(
                                if (message.user) "YOU" else "STONIC AI",
                                color = Color(0xFF00D9FF),
                                fontSize = 10.sp
                            )
                            Text(
                                message.text,
                                color = Color.White,
                                modifier = Modifier
                                    .background(
                                        if (message.user)
                                            Color(0xFF174B68)
                                        else Color(0xFF202E43),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(12.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Ask Stonic AI...") },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                Spacer(Modifier.width(6.dp))

                Button(
                    onClick = {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.RECORD_AUDIO
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            speechLauncher.launch(speechIntent)
                        } else {
                            permissionLauncher.launch(
                                Manifest.permission.RECORD_AUDIO
                            )
                        }
                    }
                ) {
                    Text("🎙")
                }
            }

            Button(
                onClick = {
                    val question = input.trim()
                    if (question.isNotEmpty()) {
                        val answer = reply(question)
                        messages = messages +
                                ChatMessage(question, true) +
                                ChatMessage(answer, false)
                        input = ""
                        speak(answer)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("SEND MESSAGE")
            }

            Text(
                "ST​ONIC AI • MOBILE EDITION",
                color = Color.Gray,
                fontSize = 10.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 12.dp)
            )
        }
    }
}

private fun engineLanguage(
    engine: TextToSpeech?,
    locale: Locale
) {
    engine?.language = locale
}
