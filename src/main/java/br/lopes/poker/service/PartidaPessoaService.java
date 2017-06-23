package br.lopes.poker.service;

import java.util.Date;
import java.util.Set;

import br.lopes.poker.domain.PartidaPessoa;

public interface PartidaPessoaService {

    Set<PartidaPessoa> findByPartidaData(final Date data);
}
