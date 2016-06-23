package br.lopes.poker.service.impl;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import br.lopes.poker.data.Classificacao;
import br.lopes.poker.domain.Pessoa;
import br.lopes.poker.helper.PokerPaths;
import br.lopes.poker.service.ExportRanking;

@Service
public class ExportRankingServiceImpl implements ExportRanking {

	private String getFilename() {
		return "Ranking pds " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".xlsx";
	}

	@Override
	public void export(final Map<Pessoa, Classificacao> map, final String ano) throws Exception {
		if (map == null || map.isEmpty()) {
			return;
		}
		final Workbook wb = new XSSFWorkbook();
		final Sheet sheet = wb.createSheet(ano);

		criaCabecalho(sheet);
		criaConteudo(sheet, map);

		final FileOutputStream fileOut = new FileOutputStream(
				PokerPaths.POKER_RANKING_GERADO_FOLDER + "/" + ano + "/" + getFilename());

		wb.write(fileOut);
		wb.close();
	}

	private void criaConteudo(final Sheet sheet, final Map<Pessoa, Classificacao> map) {
		int linha = 1;
		final Iterator<Entry<Pessoa, Classificacao>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			final Entry<Pessoa, Classificacao> entry = iterator.next();
			final Classificacao classificacao = entry.getValue();

			final Row row = sheet.createRow(linha++);
			row.createCell(0).setCellValue(classificacao.getPosicaoAtual());
			row.createCell(1).setCellValue(classificacao.getMovimentacao());
			row.createCell(2).setCellValue(classificacao.getPessoa().getNome());
			row.createCell(3).setCellValue(classificacao.getSaldo().doubleValue());
			row.createCell(4).setCellValue(classificacao.getJogos());
			row.createCell(5).setCellValue(classificacao.getVitoria());
			row.createCell(6).setCellValue(classificacao.getDerrota());
			row.createCell(7).setCellValue(classificacao.getEmpate());

		}

	}

	private void criaCabecalho(Sheet sheet) {
		// TODO Auto-generated method stub

	}

}
