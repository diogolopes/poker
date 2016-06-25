package br.lopes.poker.domain;

import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class PartidaPessoa extends AbstractEntity<Integer> {

	private static final long serialVersionUID = -1349284861476161506L;

	@ManyToOne(cascade = CascadeType.ALL, optional = false)
	@JoinColumn(name = "partidaId", nullable = false)
	private Partida partida;

	// @ManyToOne(cascade = { CascadeType.MERGE, CascadeType.DETACH,
	// CascadeType.REFRESH, CascadeType.REMOVE },
	@ManyToOne(cascade = { CascadeType.ALL }, optional = false)
	@JoinColumn(name = "pessoaId", nullable = false)
	private Pessoa pessoa;

	private BigDecimal saldo;
	private BigDecimal bonus;

	public Partida getPartida() {
		return partida;
	}

	public void setPartida(final Partida partida) {
		this.partida = partida;
	}

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((partida == null) ? 0 : partida.hashCode());
		result = prime * result + ((pessoa == null) ? 0 : pessoa.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final PartidaPessoa other = (PartidaPessoa) obj;
		if (partida == null) {
			if (other.partida != null) {
				return false;
			}
		} else if (!partida.equals(other.partida)) {
			return false;
		}
		if (pessoa == null) {
			if (other.pessoa != null) {
				return false;
			}
		} else if (!pessoa.equals(other.pessoa)) {
			return false;
		}
		return true;
	}

	public BigDecimal getBonus() {
		return bonus;
	}

	public void setBonus(final BigDecimal bonus) {
		this.bonus = bonus;
	}

}
