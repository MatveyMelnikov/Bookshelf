package com.example.bookshelf.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bookshelf.EntryController;
import com.example.bookshelf.R;
import com.example.bookshelf.databinding.FragmentRegisterBinding;
import com.example.bookshelf.repository.DataController;
import com.example.bookshelf.repository.Repository;
import com.example.bookshelf.repository.converters.UserConverter;
import com.example.bookshelf.repository.objects.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {
    private FragmentRegisterBinding binding;
    public RegisterFragment() {}
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
        binding = FragmentRegisterBinding.inflate(inflater, container, false);

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

            if (Repository.selectObject(new User(0, login, ""), new UserConverter()) != null) {
                binding.errorMessage.setText(R.string.different_login);
                binding.errorMessage.setVisibility(View.VISIBLE);
                return;
            }
            EntryController.register(login, password);
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