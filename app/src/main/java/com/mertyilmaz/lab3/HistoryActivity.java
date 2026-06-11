package com.mertyilmaz.lab3;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Part 2.3: shows all operations performed since app launch,
 * one operation per line (e.g. "2 + 2 = 4").
 */
public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ListView listHistory = findViewById(R.id.listHistory);
        TextView textEmpty = findViewById(R.id.textEmpty);

        listHistory.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, HistoryStore.getEntries()));
        listHistory.setEmptyView(textEmpty);
    }
}
