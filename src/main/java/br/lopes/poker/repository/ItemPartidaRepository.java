package br.lopes.poker.repository;

import java.util.Date;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import br.lopes.poker.domain.ItemPartida;

public interface ItemPartidaRepository extends JpaRepository<ItemPartida, Integer> {

    Set<ItemPartida> findByPartidaData(final Date data);

}