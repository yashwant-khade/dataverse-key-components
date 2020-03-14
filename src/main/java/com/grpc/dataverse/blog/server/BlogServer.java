package com.grpc.dataverse.blog.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.netty.shaded.io.netty.util.internal.ReflectionUtil;

import java.io.IOException;

public class BlogServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(50052).addService(new BlogServiceImpl())
                .build();

        System.out.println("server started");
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            System.out.println("server shutdown requested");
            server.shutdown();
        }));

        server.awaitTermination();
    }
}
