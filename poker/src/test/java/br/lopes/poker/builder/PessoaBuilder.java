package br.lopes.poker.builder;

import java.math.BigDecimal;

import br.lopes.poker.domain.Pessoa;
import br.lopes.poker.faker.PartidaFaker;
import br.lopes.poker.faker.PartidaFaker.EPartida;
import br.lopes.poker.faker.PessoaFaker;
import br.lopes.poker.faker.PessoaFaker.EPessoa;

public class PessoaBuilder {

    private final Pessoa pessoa;

    private PessoaBuilder(final EPessoa ePessoa) {
        this.pessoa = PessoaFaker.get(ePessoa);
    }

    public static PessoaBuilder get(final EPessoa ePessoa) {
        return new PessoaBuilder(ePessoa);
    }

    public PessoaBuilder withNome(final String nome) {
        pessoa.setNome(nome);
        return this;
    }

    public PessoaBuilder withTelefone(final String telefone) {
        pessoa.setTelefone(telefone);
        return this;
    }

    public PessoaBuilder withEmail(final String email) {
        pessoa.setEmail(email);
        return this;
    }

    public PessoaBuilder withPartidaPessoa(final EPartida ePartida, final int rebuys, final String saldo) {
        this.pessoa.addPartida(PartidaFaker.get(ePartida), rebuys, new BigDecimal(saldo));
        return this;
    }

    public Pessoa build() {
        return pessoa;
    }
}
