package com.studydetector.app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class StudyDetector extends AppCompatActivity implements SensorEventListener {
    
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private Sensor proximity;
    
    private TextView statusText;
    private TextView confidenceText;
    private TextView currentAppText;
    
    private BehaviorAnalyzer behaviorAnalyzer;
    private ContentAnalyzer contentAnalyzer;
    private MLModel mlModel;
    
    private List<Float> accelerometerData = new ArrayList<>();
    private List<Float> gyroscopeData = new ArrayList<>();
    private long lastInteractionTime = 0;
    private int scrollCount = 0;
    private int tapCount = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_detector);
        
        // 初始化UI组件
        statusText = findViewById(R.id.status_text);
        confidenceText = findViewById(R.id.confidence_text);
        currentAppText = findViewById(R.id.current_app_text);
        
        // 初始化传感器
        initializeSensors();
        
        // 初始化分析器
        behaviorAnalyzer = new BehaviorAnalyzer();
        contentAnalyzer = new ContentAnalyzer();
        mlModel = new MLModel(this);
        
        // 请求权限
        requestPermissions();
        
        // 开始检测
        startDetection();
    }
    
    private void initializeSensors() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }
    
    private void requestPermissions() {
        String[] permissions = {
            Manifest.permission.ACTIVITY_RECOGNITION,
            Manifest.permission.SYSTEM_ALERT_WINDOW
        };
        
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) 
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, 1);
            }
        }
    }
    
    private void startDetection() {
        // 注册传感器监听器
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, proximity, SensorManager.SENSOR_DELAY_NORMAL);
        
        // 启动后台检测服务
        DetectionService.startService(this);
    }
    
    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                handleAccelerometerData(event);
                break;
            case Sensor.TYPE_GYROSCOPE:
                handleGyroscopeData(event);
                break;
            case Sensor.TYPE_PROXIMITY:
                handleProximityData(event);
                break;
        }
    }
    
    private void handleAccelerometerData(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        
        accelerometerData.add((float) Math.sqrt(x*x + y*y + z*z));
        
        // 保持数据量在合理范围内
        if (accelerometerData.size() > 100) {
            accelerometerData.remove(0);
        }
    }
    
    private void handleGyroscopeData(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        
        gyroscopeData.add((float) Math.sqrt(x*x + y*y + z*z));
        
        if (gyroscopeData.size() > 100) {
            gyroscopeData.remove(0);
        }
    }
    
    private void handleProximityData(SensorEvent event) {
        float distance = event.values[0];
        // 检测用户是否在观看屏幕
        boolean isWatching = distance > 0;
        behaviorAnalyzer.updateProximityData(isWatching);
    }
    
    public void updateDetectionResult(String activity, float confidence, String appName) {
        runOnUiThread(() -> {
            statusText.setText("当前活动: " + activity);
            confidenceText.setText("置信度: " + String.format("%.1f%%", confidence * 100));
            currentAppText.setText("应用: " + appName);
            
            // 根据检测结果给出建议
            if (activity.equals("学习")) {
                statusText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            } else {
                statusText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }
        });
    }
    
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 传感器精度变化处理
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
        DetectionService.stopService(this);
    }
} 