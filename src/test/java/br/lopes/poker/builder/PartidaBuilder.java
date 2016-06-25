package br.lopes.poker.builder;

import java.math.BigDecimal;
import java.util.Date;

import br.lopes.poker.domain.Partida;
import br.lopes.poker.faker.PartidaFaker;
import br.lopes.poker.faker.PartidaFaker.EPartida;
import br.lopes.poker.faker.PessoaFaker;
import br.lopes.poker.faker.PessoaFaker.EPessoa;

public class PartidaBuilder {

    private final Partida partida;

    private PartidaBuilder(final EPartida ePartida) {
        this.partida = PartidaFaker.get(ePartida);
    }

    public static PartidaBuilder get(final EPartida ePartida) {
        return new PartidaBuilder(ePartida);
    }

    public PartidaBuilder withData(final Date data) {
        this.partida.setData(data);
        return this;
    }

    public PartidaBuilder withLocal(final String local) {
        this.partida.setLocal(local);
        return this;
    }

    public PartidaBuilder withPartidaPessoa(final EPessoa ePessoa, final double saldo, final double bonus) {
        this.partida.addPessoa(PessoaFaker.get(ePessoa), new BigDecimal(saldo), new BigDecimal(bonus));
        return this;
    }

    public Partida build() {
        return partida;
    }
}
