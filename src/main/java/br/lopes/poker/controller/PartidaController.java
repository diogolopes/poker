package br.lopes.poker.controller;

import static br.lopes.poker.helper.PokerEnvironment.JSON_CHARSET;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.lopes.poker.domain.Partida;
import br.lopes.poker.domain.ItemPartida;
import br.lopes.poker.service.ItemPartidaService;
import br.lopes.poker.service.PartidaService;
import br.lopes.poker.service.sheet.ImportPartida;

@RestController
@RequestMapping("/partidas")
public class PartidaController extends BaseController {

    private static final String PARAM_DATE = "data";
    private static final String DATE_PATTERN = "dd-MM-yyyy";
    private static final String URL_IMPORT = "/import";

    @Autowired
    private PartidaService partidaService;

    @Autowired
    private ImportPartida importPartida;

    @Autowired
    private ItemPartidaService partidaPessoaService;

    @RequestMapping(method = GET, produces = JSON_CHARSET)
    public ResponseEntity<List<Partida>> getPartidas() {

        List<Partida> partidas = partidaService.findAll();
        return createResponse(partidas);
    }

    @RequestMapping(method = GET, produces = JSON_CHARSET, params = PARAM_DATE)
    public ResponseEntity<Set<ItemPartida>> getPartidasByData(
            @RequestParam(PARAM_DATE) @DateTimeFormat(pattern = DATE_PATTERN) @NotBlank final Date data) {

        return createResponse(partidaPessoaService.findByPartidaData(data));
    }

    @DeleteMapping("/{dataPartida}")
    public ResponseEntity<String> remove(
            @PathVariable("dataPartida") @DateTimeFormat(pattern = DATE_PATTERN) @NotBlank final Date data) {

        if (null == partidaService.delete(data)) {
            return createResponse("No partida found for date " + data, HttpStatus.NOT_FOUND);
        }

        return createResponse("Deleted " + data, HttpStatus.OK);
    }

    @RequestMapping(value = URL_IMPORT, method = GET, produces = JSON_CHARSET)
    public ResponseEntity<?> importPartida() {
        try {
            final List<Partida> partidas = importPartida.importPartidas();
            return createResponse(partidas);

        } catch (final Exception exception) {
            return new ResponseEntity<>("Error in import", BAD_REQUEST);
        }
    }

}
