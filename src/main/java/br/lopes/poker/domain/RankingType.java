package br.lopes.poker.domain;

public enum RankingType {
	SALDO("Saldo"), APROVEITAMENTO("Aproveitamento"), PONTUACAO("Pontuacao");
	private final String nome;

	RankingType(final String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}
}