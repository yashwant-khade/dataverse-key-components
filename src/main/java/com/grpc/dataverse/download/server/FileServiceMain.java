package com.grpc.dataverse.download.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;


public class FileServiceMain {

	public static void main(String[] args) throws IOException, InterruptedException {
		Server server = ServerBuilder.forPort(50051)
//				.maxInboundMessageSize(FILE_SPLIT_UNIT)
//				.maxInboundMetadataSize(Integer.MAX_VALUE)
				.addService(new FileService())
				.build();

		server.start();
		System.out.println("start server on port:" + 50051);
		server.awaitTermination();
	}
}
