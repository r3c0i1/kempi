package com.r3c0i1.kempa.core.di

import android.content.Context
import com.r3c0i1.kempa.features.auth.data.local.TokenStorage
import com.r3c0i1.kempa.features.auth.data.remote.AuthApiService
import com.r3c0i1.kempa.features.auth.data.repository.AuthRepositoryImpl
import com.r3c0i1.kempa.features.auth.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideTokenStorage(
        @ApplicationContext context: Context
    ): TokenStorage {
        return TokenStorage(context)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        api: AuthApiService,
        tokenStorage: TokenStorage
    ): AuthRepository {
        return AuthRepositoryImpl(api, tokenStorage)
    }
}