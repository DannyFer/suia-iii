package ec.gob.ambiente.suia.domain.enums;

import lombok.Getter;

/**
 * @author carlos.pupo
 *         Almacena los tipos de documentos
 */
public enum TipoDocumentoSistema {

	TIPO_CONSECION_CAMARONERA(3005),
	TIPO_DOCUMENTO_ANEXOS(1),
    TIPO_CERTIFICADO_INTERSECCION_MAPA(3),
    TIPO_CERTIFICADO_INTERSECCION_OFICIO(4),
    TIPO_CERTIFICADO_VIABILIDAD(5),
    TIPO_DOCUMENTO_MAPA(16),
    TIPO_CERTIFICADO_CATEGORIA_UNO(8),
    TIPO_CONTRATO_OPERACION(9),
    TIPO_REGISTRO_MINERO(10),
    TIPO_COORDENADAS(12),
    TIPO_OFICIO_SOLICITAR_PRONUNCIAMIENTO(1000),
    TIPO_INFORME_TECNICO_EIA(1001),
    TIPO_OFICIO_APROBACION_EIA(1004),
    SIGLAS_ABREVIATURAS(300),
    INVENTARIO_FORESTAL(301),
    PLAN_DE_ACCION(302),
    DETERMINACION_AREA_INFLUENCIA(303),
    IDENTIFICACION_HALLAZGOS(304),
    MEDIO_FISICO(305),
    MEDIO_BIOTICO(306),
    LINEA_BASE(307),
    INVENTARIO_FORESTAL_GEN(308),
    DETERMINACION_AREA_INFLUENCIA_GEN(309),
    PLAN_MANEJO_AMBIENTAL(310),
    PLAN_MONITOREO(311),
    CRONOGRAMA(312),
    ANALISIS_RIESGOS_GEN(313),
    DESCRIPCION_PROYECTO(314),
    TIPO_INFORME_TECNICO_GENERAL_LA(1002),
    FICHA_TECNICA_GEN(315),
    DOCUMENTO_DEL_PROPONENTE_EIA(316),
    TIPO_OFICIO_OBSERVACION_EIA(1005),
    TIPO_LICENCIA_AMBIENTAL(1007),
    ADJUNTO_MATERIALES_HERRAMIENTAS_INSUMOS(1008),
    PRONUNCIAMIENTO_MINISTERIO_JUSTICIA(1003),
    PREGUNTAS_RESPUESTAS_INTERSECCION(1009),
    //PARA  PROYECTO HIDROCARBUROS PDF
    ANALISIS_ALTERNATIVAS(1015),
    TIPO_DOCUMENTO_GLOSARIO_TERMINOS(1016),
    TIPO_DOCUMENTO_REFERENCIAS_BIBLIOGRAFIA(1017),


    ADJUNTO_TDR(1056),
    ADJUNTO_OFICIO_OG(1057),

    TIPO_INFORME_TECNICO_TDR_LA(1058),
    TIPO_OFICIO_OBSERVACION_TDR_LA(1059),
    TIPO_OFICIO_APROBACION_TDR_LA(1060),

    TIPO_CERTIFICADO_USO_SUELOS(2000),
    TIPO_CERTIFICADO_COMPATIBILIDAD_USO_SUELOS(2001),
    TIPO_MODELO_ETIQUETA(2002),
    TIPO_PERMISO_AMBIENTAL(2010),

    TIPO_INFORME_TECNICO_RGD(2500),
    TIPO_INFORME_TECNICO_PMA(3000),

    OFICIO_PRONUNCIAMIENTO_OBSERVACIONES(3011),
    OFICIO_PRONUNCIAMIENTO_APROBACION(3010),

    TIPO_OFICIO_EMISION_RGD(2501),
    TIPO_OFICIO_OBSERVACION_RGD(2503),
    TIPO_BORRADOR_RGD(2504),
    TIPO_BORRADOR_RGD_RESPONSABILIDAD_EXTENDIDA_SI(4129), 
    TIPO_BORRADOR_NORMATIVA_DESCHOS_PROVINCIALES_RGD(2505),
    TIPO_BORRADOR_NORMATIVA_NEUMATICOS_USADOS_RGD(2506),
    TIPO_BORRADOR_NORMATIVA_PILAS_USADAS_RGD(2507),
    TIPO_BORRADOR_NORMATIVA_PLASTICOS_AGRICOLAS_RGD(2508),
    TIPO_BORRADOR_NORMATIVA_CELULARES_DESUSO_RGD(2509),
    TIPO_JUSTIFICACION_PROPONENTE_RGD(2517),
    TIPO_OFICIO_EMISION_RGD_REP(2520),
    TIPO_OFICIO_OBSERVACION_RGD_REP(2521),

    TIPO_INFORME_TECNICO_ART(1006),
    TIPO_OFICIO_APROBACION_ART(1501),
    TIPO_OFICIO_OBSERVACION_ART(1502),

    TIPO_INFORME_PROVINCIAL_GAD(17),
    TIPO_MINIRIA_MUNICIPAL_GAD(224),
    TIPO_INFORME_PROVINCIAL_GAD_SINMI(20),
    TIPO_SINMI_MUNICIPAL_GAD(225),

    TABLA_DE_VOLUMEN(1010),
    INDICE_DE_VALOR_IMPORTANCIA(1011),
    PARTICIPACION_SOCIAL(999),

    TIPO_DOCUMENTO_FICHA_MINERIA(7),
    TIPO_DOCUMENTO_REGISTRO_AMBIENTAL(6),
    TIPO_DOCUMENTO_RESOLUCION_REGISTRO_AMBIENTAL(13), INFORME_INSPECCION_VIABILIDAD_BIODIVERSIDAD(1012),
    INFORME_INSPECCION_VIABILIDAD_FORESTAL(1013),
    PREGUNTAS_RESPUESTAS_INTERSECCION_SNAP(1014),


