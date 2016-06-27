package br.lopes.poker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.lopes.poker.domain.Pessoa;

public interface PessoaRepository extends JpaRepository<Pessoa, Integer> {

    Pessoa findByNomeIgnoreCase(final String nome);

}
