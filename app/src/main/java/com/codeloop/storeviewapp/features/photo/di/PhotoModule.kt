package com.codeloop.storeviewapp.features.photo.di

import com.codeloop.storeviewapp.features.photo.data.local.MediaFileRepositoryImpl
import com.codeloop.storeviewapp.features.photo.data.local.PhotoLocalRepositoryImpl
import com.codeloop.storeviewapp.features.photo.domain.repository.MediaFileRepository
import com.codeloop.storeviewapp.features.photo.domain.repository.PhotoLocalRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface PhotoModule {
    @Binds
    @Singleton
    fun bindPhotoLocalRepository(photoLocalRepositoryImpl: PhotoLocalRepositoryImpl): PhotoLocalRepository

    @Binds
    @Singleton
    fun bindMediaFiles(mediaFileRepositoryImpl: MediaFileRepositoryImpl): MediaFileRepository

}