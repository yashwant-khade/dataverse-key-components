package com.grpc.dataverse.bully;

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

public class FileService1  extends FileServiceGrpc.FileServiceImplBase {

    private String outDir = "F:\\275\\server1\\";

    @Override
    public StreamObserver<FileRequest> upload(StreamObserver<FileResponse> responseObserver) {
        List<ByteString> blob = new ArrayList<>();

        return new StreamObserver<FileRequest>() {

            long threadId = 0;
            String fileName = "";

            @Override
            public void onNext(FileRequest value) {

                blob.add(value.getData());
                fileName = value.getName();
                threadId = value.getId();

            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {


                try (FileOutputStream out = new FileOutputStream(outDir + fileName)) {

                    //get the file data back from the bytes
                    int size = blob.stream().mapToInt(ByteString::size).sum();
                    ByteString bs = blob.stream().reduce((a, b) -> {
                        return a.concat(b);
                    }).get();

                    bs.writeTo(out);
//					ByteString.newOutput().writeTo(out);
                    responseObserver.onNext(FileResponse.newBuilder().setSize(size).build());
                    responseObserver.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        };
    }


}
