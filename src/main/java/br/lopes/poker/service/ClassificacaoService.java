package br.lopes.poker.service;

import java.util.Map;
import java.util.Set;

import br.lopes.poker.data.ClassificacaoImpl;
import br.lopes.poker.domain.Partida;
import br.lopes.poker.domain.Pessoa;
import br.lopes.poker.domain.Ranking;

public interface ClassificacaoService {

    public enum RankingType {
        SALDO, APROVEITAMENTO;
    }

    Map<Pessoa, ClassificacaoImpl> ranking(final Partida partida, final RankingType rankingType);
    Map<Pessoa, ClassificacaoImpl> ranking(final Set<Partida> partidas, final RankingType rankingType);
    Ranking ranking(final Ranking ranking, final Set<Partida> partidas, final RankingType rankingType);

}
