package br.lopes.poker.data;

import java.math.BigDecimal;

public class AcumuladorValor {
	private final BigDecimal saldoTotal;
	private final int pontoTotal;
	private BigDecimal saldoAcumulado = BigDecimal.ZERO;
	private int pontoAcumulado = 0;

	public AcumuladorValor(final BigDecimal saldoTotal, final int pontoTotal) {
		this.saldoTotal = saldoTotal;
		this.pontoTotal = pontoTotal;
	}

	public BigDecimal getSaldoAcumulado() {
		return saldoAcumulado;
	}

	public int getPontoAcumulado() {
		return pontoAcumulado;
	}

	public BigDecimal getSaldoTotal() {
		return saldoTotal;
	}

	public int getPontoTotal() {
		return pontoTotal;
	}

	public void addPontoAcumulado(final int pontoAcumulado) {
		this.pontoAcumulado = pontoAcumulado + this.pontoAcumulado;
	}

	public void addSaldoAcumulado(final BigDecimal value) {
		if (value != null) {
			this.saldoAcumulado = saldoAcumulado.add(value);
		}
	}

}