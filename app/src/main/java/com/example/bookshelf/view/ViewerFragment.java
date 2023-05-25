package com.example.bookshelf.view;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bookshelf.EntryController;
import com.example.bookshelf.R;
import com.example.bookshelf.databinding.FragmentViewerBinding;
import com.example.bookshelf.databinding.ViewerActionBarBinding;
import com.example.bookshelf.model.Book;
import com.example.bookshelf.repository.Repository;
import com.example.bookshelf.repository.converters.BookConverter;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

//public class ViewerFragment extends Fragment implements MenuProvider {
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
            if (currentBookInDB.getBookmark() != 0)
                currentPage = currentBookInDB.getBookmark();
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
        });

        actionBarBinding.quotes.setOnClickListener(v -> {
            QuotesFragment fragment = QuotesFragment.newInstance(Repository.currentBookId);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, fragment, null)
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .commit();
        });

        return binding.getRoot();
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
                //handleBackButton();
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

        InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);

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

//    private void handleBackButton() {
//        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayShowCustomEnabled(false);
//            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
//            actionBar.setTitle("Welcome, " + EntryController.getLoggedUser().getName() + "!");
//        }
//        getParentFragmentManager().popBackStack();
//    }
}