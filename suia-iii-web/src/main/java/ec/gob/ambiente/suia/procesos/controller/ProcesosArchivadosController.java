package ec.gob.ambiente.suia.procesos.controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.FacilitadorProyecto;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.GeneradorDesechosPeligrosos;
import ec.gob.ambiente.suia.domain.ProcesoSuspendido;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoSuspendidoFacade;
import ec.gob.ambiente.suia.procesos.bean.ResumenProcesosAdministradorBean;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ProcesosArchivadosController implements Serializable {

	private static final long serialVersionUID = -9143923922398179951L;
	private static final Logger LOG = Logger.getLogger(ProcesosArchivadosController.class);
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{resumenProcesosAdministradorBean}")
	private ResumenProcesosAdministradorBean resumenProcesosAdministradorBean;
	
	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@EJB
	private DocumentosFacade documentosFacade;
	
	@EJB
	private ProcesoSuspendidoFacade procesoSuspendidoFacade;
	
	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;
	
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
		
	public String dblinkSuiaVerde=Constantes.getDblinkSuiaVerde();

	@Getter
	@Setter
	private List<Documento> documentosList;
	
	@Getter
	@Setter
	private String motivo;
	
	@Getter
	@Setter
	private Integer numDias;
	
	//Proyecto Seleccionado
	@Getter	
	private String codigoSeleccionado;
	
	@Getter
    @Setter
    private Documento documentoAdjunto;
	
	@Getter
    private boolean rolReactivar;
	
	@Getter
	@Setter
	private List<ProcesoSuspendido> rgdNoAsociados;

	@PostConstruct
	private void init() {
		try {
			proyectosBean.setProyectos(proyectoLicenciamientoAmbientalFacade.getAllProjectsArchivedByUser(JsfUtil.getLoggedUser()));
			//Los Roles para Reactivar se pusieron los mismos de Suspender (Solicitado por QA Danny)
			rolReactivar=Usuario.isUserInRole(JsfUtil.getLoggedUser(), "admin") || Usuario.isUserInRole(JsfUtil.getLoggedUser(), "AUTORIDAD AMBIENTAL") || Usuario.isUserInRole(JsfUtil.getLoggedUser(), "AUTORIDAD AMBIENTAL MAE");
			rgdNoAsociados=procesoSuspendidoFacade.getProcesoSuspendidoPorTipo(ProcesoSuspendido.TIPO_RGD_NO_ASOCIADO,true);
			documentoAdjunto=new Documento();
		} catch (Exception e) {
			LOG.error("Error cargando proyectos archivados", e);
		}
	}
	
	public void proyectoListener(String codigo)
	{
		codigoSeleccionado=codigo;
		motivo="";
	}

	public void verDocumentos(Integer idTabla,String codigo,String motivo) {
		try {	
				documentosList = new ArrayList<Documento>();
				if(idTabla!=0)
				{					
					for (Documento documento : documentosFacade.documentoXTablaIdXIdDoc(idTabla, ProyectoLicenciamientoAmbiental.class.getSimpleName(), TipoDocumentoSistema.ARCHIVACION_PROYECTO_ADJUNTO)) {
						documento.setContenidoDocumento(documentosFacade.descargar(documento.getIdAlfresco(),documento.getFechaCreacion()));
						documentosList.add(documento);
						
					}
					for (Documento documento : documentosFacade.documentoXTablaIdXIdDoc(idTabla, ProyectoLicenciamientoAmbiental.class.getSimpleName(), TipoDocumentoSistema.REACTIVACION_PROYECTO_ADJUNTO)) {
						documento.setContenidoDocumento(documentosFacade.descargar(documento.getIdAlfresco(),documento.getFechaCreacion()));
						documentosList.add(documento);
						
					}
				}else
				{
					String query="select * from dblink('"+dblinkSuiaVerde+"', "
							+ "'select d.nombre||''.''||d.extension as nombre,d.urlalfresco from documento d"
							+ "	where d.proyecto =''"+codigo+"'' and d.key=''documentoEliminacion''') t1 (nombre varchar,urlalfresco varchar)";		   			
					List<Object> lista = crudServiceBean.getEntityManager().createNativeQuery(query).getResultList();
					
					for (Object object : lista) {
						Object[] obj=(Object[])object;
						String nombre=(String)obj[0];
						String urlalfresco=(String)obj[1];
						
						if(urlalfresco!=null)							
						{
							Documento documento=new Documento();
							documento.setNombre(nombre);
							documento.setContenidoDocumento(documentosFacade.descargar(urlalfresco,new Date()));
							documentosList.add(documento);
						}
					}
				}
				
				this.motivo=motivo;
				
		} catch (Exception e) {
			LOG.error("Error obteniendo documentos.", e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public void verDocumentosRGD(String codigo) {
		try {	
				documentosList = new ArrayList<Documento>();
				List<Integer> rgdIds= registroGeneradorDesechosFacade.getRGDIdByRequest(codigo);
				
				for (Integer id : rgdIds) {
					for (Documento documento : documentosFacade.documentoXTablaIdXIdDoc(id, GeneradorDesechosPeligrosos.class.getSimpleName(), TipoDocumentoSistema.ARCHIVACION_PROYECTO_ADJUNTO)) {
						documento.setContenidoDocumento(documentosFacade.descargar(documento.getIdAlfresco(),documento.getFechaCreacion()));
						documentosList.add(documento);					
					}
				}
		} catch (Exception e) {
			LOG.error("Error obteniendo documentos.", e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public StreamedContent getStream(Documento documento) {
		if (documento.getContenidoDocumento() != null) {
			InputStream is = new ByteArrayInputStream(documento.getContenidoDocumento());
			return new DefaultStreamedContent(is, documento.getMime(), documento.getNombre());
		} else{
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		return null;
	}
	
	public void uploadFile(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoAdjunto=new Documento();
		documentoAdjunto.setContenidoDocumento(contenidoDocumento);
		documentoAdjunto.setNombre(event.getFile().getFileName());		
		String ext = JsfUtil.devuelveExtension(event.getFile().getFileName());
		String mime = ext.contains("pdf")||ext.contains("rar")||ext.contains("zip")?"application":"image";
		mime+="/"+ext;
		documentoAdjunto.setExtesion("."+ext);
		documentoAdjunto.setMime(mime);
		//pdf|rar|zip|png|jpe?g|gif|
	}
	
    private Documento guardarDocumento(String codigoProyecto,Documento documento,TipoDocumentoSistema tipoDocumento){
    	try {        	
        	documento.setNombreTabla(ProyectoLicenciamientoAmbiental.class.getSimpleName());
    		return documentosFacade.guardarDocumentoAlfresco(codigoProyecto,Constantes.CARPETA_ARCHIVACION,null,documento,tipoDocumento,null);
    		
    	} catch (Exception e) {
    		LOG.error(e.getMessage());
    		return null;
    	}    	
    }
    
    public void reactivarProyecto() {
		try {
			if(documentoAdjunto.getContenidoDocumento()==null)
			{
				JsfUtil.addMessageError("El documento adjunto es requerido");
				return;
			}
			if(numDias == null || numDias < 1 || numDias > 90)
			{
				JsfUtil.addMessageError("Los días de activación deben estar entre 1 y 90");
				return;
			}
			
			ProcesoSuspendido procesoSuspendido=procesoSuspendidoFacade.getProcesoSuspendidoPorCodigo(codigoSeleccionado);
			procesoSuspendido.setDescripcion(motivo);				
			Integer proyectoId=proyectoLicenciamientoAmbientalFacade.getProyectoPorCodigo(procesoSuspendido.getCodigo(),false);
			documentoAdjunto.setIdTable(proyectoId!=null?proyectoId:0);
			resumenProcesosAdministradorBean.reactivarProceso(procesoSuspendido.getId(),motivo,numDias);
			documentoAdjunto=guardarDocumento(procesoSuspendido.getCodigo(), documentoAdjunto, TipoDocumentoSistema.REACTIVACION_PROYECTO_ADJUNTO);					
			if(documentoAdjunto.getId()==null)
			{
				JsfUtil.addMessageError("Error al guardar el documento");
				return;
			}
			enviarNotificacionesReactivar(proyectoLicenciamientoAmbientalFacade.getProyectoPorCodigo(procesoSuspendido.getCodigo()),procesoSuspendido.getDescripcion());			
			proyectosBean.setProyectos(proyectoLicenciamientoAmbientalFacade.getAllProjectsArchivedByUser(JsfUtil.getLoggedUser()));
			numDias=null;
			JsfUtil.addCallbackParam("reactivarProyecto");
		} catch (Exception ex) {
			LOG.error(ex);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
    
    public void reactivarRGD() {
		try {
			if(documentoAdjunto.getContenidoDocumento()==null)
			{
				JsfUtil.addMessageError("El documento adjunto es requerido");
				return;
			}else
			{
				ProcesoSuspendido procesoSuspendido=procesoSuspendidoFacade.getProcesoSuspendidoPorCodigo(codigoSeleccionado);
				procesoSuspendido.setDescripcion(motivo);
				List<Integer> rgdIds=registroGeneradorDesechosFacade.getRGDIdByRequest(codigoSeleccionado);				
				documentoAdjunto.setIdTable(rgdIds.get(0));
				resumenProcesosAdministradorBean.reactivarProceso(procesoSuspendido.getId(),motivo,null);
				
				documentoAdjunto.setNombreTabla(GeneradorDesechosPeligrosos.class.getSimpleName());
	    		documentosFacade.guardarDocumentoAlfrescoSinProyecto(codigoSeleccionado,Constantes.CARPETA_ARCHIVACION,null,documentoAdjunto, TipoDocumentoSistema.REACTIVACION_PROYECTO_ADJUNTO,null);
	    		procesoSuspendidoFacade.setEstadoProyectoRGD(rgdIds.get(0),true);	
			}
			rgdNoAsociados=procesoSuspendidoFacade.getProcesoSuspendidoPorTipo(ProcesoSuspendido.TIPO_RGD_NO_ASOCIADO,true);
			JsfUtil.addCallbackParam("reactivarRGD");
		} catch (Exception ex) {
			LOG.error(ex);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
    
    private void enviarNotificacionesReactivar(ProyectoLicenciamientoAmbiental proyecto,String motivo){
		try {
			NotificacionAutoridadesController mail = new NotificacionAutoridadesController();
			
			//Notificar al proponente
			String mensaje = mensajeNotificacionFacade.recuperarValorMensajeNotificacion("bodyNotificacionReactivacionProyecto", new Object[]{});			
			mensaje = mensaje.replace("codigo_proyecto", proyecto.getCodigo());
			mensaje = mensaje.replace("motivo_reactivacion", motivo);
			mensaje = mensaje.replace("url_firma",JsfUtil.getStartPage()+"/resources/images/firma_etiqueta.png");
			
			String mensajeProponente = mensaje.replace("nombre_usuario", proyecto.getUsuario().getPersona().getNombre());
			mail.sendEmailInformacionProponente(proyecto.getUsuario().getPersona().obtenerContactoPorId(FormasContacto.EMAIL).getValor(), proyecto.getUsuario().getPersona().getNombre(), mensajeProponente, "Proceso reactivado", proyecto.getCodigo(), proyecto.getUsuario(), new Usuario());
						
			//Notificar al tecnico responsable 
			List<String> usuariosTecnico = procesoSuspendidoFacade.listaTecnicos(proyecto.getCodigo(), JsfUtil.getLoggedUser());
			for (String nombreUsuario : usuariosTecnico) {				
				Usuario usuario = usuarioFacade.buscarUsuario(nombreUsuario);
				if(usuario!=null)
				{
					String mensajeTecnico = mensaje.replace("nombre_usuario", usuario.getPersona().getNombre());
					mail.sendEmailInformacionProponente(usuario.getPersona().obtenerContactoPorId(FormasContacto.EMAIL).getValor(), usuario.getPersona().getNombre(), mensajeTecnico, "Proceso reactivado", proyecto.getCodigo(), usuario, new Usuario());
				}
			}
			
		} catch (Exception e) {
			LOG.error(e);
		}
	}
    
    
    
   
}
