import asyncio

import grpc.aio
import math_pb2
import math_pb2_grpc


async def main() -> None:
    async with grpc.aio.insecure_channel("localhost:50051") as channel:
        stub = math_pb2_grpc.MathServiceStub(channel)
        request = math_pb2.BinaryOpRequest(a=5, b=3)

        add_response, mult_response = await asyncio.gather(
            stub.Add(request),
            stub.Mult(request),
        )

        print(f"Add:  {add_response.result}")
        print(f"Mult: {mult_response.result}")


if __name__ == "__main__":
    asyncio.run(main())
