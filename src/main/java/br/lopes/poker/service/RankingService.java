package br.lopes.poker.service;

import java.util.Collection;
import java.util.List;

import br.lopes.poker.domain.Ranking;
import br.lopes.poker.service.ClassificacaoService.RankingType;

public interface RankingService {

    Ranking findByAno(final int ano, final RankingType rankingType);

    Ranking save(final Ranking ranking);

    Ranking clone(final Ranking ranking);

    List<Ranking> save(final Collection<Ranking> rankings);

    void delete(final Ranking ranking);
}
