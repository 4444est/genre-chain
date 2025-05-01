package com.example.genrechain.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.genrechain.data.remote.dto.ArtistDto
import com.example.genrechain.ui.theme.PurpleText
import com.example.genrechain.viewmodel.GameViewModel
import com.example.genrechain.R
import androidx.compose.foundation.Image
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource

@Composable
fun StartScreen(
    onStartClick: (ArtistDto?) -> Unit,
    onTargetClick: (ArtistDto?) -> Unit,
    onStartGame: () -> Unit
) {
    val vm: GameViewModel = viewModel()

    var query by remember { mutableStateOf("") }
    var targetQuery by remember { mutableStateOf("") }
    var activeSection by remember { mutableStateOf(0) }
    var listVisible by remember { mutableStateOf(false) }

    val results by vm.searchResults.collectAsState()
    val error by vm.error.collectAsState()
    val fullStart by vm.startArtist.collectAsState()
    val fullTarget by vm.targetArtist.collectAsState()

    LaunchedEffect(fullStart) { fullStart?.let(onStartClick) }
    LaunchedEffect(fullTarget) { fullTarget?.let(onTargetClick) }

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.background,
                elevation       = 0.dp,
                // stub nav icon to reserve space
                navigationIcon = {
                    IconButton(onClick = { /* no-op */ }, enabled = false) {
                        Icon(
                            imageVector     = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint            = Color.Transparent
                        )
                    }
                },
                title = {
                    Box(Modifier.fillMaxWidth()) {
                        Row(
                            Modifier.align(Alignment.Center).offset(x = (-16).dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
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
                // reserve same width on the right
                actions = {
                    Spacer(Modifier.width(56.dp))
                }
            )
        },
        backgroundColor = MaterialTheme.colors.background
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            SearchCard(
                label = "Search start artist",
                query = query,
                onQueryChange = { query = it },
                onSearch = {
                    vm.search(query)
                    activeSection = 1
                    listVisible = true
                },
                results = results,
                visible = listVisible && activeSection == 1,
                onItemClick = {
                    vm.onStartClicked(it)
                    listVisible = false
                }
            )

            Spacer(Modifier.height(16.dp))

            SearchCard(
                label = "Search target artist",
                query = targetQuery,
                onQueryChange = { targetQuery = it },
                onSearch = {
                    vm.search(targetQuery)
                    activeSection = 2
                    listVisible = true
                },
                results = results,
                visible = listVisible && activeSection == 2,
                onItemClick = {
                    vm.onTargetClicked(it)
                    listVisible = false
                }
            )

            Spacer(Modifier.weight(1f))

            error?.let {
                Text("⚠️ Error: $it", color = MaterialTheme.colors.error)
                Spacer(Modifier.height(8.dp))
            }

            fullStart?.let { artist ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Start: ${artist.name}",
                        color = PurpleText,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = {
                        vm.clearStart()
                        onStartClick(null)
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Clear start",
                            tint = PurpleText
                        )
                    }
                }
                Text(
                    "Genres: ${artist.genres.joinToString()}",
                    color = PurpleText,
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                )
            }

            fullTarget?.let { artist ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Target: ${artist.name}",
                        color = PurpleText,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = {
                        vm.clearTarget()
                        onTargetClick(null)
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Clear target",
                            tint = PurpleText
                        )
                    }
                }
                Text(
                    "Genres: ${artist.genres.joinToString()}",
                    color = PurpleText,
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                )
            }

            if (fullStart != null && fullTarget != null) {
                Button(
                    onClick = onStartGame,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.background
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colors.onSurface)
                ) {
                    Text("Start Game", color = PurpleText)
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = {
                        vm.clearStart()
                        vm.clearTarget()
                        onStartClick(null)
                        onTargetClick(null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.background
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colors.onSurface)
                ) {
                    Text("Clear All", color = PurpleText)
                }
            }
        }
    }
}

@Composable
private fun SearchCard(
    label: String,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    results: List<ArtistDto>,
    visible: Boolean,
    onItemClick: (ArtistDto) -> Unit
) {
    Column {
        Card(
            backgroundColor = MaterialTheme.colors.background,
            elevation = 8.dp,
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, MaterialTheme.colors.onSurface)
        ) {
            Column(Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    label = { Text(label, color = PurpleText) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(color = PurpleText),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colors.onSurface,
                        unfocusedBorderColor = PurpleText.copy(alpha = 0.5f),
                        cursorColor = PurpleText
                    )
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = onSearch,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.background
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colors.onSurface)
                ) {
                    Text("Search", color = PurpleText)
                }

                if (visible && results.isNotEmpty()) {
                    Surface(
                        color = MaterialTheme.colors.surface,
                        elevation = 8.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .padding(top = 8.dp)
                            .zIndex(1f)
                    ) {
                        LazyColumn {
                            items(results) { artist ->
                                Text(
                                    artist.name,
                                    color = PurpleText,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onItemClick(artist) }
                                        .padding(12.dp)
                                )
                                Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f))
                            }
                        }
                    }
                }
            }
        }
    }
}
