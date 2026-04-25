# Banking gRPC System — Technology & Implementation Plan

## Overview

A banking client-server application using **gRPC** and **Protocol Buffers** as the communication middleware. The system demonstrates cross-language RPC: the server runs on the JVM (Java), the client runs in Python — both share a single `.proto` contract.

---

## Technology Stack

### Server — Java 17 + Maven

**Rationale:**

- The provided `docs/MathGRPC/` example is a complete reference implementation in Java — the banking server adapts the same Maven + `protobuf-maven-plugin` setup.
- `grpc-java` (io.grpc) is the reference gRPC implementation for the JVM — battle-tested, well-documented.
- Maven auto-generates Java stubs from `.proto` at `mvn compile` — no manual `protoc` invocation needed.

**Key dependencies:**

| Dependency              | Purpose                                 |
| ----------------------- | --------------------------------------- |
| `grpc-netty-shaded`     | HTTP/2 transport layer                  |
| `grpc-protobuf`         | Protobuf serialization                  |
| `grpc-stub`             | Generated blocking/async stubs          |
| `protobuf-maven-plugin` | Runs `protoc` at build time             |
| `javax.annotation-api`  | Required for Java 9+ annotation support |

---

### Client — Python 3

**Rationale:**

- Maximum language contrast with Java — different type system, runtime, and ecosystem.
- `grpcio` and `grpcio-tools` are first-class gRPC libraries maintained by Google.
- `grpc_tools.protoc` generates Python stubs from the **same shared `.proto`** used by the Java server, directly demonstrating cross-language interoperability.
- No compilation or build system needed; the client runs with a single `python client.py` command.
- Concise, readable code — ideal for academic demonstration of the RPC pattern.

**Key packages:**

| Package        | Purpose                                      |
| -------------- | -------------------------------------------- |
| `grpcio`       | gRPC runtime for Python                      |
| `grpcio-tools` | Includes `protoc` for Python stub generation |

---

## Shared Proto Contract

One `.proto` file defines the service — used by **both** Java and Python:

```protobuf
syntax = "proto3";

package banking;

option java_package = "banking";
option java_multiple_files = true;

service BankingService {
  rpc Deposit    (DepositRequest)   returns (OperationResponse);
  rpc Withdraw   (WithdrawRequest)  returns (OperationResponse);
  rpc Transfer   (TransferRequest)  returns (OperationResponse);
  rpc GetBalance (BalanceRequest)   returns (BalanceResponse);
}

message DepositRequest  { string account_id = 1; double amount = 2; }
message WithdrawRequest { string account_id = 1; double amount = 2; }
message TransferRequest { string from_account = 1; string to_account = 2; double amount = 3; }
message BalanceRequest  { string account_id = 1; }

message OperationResponse { bool success = 1; string message = 2; double new_balance = 3; }
message BalanceResponse   { string account_id = 1; double balance = 2; }
```

---

## Project Structure

```
Banking-System-gRPC/
├── proto/
│   └── banking.proto              # Shared contract (single source of truth)
├── server/                        # Java (Maven)
│   ├── pom.xml
│   └── src/main/
│       ├── proto/                 # Copy of banking.proto (for Maven plugin)
│       └── java/banking/
│           ├── BankingServiceImpl.java   # Business logic + in-memory store
│           └── BankingServer.java        # Server entry point (port 50051)
└── client/                        # Python
    ├── requirements.txt
    ├── generate_stubs.sh          # python -m grpc_tools.protoc ...
    ├── banking_pb2.py             # generated — Protobuf messages
    ├── banking_pb2_grpc.py        # generated — service stubs
    └── banking_client.py          # Demo: deposit, withdraw, transfer, balance
```

---

## Implementation Steps

### 1. Proto

Create `proto/banking.proto` with the contract above.

### 2. Java Server

- Copy `docs/MathGRPC/pom.xml` as base — update `artifactId`, point proto source at `src/main/proto/`.
- `BankingServiceImpl` extends `BankingServiceGrpc.BankingServiceImplBase`, holds a `Map<String, Double>` as the in-memory account store.
- Implement all four RPCs with proper validation (insufficient funds, unknown account).
- `BankingServer` starts `ServerBuilder.forPort(50051)`.

**Build & run:**

```bash
cd server
mvn clean compile
mvn exec:java -Dexec.mainClass="banking.BankingServer"
```

### 3. Python Client

Generate stubs from the shared proto:

```bash
cd client
pip install grpcio grpcio-tools
python -m grpc_tools.protoc -I../proto \
    --python_out=. --grpc_python_out=. banking.proto
```

`banking_client.py` connects to `localhost:50051` and exercises all four operations with example accounts.

**Run:**

```bash
python banking_client.py
```

---

## Verification

| Step         | Command                                                  | Expected                                 |
| ------------ | -------------------------------------------------------- | ---------------------------------------- |
| Start server | `mvn exec:java -Dexec.mainClass="banking.BankingServer"` | `Server started on port 50051`           |
| Run client   | `python banking_client.py`                               | All 4 operations return correct balances |
| Server logs  | (stdout)                                                 | RPC calls logged per operation           |

---

## Key Design Decisions

| Decision        | Choice                    | Reason                                             |
| --------------- | ------------------------- | -------------------------------------------------- |
| Account storage | `HashMap<String, Double>` | Simplicity — focus on RPC, not persistence         |
| Transport       | Plaintext (no TLS)        | Academic context, matches MathGRPC example         |
| Stub type       | Blocking (synchronous)    | Simpler to demonstrate; matches MathClient pattern |
| Port            | 50051                     | gRPC convention, matches provided example          |
