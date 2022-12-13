package com.example.bookshelf;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ViewerFragment extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View fragmentView = inflater.inflate(
                R.layout.activity_viewer_fragment, container, false
        );

        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.viewer_action_bar);

        Bundle bundle = getArguments();
        if (bundle != null) {
            TextView bottomText = fragmentView.findViewById(R.id.bottomText);
            bottomText.setText(bundle.getString("author") + " - " +
                    bundle.getString("name"));
        }

        return fragmentView;
    }
}