    ANEXO_FOTOGRAFICO_PUNTOS_RUIDO(3009),
    INFORME_PLAN_EMERGENTE_TECNICO(3001),

    // VIABILIDAD TECNICA
    VIAB_TECN_DCT_MANEJO_ESCORENTIA_SUPERFICIAL(3014),
    VIAB_TECN_DCT_MANEJO_EROSION_SEDIMENTACION(3015),
    VIAB_TECN_DCT_MANEJO_LIXIVIADOS(3016),
    VIAB_TECN_DCT_MANEJO_BIOGAS(3017),
    VIAB_TECN_DCT_ESTABILIDAD_CIERRE_TECNICO(3018),
    VIAB_TECN_DCT_DISENO_CAPA_COBERTURA(3019),
    VIAB_TECN_DCT_OBRAS_COMPLEMENTARIAS(3020),
    VIAB_TECN_DCT_USO_FINAL_SUELO(3021),
    VIAB_TECN_DCT_DISENO_CIERRE_TECNICO(3022),
    VIAB_TECN_BDCE_BASE_DISENO_CELDA_EMERGENTE(3023),
    VIAB_TECN_DDCE_DISENO_DEFINITIVO_CELDA_EMERGENTE(3024),
    VIAB_TECN_DDCE_MANEJO_ESCORENTIA_SUPERFICIAL(3025),
    VIAB_TECN_DDCE_MANEJO_EROSION_SEDIMENTACION(3026),
    VIAB_TECN_DDCE_MANEJO_LIXIVIADOS(3027),
    VIAB_TECN_DDCE_MANEJO_BIOGAS(3028),
    VIAB_TECN_DDCE_ESTABILIDAD_CELDA_EMERGENTE(3029),
    VIAB_TECN_DDCE_DISENO_CAPA_COBERTURA(3030),
    VIAB_TECN_DDCE_OBRAS_COMPLEMENTARIAS(3031),
    VIAB_TECN_DDCE_DISENO_CELDA_DESECHOS_SANITARIOS(3032),
    VIAB_TECN_PRESUPUESTO_INVERSION(3033),
    VIAB_TECN_PRESUPUESTO_TOTAL(3034),
    VIAB_TECN_MANUAL_OPERACION_MANTENIMIENTO(3035),
    VIAB_TECN_ESPECIFICACIONES_TECNICAS(3036),
    VIAB_TECN_CRONOGRAMA_PROYECTO(3037),
    VIAB_TECN_ANEXO_OFICIO_ACEPTACION(3038),
    VIAB_TECN_TOPOGRAFIA(3039),
    VIAB_TECN_SUELOS_GEOLOGIA_GEOTECNICA(3040),
    VIAB_TECN_ANAISIS_HIDROMETEO(3041),
    VIAB_TECN_AGUAS_SUPERFICIALES(3042),
    VIAB_TECN_APROV_SISTEMA_EXISTENTE(3043),
    VIAB_TECN_APROV_TEMPORAL(3044),
    VIAB_TECN_DISENO_BARRIDO(3045),
    VIAB_TECN_DISENO_RECOLECCION_TRANSPORTE(3046),
    VIAB_TECN_DISENO_ESTACIONES_TRANSFERENCIA(3047),
    VIAB_TECN_DISENO_SISTEMA_APROVECHAMIENTO(3048),
    VIAB_TECN_DISENO_DRENAJE_PLUVIAL(3049),
    VIAB_TECN_DISENO_SISTEMA_DISPOSICION_FINAL(3050),
    VIAB_TECN_DISENO_SISTEMA_TRATAMIENTO(3051),
    VIAB_TECN_CELDA_DESECHOS_SOLIDOS(3052),
    VIAB_TECN_DISENO_SISTEMA_TRATAMIENTO_LIXIVIADO(3053),
    VIAB_TECN_DISENO_ELECTRICO(3054),
    VIAB_TECN_DISENO_MODELO_GESTION(3055),

    PS_INFORME_DE_VISITA_PREVIA(1101),
    PS_INVITACIONES_PERSONALES(1102),
    PS_CONVOCATORIA_PÚBLICA(1103),
    PS_ACTA_APERTURA_Y_CIERRE_CIP(1104),
    PS_ACTA_DE_LA_ASAMBLEA(1105),
    PS_REGISTRO_DE_ASISTENCIA(1106),
    PS_REGISTRO_CIP(1107),
    PS_INFORME_SISTEMATIZACION(1108),
    PS_RESPALDO_MEDIOS_VERIFICACION(1109),
    PS_RESPALDO_COMENTARIOS_ESTUDIO(1110),
    PS_INFORME_EVALUACION(1111),
    PS_INFORME_REUNION_INFORMATIVA(1112),
    PS_OFICIO_FINAL(1113),
    PS_DOCUMENTACION_COMPLEMENTARIA(1114),
    PS_CORRECCION_DOCUMENTACION(1115),
    PS_MECANISMO_PPS(1116),
    PS_INFORME_TECNICO_PPS(1117),
    FLORA(2510),
    MASTOFAUNA(2511),
    ORNITOFAUNA(2512),
    HERPETOFAUNA(2513),
    ENTOMOFAUNA(2514),
    ICTIOFAUNA(2515),
    MACROINVERTEBRADOS(2516),
    LIMNOFAUNA(2518),

    VIAB_TECN_PROPIEDAD_DERECHO_USO(3056),
    VIAB_TECN_PRESUPUESTO_GESTION_INTEGRA(3057),
    VIAB_TECN_EVAlUACION_ECONOMICA_FINANCIERA(3058),
    VIAB_TECN_ANEX_INSPECCION_OBSERVACIONES(3059),

