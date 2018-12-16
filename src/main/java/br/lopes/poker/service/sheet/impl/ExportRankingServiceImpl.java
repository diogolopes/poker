package br.lopes.poker.service.sheet.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.lopes.poker.data.ExportedItemRanking;
import br.lopes.poker.domain.ItemRanking;
import br.lopes.poker.domain.Pessoa;
import br.lopes.poker.domain.Ranking;
import br.lopes.poker.domain.RankingType;
import br.lopes.poker.helper.Dates;
import br.lopes.poker.helper.PokerPaths;
import br.lopes.poker.helper.PokerPlanilha;
import br.lopes.poker.service.RankingService;
import br.lopes.poker.service.sheet.ExportRankingService;

@Service
public class ExportRankingServiceImpl implements ExportRankingService {

	@Autowired
	private RankingService rankingService;

	@Autowired
	private PokerPaths pokerPaths;

	private String getFilename(final Ranking ranking) {
		return "Ranking PDS (" + ranking.getRankingType().getNome() + ") " + Dates.format(ranking.getDataAtualizacao(), "MM-dd-yyyy") + ".xlsx";
	}

	private Collection<ExportedItemRanking> fromItemRanking(final Collection<ItemRanking> itemRanking) {
		final Collection<ExportedItemRanking> exportedItem = new LinkedHashSet<>(itemRanking.size());
		itemRanking.forEach(ir -> exportedItem.add(new ExportedItemRanking(ir)));
		return exportedItem;

	}

	private void generateRankingFile(final Ranking ranking, final Collection<ExportedItemRanking> exportedtemRanking)
			throws IOException, FileNotFoundException {
		final Workbook wb = new XSSFWorkbook();
		final Sheet sheet = wb.createSheet(String.valueOf(ranking.getAno()));

		final CellStyle cabecalhoCellStyle = cabecalhoCellStyle(wb);
		final CellStyle conteudoCellStyle = conteudoCellStyle(wb);
		final CellStyle movimentacaoPositivaCellStyle = movimentacaoPositivaCellStyle(wb);
		final CellStyle movimentacaoNegativaCellStyle = movimentacaoNegativaCellStyle(wb);
		final CellStyle saldoCellStyle = saldoCellStyle(wb);

		criaCabecalho(sheet, ranking, cabecalhoCellStyle);
		criaConteudo(sheet, exportedtemRanking, ranking, conteudoCellStyle, movimentacaoPositivaCellStyle,
				movimentacaoNegativaCellStyle, saldoCellStyle);

		autosizeColumns(sheet);
		final Integer ano = ranking.getAno();
		final String rankingGeradoFolder = pokerPaths.getRankingGeradoFolder();
		final File fileDirectory = new File(rankingGeradoFolder + "/" + ano + "/");
		final File file = new File(rankingGeradoFolder + "/" + ano + "/" + getFilename(ranking));

		if (!Files.exists(fileDirectory.toPath())) {
			Files.createDirectories(fileDirectory.toPath());
		}

		final FileOutputStream fileOut = new FileOutputStream(file);

		wb.write(fileOut);
		wb.close();
	}

	private CellStyle saldoCellStyle(final Workbook wb) {
		final CreationHelper ch = wb.getCreationHelper();
		final CellStyle saldoCellStyle = wb.createCellStyle();

		saldoCellStyle.setBorderBottom(CellStyle.BORDER_THIN);
		saldoCellStyle.setBorderLeft(CellStyle.BORDER_THIN);
		saldoCellStyle.setBorderRight(CellStyle.BORDER_THIN);
		saldoCellStyle.setBorderTop(CellStyle.BORDER_THIN);
		saldoCellStyle.setDataFormat(ch.createDataFormat().getFormat("R$ #,##0.00;[Red]-R$ #,##0.00"));

		return saldoCellStyle;
	}

	private CellStyle movimentacaoNegativaCellStyle(final Workbook wb) {
		final Font boldFont = wb.createFont();
		boldFont.setBold(true);
		boldFont.setColor(HSSFColor.RED.index);

		final CellStyle cabecalhoCellStyle = wb.createCellStyle();
		cabecalhoCellStyle.setBorderBottom(CellStyle.BORDER_THIN);
		cabecalhoCellStyle.setBorderLeft(CellStyle.BORDER_THIN);
		cabecalhoCellStyle.setBorderRight(CellStyle.BORDER_THIN);
		cabecalhoCellStyle.setBorderTop(CellStyle.BORDER_THIN);
		cabecalhoCellStyle.setFont(boldFont);
		cabecalhoCellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
		return cabecalhoCellStyle;
	}

