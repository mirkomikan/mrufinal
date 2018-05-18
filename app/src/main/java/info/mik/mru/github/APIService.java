package info.mik.mru.github;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by mik on 2018-05-16.
 */

public interface APIService {

   @GET("/search/repositories")
   Call<ModelRepoResponse> getRepositoryList(@QueryMap(encoded = false)  Map<String,String> filter );

}
