package com.codeloop.storeviewapp.features.video.di

import com.codeloop.storeviewapp.features.video.data.local.VideoLocalRepositoryImpl
import com.codeloop.storeviewapp.features.video.domain.repository.VideoLocalRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface VideoModule {

    @Binds
    @Singleton
    fun bindVideoRepository(videoRepositoryImpl: VideoLocalRepositoryImpl): VideoLocalRepository

}