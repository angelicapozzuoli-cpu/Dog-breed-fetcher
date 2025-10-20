package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        String url = "https://dog.ceo/api/breed/" + breed + "/list";
        List<String> subBreeds = new ArrayList<>();
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            String jsonData = response.body().string();
            JSONObject json = new JSONObject(jsonData);
            //check to see if the breed was not found:
            if (!json.getString("status").equals("success")) {
                throw new BreedNotFoundException("Breed not found: " + breed);
            }
            //get the list of sub-breeds, since it was a success implied by the if statement
            JSONArray subBreedsArray = json.getJSONArray("message");
            for (int i = 0; i < subBreedsArray.length(); i++) {
                subBreeds.add(subBreedsArray.getString(i));
            }
        } catch (IOException | org.json.JSONException e) {
            throw new BreedNotFoundException("Failed to fetch breed: " + breed);
        }
        return new ArrayList<>(subBreeds);
    }
}