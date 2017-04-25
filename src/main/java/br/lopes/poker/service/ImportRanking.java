package br.lopes.poker.service;

import java.util.List;

import br.lopes.poker.domain.Ranking;
import br.lopes.poker.exception.PokerException;

public interface ImportRanking {

    List<Ranking> importRankings() throws PokerException;

}
