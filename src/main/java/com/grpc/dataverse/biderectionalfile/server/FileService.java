package com.grpc.dataverse.biderectionalfile.server;

import com.google.protobuf.ByteString;
import com.proto.upload.FileRequest;
import com.proto.upload.FileResponse;
import com.proto.upload.FileServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileService extends FileServiceGrpc.FileServiceImplBase {

	private String outDir = "F:\\275\\output\\";

	@Override
	public StreamObserver<FileRequest> upload(StreamObserver<FileResponse> responseObserver) {
		List<ByteString> blob = new ArrayList<>();

		return new StreamObserver<FileRequest>() {

			long threadId = 0;
			String fileName = "";
			int count = 0;

			@Override
			public void onNext(FileRequest value) {
				System.out.println("ThreadId : " + threadId + "->" + count + " times " + "FileService onNext start");

				long before = System.currentTimeMillis();
				System.out.println("ThreadId : " + threadId + "->" + count + " times before:" + before);

				blob.add(value.getData());
				fileName = value.getName();
				threadId = value.getId();
				System.out.println("ThreadId : " + threadId + "->"  + count + " times " + "FileService onNext end");

				long after = System.currentTimeMillis();
				System.out.println("ThreadId : " + threadId + "->" + count + " times after:" + after);

				System.out.println("ThreadId : " + threadId + "->" + count + " times elapsed:" + (double) (after - before) / 1000 + " sec");

				count++;
			}

			@Override
			public void onError(Throwable t) {
				System.out.println("ThreadId : " + threadId + "->" + count + " times " + "FileService onError start");
				t.printStackTrace();
				System.out.println("ThreadId : " + threadId + "->" + count + " times " + "FileService onError end");
			}

			@Override
			public void onCompleted() {

				System.out.println("ThreadId : " + threadId + "->" + count + " times " + "FileService onComplete start");

				long before = System.currentTimeMillis();
				System.out.println("ThreadId : " + threadId + "->" + count + " times before:" + before);

				try(FileOutputStream out = new FileOutputStream(outDir + fileName)) {

					//get the file data back from the bytes
					int size = blob.stream().mapToInt(ByteString::size).sum();
					ByteString bs = blob.stream().reduce((a, b) -> {
						return a.concat(b);
					}).get();

					bs.writeTo(out);
//					ByteString.newOutput().writeTo(out);
					responseObserver.onNext(FileResponse.newBuilder().setSize(size).build());
					responseObserver.onCompleted();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				long after = System.currentTimeMillis();
				System.out.println("ThreadId : " + threadId + "->" + count + " times after:" + after);

				System.out.println("ThreadId : " + threadId + "->" + count + " times " + "FileService onComplete end. elapsed:" + (double) (after - before) / 1000);
			}

		};
	}


}
