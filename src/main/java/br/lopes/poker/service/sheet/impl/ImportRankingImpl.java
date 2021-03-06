package br.lopes.poker.service.sheet.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import br.lopes.poker.domain.ItemRanking;
import br.lopes.poker.domain.Pessoa;
import br.lopes.poker.domain.Ranking;
import br.lopes.poker.domain.RankingType;
import br.lopes.poker.exception.PokerException;
import br.lopes.poker.helper.PokerPaths;
import br.lopes.poker.helper.PokerPlanilha;
import br.lopes.poker.helper.Sheets;
import br.lopes.poker.service.PessoaService;
import br.lopes.poker.service.RankingService;
import br.lopes.poker.service.sheet.ImportRanking;

@Service
public class ImportRankingImpl implements ImportRanking {
	private static final Logger LOGGER = LoggerFactory.getLogger(ImportRankingImpl.class);

	@Autowired
	private PessoaService pessoaService;

	@Autowired
	private RankingService rankingService;

	@Autowired
	private PokerPaths pokerPaths;

	private AtomicInteger atomicInteger;

	@Override
	@Transactional(readOnly = false)
	public List<Ranking> importRankings() throws PokerException {
		atomicInteger = new AtomicInteger(pessoaService.getMaxCodigo());

		final Collection<File> searchFromRankingFile = searchFromRankingFile();
		final Collection<Ranking> rankingSet = new HashSet<>();
		for (final File file : searchFromRankingFile) {
			final Ranking ranking = createRankingFromDirectory(file);
			if (ranking != null) {
				rankingSet.add(ranking);
			}
		}
		return rankingSet.isEmpty() ? Collections.emptyList() : rankingService.save(rankingSet);
	}

	private Collection<File> searchFromRankingFile() throws PokerException {
		final Collection<File> files = new ArrayList<>();
		try {
			final String rankingEntradaFolder = pokerPaths.getRankingEntradaFolder();
			final File directory = new File(rankingEntradaFolder);
			LOGGER.info("Buscando pastas no diretório {}", rankingEntradaFolder);
			Files.list(directory.toPath()).forEach(filePath -> {
				if (Files.isDirectory(filePath)) {
					files.add(filePath.toFile());
					LOGGER.info("Encontrou o path " + filePath);
				}
			});

		} catch (final IOException exception) {
			LOGGER.error("Erro na importação da planilha.", exception);
			throw new PokerException("Error in createPartidasFromFile", exception);
		}
		LOGGER.info("Encontrou os subdiretórios: " + files);
		return files;
	}

