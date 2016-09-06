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

import br.lopes.poker.data.Saldo;
import br.lopes.poker.domain.Partida;
import br.lopes.poker.domain.Pessoa;
import br.lopes.poker.helper.Dates;
import br.lopes.poker.helper.PokerPaths;
import br.lopes.poker.helper.Sheets;
import br.lopes.poker.helper.Validator;
import br.lopes.poker.service.ImportPartida;
import br.lopes.poker.service.PartidaService;
import br.lopes.poker.service.PessoaService;

@Service
public class ImportPartidaImpl implements ImportPartida {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ImportPartidaImpl.class);

    @Autowired
    private PessoaService pessoaService;

    @Autowired
    private PartidaService partidaService;

    private Set<PlayerCreated> playerCreatedSet = new HashSet<>();

    private AtomicInteger atomicInteger;

    @Override
    @Transactional
    public List<Partida> importPartidas() {
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

    private Collection<File> searchFromPartidaDirectory() {
        final Collection<File> files = new ArrayList<>();
        try {

            final File directory = new File(PokerPaths.POKER_PARTIDA_FOLDER);
            Files.list(directory.toPath()).forEach(filePath -> {
                if (Files.isDirectory(filePath)) {
                    files.add(filePath.toFile());
                    LOGGER.info("Encontrou o path da partida " + filePath);
                }
            });

        } catch (final IOException e) {
            LOGGER.error("ErroIOException", e);
        }
        return files;
    }

    private Set<Partida> createPartidasFromDirectory(final File file) {
        final File[] listFiles = file.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(final File dir, final String name) {
                return name.endsWith("xlsx") || name.endsWith("xls");
            }
        });

        return createPartidasFromFiles(file.getName(), listFiles);
    }

    private Set<Partida> createPartidasFromFiles(final String year, final File[] listFiles) {
        Validator.deletarArquivo(year);
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

    private Set<Partida> createPartidasFromFile(final String year, final File file) {
        try {
            LOGGER.info("Iniciando o processamento da partida " + file);
            final Workbook wb = WorkbookFactory.create(file);
            final Sheet sheet = wb.getSheetAt(0);
            final Set<Partida> partidas = getPartidas(year, sheet);

            final Optional<Partida> firstPartida = partidas.stream().min((p1, p2) -> p1.getData().compareTo(p2.getData()));
            wb.close();

            if (partidas.isEmpty() && !playerCreatedSet.isEmpty()) {
                final String filePath = FilenameUtils.getFullPath(file.getAbsolutePath());
                final String fileName = PokerPaths.POKER_PARTIDA_FILE + "." + FilenameUtils.getExtension(file.getName());
                final Path targetPath = new File(filePath + "/" + fileName).toPath();
                Files.move(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                return java.util.Collections.emptySet();
            }

            createBackupFile(year, file, firstPartida);
            return partidas;
        } catch (final EncryptedDocumentException | InvalidFormatException | IOException e) {
            e.printStackTrace();
        }
        return java.util.Collections.emptySet();
    }

    private Set<Partida> getPartidas(final String year, final Sheet sheet) {
        final Set<Partida> partidas = new HashSet<Partida>();
        final Map<Integer, Partida> partidaMap = new HashMap<>();
        final Map<Pessoa, Saldo> saldoMap = new HashMap<>();

        int subTotalIndex = -1, bonusIndex = -1, totalIndex = -1, codigoIndex = -1, participanteIndex = -1;

        boolean header = true;
        for (final Row row : sheet) {
            if (header) {
                for (final Cell cell : row) {

                    if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                        final String valorCelulaDataPartida = cell.getStringCellValue().trim();
                        if (valorCelulaDataPartida.equalsIgnoreCase("Sub-total") || valorCelulaDataPartida.equalsIgnoreCase("Sub total")) {
                            subTotalIndex = cell.getColumnIndex();
                            continue;
                        }

                        if (valorCelulaDataPartida.equalsIgnoreCase("Bônus") || valorCelulaDataPartida.equalsIgnoreCase("Bonus") || valorCelulaDataPartida.contains("Bonus")
                                || valorCelulaDataPartida.contains("Bônus")) {
                            bonusIndex = cell.getColumnIndex();
                            continue;
                        }

                        if (valorCelulaDataPartida.equalsIgnoreCase("Total")) {
                            totalIndex = cell.getColumnIndex();
                            continue;
                        }

                        if (valorCelulaDataPartida.equalsIgnoreCase("CÓDIGO")) {
                            codigoIndex = cell.getColumnIndex();
                            continue;
                        }

                        if (valorCelulaDataPartida.equalsIgnoreCase("Participante")) {
                            participanteIndex = cell.getColumnIndex();
                            continue;
                        }

                        try {
                            final int tamanhoCampoData = valorCelulaDataPartida.trim().length();
                            final LocalDate partidaDate;
                            if (tamanhoCampoData == 8) {
                                partidaDate = LocalDate.parse(valorCelulaDataPartida, DateTimeFormatter.ofPattern("dd/MM/yy"));
                            } else {
                                partidaDate = LocalDate.parse(valorCelulaDataPartida, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                            }

                            final Date dataPartida = Dates.localDateToDateWithoutTime(partidaDate);
                            createPartida(partidas, partidaMap, cell, dataPartida);
                        } catch (final DateTimeParseException e) {
                            LOGGER.error("Não consegui converter o valor " + valorCelulaDataPartida + " no formato dd/MM/yy");
                        }
                    }

                    if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        if (DateUtil.isCellDateFormatted(cell)) {
                            final Date dataPartida = cell.getDateCellValue();
                            final Date localDateToDateWithoutTime = Dates.localDateToDateWithoutTime(Dates.dateToLocalDate(dataPartida));
                            createPartida(partidas, partidaMap, cell, localDateToDateWithoutTime);
                        }
                    }

                }
                header = false;
                continue;
            }

            final Iterator<Entry<Integer, Partida>> iterator = partidaMap.entrySet().iterator();
            while (iterator.hasNext()) {
                final Entry<Integer, Partida> entry = iterator.next();
                final Integer column = entry.getKey();
                final Partida partida = entry.getValue();

                if (row.getCell(participanteIndex) == null || StringUtils.isEmpty(row.getCell(participanteIndex).getStringCellValue())) {
                    break;
                }

                final Integer codigo = Sheets.getIntegerValue(row.getCell(codigoIndex));
                final String nome = row.getCell(participanteIndex).getStringCellValue();
                final Pessoa pessoa = getPessoa(codigo, nome, partida);

                final BigDecimal saldo = Sheets.getBigDecimalValue(row.getCell(column));

                if (saldo != null) {
                    final BigDecimal bonus = Sheets.getBigDecimalValue(row.getCell(bonusIndex));
                    updateSaldoPessoa(pessoa, saldoMap, saldo, row.getCell(subTotalIndex), bonus, row.getCell(totalIndex));
                    partida.addPessoa(pessoa, saldo, BigDecimal.ONE);
                }

            }

        }
        if (!playerCreatedSet.isEmpty()) {
            for (final PlayerCreated playerValidator : playerCreatedSet) {
                Validator.validar("Jogador novo encontrado: " + playerValidator.getNome() + " no ranking atual que veio da partida do dia " + playerValidator.getPartida().getData(),
                        String.valueOf(Dates.dateToLocalDate(playerValidator.getPartida().getData()).getYear()));
            }
        }

        Validator.validarSaldo(year, saldoMap);
        return partidas;
    }

    private void createPartida(final Set<Partida> partidas, final Map<Integer, Partida> partidaMap, final Cell cell, final Date dataPartida) {

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

    private void updateSaldoPessoa(final Pessoa pessoa, final Map<Pessoa, Saldo> saldoMap, final BigDecimal saldo, final Cell subTotalCell, final BigDecimal bonus, final Cell totalCell) {
        Saldo saldoAcumulado = saldoMap.get(pessoa);
        if (saldoAcumulado == null) {
            saldoAcumulado = new Saldo();
            saldoAcumulado.setSubTotalLancado(Sheets.getBigDecimalValue(subTotalCell));
            saldoAcumulado.setBonusLancado(bonus);
            saldoAcumulado.setTotalLancado(Sheets.getBigDecimalValue(totalCell));
            saldoMap.put(pessoa, saldoAcumulado);
        }
        saldoAcumulado.addSaldoAcumulado(saldo);

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

    private void createBackupFile(final String year, final File file, final Optional<Partida> partida) {
        final String partidaDate = partida.isPresent() ? Dates.dateToLocalDate(partida.get().getData()).format(DateTimeFormatter.ofPattern(" MM-dd-yyyy"))
                : LocalDateTime.now().format(DateTimeFormatter.ofPattern(" MM-dd-yyyy"));
        final String fileName = PokerPaths.POKER_PARTIDA_FILE + partidaDate + "." + FilenameUtils.getExtension(file.getName());

        Path targetPath = new File(PokerPaths.POKER_PARTIDA_BACKUP_FOLDER + "/" + year + "/" + fileName).toPath();
        try {
            if (!Files.exists(targetPath)) {
                Files.createDirectories(targetPath);
            } else {
                int i = 1;
                while (Files.exists(targetPath)) {
                    targetPath = new File(PokerPaths.POKER_PARTIDA_BACKUP_FOLDER + "/" + year + "/" + PokerPaths.POKER_PARTIDA_FILE + partidaDate + " (" + i + ")."
                            + FilenameUtils.getExtension(file.getName())).toPath();
                    i++;
                }
            }
            LOGGER.info("Gerando o backup de " + file + " para " + targetPath);
            Files.move(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException e) {
            LOGGER.error("Error", e);
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
