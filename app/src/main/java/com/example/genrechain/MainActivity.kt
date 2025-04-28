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

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GenreChainTheme {
                StartScreen { artist ->
                    // here you get back the clicked ArtistDto —
                    // for now we just log it, later you can navigate
                    Log.d("MainActivity", "User picked: ${artist.name} (${artist.id})")
                }
            }
        }
    }
}

