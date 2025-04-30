package com.example.genrechain.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.genrechain.data.remote.dto.ArtistDto
import com.example.genrechain.viewmodel.GameViewModel
import com.example.genrechain.ui.theme.PurpleText
import kotlinx.coroutines.launch
import android.util.Log
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun GameScreen(
    start: ArtistDto,
    target: ArtistDto,
    onBack: () -> Unit,
    viewModel: GameViewModel = viewModel()
) {
    val navBarColor = MaterialTheme.colors.background;
    // color nav‐ and status bars
    val sysUi = rememberSystemUiController()
    SideEffect {
        // tint the nav-bar to match your background
        sysUi.setNavigationBarColor(navBarColor)
    }

    var current    by remember { mutableStateOf<ArtistDto?>(null) }
    var fullTarget by remember { mutableStateOf<ArtistDto?>(null) }

    LaunchedEffect(start.id)  { current = viewModel.lookupArtist(start.id) }
    LaunchedEffect(target.id) { fullTarget = viewModel.lookupArtist(target.id) }

    if (current == null || fullTarget == null) {
        // match your theme background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = PurpleText)
        }
        return
    }


    val curr = current!!
    val targ = fullTarget!!

    var guessQuery    by remember { mutableStateOf("") }
    var listVisible   by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf<String?>(null) }
    var showWinDialog by remember { mutableStateOf(false) }

    val results by viewModel.searchResults.collectAsState()
    val error   by viewModel.error.collectAsState()
    val scope   = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.surface,
                elevation = 0.dp,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = PurpleText)
                    }
                },
                title = {
                    Box(Modifier.fillMaxWidth()) {
                        Text(
                            "Genre Chain",
                            modifier = Modifier
                                .align(Alignment.Center)
                            .offset(x = (-10).dp) // move left a bit
                            ,
                            textAlign = TextAlign.Center,
                            color = PurpleText,
                            style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                },
                // add an empty action slot the same width to properly balance out
                actions = {
                    Spacer(Modifier.width(56.dp))  // 56.dp == standard IconButton size
                }
            )
        },
        backgroundColor = MaterialTheme.colors.background
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ─── CARDS ────────────────────────────────────────────
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ArtistCard(curr,  "Current")
                ArtistCard(targ, "Target")
            }

            Spacer(Modifier.height(30.dp))

            // ─── GUESS INPUT ──────────────────────────────────────
            OutlinedTextField(
                value = guessQuery,
                onValueChange = { guessQuery = it },
                label = { Text("Search next artist", color = PurpleText) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = PurpleText),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    cursorColor          = PurpleText,
                    focusedBorderColor   = MaterialTheme.colors.onSurface,
                    unfocusedBorderColor = PurpleText.copy(alpha = 0.5f)
                )
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    resultMessage = null
                    viewModel.search(guessQuery)
                    listVisible = true
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.background),
                border = BorderStroke(1.dp, MaterialTheme.colors.onSurface)
            ) {
                Text("Search Guess", color = PurpleText)
            }

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
                                shallow.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        listVisible = false
                                        guessQuery = ""
                                        scope.launch {
                                            val guessFull = viewModel.lookupArtist(shallow.id)
                                            val common = curr.genres.map(String::lowercase)
                                                .intersect(guessFull.genres.map(String::lowercase))
                                            if (common.isNotEmpty()) {
                                                current = guessFull
                                                resultMessage = "Nice! Shared: ${common.joinToString()}"
                                                if (common.intersect(targ.genres.map(String::lowercase)).isNotEmpty()) {
                                                    showWinDialog = true
                                                }
                                            } else {
                                                resultMessage = "No genre in common with ${curr.name}."
                                                scaffoldState.snackbarHostState.showSnackbar(resultMessage!!)
                                            }
                                        }
                                    }
                                    .padding(12.dp)
                            )
                            Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f))
                        }
                    }
                }
            }

            error?.let {
                Text("Error: $it", color = MaterialTheme.colors.error, modifier = Modifier.padding(top = 8.dp))
            }
            resultMessage?.let {
                Text(it, modifier = Modifier.padding(top = 8.dp), color = PurpleText)
            }
        }

        if (showWinDialog) {
            AlertDialog(
                onDismissRequest = { showWinDialog = false },
                title   = { Text("You Win!", color = PurpleText) },
                text    = { Text("You’ve connected to your target by genre!", color = PurpleText) },
                confirmButton = {
                    TextButton(onClick = { showWinDialog = false }) {
                        Text("OK", color = PurpleText)
                    }
                }
            )
        }
    }
}

@Composable
private fun ArtistCard(artist: ArtistDto, label: String) {
    Card(
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 4.dp,
        modifier = Modifier.width(140.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            // placeholder / image
            Box(
                Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colors.onSurface.copy(alpha = 0.1f))
            )
            Spacer(Modifier.height(4.dp))
            Text(artist.name, color = PurpleText, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(label, style = MaterialTheme.typography.caption, color = PurpleText)
        }
    }
}