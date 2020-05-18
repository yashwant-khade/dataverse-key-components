package com.grpc.dataverse.bully;

import com.google.common.hash.Funnel;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.hash.PrimitiveSink;
import com.proto.message.MessageRequest;
import com.proto.message.MessageResponse;
import com.proto.message.MessageServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class MessageService extends MessageServiceGrpc.MessageServiceImplBase {


    static TreeMap<Integer,Integer> processes_port;

    MessageService() {
        processes_port = new TreeMap<>();

        processes_port.put(5,55110);
        processes_port.put(4,55120);
        processes_port.put(3,55130);
        processes_port.put(2,55140);
    }

    private static final HashFunction hfunc = Hashing.murmur3_128();

    private static final Funnel<String> strFunnel = new Funnel<String>(){
        public void funnel(String from, PrimitiveSink into) {
            into.putBytes(from.getBytes());
        }};

    private static RendezvousHash<String, String> genEmpty() {
        return new RendezvousHash<String, String>(hfunc, strFunnel, strFunnel, new ArrayList<String>());
    }

    public static String getServerandReplica(String file_key){

        RendezvousHash<String, String> h = genEmpty();

        for (int key : processes_port.keySet())
        {

            h.add(String.valueOf(processes_port.get(key)));
        }


        String bestserver=h.get(file_key);
        RendezvousHash<String, String> h1 = genEmpty();
        for (int key : processes_port.keySet())
        {

            if (!String.valueOf(processes_port.get(key)).equals(bestserver)) {
                h1.add(String.valueOf(processes_port.get(key)));
            }

        }

        String repServer =h1.get(file_key);


        return bestserver+","+repServer;

    }

    @Override
    public void sendMessage(MessageRequest request, StreamObserver<MessageResponse> responseObserver) {
        if (request.getMessage().equals("service_port")) {

            String server = getServerandReplica(request.getFilename());
            responseObserver.onNext(MessageResponse.newBuilder().setResult(server).build());

            //complete the RPC call
            responseObserver.onCompleted();
        }
    }

}
