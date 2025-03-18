package com.hash.monitor.server;

import com.hash.monitor.server.handler.DeviceDataHandler;
import com.hash.monitor.server.handler.NettyServerChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
@Slf4j
@Component
public class NettyServer {

    //负责处理接受进来的链接
    private EventLoopGroup bossGroup;
    //负责处理已经被接收的连接上的I/O操作
    private EventLoopGroup workerGroup;
    //在这个场景中，它表示服务器的绑定操作的结果
    private ChannelFuture future;
    private final ChannelInitializer channelInitializer;
    @Value("${driver.mqtt.socket.port:1883}")
    private int socketPort;
    @Autowired
    public NettyServer(ChannelInitializer channelInitializer) {
        this.channelInitializer = channelInitializer;
    }
//    @PostConstruct
    public void startServer() throws Exception {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

            //创建ServerBootstrap,这个类封装了服务器端的网络配置，使得我们可以轻松地设置服务器参数
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
        //ChannelOption.SO_BACKLOG对应的是tcp/ip协议listen函数中的backlog参数，
        // 函数listen(int socketfd,int backlog)用来初始化服务端可连接队列，
        // 服务端处理客户端连接请求是顺序处理的，所以同一时间只能处理一个客户端连接，
        // 多个客户端来的时候，服务端将不能处理的客户端连接请求放在队列中等待处理，backlog参数指定了队列的大小
                    .option(ChannelOption.SO_BACKLOG,1024)
                    //快速复用,防止服务端重启端口被占用的情况发生
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .childHandler(channelInitializer)

                     //如果TCP_NODELAY没有设置为true,那么底层的TCP为了能减少交互次数,会将网络数据积累到一定的数量后,
                     // 服务器端才发送出去,会造成一定的延迟。在互联网应用中,通常希望服务是低延迟的,建议将TCP_NODELAY设置为true
                    .childOption(ChannelOption.TCP_NODELAY, true)
                //默认的心跳间隔是7200s即2小时。Netty默认关闭该功能。
                .childOption(ChannelOption.SO_KEEPALIVE, true);



            // 绑定端口并开始接受进来的连接
            future = bootstrap.bind(1883).sync();
        if (future.isSuccess()) {
            log.info("[*MQTT驱动服务端启动成功]");
            future.channel().closeFuture().sync();
        } else {
            log.info("[~~~MQTT驱动服务端启动失败~~~]");
        }
            // 等待服务器套接字关闭
            future.channel().closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    workerGroup.shutdownGracefully();
                    bossGroup.shutdownGracefully();
                    log.info("netty server shutdownGracefully successful ");
                }
            });

    }

    @PreDestroy
    public void stopServer() throws InterruptedException {
        if (future != null) {
            future.channel().close().sync();
        }
        // 优雅关闭两个 EventLoopGroup 对象
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        log.info("[*MQTT服务端关闭成功]");
    }
}