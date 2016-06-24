package br.lopes.poker.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import br.lopes.poker.domain.Colocacao;
import br.lopes.poker.domain.Ranking;

public interface ColocacaoRepository extends JpaRepository<Colocacao, Integer> {

    Set<Colocacao> findByRanking(final Ranking ranking);
    Colocacao findByRankingAndPessoaNome(final Ranking ranking, final String nome);
}
