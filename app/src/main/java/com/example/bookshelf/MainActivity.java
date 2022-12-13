package com.example.bookshelf;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startAddBookFragment(Bundle bundle) {
        Fragment fragment = new AddBookFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void startBookListFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView, new BookList())
                .commit();
    }

    public void startViewerFragment(Bundle bundle) {
        Fragment fragment = new ViewerFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(null)
                .commit();
    }

    void handleBackButton() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0)
            super.onBackPressed();
        else
            getSupportFragmentManager().popBackStack();

        ActionBar actionBar = ((AppCompatActivity) this).getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setDisplayShowCustomEnabled(false);
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setTitle("Bookshelf");
        }
    }

    @Override
    public void onBackPressed() {
        handleBackButton();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Exit button in action bar
        if (item.getItemId() == android.R.id.home)
            handleBackButton();
        return true;
    }
}