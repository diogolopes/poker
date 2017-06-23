package br.lopes.poker;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.lopes.poker.service.ClassificacaoService;
import br.lopes.poker.service.ImportPartida;
import br.lopes.poker.service.ImportRanking;
import br.lopes.poker.service.RankingService;

@SpringBootApplication
public class PokerApp {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(PokerApp.class);

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

    // @Bean
    // CommandLineRunner init() {
    // return args -> {
    // try {
    // final List<Ranking> importRankings = importRanking.importRankings();
    // final List<Partida> partidas = importPartida.importPartidas();
    // final Optional<Ranking> optionalMaxRaking = importRankings.stream()
    // .max((r1, r2) -> r1.getDataAtualizacao().compareTo(r2.getDataAtualizacao()));
    //
    // if (!partidas.isEmpty()) {
    // final Optional<Partida> partidaMax = partidas.stream()
    // .max((r1, r2) -> r1.getData().compareTo(r2.getData()));
    //
    // if (optionalMaxRaking.isPresent()) {
    // final Ranking ranking = optionalMaxRaking.get();
    // classificacaoService.generateRankingFileByPartidasAndType(ranking, new HashSet<>(partidas));
    // } else {
    // final int ano = Dates.dateToLocalDate(partidaMax.get().getData()).getYear();
    // Ranking lastRankingBySaldo = rankingService.findByAno(ano, RankingType.SALDO);
    // if (lastRankingBySaldo == null) {
    // lastRankingBySaldo = new Ranking();
    //
    // final Ranking newYearRanking = new Ranking();
    // newYearRanking.setAno(ano);
    // newYearRanking.setDataAtualizacao(new Date());
    // newYearRanking.setRankingType(RankingType.SALDO);
    //
    // classificacaoService.generateRankingFileByPartidasAndType(
    // rankingService.save(newYearRanking), new HashSet<>(partidas), RankingType.SALDO);
    // } else {
    // final Ranking rankingBySaldo = rankingService.clone(lastRankingBySaldo);
    // classificacaoService.generateRankingFileByPartidasAndType(rankingBySaldo,
    // new HashSet<>(partidas), RankingType.SALDO);
    // }
    // }
    // } else {
    // final Ranking rankingBySaldo = rankingService.findByAno(LocalDate.now().getYear(),
    // RankingType.SALDO);
    // classificacaoService.generateRankingFileByType(rankingBySaldo);
    // }
    // } catch (final Exception exception) {
    // LOGGER.error("Error", exception);
    // }
    // };
    // }

}
