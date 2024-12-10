package com.example.mad_final_game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;

// Helpful Links
// https://stackoverflow.com/questions/69626022/is-there-any-way-to-hide-components-in-the-layout-editor-in-android-studio For Visibility
public class MainActivity extends AppCompatActivity implements SensorEventListener{
    //  Variables
    View menuView = null;
    View gameView = null;

    private SensorManager mSensorManager;
    private Sensor mSensor;

    TextView tvX = null;
    TextView tvY = null;
    TextView tvInput = null;

    Button btnNorth = null;
    Button btnEast = null;
    Button btnWest = null;
    Button btnSouth = null;

    boolean emulator = false;
    Float initialX = (float) -0;
    Float initialY = (float) -0;
    Boolean firstRun = true;
    Boolean readyForNewInput = true;
    List<Integer> playerInput = new ArrayList<>();
    List<Integer> roundSequence = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        menuView = findViewById(R.id.viewMenu);
        gameView = findViewById(R.id.viewGame);
        gameView.setVisibility(View.GONE);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        btnNorth = findViewById(R.id.btnNorth);
        btnEast = findViewById(R.id.btnEast);
        btnSouth = findViewById(R.id.btnSouth);
        btnWest = findViewById(R.id.btnWest);

        tvX = findViewById((R.id.tvX));
        tvY = findViewById((R.id.tvY));
        tvInput = findViewById(R.id.tvInput);
    }

    public void Play(View v) {
        Handler handler = new Handler();
        Button btnToChange;

        // Change to Play Screen
        menuView.setVisibility(View.GONE);
        gameView.setVisibility(View.VISIBLE);

        //  Round Setup
        roundSequence = RandomSequence(4);
        for (int i = 0;i < roundSequence.toArray().length;i++){
            int index = i;
            handler.postDelayed(() -> {
                switch (roundSequence.get(index)) {
                    case 1:
                        changeOpacity(btnNorth);
                        break;
                    case 2:
                        changeOpacity(btnEast);
                        break;
                    case 3:
                        changeOpacity(btnSouth);
                        break;
                    case 4:
                        changeOpacity(btnWest);
                        break;
                }
            }, i * 2000);
        }

        //  Let Player Input
        mSensorManager.registerListener((SensorEventListener) this, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL);

    }

    public void onSensorChanged(SensorEvent event) {

        int xSensor, ySensor;
        //  Emulator V Actual Device
        if (emulator){
            // On Emulator; Sensors are 0:z,2:x,1:y
            xSensor = 2;
            ySensor = 1;
        }
        else {
            // On Device; Sensors are 0:x,1:y,2:z
            xSensor = 0;
            ySensor = 1;
        }

        float x = 0, y = 0, tilt = 3;
        // Assign x/y values
        if (firstRun)
        {
            initialX = event.values[xSensor];
            initialY = event.values[ySensor];

            x = event.values[xSensor] - initialX;
            y = event.values[ySensor] - initialY;

            tvX.setText(String.valueOf(x));
            tvY.setText(String.valueOf(y));
            firstRun = false;
        }
        else
        {
            x = event.values[xSensor] - initialX;
            y = event.values[ySensor] - initialY;

            tvX.setText(String.valueOf(x));
            tvY.setText(String.valueOf(y));
        }

        if ((x < 1 && y < 1) && ((x > -1 && y > -1))) { // Close to 0 and not ready for input, set ready for next input
            readyForNewInput = true;
        }
        if (y > tilt) { // East
            if (readyForNewInput) {
                btnEast.getBackground().setAlpha(64);
                readyForNewInput = false;
                playerInput.add(2);
            }
        } else if (y < -tilt) { // West
            if (readyForNewInput) {
                btnWest.getBackground().setAlpha(64);
                readyForNewInput = false;
                playerInput.add(4);
            }
        } else if (x < -tilt) { // North
            if (readyForNewInput) {
                btnNorth.getBackground().setAlpha(64);
                readyForNewInput = false;
                playerInput.add(1);
            }
        } else if (x > tilt) { // South
            if (readyForNewInput) {
                btnSouth.getBackground().setAlpha(64);
                readyForNewInput = false;
                playerInput.add(3);
            }
        } else { // Reset all buttons when returning to neutral
            if (!readyForNewInput) {
                btnEast.getBackground().setAlpha(255);
                btnWest.getBackground().setAlpha(255);
                btnNorth.getBackground().setAlpha(255);
                btnSouth.getBackground().setAlpha(255);
                readyForNewInput = true;
            }
        }
        tvInput.setText(String.valueOf( playerInput ));
        CorrectInput(roundSequence);
    }

    public void OpenHighScores(View v){
        Intent gameActivity = new Intent(MainActivity.this, HighscoreActivity.class);
        startActivity(gameActivity);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public List<Integer> RandomSequence(int length) {
        // https://www.geeksforgeeks.org/generating-random-numbers-in-java/

        Random rand = new Random();
        int max = 3;
        List<Integer> sequence = new ArrayList<>();

        for (int i = 0; i < length;i++){
            int temp;
            temp = rand.nextInt(max);
            sequence.add(temp + 1); // + 1 as nextInt Method is 0 inclusive
        }

        return sequence;
    }

    public void changeOpacity(final Button btnToChange) {
        // ChatGPT
        Handler handler = new Handler();
        // First change: Set alpha to 64 after 0ms
        btnToChange.getBackground().setAlpha(64);
        // Second change: Set alpha back to 255 after 3 seconds
        handler.postDelayed(() -> btnToChange.getBackground().setAlpha(255), 1000);
    }

    public void CorrectInput(List<Integer> sequence) {
        if (playerInput.equals(sequence)){
            Context context = getApplicationContext();
            CharSequence text = "Hello toast!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            playerInput.clear();
        }
        else  {
            if ((playerInput.size() != sequence.size()) && !playerInput.isEmpty()) {
                int latestInput = playerInput.get(playerInput.size() - 1);
                if ( latestInput != sequence.get(playerInput.size() - 1) ){
                    playerInput.clear();
                }
            }
        }
    }
}