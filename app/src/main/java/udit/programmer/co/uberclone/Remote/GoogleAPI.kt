package udit.programmer.co.uberclone.Remote

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

public interface GoogleAPI {

    @GET
    suspend fun getPath(@Url url: String): Call<String>

}