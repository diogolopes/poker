package br.lopes.poker.domain;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(indexes = { //
        @Index(name = "idx_pessoa_nome", columnList = "nome"),
        @Index(name = "idx_pessoa_codigo", columnList = "codigo") } //
) //
public class Pessoa extends AbstractEntity<Integer> {

    private static final long serialVersionUID = -3519792787295875731L;

    @Column(nullable = false, unique = true, length = 200)
    private String nome;
    private Integer codigo;
    private String email;
    private String telefone;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pessoa")
    private final Set<ItemPartida> itemPartidas = new HashSet<>();

    public String getNome() {
        return nome;
    }

    public void setNome(final String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(final String telefone) {
        this.telefone = telefone;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(final Integer codigo) {
        this.codigo = codigo;
    }

    public Set<ItemPartida> getItemPartidas() {
        return itemPartidas;
    }

    public void addPartida(final ItemPartida... itemPartidas) {
        for (final ItemPartida itemPartida : itemPartidas) {
            this.itemPartidas.add(itemPartida);
        }
    }

    public void addPartida(final Partida partida, final BigDecimal saldo, final int pontos) {
        final ItemPartida itemPartida = new ItemPartida();
        itemPartida.setPartida(partida);
        itemPartida.setPessoa(this);
        itemPartida.setSaldo(saldo);
        itemPartida.setPontos(pontos);

        this.itemPartidas.add(itemPartida);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((nome == null) ? 0 : nome.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (this.getId() != null && ((Pessoa) obj).getId() != null && !super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        Pessoa other = (Pessoa) obj;
        if (nome == null) {
            if (other.nome != null)
                return false;
        } else if (!nome.equals(other.nome))
            return false;
        return true;
    }

}
