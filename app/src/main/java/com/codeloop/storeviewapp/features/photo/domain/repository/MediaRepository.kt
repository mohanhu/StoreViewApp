package com.codeloop.storeviewapp.features.photo.domain.repository

interface MediaRepository {

    fun getImageFolders()

    fun fetchImages(relativePath:String)


    fun getVideoFolders()

    fun fetchVideos(relativePath:String)

    fun getAudioFolders()

    fun fetchAudio(relativePath:String)

}
