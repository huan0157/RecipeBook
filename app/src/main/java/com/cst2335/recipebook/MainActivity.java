package com.cst2335.recipebook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.cst2335.recipebook.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    /**
     * This is an Override of  the onCreate from the super class, it displays a SnackBar and sets a
     * clickListener for the go to Search activity button
     *
     * @param savedInstanceState @See AppCompatActivity.onCreate()
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.recipeToolbar);
        setSupportActionBar(toolbar);

        String chickOrLasgne;
        Boolean bool = false;
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);

        pref.getBoolean("chicken", bool);

        if (bool) {
            chickOrLasgne = getString(R.string.lastchoicechicken);
        } else {
            chickOrLasgne = getString(R.string.lastchoicelasagan);
        }
        CoordinatorLayout cLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        Snackbar.make(cLayout, chickOrLasgne, Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.gotcha), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Respond to the click, or not it's really up to you
                    }
                }).show();
        Button button = findViewById(R.id.recipeMainButton);

        button.setOnClickListener(click ->
        {
            //first parameter is any view on screen. second parameter is the text. Third parameter is the length (SHORT/LONG)
            Intent nextActivity = new Intent(RecipeMain.this, RecipeSearch.class);
            startActivityForResult(nextActivity, 1); //make the transition
        });


    }

    /**
     * This simply creates the toolbar menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        //Inflate the menu; this adds items to the app bar.
        getMenuInflater().inflate(R.menu.recipe_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * This sets up the onclickListeners and actions for the menu toolbar
     *
     * @param item takes which item was clicked
     * @return returns the return of the super method call
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.recipeHelp:
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.information))
                        .setMessage(getString(R.string.recipeVersion) + "\n" + getString(R.string.recipeMainHelp))
                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                break;
            case R.id.recipeFav:
                // RecipeSearch.setTable = false;
                Intent nextActivity = new Intent(RecipeMain.this, RecipeSearch.class);
                nextActivity.putExtra(RecipeSearch.SHOW_FAVE, true);
                startActivityForResult(nextActivity, 1); //make the transition
                break;

            case R.id.search:
                // RecipeSearch.setTable = false;
                Intent nextActivity2 = new Intent(RecipeMain.this, RecipeSearch.class);
                startActivityForResult(nextActivity2, 1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
