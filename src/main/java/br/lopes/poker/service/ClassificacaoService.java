package br.lopes.poker.service;

import java.util.Map;
import java.util.Set;

import br.lopes.poker.data.Classificacao;
import br.lopes.poker.domain.Partida;
import br.lopes.poker.domain.Pessoa;
import br.lopes.poker.domain.Ranking;

public interface ClassificacaoService {

    public enum RankingType {
        SALDO("Saldo"), APROVEITAMENTO("Aproveitamento");
        private final String nome;

        RankingType(final String nome) {
            this.nome = nome;
        }

        public String getNome() {
            return nome;
        }
    }

    Map<Pessoa, Classificacao> ranking(final Partida partida, final RankingType rankingType);

    Map<Pessoa, Classificacao> ranking(final Set<Partida> partidas, final RankingType rankingType);

    void generateRankingFileByRankingAndType(final Ranking ranking, final RankingType rankingType) throws Exception;

    void generateRankingFileByPartidasAndType(final Ranking ranking, final Set<Partida> partidas,
            final RankingType rankingType) throws Exception;

    void generateRankingFileByPartidasAndType(final Ranking ranking, final Set<Partida> partidas) throws Exception;

    void generateRankingFileByRanking(final Ranking ranking) throws Exception;

}
