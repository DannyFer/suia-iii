package ec.gob.ambiente.control.retce.beans;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang.SerializationUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.Visibility;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.retce.model.DesechoExportacion;
import ec.gob.ambiente.retce.model.DetalleCatalogoGeneral;
import ec.gob.ambiente.retce.model.DocumentoExportacion;
import ec.gob.ambiente.retce.model.GeneradorDesechosPeligrososRetce;
import ec.gob.ambiente.retce.model.IdentificacionDesecho;
import ec.gob.ambiente.retce.services.DetalleCatalogoGeneralFacade;
import ec.gob.ambiente.retce.services.ExportacionDesechosFacade;
import ec.gob.ambiente.retce.services.GeneradorDesechosPeligrososFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ExportacionDesechosBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value = "#{declaracionGeneradorRetceBean}")
	@Getter
	@Setter
	private DeclaracionGeneradorRetceBean declaracionGeneradorRetceBean;

	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;

	@EJB
	private DetalleCatalogoGeneralFacade detalleCatalogoGeneralFacade;
	
	@EJB
	private ExportacionDesechosFacade exportacionDesechosFacade;
	
	@EJB
	private DocumentosFacade documentosFacade;
	
	@EJB
	private GeneradorDesechosPeligrososFacade generadorDesechosPeligrososFacade;

	@Getter
	@Setter
	private List<UbicacionesGeografica> listaPaisDestino;

	@Getter
	@Setter
	private List<DesechoExportacion> listaExportacionDesechos, listaExportacionDesechosOriginales, listaExportacionDesechosEliminados;

	@Getter
	@Setter
	private List<DesechoExportacion> listaExportacionDesechosEliminar;

	@Getter
	@Setter
	private List<DocumentoExportacion> listaDocumentosExportacion, listaDocumentosExportacionHistorial;
	
	@Getter
	@Setter
	private List<Documento> listaDocumentosExportacionEliminar, listaDocumentosExportacionOriginales;
	
	@Getter
	@Setter
	private List<Documento> listaDocumentosNotificacion, listaDocumentosAutorizacion, listaDocumentosMovimiento, listaDocumentosDestruccion;
	
	@Getter
	@Setter
	private List<GeneradorDesechosPeligrososRetce> listaRealizaExportacionHistorial;

	@Getter
	@Setter
	private List<DetalleCatalogoGeneral> listaFases;

	@Getter
	@Setter
	private DesechoPeligroso desechoSeleccionado;

	@Getter
	@Setter
	private DesechoExportacion desechoExportacion, desechoExportacionOriginal, desechoExportacionSeleccionado;

	@Getter
	@Setter
	private Documento nuevoDocumento;
	
	@Getter
	@Setter
	private GeneradorDesechosPeligrososRetce generadorDesechosRetce;

	@Getter
	@Setter
	private boolean editar;

	@Getter
	private boolean panelAdicionarVisible;
	
	@Getter
	@Setter
	private boolean habilitarBtnSiguiente, mostrarHistorialDesechos;
	
	@Getter
	@Setter
	private Integer numeroObservaciones;

	@PostConstruct
	private void initDatos() {
		inicializacionObj();
		
		habilitarBtnSiguiente = false;

		listaPaisDestino = ubicacionGeograficaFacade.getListaPaises();

		listaFases = detalleCatalogoGeneralFacade
				.findByCatalogoGeneralString("generador.fases_exportacion");

		generadorDesechosRetce = declaracionGeneradorRetceBean.getGeneradorDesechosRetce();
		
	}
	
	public void inicializacionObj() {
		desechoSeleccionado = null;
		desechoExportacion = new DesechoExportacion();

		listaExportacionDesechos = new ArrayList<>();
		listaDocumentosExportacion = new ArrayList<>();
		
		listaExportacionDesechosEliminar = new ArrayList<>();
		listaDocumentosExportacionEliminar = new ArrayList<>();
		listaExportacionDesechosOriginales = new ArrayList<>();
		listaDocumentosExportacionOriginales = new ArrayList<>();
		listaDocumentosNotificacion = new ArrayList<>();
		listaDocumentosAutorizacion = new ArrayList<>();
		listaDocumentosMovimiento = new ArrayList<>();
		listaDocumentosDestruccion = new ArrayList<>();

		panelAdicionarVisible = false;
		editar = false;
	}
	
	public void cargarDatos() {
		try{ 
			inicializacionObj();
			
			if (generadorDesechosRetce != null && generadorDesechosRetce.getId() != null && generadorDesechosRetce.getRealizaExportacion() != null) {
				listaExportacionDesechos = exportacionDesechosFacade.getDesechosExportacionPorRgdRetce( generadorDesechosRetce.getId());
				
				if (listaExportacionDesechos != null && listaExportacionDesechos.size() > 0) {
					habilitarBtnSiguiente = true;						
					
					List<Integer> listaTipos = new ArrayList<Integer>(); 
			    	listaTipos.add(TipoDocumentoSistema.DOCUMENTO_EXPORTACION_AUTORIZACION.getIdTipoDocumento());
			    	listaTipos.add(TipoDocumentoSistema.DOCUMENTO_EXPORTACION_NOTIFICACION.getIdTipoDocumento());
			    	listaTipos.add(TipoDocumentoSistema.DOCUMENTO_EXPORTACION_MOVIMIENTOS.getIdTipoDocumento());
			    	listaTipos.add(TipoDocumentoSistema.DOCUMENTO_EXPORTACION_DESTRUCCION.getIdTipoDocumento());
			    	
			    	for (DesechoExportacion desechoExportacion : listaExportacionDesechos) {
						List<Documento> documentosExportacion = documentosFacade.recuperarDocumentosPorTipo(
								desechoExportacion.getId(),
								"DesechoExportacionRetce", listaTipos);
						
						List<DocumentoExportacion> listaDocumentosDesecho = new ArrayList<>();
						desechoExportacion.setDocumentosNotificacion(new ArrayList<Documento>());
						desechoExportacion.setDocumentosAutorizacion(new ArrayList<Documento>());
						desechoExportacion.setDocumentosMovimiento(new ArrayList<Documento>());
						desechoExportacion.setDocumentosDestruccion(new ArrayList<Documento>());
						
						for (Documento documento : documentosExportacion) {
							DocumentoExportacion nuevo = new DocumentoExportacion();
							nuevo.setDocumento(documento);
							
							switch (documento.getTipoDocumento().getId()) {
							case 4200:
								desechoExportacion.getDocumentosNotificacion().add(documento);
								
								nuevo.setTipo(TipoDocumentoSistema.DOCUMENTO_EXPORTACION_NOTIFICACION);
								nuevo.setTipoDocumento("Fase 1 - Notificación");
								break;
							case 4201:
								desechoExportacion.getDocumentosAutorizacion().add(documento);
								
								nuevo.setTipo(TipoDocumentoSistema.DOCUMENTO_EXPORTACION_AUTORIZACION);
								nuevo.setTipoDocumento("Fase 2 - Autorización");
								break;
							case 4202:
								desechoExportacion.getDocumentosMovimiento().add(documento);
								
								nuevo.setTipo(TipoDocumentoSistema.DOCUMENTO_EXPORTACION_MOVIMIENTOS);
								nuevo.setTipoDocumento("Fase 3 - Documento de movimiento");
								break;
							case 4203:
								desechoExportacion.getDocumentosDestruccion().add(documento);
								
								nuevo.setTipo(TipoDocumentoSistema.DOCUMENTO_EXPORTACION_DESTRUCCION);
								nuevo.setTipoDocumento("Fase 3 - Acta de destrucción");
								break;

							default:
								break;
							}
							
							listaDocumentosDesecho.add(nuevo);
						}
						
						desechoExportacion.setListaDocumentosExportacion(listaDocumentosDesecho);
					}
					
				} else
					listaExportacionDesechos = new ArrayList<>();
				
		    	
		    	
		    	if(habilitarBtnSiguiente) {
		    		List<FacesMessage> errorMessages = validateDataExportacion();
					if (!errorMessages.isEmpty())
						habilitarBtnSiguiente = false;
		    	}
			}
		} catch (Exception e) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
        }
	}

	public void toggleHandle(ToggleEvent event) {
		if (event.getVisibility().equals(Visibility.HIDDEN)) {
			panelAdicionarVisible = false;
			editar = false;
		} else
			panelAdicionarVisible = true;
	}

	public List<DesechoPeligroso> getDesechosPendientes() {
		List<DesechoPeligroso> result = new ArrayList<>();
		
		List<DesechoPeligroso> desechosRegistrados = JsfUtil.getBean(AutogestionDesechosBean.class).getDesechosNoAutogestion();
		result.addAll(desechosRegistrados);

		if(listaExportacionDesechos != null) {
			for (DesechoExportacion exportacion : listaExportacionDesechos) {
				if (editar && exportacion.getDesechoPeligroso().equals(desechoSeleccionado))
					continue;

				if (exportacion.getDesechoPeligroso() != null)
					result.remove(exportacion.getDesechoPeligroso());
			}
		}

		return result;
	}

	public void editarDesecho(DesechoExportacion desecho) {
		desechoExportacionOriginal = (DesechoExportacion) SerializationUtils.clone(desecho);
		desechoExportacion = desecho;
		desechoSeleccionado = desecho.getDesechoPeligroso();
		
		listaDocumentosNotificacion = desecho.getDocumentosNotificacion();
		listaDocumentosAutorizacion = desecho.getDocumentosAutorizacion();
		listaDocumentosMovimiento = desecho.getDocumentosMovimiento();
		listaDocumentosDestruccion = desecho.getDocumentosDestruccion();

		listaExportacionDesechos.remove(desecho);
		editar = true;
	}

	public void eliminarDesecho(DesechoExportacion desecho) {
		try {
			if (desecho.getId() != null) {
				if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0) {
					DesechoExportacion desechoOriginal = (DesechoExportacion) SerializationUtils.clone(desecho);
					buscarHistorialDesechoExportacion(desechoOriginal, desecho, true);
				}
				
				desecho.setEstado(false);
				listaExportacionDesechosEliminar.add(desecho);
				
				
			}

			listaExportacionDesechos.remove(desecho);
		} catch (Exception e) {

		}
	}

	public void aceptar() {
		desechoExportacion.setDesechoPeligroso(desechoSeleccionado);
		desechoExportacion.setDocumentosNotificacion(listaDocumentosNotificacion);
		desechoExportacion.setDocumentosAutorizacion(listaDocumentosAutorizacion);
		desechoExportacion.setDocumentosMovimiento(listaDocumentosMovimiento);
		desechoExportacion.setDocumentosDestruccion(listaDocumentosDestruccion);
		
		if (!listaExportacionDesechos.contains(desechoExportacion))
			listaExportacionDesechos.add(desechoExportacion);

		verificarCambios();
		
		if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0) {
			if(desechoExportacion.getId() != null) {
				if(!desechoExportacionOriginal.equalsObject(desechoExportacion)) 
					buscarHistorialDesechoExportacion(desechoExportacionOriginal, desechoExportacion, false);
			}else {
				desechoExportacion.setNumeroObservacion(declaracionGeneradorRetceBean.getNumeroObservacion());
			}
		}
				
		desechoSeleccionado = null;
		desechoExportacion = new DesechoExportacion();
		listaDocumentosNotificacion = new ArrayList<>();
		listaDocumentosAutorizacion = new ArrayList<>();
		listaDocumentosMovimiento = new ArrayList<>();
		listaDocumentosDestruccion = new ArrayList<>();
		
		JsfUtil.addCallbackParam("addExportacion");
	}
	
	public void buscarHistorialDesechoExportacion(DesechoExportacion desechoExportacionOriginal, DesechoExportacion desechoExportacion, Boolean eliminacion) {
		if(desechoExportacion.getNumeroObservacion() == null || 
				desechoExportacion.getNumeroObservacion() != declaracionGeneradorRetceBean.getNumeroObservacion()) {
			DesechoExportacion desechoHistorial = exportacionDesechosFacade.getDesechoExportacionHistorial(desechoExportacion.getId(), declaracionGeneradorRetceBean.getNumeroObservacion());
			if(desechoHistorial == null){
				Boolean tieneHistorial = false;
				for (DesechoExportacion desecho : listaExportacionDesechosOriginales) {
					if(desecho.getIdRegistroOriginal().equals(desechoExportacion.getId())) {
						tieneHistorial = true;
						break;
					}
				}
				
				if(!tieneHistorial){
					desechoExportacionOriginal.setId(null);
					desechoExportacionOriginal.setHistorial(true);
					desechoExportacionOriginal.setIdRegistroOriginal(desechoExportacion.getId());
					desechoExportacionOriginal.setNumeroObservacion(declaracionGeneradorRetceBean.getNumeroObservacion());
					listaExportacionDesechosOriginales.add(desechoExportacionOriginal);
				}
				
				if(eliminacion) {
					List<Documento> documentosDesecho = new ArrayList<>();
					documentosDesecho.addAll(desechoExportacion.getDocumentosNotificacion());
					documentosDesecho.addAll(desechoExportacion.getDocumentosAutorizacion());
					documentosDesecho.addAll(desechoExportacion.getDocumentosMovimiento());
					documentosDesecho.addAll(desechoExportacion.getDocumentosDestruccion());
					
					for (Documento documentoExportacion : documentosDesecho) {
						if(documentoExportacion.getId() != null && 
								!listaDocumentosExportacionEliminar.contains(documentoExportacion)) {
							
							if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0 && 
									!declaracionGeneradorRetceBean.getNumeroObservacion().equals(documentoExportacion.getNumeroNotificacion())) 
								listaDocumentosExportacionOriginales.add((Documento) SerializationUtils.clone(documentoExportacion));
							
							documentoExportacion.setEstado(false);
							listaDocumentosExportacionEliminar.add(documentoExportacion);
						}
					}
				}
			}
		}
	}
	
	public void cancelar() {
		if(editar)
			listaExportacionDesechos.add(desechoExportacionOriginal);
		
		desechoSeleccionado = null;
		desechoExportacion = new DesechoExportacion();
		listaDocumentosNotificacion = new ArrayList<>();
		listaDocumentosAutorizacion = new ArrayList<>();
		listaDocumentosMovimiento = new ArrayList<>();
		listaDocumentosDestruccion = new ArrayList<>();
	}
	
	public void validateDesechoExportacion(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		if(listaDocumentosNotificacion == null || listaDocumentosNotificacion.size() == 0)
			errorMessages.add(new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"Debe ingresar documentos en Fase 1 - Notificación", null));
		
		if(listaDocumentosAutorizacion == null || listaDocumentosAutorizacion.size() == 0)
			errorMessages.add(new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"Debe ingresar documentos en Fase 2 - Autorización", null));
		
		if(listaDocumentosMovimiento == null || listaDocumentosMovimiento.size() == 0)
			errorMessages.add(new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"Debe ingresar documentos en Fase 3 - Documento de movimiento", null));
		
		if(listaDocumentosDestruccion == null || listaDocumentosDestruccion.size() == 0)
			errorMessages.add(new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"Debe ingresar documentos en Fase 3 - Acta de destrucción", null));			
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public void uploadFileExportacion(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		nuevoDocumento = new Documento();
		nuevoDocumento.setId(null);
		nuevoDocumento.setContenidoDocumento(contenidoDocumento);
		nuevoDocumento.setNombre(event.getFile().getFileName());
		nuevoDocumento.setMime("application/pdf");
		nuevoDocumento.setExtesion(".pdf");
		
    	String faseDocumento = (String) event.getComponent().getAttributes().get("tipoDocumento");
    	
    	switch (faseDocumento) {
		case "1":
			if(!listaDocumentosNotificacion.contains(nuevoDocumento))
				listaDocumentosNotificacion.add(nuevoDocumento);
			break;
		case "2":
			if(!listaDocumentosAutorizacion.contains(nuevoDocumento))
				listaDocumentosAutorizacion.add(nuevoDocumento);
			break;
		case "3":
			if(!listaDocumentosMovimiento.contains(nuevoDocumento))
				listaDocumentosMovimiento.add(nuevoDocumento);
			break;
		case "4":
			if(!listaDocumentosDestruccion.contains(nuevoDocumento))
				listaDocumentosDestruccion.add(nuevoDocumento);
			break;
		default:
			break;
		}

		nuevoDocumento = new Documento();
	}
	
	public void getInfoDesecho() {
		if (desechoSeleccionado != null) {
			List<IdentificacionDesecho> listaIdentificacionDesechos = JsfUtil.getBean(IdentificacionDesechosBean.class).getListaIdentificacionDesechos();
			
			for (IdentificacionDesecho desecho : listaIdentificacionDesechos) {
				if(desechoSeleccionado.getId().equals(desecho.getDesechoPeligroso().getId())){
					
					desechoExportacion.setCantidad(desecho.getCantidadTotalToneladas());
				}
			}
			
		}
	}
	
	public void eliminarDocumento(String faseDocumento, Documento documentoExp) {
		try {
			if (documentoExp.getId() != null) {
				if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0 && 
						!declaracionGeneradorRetceBean.getNumeroObservacion().equals(documentoExp.getNumeroNotificacion())) 
					listaDocumentosExportacionOriginales.add((Documento) SerializationUtils.clone(documentoExp));
				
				documentoExp.setEstado(false);
				listaDocumentosExportacionEliminar.add(documentoExp);
			}
			
			switch (faseDocumento) {
			case "1":
				listaDocumentosNotificacion.remove(documentoExp);
				break;
			case "2":
				listaDocumentosAutorizacion.remove(documentoExp);
				break;
			case "3":
				listaDocumentosMovimiento.remove(documentoExp);
				break;
			case "4":
				listaDocumentosDestruccion.remove(documentoExp);
				break;
			default:
				break;
			}

		} catch (Exception e) {

		}
	}
	
	public void validateExportacion(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = validateDataExportacion();
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public List<FacesMessage> validateDataExportacion() {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		if (generadorDesechosRetce.getRealizaExportacion() != null && 
				generadorDesechosRetce.getRealizaExportacion()) {
			if(listaExportacionDesechos.size() == 0)
				errorMessages.add(new FacesMessage(
						FacesMessage.SEVERITY_ERROR,
						"Debe ingresar la información de exportación en uno o varios desechos.", null));
		}
		
		return errorMessages;
	}
	
	public Boolean guardarExportacionDesechos(){
		try {
			for (DesechoExportacion desechoExportacion : listaExportacionDesechos) {
				desechoExportacion.setIdGeneradorRetce(generadorDesechosRetce.getId());
				
				exportacionDesechosFacade.guardarExportacionDesecho(desechoExportacion);
				
				List<Documento> guardados = new ArrayList<>();
				for (Documento documentoExportacion : desechoExportacion.getDocumentosNotificacion()) { 
					documentoExportacion = guardarDocumento(desechoExportacion.getId(), documentoExportacion, TipoDocumentoSistema.DOCUMENTO_EXPORTACION_NOTIFICACION);
					guardados.add(documentoExportacion);
				}
				desechoExportacion.setDocumentosNotificacion(guardados);
				
				guardados = new ArrayList<>();
				for (Documento documentoExportacion : desechoExportacion.getDocumentosAutorizacion()) { 
					documentoExportacion = guardarDocumento(desechoExportacion.getId(), documentoExportacion, TipoDocumentoSistema.DOCUMENTO_EXPORTACION_AUTORIZACION);
					guardados.add(documentoExportacion);
				}
				desechoExportacion.setDocumentosAutorizacion(guardados);
				
				guardados = new ArrayList<>();
				for (Documento documentoExportacion : desechoExportacion.getDocumentosMovimiento()) { 
					documentoExportacion = guardarDocumento(desechoExportacion.getId(), documentoExportacion, TipoDocumentoSistema.DOCUMENTO_EXPORTACION_MOVIMIENTOS);
					guardados.add(documentoExportacion);
				}
				desechoExportacion.setDocumentosMovimiento(guardados);
				
				guardados = new ArrayList<>();
				for (Documento documentoExportacion : desechoExportacion.getDocumentosDestruccion()) { 
					documentoExportacion =  guardarDocumento(desechoExportacion.getId(), documentoExportacion, TipoDocumentoSistema.DOCUMENTO_EXPORTACION_DESTRUCCION);
					guardados.add(documentoExportacion);
				}
				desechoExportacion.setDocumentosDestruccion(guardados);
			}
			
			exportacionDesechosFacade.eliminarExportacionDesechos(listaExportacionDesechosEliminar);
			
			for (Documento documentoExportacion : listaDocumentosExportacionEliminar) {
				documentosFacade.eliminarDocumento(documentoExportacion);
			}
			
			generadorDesechosPeligrososFacade.guardarRgdRetce(generadorDesechosRetce);
			
			declaracionGeneradorRetceBean.eliminarDatosAsociados();
			
			if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0) {
				guardarHistorial();
			}
			
			return true;
		} catch (Exception e) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
            return false;
        }
	}
	
	public Documento guardarDocumento (Integer idDesechoExportacion, Documento documentoExportacion, TipoDocumentoSistema tipoDocumento) throws ServiceException, CmisAlfrescoException {
		if(documentoExportacion.getContenidoDocumento()!=null){
			documentoExportacion.setIdTable(idDesechoExportacion);
			documentoExportacion.setNombreTabla("DesechoExportacionRetce");
			documentoExportacion.setDescripcion("Documento exportación");
			documentoExportacion.setEstado(true);
			
			Documento documentoSave = documentosFacade.guardarDocumentoAlfrescoSinProyecto(
							generadorDesechosRetce.getCodigoGenerador(),
							"GENERADOR_DESECHOS",
							idDesechoExportacion.longValue(),
							documentoExportacion, tipoDocumento,
							null);
			
			if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0) {
				documentoSave.setNumeroNotificacion(declaracionGeneradorRetceBean.getNumeroObservacion());
				documentosFacade.actualizarDocumento(documentoSave);
			}
			
			return documentoSave;
		}
		
		return documentoExportacion;
	}
	
	public StreamedContent descargar(Documento documento) throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			
			if (documento != null && documento.getIdAlfresco() != null) {
				documentoContent = documentosFacade.descargar(documento.getIdAlfresco());
			} else if (documento.getContenidoDocumento() != null) {
				documentoContent = documento.getContenidoDocumento();
			}
			
			if (documento != null && documento.getNombre() != null
					&& documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documento.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void actualizarRealizaExportacion() {
		if(!generadorDesechosRetce.getRealizaExportacion()) {
			if(listaExportacionDesechos.size() > 0){
				for (DesechoExportacion desecho : listaExportacionDesechos) {
					if(desecho.getId() != null && !listaExportacionDesechosEliminar.contains(desecho)) {
						if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0) {
							DesechoExportacion desechoOriginal = (DesechoExportacion) SerializationUtils.clone(desecho);
							buscarHistorialDesechoExportacion(desechoOriginal, desecho, true);
						}
						
						desecho.setEstado(false);
						listaExportacionDesechosEliminar.add(desecho);
					}
				}
			}
		} else {
			listaExportacionDesechosEliminar = new ArrayList<>();
			listaDocumentosExportacionEliminar = new ArrayList<>();
			cargarDatos();
		}
	}
	
	public void verificarCambios() {
		if (desechoExportacion != null) {
			declaracionGeneradorRetceBean.validarDatosRelacionados(4, desechoExportacion.getDesechoPeligroso().getId());
		}
	}
	
	public void guardarHistorial() {
		GeneradorDesechosPeligrososRetce generadorDesechosOriginal = generadorDesechosPeligrososFacade.getRgdRetcePorID(generadorDesechosRetce.getId());
		GeneradorDesechosPeligrososRetce generadorDesechosRetceHistorial = generadorDesechosPeligrososFacade.getRgdRetceHistorialPorID(generadorDesechosRetce.getId(), declaracionGeneradorRetceBean.getNumeroObservacion());
		
		if(generadorDesechosRetceHistorial == null 
				&& !generadorDesechosOriginal.getRealizaExportacion().equals(generadorDesechosRetce.getRealizaAutogestion())){
			generadorDesechosOriginal.setId(null);
			generadorDesechosOriginal.setHistorial(true);
			generadorDesechosOriginal.setIdRegistroOriginal(generadorDesechosRetce.getId());
			generadorDesechosOriginal.setNumeroObservacion(declaracionGeneradorRetceBean.getNumeroObservacion());
			
			generadorDesechosPeligrososFacade.guardarRgdRetce(generadorDesechosOriginal);
		}	
		
		
		if(listaExportacionDesechosOriginales.size() > 0)
			exportacionDesechosFacade.guardarExportacionDesecho(listaExportacionDesechosOriginales);
		
		if(listaDocumentosExportacionOriginales.size() > 0) {
			for (Documento documentoExportacion : listaDocumentosExportacionOriginales) {
				documentoExportacion.setIdHistorico(documentoExportacion.getId());
				documentoExportacion.setId(null);
				documentoExportacion.setNumeroNotificacion(declaracionGeneradorRetceBean.getNumeroObservacion());
				
				documentosFacade.actualizarDocumento(documentoExportacion);
			}
		}
		
		listaExportacionDesechosOriginales = new ArrayList<>();
		listaDocumentosExportacionOriginales = new ArrayList<>();
	}
	
	public void verDocumentosDesecho (DesechoExportacion desecho) {
		listaDocumentosExportacion = new ArrayList<>();
		
		for (Documento documento : desecho.getDocumentosNotificacion()) {
			DocumentoExportacion nuevo = new DocumentoExportacion();
			nuevo.setDocumento(documento);
			nuevo.setTipo(TipoDocumentoSistema.DOCUMENTO_EXPORTACION_NOTIFICACION);
			nuevo.setTipoDocumento("Fase 1 - Notificación");
			
			listaDocumentosExportacion.add(nuevo);
		}
		
		for (Documento documento : desecho.getDocumentosAutorizacion()) {
			DocumentoExportacion nuevo = new DocumentoExportacion();
			nuevo.setDocumento(documento);
			nuevo.setTipo(TipoDocumentoSistema.DOCUMENTO_EXPORTACION_AUTORIZACION);
			nuevo.setTipoDocumento("Fase 2 - Autorización");
			
			listaDocumentosExportacion.add(nuevo);
		}
		
		for (Documento documento : desecho.getDocumentosMovimiento()) {
			DocumentoExportacion nuevo = new DocumentoExportacion();
			nuevo.setDocumento(documento);
			nuevo.setTipo(TipoDocumentoSistema.DOCUMENTO_EXPORTACION_MOVIMIENTOS);
			nuevo.setTipoDocumento("Fase 3 - Documento de movimiento");
			
			listaDocumentosExportacion.add(nuevo);
		}
		
		for (Documento documento : desecho.getDocumentosDestruccion()) {
			DocumentoExportacion nuevo = new DocumentoExportacion();
			nuevo.setDocumento(documento);
			nuevo.setTipo(TipoDocumentoSistema.DOCUMENTO_EXPORTACION_DESTRUCCION);
			nuevo.setTipoDocumento("Fase 3 - Acta de destrucción");
			
			listaDocumentosExportacion.add(nuevo);
		}
	}
	
	public void cargarDatosHistorial(Integer numeroObservaciones) {
		try {
			setNumeroObservaciones(numeroObservaciones);
			
			List<GeneradorDesechosPeligrososRetce> historiales = generadorDesechosPeligrososFacade
					.getRgdRetceHistorialPorID(generadorDesechosRetce.getId());
			
			if(historiales != null) {
				listaRealizaExportacionHistorial = new ArrayList<>();
				
				for (GeneradorDesechosPeligrososRetce generadorRetce : historiales) {
					if(!generadorRetce.getRealizaExportacion().equals(generadorDesechosRetce.getRealizaExportacion()))
						listaRealizaExportacionHistorial.add(generadorRetce);
				}
			}
			
			List<Integer> listaTipos = new ArrayList<Integer>(); 
	    	listaTipos.add(TipoDocumentoSistema.DOCUMENTO_EXPORTACION_AUTORIZACION.getIdTipoDocumento());
	    	listaTipos.add(TipoDocumentoSistema.DOCUMENTO_EXPORTACION_NOTIFICACION.getIdTipoDocumento());
	    	listaTipos.add(TipoDocumentoSistema.DOCUMENTO_EXPORTACION_MOVIMIENTOS.getIdTipoDocumento());
	    	listaTipos.add(TipoDocumentoSistema.DOCUMENTO_EXPORTACION_DESTRUCCION.getIdTipoDocumento());
			
			mostrarHistorialDesechos = false;
			if (listaExportacionDesechos != null && listaExportacionDesechos.size() > 0) {
				for (DesechoExportacion desecho : listaExportacionDesechos) {
					if(desecho.getNumeroObservacion() != null && desecho.getNumeroObservacion().equals(numeroObservaciones)) {
						desecho.setNuevoEnModificacion(true);
						mostrarHistorialDesechos = true;
					} else {
						List<DesechoExportacion> historialDesecho =  exportacionDesechosFacade.getHistorialDesechoExportacion(desecho.getId());
						if(historialDesecho != null) {
							desecho.setListaHistorial(historialDesecho);
							mostrarHistorialDesechos = true;
						}
						
						//historial de documentos
						List<Documento> documentosExportacion = documentosFacade.recuperarHistorialDocumentosPorTipo(desecho.getId(),
								"DesechoExportacionRetce", listaTipos);
						
			    		List<DocumentoExportacion> listaDocumentosDesecho = generarListaDocumentosExportacion(documentosExportacion);
			    		
			    		if(listaDocumentosDesecho.size() > 0)
							mostrarHistorialDesechos = true;
						
			    		desecho.setListaDocumentosExportacionHistorial(listaDocumentosDesecho);
					}
				}
			}
			
			List<DesechoExportacion> desechoResultados = exportacionDesechosFacade.getDesechosEliminadosPorRgdRetce(generadorDesechosRetce.getId());
			if(desechoResultados != null) {
				listaExportacionDesechosEliminados = new ArrayList<>();
				for (DesechoExportacion desecho : desechoResultados) {
					Boolean ingresarDesecho = true;
					for (DesechoExportacion desechoEliminar : listaExportacionDesechosEliminados) {
						if(desecho.getIdRegistroOriginal().equals(desechoEliminar.getIdRegistroOriginal())) {
							ingresarDesecho = false;
							break;
						}
					}
					
					if(ingresarDesecho) {
						List<Documento> documentosExportacion = documentosFacade.recuperarHistorialDocumentosPorTipo(desecho.getIdRegistroOriginal(),
								"DesechoExportacionRetce", listaTipos);
						
			    		List<DocumentoExportacion> listaDocumentosDesecho = generarListaDocumentosExportacion(documentosExportacion);
			    		
			    		if(listaDocumentosDesecho.size() > 0)
							mostrarHistorialDesechos = true;
						
			    		desecho.setListaDocumentosExportacion(listaDocumentosDesecho);
			    		
			    		listaExportacionDesechosEliminados.add(desecho);
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<DocumentoExportacion> generarListaDocumentosExportacion(List<Documento> documentosExportacion) {
		List<DocumentoExportacion> listaDocumentosExportacion = new ArrayList<>();
		
		for (Documento documento : documentosExportacion) {
			DocumentoExportacion nuevo = new DocumentoExportacion();
			nuevo.setDocumento(documento);
			
			switch (documento.getTipoDocumento().getId()) {
			case 4200:
				nuevo.setTipo(TipoDocumentoSistema.DOCUMENTO_EXPORTACION_NOTIFICACION);
				nuevo.setTipoDocumento("Fase 1 - Notificación");
				break;
			case 4201:
				nuevo.setTipo(TipoDocumentoSistema.DOCUMENTO_EXPORTACION_AUTORIZACION);
				nuevo.setTipoDocumento("Fase 2 - Autorización");
				break;
			case 4202:
				nuevo.setTipo(TipoDocumentoSistema.DOCUMENTO_EXPORTACION_MOVIMIENTOS);
				nuevo.setTipoDocumento("Fase 3 - Documento de movimiento");
				break;
			case 4203:
				nuevo.setTipo(TipoDocumentoSistema.DOCUMENTO_EXPORTACION_DESTRUCCION);
				nuevo.setTipoDocumento("Fase 3 - Acta de destrucción");
				break;

			default:
				break;
			}
			
			listaDocumentosExportacion.add(nuevo);
		}
		
		return listaDocumentosExportacion;
	}
	
	public void verHistorialDesecho(DesechoExportacion desecho) {
		desechoExportacionSeleccionado = desecho;
	}

}
