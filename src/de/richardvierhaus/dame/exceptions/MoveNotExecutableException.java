package de.richardvierhaus.dame.exceptions;

public class MoveNotExecutableException extends RuntimeException{

	private static final long serialVersionUID = -1905805490877441945L;

	public MoveNotExecutableException() {
	}
	public MoveNotExecutableException(String errorMessage) {
		super(errorMessage);
	}

}
