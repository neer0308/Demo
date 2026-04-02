package com.demo.newedgedemo.di

import com.demo.newedgedemo.data.remote.GrammarApiService
import com.demo.newedgedemo.data.repository.GrammarRepositoryImpl
import com.demo.newedgedemo.domain.repository.GrammarRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGrammarApiService(): GrammarApiService {
        return Retrofit.Builder()
            .baseUrl(GrammarApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GrammarApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideGrammarRepository(api: GrammarApiService): GrammarRepository {
        return GrammarRepositoryImpl(api)
    }
}
