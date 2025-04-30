package com.example.genrechain.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import com.example.genrechain.data.remote.dto.ArtistDto
import com.example.genrechain.viewmodel.GameViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import android.util.Log

@Composable
fun GameScreen(
    start: ArtistDto,
    target: ArtistDto,
    onBack: () -> Unit,
    viewModel: GameViewModel = viewModel()
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    // 1️⃣ load the *full* versions of both start & target
    var current by remember { mutableStateOf<ArtistDto?>(null) }
    var fullTarget by remember { mutableStateOf<ArtistDto?>(null) }

    LaunchedEffect(start.id)  { current = viewModel.lookupArtist(start.id) }
    LaunchedEffect(target.id) { fullTarget = viewModel.lookupArtist(target.id) }

    // 2️⃣ block until both are ready
    if (current == null || fullTarget == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // unwrap non-nullables
    val curr = current!!
    val targ = fullTarget!!

    // 3️⃣ UI state for the guess dropdown + result message
    var guessQuery    by remember { mutableStateOf("") }
    var listVisible   by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf<String?>(null) }
    var showWinDialog by remember { mutableStateOf(false) }

    // flows from VM
    val results by viewModel.searchResults.collectAsState()
    val error   by viewModel.error.collectAsState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("Genre Chain") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // ─── Current & Target ────────────────────────────────────────────
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ArtistCard(artist = curr, label = "Current")
                ArtistCard(artist = targ, label = "Target")
            }

            Spacer(Modifier.height(24.dp))

            // ─── Guess input + button ────────────────────────────────────────
            OutlinedTextField(
                value = guessQuery,
                onValueChange = { guessQuery = it },
                label = { Text("Search next artist") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    resultMessage = null           // clear prior feedback
                    viewModel.search(guessQuery)
                    listVisible = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Search Guess")
            }

            // ─── dropdown suggestions ───────────────────────────────────────
            if (listVisible && results.isNotEmpty()) {
                Surface(
                    Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .padding(top = 8.dp),
                    elevation = 8.dp
                ) {
                    LazyColumn {
                        items(results) { shallow ->
                            Text(
                                text = shallow.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        listVisible = false
                                        guessQuery  = ""
                                        scope.launch {
                                            try {
                                                // fetch full data for the guess
                                                val guessFull = viewModel.lookupArtist(shallow.id)
                                                Log.d("GameScreen", "Full artist genres: ${guessFull.genres}")

                                                // find intersection of genres
                                                val common = curr.genres
                                                    .map(String::lowercase)
                                                    .intersect(guessFull.genres.map(String::lowercase))

                                                if (common.isNotEmpty()) {
                                                    // success: update current and feedback
                                                    current = guessFull
                                                    resultMessage = "Nice! You share: ${common.joinToString()}"

                                                    // check for win against the *full* target
                                                    if (common.intersect(targ.genres.map(String::lowercase)).isNotEmpty()) {
                                                        showWinDialog = true
                                                    }
                                                } else {
                                                    resultMessage = "No genre in common with ${curr.name}."
                                                    scaffoldState.snackbarHostState
                                                        .showSnackbar(resultMessage!!)
                                                }
                                            } catch (e: Exception) {
                                                scaffoldState.snackbarHostState
                                                    .showSnackbar("Lookup failed: ${e.message}")
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

            // ─── error & feedback ────────────────────────────────────────────
            error?.let {
                Text("Error: $it", color = MaterialTheme.colors.error,
                    modifier = Modifier.padding(top = 8.dp))
            }
            resultMessage?.let {
                Text(it, modifier = Modifier.padding(top = 8.dp))
            }
        }

        // ─── Win dialog ─────────────────────────────────────────────────
        if (showWinDialog) {
            AlertDialog(
                onDismissRequest = { showWinDialog = false },
                title            = { Text("You Win!") },
                text             = { Text("You’ve connected to your target by genre!") },
                confirmButton    = {
                    TextButton(onClick = { showWinDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

// small reusable card
@Composable
private fun ArtistCard(artist: ArtistDto, label: String) {
    Card(elevation = 4.dp, modifier = Modifier.width(140.dp)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            if (artist.imageUrl != null) {
                Image(
                    painter            = rememberAsyncImagePainter(artist.imageUrl),
                    contentDescription = artist.name,
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier
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
                text      = artist.name,
                style     = MaterialTheme.typography.subtitle1,
                maxLines  = 1,
                overflow  = TextOverflow.Ellipsis,
                modifier  = Modifier.padding(top = 8.dp)
            )
            Text(label, style = MaterialTheme.typography.caption)
        }
    }
}
