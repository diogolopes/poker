package br.lopes.poker.service;

import java.util.Date;
import java.util.Set;

import br.lopes.poker.domain.ItemPartida;

public interface ItemPartidaService {

    Set<ItemPartida> findByPartidaData(final Date data);
}
