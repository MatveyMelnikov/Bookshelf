package com.example.bookshelf.view;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.bookshelf.R;
import com.example.bookshelf.databinding.FragmentAddBookBinding;
import com.example.bookshelf.model.Book;
import com.example.bookshelf.repository.ChildBookListFragment;
import com.example.bookshelf.repository.Repository;
import com.example.bookshelf.repository.converters.BookConverter;
import com.example.bookshelf.repository.objects.User;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AddBookFragment extends Fragment implements MenuProvider {
    private FragmentAddBookBinding binding;
    private static final String USER_PARAM = "user";
    private static final String EDIT_BOOK_PARAM = "editBook";
    private User user;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private ActivityResultLauncher<Intent> activityResultLauncherPDF;
    private Bitmap currentBookCard;
    private Book editableBook = null;
    private Uri selectedPdf = null;
    private FragmentResultListener listener = null;

    public AddBookFragment() {}

    public static AddBookFragment newInstance(User user, Book book) {
        AddBookFragment fragment = new AddBookFragment();
        Bundle args = new Bundle();
        args.putSerializable(USER_PARAM, user);
        args.putSerializable(EDIT_BOOK_PARAM, book);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable(USER_PARAM);
            editableBook = (Book) getArguments().getSerializable(EDIT_BOOK_PARAM);
        }
    }

    public void setResultListener(FragmentResultListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentAddBookBinding.inflate(inflater, container, false);
        setActionBar();

        if (editableBook != null) {
            binding.editBookName.setText(editableBook.name);
            binding.editAuthor.setText(editableBook.author);

            com.example.bookshelf.repository.objects.Book bookDB =
                    (com.example.bookshelf.repository.objects.Book) Repository.selectObject(
                            editableBook.id, new BookConverter()
                    );
            assert bookDB != null;
            //selectedPdf = Uri.parse(bookDB.getPdf());
            File pdfFile = new File(bookDB.getPdf());
            selectedPdf = Uri.fromFile(pdfFile);
            binding.choosePdf.setText(R.string.file_selected);

            currentBookCard = bookDB.getCover();
            if (currentBookCard != null) {
                binding.bookCard.setBackground(
                        new BitmapDrawable(requireContext().getResources(), currentBookCard)
                );
            }
        }

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();

                        if (data != null) {
                            currentBookCard = getBookCard(data);
                            binding.bookCard.setBackground(
                                    new BitmapDrawable(
                                            requireContext().getResources(),
                                            currentBookCard
                                    )
                            );
                        }
                    }
                });

        activityResultLauncherPDF = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        assert result.getData() != null;
                        selectedPdf = result.getData().getData();

                        binding.choosePdf.setText(R.string.file_selected);
                    }
                });

        binding.bookCard.setOnClickListener(v -> selectSource());

        binding.confirmButton.setOnClickListener(v -> {
            String bookName = binding.editBookName.getText().toString();
            String author = binding.editAuthor.getText().toString();
            if (bookName.isEmpty() || author.isEmpty() || selectedPdf == null)
                return;

            File pdfFile = savePDFToLocalStorage(
                    bookName + "_" + author + "_" +
                            user.getName(),
                    selectedPdf
            );

            com.example.bookshelf.repository.objects.Book bookDB =
                    new com.example.bookshelf.repository.objects.Book(
                            editableBook == null ? 0 : editableBook.id,
                            user.getId(),
                            bookName,
                            author,
                            pdfFile.getAbsolutePath()
                    );
            if (currentBookCard != null)
                bookDB.setCover(currentBookCard);

            if (editableBook != null)
                Repository.updateObject(bookDB, new BookConverter());
            else
                Repository.insertNewObject(bookDB, new BookConverter());

            handleBackButton();
            getParentFragmentManager().popBackStack(
                    "addBookFragment",
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
            );
        });

        binding.choosePdf.setOnClickListener(v -> startPickingFile());

        return binding.getRoot();
    }

    private File savePDFToLocalStorage(String newFileName, Uri uri) {
        // Save pdf to local storage
        try {
            File myFile = new File(requireActivity().getFilesDir(), newFileName);
            InputStream inputStream;
            OutputStream outputStream;

            inputStream = requireActivity().getContentResolver().openInputStream(uri);
            outputStream = new FileOutputStream(myFile);

            byte[] buf = new byte[1024];

            while (inputStream.read(buf) > 0) {
                outputStream.write(buf, 0, 1024);
            }

            outputStream.close();
            inputStream.close();

            return myFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void startTakingPicture()
    {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        activityResultLauncher.launch(intent);
    }

    void startPickingImageFromGallery()
    {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        activityResultLauncher.launch(intent);
    }

    void startPickingFile()
    {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/pdf");
        activityResultLauncherPDF.launch(intent);
    }

    Bitmap blurBitmap(Bitmap bitmap) {
        int COMPRESSED_HEIGHT = 150;
        Bitmap inputBitmap;
        if (bitmap.getHeight() > 200) {
            inputBitmap = Bitmap.createScaledBitmap(
                    bitmap,
                    (int) (COMPRESSED_HEIGHT * 0.75F),
                    COMPRESSED_HEIGHT,
                    false
            );
        } else {
            inputBitmap = bitmap;
        }
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        RenderScript rs = RenderScript.create(getContext());
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        theIntrinsic.setRadius(2.5F);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);

        return outputBitmap.copy(Bitmap.Config.ARGB_8888,true);
    }

    Bitmap cropImage(Bitmap bitmap) {
        Bitmap result;
        // Bitmap.createBitmap(source, x, y, width, height)
        if (bitmap.getHeight() < bitmap.getWidth()) {
            int newWidth = (int) (bitmap.getHeight() * 0.75F);
            result = Bitmap.createBitmap(
                    bitmap,
                    (bitmap.getWidth() - newWidth) / 2,
                    0,
                    newWidth,
                    bitmap.getHeight()
            );
        } else {
            int newHeight = (int) (bitmap.getWidth() / 0.75F);
            int newWidth = bitmap.getWidth();
            if (newHeight > bitmap.getHeight()) {
                newHeight = bitmap.getHeight();
                newWidth = (int) (bitmap.getHeight() * 0.75F);
            }
            result = Bitmap.createBitmap(
                    bitmap,
                    0,
                    (bitmap.getHeight() - newHeight) / 2,
                    newWidth,
                    newHeight
            );
        }
        return result;
    }

    Bitmap getDarkenBitmap(Bitmap bitmap) {
        int BRIGHTNESS_PERCENTAGE = 60;
        int multiply = (int) ((BRIGHTNESS_PERCENTAGE / 100.0f) * 255);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        // Just dividing each color component
        ColorFilter filter = new LightingColorFilter(
                Color.argb(255, multiply, multiply, multiply),
                0x00000000
        );
        paint.setColorFilter(filter);
        canvas.drawBitmap(bitmap, 0, 0, paint);

        return bitmap;
    }

    Bitmap compressBitmap(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
        return BitmapFactory.decodeStream(
                new ByteArrayInputStream(outputStream.toByteArray())
        );
    }

    Bitmap getBookCard(Intent data) {
        Bitmap bitmap = null;
        try {
            if (data.hasExtra("data")) {
                bitmap = (Bitmap) data.getExtras().get("data");
            } else {
                bitmap = cropImage(
                        MediaStore.Images.Media.getBitmap(
                                this.requireContext().getContentResolver(),
                                data.getData()
                        )
                );
            }

            bitmap = cropImage(bitmap);
            bitmap = compressBitmap(bitmap);
            bitmap = blurBitmap(bitmap);
            bitmap = getDarkenBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    void selectSource() {
        final CharSequence[] optionsMenu = {"Take Photo", "Choose from Gallery" };

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setItems(
                optionsMenu,
                (dialogInterface, i) -> {
                    if (optionsMenu[i].equals("Take Photo")) {
                        startTakingPicture();
                    } else if (optionsMenu[i].equals("Choose from Gallery")) {
                        startPickingImageFromGallery();
                    }
                }
        );
        builder.show();
    }


    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {}

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        handleBackButton();
        return true;
    }

    private void setActionBar() {
        requireActivity().addMenuProvider(
                this, getViewLifecycleOwner(), Lifecycle.State.CREATED
        );
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleBackButton();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(), callback
        );

        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Adding a book");
        }
    }

    private void handleBackButton() {
        if (user.isChild()) {
            ChildBookListFragment.handleBackButton(
                    requireActivity(), getParentFragmentManager(), user
            );
        } else {
            BookListFragment.handleBackButton(
                    requireActivity(), getParentFragmentManager()
            );
        }

        if (listener != null)
            listener.handleResult();
    }
}