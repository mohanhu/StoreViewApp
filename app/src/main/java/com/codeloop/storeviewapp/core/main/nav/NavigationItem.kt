package com.codeloop.storeviewapp.core.main.nav

import kotlinx.serialization.Serializable

@Serializable
sealed class NavigationItem(val route: String) {
    @Serializable
    data object Photo : NavigationItem(route = "photo")

    @Serializable
    data class PhotoList (val folderName:String ? = ""): NavigationItem(route = "photoList")

    @Serializable
    data object Video : NavigationItem(route = "video")

    @Serializable
    data class VideoList (val folderName:String ? = ""): NavigationItem(route = "videoList")

    @Serializable
    data object Music : NavigationItem(route = "music")

    @Serializable
    data class MusicList (val folderName : String ?= "" ) : NavigationItem(route = "musicList")

    @Serializable
    data object Contact : NavigationItem(route = "contact")

    @Serializable
    data object Documents : NavigationItem(route = "documents")
}