package ec.gob.ambiente.rcoa.util;

public enum FormatoFechaEnum {
	
	DD_MM_YYYY("dd/MM/yyyy"), 
	YYYY_MM_DD("yyyy-MM-dd");

	private String formato;

	FormatoFechaEnum(String formato) {
		this.formato = formato;
	}
	
	@Override
	public String toString() {
		return formato;
	}

}
