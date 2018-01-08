package br.lopes.poker.service.sheet;

import java.util.Set;

import br.lopes.poker.domain.Partida;
import br.lopes.poker.domain.Ranking;
import br.lopes.poker.domain.RankingType;

public interface ClassificacaoService {

	void generateRankingFileByRankingAndType(final Ranking ranking, final RankingType rankingType) throws Exception;

	void generateRankingFileByPartidasAndType(final Ranking ranking, final Set<Partida> partidas,
			final RankingType rankingType) throws Exception;

	void generateRankingFileByPartidasAndType(final Ranking ranking, final Set<Partida> partidas) throws Exception;

	void generateRankingFileByRanking(final Ranking ranking) throws Exception;

}
