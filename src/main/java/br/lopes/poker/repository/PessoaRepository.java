package br.lopes.poker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.lopes.poker.domain.Pessoa;

public interface PessoaRepository extends JpaRepository<Pessoa, Integer> {

    Pessoa findByNomeIgnoreCase(final String nome);
    
    Pessoa findByCodigo(final Integer codigo);

    @Query("select coalesce(max(p.codigo), '0') from Pessoa p")
    Integer getMaxCodigo();
}