    ///Gestion Integral
    VIAB_TECN_ESTUDIO_CALIDAD(3060),
    VIAB_TECN_ESTRUCTURA_ADMINISTRATIVA(3061),
    VIAB_TECN_INFORME_ENCUESTA(3062),
    VIAB_TECN_BASE_DISEÑO_ESTUDIO(3063),
    VIAB_TECN_ESTUDIO_ALTERNATIVAS(3064),
    VIAB_TECN_INFORME_EVALUACION_ECO(3065),
    VIAB_TECN_INFORME_FACTIBILIDAD(3066),
    ///Gestion Integral

    VIAB_TECN_ESTUDIO_CALIDAD_DESECHOS(3067),
    VIAB_TECN_ESTUDIO_AREA_PROYECTO(3068),
    VIAB_TECN_ESTUDIO_PLANTEAMIENTO_ANALISIS(3069),
    VIAB_TECN_ESTUDIO_ACEPTACION_ALTERNATIVA_GADM(3070),

    TIPO_LICENCIA_PROVINCIAL_GEN(231),
    TIPO_LICENCIA_PROVINCIAL_MIN(230),
    TIPO_LICENCIA_MUNICIPAL_GEN(233),
    TIPO_LICENCIA_MUNICIPAL_MIN(232),
    TIPO_LICENCIA_HIDROCARBUROS(234),
    
    TIPO_PRONUNCIAMIENTO_FORESTAL(260),
    TIPO_PRONUNCIAMIENTO_BIODIVERSIDAD(261),

    TIPO_INFORME_TECNICO_ICA(2600),
    TIPO_OFICIO_PRONUNCIAMIENTO_ICA(2601),
    TIPO_OFICIO_OBSERVACION_ICA(2602),
    
    TIPO_RESPALDOS_APROBACION_TDRS(1061),
    TIPO_RESPALDOS_OBSERVACION_TDRS(1062),

    //PARA LICENCIAMIENTO AMBIENTAL PROYEVTO HIDROCARBUROS
		
	//PARTICIPACION CIUDADANA REGISTRO AMBIENTAL
	RA_PC_ACTA_APERTURA_CIERRE(3401),
	RA_PC_ACTA_ASAMBLEA_PRESENTACION(3402),
	RA_PC_CONVOCATORIA(3403),
	RA_PC_SISTEMATIZACION_PPC(3404),
	RA_PC_INVITACION_PERSONAL(3405),
	RA_PC_ASISTENCIA_ASAMBLEA(3406),
	RA_PC_OBSERVACIONES(3407),
	RA_PC_AUDIO_PRESENTACION(3408),
	
	//REGISTRO AMBIENTAL CON PPC
	REGISTRO_AMBIENTAL_PPC_FISICO(799),
	RESOLUCION_AMBIENTAL_PPC_FISICO(798),
	OFICIO_PRONUNCIAMIENTO_REGISTRO_AMBIENTAL_PPC(797),
	FACTURA_REGISTRO_AMBIENTAL_PPC(804),
	
	//PARA DOCUMENTOS RESPALDO PAGO LICENCIA AMBIENTAL
	TIPO_RLA_FACTURA_PERMISO_AMBIENTAL(1020),
	TIPO_RLA_PROTOCOLIZACION_PAGO(1021),
	TIPO_RLA_POLIZA_PMA(1022),
	TIPO_RLA_JUSTIFICACION_PMA(1023),
	TIPO_RLA_CRONOGRAMA_PMA(1024),
	
	//CF: NUEVOS DOCUMENTOS PARTICIPACION SOCIAL
	JUSTIFICACION_RECHAZO_PPS(1120),
	FACTURA_PAGO_FACILITADORES(1121),
	INFORME_TECNICO_VISITA_PREVIA_PPS(1122),
	
	//CERTIFICADO AMBIENTAL CON FLUJO
    CERTIFICADO_AMBIENTAL_ESTADO_VEGETACION(3300),
	CERTIFICADO_AMBIENTAL_INFORME_SNAP(3301),
	CERTIFICADO_AMBIENTAL_INFORME_FORESTAL(3302),
	CERTIFICADO_AMBIENTAL_PRONUNCIAMIENTO(3303),
	CERTIFICADO_AMBIENTAL_INFORME_FORESTAL_ADJUNTO(3304),
	
	//ARCHIVAR PROYECTO
	ARCHIVACION_PROYECTO_ADJUNTO(3500),
	REACTIVACION_PROYECTO_ADJUNTO(3501),
	ARCHIVAR_PROYECTOS_MESADEAYUDA(3502),
	
	//NUT PAGOS --PROCESO RECAUDACIONES
	RECAUDACIONES_NUT_PAGOS_XBANCO(3599),
	RECAUDACIONES_NUT_PAGOS(3600),
	COMPROBANTE_PAGO_NUT(3601),	
	
	DOCUMENTO_ESTADO_PERMISO_AMBIENTAL(4117),
	DOCUMENTO_ESTADO_PERMISO_AMBIENTAL_4CATEGORIAS(4118),
	
	//documentos 020
	DOCUMENTO_COMUNIDADES_020(4107),
	DOCUMENTO_PREDIOS_020(4108),
	DOCUMENTO_INFRAESTRUCTURA_020(4109),
	DOCUMENTO_INFRAESTRUCTURA_SALUD_020(4110),
	RESOLUCION_REGISTRO_AMBIENTAL_020(3409),
	
	DOCUMENTOS_LEGALES_020(4111),
	REGISTRO_FOTOGRAFICO_020(4112),
	MAPAS_020(4113),
	POLIZA_GARANTIA_020(4114),
	DERECHO_MINERO(4115),
	POLIZA_020(4116),
	
