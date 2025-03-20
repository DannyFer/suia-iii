package ec.gob.ambiente.rcoa.sustancias.quimicas.controllers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.enums.CatalogoTipoCoaEnum;
import ec.gob.ambiente.rcoa.enums.TipoInformeOficioEnum;
import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.AnalisisTecnicoRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.DocumentosRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.InformesOficiosRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.ObservacionesRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.RegistroSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.UbicacionSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DocumentosSustanciasQuimicasRcoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.InformeOficioRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.ObservacionesFormulariosRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.RegistroSustanciaQuimica;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.UbicacionSustancia;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class RevisarResultadoInspeccionRSQController implements Serializable {

	private static final long serialVersionUID = -1L;

	private static final Logger LOG = Logger.getLogger(RevisarResultadoInspeccionRSQController.class);
	
	//EJBs
	@EJB
	private AnalisisTecnicoRSQFacade analisisTecnicoRSQFacade;
	
	@EJB
	private CatalogoCoaFacade catalogoCoaFacade;
	
	@EJB
	private DocumentosRSQFacade documentosRSQFacade;

	@EJB
	private InformesOficiosRSQFacade informesOficiosRSQFacade;	
	        
    @EJB
    private ObservacionesRSQFacade observacionesRSQFacade;
    
    @EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
    
    @EJB
    private OrganizacionFacade organizacionFacade;

	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	    
	@EJB
	private RegistroSustanciaQuimicaFacade registroSustanciaQuimicaFacade;  
		
	@EJB
    private UbicacionSustanciaQuimicaFacade ubicacionSustanciaQuimicaFacade;
	
	//BEANs
    @ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
  	
    //LISTs
    @Getter
    private List<CatalogoGeneralCoa> tipoPronunciamientoLista;
    
    @Getter
    private List<InformeOficioRSQ> informeOficioRSQLista;
    
    @Getter
    private List<UbicacionSustancia> ubicacionSustanciaProyectoLista;
    
    //OBJs
    @Getter
    @Setter
    private DocumentosSustanciasQuimicasRcoa documentoInforme;
    
    private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
	private RegistroSustanciaQuimica registroSustanciaQuimica;
	
    @Getter
	@Setter
	private Usuario usuarioDirector;
    
    //MAPs    
	private Map<String, Object> variables;
	      
    
    //STRINGs	
	private String varTramite;
    			
	
	//INTEGERs
	private Integer numeroRevision;

	@PostConstruct
	public void init() {
		try {
			cargarDatosTarea();
			cargarDatosProyecto();	
		} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos InformeInspeccionRSQController.");
		}
	}
	
	private void cargarDatosTarea() throws ServiceException{

		try {
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			varTramite=(String)variables.get("tramite");			
			//varTramite="MAE-RA-2020-415478";
			numeroRevision=variables.containsKey("numero_revision")?(Integer.valueOf((String)variables.get("numero_revision"))):1;
		} catch (JbpmException e) {
			LOG.error("Error al recuperar variables numero_observaciones "+e.getCause()+" "+e.getMessage());
		}
	}
	
	private void cargarDatosProyecto(){		
		proyectoLicenciaCoa=proyectoLicenciaCoaFacade.buscarProyecto(varTramite);
		registroSustanciaQuimica=registroSustanciaQuimicaFacade.obtenerRegistroPorProyecto(proyectoLicenciaCoa);
		tipoPronunciamientoLista=catalogoCoaFacade.obtenerCatalogo(CatalogoTipoCoaEnum.TIPO_PRONUNCIAMIENTO);
		
		
		if(registroSustanciaQuimica!=null) {
			ubicacionSustanciaProyectoLista=ubicacionSustanciaQuimicaFacade.obtenerUbicacionesPorRSQ(registroSustanciaQuimica);
			informeOficioRSQLista=informesOficiosRSQFacade.obtenerPorRSQLista(registroSustanciaQuimica, TipoInformeOficioEnum.INFORME_INSPECCION, numeroRevision);
			
			for (InformeOficioRSQ item : informeOficioRSQLista) {
				//Informe de Inspeccion del Tecnico Principal
				if(item.getArea().getId().intValue()==registroSustanciaQuimica.getArea().getId().intValue()) {
					documentoInforme=documentosRSQFacade.obtenerDocumentoPorTipo(TipoDocumentoSistema.RCOA_RSQ_INFORME_INSPECCION_APROBADO_OBSERVADO, InformeOficioRSQ.class.getSimpleName(), item.getId());	
				}
				
				//Informes de Inspeccion  de los Tecnicos de apoyo
				item.setDocumento(documentosRSQFacade.obtenerDocumentoPorTipo(TipoDocumentoSistema.RCOA_RSQ_INFORME_INSPECCION_APROBADO_OBSERVADO, InformeOficioRSQ.class.getSimpleName(), item.getId()));
				
			}
			
		}	
		
	}
	
	public List<UbicacionSustancia> getUbicacionSustanciaProyectoListaObservaciones(){
		List<UbicacionSustancia> lista=new ArrayList<>();
		for (UbicacionSustancia item : ubicacionSustanciaProyectoLista) {
			if(item.getNecesitaInspeccion() && item.getCumpleValor()!=null && !item.getCumpleValor()) {
				lista.add(item);
			}
		}
		
		return lista;
	} 	
		
	public InformeOficioRSQ buscarInforme(UbicacionSustancia ubicacionSustancia) {
		try {			
			for (InformeOficioRSQ item : informeOficioRSQLista) {
				if(ubicacionSustancia.getArea()!=null && ubicacionSustancia.getArea().getId().intValue()==item.getArea().getId().intValue()) {
					return item;					
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public StreamedContent descargarDocumento(UbicacionSustancia ubicacionSustancia) {
		InformeOficioRSQ informe=buscarInforme(ubicacionSustancia);
		if(informe!=null)
			return descargarDocumento(informe.getDocumento());
		return null;
	}
	
	private StreamedContent descargarDocumento(DocumentosSustanciasQuimicasRcoa documento) {
		try {
			if(documento.getContenidoDocumento()==null) {
				documento.setContenidoDocumento(documentosRSQFacade.descargar(documento.getIdAlfresco()));
			}
			
            byte[] byteFile = documento.getContenidoDocumento();
            if (byteFile != null) {
                InputStream is = new ByteArrayInputStream(byteFile);
                return new DefaultStreamedContent(is, "application/pdf", documento.getNombre());
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
	
	
	private boolean validarObservaciones() {
		List<ObservacionesFormulariosRSQ> lista=new ArrayList<ObservacionesFormulariosRSQ>();
		try {
			lista=observacionesRSQFacade.listarPorIdClaseNombreClaseNoCorregidas(registroSustanciaQuimica.getId(), RegistroSustanciaQuimica.class.getSimpleName());
			if(registroSustanciaQuimica.getTipoPronunciamiento().getNombre().contains("APROBADO")) {
				if(lista!=null && !lista.isEmpty() || !getUbicacionSustanciaProyectoListaObservaciones().isEmpty()) {
					JsfUtil.addMessageWarning("Existen Observaciones Sin Corregir");
					return false;
				}				
				
			}else {
				if((lista==null || lista.isEmpty()) && getUbicacionSustanciaProyectoListaObservaciones().isEmpty()) {
					JsfUtil.addMessageWarning("Debe agregar al menos 1 Observación");
					return false;
				}				
			}
			return true;
		} catch (ServiceException e) {
			LOG.error("Error al validarObservaciones en RevisarResultadoInspeccionRSQController."+e.getMessage());
		}
		
		return false;
	}
	
	public boolean guardar() {
		try {
			if(registroSustanciaQuimica.getTipoPronunciamiento()!=null)
				registroSustanciaQuimicaFacade.guardar(registroSustanciaQuimica,JsfUtil.getLoggedUser());
			
			for (UbicacionSustancia item : ubicacionSustanciaProyectoLista) {
				ubicacionSustanciaQuimicaFacade.guardar(item,JsfUtil.getLoggedUser());
			}	
			JsfUtil.addMessageInfo("Información Guardada Correctamente"); 
			return true;

		} catch (Exception e) {
			LOG.error("Error al guardar en RevisarResultadoInspeccionRSQController.", e);
			JsfUtil.addMessageError("Ocurrió un error al guardar. Comuníquese con mesa de ayuda.");
		}
		return false;
	}
	
	public void enviar()
	{
		if(guardar() && validarObservaciones()) {
			try {
				
				if(registroSustanciaQuimica.pronunciamientoObservado()) {
					notificacionObservaciones();
				}
				
				Map<String, Object> params=new HashMap<>();							
				params.put("observaciones_rsq",registroSustanciaQuimica.pronunciamientoObservado());
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), params);
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
				JsfUtil.redirectToBandeja();
			} catch (/*Jbpm*/Exception e) {
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				LOG.error("Error al enviar en RevisarResultadoInspeccionRSQController."+e.getMessage());
			}
		}
	}
	
	private void notificacionObservaciones(){		
		try {
			Usuario uOperador=registroSustanciaQuimica.getProyectoLicenciaCoa().getUsuario();		
			String nombreOperador=JsfUtil.getNombreOperador(uOperador, organizacionFacade.buscarPorRuc(uOperador.getNombre()));
			String nombreProyecto=registroSustanciaQuimica.getProyectoLicenciaCoa().getNombreProyecto();
			String codigoTramite=registroSustanciaQuimica.getProyectoLicenciaCoa().getCodigoUnicoAmbiental();			
			String mensaje = mensajeNotificacionFacade.recuperarValorMensajeNotificacion("bodyNotificacionRSQPronunciamientoObservado", new Object[]{nombreOperador,nombreProyecto, codigoTramite});
			Email.sendEmail(uOperador, "Registro Sustancias Quimicas", mensaje, varTramite, JsfUtil.getLoggedUser());				
		
		} catch (Exception e) {
			LOG.error("No se envio la notificacion al usuario. "+e.getCause()+" "+e.getMessage());
		}			
	}
	
	public void irBandeja() {
		JsfUtil.redirectToBandeja();
	}
}
