/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.utils;

import ec.gob.ambiente.suia.configuration.ConfigurationBean;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author carlos.pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: carlos.pupo, Fecha: May 25, 2015]
 *          </p>
 */
public class Constantes {

	// VARIABLES DE SESSION
	public static final String SESSION_EIA_OBJECT = "SESSION_EIA_OBJECT";
	public static final String SESSION_PROJECT_OBJECT = "SESSION_PROJECT_OBJECT";
	public static final String SESSION_FICHA_AMBIENTAL_MINERA_OBJECT = "SESSION_FICHA_MINERA";
	public static final String SESSION_TIPO_PMA_OBJECT = "SESSION_TIPO_PMA_OBJECT";
	public static final String SESSION_APROBACION_REQ_TEC_OBJECT = "SESSION_APROBACION_REQ_TEC_OBJECT";

	// LOOKUP
	public static final String ALFRESCO_SERVICE_BEAN = "java:global/alfresco-ejb-1.0/AlfrescoServiceBean!ec.gob.ambiente.alfresco.service.AlfrescoServiceBean";
	public static final String JBPM_EJB_PROCESS_BEAN = "java:global/jbpm-ejb-1.0/ProcessBeanFacade!ec.gob.ambiente.jbpm.facade.ProcessBeanFacade";
	public static final String JBPM_EJB_TASK_BEAN = "java:global/jbpm-ejb-1.0/TaskBeanFacade!ec.gob.ambiente.jbpm.facade.TaskBeanFacade";

	//PLANTILLA CONSULTORES
		public static final String PLANTILLA_CONDUCTORES = "Plantilla de conductores_final.xls";
		
	// NOMBRES PROCESOS
	public static final String NOMBRE_PROCESO_SEGUIMIENTO_REPORTES_MONITOREO = "seguimientoReportesMonitoreo.seguimientoReportesMonitoreo";
	public static final String NOMBRE_PROCESO_DENUNCIAS = "AtencionDenuncias.atencionDenuncias";
	public static final String NOMBRE_PROCESO_REQUISITOS_PREVIOS_LICENCIAMIENTO = "mae-procesos.Requisitos_Previos_al_Licenciamiento";
	public static final String NOMBRE_PROCESO_CERTIFICADO_VIABILIDAD_AMBIENTAL = "Certificado-viabilidad-ambiental";
	public static final String NOMBRE_PROCESO_PLAN_CIERRE = "mae-procesos.Planes_de_Cierre_y_Abandono";
	public static final String NOMBRE_PROCESO_APROBACION_PUNTOS_MONITOREO = "mae-procesos.Aprobacion_de_Puntos_de_Monitoreo";
	public static final String NOMBRE_PROCESO_INFORME_TECNICO = "mae-procesos.InformeTecnico";
	public static final String NOMBRE_PROCESO_ELIMINAR_PROYECTO = "mae-procesos.Eliminarproyecto";
	public static final String NOMBRE_PROCESO_TDR = "tdr";
	public static final String NOMBRE_PROCESO_PARTICIPACION_SOCIAL = "suia.participacion-social";
	public static final String NOMBRE_PROCESO_CATEGORIA_UNO = "mae-procesos.CategoriaUno";
	public static final String NOMBRE_PROCESO_CATEGORIA2V2 = "mae-procesos.RegistroAmbiental";
    public static final String NOMBRE_PROCESO_CATEGORIA2 = "mae-procesos.registro-ambiental";
	public static final String NOMBRE_PROCESO_EIA = "mae-procesos.estudioImpactoAmbiental";
	public static final String NOMBRE_PROCESO_CERTIFICAO_VIABILIDAD = "Certificado-viabilidad-ambiental";
    public static final String NOMBRE_PROCESO_EMISION_VIABILIDAD_TECNICA = "mae-procesos.EmisionViabilidadTecnica";
	public static final String NOMBRE_PROCESO_REQUISITOS_PREVIOS = "mae-procesos.RequisitosPrevios";
	public static final String NOMBRE_PROCESO_LICENCIA_AMBIENTAL = "SUIA.LicenciaAmbiental";
	public static final String NOMBRE_PROCESO_GENERADOR_DESECHOS = "mae-procesos.GeneradorDesechos";
	public static final String NOMBRE_PROCESO_APROBACION_REQUISITOS_TECNICOS = "Suia.AprobracionRequisitosTecnicosGesTrans2";
	public static final String NOMBRE_PROCESO_INSPECCIONES_CONTROL_AMBIENTAL = "mae-procesos.InspeccionesControlAmbiental";
	public static final String NOMBRE_PROCESO_ACTUALIZACION_PMA = "mae-procesos.ActualizacionPMA";
	public static final String NOMBRE_PROCESO_PLAN_EMERGENTE = "mae-procesos.PlanEmergente";
	public static final String NOMBRE_PROCESO_CERTIFICADO_AMBIENTAL = "mae-procesos.CertificadoAmbiental";
	public static final String NOMBRE_PROCESO_REGISTRO_CONTAMINANTES = "mae-procesos.RegistroEmisionesTransferenciaContaminantesEcuador";
	public static final String NOMBRE_PROCESO_RECAUDACIONES = "mae-procesos.Recaudaciones";
	public static final String NOMBRE_PROCESO_COACTIVAS = "suia-iii.Coactivas";
	public static final String RCOA_PROCESO_VIABILIDAD = "rcoa.ViabilidadAmbiental";
	public static final String RCOA_REGISTRO_PRELIMINAR = "rcoa.RegistroPreliminar";
	public static final String RCOA_CERTIFICADO_AMBIENTAL = "rcoa.CertificadoAmbiental";
	public static final String RCOA_REGISTRO_AMBIENTAL = "rcoa.RegistroAmbiental";
	public static final String RCOA_REGISTRO_AMBIENTAL_PPC = "rcoa.RegistroAmbientalconPPC";
	public static final String RCOA_REGISTRO_GENERADOR_DESECHOS = "rcoa.RegistrodeGeneradordeDesechosPeligrososyEspeciales";
	public static final String RCOA_ESTUDIO_IMPACTO_AMBIENTAL = "rcoa.EstudioImpactoAmbiental";
	public static final String RCOA_ESTUDIO_APROBACION_FINAL="rcoa.AprobacionFinalEstudio";
	public static final String RCOA_PROCESO_PARTICIPACION_CIUDADANA="rcoa.ProcesoParticipacionCiudadana";
	public static final String ROCA_PROCESO_RESOLUCION_LICENCIA_AMBIENTAL="rcoa.ResolucionLicenciaAmbiental"; 
	public static final String RCOA_PROCESO_SUSTANCIAS_QUIMICAS_IMPORTACION="rcoa.RegistroSustanciasQuimicasImportacion";
	public static final String RCOA_PROCESO_SUSTANCIAS_QUIMICAS_IMPORTACION_VUE="rcoa.RegistroSustanciasQuimicasImportacionVue";
	public static final String RCOA_DECLARACION_SUSTANCIA_QUIMICA="rcoa.DeclaracionSustanciasQuimicas";
	public static final String RCOA_PROCESO_PARTICIPACION_CIUDADANA_BYPASS="rcoa.ProcesoParticipacionCiudadanaBypass";
	public static final String PROCESO_DIGITALIZACION="rcoa.DigitalizacionProyectos";
	public static final String RCOA_PROCESO_VIABILIDAD_BYPASS = "rcoa.ViabilidadAmbientalBypass";
	public static final String RCOA_ESTUDIO_IMPACTO_AMBIENTAL_v2 = "rcoa.EstudioImpactoAmbiental_v2";
	public static final String RCOA_PROCESO_VIABILIDAD_SNAP = "rcoa.EmisionViabilidadAmbientalSnap";
	public static final String RCOA_PROCESO_SUSTANCIAS_QUIMICAS="rcoa.RegistroSustanciasQuimicas";
	public static final String RCOA_PROCESO_ACTUALIZACION_ETIQUETADO_PLAGUICIDAS="rcoa.ActualizacionEtiquetadoPlaguicidasUsoAgricola";
	public static final String RCOA_PROCESO_BYPASS_PARTICIPACION_CIUDADANA="rcoa.PpcConsultaAmbiental";
	public static final String RCOA_RESOLUCION_LICENCIA_AMBIENTAL="suia-iii.ResolucionLicenciaAmbientalV2";

