package br.lopes.poker.faker;

import java.math.BigDecimal;

import br.lopes.poker.domain.Colocacao;
import br.lopes.poker.faker.PessoaFaker.EPessoa;

public class ColocacaoFaker {

	public enum EColocacao {
		PRIMEIRO(EPessoa.DIOGO, 50.0, 2, 2, 0, 0, 1, 1), //
		SEGUNDO(EPessoa.EDUARDO, 40.0, 2, 1, 0, 0, 2, 3), //
		TERCEIRO(EPessoa.SONIA, 30.0, 2, 0, 0, 0, 3, 2), //
		QUARTO(EPessoa.MARCINHO, 20.0, 2, 0, 1, 0, 4, 6), //
		QUINTO(EPessoa.FILIPE, 10.0, 2, 0, 1, 1, 5, 4), //
		SEXTO(EPessoa.MAEDA, 0.0, 2, 0, 0, 2, 6, 5);

		private final EPessoa ePessoa;
		private final double saldo;
		private final int jogos;
		private final int vitoria;
		private final int empate;
		private final int derrota;
		private final int posicaoAtual;
		private final int posicaoAnterior;

		private EColocacao(final EPessoa ePessoa, final double saldo, final int jogos, final int vitoria,
				final int empate, final int derrota, final int posicaoAtual, final int posicaoAnterior) {
			this.ePessoa = ePessoa;
			this.saldo = saldo;
			this.jogos = jogos;
			this.vitoria = vitoria;
			this.empate = empate;
			this.derrota = derrota;
			this.posicaoAtual = posicaoAtual;
			this.posicaoAnterior = posicaoAnterior;
		}

		public String getNome() {
			return ePessoa.getNome();
		}

	}

	public static Colocacao get(final EColocacao eColocacao) {
		final Colocacao colocacao = new Colocacao();
		colocacao.setPessoa(PessoaFaker.get(eColocacao.ePessoa));
		colocacao.setSaldo(BigDecimal.valueOf(eColocacao.saldo));
		colocacao.setJogos(eColocacao.jogos);
		colocacao.setVitoria(eColocacao.vitoria);
		colocacao.setEmpate(eColocacao.empate);
		colocacao.setDerrota(eColocacao.derrota);
		colocacao.setPosicaoAnterior(eColocacao.posicaoAtual);
		colocacao.setPosicaoAnterior(eColocacao.posicaoAnterior);
		return colocacao;
	}
}
