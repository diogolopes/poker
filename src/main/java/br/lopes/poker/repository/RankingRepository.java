package br.lopes.poker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.lopes.poker.domain.Ranking;

public interface RankingRepository extends JpaRepository<Ranking, Integer> {

    Ranking findByAno(final int ano);

}
