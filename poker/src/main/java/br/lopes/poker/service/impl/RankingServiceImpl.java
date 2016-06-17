package br.lopes.poker.service.impl;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

import br.lopes.poker.data.Ranking;
import br.lopes.poker.domain.Partida;
import br.lopes.poker.domain.PartidaPessoa;
import br.lopes.poker.domain.Pessoa;
import br.lopes.poker.service.RankingService;

@Service
public class RankingServiceImpl implements RankingService {

    private RankingCriteriaFactory rankingCriteriaFactory = new RankingCriteriaFactory();

    @Override
    public Map<Pessoa, Ranking> ranking(final Partida partida, final RankingType rankingType) {
        final Map<Pessoa, Ranking> rankingBySaldo = getRankingMap(partida, null);
        final Map<Pessoa, Ranking> treeMap = rankingCriteriaFactory.create(rankingBySaldo, rankingType);
        treeMap.putAll(rankingBySaldo);
        updatePosition(treeMap);
        return treeMap;
    }

    
    @Override
    public Map<Pessoa, Ranking> ranking(final Set<Partida> partidas, RankingType rankingType) {
        Map<Pessoa, Ranking> rankingMap = null;
        for (final Partida partida : partidas) {
            rankingMap = getRankingMap(partida, rankingMap);

            final Map<Pessoa, Ranking> treeMap = rankingCriteriaFactory.create(rankingMap, rankingType);
            treeMap.putAll(rankingMap);
            updatePosition(treeMap);
        }

        final Map<Pessoa, Ranking> treeMap = rankingCriteriaFactory.create(rankingMap, rankingType);
        treeMap.putAll(rankingMap);
        return treeMap;
    }

    private void updatePosition(final Map<Pessoa, Ranking> treeMap) {
        final Iterator<Entry<Pessoa, Ranking>> iterator = treeMap.entrySet().iterator();

        int posicao = 1;
        while (iterator.hasNext()) {
            final Entry<Pessoa, Ranking> entry = iterator.next();
            final Ranking value = entry.getValue();
            value.setPosicao(posicao++);
            System.out.println(value);
        }
        System.out.println("");

    }

    private Map<Pessoa, Ranking> getRankingMap(final Partida partida, final Map<Pessoa, Ranking> rankingMap) {
        final Map<Pessoa, Ranking> rankings;
        if (rankingMap != null) {
            rankings = rankingMap;
        } else {
            rankings = new HashMap<Pessoa, Ranking>();
        }
        return rankingByPartida(partida, rankings);
    }

    private Map<Pessoa, Ranking> rankingByPartida(final Partida partida, final Map<Pessoa, Ranking> rankingMap) {
        final Set<PartidaPessoa> partidaPessoas = partida.getPartidaPessoas();

        for (final PartidaPessoa partidaPessoa : partidaPessoas) {
            final Ranking rankingByPessoa = getRankingByPessoa(rankingMap, partidaPessoa);
            rankingByPessoa.update(partidaPessoa);
        }
        return rankingMap;
    }

    private Ranking getRankingByPessoa(final Map<Pessoa, Ranking> rankingMap, final PartidaPessoa partidaPessoa) {
        Ranking ranking = rankingMap.get(partidaPessoa.getPessoa());
        if (ranking == null) {
            ranking = new Ranking(partidaPessoa);
            rankingMap.put(partidaPessoa.getPessoa(), ranking);
        }
        return ranking;
    }

    public class RankingCriteriaFactory {

        public Map<Pessoa, Ranking> create(final Map<Pessoa, Ranking> rankingBySaldo, final RankingType rankingType) {
            switch (rankingType) {
            case SALDO:
                return new TreeMap<Pessoa, Ranking>(new RankingBySaldoComparator(rankingBySaldo));
            default:
                return new TreeMap<Pessoa, Ranking>(new RankingByAproveitamentoComparator(rankingBySaldo));
            }
        }
    }

    public class RankingBySaldoComparator implements Comparator<Pessoa> {
        private final Map<Pessoa, Ranking> base;

        public RankingBySaldoComparator(final Map<Pessoa, Ranking> base) {
            this.base = base;
        }

        public int compare(final Pessoa a, final Pessoa b) {
            final Ranking ranking = base.get(a);
            final Ranking ranking2 = base.get(b);

            int compareTo = ranking2.getSaldo().compareTo(ranking.getSaldo());
            // Quem tiver menos jogos
            if (compareTo == 0) {
                final Integer jogos1 = Integer.valueOf(ranking.getJogos());
                final Integer jogos2 = Integer.valueOf(ranking2.getJogos());
                compareTo = jogos1.compareTo(jogos2);

                // Quem tiver mais vitórias
                if (compareTo == 0) {
                    final Integer vitoria1 = Integer.valueOf(ranking.getVitoria());
                    final Integer vitoria2 = Integer.valueOf(ranking2.getVitoria());
                    compareTo = vitoria2.compareTo(vitoria1);
                    // Quem tiver menos derrotas
                    if (compareTo == 0) {
                        final Integer derrota1 = Integer.valueOf(ranking.getDerrota());
                        final Integer derrota2 = Integer.valueOf(ranking2.getDerrota());
                        compareTo = derrota1.compareTo(derrota2);
                    }
                }
            }
            return compareTo;
        }
    }

    public class RankingByAproveitamentoComparator implements Comparator<Pessoa> {
        private final Map<Pessoa, Ranking> base;

        public RankingByAproveitamentoComparator(final Map<Pessoa, Ranking> base) {
            this.base = base;
        }

        public int compare(final Pessoa a, final Pessoa b) {
            final Ranking ranking = base.get(a);
            final Ranking ranking2 = base.get(b);

            int compareTo = ranking2.getAproveitamento().compareTo(ranking.getAproveitamento());
            // Quem tiver menos jogos
            if (compareTo == 0) {
                final Integer jogos1 = Integer.valueOf(ranking.getJogos());
                final Integer jogos2 = Integer.valueOf(ranking2.getJogos());
                compareTo = jogos1.compareTo(jogos2);

                // Quem tiver mais vitórias
                if (compareTo == 0) {
                    final Integer vitoria1 = Integer.valueOf(ranking.getVitoria());
                    final Integer vitoria2 = Integer.valueOf(ranking2.getVitoria());
                    compareTo = vitoria2.compareTo(vitoria1);
                    // Quem tiver menos derrotas
                    if (compareTo == 0) {
                        final Integer derrota1 = Integer.valueOf(ranking.getDerrota());
                        final Integer derrota2 = Integer.valueOf(ranking2.getDerrota());
                        compareTo = derrota1.compareTo(derrota2);
                    }
                }
            }
            return compareTo;
        }
    }

}
