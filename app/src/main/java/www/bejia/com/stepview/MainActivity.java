package www.bejia.com.stepview;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import www.bejia.com.stepview.step.SimpleStepDetector;
import www.bejia.com.stepview.step.StepListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener, StepListener {
    private static final String TAG = "MainActivity";

    public static final int REQUESTCODE = 100;

    private www.bejia.com.stepview.view.StepView stepView;

    SharedPreferences mSharedPreferences;

    private SimpleStepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;

    private Button btn, btn3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSharedPreferences = this.getSharedPreferences(Keys.SPNAME, MODE_PRIVATE);
        Log.i(TAG, "onCreate1");
        initView();
        /**
         * 加载默认的目标bushu
         */
        stop();
        int step = mSharedPreferences.getInt(Keys.ALLSTEP, 1000);
        stepView.setStepAllCcount(step);
        stepView.startAnimation();
    }


    private void initView() {
        btn = (Button) findViewById(R.id.btn1);
        btn.setOnClickListener(this);
        btn3 = (Button) findViewById(R.id.btn3);
        btn3.setOnClickListener(this);
        stepView = (www.bejia.com.stepview.view.StepView) findViewById(R.id.step);


        // Get an instance of the SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new SimpleStepDetector();
        simpleStepDetector.registerListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void step(long timeNs) {
        handler.sendEmptyMessageDelayed(Integer.parseInt(stepView.getStepCount()), 500);
    }

    /**
     * 创建菜单
     *
     * @param menu
     * @return
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_setting:
                stop();
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, SettionActivity.class);
                startActivityForResult(intent, REQUESTCODE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            stepView.setStepCount(msg.what + 1 + "");
            stepView.invalidate();
            super.handleMessage(msg);
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == REQUESTCODE) {
            stop();
            int step = mSharedPreferences.getInt(Keys.ALLSTEP, 1000);
            stepView.setStepAllCcount(step);
            handler.sendEmptyMessageDelayed(0, 500);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                running();
                stepView.stopAnimation();
                sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_FASTEST);
                break;
            case R.id.btn3:
                stop();
                sensorManager.unregisterListener(this);
                stepView.startAnimation();
                break;
        }
    }

    private void running() {
        btn.setEnabled(false);
        btn3.setEnabled(true);
    }

    private void stop() {
        btn.setEnabled(true);
        btn3.setEnabled(false);
    }
}
