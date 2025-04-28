package com.example.genrechain.ui.screens

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.genrechain.data.remote.dto.ArtistDto
import com.example.genrechain.viewmodel.GameViewModel

@Composable
fun StartScreen(
    onArtistClick: (ArtistDto) -> Unit
) {
    // pull Application for your AndroidViewModel
//    val application = LocalContext.current.applicationContext as Application

    // explicitly create your VM with the Application constructor
    val viewModel: GameViewModel = viewModel()

    // UI state
    var query by rememberSaveable { mutableStateOf("") }
    val results by viewModel.searchResults.collectAsState()
    val error   by viewModel.error.collectAsState()
    var listVisible by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Search bar + button
        OutlinedTextField(value = query, onValueChange = { query = it },
            label = { Text("Search artist") }, modifier = Modifier.fillMaxWidth())
        Button(onClick = {
                viewModel.search(query)
                listVisible = true
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) { Text("Search") }

        // Error display
        error?.let {
            Text("Error: $it",
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(Modifier.height(16.dp))

        // Results list
        if (listVisible && results.isNotEmpty()) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
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
        }

        // After click: show chosen artist
        if (!listVisible) {
            viewModel.selected.collectAsState().value?.let { chosen ->
                Text(
                    "You picked: ${chosen.name}\n" +
                            "Genres: ${chosen.genres.joinToString()}",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}
