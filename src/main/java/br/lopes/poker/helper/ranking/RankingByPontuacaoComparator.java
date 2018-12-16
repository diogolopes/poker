package br.lopes.poker.helper.ranking;

import java.util.Comparator;
import java.util.Map;

import br.lopes.poker.data.ExportedItemRanking;
import br.lopes.poker.domain.Pessoa;

public class RankingByPontuacaoComparator implements Comparator<Pessoa> {
		private final Map<Pessoa, ExportedItemRanking> base;

		public RankingByPontuacaoComparator(final Map<Pessoa, ExportedItemRanking> base) {
			this.base = base;
		}

		public int compare(final Pessoa a, final Pessoa b) {
			final ExportedItemRanking classificacaoJogador1 = base.get(a);
			final ExportedItemRanking classificacaoJogador2 = base.get(b);

			final Integer pontosJogador1 = Integer.valueOf(classificacaoJogador1.getPontos());
			final Integer pontosJogador2 = Integer.valueOf(classificacaoJogador2.getPontos());

			int compareTo = pontosJogador2.compareTo(pontosJogador1);
			if (compareTo == 0) {
				compareTo = classificacaoJogador2.getSaldo().compareTo(classificacaoJogador1.getSaldo());
				if (compareTo == 0) {
					compareTo = classificacaoJogador1.getCodigoPessoa()
							.compareTo(classificacaoJogador2.getCodigoPessoa());
					if (compareTo == 0) {
						compareTo = classificacaoJogador1.getNomePessoa()
								.compareTo(classificacaoJogador2.getNomePessoa());
					}
				}
			}
			return compareTo;
		}
	}