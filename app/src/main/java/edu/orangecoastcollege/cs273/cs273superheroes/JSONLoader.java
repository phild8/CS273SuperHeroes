package edu.orangecoastcollege.cs273.cs273superheroes;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Class loads Country data from a formatted JSON (JavaScript Object Notation) file.
 * Populates data model (Country) with data.
 */
public class JSONLoader {

    /**
     * Loads JSON data from a file in the assets directory.
     *
     * @param context The activity from which the data is loaded.
     * @throws IOException If there is an error reading from the JSON file.
     */
    public static List<Superhero> loadJSONFromAsset(Context context) throws IOException {
        List<Superhero> allSuperheroesList = new ArrayList<>();
        String json = null;
        InputStream is = context.getAssets().open("cs273superheroes.json");
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        json = new String(buffer, "UTF-8");


        // Loop through all the countries in the JSON data, create a Country
        // object for each and add the object to the allCountriesList

        try {
            JSONObject jsonRootObject = new JSONObject(json);
            JSONArray allSuperheroesJSON = jsonRootObject.getJSONArray("CS273Superheroes");

            int length = allSuperheroesJSON.length();
            for (int i = 0; i < length; ++i)
            {
                JSONObject superheroObject = allSuperheroesJSON.getJSONObject(i);
                String userName = superheroObject.getString("Username");
                String name = superheroObject.getString("Name");
                String superpower = superheroObject.getString("Superpower");
                String oneThing = superheroObject.getString("OneThing");
                Superhero newSuperhero = new Superhero(userName, name, superpower, oneThing);
                allSuperheroesList.add(newSuperhero);
            }

        } catch (JSONException e) {
            Log.e("Flag Quiz", e.getMessage());
        }

        return allSuperheroesList;
    }
}
