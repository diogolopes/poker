package br.lopes.poker.service;

import br.lopes.poker.domain.Pessoa;

public interface PessoaService {

    Pessoa findByNome(final String nome);

    Pessoa save(final Pessoa pessoa);
}
