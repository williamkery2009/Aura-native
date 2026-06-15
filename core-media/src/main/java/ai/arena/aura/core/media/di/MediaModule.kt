package ai.arena.aura.core.media.di

import ai.arena.aura.core.domain.repository.PlaybackController
import ai.arena.aura.core.media.playback.AuraPlaybackController
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MediaModule {
    @Binds abstract fun bindPlaybackController(controller: AuraPlaybackController): PlaybackController
}
