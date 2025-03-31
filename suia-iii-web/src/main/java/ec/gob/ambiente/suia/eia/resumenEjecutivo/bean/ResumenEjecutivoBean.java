package ec.gob.ambiente.suia.eia.resumenEjecutivo.bean;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.eia.facade.EstudioImpactoAmbientalFacade;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;

@ManagedBean
@ViewScoped
public class ResumenEjecutivoBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -856122376157158018L;

	@EJB
	EstudioImpactoAmbientalFacade estudioImpactoAmbientalFacade;
	
	@EJB
	private ValidacionSeccionesFacade validacionSeccionesFacade;
	
	@EJB
	private DocumentosFacade documentosFacade;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@ManagedProperty(value = "#{proyectosBean}")
	@Getter
	@Setter
	private ProyectosBean proyectosBean;

	@Getter
	@Setter
	private boolean proyectoHidrocarburos;
		
	@Getter
	private boolean esMineriaNoMetalicos;

	@Getter
	@Setter
	private EstudioImpactoAmbiental estudio, estudioHistorico, estudioOriginal;
	
	@Getter
	@Setter
	private Documento documentoGeneral, documentoGeneralHistorico, documentoGeneralOriginal;
	
	@Getter
	private boolean existeDocumentoAdjuntoGeneral;
		
	private boolean verDiag=false;	//Cris F: estaba en true
	private boolean verDiagActividades=true;

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(ResumenEjecutivoBean.class);
	@Getter
	@Setter
	private Documento documentoDefinicionAreaEstudio;
	
	//Cris F: Aumento de Variables
	@Getter
	@Setter
	private boolean existeObservaciones;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
	
	private Map<String, Object> processVariables;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
	
	private Integer numeroNotificaciones;
	
	@Getter
	@Setter
	private List<EstudioImpactoAmbiental> listaEstudioOriginales;
	
	@Getter
	@Setter
	private List<Documento> listaDocumentosHistorico;
	//Fin de aumento variables
	
	@Getter
	@Setter
	private boolean alertaEIA;
	
	@Getter
	@Setter
	private List<Documento> documentosAprobacionTdrs;
	
	
	@PostConstruct
	public void init() throws CmisAlfrescoException, ParseException, JbpmException{
		
		documentoGeneral = new Documento();
		documentoGeneralHistorico = new Documento();
		
		processVariables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getProcessId());
		
		String numNotificaciones = (String) processVariables.get("cantidadNotificaciones");
		if(numNotificaciones != null){
			numeroNotificaciones = Integer.valueOf(numNotificaciones);
		}else{
			numeroNotificaciones = 0;
		}			
		
		String promotor = (String) processVariables.get("u_Promotor");		
		
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        estudio=(EstudioImpactoAmbiental) request.getSession().getAttribute("estudio");
//		estudio = estudioImpactoAmbientalFacade.obtenerEIAPorId(((EstudioImpactoAmbiental) JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT)).getId());
		
		if(estudio.getProyectoLicenciamientoAmbiental().getTipoSector().getId().equals(TipoSector.TIPO_SECTOR_HIDROCARBUROS)){
			proyectoHidrocarburos = true;

		}
		esMineriaNoMetalicos=estudio.getProyectoLicenciamientoAmbiental().getCatalogoCategoria().getCodigo().equals("21.02.03.02") && estudio.getResumenEjecutivo()==null?true:false;

		if(esMineriaNoMetalicos)
			this.cargarAdjuntosEIA(TipoDocumentoSistema.FICHA_TECNICA_GEN);
		
		//Cristina Flores aumento de método para historico
		if (numeroNotificaciones > 0) {
			existeObservaciones = true;
//			if (promotor.equals(loginBean.getNombreUsuario())) {
				consultarDocumentoAnterior();
				consultarRegistroAnterior();
//			} else {
			if(existeObservaciones){
				if (!esMineriaNoMetalicos)
					consultarHistorico();
				else
					consultarDocumentoAnterior();
			}
//			}
		}
		//buscar archivos de aprobacion de TDR's
		if (validarProyectoRequiereAprobacionTdrs()){
			documentosAprobacionTdrs = documentosFacade.documentoXTablaIdXIdDoc(proyectosBean.getProyecto().getId(), 
							ProyectoLicenciamientoAmbiental.class.getSimpleName(),
							TipoDocumentoSistema.TIPO_RESPALDOS_APROBACION_TDRS);
		}
	}

	public void guardar() throws CmisAlfrescoException{
		try {
			if(!esMineriaNoMetalicos){
				//historico
				if(existeObservaciones){
					if(validarGuardarHistoricoEA()){
						estudioHistorico.setNumeroNotificacion(numeroNotificaciones);
						estudioImpactoAmbientalFacade.guardar(estudioHistorico);
					}
				}
				
				estudio = estudioImpactoAmbientalFacade.guardar(estudio);
								
			}else{
				if (this.documentoGeneral.getNombre() == null) {					
					JsfUtil.addMessageError("El campo 'Resumen Ejecutivo' es requerido.");
					return;
				}
				if(!salvarDocumentos()){
					return;
				}
			}
			
			validacionSeccionesFacade.guardarValidacionSeccion("EIA", "resumenEjecutivo",estudio.getId().toString());
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);	
			JsfUtil.redirectTo("/prevencion/licenciamiento-ambiental/eia/resumenEjecutivo/resumenEjecutivo.jsf");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void cancelar() {
		JsfUtil.redirectTo("/prevencion/licenciamiento-ambiental/eia/resumenEjecutivo/resumenEjecutivo.jsf");

	}
	
	public void uploadListenerDocumentoGeneral(FileUploadEvent event) {
		documentoGeneral = this.uploadListener(event, EstudioImpactoAmbiental.class, "pdf");
		this.existeDocumentoAdjuntoGeneral = true;
	}
	
	private Documento uploadListener(FileUploadEvent event, Class<?> clazz, String extension) {
		byte[] contenidoDocumento = event.getFile().getContents();
		Documento documento = crearDocumento(contenidoDocumento, clazz, extension);
		documento.setNombre(event.getFile().getFileName());
		return documento;
	}
	
	/**
	 * Crea el documento
	 *
	 * @param contenidoDocumento arreglo de bytes
	 * @param clazz              Clase a la cual se va a ligar al documento
	 * @param extension          extension del archivo
	 * @return Objeto de tipo Documento
	 */
	public Documento crearDocumento(byte[] contenidoDocumento, Class<?> clazz, String extension) {
		Documento documento = new Documento();
		documento.setContenidoDocumento(contenidoDocumento);
		documento.setNombreTabla(clazz.getSimpleName());
		documento.setIdTable(0);
		documento.setExtesion("." + extension);

		documento.setMime(extension == "pdf" ? "application/pdf" : "application/vnd.ms-excel");
		return documento;
	}
	
	public boolean salvarDocumentos() {
		try {
			documentoGeneral.setIdTable(this.estudio.getId());
			documentoGeneral.setDescripcion("Resumen Ejecutivo");
			documentoGeneral.setEstado(true);
			if (documentoGeneral.getContenidoDocumento() != null) {
				Documento documento = documentosFacade.guardarDocumentoAlfresco(
						this.estudio.getProyectoLicenciamientoAmbiental().getCodigo(), Constantes.CARPETA_EIA, 0L,
						documentoGeneral, TipoDocumentoSistema.FICHA_TECNICA_GEN, null);
				
				if (existeObservaciones && documentoGeneralHistorico != null) {
					documentoGeneralHistorico.setIdHistorico(documento.getId());
					documentoGeneralHistorico.setNumeroNotificacion(numeroNotificaciones);
					documentosFacade.actualizarDocumento(documentoGeneralHistorico);
				}
				
				return true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public StreamedContent getStreamContent() throws Exception {
		DefaultStreamedContent content = null;
		try {
			this.documentoGeneral = this.descargarAlfresco(this.documentoGeneral);
			if (documentoGeneral != null && documentoGeneral.getNombre() != null && documentoGeneral.getContenidoDocumento() != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoGeneral.getContenidoDocumento()));
				content.setName(documentoGeneral.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
		} catch (Exception exception) {
			LOG.error(JsfUtil.MESSAGE_ERROR_ALFRESCO, exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
		}
		return content;
	}
	
	public Documento descargarAlfresco(Documento documento) throws CmisAlfrescoException {
		byte[] documentoContenido = null;
		if (documento != null && documento.getIdAlfresco() != null)
			documentoContenido = documentosFacade.descargar(documento.getIdAlfresco());
		if (documentoContenido != null)
			documento.setContenidoDocumento(documentoContenido);
		return documento;
	}

	public boolean isVerDiag() {
		if(verDiag){
			verDiag=false;
			return true;
		}
		return verDiag;
	}
	
	 public void cancelarActividadesMineria() {
		 JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
	    }
	 
	public boolean isverDiagActividades() throws ParseException {
		verDiagActividades=true;

    	if (validarProyectoRequiereAprobacionTdrs()){
			if (verDiagActividades) {				
				if (!documentosAprobacionTdrs.isEmpty()) {
					verDiagActividades = false;
					return false;
				}
				verDiagActividades = false;
				return true;
			} else {
				verDiagActividades = false;
				return false;
			}
		} else {
			verDiagActividades = false;
			return false;
		}
//		return verDiagActividades;
	}
	
	private void cargarAdjuntosEIA(TipoDocumentoSistema tipoDocumento) throws CmisAlfrescoException {
		List<Documento> documentosXEIA = documentosFacade.documentoXTablaIdXIdDoc(this.estudio.getId(),
				"EstudioImpactoAmbiental", tipoDocumento);
		for(int i = 0; i < documentosXEIA.size(); i++) {
			if(documentosXEIA.get(i).getDescripcion().equals("Resumen Ejecutivo")){
				this.documentoGeneral = documentosXEIA.get(i);
				this.existeDocumentoAdjuntoGeneral = true;
				break;
			}			
		}
	}
	
	private boolean validarProyectoRequiereAprobacionTdrs() throws ParseException{
		//Cris F: Descomentar el codigo abajo comentado
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        Date fechabloqueo = sdf.parse(Constantes.getFechaBloqueoTdrMineria());
//        Date fechaproyecto=sdf.parse(proyectosBean.getProyecto().getFechaRegistro().toString());
//        boolean bloquear=false;        
//        if (fechaproyecto.before(fechabloqueo)){
//        	bloquear=true;
//        }    
		if ((proyectosBean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.03.01") 
    			|| proyectosBean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.03.02")
    			|| proyectosBean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.03.03")
    			|| proyectosBean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.03.04")    			
    			|| proyectosBean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.04.01")
    			|| proyectosBean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.04.02")    			
    			|| proyectosBean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.05.01")
    			|| proyectosBean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.05.02")    			
    			|| proyectosBean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.08.01")    			
    			|| proyectosBean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.07.01")
    			|| proyectosBean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.07.02")
    			|| proyectosBean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.06.01")
    			|| proyectosBean.getProyecto().getCatalogoCategoria().getCodigo().equals("21.02.06.03")
				) //&& proyectosBean.getProyecto().getIdEstadoAprobacionTdr()==null && bloquear
				){
			return true;
		}
		return false;
	}
	
	/**
	 * Cristina Flores aumento de metódo para guardar el historico.
	 */		
	private void consultarRegistroAnterior(){
		try {
				estudioHistorico = estudio.clone();
				estudioHistorico.setFechaModificacion(null);
				estudioHistorico.setIdHistorico(estudio.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	private void consultarDocumentoAnterior() {
		try {
			if(existeDocumentoAdjuntoGeneral){
				documentoGeneralHistorico = validarDocumentoHistorico(documentoGeneral);		
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean validarGuardarHistoricoEA(){		
		try {
			List<EstudioImpactoAmbiental> lista = estudioImpactoAmbientalFacade.busquedaRegistrosHistorico(estudio.getId());
		
			/**
			 * Si el tamaño de la lista es el mismo que el número de notificiaciones entonces no se guarda
			 * Si la lista es nula entonces se guarda
			 * Si el tamaño de la lista es menor que el número de notificaciones se guarda
			 */
			
			if(lista != null && !lista.isEmpty()){
				if(numeroNotificaciones > lista.size())
					return true; // se guarda
				else
					return false;  //no se guarda
			}else {
				/**
				 * Se valida si hubo algún cambio para poder guardar el objeto, si no existió ningún cambio 
				 * no se guarda
				 */
				
				if(estudio.getResumenEjecutivo().equals(estudioHistorico.getResumenEjecutivo())){
					//Si son iguales entonces no se guarda el historico
					return false;
				}else			
					return true; // se guarda
			}				
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}		
	}	
	
	private Documento validarDocumentoHistorico(Documento documentoIngresado){        
        try {
            listaDocumentosHistorico = new ArrayList<Documento>();
            List<Documento> documentosList = documentosFacade.getDocumentosPorIdHistorico(documentoIngresado.getId());
            
            if(documentosList != null && !documentosList.isEmpty()){
                documentoGeneralOriginal = documentosList.get(0);
                
                if(numeroNotificaciones > 1){
                    List<Documento> documentosHist = documentosFacade.getDocumentosPorIdHistorico(documentoGeneralOriginal.getId());
                    if(documentosHist != null && !documentosHist.isEmpty()){
                        listaDocumentosHistorico.add(documentosHist.get(0));
                    }
                }
                    
                listaDocumentosHistorico.add(documentosList.get(0));
                
                boolean existeDocumentoNotificacion = false;
                Documento documentoHistoricoNotificacion = new Documento();
                for(Documento documentoHistorico : documentosList){
                	if(documentoHistorico.getNumeroNotificacion().equals(numeroNotificaciones)){
                		documentoHistoricoNotificacion = documentoHistorico;
                		existeDocumentoNotificacion = true;
                	}
                }
                
                if(existeDocumentoNotificacion)
                	return documentoHistoricoNotificacion;
                else
                	return documentoIngresado;
            }else{
                return documentoIngresado;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return documentoIngresado;
        }        
    }
	
	/**
	 * MarielaG
	 * Consultar el estudio ingresado antes de las correcciones
	 * Cris F: cambio para que se tenga una lista de estudio para mostrar en una tabla.
	 */	
	@Getter
	@Setter
	private boolean mostrarTabla;
	private void consultarHistorico() {
		try {
			List<EstudioImpactoAmbiental> listaHistorial =  estudioImpactoAmbientalFacade.busquedaRegistrosHistorico(estudio.getId());
			listaEstudioOriginales = new ArrayList<EstudioImpactoAmbiental>();
			
			if(listaHistorial != null && !listaHistorial.isEmpty()){
				
				for(EstudioImpactoAmbiental estudioH : listaHistorial){
					if(!estudio.getResumenEjecutivo().equals(estudioH.getResumenEjecutivo())){
						listaEstudioOriginales.add(0, estudioH);
					}
				}				
			}

			if(listaEstudioOriginales.isEmpty())
				mostrarTabla = false;
			else
				mostrarTabla = true;
			
//			if (lista != null && !lista.isEmpty() && lista.size() == numeroNotificaciones) {
//				estudioOriginal = lista.get(0);
//			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Cris F:
	 * Obtener documento historico
	 */
	public StreamedContent getStreamContentOriginal(Documento documentoGeneralOriginal) throws Exception {
		DefaultStreamedContent content = null;
		try {
			documentoGeneralOriginal = this.descargarAlfresco(documentoGeneralOriginal);
			if (documentoGeneralOriginal != null && documentoGeneralOriginal.getNombre() != null && documentoGeneralOriginal.getContenidoDocumento() != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoGeneralOriginal.getContenidoDocumento()));
				content.setName(documentoGeneralOriginal.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
		} catch (Exception exception) {
			LOG.error(JsfUtil.MESSAGE_ERROR_ALFRESCO, exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
		}
		return content;
	}	
}
