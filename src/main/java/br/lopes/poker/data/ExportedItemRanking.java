package br.lopes.poker.data;

import java.math.BigDecimal;

import br.lopes.poker.domain.ItemPartida;
import br.lopes.poker.domain.ItemRanking;
import br.lopes.poker.domain.Pessoa;

public class ExportedItemRanking {
	private final ItemRanking itemRanking;
	private final Pessoa pessoa;
	private int movimentacao;
	private int posicaoAnterior;
	private int posicaoAtual;

	public ExportedItemRanking(final ItemRanking itemRanking) {
		this.itemRanking = itemRanking;
		this.pessoa = itemRanking.getPessoa();
		this.movimentacao = (itemRanking.getPosicaoAnterior() != 0)
				? (itemRanking.getPosicaoAnterior() - itemRanking.getPosicaoAtual())
				: 0;
	}

	public ExportedItemRanking(final ItemPartida itemPartida) {
		this.itemRanking = new ItemRanking();
		this.pessoa = itemPartida.getPessoa();

	}

	public void setPosicao(final int posicao) {
		this.movimentacao = (itemRanking.getPosicaoAnterior() != 0) ? (itemRanking.getPosicaoAnterior() - posicao) : 0;
		this.posicaoAnterior = this.posicaoAtual;
		this.posicaoAtual = posicao;
	}

	public int getMovimentacao() {
		return movimentacao;
	}

	public int getPosicaoAtual() {
		return posicaoAtual;
	}

	public int getPosicaoAnterior() {
		return posicaoAnterior;
	}

	public int getPontos() {
		return itemRanking.getPontos();
	}

	public BigDecimal getSaldo() {
		return itemRanking.getSaldo();
	}

	public Integer getCodigoPessoa() {
		return pessoa.getCodigo();
	}

	public String getNomePessoa() {
		return pessoa.getNome();
	}

	public int getJogos() {
		return itemRanking.getJogos();
	}

	public void update(final ItemPartida itemPartida) {
		itemRanking.setSaldo(itemRanking.getSaldo().add(itemPartida.getSaldo()));
		itemRanking.setJogos(getJogos() + 1);
		itemRanking.setPontos(getPontos() + itemPartida.getPontos());
	}

	@Override
	public String toString() {
		return posicaoAtual + "o " + pessoa.getNome() + ": Movimentacao[" + movimentacao + "], Pontos[" + getPontos()
				+ "], Saldo[" + getSaldo().setScale(2) + "], Jogos[" + getJogos() + "]";
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
		ExportedItemRanking other = (ExportedItemRanking) obj;
		if (pessoa == null) {
			if (other.pessoa != null)
				return false;
		} else if (!pessoa.equals(other.pessoa))
			return false;
		return true;
	}

	public int getPosicao() {
		return itemRanking.getPosicaoAtual();
	}

}
