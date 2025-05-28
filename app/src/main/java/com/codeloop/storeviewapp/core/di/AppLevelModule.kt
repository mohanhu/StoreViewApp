package com.codeloop.storeviewapp.core.di

import android.content.Context
import androidx.room.Room
import com.codeloop.storeviewapp.core.data.local.CommonAppDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class AppLevelModule {

    @Provides
    fun providePhotoFolderDao(
        @ApplicationContext context: Context
    ): CommonAppDB {
        return Room.databaseBuilder(
            context, CommonAppDB::class.java, "common_app_db"
        ).fallbackToDestructiveMigration()
            .build()
    }

}