package com.hash.monitor.server.domain;

import lombok.Data;

@Data
public class DeviceData {
    // 设备ID
    private String deviceId;

    // 时间戳（设备上报数据的时间）
    private long timestamp;

    // 温度值
    private double temperature;

    // 湿度值
    private double humidity;

    // 设备地理位置（可选字段）
    private String location;

    // 默认构造方法
    public DeviceData() {}

    // 全参构造方法
    public DeviceData(String deviceId, long timestamp, double temperature, double humidity, String location) {
        this.deviceId = deviceId;
        this.timestamp = timestamp;
        this.temperature = temperature;
        this.humidity = humidity;
        this.location = location;
    }

    // 示例：格式化输出设备数据
    @Override
    public String toString() {
        return String.format("DeviceData{deviceId='%s', timestamp=%d, temperature=%.2f, humidity=%.2f, location='%s'}",
                deviceId, timestamp, temperature, humidity, location);
    }

    // 示例：将对象转换为JSON字符串
    public String toJson() {
        return String.format("{\"deviceId\":\"%s\",\"timestamp\":%d,\"temperature\":%.2f,\"humidity\":%.2f,\"location\":\"%s\"}",
                deviceId, timestamp, temperature, humidity, location);
    }

    // 示例：从JSON字符串解析为DeviceData对象
    public static DeviceData fromJson(String json) {
        // 这里可以使用Gson、Jackson等库，以下为手动解析示例
        String deviceId = json.split("\"deviceId\":\"")[1].split("\"")[0];
        long timestamp = Long.parseLong(json.split("\"timestamp\":")[1].split(",")[0]);
        double temperature = Double.parseDouble(json.split("\"temperature\":")[1].split(",")[0]);
        double humidity = Double.parseDouble(json.split("\"humidity\":")[1].split(",")[0]);
        String location = json.split("\"location\":\"")[1].split("\"")[0];
        return new DeviceData(deviceId, timestamp, temperature, humidity, location);
    }
}
