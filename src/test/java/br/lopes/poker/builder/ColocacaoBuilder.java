package br.lopes.poker.builder;

import java.math.BigDecimal;

import br.lopes.poker.domain.Colocacao;
import br.lopes.poker.faker.ColocacaoFaker;
import br.lopes.poker.faker.ColocacaoFaker.EColocacao;
import br.lopes.poker.faker.PessoaFaker;
import br.lopes.poker.faker.PessoaFaker.EPessoa;

public class ColocacaoBuilder {

	private final Colocacao colocacao;

	private ColocacaoBuilder(final EColocacao eColocacao) {
		this.colocacao = ColocacaoFaker.get(eColocacao);
	}

	public static ColocacaoBuilder get(final EColocacao eColocacao) {
		return new ColocacaoBuilder(eColocacao);
	}

	public ColocacaoBuilder withPessoa(final EPessoa ePessoa) {
		this.colocacao.setPessoa(PessoaFaker.get(ePessoa));
		return this;
	}

	public ColocacaoBuilder withSaldo(final double saldo) {
		this.colocacao.setSaldo(BigDecimal.valueOf(saldo));
		return this;
	}

	public ColocacaoBuilder withJogos(final int jogos) {
		this.colocacao.setJogos(jogos);
		return this;
	}

	public ColocacaoBuilder withVitoria(final int vitoria) {
		this.colocacao.setVitoria(vitoria);
		return this;
	}

	public ColocacaoBuilder withEmpate(final int empate) {
		this.colocacao.setEmpate(empate);
		return this;
	}

	public ColocacaoBuilder withDerrota(final int derrota) {
		this.colocacao.setDerrota(derrota);
		return this;
	}

	public ColocacaoBuilder withPosicaoAtual(final int posicaoAtual) {
		this.colocacao.setPosicaoAtual(posicaoAtual);
		return this;
	}

	public ColocacaoBuilder withPosicaoAnterior(final int posicaoAnterior) {
		this.colocacao.setPosicaoAnterior(posicaoAnterior);
		return this;
	}

	public Colocacao build() {
		return colocacao;
	}
}
