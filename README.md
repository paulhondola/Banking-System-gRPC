# Banking System gRPC

A cross-language banking application built with gRPC and Protocol Buffers. This project demonstrates Remote Procedure Call (RPC) middleware concepts, implementing a Java-based server and a Python-based client that communicate using a shared `.proto` contract.

## Features

- **Cross-Language Communication**: Java Server and Python Client.
- **gRPC Middleware**: Uses `io.grpc` for Java and `grpcio` / `grpcio-tools` for Python.
- **Thread-safe Operations**: The Java server securely manages concurrent banking operations (deposit, withdraw, transfer).
- **Asynchronous Client**: The Python client leverages `grpc.aio` for non-blocking RPC calls.
- **Robust Server Logging**: The server incorporates Log4j2 and LMAX Disruptor for asynchronous, high-performance logging, including gRPC interceptors for request tracing.

## Project Structure

```text
Banking-System-gRPC/
├── BankingServer/       # Java Server implementation
│   ├── build.gradle.kts # Gradle build configuration
│   └── src/             # Java source code, gRPC services, and Log4j2 config
├── BankingClient/       # Python Client implementation
│   ├── proto/           # Contains the shared banking.proto file
│   ├── pyproject.toml   # Project dependencies and metadata
│   └── src/             # Python source code, generated stubs, and main app
└── docs/                # Project plans and task specifications
```

## Prerequisites

To run both parts of the application, you will need:

- **Java Development Kit (JDK) 17** or higher
- **Python 3.11** or higher
- **uv** (recommended for Python dependency management) or **pip**

## Getting Started

### 1. The Java Server

The Java server listens for banking requests and maintains the thread-safe, in-memory state of the bank accounts.

**Running the Server:**

Navigate to the `BankingServer` directory and use the included Gradle wrapper. The Gradle configuration uses the `protobuf-maven-plugin` equivalent to automatically generate the required Java stubs from your `.proto` file before building.

```bash
cd BankingServer
./gradlew run
```

This will download dependencies, compile the Protocol Buffers, build the Java application, and start the gRPC server on `localhost:50051`.

### 2. The Python Client

The Python client connects to the Java server and executes asynchronous banking operations to test the connection and logic.

**Setting up the Environment:**

Navigate to the `BankingClient` directory. It is highly recommended to use a virtual environment.

Using `uv` (faster):
```bash
cd BankingClient
uv venv
source .venv/bin/activate  # On Windows: .venv\Scripts\activate
uv pip install -e .
```

Using standard `pip`:
```bash
cd BankingClient
python -m venv .venv
source .venv/bin/activate  # On Windows: .venv\Scripts\activate
pip install -e .
```

**Generating Stubs:**

Before running the client, generate the Python gRPC stubs from the `banking.proto` file. The project includes a custom command mapped to `src/gen_stubs.py` for this exact purpose:

```bash
gen-stubs
```

**Running the Client:**

Ensure the Java server is running in a separate terminal. Then, execute the client:

```bash
python src/main.py
```

You should see output in both the client terminal (showing operation successes and balances) and the server terminal (logging the intercepted RPC requests).

## The Protocol Buffer Contract

The entire communication interface is defined via Protocol Buffers. It supports the following RPC operations:

- `Deposit`: Adds funds to a specified account.
- `Withdraw`: Removes funds from a specified account, failing safely if there are insufficient funds.
- `Transfer`: Safely moves funds between two accounts in an atomic fashion.
- `GetBalance`: Retrieves the current balance for an account.

For full details on request and response messages, see the `.proto` file located at `BankingClient/proto/banking.proto`.
