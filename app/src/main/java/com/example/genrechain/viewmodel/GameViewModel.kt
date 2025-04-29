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
import android.util.Log
import kotlinx.coroutines.flow.first
import com.example.genrechain.data.remote.MbApi

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val store = SelectedArtistStore(application)
    private val repo  = MusicBrainzRepository(MbApi.service, store)

    private val _start  = MutableStateFlow<ArtistDto?>(null)
    val startArtist    = _start.asStateFlow()
    private val _target = MutableStateFlow<ArtistDto?>(null)
    val targetArtist   = _target.asStateFlow()
    private val _error  = MutableStateFlow<String?>(null)
    val error          = _error.asStateFlow()
    private val _search = MutableStateFlow<List<ArtistDto>>(emptyList())
    val searchResults  = _search.asStateFlow()

    init {
        // restore saved start/target from disk
        viewModelScope.launch {
            store.startFlow.collect { _start.value = it }
        }
        viewModelScope.launch {
            store.targetFlow.collect { _target.value = it }
        }
    }

    fun search(name: String) {
        viewModelScope.launch {
            try {
                _search.value = repo.searchArtist(name)
            } catch (t: Throwable) {
                _error.value = t.message
            }
        }
    }

    fun onStartClicked(artist: ArtistDto) {
        viewModelScope.launch {
            try {
                val full = repo.lookupArtistWithGenres(artist.id)
                _start.value = full
                repo.saveStartArtist(full)
            } catch (t: Throwable) {
                _error.value = t.message
            }
        }
    }

    fun onTargetClicked(artist: ArtistDto) {
        viewModelScope.launch {
            try {
                val full = repo.lookupArtistWithGenres(artist.id)
                _target.value = full
                repo.saveTargetArtist(full)
            } catch (t: Throwable) {
                _error.value = t.message
            }
        }
    }

    fun haveGenreInCommon(artist1: ArtistDto, artist2: ArtistDto): Boolean {
        val artist1Genres = artist1.genres.map { it.lowercase() }
        val artist2Genres = artist2.genres.map { it.lowercase() }
        return artist1Genres.any { it in artist2Genres }
    }
}