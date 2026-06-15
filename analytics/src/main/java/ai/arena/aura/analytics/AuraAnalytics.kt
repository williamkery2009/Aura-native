package ai.arena.aura.analytics

import ai.arena.aura.core.domain.repository.ReplayRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuraAnalytics @Inject constructor(val replayRepository: ReplayRepository)
