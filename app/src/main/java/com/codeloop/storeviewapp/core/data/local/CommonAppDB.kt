package com.codeloop.storeviewapp.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.codeloop.storeviewapp.features.music.data.local.MusicFolderDao
import com.codeloop.storeviewapp.features.music.data.local.MusicFolderEntity
import com.codeloop.storeviewapp.features.photo.data.local.MediaFileDao
import com.codeloop.storeviewapp.features.photo.data.local.MediaFolderEntity
import com.codeloop.storeviewapp.features.photo.data.local.PhotoFolderDao
import com.codeloop.storeviewapp.features.photo.data.local.PhotoFolderEntity
import com.codeloop.storeviewapp.features.video.data.local.VideoFolderDao
import com.codeloop.storeviewapp.features.video.data.local.VideoFolderEntity

@Database(
    entities = [
        PhotoFolderEntity::class,
        MusicFolderEntity::class,
        VideoFolderEntity::class,
        MediaFolderEntity::class,
    ],
    version = 2,
    exportSchema = false
)
abstract class CommonAppDB : RoomDatabase() {

    abstract val photoFolderDao: PhotoFolderDao

    abstract val musicFolderDao: MusicFolderDao

    abstract val videoFolderDao: VideoFolderDao

    abstract val mediaFolderDao: MediaFileDao

}