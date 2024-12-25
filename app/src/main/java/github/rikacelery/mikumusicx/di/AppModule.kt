package github.rikacelery.mikumusicx.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import github.rikacelery.mikumusicx.service.MusicController
import github.rikacelery.mikumusicx.service.MusicControllerImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideMusicController(
        @ApplicationContext context: Context,
    ): MusicController = MusicControllerImpl(context)
}