	// VARIABLES PROCESOS
	public static final String VARIABLE_PROCESO_TRAMITE = "tramite";
	public static final String VARIABLE_TRAMITE_NO_PROYECTO = "No asociado";
	public static final String VARIABLE_PROCESO_TRAMITE_RESOLVER = "procedureResolverClass";
	public static final String VARIABLE_SESION_PROCESS_ID = "VARIABLE_SESION_PROCESS_ID";
	public static final String VARIABLE_PROCESO_AUTORIDADAMBIENTAL = "autoridadAmbiental";
	public static final String VARIABLE_TIPO_RGD = "tipoRGD";
	public static final String CODIGO_PROYECTO = "codigoProyecto";
	public static final String ID_PROYECTO = "idProyecto";
	public static final String U_ID_PROYECTO = "u_idProyecto";
	
	public static final String USUARIO_VISTA_MIS_PROCESOS = "usuarioVistaMisProcesos";
	public static final String USUARIO_FUNCIONARIO_VISTA_MIS_PROCESOS = "usuarioFuncionarioVistaMisProcesos";
	public static final String VARIABLE_OBSERVACIONES_VIABLES="observaciones_viables";
	
	// WEBSERVICE SRI
	//public static final String USUARIO_WS_MAE_SRI_RC = "1712444593";
	public static final String USUARIO_WS_MAE_SRI_RC = getUserWebServicesSnap();
	//public static final String PASSWORD_WS_MAE_SRI_RC = "web_consultora";
	public static final String PASSWORD_WS_MAE_SRI_RC = "1234";
	public static final String NO_ERROR_WS_MAE_SRI_RC = "NO ERROR";

	// ALFRESCO
	public static final String ALFRESCO_TDR_FOLDER_NAME = "TDR-EIA-LICENCIA";
	public static final String ALFRESCO_PARTICIPACION_SOCIAL_FOLDER_NAME = "PARTICIPACION-SOCIAL";
	public static final String GUIA_BUENAS_PRACTICAS = "GU\u00CDA DE BUENAS PR\u00C1CTICAS AMBIENTALES.pdf";
	public static final String PLANTILLA_COORDENADAS = "Plantilla de coordenadas.xls";
	public static final String PLANTILLA_COORDENADAS_GEOGRAFICAS = "Coordenadas área(s) geográfica(s).xls";
	public static final String PLANTILLA_COORDENADAS_IMPLANTACION = "Coordenadas área(s) de implantación.xls";
	public static final String PLANTILLA_COORDENADAS_INVENTARIO_FORESTAL = "Coordenadas área de desbroce.xls";
	public static final String PLANTILLA_COORDENADAS_REGISTRO_GENERADOR = "Coordenadas Área geográfica_rgd.xls";
	public static final String PLANTILLA_COORDENADAS_REGISTRO_GENERADOR_AAA = "Coordenadas Área geográfica_rgd_AAA.xls";
	public static final String PLANTILLA_COORDENADAS_REGISTRO_GENERADOR_REP = "Coordenadas Área geográfica_rgd_rep.xls";
	public static final String PLANTILLA_COORDENADAS_PSAD = "Coordenadas Área geográfica PSAD.xls";
	public static final String PLANTILLA_COORDENADAS_WGS_17S = "Coordenadas Área geográfica WGS84 17S.xls";
	public static final String PLANTILLA_COORDENADAS_WGS = "Coordenadas Área geográfica WGS84.xls";
	public static final String PLANTILLA_COORDENADAS_VIABILIDAD_FORESTAL = "Plantilla de coordenadas viabilidad forestal.xls";
	
