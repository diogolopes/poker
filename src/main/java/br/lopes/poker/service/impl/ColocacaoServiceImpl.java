package br.lopes.poker.service.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.lopes.poker.domain.Colocacao;
import br.lopes.poker.domain.Ranking;
import br.lopes.poker.repository.ColocacaoRepository;
import br.lopes.poker.service.ColocacaoService;

@Service
public class ColocacaoServiceImpl implements ColocacaoService {

    private final ColocacaoRepository repository;

    @Autowired
    public ColocacaoServiceImpl(final ColocacaoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Set<Colocacao> findByRanking(final Ranking ranking) {
        return repository.findByRanking(ranking);
    }

    @Override
    public Colocacao findByRankingAndPessoaNome(final Ranking ranking, final String nome) {
        return repository.findByRankingAndPessoaNome(ranking, nome);
    }

}
