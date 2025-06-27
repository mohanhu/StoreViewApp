package com.codeloop.storeviewapp.features.photo.di

import com.codeloop.storeviewapp.features.photo.data.local.MediaFileLocalRepositoryImpl
import com.codeloop.storeviewapp.features.photo.data.local.MediaRepositoryImpl
import com.codeloop.storeviewapp.features.photo.data.local.PhotoLocalRepositoryImpl
import com.codeloop.storeviewapp.features.photo.domain.repository.MediaFileLocalRepository
import com.codeloop.storeviewapp.features.photo.domain.repository.MediaRepository
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
    fun bindMediaFiles(mediaFileRepositoryImpl: MediaFileLocalRepositoryImpl): MediaFileLocalRepository

    @Binds
    @Singleton
    fun bindMediaRepository(mediaRepositoryImpl: MediaRepositoryImpl): MediaRepository

}