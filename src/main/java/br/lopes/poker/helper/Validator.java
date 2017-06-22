package br.lopes.poker.helper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.lopes.poker.data.Saldo;
import br.lopes.poker.domain.Pessoa;

@Component
public class Validator {
    private static final Logger LOGGER = LoggerFactory.getLogger(Validator.class);

    @Autowired
    private PokerPaths pokerPaths;

    public void deletarArquivo(final String year) {
        final String rankingGeradoFolder = pokerPaths.getRankingGeradoFolder();

        final File file = new File(rankingGeradoFolder + "/" + year + "/validacoes.txt");
        try {
            Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            LOGGER.error("Não consegui deletar o arquivo " + file, e);
        }
    }

    public void validarSaldo(final String year, final Map<Pessoa, Saldo> saldoMap) {
        try {
            final List<String> errorLines = new ArrayList<>();
            final String rankingGeradoFolder = pokerPaths.getRankingGeradoFolder();
            final File file = new File(rankingGeradoFolder + "/" + year + "/validacoes.txt");

            final Iterator<Entry<Pessoa, Saldo>> iterator2 = saldoMap.entrySet().iterator();
            while (iterator2.hasNext()) {
                final Entry<Pessoa, Saldo> entry2 = iterator2.next();
                final Pessoa key = entry2.getKey();
                final Saldo saldo = entry2.getValue();
                final BigDecimal bonusLancado = saldo.getBonusLancado();
                final BigDecimal totalLancado = saldo.getTotalLancado();

                if (saldo.getSaldoAcumulado().compareTo(saldo.getSubTotalLancado()) != 0) {
                    errorLines.add(key.getNome() + ": sub-total = " + saldo.getSubTotalLancado() + " e deveria ser = "
                            + saldo.getSaldoAcumulado());
                }

                final BigDecimal totalEsperado = saldo.getSaldoAcumulado().add(bonusLancado);
                if (totalEsperado.compareTo(totalLancado) != 0) {
                    errorLines.add(key.getNome() + ": total = " + totalLancado + " e deveria ser = " + totalEsperado);
                }

            }

            if (errorLines.isEmpty()) {
                return;
            }

            if (!file.exists()) {
                Files.createDirectories(new File(rankingGeradoFolder + "/" + year).toPath());
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

    public void validar(final String texto, final String year) {
        try {
            final String rankingGeradoFolder = pokerPaths.getRankingGeradoFolder();
            final File file = new File(rankingGeradoFolder + "/" + year + "/validacoes.txt");
            if (!file.exists()) {
                Files.createDirectories(new File(rankingGeradoFolder + "/" + year).toPath());
                Files.createFile(file.toPath());
            }

            final FileWriter writer = new FileWriter(file.getAbsoluteFile(), true);
            final BufferedWriter bw = new BufferedWriter(writer);

            bw.write(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss")) + " - " + texto);
            bw.newLine();

            bw.close();
        } catch (final IOException e) {
            LOGGER.error("validarInformacoes", e);
            e.printStackTrace();
        }

    }
}