	TITULO_MINERO_020(4131),
	GARANTIA_020_020(4132),
	DECLARACION_020(4133),
	FACTURAS_020(4134),
	AREA_ESTUDIO_020(4135),  
	UBICACION_020(4136),  
	GEOLOGICO_020(4137),  
	GEOMORFOLOGICO_020(4138),  
	EDAFOLOGICO_020(4139),  
	HIDROLOGICO_020(4140),  
	USO_SUELO_020(4141),  
	SOCIAL_020(4142),  
	COMUNIDADES_020(4143),  
	PREDIOS_020(4144),  
	OPERATIVA_020(4145),  
	MUESTREOS_FISICOS_020(4146),  
	MUESTREOS_FLORA_FAUNA_020(4147),  
	DIRECTA_FISICO_020(4148),  
	DIRECTA_TOTAL_FISICO_020(4149),  
	DIRECTA_BIOTICO_020(4150),  
	DIRECTA_TOTAL_BIOTICO_020(4151),  
	DIRECTA_SOCIAL_020(4152),  
	INDIRECTA_FISICO_020(4153),  
	INDIRECTA_TOTAL_FISICO_020(4154),  
	INDIRECTA_BIOTICO_020(4155),  
	INDIRECTA_TOTAL_BIOTICO_020(4156),  
	INDIRECTA_SOCIAL_020(4157), 
	
	//COACTIVAS
	PROVIDENCIA_NOTITIFICACIONES_DZ(4500),
	PROVIDENCIA_CERTIFICACION_DZ(4501),
	PROVIDENCIA_BOLETA_NOTIFICACION_DZ(4502),
	PROVIDENCIA_BOLETA_CERTIFICACION_DZ(4503),
	PROVIDENCIA_CERTIFICADO_PAGO_DZ(4504),
	PROVIDENCIA_ARCHIVO_DE_CAUSA_DZ(4505),
	PROVIDENCIA_CERTIFICADO_NO_PAGO_DZ(4506),
	PROVIDENCIA_ORDEN_COBRO_DZ(4507),
	PROVIDENCIA_CERTIFICADO_DE_EJECUTORIA_DZ(4508),
	RAZON_DE_NOTIFICACION_DZ(4509),
	BOLETA_ARCHIVO_DE_CAUSA_DZ(4510),
	TITULO_DE_CREDITO_DC(4511),
	PROVIDENCIA_ORDEN_PAGO_MEDIDAS_CAUTELARES_DC(4512),
	BOLETA_NOTIFICACION_ORDEN_PAGO_MEDIDAS_CAUTELARES_DC(4513),
	PROVIDENCIA_CERTIFICADO_PAGO_DC(4514),
	PROVIDENCIA_CERTIFICADO_NO_PAGO_DC(4515),
	PROVIDENCIA_ARCHIVO_DE_CAUSA_DC(4516),
	BOLETA_ARCHIVO_DE_CAUSA_DC(4517),
	PROVIDENCIA_DE_EMBARGO_DE_VALORES_DC(4518),
	BOLETA_NOTIFICACION_DE_EMBARGO_DE_VALORES_DC(4519),
	PROVIDENCIA_DE_SECUESTRO_DE_VEHICULOS_DC(4520),
	BOLETA_NOTIFICACION_DE_SECUESTRO_DE_VEHICULOS_DC(4521),
	PROVIDENCIA_DE_EMBARGO_DE_BIENES_DC(4522),
	BOLETA_NOTIFICACION_DE_EMBARGO_DE_BIENES_DC(4523),
	PROVIDENCIA_CERTIFICADO_PAGO_TOTAL_DC(4524),
	PROVIDENCIA_ARCHIVO_DE_CAUSA_LEVANTAMIENTO_MEDIDAS_DC(4525),
	BOLETA_ARCHIVO_DE_CAUSA_LEVANTAMIENTO_MEDIDAS_DC(4526),
	RESOLUCION_SANCIONATORIA(4530),
	
	// RCOA
	REGISTRO_AMBIENTAL_RCOA(800),
	RESOLUCION_AMBIENTAL_RCOA(801),
	PPC_REGISTRO_AMBIENTAL(802),
	FICHA_AMBIENTAL_PPC(803),
	RA_PPC_OFICIO_ARCHIVO(805),
	RA_PPC_PRONUNCIAMIENTO_FAVORABLE(806),
	
	//RETCE
	DOCUMENTO_EXPORTACION_NOTIFICACION(4200),
	DOCUMENTO_EXPORTACION_AUTORIZACION(4201),
	DOCUMENTO_EXPORTACION_MOVIMIENTOS(4202),
	DOCUMENTO_EXPORTACION_DESTRUCCION(4203),
	DOCUMENTO_AUTORIZACION_AUTOGESTION(4204),
	DOCUMENTO_AUTORIZACION_TRANSPORTE_MEDIO_PROPIO(4205),
	DOCUMENTO_MANIFIESTO_UNICO(4206),
	DOCUMENTO_CERTIFICADO_DESTRUCCION(4207),
	DOCUMENTO_OBSERVACIONES_GENERADOR(4208),
	INFORME_TECNICO_GENERADOR(4209),
	TIPO_DOCUMENTO_OFICIO_DE_APROBACION_PUNTO_MONITOREO(4210),
	TIPO_DOCUMENTO_INFORME_LABORATORIO(4211),
	TIPO_DOCUMENTO_RESPALDO_LABORATORIO(4212),
	DOCUMENTO_JUSTIFICACION_ESTADO_FUENTE(4213),
	JUSTIFICACION_DESCARGA_LIQUIDA(4214),
	JUSTIFICATIVO_EMISION_ATMOSFERICA(4215),	
	INFORME_TECNICO_EMISIONES_ATMOSFERICAS(4216),	
	INFORME_TECNICO_DESCARGA_LIQUIDA(4217),
	OFICIO_PRONUNCIAMIENTO_DESCARGA_LIQUIDA(4218),
	OFICIO_PRONUNCIAMIENTO_EMISION_ATMOSFERICA(4219),
	OFICIO_OBSERVACION_GENERADOR(4220),
	OFICIO_APROBACION_GENERADOR(4221),
	DOCUMENTO_MEDIO_VERIFICACION(4222),
	RESOLUCION_APROVECHAMIENTO_AGUA(4223),
	RETCE_OFICIO_NOTIFICACION_DERRAME(4224),
	RETCE_ESPECIES_AFECTADAS_DERRAME_FAUNA(4231),
	RETCE_ESPECIES_AFECTADAS_DERRAME_FLORA(4232),
	RETCE_RESOLUCION_INCLUSION(4225),	
	RETCE_ESTUDIO_COMPLEMENTARIO(4229),	
	RETCE_SUSTANCIA_CALCULO(4226),
	INFORME_TECNICO_GESTOR_DESECHOS(4227),
	OFICIO_PRONUNCIAMIENTO_GESTOR_DESECHOS(4228),
	INFORMACION_DECLARACION_RESIDUOS_DESECHOS(4230),
	RETCE_SAE_LABORATORIO(5901),
	RETCE_CADENA_CUSTODIA(5902),
	RETCE_PROTOCOLO_MUESTREO(5903),
	RETCE_ADICIONAL_LABORATORIO(5904),
	RETCE_IMAGEN_COORDENADAS(5905),
	

