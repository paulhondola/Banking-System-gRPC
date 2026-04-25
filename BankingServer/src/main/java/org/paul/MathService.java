package org.paul;

import io.grpc.stub.StreamObserver;
import java.util.concurrent.CompletableFuture;

public class MathService extends MathServiceGrpc.MathServiceImplBase {

    @Override
    public void add(BinaryOpRequest request, StreamObserver<BinaryOpResponse> responseObserver) {
        CompletableFuture
                .supplyAsync(() -> request.getA() + request.getB())
                .thenAccept(result -> {
                    System.out.println("MathService performed add, result=" + result);
                    responseObserver.onNext(BinaryOpResponse.newBuilder().setResult(result).build());
                    responseObserver.onCompleted();
                })
                .exceptionally(ex -> {
                    responseObserver.onError(ex);
                    return null;
                });
    }

    @Override
    public void mult(BinaryOpRequest request, StreamObserver<BinaryOpResponse> responseObserver) {
        CompletableFuture
                .supplyAsync(() -> request.getA() * request.getB())
                .thenAccept(result -> {
                    System.out.println("MathService performed mult, result=" + result);
                    responseObserver.onNext(BinaryOpResponse.newBuilder().setResult(result).build());
                    responseObserver.onCompleted();
                })
                .exceptionally(ex -> {
                    responseObserver.onError(ex);
                    return null;
                });
    }
}
