package com.example.genrechain.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.genrechain.data.remote.dto.ArtistDto
import com.example.genrechain.viewmodel.GameViewModel

@Composable
fun StartScreen(
    onArtistClick: (ArtistDto) -> Unit
) {
    val viewModel: GameViewModel = viewModel()

    var query by rememberSaveable { mutableStateOf("") }
    var targetQuery by rememberSaveable { mutableStateOf("") }  // Target artist input

    val results by viewModel.searchResults.collectAsState()
    val error by viewModel.error.collectAsState()
    var listVisible by remember { mutableStateOf(true) }

    Surface(color = MaterialTheme.colors.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Search Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = 8.dp
            ) {
                Column(Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        label = { Text("Search artist") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {
                            viewModel.search(query)
                            listVisible = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Search")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Target Artist Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = 8.dp
            ) {
                Column(Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = targetQuery,
                        onValueChange = { targetQuery = it },
                        label = { Text("Enter target artist") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (targetQuery.isNotEmpty()) " Target: $targetQuery" else "No target artist entered",
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.primary
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Error Section
            error?.let {
                Text(
                    "Error: $it",
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            // Search Results or Picked Artist
            if (listVisible && results.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(results) { artist ->
                        Text(
                            text = artist.name,
                            style = MaterialTheme.typography.subtitle1,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.onArtistClicked(artist)
                                    listVisible = false
                                    onArtistClick(artist)
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp)
                        )
                        Divider()
                    }
                }
            } else {
                viewModel.selected.collectAsState().value?.let { chosen ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = 8.dp
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                text = " You picked: ${chosen.name}",
                                style = MaterialTheme.typography.h6,
                                color = MaterialTheme.colors.secondary
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Genres: ${chosen.genres.joinToString()}",
                                style = MaterialTheme.typography.subtitle1
                            )
                        }
                    }
                }
            }
        }
    }
}
