package br.lopes.poker.controller;

import static br.lopes.poker.helper.PokerEnvironment.JSON_CHARSET;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.lopes.poker.domain.Ranking;
import br.lopes.poker.domain.RankingType;
import br.lopes.poker.service.RankingService;
import br.lopes.poker.service.sheet.ClassificacaoService;
import br.lopes.poker.service.sheet.ImportRanking;

@RestController
@RequestMapping("/ranking")
public class RankingController extends BaseController {

	private static final String PARAM_ANO = "ano";
	private static final String URL_IMPORT = "/import";
	private static final String URL_EXPORT = "/export";

	@Autowired
	private ClassificacaoService classificacaoService;

	@Autowired
	private RankingService rankingService;

	@Autowired
	private ImportRanking importRanking;

	@RequestMapping(method = GET, produces = JSON_CHARSET)
	public ResponseEntity<Ranking> getRanking() {
		return createResponse(rankingService.findByAno(LocalDate.now().getYear(), RankingType.SALDO));
	}

	@RequestMapping(method = GET, produces = JSON_CHARSET, params = PARAM_ANO)
	public ResponseEntity<Ranking> getRankingByYear(@RequestParam(PARAM_ANO) @NotBlank final Integer ano) {
		return createResponse(rankingService.findByAno(ano, RankingType.SALDO));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> remove(@PathVariable("id") @NotBlank final Integer id) {

		if (null == rankingService.delete(id)) {
			return createResponse("No ranking found with id " + id, HttpStatus.NOT_FOUND);
		}

		return createResponse("Deleted ranking " + id, HttpStatus.OK);
	}

	@RequestMapping(value = URL_IMPORT, method = GET, produces = JSON_CHARSET)
	public ResponseEntity<?> importRanking() {
		try {
			final List<Ranking> importRankings = importRanking.importRankings();
			return createResponse(importRankings);

		} catch (final Exception exception) {
			return new ResponseEntity<>("Error in import", BAD_REQUEST);
		}
	}

	@RequestMapping(value = URL_EXPORT, method = GET, produces = JSON_CHARSET)
	public ResponseEntity<?> exportRanking() {
		try {
			final Ranking rankingBySaldo = rankingService.findByAno(LocalDate.now().getYear(), RankingType.SALDO);
			classificacaoService.generateRankingFileByRanking(rankingBySaldo);
			return new ResponseEntity<>("Export success", HttpStatus.OK);
		} catch (final Exception e) {
			return new ResponseEntity<>("Error in export", BAD_REQUEST);
		}

	}

}
