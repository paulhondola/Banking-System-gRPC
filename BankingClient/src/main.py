import grpc
import math_pb2
import math_pb2_grpc


def main() -> None:
    with grpc.insecure_channel("localhost:50051") as channel:
        stub = math_pb2_grpc.MathServiceStub(channel)
        request = math_pb2.BinaryOpRequest(a=5, b=3)

        add_response = stub.Add(request)
        print(f"Add: {add_response.result}")

        mult_response = stub.Mult(request)
        print(f"Mult: {mult_response.result}")


if __name__ == "__main__":
    main()
