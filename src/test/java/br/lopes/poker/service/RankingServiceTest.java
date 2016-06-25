package br.lopes.poker.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Sets;

import br.lopes.poker.ServiceTestConfig;
import br.lopes.poker.builder.PartidaBuilder;
import br.lopes.poker.data.Classificacao;
import br.lopes.poker.domain.Partida;
import br.lopes.poker.domain.Pessoa;
import br.lopes.poker.faker.PartidaFaker.EPartida;
import br.lopes.poker.faker.PessoaFaker.EPessoa;
import br.lopes.poker.service.ClassificacaoService.RankingType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceTestConfig.class })
public class RankingServiceTest {

	@Autowired
	private ClassificacaoService rankingService;

	@Before
	public void setUp() {
		// Mockito.reset(partidaMock);
	}

	@Test
	public void deveGerarRankingPorSaldoCom4Jogadores() {

		final Partida partida = PartidaBuilder.get(EPartida.CASA_SONIA) //
				.withPartidaPessoa(EPessoa.EDUARDO, 2, 35) //
				.withPartidaPessoa(EPessoa.DIOGO, 1, 45) //
				.withPartidaPessoa(EPessoa.SONIA, 0, 10) //
				.withPartidaPessoa(EPessoa.FILIPE, 0, -10) //
				.build(); //

		// // specify mock behave when method called
		// when(partidaMock.findByData(EPartida.CASA_DA_SONIA.getData())).thenReturn(Sets.newHashSet(partida));

		final Map<Pessoa, Classificacao> rankingBySaldo = rankingService.ranking(partida, RankingType.SALDO);
		assertThat("Ranking está vazio", rankingBySaldo.isEmpty(), equalTo(false));

		final Iterator<Entry<Pessoa, Classificacao>> iterator = rankingBySaldo.entrySet().iterator();
		assertThat("1o colocado está errado", EPessoa.DIOGO.getNome(),
				equalTo(iterator.next().getValue().getPessoa().getNome()));
		assertThat("2o colocado está errado", EPessoa.EDUARDO.getNome(),
				equalTo(iterator.next().getValue().getPessoa().getNome()));
		assertThat("3o colocado está errado", EPessoa.SONIA.getNome(),
				equalTo(iterator.next().getValue().getPessoa().getNome()));
		assertThat("4o colocado está errado", EPessoa.FILIPE.getNome(),
				equalTo(iterator.next().getValue().getPessoa().getNome()));

	}

	@Test
	public void deveGerarRankingPorAproveitamentoCom4Jogadores() {

		final Partida partida = PartidaBuilder.get(EPartida.CASA_SONIA) //
				.withPartidaPessoa(EPessoa.EDUARDO, 2, 35) //
				.withPartidaPessoa(EPessoa.DIOGO, 1, 45) //
				.withPartidaPessoa(EPessoa.SONIA, 0, 10) //
				.withPartidaPessoa(EPessoa.FILIPE, 0, -10) //
				.build(); //

		// // specify mock behave when method called
		// when(partidaMock.findByData(EPartida.CASA_DA_SONIA.getData())).thenReturn(Sets.newHashSet(partida));

		final Map<Pessoa, Classificacao> rankingByAproveitamento = rankingService.ranking(partida,
				RankingType.APROVEITAMENTO);
		assertThat("Ranking está vazio", rankingByAproveitamento.isEmpty(), equalTo(false));

		final Iterator<Entry<Pessoa, Classificacao>> iterator = rankingByAproveitamento.entrySet().iterator();

		assertThat("1o colocado está errado", EPessoa.DIOGO.getNome(),
				equalTo(iterator.next().getValue().getPessoa().getNome()));
		assertThat("2o colocado está errado", EPessoa.EDUARDO.getNome(),
				equalTo(iterator.next().getValue().getPessoa().getNome()));
		assertThat("3o colocado está errado", EPessoa.SONIA.getNome(),
				equalTo(iterator.next().getValue().getPessoa().getNome()));
		assertThat("4o colocado está errado", EPessoa.FILIPE.getNome(),
				equalTo(iterator.next().getValue().getPessoa().getNome()));
	}

