package br.lopes.poker.service;

import java.util.Map;

import br.lopes.poker.data.Classificacao;
import br.lopes.poker.domain.Pessoa;

public interface ExportRanking {

	void export(final Map<Pessoa, Classificacao> treeMap, final String ano) throws Exception;

}
