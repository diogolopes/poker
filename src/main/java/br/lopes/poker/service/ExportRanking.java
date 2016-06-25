package br.lopes.poker.service;

import java.util.Map;

import br.lopes.poker.data.Classificacao;
import br.lopes.poker.domain.Pessoa;
import br.lopes.poker.domain.Ranking;

public interface ExportRanking {

	void export(final Map<Pessoa, Classificacao> treeMap, final Integer ano) throws Exception;

	void export(final Ranking ranking) throws Exception;

}
