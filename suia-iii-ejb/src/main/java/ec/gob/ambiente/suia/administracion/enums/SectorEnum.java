package ec.gob.ambiente.suia.administracion.enums;

public enum SectorEnum {
	GENERAL(0), AGRICULTURA_AGROINDUSTRIA(1), HIDROCARBUROS(2), MINERIA(3), ELECTRICIDAD_TELECOMUNICACIONES(
			4), SANEAMIENTO(5), OTROS_SECTORES(6), PESCA(7), PESCA_ACUACULTURA(
			8), PESCA_MARICULTURA(9);

	private Integer sectorId;

	private SectorEnum(Integer sectorId) {
		this.sectorId = sectorId;
	}

	public static String getNombreEstado(int estado) {
		if (GENERAL.sectorId != null && GENERAL.sectorId == estado) {
			return "General";
		} else if (AGRICULTURA_AGROINDUSTRIA.sectorId != null
				&& AGRICULTURA_AGROINDUSTRIA.sectorId == estado) {
			return "Agricultura y agroindustria";
		} else if (HIDROCARBUROS.sectorId != null
				&& HIDROCARBUROS.sectorId == estado) {
			return "Hidrocarburos";
		} else if (MINERIA.sectorId != null && MINERIA.sectorId == estado) {
			return "Mineria";
		} else if (ELECTRICIDAD_TELECOMUNICACIONES.sectorId != null
				&& ELECTRICIDAD_TELECOMUNICACIONES.sectorId == estado) {
			return "Electricidad y telecomunicaciones";
		} else if (SANEAMIENTO.sectorId != null && SANEAMIENTO.sectorId == estado) {
			return "Saneamiento";
		} else if (OTROS_SECTORES.sectorId != null
				&& OTROS_SECTORES.sectorId == estado) {
			return "Otros sectores";
		} else if (PESCA.sectorId != null && PESCA.sectorId == estado) {
			return "Pesca";
		} else if (PESCA_ACUACULTURA.sectorId != null
				&& PESCA_ACUACULTURA.sectorId == estado) {
			return "Pesca-acuacultura";
		} else if (PESCA_MARICULTURA.sectorId != null
				&& PESCA_MARICULTURA.sectorId == estado) {
			return "Pesca-maricultura";
		}
		return null;
	}
}
