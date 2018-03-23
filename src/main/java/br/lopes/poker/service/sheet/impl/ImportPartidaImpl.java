package br.lopes.poker.service.sheet.impl;

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
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.transaction.Transactional;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import br.lopes.poker.data.AcumuladorValor;
import br.lopes.poker.domain.Partida;
import br.lopes.poker.domain.Pessoa;
import br.lopes.poker.exception.PokerException;
import br.lopes.poker.helper.Dates;
import br.lopes.poker.helper.PokerPaths;
import br.lopes.poker.helper.Sheets;
import br.lopes.poker.helper.Validator;
import br.lopes.poker.service.PartidaService;
import br.lopes.poker.service.PessoaService;
import br.lopes.poker.service.sheet.ImportPartida;

@Service
public class ImportPartidaImpl implements ImportPartida {
	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ImportPartidaImpl.class);

	@Autowired
	private PessoaService pessoaService;

	@Autowired
	private PartidaService partidaService;

	@Autowired
	private PokerPaths pokerPaths;

	@Autowired
	private Validator validator;

	private Set<PlayerCreated> playerCreatedSet = new HashSet<>();

	private AtomicInteger atomicInteger;

	@Override
	@Transactional
	public List<Partida> importPartidas() throws PokerException {
		atomicInteger = new AtomicInteger(pessoaService.getMaxCodigo());
		final Collection<File> partidaFiles = searchFromPartidaDirectory();
		final Set<Partida> partidasSet = new HashSet<>();
		for (final File file : partidaFiles) {
			final Set<Partida> partidasFromDirectory = createPartidasFromDirectory(file);
			if (partidasFromDirectory == null || partidasFromDirectory.isEmpty()) {
				LOGGER.info("Não foi encontrada nenhuma partida para importar");
			} else {
				partidasSet.addAll(partidasFromDirectory);
			}
		}
		return (partidasSet.isEmpty()) ? Collections.emptyList() : partidaService.save(partidasSet);
	}

	private Collection<File> searchFromPartidaDirectory() throws PokerException {
		final Collection<File> files = new ArrayList<>();
		try {

			final File directory = new File(pokerPaths.getPartidaFolder());
			Files.list(directory.toPath()).forEach(filePath -> {
				if (Files.isDirectory(filePath)) {
					files.add(filePath.toFile());
					LOGGER.info("Encontrou o path da partida {}", filePath);
				}
			});

		} catch (final IOException exception) {
			LOGGER.error("Erro IOException", exception);
			throw new PokerException("Error in createPartidasFromFile", exception);
		}
		return files;
	}

	private Set<Partida> createPartidasFromDirectory(final File file) throws PokerException {
		final File[] listFiles = file.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(final File dir, final String name) {
				return name.endsWith("xlsx") || name.endsWith("xls");
			}
		});

		return createPartidasFromFiles(file.getName(), listFiles);
	}

	private Set<Partida> createPartidasFromFiles(final String year, final File[] listFiles) throws PokerException {
		validator.deletarArquivo(year);
		if (listFiles.length > 0) {
			final Set<Partida> partidas = new HashSet<>();
			for (int i = 0; i < listFiles.length; i++) {
				final Set<Partida> importPartidas = createPartidasFromFile(year, listFiles[i]);
				partidas.addAll(importPartidas);
			}
			return partidas;
		}
		LOGGER.info("Diretório " + year + " não tem nenhum arquivo com extensão: xlsx ou xls");
		return null;
	}

	private Set<Partida> createPartidasFromFile(final String year, final File file) throws PokerException {
		try {
			LOGGER.info("Iniciando o processamento da partida " + file);
			final Workbook wb = WorkbookFactory.create(file);
			final Sheet sheet = wb.getSheetAt(0);
			final Set<Partida> partidas = getPartidas(year, sheet);

			final Optional<Partida> firstPartida = partidas.stream()
					.min((p1, p2) -> p1.getData().compareTo(p2.getData()));
			wb.close();

			if (partidas.isEmpty() && !playerCreatedSet.isEmpty()) {
				final String filePath = FilenameUtils.getFullPath(file.getAbsolutePath());
				final String fileName = PokerPaths.POKER_PARTIDA_FILE + "."
						+ FilenameUtils.getExtension(file.getName());
				final Path targetPath = new File(filePath + "/" + fileName).toPath();
				Files.move(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
				return java.util.Collections.emptySet();
			}

			createBackupFile(year, file, firstPartida);
			return partidas;
		} catch (final EncryptedDocumentException | InvalidFormatException | IOException exception) {
			LOGGER.error("createPartidasFromFile", exception);
			throw new PokerException("Error in createPartidasFromFile", exception);
		}
	}

	private Set<Partida> getPartidas(final String year, final Sheet sheet) throws PokerException {
		final Set<Partida> partidas = new HashSet<Partida>();
		final Map<Integer, Partida> partidaMap = new HashMap<>();
		final Map<Pessoa, AcumuladorValor> acumuladorValor = new HashMap<>();

		int codigoIndex = -1, participanteIndex = -1, totalSaldoIndex = -1, totalPontosIndex = -1;

		final String[] codigoString = { "CÓDIGO", "CODIGO" };
		final String[] participanteString = { "PARTICIPANTE" };
		final String[] totalString = { "TOTAL" };
		final String[] totalAcumuladoString = { "TOTAL-C", "TOTALC", "TOTAL C", "TOTAL.C", "TOTAL-P" };

		boolean header = true;
		for (final Row row : sheet) {
			if (header) {
				for (final Cell cell : row) {

					if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
						final String valorCelulaDataPartida = cell.getStringCellValue().trim().toUpperCase();

						if (containsStringInArray(valorCelulaDataPartida, codigoString)) {
							codigoIndex = cell.getColumnIndex();
							continue;
						}

						if (containsStringInArray(valorCelulaDataPartida, participanteString)) {
							participanteIndex = cell.getColumnIndex();
							continue;
						}

						if (containsStringInArray(valorCelulaDataPartida, totalString)) {
							totalSaldoIndex = cell.getColumnIndex();
							continue;
						}

						if (containsStringInArray(valorCelulaDataPartida, totalAcumuladoString)) {
							totalPontosIndex = cell.getColumnIndex();
							continue;
						}

						try {
							if (org.apache.commons.lang3.StringUtils.isAlphaSpace(valorCelulaDataPartida)) {
								continue;
							}
							final int tamanhoCampoData = valorCelulaDataPartida.trim().length();
							final LocalDate partidaDate;
							if (tamanhoCampoData == 8) {
								partidaDate = LocalDate.parse(valorCelulaDataPartida,
										DateTimeFormatter.ofPattern("dd/MM/yy"));
							} else {
								partidaDate = LocalDate.parse(valorCelulaDataPartida,
										DateTimeFormatter.ofPattern("dd/MM/yyyy"));
							}

							final Date dataPartida = Dates
									.localDateToDateWithoutTime(partidaDate.withYear(Integer.valueOf(year)));
							createPartida(partidas, partidaMap, cell, dataPartida);
						} catch (final DateTimeParseException exception) {
							LOGGER.error("Não consegui converter o valor " + valorCelulaDataPartida
									+ " no formato dd/MM/yy");
							throw new PokerException("DateTimeParseException", exception);
						}
					}

					if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
						if (DateUtil.isCellDateFormatted(cell)) {
							final Date dataPartida = cell.getDateCellValue();
							final Date localDateToDateWithoutTime = Dates
									.localDateToDateWithoutTime(Dates.dateToLocalDate(dataPartida));
							createPartida(partidas, partidaMap, cell, localDateToDateWithoutTime);
						}
					}

				}
				header = false;
				continue;
			}

			final BigDecimal totalSaldo = Sheets.getBigDecimalValue(row.getCell(totalSaldoIndex));
			final Integer totalPontos = Sheets.getIntegerValue(row.getCell(totalPontosIndex));

			final Iterator<Entry<Integer, Partida>> iterator = partidaMap.entrySet().iterator();
			while (iterator.hasNext()) {
				final Entry<Integer, Partida> entry = iterator.next();
				final Integer column = entry.getKey();
				final Partida partida = entry.getValue();
				
				//Se não tiver informações na partida
				if (row.getCell(participanteIndex) == null
						|| StringUtils.isEmpty(row.getCell(participanteIndex).getStringCellValue())
						|| Sheets.isIgnoreValue(row.getCell(column))) {
					continue;
				}

				final Integer codigo = Sheets.getIntegerValue(row.getCell(codigoIndex));
				final String nome = row.getCell(participanteIndex).getStringCellValue();
				final Pessoa pessoa = getPessoa(codigo, nome, partida);

				final BigDecimal saldo = Sheets.getBigDecimalValue(row.getCell(column));
				final int pontos = Sheets.getIntegerValue(row.getCell(column + 1));

				adicionarValorAcumuladoItemPartida(pessoa, partida, acumuladorValor, saldo, pontos, totalSaldo, totalPontos);

				partida.addPessoa(pessoa, saldo, pontos);
			}
		}

		if (!playerCreatedSet.isEmpty()) {
			for (final PlayerCreated playerValidator : playerCreatedSet) {
				validator.validar("Jogador novo encontrado: " + playerValidator.getNome()
						+ " no ranking atual que veio da partida do dia " + playerValidator.getPartida().getData(),
						String.valueOf(Dates.dateToLocalDate(playerValidator.getPartida().getData()).getYear()));
			}
		}

		validator.validarSaldo(year, acumuladorValor);
		validator.validarPontos(partidas, year);
		return partidas;
	}

	private boolean containsStringInArray(final String value, final String[] stringArray) {
		for (final String s : stringArray) {
			if (s.equalsIgnoreCase(value)) {
				return true;
			}
		}
		return false;
	}

	private void createPartida(final Set<Partida> partidas, final Map<Integer, Partida> partidaMap, final Cell cell,
			final Date dataPartida) {

		final Partida partidaExistente = partidaService.findByData(dataPartida);
		if (partidaExistente != null) {
			partidaService.delete(partidaExistente);
			LOGGER.info("Partida de " + dataPartida + " ja existia. Deletando e recriando...");
		} else {
			LOGGER.info("Não existia a partida de " + dataPartida + " criada ainda. Criando....");
		}

		final Partida partida = new Partida();
		partida.setData(dataPartida);

		partidaMap.put(cell.getColumnIndex(), partida);
		partidas.add(partida);
	}

	private void adicionarValorAcumuladoItemPartida(final Pessoa pessoa, final Partida partida, final Map<Pessoa, AcumuladorValor> saldoMap,
			final BigDecimal saldo, final int pontos, final BigDecimal totalSaldo, final int totalPontos) {
		AcumuladorValor acumuladorValor = saldoMap.get(pessoa);
		if (acumuladorValor == null) {
			acumuladorValor = new AcumuladorValor(totalSaldo, totalPontos);
			saldoMap.put(pessoa, acumuladorValor);
		}
		acumuladorValor.addSaldoAcumulado(saldo);
		acumuladorValor.addPontoAcumulado(pontos);
		acumuladorValor.addPartidaPonto(partida,pontos);
	}

	private Pessoa getPessoa(final Integer codigo, final String nome, final Partida partida) {
		Pessoa pessoa = null;
		final String nameToFind = nome.trim();
		if (codigo != null && !Integer.valueOf(0).equals(codigo)) {
			pessoa = pessoaService.findByCodigo(codigo);
		} else if (!StringUtils.isEmpty(nameToFind)) {
			pessoa = pessoaService.findByNome(nameToFind);
		}

		if (pessoa == null) {
			final String playerName = WordUtils.capitalizeFully(nameToFind);
			pessoa = new Pessoa();
			pessoa.setCodigo(atomicInteger.incrementAndGet());
			pessoa.setNome(playerName);
			pessoa = pessoaService.save(pessoa);
			playerCreatedSet.add(new PlayerCreated(playerName, partida));
		}

		return pessoa;
	}

	private void createBackupFile(final String year, final File file, final Optional<Partida> partida)
			throws PokerException {
		final String partidaDate = partida.isPresent()
				? Dates.dateToLocalDate(partida.get().getData()).format(DateTimeFormatter.ofPattern(" MM-dd-yyyy"))
				: LocalDateTime.now().format(DateTimeFormatter.ofPattern(" MM-dd-yyyy"));
		final String fileName = PokerPaths.POKER_PARTIDA_FILE + partidaDate + "."
				+ FilenameUtils.getExtension(file.getName());

		final String pokerPartidaBackupFolder = pokerPaths.getPartidaBackupFolder();

		Path targetPath = new File(pokerPartidaBackupFolder + "/" + year + "/" + fileName).toPath();
		try {
			if (!Files.exists(targetPath)) {
				Files.createDirectories(targetPath);
			} else {
				int i = 1;
				while (Files.exists(targetPath)) {
					targetPath = new File(pokerPartidaBackupFolder + "/" + year + "/" + PokerPaths.POKER_PARTIDA_FILE
							+ partidaDate + " (" + i + ")." + FilenameUtils.getExtension(file.getName())).toPath();
					i++;
				}
			}
			LOGGER.info("Gerando o backup de " + file + " para " + targetPath);
			Files.move(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
		} catch (final IOException exception) {
			LOGGER.error("Error", exception);
			throw new PokerException("DateTimeParseException", exception);
		}

	}

	private class PlayerCreated {
		private final String nome;
		private final Partida partida;

		public PlayerCreated(final String nome, final Partida partida) {
			this.nome = nome;
			this.partida = partida;
		}

		public String getNome() {
			return nome;
		}

		public Partida getPartida() {
			return partida;
		}
	}

}
