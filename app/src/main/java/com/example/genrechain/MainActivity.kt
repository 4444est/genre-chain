package com.example.genrechain

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.genrechain.data.remote.dto.ArtistDto
import com.example.genrechain.ui.screens.GameScreen
import com.example.genrechain.ui.screens.StartScreen
import com.example.genrechain.ui.theme.GenreChainTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GenreChainTheme {
                val ctx = LocalContext.current

                // begin with start screen, set start and target artists to null
                var screen       by remember { mutableStateOf("start") }
                var startArtist  by remember { mutableStateOf<ArtistDto?>(null) }
                var targetArtist by remember { mutableStateOf<ArtistDto?>(null) }

                when (screen) {
                    "start" -> StartScreen(
                        onStartClick  = { startArtist  = it },
                        onTargetClick = { targetArtist = it },
                        onStartGame   = {
                            if (startArtist != null && targetArtist != null) {
                                // compute lowercase intersection
                                val common = startArtist!!.genres
                                    .map(String::lowercase)
                                    .intersect(targetArtist!!.genres.map(String::lowercase))

                                // if genres in common, show toast. don't let game start
                                if (common.isNotEmpty()) {
                                    Toast.makeText(
                                        ctx,
                                        "These two share genres: ${common.joinToString()}. Pick different artists.",
                                        Toast.LENGTH_LONG
                                    ).show()

                                } else { // only allow game start if there are no starting genres in common
                                    screen = "game"
                                }
                            } else {
                                Toast.makeText(ctx, "Select both a start and target artist.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )

                    "game" -> GameScreen(
                        start  = startArtist!!,
                        target = targetArtist!!,
                        onBack = { screen = "start" },
                        onRestart = {
                            // reset the game screen to trigger a restart
                            screen = "start"
                            startArtist = null
                            targetArtist = null
                        }
                    )
                }
            }
        }
    }
}
