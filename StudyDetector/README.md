# 学习检测器 (Study Detector)

一个智能的Android应用，可以自动识别用户是在学习还是在娱乐，无需联网即可工作。

## 功能特点

- **离线工作**：所有分析都在本地完成，保护用户隐私
- **多维度分析**：结合传感器数据、用户行为和内容分析
- **实时检测**：每5秒更新一次检测结果
- **智能识别**：支持学习类应用和娱乐类应用的自动识别

## 技术原理

### 1. 传感器数据分析
- 加速度计：检测设备运动模式
- 陀螺仪：分析设备旋转和稳定性
- 距离传感器：判断用户是否在观看屏幕

### 2. 用户行为分析
- 交互频率：学习时交互较少但规律
- 滚动模式：学习时滚动较慢且有序
- 观看时间：分析用户专注度

### 3. 内容分析
- 应用类型识别：学习类vs娱乐类应用
- 关键词分析：识别内容的学习/娱乐属性

### 4. 机器学习模型
- 多特征融合：综合各种数据源
- 历史平滑：减少误判
- 自适应权重：根据用户习惯调整

## 安装和部署

### 环境要求
- Android Studio 4.0+
- Android SDK 24+
- Java 8+

### 构建步骤

1. **克隆项目**
```bash
git clone [项目地址]
cd StudyDetector
```

2. **打开Android Studio**
```bash
# 在Android Studio中打开项目
```

3. **同步Gradle**
```bash
# 等待Gradle同步完成
```

4. **构建APK**
```bash
# Build -> Build Bundle(s) / APK(s) -> Build APK(s)
```

5. **安装到设备**
```bash
# 将生成的APK安装到Android设备
```

### 权限配置

应用需要以下权限：
- `ACTIVITY_RECOGNITION`：活动识别
- `PACKAGE_USAGE_STATS`：应用使用统计
- `SYSTEM_ALERT_WINDOW`：系统悬浮窗
- `FOREGROUND_SERVICE`：前台服务

## 使用方法

1. **启动应用**：点击应用图标启动
2. **授予权限**：按照提示授予必要权限
3. **开始检测**：点击"开始检测"按钮
4. **正常使用**：正常使用手机，应用会自动分析
5. **查看结果**：实时查看检测结果和置信度

## 项目结构

```
StudyDetector/
├── app/
│   ├── src/main/
│   │   ├── java/com/studydetector/app/
│   │   │   ├── StudyDetector.java      # 主Activity
│   │   │   ├── BehaviorAnalyzer.java   # 行为分析器
│   │   │   ├── ContentAnalyzer.java    # 内容分析器
│   │   │   ├── MLModel.java           # 机器学习模型
│   │   │   └── DetectionService.java   # 后台检测服务
│   │   ├── res/
│   │   │   ├── layout/                 # 布局文件
│   │   │   ├── values/                 # 资源文件
│   │   │   └── drawable/               # 图形资源
│   │   └── AndroidManifest.xml        # 应用清单
│   └── build.gradle                   # 应用级构建配置
├── build.gradle                       # 项目级构建配置
└── settings.gradle                    # Gradle设置
```

## 自定义配置

### 调整检测参数
在 `MLModel.java` 中可以调整各种权重参数：
```java
private float behaviorWeight = 0.4f;    // 行为权重
private float contentWeight = 0.4f;     // 内容权重
private float sensorWeight = 0.2f;      // 传感器权重
```

### 添加新的应用类型
在 `ContentAnalyzer.java` 中可以添加新的应用包名：
```java
private static final Set<String> STUDY_APPS = new HashSet<>(Arrays.asList(
    "com.your.study.app",  // 添加你的学习应用
    // ... 其他应用
));
```

## 注意事项

1. **电池优化**：建议将应用加入电池优化白名单
2. **权限管理**：确保所有必要权限都已授予
3. **系统兼容性**：支持Android 7.0+系统
4. **隐私保护**：所有数据都在本地处理，不会上传

## 故障排除

### 常见问题

1. **检测不准确**
   - 检查权限是否已授予
   - 确保应用在后台运行
   - 调整检测参数

2. **应用崩溃**
   - 检查Android版本兼容性
   - 查看日志文件
   - 重新安装应用

3. **权限问题**
   - 手动在设置中授予权限
   - 重启应用

## 开发计划

- [ ] 添加更多传感器支持
- [ ] 优化机器学习模型
- [ ] 增加个性化设置
- [ ] 支持更多应用类型
- [ ] 添加数据统计功能

## 许可证

MIT License

## 贡献

欢迎提交Issue和Pull Request来改进这个项目！ 