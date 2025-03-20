package ec.gob.ambiente.prevencion.categoria1.controllers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.prevencion.categoria1.bean.RecibirCertificadoRegistroAmbientalBean;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.CertificadoAmbientalInformeForestal;
import ec.gob.ambiente.suia.domain.CertificadoAmbientalPronunciamiento;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.DocumentoProyecto;
import ec.gob.ambiente.suia.domain.DocumentosTareasProceso;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.prevencion.categoria1.facade.Categoria1Facade;
import ec.gob.ambiente.suia.prevencion.categoria1.facade.InformeInspeccionForestalCAFacade;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
import ec.gob.ambiente.suia.prevencion.categoria1.facade.PronunciamientoCAFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilAlfresco;

@ManagedBean
@ViewScoped
public class DescargarDocumentacionCAController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2473064315917885386L;

	private static final Logger LOG = Logger.getLogger(DescargarDocumentacionCAController.class);
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private PronunciamientoCAFacade pronunciamientoFacade;
	
	@EJB
	private Categoria1Facade categoria1Facade;
	
	@EJB
	private DocumentosFacade documentosFacade;
	
	@EJB
	private InformeInspeccionForestalCAFacade informeForestalFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
        
    @ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
  
    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;
	
    @ManagedProperty(value = "#{recibirCertificadoRegistroAmbientalBean}")
    @Getter
    @Setter
    private RecibirCertificadoRegistroAmbientalBean guiaPracticas;
          
    @Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyecto; 

	@Getter
	@Setter
	private CertificadoAmbientalPronunciamiento pronunciamiento=null;
	
	@Getter
	@Setter
	private CertificadoAmbientalInformeForestal informeForestal;
		
	@Getter
    @Setter
    private Documento documentoCertificado;
	
	@Getter
    @Setter
    private Documento documentoInformeForestal;
	
    @Getter
    @Setter
    private Documento documentoPronunciamiento;
    
    private boolean descargadoGuias=false,descargadoCertificado=false,descargadoInformeForestal=false,descargadoPronunciamientoSnap=false,tareaCompleta=false;
    
    @Getter
	private boolean pronunciamientoFavorable;
	
	private boolean intersecaSnap,intersecaForestal;
	
	private Map<String, Object> variables;
	
	@EJB
	private UsuarioFacade usuarioFacade;

	@EJB
	private ContactoFacade contactoFacade;
	
	
	@PostConstruct
	public void init() {
		try {			
			proyecto=proyectosBean.getProyecto();
			variables=procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getProcessId());
			
			intersecaSnap=Boolean.valueOf((String)variables.get("interseca_snap"));
			intersecaForestal=Boolean.valueOf((String)variables.get("interseca_forestal"));
			pronunciamientoFavorable=Boolean.valueOf((String)variables.get("pronunciamiento_favorable"));
			
			
			JsfUtil.cargarObjetoSession("codigoProyecto", proyecto.getCodigo());
			JsfUtil.getBean(FirmarCertificadoAmbientalController.class).init();
			JsfUtil.getBean(FirmarCertificadoAmbientalController.class).firmarDocumento();
			enviarMailOperador(proyecto);
			
			JsfUtil.cargarObjetoSession("codigoProyecto", null);

			
		} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar al descargar documentacion.");
		}
	}
	
	


    /**
     * fecha:  2019-01-08
     * descripcion: metodo para envio de notificaciones en caso de error por no encontrar firma o usuario responsable
     * @param tipoError
     * @throws ServiceException
     * @throws InterruptedException
     */
	public void enviarNotificacionError( String msgError) throws ServiceException, InterruptedException {
		String mensajeMail ="";
		if(msgError.contains("Error al buscar usuario Autoridad") || msgError.contains("Error de firma")){
			mensajeMail = msgError;
			//obtengo los usuarios con el rol de notificacion de errores para el envio de la notificacion
			List<Usuario> usuarios_notificacion= new ArrayList<Usuario>();
			usuarios_notificacion = usuarioFacade.buscarUsuarioPorRol("NOTIFICACIONES APLICATIVOS");
			List<Contacto> listContacto = new ArrayList<Contacto>();
			for (int i = 0; i < usuarios_notificacion.size(); i++) {
				listContacto = contactoFacade.buscarPorPersona(usuarios_notificacion.get(i).getPersona());
				for (int j = 0; j < listContacto.size(); j++) {
					if (listContacto.get(j).getFormasContacto().getId().equals(5)) {
						// envio de correo
						NotificacionAutoridadesController mail_a = new NotificacionAutoridadesController();
						mail_a.sendEmailNotificacionesError(listContacto.get(j).getValor(), 
								"Registro del proyecto",mensajeMail,
								proyecto.getNombre(),
								proyecto.getCodigo(),
								JsfUtil.getLoggedUser().getPersona().getNombre(),
								proyecto.getCatalogoCategoria().getDescripcion(),
								proyecto.getAreaResponsable().getAreaName(), usuarios_notificacion.get(i), loginBean.getUsuario());
						Thread.sleep(2000);
						break;
					}
				}
			}
		}
	}
	
	public StreamedContent descargarGuiaPracticas() {
		
		try {			
			guiaPracticas.iniciar(proyecto.getId());
			descargadoGuias=true;
			return guiaPracticas.getStream(guiaPracticas.getGuiaBuenasPracticas());
		} catch (Exception e) {
			LOG.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comu"
					+ "nicarse con mesa de ayuda.");
            return null;
		}
	}
	
	public StreamedContent descargarCertificado() {
		try {
			if(!descargadoGuias)
			{
				JsfUtil.addMessageError("Antes de obtener su certificado debe descargar la Guía de Buenas Prácticas.");
				return null;
			}
			
            byte[] byteFile = pronunciamientoFacade.descargarFile(documentoCertificado);
            if (byteFile != null) {
                InputStream is = new ByteArrayInputStream(byteFile);
                descargadoCertificado=true;
                completarTarea();
                return new DefaultStreamedContent(is, "application/pdf",  documentoCertificado.getNombre());
            } else {
                JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
                return null;
            }
		} catch (Exception e) {
			LOG.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
            return null;
		}
	}
	
	public StreamedContent descargarPronunciamiento() {
		try {			
            byte[] byteFile = pronunciamientoFacade.descargarFile(documentoPronunciamiento);
            if (byteFile != null) {
                InputStream is = new ByteArrayInputStream(byteFile);
                descargadoPronunciamientoSnap=true;
                return new DefaultStreamedContent(is, "application/pdf",  documentoPronunciamiento.getNombre());
            } else {
                JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
                return null;
            }
		} catch (Exception e) {
			LOG.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
            return null;
		}
	}
	
	public StreamedContent descargarInformeForestal() {
		try {			
            byte[] byteFile = pronunciamientoFacade.descargarFile(documentoInformeForestal);
            if (byteFile != null) {
                InputStream is = new ByteArrayInputStream(byteFile);
                descargadoInformeForestal=true;
                return new DefaultStreamedContent(is, "application/pdf",  documentoInformeForestal.getNombre());
            } else {
                JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
                return null;
            }
		} catch (Exception e) {
			LOG.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
            return null;
		}
	}
	

	
	private void completarTarea(){
		try {
			
//			if(pronunciamientoFavorable && !descargadoCertificado 
//				||(!pronunciamientoFavorable && !intersecaForestal && !intersecaSnap && !descargadoCertificado)
//				||(!pronunciamientoFavorable && intersecaForestal && !descargadoInformeForestal) 
//				||(!pronunciamientoFavorable && intersecaSnap && !descargadoPronunciamientoSnap)){
//			
//				JsfUtil.addMessageWarning("Descargar la documentación");
//				return;
//			}
			
//			if(!tareaCompleta)
//			{
				procesoFacade.aprobarTarea(loginBean.getUsuario(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
				tareaCompleta=true;
//			}
			
			
		} catch (JbpmException e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			e.printStackTrace();
		}
	}
	
	
	
	public void enviar()
	{
		completarTarea();
//		if(tareaCompleta)
		JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");		
	}
	
	public void enviarMailOperador(ProyectoLicenciamientoAmbiental proyecto){
		try {					
			
			Organizacion organizacion = organizacionFacade.buscarPorRuc(proyecto.getUsuario().getNombre());
			
			List<Contacto> listaContactosAutoridad = new ArrayList<Contacto>();
			if(organizacion != null && organizacion.getId() != null){
				listaContactosAutoridad = contactoFacade.buscarPorOrganizacion(organizacion);
			}else{
				listaContactosAutoridad = contactoFacade.buscarPorPersona(proyecto.getUsuario().getPersona());
			}
			
			
			String emailOperador = "";
			for(Contacto contacto : listaContactosAutoridad){
				if(contacto.getFormasContacto().getId() == FormasContacto.EMAIL){
					emailOperador = contacto.getValor();
					break;
				}						
			}
			
			String ente = "";
			String siglas=proyecto.getAreaResponsable().getTipoArea().getSiglas();			
			if(siglas.compareTo("OT")==0){
				ente = proyecto.getAreaResponsable().getArea().getAreaName();
			}else{
				ente = proyecto.getAreaResponsable().getAreaName();
			}
			
			
			Object[] parametrosCorreo = new Object[] {proyecto.getUsuario().getPersona().getNombre(), 
					proyecto.getCodigo(), proyecto.getNombre(), 
					ente};
			
			String notificacion = mensajeNotificacionFacade.recuperarValorMensajeNotificacion(
							"bodyNotificacionCertificadoOperador",
							parametrosCorreo);			
			
			NotificacionAutoridadesController mail_a = new NotificacionAutoridadesController();
			mail_a.sendEmailInformacionProponente(emailOperador, "MAAETE", notificacion, "Certificado Ambiental Generado", proyecto.getCodigo(), proyecto.getUsuario(), loginBean.getUsuario());			
			
		} catch (Exception e) {
			LOG.error("Error en envio de notificación", e);
		}		
	}	
}
