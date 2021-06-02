package com.rerere.iwara4a.di

import com.rerere.iwara4a.api.IwaraApi
import com.rerere.iwara4a.api.IwaraApiImpl
import com.rerere.iwara4a.api.service.IwaraParser
import com.rerere.iwara4a.api.service.IwaraService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideRetrofitClient(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl("http://ecchi.iwara.tv/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideIwaraParser(okHttpClient: OkHttpClient) = IwaraParser(okHttpClient)

    @Provides
    @Singleton
    fun provideIwaraService(retrofit: Retrofit): IwaraService = retrofit
        .create(IwaraService::class.java)

    @Provides
    @Singleton
    fun provideIwaraApi(iwaraParser: IwaraParser, iwaraService: IwaraService): IwaraApi =
        IwaraApiImpl(iwaraParser, iwaraService)
}