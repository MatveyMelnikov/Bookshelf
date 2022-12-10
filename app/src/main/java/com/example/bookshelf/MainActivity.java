package com.example.bookshelf;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startAddBookFragment()
    {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerView, new AddBookFragment())
                .addToBackStack(null)
                .commit();
    }

    void handleBackButton()
    {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onBackPressed() {
        handleBackButton();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Exit button in action bar
        if (item.getItemId() == android.R.id.home) {
            handleBackButton();
        }
        return true;
    }
}