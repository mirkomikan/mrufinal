package info.mik.mru.github;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mik on 2018-05-16.
 */

public class APIMaker {

    public static final String BASE_URL = "https://api.github.com";

    private Retrofit retrofit;

    public APIMaker() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public APIService getService() {

        return retrofit.create(APIService.class);
    }
}
