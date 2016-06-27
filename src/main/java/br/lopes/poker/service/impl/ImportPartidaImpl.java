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
import java.util.Set;

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

    @Override
    @Transactional
    public List<Partida> importPartidas() {
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
            createBackupFile(year, file);
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
                    Partida partida;
                    if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                        final String valorCelulaDataPartida = cell.getStringCellValue().trim();
                        if (valorCelulaDataPartida.equalsIgnoreCase("Sub-total") || valorCelulaDataPartida.equalsIgnoreCase("Sub total")) {
                            subTotalIndex = cell.getColumnIndex();
                            continue;
                        }

                        if (valorCelulaDataPartida.equalsIgnoreCase("Bônus") || valorCelulaDataPartida.equalsIgnoreCase("Bonus")) {
                            bonusIndex = cell.getColumnIndex();
                            continue;
                        }

                        if (valorCelulaDataPartida.equalsIgnoreCase("Total")) {
                            totalIndex = cell.getColumnIndex();
                            continue;
                        }

                        if (valorCelulaDataPartida.equalsIgnoreCase("Participante")) {
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

                            final Date dataPartida = Dates.localDateToDate(partidaDate);
                            partida = partidaService.findByData(dataPartida);
                            if (partida == null) {
                                partida = new Partida();
                                partida.setData(dataPartida);
                                LOGGER.info("Não existia a partida de " + dataPartida + " criada ainda. Criando....");
                            } else {
                                LOGGER.info("Partida de " + dataPartida + " ja existia. Recuperando....");
                            }

                            partidaMap.put(cell.getColumnIndex(), partida);
                            partidas.add(partida);
                        } catch (final DateTimeParseException e) {
                            LOGGER.error("Não consegui converter o valor " + valorCelulaDataPartida + " no formato dd/MM/yy");
                        }
                    }

                    if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        if (DateUtil.isCellDateFormatted(cell)) {
                            final Date dataPartida = cell.getDateCellValue();
                            partida = partidaService.findByData(dataPartida);
                            if (partida == null) {
                                partida = new Partida();
                                partida.setData(dataPartida);
                            }
                            partidaMap.put(cell.getColumnIndex(), partida);
                            partidas.add(partida);
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
        Validator.validarSaldo(year, saldoMap);
        return partidas;
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
            pessoa.setNome(WordUtils.capitalizeFully(nome));
            pessoa = pessoaService.save(pessoa);
        }
        return pessoa;
    }

    private void createBackupFile(final String year, final File file) {
        final String fileName = FilenameUtils.getBaseName(file.getName()) + LocalDateTime.now().format(DateTimeFormatter.ofPattern(" dd-MM-yyyy hh-mm-ss")) + "."
                + FilenameUtils.getExtension(file.getName());

        Path targetPath = new File(PokerPaths.POKER_PARTIDA_BACKUP_FOLDER + "/" + year + "/" + fileName).toPath();
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

}
