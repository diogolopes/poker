package br.lopes.poker.service;

import java.util.Set;

import br.lopes.poker.domain.ItemRanking;
import br.lopes.poker.domain.Ranking;

public interface ItemRankingService {

    Set<ItemRanking> findByRanking(final Ranking ranking);

    ItemRanking findByRankingAndPessoaNome(final Ranking ranking, final String nome);

}
