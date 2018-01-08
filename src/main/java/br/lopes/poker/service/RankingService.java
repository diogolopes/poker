package br.lopes.poker.service;

import java.util.Collection;
import java.util.List;

import br.lopes.poker.domain.Ranking;
import br.lopes.poker.domain.RankingType;

public interface RankingService {

	Ranking findByAno(final int ano, final RankingType rankingType);

	Ranking save(final Ranking ranking);

	Ranking clone(final Ranking ranking);

	List<Ranking> save(final Collection<Ranking> rankings);

	Ranking findById(final Integer id);

	List<Ranking> findAll();

	void delete(final Ranking ranking);

	Integer delete(final Integer id);
}
