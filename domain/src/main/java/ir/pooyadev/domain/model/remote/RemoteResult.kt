package ir.pooyadev.domain.model.remote

sealed class RemoteResult<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : RemoteResult<T>(data)
    class Error<T>(message: String, data: T? = null) : RemoteResult<T>(data, message)
    class Loading<T> : RemoteResult<T>()
}