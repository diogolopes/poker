package br.lopes.poker.service;

import java.util.Set;

import br.lopes.poker.domain.Colocacao;
import br.lopes.poker.domain.Ranking;

public interface ColocacaoService {

    Set<Colocacao> findByRanking(final Ranking ranking);

    Colocacao findByRankingAndPessoaNome(final Ranking ranking, final String nome);

}
