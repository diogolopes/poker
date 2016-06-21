package br.lopes.poker.data;

import java.math.BigDecimal;

public class Saldo {
    private BigDecimal saldoAcumulado = BigDecimal.ZERO;
    private BigDecimal subTotalLancado = BigDecimal.ZERO;
    private BigDecimal bonusLancado = BigDecimal.ZERO;
    private BigDecimal totalLancado = BigDecimal.ZERO;

    public BigDecimal getSaldoAcumulado() {
        return saldoAcumulado;
    }

    public BigDecimal getSubTotalLancado() {
        return subTotalLancado;
    }

    public BigDecimal getBonusLancado() {
        return bonusLancado;
    }

    public BigDecimal getTotalLancado() {
        return totalLancado;
    }

    public void setSubTotalLancado(final BigDecimal value) {
        if (value != null) {
            this.subTotalLancado = subTotalLancado.add(value);
        }
    }

    public void setBonusLancado(final BigDecimal value) {
        if (value != null) {
            this.bonusLancado = bonusLancado.add(value);
        }
    }

    public void setTotalLancado(final BigDecimal value) {
        if (value != null) {
            this.totalLancado = totalLancado.add(value);
        }
    }

    public void addSaldoAcumulado(final BigDecimal value) {
        if (value != null) {
            this.saldoAcumulado = saldoAcumulado.add(value);
        }
    }

}