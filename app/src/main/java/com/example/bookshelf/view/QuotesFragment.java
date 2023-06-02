package com.example.bookshelf.view;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.bookshelf.R;
import com.example.bookshelf.databinding.FragmentQuotesBinding;
import com.example.bookshelf.repository.Repository;
import com.example.bookshelf.repository.converters.BookConverter;
import com.example.bookshelf.repository.converters.QuoteConverter;
import com.example.bookshelf.repository.objects.Book;
import com.example.bookshelf.repository.objects.Quote;
import com.example.bookshelf.view.recyclerview.QuoteAdapter;
import com.example.bookshelf.view.recyclerview.RecyclerListener;

import java.util.ArrayList;

public class QuotesFragment extends Fragment implements RecyclerListener, MenuProvider {
    FragmentQuotesBinding binding;
    private static final String BOOK_PARAM = "bookId";
    private int bookId;

    public QuotesFragment() {}
    public static QuotesFragment newInstance(int bookId) {
        QuotesFragment fragment = new QuotesFragment();
        Bundle args = new Bundle();
        args.putInt(BOOK_PARAM, bookId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            bookId = getArguments().getInt(BOOK_PARAM, 0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentQuotesBinding.inflate(inflater, container, false);
        setActionBar();

        ArrayList<Quote> quotes;
        if (bookId == 0) {
            quotes = Repository.getArrayOfAllUserQuotes();
        } else {
            quotes = Repository.getArrayOfQuotes(bookId);
        }
        QuoteAdapter quoteAdapter = new QuoteAdapter(this, quotes);
        binding.quotesList.setAdapter(quoteAdapter);
        binding.quotesList.setLayoutManager(new LinearLayoutManager(requireActivity()));

        return binding.getRoot();
    }

    @Override
    public void onElementClick(int index) {
        QuoteAdapter adapter = (QuoteAdapter) binding.quotesList.getAdapter();
        assert adapter != null;
        Quote selectedQuote = adapter.getItem(index);

        Book quoteBook = (Book) Repository.selectObject(selectedQuote.getBookId(), new BookConverter());
        assert quoteBook != null;
        Toast.makeText(
                requireActivity(),
                getString(R.string.book_indicator, quoteBook.getAuthor(), quoteBook.getName()),
                Toast.LENGTH_SHORT
        ).show();
    }

    @Override
    public void onLongElementClick(int index) {
        QuoteAdapter adapter = (QuoteAdapter) binding.quotesList.getAdapter();
        assert adapter != null;
        Quote deletedQuote = adapter.getItem(index);
        Repository.deleteObject(deletedQuote.getId(), new QuoteConverter());
        adapter.deleteItem(index);
    }

    private void setActionBar() {
        requireActivity().addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.CREATED);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleBackButton();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowCustomEnabled(false);
            actionBar.setTitle("Quotes");
        }
    }

    private void handleBackButton() {
        getParentFragmentManager().popBackStack();
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {}

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        handleBackButton();
        return true;
    }
}