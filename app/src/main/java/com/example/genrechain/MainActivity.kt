package com.example.genrechain

import android.os.Bundle
import android.util.Log
//import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.genrechain.ui.screens.StartScreen
import com.example.genrechain.ui.theme.GenreChainTheme
import androidx.compose.runtime.*
import com.example.genrechain.data.remote.dto.ArtistDto
import com.example.genrechain.ui.screens.GameScreen

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GenreChainTheme {
                var screen by remember { mutableStateOf("start") }
                var startArtist by remember { mutableStateOf<ArtistDto?>(null) }
                var targetArtist by remember { mutableStateOf<ArtistDto?>(null) }

                when (screen) {
                    "start" -> StartScreen(
                        onStartClick  = { startArtist = it },
                        onTargetClick = { targetArtist = it },
                        onStartGame = {
                            Log.d("MainActivity", "Starting game with $startArtist → $targetArtist")
                            if (startArtist != null && targetArtist != null) {

                                screen = "game"
                            } else {
                                Log.e("MainActivity", "Can't start game – start=$startArtist target=$targetArtist")
                            }
                        }
                    )
                    "game" -> {
                        // safe unwrap
                        GameScreen(start  = startArtist!!, target = targetArtist!!)
                    }
                }
            }
        }
    }
}


