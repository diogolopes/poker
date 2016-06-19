package br.lopes.poker.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public class Partida extends AbstractEntity<Integer> {

    private static final long serialVersionUID = -7229048611382540986L;

    @Column(nullable = false)
    private LocalDate data;
    private String local;

    @OneToMany(cascade = CascadeType.ALL,
            mappedBy = "partida")
    private final Set<PartidaPessoa> partidaPessoas = new HashSet<>();

    public LocalDate getData() {
        return data;
    }

    public void setData(final LocalDate data) {
        this.data = data;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(final String local) {
        this.local = local;
    }

    public Set<PartidaPessoa> getPartidaPessoas() {
        return partidaPessoas;
    }

    public void addPessoa(final PartidaPessoa... partidaPessoas) {
        for (final PartidaPessoa partidaPessoa : partidaPessoas) {
            this.partidaPessoas.add(partidaPessoa);
        }
    }

    public void addPessoa(final Pessoa pessoa, final int rebuys, final BigDecimal saldo) {
        final PartidaPessoa partidaPessoa = new PartidaPessoa();
        partidaPessoa.setPartida(this);
        partidaPessoa.setPessoa(pessoa);
        partidaPessoa.setRebuys(rebuys);
        partidaPessoa.setSaldo(saldo);

        this.partidaPessoas.add(partidaPessoa);
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