	private CellStyle movimentacaoPositivaCellStyle(final Workbook wb) {
		final Font boldFont = wb.createFont();
		boldFont.setBold(true);
		boldFont.setColor(HSSFColor.BLUE.index);

		final CellStyle cabecalhoCellStyle = wb.createCellStyle();
		cabecalhoCellStyle.setBorderBottom(CellStyle.BORDER_THIN);
		cabecalhoCellStyle.setBorderLeft(CellStyle.BORDER_THIN);
		cabecalhoCellStyle.setBorderRight(CellStyle.BORDER_THIN);
		cabecalhoCellStyle.setBorderTop(CellStyle.BORDER_THIN);
		cabecalhoCellStyle.setFont(boldFont);
		cabecalhoCellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
		return cabecalhoCellStyle;
	}

	private CellStyle conteudoCellStyle(final Workbook wb) {
		final CellStyle cabecalhoCellStyle = wb.createCellStyle();
		cabecalhoCellStyle.setBorderBottom(CellStyle.BORDER_THIN);
		cabecalhoCellStyle.setBorderLeft(CellStyle.BORDER_THIN);
		cabecalhoCellStyle.setBorderRight(CellStyle.BORDER_THIN);
		cabecalhoCellStyle.setBorderTop(CellStyle.BORDER_THIN);
		return cabecalhoCellStyle;
	}

	private CellStyle cabecalhoCellStyle(final Workbook wb) {
		final Font boldFont = wb.createFont();
		boldFont.setBold(true);

		final CellStyle cabecalhoCellStyle = wb.createCellStyle();
		cabecalhoCellStyle.setBorderBottom(CellStyle.BORDER_THIN);
		cabecalhoCellStyle.setBorderLeft(CellStyle.BORDER_THIN);
		cabecalhoCellStyle.setBorderRight(CellStyle.BORDER_THIN);
		cabecalhoCellStyle.setBorderTop(CellStyle.BORDER_THIN);
		cabecalhoCellStyle.setFont(boldFont);
		return cabecalhoCellStyle;
	}

