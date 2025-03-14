package com.hash.monitor.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
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

    @PostConstruct
    public void startServer() throws Exception {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

            //创建ServerBootstrap,这个类封装了服务器端的网络配置，使得我们可以轻松地设置服务器参数
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new NettyServerInitializer());

            // 绑定端口并开始接受进来的连接
            future = bootstrap.bind(7000).sync();
            log.info("server channel={}",future.channel());
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

    }
}