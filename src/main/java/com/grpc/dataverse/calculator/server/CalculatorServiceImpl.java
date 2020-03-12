package com.grpc.dataverse.calculator.server;

import com.proto.calculator.*;
import io.grpc.stub.StreamObserver;

public class CalculatorServiceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {
    @Override
    public StreamObserver<CalculateAverageRequest>
    computeAverage(StreamObserver<CalculateAverageResponse> responseObserver) {

        System.out.println("Streaming has started");

        StreamObserver<CalculateAverageRequest> requestObserver =
                new StreamObserver<CalculateAverageRequest>() {
            int sum = 0;
            int count = 0;
            @Override
            public void onNext(CalculateAverageRequest value) {
                sum += value.getNumber();
                count++;
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                double average = (double) sum / count;
                responseObserver.onNext(
                        CalculateAverageResponse.
                                newBuilder().
                                setAverage(average).
                                build()
                );
                System.out.println("Response calculated");
                responseObserver.onCompleted();
            }

        };
        return requestObserver;
    }

    @Override
    public StreamObserver<FindMaximumRequest> findMaximum(StreamObserver<FindMaximumResponse> responseObserver) {



        return new StreamObserver<FindMaximumRequest>() {

            int maxNumber = 0;

            @Override
            public void onNext(FindMaximumRequest value) {
                int currentNumber = value.getNumber();
                if (currentNumber > maxNumber) {
                    maxNumber = currentNumber;
                    responseObserver.onNext(FindMaximumResponse.newBuilder().setMaxNumber(maxNumber).build());
                }
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onCompleted();
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(FindMaximumResponse.newBuilder().setMaxNumber(maxNumber).build());

                responseObserver.onCompleted();
            }
        };
    }
}
