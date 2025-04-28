package com.example.genrechain.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.genrechain.data.remote.dto.ArtistDto
import com.example.genrechain.data.repository.MusicBrainzRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel(
    private val repo: MusicBrainzRepository = MusicBrainzRepository()
) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<ArtistDto>>(emptyList())
    val searchResults: StateFlow<List<ArtistDto>> = _searchResults.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun search(name: String) {
        viewModelScope.launch {
            try {
                _searchResults.value = repo.searchArtist(name)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun haveGenreInCommon(artist1: ArtistDto, artist2: ArtistDto): Boolean {
        val artist1Genres = artist1.genres.map { it.lowercase() }
        val artist2Genres = artist2.genres.map { it.lowercase() }
        return artist1Genres.any { it in artist2Genres }
    }
}
