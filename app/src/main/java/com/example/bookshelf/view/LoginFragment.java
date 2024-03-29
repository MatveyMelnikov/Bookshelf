package com.example.bookshelf.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bookshelf.EntryController;
import com.example.bookshelf.R;
import com.example.bookshelf.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    public LoginFragment() {}
    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);

        binding.register.setOnClickListener(v -> {
            RegisterFragment fragment = RegisterFragment.newInstance(true);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, fragment, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("registration")
                    .commit();
        });

        binding.enter.setOnClickListener(v -> {
            String login = binding.editTextLogin.getText().toString();
            String password = binding.editTextPassword.getText().toString();

            if (!EntryController.logIn(login, password)) {
                binding.errorMessage.setText(R.string.incorrect_data);
                binding.errorMessage.setVisibility(View.VISIBLE);
                return;
            }

            getParentFragmentManager().popBackStack( // Clear stack
                    null,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
            );
            // Success
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, BookListFragment.class, null)
                    .setReorderingAllowed(true)
                    .commit();
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}