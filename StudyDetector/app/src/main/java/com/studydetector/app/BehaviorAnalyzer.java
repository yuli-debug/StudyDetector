package com.studydetector.app;

import java.util.ArrayList;
import java.util.List;

public class BehaviorAnalyzer {
    
    private List<Long> interactionTimes = new ArrayList<>();
    private List<Integer> scrollEvents = new ArrayList<>();
    private List<Integer> tapEvents = new ArrayList<>();
    private boolean isWatching = true;
    private long sessionStartTime;
    private int totalScrolls = 0;
    private int totalTaps = 0;
    
    public BehaviorAnalyzer() {
        sessionStartTime = System.currentTimeMillis();
    }
    
    public void recordInteraction() {
        long currentTime = System.currentTimeMillis();
        interactionTimes.add(currentTime);
        
        // 保持最近100次交互记录
        if (interactionTimes.size() > 100) {
            interactionTimes.remove(0);
        }
    }
    
    public void recordScroll() {
        totalScrolls++;
        scrollEvents.add((int)(System.currentTimeMillis() - sessionStartTime));
        
        if (scrollEvents.size() > 50) {
            scrollEvents.remove(0);
        }
    }
    
    public void recordTap() {
        totalTaps++;
        tapEvents.add((int)(System.currentTimeMillis() - sessionStartTime));
        
        if (tapEvents.size() > 50) {
            tapEvents.remove(0);
        }
    }
    
    public void updateProximityData(boolean watching) {
        this.isWatching = watching;
    }
    
    public BehaviorFeatures extractFeatures() {
        BehaviorFeatures features = new BehaviorFeatures();
        
        // 计算交互频率
        if (interactionTimes.size() > 1) {
            long totalTime = interactionTimes.get(interactionTimes.size() - 1) - interactionTimes.get(0);
            features.interactionFrequency = (float) interactionTimes.size() / (totalTime / 1000.0f);
        }
        
        // 计算滚动频率
        long sessionDuration = (System.currentTimeMillis() - sessionStartTime) / 1000;
        if (sessionDuration > 0) {
            features.scrollFrequency = (float) totalScrolls / sessionDuration;
        }
        
        // 计算点击频率
        if (sessionDuration > 0) {
            features.tapFrequency = (float) totalTaps / sessionDuration;
        }
        
        // 计算观看时间比例
        features.watchingRatio = isWatching ? 1.0f : 0.0f;
        
        // 计算交互规律性（标准差）
        features.interactionRegularity = calculateRegularity(interactionTimes);
        
        return features;
    }
    
    private float calculateRegularity(List<Long> times) {
        if (times.size() < 2) return 0.0f;
        
        List<Long> intervals = new ArrayList<>();
        for (int i = 1; i < times.size(); i++) {
            intervals.add(times.get(i) - times.get(i - 1));
        }
        
        // 计算间隔的标准差
        long sum = 0;
        for (Long interval : intervals) {
            sum += interval;
        }
        float mean = (float) sum / intervals.size();
        
        float variance = 0;
        for (Long interval : intervals) {
            variance += Math.pow(interval - mean, 2);
        }
        variance /= intervals.size();
        
        return (float) Math.sqrt(variance);
    }
    
    public static class BehaviorFeatures {
        public float interactionFrequency = 0.0f;
        public float scrollFrequency = 0.0f;
        public float tapFrequency = 0.0f;
        public float watchingRatio = 1.0f;
        public float interactionRegularity = 0.0f;
    }
} 