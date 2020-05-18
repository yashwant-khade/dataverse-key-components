package com.grpc.dataverse.download.server;

import com.google.protobuf.ByteString;
import com.proto.download.DownloadRequest;
import com.proto.download.DownloadResponse;
import com.proto.download.DownloadServiceGrpc;
import com.proto.upload.FileRequest;
import com.proto.upload.FileResponse;
import com.proto.upload.FileServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileService extends DownloadServiceGrpc.DownloadServiceImplBase {

	public static final int FILE_SPLIT_UNIT = 4194276; //max 4MB

	private String outDir = "F:\\275\\output\\";

	@Override
	public void download(DownloadRequest request, StreamObserver<DownloadResponse> responseObserver) {

		File file = new File(outDir + request.getName());
		try(FileInputStream input = new FileInputStream(file)) {
			long fileSize = Files.size(Paths.get(outDir + request.getName()));

			long loopCount = fileSize / FILE_SPLIT_UNIT;
			int remain = (int) (fileSize % FILE_SPLIT_UNIT);
			boolean hasRem = remain > 0;
			if(hasRem) {
				loopCount++;
			}

			for(int i = 0; i < loopCount; i++) {
				int byteLength = (i + 1 != loopCount) ? FILE_SPLIT_UNIT : remain;

				byte[] bytes = new byte[byteLength];
				responseObserver.onNext(DownloadResponse.newBuilder().setName(request.getName()).setData(ByteString.copyFrom(bytes)).setId(i).build());
			}
			Thread.sleep(100);

			responseObserver.onCompleted();

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}



		System.out.println("Data transferred");
	}

//	public StreamObserver<FileRequest> upload(StreamObserver<FileResponse> responseObserver) {
//		List<ByteString> blob = new ArrayList<>();
//
//		return new StreamObserver<FileRequest>() {
//
//			long threadId = 0;
//			String fileName = "";
//			int count = 0;
//
//			@Override
//			public void onNext(FileRequest value) {
//				System.out.println("ThreadId : " + threadId + "->" + count + " times " + "FileService onNext start");
//
//				long before = System.currentTimeMillis();
//				System.out.println("ThreadId : " + threadId + "->" + count + " times before:" + before);
//
//				blob.add(value.getData());
//				fileName = value.getName();
//				threadId = value.getId();
//				System.out.println("ThreadId : " + threadId + "->"  + count + " times " + "FileService onNext end");
//
//				long after = System.currentTimeMillis();
//				System.out.println("ThreadId : " + threadId + "->" + count + " times after:" + after);
//
//				System.out.println("ThreadId : " + threadId + "->" + count + " times elapsed:" + (double) (after - before) / 1000 + " sec");
//
//				count++;
//			}
//
//			@Override
//			public void onError(Throwable t) {
//				System.out.println("ThreadId : " + threadId + "->" + count + " times " + "FileService onError start");
//				t.printStackTrace();
//				System.out.println("ThreadId : " + threadId + "->" + count + " times " + "FileService onError end");
//			}
//
//			@Override
//			public void onCompleted() {
//
//				System.out.println("ThreadId : " + threadId + "->" + count + " times " + "FileService onComplete start");
//
//				long before = System.currentTimeMillis();
//				System.out.println("ThreadId : " + threadId + "->" + count + " times before:" + before);
//
//				try(FileOutputStream out = new FileOutputStream(outDir + fileName)) {
//
//					//get the file data back from the bytes
//					int size = blob.stream().mapToInt(ByteString::size).sum();
//					ByteString bs = blob.stream().reduce((a, b) -> {
//						return a.concat(b);
//					}).get();
//
//					bs.writeTo(out);
////					ByteString.newOutput().writeTo(out);
//					responseObserver.onNext(FileResponse.newBuilder().setSize(size).build());
//					responseObserver.onCompleted();
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				long after = System.currentTimeMillis();
//				System.out.println("ThreadId : " + threadId + "->" + count + " times after:" + after);
//
//				System.out.println("ThreadId : " + threadId + "->" + count + " times " + "FileService onComplete end. elapsed:" + (double) (after - before) / 1000);
//			}
//
//		};
//	}


}
