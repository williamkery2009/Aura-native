package ai.arena.aura.core.media.playback

import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuraPlaybackService : MediaSessionService() {
    @Inject lateinit var controller: AuraPlaybackController
    private var session: MediaSession? = null
    override fun onCreate() {
        super.onCreate()
        session = controller.createSession(this)
    }
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = session
    override fun onDestroy() {
        session?.release()
        session = null
        super.onDestroy()
    }
}
