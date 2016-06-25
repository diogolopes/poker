package br.lopes.poker.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;

import br.lopes.poker.H2TestConfig;
import br.lopes.poker.builder.PartidaBuilder;
import br.lopes.poker.domain.Partida;
import br.lopes.poker.faker.PartidaFaker.EPartida;
import br.lopes.poker.helper.DDLValidator;
import br.lopes.poker.helper.Dates;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = H2TestConfig.class)
@Rollback
@Transactional(readOnly = false)
public class PartidaRepositoryTest {

	@Autowired
	private PartidaRepository repository;

	@Autowired
	private DDLValidator ddlValidator;

	private static final String TB_AREA = "Partida";
	private static final String[] TB_AREA_COLUMNS = new String[] { "id", "version", "data", "local" };
	private static final String[] TB_AREA_PKS = new String[] { "id" };
	private static final String[] TB_AREA_FKS = new String[] {};

	/**
	 * logic to verify the initial state before a transaction is started
	 */
	@BeforeTransaction
	public void asEntidadesDevemGerarTabelasIgualProducao() throws SQLException {
		ddlValidator.validateAll(TB_AREA, TB_AREA_COLUMNS, TB_AREA_PKS, TB_AREA_FKS);
	}

	/**
	 * set up test data within the transaction
	 */
	@Before
	public void beforeTests() {
		// table should be empty at this moment
		final long count = repository.count();

		assertThat("Base de dados nao esta vazia", 0l, equalTo(count));

		final Partida partida = PartidaBuilder.get(EPartida.CASA_MAEDA).build();

		// Manual flush is required to avoid false positive in test
		repository.saveAndFlush(partida);
		assertThat("Quantidade de registros dps do insert: ", count + 1, equalTo(repository.count()));
	}

	@Test
	public void findByDataTest() {
		final Partida partida = repository.findByData(EPartida.CASA_MAEDA.getData());
		assertThat("Objeto não encontrado", partida, notNullValue());
	}

	@Test
	public void findByDataBetweenTest() {
		final Set<Partida> partidas = repository.findByDataBetween(Dates.localDateToDate(LocalDate.of(2016, 1, 1)),
				Dates.localDateToDate(LocalDate.of(2016, 12, 31)));
		assertThat("Objeto não encontrado", partidas.size(), equalTo(1));
	}
}
