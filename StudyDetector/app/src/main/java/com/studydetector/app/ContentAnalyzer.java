package com.studydetector.app;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ContentAnalyzer {
    
    // 学习类应用包名
    private static final Set<String> STUDY_APPS = new HashSet<>(Arrays.asList(
        "com.duolingo",           // 多邻国
        "com.khanacademy.android", // 可汗学院
        "org.edx.mobile",         // edX
        "com.coursera.android",   // Coursera
        "com.udacity.android",    // Udacity
        "com.skype.raider",       // Skype (视频学习)
        "com.zoom.us",           // Zoom (在线课程)
        "com.microsoft.teams",   // Teams (学习会议)
        "com.google.android.apps.docs.editors.docs", // Google Docs
        "com.microsoft.office.word", // Word
        "com.adobe.reader",      // PDF阅读器
        "com.google.android.apps.books", // Google Books
        "com.amazon.kindle",     // Kindle
        "com.zhihu.android",     // 知乎
        "com.tencent.edu",       // 腾讯课堂
        "com.netease.cloudmusic", // 网易云音乐(学习音乐)
        "com.ximalaya.ting.android" // 喜马拉雅(有声书)
    ));
    
    // 娱乐类应用包名
    private static final Set<String> ENTERTAINMENT_APPS = new HashSet<>(Arrays.asList(
        "com.instagram.android",  // Instagram
        "com.facebook.katana",    // Facebook
        "com.twitter.android",    // Twitter
        "com.snapchat.android",   // Snapchat
        "com.zhiliaoapp.musically", // TikTok
        "com.ss.android.ugc.aweme", // 抖音
        "com.tencent.mm",        // 微信
        "com.tencent.mobileqq",  // QQ
        "com.netease.cloudmusic", // 网易云音乐
        "com.spotify.music",     // Spotify
        "com.google.android.youtube", // YouTube
        "com.netflix.mediaclient", // Netflix
        "com.amazon.avod.thirdpartyclient", // Prime Video
        "com.tencent.ig",        // PUBG
        "com.activision.callofduty.shooter", // COD
        "com.roblox.client",     // Roblox
        "com.mojang.minecraftpe", // Minecraft
        "com.douyin.video",      // 抖音
        "com.ss.android.ugc.aweme.lite" // 抖音极速版
    ));
    
    // 视频学习关键词
    private static final Set<String> STUDY_KEYWORDS = new HashSet<>(Arrays.asList(
        "教程", "学习", "课程", "教学", "培训", "讲座", "演讲",
        "tutorial", "course", "lecture", "education", "learning",
        "编程", "代码", "算法", "数学", "物理", "化学", "生物",
        "programming", "code", "algorithm", "math", "physics",
        "历史", "文学", "哲学", "艺术", "音乐理论", "语言学习",
        "history", "literature", "philosophy", "art", "language"
    ));
    
    // 娱乐关键词
    private static final Set<String> ENTERTAINMENT_KEYWORDS = new HashSet<>(Arrays.asList(
        "搞笑", "娱乐", "游戏", "音乐", "舞蹈", "美食", "旅游",
        "funny", "entertainment", "game", "music", "dance", "food", "travel",
        "明星", "网红", "直播", "挑战", "恶搞", "段子", "梗",
        "celebrity", "influencer", "live", "challenge", "meme",
        "电影", "电视剧", "综艺", "动画", "漫画", "小说",
        "movie", "tv", "show", "anime", "manga", "novel"
    ));
    
    public ContentFeatures analyzeContent(String packageName, String appName, String contentText) {
        ContentFeatures features = new ContentFeatures();
        
        // 分析应用类型
        features.appType = analyzeAppType(packageName, appName);
        
        // 分析内容关键词
        features.contentType = analyzeContentType(contentText);
        
        // 计算综合得分
        features.studyScore = calculateStudyScore(features);
        
        return features;
    }
    
    private String analyzeAppType(String packageName, String appName) {
        if (STUDY_APPS.contains(packageName)) {
            return "study";
        } else if (ENTERTAINMENT_APPS.contains(packageName)) {
            return "entertainment";
        } else {
            // 根据应用名称判断
            String lowerAppName = appName.toLowerCase();
            if (lowerAppName.contains("学习") || lowerAppName.contains("study") ||
                lowerAppName.contains("教育") || lowerAppName.contains("education") ||
                lowerAppName.contains("课程") || lowerAppName.contains("course")) {
                return "study";
            } else if (lowerAppName.contains("游戏") || lowerAppName.contains("game") ||
                       lowerAppName.contains("娱乐") || lowerAppName.contains("entertainment") ||
                       lowerAppName.contains("视频") || lowerAppName.contains("video")) {
                return "entertainment";
            }
        }
        return "unknown";
    }
    
    private String analyzeContentType(String contentText) {
        if (contentText == null || contentText.isEmpty()) {
            return "unknown";
        }
        
        String lowerContent = contentText.toLowerCase();
        int studyCount = 0;
        int entertainmentCount = 0;
        
        // 统计学习关键词
        for (String keyword : STUDY_KEYWORDS) {
            if (lowerContent.contains(keyword)) {
                studyCount++;
            }
        }
        
        // 统计娱乐关键词
        for (String keyword : ENTERTAINMENT_KEYWORDS) {
            if (lowerContent.contains(keyword)) {
                entertainmentCount++;
            }
        }
        
        if (studyCount > entertainmentCount) {
            return "study";
        } else if (entertainmentCount > studyCount) {
            return "entertainment";
        } else {
            return "mixed";
        }
    }
    
    private float calculateStudyScore(ContentFeatures features) {
        float score = 0.5f; // 基础分数
        
        // 应用类型权重
        switch (features.appType) {
            case "study":
                score += 0.3f;
                break;
            case "entertainment":
                score -= 0.3f;
                break;
            case "unknown":
                score += 0.0f;
                break;
        }
        
        // 内容类型权重
        switch (features.contentType) {
            case "study":
                score += 0.2f;
                break;
            case "entertainment":
                score -= 0.2f;
                break;
            case "mixed":
                score += 0.0f;
                break;
        }
        
        return Math.max(0.0f, Math.min(1.0f, score));
    }
    
    public static class ContentFeatures {
        public String appType = "unknown";
        public String contentType = "unknown";
        public float studyScore = 0.5f;
    }
} 