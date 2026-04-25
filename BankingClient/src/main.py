import asyncio
import logging

import grpc.aio
import banking_pb2
import banking_pb2_grpc


from logger import setup_logging


async def main() -> None:
    setup_logging()

    async with grpc.aio.insecure_channel("localhost:50051") as channel:
        stub = banking_pb2_grpc.BankingServiceStub(channel)

        logging.info("Connected to BankingService.")

        # 1. Deposit
        try:
            deposit_req = banking_pb2.DepositRequest(account_id="acc1", amount=100.0)
            response = await stub.Deposit(deposit_req)
            print(
                f"Deposit: {response.success}, {response.message}, Balance: {response.new_balance}"
            )
        except grpc.aio.AioRpcError as e:
            logging.error(f"Deposit failed: Status {e.code()} - {e.details()}")

        # 2. Withdraw
        try:
            withdraw_req = banking_pb2.WithdrawRequest(account_id="acc1", amount=150.0)
            response = await stub.Withdraw(withdraw_req)
            print(
                f"Withdraw: {response.success}, {response.message}, Balance: {response.new_balance}"
            )
        except grpc.aio.AioRpcError as e:
            logging.error(f"Withdraw failed: Status {e.code()} - {e.details()}")

        # 3. Transfer
        try:
            transfer_req = banking_pb2.TransferRequest(
                from_account="acc1", to_account="acc2", amount=25.0
            )
            response = await stub.Transfer(transfer_req)
            print(
                f"Transfer: {response.success}, {response.message}, Balance: {response.new_balance}"
            )
        except grpc.aio.AioRpcError as e:
            logging.error(f"Transfer failed: Status {e.code()} - {e.details()}")

        # 4. GetBalance
        try:
            balance_req = banking_pb2.BalanceRequest(account_id="acc1")
            response = await stub.GetBalance(balance_req)
            print(f"Balance for {response.account_id}: {response.balance}")
        except grpc.aio.AioRpcError as e:
            logging.error(f"GetBalance failed: Status {e.code()} - {e.details()}")


if __name__ == "__main__":
    asyncio.run(main())
