package com.cst2335.recipebook.RecipeAsyncTask;

import static com.cst2335.recipebook.Database.RecipeHelper.COL_IMAGE;
import static com.cst2335.recipebook.Database.RecipeHelper.COL_RECIPE_ID;
import static com.cst2335.recipebook.Database.RecipeHelper.COL_TITLE;
import static com.cst2335.recipebook.Database.RecipeHelper.PK_ID;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.cst2335.recipebook.Database.RecipeHelper;
import com.cst2335.recipebook.MainActivity;
import com.cst2335.recipebook.R;
import com.google.android.material.snackbar.Snackbar;

public class RecipeSearch extends AppCompatActivity {

    public static String SHOW_FAVE = "SHOW_FAVE";

    private SQLiteDatabase db;
    private Cursor cursor;
    private Menu menu;
    private Toolbar toolbar;
    private Button searchButton;
    private EditText searchText;
    private ListView list;
    private RecipeHelper opener;
    private SimpleCursorAdapter chatAdapter;

    private boolean showFave = false;

    /**
     * This Overrides the superclass's onCreate method,
     * It sets up the tool bar and button as well as selects the right Table to show in the list view.
     * It sets up the Click Listener for the listview and the search button.
     *
     * @param savedInstanceState @See AppCompatActivity.onCreate()
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async);

        toolbar = (Toolbar) findViewById(R.id.recipeToolbar);
        setSupportActionBar(toolbar);
        searchButton = findViewById(R.id.recipeSearchButton);
        searchText = findViewById(R.id.searchEditText);
        list = findViewById(R.id.recipeListView);
        opener = new RecipeHelper(this);

        //Set up views
        showFave = this.getIntent().getBooleanExtra(SHOW_FAVE, false);
        if (showFave) {
            showFavorite();
        } else {
            showResults();
        }

        //This is the onClickListener for my List
        list.setOnItemClickListener((mlist, item, position, id) -> {

            cursor.moveToPosition(position);

            Bundle mBundle = new Bundle();
            mBundle.putString(PK_ID, cursor.getString(cursor.getColumnIndex(PK_ID)));
            mBundle.putString(COL_TITLE, cursor.getString(cursor.getColumnIndex(COL_TITLE)));
            mBundle.putString(COL_IMAGE, cursor.getString(cursor.getColumnIndex(COL_IMAGE)));
            mBundle.putString(COL_DETAIL, cursor.getString(cursor.getColumnIndex(COL_DETAIL)));
            mBundle.putString(COL_RECIPE_ID, cursor.getString(cursor.getColumnIndex(COL_RECIPE_ID)));
            mBundle.putInt("position", position);

            boolean isTablet = findViewById(R.id.recipeFragmentLocation) != null;
            if (isTablet) {
                RecipeDetailFragment fragment = new RecipeDetailFragment();
                fragment.setArguments(mBundle);
                fragment.setTablet(true);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.recipeFragmentLocation, fragment)
                        .commit();
            } else //isPhone
            {
                Intent nextActivity = new Intent(RecipeSearch.this, RecipeEmptyActivity.class);
                nextActivity.putExtras(mBundle); //send data to next activity
                startActivityForResult(nextActivity, 346); //make the transition

            }
        });


        searchButton.setOnClickListener(click ->
        {
            //show a notification: first parameter is any view on screen. second parameter is the text. Third parameter is the length (SHORT/LONG)
            Snackbar.make(searchButton, "Searching online for Chicken. That is what you typed right?", Snackbar.LENGTH_LONG).show();
            Intent nextActivity = new Intent(RecipeSearch.this, RecipeAsync.class);
            nextActivity.putExtra(RecipeAsync.RECIPE_QUERY, searchText.getText().toString());
            startActivityForResult(nextActivity, 346); //make the transition

            list.deferNotifyDataSetChanged();

            searchButton.setEnabled(false);
            searchText.setEnabled(false);
        });

    }

    /**
     * This method Overrides the superclass's onCreateOptionsMenu() method
     * It sets up the toolbar and sets the toggleling icon based on the current list showing
     *
     * @param menu @see AppCompatActivity.onCreateOptionsMenu()
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the app bar.
        getMenuInflater().inflate(R.menu.main, menu);

        if (showFave) {
            menu.getItem(0).setIcon(R.drawable.ic_baseline_search_24);
        }

        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * This sets actions for what will happen when items in the Toolbar are clicked
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.recipeHelp:
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.information))
                        .setMessage(getString(R.string.recipeVersion) + "\n" + getString(R.string.recipeSearchHelp))
                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                break;
            case R.id.recipeFav:

                if (showFave) {
                    showResults();
                    menu.getItem(0).setIcon(R.drawable.ic_baseline_favorite_border_24);
                } else {
                    showFavorite();
                    menu.getItem(0).setIcon(R.drawable.ic_baseline_search_24);
                }
                showFave = !showFave;
                break;

            case R.id.home:
                // RecipeSearch.setTable = false;
                Intent nextActivity2 = new Intent(RecipeSearch.this, MainActivity.class);
                startActivityForResult(nextActivity2, 346);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finish();
    }

    /**
     * This method is to load the Favorite table into the list view
     */
    private void showFavorite() {
        try {
            db = opener.getWritableDatabase();
            cursor = opener.getCursor("faveTable");

            chatAdapter = new SimpleCursorAdapter(this,
                    android.R.layout.simple_list_item_2,
                    cursor,
                    new String[]{COL_TITLE/*PUBLISHER*/, COL_IMAGE, PK_ID},
                    new int[]{android.R.id.text1/*, android.R.id.text2*/},
                    0);
            list.setAdapter(chatAdapter);
        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(this,
                    "Database unavailable",
                    Toast.LENGTH_SHORT);
            toast.show();
        }
        list.deferNotifyDataSetChanged();

        searchText.setVisibility(View.INVISIBLE);
        searchButton.setVisibility(View.INVISIBLE);
    }

    /**
     * This method is to load the Search Results Table into the List
     */
    private void showResults() {
        try {
            db = opener.getWritableDatabase();
            cursor = opener.getCursor("resultTable");

            chatAdapter = new SimpleCursorAdapter(this,
                    android.R.layout.simple_list_item_2,
                    cursor,
                    new String[]{COL_TITLE, COL_IMAGE,COL_RECIPE_ID, PK_ID},
                    new int[]{android.R.id.text1/*, android.R.id.text2*/},
                    0);
            list.setAdapter(chatAdapter);
        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(this,
                    "Database unavailable",
                    Toast.LENGTH_SHORT);
            toast.show();
        }

        list.deferNotifyDataSetChanged();

        searchText.setVisibility(View.VISIBLE);
        searchButton.setVisibility(View.VISIBLE);
    }

    /**
     * This method ensures that the right Table is shown in the list. And that it is updated if its
     * the Favorites Table. It needs to recheck the database for new entries or in case one was deleted.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (showFave) {
            showFavorite();
        } else {
            showResults();
        }
    }
}