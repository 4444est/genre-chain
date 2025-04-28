package com.example.genrechain.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.genrechain.data.local.SelectedArtistStore
import com.example.genrechain.data.remote.dto.ArtistDto
import com.example.genrechain.data.repository.MusicBrainzRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    // now that we have the Application, we can wire up our repo/store
    private val store = SelectedArtistStore(application)
    private val repo  = MusicBrainzRepository(store = store)

    private val _searchResults = MutableStateFlow<List<ArtistDto>>(emptyList())
    val searchResults: StateFlow<List<ArtistDto>> = _searchResults.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _selected = MutableStateFlow<ArtistDto?>(null)
    val selected: StateFlow<ArtistDto?> = _selected.asStateFlow()

    init {
        // keep DataStore & in-memory in sync
        viewModelScope.launch {
            repo.savedArtistFlow().collect { _selected.value = it }
        }
    }

    fun search(name: String) {
        viewModelScope.launch {
            try {
                _searchResults.value = repo.searchArtist(name)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun onArtistClicked(artist: ArtistDto) {
        viewModelScope.launch {
            try {
                // **this** is the new lookup call that pulls genres
                val fullArtist = repo.lookupArtistWithGenres(artist.id)
                _selected.value = fullArtist
                repo.saveSelectedArtist(fullArtist)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    // your helper from before, unchanged
    fun haveGenreInCommon(a: ArtistDto, b: ArtistDto): Boolean {
        val g1 = a.genres.map(String::lowercase)
        val g2 = b.genres.map(String::lowercase)
        return g1.any { it in g2 }
    }
}
