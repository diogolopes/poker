package br.lopes.poker.faker;

import java.time.LocalDate;

import br.lopes.poker.domain.Ranking;

public class RankingFaker {

	public enum ERanking {
		HOJE(LocalDate.now()), //
		MAIO(LocalDate.of(2016, 05, 10)), //
		ABRIL(LocalDate.of(2016, 04, 10));

		private final LocalDate data;

		private ERanking(final LocalDate data) {
			this.data = data;
		}
	}

	public static Ranking get(final ERanking eRanking) {
		final Ranking ranking = new Ranking();
		ranking.setDataAtualizacao(eRanking.data);
		return ranking;
	}
}
