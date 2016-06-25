package br.lopes.poker.faker;

import java.time.LocalDate;
import java.util.Date;

import br.lopes.poker.domain.Partida;
import br.lopes.poker.helper.Dates;

public class PartidaFaker {

	public enum EPartida {
		CASA_MAEDA(LocalDate.of(2016, 1, 10), "Casa do Maeda"), //
		CASA_MOACIR(LocalDate.of(2016, 2, 12), "Casa do Moacir"), //
		CASA_SONIA(LocalDate.of(2016, 3, 14), "Casa do Sonia"), //
		CASA_FILIPE(LocalDate.of(2016, 4, 16), "Casa do Filipe");

		private final LocalDate data;
		private final String local;

		private EPartida(final LocalDate data, final String local) {
			this.data = data;
			this.local = local;
		}

		public Date getData() {
			return Dates.localDateToDate(data);
		}

	}

	public static Partida get(final EPartida ePartida) {
		final Partida partida = new Partida();
		partida.setData(ePartida.getData());
		partida.setLocal(ePartida.local);
		return partida;
	}
}
