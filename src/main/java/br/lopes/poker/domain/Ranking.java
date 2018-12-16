package br.lopes.poker.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

@Entity
@NamedEntityGraph(name = "RankingWithItemRanking", attributeNodes = { @NamedAttributeNode(value = "itemRankings") })
public class Ranking extends AbstractEntity<Integer> {

	private static final long serialVersionUID = -7229048611382540986L;

	@Column(nullable = false)
	private Integer ano;

	@Column(nullable = false)
	private Date dataAtualizacao;

	@OrderBy(value = "posicaoAtual")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "ranking")
	private final Set<ItemRanking> itemRankings = new HashSet<>();

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private RankingType rankingType;

	public Set<ItemRanking> getItemRankings() {
		return itemRankings;
	}

	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(final Date data) {
		this.dataAtualizacao = data;
	}

	public void addColocacao(final ItemRanking itemRanking) {
		this.itemRankings.add(itemRanking);
		itemRanking.setRanking(this);
	}

	public void addAllColocacao(final Set<ItemRanking> itemRankings) {
		itemRankings.stream().forEach(consumer -> {
			consumer.setRanking(this);
			this.itemRankings.add(consumer);
		});
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(final Integer ano) {
		this.ano = ano;
	}

	public RankingType getRankingType() {
		return rankingType;
	}

	public void setRankingType(final RankingType rankingType) {
		this.rankingType = rankingType;
	}

}
