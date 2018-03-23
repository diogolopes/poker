package br.lopes.poker.helper.ranking;

import java.util.HashMap;
import java.util.Map;

public class RankingScore {

	private static final Map<Integer,Integer> scoreMap = new HashMap<>();
	
	static {
		scoreMap.put(1, 10);
		scoreMap.put(2, 8);
		scoreMap.put(3, 6);
		scoreMap.put(4, 4);
		scoreMap.put(5, 3);
		scoreMap.put(6, 2);
		scoreMap.put(7, 1);
	}
	
	public static boolean isRight(int posicao, int pontuacao) {
		final Integer score = scoreMap.get(posicao);
		if (score != null) {
			return score.equals(pontuacao);	
		} else {
			return 0 == pontuacao;
		}
	}
	
	public static int pontuacao(int posicao) {
		final Integer score = scoreMap.get(posicao);
		if (score != null) {
			return score;
		}
		return 0;
	}

}
