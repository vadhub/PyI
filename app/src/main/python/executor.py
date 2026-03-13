import sys
from io import StringIO

def execute(code: str) -> str:
    old_stdout = sys.stdout
    sys.stdout = StringIO()
    try:
        exec(code)
        output = sys.stdout.getvalue()
    except Exception as e:
        output = f"error executor: {type(e).__name__}: {e}"
    finally:
        sys.stdout = old_stdout
    return output