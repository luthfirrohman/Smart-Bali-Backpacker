package com.smart.smartbalibackpaker.core.di

import android.content.Context
import com.smart.smartbalibackpaker.core.data.TourismRepository
import com.smart.smartbalibackpaker.core.data.source.local.LocalDataSource
import com.smart.smartbalibackpaker.core.data.source.local.room.TourismDatabase
import com.smart.smartbalibackpaker.core.data.source.remote.RemoteDataSource
import com.smart.smartbalibackpaker.core.utils.AppExecutors

object Injection {
    fun provideRepository(context: Context): TourismRepository {
        val database = TourismDatabase.getInstance(context)
        val remoteDataSource = RemoteDataSource.getInstance()
        val localDataSource = LocalDataSource.getInstance(database.tourismDao())
        val appExecutors = AppExecutors()
        return TourismRepository.getInstance(remoteDataSource, localDataSource, appExecutors)
    }
}