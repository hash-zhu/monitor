package com.hash.monitor.server.handler;

import com.hash.monitor.server.domain.DeviceData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
@Slf4j
@ChannelHandler.Sharable
@Component
public class DeviceDataHandler extends SimpleChannelInboundHandler<MqttMessage> {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MqttMessage mqttMessage) throws Exception {
        log.info("mqttMessage:{}",mqttMessage);
        if (mqttMessage.fixedHeader().messageType() == MqttMessageType.PUBLISH) {
            MqttPublishMessage publishMsg = (MqttPublishMessage) mqttMessage;
            ByteBuf payload = publishMsg.content();
            String json = payload.toString(StandardCharsets.UTF_8);
            DeviceData data = DeviceData.fromJson(json);
            kafkaTemplate.send("device-data", data.getDeviceId(), json);
        }
    }
}
