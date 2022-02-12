package com.copperleaf.ballast.repository.cache

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * Returns true if this cached variable indicates a state which should show a progress indicator in the UI.
 */
public fun <T : Any> Cached<T>.isLoading(): Boolean {
    return when (this) {
        is Cached.Fetching -> true
        is Cached.FetchingFailed -> false
        is Cached.NotLoaded -> true
        is Cached.Value -> false
    }
}

/**
 * Returns true if the Repository has not started fetching from the remote source yet, or if it has started fetching and
 * did not have a prior value (thus, is the first time attempting to load this value).
 */
public fun <T : Any> Cached<T>.isFirstLoad(): Boolean {
    return when (this) {
        is Cached.Fetching -> this.cachedValue == null
        is Cached.FetchingFailed -> false
        is Cached.NotLoaded -> true
        is Cached.Value -> false
    }
}

/**
 * Get the value if the remote data source returned valid data. This method will not return previously cached values
 * once the cache has started refreshing, but instead will return the result of [defaultValue].
 */
public fun <T : Any> Cached<T>.getValueOrElse(defaultValue: () -> T): T {
    return when (this) {
        is Cached.Fetching -> defaultValue()
        is Cached.FetchingFailed -> defaultValue()
        is Cached.NotLoaded -> defaultValue()
        is Cached.Value -> value
    }
}

/**
 * Get the value if the remote data source returned valid data. This method will not return previously cached values
 * once the cache has started refreshing, but instead will return null.
 */
public fun <T : Any> Cached<T>.getValueOrNull(): T? {
    return when (this) {
        is Cached.Fetching -> null
        is Cached.FetchingFailed -> null
        is Cached.NotLoaded -> null
        is Cached.Value -> value
    }
}

/**
 * Get the value if the remote data source returned valid data. If the cache is currently refreshing, this will return
 * the previous cached value so the UI can continue displaying it while it adds a progress indicator over it. If there
 * was no previosuly cached value to be displayed, return the result of [defaultValue].
 */
public fun <T : Any> Cached<T>.getCachedOrElse(defaultValue: () -> T): T {
    return when (this) {
        is Cached.Fetching -> cachedValue ?: defaultValue()
        is Cached.FetchingFailed -> cachedValue ?: defaultValue()
        is Cached.NotLoaded -> previousCachedValue ?: defaultValue()
        is Cached.Value -> value
    }
}

/**
 * Get the value if the remote data source returned valid data. If the cache is currently refreshing, this will return
 * the previous cached value so the UI can continue displaying it while it adds a progress indicator over it. If there
 * was no previosuly cached value to be displayed, return null.
 */
public fun <T : Any> Cached<T>.getCachedOrNull(): T? {
    return when (this) {
        is Cached.Fetching -> cachedValue
        is Cached.FetchingFailed -> cachedValue
        is Cached.NotLoaded -> previousCachedValue
        is Cached.Value -> value
    }
}

/**
 * Get the value if the remote data source returned valid data. If the cache is currently refreshing, this will return
 * the previous cached value so the UI can continue displaying it while it adds a progress indicator over it. If there
 * was no previosuly cached value to be displayed, return the result of [defaultValue].
 */
public fun <T : Any> Cached<List<T>>.getCachedOrEmptyList(): List<T> {
    return when (this) {
        is Cached.Fetching -> cachedValue ?: emptyList()
        is Cached.FetchingFailed -> cachedValue ?: emptyList()
        is Cached.NotLoaded -> previousCachedValue ?: emptyList()
        is Cached.Value -> value
    }
}

/**
 * Determines whether the value of this cache is considered "valid", according to the provided [validator] function. The
 * cache is considered invalid when fetching failed, or when fetching completed but fails to pass the [validator]
 * function. Data that is currently loading is considered valid.
 */
public fun <T : Any> Cached<T>.isValid(validator: (T) -> Boolean): Boolean {
    return when (this) {
        is Cached.Fetching -> true
        is Cached.FetchingFailed -> false
        is Cached.NotLoaded -> true
        is Cached.Value -> validator(value)
    }
}

/**
 * Unwrap the cached value, apply the [transform] function to it if there is a current or previously-cached value, and
 * then wrap it in the same status.
 */
public fun <T : Any, U : Any> Cached<T>.map(transform: (T) -> U): Cached<U> {
    return when (this) {
        is Cached.Fetching -> Cached.Fetching(cachedValue?.let { transform(it) })
        is Cached.FetchingFailed -> Cached.FetchingFailed(error, cachedValue?.let { transform(it) })
        is Cached.NotLoaded -> Cached.NotLoaded(previousCachedValue?.let { transform(it) })
        is Cached.Value -> Cached.Value(transform(value))
    }
}

/**
 * Wait for the cache to finish loading, and return the result.
 */
public suspend fun <T : Any> Flow<Cached<T>>.awaitValue(): Cached<T> {
    return this.first { it is Cached.Value || it is Cached.FetchingFailed }
}