	ADJUNTO_BAJALE_PLASTICO(5004),
	
	TIPO_CERTIFICADO_AMBIENTAL_MAE(5050),
	TIPO_CERTIFICADO_AMBIENTAL_GAD(5051),
	REPORTE_INFORMACION_PRELIMINAR(5060),
	CERTIFICADO_AMBIENTAL_MAE(5052),
	GUIA_BUENAS_PRACTICAS_AMBIENTALES(5800),
	GUIA_REFERENCIAL_ALMACENAMIENTO_RESIDUOS(5801),
	DOCUMENTO_DESECHO_PELIGROSO(5802),
	
	//NUEVO RGD
	RGD_GUIA_REFERENCIAL_ALMACENAMIENTO(5028),
	RGD_GUIA_ETIQUETADO(5029),
	RGD_RESOLUCION_AUTORIZACION_ADMINISTRATIVA_AMBIENTAL(5030),
	RGD_NO_GENERA_DESECHOS(5031), 
	RGD_GESTION_INTERNA(5032),
	RGD_REGISTRO_GENERADOR_DESECHOS(5033),
	RGD_OFICIO_PRONUNCIAMIENTO(5034),
	RGD_REGISTRO_GENERADOR_DESECHOS_REP(5035),
	RGD_OFICIO_PRONUNCIAMIENTO_REP(5036),
	RGD_OFICIO_ARCHIVACION(5037),
	RGD_REGISTRO_GENERADOR_DESECHOS_AAA(5038),
	RGD_OFICIO_PRONUNCIAMIENTO_AAA(5039),
	RGD_FINAL_REGISTRO_GENERADOR_DESECHOS(5040),
	RGD_FINAL_OFICIO_PRONUNCIAMIENTO(5041),

	
	//VIABILIDAD AMBIENTAL
	RCOA_VIABILIDAD_SNAP_FORMULARIO_OPERADOR(5005),
	RCOA_VIABILIDAD_SNAP_SUSTENTO(5006),
	RCOA_VIABILIDAD_ANALISIS_JURIDICO(5007),
	RCOA_VIABILIDAD_SNAP_INFORME_INSPECCION(5008),
	RCOA_VIABILIDAD_SNAP_FAVORABLE(5009),
	RCOA_VIABILIDAD_SNAP_NO_FAVORABLE(5010),
	RCOA_VIABILIDAD_FORESTAL_ANEXO(5011),
	RCOA_VIABILIDAD_FOTOGRAFIA_INFORME_FORESTAL(5012),
	RCOA_VIABILIDAD_FORESTAL_INFORME_INSPECCION(5013),
	RCOA_VIABILIDAD_FORESTAL_INFORME_REVISION(5014),
	RCOA_VIABILIDAD_FORESTAL_FAVORABLE(5015),
	RCOA_VIABILIDAD_FORESTAL_NO_FAVORABLE(5016),
	RCOA_VIABILIDAD_INFORMACION_RESPALDO_OPERADOR(5017),
	//VIABILIDAD BYPASS
	RCOA_VIABILIDAD_GUIA_OPERADOR_SNAP(5018),
	RCOA_VIABILIDAD_GUIA_OPERADOR_FORESTAL(5019),
	RCOA_VIABILIDAD_BYPASS_INFORME_SNAP(5020),
	RCOA_VIABILIDAD_BYPASS_OFICIO_SNAP(5021),
	RCOA_VIABILIDAD_BYPASS_INFORME_FORESTAL(5022),
	RCOA_VIABILIDAD_BYPASS_OFICIO_FORESTAL(5023),
	RCOA_VIABILIDAD_BYPASS_OFICIO_ARCHIVO_DOS_NOFAVORABLE(5024),
	RCOA_VIABILIDAD_BYPASS_OFICIO_ARCHIVO_UNO_NOFAVORABLE(5025),
	RCOA_VIABILIDAD_BYPASS_OFICIO_ARCHIVO_SNAP(5026),
	RCOA_VIABILIDAD_BYPASS_OFICIO_ARCHIVO_FORESTAL(5027),
	
