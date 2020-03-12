package com.grpc.dataverse.greeting.server;

import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.stub.StreamObserver;

public class GreetServiceImpl extends GreetServiceGrpc.GreetServiceImplBase {
    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
//        super.greet(request, responseObserver);
        Greeting greeting = request.getGreeting();
        String firstName = greeting.getFirstName();

        System.out.println(greeting);

        String result = "Hello " + firstName;

        GreetResponse greetResponse = GreetResponse
                .newBuilder()
                .setResult(result)
                .build();

        //send the response
        responseObserver.onNext(greetResponse);

        //complete the RPC call
        responseObserver.onCompleted();
    }
}
