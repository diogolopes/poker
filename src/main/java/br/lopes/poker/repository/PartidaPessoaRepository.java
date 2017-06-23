package br.lopes.poker.repository;

import java.util.Date;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import br.lopes.poker.domain.PartidaPessoa;

public interface PartidaPessoaRepository extends JpaRepository<PartidaPessoa, Integer> {

    Set<PartidaPessoa> findByPartidaData(final Date data);

}