package ir.pooyadev.data.remote

import ir.pooyadev.data.remote.model.TaskDto
import ir.pooyadev.domain.model.remote.RemoteResult
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val apiInterface: ApiInterface
) {

    suspend fun fetchTasks(): RemoteResult<List<TaskDto>> {
        return try {
            val response = apiInterface.fetchRemoteTasks()
            if (response.isSuccessful) {
                response.body()?.let {
                    RemoteResult.Success(it)
                } ?: RemoteResult.Error("Response body is empty")
            } else {
                RemoteResult.Error("Error: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            RemoteResult.Error(e.message ?: "An unknown error occurred")
        }
    }

}