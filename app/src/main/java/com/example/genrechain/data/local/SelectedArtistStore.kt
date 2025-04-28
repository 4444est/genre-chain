package com.example.genrechain.data.local

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.genrechain.data.remote.dto.ArtistDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

// 1) Create your DataStore<Preferences> delegate on Context:
private val Context.dataStore by preferencesDataStore(name = "genre_chain_prefs")

class SelectedArtistStore(private val context: Context) {

    // 2) Use the built-in helper for String keys:
    private val SELECTED_ARTIST_KEY = stringPreferencesKey("selected_artist")

    /** Persist artist as JSON into Preferences */
    suspend fun save(artist: ArtistDto) {
        context.dataStore.edit { prefs ->
            // now 'prefs' is a MutablePreferences, and .set() is available
            prefs[SELECTED_ARTIST_KEY] = Json.encodeToString(artist)
        }
    }

    /** Observe the saved artist (or null) */
    val flow: Flow<ArtistDto?> = context.dataStore.data
        .map { prefs ->
            prefs[SELECTED_ARTIST_KEY]
                ?.let { Json.decodeFromString<ArtistDto>(it) }
        }
}
