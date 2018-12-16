package br.lopes.poker.service.sheet;

import java.util.List;

import br.lopes.poker.domain.Partida;
import br.lopes.poker.exception.PokerException;

public interface ImportPartida {

    List<Partida> importPartidas() throws PokerException;

}
