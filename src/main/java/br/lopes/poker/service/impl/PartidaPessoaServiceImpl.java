package br.lopes.poker.service.impl;

import java.util.Date;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.lopes.poker.domain.PartidaPessoa;
import br.lopes.poker.repository.PartidaPessoaRepository;
import br.lopes.poker.service.PartidaPessoaService;

@Service
public class PartidaPessoaServiceImpl implements PartidaPessoaService {

    private final PartidaPessoaRepository repository;

    @Autowired
    public PartidaPessoaServiceImpl(final PartidaPessoaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Set<PartidaPessoa> findByPartidaData(Date data) {
        return repository.findByPartidaData(data);
    }

}
