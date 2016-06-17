package br.lopes.poker.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.lopes.poker.repository.PartidaRepository;
import br.lopes.poker.service.PartidaService;

@Service
public class PartidaServiceImpl implements PartidaService {

    private final PartidaRepository repository;

    @Autowired
    public PartidaServiceImpl(final PartidaRepository repository) {
        this.repository = repository;
    }
}
