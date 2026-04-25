package org.paul;

import io.grpc.stub.StreamObserver;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class BankingService extends BankingServiceGrpc.BankingServiceImplBase {

    private final ConcurrentHashMap<String, Double> accounts = new ConcurrentHashMap<>();

    @Override
    public void deposit(DepositRequest request, StreamObserver<OperationResponse> responseObserver) {
        CompletableFuture
                .supplyAsync(() -> {
                    double amount = request.getAmount();
                    if (amount <= 0) {
                        throw new IllegalArgumentException("Deposit amount must be positive");
                    }
                    return accounts.merge(request.getAccountId(), amount, Double::sum);
                })
                .thenAccept(newBalance -> {
                    System.out.printf("Deposit %s += %.2f → balance: %.2f%n",
                            request.getAccountId(), request.getAmount(), newBalance);
                    responseObserver.onNext(OperationResponse.newBuilder()
                            .setSuccess(true)
                            .setMessage("Deposit successful")
                            .setNewBalance(newBalance)
                            .build());
                    responseObserver.onCompleted();
                })
                .exceptionally(ex -> {
                    responseObserver.onError(ex);
                    return null;
                });
    }

    @Override
    public void withdraw(WithdrawRequest request, StreamObserver<OperationResponse> responseObserver) {
        // TODO
        responseObserver.onNext(OperationResponse.newBuilder().setSuccess(false).setMessage("Not implemented").build());
        responseObserver.onCompleted();
    }

    @Override
    public void transfer(TransferRequest request, StreamObserver<OperationResponse> responseObserver) {
        // TODO
        responseObserver.onNext(OperationResponse.newBuilder().setSuccess(false).setMessage("Not implemented").build());
        responseObserver.onCompleted();
    }

    @Override
    public void getBalance(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {
        // TODO
        responseObserver
                .onNext(BalanceResponse.newBuilder().setAccountId(request.getAccountId()).setBalance(0).build());
        responseObserver.onCompleted();
    }
}
