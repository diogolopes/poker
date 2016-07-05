package br.lopes.poker;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.lopes.poker.domain.Partida;
import br.lopes.poker.domain.Ranking;
import br.lopes.poker.service.ClassificacaoService;
import br.lopes.poker.service.ClassificacaoService.RankingType;
import br.lopes.poker.service.ImportPartida;
import br.lopes.poker.service.ImportRanking;
import br.lopes.poker.service.RankingService;

@SpringBootApplication
public class PokerApp implements CommandLineRunner {
    @Autowired
    private RankingService rankingService;

    @Autowired
    private ImportRanking importRanking;

    @Autowired
    private ImportPartida importPartida;

    @Autowired
    private ClassificacaoService classificacaoService;

    public static void main(final String[] args) {
        SpringApplication.run(PokerApp.class, args);
    }

    @Override
    public void run(final String... args) throws Exception {
        final List<Ranking> importRankings = importRanking.importRankings();
        final List<Partida> partidas = importPartida.importPartidas();

        final Optional<Ranking> optionalMaxRaking = importRankings.stream().max((r1, r2) -> r1.getDataAtualizacao().compareTo(r2.getDataAtualizacao()));

        if (!partidas.isEmpty()) {

            if (optionalMaxRaking.isPresent()) {
                final Ranking ranking = optionalMaxRaking.get();
                classificacaoService.generateRankingFileByPartidasAndType(ranking, new HashSet<>(partidas));
                classificacaoService.generateRankingFileByPartidasAndType(ranking, new HashSet<>(partidas));
            } else {
                final Ranking lastRankingBySaldo = rankingService.findByAno(LocalDate.now().getYear(), RankingType.SALDO);
                final Ranking lastRankingByAproveitamento = rankingService.findByAno(LocalDate.now().getYear(), RankingType.APROVEITAMENTO);

                final Ranking rankingBySaldo = rankingService.clone(lastRankingBySaldo);
                final Ranking rankingByAproveitamento = rankingService.clone(lastRankingByAproveitamento);
                classificacaoService.generateRankingFileByPartidasAndType(rankingBySaldo, new HashSet<>(partidas), RankingType.SALDO);
                classificacaoService.generateRankingFileByPartidasAndType(rankingByAproveitamento, new HashSet<>(partidas), RankingType.APROVEITAMENTO);
            }
        } else {
            final Ranking rankingBySaldo = rankingService.findByAno(LocalDate.now().getYear(), RankingType.SALDO);
            final Ranking rankingByAproveitamento = rankingService.findByAno(LocalDate.now().getYear(), RankingType.APROVEITAMENTO);
            classificacaoService.generateRankingFileByType(rankingBySaldo);
            classificacaoService.generateRankingFileByType(rankingByAproveitamento);
        }

    }

}
