package com.example.a0321;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
public interface ApiService {
    @GET("api/allfqa") //
    Call<List<FqaModel>> getData();

    @GET("api/alltrans") //
    Call<List<TranslateList>> getData2();
}
