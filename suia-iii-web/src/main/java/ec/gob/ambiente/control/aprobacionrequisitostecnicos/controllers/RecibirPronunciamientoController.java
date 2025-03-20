package ec.gob.ambiente.control.aprobacionrequisitostecnicos.controllers;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.AprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.EliminacionDesechoFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.InformeOficioArtFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.OficioAproReqTec;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.survey.SurveyResponseFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

/***
 * 
 * <b> Controlador para tarea recibir probunciamiento. </b>
 * 
 * @author vero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: vero $, $Date: 23/07/2015 $]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class RecibirPronunciamientoController implements Serializable {

	private static final long serialVersionUID = -4595034866077116442L;

	public static final String OFICIO_APROBACION = "Oficio_Aprobacion_Requisitos";
	public static final String TIPO_PDF = ".pdf";

	private static final Logger LOGGER = org.apache.log4j.Logger.getLogger(RecibirPronunciamientoController.class);

	@EJB
	private AprobacionRequisitosTecnicosFacade aprobacionRequisitosTecnicosFacade;

	@EJB
	private InformeOficioArtFacade informeOficioFacade;

	@EJB
	private DocumentosFacade documentosFacade;

	@EJB
	private EliminacionDesechoFacade eliminacionDesechoFacade;

	@EJB
	ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade;

	@Getter
	@Setter
	private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;

	@Getter
	private byte[] oficioDoc;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@Getter
	@Setter
	private boolean existeGenerador;

	@EJB
	private ProcesoFacade procesoFacade;
	
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
	

	private static boolean redirect;

	private int cantDias;
	
	private Map<String, Object> variables;
	
	private Boolean esproyectoCoa = false;

	@PostConstruct
	public void init() {
		try {//Hereeeee
			
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId());
			
			aprobacionRequisitosTecnicos = aprobacionRequisitosTecnicosFacade
					.recuperarCrearAprobacionRequisitosTecnicos(JsfUtil.getCurrentProcessInstanceId(),
							JsfUtil.getLoggedUser());
			OficioAproReqTec oficioArt = this.informeOficioFacade.obtenerOficioAprobacionPorArt(
					TipoDocumentoSistema.TIPO_OFICIO_APROBACION_ART, this.aprobacionRequisitosTecnicos.getId(),
					aprobacionRequisitosTecnicosFacade.getCantidadVecesObservadoInformacion(loginBean.getUsuario(),
							bandejaTareasBean.getProcessId()));
			oficioDoc = documentosFacade.descargar(oficioArt.getDocumentoOficio().getIdAlfresco());
		} catch (Exception e) {
			LOGGER.error("Error al inicializar: OficioAprobacionRtBean: ", e);
			JsfUtil.addMessageError("Ocurrio un error al inicializar los datos. Por favor intentelo mas tarde.");
		}
	}

	/**
	 * 
	 * <b> Metodo que completa la tarea. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 13/08/2015]
	 * </p>
	 * 
	 * @return String: ir a la bandeja
	 */
	public String completarTarea() {
		try {

			Map<String, Object> data = new HashMap<>();
			if(this.redirect){

				data.put("mostrarNotifInicProcGD", true);
				data.put("asuntoNotificacionPronunciamiento ", "Notificación de Registro de Generador de Desechos Peligrosos y/o Especiales");
				data.put("bodyNotificacionPronunciamiento", "Estimado usuario, en cumplimiento de la normativa ambiental actual, " +
						"usted debe realizar el Registro de Generador de Desechos Peligrosos y/o Especiales en un plazo de "+this.cantDias+" días.");

				procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId(), data);

				aprobacionRequisitosTecnicosFacade.enviarAprobacionRequisitosTecnicos(
						JsfUtil.getCurrentProcessInstanceId(), JsfUtil.getLoggedUser(), JsfUtil.getCurrentTask()
								.getTaskId());
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);

				return JsfUtil.actionNavigateTo("/procesos/procesos.jsf");

			}

			data.put("mostrarNotifInicProcGD", false);
			procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId(), data);
			
			if(esproyectoCoa)
			{
				aprobacionRequisitosTecnicosFacade.completarTarea(null, JsfUtil.getCurrentTask().getTaskId(), JsfUtil.getCurrentProcessInstanceId(), JsfUtil.getLoggedUser());
			}
			else
			{
				aprobacionRequisitosTecnicosFacade.enviarAprobacionRequisitosTecnicos(
						JsfUtil.getCurrentProcessInstanceId(), JsfUtil.getLoggedUser(), JsfUtil.getCurrentTask()
								.getTaskId());
			}
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);

			return JsfUtil.actionNavigateToBandeja();

		} catch (JbpmException e) {
			LOGGER.error(e);
			JsfUtil.addMessageError("Ocurrió un error al enviar la información.");
		}
		return "";
	}

	public String enviarAprobacionRequisitosTecnicos() throws Exception {

		this.redirect = false;
		try {
			esproyectoCoa = Boolean.parseBoolean(variables.get("vieneRcoa").toString()) ;
		} catch (Exception e) {
			esproyectoCoa = false;
		}
		if(esproyectoCoa)
		{
			return completarTarea();
		}

		if(this.aprobacionRequisitosTecnicos.getVoluntario()){
			ProyectoLicenciamientoAmbiental proyecto = this.proyectoLicenciaAmbientalFacade.getProyectoPorCodigo(this.aprobacionRequisitosTecnicos.getProyecto());
			if(proyecto!=null){
				if (!validarGenerador()) {
					this.redirect = true;
					this.cantDias = proyecto.getTipoEstudio().getId() == 1 ? 60 : 30;
					RequestContext.getCurrentInstance().execute(
							"PF('generadorWdgt').show();");
				}
				else{
					return completarTarea();
				}
			}
			else{
				RequestContext.getCurrentInstance().execute(
						"PF('generadorWdgt').show();");
			}
		}
		else{
			RequestContext.getCurrentInstance().execute(
					"PF('generadorNoVoluntaioWdgt').show();");
		}


		return "";
	}

	/**
	 *
	 * <b> Método que valida si existen desechos sin código de generador. </b>
	 * <p>
	 * [Author: Denis Linares, Date: 31/12/2015]
	 * </p>
	 *
	 * @return boolean: true existe el desecho sin código
	 * @throws ServiceException
	 *             : Excepcion
	 */
	public boolean validarGenerador() throws ServiceException {
		boolean existe = false;
		if (aprobacionRequisitosTecnicos.isGestion()) {
			existe = eliminacionDesechoFacade.validarDesechoSinGeneradorPorProyecto(aprobacionRequisitosTecnicos.getProyecto(), aprobacionRequisitosTecnicos.getSolicitud());
		}
		return existe;

	}

	/**
	 * 
	 * <b> Metodo oara la descarga del documento. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 13/08/2015]
	 * </p>
	 * 
	 * @param oficioDoc
	 *            : oficio
	 * @return StreamedContent: formato para el documento
	 * @throws Exception
	 *             : Excepcion
	 */
	public StreamedContent getStream(byte[] oficioDoc) throws Exception {
		DefaultStreamedContent content = null;
		if (oficioDoc != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(oficioDoc), "application/pdf");
			content.setName(OFICIO_APROBACION + TIPO_PDF);
		}

		return content;
	}
	
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
		String usuarioUrl =aprobacionRequisitosTecnicos.getUsuario().getNombre();
		String proyectoUrl = aprobacionRequisitosTecnicos.getSolicitud();
		String appUlr = "suia";
		Organizacion organizacion = organizacionFacade.buscarPorPersona(aprobacionRequisitosTecnicos.getUsuario().getPersona(), aprobacionRequisitosTecnicos.getUsuario().getNombre());
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
					if(surveyResponseFacade.findByProject(aprobacionRequisitosTecnicos.getSolicitud())) {
						return false;
					} else {	// no se respondió la encuesta
						return true;
					}
	}
	/****** fin módulo encuesta ******/
	
	
}
