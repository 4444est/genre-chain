package com.example.genrechain.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.genrechain.data.remote.dto.ArtistDto
import com.example.genrechain.viewmodel.GameViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.background
import kotlinx.coroutines.launch


@Composable
fun GameScreen(
    start: ArtistDto,
    target: ArtistDto
) {
    val viewModel: GameViewModel = viewModel()

    // 1️⃣ the “current” artist in the chain
    var current by remember { mutableStateOf(start) }

    // 2️⃣ guess UI state
    var guessQuery by remember { mutableStateOf("") }
    var listVisible by remember { mutableStateOf(false) }

    // 3️⃣ flows from VM
    val results by viewModel.searchResults.collectAsState()
    val error   by viewModel.error.collectAsState()

    // 4️⃣ win dialog + snackbar
    var showWinDialog by remember { mutableStateOf(false) }
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(scaffoldState = scaffoldState) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // ─── Current & Target Cards ────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ArtistCard(artist = current, label = "Current")
                ArtistCard(artist = target,  label = "Target")
            }

            Spacer(Modifier.height(24.dp))

            // ─── Guess input + button ─────────────────────────────────────────
            OutlinedTextField(
                value = guessQuery,
                onValueChange = { guessQuery = it },
                label = { Text("Guess next artist") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    viewModel.search(guessQuery)
                    listVisible = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Search Guess")
            }

            // ─── suggestion dropdown ───────────────────────────────────────────
            if (listVisible && results.isNotEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .padding(top = 8.dp),
                    elevation = 8.dp
                ) {
                    LazyColumn {
                        items(results) { artist ->
                            Text(
                                artist.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        listVisible = false
                                        guessQuery = ""

                                        // check chain step
                                        if (viewModel.haveGenreInCommon(current, artist)) {
                                            current = artist

                                            // did we hit the target?
                                            if (viewModel.haveGenreInCommon(current, target)) {
                                                showWinDialog = true
                                            }

                                        } else {
                                            // no shared genre → show snackbar
                                            scope.launch {
                                                scaffoldState.snackbarHostState
                                                    .showSnackbar("No genre in common with current!")
                                            }
                                        }
                                    }
                                    .padding(12.dp)
                            )
                            Divider()
                        }
                    }
                }
            }

            // ─── error display ─────────────────────────────────────────────────
            error?.let {
                Text("Error: $it", color = MaterialTheme.colors.error)
            }
        }

        // ─── Win Dialog ─────────────────────────────────────────────────────
        if (showWinDialog) {
            AlertDialog(
                onDismissRequest = { showWinDialog = false },
                title   = { Text("You Win!") },
                text    = { Text("You’ve connected to your target by genre!") },
                confirmButton = {
                    TextButton(onClick = { showWinDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Composable
private fun ArtistCard(artist: ArtistDto, label: String) {
    Card(elevation = 4.dp, modifier = Modifier.width(140.dp)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            // if you ever populate artist.imageUrl via your repo, Coil will load it here;
            // for now this is a gray placeholder circle
            if (artist.imageUrl != null) {
                Image(
                    painter = rememberAsyncImagePainter(artist.imageUrl),
                    contentDescription = artist.name,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                )
            } else {
                Box(
                    Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colors.onSurface.copy(alpha = 0.1f))
                )
            }

            Text(
                text = artist.name,
                style = MaterialTheme.typography.subtitle1,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(label, style = MaterialTheme.typography.caption)
        }
    }
}