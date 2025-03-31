package ec.gob.ambiente.rcoa.registroAmbiental.controller;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.rcoa.facade.DocumentosRegistroAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.PagoRegistroAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.facade.RegistroAmbientalCoaFacade;
import ec.gob.ambiente.rcoa.model.DocumentoRegistroAmbiental;
import ec.gob.ambiente.rcoa.model.PagoRegistroAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.model.RegistroAmbientalRcoa;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class IngresarExpedientePPCRegistroController {
	
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private RegistroAmbientalCoaFacade registroAmbientalCoaFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private DocumentosRegistroAmbientalFacade documentoRegistroAmbientalFacade;
	@EJB
	private PagoRegistroAmbientalFacade pagoRegistroAmbientalFacade;
	@EJB
	private AreaFacade areaFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	
	private Map<String, Object> variables;
	
	private String tramite;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
	
	@Getter
	@Setter
	private RegistroAmbientalRcoa registroAmbiental;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;
	
	@Getter
	@Setter
	private List<DocumentoRegistroAmbiental> listaDocumentos, listaDocumentosOpcional, listaDocumentosElimnados, listaDocumentosOpcionalEliminados, listaFacturas, listaFacturasEliminadas;
	
	@Getter
	@Setter
	private boolean guardado = false;
	
	@Getter
	@Setter
	private PagoRegistroAmbiental pagoRegistroAmbiental;
	
	@Getter
    @Setter
	private ProyectoLicenciaCuaCiuu proyectoLicenciaCuaCiuu;
	
	@Getter
    @Setter
    private boolean esScoutDrilling;
	
	@PostConstruct
	public void init(){
		try {
			
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
			
			proyectoLicenciaCuaCiuu = proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyecto);
			esScoutDrilling = proyectoLicenciaCuaCiuu.getCatalogoCIUU().getScoutDrilling();
					
			registroAmbiental = registroAmbientalCoaFacade.obtenerRegistroAmbientalPorProyecto(proyecto);
			
			pagoRegistroAmbiental = pagoRegistroAmbientalFacade.obtenerPagoPorRegistro(registroAmbiental.getId());
			
			if(pagoRegistroAmbiental == null)
				pagoRegistroAmbiental = new PagoRegistroAmbiental();
			
			listaDocumentos = new ArrayList<>();
			listaDocumentosOpcional = new ArrayList<>();
			listaDocumentosElimnados = new ArrayList<DocumentoRegistroAmbiental>();
			listaDocumentosOpcionalEliminados = new ArrayList<DocumentoRegistroAmbiental>();
			listaFacturas = new ArrayList<DocumentoRegistroAmbiental>();
			listaFacturasEliminadas = new ArrayList<DocumentoRegistroAmbiental>();
			
			listaDocumentos = documentoRegistroAmbientalFacade.documentoXTablaIdXIdDocLista(registroAmbiental.getId(),  RegistroAmbientalRcoa.class.getSimpleName(), TipoDocumentoSistema.PPC_REGISTRO_AMBIENTAL);
			listaDocumentosOpcional = documentoRegistroAmbientalFacade.documentoXTablaIdXIdDocLista(registroAmbiental.getId(),  RegistroAmbientalRcoa.class.getSimpleName(), TipoDocumentoSistema.FICHA_AMBIENTAL_PPC);
			listaFacturas = documentoRegistroAmbientalFacade.documentoXTablaIdXIdDocLista(pagoRegistroAmbiental.getId(),  PagoRegistroAmbiental.class.getSimpleName(), TipoDocumentoSistema.FACTURA_REGISTRO_AMBIENTAL_PPC);
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
		
	public void adjuntarDocumento(FileUploadEvent event) {
		try {
			listaDocumentos.add(uploadListener(event, RegistroAmbientalRcoa.class));
			
			RequestContext.getCurrentInstance().execute("PF('uploadFile').hide();");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void adjuntarDocumentoFicha(FileUploadEvent event) {
		try {
			listaDocumentosOpcional.add(uploadListener(event, RegistroAmbientalRcoa.class));
			
			RequestContext.getCurrentInstance().execute("PF('uploadFileFicha').hide();");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void adjuntarDocumentoFactura(FileUploadEvent event) {
		try {
			if(!listaFacturas.isEmpty() && listaFacturas.size() == 4){
				JsfUtil.addMessageError("Puede adjuntar máximo 4 archivos");
				return;
			}
			
			listaFacturas.add(uploadListener(event, PagoRegistroAmbiental.class));
			
			RequestContext.getCurrentInstance().execute("PF('uploadFileFicha').hide();");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public DocumentoRegistroAmbiental uploadListener(FileUploadEvent event, Class<?> clazz) {
		byte[] contenidoDocumento = event.getFile().getContents();
		DocumentoRegistroAmbiental documento = new DocumentoRegistroAmbiental();
		documento.setContenidoDocumento(contenidoDocumento);
		documento.setNombreTabla(clazz.getSimpleName());
		documento.setUsuarioCreacion(JsfUtil.getLoggedUser().getNombre());
		documento.setMime(event.getFile().getContentType());		
		String fileName=event.getFile().getFileName();
		String[] split=fileName.split("\\.");
		documento.setExtesion("."+split[split.length-1]);		
		documento.setNombre(event.getFile().getFileName());		
//		documento.setIdProceso(bandejaTareasBean.getProcessId());
		return documento;
	}
	
	public void eliminarDocumento(DocumentoRegistroAmbiental documento){
		listaDocumentos.remove(documento);
		listaDocumentosElimnados.add(documento);
	}
	
	public void eliminarDocumentoOpcional(DocumentoRegistroAmbiental documento){
		listaDocumentosOpcional.remove(documento);
		listaDocumentosOpcionalEliminados = new ArrayList<DocumentoRegistroAmbiental>();
	}
	
	public void eliminarFactura(DocumentoRegistroAmbiental documento){
		listaFacturas.remove(documento);
		listaFacturasEliminadas.add(documento);
	}
		
	public StreamedContent descargarDocumentos(DocumentoRegistroAmbiental documento) throws Exception {

		DefaultStreamedContent content = new DefaultStreamedContent();
		if (documento != null) {
			if (documento.getContenidoDocumento() == null) {
				documento.setContenidoDocumento(documentoRegistroAmbientalFacade.descargar(documento.getAlfrescoId()));
			}
			content = new DefaultStreamedContent(new ByteArrayInputStream(documento.getContenidoDocumento()),
					documento.getExtesion());
			content.setName(documento.getNombre());
		}
		return content;
	}
	
	public StreamedContent descargarDocumentosOpcional(DocumentoRegistroAmbiental documento) throws Exception {

		DefaultStreamedContent content = new DefaultStreamedContent();
		if (documento != null) {
			if (documento.getContenidoDocumento() == null) {
				documento.setContenidoDocumento(documentoRegistroAmbientalFacade.descargar(documento.getAlfrescoId()));
			}
			content = new DefaultStreamedContent(new ByteArrayInputStream(documento.getContenidoDocumento()),
					documento.getExtesion());
			content.setName(documento.getNombre());
		}
		return content;
	}
	
	public StreamedContent descargarFacturas(DocumentoRegistroAmbiental documento) throws Exception {

		DefaultStreamedContent content = new DefaultStreamedContent();
		if (documento != null) {
			if (documento.getContenidoDocumento() == null) {
				documento.setContenidoDocumento(documentoRegistroAmbientalFacade.descargar(documento.getAlfrescoId()));
			}
			content = new DefaultStreamedContent(new ByteArrayInputStream(documento.getContenidoDocumento()),
					documento.getExtesion());
			content.setName(documento.getNombre());
		}
		return content;
	}
	
	public void regresar() throws RuntimeException{
		JsfUtil.redirectToBandeja();
	}
	
	public void guardar(){
		try {
			
			registroAmbientalCoaFacade.guardar(registroAmbiental);
			
			for(DocumentoRegistroAmbiental documento : listaDocumentos){
				if(documento.getId() == null){
					documento.setIdTable(registroAmbiental.getId());
					documentoRegistroAmbientalFacade.guardarDocumentoAlfrescoCA(
							registroAmbiental.getProyectoCoa().getCodigoUnicoAmbiental(),
							"REGISTRO AMBIENTAL PPC", documento,
							TipoDocumentoSistema.PPC_REGISTRO_AMBIENTAL);		
				}						
			}
			
			for(DocumentoRegistroAmbiental documento : listaDocumentosOpcional){
				if(documento.getId() == null){
					documento.setIdTable(registroAmbiental.getId());
					documentoRegistroAmbientalFacade.guardarDocumentoAlfrescoCA(
							registroAmbiental.getProyectoCoa().getCodigoUnicoAmbiental(),
							"REGISTRO AMBIENTAL PPC", documento,
							TipoDocumentoSistema.FICHA_AMBIENTAL_PPC);	
				}							
			}
			
			for(DocumentoRegistroAmbiental documento : listaDocumentosElimnados){
				if(documento.getId() != null){
					documento.setEstado(false);
					documentoRegistroAmbientalFacade.guardar(documento);
				}					
			}
			
			for(DocumentoRegistroAmbiental documento : listaDocumentosOpcionalEliminados){
				if(documento.getId() != null){
					documento.setEstado(false);
					documentoRegistroAmbientalFacade.guardar(documento);
				}					
			}
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			
			guardado = true;
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void completarTarea(){
		try {
			if(!validarGuardar()){
				return;
			}
			
			Map<String, Object> parametros = new HashMap<>();
			
			parametros.put("esPpcFisico", registroAmbiental.getEsOficioPPCFisico());
			parametros.put("aprobado", registroAmbiental.getEsPronunciamientoAprobadoPPC());		
			parametros.put("tieneResolucion", registroAmbiental.getEsResolucionRegistroFisica());	
			
			
			if(registroAmbiental.getEsOficioPPCFisico() && registroAmbiental.getEsPronunciamientoAprobadoPPC() && !registroAmbiental.getEsResolucionRegistroFisica()){
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
												
				RequestContext.getCurrentInstance().execute("PF('verificacionPago').show();");
				
			}else{
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),bandejaTareasBean.getTarea().getTaskId(),bandejaTareasBean.getTarea().getProcessInstanceId(), null);
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
				JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public boolean validarGuardar(){
		List<String> mensajes = new ArrayList<String>();
		
		if(listaDocumentos == null || listaDocumentos.isEmpty()){
			mensajes.add("Debe añadir por lo menos un documento en la sección Adjuntar los archivos del Proceso de Participación Ciudadana para la Consulta Ambiental");
		}
				
		if(!mensajes.isEmpty()){
			JsfUtil.addMessageError(mensajes);
			return false;
		}else{
			return true;
		}
	}
	
	public boolean validarPagoFinalizar(){
		List<String> mensajes = new ArrayList<String>();
		
		if(listaFacturas == null || listaFacturas.isEmpty()){
			mensajes.add("Debe añadir por lo menos un documento en la sección Adjuntar los archivos del Proceso de Participación Ciudadana para la Consulta Ambiental");
		}
				
		if(!mensajes.isEmpty()){
			JsfUtil.addMessageError(mensajes);
			return false;
		}else{
			return true;
		}
	}
		
	public void finalizar(){
		try {
			
			Map<String, Object> parametros = new HashMap<>();
			
			registroAmbientalCoaFacade.guardar(registroAmbiental);
			
			if(registroAmbiental.getTienePagoResolucion()){
				if(!validarPagoFinalizar()){
					return;
				}
				
				pagoRegistroAmbiental.setRegistroAmbientalRcoa(registroAmbiental);
				pagoRegistroAmbiental = pagoRegistroAmbientalFacade.guardar(pagoRegistroAmbiental);
				
				for(DocumentoRegistroAmbiental documento : listaFacturas){
					if(documento.getId() == null){
						documento.setIdTable(pagoRegistroAmbiental.getId());
						documentoRegistroAmbientalFacade.guardarDocumentoAlfrescoCA(
								registroAmbiental.getProyectoCoa().getCodigoUnicoAmbiental(),
								"REGISTRO AMBIENTAL PPC", documento,
								TipoDocumentoSistema.FACTURA_REGISTRO_AMBIENTAL_PPC);	
					}							
				}
				
				for(DocumentoRegistroAmbiental documento : listaFacturasEliminadas){
					if(documento.getId() != null){
						documento.setEstado(false);
						documentoRegistroAmbientalFacade.guardar(documento);
					}					
				}
				
				
				List<Usuario> listaUsuario = null;
				String rol="role.registro.ambiental.autoridad";
				Area area = proyecto.getAreaResponsable();
				String rolAutoridad = Constantes.getRoleAreaName(rol);
				
				if(area.getTipoArea().getSiglas().toUpperCase().equals("ZONALES")){
					UbicacionesGeografica provincia = proyecto.getAreaResponsable().getUbicacionesGeografica();	
					area = areaFacade.getAreaCoordinacionZonal(provincia);
				}
								
				if(area.getTipoArea().getSiglas().equals("PC")){
					listaUsuario = usuarioFacade.buscarUsuarioPorRol(Constantes.getRoleAreaName("role.area.subsecretario.calidad.ambiental"));					
				}else if(area.getTipoArea().getSiglas().equals("OT")){
					listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea(rolAutoridad, area.getArea());
				}else{
					listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea(rolAutoridad, area);
				}
				if(listaUsuario != null && listaUsuario.size() > 0){
					parametros.put("autoridadAmbiental", listaUsuario.get(0).getNombre());
				}else{
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
					return;
				}			
			}			
			
			RequestContext.getCurrentInstance().execute("PF('verificacionPago').hide();");			
			
			parametros.put("tienePago", registroAmbiental.getTienePagoResolucion());
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),bandejaTareasBean.getTarea().getTaskId(),bandejaTareasBean.getTarea().getProcessInstanceId(), null);
						
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
			
		} catch (Exception e) {
			e.printStackTrace();
		}		 
	}
}