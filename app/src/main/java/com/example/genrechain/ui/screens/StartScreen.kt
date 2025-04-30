package com.example.genrechain.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.genrechain.data.remote.dto.ArtistDto
import com.example.genrechain.viewmodel.GameViewModel
import androidx.compose.ui.graphics.Color

@Composable
fun StartScreen(
    onStartClick: (ArtistDto) -> Unit,
    onTargetClick: (ArtistDto) -> Unit,
    onStartGame: () -> Unit
) {
    val viewModel: GameViewModel = viewModel()

    // two inputs
    var query by remember { mutableStateOf("") }
    var targetQuery by remember { mutableStateOf("") }

    // track which dropdown is showing
    var activeSection by remember { mutableStateOf(0) }
    var listVisible by remember { mutableStateOf(false) }

    // flows from VM
    val results by viewModel.searchResults.collectAsState()
    val error   by viewModel.error.collectAsState()
    // clicked results
    val start   by viewModel.startArtist.collectAsState()
    val target  by viewModel.targetArtist.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        // ─── First Search Card ──────────────────────────────────
        Card(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = 8.dp
        ) {
            Column(Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Search start artist") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        viewModel.search(query)
                        activeSection = 1
                        listVisible = true
                    },
                    Modifier.fillMaxWidth()
                ) {
                    Text("Search")
                }

                if (listVisible && activeSection == 1 && results.isNotEmpty()) {
                    Surface(
                        Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .padding(top = 8.dp)
                            .zIndex(1f),
                        elevation = 8.dp
                    ) {
                        LazyColumn {
                            items(results) { artist ->
                                Text(
                                    artist.name,
                                    Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.onStartClicked(artist)
                                            listVisible = false
                                            onStartClick(artist)
                                        }
                                        .padding(12.dp)
                                )
                                Divider()
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ─── Second Search Card ─────────────────────────────────
        Card(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = 8.dp
        ) {
            Column(Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = targetQuery,
                    onValueChange = { targetQuery = it },
                    label = { Text("Search target artist") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        viewModel.search(targetQuery)
                        activeSection = 2
                        listVisible = true
                    },
                    Modifier.fillMaxWidth()
                ) {
                    Text("Search")
                }

                if (listVisible && activeSection == 2 && results.isNotEmpty()) {
                    Surface(
                        Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .padding(top = 8.dp)
                            .zIndex(1f),
                        elevation = 8.dp
                    ) {
                        LazyColumn {
                            items(results) { artist ->
                                Text(
                                    artist.name,
                                    Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.onTargetClicked(artist)
                                            listVisible = false
                                            onTargetClick(artist)
                                        }
                                        .padding(12.dp)
                                )
                                Divider()
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ─── error display ─────────────────────────────────────
        error?.let {
            Text("Error: $it", color = MaterialTheme.colors.error)
        }

        Spacer(Modifier.height(16.dp))

        // ─── Picked artists display ────────────────────────────
        start?.let { artist ->
            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                elevation = 4.dp
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Start: ${artist.name}", style = MaterialTheme.typography.h6)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Genres: ${artist.genres.joinToString()}",
                        style = MaterialTheme.typography.body2
                    )
                }
            }
        }

        target?.let { artist ->
            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                elevation = 4.dp
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Target: ${artist.name}", style = MaterialTheme.typography.h6)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Genres: ${artist.genres.joinToString()}",
                        style = MaterialTheme.typography.body2
                    )
                }
            }
        }

        Spacer(Modifier.weight(1f))  // push the button to the bottom

        // only show once both have a value:
        if (start != null && target != null) {
            Button(
                onClick = { onStartGame() },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text("Start Game")
            }
        }
        error?.let {
            Text("Error: $it", color = Color.Red, modifier = Modifier.padding(top = 8.dp))
        }

    }
}
