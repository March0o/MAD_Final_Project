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

import java.security.cert.CertificateEncodingException;
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
    Integer roundLength = 3;
    Integer score = -1;

    @Override
    protected void onResume() {
        super.onResume();
        // Change back to Menu Screen
        if (menuView != null && gameView != null) {
            menuView.setVisibility(View.VISIBLE);
            gameView.setVisibility(View.GONE);
        }
        ResetButtons();
        //  Reset Game Variables
        firstRun = true;
        roundLength=3;
        score=-1;
        playerInput.clear();
    }

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
        //  Get References to Views
        menuView = findViewById(R.id.viewMenu);
        gameView = findViewById(R.id.viewGame);
        //  Hide Game View
        gameView.setVisibility(View.GONE);
        //  Setup Sensors
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //  Get button References
        btnNorth = findViewById(R.id.btnNorth);
        btnEast = findViewById(R.id.btnEast);
        btnSouth = findViewById(R.id.btnSouth);
        btnWest = findViewById(R.id.btnWest);
        //  Get tv references
        tvX = findViewById((R.id.tvX));
        tvY = findViewById((R.id.tvY));
        tvInput = findViewById(R.id.tvInput);
    }
    public void Play(View v) {
        Handler handler = new Handler(); // Allows Scheduling of the round

        // Change to Play Screen
        menuView.setVisibility(View.GONE);
        gameView.setVisibility(View.VISIBLE);

        //  Round Setup
        roundSequence = RandomSequence(roundLength);
        for (int i = 0;i <= roundSequence.size();i++){
            int index = i;

            handler.postDelayed(() -> {
                if (index == roundSequence.size()){ // On Last Run
                    tvInput.setText(String.valueOf(roundSequence));
                    //  Let Player Input
                    mSensorManager.registerListener((SensorEventListener) this, mSensor,
                            SensorManager.SENSOR_DELAY_NORMAL);
                }
                else { // Display Sequence to remember
                    //  Display Sequence
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
                }
            }, i * 1000 + 1000);
        }

    }
    public void onSensorChanged(SensorEvent event) {
        //  Variables
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

        if ((x < 1.5 && y < 1.5) && ((x > -1.5 && y > -1.5))) { // Close to 0 and not ready for input, set ready for next input
            readyForNewInput = true;
            ResetButtons();
        }
        if (y > tilt && readyForNewInput) { // East
                btnEast.getBackground().setAlpha(64);
                readyForNewInput = false;
                playerInput.add(2);
        } else if (y < -tilt && readyForNewInput) { // West
                btnWest.getBackground().setAlpha(64);
                readyForNewInput = false;
                playerInput.add(4);
        } else if (x < -tilt && readyForNewInput) { // North
                btnNorth.getBackground().setAlpha(64);
                readyForNewInput = false;
                playerInput.add(1);
        } else if (x > tilt && readyForNewInput) { // South
                btnSouth.getBackground().setAlpha(64);
                readyForNewInput = false;
                playerInput.add(3);
        }

        CorrectInput(roundSequence);
    }
    public void OpenHighScores(View v){
        //  Create intent and open
        Intent gameActivity = new Intent(MainActivity.this, HighscoreActivity.class);
        gameActivity.putExtra("Score", score);
        startActivity(gameActivity);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    public List<Integer> RandomSequence(int length) {
        // https://www.geeksforgeeks.org/generating-random-numbers-in-java/
        //  Variables
        Random rand = new Random();
        int max = 4;
        List<Integer> sequence = new ArrayList<>();
        //  Create random sequence
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
        btnToChange.getBackground().setAlpha(32);
        // Second change: Set alpha back to 255 after 3 seconds
        handler.postDelayed(() -> btnToChange.getBackground().setAlpha(255), 500);
    }
    public void CorrectInput(List<Integer> sequence) {
        if (playerInput.equals(sequence)){ // On Successful PLayer Input
            Handler handler = new Handler(); // Allows Scheduling of the round
            for (int i = 0; i<2; i++)
            {
                int index = i;
                handler.postDelayed(() -> {
                    if (index == 0){
                        mSensorManager.unregisterListener(this);
                        ResetButtons();

                        Context context = getApplicationContext();
                        CharSequence text = "Nice!";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                        playerInput.clear();
                        score += roundSequence.size();
                        roundLength += 2;
                        Celebration();
                    }
                    else {
                        Play(null);
                    }
                }, i * 3000);}
        }
        else  { // On unsuccessful input
            if ((playerInput.size() != sequence.size()) && !playerInput.isEmpty()) {
                int latestInput = playerInput.get(playerInput.size() - 1);
                if ( latestInput != sequence.get(playerInput.size() - 1) ){
                    mSensorManager.unregisterListener(this);
                    OpenHighScores(null);
                }
            }
        }
    }
    public void ResetButtons() {
        //  Set buttons back to full opacity
        btnEast.getBackground().setAlpha(255);
        btnWest.getBackground().setAlpha(255);
        btnNorth.getBackground().setAlpha(255);
        btnSouth.getBackground().setAlpha(255);
    }

    public void Celebration() {
        Handler handler = new Handler(); // Allows Scheduling of the round

        for (int i = 0; i <= 3;i++){
            int index = i;
            handler.postDelayed(() -> {
                if (index == 0 || index == 2){ // Every Second loop, hide buttons
                    btnNorth.getBackground().setAlpha(0);
                    btnEast.getBackground().setAlpha(0);
                    btnSouth.getBackground().setAlpha(0);
                    btnWest.getBackground().setAlpha(0);}
                else {  //  Reset Buttons
                    ResetButtons();
                }
            }, i * 1000);
        }
    }
}