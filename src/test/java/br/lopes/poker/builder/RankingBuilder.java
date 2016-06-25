package br.lopes.poker.builder;

import java.util.Date;

import br.lopes.poker.domain.Ranking;
import br.lopes.poker.faker.ColocacaoFaker;
import br.lopes.poker.faker.ColocacaoFaker.EColocacao;
import br.lopes.poker.faker.RankingFaker;
import br.lopes.poker.faker.RankingFaker.ERanking;

public class RankingBuilder {

    private final Ranking ranking;

    private RankingBuilder(final ERanking eRanking) {
        this.ranking = RankingFaker.get(eRanking);
    }

    public static RankingBuilder get(final ERanking eRanking) {
        return new RankingBuilder(eRanking);
    }

    public RankingBuilder withData(final Date data) {
        this.ranking.setDataAtualizacao(data);
        return this;
    }

    public RankingBuilder withColocacao(final EColocacao... eColocacoes) {
        for (final EColocacao eColocacao : eColocacoes) {
            this.ranking.addColocacao(ColocacaoFaker.get(eColocacao));
        }
        return this;
    }

    public Ranking build() {
        return ranking;
    }
}