	public static final String PLANTILLA_INVENTARIO_FORESTAL="Plantilla_Inventario_Forestal_Registro.xls";
	public static final String AYUDA_COORDENADAS = "Guia para obtener coordenadas.pdf";
	public static final String AYUDA_CATALOGO_CIIU = "Guía de catálogo ciiu.pdf";
	public static final String NOMBRE_DOCUMENTO_CONTRATO_OPERACION = "Contrato de operaci\u00F3n";
	public static final String NOMBRE_DOCUMENTO_REGISTRO_MINERO_ARTESANAL = "Registro de minero artesanal MRNNR";
	public static final String NOMBRE_DOCUMENTO_COORDENADAS_PROYECTO = "Coordenadas del proyecto";
	public static final String NOMBRE_DOCUMENTO_CERTIFICADO_USO_SUELOS = "Certificado de uso de suelos";
	public static final String NOMBRE_DOCUMENTO_CERTIFICADO_COMPATIBILIDAD_USO_SUELOS = "Certificado de compatibilidad de uso de suelos";
	public static final String NOMBRE_DOCUMENTO_BORRADOR_REGISTRO_GENERADOR = "Borrador del registro de generador";
	public static final String NOMBRE_DOCUMENTO_JUSTIFICACION_PROPONENTE_REGISTRO_GENERADOR = "Justificaciones";
	public static final String NOMBRE_DOCUMENTO_MODELO_ETIQUETA = "Modelo de etiqueta";
	public static final String NOMBRE_PROCESO_GENERADOR_DESECHOS_PERMISO_AMBIENTAL = "mae-procesos.generadorDesechos-permiso-ambiental";
	public static final String CARPETA_DOCS_REGISTRO_PROYECTO_ALFRESCO = "REGISTRO-PROYECTO";
	public static final String CARPETA_CONTRATOS_OPERACION = "CONTRATOS-OPERACION-MINEROS";
	public static final String CARPETA_CONSECION_CAMARONERA = "CONSECION_CAMARONERA";
	public static final String CARPETA_REGISTROS_MINEROS = "REGISTROS-MINEROS";
	public static final String CARPETA_CATEGORIA_UNO = "CATEGORIA-UNO";
	public static final String CARPETA_EIA = "EIA";
	public static final String CARPETA_PARTICIPACION_SOCIAL = "PARTICIPACION_SOCIAL";
	public static final String CARPETA_COORDENADAS = "COORDENADAS";
	public static final String CARPETA_PARTICIPACION_CIUDADANA = "PARTICIPACION_CIUDADANA";
	public static final String CARPETA_ARCHIVACION = "ARCHIVACION_PROYECTO";
	public static final String AYUDA_GENERACION_DESECHOS_ESPECIALES_O_PELIGROSOS = "Generaci\u00F3n de Desechos (Anexo B y C).pdf";
	public static final String AYUDA_EMPLEO_SUSTANCIAS_QUIMICAS = "Empleo de Sustancias(Anexo A).pdf";
	public static final String AYUDA_TRANSPORTE_SUSTANCIAS_QUIMICAS_PELIGROSAS = "Transporte de Sustancias(Anexo A).pdf";
	public static final String PLANTILLA_AGREGAR_PUNTOS_MONITOREO = "Plantilla de puntos de monitoreo.xlsx";
	public static final String EIA_INDICACIONES_PARA_GENERAR_DOCUMENTO_ADJUNTO = "Indicaciones_para_generar_documento_adjunto.pdf";
    public static final String PLANTILLA_ETIQUETA = "Plantilla de etiqueta.xls";
    public static final String NOMBRE_DOCUMENTO_REGISTRO_GENERADOR_APROBACION = "Documento de aprobación";
    public static final String NOMBRE_DOCUMENTO_REGISTRO_GENERADOR_GeNERADOR = "Documento de registro generador";
    public static final String NOMBRE_DOCUMENTO_REGISTRO_GENERADOR_OBSERVACION = "Documento de observación";
    public static final String PLANTILLA_COMPONENTE_FAUNA = "Plantilla componente fauna.xls";
    public static final String PLANTILLA_COMPONENTE_FLORA = "Plantilla componente flora.xls";
    public static final String CARPETA_ARCHIVO_PROYECTO = "ARCHIVO_PROYECTO";
    public static final String ARCHIVO_MASIVO_PROYECTOS = "Plantilla_Archivo_Masivo.xls";
	// CERTIFICADO DE INTERSECCION
	public static final Integer ID_CAPA_SNAP = 3;
	public static final Integer ID_CAPA_BP = 1;
	public static final Integer ID_CAPA_PFE = 8;
	public static final Integer ID_CAPA_RAMSAR = 11;

	public static final String CAPA_SNAP = "SNAP";
	public static final String CAPA_BP = "BP";
	public static final String CAPA_PFE = "PFE";
	public static final String CAPA_RAMSAR_AREA = "RAMSAR_AREA";
	public static final String CAPA_RAMSAR_PUNTO = "RAMSAR_PUNTO";
	public static final Integer ID_CAPA_RAMSAR_AREA = 11;
	public static final Integer ID_CAPA_RAMSAR_PUNTO = 12;
	public static final String CAPAS_AMORTIGUAMIENTO = "AMORTIGUAMIENTO";
    public static final String CARPETA_CARTIFICADO_INTERSECCION_ALFRESCO = "Certificado-Interseccion";
	public static final String CARPETA_REQUISITOS_PREVIOS = "Requisitos-Previos";
	public static final String SESSION_OPCIONES_EIA = "opcionesEia";

	public static final String REST_MAP_PARAMETER_NAME = "nombreParametros";
	public static final String REST_MAP_PARAMETER_OBJECT = "objetoParametros";