	@Test
	public void deveGerarRankingDe4PartidasDiferentesCom4JogadoresIguaisPorSaldo() {

		final Partida partidaCasaSonia = PartidaBuilder.get(EPartida.CASA_SONIA) //
				.withPartidaPessoa(EPessoa.EDUARDO, 2, 35) //
				.withPartidaPessoa(EPessoa.DIOGO, 1, 45) //
				.withPartidaPessoa(EPessoa.SONIA, 0, 10) //
				.withPartidaPessoa(EPessoa.FILIPE, 0, -10) //
				.build(); //

		final Partida partidaCasaFilipe = PartidaBuilder.get(EPartida.CASA_FILIPE) //
				.withPartidaPessoa(EPessoa.EDUARDO, 2, 35) //
				.withPartidaPessoa(EPessoa.DIOGO, 1, 45) //
				.withPartidaPessoa(EPessoa.SONIA, 0, 10) //
				.withPartidaPessoa(EPessoa.FILIPE, 0, -10) //
				.build(); //

		final Partida partidaCasaMaeda = PartidaBuilder.get(EPartida.CASA_MAEDA) //
				.withPartidaPessoa(EPessoa.EDUARDO, 2, 35) //
				.withPartidaPessoa(EPessoa.DIOGO, 1, 45) //
				.withPartidaPessoa(EPessoa.SONIA, 0, 10) //
				.withPartidaPessoa(EPessoa.FILIPE, 0, -10) //
				.build(); //

		final Partida partidaCasaMoacir = PartidaBuilder.get(EPartida.CASA_MOACIR) //
				.withPartidaPessoa(EPessoa.EDUARDO, 2, 35) //
				.withPartidaPessoa(EPessoa.DIOGO, 1, 45) //
				.withPartidaPessoa(EPessoa.SONIA, 0, 10) //
				.withPartidaPessoa(EPessoa.FILIPE, 0, -10) //
				.build(); //

		// // specify mock behave when method called
		// when(partidaMock.findByData(EPartida.CASA_DA_SONIA.getData())).thenReturn(Sets.newHashSet(partida));

		final Map<Pessoa, Classificacao> rankingBySaldo = rankingService.ranking(
				Sets.newHashSet(partidaCasaSonia, partidaCasaFilipe, partidaCasaMaeda, partidaCasaMoacir),
				RankingType.SALDO);
		assertThat("Qtde de jogadores no ranking errada", 4, equalTo(rankingBySaldo.size()));

		Iterator<Entry<Pessoa, Classificacao>> iterator = rankingBySaldo.entrySet().iterator();
		int i = 0;
		assertThat(i++ + "o colocado está errado", EPessoa.DIOGO.getNome(),
				equalTo(iterator.next().getValue().getPessoa().getNome()));
		assertThat(i++ + "o colocado está errado", EPessoa.EDUARDO.getNome(),
				equalTo(iterator.next().getValue().getPessoa().getNome()));
		assertThat(i++ + "o colocado está errado", EPessoa.SONIA.getNome(),
				equalTo(iterator.next().getValue().getPessoa().getNome()));
		assertThat(i++ + "o colocado está errado", EPessoa.FILIPE.getNome(),
				equalTo(iterator.next().getValue().getPessoa().getNome()));

		iterator = rankingBySaldo.entrySet().iterator();
		i = 1;
		assertThat(i++ + "o colocado está com saldo errado", iterator.next().getValue().getSaldo().longValue(),
				equalTo(Long.valueOf(45 * 4) + 4));
		assertThat(i++ + "o colocado está com saldo errado", iterator.next().getValue().getSaldo().longValue(),
				equalTo(Long.valueOf(35 * 4) + 8));
		assertThat(i++ + "o colocado está com saldo errado", iterator.next().getValue().getSaldo().longValue(),
				equalTo(Long.valueOf(10 * 4)));
		assertThat(i++ + "o colocado está com saldo errado", iterator.next().getValue().getSaldo().longValue(),
				equalTo(Long.valueOf(-10 * 4)));

	}

