package info.mik.mru.github;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Auto-Created on 2018-05-16.
 */

public class ModelRepoResponse {

    @SerializedName("items")
    private List<ModelRepo> items;
    public List<ModelRepo> getItems() {
        return items;
    }

}
