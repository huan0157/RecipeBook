package com.cst2335.recipebook.RecipeAsyncTask;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cst2335.recipebook.Database.RecipeHelper;
import com.cst2335.recipebook.R;

public class AsyncActivity extends AppCompatActivity {
    public static final String ACTIVITY_NAME = "RecipeAsyncTask";
    public static final String RECIPE_QUERY = "RECIPE_QUERY";

    private static final String TABLE_NAME = "resultTable";

    private static String baseUrl = "https://api.spoonacular.com/recipes";
    private static String apiKey = "7752c09eac9d40e78339d03c78d7810c";

    private SQLiteDatabase db = null;
    private RecipeHelper dbHelper = null;
    private ProgressBar progressBar = null;
    private Button searchBtn = null;
    private EditText editText = null;



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

    /**
     * This Method is the onCreate() for the class RecipeAsync. It sets up access to the database,
     * saves last search in SharedPrefrences and implements a progressBar for use with the RecipeQuery which
     * pulls the data from the weburl on the search. It also shows Toasts to confirm your search.
     *
     * @param savedInstanceState Same as param in @see AppCompatActivity.onCreate()
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_async);

        progressBar = (ProgressBar) findViewById(R.id.recipeSearchProgressBar);
        searchBtn =  findViewById(R.id.recipeSearchButton);
        editText = findViewById(R.id.searchEditText);


        super.onCreate(savedInstanceState);
        dbHelper = new RecipeHelper(this);
        db = dbHelper.getWritableDatabase();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();

        RecipeTask theQuery = new RecipeTask();
        theQuery.execute();

        String prompt;
        if(false) {
            prompt = "Hmm pretty sure you just asked to search chicken.";
        } else {
            prompt = "Oh looking for lasagna? Well most certainly one second please.";
        }
        Toast toast = Toast.makeText(this, prompt, Toast.LENGTH_SHORT);
        toast.show();


    }

    /**
     * This just makes it go back to the Calling Activity when it is finished passing the normal information
     *
     * @param requestCode Same as on @See AppCompatActivity.onActivityResult()
     * @param resultCode Same as on @See AppCompatActivity.onActivityResult()
     * @param data Same as on @See AppCompatActivity.onActivityResult()
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finish();

    }
}