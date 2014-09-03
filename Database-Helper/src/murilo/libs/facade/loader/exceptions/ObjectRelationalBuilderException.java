package murilo.libs.facade.loader.exceptions;

public class ObjectRelationalBuilderException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public ObjectRelationalBuilderException() {
	}

	public ObjectRelationalBuilderException(String message) {
		super(message);
	}

	public ObjectRelationalBuilderException(Throwable cause) {
		super(cause);
	}

	public ObjectRelationalBuilderException(String message, Throwable cause) {
		super(message, cause);
	}
}
