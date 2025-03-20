/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.eia.descripcion.proyecto.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.security.auth.message.callback.PrivateKeyCallback.Request;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.AdjuntosEiaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.catalogos.facade.CatalogoAnexoFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.CatalogoAnexo;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.eia.descripcion.proyecto.bean.AdjuntosEiaBean;
import ec.gob.ambiente.suia.eia.facade.EstudioImpactoAmbientalFacade;
import ec.gob.ambiente.suia.eia.facade.LicenciaAmbientalFacade;
import ec.gob.ambiente.suia.eia.ficha.bean.EstudioImpactoAmbientalBean;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;

/**
 *
 * @author Oscar Campania
 */
@ManagedBean
@ViewScoped
public class AdjuntosEiaController implements Serializable {

	private static final long serialVersionUID = 3043646445042341407L;

	@ManagedProperty(value = "#{estudioImpactoAmbientalBean}")
	@Getter
	@Setter
	private EstudioImpactoAmbientalBean estudioImpactoAmbientalBean;

	@Getter
	@Setter
	private Documento documento;

	@Getter
	@Setter
	private Documento documentoDescargar;

	@Getter
	@Setter
	private Documento documentoGeneral;

	@EJB
	private EstudioImpactoAmbientalFacade estudioImpactoAmbientalFacade;
	
	//Cris F: aumento de facade para historial
	@EJB
	private ProyectoLicenciaAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@Getter
	@Setter
	private String nombrePlantilla;

	@EJB
	private DocumentosFacade documentosFacade;

	@EJB
	private AdjuntosEiaFacade adjuntosEiaFacade;

	@Getter
	@Setter
	private AdjuntosEiaBean adjuntosEiaBean;

	@Getter
	@Setter
	private Integer codigoParametro;

	private TipoDocumentoSistema tipoDocumentoUsado;

	private TipoDocumentoSistema tipoDocumentoUsadoGen;

	@Getter
	private boolean necesitaDocGeneral;

	@Getter
	private boolean existeDocumentoAdjunto;

	@Getter
	private boolean existeDocumentoAdjuntoGeneral;

	@Getter
	@Setter
	private String tipoAnexoSeleccionado;

	@Getter
	@Setter
	private List<SelectItem> tipoAnexos;

	@Getter
	@Setter
	private List<Documento> documentos, documentosOriginales, documentosEliminadosBdd;

	@Getter
	@Setter
	private String ver;

	@Getter
	@Setter
	private Documento documentoEliminar;

	@Getter
	@Setter
	private Boolean documentoPorAdjuntar;
	// private String codigoOpcion;

	@Getter
	@Setter
	private String linkPrev;

	@Getter
	@Setter
	private String linkNext;

	@Getter
	@Setter
	private String modeAction;

	@Getter
	private boolean esZip;

	@Getter
	private boolean esXls;

	@Getter
	private boolean esXlsZipRar;

	@Getter
	private boolean tieneDescargaDoc;

	private String seccion;

	@Getter
	private String mensajeInformativo;

	@Getter
	private String mensajeInformativoAnexos;

	@Getter
	private String mensajeInformativoAdjuntosGenerales;

	@Setter
	@Getter
	private String tipoAdjunto;

	@Setter
	@Getter
	private Boolean esPdf;

	@Setter
	@Getter
	private boolean show;

	@EJB
	private ValidacionSeccionesFacade validacionSeccionesFacade;
	
