package com.hash.monitor;

import com.hash.monitor.server.NettyServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonitorApplication.class, args);
    }
    @Bean
    public CommandLineRunner run(NettyServer nettyServer) {
        return args -> nettyServer.startServer();
    }
}
