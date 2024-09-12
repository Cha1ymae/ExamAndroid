package com.example.android.lifecycle;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class RatesDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "movie_rates.db";
    private static final int DATABASE_VERSION = 1; // Incremented version for upgrades



    // Table des évaluations
    public static final String TABLE_RATES = "rates";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_NOTE_S = "note_scenario";
    public static final String COLUMN_NOTE_R = "note_realisation";
    public static final String COLUMN_NOTE_M = "note_musique";
    public static final String COLUMN_DESCRIPTION = "description";



    private static final String TABLE_CREATE_RATES = "CREATE TABLE " + TABLE_RATES + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_TITLE + " TEXT, " +
            COLUMN_DATE + " TEXT, " +
            COLUMN_TIME + " TEXT, " +
            COLUMN_NOTE_S + " REAL, " +
            COLUMN_NOTE_R + " REAL, " +
            COLUMN_NOTE_M + " REAL, " +
            COLUMN_DESCRIPTION + " TEXT);";
    public RatesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_RATES);
        Log.d("RatesDatabaseHelper", "Tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    // Méthodes pour gérer les évaluations
    public void addRate( String title, String date, String time, float noteS, float noteR, float noteM, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_NOTE_S, noteS);
        values.put(COLUMN_NOTE_R, noteR);
        values.put(COLUMN_NOTE_M, noteM);
        values.put(COLUMN_DESCRIPTION, description);

        long result = db.insert(TABLE_RATES, null, values);
        if (result == -1) {
            Log.e("RatesDatabaseHelper", "Failed to insert rate");
        } else {
            Log.d("RatesDatabaseHelper", "Inserted rate with ID: " + result);
        }
        db.close();
    }

    public Cursor getAllRates() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_RATES, null, null, null, null, null, null);
    }

    public Cursor getRate(long rateId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_RATES,
                null,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(rateId)},
                null,
                null,
                null
        );
        if (cursor == null || cursor.getCount() == 0) {
            Log.e("RatesDatabaseHelper", "No rate found with ID: " + rateId);
        }
        return cursor;
    }



    public void deleteRate(long rateId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_RATES, COLUMN_ID + " = ?", new String[]{String.valueOf(rateId)});
        if (result == 0) {
            Log.e("RatesDatabaseHelper", "Failed to delete rate with ID: " + rateId);
        } else {
            Log.d("RatesDatabaseHelper", "Deleted rate with ID: " + rateId);
        }
        db.close();
    }
}