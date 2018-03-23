package br.lopes.poker.helper.ranking;

import java.util.Map;
import java.util.TreeMap;

import br.lopes.poker.data.ExportedItemRanking;
import br.lopes.poker.domain.Pessoa;
import br.lopes.poker.domain.RankingType;

public class RankingCriteriaFactory {

	public Map<Pessoa, ExportedItemRanking> create(final Map<Pessoa, ExportedItemRanking> exportedtemRanking,
			final RankingType rankingType) {
		switch (rankingType) {
		case PONTUACAO:
			return new TreeMap<Pessoa, ExportedItemRanking>(new RankingByPontuacaoComparator(exportedtemRanking));
		default:
			throw new IllegalArgumentException("Ranking type invalid");
		}
	}
}