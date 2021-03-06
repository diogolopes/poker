package br.lopes.poker.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@NamedEntityGraph(name = "PartidaWithItemPartidas", attributeNodes = { @NamedAttributeNode(value = "itemPartidas") })
public class Partida extends AbstractEntity<Integer> {

    private static final long serialVersionUID = -7229048611382540986L;

    @Column(nullable = false, unique = true)
    private Date data;
    private String local;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "partida", orphanRemoval = true)
    private final Set<ItemPartida> itemPartidas = new HashSet<ItemPartida>();

    public Date getData() {
        return data;
    }

    public void setData(final Date data) {
        this.data = data;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(final String local) {
        this.local = local;
    }

    public Set<ItemPartida> getItemPartidas() {
        return itemPartidas;
    }

    public void addPessoa(final ItemPartida... partidaPessoas) {
        for (final ItemPartida partidaPessoa : partidaPessoas) {
            this.itemPartidas.add(partidaPessoa);
        }
    }

    public void addPessoa(final Pessoa pessoa, final BigDecimal saldo, int pontuacao) {
        final ItemPartida itemPartida = new ItemPartida();
        itemPartida.setPartida(this);
        itemPartida.setPessoa(pessoa);
        itemPartida.setSaldo(saldo);
        itemPartida.setPontos(pontuacao);

        this.itemPartidas.add(itemPartida);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((data == null) ? 0 : data.hashCode());
        result = prime * result + ((local == null) ? 0 : local.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        Partida other = (Partida) obj;
        if (data == null) {
            if (other.data != null)
                return false;
        } else if (!data.equals(other.data))
            return false;
        if (local == null) {
            if (other.local != null)
                return false;
        } else if (!local.equals(other.local))
            return false;
        return true;
    }

}
