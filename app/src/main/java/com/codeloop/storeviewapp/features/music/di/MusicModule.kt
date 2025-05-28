package com.codeloop.storeviewapp.features.music.di

import com.codeloop.storeviewapp.features.music.data.local.MusicLocalRepositoryImpl
import com.codeloop.storeviewapp.features.music.domain.repository.MusicLocalRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface MusicModule {

    @Binds
    @Singleton
    fun bindMusicLocalRepository(musicLocalRepositoryImpl: MusicLocalRepositoryImpl): MusicLocalRepository

}