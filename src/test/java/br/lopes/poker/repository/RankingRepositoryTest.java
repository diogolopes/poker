package br.lopes.poker.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.sql.SQLException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Iterables;

import br.lopes.poker.H2TestConfig;
import br.lopes.poker.builder.RankingBuilder;
import br.lopes.poker.domain.Ranking;
import br.lopes.poker.faker.ColocacaoFaker.EColocacao;
import br.lopes.poker.faker.RankingFaker.ERanking;
import br.lopes.poker.helper.DDLValidator;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = H2TestConfig.class)
@Rollback
@Transactional(readOnly = false)
public class RankingRepositoryTest {

	@Autowired
	private RankingRepository repository;

	@Autowired
	private DDLValidator ddlValidator;

	private static final String TB_RANKING = "Ranking";
	private static final String[] TB_RANKING_COLUMNS = new String[] { "id", "version", "ano", "dataAtualizacao" };
	private static final String[] TB_RANKING_PKS = new String[] { "id" };
	private static final String[] TB_RANKING_FKS = new String[] {};

	private static final String TB_COLOCACAO = "Colocacao";
	private static final String[] TB_COLOCACAO_COLUMNS = new String[] { "id", "version", "rankingId", "pessoaId",
			"saldo", "jogos", "vitoria", "empate", "derrota", "posicaoAtual", "posicaoAnterior" };
	private static final String[] TB_COLOCACAO_PKS = new String[] { "id" };
	private static final String[] TB_COLOCACAO_FKS = new String[] { "ranking.id", "pessoa.id" };

	/**
	 * logic to verify the initial state before a transaction is started
	 */
	@BeforeTransaction
	public void asEntidadesDevemGerarTabelasIgualProducao() throws SQLException {
		ddlValidator.validateAll(TB_RANKING, TB_RANKING_COLUMNS, TB_RANKING_PKS, TB_RANKING_FKS);
		ddlValidator.validateAll(TB_COLOCACAO, TB_COLOCACAO_COLUMNS, TB_COLOCACAO_PKS, TB_COLOCACAO_FKS);
	}

	/**
	 * set up test data within the transaction
	 */
	@Before
	public void beforeTests() {
		// table should be empty at this moment
		final long count = repository.count();

		assertThat("Base de dados nao esta vazia", 0l, equalTo(count));

		final Ranking ranking = RankingBuilder.get(ERanking.MAIO)
				.withColocacao(EColocacao.PRIMEIRO, EColocacao.SEGUNDO, EColocacao.QUINTO).build();

		// Manual flush is required to avoid false positive in test
		repository.saveAndFlush(ranking);
		assertThat("Quantidade de registros dps do insert: ", count + 1, equalTo(repository.count()));
	}

	@Test
	public void testColocaoDeCadaUm() {
		final List<Ranking> findAll = repository.findAll();
		final Ranking ranking = Iterables.getOnlyElement(findAll);

		assertThat("Quantidade de registros dps do insert: ",
				Iterables.get(ranking.getColocacoes(), 0).getPessoa().getNome(),
				equalTo(EColocacao.PRIMEIRO.getNome()));
		assertThat("Quantidade de registros dps do insert: ",
				Iterables.get(ranking.getColocacoes(), 1).getPessoa().getNome(), equalTo(EColocacao.SEGUNDO.getNome()));
		assertThat("Quantidade de registros dps do insert: ",
				Iterables.get(ranking.getColocacoes(), 2).getPessoa().getNome(), equalTo(EColocacao.QUINTO.getNome()));

	}
}
