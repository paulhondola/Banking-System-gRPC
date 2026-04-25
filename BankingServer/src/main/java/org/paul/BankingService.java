package org.paul;

import com.google.common.util.concurrent.AtomicDouble;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BankingService extends BankingServiceGrpc.BankingServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(BankingService.class);

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
                    logger.info("Deposit {} += {} → balance: {}", request.getAccountId(), request.getAmount(), newBalance);
                    responseObserver.onNext(OperationResponse.newBuilder()
                            .setSuccess(true)
                            .setMessage("Deposit successful")
                            .setNewBalance(newBalance)
                            .build());
                    responseObserver.onCompleted();
                })
                .exceptionally(ex -> {
                    handleError(ex, responseObserver, "Deposit");
                    return null;
                });
    }

    @Override
    public void withdraw(WithdrawRequest request, StreamObserver<OperationResponse> responseObserver) {
        CompletableFuture
                .supplyAsync(() -> {
                    double amount = request.getAmount();
                    if (amount <= 0) {
                        throw new IllegalArgumentException("Withdrawal amount must be positive");
                    }

                    double[] newBalance = new double[1];
                    accounts.compute(request.getAccountId(), (k, v) -> {
                        double current = (v == null) ? 0.0 : v;
                        if (current < amount) {
                            throw new IllegalArgumentException("Insufficient funds");
                        }
                        newBalance[0] = current - amount;
                        return newBalance[0];
                    });

                    return newBalance[0];
                })
                .thenAccept(newBalance -> {
                    logger.info("Withdrawal {} -= {} → balance: {}", request.getAccountId(), request.getAmount(), newBalance);
                    responseObserver.onNext(OperationResponse.newBuilder()
                            .setSuccess(true)
                            .setMessage("Withdrawal successful")
                            .setNewBalance(newBalance)
                            .build());
                    responseObserver.onCompleted();
                })
                .exceptionally(ex -> {
                    handleError(ex, responseObserver, "Withdrawal");
                    return null;
                });
    }

    @Override
    public void transfer(TransferRequest request, StreamObserver<OperationResponse> responseObserver) {
        CompletableFuture
                .supplyAsync(() -> {
                    double amount = request.getAmount();
                    if (amount <= 0) {
                        throw new IllegalArgumentException("Transfer amount must be positive");
                    }

                    String from = request.getFromAccount();
                    String to = request.getToAccount();

                    if (from.equals(to)) {
                        throw new IllegalArgumentException("Cannot transfer to the same account");
                    }

                    // Atomically check balance and deduct from the sender
                    AtomicDouble newBalance = new AtomicDouble();
                    accounts.compute(from, (k, v) -> {
                        double current = (v == null) ? 0.0 : v;
                        if (current < amount) {
                            throw new IllegalArgumentException("Insufficient funds");
                        }
                        newBalance.set(current - amount);
                        return newBalance.get();
                    });

                    // Atomically add to the receiver
                    accounts.merge(to, amount, Double::sum);

                    return newBalance.get();
                })
                .thenAccept(newBalance -> {
                    logger.info("Transfer {} from {} to {} → new balance: {}", request.getAmount(), request.getFromAccount(), request.getToAccount(), newBalance);
                    responseObserver.onNext(OperationResponse.newBuilder()
                            .setSuccess(true)
                            .setMessage("Transfer successful")
                            .setNewBalance(newBalance)
                            .build());
                    responseObserver.onCompleted();
                })
                .exceptionally(ex -> {
                    handleError(ex, responseObserver, "Transfer");
                    return null;
                });
    }

    @Override
    public void getBalance(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {
        CompletableFuture
                .supplyAsync(() -> (double) accounts.getOrDefault(request.getAccountId(), 0.0))
                .thenAccept(balance -> {
                    logger.info("Balance for {}: {}", request.getAccountId(), balance);
                    responseObserver.onNext(BalanceResponse.newBuilder()
                            .setAccountId(request.getAccountId())
                            .setBalance(balance)
                            .build());
                    responseObserver.onCompleted();
                })
                .exceptionally(ex -> {
                    handleError(ex, responseObserver, "Get balance");
                    return null;
                });
    }

    private void handleError(Throwable ex, StreamObserver<?> responseObserver, String operation) {
        logger.error("{} failed", operation, ex);
        Throwable cause = (ex instanceof java.util.concurrent.CompletionException && ex.getCause() != null) ? ex.getCause() : ex;
        if (cause instanceof IllegalArgumentException) {
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription(cause.getMessage()).asRuntimeException());
        } else {
            responseObserver.onError(io.grpc.Status.INTERNAL.withDescription(cause.getMessage()).asRuntimeException());
        }
    }
}
