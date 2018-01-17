package br.lopes.poker.service.sheet;

import java.util.Map;

import br.lopes.poker.data.ExportedItemRanking;
import br.lopes.poker.domain.Pessoa;
import br.lopes.poker.domain.Ranking;
import br.lopes.poker.domain.RankingType;

public interface ExportRankingService {

	void export(final Map<Pessoa, ExportedItemRanking> exportedItemRankingMap, final Ranking ranking) throws Exception;

	void export(final Ranking ranking) throws Exception;

	void export(final Ranking ranking, final RankingType rankingType) throws Exception;

}
