package ec.gob.ambiente.proyectos.controllers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.prevencion.registrogeneradordesechos.bean.VerRegistroGeneradorDesechoBean;
import ec.gob.ambiente.proyectos.datamodel.LazyProyectRGDDataModel;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.AreaUsuario;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.GeneradorDesechosPeligrosos;
import ec.gob.ambiente.suia.domain.ProcesoSuspendido;
import ec.gob.ambiente.suia.domain.PuntoRecuperacion;
import ec.gob.ambiente.suia.domain.TransaccionFinanciera;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.ResumenInstanciaProceso;
import ec.gob.ambiente.suia.dto.Tarea;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.TransaccionFinancieraFacade;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoSuspendidoFacade;
import ec.gob.ambiente.suia.procesos.bean.ResumenProcesosBean;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ProyectosRGDController implements Serializable {

	private static final long serialVersionUID = 5147000484930491102L;
	private static final Logger LOG = Logger.getLogger(ProyectosRGDController.class);
	

	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;
	
	@EJB
	private ProcesoSuspendidoFacade procesoSuspendidoFacade;
	
	@EJB
	private DocumentosFacade documentosFacade;
	
	@EJB
	private TransaccionFinancieraFacade  transaccionFinancieraFacade;
	
	@EJB
	protected ProcesoFacade procesoFacade;
	
	@EJB
	ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
	

	@Getter
	@Setter	
	private List<GeneradorDesechosPeligrosos> generadorDesechosList;
	
	@Setter
	@Getter
	private LazyDataModel<GeneradorDesechosPeligrosos> proyectosLazy;
	
	@Getter
	@Setter	
	private GeneradorDesechosPeligrosos generadorSeleccionado;
	
	@Getter
	@Setter
	private String motivoEliminar;
	
	@Getter
	@Setter
	private String pagoRgd;
	
	@Getter
    @Setter
    private Documento documentoAdjunto;
	
	@Getter
	@Setter
	private boolean deletionActive;
	
	@Getter	
	private List<Tarea> tareasBpm;
	
	@Getter
	private List<Documento> documentos;
	
	@Getter
	@Setter
	private double montoPagoRgd;
	
	@PostConstruct
	private void init() {
		try {
			deletionActive = Usuario.isUserInRole(JsfUtil.getLoggedUser(), "admin") || Usuario.isUserInRole(JsfUtil.getLoggedUser(), "AUTORIDAD AMBIENTAL")|| Usuario.isUserInRole(JsfUtil.getLoggedUser(), "AUTORIDAD AMBIENTAL MAE");
			//generadorDesechosList=registroGeneradorDesechosFacade.buscarRegistrosGeneradorDesechos(Usuario.isUserInRole(JsfUtil.getLoggedUser(), "admin")?JsfUtil.getLoggedUser().getArea():JsfUtil.getLoggedUser().getArea(),true);
			proyectosLazy=new LazyProyectRGDDataModel(JsfUtil.getLoggedUser());
			documentoAdjunto=new Documento();
			
			
		} catch (Exception e) {
			LOG.error("Error cargando proyectos", e);
		}
	}
	
	public void seleccionarRGD(GeneradorDesechosPeligrosos generadorSeleccionado) {
		documentoAdjunto=new Documento();
		this.generadorSeleccionado=generadorSeleccionado;
		motivoEliminar="";	
		List<TransaccionFinanciera> listTransaccionFinanciera= new ArrayList<TransaccionFinanciera>();
		if(generadorSeleccionado.getCodigo()!=null)			
			listTransaccionFinanciera=transaccionFinancieraFacade.cargarTransacciones(proyectoLicenciamientoAmbientalFacade.getProyectoPorCodigo(generadorSeleccionado.getCodigo(), true));
		else
			listTransaccionFinanciera=transaccionFinancieraFacade.cargarTransacciones(generadorSeleccionado.getSolicitud());
		
		pagoRgd=null;
//		montoPagoRgd = 0.0;
		montoPagoRgd = 180.0;
		if(listTransaccionFinanciera.size()>0){
		pagoRgd=listTransaccionFinanciera.get(0).getNumeroTransaccion();
//		montoPagoRgd = listTransaccionFinanciera.get(0).getMontoTransaccion();
//		montoPagoRgd=procesoSuspendidoFacade.valorPagoRGD(listTransaccionFinanciera.get(0).getNumeroTransaccion());
		}
		
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
        	documento.setNombreTabla(GeneradorDesechosPeligrosos.class.getSimpleName());
    		return documentosFacade.guardarDocumentoAlfrescoSinProyecto(codigoProyecto,Constantes.CARPETA_ARCHIVACION,null,documento,tipoDocumento,null);
    		
    	} catch (Exception e) {
    		LOG.error(e.getMessage());
    		return null;
    	}    	
    }
    
    public String ubicacion(GeneradorDesechosPeligrosos generadorDesechosPeligrosos) {
		List<PuntoRecuperacion> puntoRecuperacion =registroGeneradorDesechosFacade.buscarPuntoRecuperacion(generadorDesechosPeligrosos);
		if(puntoRecuperacion!=null && !puntoRecuperacion.isEmpty()){
			try {
				String ubicacion = puntoRecuperacion.get(0).getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica().getNombre();
				
				return ubicacion;
			} catch (Exception ex) {
				return "SIN DATO";
			}
		}
		return "SIN DATO";
	}
    
    /*public void suspenderRGD() {
		try {
			
			if(documentoAdjunto.getContenidoDocumento()==null)
			{
				JsfUtil.addMessageError("El documento adjunto es requerido");
				return;
			}else
			{				
				documentoAdjunto.setIdTable(generadorSeleccionado.getId());				
				documentoAdjunto=guardarDocumento(generadorSeleccionado.getSolicitud(), documentoAdjunto, TipoDocumentoSistema.ARCHIVACION_PROYECTO_ADJUNTO);					
				if(documentoAdjunto.getId()==null)
				{
					JsfUtil.addMessageError("Error al guardar el documento");
					return;
				}
			}
			boolean reactivarPago=true;
			if(generadorSeleccionado.isFinalizado() && generadorSeleccionado.getCodigo()!=null){
				reactivarPago=false;
			}
			/*			
			if(procesoSuspendidoFacade.modificarPropietarioTareasRGD(generadorSeleccionado.getSolicitud(),JsfUtil.getLoggedUser(),true,reactivarPago,pagoRgd,JsfUtil.getSenderIp()))
			{
				ProcesoSuspendido ps =procesoSuspendidoFacade.getProcesoSuspendidoPorCodigo(generadorSeleccionado.getCodigo());
				if(ps==null)
					ps=new ProcesoSuspendido();
				
				ps.setSuspendido(true);generadorSeleccionado.getSolicitud()
				ps.setCodigo(generadorSeleccionado.getSolicitud());
				ps.setTipoProyecto(ProcesoSuspendido.TIPO_RGD_NO_ASOCIADO);
				ps.setDescripcion(motivoEliminar);				
				
				procesoSuspendidoFacade.guardar(ps,JsfUtil.getLoggedUser().getNombre());
				procesoSuspendidoFacade.setEstadoProyectoRGD(generadorSeleccionado.getId(),false);						
				
				JsfUtil.addMessageInfo("Se ha archivado el Registro generador de desecho.");
			}else{
				JsfUtil.addMessageError("No se encontraron tareas para archivar");
			}	*/	
		/*	generadorDesechosList=registroGeneradorDesechosFacade.buscarRegistrosGeneradorDesechos(Usuario.isUserInRole(JsfUtil.getLoggedUser(), "admin")?JsfUtil.getLoggedUser().getArea():JsfUtil.getLoggedUser().getArea(),true);
			//JsfUtil.addCallbackParam("eliminarRGD");
		} catch (Exception ex) {
			LOG.error(ex);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}    */
    private ResumenInstanciaProceso buscarProceso(String solicitudRgd){    	    	
    	try {
			List<ProcessInstanceLog> process= procesoFacade.getProcessInstancesLogsVariableValue(JsfUtil.getLoggedUser(), "numeroSolicitud", solicitudRgd);
			for (ProcessInstanceLog processInstanceLog : process) {				
				if(processInstanceLog.getProcessId().compareTo(Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS)==0)
				{					 
					return new ResumenInstanciaProceso(processInstanceLog, solicitudRgd); 			
				}
			}
		} catch (JbpmException e) {
			LOG.error(e);
		}
    	return null;
    }    
    public void verTareas(String solicitudRgd){
    	tareasBpm=new ArrayList<Tarea>();   
    	ResumenInstanciaProceso proceso=buscarProceso(solicitudRgd);
    	if(proceso!=null)
    	{
    		ResumenProcesosBean resumenBean = JsfUtil.getBean(ResumenProcesosBean.class);
    		resumenBean.verTareas(proceso);
    		tareasBpm=resumenBean.getTareas();	
    	}    	
    }    
    public void verDocumentos(String solicitudRgd){
    	documentos=new ArrayList<Documento>();
    	ResumenInstanciaProceso proceso=buscarProceso(solicitudRgd);
    	if(proceso!=null)
    	{
    		ResumenProcesosBean resumenBean = JsfUtil.getBean(ResumenProcesosBean.class);
    		resumenBean.verDocumentos(proceso);
    		documentos=resumenBean.getDocumentos();	
    	}
    }
	public StreamedContent getStream(Documento documento) {
		if (documento.getContenidoDocumento() != null) {
			InputStream is = new ByteArrayInputStream(documento.getContenidoDocumento());
			return new DefaultStreamedContent(is, documento.getMime(), documento.getNombre());
		} else {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		return null;
	}	
	public void verDatosRgd(Integer id){
		JsfUtil.cargarObjetoSession("rgdIdLectura", id);		
		JsfUtil.getBean(VerRegistroGeneradorDesechoBean.class).redirectVerGeneradorDesechosPeligrosos(id,false, true,false);
	} 
	
    public void suspenderRGD() {
		try {
			boolean reactivarPago=true;
			if(!procesoSuspendidoFacade.verificarTareasRGD(generadorSeleccionado.getCodigo()!=null?generadorSeleccionado.getCodigo():generadorSeleccionado.getSolicitud(),JsfUtil.getLoggedUser()))
			{
				JsfUtil.addMessageError("No es posible archivar el proceso, se encuentra finalizado");
				return;
			}
					
//			if(generadorSeleccionado.isFinalizado() && generadorSeleccionado.getCodigo()!=null){
//				reactivarPago=false;
//			}
			
			if(documentoAdjunto.getContenidoDocumento()==null)
			{
				JsfUtil.addMessageError("El documento adjunto es requerido");
				return;
			}else
			{				
				documentoAdjunto.setIdTable(generadorSeleccionado.getId());				
				documentoAdjunto=guardarDocumento(generadorSeleccionado.getSolicitud(), documentoAdjunto, TipoDocumentoSistema.ARCHIVACION_PROYECTO_ADJUNTO);					
				if(documentoAdjunto.getId()==null)
				{
					JsfUtil.addMessageError("Error al guardar el documento");
					return;
				}
			}
						
			if(procesoSuspendidoFacade.modificarPropietarioTareasRGD(generadorSeleccionado.getCodigo()!=null?generadorSeleccionado.getCodigo():generadorSeleccionado.getSolicitud(),JsfUtil.getLoggedUser(),true,reactivarPago,pagoRgd,JsfUtil.getSenderIp()))
			{
				ProcesoSuspendido ps =procesoSuspendidoFacade.getProcesoSuspendidoPorCodigo(generadorSeleccionado.getCodigo());
				if(ps==null)
					ps=new ProcesoSuspendido();
				
				ps.setSuspendido(true);
				ps.setCodigo(generadorSeleccionado.getSolicitud());
				ps.setTipoProyecto(ProcesoSuspendido.TIPO_RGD_NO_ASOCIADO);
				ps.setDescripcion(motivoEliminar);				
				
				procesoSuspendidoFacade.guardar(ps,JsfUtil.getLoggedUser().getNombre());
				procesoSuspendidoFacade.setEstadoProyectoRGD(generadorSeleccionado.getId(),false);						
				
				JsfUtil.addMessageInfo("Se ha archivado el Registro generador de Desechos.");
			}else{
				JsfUtil.addMessageError("No es posible archivar el proceso, se encuentra finalizado");
			}		
			
			generadorDesechosList = new ArrayList<GeneradorDesechosPeligrosos>();
			for(AreaUsuario areaUsuario : JsfUtil.getLoggedUser().getListaAreaUsuario()){
				generadorDesechosList.addAll(registroGeneradorDesechosFacade.buscarRegistrosGeneradorDesechos(areaUsuario.getArea(), true));
			}
			
//			generadorDesechosList=registroGeneradorDesechosFacade.buscarRegistrosGeneradorDesechos(Usuario.isUserInRole(JsfUtil.getLoggedUser(), "admin")?JsfUtil.getLoggedUser().getArea():JsfUtil.getLoggedUser().getArea(),true);
			JsfUtil.addCallbackParam("eliminarRGD");
		} catch (Exception ex) {
			
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
}