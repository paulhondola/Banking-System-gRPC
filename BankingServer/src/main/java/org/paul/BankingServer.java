package org.paul;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;

public class BankingServer {

    public static void main(String[] args) {
        try {
            Server server = ServerBuilder
                    .forPort(50051)
                    .addService(ServerInterceptors.intercept(new BankingService(), new LoggingInterceptor()))
                    .build();

            server.start();
            System.out.println("Server started on port 50051");

            server.awaitTermination();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
