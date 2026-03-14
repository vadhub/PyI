import sys
from io import StringIO

def execute(code: str, input_data: str = "") -> str:

    old_stdout = sys.stdout
    old_stdin = sys.stdin

    stdout_buffer = StringIO()
    stdin_buffer = StringIO(input_data)

    try:
        sys.stdout = stdout_buffer
        sys.stdin = stdin_buffer

        exec(code)

        output = stdout_buffer.getvalue()
    except Exception as e:
        output = f"error executor: {type(e).__name__}: {e}"
    finally:
        sys.stdout = old_stdout
        sys.stdin = old_stdin

    return output