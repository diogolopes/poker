package br.lopes.poker.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import br.lopes.poker.domain.Partida;

public interface PartidaService {

    Set<Partida> findByYear(final int year);

    List<Partida> save(final Collection<Partida> partidas);

    Partida findByData(final Date data);

    void delete(final Partida partida);

    List<Partida> findAll();

    Integer delete(final Date data);
}