	//CERTIFICADO INTERSECCION
	RCOA_CERTIFICADO_INTERSECCION_OFICIO(5100),
	RCOA_CERTIFICADO_INTERSECCION_MAPA(5101),
	RCOA_COORDENADA_GEOGRAFICA(5102),
	RCOA_COORDENADA_IMPLANTACION(5103),
	RCOA_DOCUMENTO_ALTO_IMPACTO(5104),
	RCOA_DOCUMENTO_GENETICO(5105),
	RCOA_DOCUMENTO_SECTORIAL(5106),
	RCOA_DOCUMENTO_FRONTERA(5107),
	RCOA_DOCUMENTO_SANEAMIENTO(5108),
	RCOA_DOCUMENTO_AUTORIZACION_SECTORES(5109),
	RCOA_PRONUNCIAMIENTO_PROHIBICION_ACTIVIDAD(5110),
	RCOA_DOCUMENTO_INFORME_VIABILIDAD_PNGIDS(5111),
	RCOA_DOCUMENTO_DIAGNOSTICO_AMBIENTAL(5112),
	RCOA_DOCUMENTO_PLAN_ACCION(5113),
	RCOA_PRONUNCIAMIENTO_OBSERVACIONES_NO_SUBSANABLES(5114),
	RCOA_CERTIFICADO_INGRESO_DIAGNOSTICO_AMBIENTAL(5115),
	RCOA_INFORME_DIAGNOSTICO_AMBIENTAL(5119),
	RCOA_OFICIO_DIAGNOSTICO_AMBIENTAL(5120),
	RCOA_ARCHIVO_OBSERVACIONES_DIAGNOSTICO_AMBIENTAL(5121),
	RCOA_ARCHIVO_NO_INICIA_REGULARIZACION(5122),
	RCOA_CERTIFICADO_INTERSECCION_OFICIO_ACTUALIZADO(5123),
	RCOA_CERTIFICADO_INTERSECCION_MAPA_ACTUALIZADO(5124),
	RCOA_COORDENADA_GEOGRAFICA_ACTUALIZADA(5125),
	RCOA_COORDENADA_IMPLANTACION_ACTUALIZADA(5126),
	RCOA_MEDIO_VERIFICACION_PLAN_ACCION(5130),
	RCOA_INSTRUMENTO_AVANCE_PLAN_ACCION(5131),
	RCOA_PLAN_ACCION_GENERADO(5132),
	RCOA_PRONUNCIAMIENTO_ARCHIVO_NO_FAVORABLE(5133),
	RCOA_DOCUMENTO_TITULO_CONCESION_CAMARONERA(5134),

	//AUB PLAMES DE MANEJO AMBIENTAL REGISTRO AMBIENTAL
	RCOA_SUBPLAN_MANEJO_AMBIENTAL(3411),
	
	// INVENTARIO FORESTAL
	RCOA_COORDENADAS_MUESTRAS(5200),
	RCOA_LISTA_ESPECIES(5201),
	RCOA_VALORACION_BIENES(5202),
	INVENTARIO_FORESTAL_INFORME_INSPECCION(5053),
	INVENTARIO_FORESTAL_INFORME_TECNICO(5054),
	INVENTARIO_FORESTAL_OFICIO_PRONUNCIAMIENTO(5055),
	IMAGEN_COBERTURA_VEGETAL(5056),
	IMAGEN_ECOSISTEMAS_PRESENTES(5057),
	IMAGEN_AREA_IMPLANTACION(5058),
	IMAGEN_ANEXO(5059),
	
	//AGRUPACION
	RCOA_AGRUPACION_RESPALDO_OPERADOR(5150),
	
	//ESTUDIO DE IMPACTO AMBIENTAL RCOA
	EIA_ALCANCE_CICLO_VIDA(4001),
	EIA_ANALISIS_ALTERNATIVAS(4002),
	EIA_DEMANDA_RECURSOS_NATURALES(4003),
	EIA_DIAGNOSTICO_AMBIENTAL(4004),
	EIA_INVENTARIO_FORESTAL(4005),
	EIA_IDENTIFICACION_DETERMINACION_AREAS(4006),
	EIA_ANALISIS_RIESGOS(4007),
	EIA_EVALUACION_IMPACTOS_SOCIAMBIENTALES(4008),
	EIA_PLAN_MANEJO_AMBIENTAL_Y_SUB_PLANES(4009),
	EIA_ANEXOS(4010),
	EIA_CERTIFICADO_PARTICIPACION_CONSULTOR(4011),
	EIA_INFORME_TECNICO_RCOA(4012),
	EIA_OFICIO_RESPUESTA_RCOA(4013),
	EIA_MEMORANDO_RCOA(4014),
	EIA_INFORME_CONSOLIDADO_RCOA(4015),
	EIA_INFORME_INSPECCION_RCOA(4016),
	EIA_CONTRATO_VALOR_ELABORACION(4017),
	EIA_RESPONSABLIDAD_INFORMACION_CONTENIDA(4018),
	EIA_ACTA_REUNION(4019),
	EIA_JUSTIFICACION_REUNION(4020),	
	EIA_JUSTIFICACION_PRORROGA(4022),
	EIA_OFICIO_APROBACION_CONSOLIDADO(4021),
	EIA_OFICIO_ARCHIVACION_AUTOMATICA(4023),
	EIA_CERTIFICADO_PARTICIPACION_CONSULTOR_COMPAÑIA(4024),
	EIA_CERTIFICADO_PARTICIPACION_EQUIPO_CONSULTOR(4025),
	EIA_ESTUDIO_ACTUALIZADO(4026),
	EIA_PLANTILLA_DETALLE_PMA(4027),
	EIA_ANEXO_SUB_PLAN_PMA(4028),
	EIA_DOCUMENTO_HABILITANTE(5906),	
	
