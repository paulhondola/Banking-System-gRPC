package org.paul;

import io.grpc.ForwardingServerCall;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

import java.time.Instant;

public class LoggingInterceptor implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        String method = call.getMethodDescriptor().getFullMethodName();
        System.out.printf("[%s] Incoming request: %s%n", Instant.now(), method);

        return next.startCall(
                new ForwardingServerCall.SimpleForwardingServerCall<>(call) {},
                headers
        );
    }
}
