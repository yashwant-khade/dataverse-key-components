package com.grpc.dataverse.grpcbully.server;

import com.proto.bully.BullyRequest;
import com.proto.bully.BullyResponse;
import com.proto.bully.BullyServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.HashMap;

public class BullyServerImpl extends BullyServiceGrpc.BullyServiceImplBase {

    static int leader_id=-1;
    static int self_id=-1;
    static int server_Port = 5511 ;
    String operation;
    String reqtype;
    static int source_id=-1;
    static HashMap<Integer,String> processes= new HashMap<Integer,String>();
    static HashMap<Integer,Integer> processes_port= new HashMap<>();
    static boolean received=false;
    static long start_time=-1;
    static boolean leader_flag=false;
    static boolean election_req=false;
    static int higher;
    static int ok_ctr=0;
    static long  start_time_ok=-1;

//    public BullyAlgo(String operation) {
//        this.operation = operation;
//
//    }
//
//    public BullyAlgo(String operation, String reqtype) {
//        this.operation = operation;
//        this.reqtype=reqtype;
//
//    }
    @Override
    public StreamObserver<BullyRequest> greet(StreamObserver<BullyResponse> responseObserver) {
        return super.greet(responseObserver);
    }
}
