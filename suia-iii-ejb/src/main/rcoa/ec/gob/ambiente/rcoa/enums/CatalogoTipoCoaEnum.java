package ec.gob.ambiente.rcoa.enums;

public enum CatalogoTipoCoaEnum {
	RSQ_TIPO_LUGAR(1),
	RSQ_TIPO_RESPONSABLE(2),
	RSQ_TIPO_USUARIO(3),
	TIPO_IDENTIFICACION(4),
	RSQ_UNIDAD_MEDIDA(5),
	RSQ_INFORME_HALLAZGOS(7),
	RSQ_INFORME_REQUISITOS(8),
	TIPO_PRONUNCIAMIENTO(10),
	RSQ_TIPO_IDENTIFICACION_RESPONSABLE(11),
	COA_CAPAS_EXTERNAS_CONALI(13),
	
	DSQ_MESES(20),
	DSQ_TIPO_TRANSACCION(22),
	DSQ_TIPO_PRESENTACION(23),
	DSQ_ESTADOS_DECLARACION(26),
	DSQ_VALOR_TASA(53),
	DSQ_DIAS_DECLARACION(62),
	
	RCOA_LA_RESOLUCION(64),
	RCOA_LA_MEMORANDO(65),
	RCOA_LA_PRONUNCIAMIENTO(66),
	RCOA_LA_MEMORANDO_JURIDICO(67),
	
	PPC_TRATAMIENTO_DOCUMENTOS(19),
	PPC_FIRMA_RESPONSABILIDAD_PLANIFICACION(123),
	PPC_INFORME_TECNICO_PLANIFICACION_OBSERVADO(124),
	PPC_OFICIO_PRONUNCIAMIENTO_PLANIFICACION_OBSERVADO(125),
	PPC_INFORME_TECNICO_PLANIFICACION_RESPUESTA_OBSERVACIONES_NO_SOLVENTADAS(126),
	PPC_INFORME_TECNICO_PLANIFICACION_APROBADO(127),
	PPC_INFORME_TECNICO_PLANIFICACION_RESPUESTA_OBSERVACIONES_SOLVENTADAS(128),
	PPC_OFICIO_PRONUNCIAMIENTO_PLANIFICACION_APROBADO(129),
	PPC_FIRMA_DE_RESPONSABILIDAD_SISTEMATIZACION(130),
	PPC_INFORME_TECNICO_SISTEMATIZACION_OBSERVADO(131),
	PPC_OFICIO_PRONUNCIAMIENTO_SISTEMATIZACION_OBSERVADO(132),
	PPC_INFORME_TECNICO_SISTEMATIZACION_RESPUESTA_OBSERVACIONES_NO_SOLVENTADAS(133),
	PPC_INFORME_TECNICO_REVISION_INFORME_SISTEMATIZACION(134),
	PPC_INFORME_TECNICO_ACCIONES_COMPLEMENTARIAS(135),
	PPC_OFICIO_PRONUNCIAMIENTO_ACCIONES_COMPLEMENTARIAS(136),
	PPC_FIRMA_DE_RESPONSABILIDAD_ACCIONES_COMPLEMENTARIAS(137),
	PPC_INFORME_TECNICO_INFORME_SISTEMATIZACION_OBSERVADO(138),
	PPC_INFORME_TECNICO_EVALUACION_ACCIONES_COMPLEMENTARIAS(139),
	PPC_OFICIO_PRONUNCIAMIENTO_ACCIONES_COMPLEMENTARIAS_OBSERVADO(140),
	PPC_INFORME_TECNICO_ACCIONES_COMPLEMENTARIAS_RESPUESTA_OBSERVACIONES_NO_SOLVENTADAS(141),
	PPC_INFORME_TECNICO_EVALUACION_ACCIONES_COMPLEMENTARIAS_APR(142),
	PPC_OFICIO_PRONUNCIAMIENTO_NO_CUMPLE_TRABAJO_OPERADOR(143),
	PPC_INFORME_TECNICO_OPINIONES_OBSERVACIONES_ECONOMICAMENTE_VIABLES(144),
	PPC_OFICIO_PRONUNCIAMIENTO_APROBACION_PPC(145),
	
	TIPO_VALOR_IVA(28),
	
	IF_METODO_RECOLECCION_DATOS(30),
	
	TIPO_VALOR_KUSHKI(38),
	
	VAF_APENDICE_CITES(56),
	TIPO_FASE_VIABILIDAD(54),
	VAF_ESTADO_CONSERVACION_UICN(55),
	TIPO_REGIMEN_MINERO(61),
	PQUA_TIPO_PRODUCTO(63),
	PQUA_TIPO_RECIPIENTE(64),
	PQUA_COLOR_FRANJA(65),
	PQUA_TIPO_CATEGORIA(66),
	PQUA_UNIDADES(79),
	PQUA_TIPO_CULTIVO(103),
	PQUA_FAMILIA_CULTIVO(104),
	PQUA_GREMIO(105);

	
	private int id;

	private CatalogoTipoCoaEnum(int id) {
		this.id = id;
		
	}
	
	public int getId() {
		return id;
	}
}
