package com.example.genrechain.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.genrechain.data.remote.dto.ArtistDto
import com.example.genrechain.ui.theme.PurpleText
import com.example.genrechain.viewmodel.GameViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import com.example.genrechain.R
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource


@Composable
fun GameScreen(
    start: ArtistDto,
    target: ArtistDto,
    onBack: () -> Unit,
    onRestart: () -> Unit, // NEW
    viewModel: GameViewModel = viewModel()
) {
    val navBarColor = MaterialTheme.colors.background
    val sysUi = rememberSystemUiController()
    SideEffect {
        sysUi.setNavigationBarColor(navBarColor)
    }

    var current by remember { mutableStateOf<ArtistDto?>(null) }
    var fullTarget by remember { mutableStateOf<ArtistDto?>(null) }

    LaunchedEffect(start.id) { current = viewModel.lookupArtist(start.id) }
    LaunchedEffect(target.id) { fullTarget = viewModel.lookupArtist(target.id) }

    if (current == null || fullTarget == null) {
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

    // save inputted data to viewmodel
    var guessQuery by remember { mutableStateOf("") }
    var listVisible by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf<String?>(null) }
    var showWinDialog by remember { mutableStateOf(false) }

    val results by viewModel.searchResults.collectAsState()
    val error by viewModel.error.collectAsState()
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()


    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.background,
                elevation       = 0.dp,
                navigationIcon = {
                    IconButton(onClick = onBack) { // back button
                        Icon(
                            imageVector     = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint            = PurpleText
                        )
                    }
                },
                title = {
                    Box(Modifier.fillMaxWidth()) {
                        Row(
                            Modifier.align(Alignment.Center).offset(x = (-16).dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image( // genrechain logo
                                painter            = painterResource(R.drawable.genrechain_logo),
                                contentDescription = "Logo",
                                modifier           = Modifier.size(32.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Genre Chain",
                                color     = PurpleText,
                                style     = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                },
                actions = {
                    Spacer(Modifier.width(56.dp))
                }
            )
        },
        scaffoldState   = scaffoldState,
        backgroundColor = MaterialTheme.colors.background
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ArtistCard(curr, "Current")
                ArtistCard(targ, "Target")
            }

            Spacer(Modifier.height(30.dp))

            OutlinedTextField(
                value = guessQuery,
                onValueChange = { guessQuery = it },
                label = { Text("Search next artist", color = PurpleText) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = PurpleText),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    cursorColor = PurpleText,
                    focusedBorderColor = MaterialTheme.colors.onSurface,
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
                enabled = guessQuery.isNotBlank(),
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
                                            val currGenres = curr.genres.map { it.lowercase().trim() }.toSet()
                                            val guessGenres = guessFull.genres.map { it.lowercase().trim() }.toSet()
                                            val common = currGenres.intersect(guessGenres)
                                            if (common.isNotEmpty()) {
                                                current = guessFull
                                                resultMessage = "✅ Nice! Shared: ${common.joinToString()}"
                                                if (guessGenres.intersect(targ.genres.map { it.lowercase().trim() }.toSet()).isNotEmpty()) {
                                                    showWinDialog = true
                                                }
                                            } else {
                                                resultMessage = "❌ No genre in common with ${curr.name}."
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
                title = { Text("🎉 You Win!", color = PurpleText, style = MaterialTheme.typography.h6) },
                text = {
                    Text(
                        "You’ve connected to your target artist by genre!\nTry again to beat your chain length.",
                        color = PurpleText
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showWinDialog = false }) {
                        Text("OK", color = PurpleText)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showWinDialog = false
                        onRestart() // <- Restart logic
                    }) {
                        Text("Restart", color = PurpleText)
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
        elevation = 6.dp,
        modifier = Modifier
            .width(160.dp)
            .padding(4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            Box(
                Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colors.onSurface.copy(alpha = 0.1f))
            )
            Spacer(Modifier.height(8.dp))
            Text(
                artist.name,
                color = PurpleText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.subtitle1
            )
            Text(
                text = label,
                style = MaterialTheme.typography.caption,
                color = PurpleText.copy(alpha = 0.8f)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = artist.genres.take(2).joinToString(),
                style = MaterialTheme.typography.caption,
                textAlign = TextAlign.Center,
                color = PurpleText.copy(alpha = 0.6f)
            )
        }
    }
}
