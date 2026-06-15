package ai.arena.aura.core.common

sealed interface AuraResult<out T> {
    data class Success<T>(val value: T) : AuraResult<T>
    data class Failure(val throwable: Throwable, val userMessage: String = throwable.message ?: "Aura operation failed") : AuraResult<Nothing>
}