    @ManagedProperty(value = "#{proyectosBean}")
    @Getter
    @Setter
    private ProyectosBean proyectosBean;

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AdjuntosEiaController.class);

	private static final int SIGLAS_ABREVIATURAS = 1;
	private static final int GLOSARIO_TERMINOS = 2;
	private static final int REFERENCIAS_BIBLIOGRAFIA = 3;
	private static final int FIRMA_RESPONSABILIDAD = 4;
	private static final int ANEXOS = 5;
	private static final int RESUMEN_EJECUTIVO = 6;
	private static final int AREA_ESTUDIO = 7;
	private static final int INVENTARIO_FORESTAL = 8;
	private static final int PLAN_DE_ACCION = 9;
	private static final int DETERMINACION_AREA_INFLUENCIA = 10;
	private static final int IDENTIFICACION_HALLAZGOS = 11;
	private static final int MEDIO_FISICO = 12;
	private static final int MEDIO_FISICO_MINERIA_NO_METALICOS = 122;
	private static final int MEDIO_BIOTICO = 13;
	private static final int LINEA_BASE = 14;
	private static final int PMA = 15;
	private static final int PLAN_MONITOREO = 16;
	private static final int CRONOGRAMA = 17;
	private static final int DESCRIPCION_PRYECTO = 18;
	@Getter
	private Boolean renderPanelDescarga = true;

	@EJB
	private CatalogoAnexoFacade catalogoAnexoFacade;	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
	
	private Integer numeroNotificaciones;
	
	private Map<String, Object> processVariables;
	
	private Documento documentoAdjuntoHistorico, documentoAdjuntoGeneralHistorico;
	
	private ProyectoLicenciamientoAmbiental proyecto;
	
	private boolean existeObservaciones;
	private String promotor;
	
	@Getter
	@Setter
	private Documento documentoOriginal, documentoGeneralOriginal;
	
	@Getter
	private List<Documento> listaDocumentoOriginal, listaDocumentoGeneralOriginal;

	@PostConstruct
	private void cargarDatos() throws CmisAlfrescoException, JbpmException {
		
		/**
		 * Cristina Flores obtener variables
		 */
		processVariables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getProcessId());
		String numNotificaciones = (String) processVariables.get("cantidadNotificaciones");
		if(numNotificaciones != null){
			numeroNotificaciones = Integer.valueOf(numNotificaciones);
		}else{
			numeroNotificaciones = 0;
		}	
		promotor = (String) processVariables.get("u_Promotor");
		if(numeroNotificaciones > 0)
			existeObservaciones = true;
		//Fin CF
		
		this.show = true;
		this.necesitaDocGeneral = false;
		this.existeDocumentoAdjunto = false;
		this.existeDocumentoAdjuntoGeneral = false;
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		Map params = externalContext.getRequestParameterMap();
		setCodigoParametro(new Integer((String) params.get("id")));
		setAdjuntosEiaBean(new AdjuntosEiaBean());
		getAdjuntosEiaBean().iniciarDatos();		
		mensajeInformativo = "Debe descargar la plantilla, llenarla y adjuntarla en la sección Documento requerido";
		cargarNombrePanel();
		this.mensajeInformativoAnexos = "Considerando la importancia que tiene la información cartográfica ingresada en el sistema "
				+ "(anexos en pdf de planos, mapas, etc), se informa al usuario que esta  documentación con sus respectivas bases de datos "
				+ "(Shapefile's),  debe ser remitida a la Subsecretaría de Calidad Ambiental en formato analógico y digital en medios "
				+ "de almacenamiento (CD’s, Memorias USB, etc), con el fin de garantizar la  adecuada gestión de información de su proyecto.";

		if (this.tipoAdjunto.equals("xls")) {
			this.esXls = true;
			this.esPdf = false;
			this.esZip = false;
		}
		if (this.tipoAdjunto.equals("pdf")) {
			this.esXls = false;
			this.esPdf = true;
			this.esZip = false;
		}
		if (this.tipoAdjunto.equals("zip")) {
			this.esXls = false;
			this.esPdf = false;
			this.esZip = true;
		}

	}

	private void cargarDatosAux() throws CmisAlfrescoException {
		setAdjuntosEiaBean(new AdjuntosEiaBean());
		getAdjuntosEiaBean().iniciarDatos();
		cargarNombrePanel();
	}

	private void cargarNombrePanel() throws CmisAlfrescoException {

		switch (getCodigoParametro()) {
			case SIGLAS_ABREVIATURAS:
				getAdjuntosEiaBean().setNombrePanel("Siglas y abreviaturas");
				// this.codigoOpcion = EiaOpciones.SIGLAS_ABREVIATURAS_HIDRO;
				this.nombrePlantilla = "Plantilla_Siglas_Abreviaturas.xls";
				this.show = false;
				cargarAdjuntosEIA(TipoDocumentoSistema.SIGLAS_ABREVIATURAS, "Adjunto");
				if(existeObservaciones){
					cargarAdjuntosEIAHistoricoAdjunto();
				}
				this.tipoDocumentoUsado = TipoDocumentoSistema.SIGLAS_ABREVIATURAS;
				this.seccion = "adjuntos";
				this.mensajeInformativo = "Descargar la plantilla, ingresar la información y adjuntar la misma en la sección documento requerido";
				this.tipoAdjunto="xls";
				//this.esZip = false;
				this.linkPrev = "/prevencion/licenciamiento-ambiental/eia/ficha/fichaTecnicaEIA.jsf";
				this.linkNext = "/prevencion/licenciamiento-ambiental/eia/introduccion/introduccion.jsf";
				break;
			case INVENTARIO_FORESTAL:
				this.necesitaDocGeneral = true;
				getAdjuntosEiaBean().setNombrePanel("Inventario forestal");
				// this.codigoOpcion = EiaOpciones.INVENTARIO_FORESTAL;
				this.nombrePlantilla = "Plantilla_Inventario_Forestal.xls";
				cargarAdjuntosEIA(TipoDocumentoSistema.INVENTARIO_FORESTAL, "Adjunto");				
				cargarAdjuntosEIA(TipoDocumentoSistema.INVENTARIO_FORESTAL_GEN, "General");
				
				if(existeObservaciones){
					cargarAdjuntosEIAHistoricoAdjunto();
					cargarAdjuntosEIAHistoricoGeneral();
				}
				this.tipoDocumentoUsado = TipoDocumentoSistema.INVENTARIO_FORESTAL;
				this.tipoDocumentoUsadoGen = TipoDocumentoSistema.INVENTARIO_FORESTAL_GEN;
				this.seccion = "adjuntos8";
				this.mensajeInformativo = "Deben descargar el formato de la plantilla del inventario forestal, en el cual se cargará la información requerida en la misma y posteriormente deberá ser anexada al campo requerido";
				this.mensajeInformativoAdjuntosGenerales = "Adjuntar el documento “Análisis de parámetros del inventario forestal“";
				this.tipoAdjunto="xls";
				//this.esZip = false;
				break;
			case PLAN_DE_ACCION:
				getAdjuntosEiaBean().setNombrePanel("Plan de acción hallazgos");
				// this.codigoOpcion = EiaOpciones.PLAN_DE_ACCION;
				this.nombrePlantilla = "Plantilla_Plan_Accion.xls";
				cargarAdjuntosEIA(TipoDocumentoSistema.PLAN_DE_ACCION, "Adjunto");
				if(existeObservaciones){
					cargarAdjuntosEIAHistoricoAdjunto();
				}
				this.tipoDocumentoUsado = TipoDocumentoSistema.PLAN_DE_ACCION;
				this.seccion = "adjuntos9";
				this.tipoAdjunto="xls";
				//this.esZip = false;
				break;
			case DETERMINACION_AREA_INFLUENCIA:
				this.necesitaDocGeneral = true;
				getAdjuntosEiaBean().setNombrePanel("Determinación del área influencia");
				// this.codigoOpcion = EiaOpciones.DETERMINACION_AREA_INFLUENCIA;
				this.nombrePlantilla = "Plantilla_Determinacion_Area_Influencia.xls";
				cargarAdjuntosEIA(TipoDocumentoSistema.DETERMINACION_AREA_INFLUENCIA, "Adjunto");
				cargarAdjuntosEIA(TipoDocumentoSistema.DETERMINACION_AREA_INFLUENCIA_GEN, "General");
				if(existeObservaciones){
					cargarAdjuntosEIAHistoricoAdjunto();
					cargarAdjuntosEIAHistoricoGeneral();
				}				
				this.tipoDocumentoUsado = TipoDocumentoSistema.DETERMINACION_AREA_INFLUENCIA;
				this.tipoDocumentoUsadoGen = TipoDocumentoSistema.DETERMINACION_AREA_INFLUENCIA_GEN;
				this.seccion = "adjuntos10";
				this.mensajeInformativo = "Deben descargar el formato de la plantilla del área de influencia, en el cual se cargará la información requerida en la misma y posteriormente deberá ser anexada al campo requerido";
				this.mensajeInformativoAdjuntosGenerales = "Adjuntar el documento del análisis de la determinación del área de influencia y áreas sensibles";
				this.tipoAdjunto="xls";
				//this.esZip = false;
				break;
			case IDENTIFICACION_HALLAZGOS:
				getAdjuntosEiaBean().setNombrePanel("Identificación de hallazgos");
				// this.codigoOpcion = EiaOpciones.IDENTIFICACION_HALLAZGOS;
				this.nombrePlantilla = "Plantilla_Identificacion_Hallazgos.xls";
				cargarAdjuntosEIA(TipoDocumentoSistema.IDENTIFICACION_HALLAZGOS, "Adjunto");
				if(existeObservaciones){
					cargarAdjuntosEIAHistoricoAdjunto();
				}	
				this.tipoDocumentoUsado = TipoDocumentoSistema.IDENTIFICACION_HALLAZGOS;
				this.seccion = "adjuntos11";
				this.tipoAdjunto="xls";
				//this.esZip = false;
				break;
			case MEDIO_FISICO:
				getAdjuntosEiaBean().setNombrePanel("Medio físico");
				// this.codigoOpcion = EiaOpciones.MEDIO_FISICO;
				this.show = false;
				this.nombrePlantilla = "Plantilla_Medio_Fisico.xls";
				cargarAdjuntosEIA(TipoDocumentoSistema.MEDIO_FISICO, "Adjunto");
				if(existeObservaciones){
					cargarAdjuntosEIAHistoricoAdjunto();
				}
				this.tipoDocumentoUsado = TipoDocumentoSistema.MEDIO_FISICO;
				this.seccion = "adjuntos12";
				this.mensajeInformativo = "Descargar el formato de la plantilla del medio físico en el cual se cargará la información primaria de levantamiento en campo o de información secundaria que aplique, posteriormente deberá ser anexada al campo requerido.";
				this.tipoAdjunto="xls";
				//this.esZip = false;
				this.linkPrev = "/prevencion/licenciamiento-ambiental/eia/adjuntos/adjuntos.jsf?id=14";
				//this.linkNext = "/prevencion/licenciamiento-ambiental/eia/medioFisico/clima.jsf?id=12";
				this.linkNext = "/prevencion/licenciamiento-ambiental/eia/adjuntos/adjuntos.jsf?id=13";
				//this.linkNext = "/prevencion/licenciamiento-ambiental/eia/adjuntos/adjuntos.jsf?id=13";
				//this.renderPanelDescarga = false;
				break;
			case MEDIO_FISICO_MINERIA_NO_METALICOS:
				getAdjuntosEiaBean().setNombrePanel("Medio físico");
				// this.codigoOpcion = EiaOpciones.MEDIO_FISICO;
				this.show = false;
				this.nombrePlantilla = "Plantilla_Medio_Fisico_Mineria_No_Metalicos.xls";
				cargarAdjuntosEIA(TipoDocumentoSistema.MEDIO_FISICO, "Adjunto");
				if(existeObservaciones){
					cargarAdjuntosEIAHistoricoAdjunto();
				}
				
				this.tipoDocumentoUsado = TipoDocumentoSistema.MEDIO_FISICO;
				this.seccion = "adjuntos12";
				this.mensajeInformativo = "Descargar el formato de la plantilla del medio físico en el cual se cargará la información primaria de levantamiento en campo o de información secundaria que aplique, posteriormente deberá ser anexada al campo requerido.";
				this.tipoAdjunto="xls";
				//this.esZip = false;
				this.linkPrev = "/prevencion/licenciamiento-ambiental/eia/adjuntos/adjuntos.jsf?id=14";
				//this.linkNext = "/prevencion/licenciamiento-ambiental/eia/medioFisico/clima.jsf?id=12";
				this.linkNext = "/prevencion/licenciamiento-ambiental/eia/adjuntos/adjuntos.jsf?id=13";
				//this.linkNext = "/prevencion/licenciamiento-ambiental/eia/adjuntos/adjuntos.jsf?id=13";
				//this.renderPanelDescarga = false;
				break;	
			case MEDIO_BIOTICO:
				this.show = false;
				getAdjuntosEiaBean().setNombrePanel("Medio biótico");
				// this.codigoOpcion = EiaOpciones.MEDIO_BIOTICO;
				this.nombrePlantilla = "Plantilla_Medio_Biotico.zip";
				cargarAdjuntosEIA(TipoDocumentoSistema.MEDIO_BIOTICO, "Adjunto");
				if(existeObservaciones){
					cargarAdjuntosEIAHistoricoAdjunto();
				}
				this.tipoDocumentoUsado = TipoDocumentoSistema.MEDIO_BIOTICO;
				this.seccion = "adjuntos13";
				this.tipoAdjunto="zip";
				//this.esZip = true;
				this.mensajeInformativo = "Descargar el instructivo general y el formato con los lineamientos para generar el informe técnico. En las plantillas del medio biótico se registra la información primaria de levantamiento en campo. Considerar todas las tablas conforme a la actividad del proyecto.";
				//this.linkPrev = "/prevencion/licenciamiento-ambiental/eia/medioFisico/nivelPresionSonora.jsf?id=12";
				//this.linkPrev = "/prevencion/licenciamiento-ambiental/eia/medioFisico/calidadSuelo.jsf?id=12";
				this.linkPrev = "/prevencion/licenciamiento-ambiental/eia/adjuntos/adjuntos.jsf?id=12";
				//this.linkPrev = "/prevencion/licenciamiento-ambiental/eia/adjuntos/adjuntos.jsf?id=12";
				//this.linkNext = "/prevencion/licenciamiento-ambiental/eia/medioBiotico/flora.jsf?id=flora";
				this.linkNext = "/prevencion/licenciamiento-ambiental/eia/descripcionObraActividad/descripcionProyectoObraActividad.jsf";
				//this.linkNext = "/prevencion/licenciamiento-ambiental/eia/descripcionObraActividad/descripcionProyectoObraActividad.jsf";
				//this.renderPanelDescarga = false;
				break;
			case LINEA_BASE:
				getAdjuntosEiaBean().setNombrePanel("Línea base");
				// this.codigoOpcion = EiaOpciones.LINEA_BASE;
				this.nombrePlantilla = "Plantilla_Linea_Base.xls";
				cargarAdjuntosEIA(TipoDocumentoSistema.LINEA_BASE, "Adjunto");
				if(existeObservaciones){
					cargarAdjuntosEIAHistoricoAdjunto();
				}
				this.tipoDocumentoUsado = TipoDocumentoSistema.LINEA_BASE;
				this.seccion = "adjuntos14";
				//this.mensajeInformativo = "Adjuntar documento de Estudio de Línea Base, de conformidad con los Términos de Referencia del Sector";
				//Alejandro 20151123
				this.mensajeInformativo = "Adjuntar documento de Estudio de Línea Base, de conformidad con los Términos de Referencia del Sector. Debe contener Medio Físico, Medio Biótico y Medio Social.";
				this.tipoAdjunto="pdf";
				//this.esZip = false;
				this.linkPrev = "/prevencion/licenciamiento-ambiental/eia/definicionAreaEstudio/definicionAreaEstudio.jsf";
				this.linkNext = "/prevencion/licenciamiento-ambiental/eia/adjuntos/adjuntos.jsf?id=12";
				this.renderPanelDescarga=false;
				break;
			case PMA:
				getAdjuntosEiaBean().setNombrePanel("Plan de manejo ambiental ");
				// this.codigoOpcion = EiaOpciones.LINEA_BASE;
				this.nombrePlantilla = "Plantilla_Plan_Manejo_Ambiental.xls";
				cargarAdjuntosEIA(TipoDocumentoSistema.PLAN_MANEJO_AMBIENTAL, "Adjunto");
				if(existeObservaciones){
					cargarAdjuntosEIAHistoricoAdjunto();
				}
				this.tipoDocumentoUsado = TipoDocumentoSistema.PLAN_MANEJO_AMBIENTAL;
				this.seccion = "adjuntos15";
				this.mensajeInformativo = "Descargar el formato de la plantilla del PMA, en el cual se cargará la información en la misma y posteriormente deberá ser anexada al campo requerido";
				this.tipoAdjunto="xls";
				//this.esZip = false;
				break;
			case PLAN_MONITOREO:
				getAdjuntosEiaBean().setNombrePanel("Plan de monitoreo");
				// this.codigoOpcion = EiaOpciones.LINEA_BASE;
				this.nombrePlantilla = "Plantilla_Plan_Monitoreo.xls";
				cargarAdjuntosEIA(TipoDocumentoSistema.PLAN_MONITOREO, "Adjunto");
				if(existeObservaciones){
					cargarAdjuntosEIAHistoricoAdjunto();
				}
				this.tipoDocumentoUsado = TipoDocumentoSistema.PLAN_MONITOREO;
				this.seccion = "adjuntos16";
				this.mensajeInformativo = "Deben descargar el formato de la plantilla del Plan de Monitoreo, en el cual se cargará la información en la misma y posteriormente deberá ser anexada al campo requerido";
				this.tipoAdjunto="xls";
				//this.esZip = false;
				break;
			case DESCRIPCION_PRYECTO:
				getAdjuntosEiaBean().setNombrePanel("Descripción del proyecto, obra o actividad");
				// this.codigoOpcion = EiaOpciones.LINEA_BASE;
				this.nombrePlantilla = "Plantilla_Descripcion_Proyecto.xls";
				cargarAdjuntosEIA(TipoDocumentoSistema.DESCRIPCION_PROYECTO, "Adjunto");
				if(existeObservaciones){
					cargarAdjuntosEIAHistoricoAdjunto();
				}
				this.tipoDocumentoUsado = TipoDocumentoSistema.DESCRIPCION_PROYECTO;
				this.seccion = "gruposTaxonomicos";
				this.mensajeInformativo = "Adjuntar documento de Descripción del Proyecto, de conformidad con los Términos de Referencia del Sector";
				this.tipoAdjunto="xls";
				//this.esZip = false;
				break;
			case CRONOGRAMA:
				getAdjuntosEiaBean().setNombrePanel("Cronograma valorado del PMA");
				// this.codigoOpcion = EiaOpciones.LINEA_BASE;
				this.nombrePlantilla = "Plantilla_Cronograma_Valorado.xls";
				cargarAdjuntosEIA(TipoDocumentoSistema.CRONOGRAMA, "Adjunto");
				if(existeObservaciones){
					cargarAdjuntosEIAHistoricoAdjunto();
				}
				this.tipoDocumentoUsado = TipoDocumentoSistema.CRONOGRAMA;
				this.seccion = "adjuntos17";
				this.mensajeInformativo = "Deben descargar el formato de la plantilla del Cronograma Valorado, en el cual se cargará la información en la misma y posteriormente deberá ser anexada al campo requerido";
				this.tipoAdjunto="xls";
				//this.esZip = false;
				break;
			case ANEXOS:
				getAdjuntosEiaBean().setNombrePanel("Anexos");
				// this.codigoOpcion = EiaOpciones.ANEXOS_HIDRO;
				this.nombrePlantilla = "Plantilla_Anexos.xls";
				this.tipoDocumentoUsado = TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS;
				documentos = new ArrayList<Documento>();
				documento = new Documento();
				documentoDescargar = new Documento();
				cargarTipoAnexos();
				//MarielaG
				//cambio metodo de busqueda de documentos
				cargarListaAnexosTodos(TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS);
				this.seccion = "anexos5";
				this.tipoAdjunto="xls";
				//this.esZip = false;
				break;
			//GLOSARIO DE TERMINOS HIDROCARBURO (PROBANDO)
			case GLOSARIO_TERMINOS:
				getAdjuntosEiaBean().setNombrePanel("Glosario de Términos");
				// this.codigoOpcion = EiaOpciones.ANEXOS_HIDRO;
				this.nombrePlantilla = "Plantilla_Anexos.xls";
				this.tipoDocumentoUsado = TipoDocumentoSistema.TIPO_DOCUMENTO_GLOSARIO_TERMINOS;
				documentos = new ArrayList<Documento>();
				documento = new Documento();
				documentoDescargar = new Documento();
				cargarTipoAnexos();
				cargarListaAnexos(TipoDocumentoSistema.TIPO_DOCUMENTO_GLOSARIO_TERMINOS);
				this.seccion = "glosario de términos2";
				this.tipoAdjunto="xls";
				//this.esZip = false;
				break;
			case REFERENCIAS_BIBLIOGRAFIA:
				getAdjuntosEiaBean().setNombrePanel("Referencia Bibliografica");
				// this.codigoOpcion = EiaOpciones.ANEXOS_HIDRO;
				this.nombrePlantilla = "Plantilla_Anexos.xls";
				this.tipoDocumentoUsado = TipoDocumentoSistema.TIPO_DOCUMENTO_REFERENCIAS_BIBLIOGRAFIA;
				documentos = new ArrayList<Documento>();
				documento = new Documento();
				documentoDescargar = new Documento();
				cargarTipoAnexos();
				cargarListaAnexos(TipoDocumentoSistema.TIPO_DOCUMENTO_REFERENCIAS_BIBLIOGRAFIA);
				this.seccion = "referencia bibliografica3";
				this.tipoAdjunto="xls";
				//this.esZip = false;
				break;
			case RESUMEN_EJECUTIVO:
				getAdjuntosEiaBean().setNombrePanel("Resumen ejecutivo");
				// this.codigoOpcion = EiaOpciones.RESUMEN_EJECUTIVO_HIDRO;
				this.nombrePlantilla = "Plantilla_Resumen_Ejecutivo.xls";
				this.tipoAdjunto="xls";
				//this.esZip = false;
				break;
			case AREA_ESTUDIO:
				getAdjuntosEiaBean().setNombrePanel("Definición del área de estudio");
				this.tipoAdjunto="xls";
				//this.esZip = false;
				break;
			default:
				break;
		}
	}

	/**
	 * Carga el comboBox de Tipos de Anexo
	 */
	private void cargarTipoAnexos() {

		tipoAnexos = new ArrayList<SelectItem>();
		List<CatalogoAnexo> catalogoAnexo = catalogoAnexoFacade.obtenerListaAnexoParents();
		for (CatalogoAnexo catalogo : catalogoAnexo) {

			if (!catalogo.isCategoriaFinal()) {
				SelectItemGroup g1 = new SelectItemGroup(catalogo.getNombre());
				g1.setDescription(catalogo.getDescripcion());
				SelectItem[] hijos=new SelectItem[catalogo.getCatalogoAnexoHijos().size()];				
				for (CatalogoAnexo hijo : catalogo.getCatalogoAnexoHijos()) {
					if(hijo.getEstado())
					{
						SelectItem selectItem=new SelectItem(hijo.getNombre(), hijo.getNombre());
						hijos[catalogo.getCatalogoAnexoHijos().indexOf(hijo)]=selectItem;
					}
										
				}				
				g1.setSelectItems(hijos);
				tipoAnexos.add(g1);
			}
		}
	}

	public Documento descargarAlfresco(Documento documento) throws CmisAlfrescoException {
		byte[] documentoContenido = null;
		if (documento != null && documento.getIdAlfresco() != null)
			documentoContenido = documentosFacade.descargar(documento.getIdAlfresco());
		if (documentoContenido != null)
			documento.setContenidoDocumento(documentoContenido);
		return documento;
	}

	public void uploadListenerDocumentosEIA(FileUploadEvent event) {
		documento = this.uploadListener(event, EstudioImpactoAmbiental.class, "xls");
		this.existeDocumentoAdjunto = true;
		// Entidad que tiene el documento adjuntado
		// getPuntoRecuperacion().setCertificadoCompatibilidadUsoSuelos(documento);
	}

	public void uploadListenerAnexos(FileUploadEvent event) {

		String descripcion = documento.getDescripcion();
		documento = this.uploadListener(event, EstudioImpactoAmbiental.class, "pdf");
		documento.setDescripcion(descripcion);

		// Entidad que tiene el documento adjuntado
		// getPuntoRecuperacion().setCertificadoCompatibilidadUsoSuelos(documento);
	}	

	public void uploadListenerDocumentosEIAGenerales(FileUploadEvent event) {
		documentoGeneral = this.uploadListener(event, EstudioImpactoAmbiental.class, "pdf");
		this.existeDocumentoAdjuntoGeneral = true;
		// Entidad que tiene el documento adjuntado
		// getPuntoRecuperacion().setCertificadoCompatibilidadUsoSuelos(documento);
	}

	private Documento uploadListener(FileUploadEvent event, Class<?> clazz, String extension) {
		byte[] contenidoDocumento = event.getFile().getContents();
		Documento documento = crearDocumento(contenidoDocumento, clazz, extension);
		documento.setNombre(event.getFile().getFileName());
		return documento;
	}

	public void aniadirDocumento() throws ServiceException {

		if (!this.verificarExistenciaAnexo()) {
			try {
				documento.setIdTable(this.obtenerEIA().getId());
				documento.setEstado(true);
				
				// documento.setDescripcion(this.tipoAnexoSeleccionado);

				Documento documentoGuardado = documentosFacade.guardarDocumentoAlfresco(proyectosBean.getProyecto().getCodigo(), Constantes.CARPETA_EIA, 0L, documento, this.tipoDocumentoUsado, null);

				documentoPorAdjuntar = true;
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);

				this.documentos.add(documento);
				
				if(existeObservaciones){
					documentoGuardado.setNumeroNotificacion(numeroNotificaciones);
					documentosFacade.actualizarDocumento(documentoGuardado);
				}
				
				documento = new Documento();

				validacionSeccionesFacade.guardarValidacionSeccion("EIA", this.seccion, this.obtenerEIA().getId()
						.toString());			
				

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			this.documento = new Documento();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ANEXO_EXISTENTE);
		}

	}

	private boolean verificarExistenciaAnexo() {

		for (Documento doc : this.documentos) {
			if (doc.getDescripcion().equalsIgnoreCase(this.documento.getDescripcion()) && !doc.getDescripcion().equalsIgnoreCase("Otro")) {
				return true;
			}
		}

		return false;
	}

	public void selecionarDocumento(Documento doc) {
		documentoEliminar = new Documento();
		documentoEliminar = doc;
	}

	public void eliminarDocumento() {
		
		try {
			if(documentoEliminar.getNumeroNotificacion() == null ||
					(documentoEliminar.getNumeroNotificacion() != null && documentoEliminar.getNumeroNotificacion() < numeroNotificaciones)){
				Documento documentoHistorico = documentoEliminar.clone();
				documentoHistorico.setIdHistorico(documentoEliminar.getId());
				documentoHistorico.setNumeroNotificacion(numeroNotificaciones);
				documentosFacade.actualizarDocumento(documentoHistorico);	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		documentoEliminar.setEstado(false);
		this.documentosFacade.eliminarDocumento(documentoEliminar);
		this.documentos.remove(documentoEliminar);
		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
	}

	public void asignarDocumento(Documento doc) {

		this.documento = doc;
	}

	/**
	 * Crea el documento
	 *
	 * @param contenidoDocumento
	 *            arreglo de bytes
	 * @param clazz
	 *            Clase a la cual se va a ligar al documento
	 * @param extension
	 *            extension del archivo
	 * @return Objeto de tipo Documento
	 */
	public Documento crearDocumento(byte[] contenidoDocumento, Class<?> clazz, String extension) {
		Documento documento = new Documento();
		documento.setContenidoDocumento(contenidoDocumento);
		documento.setNombreTabla(clazz.getSimpleName());
		documento.setIdTable(0);
		documento.setExtesion("." + extension);

		documento.setMime(extension == "pdf" ? "application/pdf" : "application/vnd.ms-excel");
		return documento;
	}

	/**
	 * Crea un anexo y lo adhiere a la lista de anexos
	 */

	/*
	 * Crear la entidad documento y subir al alfresco
	 */
	public void salvarDocumento() {
		try {
			
			if (this.necesitaDocGeneral) {

				// this.documentoGeneral = this.descargarAlfresco(this.documentoGeneral);

				if (documentoGeneral != null && this.documentoGeneral.getContenidoDocumento() != null) {

					documentoGeneral.setIdTable(this.obtenerEIA().getId());
					documentoGeneral.setDescripcion("Documento General");
					documentoGeneral.setEstado(true);

					
					Documento documentoG = documentosFacade.guardarDocumentoAlfresco(proyectosBean.getProyecto().getCodigo(), Constantes.CARPETA_EIA, 0L, documentoGeneral,
									this.tipoDocumentoUsadoGen, null);
					
					if(existeObservaciones && documentoAdjuntoGeneralHistorico != null){
						documentoAdjuntoGeneralHistorico.setIdHistorico(documentoG.getId());
						documentoAdjuntoGeneralHistorico.setNumeroNotificacion(numeroNotificaciones);
						documentosFacade.actualizarDocumento(documentoAdjuntoGeneralHistorico);
					}
				} else {
					JsfUtil.addMessageError(JsfUtil.MESSAGE_NO_EXISTE_DOCUMENTO);
					return;
				}

			}

			// this.documento = this.descargarAlfresco(this.documento);
			if (documento != null && documento.getContenidoDocumento() != null) {

				documento.setIdTable(this.obtenerEIA().getId());
				documento.setDescripcion("Estudio Impacto Ambiental");
				documento.setEstado(true);

				
				Documento documentoA = documentosFacade.guardarDocumentoAlfresco(proyectosBean.getProyecto().getCodigo(), Constantes.CARPETA_EIA, 0L, documento, this.tipoDocumentoUsado, null);
				
				if(existeObservaciones && documentoAdjuntoHistorico != null){
					documentoAdjuntoHistorico.setIdHistorico(documentoA.getId());
					documentoAdjuntoHistorico.setNumeroNotificacion(numeroNotificaciones);
					documentosFacade.actualizarDocumento(documentoAdjuntoHistorico);
				}

			} else {
				JsfUtil.addMessageError(JsfUtil.MESSAGE_NO_EXISTE_DOCUMENTO);
				return;
			}

			validacionSeccionesFacade.guardarValidacionSeccion("EIA", this.seccion, this.obtenerEIA().getId()
					.toString());			
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);	
			
			/**
			 * Cris F: aumento para que aparezca el historial
			 */
			JsfUtil.redirectTo("/prevencion/licenciamiento-ambiental/eia/adjuntos/adjuntos.jsf?id=" + getCodigoParametro());
					

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public StreamedContent getStreamContent(String tipo) throws Exception {
		DefaultStreamedContent content = null;
		try {

			if (tipo.equals("General")) {

				if (documentoGeneral != null && documentoGeneral.getNombre() != null
						&& documentoGeneral.getContenidoDocumento() != null) {
					content = new DefaultStreamedContent(new ByteArrayInputStream(
							documentoGeneral.getContenidoDocumento()));
					content.setName(documentoGeneral.getNombre());
				} else
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
			} else {
				if (documento != null && documento.getNombre() != null && documento.getContenidoDocumento() != null) {
					content = new DefaultStreamedContent(new ByteArrayInputStream(documento.getContenidoDocumento()));
					content.setName(documento.getNombre());
				} else
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
			}

		} catch (Exception exception) {
			LOG.error(JsfUtil.MESSAGE_ERROR_ALFRESCO, exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
		}
		return content;
	}
	
	//Cris F: cambio para descargar documentos de mas de 2GB
	public StreamedContent getStreamContentAnexo(Documento documentoDescargar) throws Exception {
//		DefaultStreamedContent content = null;
//
//		documentoDescargar = this.descargarAlfresco(documentoDescargar);
//
//		try {
//			if (documentoDescargar != null && documentoDescargar.getNombre() != null
//					&& documentoDescargar.getContenidoDocumento() != null) {
//				content = new DefaultStreamedContent(new ByteArrayInputStream(
//						documentoDescargar.getContenidoDocumento()));
//				content.setName(documentoDescargar.getNombre());
//			} else
//				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
//
//		} catch (Exception exception) {
//			LOG.error(JsfUtil.MESSAGE_ERROR_ALFRESCO, exception);
//			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
//		}
//		return content;
		
		DefaultStreamedContent content = null;
        Documento documento = this.descargarAlfrescoFile(documentoDescargar);
        try {
            if (documento != null && documento.getNombre() != null
                    && documento.getContenidoDocumentoFile() != null) {
            	File file=documento.getContenidoDocumentoFile();
            	content = new DefaultStreamedContent(new FileInputStream(file), new MimetypesFileTypeMap().getContentType(file));
                content.setName(documento.getNombre());
            } else {
                JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
            }
        } catch (Exception exception) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
        }
        return content;	
	}
	
	public Documento descargarAlfrescoFile(Documento documento) throws CmisAlfrescoException {
        File documentoContenido = null;
        if (documento != null && documento.getIdAlfresco() != null)
			try {
				documentoContenido = documentosFacade.descargarFile(documento.getIdAlfresco());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
        if (documentoContenido != null)
            documento.setContenidoDocumentoFile(documentoContenido);
        return documento;
    }
 //fin de cambio

	public void descargarPlantilla() throws CmisAlfrescoException, IOException {

		JsfUtil.descargarMimeType(documentosFacade.descargarDocumentoPorNombre(this.nombrePlantilla),
				this.nombrePlantilla, this.esZip ? "zip" : "xls", this.esZip ? "application/zip"
						: "application/vnd.ms-excel");

	}

	public void cancelar() {
		JsfUtil.redirectTo("/prevencion/licenciamiento-ambiental/eia/adjuntos/adjuntos.jsf?id=" + getCodigoParametro());
	}

	private EstudioImpactoAmbiental obtenerEIA() {
		EstudioImpactoAmbiental es = (EstudioImpactoAmbiental) JsfUtil
				.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT);

		return es;
	}

	private void cargarAdjuntosEIA(TipoDocumentoSistema tipoDocumento, String tipo) throws CmisAlfrescoException {

		List<Documento> documentosXEIA = documentosFacade.documentoXTablaIdXIdDoc(this.obtenerEIA().getId(),
				"EstudioImpactoAmbiental", tipoDocumento);

		if (documentosXEIA.size() > 0) {

			if (tipo.equalsIgnoreCase("General")) {
				this.documentoGeneral = documentosXEIA.get(0);
				this.descargarAlfresco(this.documentoGeneral);
				this.existeDocumentoAdjuntoGeneral = true;

			} else {
				this.documento = documentosXEIA.get(0);
				this.descargarAlfresco(this.documento);
				this.existeDocumentoAdjunto = true;
			}
		}
	}

	private void cargarListaAnexos(TipoDocumentoSistema tipoDocumento) throws CmisAlfrescoException {

		documentos = documentosFacade.documentoXTablaIdXIdDoc(this.obtenerEIA().getId(), "EstudioImpactoAmbiental",
				tipoDocumento);
	}

	public void linkNextVal() {
		JsfUtil.redirectTo(this.linkNext);
	}

	public void linkPrevVal() {
		JsfUtil.redirectTo(this.linkPrev);
	}

	/*
	 * Validar anexos obligatorios
	 */
	public void validarAnexos() {
		int cont = 0;
		if (this.documentos.size() > 0) {

			for (Documento doc : this.documentos) {
				if (doc.getDescripcion().equals("Certificado de depósitos de especímenes (obligatorio)")
						|| doc.getDescripcion().equals("Glosario de Términos (obligatorio)") 
						|| doc.getDescripcion().equals("Estudio de Impacto Ambiental (obligatorio)")) {
					cont += 1;
				}
			}

			if (cont < 3) {
				JsfUtil.addMessageError("Debe adjuntar todos los documentos obligatorios.");
			} else {
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			}
			
			JsfUtil.redirectTo("/prevencion/licenciamiento-ambiental/eia/anexos/anexos.jsf?id=5&amp;faces-redirect=true");
		} else {
			JsfUtil.addMessageError("Debe adjuntar todos los documentos obligatorios.");
		}
	}

	/**
	 * Cristina F: Cargar los documentos para historico
	 */	
	private void cargarAdjuntosEIAHistoricoGeneral(){
		try{
			if(existeDocumentoAdjuntoGeneral){
				documentoAdjuntoGeneralHistorico = validarDocumentoHistorico(documentoGeneral, 1);				
			}			
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
	
	private void cargarAdjuntosEIAHistoricoAdjunto(){
		try{		
			if(existeDocumentoAdjunto){
				documentoAdjuntoHistorico = validarDocumentoHistorico(documento, 2);
			}			
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
	
	/**
	 * Al actualizar un documento se guarda un nuevo registro por lo que al anterior registro del documento se coloca como historico
	 * la id del nuevo documento guardado
	 * Cris F: cambio del método para que muestre los originales en lista.
	 * @param documentoIngresado
	 * @return
	 */
	private Documento validarDocumentoHistorico(Documento documentoIngresado, Integer tipoDocumento){		
		try {
			List<Documento> documentosList = documentosFacade.getDocumentosPorIdHistorico(documentoIngresado.getId());
			listaDocumentoGeneralOriginal = new ArrayList<Documento>();
			listaDocumentoOriginal = new ArrayList<Documento>();
			
			if (documentosList != null && !documentosList.isEmpty()) {
				//**MarielaG
				//para recuperar el archivo original por tipo de documento	
				//cambio para tener los documentos en listas.
				switch (tipoDocumento) {
				case 1:
					//documentoGeneralOriginal = documentosList.get(0); 
					for(Documento documentoG : documentosList){
						listaDocumentoGeneralOriginal.add(0, documentoG);
					}
					//listaDocumentoGeneralOriginal = documentosList;
					break;
				case 2:
					//documentoOriginal = documentosList.get(0);
					for(Documento documentoG : documentosList){
						listaDocumentoOriginal.add(0, documentoG);
					}
					//listaDocumentoOriginal = documentosList;
					break;
				default:
					break;
				}
				//**
				
				boolean existeDocumentoNotificacion = false;
                Documento documentoHistoricoNotificacion = new Documento();
                for(Documento documentoHistorico : documentosList){
                	if(documentoHistorico.getNumeroNotificacion().equals(numeroNotificaciones)){
                		documentoHistoricoNotificacion = documentoHistorico;
                		existeDocumentoNotificacion = true;
                		break;
                	}
                }
				
                if(existeDocumentoNotificacion)
                	return documentoHistoricoNotificacion;
                else
                	return documentoIngresado;
                
			} else {
				listaDocumentoGeneralOriginal = new ArrayList<Documento>();
				listaDocumentoOriginal = new ArrayList<Documento>();
				return documentoIngresado;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return documentoIngresado;
		}		
	}
	
	/**
	 * MarielaG
	 * Descargar archivos originales 
	 */
	public StreamedContent getStreamContentOriginal(String tipo) throws Exception {
		DefaultStreamedContent content = null;
		try {

			if (tipo.equals("General")) {
				this.documentoGeneralOriginal = this.descargarAlfresco(this.documentoGeneralOriginal);
				if (documentoGeneralOriginal != null && documentoGeneralOriginal.getNombre() != null
						&& documentoGeneralOriginal.getContenidoDocumento() != null) {
					content = new DefaultStreamedContent(new ByteArrayInputStream(
							documentoGeneralOriginal.getContenidoDocumento()));
					content.setName(documentoGeneralOriginal.getNombre());
				} else
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
			} else {
				this.documentoOriginal = this.descargarAlfresco(this.documentoOriginal);
				if (documentoOriginal != null && documentoOriginal.getNombre() != null && documentoOriginal.getContenidoDocumento() != null) {
					content = new DefaultStreamedContent(new ByteArrayInputStream(documentoOriginal.getContenidoDocumento()));
					content.setName(documentoOriginal.getNombre());
				} else
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
			}

		} catch (Exception exception) {
			LOG.error(JsfUtil.MESSAGE_ERROR_ALFRESCO, exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
		}
		return content;
	}
	
	/**
	 * MarielaG
	 * Bucar todos los documentos existentes en la bdd 
	 */
	private void cargarListaAnexosTodos(TipoDocumentoSistema tipoDocumento) throws CmisAlfrescoException {
		List<Documento> documentosEnBdd = new ArrayList<>();
		documentosEnBdd = documentosFacade.documentosTodosXTablaIdXIdDoc(this.obtenerEIA().getId(), "EstudioImpactoAmbiental",
				tipoDocumento);
		for(Documento documentoBdd : documentosEnBdd){
			if(documentoBdd.getIdHistorico() == null){
				documentos.add(documentoBdd); //solo documentos actuales para visualizar en tabla principal
			}
		}
		
		//Si existe obs recuperar documentos originales
		if (existeObservaciones) {
//			if (!promotor.equals(loginBean.getNombreUsuario())) {
				consultarDocumentosOriginales(documentosEnBdd);
//			}
		}
	}
	
	/**
	 * MarielaG
	 * Consultar el listado de documentos ingresados antes de las correcciones
	 * y eliminados en la correccion
	 */
	private void consultarDocumentosOriginales(List<Documento>  listaDocumentos) {
		try {
			List<Documento> listaDocumentosOriginales = new ArrayList<Documento>();
			List<Documento> documentosEliminados = new ArrayList<>();
			int totalModificados = 0;
			
			for(Documento documentoBdd : listaDocumentos){
				if(documentoBdd.getNumeroNotificacion() == null ||
						!documentoBdd.getNumeroNotificacion().equals(numeroNotificaciones)){
					boolean agregarItemLista = true;
	    			//buscar si tiene historial
					for (Documento documentoHistorico : listaDocumentos) {
						if (documentoHistorico.getIdHistorico() != null  
								&& documentoHistorico.getIdHistorico().equals(documentoBdd.getId())) {
							//si existe un registro historico, no se agrega a la lista en este paso
							agregarItemLista = false;
							documentoBdd.setRegistroModificado(true);
							break;
						}
					}
					if (agregarItemLista) {
						listaDocumentosOriginales.add(documentoBdd);
					}
				} else {
					totalModificados++;
	    			//es una modificacion
	    			if(documentoBdd.getIdHistorico() == null && documentoBdd.getNumeroNotificacion().equals(numeroNotificaciones)){
	    				//es un registro nuevo
	    				//no ingresa en el lista de originales
	    				documentoBdd.setNuevoEnModificacion(true);
	    			}else{
	    				documentoBdd.setRegistroModificado(true);
	    				if(!listaDocumentosOriginales.contains(documentoBdd)){
	    					listaDocumentosOriginales.add(documentoBdd);
	    				}
	    			}
	    		}
				
				//para consultar eliminados
				if (documentoBdd.getIdHistorico() != null
						&& documentoBdd.getNumeroNotificacion() != null) {
					boolean existePunto = false;
					for (Documento itemActual : this.documentos) {
						if (itemActual.getId().equals(documentoBdd.getIdHistorico())) {
							existePunto = true;
							break;
						}
					}

					if (!existePunto) {
						documentosEliminados.add(documentoBdd);
					}
				}
			}
			
			if (totalModificados > 0){
				this.documentosOriginales = listaDocumentosOriginales;
			}
			
			this.documentosEliminadosBdd = documentosEliminados; 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Recuperar originales de documentos para adjuntos
	 * Cris F:
	 * Obtener documento historico
	 */
	public StreamedContent getStreamContentDocumentoOriginal(Documento documentoOriginal) throws Exception {
		DefaultStreamedContent content = null;
		try {
			documentoOriginal = this.descargarAlfresco(documentoOriginal);
			if (documentoOriginal != null && documentoOriginal.getNombre() != null && documentoOriginal.getContenidoDocumento() != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoOriginal.getContenidoDocumento()));
				content.setName(documentoOriginal.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
		} catch (Exception exception) {
			LOG.error(JsfUtil.MESSAGE_ERROR_ALFRESCO, exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
		}
		return content;
	}
	
	
}
