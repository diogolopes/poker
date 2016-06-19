package br.lopes.poker.service;

import java.util.Map;
import java.util.Set;

import br.lopes.poker.data.Ranking;
import br.lopes.poker.domain.Partida;
import br.lopes.poker.domain.Pessoa;

public interface RankingService {

    enum RankingType {
        SALDO, APROVEITAMENTO;
    }

    Map<Pessoa, Ranking> ranking(final Partida partida, final RankingType rankingType);
    Map<Pessoa, Ranking> ranking(final Set<Partida> partidas, final RankingType rankingType);

}
