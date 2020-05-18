package com.grpc.dataverse.grpcbully.server;

import com.grpc.dataverse.calculator.server.CalculatorServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class BullyServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder
                .forPort(50052)
                .addService(new CalculatorServiceImpl())
                .build();

        System.out.println("server started");
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("server shutdown requested");
            server.shutdown();
        }));

        server.awaitTermination();
    }
}