package br.lopes.poker.service.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.lopes.poker.domain.ItemRanking;
import br.lopes.poker.domain.Ranking;
import br.lopes.poker.repository.ItemRankingRepository;
import br.lopes.poker.service.ItemRankingService;

@Service
public class ItemRankingServiceImpl implements ItemRankingService {

    private final ItemRankingRepository repository;

    @Autowired
    public ItemRankingServiceImpl(final ItemRankingRepository repository) {
        this.repository = repository;
    }

    @Override
    public Set<ItemRanking> findByRanking(final Ranking ranking) {
        return repository.findByRanking(ranking);
    }

    @Override
    public ItemRanking findByRankingAndPessoaNome(final Ranking ranking, final String nome) {
        return repository.findByRankingAndPessoaNome(ranking, nome);
    }

}
