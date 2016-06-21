package br.lopes.poker.faker;

import java.time.LocalDate;

import br.lopes.poker.domain.Ranking;

public class RankingFaker {

    public enum ERanking {
        HOJE(2016, LocalDate.now()), //
        MAIO(2016, LocalDate.of(2016, 05, 10)), //
        ABRIL(2016, LocalDate.of(2016, 04, 10));

        private final int ano;
        private final LocalDate data;

        private ERanking(final int ano, final LocalDate data) {
            this.ano = ano;
            this.data = data;
        }
    }

    public static Ranking get(final ERanking eRanking) {
        final Ranking ranking = new Ranking();
        ranking.setAno(eRanking.ano);
        ranking.setDataAtualizacao(eRanking.data);
        return ranking;
    }
}
