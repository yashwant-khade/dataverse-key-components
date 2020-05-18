package com.grpc.dataverse.client;

import com.grpc.dataverse.bully.MessageService;
import com.grpc.dataverse.file.client.ClientThread;
import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import com.proto.message.MessageRequest;
import com.proto.message.MessageResponse;
import com.proto.message.MessageServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Client {


    private static final int THREAD_COUNT = 2;
    private static final String SEND_FILE_NAME = "myfile0.mp4";
    private static ExecutorService executorService;

    public static void main(String[] args) {
        System.out.println("gRPC client started");

        ManagedChannel channel = ManagedChannelBuilder.
                forAddress("localhost", 55130).
                usePlaintext().
                build();

        MessageServiceGrpc.MessageServiceBlockingStub greetService = MessageServiceGrpc.newBlockingStub(channel);



        MessageResponse messageResponse = greetService.sendMessage(MessageRequest.newBuilder().setMessage("service_port").setFilename(SEND_FILE_NAME).build());

        System.out.println(messageResponse.getResult());

        String[] servers = messageResponse.getResult().split(",",2);


        channel.shutdown();

        executorService = Executors.newFixedThreadPool(THREAD_COUNT);

        for(int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(new UploadThread(SEND_FILE_NAME , Integer.parseInt(servers[i])));
        }


        try {
            executorService.shutdown();
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            // TODO
            e.printStackTrace();
        }


    }
}
