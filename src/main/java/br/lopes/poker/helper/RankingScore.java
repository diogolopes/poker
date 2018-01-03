package br.lopes.poker.helper;

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
	
}
