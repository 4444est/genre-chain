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

import com.example.genrechain.viewmodel.GameViewModel          // ← your VM
import androidx.lifecycle.viewmodel.compose.viewModel          // viewModel()
import androidx.compose.runtime.collectAsState                  // flow → State
import androidx.compose.foundation.lazy.LazyColumn              // list UI
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.Color

@Composable
fun StartScreen(
    viewModel: GameViewModel = viewModel()
) {
    var query by rememberSaveable { mutableStateOf("") }
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

        LazyColumn {
            items(results) { artist ->
                Text(artist.name)
            }
        }
    }
}