	private Ranking createRankingFromDirectory(final File file) throws PokerException {
		final File[] listFiles = file.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith("xlsx") || name.endsWith("xls");
			}
		});

		return createRankingFromFiles(file.getName(), listFiles);
	}

	private Ranking createRankingFromFiles(final String year, final File[] listFiles) throws PokerException {
		if (listFiles.length > 0) {
			return createRankingFromFile(year, listFiles[0]);
		}
		LOGGER.info("Diretório " + year + " não tem nenhum arquivo com extensão: xlsx ou xls");
		return null;
	}

	private Ranking createRankingFromFile(final String year, final File file) throws PokerException {
		try {
			LOGGER.info("Iniciando o processamento do ranking " + file);
			final Workbook wb = WorkbookFactory.create(file);
			final Sheet sheet = wb.getSheetAt(0);
			final Ranking ranking = getRanking(Integer.valueOf(year));
			getColocacao(ranking, sheet);

			wb.close();
			createBackupFile(year, file);
			return ranking;
		} catch (final EncryptedDocumentException | InvalidFormatException | IOException exception) {
			LOGGER.error("createRankingFromFile.", exception);
			throw new PokerException("Error in createPartidasFromFile", exception);
		}
	}

	private Ranking getRanking(final Integer year) {
		final Ranking existingRanking = rankingService.findByAno(year, RankingType.SALDO);
		if (existingRanking != null) {
			LOGGER.info("Ja existia um ranking de " + existingRanking.getAno() + " criado em "
					+ existingRanking.getDataAtualizacao() + ". Irei sobreescreve-lo");
		}

		final Ranking ranking = new Ranking();
		ranking.setAno(year);
		ranking.setDataAtualizacao(new Date());
		ranking.setRankingType(RankingType.SALDO);

		return ranking;
	}

	private void getColocacao(final Ranking ranking, final Sheet sheet) {
		final Set<ItemRanking> colocacoes = ranking.getItemRankings();

		int colunaMovimentacao = -1, colunaNome = -1, colunaCodigo = -1;
		int colunaPosicaoAtual = 0, colunaSaldo = -1, colunaJogos = -1;

		boolean header = true;
		for (final Row row : sheet) {
			if (header) {
				for (final Cell cell : row) {
					/*
					 * if (colunaMovimentacao != -1 && colunaNome != -1) { break; }
					 */
					if (cell.getStringCellValue().trim().equalsIgnoreCase(PokerPlanilha.COLUNA_CODIGO)) {
						colunaCodigo = cell.getColumnIndex();
					} else if (cell.getStringCellValue().trim().equalsIgnoreCase(PokerPlanilha.COLUNA_NOME)) {
						colunaNome = cell.getColumnIndex();
					} else if (cell.getStringCellValue().trim().equalsIgnoreCase(PokerPlanilha.COLUNA_MOVIMENTACAO)) {
						colunaMovimentacao = cell.getColumnIndex();
					} else if (cell.getStringCellValue().trim().equalsIgnoreCase(PokerPlanilha.COLUNA_PONTUACAO)) {
						colunaSaldo = cell.getColumnIndex();
					} else if (cell.getStringCellValue().trim().equalsIgnoreCase(PokerPlanilha.COLUNA_JOGOS)) {
						colunaJogos = cell.getColumnIndex();
					}
				}

				LOGGER.info("colunaPosicaoAtual[" + colunaPosicaoAtual + "], colunaMovimentacao[" + colunaMovimentacao
						+ "], colunaCodigo[" + colunaCodigo + "], colunaNome[" + colunaNome + "], colunaSaldo["
						+ colunaSaldo + "], colunaJogos[" + colunaJogos + "]");
				header = false;
				continue;
			}

			final Integer codigo = colunaCodigo > -1 ? Sheets.getIntegerValue(row.getCell(colunaCodigo)) : null;
			final String nome = row.getCell(colunaNome).getStringCellValue();
			if (StringUtils.isEmpty(nome)) {
				break;
			}

			final ItemRanking colocacao = new ItemRanking();
			final Double posicaoAtual = row.getCell(colunaPosicaoAtual).getNumericCellValue();
			final String movimentacao = (colunaMovimentacao > 0) ? row.getCell(colunaMovimentacao).getStringCellValue()
					: null;
			final Pessoa pessoa = getPessoa(codigo, nome);
			final BigDecimal saldo = BigDecimal.valueOf(row.getCell(colunaSaldo).getNumericCellValue());
			final Double jogos = row.getCell(colunaJogos).getNumericCellValue();

			colocacao.setPosicaoAtual(posicaoAtual.intValue());
			colocacao.setPosicaoAnterior(getPosicaoAnterior(movimentacao, posicaoAtual.intValue()));
			colocacao.setPessoa(pessoa);
			colocacao.setSaldo(saldo);
			colocacao.setJogos(jogos.intValue());
			colocacao.setRanking(ranking);
			colocacoes.add(colocacao);
		}
	}

	private int getPosicaoAnterior(final String movimentacao, final int posicaoAtual) {
		if (!StringUtils.isEmpty(movimentacao)) {
			final boolean positivo = movimentacao.substring(0, 1).equals("▲");
			final int posicoes = Integer.valueOf(movimentacao.substring(2, movimentacao.length()));
			return (positivo) ? (posicaoAtual + posicoes) : (posicaoAtual - posicoes);
		}
		return 0;
	}

	private Pessoa getPessoa(final Integer codigo, final String nome) {
		Pessoa pessoa = null;
		if (codigo != null && !Integer.valueOf(0).equals(codigo)) {
			pessoa = pessoaService.findByCodigo(codigo);
		}
		if (pessoa == null) {
			pessoa = new Pessoa();
			pessoa.setCodigo(atomicInteger.incrementAndGet());
			pessoa.setNome(nome);
			pessoa = pessoaService.save(pessoa);
		}
		return pessoa;
	}

	private void createBackupFile(final String year, final File file) throws PokerException {
		final String fileName = PokerPaths.POKER_RANKING_FILE
				+ LocalDateTime.now().format(DateTimeFormatter.ofPattern(" MM-dd-yyyy")) + "."
				+ FilenameUtils.getExtension(file.getName());
		final String pokerRankingBackupFolder = pokerPaths.getRankingBackupFolder();
		Path targetPath = new File(pokerRankingBackupFolder + "/" + year + "/" + fileName).toPath();
		try {
			if (!Files.exists(targetPath)) {
				Files.createDirectories(targetPath);
			} else {
				int i = 1;
				while (Files.exists(targetPath)) {
					targetPath = new File(pokerRankingBackupFolder + "/" + year + "/" + PokerPaths.POKER_RANKING_FILE
							+ LocalDateTime.now().format(DateTimeFormatter.ofPattern(" MM-dd-yyyy")) + " (" + i + ")."
							+ FilenameUtils.getExtension(file.getName())).toPath();
					i++;
				}
			}
			LOGGER.info("Gerando o backup de " + file + " para " + targetPath);
			Files.move(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
		} catch (final IOException exception) {
			LOGGER.error("Error", exception);
			throw new PokerException("Error in createPartidasFromFile", exception);
		}

	}
}
