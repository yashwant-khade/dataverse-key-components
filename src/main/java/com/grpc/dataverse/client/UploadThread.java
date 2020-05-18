package com.grpc.dataverse.client;

import com.google.protobuf.ByteString;
import com.proto.upload.FileRequest;
import com.proto.upload.FileResponse;
import com.proto.upload.FileServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

public class UploadThread implements Runnable {
    public static final int FILE_SPLIT_UNIT = 4194276; //max 4MB

    private ManagedChannel channel;
    private final String filePath = "F:\\275\\input\\";
    private String fileName = "";
    private long threadId;

    public UploadThread(String fileName, int id) {
        super();
        this.channel = ManagedChannelBuilder.forAddress("localhost", id)
                .usePlaintext()
                .build();
        this.fileName = fileName;
        this.threadId = id;
    }



    @Override
    public void run() {
        System.out.println("upload started on port " + this.threadId);
        FileServiceGrpc.FileServiceStub stub = FileServiceGrpc.newStub(channel);
        CountDownLatch client = new CountDownLatch(1);

        StreamObserver<FileRequest> requestSender = stub.upload(new StreamObserver<FileResponse>() {

            @Override
            public void onNext(FileResponse value) {
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
                client.countDown();
            }

            @Override
            public void onCompleted() {
                client.countDown();
            }
        });

        File file = new File(filePath + fileName);
        try(FileInputStream input = new FileInputStream(file)) {
            long fileSize = Files.size(Paths.get(filePath + fileName));
            int data = 0;
            int count = 0;

            long loopCount = fileSize / FILE_SPLIT_UNIT;
            int remain = (int) (fileSize % FILE_SPLIT_UNIT);
            boolean hasRem = remain > 0;
            if(hasRem) {
                loopCount++;
            }

            for(int i = 0; i < loopCount; i++) {
                int byteLength = (i + 1 != loopCount) ? FILE_SPLIT_UNIT : remain;

                byte[] bytes = new byte[byteLength];
                data = input.read(bytes);

                requestSender.onNext(FileRequest.newBuilder().setName(fileName).setData(ByteString.copyFrom(bytes)).setId(threadId).build());

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        requestSender.onCompleted();

        try {
            client.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}

