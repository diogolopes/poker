package br.lopes.poker.repository;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import br.lopes.poker.domain.Partida;

public interface PartidaRepository extends JpaRepository<Partida, Integer> {

    Set<Partida> findByData(final LocalDate data);

}