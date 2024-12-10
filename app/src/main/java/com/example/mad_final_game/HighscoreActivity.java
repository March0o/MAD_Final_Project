package com.example.mad_final_game;

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


    private HighscoreDataSource dataSource;
    EditText nameInput;
    TextView dbInfo;
    Integer score;

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
        dataSource = new HighscoreDataSource(this);
        dataSource.open();

        Intent intent = getIntent();
        score = intent.getIntExtra("Score",0);
        nameInput = findViewById(R.id.etName);
        dbInfo = findViewById(R.id.viewDB);
    }

    public void addHighscore(View v) {
        String name;
        name = nameInput.getText().toString();

        dataSource.createHighscore(name, score);

        String message = "";
        List<Highscore> list = dataSource.getAllHighscores();
        for (int i = 0; i < list.toArray().length; i++)
        {
            message += list.get(i).toString();
        }
        dbInfo.setText(message);
    }
}