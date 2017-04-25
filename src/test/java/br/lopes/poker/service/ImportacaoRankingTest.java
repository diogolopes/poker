package br.lopes.poker.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import br.lopes.poker.ServiceTestConfig;
import br.lopes.poker.exception.PokerException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceTestConfig.class })
public class ImportacaoRankingTest {

    @Autowired
    private ImportRanking importRanking;

    @Test
    public void test() {
        try {
			importRanking.importRankings();
		} catch (PokerException e) {
			e.printStackTrace();
		}
    }
}
