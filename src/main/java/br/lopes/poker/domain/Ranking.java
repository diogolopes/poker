package br.lopes.poker.domain;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public class Ranking extends AbstractEntity<Integer> {

	private static final long serialVersionUID = -7229048611382540986L;

	@Column(nullable = false)
	private LocalDate data;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "ranking")
	private final Set<Colocacao> colocacoes = new HashSet<>();

	public Set<Colocacao> getColocacoes() {
		return colocacoes;
	}

	public LocalDate getData() {
		return data;
	}

	public void setData(final LocalDate data) {
		this.data = data;
	}

	public void addColocacao(final Colocacao colocacao) {
		this.colocacoes.add(colocacao);
		colocacao.setRanking(this);

	}

}