	@Test
	public void deveGerarRankingDe4PartidasDiferentesComJogadoresDiferentesPorSaldo() {

		final Partida partidaCasaSonia = PartidaBuilder.get(EPartida.CASA_SONIA) //
				.withPartidaPessoa(EPessoa.EDUARDO, 2, 35) //
				.withPartidaPessoa(EPessoa.DIOGO, 1, 45) //
				.withPartidaPessoa(EPessoa.SONIA, 0, 10) //
				.build(); //

		final Partida partidaCasaFilipe = PartidaBuilder.get(EPartida.CASA_FILIPE) //
				.withPartidaPessoa(EPessoa.EDUARDO, 0, 10) //
				.withPartidaPessoa(EPessoa.FILIPE, 0, 0) //
				.withPartidaPessoa(EPessoa.MAEDA, 0, 20) //
				.build(); //

		final Partida partidaCasaMaeda = PartidaBuilder.get(EPartida.CASA_MAEDA) //
				.withPartidaPessoa(EPessoa.MARCINHO, 2, 10) //
				.withPartidaPessoa(EPessoa.SONIA, 0, 20) //
				.build(); //

		final Partida partidaCasaMoacir = PartidaBuilder.get(EPartida.CASA_MOACIR) //
				.withPartidaPessoa(EPessoa.EDUARDO, 2, 10.5) //
				.withPartidaPessoa(EPessoa.DIOGO, 1, 0) //
				.withPartidaPessoa(EPessoa.SONIA, 0, 20) //
				.withPartidaPessoa(EPessoa.MAEDA, 0, 5) //
				.withPartidaPessoa(EPessoa.FILIPE, 0, -5) //
				.build(); //

		// // specify mock behave when method called
		// when(partidaMock.findByData(EPartida.CASA_DA_SONIA.getData())).thenReturn(Sets.newHashSet(partida));

		final Map<Pessoa, Classificacao> rankingBySaldo = rankingService.ranking(
				Sets.newHashSet(partidaCasaSonia, partidaCasaFilipe, partidaCasaMaeda, partidaCasaMoacir),
				RankingType.SALDO);
		assertThat("Qtde de jogadores no ranking errada", 6, equalTo(rankingBySaldo.size()));

		Iterator<Entry<Pessoa, Classificacao>> iterator = rankingBySaldo.entrySet().iterator();
		int i = 0;
		assertThat(i++ + "o colocado está errado", EPessoa.EDUARDO.getNome(),
				equalTo(iterator.next().getValue().getPessoa().getNome()));
		assertThat(i++ + "o colocado está errado", EPessoa.SONIA.getNome(),
				equalTo(iterator.next().getValue().getPessoa().getNome()));
		assertThat(i++ + "o colocado está errado", EPessoa.DIOGO.getNome(),
				equalTo(iterator.next().getValue().getPessoa().getNome()));
		assertThat(i++ + "o colocado está errado", EPessoa.MAEDA.getNome(),
				equalTo(iterator.next().getValue().getPessoa().getNome()));
		assertThat(i++ + "o colocado está errado", EPessoa.MARCINHO.getNome(),
				equalTo(iterator.next().getValue().getPessoa().getNome()));
		assertThat(i++ + "o colocado está errado", EPessoa.FILIPE.getNome(),
				equalTo(iterator.next().getValue().getPessoa().getNome()));

		iterator = rankingBySaldo.entrySet().iterator();
		i = 1;
		assertThat(i++ + "o colocado está com saldo errado", iterator.next().getValue().getSaldo().doubleValue(),
				equalTo(59.5));
		assertThat(i++ + "o colocado está com saldo errado", iterator.next().getValue().getSaldo().doubleValue(),
				equalTo(50.0));
		assertThat(i++ + "o colocado está com saldo errado", iterator.next().getValue().getSaldo().doubleValue(),
				equalTo(47.0));
		assertThat(i++ + "o colocado está com saldo errado", iterator.next().getValue().getSaldo().doubleValue(),
				equalTo(25.0));
		assertThat(i++ + "o colocado está com saldo errado", iterator.next().getValue().getSaldo().doubleValue(),
				equalTo(12.0));
		assertThat(i++ + "o colocado está com saldo errado", iterator.next().getValue().getSaldo().doubleValue(),
				equalTo(-5.0));
	}

