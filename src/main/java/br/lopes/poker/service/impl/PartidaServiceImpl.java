package br.lopes.poker.service.impl;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.lopes.poker.domain.Partida;
import br.lopes.poker.helper.Dates;
import br.lopes.poker.repository.PartidaRepository;
import br.lopes.poker.service.PartidaService;

@Service
public class PartidaServiceImpl implements PartidaService {

    private final PartidaRepository repository;

    @Autowired
    public PartidaServiceImpl(final PartidaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Set<Partida> findByYear(int year) {
        return repository.findByDataBetween(Dates.localDateToDate(LocalDate.of(year, 1, 1)),
                Dates.localDateToDate(LocalDate.of(year, 12, 31)));
    }

    @Override
    public List<Partida> save(final Collection<Partida> partidas) {
        return repository.save(partidas);
    }

    @Override
    public Partida findByData(final Date data) {
        return repository.findByData(data);
    }

    @Override
    public void delete(final Partida partida) {
        repository.delete(partida);
        repository.flush();
    }

    @Override
    public List<Partida> findAll() {
        return repository.findAll();
    }

    @Override
    public Integer delete(final Date data) {
        final Partida partida = repository.findByData(data);
        if (partida != null) {
            repository.delete(partida);
            return partida.getId();
        }
        return null;
    }

}
