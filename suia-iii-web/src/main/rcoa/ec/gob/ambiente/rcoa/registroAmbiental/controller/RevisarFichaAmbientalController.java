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

import lombok.Getter;
import lombok.Setter;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.rcoa.facade.DocumentosRegistroAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.facade.RegistroAmbientalCoaFacade;
import ec.gob.ambiente.rcoa.model.DocumentoRegistroAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.model.RegistroAmbientalRcoa;
import ec.gob.ambiente.rcoa.registroAmbiental.bean.DatosFichaRegistroAmbientalBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.PerforacionExplorativa;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.FichaAmbientalMineria020Facade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class RevisarFichaAmbientalController {
	
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private RegistroAmbientalCoaFacade registroAmbientalCoaFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private DocumentosRegistroAmbientalFacade documentoRegistroAmbientalFacade;
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	@EJB
	private FichaAmbientalMineria020Facade fichaAmbientalMineria020Facade;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{datosFichaRegistroAmbientalBean}")
    private DatosFichaRegistroAmbientalBean datosFichaRegistroAmbientalBean;
	
	private Map<String, Object> variables;
	
	private String tramite;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;
	
	@Getter
	@Setter
	private RegistroAmbientalRcoa registroAmbiental = new RegistroAmbientalRcoa();
		
	@Getter
	@Setter
	private List<DocumentoRegistroAmbiental> listaDocumentos, listaDocumentosElimnados;
	
	@Getter
	@Setter
	private boolean guardado = false;
	
	@Getter
    @Setter
    private Boolean esPronunciamientoAprobado;
	
	@Getter
    @Setter
	private ProyectoLicenciaCuaCiuu proyectoLicenciaCuaCiuu;
	
	@Getter
    @Setter
	private PerforacionExplorativa scoutdrilling;
	
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
					
			if(esScoutDrilling){
				scoutdrilling = fichaAmbientalMineria020Facade.cargarPerforacionExplorativaRcoa(proyecto.getId());
			} else {
				registroAmbiental = registroAmbientalCoaFacade.obtenerRegistroAmbientalPorProyecto(proyecto);
				esPronunciamientoAprobado = true;
			}

			listaDocumentos = new ArrayList<>();
			listaDocumentosElimnados = new ArrayList<DocumentoRegistroAmbiental>();
			
			listaDocumentos = documentoRegistroAmbientalFacade.documentoXTablaIdXIdDocLista(registroAmbiental.getId(), RegistroAmbientalRcoa.class.getSimpleName(), TipoDocumentoSistema.RA_PPC_PRONUNCIAMIENTO_FAVORABLE);
						
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void adjuntarDocumento(FileUploadEvent event) {
		try {
			
			if(listaDocumentos != null && listaDocumentos.size() < 2){
				listaDocumentos.add(uploadListener(event, RegistroAmbientalRcoa.class));
				
				RequestContext.getCurrentInstance().execute("PF('uploadFile').hide();");
			}else{
				JsfUtil.addMessageError("Solo puede añadir máximo dos documentos ");
				RequestContext.getCurrentInstance().execute("PF('uploadFile').hide();");
			}
			
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
		return documento;
	}
	
	public void eliminarDocumento(DocumentoRegistroAmbiental documento){
		listaDocumentos.remove(documento);
		listaDocumentosElimnados.add(documento);
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
	
	public void guardar(){
		try {
			if(esScoutDrilling){
				scoutdrilling.setProyectoLicenciaCoa(proyecto);
				scoutdrilling.setRevisionFichaAmbiental(esPronunciamientoAprobado);
				scoutdrilling = fichaAmbientalMineria020Facade.guardarPerforacionExplorativa(scoutdrilling);
			}
			
			for(DocumentoRegistroAmbiental documento : listaDocumentos){
				if(documento.getId() == null){
					documento.setIdTable(registroAmbiental.getId());
					documentoRegistroAmbientalFacade.guardarDocumentoAlfrescoCA(
							registroAmbiental.getProyectoCoa().getCodigoUnicoAmbiental(),
							"REGISTRO AMBIENTAL PPC", documento,
							TipoDocumentoSistema.RA_PPC_PRONUNCIAMIENTO_FAVORABLE);		
				}						
			}
			
			for(DocumentoRegistroAmbiental documento : listaDocumentosElimnados){
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
	public void registrarApertura() {
		datosFichaRegistroAmbientalBean.setSeRevisoFicha(true);
	}
	
	public boolean validarGuardar(){
		List<String> mensajes = new ArrayList<String>();
		
		if(listaDocumentos == null || listaDocumentos.isEmpty()){
			mensajes.add("Debe añadir por lo menos un documento en la sección Pronunciamiento favorable revisión Ficha Ambiental");
		}
		
		if(!datosFichaRegistroAmbientalBean.isSeRevisoFicha()){
			mensajes.add("Debe ingresar a revisar  los datos de la Ficha Ambiental");
		}
				
		if(!mensajes.isEmpty()){
			JsfUtil.addMessageError(mensajes);
			return false;
		}else{
			return true;
		}
	}
	
	public void completarTarea(){
		try {
			if(!validarGuardar()){
				return;
			}

			if (Boolean.FALSE.equals(esPronunciamientoAprobado)) {
			    proyectoLicenciaCoaFacade.archivarTramiteRcoa(tramite, "Archivo automático por revisión de Ficha Ambiental Scout Drilling");
			    JsfUtil.addMessageWarning("El proyecto " + proyecto.getCodigoUnicoAmbiental() + " ha sido archivado" );
			}

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("esAprobacion", esPronunciamientoAprobado);
			
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),bandejaTareasBean.getTarea().getTaskId(),bandejaTareasBean.getTarea().getProcessInstanceId(), null);
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);		
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}	
	
	public void regresar() throws RuntimeException{
		JsfUtil.redirectToBandeja();
	}
}