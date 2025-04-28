package com.example.genrechain.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.genrechain.viewmodel.GameViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.Color

@Composable
fun StartScreen(
    viewModel: GameViewModel = viewModel()
) {
    var query by rememberSaveable { mutableStateOf("") }
    var targetQuery by rememberSaveable { mutableStateOf("") }  // New target artist input

    val results by viewModel.searchResults.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search artist") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { viewModel.search(query) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) { Text("Search") }

        error?.let { Text("Error: $it", color = Color.Red) }

        // First LazyColumn: search results
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            items(results) { artist ->
                Text(artist.name)
            }
        }

        // New section: Target Artist Input
        OutlinedTextField(
            value = targetQuery,
            onValueChange = { targetQuery = it },
            label = { Text("Enter target artist") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        )

        // Second LazyColumn: showing Target Artist
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            item {
                Text(
                    text = if (targetQuery.isNotEmpty()) targetQuery else "No target artist entered",
                    style = MaterialTheme.typography.h6
                )
            }
        }
    }
}
