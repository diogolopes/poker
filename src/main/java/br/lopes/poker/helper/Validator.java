package br.lopes.poker.helper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

import br.lopes.poker.data.Saldo;
import br.lopes.poker.domain.Pessoa;

public class Validator {
	private static final Logger LOGGER = LoggerFactory.getLogger(Validator.class);

	public static void deletarArquivo(final String year) {
		final File file = new File(PokerPaths.POKER_RANKING_GERADO_FOLDER + "/" + year + "/validacoes.txt");
		try {
			Files.deleteIfExists(file.toPath());
		} catch (IOException e) {
			LOGGER.error("Não consegui deletar o arquivo " + file, e);
		}
	}

	public static void validarSaldo(final String year, final Map<Pessoa, Saldo> saldoMap) {
		try {
			final List<String> errorLines = new ArrayList<>();
			final File file = new File(PokerPaths.POKER_RANKING_GERADO_FOLDER + "/" + year + "/validacoes.txt");

			final Iterator<Entry<Pessoa, Saldo>> iterator2 = saldoMap.entrySet().iterator();
			while (iterator2.hasNext()) {
				final Entry<Pessoa, Saldo> entry2 = iterator2.next();
				final Pessoa key = entry2.getKey();
				final Saldo value = entry2.getValue();

				if (value.getSaldoAcumulado().compareTo(value.getSubTotalLancado()) != 0) {
					errorLines.add(key.getNome() + ": sub-total = " + value.getSubTotalLancado() + " e deveria ser = "
							+ value.getSaldoAcumulado());
				}
			}

			if (errorLines.isEmpty()) {
				return;
			}

			if (!file.exists()) {
				Files.createDirectories(new File(PokerPaths.POKER_RANKING_GERADO_FOLDER + "/" + year).toPath());
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

	public static void validar(final String texto, final String year) {
		try {
			final File file = new File(PokerPaths.POKER_RANKING_GERADO_FOLDER + "/" + year + "/validacoes.txt");
			if (!file.exists()) {
				Files.createDirectories(new File(PokerPaths.POKER_RANKING_GERADO_FOLDER + "/" + year).toPath());
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
