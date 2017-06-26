package br.lopes.poker.service.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.lopes.poker.domain.Colocacao;
import br.lopes.poker.domain.Ranking;
import br.lopes.poker.repository.RankingRepository;
import br.lopes.poker.service.ClassificacaoService.RankingType;
import br.lopes.poker.service.RankingService;

@Service
public class RankingServiceImpl implements RankingService {

    private final RankingRepository repository;

    @Autowired
    public RankingServiceImpl(final RankingRepository repository) {
        this.repository = repository;
    }

    @Override
    public Ranking findByAno(final int ano, final RankingType rankingType) {
        return repository.findFirstByAnoAndRankingTypeOrderByDataAtualizacaoDesc(ano, rankingType);
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

    @Override
    public Ranking clone(final Ranking ranking) {
        final Ranking clonedRanking = new Ranking();
        clonedRanking.setAno(ranking.getAno());
        clonedRanking.setDataAtualizacao(new Date());
        clonedRanking.setRankingType(ranking.getRankingType());

        ranking.getColocacoes().stream().forEach(c -> {
            final Colocacao colocacao = new Colocacao();
            // colocacao.setPessoa(pessoaRepository.findOne(c.getPessoa().getId()));
            colocacao.setPessoa(c.getPessoa());
            colocacao.setSaldo(c.getSaldo());
            colocacao.setJogos(c.getJogos());
            colocacao.setVitoria(c.getVitoria());
            colocacao.setEmpate(c.getEmpate());
            colocacao.setDerrota(c.getDerrota());
            colocacao.setPosicaoAtual(c.getPosicaoAtual());
            colocacao.setPosicaoAnterior(c.getPosicaoAnterior());
            clonedRanking.addColocacao(colocacao);
        });

        return repository.save(clonedRanking);
    }

    @Override
    public Ranking findById(final Integer id) {
        return repository.getOne(id);
    }

    @Override
    public List<Ranking> findAll() {
        return repository.findAll();
    }

    @Override
    public Integer delete(final Integer id) {
        final Ranking ranking = repository.getOne(id);
        if (ranking != null) {
            repository.delete(ranking);
            return ranking.getId();
        }
        return null;
    }

}
