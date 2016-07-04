package br.lopes.poker.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

@Entity
public class Ranking extends AbstractEntity<Integer> {

    private static final long serialVersionUID = -7229048611382540986L;

    @Column(nullable = false)
    private Integer ano;

    @Column(nullable = false)
    private Date dataAtualizacao;

    @OrderBy(value = "posicaoAtual")
    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            mappedBy = "ranking")
    private final Set<Colocacao> colocacoes = new HashSet<>();

    public Set<Colocacao> getColocacoes() {
        return colocacoes;
    }

    public Date getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(final Date data) {
        this.dataAtualizacao = data;
    }

    public void addColocacao(final Colocacao colocacao) {
        this.colocacoes.add(colocacao);
        colocacao.setRanking(this);
    }

    public void addAllColocacao(final Set<Colocacao> colocacoes) {
        final Stream<Colocacao> stream = colocacoes.stream();
        stream.forEach(consumer -> {
            consumer.setRanking(this);
            this.colocacoes.add(consumer);
        });
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

}
