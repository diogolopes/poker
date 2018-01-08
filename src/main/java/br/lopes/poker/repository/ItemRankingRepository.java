package br.lopes.poker.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import br.lopes.poker.domain.ItemRanking;
import br.lopes.poker.domain.Ranking;

public interface ItemRankingRepository extends JpaRepository<ItemRanking, Integer> {

    Set<ItemRanking> findByRanking(final Ranking ranking);
    ItemRanking findByRankingAndPessoaNome(final Ranking ranking, final String nome);
}
