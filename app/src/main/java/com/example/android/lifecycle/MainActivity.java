package com.example.android.lifecycle;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextDate, editTextTime, editTextDescription;
    private RatingBar ratingBarNoteS, ratingBarNoteR, ratingBarNoteM;
    private RatesDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialisation des vues
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDate = findViewById(R.id.editTextDate);
        editTextTime = findViewById(R.id.editTextTime);
        ratingBarNoteS = findViewById(R.id.ratingBarNoteS);
        ratingBarNoteR = findViewById(R.id.ratingBarNoteR);
        ratingBarNoteM = findViewById(R.id.ratingBarNoteM);
        editTextDescription = findViewById(R.id.editTextDescription);
        Button buttonSubmit = findViewById(R.id.buttonSubmit);

        dbHelper = new RatesDatabaseHelper(this);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isValid = true;

                String date = editTextDate.getText().toString();
                if (!isValidDate(date)) {
                    editTextDate.setTextColor(Color.RED);
                    isValid = false;
                } else {
                    editTextDate.setTextColor(Color.BLACK);
                }

                String time = editTextTime.getText().toString();
                if (!isValidTime(time)) {
                    editTextTime.setTextColor(Color.RED);
                    isValid = false;
                } else {
                    editTextTime.setTextColor(Color.BLACK);
                }

                if (isValid) {
                    saveReview();
                    Toast.makeText(MainActivity.this, "Form submitted successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Please correct the errors in the form.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isValidDate(String date) {
        return date.matches("\\d{4}-\\d{2}-\\d{2}"); // Format yyyy-mm-dd
    }

    private boolean isValidTime(String time) {
        return time.matches("\\d{2}:\\d{2}"); // Format HH:mm
    }

    private void saveReview() {
        // Récupération des données depuis les champs de texte et rating bars
        String title = editTextTitle.getText().toString();
        String date = editTextDate.getText().toString();
        String time = editTextTime.getText().toString();
        float noteS = ratingBarNoteS.getRating();
        float noteR = ratingBarNoteR.getRating();
        float noteM = ratingBarNoteM.getRating();
        String description = editTextDescription.getText().toString();

        // Enregistrement dans la base de données
        dbHelper.addRate(title, date, time, noteS, noteR, noteM, description);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_main:
                if (!(this instanceof MainActivity)) {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }
                return true;
            case R.id.action_rates:
                Intent intent = new Intent(this, RatesActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}