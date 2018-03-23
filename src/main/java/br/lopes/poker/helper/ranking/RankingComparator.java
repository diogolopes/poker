package br.lopes.poker.helper.ranking;

import java.util.Comparator;

import br.lopes.poker.domain.ItemPartida;

public class RankingComparator implements Comparator<ItemPartida> {

	@Override
	public int compare(final ItemPartida ip1, final ItemPartida ip2) {

		int compareTo = Integer.compare(ip2.getPontos(), ip1.getPontos());
		if (compareTo == 0) {
			compareTo = ip2.getSaldo().compareTo(ip1.getSaldo());
			if (compareTo == 0) {
				compareTo = ip1.getPessoa().getCodigo().compareTo(ip2.getPessoa().getCodigo());
				if (compareTo == 0) {
					compareTo = ip1.getPessoa().getNome().compareTo(ip1.getPessoa().getNome());
				}
			}
		}
		return compareTo;
	}

}
