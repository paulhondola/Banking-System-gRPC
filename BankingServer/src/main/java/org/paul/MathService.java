package org.paul;

import io.grpc.stub.StreamObserver;

public class MathService extends MathServiceGrpc.MathServiceImplBase {

    @Override
    public void add(BinaryOpRequest request, StreamObserver<BinaryOpResponse> responseObserver) {
        
        int result = request.getA() + request.getB();

        System.out.println("MathService performed add, result="+result);

        BinaryOpResponse response = BinaryOpResponse.newBuilder()
                .setResult(result)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void mult(BinaryOpRequest request, StreamObserver<BinaryOpResponse> responseObserver) {

        int result = request.getA() * request.getB();

        System.out.println("MathService performed mult, result="+result);

        BinaryOpResponse response = BinaryOpResponse.newBuilder()
                .setResult(result)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
