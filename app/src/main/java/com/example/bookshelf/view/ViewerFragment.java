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
import com.example.bookshelf.model.Book;
import com.example.bookshelf.repository.Repository;
import com.example.bookshelf.repository.converters.BookConverter;
import com.example.bookshelf.repository.converters.QuoteConverter;
import com.example.bookshelf.repository.objects.Quote;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ViewerFragment extends Fragment {
    private FragmentViewerBinding binding;
    private ViewerActionBarBinding actionBarBinding;
    private Book currentBook;
    private com.example.bookshelf.repository.objects.Book currentBookInDB;
    private int currentPage = 1;
    private int pagesNum = 0;
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

        Bundle bundle = getArguments();
        if (bundle != null) {
            currentBook = (Book) bundle.getSerializable("book");
            binding.bottomText.setText(
                    getString(R.string.book_indicator, currentBook.author, currentBook.name)
            );
            Repository.currentBookId = currentBook.id;

            currentBookInDB = (com.example.bookshelf.repository.objects.Book)
                    Repository.selectObject(currentBook.id, new BookConverter());
            assert currentBookInDB != null;
            if (currentBookInDB.getBookmark() != 0) {
                currentPage = currentBookInDB.getBookmark();
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
            loadPDF();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        actionBarBinding.bookmark.setOnClickListener(v -> {
            currentBookInDB = (com.example.bookshelf.repository.objects.Book)
                    Repository.selectObject(currentBook.id, new BookConverter());
            assert currentBookInDB != null;
            currentBookInDB.setBookmark(currentPage);
            Repository.updateObject(currentBookInDB, new BookConverter());
            actionBarBinding.bookmark.setImageResource(R.drawable.ic_bookmark_activated);
        });

        actionBarBinding.quotes.setOnClickListener(v -> {
            QuotesFragment fragment = QuotesFragment.newInstance(Repository.currentBookId);
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

                    Quote quote = new Quote(0, Repository.currentBookId, text);
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

    private void loadPDF() throws IOException {
        currentBookInDB = (com.example.bookshelf.repository.objects.Book)
                        Repository.selectObject(currentBook.id, new BookConverter());
        assert currentBookInDB != null;

        changePagePDF(currentPage);
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
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

    private void changePagePDF(int nextPage) throws IOException {
        File pdfFile = new File(currentBookInDB.getPdf());
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