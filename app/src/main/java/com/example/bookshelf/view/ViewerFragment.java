package com.example.bookshelf.view;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.bookshelf.R;
import com.example.bookshelf.databinding.FragmentViewerBinding;
import com.example.bookshelf.databinding.ViewerActionBarBinding;
import com.example.bookshelf.repository.Repository;
import com.example.bookshelf.repository.converters.BookConverter;
import com.example.bookshelf.repository.converters.QuoteConverter;
import com.example.bookshelf.repository.objects.Book;
import com.example.bookshelf.repository.objects.Quote;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ViewerFragment extends Fragment {
    private FragmentViewerBinding binding;
    private static final String BOOK_PARAM = "book";
    private ViewerActionBarBinding actionBarBinding;
    private Book currentBook;
    private int currentPage = 1;
    private int pagesNum = 0;

    public ViewerFragment() {}

    public static ViewerFragment newInstance(Integer bookId) {
        ViewerFragment fragment = new ViewerFragment();
        Bundle args = new Bundle();
        args.putInt(BOOK_PARAM, bookId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            int bookId = getArguments().getInt(BOOK_PARAM, 0);
            if (bookId != 0) {
                currentBook = (Book) Repository.selectObject(bookId, new BookConverter());
            }
        }
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentViewerBinding.inflate(inflater, container, false);

        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        ViewGroup vg = (ViewGroup) View.inflate(getActivity(), R.layout.viewer_action_bar, null);
        actionBarBinding = ViewerActionBarBinding.inflate(inflater, vg, false);
        actionBar.setCustomView(actionBarBinding.getRoot());
        setBackButtonHandler();

        if (currentBook != null) {
            binding.bottomText.setText(
                    getString(R.string.book_indicator,
                            currentBook.getAuthor(),
                            currentBook.getName()
                    )
            );

            if (currentBook.getBookmark() != 0) {
                currentPage = currentBook.getBookmark();
                actionBarBinding.bookmark.setImageResource(R.drawable.ic_bookmark_activated);
            }
        }

        actionBarBinding.nextButton.setOnClickListener(v -> {
            if (currentPage >= pagesNum)
                return;
            try {
                changePagePDF(++currentPage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        actionBarBinding.previousButton.setOnClickListener(v -> {
            if (currentPage <= 1)
                return;
            try {
                changePagePDF(--currentPage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        try {
            changePagePDF(currentPage); // Load pdf
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        actionBarBinding.bookmark.setOnClickListener(v -> {
            currentBook.setBookmark(currentPage);
            Repository.updateObject(currentBook, new BookConverter());
            actionBarBinding.bookmark.setImageResource(R.drawable.ic_bookmark_activated);
        });

        actionBarBinding.quotes.setOnClickListener(v -> {
            QuotesFragment fragment = QuotesFragment.newInstance(currentBook.getId());
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, fragment, null)
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .commit();
        });

        binding.viewerText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                Activity activity = getActivity();
                assert activity != null;
                activity.getMenuInflater().inflate(R.menu.text_selection_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.menu_item_quote) {
                    String text = getSelectedText();
                    if (text.isEmpty())
                        return true;

                    Quote quote = new Quote(0, currentBook.getId(), text);
                    Repository.insertNewObject(quote, new QuoteConverter());

                    return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {}
        });

        return binding.getRoot();
    }

    private String getSelectedText() {
        String result = "";
        if (binding.viewerText.isFocused()) {
            final int startIndex = binding.viewerText.getSelectionStart();
            final int endIndex = binding.viewerText.getSelectionEnd();

            result = binding.viewerText.getText().subSequence(startIndex, endIndex).toString().trim();
        }
        return result;
    }

    private void setBackButtonHandler() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                BookListFragment.handleBackButton(
                        requireActivity(), getParentFragmentManager()
                );
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(), callback
        );
    }

    private void changePagePDF(int nextPage) throws IOException {
        File pdfFile = new File(currentBook.getPdf());
        Uri uri = Uri.fromFile(pdfFile);
        Activity activity = getActivity();
        assert activity != null;

        InputStream inputStream = activity.getContentResolver().openInputStream(uri);

        PdfReader reader = new PdfReader(inputStream);
        pagesNum = reader.getNumberOfPages();
        binding.viewerText.setText(PdfTextExtractor.getTextFromPage(reader, nextPage).trim());

        actionBarBinding.pageIndicator.setText(
                getString(R.string.page_indicator, nextPage, pagesNum)
        );

        reader.close();
        inputStream.close();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        actionBarBinding = null;
    }
}