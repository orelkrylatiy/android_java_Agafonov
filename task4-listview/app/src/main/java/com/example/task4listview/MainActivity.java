package com.example.task4listview;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "ListAlgorithms";
    private final ArrayList<String> items = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private EditText itemEditText;
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        itemEditText = findViewById(R.id.itemEditText);
        searchEditText = findViewById(R.id.searchEditText);
        Button addButton = findViewById(R.id.addButton);
        Button sortButton = findViewById(R.id.sortButton);
        Button searchButton = findViewById(R.id.searchButton);
        ListView itemsListView = findViewById(R.id.itemsListView);

        Collections.addAll(items, "Banana", "Apple", "Orange", "Kiwi");
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        itemsListView.setAdapter(adapter);

        addButton.setOnClickListener(view -> addItem());
        sortButton.setOnClickListener(view -> sortItems());
        searchButton.setOnClickListener(view -> findItem());
        itemsListView.setOnItemClickListener((parent, view, position, id) -> removeItem(position));

        benchmarkListInsertion();
    }

    private void addItem() {
        String value = itemEditText.getText().toString().trim();
        if (value.isEmpty()) {
            Toast.makeText(this, "Enter non-empty text", Toast.LENGTH_SHORT).show();
            return;
        }
        items.add(value);
        adapter.notifyDataSetChanged();
        itemEditText.setText("");
    }

    private void sortItems() {
        Collections.sort(items);
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "List sorted", Toast.LENGTH_SHORT).show();
    }

    private void findItem() {
        String query = searchEditText.getText().toString().trim();
        if (query.isEmpty()) {
            Toast.makeText(this, "Enter search text", Toast.LENGTH_SHORT).show();
            return;
        }

        // binarySearch корректен только для отсортированного списка.
        Collections.sort(items);
        adapter.notifyDataSetChanged();
        int index = Collections.binarySearch(items, query);
        if (index >= 0) {
            Toast.makeText(this, "Found at index: " + index, Toast.LENGTH_SHORT).show();
        } else {
            int insertionPoint = -index - 1;
            Toast.makeText(this, "Not found. Insertion point: " + insertionPoint, Toast.LENGTH_SHORT).show();
        }
    }

    private void removeItem(int position) {
        if (position < 0 || position >= items.size()) {
            return;
        }
        String removed = items.remove(position);
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Removed: " + removed, Toast.LENGTH_SHORT).show();
    }

    private void benchmarkListInsertion() {
        long arrayListTime = measureInsertAtBeginning(new ArrayList<>());
        long linkedListTime = measureInsertAtBeginning(new LinkedList<>());
        Log.d(TAG, "ArrayList insert 100000 at beginning: " + arrayListTime + " ms");
        Log.d(TAG, "LinkedList insert 100000 at beginning: " + linkedListTime + " ms");
    }

    private long measureInsertAtBeginning(List<Integer> list) {
        long start = System.nanoTime();
        for (int index = 0; index < 100_000; index++) {
            list.add(0, index);
        }
        long durationNanos = System.nanoTime() - start;
        return durationNanos / 1_000_000;
    }
}
