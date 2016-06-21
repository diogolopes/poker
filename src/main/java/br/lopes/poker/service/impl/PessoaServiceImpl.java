package br.lopes.poker.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.lopes.poker.domain.Pessoa;
import br.lopes.poker.repository.PessoaRepository;
import br.lopes.poker.service.PessoaService;

@Service
public class PessoaServiceImpl implements PessoaService {

    private final PessoaRepository repository;

    @Autowired
    public PessoaServiceImpl(final PessoaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Pessoa findByNome(final String nome) {
        return repository.findByNome(nome);
    }

    @Override
    public Pessoa save(final Pessoa pessoa) {
        return repository.save(pessoa);
    }
}
