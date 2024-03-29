package com.example.bookshelf.view;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.bookshelf.EntryController;
import com.example.bookshelf.R;
import com.example.bookshelf.databinding.FragmentRegisterBinding;
import com.example.bookshelf.repository.Repository;
import com.example.bookshelf.repository.converters.UserConverter;
import com.example.bookshelf.repository.objects.RepositoryObject;
import com.example.bookshelf.repository.objects.User;

public class RegisterFragment extends Fragment implements MenuProvider {
    private FragmentRegisterBinding binding;
    // Alternative is registration of child
    private static final String IS_MAIN_REGISTRATION_PARAM = "isMainRegistration";
    private Boolean isMainRegistration = true;

    public RegisterFragment() {}
    public static RegisterFragment newInstance(Boolean isMainRegistration) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putBoolean(IS_MAIN_REGISTRATION_PARAM, isMainRegistration);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            isMainRegistration = getArguments().getBoolean(IS_MAIN_REGISTRATION_PARAM);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);

        if (!isMainRegistration) {
            setActionBar();
            setBackButtonHandler();
        }

        binding.register.setOnClickListener(v -> {
            String login = binding.editTextLogin.getText().toString();
            String password = binding.editTextPassword.getText().toString();
            String repeat = binding.editTextRepeat.getText().toString();

            if (login.isEmpty() || password.isEmpty() || repeat.isEmpty()) {
                binding.errorMessage.setText(R.string.incorrect_data);
                binding.errorMessage.setVisibility(View.VISIBLE);
                return;
            }
            if (!password.equals(repeat)) {
                binding.errorMessage.setText(R.string.password_mismatch);
                binding.errorMessage.setVisibility(View.VISIBLE);
                return;
            }

            RepositoryObject foundUser = Repository.selectObject(
                    new User(0, login, "", false, null), new UserConverter()
            );
            if (foundUser != null) {
                binding.errorMessage.setText(R.string.different_login);
                binding.errorMessage.setVisibility(View.VISIBLE);
                return;
            }

            if (isMainRegistration) {
                EntryController.register(login, password, false, null, true);
                getParentFragmentManager().popBackStack( // Clear stack
                        null,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                );
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView, BookListFragment.class, null)
                        .setReorderingAllowed(true)
                        .commit();
            } else {
                EntryController.register(
                        login,
                        password,
                        true,
                        EntryController.getLoggedUser().getFamilyId(),
                        false
                );
                getParentFragmentManager().popBackStack(
                        "registration",
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                );
            }
        });

        return binding.getRoot();
    }

    private void setActionBar() {
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar == null)
            return;

        actionBar.setTitle("Registration of child account");
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void setBackButtonHandler() {
        requireActivity().addMenuProvider(
                this, getViewLifecycleOwner(), Lifecycle.State.CREATED
        );
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FamilyFragment.handleBackButton(
                        requireActivity(), getParentFragmentManager()
                );
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(), callback
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {}

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        FamilyFragment.handleBackButton(
                requireActivity(), getParentFragmentManager()
        );
        return true;
    }
}