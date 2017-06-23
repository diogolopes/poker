package br.lopes.poker.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.lopes.poker.config.EnvironmentProperties;

@Component
public class PokerPaths {

    @Autowired
    private EnvironmentProperties environment;

    private static final String POKER_PARTIDA_FOLDER = "/partidas";
    private static final String POKER_PARTIDA_BACKUP_FOLDER = POKER_PARTIDA_FOLDER + "/backup";
    private static final String POKER_RANKING_FOLDER = "/ranking";
    private static final String POKER_RANKING_GERADO_FOLDER = POKER_RANKING_FOLDER + "/gerado";
    private static final String POKER_RANKING_ENTRADA_FOLDER = "/ranking/entrada";
    private static final String POKER_RANKING_BACKUP_FOLDER = "/ranking/backup";

    public static final String POKER_PARTIDA_FILE = "Partida (PDS)";
    public static final String POKER_RANKING_FILE = "Ranking (PDS)";

    private String getDatasourcePath() {
        return environment.getDatasourcePath();
    }

    public String getRankingEntradaFolder() {
        return getDatasourcePath() + POKER_RANKING_ENTRADA_FOLDER;
    }

    public String getRankingGeradoFolder() {
        return getDatasourcePath() + POKER_RANKING_GERADO_FOLDER;
    }

    public String getRankingBackupFolder() {
        return getDatasourcePath() + POKER_RANKING_BACKUP_FOLDER;
    }

    public String getPartidaFolder() {
        return getDatasourcePath() + POKER_PARTIDA_FOLDER;
    }

    public String getPartidaBackupFolder() {
        return getDatasourcePath() + POKER_PARTIDA_BACKUP_FOLDER;
    }
}
