package com.example.bookshelf.view.selection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.bookshelf.repository.Repository;
import com.example.bookshelf.repository.converters.QuoteConverter;
import com.example.bookshelf.repository.objects.Quote;

public class QuoteActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CharSequence text = getIntent().getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);

        Quote quote = new Quote(0, Repository.currentBookId, text.toString());
        Repository.insertNewObject(quote, new QuoteConverter());

        finish();
    }
}

