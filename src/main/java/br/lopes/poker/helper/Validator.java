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
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.lopes.poker.data.AcumuladorValor;
import br.lopes.poker.domain.ItemPartida;
import br.lopes.poker.domain.Partida;
import br.lopes.poker.domain.Pessoa;
import br.lopes.poker.helper.ranking.RankingComparator;
import br.lopes.poker.helper.ranking.RankingScore;

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

	private void logValidacao(final String year, final List<String> errorLines) {
		try {
			final String rankingGeradoFolder = pokerPaths.getRankingGeradoFolder();
			final File file = new File(rankingGeradoFolder + "/" + year + "/validacoes.txt");

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

	public void validarSaldo(final String year, final Map<Pessoa, AcumuladorValor> saldoMap) {
		final List<String> errorLines = new ArrayList<>();
		final Iterator<Entry<Pessoa, AcumuladorValor>> iterator = saldoMap.entrySet().iterator();
		
		while (iterator.hasNext()) {
			final Entry<Pessoa, AcumuladorValor> pesoaEntry = iterator.next();
			final Pessoa key = pesoaEntry.getKey();
			final AcumuladorValor saldo = pesoaEntry.getValue();
			final BigDecimal saldoTotal = saldo.getSaldoTotal();
			final int pontoTotal = saldo.getPontoTotal();

			if (saldo.getSaldoAcumulado().compareTo(saldoTotal) != 0) {
				errorLines.add(key.getNome() + ": saldoTotal = " + saldoTotal + " e deveria ser = "
						+ saldo.getSaldoAcumulado());
			}

			if (saldo.getPontoAcumulado() != pontoTotal) {
				errorLines.add(key.getNome() + ": pontoTotal = " + pontoTotal + " e deveria ser = "
						+ saldo.getPontoAcumulado());
			}
		}

		logValidacao(year, errorLines);
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

	public void validarPontos(final Set<Partida> partidas, final String year) {
		final List<String> errorLines = new ArrayList<>();

		for (final Partida partida : partidas) {
			final TreeSet<ItemPartida> treeSet = new TreeSet<>(new RankingComparator());
			treeSet.addAll(partida.getItemPartidas());

			// Verifica se a pontuação lançada está correta
			int posicao = 1;
			final Iterator<ItemPartida> iterator = treeSet.iterator();
			while (iterator.hasNext()) {
				final ItemPartida itemPartida = iterator.next();
				final int pontuacaoLancada = itemPartida.getPontos();
				final int pontuacaoCorreta = RankingScore.pontuacao(posicao);

				if (pontuacaoCorreta != pontuacaoLancada) {

					errorLines.add(itemPartida.getPessoa().getNome() + ": pontuação errada na partida "
							+ Dates.formatPtBr(itemPartida.getPartida().getData()) + ". Pontuação lançada["
							+ pontuacaoLancada + "], e deveria ser [" + pontuacaoCorreta + "] para a colocação "
							+ posicao + ".\r\n");
				}

				posicao++;
			}
		}
		logValidacao(year, errorLines);
	}

}
