package org.example.project.core.domain

import kotlin.Error

// Implement explicit, safe and composable logic to handle results instead of exception based
//Typed Errors: -> Define Error types instead of using Throwable
//Clear function signature: -> every function explicitly says what Data D and error E it returns
//Functional Helpers: -> map, onSuccess, onError -> transform or react to results cleanly without try/catch
//Predictable flow: -> no hidden exceptions, easy to tests, works well in coroutines and clean-arch layers:

// More details:
//=> What this custom Result<D, E> offers:
//    -> Typed, compiled-time checked errors E: Error -> explicit error handling:
//    -> Exhaustive when thanks to sealed typed
//    -> no exception control flow: encorage pure data flow not try/catch
//    -> Composable transformers: clean pipelines with maps, onSuccess, onError, typealias, EmptyResults<E>= Result<Unit, E
//
//=> Why not simple results
//    -> API clarity: with typed errors, function signature can communicates exactly what can go wrong.
//    -> Interop/Serialization: Result can be awkword for KMP and serialization, against this sealed custom result that is serializable, stable accross modules
//    -> Failure is always throwable: stdlin Result models errors as exceptions, if you want typed domain errors: NetwordkError, AuthError, need to encode them inside a Throwable



/**
 * A closed (sealed) result type that models either a success with data D
 * or a failure with a typed domain error E.
 *
 * Benefits vs kotlin.Result:
 * - Typed error channel (E) instead of Throwable
 * - Exhaustive when handling
 * - Easy to compose/map without exceptions
 */
sealed interface Result<out D, out E: org.example.project.core.domain.Error> {
    data class Success<out D>(val data: D): Result<D, Nothing>
    data class Error<out E: org.example.project.core.domain.Error>(val error: E):
        Result<Nothing, E>
}


/**
 * Transform the success value T into R while preserving the same error type E.
 * - If this is Success, apply the mapper to data.
 * - If this is Error, pass the error through unchanged.
 *
 * Usage: repoResult.map { dto -> dto.toDomain() }
 */
inline fun <T, E: org.example.project.core.domain.Error, R> Result<T, E>.map(map: (T) -> R): Result<R, E> {
    return when(this) {
        is Result.Error -> Result.Error(error)
        is Result.Success -> Result.Success(map(data))
    }
}


/**
 * Convert any Result<T, E> into Result<Unit, E> (i.e., data is irrelevant).
 * Useful after commands (POST/DELETE) where you only care about success/failure.
 *
 * Implemented by mapping the success value to Unit.
 */
fun <T, E: org.example.project.core.domain.Error> Result<T, E>.asEmptyDataResult(): EmptyResult<E> {
    return map {  } // Lambda returns Unit explicitly
}

/**
 * Run a side-effect only when this is Success, then return the original Result unchanged.
 * Great for logging, analytics, or UI side-effects without breaking the chain.
 *
 * Usage: result.onSuccess { data -> showToast("Loaded ${data.size}") }
 */
inline fun <T, E: org.example.project.core.domain.Error> Result<T, E>.onSuccess(action: (T) -> Unit): Result<T, E> {
    return when(this) {
        is Result.Error -> this
        is Result.Success -> {
            action(data) //side effect
            this    //Keep original results for further chaining
        }
    }
}

/**
 * Run a side-effect only when this is Error, then return the original Result unchanged.
 * Great for logging errors, mapping to UI toasts, metrics, etc.
 *
 * Usage: result.onError { e -> logger.warn("Failed: $e") }
 */
inline fun <T, E: org.example.project.core.domain.Error> Result<T, E>.onError(action: (E) -> Unit): Result<T, E> {
    return when(this) {
        is Result.Error -> {
            action(error) //side effect
            this    // keep orginal results
        }
        is Result.Success -> this
    }
}


/**
 * Convenience alias for "Result with no meaningful data".
 * Use when an operation's success has no payload (Unit), but failures are typed.
 *
 * Example: suspend fun deleteBook(id: Id): EmptyResult<DomainError>
 */
typealias EmptyResult<E> = Result<Unit, E>
