package com.hash.monitor;


import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Random;
import java.util.UUID;

/**
 * 模拟设备上传数据
 */
public class DeviceSimulator {

    // MQTT Broker 地址
    private static final String BROKER = "tcp://localhost:1883";

    // MQTT Topic
    private static final String TOPIC = "sensor/data";

    // 设备ID
    private static final String DEVICE_ID = "DEV-" + UUID.randomUUID().toString().substring(0, 8);

    // MQTT 客户端
    private MqttClient mqttClient;

    public DeviceSimulator() throws MqttException {
        // 初始化 MQTT 客户端
        mqttClient = new MqttClient(BROKER, DEVICE_ID, new MemoryPersistence());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true); // 清除会话
        options.setConnectionTimeout(10); // 连接超时时间
        options.setKeepAliveInterval(60); // 心跳间隔
        mqttClient.connect(options);
    }

    /**
     * 模拟设备数据
     */
    private String generateSensorData() {
        Random random = new Random();
        double temperature = 20 + random.nextDouble() * 15; // 温度范围：20℃ ~ 35℃
        double humidity = 40 + random.nextDouble() * 30;    // 湿度范围：40% ~ 70%
        long timestamp = System.currentTimeMillis();

        // 生成 JSON 格式的数据
        return String.format("{\"deviceId\":\"%s\",\"timestamp\":%d,\"temperature\":%.2f,\"humidity\":%.2f}",
                DEVICE_ID, timestamp, temperature, humidity);
    }

    /**
     * 发送数据到 MQTT Broker
     */
    public void sendData() throws MqttException {
        String payload = generateSensorData();
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(1); // QoS 1：至少一次
        mqttClient.publish(TOPIC, message);
        System.out.println("Sent: " + payload);
    }

    /**
     * 启动模拟设备
     */
    public void start() {
        try {
            while (true) {
                sendData();
                Thread.sleep(5000); // 每 5 秒发送一次数据
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                mqttClient.disconnect();
                mqttClient.close();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            DeviceSimulator simulator = new DeviceSimulator();
            simulator.start();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
