package br.lopes.poker.service;

import java.util.Map;

import br.lopes.poker.data.Classificacao;
import br.lopes.poker.domain.Pessoa;
import br.lopes.poker.domain.Ranking;
import br.lopes.poker.service.ClassificacaoService.RankingType;

public interface ExportRanking {

    void export(final Map<Pessoa, Classificacao> treeMap, final Integer ano, final RankingType rankingType) throws Exception;

    void export(final Ranking ranking) throws Exception;

    void export(final Ranking ranking, final RankingType rankingType) throws Exception;

}
