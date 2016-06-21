package br.lopes.poker.data;

import java.math.BigDecimal;
import java.math.MathContext;

import br.lopes.poker.domain.Colocacao;
import br.lopes.poker.domain.PartidaPessoa;
import br.lopes.poker.domain.Pessoa;

public class Classificacao {
    private Pessoa pessoa;
    private BigDecimal saldo = BigDecimal.ZERO;
    private int jogos;
    private int vitoria;
    private int empate;
    private int derrota;
    private int posicaoAtual;
    private int posicaoAnterior;
    private int movimentacao;

    public Classificacao(final PartidaPessoa partidaPessoa) {
        this.pessoa = partidaPessoa.getPessoa();
    }

    public Classificacao(final Colocacao colocacao) {
        this.pessoa = colocacao.getPessoa();
        this.saldo = colocacao.getSaldo();
        this.jogos = colocacao.getJogos();
        this.vitoria = colocacao.getVitoria();
        this.empate = colocacao.getEmpate();
        this.derrota = colocacao.getDerrota();
        this.posicaoAtual = colocacao.getPosicaoAtual();
        this.posicaoAnterior = colocacao.getPosicaoAnterior();
        this.movimentacao = (this.posicaoAnterior != 0) ? (this.posicaoAnterior - this.posicaoAtual) : 0;
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

    public int getJogos() {
        return jogos;
    }

    public void setJogos(int jogos) {
        this.jogos = jogos;
    }

    public BigDecimal getAproveitamento() {
        return saldo.divide(BigDecimal.valueOf(jogos), MathContext.DECIMAL32);
    }

    public void update(final PartidaPessoa partidaPessoa) {
        this.saldo = saldo.add(partidaPessoa.getSaldo()).add(partidaPessoa.getBonus());
        this.jogos++;
        final int compareTo = saldo.compareTo(BigDecimal.ZERO);
        this.vitoria = (compareTo == 1) ? vitoria + 1 : vitoria;
        this.empate = (compareTo == 0) ? empate + 1 : empate;
        this.derrota = (compareTo == -1) ? derrota + 1 : derrota;

    }

    @Override
    public String toString() {
        return posicaoAtual + "o " + pessoa.getNome() + ": Movimentacao[" + movimentacao + "], Saldo[" + getSaldo() + ", Jogos[" + getJogos() + "], Aproveitamento[" + getAproveitamento()
                + "], Vitorias[" + getVitoria() + "], Derrotas[" + getDerrota() + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((pessoa == null) ? 0 : pessoa.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Classificacao other = (Classificacao) obj;
        if (pessoa == null) {
            if (other.pessoa != null)
                return false;
        } else if (!pessoa.equals(other.pessoa))
            return false;
        return true;
    }

    public void setPosicao(final int posicao) {
        this.posicaoAnterior = this.posicaoAtual;
        this.posicaoAtual = posicao;
        this.movimentacao = (this.posicaoAnterior != 0) ? (this.posicaoAnterior - this.posicaoAtual) : 0;
    }

    public int getPosicaoAtual() {
        return posicaoAtual;
    }

    public void setPosicaoAtual(int posicaoAtual) {
        this.posicaoAtual = posicaoAtual;
    }

    public int getPosicaoAnterior() {
        return posicaoAnterior;
    }

    public void setPosicaoAnterior(int posicaoAnterior) {
        this.posicaoAnterior = posicaoAnterior;
    }

    public int getMovimentacao() {
        return movimentacao;
    }

    public void setMovimentacao(int movimentacao) {
        this.movimentacao = movimentacao;
    }

}
