package project.main.api

import com.wang.gogolook.test.api.SearchModel
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET
    fun get(@Url url: String): Call<Any>


    @GET("/api/")
    fun search(@QueryMap map: Map<String, String>): Call<SearchModel>

}