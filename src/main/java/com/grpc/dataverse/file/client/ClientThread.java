package com.grpc.dataverse.file.client;

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

public class ClientThread implements Runnable {
	public static final int FILE_SPLIT_UNIT = 4194276; //max 4MB

	private ManagedChannel channel;
	private final String filePath = "F:\\275\\input\\";
	private String fileName = "";
	private long threadId;

	public ClientThread(String fileName, long id) {
		super();
		this.channel = ManagedChannelBuilder.forAddress("localhost", 50051)
		        .usePlaintext()
		        .build();
		this.fileName = fileName;
		this.threadId = id;
	}



	@Override
	public void run() {
		long threadStart = System.currentTimeMillis();
		System.out.println("Thread start : " + threadStart + " ThreadId : " + threadId);

		FileServiceGrpc.FileServiceStub stub = FileServiceGrpc.newStub(channel);
		CountDownLatch client = new CountDownLatch(1);

		StreamObserver<FileRequest> requestSender = stub.upload(new StreamObserver<FileResponse>() {

			@Override
			public void onNext(FileResponse value) {
				System.out.println("ThreadId : " + threadId + " " + "response message: " + value.getSize());
			}

			@Override
			public void onError(Throwable t) {
				System.out.println("ThreadId : " + threadId + " " + "Client onError start");
				t.printStackTrace();
				System.out.println("ThreadId : " + threadId + " " + "Client onError end");
			}

			@Override
			public void onCompleted() {
				System.out.println("ThreadId : " + threadId + " " + "Client onCompleted start");
				client.countDown();
				System.out.println("ThreadId : " + threadId + " " + "Client onCompleted end");
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

			long beforeAllRequest = System.currentTimeMillis();
			System.out.println("ThreadId : " + threadId + " " + "Send request start all : " + beforeAllRequest);
			for(int i = 0; i < loopCount; i++) {
				int byteLength = (i + 1 != loopCount) ? FILE_SPLIT_UNIT : remain;

				byte[] bytes = new byte[byteLength];
				data = input.read(bytes);

				long beforeRequest = System.currentTimeMillis();
				System.out.println("ThreadId : " + threadId + " " + "Send request start : " + beforeRequest);
				requestSender.onNext(FileRequest.newBuilder().setName(fileName).setData(ByteString.copyFrom(bytes)).setId(threadId).build());
				long afterRequest = System.currentTimeMillis();
				System.out.println("ThreadId : " + threadId + " " + "Send request end : " + afterRequest);
				double elapsedRequest = afterRequest - beforeRequest;
				System.out.println("ThreadId : " + threadId + " " + "Send request elapsed :" + elapsedRequest / 1000);
			}
			long afterAllRequest = System.currentTimeMillis();
			System.out.println("ThreadId : " + threadId + " " + "Send request end all : " + afterAllRequest);

		} catch (IOException e) {
			e.printStackTrace();
		}


		requestSender.onCompleted();

		try {
			client.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		long threadEnd = System.currentTimeMillis();
		System.out.println("ThreadId : " + threadId + "" + "Thread end : " + threadEnd);
		System.out.println("ThreadId : " + threadId + "" + "All process elapsed : " + ((double)(threadEnd - threadStart)) / 1000);

	}

}