	//REGISTRO SUSTANCIAS QUIMICAS
	RCOA_RSQ_REGISTRO_SUSANCIAS_QUIMICAS(5300),
	RCOA_RSQ_INFORME_TECNICO_APROBADO(5301),
	RCOA_RSQ_INFORME_TECNICO_NEGADO(5302),
	RCOA_RSQ_INFORME_INSPECCION_APROBADO_OBSERVADO(5303),
	RCOA_RSQ_OFICIO_APROBADO(5304),
	RCOA_RSQ_OFICIO_NEGADO(5305),
	RCOA_RSQ_EMISION_RSQ_INSTITUCION_EDUCATIVA(5306),
	RCOA_RSQ_RESPONSABILIDAD_TECNICO(5307),
	RCOA_RSQ_TITULO_PROFESIONAL_TECNICO(5308),
	RCOA_RSQ_TITULO_ARTESANAL_TECNICO(5309),
	RCOA_RSQ_JUSTIFICAIONES_ADICIONALES(5310),
	RCOA_RSQ_JUSTIFICACION_PRORROGA(5311),
	RCOA_RSQ_JUSTIFICACION_ANULACION_IMPORTACION(5312),
	RCOA_RSQ_AUTORIZACION_IMPORTACION(5313),
	RCOA_RSQ_ANULACION_IMPORTACION(5314),
	RCOA_RSQ_FINAL_REGISTRO_SUSANCIAS_QUIMICAS(5315),
	RCOA_RSQ_CORRECCION_IMPORTACION_VUE(5317), //Similar a registro en tabla: suia_iii.document_types
	RCOA_RSQ_AUTORIZACION_IMPORTACION_VUE(5318), //Similar a registro en tabla: suia_iii.document_types
	RCOA_FORMULARIO_VUE_IMPORTACION_SUSTANCIAS_QUIMICAS(5319), //Similar a registro en tabla: suia_iii.document_types
	RCOA_REPORTE_VUE_IMPORTACION_SUSTANCIAS_QUIMICAS(5320), //Similar a registro en tabla: suia_iii.document_types
	RCOA_RSQ_DECLARACION_DOCUMENTO_RESPALDO(5316),
	RCOA_RSQ_EVIDENCIA_FOTOGRAFICA(5321),
	
	//PRONUNCIAMIENTO APROBADO
	RCOA_PA_OFICIO_APROBADO(5400),
	RCOA_PA_OFICIO_OBSERVADO(5401),
	RCOA_PA_OFICIO_ARCHIVADO(5402),
	
	
	//PARTICIPACION CIUDADANA
	RCOA_PPC_INFORME_PLANIFICACION(5600),
	RCOA_PPC_FIRMA_DE_RESPONSABILIDAD(5601),
	RCOA_PPC_INFORME_PLANIFICACION_OBSERVACION(5603),
	RCOA_PPC_INFORME_PLANIFICACIÓN_OBSERVADO_RESPUESTAS_OBSERVACIONES_NO_SOLVENTADAS(5604),
	RCOA_PPC_OFICIO_PLANIFICACION_OBSERVACION(5605),
	RCOA_PPC_INFORME_PLANIFICACIÓN_OBSERVADO_RESPUESTAS_OBSERVACIONES_SOLVENTADAS(5606),
	RCOA_PPC_INFORME_PLANIFICACION_APROBACION(5608),
	RCOA_PPC_OFICIO_PLANIFICACION_APROBACION(5602),
	RCOA_PPC_INFORME_SISTEMATIZACION_OBSERVACION(5607),
	RCOA_PPC_OFICIO_SISTEMATIZACION_OBSERVACION(5609),
	RCOA_PPC_INFORME_RESPUESTA_OBSERVACIONES_SISTEMATIZACION_NO_SOLVENTADAS(5610),
	RCOA_PPC_INFORME_TECNICO_REVISION_INFORME_DE_SISTEMATIZACION(5611),
	RCOA_PPC_INFORME_PARA_ACCIONES_COMPLEMENTARIAS(5612),
	RCOA_PPC_OFICIO_ACCIONES_COMPLEMENTARIAS(5613),
	RCOA_PPC_FIRMA_DE_RESPONSABILIDAD_ACCIONES_COMPLEMENTARIAS(5614),
	RCOA_PPC_INFORME_TECNICO_REVISION_INFORME_DE_SISTEMATIZACION_COMPLEMENTARIO_OBSERVADO(5615),
	RCOA_PPC_INFORME_TECNICO_EVALUACION_DE_ACCIONES_COMPLEMENTARIAS(5616),
	RCOA_PPC_OFICIO_INFORME_DE_SISTEMATIZACION_ACCIONES_COMPLEMENTARIAS_OBSERVADO(5617),
	RCOA_PPC_INFORME_REVISION_RESPUESTA_OBSERVACIONES_AL_INFORME_DE_ACCIONES_COMPLEMENTARIAS_NO_SOLVENTADAS(5618),
	
	RCOA_PPC_OFICIO_APROBACION_DEL_TRABAJO_DEL_FACILITADOR(5620),
	RCOA_PPC_INFORME_TECNICO_DE_OPINIONES_Y_OBSERVACIONES_TECNICA_Y_ECONOMICAMENTE_VIABLES(5621),
	RCOA_PPC_OFICIO_DE_APROBACION_DEL_PPC(5622),
	RCOA_PPC_FIRMA_DE_RESPONSABILIDAD_SISTEMATIZACION(5630),
	// ARCHIVOS SUBIDOS
	RCOA_PPC_INFORME_SISTEMATIZACION(5623),
	RCOA_PPC_INFORME_ACCIONES_COMPLEMENTARIAS(5624),
	RCOA_PPC_RESPALDOS_COMENTARIOS(5625),
	RCOA_PPC_ANEXOS_SISTEMATIZACION(5626),
	RCOA_PPC_ANEXOS_SISTEMATIZACION_CORRECCIONES_OPERADOR(5627),
	RCOA_PPC_SISTEMATIZACION_CORRECCIONES_OPERADOR(5628),
	RCOA_PPC_ANEXO_ACCIONES_COMPLEMENTARIAS(5629),
	RCOA_PPC_BYPASS_EXPEDIENTE(5631),
	RCOA_PPC_BYPASS_OFICIO_APROBACION(5632),
	RCOA_PPC_BYPASS_OFICIO_ARCHIVO(5633),
	RCOA_PPC_BYPASSS_ADJUNTOS(5634),
	RCOA_PPC_BYPASSS_EIA(5635),

	
	// EMISION LICENCIA AMBIENTAL
	EMISION_LICENCIA_FACTURA_PERMISO_AMBIENTAL(5500),
	EMISION_LICENCIA_PAGO_PERMISO_AMBIENTAL(5501),
	EMISION_LICENCIA_POLIZA_PMA(5502),
	EMISION_LICENCIA_MEDIDAS_PMA(5503),
	EMISION_LICENCIA_CRONOGRAMA_PMA(5504),
	EMISION_LICENCIA_MEMORANDO_TECNICO_JURIDICO(5505),
	EMISION_LICENCIA_MEMORANDO_TECNICO_JURIDICO_FIRMADO(5509),
	RCOA_LA_RESOLUCION(5506),
	RCOA_LA_MEMORANDO(5507),
	RCOA_LA_PRONUNCIAMIENTO(5508),
	RCOA_LA_AUTORIZACION_ADMINISTRATIVA_AMBIENTAL(5510),
	FACTURA_PAGO_TASA_INVENTARIO_FORESTAL(5511),
	FACTURA_PAGO_TASA_CONTROL_SEGUIMIENTO(5512),
	TERMINOS_CONDICIONES_FIRMADO_USUARIO(5700),
	
