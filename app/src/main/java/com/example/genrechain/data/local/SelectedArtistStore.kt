package com.example.genrechain.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.genrechain.data.remote.dto.ArtistDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// property-delegate for your DataStore<Preferences>
private val Context.ds by preferencesDataStore("genre_chain_prefs")

// two string keys, one for “start” and one for “target”
object PrefKeys {
    val START  = stringPreferencesKey("start_artist")
    val TARGET = stringPreferencesKey("target_artist")
}

class SelectedArtistStore(private val context: Context) {
    private val ds = context.ds

    // persist start artist
    suspend fun saveStart(artist: ArtistDto) {
        ds.edit { prefs ->
            prefs[PrefKeys.START] = Json.encodeToString(artist)
        }
    }

    // persist target artist
    suspend fun saveTarget(artist: ArtistDto) {
        ds.edit { prefs ->
            prefs[PrefKeys.TARGET] = Json.encodeToString(artist)
        }
    }

    suspend fun clearStart() = ds.edit { it.remove(PrefKeys.START) }

    suspend fun clearTarget() = ds.edit { it.remove(PrefKeys.TARGET) }


    // flow of start artist (or null)
    val startFlow: Flow<ArtistDto?> = ds.data.map { prefs ->
        prefs[PrefKeys.START]?.let { Json.decodeFromString<ArtistDto>(it) }
    }

    // flow of target artist (or null)
    val targetFlow: Flow<ArtistDto?> = ds.data.map { prefs ->
        prefs[PrefKeys.TARGET]?.let { Json.decodeFromString<ArtistDto>(it) }
    }

}
