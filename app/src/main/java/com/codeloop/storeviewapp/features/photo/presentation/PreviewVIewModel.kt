package com.codeloop.storeviewapp.features.photo.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PreviewVIewModel @Inject constructor(
    internal val player: Player
) : ViewModel() {

    init {
        player.prepare()
    }

    fun setMediaItem(uri: Uri, ready: (Player) -> Unit) {
        player.stop()
        player.clearMediaItems()
        player.addMediaItem(MediaItem.fromUri(uri))
        player.setMediaItem(MediaItem.fromUri(uri))
        player.prepare()
        player.playWhenReady = true
        player.repeatMode = Player.REPEAT_MODE_ALL
        ready.invoke(player)
    }
    fun playerRelease() {
        player.stop()
        player.clearMediaItems()
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}