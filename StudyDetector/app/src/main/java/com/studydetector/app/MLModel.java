package com.studydetector.app;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;

public class MLModel {
    
    private Context context;
    private List<Float> recentPredictions = new ArrayList<>();
    private static final int PREDICTION_HISTORY_SIZE = 10;
    
    // 权重参数（可以通过训练调整）
    private float behaviorWeight = 0.4f;
    private float contentWeight = 0.4f;
    private float sensorWeight = 0.2f;
    
    public MLModel(Context context) {
        this.context = context;
    }
    
    public PredictionResult predict(BehaviorAnalyzer.BehaviorFeatures behaviorFeatures,
                                   ContentAnalyzer.ContentFeatures contentFeatures,
                                   SensorFeatures sensorFeatures) {
        
        PredictionResult result = new PredictionResult();
        
        // 计算行为得分
        float behaviorScore = calculateBehaviorScore(behaviorFeatures);
        
        // 计算内容得分
        float contentScore = contentFeatures.studyScore;
        
        // 计算传感器得分
        float sensorScore = calculateSensorScore(sensorFeatures);
        
        // 加权平均
        float finalScore = behaviorWeight * behaviorScore + 
                          contentWeight * contentScore + 
                          sensorWeight * sensorScore;
        
        // 应用历史平滑
        recentPredictions.add(finalScore);
        if (recentPredictions.size() > PREDICTION_HISTORY_SIZE) {
            recentPredictions.remove(0);
        }
        
        float smoothedScore = calculateSmoothedScore();
        
        // 确定活动类型
        if (smoothedScore > 0.6f) {
            result.activityType = "学习";
            result.confidence = smoothedScore;
        } else if (smoothedScore < 0.4f) {
            result.activityType = "娱乐";
            result.confidence = 1.0f - smoothedScore;
        } else {
            result.activityType = "混合";
            result.confidence = 0.5f;
        }
        
        return result;
    }
    
    private float calculateBehaviorScore(BehaviorAnalyzer.BehaviorFeatures features) {
        float score = 0.5f; // 基础分数
        
        // 交互频率分析（学习时交互较少但规律）
        if (features.interactionFrequency > 0 && features.interactionFrequency < 2.0f) {
            score += 0.2f;
        } else if (features.interactionFrequency > 5.0f) {
            score -= 0.2f; // 频繁交互可能是娱乐
        }
        
        // 滚动频率分析（学习时滚动较慢）
        if (features.scrollFrequency > 0 && features.scrollFrequency < 1.0f) {
            score += 0.15f;
        } else if (features.scrollFrequency > 3.0f) {
            score -= 0.15f;
        }
        
        // 观看时间比例
        score += features.watchingRatio * 0.1f;
        
        // 交互规律性（学习时更规律）
        if (features.interactionRegularity > 0 && features.interactionRegularity < 1000f) {
            score += 0.1f;
        }
        
        return Math.max(0.0f, Math.min(1.0f, score));
    }
    
    private float calculateSensorScore(SensorFeatures features) {
        float score = 0.5f;
        
        // 设备稳定性（学习时设备更稳定）
        if (features.deviceStability > 0.8f) {
            score += 0.2f;
        } else if (features.deviceStability < 0.3f) {
            score -= 0.2f;
        }
        
        // 观看距离（适中距离更适合学习）
        if (features.viewingDistance > 0.3f && features.viewingDistance < 0.8f) {
            score += 0.1f;
        }
        
        return Math.max(0.0f, Math.min(1.0f, score));
    }
    
    private float calculateSmoothedScore() {
        if (recentPredictions.isEmpty()) {
            return 0.5f;
        }
        
        float sum = 0;
        for (int i = 0; i < recentPredictions.size(); i++) {
            // 给最近的预测更高权重
            float weight = (float) (i + 1) / recentPredictions.size();
            sum += recentPredictions.get(i) * weight;
        }
        
        return sum / recentPredictions.size();
    }
    
    public void updateWeights(float behaviorWeight, float contentWeight, float sensorWeight) {
        this.behaviorWeight = behaviorWeight;
        this.contentWeight = contentWeight;
        this.sensorWeight = sensorWeight;
    }
    
    public static class PredictionResult {
        public String activityType = "未知";
        public float confidence = 0.0f;
    }
    
    public static class SensorFeatures {
        public float deviceStability = 0.5f;  // 设备稳定性 (0-1)
        public float viewingDistance = 0.5f;  // 观看距离 (0-1)
        public float motionIntensity = 0.5f;  // 运动强度 (0-1)
    }
} 