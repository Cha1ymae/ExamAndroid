package com.example.android.lifecycle;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class RatesActivity extends AppCompatActivity {

    private RatesDatabaseHelper dbHelper;
    private ListView listViewMovies;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rates);

        listViewMovies = findViewById(R.id.listViewMovies);
        dbHelper = new RatesDatabaseHelper(this);

        loadMovies();
    }

    private void loadMovies() {
        cursor = dbHelper.getAllRates();

        if (cursor != null && cursor.getCount() > 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    getMovieTitles(cursor)
            );

            listViewMovies.setAdapter(adapter);

            listViewMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    cursor.moveToPosition(position);

                    // Récupérer les informations du film
                    long movieId = cursor.getLong(cursor.getColumnIndex(RatesDatabaseHelper.COLUMN_ID));
                    String title = cursor.getString(cursor.getColumnIndex(RatesDatabaseHelper.COLUMN_TITLE));
                    String date = cursor.getString(cursor.getColumnIndex(RatesDatabaseHelper.COLUMN_DATE));
                    String time = cursor.getString(cursor.getColumnIndex(RatesDatabaseHelper.COLUMN_TIME));
                    float noteS = cursor.getFloat(cursor.getColumnIndex(RatesDatabaseHelper.COLUMN_NOTE_S));
                    float noteR = cursor.getFloat(cursor.getColumnIndex(RatesDatabaseHelper.COLUMN_NOTE_R));
                    float noteM = cursor.getFloat(cursor.getColumnIndex(RatesDatabaseHelper.COLUMN_NOTE_M));
                    String description = cursor.getString(cursor.getColumnIndex(RatesDatabaseHelper.COLUMN_DESCRIPTION));

                    // Afficher les détails du film avec une option de suppression
                    showMovieDetailsDialog(movieId, title, date, time, noteS, noteR, noteM, description);
                }
            });
        } else {
            Toast.makeText(this, "No movies available.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showMovieDetailsDialog(final long movieId, String title, String date, String time, float noteS, float noteR, float noteM, String description) {
        String details = "Title: " + title + "\n" +
                "Date: " + date + "\n" +
                "Time: " + time + "\n" +
                "Note Scénario: " + noteS + "\n" +
                "Note Réalisation: " + noteR + "\n" +
                "Note Musique: " + noteM + "\n" +
                "Description: " + description;

        new AlertDialog.Builder(this)
                .setTitle("Movie Details")
                .setMessage(details)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteMovie(movieId);
                    }
                })
                .show();
    }

    private void shareMovieDetails(String title, String details) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822"); // Type MIME pour les e-mails
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{}); // Laisser vide pour que l'utilisateur puisse choisir le destinataire
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Rate Film: " + title);
        emailIntent.putExtra(Intent.EXTRA_TEXT, details);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send email via..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "No email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    // Méthode pour supprimer un film de la BD et de la liste
    private void deleteMovie(long movieId) {
        dbHelper.deleteRate(movieId);
        loadMovies();  // Recharger la liste des films
        Toast.makeText(this, "Movie deleted", Toast.LENGTH_SHORT).show();
    }

    private String[] getMovieTitles(Cursor cursor) {
        String[] titles = new String[cursor.getCount()];
        int i = 0;
        if (cursor.moveToFirst()) {
            do {
                titles[i++] = cursor.getString(cursor.getColumnIndex(RatesDatabaseHelper.COLUMN_TITLE));
            } while (cursor.moveToNext());
        }
        return titles;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_main) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_share) {
            if (cursor != null && cursor.moveToFirst()) {
                // Récupérer les détails du premier film dans la liste
                cursor.moveToFirst();
                String title = cursor.getString(cursor.getColumnIndex(RatesDatabaseHelper.COLUMN_TITLE));
                String date = cursor.getString(cursor.getColumnIndex(RatesDatabaseHelper.COLUMN_DATE));
                String time = cursor.getString(cursor.getColumnIndex(RatesDatabaseHelper.COLUMN_TIME));
                float noteS = cursor.getFloat(cursor.getColumnIndex(RatesDatabaseHelper.COLUMN_NOTE_S));
                float noteR = cursor.getFloat(cursor.getColumnIndex(RatesDatabaseHelper.COLUMN_NOTE_R));
                float noteM = cursor.getFloat(cursor.getColumnIndex(RatesDatabaseHelper.COLUMN_NOTE_M));
                String description = cursor.getString(cursor.getColumnIndex(RatesDatabaseHelper.COLUMN_DESCRIPTION));

                String details = "Title: " + title + "\n" +
                        "Date: " + date + "\n" +
                        "Time: " + time + "\n" +
                        "Note Scénario: " + noteS + "\n" +
                        "Note Réalisation: " + noteR + "\n" +
                        "Note Musique: " + noteM + "\n" +
                        "Description: " + description;

                shareMovieDetails(title, details);
            } else {
                Toast.makeText(this, "No movies available to share.", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null) {
            cursor.close();
        }
    }
}
