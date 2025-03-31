package ec.gob.ambiente.rcoa.enums;

public enum TipoInformeOficioEnum {
	INFORME_INSPECCION(52),
	//INFORME_INSPECCION_OBSERVADO(53),
	//INFORME_TECNICO_NEGACION(54),
	INFORME_TECNICO(55),
	//OFICIO_NEGACION(56),
	OFICIO_PRONUNCIAMIENTO(57);
	
	private int id;

	private TipoInformeOficioEnum(int id) {
		this.id = id;
		
	}
	
	public int getId() {
		return id;
	}
}