	private void autosizeColumns(final Sheet sheet) {
		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2);
		sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4);
		sheet.autoSizeColumn(5);
		sheet.autoSizeColumn(6);
		sheet.autoSizeColumn(7);
		sheet.autoSizeColumn(8);
		sheet.autoSizeColumn(9);
	}

	private void criaConteudo(final Sheet sheet, final Collection<ExportedItemRanking> exportedtemRankings,
			final Ranking ranking, final CellStyle conteudoCellStyle, final CellStyle movimentacaoPositivaCellStyle,
			final CellStyle movimentacaoNegativaCellStyle, final CellStyle saldoCellStyle) {
		int linha = 2;

		final Iterator<ExportedItemRanking> iterator = exportedtemRankings.iterator();
		while (iterator.hasNext()) {
			final ExportedItemRanking classificacao = iterator.next();
			final Row row = sheet.createRow(linha++);

			final Cell cellPosicao = row.createCell(PokerPlanilha.COLUNA_COLOCACAO_INDEX);
			cellPosicao.setCellValue(classificacao.getPosicao());
			cellPosicao.setCellStyle(conteudoCellStyle);

			final Cell movimentacaoCell = row.createCell(PokerPlanilha.COLUNA_MOVIMENTACA_INDEX);
			if (classificacao.getMovimentacao() > 0) {
				movimentacaoCell.setCellValue("▲ " + classificacao.getMovimentacao());
				movimentacaoCell.setCellStyle(movimentacaoPositivaCellStyle);
			} else if (classificacao.getMovimentacao() < 0) {
				movimentacaoCell.setCellValue("▼ " + (classificacao.getMovimentacao() * -1));
				movimentacaoCell.setCellStyle(movimentacaoNegativaCellStyle);
			} else {
				movimentacaoCell.setCellStyle(conteudoCellStyle);
			}

			final Cell nomeCell = row.createCell(PokerPlanilha.COLUNA_NOME_INDEX);
			nomeCell.setCellValue(classificacao.getNomePessoa());
			nomeCell.setCellStyle(conteudoCellStyle);

			final Cell codigoCell = row.createCell(PokerPlanilha.COLUNA_CODIGO_INDEX);
			codigoCell.setCellValue(classificacao.getCodigoPessoa());
			codigoCell.setCellStyle(conteudoCellStyle);

			final Cell pontosCell = row.createCell(PokerPlanilha.COLUNA_PONTUACAO_INDEX);
			pontosCell.setCellValue(classificacao.getPontos());
			pontosCell.setCellStyle(conteudoCellStyle);

			final Cell jogosCell = row.createCell(PokerPlanilha.COLUNA_JOGOS_INDEX);
			jogosCell.setCellValue(classificacao.getJogos());
			jogosCell.setCellStyle(conteudoCellStyle);

			final Cell saldoCell = row.createCell(PokerPlanilha.COLUNA_SALDO_INDEX);
			saldoCell.setCellValue(classificacao.getSaldo().doubleValue());
			saldoCell.setCellStyle(saldoCellStyle);

		}

	}

	private void criaCabecalho(final Sheet sheet, final Ranking ranking, final CellStyle borderCellStyle) {
		final Row infoRow = sheet.createRow(0);
		final Row headerRow = sheet.createRow(1);

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, PokerPlanilha.COLUNA_SALDO_INDEX));

		final Cell infoCell = infoRow.createCell(0);
		
		infoCell.setCellValue("Atualizado em " + Dates.formatPtBr(ranking.getDataAtualizacao()));

		final Cell colocacaoCell = headerRow.createCell(PokerPlanilha.COLUNA_COLOCACAO_INDEX);
		colocacaoCell.setCellValue(PokerPlanilha.COLUNA_COLOCACAO);
		colocacaoCell.setCellStyle(borderCellStyle);

		final Cell movimentacaoCell = headerRow.createCell(PokerPlanilha.COLUNA_MOVIMENTACA_INDEX);
		movimentacaoCell.setCellValue(PokerPlanilha.COLUNA_MOVIMENTACAO);
		movimentacaoCell.setCellStyle(borderCellStyle);

		final Cell nomeCell = headerRow.createCell(PokerPlanilha.COLUNA_NOME_INDEX);
		nomeCell.setCellValue(PokerPlanilha.COLUNA_NOME);
		nomeCell.setCellStyle(borderCellStyle);

		final Cell codigoCell = headerRow.createCell(PokerPlanilha.COLUNA_CODIGO_INDEX);
		codigoCell.setCellValue(PokerPlanilha.COLUNA_CODIGO);
		codigoCell.setCellStyle(borderCellStyle);

		final Cell pontuacaoCell = headerRow.createCell(PokerPlanilha.COLUNA_PONTUACAO_INDEX);
		pontuacaoCell.setCellValue(PokerPlanilha.COLUNA_PONTUACAO);
		pontuacaoCell.setCellStyle(borderCellStyle);

		final Cell jogosCell = headerRow.createCell(PokerPlanilha.COLUNA_JOGOS_INDEX);
		jogosCell.setCellValue(PokerPlanilha.COLUNA_JOGOS);
		jogosCell.setCellStyle(borderCellStyle);

		final Cell saldoCell = headerRow.createCell(PokerPlanilha.COLUNA_SALDO_INDEX);
		saldoCell.setCellValue(PokerPlanilha.COLUNA_SALDO);
		saldoCell.setCellStyle(borderCellStyle);
	}

	@Override
	public void export(final Ranking ranking) throws Exception {
		export(ranking, RankingType.SALDO);
	}

	@Transactional
	@Override
	public void export(final Ranking ranking, final RankingType rankingType) throws Exception {
		if (ranking == null) {
			return;
		}
		final Ranking currentRanking = rankingService.findById(ranking.getId());
		final Collection<ItemRanking> classificacoes = currentRanking.getItemRankings();

		if (classificacoes.isEmpty()) {
			return;
		}
		generateRankingFile(ranking, fromItemRanking(classificacoes));

	}

	@Override
	public void export(final Map<Pessoa, ExportedItemRanking> exportedItemRankingMap, final Ranking ranking)
			throws Exception {

		if ((exportedItemRankingMap == null) || exportedItemRankingMap.isEmpty()) {
			return;
		}
		final Collection<ExportedItemRanking> exportedtemRankings = exportedItemRankingMap.values();
		generateRankingFile(ranking, exportedtemRankings);

	}

}
