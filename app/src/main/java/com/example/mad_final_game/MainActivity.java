package com.example.mad_final_game;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

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

    Button btnNorth = null;
    Button btnEast = null;
    Button btnWest = null;
    Button btnSouth = null;

    boolean emulator = false;
    Float initialX = (float) -0;
    Float initialY = (float) -0;
    Boolean firstRun = true;
    Boolean readyForNewInput = true;

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
    }

    public void Play(View v) {
        menuView.setVisibility(View.GONE);
        gameView.setVisibility(View.VISIBLE);
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
                btnEast.setText("Yippee");
                readyForNewInput = false;
            }
        } else if (y < -tilt) { // West
            if (readyForNewInput) {
                btnWest.setText("Wow");
                readyForNewInput = false;
            }
        } else if (x < -tilt) { // North
            if (readyForNewInput) {
                btnNorth.setText("Hooray");
                readyForNewInput = false;
            }
        } else if (x > tilt) { // South
            if (readyForNewInput) {
                btnSouth.setText("Yahoo");
                readyForNewInput = false;
            }
        } else { // Reset all buttons when returning to neutral
            if (!readyForNewInput) {
                btnEast.setText("East");
                btnWest.setText("West");
                btnNorth.setText("North");
                btnSouth.setText("South");
                readyForNewInput = true;
            }
        }

    }

    public void OpenHighScores(View v){
        Intent gameActivity = new Intent(MainActivity.this, HighscoreActivity.class);
        startActivity(gameActivity);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}