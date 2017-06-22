package br.lopes.poker.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;

import br.lopes.poker.H2TestConfig;
import br.lopes.poker.builder.PessoaBuilder;
import br.lopes.poker.domain.Pessoa;
import br.lopes.poker.faker.PartidaFaker.EPartida;
import br.lopes.poker.faker.PessoaFaker.EPessoa;
import br.lopes.poker.helper.DDLValidator;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = H2TestConfig.class)
@Rollback
@Transactional(readOnly = false)
public class PessoaRepositoryTest {

    @Autowired
    private PessoaRepository repository;

    @Autowired
    private DDLValidator ddlValidator;

    private static final String TB_AREA = "Pessoa";
    private static final String[] TB_AREA_COLUMNS = new String[] { "id", "version", "nome", "email", "telefone" };
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

        final Pessoa pessoa = PessoaBuilder.get(EPessoa.DIOGO).withPartidaPessoa(EPartida.CASA_SONIA, 35, 1).build();

        // Manual flush is required to avoid false positive in test
        repository.saveAndFlush(pessoa);
        assertThat("Quantidade de registros dps do insert: ", count + 1, equalTo(repository.count()));
    }

    @Test
    public void findByNomeTest() {
        final Pessoa pessoa = repository.findByNomeIgnoreCase(EPessoa.DIOGO.getNome());
        assertThat("Objeto não encontrado", pessoa, notNullValue());
    }

}
