package br.lopes.poker.service.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import br.lopes.poker.domain.Colocacao;
import br.lopes.poker.domain.Pessoa;
import br.lopes.poker.domain.Ranking;
import br.lopes.poker.helper.PokerPaths;
import br.lopes.poker.service.ImportRanking;
import br.lopes.poker.service.PessoaService;
import br.lopes.poker.service.RankingService;

@Service
public class ImportRankingImpl implements ImportRanking {
	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ImportRankingImpl.class);

	@Autowired
	private PessoaService pessoaService;

	@Autowired
	private RankingService rankingService;

	@Override
	@Transactional(readOnly = false)
	public List<Ranking> importRankings() {
		final Collection<File> searchFromRankingFile = searchFromRankingFile();
		final Collection<Ranking> rankingSet = new HashSet<>();
		for (final File file : searchFromRankingFile) {
			final Ranking createRankingFromDirectory = createRankingFromDirectory(file);
			if (createRankingFromDirectory != null) {
				rankingSet.add(createRankingFromDirectory);
			}
		}
		return rankingService.save(rankingSet);
	}

	private Collection<File> searchFromRankingFile() {
		final Collection<File> files = new ArrayList<>();
		try {

			final File directory = new File(PokerPaths.POKER_RANKING_ENTRADA_FOLDER);
			// final Stream<Path> list = Files.list(directory.toPath());
			LOGGER.info("Buscando pastas dno diretório " + PokerPaths.POKER_RANKING_ENTRADA_FOLDER);
			Files.list(directory.toPath()).forEach(filePath -> {
				if (Files.isDirectory(filePath)) {
					files.add(filePath.toFile());
					LOGGER.info("Encontrou o path " + filePath);
				}
			});

		} catch (final IOException e) {
			LOGGER.error("Erro na importação da planilha.", e);
		}
		LOGGER.info("Encontrou os subdiretórios: " + files);
		return files;
	}

	private Ranking createRankingFromDirectory(final File file) {
		final File[] listFiles = file.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith("xlsx") || name.endsWith("xls");
			}
		});

		return createRankingFromFiles(file.getName(), listFiles);
	}

	private Ranking createRankingFromFiles(final String year, final File[] listFiles) {
		if (listFiles.length > 0) {
			return createRankingFromFile(year, listFiles[0]);
		}
		LOGGER.info("Diretório " + year + " não tem nenhum arquivo com extensão: xlsx ou xls");
		return null;
	}

	private Ranking createRankingFromFile(final String year, final File file) {
		try {
			LOGGER.info("Iniciando o processamento do ranking " + file);
			final Workbook wb = WorkbookFactory.create(file);
			final Sheet sheet = wb.getSheetAt(0);
			final Set<Colocacao> colocacoes = getColocacao(sheet);
			final Ranking ranking = getRanking(Integer.valueOf(year), colocacoes);
			wb.close();
			createBackupFile(year, file);
			return ranking;
		} catch (final EncryptedDocumentException | InvalidFormatException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private Ranking getRanking(final Integer year, final Set<Colocacao> colocacoes) {
		Ranking ranking = rankingService.findByAno(year);
		if (ranking == null) {
			ranking = new Ranking();
			ranking.setAno(year);
		}
		ranking.setDataAtualizacao(LocalDate.now());
		ranking.addAllColocacao(colocacoes);

		return ranking;
	}

	private Set<Colocacao> getColocacao(final Sheet sheet) {
		final Set<Colocacao> colocacoes = new HashSet<>();
		boolean header = true;
		for (final Row row : sheet) {
			if (header) {
				header = false;
				continue;
			}
			final String nome = row.getCell(1).getStringCellValue();
			if (StringUtils.isEmpty(nome)) {
				break;
			}

			final Colocacao colocacao = new Colocacao();
			final Double posicaoAtual = row.getCell(0).getNumericCellValue();
			final Pessoa pessoa = getPessoa(nome);
			final BigDecimal saldo = BigDecimal.valueOf(row.getCell(2).getNumericCellValue());
			final Double jogos = row.getCell(3).getNumericCellValue();
			final Double vitorias = row.getCell(4).getNumericCellValue();
			final Double derrotas = row.getCell(5).getNumericCellValue();
			final Double empates = row.getCell(6).getNumericCellValue();

			colocacao.setPosicaoAtual(posicaoAtual.intValue());
			colocacao.setPessoa(pessoa);
			colocacao.setSaldo(saldo);
			colocacao.setJogos(jogos.intValue());
			colocacao.setVitoria(vitorias.intValue());
			colocacao.setDerrota(derrotas.intValue());
			colocacao.setEmpate(empates.intValue());
			colocacoes.add(colocacao);
		}
		return colocacoes;
	}

	private Pessoa getPessoa(final String nome) {
		Pessoa pessoa = pessoaService.findByNome(nome);
		if (pessoa == null) {
			pessoa = new Pessoa();
			pessoa.setNome(nome);
			pessoa = pessoaService.save(pessoa);
		}
		return pessoa;
	}

	private void createBackupFile(final String year, final File file) {
		Path targetPath = new File(PokerPaths.POKER_RANKING_BACKUP_FOLDER + "/" + year + "/" + file.getName()).toPath();
		try {
			if (!Files.exists(targetPath)) {
				Files.createDirectories(targetPath);
			} else {
				final String fileName = FilenameUtils.getBaseName(file.getName())
						+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyy hhmmss"))
						+ "." + FilenameUtils.getExtension(file.getName());
				targetPath = new File(PokerPaths.POKER_RANKING_BACKUP_FOLDER + "/" + year + "/" + fileName).toPath();
			}
			LOGGER.info("Gerando o backup de " + file + " para " + targetPath);
			Files.move(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
		} catch (final IOException e) {
			LOGGER.error("Error", e);
		}

	}
}
