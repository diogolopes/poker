package br.lopes.poker.service.impl;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.lopes.poker.domain.Ranking;
import br.lopes.poker.repository.RankingRepository;
import br.lopes.poker.service.RankingService;

@Service
public class RankingServiceImpl implements RankingService {

	private final RankingRepository repository;

	@Autowired
	public RankingServiceImpl(final RankingRepository repository) {
		this.repository = repository;
	}

	@Override
	public Ranking findByAno(int ano) {
		return repository.findByAno(ano);
	}

	@Override
	public Ranking save(final Ranking ranking) {
		return repository.save(ranking);
	}

	@Override
	public List<Ranking> save(final Collection<Ranking> rankings) {
		return repository.save(rankings);
	}

	@Override
	public void delete(final Ranking ranking) {
		repository.delete(ranking);
	}

}
