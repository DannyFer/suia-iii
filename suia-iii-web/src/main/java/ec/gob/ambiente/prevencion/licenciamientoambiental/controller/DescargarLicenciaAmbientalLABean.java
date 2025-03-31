package ec.gob.ambiente.prevencion.licenciamientoambiental.controller;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import ec.gob.ambiente.suia.domain.TipoEstudio;
import ec.gob.ambiente.suia.prevencion.licenciamientoAmbiental.facade.InformeOficioFacade;
import lombok.Getter;
import lombok.Setter;

import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.AprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.EliminacionDesechoFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.InformeTecnicoGeneralLA;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.survey.SurveyResponseFacade;
import ec.gob.ambiente.suia.tramiteresolver.RegistroGeneradorTramiteResolver;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class DescargarLicenciaAmbientalLABean implements Serializable {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(DescargarLicenciaAmbientalLABean.class);
	private static final long serialVersionUID = -3467868184633762696L;

	@EJB
	private DocumentosFacade documentosFacade;

	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;
	@EJB
	private ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade;

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@EJB
	private ProcesoFacade procesoFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
	private TaskBeanFacade taskBeanFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyecto;

	@ManagedProperty(value = "#{proyectosBean}")
	@Getter
	@Setter
	private ProyectosBean proyectosBean;

	private byte[] documento;

	private String nombreDocumento;

	@Getter
	@Setter
	private boolean descargado = false;

	@Getter
	@Setter
	private Object[] result;

	@EJB
	private AprobacionRequisitosTecnicosFacade aprobacionRequisitosTecnicosFacade;

	@Getter
	@Setter
	private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;

	@EJB
	private EliminacionDesechoFacade eliminacionDesechoFacade;

	@Getter
	@Setter
	private String mensajeFinalizar;

	@EJB
	private InformeOficioFacade informeOficioFacade;

	@PostConstruct
	public void init() {

		Map<String, Object> variables = null;
		try {
			proyecto = proyectosBean.getProyecto();
			documento = documentosFacade.descargarDocumentoAlfrescoQueryDocumentos(
					InformeTecnicoGeneralLA.class.getSimpleName(), proyecto.getId(),
					TipoDocumentoSistema.TIPO_INFORME_TECNICO_GENERAL_LA);
			nombreDocumento = "LICENCIA_AMBIENTAL.pdf";
		} catch (CmisAlfrescoException e) {
			JsfUtil.addMessageError("Error al descargar el documento. Intente más tarde.");
			LOG.error("Error al descargar el documento. Intente más tarde.", e);
		} catch (Exception e) {
			JsfUtil.addMessageError("Error al realizar la operación. Intente más tarde.");
			LOG.error("Error al realizar la operación.", e);
		}

	}

	public StreamedContent getStream() throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (documento != null) {
			descargado = true;
			content = new DefaultStreamedContent(new ByteArrayInputStream(documento), "application/pdf");
			content.setName(nombreDocumento);

		}
		return content;

	}

	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(),
				"/prevencion/licenciamiento-ambiental/documentos/descargarLicenciaAmbiental.jsf");
	}

	public void iniciarTarea() {
		if (isDescargado()) {
			setMensajeFinalizar("Estimado Usuario ha culminado satisfactoriamente la obtención de su Licencia Ambiental. ");
			InformeTecnicoGeneralLA informeTecnicoGeneralLA = informeOficioFacade.obtenerInformeTecnicoLAGeneralPorProyectoId(TipoDocumentoSistema.TIPO_INFORME_TECNICO_GENERAL_LA.getIdTipoDocumento(), proyectosBean.getProyecto().getId());
			informeTecnicoGeneralLA.setFinalizado(true);
			informeOficioFacade.guardarInformeTecnicoGeneralLA(informeTecnicoGeneralLA);
			if(generaDesechosEspeciales() || validarGenerador()) {
				setMensajeFinalizar(getMensajeFinalizar()
						+ "Para iniciar el proceso Registro/Actualización de Generador de Desechos Peligrosos "
						+ "y/o Especiales debe ir a la bandeja de tareas.");
			}
			RequestContext.getCurrentInstance().execute("PF('continuarDialog').show();");

		} else {
			JsfUtil.addMessageError("Para continuar debe descargar el documento.");
		}
	}

	/**
	 * <b> Metodo que valida si existen desechos sin codigo de generador. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 18/09/2015]
	 * </p>
	 * 
	 * @return boolean: true existe el desecho sin codigo
	 * @throws ServiceException
	 *             : Excepcion
	 */
	public boolean validarGenerador() {
		try {
			aprobacionRequisitosTecnicos = aprobacionRequisitosTecnicosFacade.getAprobacionRequisitosTecnicos(proyecto.getCodigo());
			boolean existe = false;
			if (aprobacionRequisitosTecnicos != null && aprobacionRequisitosTecnicos.isGestion()) {
				existe = eliminacionDesechoFacade.validarDesechoSinGeneradorPorProyecto(aprobacionRequisitosTecnicos.getProyecto(), aprobacionRequisitosTecnicos.getSolicitud());
			}
			return existe;
		} catch (ServiceException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * <b> Metodo que envia la aprobacion del formulario. </b>
	 * <p>
	 * [Author: JAvier Lucero, Date: 18/09/2015]
	 * </p>
	 * 
	 * @return String: url
	 * @throws JbpmException
	 */
	public String direccionar() throws JbpmException {
		Map<String, Object> data = new ConcurrentHashMap<String, Object>();
		taskBeanFacade.approveTask(loginBean.getNombreUsuario(), bandejaTareasBean.getTarea().getTaskId(),
				bandejaTareasBean.getProcessId(), data, loginBean.getPassword(), Constantes.getUrlBusinessCentral(),
				Constantes.getRemoteApiTimeout(), Constantes.getNotificationService());

		/*
		 * boolean saltarRGD = false; if
		 * (proyecto.getTipoSector().getId().equals
		 * (TipoSector.TIPO_SECTOR_HIDROCARBUROS) &&
		 * (proyecto.getCatalogoCategoria
		 * ().getCategoria().getId().equals(Categoria.CATEGORIA_III) || proyecto
		 * .getCatalogoCategoria().getCategoria().getId().equals(Categoria.
		 * CATEGORIA_IV))) { saltarRGD = true; }
		 */
		if ((validarGenerador() || generaDesechosEspeciales()) && result == null) {
			result = registroGeneradorDesechosFacade.iniciarEmisionProcesoRegistroGenerador(JsfUtil.getLoggedUser(),
					RegistroGeneradorTramiteResolver.class, proyecto);


			proyecto.setRgdEncurso(true);
			proyectoLicenciamientoAmbientalFacade.actualizarProyecto(proyecto);
		}
		JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
		return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");

	}

	public boolean generaDesechosEspeciales() {
		return proyecto.getGeneraDesechos() != null && proyecto.getGeneraDesechos();
	}

	public boolean proyectoExante() {
		return proyecto.getTipoEstudio().getId().equals(TipoEstudio.ESTUDIO_EX_ANTE);
	}

	public boolean proyectoExpost() {
		return proyecto.getTipoEstudio().getId().equals(TipoEstudio.ESTUDIO_EX_POST);
	}

	public boolean mostrar30() {
		boolean a = proyectoExpost() && (generaDesechosEspeciales() || validarGenerador());
		return a;
	}

	public boolean mostrar60() {
		boolean a = proyectoExante() && (generaDesechosEspeciales() || validarGenerador());
		return a;
	}

	public boolean mostrar00() {
		boolean a = false;
		if(proyecto.getTipoEstudio().getId().equals(TipoEstudio.AUDITORIA_FINES_LICENCIAMIENTO) ||
				proyecto.getTipoEstudio().getId().equals(TipoEstudio.ESTUDIO_EMISION_INCLUSION_AMBIENTAL))
			a = (generaDesechosEspeciales() || validarGenerador());
		return a;
	}
	
	public Boolean mostrarMsjAcuerdosCamaroneras() {
		boolean resultado = false;
		try {
			Date fechaInicioCambio=new SimpleDateFormat("yyyy-MM-dd").parse(Constantes.getFechaAcuerdosCamaronerasOpcional());
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			Date fechaFin = formatter.parse(formatter.format(proyectosBean.getProyecto().getFechaRegistro()));
			
			if ((proyecto.getCatalogoCategoria().getCodigo().equals("11.03.04")
					|| proyecto.getCatalogoCategoria().getCodigo().equals("11.03.03"))
					&& fechaFin.compareTo(fechaInicioCambio) >= 0) {
				resultado = true;
			} 
		} catch (Exception e) {
			// TODO: handle exception
		}
		return resultado;
	}

	
	/***** módulo de encuesta *****/
	
	@EJB
    private OrganizacionFacade organizacionFacade;
	
	
	@EJB 
	private SurveyResponseFacade surveyResponseFacade;
	@Getter
    @Setter
    public static String surveyLink = Constantes.getPropertyAsString("suia.survey.link");
	
	@Getter
    @Setter
    public static String reportLink = Constantes.getPropertyAsString("suia.report.link");
	/***** fin módulo de encuesta *****/
	
	@Setter
	@Getter
	private boolean showSurveyD = false;

	public void showDialogSurvey() {
		showSurveyD = true;
	}
	
	/****** módulo encuesta 
	 * @throws ServiceException ******/	
	// metodo para crear url de la encuesta
	public String urlLinkSurvey() throws ServiceException {
		String url = surveyLink;
		String usuarioUrl =proyecto.getUsuario().getNombre();
		String proyectoUrl = proyecto.getCodigo();
		String appUlr = "suia";
		Organizacion organizacion = organizacionFacade.buscarPorPersona(proyecto.getUsuario().getPersona(), proyecto.getUsuario().getNombre());
        if (organizacion != null) {
            organizacion.getNombre();
        }
        
		String tipoPerUrl = (organizacion!=null)?"juridico":"natural";
		String tipoUsr = "externo";
		url = url + "/faces/index.xhtml?" 
				+ "usrid=" + usuarioUrl + "&app=" + appUlr + "&project=" + proyectoUrl 
				+ "&tipoper=" + tipoPerUrl + "&tipouser=" + tipoUsr;
		System.out.println("enlace>>>" + url);
		return url;
	}
	
	// método para mostrar el enlace de la encuesta
	public boolean showSurvey() {
					if(surveyResponseFacade.findByProject(proyecto.getCodigo())) {
						return false;
					} else {	// no se respondió la encuesta
						return true;
					}
	}
	/****** fin módulo encuesta ******/

	
}
