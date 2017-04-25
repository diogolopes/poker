package br.lopes.poker.exception;

public class PokerException extends Exception {

	private static final long serialVersionUID = -6760723356120318405L;

	public PokerException(final Throwable throwable) {
		super(throwable);
	}

	public PokerException(final String message, final Throwable throwable) {
		super(message, throwable);
	}
}
