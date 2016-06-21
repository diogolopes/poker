package br.lopes.poker.service.impl;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

import br.lopes.poker.data.Classificacao;
import br.lopes.poker.domain.Partida;
import br.lopes.poker.domain.PartidaPessoa;
import br.lopes.poker.domain.Pessoa;
import br.lopes.poker.service.ClassificacaoService;

@Service
public class ClassificacaoServiceImpl implements ClassificacaoService {

    private RankingCriteriaFactory rankingCriteriaFactory = new RankingCriteriaFactory();

    @Override
    public Map<Pessoa, Classificacao> ranking(final Partida partida, final RankingType rankingType) {
        final Map<Pessoa, Classificacao> rankingBySaldo = getRankingMap(partida, null);
        final Map<Pessoa, Classificacao> treeMap = rankingCriteriaFactory.create(rankingBySaldo, rankingType);
        treeMap.putAll(rankingBySaldo);
        updatePosition(treeMap);
        return treeMap;
    }

    
    @Override
    public Map<Pessoa, Classificacao> ranking(final Set<Partida> partidas, RankingType rankingType) {
        Map<Pessoa, Classificacao> rankingMap = null;
        for (final Partida partida : partidas) {
            rankingMap = getRankingMap(partida, rankingMap);

            final Map<Pessoa, Classificacao> treeMap = rankingCriteriaFactory.create(rankingMap, rankingType);
            treeMap.putAll(rankingMap);
            updatePosition(treeMap);
        }

        final Map<Pessoa, Classificacao> treeMap = rankingCriteriaFactory.create(rankingMap, rankingType);
        treeMap.putAll(rankingMap);
        return treeMap;
    }

    private void updatePosition(final Map<Pessoa, Classificacao> treeMap) {
        final Iterator<Entry<Pessoa, Classificacao>> iterator = treeMap.entrySet().iterator();

        int posicao = 1;
        while (iterator.hasNext()) {
            final Entry<Pessoa, Classificacao> entry = iterator.next();
            final Classificacao value = entry.getValue();
            value.setPosicao(posicao++);
            System.out.println(value);
        }
        System.out.println("");

    }

    private Map<Pessoa, Classificacao> getRankingMap(final Partida partida, final Map<Pessoa, Classificacao> rankingMap) {
        final Map<Pessoa, Classificacao> rankings;
        if (rankingMap != null) {
            rankings = rankingMap;
        } else {
            rankings = new HashMap<Pessoa, Classificacao>();
        }
        return rankingByPartida(partida, rankings);
    }

    private Map<Pessoa, Classificacao> rankingByPartida(final Partida partida, final Map<Pessoa, Classificacao> rankingMap) {
        final Set<PartidaPessoa> partidaPessoas = partida.getPartidaPessoas();

        for (final PartidaPessoa partidaPessoa : partidaPessoas) {
            final Classificacao rankingByPessoa = getRankingByPessoa(rankingMap, partidaPessoa);
            rankingByPessoa.update(partidaPessoa);
        }
        return rankingMap;
    }

    private Classificacao getRankingByPessoa(final Map<Pessoa, Classificacao> rankingMap, final PartidaPessoa partidaPessoa) {
        Classificacao ranking = rankingMap.get(partidaPessoa.getPessoa());
        if (ranking == null) {
            ranking = new Classificacao(partidaPessoa);
            rankingMap.put(partidaPessoa.getPessoa(), ranking);
        }
        return ranking;
    }

    public class RankingCriteriaFactory {

        public Map<Pessoa, Classificacao> create(final Map<Pessoa, Classificacao> rankingBySaldo, final RankingType rankingType) {
            switch (rankingType) {
            case SALDO:
                return new TreeMap<Pessoa, Classificacao>(new RankingBySaldoComparator(rankingBySaldo));
            default:
                return new TreeMap<Pessoa, Classificacao>(new RankingByAproveitamentoComparator(rankingBySaldo));
            }
        }
    }

    public class RankingBySaldoComparator implements Comparator<Pessoa> {
        private final Map<Pessoa, Classificacao> base;

        public RankingBySaldoComparator(final Map<Pessoa, Classificacao> base) {
            this.base = base;
        }

        public int compare(final Pessoa a, final Pessoa b) {
            final Classificacao ranking = base.get(a);
            final Classificacao ranking2 = base.get(b);

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
        private final Map<Pessoa, Classificacao> base;

        public RankingByAproveitamentoComparator(final Map<Pessoa, Classificacao> base) {
            this.base = base;
        }

        public int compare(final Pessoa a, final Pessoa b) {
            final Classificacao ranking = base.get(a);
            final Classificacao ranking2 = base.get(b);

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
