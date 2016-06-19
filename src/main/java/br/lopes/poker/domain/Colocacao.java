package br.lopes.poker.domain;

import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Colocacao extends AbstractEntity<Integer> {

	private static final long serialVersionUID = 5254083530813241581L;

	@ManyToOne(cascade = CascadeType.ALL, optional = false)
	@JoinColumn(name = "rankingId", nullable = false)
	private Ranking ranking;

	@ManyToOne(cascade = CascadeType.ALL, optional = false)
	@JoinColumn(unique = true, name = "pessoaId", nullable = false)
	private Pessoa pessoa;

	private BigDecimal saldo = BigDecimal.ZERO;
	private int jogos;
	private int vitoria;
	private int empate;
	private int derrota;
	private int posicaoAtual;
	private int posicaoAnterior;

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(final Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public BigDecimal getSaldo() {
		return saldo;
	}

	public void setSaldo(final BigDecimal saldo) {
		this.saldo = saldo;
	}

	public int getJogos() {
		return jogos;
	}

	public void setJogos(final int jogos) {
		this.jogos = jogos;
	}

	public int getVitoria() {
		return vitoria;
	}

	public void setVitoria(final int vitoria) {
		this.vitoria = vitoria;
	}

	public int getEmpate() {
		return empate;
	}

	public void setEmpate(final int empate) {
		this.empate = empate;
	}

	public int getDerrota() {
		return derrota;
	}

	public void setDerrota(final int derrota) {
		this.derrota = derrota;
	}

	public int getPosicaoAtual() {
		return posicaoAtual;
	}

	public void setPosicaoAtual(final int posicaoAtual) {
		this.posicaoAtual = posicaoAtual;
	}

	public int getPosicaoAnterior() {
		return posicaoAnterior;
	}

	public void setPosicaoAnterior(final int posicaoAnterior) {
		this.posicaoAnterior = posicaoAnterior;
	}

	public Ranking getRanking() {
		return ranking;
	}

	public void setRanking(Ranking ranking) {
		this.ranking = ranking;
	}

}
