import subprocess
import sys
from pathlib import Path


def main() -> None:
    src = Path(__file__).parent
    proto_dir = src.parent / "proto"
    subprocess.run(
        [
            sys.executable, "-m", "grpc_tools.protoc",
            f"-I{proto_dir}",
            f"--python_out={src}",
            f"--grpc_python_out={src}",
            str(proto_dir / "banking.proto"),
        ],
        check=True,
    )
    print("Stubs regenerated.")
