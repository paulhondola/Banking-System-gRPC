package org.paul;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BankingServer {

    private static final Logger logger = LoggerFactory.getLogger(BankingServer.class);

    public static void main(String[] args) {
        try {
            Server server = ServerBuilder
                    .forPort(50051)
                    .addService(ServerInterceptors.intercept(new BankingService(), new LoggingInterceptor()))
                    .build();

            server.start();
            logger.info("Server started on port 50051");

            server.awaitTermination();

        } catch (Exception e) {
            logger.error("Server failed to start or was interrupted", e);
        }
    }
}
