package br.lopes.poker;

import static org.mockito.Mockito.mock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import br.lopes.poker.repository.PartidaRepository;
import br.lopes.poker.repository.PessoaRepository;
import br.lopes.poker.repository.RankingRepository;

@Configuration
@ComponentScan(basePackages = "br.lopes.poker.service")
public class ServiceTestConfig {

    @Bean
    public PessoaRepository pessoaRepository() {
        return mock(PessoaRepository.class);
    }

    @Bean
    public PartidaRepository partidaRepository() {
        return mock(PartidaRepository.class);
    }

    @Bean
    public RankingRepository rankingRepository() {
        return mock(RankingRepository.class);
    }

}
