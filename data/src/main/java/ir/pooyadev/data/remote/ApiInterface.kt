package ir.pooyadev.data.remote

import ir.pooyadev.data.remote.model.TaskDto
import retrofit2.Response
import retrofit2.http.GET

interface ApiInterface {

    @GET("v1/tasks")
    suspend fun fetchRemoteTasks(): Response<List<TaskDto>>

}