	@Test
	public void deveGerarRankingDe4PartidasDiferentesCom4JogadoresIguaisPorAproveitamento() {

		final Partida partidaCasaSonia = PartidaBuilder.get(EPartida.CASA_SONIA) //
				.withPartidaPessoa(EPessoa.EDUARDO, 2, 35) //
				.withPartidaPessoa(EPessoa.DIOGO, 1, 45) //
				.withPartidaPessoa(EPessoa.SONIA, 0, 10) //
				.withPartidaPessoa(EPessoa.FILIPE, 0, -10) //
				.build(); //

		final Partida partidaCasaFilipe = PartidaBuilder.get(EPartida.CASA_FILIPE) //
				.withPartidaPessoa(EPessoa.EDUARDO, 2, 35) //
				.withPartidaPessoa(EPessoa.DIOGO, 1, 45) //
				.withPartidaPessoa(EPessoa.SONIA, 0, 10) //
				.withPartidaPessoa(EPessoa.FILIPE, 0, -10) //
				.build(); //

		final Partida partidaCasaMaeda = PartidaBuilder.get(EPartida.CASA_MAEDA) //
				.withPartidaPessoa(EPessoa.EDUARDO, 2, 35) //
				.withPartidaPessoa(EPessoa.DIOGO, 1, 45) //
				.withPartidaPessoa(EPessoa.SONIA, 0, 10) //
				.withPartidaPessoa(EPessoa.FILIPE, 0, -10) //
				.build(); //

		final Partida partidaCasaMoacir = PartidaBuilder.get(EPartida.CASA_MOACIR) //
				.withPartidaPessoa(EPessoa.EDUARDO, 2, 35) //
				.withPartidaPessoa(EPessoa.DIOGO, 1, 45) //
				.withPartidaPessoa(EPessoa.SONIA, 0, 10) //
				.withPartidaPessoa(EPessoa.FILIPE, 0, -10) //
				.build(); //

		// // specify mock behave when method called
		// when(partidaMock.findByData(EPartida.CASA_DA_SONIA.getData())).thenReturn(Sets.newHashSet(partida));

		final Map<Pessoa, Classificacao> rankingBySaldo = rankingService.ranking(
				Sets.newHashSet(partidaCasaSonia, partidaCasaFilipe, partidaCasaMaeda, partidaCasaMoacir),
				RankingType.APROVEITAMENTO);
		assertThat("Qtde de jogadores no ranking errada", 4, equalTo(rankingBySaldo.size()));

		Iterator<Entry<Pessoa, Classificacao>> iterator = rankingBySaldo.entrySet().iterator();
		int i = 0;
		assertThat(i++ + "o colocado está errado", EPessoa.DIOGO.getNome(),
				equalTo(iterator.next().getValue().getPessoa().getNome()));
		assertThat(i++ + "o colocado está errado", EPessoa.EDUARDO.getNome(),
				equalTo(iterator.next().getValue().getPessoa().getNome()));
		assertThat(i++ + "o colocado está errado", EPessoa.SONIA.getNome(),
				equalTo(iterator.next().getValue().getPessoa().getNome()));
		assertThat(i++ + "o colocado está errado", EPessoa.FILIPE.getNome(),
				equalTo(iterator.next().getValue().getPessoa().getNome()));

		iterator = rankingBySaldo.entrySet().iterator();
		i = 0;
		assertThat(i++ + "o colocado está com saldo errado", iterator.next().getValue().getSaldo().longValue(),
				equalTo(Long.valueOf(184)));
		assertThat(i++ + "o colocado está com saldo errado", iterator.next().getValue().getSaldo().longValue(),
				equalTo(Long.valueOf(148)));
		assertThat(i++ + "o colocado está com saldo errado", iterator.next().getValue().getSaldo().longValue(),
				equalTo(Long.valueOf(10 * 4)));
		assertThat(i++ + "o colocado está com saldo errado", iterator.next().getValue().getSaldo().longValue(),
				equalTo(Long.valueOf(-10 * 4)));

	}

