package br.lopes.poker.helper;

public final class PokerEnvironment {

    public static final String JSON = "application/json";
    public static final String JSON_CHARSET = JSON + "; charset=utf-8";

    private PokerEnvironment() {
        throw new IllegalAccessError();
    }
}
