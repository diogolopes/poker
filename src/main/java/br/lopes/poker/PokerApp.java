package br.lopes.poker;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import br.lopes.poker.domain.Partida;
import br.lopes.poker.domain.Ranking;
import br.lopes.poker.domain.RankingType;
import br.lopes.poker.helper.Dates;
import br.lopes.poker.service.RankingService;
import br.lopes.poker.service.sheet.ClassificacaoService;
import br.lopes.poker.service.sheet.ImportPartida;
import br.lopes.poker.service.sheet.ImportRanking;

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

	@Bean
	CommandLineRunner init() {
		return args -> {
			try {
				final List<Ranking> importRankings = importRanking.importRankings();
				final List<Partida> partidas = importPartida.importPartidas();
				final Optional<Ranking> optionalMaxRaking = importRankings.stream()
						.max((r1, r2) -> r1.getDataAtualizacao().compareTo(r2.getDataAtualizacao()));

				if (!partidas.isEmpty()) {
					final Optional<Partida> partidaMax = partidas.stream()
							.max((r1, r2) -> r1.getData().compareTo(r2.getData()));

					if (optionalMaxRaking.isPresent()) {
						final Ranking ranking = optionalMaxRaking.get();
						classificacaoService.generateRankingFileByPartidasAndType(ranking, new HashSet<>(partidas));
					} else {
						final Date dataUltimaPartida = partidaMax.get().getData();
						final int ano = Dates.dateToLocalDate(dataUltimaPartida).getYear();
						Ranking lastRankingBySaldo = rankingService.findByAno(ano, RankingType.PONTUACAO);
						if (lastRankingBySaldo == null) {
							lastRankingBySaldo = new Ranking();

							final Ranking newYearRanking = new Ranking();
							newYearRanking.setAno(ano);
							newYearRanking.setDataAtualizacao(dataUltimaPartida);
							newYearRanking.setRankingType(RankingType.PONTUACAO);

							classificacaoService.generateRankingFileByPartidasAndType(
									rankingService.save(newYearRanking), new HashSet<>(partidas),
									RankingType.PONTUACAO);
						} else {
							final Ranking rankingBySaldo = rankingService.clone(lastRankingBySaldo);
							rankingBySaldo.setDataAtualizacao(dataUltimaPartida);
							classificacaoService.generateRankingFileByPartidasAndType(rankingBySaldo,
									new HashSet<>(partidas), RankingType.PONTUACAO);
						}
					}
				} else {
					final Ranking rankingBySaldo = rankingService.findByAno(LocalDate.now().getYear(),
							RankingType.SALDO);
					classificacaoService.generateRankingFileByRanking(rankingBySaldo);
				}
			} catch (final Exception exception) {
				LOGGER.error("Error", exception);
			}
		};
	}

}