	// UTILES
	public static final String SEPARADOR = ";;";
	public static final String PATH_SVG_DIAGRAMA_PROCESO = "/WEB-INF/svg/procesos/";
	public static final String SPLIT_CARGA_ARCHIVOS = "\t";
	public static final String ESTADO_PROCESO_NO_INICIADO = "No iniciado";
	public static final String ESTADO_PROCESO_ABORTADO = "Abortado";
	public static final String ESTADO_TAREA_INICIADA = "En curso";
	public static final String ESTADO_TAREA_COMPLETADA = "Completada";
	public static final String ESTADO_EVENTO_TAREA_COMPLETADA = "COMPLETED";
	public static final String SIGLAS_TIPO_AREA_PC = "PC";
	public static final String SECTOR_HIDROCARBURO_CODIGO = "21.01";
	public static final Integer ID_DIRECCION_NACIONAL_CONTROL_AMBIENTAL = 257;
	public static final String ROL_DIRECTOR = "AUTORIDAD AMBIENTAL2 MAE";
	public static final String ROL_DIRECTOR_PROVINCIAL = "AUTORIDAD AMBIENTAL2";
	public static final String NOMBRE_PROPIEDADES_UTILS = "ec/gob/ambiente/core/resources/utils_es.properties";
	public static final String MENSAJES_PROPIEDADES_UTILS = "ec/gob/ambiente/core/resources/mensajes.properties";
	public static final String ROL_DIRECTOR_EA = "AUTORIDAD AMBIENTAL";
	public static final String ROL_TEMP_AUTORIDAD_AMBIENTAL = "TEMP AUTORIDAD AMBIENTAL";
	public static final String CARPETA_RETCE_INCLUSION="Retce-Inclusion";
	public static final String SIGLAS_INSTITUCION = getSiglasInstitucion();
	public static final String SIGLAS_SUIA = "SUIA";
	public static final String SIGLAS_DIRECCION_NACIONAL_BIODIVERSIDAD= "DNB";
	public static final String SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL= "DRA";
	public static final String SIGLAS_DIRECCION_BOSQUES= "DB";
	public static final String SIGLAS_DIRECCION_AREAS_PROTEGIDAS= "DAPOFC";
	public static final String SIGLAS_DIRECCION_FINANCIERA= "DF";
	public static final String SIGLAS_TIPO_AREA_OT = "OT";
	public static final String SIGLAS_TIPO_AREA_EA = "EA";
	public static final String SIGLAS_TIPO_AREA_ZONALES = "ZONALES";
	public static final String SIGLAS_SITEAA = "SITEAA";
	public static final Integer ID_TIPO_AREA_PC = 1;
	public static final Integer ID_PARQUE_NACIONAL_GALAPAGOS = 272;
	public static final String SIGLAS_SUBSECRETARIA_PATRIMONIO_NATURAL= "SPN";
	public static final String SIGLAS_DIRECCION_NORMATIVA_CONTROL= "DNCA";
	public static final String SIGLAS_GRECI= "GRECI";
	public static final String SIGLAS_DIRECCION_SUSTANCIAS_RESIDUOS_DESECHOS= "DSRD";
	public static final String CARGO_AUTORIDAD_ZONAL= "DIRECTOR ZONAL";
	public static final String CODIGO_INEC_GALAPAGOS = "20";
	public static final String TASK_NAME_DESCARGA_GUIAS_ESIA = "Descargar GuIas para elaboracion de EsIA";
	public static final String NEW_TASK_NAME_DESCARGA_GUIAS_ESIA = "Ingreso de estudio de impacto ambiental";
	public static final String NOMBRE_INSTITUCION = "MINISTERIO DEL AMBIENTE, AGUA Y TRANSICIÓN ECOLÓGICA";
	public static final String ACTIVIDAD_SUBESTACION_ELECTRICA_PADRE = "D3510.02";
	public static final String ACTIVIDAD_SUBESTACION_ELECTRICA_HIJO = "D3510.02.01";
	public static final String ACTIVIDAD_SUBESTACION_SUBTRANSMISION = "Líneas de Subtransmisión menor o igual a 138 KV y Subestaciones";
	public static final String ACTIVIDAD_SUBESTACION_SUBTRANSMISION_MAYOR = "Líneas de Transmisión menor o igual a 230 KV con una longitud de 10 km y Subestaciones";
	public static final String ACTIVIDAD_ESCOMBRERAS_RA = "Menor o igual a 20000 m3 o superficie menor o igual a 2 ha";
	public static final String ROL_TECNICO_VIABILIDAD_DB = "TÉCNICO VIABILIDAD DB";
	public static final String ACTIVIDAD_MINERIA_CONTRATO = "Actividades de minería artesanal bajo contratos de operación";
	public static final String ACTIVIDAD_TIPO_MINERIA_CONTRATO = "¿Su actividad es de minería ARTESANAL?";
	private static final Logger LOG = Logger.getLogger(Constantes.class);

	// TIPO RGD
	public static final String TIPO_RGD_AAA = "RGDAAA";
	public static final String TIPO_RGD_REP = "RGDREP";
	
	// MARCO LEGAL
	public static final String ARTICULO_REGISTRO = "REGISTRO";
	public static final String ARTICULO_LICENCIAMIENTO = "LICENCIA";
	
	// tipos de gestion
	public static final Integer RESIDUOS_O_DESECHOS_ESPECIALES = 148, RESIDUOS_O_DESECHOS_PELIGROSOS = 149;

	// PAGOENLINEA KUSHKI
	public static final String Aux_Direccion = "MAATE";
	public static final String Aux_Pais = "Ecuador";
	public static final String Aux_Provincia = "Provincia";
	public static final String Aux_Ciudad = "Ciudad";
	public static final String respuestaKushki = "Error en el Pago";
	public static final String celular_response = "0999999999";
	public static final Integer respuesta_servicio_online = 201;
	
