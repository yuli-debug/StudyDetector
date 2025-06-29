package com.studydetector.app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.app.usage.UsageStatsManager;
import android.app.usage.UsageStats;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class DetectionService extends Service {
    
    private static final long DETECTION_INTERVAL = 5000; // 5秒检测一次
    private Handler handler;
    private Runnable detectionRunnable;
    
    private BehaviorAnalyzer behaviorAnalyzer;
    private ContentAnalyzer contentAnalyzer;
    private MLModel mlModel;
    private StudyDetector mainActivity;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        handler = new Handler(Looper.getMainLooper());
        behaviorAnalyzer = new BehaviorAnalyzer();
        contentAnalyzer = new ContentAnalyzer();
        mlModel = new MLModel(this);
        
        detectionRunnable = new Runnable() {
            @Override
            public void run() {
                performDetection();
                handler.postDelayed(this, DETECTION_INTERVAL);
            }
        };
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.post(detectionRunnable);
        return START_STICKY;
    }
    
    private void performDetection() {
        try {
            // 获取当前前台应用
            String currentApp = getCurrentForegroundApp();
            
            // 分析行为特征
            BehaviorAnalyzer.BehaviorFeatures behaviorFeatures = behaviorAnalyzer.extractFeatures();
            
            // 分析内容特征
            ContentAnalyzer.ContentFeatures contentFeatures = contentAnalyzer.analyzeContent(
                currentApp, currentApp, "" // 简化版本，不分析具体内容文本
            );
            
            // 分析传感器特征
            MLModel.SensorFeatures sensorFeatures = new MLModel.SensorFeatures();
            // 这里可以从主Activity获取传感器数据
            
            // 进行预测
            MLModel.PredictionResult result = mlModel.predict(behaviorFeatures, contentFeatures, sensorFeatures);
            
            // 更新UI
            if (mainActivity != null) {
                mainActivity.updateDetectionResult(result.activityType, result.confidence, currentApp);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private String getCurrentForegroundApp() {
        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        long endTime = System.currentTimeMillis();
        long beginTime = endTime - 1000; // 1秒前
        
        List<UsageStats> usageStats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, beginTime, endTime);
        
        if (usageStats != null && !usageStats.isEmpty()) {
            SortedMap<Long, UsageStats> sortedMap = new TreeMap<>();
            for (UsageStats stats : usageStats) {
                sortedMap.put(stats.getLastTimeUsed(), stats);
            }
            
            if (!sortedMap.isEmpty()) {
                String packageName = sortedMap.get(sortedMap.lastKey()).getPackageName();
                return packageName;
            }
        }
        
        return "unknown";
    }
    
    public void setMainActivity(StudyDetector activity) {
        this.mainActivity = activity;
    }
    
    public void recordUserInteraction() {
        if (behaviorAnalyzer != null) {
            behaviorAnalyzer.recordInteraction();
        }
    }
    
    public void recordScroll() {
        if (behaviorAnalyzer != null) {
            behaviorAnalyzer.recordScroll();
        }
    }
    
    public void recordTap() {
        if (behaviorAnalyzer != null) {
            behaviorAnalyzer.recordTap();
        }
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null && detectionRunnable != null) {
            handler.removeCallbacks(detectionRunnable);
        }
    }
    
    public static void startService(Context context) {
        Intent intent = new Intent(context, DetectionService.class);
        context.startService(intent);
    }
    
    public static void stopService(Context context) {
        Intent intent = new Intent(context, DetectionService.class);
        context.stopService(intent);
    }
} 