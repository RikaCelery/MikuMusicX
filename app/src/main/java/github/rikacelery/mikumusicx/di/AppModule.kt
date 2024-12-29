package github.rikacelery.mikumusicx.di

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import github.rikacelery.mikumusicx.domain.other.FFTAudioProcessor
import com.example.musicplayer.domain.repository.MusicRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import github.rikacelery.mikumusicx.data.repository.MusicRepositoryImpl
import github.rikacelery.mikumusicx.domain.service.MusicController
import github.rikacelery.mikumusicx.domain.service.MusicControllerImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

//    @Singleton
//    @Provides
//    fun provideCollection() = FirebaseFirestore.getInstance().collection(Constants.SONG_COLLECTION)
//
//    @Singleton
//    @Provides
//    fun provideMusicDatabase(songCollection: CollectionReference) =
//        MusicRemoteDatabase(songCollection)


    @OptIn(UnstableApi::class)
    @Singleton
    @Provides
    fun provideFFT(): FFTAudioProcessor {
        return FFTAudioProcessor()
    }

    @Singleton
    @Provides
    fun provideMusicRepository(
        @ApplicationContext context: Context
    ): MusicRepository =
        MusicRepositoryImpl(context)

    @Singleton
    @Provides
    fun provideMusicController(
        @ApplicationContext context: Context,
        musicRepository: MusicRepository
    ): MusicController =
        MusicControllerImpl(context, musicRepository)
}