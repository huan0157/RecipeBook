package com.cst2335.recipebook.RecipeAsyncTask;

import static com.cst2335.recipebook.RecipeAsyncTask.AsyncActivity.RECIPE_QUERY;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.View;

import com.cst2335.recipebook.Database.RecipeHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class RecipeTask extends AsyncTask<String, Integer, String> {

         private RecipeHelper dbHelper = null;
         AsyncActivity async=new AsyncActivity();

    private static String baseUrl = "https://api.spoonacular.com/recipes";
    private static String apiKey = "7752c09eac9d40e78339d03c78d7810c";

    private String getRecipeSearchUrl() {

        String ingredients = getIntent().getStringExtra(RECIPE_QUERY).replace(" ", "");
        if(ingredients.isEmpty())
            return "";
        else
            return String.format("%s/findByIngredients?apiKey=%s&number=10&ingredients=%s",
                    baseUrl, apiKey, ingredients);
    }

    private String getSummaryUrl(String id) {
        return String.format("%s/%s/summary?apiKey=%s", baseUrl, id, apiKey);
    }

        private String getRecipeDetail(String id) {
            String detail = "";
            try {
                URL url = new URL(RecipeTask.this.getSummaryUrl(id));
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inStream = urlConnection.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                JSONObject r = new JSONObject(sb.toString());
                detail = r.getString("summary");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return detail;
        }

        /**
         * This Overrides AsyncTask.doInBackGround()
         *
         * It pulls the search results based on what the droids want you to think.
         *
         * Basically it switches between searching Chicken and Lasagna
         *
         * @param @See AsyncTask.doInBackground()
         * @return @See AsyncTask.doInBackground()
         */
        @Override
        protected String doInBackground(String... strings) {

            //String jsonUrl = "https://api.spoonacular.com/recipes/complexSearch?query=pasta&maxFat=25&number=2&apiKey=2311513282b7432684777caf629d344a";
            String jsonUrl = "https://api.spoonacular.com/recipes/findByIngredients?ingredients=egg&number=2&apiKey=2311513282b7432684777caf629d344a";
            //private static String baseUrl = "https://api.spoonacular.com/recipes/";
            //private static String apiKey = "apiKey=2311513282b7432684777caf629d344a";

            dbHelper.dropTable(db, TABLE_NAME);
            dbHelper.createTable(db, TABLE_NAME);

            ArrayList<RecipeEntry> recipes = new ArrayList<RecipeEntry>();

            try {
                URL url = new URL(RecipeTask.this.getRecipeSearchUrl());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inStream = urlConnection.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                JSONArray json_recipes = new JSONArray(sb.toString());

                for (int j = 0; j < json_recipes.length(); j++){

                    publishProgress(j*(100/json_recipes.length()));

                    JSONObject r = json_recipes.getJSONObject(j);
                    RecipeEntry ent = new RecipeEntry();
                    ent.id = r.getString("id");
                    ent.title = r.getString("title");
                    ent.image_url = r.getString("image");
                    recipes.add(ent);


                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            for (RecipeEntry recipe : recipes) {
                recipe.details = getRecipeDetail(recipe.id);
                dbHelper.insertItem(TABLE_NAME, recipe.title, recipe.details, recipe.id, recipe.image_url);
            }

            return null;
        }


        /**This method Overrides the super class's onPostExecute.
         * It calls the super method and turns back on the visibility for the search button and text
         * It saves the value of the boolean that keeps track of what was last searched sends us back to our listView of the results
         *
         * @param results  @See AsyncTask.onPostExecute()
         */
        @Override                   //Type 3 of Inner Created Class
        protected void onPostExecute(String results) {
            super.onPostExecute(results);

            searchBtn.setVisibility(View.VISIBLE);
            editText.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);

            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
            SharedPreferences.Editor editor = pref.edit();

            editor.commit();

            Intent intent = new Intent(RecipeTask.this, RecipeSearch.class);
            startActivityForResult(intent, 30);

        }

        /**This method takes the param values to update our progress bar.
         * it keeps the search button and text field out of view while the progress bar is used.
         * @param values
         */
        @Override
        protected void onProgressUpdate(Integer ... values) {
            super.onProgressUpdate(values);
            editText.setVisibility(View.INVISIBLE);
            searchBtn.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(values[0]);

        }
    }

}
