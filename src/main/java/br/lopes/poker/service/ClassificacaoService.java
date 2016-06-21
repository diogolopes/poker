package br.lopes.poker.service;

import java.util.Map;
import java.util.Set;

import br.lopes.poker.data.Classificacao;
import br.lopes.poker.domain.Partida;
import br.lopes.poker.domain.Pessoa;
import br.lopes.poker.domain.Ranking;

public interface ClassificacaoService {

    public enum RankingType {
        SALDO, APROVEITAMENTO;
    }

    Map<Pessoa, Classificacao> ranking(final Partida partida, final RankingType rankingType);
    Map<Pessoa, Classificacao> ranking(final Set<Partida> partidas, final RankingType rankingType);
    Map<Pessoa, Classificacao> ranking(final Ranking ranking, final Set<Partida> partidas, final RankingType rankingType);

}