package br.lopes.poker;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.lopes.poker.data.Classificacao;
import br.lopes.poker.domain.Partida;
import br.lopes.poker.domain.Pessoa;
import br.lopes.poker.domain.Ranking;
import br.lopes.poker.service.ClassificacaoService;
import br.lopes.poker.service.ClassificacaoService.RankingType;
import br.lopes.poker.service.ExportRanking;
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

	@Autowired
	private ExportRanking exportRanking;

	public static void main(final String[] args) {
		SpringApplication.run(PokerApp.class, args);
	}

	@Override
	public void run(final String... args) throws Exception {
		final List<Ranking> importRankings = importRanking.importRankings();
		final List<Partida> partidas = importPartida.importPartidas();

		final Ranking ranking = importRankings.stream() // Convert to steam
				.filter(r -> r.getAno().equals(LocalDate.now().getYear())) // we
																			// want
																			// "michael"
																			// only
				.findAny() // If 'findAny' then return found
				.orElse(rankingService.findByAno(LocalDate.now().getYear())); // If
																				// not
																				// found,
																				// return
																				// null

		final Map<Pessoa, Classificacao> rankingMap = classificacaoService.ranking(ranking, new HashSet<>(partidas),
				RankingType.SALDO);
		exportRanking.export(rankingMap, String.valueOf(LocalDate.now().getYear()));

	}

}
