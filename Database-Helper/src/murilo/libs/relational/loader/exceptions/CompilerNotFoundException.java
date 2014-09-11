package murilo.libs.relational.loader.exceptions;

public class CompilerNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public CompilerNotFoundException() {
	}

	public CompilerNotFoundException(String message) {
		super(message);
	}

	public CompilerNotFoundException(Throwable cause) {
		super(cause);
	}

	public CompilerNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
