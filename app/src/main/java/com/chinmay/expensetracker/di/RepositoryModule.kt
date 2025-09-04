package com.chinmay.expensetracker.com.chinmay.expensetracker.di


import android.content.Context
import com.chinmay.expensetracker.data.roomExpenseRepository.RoomExpenseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideRoomExpenseRepository(
        @ApplicationContext context: Context
    ): RoomExpenseRepository {
        return RoomExpenseRepository(context)
    }
}