    public static final Double PAGO_INSPECCION_DIARIA = 80d;
    
    ///RESOLUCION AMBIENTAL
    public static final String TAREA_PAGO_FLUJO1 = "Generar NUT de pago";
    public static final String TAREA_PAGO_FLUJO2 = "Ingresar el valor del proyecto";
    
    //TEXTO PREDEFINIDO
    public static final Integer ID_MARCO_LEGAL = 296;
    public static final Integer ID_MARCO_LEGAL_TECNICO = 297;
    
	// Estados Vue
	public static final String SOLICITUD_ENVIADA = "110", SOLICITUD_RECEPTADA = "210", SOLICITUD_APROBADA = "320",
		AUCP_ENVIADO_A_LA_ADUANA = "510", CORRECCION_SOLICITADA = "150", SOLICITUD_CORRECCION_APROBADA = "340",
		SOLICITUD_CORRECCION_NO_APROBADA = "350", ANULACION_SOLICITADA = "640", ANULACION_APROBADA = "650";

	public static final String SOLICITUD_APROBADA_PROCESADA = "4", CORRECCION_APROBADA_PROCESADA = "5",
			CORRECCION_NO_APROBADA_PROCESADA = "6", ANULACION_APROBADA_PROCESADA = "7";

	//Tipo de Solicitud. Para el reporte de Importación Sustancia Químicas
	/*
	public static final String SOLICITUD_AUTORIZACIONES_EMITIDAS = "1", SOLICITUD_AUTORIZACIONES_ANULADAS = "2",
		SOLICITUD_AUTORIZACIONES_PENDIENTES = "3", SOLICITUD_AUTORIZACIONES_CORREGIDAS = "4", SOLICITUD_TODAS = "0";
	*/
	
	//Estado Trámite
	public static final String TRAMITE_ATENDIDO = "Atendido", EN_TRAMITE = "En tramite";
	
	// sustancia quimica (otros)
    public static final Integer ID_SUSTANCIA_OTROS = 5926;
    
    //Configuraciones de Entrada
	public static final String URL_ECUAPASS = "link.url.ecuapass";
	public static void resetConfigurations() {
		BeanLocator.getInstance(ConfigurationBean.class).resetConfigurations();
	}

	public static String getMessageResourceString(String key) {
		InputStream inputStream = Constantes.class.getClassLoader().getResourceAsStream(
				Constantes.MENSAJES_PROPIEDADES_UTILS);
		Properties prop = new Properties();
		try {
			prop.load(inputStream);
			return prop.getProperty(key);
		} catch (IOException e) {
			LOG.error(e, e);
		}
		return "";
	}

