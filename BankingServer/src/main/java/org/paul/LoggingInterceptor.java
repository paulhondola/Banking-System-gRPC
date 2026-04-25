package org.paul;

import io.grpc.ForwardingServerCall;
import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingInterceptor implements ServerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        String method = call.getMethodDescriptor().getFullMethodName();
        logger.debug("Incoming request: {}", method);

        long startTime = System.nanoTime();

        ServerCall.Listener<ReqT> listener = next.startCall(
                new ForwardingServerCall.SimpleForwardingServerCall<>(call) {
                },
                headers);

        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<>(listener) {
            @Override
            public void onComplete() {
                long durationMs = (System.nanoTime() - startTime) / 1_000_000;
                logger.info("Completed request: {} in {}ms", method, durationMs);
                super.onComplete();
            }

            @Override
            public void onCancel() {
                long durationMs = (System.nanoTime() - startTime) / 1_000_000;
                logger.warn("Cancelled request: {} after {}ms", method, durationMs);
                super.onCancel();
            }
        };
    }
}
