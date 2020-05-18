package com.grpc.dataverse.calculator.client;

import com.proto.calculator.*;
import io.grpc.CallCredentials;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CalculatorClient {
    public static void main(String[] args) throws InterruptedException {
//        CalculatorClient calculatorClient = new CalculatorClient();
        ManagedChannel channel = ManagedChannelBuilder.
                forAddress("localhost", 50051).
                usePlaintext().
                build();

        System.out.println("Hello I am gRPC client");

        CalculatorServiceGrpc.CalculatorServiceStub asyncClient = CalculatorServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);
//        StreamObserver<CalculateAverageRequest> requestObserver = asyncClient.computeAverage(new StreamObserver<CalculateAverageResponse>() {
//            @Override
//            public void onNext(CalculateAverageResponse value) {
//                System.out.println("Response from  server");
//                System.out.println(value.getAverage());
//            }
//
//            @Override
//            public void onError(Throwable t) {
//
//            }
//
//            @Override
//            public void onCompleted() {
//                System.out.println("server has completed sending us data");
//                latch.countDown();
//            }
//        });


        StreamObserver<FindMaximumRequest> requestObserver = asyncClient.findMaximum(new StreamObserver<FindMaximumResponse>() {
            @Override
            public void onNext(FindMaximumResponse value) {
                System.out.println("Got new max value " + value.getMaxNumber());
            }

            @Override
            public void onError(Throwable t) {
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                latch.countDown();

            }
        });

        Arrays.asList(5,7,16,2,13,30,12).forEach(num->{
            System.out.println("Sending number "+ num);
            requestObserver.onNext(FindMaximumRequest.newBuilder().setNumber(num).build());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        requestObserver.onCompleted();

        latch.await(3L, TimeUnit.SECONDS);

        channel.shutdown();

    }
}
