package br.lopes.poker.service;

import java.util.Collection;
import java.util.List;

import br.lopes.poker.domain.Ranking;

public interface RankingService {

    Ranking findByAno(final int ano);

    Ranking save(final Ranking ranking);

    List<Ranking> save(final Collection<Ranking> rankings);
}
