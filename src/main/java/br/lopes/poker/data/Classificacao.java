package br.lopes.poker.data;

import java.math.BigDecimal;

import br.lopes.poker.domain.PartidaPessoa;
import br.lopes.poker.domain.Pessoa;

public interface Classificacao {

	Pessoa getPessoa();

	BigDecimal getSaldo();

	int getVitoria();

	int getEmpate();

	int getDerrota();

	int getJogos();

	BigDecimal getAproveitamento();

	int getPosicaoAtual();

	int getPosicaoAnterior();

	int getMovimentacao();

	void setPosicao(int posicao);

	void update(final PartidaPessoa partidaPessoa);
}
