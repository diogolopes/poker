package br.lopes.poker.service.impl;

import java.util.Date;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.lopes.poker.domain.ItemPartida;
import br.lopes.poker.repository.ItemPartidaRepository;
import br.lopes.poker.service.ItemPartidaService;

@Service
public class PartidaPessoaServiceImpl implements ItemPartidaService {

    private final ItemPartidaRepository repository;

    @Autowired
    public PartidaPessoaServiceImpl(final ItemPartidaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Set<ItemPartida> findByPartidaData(Date data) {
        return repository.findByPartidaData(data);
    }

}
