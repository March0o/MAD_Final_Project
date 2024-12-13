package com.example.mad_final_game;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class HighscoreActivity extends AppCompatActivity {


    //  https://stackoverflow.com/questions/2115758/how-do-i-display-an-alert-dialog-on-android
    //  https://stackoverflow.com/questions/55684053/edittext-in-alert-dialog-android

    private HighscoreDataSource dataSource;
    EditText nameInput;
    TextView tvNumbering;
    TextView tvName;
    TextView tvScore;
    Integer score;
    private int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_highscore);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Open DB
        dataSource = new HighscoreDataSource(this);
        dataSource.open();
        //  Setup Variables / Views
        Intent intent = getIntent();
        score = intent.getIntExtra("Score",-1);
        tvName = findViewById(R.id.tvName);
        tvScore = findViewById(R.id.tvScores);

        DisplayScores(); // Update Leaderboard
        if (score != -1){ // If a score is present open submit popup
            addHighscore(null);
        }
        //  Setup Numbering tv
        tvNumbering = findViewById(R.id.tvNumbering);
        tvNumbering.setText("1.\n2.\n3.\n4.\n5.");
    }
    public void addHighscore(View v) {
        //  Get context to display pop-up on
        Context context = getApplicationContext();
        nameInput =  new EditText(context);
        //  Build Dialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("ENTER YOUR NAME")
                .setView(nameInput)
                .setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = nameInput.getText().toString();
                        dataSource.createHighscore(name, score);
                        DisplayScores(); // Re-display Leaderboard
                    }
                })
                .create();
        dialog.show();
    }
    public void DisplayScores() {
        //  Variables
        String nameMsg = "";
        String scoreMsg = "";
        List<Highscore> list = dataSource.getAllHighscores();
        // Sort by score in descending order
        list.sort((h1, h2) -> Integer.compare(h2.getHighscore(), h1.getHighscore()));
        //  Foreach entry populate score and name tv's
        for (int i = 0; i < 5;i++) {
            nameMsg += list.get(i).getName() + "\n";
            scoreMsg += list.get(i).getScore() + "\n";
        }
        //  Set textContents
        tvName.setText(nameMsg);
        tvScore.setText(scoreMsg);
    }

    public void GoMainMenu(View v) {
        finish();
    }
}