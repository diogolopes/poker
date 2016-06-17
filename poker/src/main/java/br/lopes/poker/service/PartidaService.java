package br.lopes.poker.service;

import java.util.Set;

import br.lopes.poker.domain.Partida;

public interface PartidaService {

    Set<Partida> findByYear(final int year);
}
