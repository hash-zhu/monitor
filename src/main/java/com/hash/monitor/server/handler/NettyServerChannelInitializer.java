package com.hash.monitor.server.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: hashzhu
 * @description: 配置管道  服务端初始化，客户端与服务器端连接一旦创建，这个类中方法就会被回调，设置出站编码器和入站解码器
 **/
@Component
public class NettyServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired
    private DeviceDataHandler deviceDataHandler;

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new LoggingHandler(LogLevel.INFO)); //30秒心跳检测
        pipeline.addLast(new IdleStateHandler(30, 0, 0)); //30秒心跳检测
        pipeline.addLast("decoder", new MqttDecoder(1024*1024));
        pipeline.addLast("encoder", MqttEncoder.INSTANCE);
        pipeline.addLast(deviceDataHandler);
    }
}
