package br.lopes.poker.repository;

import java.util.Date;
import java.util.Set;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;

import br.lopes.poker.domain.Partida;

public interface PartidaRepository extends JpaRepository<Partida, Integer> {

    Set<Partida> findByDataBetween(final Date begin, final Date end);

    @EntityGraph(value = "PartidaWithItemPartidas", type = EntityGraphType.LOAD)
    Partida findByData(final Date data);

}