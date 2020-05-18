package com.grpc.dataverse.biderectionalfile.client;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class ClientMain {
	private static final int THREAD_COUNT = 3;
	private static final String SEND_FILE_NAME = "myfile";
	private static ExecutorService executorService;



	public static void main(String[] args) throws InterruptedException, IOException {

		long start = System.currentTimeMillis();
		System.out.println("Start :" + start);

		executorService = Executors.newFixedThreadPool(THREAD_COUNT);

		for(int i = 0; i < THREAD_COUNT; i++) {
			executorService.submit(new ClientThread(SEND_FILE_NAME+i+".mp4" , i));
		}


		try {
			executorService.shutdown();
			executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			// TODO
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		System.out.println("End : " + end);

		double elapsed = (double) (end - start) / 1000;

		System.out.println("Elapsed : " + elapsed + " sec");

	}
}
