package com.grpc.dataverse.download.client;

import com.google.protobuf.ByteString;
import com.proto.download.DownloadRequest;
import com.proto.download.DownloadResponse;
import com.proto.download.DownloadServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


public class ClientMain {

    public static void main(String[] args) throws InterruptedException, IOException {
        List<ByteString> blob = new ArrayList<>();
        ManagedChannel channel = ManagedChannelBuilder.
                forAddress("localhost", 50051).
                usePlaintext().
                build();

        System.out.println("Hello I am gRPC client");

        DownloadServiceGrpc.DownloadServiceStub asyncClient = DownloadServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        asyncClient.download(DownloadRequest.newBuilder().setName("myfile0.mp4").build(),
                new StreamObserver<DownloadResponse>() {

                    String fileName = "";
                    int count = 0;

                    @Override
                    public void onNext(DownloadResponse value) {
                        System.out.println("Got new max value " + value.getId());
                        blob.add(value.getData());
                        fileName = value.getName();
                    }

                    @Override
                    public void onError(Throwable t) {
                        t.printStackTrace();
                        latch.countDown();
                    }

                    @Override
                    public void onCompleted() {
                        try (FileOutputStream out = new FileOutputStream("F:\\275\\input\\" + fileName)) {

                            //get the file data back from the bytes
                            int size = blob.stream().mapToInt(ByteString::size).sum();
                            ByteString bs = blob.stream().reduce((a, b) -> {
                                return a.concat(b);
                            }).get();

                            bs.writeTo(out);
                            System.out.println(fileName + " is written");
//					ByteString.newOutput().writeTo(out);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        latch.countDown();

                    }
                });


        latch.await(10L, TimeUnit.SECONDS);

        channel.shutdown();


    }
}
