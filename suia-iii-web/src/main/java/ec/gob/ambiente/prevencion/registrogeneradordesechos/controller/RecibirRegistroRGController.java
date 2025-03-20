package ec.gob.ambiente.prevencion.registrogeneradordesechos.controller;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.prevencion.registrogeneradordesechos.bean.DocumentoRGBean;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.survey.SurveyResponseFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class RecibirRegistroRGController implements Serializable {

	private static final long serialVersionUID = 2144274394627795921L;

	private final Logger LOG = Logger.getLogger(RecibirRegistroRGController.class);

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private DocumentosFacade documentosFacade;

	@EJB
	RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;

	@ManagedProperty(value = "#{documentoRGBean}")
	@Getter
	@Setter
	private DocumentoRGBean documentoRGBean;

	@Getter
	@Setter
	private String mensaje;

	@Getter
	@Setter
	private Boolean descargaroficio = false;
	@Getter
	@Setter
	private Boolean descargarrgd = false;

	@EJB
	ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
	
	@EJB
    private OrganizacionFacade organizacionFacade;
	
	/***** módulo de encuesta *****/
	@EJB 
	private SurveyResponseFacade surveyResponseFacade;
	@Getter
    @Setter
    public static String surveyLink = Constantes.getPropertyAsString("suia.survey.link");
	
	@Getter
    @Setter
    public static String reportLink = Constantes.getPropertyAsString("suia.report.link");
	/***** fin módulo de encuesta *****/
	

	@PostConstruct
	public void init() {
		try {

			Documento documentoOficioAprovacion = documentoRGBean.inicializarOficioEmisionActualizacionAsociado()
					.getDocumento();
			Documento documentoRegistroGenerador = documentoRGBean.getGenerador().getDocumentoBorrador();

			if (documentoOficioAprovacion != null)
				documentoOficioAprovacion.setContenidoDocumento(documentosFacade.descargar(documentoOficioAprovacion
						.getIdAlfresco()));

			if (documentoRegistroGenerador != null)
				documentoRegistroGenerador.setContenidoDocumento(documentosFacade.descargar(documentoRegistroGenerador
						.getIdAlfresco()));

			// Cuando es un trámite de emisión y no es responsabilidad
			// extendida.
			if (Boolean.parseBoolean(JsfUtil.getCurrentTask().getVariable("tipoTramite").toString())
					&& !documentoRGBean.getGenerador().getResponsabilidadExtendida()) {
				mensaje = "<p style='text-align:justify'>Estimado usuario, recuerde que como parte de la normativa ambiental y del Registro de Generador obtenido, Usted debe proceder a:</p>" +
						"<p style='text-align:justify'>1. Iniciar el proceso de 'Plan de minimización de desechos peligrosos y/o especiales' en un plazo de 90 días, conforme lo establecido en el literal c) del artículo 88 del Capítulo VI, Libro VI del Texto Unificado de Legislación Secundaria del Ministerio del Ambiente y Agua correspondiente al Acuerdo Ministerial No. 061 publicado en el Registro Oficial No. 316 del 04 de mayo de 2015;</p>" +
						"<p style='text-align:justify'>2. Ingresar información al proceso de 'Declaración de gestión de desechos especiales y/o peligrosos', a medida que vaya realizando la gestión de los desechos registrados, actividad que puede realizarla indistintamente durante todo el año; recordando que, al 31 de diciembre de cada año, el sistema automáticamente realizará un corte de la información suministrada hasta entonces y la presentará como la Declaración de gestión del generador ante la respectiva Dirección Provincial del Ministerio del Ambiente y Agua, en cumplimiento a los literales i) y k) del artículo 88 del Capítulo VI, Libro VI del Texto Unificado de Legislación Secundaria del Ministerio del Ambiente.\"</p>" +
						"<p style='text-align:justify'>3. Los 10 primeros días de enero de cada año, debe presentar el avance del plan de minimización aprobado ingresando la información correspondiente en el proceso \"Seguimiento al Plan de Minimización o Programa de Gestión de desechos peligrosos y/o especiales\".</p>";
			}

			// Cuando es un trámite de emisión y sí es responsabilidad
			// extendida.
			else if (Boolean.parseBoolean(JsfUtil.getCurrentTask().getVariable("tipoTramite").toString())
					&& documentoRGBean.getGenerador().getResponsabilidadExtendida()) {
				mensaje = "<p style='text-align:justify'> \"Estimado usuario, recuerde que como parte de la normativa ambiental y del Registro de Generador obtenido debido a la aplicación del Principio de Responsabilidad Extendida, Usted debe proceder a:</p>" +
						"<p style='text-align:justify'>1. Iniciar el proceso de 'Programa de Gestión de desechos peligrosos y/o especiales' conforme lo establezca el Acuerdo Ministerial que rige la política de aplicación del Principio de Responsabilidad Extendida correspondiente.</p>" +
						"<p style='text-align:justify'>2. Ingresar información al proceso de 'Declaración de gestión de desechos especiales y/o peligrosos', a medida que vaya realizando la gestión de los desechos registrados, actividad que puede realizarla indistintamente durante todo el año; recordando que, al 31 de diciembre de cada año, el sistema automáticamente realizará un corte de la información suministrada hasta entonces y la presentará como la Declaración de gestión del generador ante la dependencia del Ministerio del Ambiente y Agua en Planta Central que otorgó el Registro, en cumplimiento al Acuerdo Ministerial que rige la política correspondiente o en su defecto a los literales i) y k) del artículo 88 del Capítulo VI, Libro VI del Texto Unificado de Legislación Secundaria del Ministerio del Ambiente correspondiente al Acuerdo Ministerial No. 061 publicado en el Registro Oficial No. 316 del 04 de mayo de 2015.\"</p>" +
						"<p style='text-align:justify'>3. Los 10 primeros días de enero de cada año, debe presentar el avance del programa de gestión aprobado ingresando la información correspondiente en el proceso \"Seguimiento al Plan de Minimización o Programa de Gestión de desechos peligrosos y/o especiales\".</p>";
			}

			// Cuando es un trámite de actualización.
			else if (!Boolean.parseBoolean(JsfUtil.getCurrentTask().getVariable("tipoTramite").toString())) {
				mensaje = "<p style='text-align:justify'> \"Estimado usuario, una vez actualizado su Registro de Generador recuerde dar cumplimiento a sus obligaciones con respecto al Plan de Minimización o Programa de Gestión y las Declaraciones de Gestión de desechos peligrosos y/o especiales conforme la normativa ambiental aplicable.\"</p>";
			}

		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_INICIAR_TAREA);
			LOG.error("Error cargando datos del registro de generador", e);
		}
	}

	public void aceptar() throws ServiceException, CmisAlfrescoException {

		if (descargaroficio && descargarrgd) {
			try {

				System.out
						.println("***** Ahora corresponde llamar a los procesos que todavía no están implementados con los siguientes parámetros:*****");
				System.out.println("*****Parámetro responsabilidad extendida: "
						+ documentoRGBean.getGenerador().getResponsabilidadExtendida() + "*****");
				System.out.println("*****Parámetro tipo de trámite emisión: "
						+ Boolean.parseBoolean(JsfUtil.getCurrentTask().getVariable("tipoTramite").toString()) + "*****");

				documentoRGBean.getGenerador().setFinalizado(true);

				this.registroGeneradorDesechosFacade.guardar(documentoRGBean.getGenerador());

				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), JsfUtil.getCurrentTask().getTaskId(),
						JsfUtil.getCurrentProcessInstanceId(), null);


				ProyectoLicenciamientoAmbiental proyecto = documentoRGBean.getGenerador().getProyecto();

				//Si el RGD es asociado a un proyecto
				if (proyecto != null && proyecto.getId() != null){
					//Actulizamos el valor del campo que indica que existe un RGD en curso.
					proyecto.setRgdEncurso(false);
					proyectoLicenciamientoAmbientalFacade.actualizarProyecto(proyecto);
				}


			} catch (Exception e) {
				LOG.error("Error al completar la tarea de recibir registro de generador", e);
			}
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
		} else {
			JsfUtil.addMessageError("Debes descargar los documentos para ir a la bandeja de tareas.");
		}
	}

	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/prevencion/registrogeneradordesechos/recibirRegistroOficio.jsf");
	}

	public void descargarOficio() {
		if (documentoRGBean != null) {
			descargaroficio = true;
		}
	}

	public void descargarRgd() {
		if (documentoRGBean != null) {
			descargarrgd = true;
		}
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
		String usuarioUrl =documentoRGBean.getGenerador().getUsuario().getNombre();
		String proyectoUrl = documentoRGBean.getGenerador().getSolicitud();
		String appUlr = "suia";
		Organizacion organizacion = organizacionFacade.buscarPorPersona(documentoRGBean.getGenerador().getUsuario().getPersona(), documentoRGBean.getGenerador().getUsuario().getNombre());
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
					if(surveyResponseFacade.findByProject(documentoRGBean.getGenerador().getSolicitud())) {
						return false;
					} else {	// no se respondió la encuesta
						return true;
					}
	}
	/****** fin módulo encuesta ******/
	
	
}
