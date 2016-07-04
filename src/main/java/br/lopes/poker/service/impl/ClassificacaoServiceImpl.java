package br.lopes.poker.service.impl;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.lopes.poker.data.Classificacao;
import br.lopes.poker.data.ClassificacaoImpl;
import br.lopes.poker.domain.Colocacao;
import br.lopes.poker.domain.Partida;
import br.lopes.poker.domain.PartidaPessoa;
import br.lopes.poker.domain.Pessoa;
import br.lopes.poker.domain.Ranking;
import br.lopes.poker.helper.Dates;
import br.lopes.poker.helper.Validator;
import br.lopes.poker.service.ClassificacaoService;
import br.lopes.poker.service.ExportRanking;
import br.lopes.poker.service.RankingService;

@Service
public class ClassificacaoServiceImpl implements ClassificacaoService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassificacaoServiceImpl.class);

    private RankingCriteriaFactory rankingCriteriaFactory = new RankingCriteriaFactory();

    @Autowired
    private RankingService rankingService;

    @Autowired
    private ExportRanking exportRanking;

    @Override
    public void generateRankingFileByPartidasAndType(final Ranking ranking, final Set<Partida> partidas, final RankingType rankingType) throws Exception {
        if (ranking == null || partidas.isEmpty()) {
            LOGGER.info("ranking encontrado[" + ranking + "], partidas.size[" + partidas.size() + "]");
            exportRanking.export(ranking, rankingType);
            return;
        }
        LOGGER.info("Iniciando reclassificacao do ranking de " + ranking.getAno() + " ultima atualização em " + ranking.getDataAtualizacao() + " para " + partidas.size() + " partidas por "
                + rankingType.getNome());

        Map<Pessoa, Classificacao> rankingMap = transformFromRanking(ranking);

        // final Iterator<Entry<Pessoa, Classificacao>> iterator =
        // rankingMap.entrySet().iterator();
        //
        // while (iterator.hasNext()) {
        // final Entry<Pessoa, Classificacao> entry = iterator.next();
        // final Classificacao value = entry.getValue();
        // System.out.println(value);
        // }
        // System.out.println("");
        LOGGER.info("Processando " + partidas.size() + " novas partidas para o ranking.");

        for (final Partida partida : partidas) {
            rankingMap = getRankingMap(partida, rankingMap);

            /*
             * final Map<Pessoa, Classificacao> treeMap =
             * rankingCriteriaFactory.create(rankingMap, rankingType);
             * treeMap.putAll(rankingMap); updatePosition(treeMap);
             */
        }

        final Map<Pessoa, Classificacao> treeMap = rankingCriteriaFactory.create(rankingMap, rankingType);
        treeMap.putAll(rankingMap);
        updatePosition(treeMap);

        final Ranking save = rankingService.save(transformToRanking(ranking, treeMap));
        exportRanking.export(save, rankingType);
    }

    private Ranking transformToRanking(final Ranking ranking, final Map<Pessoa, Classificacao> treeMap) {
        final Iterator<Entry<Pessoa, Classificacao>> classificacaoIterator = treeMap.entrySet().iterator();

        final Set<Colocacao> novasPessoas = new HashSet<>();

        while (classificacaoIterator.hasNext()) {
            final Entry<Pessoa, Classificacao> entry = classificacaoIterator.next();
            final Pessoa pessoa = entry.getKey();
            final Classificacao classificacao = entry.getValue();

            Colocacao colocacao = pessoa != null ? ranking.getColocacoes().stream().filter(c -> c.getPessoa().getNome().equals(pessoa.getNome())).findFirst().orElse(null) : null;

            if (colocacao == null) {
                colocacao = new Colocacao();
                colocacao.setPessoa(pessoa);
                novasPessoas.add(colocacao);
            }
            colocacao.setSaldo(classificacao.getSaldo());
            colocacao.setJogos(classificacao.getJogos());
            colocacao.setVitoria(classificacao.getVitoria());
            colocacao.setEmpate(classificacao.getEmpate());
            colocacao.setDerrota(classificacao.getDerrota());
            colocacao.setPosicaoAtual(classificacao.getPosicaoAtual());
            colocacao.setPosicaoAnterior(classificacao.getPosicaoAnterior());
        }
        ranking.addAllColocacao(novasPessoas);
        return ranking;
    }

    private Map<Pessoa, Classificacao> transformFromRanking(final Ranking ranking) {
        Map<Pessoa, Classificacao> classificacaoMap = new HashMap<Pessoa, Classificacao>();
        for (final Colocacao colocacao : ranking.getColocacoes()) {
            final ClassificacaoImpl classificacao = new ClassificacaoImpl(colocacao, ranking.getAno());
            classificacaoMap.put(colocacao.getPessoa(), classificacao);
        }
        return classificacaoMap;
    }

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
        Classificacao classificacao = rankingMap.get(partidaPessoa.getPessoa());
        if (classificacao == null) {
            LOGGER.error("Não encontrou o nome " + partidaPessoa.getPessoa().getNome() + " no ranking atual. Ano " + partidaPessoa.getPartida().getData());

            Validator.validar("Não encontrou o nome " + partidaPessoa.getPessoa().getNome() + " no ranking atual que veio da partida do dia " + partidaPessoa.getPartida().getData(),
                    String.valueOf(Dates.dateToLocalDate(partidaPessoa.getPartida().getData()).getYear()));
            classificacao = new ClassificacaoImpl(partidaPessoa);
            rankingMap.put(partidaPessoa.getPessoa(), classificacao);
        }
        return classificacao;
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
                    // Quem tiver melhor posicao no ranking atual
                    if (compareTo == 0) {
                        compareTo = Integer.valueOf(ranking.getPosicaoAtual()).compareTo(Integer.valueOf(ranking2.getPosicaoAtual()));
                    }
                    // Quem tiver melhor posicao no ranking anterior
                    if (compareTo == 0) {
                        compareTo = Integer.valueOf(ranking.getPosicaoAnterior()).compareTo(Integer.valueOf(ranking2.getPosicaoAnterior()));
                    }
                    // Quem tiver melhor posicao no ranking anterior
                    if (compareTo == 0) {
                        return a.getNome().compareTo(b.getNome());
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
            }
            // Quem tiver mais vitórias
            if (compareTo == 0) {
                final Integer vitoria1 = Integer.valueOf(ranking.getVitoria());
                final Integer vitoria2 = Integer.valueOf(ranking2.getVitoria());
                compareTo = vitoria2.compareTo(vitoria1);
            }
            // Quem tiver menos derrotas
            if (compareTo == 0) {
                final Integer derrota1 = Integer.valueOf(ranking.getDerrota());
                final Integer derrota2 = Integer.valueOf(ranking2.getDerrota());
                compareTo = derrota1.compareTo(derrota2);
            }
            // Quem tiver melhor posicao no ranking atual
            if (compareTo == 0) {
                compareTo = Integer.valueOf(ranking.getPosicaoAtual()).compareTo(Integer.valueOf(ranking2.getPosicaoAtual()));
            }
            // Quem tiver melhor posicao no ranking anterior
            if (compareTo == 0) {
                compareTo = Integer.valueOf(ranking.getPosicaoAnterior()).compareTo(Integer.valueOf(ranking2.getPosicaoAnterior()));
            }
            return compareTo;
        }
    }

    @Override
    public void generateRankingFileByType(final Ranking ranking, final RankingType rankingType) throws Exception {
        if (ranking == null) {
            LOGGER.info("Nenhum ranking encontrado....");
            return;
        }
        LOGGER.info("Iniciando reclassificacao do ranking de " + ranking.getAno() + " ultima atualização em " + ranking.getDataAtualizacao() + " por " + rankingType.getNome());

        Map<Pessoa, Classificacao> rankingMap = transformFromRanking(ranking);

        final Map<Pessoa, Classificacao> treeMap = rankingCriteriaFactory.create(rankingMap, rankingType);
        treeMap.putAll(rankingMap);
        updatePosition(treeMap);
        exportRanking.export(treeMap, ranking.getAno(), rankingType);
    }

}
