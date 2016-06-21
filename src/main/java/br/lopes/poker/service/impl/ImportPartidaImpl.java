package br.lopes.poker.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
import br.lopes.poker.service.ImportPartida;
import br.lopes.poker.service.PartidaService;
import br.lopes.poker.service.PessoaService;

@Service
public class ImportPartidaImpl implements ImportPartida {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ImportPartidaImpl.class);
    private static final String PARTIDA_FOLDER = "c:/poker/partidas";
    private static final String PARTIDA_BACKUP_FOLDER = "c:/poker/partidas/backup";
    private static final String RANKING_FOLDER = "c:/poker/ranking/gerado";

    @Autowired
    private PessoaService pessoaService;

    @Autowired
    private PartidaService partidaService;

    @Override
    public List<Partida> importPartidas() {
        final Collection<File> partidaFiles = searchFromPartidaDirectory();
        final Set<Partida> partidasSet = new HashSet<>();
        for (final File file : partidaFiles) {
            final Set<Partida> partidasFromDirectory = createPartidasFromDirectory(file);
            partidasSet.addAll(partidasFromDirectory);
        }
        return partidaService.save(partidasSet);
    }

    private Collection<File> searchFromPartidaDirectory() {
        final Collection<File> files = new ArrayList<>();
        try {

            final File directory = new File(PARTIDA_FOLDER);
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
        if (listFiles.length > 0) {
            return createPartidasFromFile(year, listFiles[0]);
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
            wb.close();
            // createBackupFile(year, file);
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

        int subTotalIndex = -1;
        int bonusIndex = -1;
        int totalIndex = -1;

        boolean header = true;
        for (final Row row : sheet) {
            if (header) {
                for (final Cell cell : row) {
                    final String stringCellValue = cell.getStringCellValue();
                    if (stringCellValue.equalsIgnoreCase("Sub-total") || stringCellValue.equalsIgnoreCase("Sub total")) {
                        subTotalIndex = cell.getColumnIndex();
                        continue;
                    }

                    if (stringCellValue.equalsIgnoreCase("Bônus") || stringCellValue.equalsIgnoreCase("Bonus")) {
                        bonusIndex = cell.getColumnIndex();
                        continue;
                    }

                    if (stringCellValue.equalsIgnoreCase("Total")) {
                        totalIndex = cell.getColumnIndex();
                        continue;
                    }

                    if (stringCellValue.equalsIgnoreCase("Participante")) {
                        continue;
                    }

                    if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        if (DateUtil.isCellDateFormatted(cell)) {
                            final Date localDate = cell.getDateCellValue();
                            final LocalDate partidaDate = asLocalDate(localDate);
                            final Partida partida = new Partida();
                            partida.setData(partidaDate);
                            partidaMap.put(cell.getColumnIndex(), partida);
                            partidas.add(partida);
                        }
                    }
                    if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                        try {
                            final LocalDate partidaDate = LocalDate.parse(stringCellValue, DateTimeFormatter.ofPattern("dd/MM/yy"));
                            final Partida partida = new Partida();
                            partida.setData(partidaDate);
                            partidaMap.put(cell.getColumnIndex(), partida);
                            partidas.add(partida);
                        } catch (final DateTimeParseException e) {
                            LOGGER.error("Não consegui converter o valor " + stringCellValue + " no formato dd/MM/yy");
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

                final String nome = row.getCell(0).getStringCellValue();
                if (StringUtils.isEmpty(nome)) {
                    break;
                }
                final Pessoa pessoa = getPessoa(nome.trim());

                BigDecimal saldo = getBigDecimalFromCell(row.getCell(column));

                if (saldo != null) {
                    final BigDecimal bonus = getBigDecimalFromCell(row.getCell(bonusIndex));
                    updateSaldoPessoa(pessoa, saldoMap, saldo, row.getCell(subTotalIndex), bonus, row.getCell(totalIndex));
                    partida.addPessoa(pessoa, saldo, BigDecimal.ONE);
                }

            }

        }
        validarInformacoes(year, saldoMap);
        return partidas;
    }

    private void validarInformacoes(final String year, final Map<Pessoa, Saldo> saldoMap) {
        try {
            final List<String> errorLines = new ArrayList<>();
            final File file = new File(RANKING_FOLDER + "/" + year + "/validacoes.txt");
            Files.deleteIfExists(file.toPath());

            final Iterator<Entry<Pessoa, Saldo>> iterator2 = saldoMap.entrySet().iterator();
            while (iterator2.hasNext()) {
                final Entry<Pessoa, Saldo> entry2 = iterator2.next();
                final Pessoa key = entry2.getKey();
                final Saldo value = entry2.getValue();

                if (value.getSaldoAcumulado().compareTo(value.getSubTotalLancado()) != 0) {
                    errorLines.add(key.getNome() + ": sub-total = " + value.getSubTotalLancado() + " e deveria ser = " + value.getSaldoAcumulado());
                }
            }

            if (errorLines.isEmpty()) {
                return;
            }

            if (!file.exists()) {
                Files.createDirectories(new File(RANKING_FOLDER + "/" + year).toPath());
                Files.createFile(file.toPath());
            }

            final FileWriter writer = new FileWriter(file.getAbsoluteFile(), true);

            for (final String erroLine : errorLines) {
                writer.write(erroLine);
                writer.write("\r\n");
            }

            writer.close();
        } catch (final IOException e) {
            LOGGER.error("validarInformacoes", e);
            e.printStackTrace();
        }

    }

    private BigDecimal getBigDecimalFromCell(final Cell cell) {
        BigDecimal saldo;
        if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            saldo = BigDecimal.valueOf(cell.getNumericCellValue());
        } else {
            String saldoString = cell.getStringCellValue();
            saldoString = saldoString.trim();
            saldoString = saldoString.replace(",", ".");
            saldoString = saldoString.replace("+", "");

            if (saldoString.equals("-") || saldoString.equals("")) {
                saldo = null;
            } else {
                saldo = new BigDecimal(saldoString);
            }
        }
        return saldo;
    }

    private void updateSaldoPessoa(final Pessoa pessoa, final Map<Pessoa, Saldo> saldoMap, final BigDecimal saldo, final Cell subTotalCell, final BigDecimal bonus, final Cell totalCell) {
        Saldo saldoAcumulado = saldoMap.get(pessoa);
        if (saldoAcumulado == null) {
            saldoAcumulado = new Saldo();
            saldoAcumulado.setSubTotalLancado(getBigDecimalFromCell(subTotalCell));
            saldoAcumulado.setBonusLancado(bonus);
            saldoAcumulado.setTotalLancado(getBigDecimalFromCell(totalCell));
            saldoMap.put(pessoa, saldoAcumulado);
        }
        saldoAcumulado.addSaldoAcumulado(saldo);

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
        final Path targetPath = new File(PARTIDA_BACKUP_FOLDER + "/" + year + "/" + file.getName()).toPath();
        try {
            if (!Files.exists(targetPath)) {
                Files.createDirectories(targetPath);
            }
            LOGGER.info("Gerando o backup de " + file + " para " + targetPath);
            Files.move(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException e) {
            LOGGER.error("Error", e);
        }

    }

    private LocalDate asLocalDate(final Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

}
