package com.example.musicplayer.domain.repository

import github.rikacelery.mikumusicx.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    suspend fun getSongs(): Flow<List<Song>>
    fun getDefaultSongs(): List<Song>
    suspend fun addSong(song: Song)
    fun removeSong(string: String)
}