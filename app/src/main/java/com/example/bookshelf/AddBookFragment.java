package com.example.bookshelf;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;

public class AddBookFragment extends Fragment {
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View fragmentView = inflater.inflate(
                R.layout.fragment_add_book, container, false
        );

        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Adding a book");
        }

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();

                        if (data != null) {
                            fragmentView.findViewById(R.id.bookCard).setBackground(
                                    new BitmapDrawable(
                                            requireContext().getResources(),
                                            getBookCard(data)
                                    )
                            );
                        }
                    }
                });

        fragmentView.findViewById(R.id.bookCard).setOnClickListener(view ->
                //startPickingImageFromGallery()
                selectSource()
        );

        /*
        fragmentView.findViewById(R.id.confirmButton).setOnClickListener(view ->
                startPickingImageFromGallery()
        );
         */

        return fragmentView;
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

        return outputBitmap;
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
            bitmap = blurBitmap(bitmap);
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
}