	public static String getRoleAreaName(String role) {
		InputStream inputStream = null;
		String result = null;
		try {
			try {
				inputStream = Constantes.class.getClassLoader()
						.getResourceAsStream(Constantes.NOMBRE_PROPIEDADES_UTILS);
				Properties prop = new Properties();
				prop.load(inputStream);
				result = prop.getProperty(role);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (inputStream != null)
					inputStream.close();
				inputStream = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	private static String loadFromAppProperties(String key) {
		String keyOverride = ConfigurationBean.KEY_OVERRIDE_INDICATOR + key;
		String result = null;

		try {
			String realAdd = System.getProperty("jboss.home.dir") + "/standalone/configuration/mae-core.properties";
			File fileToCheck = new File(realAdd);
			if (fileToCheck.exists()) {
				Properties properties = new Properties();
				FileInputStream file = null;
				try {
					file = new FileInputStream(realAdd);
					properties.load(file);
					if (properties.get(keyOverride) != null) {
						result = properties.get(keyOverride).toString();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (file != null)
						file.close();
					file = null;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (result == null)
			result = BeanLocator.getInstance(ConfigurationBean.class).getConfigurationValue(key);

		return result;
	}

	public static boolean existsProperty(String name) {
		return getPropertyAsString(name) != null;
	}

	public static String getPropertyAsString(String name) {
		return loadFromAppProperties(name);
	}

	public static boolean getPropertyAsBoolean(String name) {
		return Boolean.parseBoolean(getPropertyAsString(name));
	}

	public static int getPropertyAsInteger(String name) {
		return Integer.parseInt(getPropertyAsString(name));
	}

	public static double getPropertyAsDouble(String name) {
		return Double.parseDouble(getPropertyAsString(name));
	}

	public static char getPropertyAsChar(String name) {
		return getPropertyAsString(name).charAt(0);
	}

	public static String getUrlBusinessCentral() {
		return getPropertyAsString("server.bpms");
	}

	public static String getDeploymentId() {
//		return "ec.gob.ambiente:suia-iii:3.0.2";
		return getPropertyAsString("server.bpms.deploymentId");
	}

	public static String getSuiaDigitalSign() {
		return getPropertyAsString("app.digital.sign.jws");
	}

	public static int getTrayUpdateInterval() {
		return getPropertyAsInteger("app.tray.update.interval");
	}

	public static String getSuiaCertificadoInterseccion() {
		return getPropertyAsString("app.genera.certificadointerseccion");
	}

	public static String getPathPdfCertificadoInterseccion() {
		return getPropertyAsString("path.pdf.certificadointerseccion");
	}

	public static String getUrlSuiaDefinicionesProcesos() {
		return getPropertyAsString("app.service.listadefinicionesprocesos");
	}

	public static String getUrlSuiaEventosTarea() {
		return getPropertyAsString("app.service.listaeventostarea");
	}

	public static String getRootId() {
		return getPropertyAsString("app.alfresco.root.id");
	}

	public static String getRootStaticDocumentsId() {
		return getPropertyAsString("app.alfresco.root.static.documents.id");
	}

	public static String getAppServicesSuiaMapasCertificadoInterseccion() {
		return getPropertyAsString("app.integration.services.suia.mapas");
	}

	public static String getProcessImageUrl() {
		return getPropertyAsString("app.process.image.viewer.url");
	}

	public static String getAppIntegrationServletHydrocarbons() {
		return getPropertyAsString("app.integration.servlet.hydrocarbons");
	}

	public static String getAppIntegrationServletSuia() {
		return getPropertyAsString("app.integration.servlet.suia");
	}

	public static String getAppIntegrationServicesTrayHydrocarbons() {
		return getPropertyAsString("app.integration.services.tray.hydrocarbons");
	}

	public static String getAppIntegrationServicesTraySuia() {
		return getPropertyAsString("app.integration.services.tray.suia");
	}

	public static String getAppIntegrationServicesProjectsSuia() {
		return getPropertyAsString("app.integration.services.projects.suia");
	}

	public static String getAppIntegrationServicesRetaskingSuia() {
		return getPropertyAsString("app.integration.services.retasking.suia");
	}

	public static String getAppIntegrationPentahoDir() {
		return getPropertyAsString("app.integration.etl.pentaho.dir");
	}

	public static boolean getAppIntegrationSuiaEnabled() {
		return getPropertyAsBoolean("app.integration.suia");
	}

	public static boolean getAppIntegrationHydrocarbonsEnabled() {
		return getPropertyAsBoolean("app.integration.hydrocarbons");
	}

	public static boolean getAppIntegrationSuiaETLEnabled() {
		return getPropertyAsBoolean("app.integration.etl.suia");
	}

	public static boolean getAppIntegrationShowParameters() {
		return getPropertyAsBoolean("app.integration.show.parameters");
	}

	public static Integer getAppIntegrationServicesTimeOut() {
		return getPropertyAsInteger("app.integration.services.timeout");
	}

	public static String getNotificationService() {
		return getPropertyAsString("app.service.notifications");
	}

	public static String getLinkMaeTransparente() {
		return getPropertyAsString("link.maetransparente");
	}

	public static String getUsuariosNotificarProcesoAdministrativo() {
		return getPropertyAsString("usuarios.notificar.proceso.administrativo");
	}

	public static String getUsuariosNotificarRegistroGeneradoresDesechos() {
		return getPropertyAsString("usuarios.notificar.registro.generadores.desechos");
	}

	public static String getUrlSuiaResumenTarea() {
		return getPropertyAsString("app.service.listaresumentarea");
	}

	public static String getHttpSuiaImagenFooterMail() {
		return getPropertyAsString("path.http.footermail");
	}

	public static String getHttpSuiaImagenInfoMail() {
		return getPropertyAsString("path.http.infomail");
	}

	public static String getServicePayAddres() {
		return getPropertyAsString("app.service.suiaPay");
	}

	public static String getServicePayAddresDE() {
		return getPropertyAsString("app.service.suiaPayde");
	}
	public static String getServicePayUserWsOPS() {
		return getPropertyAsString("userWsOPS");
	}
	public static String getServicePayPwdWsOPS() {
		return getPropertyAsString("pwdWsOPS");
	}
	public static String getServicePayUserBCE() {
		return getPropertyAsString("userBCE");
	}
	public static String getServicePayPwdBCE() {
		return getPropertyAsString("pwdBCE");
	}
	
	public static String getServiceWsdlPayWs() {
		return getPropertyAsString("WsdlPayWs");
	}
	public static String getServiceWsdlPayWsIP() {
		return getPropertyAsString("WsdlPayWsIP");
	}
	public static String getServiceWsdlPayWsPuerto() {
		return getPropertyAsString("WsdlPayWsPuerto");
	}
		
	
	public static String getUrlJbpmRestService() {
		return getPropertyAsString("app.service.jbpmrest");
	}

	public static String getUrlTemplatesService() {
		return getPropertyAsString("app.service.templates");
	}

	public static String getUrlNodeInstanceLog() {
		return getUrlJbpmRestService() + "nodeinstancelog/";
	}

	public static Integer getRemoteApiTimeout() {
		return getPropertyAsInteger("server.bpms.remote.api.timeout");
	}

	public static String getUrlWsRegistroCivilSri() {
		return getPropertyAsString("app.service.bus.snap");
	}

	public static Boolean getDocumentosBorrador() {
		return existsProperty("app.documento.borrador") ? getPropertyAsBoolean("app.documento.borrador") : false;
	}

	public static String getUrlProjectSearch() {
		return getPropertyAsString("app.service.projectsearch");
	}

	public static String getUrlVehicleSearch() {
		return getPropertyAsString("app.service.vehiclesearch");
	}

	public static String getProcedimientosAdministrativosURL() {
		return getPropertyAsString("app.integration.procedimientosadministrativos.url");
	}

	public static String getRepositorioDocumentalURL() {
		return getPropertyAsString("app.integration.repositoriodocumental.url");
	}
	
	public static Boolean getPermitirRUCPasivo() {
		return existsProperty("app.ruc.pasivo") ? getPropertyAsBoolean("app.ruc.pasivo") : false;
	}

	public static String getAppIntegrationServicesListProjectsSuia() {
		String serviceUrl = getPropertyAsString("app.integration.services.projects.suia.list");
		return serviceUrl;
//		!= null ? serviceUrl
//				: "http://qa-suia.ambiente.gob.ec:8113/ambienteseam-ambienteseam/Suia2ConsultasExternas?wsdl";
	}

	public static String getWorkloadServiceURL() {
		return getPropertyAsString("app.service.user.workload");
	}

	public static String getFacilitatorServiceURL() {
		return getPropertyAsString("app.service.facilitator.request");
	}

	public static String getStartProcessServiceURL() {
		return getPropertyAsString("app.service.start.process");
	}

	public static String getPublishSPServiceURL() {
		return getPropertyAsString("app.service.publish.social.participation");
	}
	public static String getUnPublishSPServiceURL() {
		return getPropertyAsString("app.service.unpublish.social.participation");
	}
	public static String getUrlAdjuntosPromotorEstrategicoES() {
		return getPropertyAsString("app.file.social.participation.strategic");
	}
	public static String getUrlAdjuntosPromotorNoEstrategicoES() {
		return getPropertyAsString("app.file.social.participation.no.strategic");
	}
	
	public static String getDblinkBpmsSuiaiii() {
		return getPropertyAsString("dblink.bpms.suia-iii")+" user=postgres password=postgres";
	}
	
	public static String getDblinkSuiaVerde() {
		return getPropertyAsString("dblink.suia.verde")+" user=postgres password=postgres";
	}
	
	public static String getDblinkBpmsHyD() {
		return getPropertyAsString("dblink.bpms.hyd")+" user=postgres password=postgres";
	}
	
	public static String getDblinkIdeCartografia() {
		return getPropertyAsString("dblink.idecartografia")+" user=postgres password=postgres";
	}
	
	public static String getNotificacionesFiscalia() {
		return getPropertyAsString("notificaciones.fiscalia");
	}
	public static String getServicePayAddresInsertar() {
		return getPropertyAsString("app.service.suiaPayInsertar");
	}
	
	public static String getServiceEfectivoCelular() {
		return getPropertyAsString("app.habilitar.efectivo.celular");
	}
	
	public static String getReportePentahoRegistroLicencia() {
		return getPropertyAsString("link.url.reporte.pentaho.registro.licencia")+"userid=publico&password=publico&area=";
	}
	
	public static String getReportePentahoCertificado() {
		return getPropertyAsString("link.url.reporte.pentaho.certificado")+"userid=publico&password=publico&area=";
	}
	
	public static String getReportePentahoProyectosArchivados() {
		return getPropertyAsString("link.url.reporte.pentaho.proyectos.archivados")+"userid=publico&password=publico&area=";
	}
	
	public static String getReportePentahoCargaTrabajo() {
		return getPropertyAsString("link.url.reporte.pentaho.carga.trabajo")+"userid=publico&password=publico&area=";
	}
	
	public static String getReportePentahoCargaTrabajoAdmin() {
		return getPropertyAsString("link.url.reporte.pentaho.carga.trabajo.admin")+"userid=publico&password=publico&area=";
	}
	
	public static String getReportePentahoRegistroLicenciaPlantaCentral() {
		return getPropertyAsString("link.url.reporte.pentaho.registro.licencia.planta.central")+"userid=publico&password=publico";
	}
	
	public static String getReportePentahoRegistroLicenciaEnteAcreditado() {
		return getPropertyAsString("link.url.reporte.pentaho.registro.licencia.ente.acreditado")+"userid=publico&password=publico&area=";
	}
	
	public static String getReportePentahoCertificadoEnteAcreditado() {
		return getPropertyAsString("link.url.reporte.pentaho.certificado.ente.acreditado")+"userid=publico&password=publico&area=";
	}

	public static String getReportePentahoCertificadoPlantaCentral() {
		return getPropertyAsString("link.url.reporte.pentaho.certificado.planta.central")+"userid=publico&password=publico";
	}
	
	public static String getReportePentahoProyectosArchivadosEntesAcreditados() {
		return getPropertyAsString("link.url.reporte.pentaho.proyectos.archivados.ente.acreditado")+"userid=publico&password=publico&area=";
	}
	
	public static String getReportePentahoProyectosArchivadosPlantaCentral() {
		return getPropertyAsString("link.url.reporte.pentaho.proyectos.archivados.planta.central")+"userid=publico&password=publico";
	}
	
	public static String getUserWebServicesSnap() {
		return getPropertyAsString("user.web.services.snap");
	}
	
	public static String getFirmasId() {
		return getPropertyAsString("app.alfresco.firmas.id");
	}
	public static String getFirmasIdMAE() {
		return getPropertyAsString("app.alfresco.firmas.idMAE");
	}
	public static String getUsuarioMtop() {
		return getPropertyAsString("usuarios.mtop");
	}	
	//aumento variable para reporte
	public static String getReportePentahoProcesosRechazadosAceptados() {
		return getPropertyAsString("link.url.reporte.pentaho.procesos.rechazados.aceptados")+"userid=publico&password=publico";
	}
	public static String getFechaAcuerdoRgdUsoSuelos() {
		return getPropertyAsString("fecha.acuerdo.06.rgd.uso.suelos");
	}
	public static String getFechaBloqueoTdrMineria()
	{
		return getPropertyAsString("fecha.bloqueo.tdr.mineria");
	}
	
	public static String getFechaBloqueoRegistroFisico()
	{
		return getPropertyAsString("fecha.bloqueo.proyectos.fisico");
	}
	
	//Cris F: fecha para la busqueda del anterior Alfresco
	public static String getFechaAlfrescoCommunity()
	{
		return getPropertyAsString("fecha.alfresco.community");
	}
	
	//fechas para los ppc
	
	public static String getFechaProyectosSinPccAntes()
	{
		return getPropertyAsString("fecha.proyectos.sin.ppc.antes");
	}
	public static String getFechaProyectosObligatorioPccInicio()
	{
		return getPropertyAsString("fecha.proyectos.obligatorio.ppc.inicio");
	}
	public static String getFechaProyectosObligatorioPccFin()
	{
		return getPropertyAsString("fecha.proyectos.obligatorio.ppc.fin");
	}
	
	public static String getFechaProyectosOpcionalPccInicio()
	{
		return getPropertyAsString("fecha.proyectos.opcional.ppc.inicio");
	}
	public static String getFechaProyectosOpcionalPccFin()
	{
		return getPropertyAsString("fecha.proyectos.opcional.ppc.fin");
	}
	
	
	public static String getFechaProyectosSinPpcAdelante()
	{
		return getPropertyAsString("fecha.proyectos.sin.ppc.adelante");
	}
	
	public static String getDblinkSectorSubsector() {
		return getPropertyAsString("dblink.sector.subsector")+" user=postgres password=postgres";
	}
	
	public static String getFechaObligatorioMecanismosPps()
	{
		return getPropertyAsString("fecha.obligatorio.mecanismo.informacionpublicapermanente.pps");
	}
	
	public static String getReporteCampaniaBajaleAlPlastico() {
		return getPropertyAsString("link.url.reporte.pentaho.campaña.bajalealplastico")+"userid=publico&password=publico";
	}
	
	public static String getFechaAcuerdosCamaronerasOpcional()
	{
		return getPropertyAsString("fecha.acuerdos.camaroneras.opcional");
	}
	
	
	public static String getWolframRest() {
		return getPropertyAsString("wolframRest");
	}
	
	public static String getRootRcoaId() {
		return getPropertyAsString("app.alfresco.root.rcoa.id");
	}

	public static String getGenerarMapaWS() {
		return getPropertyAsString("generarMapaWS");
	}
	public static String getGenerarMapaImagenWS() {
		return getPropertyAsString("generarMapaImagenWS");
	}
	public static String getInterseccionesWS() {
		return getPropertyAsString("interseccionesWS");
	}
	
	public static boolean getRequierePagoInicial() {
		return getPropertyAsBoolean("requierePagoInicial");
	}
	
	public static boolean getFirmaDePrueba() {
		return getPropertyAsBoolean("firmaDePrueba");
	}
	
	public static boolean getAmbienteProduccion() {
		return getPropertyAsBoolean("ambiente.produccion");
	}
	
//	public static String getiniciarEIAWS() {
//		return getPropertyAsString("app.service.iniciarEIA");
//	}
	
	public static String getActividadesDoblePregunta() {
		return getPropertyAsString("actividades.doble.pregunta");
	}
	
	public static String getActividadesCremacion() {
		return getPropertyAsString("actividades.realiza.cremacion");
	}
	
	public static String getActividadesZonaRural() {
		return getPropertyAsString("actividades.zona.rural");
	}
	
	public static String getActividadesGalapagos() {
		return getPropertyAsString("actividades.galapagos");
	}
	
	public static String getReportePentahoRSQDeclaracionesOperador() {
		return getPropertyAsString("link.url.reporte.pentaho.rsq.delcaracion.operador")+"userid=publico&password=publico";
	}
	
	public static String getReportePentahoRSQDeclaracionesTecnico() {
		return getPropertyAsString("link.url.reporte.pentaho.rsq.delcaracion.tecnico")+"userid=publico&password=publico";
	}
	
	public static String getActividadesRecuperacionMateriales() {
		return getPropertyAsString("actividades.recuperacion.materiales");
	}
	
	public static String getActividadesBloquearGestionTransporte() {
		return getPropertyAsString("actividades.bloquear.gestion.transporte");
	}
	
	public static String getUrlBusinessCentralHidro() {
		return getPropertyAsString("server.bpms.hidrocarburos");
	}
	
	public static String getDeploymentIdHidro() {
		return getPropertyAsString("server.bpms.hidrocarburos.deploymentId");
	}
	
	public static String getActividadesExcepcionTransporteSustanciasQuimicas() {
		return getPropertyAsString("actividades.excepcion.transporte.sustancias.quimicas");
	}
	
	public static String getDblinkIdeMaae() {
		return getPropertyAsString("dblink.idemaae")+" user=postgres password=postgres";
	}
	
	public static String getActualizarMapaSuiaWS() {
		return getPropertyAsString("actualizarMapaSuiaWS");
	}
	
	public static String getActualizarMapaIvCatWS() {
		return getPropertyAsString("actualizarMapaIvCatWS");
	}
	
	public static String getActualizarMapaRcoaWS() {
		return getPropertyAsString("actualizarMapaRcoaWS");
	}
	
	public static String getMailNotificacionActividadCIIU() {
		return getPropertyAsString("noficaciones.actividades.ciiu");
	}
	
	public static String getFechaSuspensionPpcBypass() {
		return getPropertyAsString("fecha.suspension.ppc.bypass");
	}
	
	public static String getFechaPagosConNut() {
		return getPropertyAsString("fecha.pagos.con.nut");
	}
	
	public static String getUrlGeneracionAutomatica() {
		return getPropertyAsString("link.generacion.certificado.ambiental");
	}
	
	public static String getUrlCodigoCertificadoQR(){
		return getPropertyAsString("link.validacionCodigoQR");
	}
	
	public static String getSiglasInstitucion() {
		return getPropertyAsString("app.siglas.institucion.principal");
	}
	
	public static String getRootOpinionPublica() {
		return getPropertyAsString("app.alfresco.root.opinionPublica");
	}
	public static String getGenerarImagenCoodenadasWS() {
		return getPropertyAsString("generar.imagen.coodenadas.retce");
	}
	
	public static String getActividadesRellenosSanitarios() {
		return getPropertyAsString("actividades.rellenos.sanitarios");
	}
	
	public static String getGenerarMapaViabilidadWS() {
		return getPropertyAsString("generarMapaViabilidadWS");
	}
	
	public static String getTramiteInventarioCero() {
		return getPropertyAsString("tramite.inventario.esia");
	}
	
	public static String getAccesoImportacion() {
		return getPropertyAsString("acceso.importacion");
	}
	
	public static String getFechaBloqueoRegistroAmbientalPpcFisico() {
		return getPropertyAsString("fecha.bloqueo.registro.ambiental.ppc.fisico");
	}
	public static String getCapasIntersacaLicencia() {
		return getPropertyAsString("capasIntersacaLicencia");
	}
	public static String getActividadesCamaroneras() {
		return getPropertyAsString("actividades.camaroneras");
	}

	public static String getReportePentahoPquaTramitesAtendidos() {
		return getPropertyAsString("link.url.reporte.pentaho.pqua.tramites.atendidos")+"userid=publico&password=publico";
	}
}
