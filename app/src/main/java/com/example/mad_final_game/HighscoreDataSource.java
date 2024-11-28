package com.example.mad_final_game;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class HighscoreDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {
            MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_NAME,
            MySQLiteHelper.COLUMN_SCORE };

    public HighscoreDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Highscore createHighscore(String name, int score) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_NAME, name);
        values.put(MySQLiteHelper.COLUMN_SCORE, score);
        long insertId = database.insert(MySQLiteHelper.TABLE_HIGHSCORES, null,
                values);

        Cursor cursor = database.query(MySQLiteHelper.TABLE_HIGHSCORES,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Highscore newHighscore = cursorToHighscore(cursor);
        cursor.close();
        return newHighscore;
    }

    public void deleteHighscore(Highscore highscore) {
        long id = highscore.getId();
        System.out.println("Highscore deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_HIGHSCORES, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Highscore> getAllHighscores() {
        List<Highscore> highscores = new ArrayList<Highscore>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_HIGHSCORES,
                allColumns,
                null,
                null,
                null,
                null,
                null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Highscore highscore = cursorToHighscore(cursor);
            highscores.add(highscore);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return highscores;
    }

    private Highscore cursorToHighscore(Cursor cursor) {
        Highscore highscore = new Highscore();
        highscore.setId(cursor.getLong(0));
        highscore.setName(cursor.getString(1));
        highscore.setScore(cursor.getInt(2));
        return highscore;
    }

}