	@Test
	public void deveGerarRankingDe4PartidasDiferentesComJogadoresDiferentesPorAproveitamento() {

		final Partida partidaCasaSonia = PartidaBuilder.get(EPartida.CASA_SONIA) //
				.withPartidaPessoa(EPessoa.EDUARDO, 2, 35) //
				.withPartidaPessoa(EPessoa.DIOGO, 1, 45) //
				.withPartidaPessoa(EPessoa.SONIA, 0, 10) //
				.build(); //

		final Partida partidaCasaFilipe = PartidaBuilder.get(EPartida.CASA_FILIPE) //
				.withPartidaPessoa(EPessoa.EDUARDO, 0, 10) //
				.withPartidaPessoa(EPessoa.FILIPE, 0, 0) //
				.withPartidaPessoa(EPessoa.MAEDA, 0, 20) //
				.build(); //

		final Partida partidaCasaMaeda = PartidaBuilder.get(EPartida.CASA_MAEDA) //
				.withPartidaPessoa(EPessoa.MARCINHO, 2, 10) //
				.withPartidaPessoa(EPessoa.SONIA, 0, 20) //
				.build(); //

		final Partida partidaCasaMoacir = PartidaBuilder.get(EPartida.CASA_MOACIR) //
				.withPartidaPessoa(EPessoa.EDUARDO, 2, 10.5) //
				.withPartidaPessoa(EPessoa.DIOGO, 1, 0) //
				.withPartidaPessoa(EPessoa.SONIA, 0, 20) //
				.withPartidaPessoa(EPessoa.MAEDA, 0, 5) //
				.withPartidaPessoa(EPessoa.FILIPE, 0, -5) //
				.build(); //

		// // specify mock behave when method called
		// when(partidaMock.findByData(EPartida.CASA_DA_SONIA.getData())).thenReturn(Sets.newHashSet(partida));

		final Map<Pessoa, Classificacao> rankingBySaldo = rankingService.ranking(
				Sets.newHashSet(partidaCasaSonia, partidaCasaFilipe, partidaCasaMaeda, partidaCasaMoacir),
				RankingType.APROVEITAMENTO);
		assertThat("Qtde de jogadores no ranking errada", 6, equalTo(rankingBySaldo.size()));

		Iterator<Entry<Pessoa, Classificacao>> iterator = rankingBySaldo.entrySet().iterator();

		// while (iterator.hasNext()) {
		// final Entry<Pessoa, Ranking> next = iterator.next();
		// final Ranking ranking = next.getValue();
		// System.out.println(ranking);
		// }
		// iterator = rankingBySaldo.entrySet().iterator();

		int i = 0;
		assertThat(i++ + "o colocado está errado", EPessoa.DIOGO.getNome(),
				equalTo(iterator.next().getValue().getPessoa().getNome()));
		assertThat(i++ + "o colocado está errado", EPessoa.EDUARDO.getNome(),
				equalTo(iterator.next().getValue().getPessoa().getNome()));
		assertThat(i++ + "o colocado está errado", EPessoa.SONIA.getNome(),
				equalTo(iterator.next().getValue().getPessoa().getNome()));
		assertThat(i++ + "o colocado está errado", EPessoa.MAEDA.getNome(),
				equalTo(iterator.next().getValue().getPessoa().getNome()));
		assertThat(i++ + "o colocado está errado", EPessoa.MARCINHO.getNome(),
				equalTo(iterator.next().getValue().getPessoa().getNome()));
		assertThat(i++ + "o colocado está errado", EPessoa.FILIPE.getNome(),
				equalTo(iterator.next().getValue().getPessoa().getNome()));

		iterator = rankingBySaldo.entrySet().iterator();
		i = 0;
		assertThat(i++ + "o colocado está com aproveitamento errado",
				iterator.next().getValue().getAproveitamento().doubleValue(), equalTo(23.5));
		assertThat(i++ + "o colocado está com aproveitamento errado",
				iterator.next().getValue().getAproveitamento().doubleValue(), equalTo(19.83333));
		assertThat(i++ + "o colocado está com aproveitamento errado",
				iterator.next().getValue().getAproveitamento().doubleValue(), equalTo(16.66667));
		assertThat(i++ + "o colocado está com aproveitamento errado",
				iterator.next().getValue().getAproveitamento().doubleValue(), equalTo(12.5));
		assertThat(i++ + "o colocado está com aproveitamento errado",
				iterator.next().getValue().getAproveitamento().doubleValue(), equalTo(12.0));
		assertThat(i++ + "o colocado está com aproveitamento errado", -2.5,
				equalTo(iterator.next().getValue().getAproveitamento().doubleValue()));
	}

}
