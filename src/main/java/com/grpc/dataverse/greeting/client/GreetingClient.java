package com.grpc.dataverse.greeting.client;

import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {
    public static void main(String[] args) {
        System.out.println("gRPC client started");

        ManagedChannel channel = ManagedChannelBuilder.
                forAddress("localhost", 50051).
                usePlaintext().
                build();

        System.out.println("Creating stub");
//        DummyServiceGrpc.DummyServiceBlockingStub syncClient = DummyServiceGrpc.newBlockingStub(channel);
//        DummyServiceGrpc.DummyServiceFutureStub asyncClient = DummyServiceGrpc.newFutureStub(channel);
        GreetServiceGrpc.GreetServiceBlockingStub greetService = GreetServiceGrpc.newBlockingStub(channel);

        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Yashwant")
                .setLastName("Khade")
                .build();

        GreetRequest greetRequest = GreetRequest.newBuilder()
                .setGreeting(greeting)
                .build();

        GreetResponse greetResponse = greetService.greet(greetRequest);

        System.out.println(greetResponse.getResult());

        System.out.println("Shutting down");
        channel.shutdown();
    }
}
