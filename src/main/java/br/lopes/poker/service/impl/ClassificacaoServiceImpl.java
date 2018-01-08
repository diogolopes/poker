package br.lopes.poker.service.impl;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.lopes.poker.data.ExportedItemRanking;
import br.lopes.poker.domain.ItemPartida;
import br.lopes.poker.domain.ItemRanking;
import br.lopes.poker.domain.Partida;
import br.lopes.poker.domain.Pessoa;
import br.lopes.poker.domain.Ranking;
import br.lopes.poker.domain.RankingType;
import br.lopes.poker.helper.Dates;
import br.lopes.poker.helper.Validator;
import br.lopes.poker.service.RankingService;
import br.lopes.poker.service.sheet.ClassificacaoService;
import br.lopes.poker.service.sheet.ExportRankingService;

@Service
public class ClassificacaoServiceImpl implements ClassificacaoService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClassificacaoServiceImpl.class);

	private RankingCriteriaFactory rankingCriteriaFactory = new RankingCriteriaFactory();

	@Autowired
	private RankingService rankingService;

	@Autowired
	private ExportRankingService exportRanking;

	@Autowired
	private Validator validator;

	@Override
	public void generateRankingFileByPartidasAndType(final Ranking ranking, final Set<Partida> partidas)
			throws Exception {
		generateRankingFileByPartidasAndType(ranking, partidas, ranking.getRankingType());
	}

	@Override
	public void generateRankingFileByRankingAndType(final Ranking ranking, final RankingType rankingType)
			throws Exception {
		if (ranking == null) {
			LOGGER.info("Nenhum ranking encontrado....");
			return;
		}

		LOGGER.info("Iniciando reclassificacao do ranking de " + ranking.getAno() + " ultima atualização em "
				+ ranking.getDataAtualizacao() + " por " + rankingType.getNome());

		final Map<Pessoa, ExportedItemRanking> rankingMap = transformFromRanking(ranking);
		final Map<Pessoa, ExportedItemRanking> treeMap = rankingCriteriaFactory.create(rankingMap, rankingType);
		treeMap.putAll(rankingMap);
		updatePosition(treeMap);

		final Ranking save = rankingService.save(transformToRanking(ranking, treeMap));
		exportRanking.export(save, rankingType);

	}

	@Override
	public void generateRankingFileByPartidasAndType(final Ranking ranking, final Set<Partida> partidas,
			final RankingType rankingType) throws Exception {
		if (ranking == null || partidas.isEmpty()) {
			LOGGER.info("Nenhum ranking encontrado....");
			return;
		}

		LOGGER.info("Iniciando reclassificacao do ranking de " + ranking.getAno() + " ultima atualização em "
				+ ranking.getDataAtualizacao() + " para " + partidas.size() + " partidas por " + rankingType.getNome());

		Map<Pessoa, ExportedItemRanking> rankingMap = transformFromRanking(ranking);

		LOGGER.info("Processando " + partidas.size() + " novas partidas para o ranking.");

		for (final Partida partida : partidas) {
			rankingMap = getRankingMap(partida, rankingMap);
		}

		final Map<Pessoa, ExportedItemRanking> treeMap = rankingCriteriaFactory.create(rankingMap, rankingType);
		treeMap.putAll(rankingMap);
		updatePosition(treeMap);

		final Ranking save = rankingService.save(transformToRanking(ranking,treeMap));
		exportRanking.export(save, rankingType);
	}

	@Override
	public void generateRankingFileByRanking(final Ranking ranking) throws Exception {
		if (ranking == null) {
			LOGGER.info("Nenhum ranking encontrado....");
			return;
		}
		final RankingType rankingType = ranking.getRankingType();
		LOGGER.info("Iniciando reclassificacao do ranking de " + ranking.getAno() + " ultima atualização em "
				+ ranking.getDataAtualizacao() + " por " + rankingType.getNome());

		final Map<Pessoa, ExportedItemRanking> exportedtemRanking = transformFromRanking(ranking);
		final Map<Pessoa, ExportedItemRanking> sortedByCriteriaMap = rankingCriteriaFactory.create(exportedtemRanking,
				rankingType);
		sortedByCriteriaMap.putAll(exportedtemRanking);
		updatePosition(sortedByCriteriaMap);
		exportRanking.export(sortedByCriteriaMap, ranking.getAno(), rankingType);
	}

	private Ranking transformToRanking(final Ranking ranking,
			final Map<Pessoa, ExportedItemRanking> exportedtemRankingMap) {
		final Iterator<Entry<Pessoa, ExportedItemRanking>> classificacaoIterator = exportedtemRankingMap.entrySet()
				.iterator();

		final Set<ItemRanking> novasPessoas = new HashSet<>();

		while (classificacaoIterator.hasNext()) {
			final Entry<Pessoa, ExportedItemRanking> entry = classificacaoIterator.next();
			final Pessoa pessoa = entry.getKey();
			final ExportedItemRanking classificacao = entry.getValue();

			ItemRanking itemRanking = pessoa != null
					? ranking.getItemRankings().stream().filter(c -> c.getPessoa().getNome().equals(pessoa.getNome()))
							.findFirst().orElse(null)
					: null;

			if (itemRanking == null) {
				itemRanking = new ItemRanking();
				itemRanking.setPessoa(pessoa);
				novasPessoas.add(itemRanking);
			}
			itemRanking.setSaldo(classificacao.getSaldo());
			itemRanking.setJogos(classificacao.getJogos());
			itemRanking.setPosicaoAtual(classificacao.getPosicaoAtual());
			itemRanking.setPosicaoAnterior(classificacao.getPosicaoAnterior());
			itemRanking.setPontos(classificacao.getPontos());
		}
		ranking.addAllColocacao(novasPessoas);
		return ranking;
	}

	private Map<Pessoa, ExportedItemRanking> transformFromRanking(final Ranking ranking) {
		Map<Pessoa, ExportedItemRanking> classificacaoMap = new HashMap<Pessoa, ExportedItemRanking>();
		for (final ItemRanking itemRanking : ranking.getItemRankings()) {
			final ExportedItemRanking exportedtemRanking = new ExportedItemRanking(itemRanking);
			classificacaoMap.put(itemRanking.getPessoa(), exportedtemRanking);
		}
		return classificacaoMap;
	}

	private void updatePosition(final Map<Pessoa, ExportedItemRanking> sortedByCriteriaMap) {
		final Iterator<Entry<Pessoa, ExportedItemRanking>> iterator = sortedByCriteriaMap.entrySet().iterator();

		int posicao = 1;
		int pontosAnterior = -1;

		while (iterator.hasNext()) {
			final Entry<Pessoa, ExportedItemRanking> entry = iterator.next();
			final ExportedItemRanking value = entry.getValue();
			final int pontos = value.getPontos();
			value.setPosicao(posicao);
			if (pontosAnterior != pontos) {
				pontosAnterior = pontos;
				posicao++;
			}
			System.out.println(value);
		}
		System.out.println("");

	}

	private Map<Pessoa, ExportedItemRanking> getRankingMap(final Partida partida,
			final Map<Pessoa, ExportedItemRanking> rankingMap) {
		final Map<Pessoa, ExportedItemRanking> rankings;
		if (rankingMap != null) {
			rankings = rankingMap;
		} else {
			rankings = new HashMap<Pessoa, ExportedItemRanking>();
		}
		return rankingByPartida(partida, rankings);
	}

	private Map<Pessoa, ExportedItemRanking> rankingByPartida(final Partida partida,
			final Map<Pessoa, ExportedItemRanking> rankingMap) {
		final Set<ItemPartida> partidaPessoas = partida.getItemPartidas();

		for (final ItemPartida itemPartida : partidaPessoas) {
			final ExportedItemRanking rankingByPessoa = getRankingByPessoa(rankingMap, itemPartida);
			rankingByPessoa.update(itemPartida);
		}
		return rankingMap;
	}

	private ExportedItemRanking getRankingByPessoa(final Map<Pessoa, ExportedItemRanking> rankingMap,
			final ItemPartida itemPartida) {
		ExportedItemRanking classificacao = rankingMap.get(itemPartida.getPessoa());
		if (classificacao == null) {
			LOGGER.error("Não encontrou o nome " + itemPartida.getPessoa().getNome() + " no ranking atual. Ano "
					+ itemPartida.getPartida().getData());

			validator.validar(
					"Não encontrou o nome " + itemPartida.getPessoa().getNome()
							+ " no ranking atual que veio da partida do dia " + itemPartida.getPartida().getData(),
					String.valueOf(Dates.dateToLocalDate(itemPartida.getPartida().getData()).getYear()));
			classificacao = new ExportedItemRanking(itemPartida);
			rankingMap.put(itemPartida.getPessoa(), classificacao);
		}
		return classificacao;
	}

	public class RankingCriteriaFactory {

		public Map<Pessoa, ExportedItemRanking> create(final Map<Pessoa, ExportedItemRanking> exportedtemRanking,
				final RankingType rankingType) {
			switch (rankingType) {
			case PONTUACAO:
				return new TreeMap<Pessoa, ExportedItemRanking>(new RankingByPontuacaoComparator(exportedtemRanking));
			default:
				throw new IllegalArgumentException("Ranking type invalid");
			}
		}
	}

	public class RankingByPontuacaoComparator implements Comparator<Pessoa> {
		private final Map<Pessoa, ExportedItemRanking> base;

		public RankingByPontuacaoComparator(final Map<Pessoa, ExportedItemRanking> base) {
			this.base = base;
		}

		public int compare(final Pessoa a, final Pessoa b) {
			final ExportedItemRanking classificacaoJogador1 = base.get(a);
			final ExportedItemRanking classificacaoJogador2 = base.get(b);

			final Integer pontosJogador1 = Integer.valueOf(classificacaoJogador1.getPontos());
			final Integer pontosJogador2 = Integer.valueOf(classificacaoJogador2.getPontos());

			int compareTo = pontosJogador2.compareTo(pontosJogador1);
			if (compareTo == 0) {
				compareTo = classificacaoJogador2.getSaldo().compareTo(classificacaoJogador1.getSaldo());
				if (compareTo == 0) {
					compareTo = classificacaoJogador1.getCodigoPessoa()
							.compareTo(classificacaoJogador2.getCodigoPessoa());
					if (compareTo == 0) {
						compareTo = classificacaoJogador1.getNomePessoa()
								.compareTo(classificacaoJogador2.getNomePessoa());
					}
				}
			}
			return compareTo;
		}
	}

}