	//DIGITALIZACION
	DIGITALIZACION_DOCUMENTO_COORDENADAS(5750),
	DIGITALIZACION_CERTIFICADO_INTERSECCION(5751),
	DIGITALIZACION_RESOLUCION(5752),
	DIGITALIZACION_FICHA_AMBIENTAL(5753),
	DIGITALIZACION_DOCUMENTOS_HABILITANTES(5754),
	DIGITALIZACION_OTROS_DOCUMENTOS(5755),
	DIGITALIZACION_ESTUDIO_IMPACTO_AMBIENTAL(5756),
	DIGITALIZACION_MAPA(5757),
	DIGITALIZACION_DOCUMENTOS_ART(5758),
	DIGITALIZACION_DOCUMENTOS_RSQ(5759),
	DIGITALIZACION_DOCUMENTOS_OFICIO_UPDATE(5760),
	DIGITALIZACION_DOCUMENTOS_MAPA_UPDATE(5761),
	DIGITALIZACION_DOCUMENTO_HISTORIAL_ACTUALIZACIONES(5762),
	
	//ACTUALIZACION CERTIFICADO INTERSECCION
	TIPO_COORDENADAS_ACTUALIZACION(4100), 
	TIPO_CI_MAPA_ACTUALIZACION(4101), 
	TIPO_CI_OFICIO_ACTUALIZACION(4102),
	
	//OPINION PUBLICA
	OPINION_PUBLICA_ANEXO(5900),
	
	//VIABILIDAD AMBIENTAL SNAP
	RCOA_VIABILIDAD_SNAP_II_COMPLEMENTARIA(6100),
	RCOA_VIABILIDAD_SNAP_II_INFORME_TECNICO(6101),
	RCOA_VIABILIDAD_SNAP_II_FAVORABLE(6102),
	RCOA_VIABILIDAD_SNAP_II_NO_FAVORABLE(6103),
	
	//VIABILIDAD AMBIENTAL FORESTAL
	RCOA_VIABILIDAD_PFN_II_INFORME_OBSERVACIONES(6150),
	RCOA_VIABILIDAD_PFN_II_OFICIO_OBSERVACIONES(6151),
	RCOA_VIABILIDAD_PFN_II_INFORME_APOYO(6152),
	RCOA_VIABILIDAD_PFN_II_MEMORANDO_APOYO(6153),
	RCOA_VIABILIDAD_PFN_II_INFORME_VIABILIDAD(6154),
	RCOA_VIABILIDAD_PFN_II_MEMORANDO_VIABILIDAD(6155),
	RCOA_VIABILIDAD_PFN_II_FAVORABLE(6156),
	RCOA_VIABILIDAD_PFN_II_NO_FAVORABLE(6157),
	RCOA_VIABILIDAD_PFN_II_MAPA_RECORRIDO(6158),
	RCOA_VIABILIDAD_PFN_II_IMAGEN_MAPA_RECORRIDO(6159),
	RCOA_VIABILIDAD_PFN_II_ANEXOS_INFORME_FACTIBILIDAD(6160),
	RCOA_VIABILIDAD_PFN_II_INFORME_FACTIBILIDAD(6161),
	RCOA_VIABILIDAD_PFN_II_FOTO_GENERAL_PROYECTO(6162),
	RCOA_VIABILIDAD_PFN_II_ADJUNTO_GENERAL_PROYECTO(6163),
	RCOA_VIABILIDAD_PFN_II_FOTO_COBERTURA_PROYECTO(6164),
	RCOA_VIABILIDAD_PFN_II_FOTO_FASE_CAMPO(6165),
	RCOA_VIABILIDAD_PFN_II_FOTO_FASE_OFICINA(6166),
	RCOA_VIABILIDAD_PFN_II_TABLA_BASAL_VOLUMEN(6259),
	
	//ACTUALIZACION PLAGUICIDAS
	PQUA_JUSTIFICACION_PRODUCTO(6249),
	PQUA_LISTADO_REGISTROS_PQUA(6250),
	PQUA_OFICIO_PRORROGA(6251),
	PQUA_RESPALDO_EFECTOS_ACUATICOS(6252),
	PQUA_DOCUMENTO_RESPALDO(6253),
	PQUA_DOCUMENTO_ETIQUETA(6254),
	PQUA_INFORME_APROBADO(6255),
	PQUA_INFORME_OBSERVADO(6256),
	PQUA_OFICIO_APROBADO(6257),
	PQUA_OFICIO_OBSERVADO(6258)
	;

    @Getter
    private int idTipoDocumento;

    private TipoDocumentoSistema(int idTipoDocumento) {
        this.idTipoDocumento = idTipoDocumento;
    }
}
