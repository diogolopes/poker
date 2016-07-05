package br.lopes.poker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.lopes.poker.domain.Ranking;
import br.lopes.poker.service.ClassificacaoService.RankingType;

public interface RankingRepository extends JpaRepository<Ranking, Integer> {

    Ranking findFirstByAnoAndRankingTypeOrderByDataAtualizacaoDesc(final Integer ano, final RankingType rankingType);

}
