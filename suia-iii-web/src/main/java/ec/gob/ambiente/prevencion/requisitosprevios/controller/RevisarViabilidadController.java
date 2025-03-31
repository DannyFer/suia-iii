package ec.gob.ambiente.prevencion.requisitosprevios.controller;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.requisitosprevios.bean.RevisarViabilidadBean;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.eia.facade.LicenciaAmbientalFacade;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.prevencion.requisitosPrevios.RequisitosPreviosLicenciaFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;

@RequestScoped
@ManagedBean
public class RevisarViabilidadController implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -3710728822083232386L;

    @Getter
    @Setter
    @ManagedProperty(value = "#{revisarViabilidadBean}")
    private RevisarViabilidadBean revisarViabilidadBean;

    @EJB
    private RequisitosPreviosLicenciaFacade requisitosPreviosFacade;

    @EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
    private TaskBeanFacade taskBeanFacade;
    
    @EJB
	private UsuarioFacade usuarioFacade;

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
	private Usuario usuario = JsfUtil.getLoggedUser();

    @Getter
    @Setter
    @ManagedProperty(value = "#{proyectosBean}")
    private ProyectosBean proyectosBean;

    @EJB
    private ProcesoFacade procesoFacade;

    @EJB
    private LicenciaAmbientalFacade licenciaAmbientalFacade;

    @EJB
    private ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade;
    
    @Setter
	private Documento documentoInformeAlfresco;
    
    @Getter
	@Setter
	private boolean token, documentoDescargado, informacionSubida, mapaDescargado, ciDescargado, acepta,
			esRegistroLicencia, ambienteProduccion, esEnte;
	
	@Getter
	@Setter
	private Boolean informeGuardado, habilitarEnviar, esProduccion, esCorreccion;
	
	@EJB
	private DocumentosFacade documentosFacade;
	
	@Getter
	@Setter
	private String urlAlfresco;

    private static final Logger LOGGER = Logger.getLogger(RevisarViabilidadController.class);
    
    public void init() {
		token = true;
		if (!ambienteProduccion) {
			verificaToken();
		}
	}

	public boolean verificaToken() {
		token = false;
		if (JsfUtil.getLoggedUser().getToken() != null && JsfUtil.getLoggedUser().getToken())
			token = true;
		return token;
	}

	public void guardarToken() {
		usuario.setToken(token);
		try {
			usuarioFacade.guardar(usuario);
			verificaToken();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void guardarDocumentos() {
		try {
			if (documentoInformeAlfresco != null && documentoInformeAlfresco.getId() != null) {
				habilitarEnviar = true;
				String documentOffice = documentosFacade.direccionDescarga(documentoInformeAlfresco);
				urlAlfresco = DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre());
			}
		} catch (Exception exception) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}

		RequestContext.getCurrentInstance().update("formDialogFirma");
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('dlgConfirmar').hide();");
		context.execute("PF('signDialog').show();");
	}

    public String completarTarea() {
        if (!revisarViabilidadBean.getDescargado()) {
            JsfUtil.addMessageError("Para continuar debe descargar el documento con las respuestas del proponente.");
            return "";
        } else {
            try {
                Map<String, Object> params = new ConcurrentHashMap<String, Object>();

                if(revisarViabilidadBean.getTipoPronunciamiento().equals("Biodiversidad")){
                    params.put("requiereInspeccionBiodiversidad", revisarViabilidadBean.getRequiereInspeccion());
                }
                else if(revisarViabilidadBean.getTipoPronunciamiento().equals("Forestal")){
                    params.put("requiereInspeccionForestal", revisarViabilidadBean.getRequiereInspeccion());
                }

                procesoFacade.modificarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
                                .getProcessInstanceId(), params);

                taskBeanFacade.approveTask(loginBean.getNombreUsuario(),
                        bandejaTareasBean.getTarea().getTaskId(),bandejaTareasBean.getTarea().getProcessInstanceId(), new ConcurrentHashMap<String, Object>(),
                         loginBean.getPassword(),
                        Constantes.getUrlBusinessCentral(),Constantes.getRemoteApiTimeout(),Constantes.getNotificationService());
                procesoFacade.envioSeguimientoLicenciaAmbiental(loginBean.getUsuario(), bandejaTareasBean.getTarea().getProcessInstanceId());
                return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
            } catch (Exception e) {
                LOGGER.error(e);
                JsfUtil.addMessageError("Error al realizar la operación. Intente más tarde.");
                return "";
            }
        }
    }

    public void descargarMapa() {
        try {
            UtilDocumento.descargarPDF(requisitosPreviosFacade
                    .getAlfrescoMapa(proyectosBean.getProyecto()), "Mapa del certificado intersección");
        } catch (Exception e) {
            LOGGER.error("error al descargar ", e);
            JsfUtil.addMessageError("El documento no fue correctamente generado, por lo que no se puede descargar. Por favor comunicarse con mesa de ayuda.");
        }
    }

    public void descargarOficio() {
        try {
            UtilDocumento.descargarPDF(requisitosPreviosFacade
                    .getAlfrescoOficio(proyectosBean.getProyecto()), "Oficio del certificado intersección");
        } catch (Exception e) {
            JsfUtil.addMessageError("El documento no fue correctamente generado, por lo que no se puede descargar. Por favor comunicarse con mesa de ayuda.");
        }
    }

    public void descargarCoordenadas() {
        try {
            UtilDocumento.descargarExcel(requisitosPreviosFacade
                    .descargarCoordenadas(proyectosBean.getProyecto()), "Coordenadas del proyecto");
        } catch (Exception e) {
            LOGGER.error("error al descargar ", e);
        }
    }
}
