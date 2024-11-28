package com.example.mad_final_game;

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

    Float initialX = (float) -99;
    Float initialY = (float) -99;
    Boolean firstRun = true;
    Boolean readyForNewInput = true;

    private HighscoreDataSource dataSource;
    EditText nameInput;
    TextView dbInfo;

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

        dataSource = new HighscoreDataSource(this);
        dataSource.open();
        nameInput = findViewById(R.id.etName);
        dbInfo = findViewById(R.id.ViewDB);
    }

    public void Play(View v) {
        menuView.setVisibility(View.GONE);
        gameView.setVisibility(View.VISIBLE);
        mSensorManager.registerListener((SensorEventListener) this, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onSensorChanged(SensorEvent event) {

        float x = 0, y = 0, tilt = 3;
        // Assign x/y values
        if (firstRun)
        {
            initialX = event.values[0];
            initialY = event.values[1];

            x = event.values[0] - initialX;
            y = event.values[1] - initialY;

            tvX.setText(String.valueOf(x));
            tvY.setText(String.valueOf(y));
            firstRun = false;
        }
        else
        {
            x = event.values[0] - initialX;
            y = event.values[1] - initialY;

            tvX.setText(String.valueOf(x));
            tvY.setText(String.valueOf(y));
        }

        if (x < 1 && y < 1){
            readyForNewInput = true;
        }
        // Click logic
        if (y > tilt){
            if (readyForNewInput){
                btnEast.setText("Yippee");
                readyForNewInput = false;
            }
        }
        else if (y < tilt){
            if (!readyForNewInput){
                btnEast.setText("East");
                readyForNewInput = true;
            }
        }


    }
    public void addHighscore(View v) {
        String name;
        name = nameInput.getText().toString();

        dataSource.createHighscore(name, 4);

        String message = "";
        List<Highscore> list = dataSource.getAllHighscores();
        for (int i = 0; i < list.toArray().length; i++)
        {
            message += list.get(i).toString();
        }
        dbInfo.setText(message);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}