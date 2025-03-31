package ec.gob.ambiente.control.retce.beans;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.SerializationUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.Visibility;

import ec.gob.ambiente.control.retce.controllers.GeneradorDesechosPeligrososVerController;
import ec.gob.ambiente.retce.model.DetalleCatalogoGeneral;
import ec.gob.ambiente.retce.model.DetalleManifiestoDesecho;
import ec.gob.ambiente.retce.model.GeneradorDesechosPeligrososRetce;
import ec.gob.ambiente.retce.model.IdentificacionDesecho;
import ec.gob.ambiente.retce.model.Manifiesto;
import ec.gob.ambiente.retce.model.ResumenManifiesto;
import ec.gob.ambiente.retce.model.TransporteEmpresasGestoras;
import ec.gob.ambiente.retce.model.TransporteGestorAmbiental;
import ec.gob.ambiente.retce.model.TransporteMediosPropios;
import ec.gob.ambiente.retce.services.DetalleCatalogoGeneralFacade;
import ec.gob.ambiente.retce.services.IdentificacionDesechosFacade;
import ec.gob.ambiente.retce.services.ManifiestoFacade;
import ec.gob.ambiente.retce.services.TransporteFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.FaseGestionDesecho;
import ec.gob.ambiente.suia.domain.SedePrestadorServiciosDesechos;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class TransporteDesechosBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value = "#{declaracionGeneradorRetceBean}")
	@Getter
	@Setter
	private DeclaracionGeneradorRetceBean declaracionGeneradorRetceBean;
	
	@EJB
	private DetalleCatalogoGeneralFacade detalleCatalogoGeneralFacade;
	
	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;
	
	@EJB
	private DocumentosFacade documentosFacade;
	
	@EJB
	private TransporteFacade transporteFacade;
	
	@EJB
	private ManifiestoFacade manifiestoFacade;
	
	@EJB
	private IdentificacionDesechosFacade identificacionDesechosFacade;

	@Getter
	@Setter
	private List<DetalleCatalogoGeneral> listaTipoUnidad, listaTipoResolucion;
	
	@Getter
	@Setter
	private List<Manifiesto> listaManifiestos, listaManifiestosGestores, listaManifiestosOriginales;
	
	@Getter
	@Setter
	private List<Manifiesto> listaManifiestosEliminar, listaManifiestosGestoresEliminar, historialManifiestosMPEliminados, historialManifiestosGAEliminados, historialManifiestosEliminados;
	
	@Getter
	@Setter
	private List<DetalleManifiestoDesecho> listaDetallesManifiestosDesechos, listaDetallesManifiestosDesechosEliminar, listaParcialDetallesManifiestosEliminar, listaDetallesManifiestoOriginales;
	
	@Getter
	@Setter
	private List<TransporteEmpresasGestoras> listaEmpresasGestoras, listaEmpresasGestorasEliminar, listaEmpresasGestorasOriginales, historialEmpresasEliminadas;
	
	@Getter
	@Setter
	private List<SedePrestadorServiciosDesechos> listaPrestadoresServicio;
	
	@Getter
	@Setter
	private List<ResumenManifiesto> resumenManifiestoMedioPropio, resumenManifiestoGestores;
	
	@Getter
	@Setter
	private List<Documento> listaDocsOriginalesMedioPropio, listaDocsOriginalesGestores, documentosAutorizacion, listaDocumentosAutorizacionHistorial;
	
	@Getter
	@Setter
	private List<TransporteMediosPropios> listaMediosPropiosHistorialEliminado;
	
	@Getter
	@Setter
	private List<TransporteGestorAmbiental> listaGestorHistorialEliminado;
	
	@Getter
	@Setter
	private List<Documento>  listaDocsManifiestosMediosPropios, listaDocsManifiestosGestor, listaDocsManifiestosEliminar;
	
	@Getter
	@Setter
	private SedePrestadorServiciosDesechos prestadorServicioSeleccionado;
	
	@Getter
	@Setter
	private GeneradorDesechosPeligrososRetce generadorDesechosRetce;
	
	@Getter
	@Setter
	private DesechoPeligroso desechoSeleccionado;
	
	@Getter
	@Setter
	private Manifiesto detalleManifiesto, manifiestoSeleccionado;
	
	@Getter
	@Setter
	private DetalleManifiestoDesecho detalleManifiestoDesecho;
	
	@Getter
	@Setter
	private TransporteMediosPropios transporteMediosPropios, transporteMediosPropiosHistorial, transporteMediosPropiosEliminar, transporteMediosPropiosOriginal;
	
	@Getter
	@Setter
	private TransporteGestorAmbiental transporteGestorAmbiental, transporteGestorAmbientalEliminar, transporteGestorAmbientalHistorial;
	
	@Getter
	@Setter
	private TransporteEmpresasGestoras transporteEmpresaGestora;
	
	@Getter
	@Setter
	private Documento documentoAutorizacion, documentoAutorizacionOriginal, documentoManifiestoUnico, documentoManifiestoUnicoOriginal;
	
	@Getter
	@Setter
	private DetalleCatalogoGeneral tipoActividad, tipoResolucion;
	
	@Getter
	@Setter
	private Documento nuevoDocumento;
	
	@Getter
	@Setter
	private boolean editar, editarManifiesto, editarManifiestoDesecho, editarEmpresaGestora, editarManifiestoGestor;
	
	@Getter
	private boolean panelAdicionarVisible, panelEmpresaVisible, panelManifiestoVisible, panelManifiestoGestorVisible, panelMedioPropioVisible, panelGestorVisible, viewGestorAmbiental, viewMedioPropio;
	
	@Getter
	@Setter
	private boolean habilitarBtnSiguiente;
	
	@Getter
//	protected String lblDesecho, lblUnidad, lblCantidadManifiesto, lblNroManifiesto, lblFechaEmbarque, lblManifiestoUnico, lblCantidadManifiestoUnidades, pnlAdicionarManifiesto, pnlDatosManifiesto, tblDesechosManifiesto, frmManifiesto;
	
	protected List<Integer> idFasesGestion;
	
	private Double factorKgT, cantidadSuma;
	
	@Getter
	@Setter
	private String resolucionLicencia;
	
	@Getter
	@Setter
	private Boolean existeReporteUnidades, mostrarHistorialManifiestoMP, mostrarHistorialManifiestoGA, mostrarHistorialEmpresaGestora, habilitarUnidad= true;
	
	@PostConstruct
	private void initDatos() {
		inicializacionObj();
		
		listaTipoUnidad = JsfUtil.getBean(IdentificacionDesechosBean.class).getListaTipoUnidad();
		listaTipoResolucion = detalleCatalogoGeneralFacade.findByCatalogoGeneralString("generador.tipo_resolucion");
		
		idFasesGestion = new ArrayList<Integer>();
		idFasesGestion.add(FaseGestionDesecho.FASE_TRANSPORTE);
		
		tipoActividad = detalleCatalogoGeneralFacade.findByCodigo("tipoactividaddesecho.transporte");
		
		generadorDesechosRetce = declaracionGeneradorRetceBean.getGeneradorDesechosRetce();
		
		factorKgT = declaracionGeneradorRetceBean.getFactorKgT();
	}
	
	public void inicializacionObj() {
		desechoSeleccionado = null;
		detalleManifiestoDesecho = new DetalleManifiestoDesecho();
		transporteMediosPropios = new TransporteMediosPropios();
		documentoAutorizacion = new Documento();
		documentoManifiestoUnico = new Documento();
		
		listaManifiestos = new ArrayList<>();
		listaManifiestosGestores = new ArrayList<>();
		listaDetallesManifiestosDesechos = new ArrayList<>();
		listaEmpresasGestoras = new ArrayList<>();
		resumenManifiestoMedioPropio = new ArrayList<>();
		resumenManifiestoGestores = new ArrayList<>();
		
		listaManifiestosEliminar = new ArrayList<>();
		listaManifiestosGestoresEliminar = new ArrayList<>();
		listaDetallesManifiestosDesechosEliminar = new ArrayList<>();
		listaEmpresasGestorasEliminar = new ArrayList<>();
		listaManifiestosOriginales = new ArrayList<>();
		listaDetallesManifiestoOriginales = new ArrayList<>();
		listaEmpresasGestorasOriginales = new ArrayList<>();
		listaDocsOriginalesMedioPropio = new ArrayList<>();
		listaDocsOriginalesGestores = new ArrayList<>();
		listaDocsManifiestosMediosPropios = new ArrayList<>();
		listaDocsManifiestosGestor = new ArrayList<>();
		listaDocsManifiestosEliminar = new ArrayList<>();
		
		panelAdicionarVisible = false;
		panelEmpresaVisible = false;
		panelManifiestoVisible = false;
		panelMedioPropioVisible = false;
		panelGestorVisible = false;
		viewGestorAmbiental = true;
		viewMedioPropio = true;
		habilitarBtnSiguiente = false;
		
		editar = false;
		editarManifiesto = false;
		editarManifiestoGestor = false;
		editarManifiestoDesecho = false;
		editarEmpresaGestora = false;
	}
	
	public void cargarDatos() {
		inicializacionObj();
		
		listaPrestadoresServicio = registroGeneradorDesechosFacade.getTodasSedesPrestadorServicios();
		
		if (generadorDesechosRetce != null && generadorDesechosRetce.getId() != null) {
			cargarDatosMediosPropios();
			cargarDatosGestor();
			
			if(habilitarBtnSiguiente) {
				List<FacesMessage> errorMessages = validateDataTransporte();
				if (!errorMessages.isEmpty())
					habilitarBtnSiguiente = false;
			}
		}
	}
	
	public void cargarDatosMediosPropios() {
		transporteMediosPropios = transporteFacade.getTransporteMediosPropiosPorRgdRetce(generadorDesechosRetce.getId());
		if (transporteMediosPropios != null) {
			habilitarBtnSiguiente = true;
			if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0)
				transporteMediosPropiosOriginal = (TransporteMediosPropios) SerializationUtils.clone(transporteMediosPropios);
			
			viewMedioPropio = false;
			panelMedioPropioVisible = true;

			documentosAutorizacion = documentosFacade.documentoXTablaIdXIdDoc(
							generadorDesechosRetce.getId(),
							GeneradorDesechosPeligrososRetce.class.getSimpleName(),
							TipoDocumentoSistema.DOCUMENTO_AUTORIZACION_TRANSPORTE_MEDIO_PROPIO);
			if (documentosAutorizacion.size() > 0) {
				documentoAutorizacion = documentosAutorizacion.get(0);
				documentoAutorizacionOriginal = (Documento) SerializationUtils.clone(documentoAutorizacion);
			}

			listaManifiestos = manifiestoFacade.getManifiestosPorMedioPropio(transporteMediosPropios.getId());
			// ver detalle
			if (listaManifiestos != null && listaManifiestos.size() > 0) {
				List<Documento> documentoManifiesto = documentosFacade
						.documentoXTablaIdXIdDoc(transporteMediosPropios.getId(),
								Manifiesto.class.getSimpleName(),
								TipoDocumentoSistema.DOCUMENTO_MANIFIESTO_UNICO);
				if (documentoManifiesto.size() > 0)
					listaDocsManifiestosMediosPropios = documentoManifiesto;
				
				resumenManifiestoMedioPropio = calcularSumatoriaTotal(listaManifiestos);
			} else
				listaManifiestos = new ArrayList<>();
		} else
			transporteMediosPropios = new TransporteMediosPropios();
	}
	
	public void cargarDatosGestor() {
		listaEmpresasGestoras = transporteFacade.getEmpresasGestorasPorRgdRetce(generadorDesechosRetce.getId(),tipoActividad.getId());
		if (listaEmpresasGestoras != null && listaEmpresasGestoras.size() > 0) {
			viewGestorAmbiental = false;
			panelGestorVisible = true;
			habilitarBtnSiguiente = true;
		} else
			listaEmpresasGestoras = new ArrayList<>();

		transporteGestorAmbiental = transporteFacade.getTransporteGestorAmbientalPorRgdRetce(generadorDesechosRetce.getId());
		if (transporteGestorAmbiental != null) {
			listaManifiestosGestores = manifiestoFacade.getManifiestosPorGestorAmbiental(transporteGestorAmbiental.getId());

			if (listaManifiestosGestores == null)
				listaManifiestosGestores = new ArrayList<>();
			else {
				List<Documento> documentoManifiesto = documentosFacade
						.documentoXTablaIdXIdDoc(transporteGestorAmbiental.getId(),
								Manifiesto.class.getSimpleName(),
								TipoDocumentoSistema.DOCUMENTO_MANIFIESTO_UNICO);
				if (documentoManifiesto.size() > 0)
					listaDocsManifiestosGestor = documentoManifiesto;
				
				resumenManifiestoGestores = calcularSumatoriaTotal(listaManifiestosGestores);
			}

		} else 
			transporteGestorAmbiental = new TransporteGestorAmbiental();
	}
	
	public void nuevoMedioPropio() {
		panelMedioPropioVisible = true;
		cargarDatosMediosPropios();
	}
	
	public void nuevoGestorAmbiental() {
		panelGestorVisible = true;
		cargarDatosGestor();
	}
	
	public void toggleHandleEmpresaGestora(ToggleEvent event) {
		if (event.getVisibility().equals(Visibility.HIDDEN)) {
			panelEmpresaVisible = false;
			editarEmpresaGestora = false;
		} else
			panelEmpresaVisible = true;
		
		if(!editarEmpresaGestora){
			transporteEmpresaGestora = new TransporteEmpresasGestoras();
		}
		
	}
	
	public void toggleHandleManifiesto(ToggleEvent event) {
		if (event.getVisibility().equals(Visibility.HIDDEN)) {
			panelManifiestoVisible = false;
			editarManifiesto = false;
		} else
			panelManifiestoVisible = true;
		
		if(!editarManifiesto){
			documentoManifiestoUnico = new Documento();
			detalleManifiesto = new Manifiesto();
			listaDetallesManifiestosDesechos = new ArrayList<>();
		}
		
		listaParcialDetallesManifiestosEliminar = new ArrayList<>();
	}
	
	public void toggleHandleManifiestoGestor(ToggleEvent event) {
		if (event.getVisibility().equals(Visibility.HIDDEN)) {
			panelManifiestoGestorVisible = false;
			editarManifiestoGestor = false;
		} else
			panelManifiestoGestorVisible = true;
		
		if(!editarManifiestoGestor){
			detalleManifiesto = new Manifiesto();
			listaDetallesManifiestosDesechos = new ArrayList<>();
		}
		
		listaParcialDetallesManifiestosEliminar = new ArrayList<>();
	}
	
	public List<DesechoPeligroso> getDesechosPendientes(){
		List<DesechoPeligroso> result = new ArrayList<>();		
		
		List<DesechoPeligroso> desechosRegistrados = JsfUtil.getBean(AutogestionDesechosBean.class).getDesechosPeligrososSinAutogestion();
		result.addAll(desechosRegistrados);
		
		if(listaDetallesManifiestosDesechos != null) {
			for (DetalleManifiestoDesecho desecho : listaDetallesManifiestosDesechos) {
				if (editarManifiestoDesecho && desecho.getDesechoPeligroso().equals(desechoSeleccionado))
					continue;

				if (desecho.getDesechoPeligroso() != null)
					result.remove(desecho.getDesechoPeligroso());
			}
		}
		
		return result;
	}
	
	public void ponerUnidad(){
		habilitarUnidad= true;
		cantidadSuma = 0.0;
		IdentificacionDesecho objIdentificacionDesecho = identificacionDesechosFacade.getIdentificacionDesechosPorRgdRetcePorDesecho(generadorDesechosRetce.getId(), desechoSeleccionado.getId());
		if(objIdentificacionDesecho != null && objIdentificacionDesecho.getTipoUnidad() != null){
			detalleManifiestoDesecho.setTipoUnidad(objIdentificacionDesecho.getTipoUnidad());
			habilitarUnidad= false;
			cantidadSuma = objIdentificacionDesecho.getCantidadTotalToneladas();
		}
	}

	public List<SedePrestadorServiciosDesechos> getPrestadoresDisponibles(){
		List<SedePrestadorServiciosDesechos> result = new ArrayList<>();		
		
		result.addAll(listaPrestadoresServicio);
		
		if(listaEmpresasGestoras != null) {
			for (TransporteEmpresasGestoras transporte : listaEmpresasGestoras) {
				if (transporte.getEmpresaGestora() != null && transporte.getOtraEmpresa() == null)
					result.remove(transporte.getEmpresaGestora());
			}
		}
		
		return result;
	}
	
	public void validateIngresoManifiesto(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();		
		if (listaDetallesManifiestosDesechos.size() == 0) {
			errorMessages.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"Debe ingresar los desechos asociados al manifiesto.", null));
		}
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public void validateIngresoManifiestoGestor(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		if (listaDetallesManifiestosDesechos.size() == 0) {
			errorMessages.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"Debe ingresar los desechos asociados al manifiesto.", null));
		}		
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public void aceptarManifiesto() {
		detalleManifiesto.setListaManifiestoDesechos(listaDetallesManifiestosDesechos);
		detalleManifiesto.setManifiestoUnico(documentoManifiestoUnico);
		
		listaManifiestos.add(detalleManifiesto);
		
		resumenManifiestoMedioPropio = calcularSumatoriaTotal(listaManifiestos);
		
		listaDetallesManifiestosDesechosEliminar.addAll(listaParcialDetallesManifiestosEliminar);
		
		if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0) {
			if(detalleManifiesto.getId() != null) {
				if(!detalleManifiestoOriginal.equalsObject(detalleManifiesto)) 
					buscarHistorialManifiesto(detalleManifiestoOriginal, detalleManifiesto, false);
				validarHistorialDesechosManifiesto(detalleManifiestoOriginal, detalleManifiesto);
				
				if(listaParcialDetallesManifiestosEliminar.size() > 0){
					for (DetalleManifiestoDesecho detalle : listaParcialDetallesManifiestosEliminar) {
						DetalleManifiestoDesecho detalleOriginal = (DetalleManifiestoDesecho) SerializationUtils.clone(detalle);
						buscarHistorialDetalleManifiesto(detalleOriginal, detalle);
					}
				}
			}else {
				detalleManifiesto.setNumeroObservacion(declaracionGeneradorRetceBean.getNumeroObservacion());
			}
			
			//ya no va documento por cada manifiesto
			//if(documentoManifiestoUnicoOriginal != null && documentoManifiestoUnico.getContenidoDocumento()!=null)
			//	listaDocsOriginalesMedioPropio.add(documentoManifiestoUnicoOriginal);
		}
		
		documentoManifiestoUnicoOriginal = null;
		documentoManifiestoUnico = new Documento();
		detalleManifiesto = new Manifiesto();
		detalleManifiestoDesecho = new DetalleManifiestoDesecho();
		JsfUtil.addCallbackParam("addManifiesto");
	}
	
	public void verDesechosManifiesto(Manifiesto manifiesto){
		listaDetallesManifiestosDesechos = manifiesto.getListaManifiestoDesechos();
		existeReporteUnidades = false;
		for (DetalleManifiestoDesecho desechoManifiesto : listaDetallesManifiestosDesechos) {
			if(desechoManifiesto.getDesechoPeligroso().isDesechoES_04() || 
					desechoManifiesto.getDesechoPeligroso().isDesechoES_06()) {
				existeReporteUnidades = true;
				break;
			}
		}
	}
	
	private Manifiesto detalleManifiestoOriginal;
	public void editarManifiesto(Manifiesto manifiesto) {
		detalleManifiestoOriginal = (Manifiesto) SerializationUtils.clone(manifiesto);
		detalleManifiesto = manifiesto;
		documentoManifiestoUnico = manifiesto.getManifiestoUnico();
		documentoManifiestoUnicoOriginal = (Documento) SerializationUtils.clone(manifiesto.getManifiestoUnico());
		listaDetallesManifiestosDesechos = manifiesto.getListaManifiestoDesechos();
		
		listaManifiestos.remove(manifiesto);
		
		editarManifiesto = true;
	}
	
	public void eliminarManifiesto(Manifiesto manifiesto) {
		try {
			if (manifiesto.getId() != null) {
				
				if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0) {
					Manifiesto manifiestoOriginal = (Manifiesto) SerializationUtils.clone(manifiesto);
					buscarHistorialManifiesto(manifiestoOriginal, manifiesto, true);
				}
				
				manifiesto.setEstado(false);
				listaManifiestosEliminar.add(manifiesto);
			}
			listaManifiestos.remove(manifiesto);
			
			resumenManifiestoMedioPropio = calcularSumatoriaTotal(listaManifiestos);
		} catch (Exception e) {

		}
	}
	
	public void buscarHistorialManifiesto(Manifiesto manifiestoOriginal, Manifiesto manifiesto, Boolean eliminacion) {
		if(manifiesto.getNumeroObservacion() == null || 
				manifiesto.getNumeroObservacion() != declaracionGeneradorRetceBean.getNumeroObservacion()) {
			Manifiesto itemHistorial = manifiestoFacade.getManifiestoHistorial(manifiesto.getId(), declaracionGeneradorRetceBean.getNumeroObservacion());
			if(itemHistorial == null){
				Boolean tieneHistorial = false;
				for (Manifiesto manifiestoOrg : listaManifiestosOriginales) {
					if(manifiestoOrg.getIdRegistroOriginal().equals(manifiesto.getId())) {
						tieneHistorial = true;
						break;
					}
				}
				
				if(!tieneHistorial){
					manifiestoOriginal.setId(null);
					manifiestoOriginal.setHistorial(true);
					manifiestoOriginal.setIdRegistroOriginal(manifiesto.getId());
					manifiestoOriginal.setNumeroObservacion(declaracionGeneradorRetceBean.getNumeroObservacion());
					listaManifiestosOriginales.add(manifiestoOriginal);
				}
				
				if(eliminacion) {
					for (DetalleManifiestoDesecho detalle : manifiesto.getListaManifiestoDesechos()) {
						DetalleManifiestoDesecho desechoOriginal = (DetalleManifiestoDesecho) SerializationUtils.clone(detalle);
						buscarHistorialDetalleManifiesto(desechoOriginal, detalle);
					}
				}
			}
		}
	}
	
	public void validarHistorialDesechosManifiesto(Manifiesto manifiestoOriginal, Manifiesto manifiesto) {
		for (DetalleManifiestoDesecho detalle : manifiesto.getListaManifiestoDesechos()) {
			if(detalle.getId() != null){
				for (DetalleManifiestoDesecho detalleOriginal : manifiestoOriginal.getListaManifiestoDesechos()) {
					if(detalleOriginal.getId().equals(detalle.getId())) {
						if(!detalleOriginal.equalsObject(detalle)) 
							buscarHistorialDetalleManifiesto(detalleOriginal, detalle);
						break;
					}
				}
			} else
				detalle.setNumeroObservacion(declaracionGeneradorRetceBean.getNumeroObservacion());
		}
		
	}
	
	public void buscarHistorialDetalleManifiesto(DetalleManifiestoDesecho manifiestoOriginal, DetalleManifiestoDesecho manifiesto) {
		if(manifiesto.getNumeroObservacion() == null || 
				manifiesto.getNumeroObservacion() != declaracionGeneradorRetceBean.getNumeroObservacion()) {
			DetalleManifiestoDesecho itemHistorial = manifiestoFacade.getDetalleManifiestoHistorial(manifiesto.getId(), declaracionGeneradorRetceBean.getNumeroObservacion());
			if(itemHistorial == null){
				Boolean tieneHistorial = false;
				for (DetalleManifiestoDesecho manifiestoOrg : listaDetallesManifiestoOriginales) {
					if(manifiestoOrg.getIdRegistroOriginal().equals(manifiesto.getId())) {
						tieneHistorial = true;
						break;
					}
				}
				
				if(!tieneHistorial){
					manifiestoOriginal.setId(null);
					manifiestoOriginal.setEstado(true);
					manifiestoOriginal.setHistorial(true);
					manifiestoOriginal.setIdRegistroOriginal(manifiesto.getId());
					manifiestoOriginal.setNumeroObservacion(declaracionGeneradorRetceBean.getNumeroObservacion());
					listaDetallesManifiestoOriginales.add(manifiestoOriginal);
				}
			}
		}
	}
	
	public void eliminarManifiestoGestor(Manifiesto manifiesto) {
		try {
			if (manifiesto.getId() != null) {
				if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0) {
					Manifiesto manifiestoOriginal = (Manifiesto) SerializationUtils.clone(manifiesto);
					buscarHistorialManifiesto(manifiestoOriginal, manifiesto, true);
				}
				
				manifiesto.setEstado(false);
				listaManifiestosEliminar.add(manifiesto);
			}
			listaManifiestosGestores.remove(manifiesto);
			
			resumenManifiestoGestores = calcularSumatoriaTotal(listaManifiestosGestores);
		} catch (Exception e) {

		}
	}
	
	public void cancelarManifiesto() {
		if(editarManifiesto)
			listaManifiestos.add(detalleManifiestoOriginal);
		
		documentoManifiestoUnicoOriginal = null;
		documentoManifiestoUnico = new Documento();
		detalleManifiesto = new Manifiesto();
		detalleManifiestoDesecho = new DetalleManifiestoDesecho();
		listaParcialDetallesManifiestosEliminar = new ArrayList<>();
	}
	
	private Manifiesto detalleManifiestoGestorOriginal;
	public void editarManifiestoGestor(Manifiesto manifiesto) {
		detalleManifiestoGestorOriginal = (Manifiesto) SerializationUtils.clone(manifiesto);
		detalleManifiesto = manifiesto;
		documentoManifiestoUnico = manifiesto.getManifiestoUnico();
		documentoManifiestoUnicoOriginal = (Documento) SerializationUtils.clone(manifiesto.getManifiestoUnico());
		listaDetallesManifiestosDesechos = manifiesto.getListaManifiestoDesechos();
		
		listaManifiestosGestores.remove(manifiesto);
		
		editarManifiestoGestor = true;
	}
	
	public void aceptarManifiestoGestor() {
		detalleManifiesto.setListaManifiestoDesechos(listaDetallesManifiestosDesechos);
		detalleManifiesto.setManifiestoUnico(documentoManifiestoUnico);
		
		listaManifiestosGestores.add(detalleManifiesto);
		
		resumenManifiestoGestores = calcularSumatoriaTotal(listaManifiestosGestores);
		
		listaDetallesManifiestosDesechosEliminar.addAll(listaParcialDetallesManifiestosEliminar);
		
		if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0) {
			if(detalleManifiesto.getId() != null) {
				if(!detalleManifiestoGestorOriginal.equalsObject(detalleManifiesto)) 
					buscarHistorialManifiesto(detalleManifiestoGestorOriginal, detalleManifiesto, false);
				validarHistorialDesechosManifiesto(detalleManifiestoGestorOriginal, detalleManifiesto);
				
				if(listaParcialDetallesManifiestosEliminar.size() > 0){
					for (DetalleManifiestoDesecho detalle : listaParcialDetallesManifiestosEliminar) {
						DetalleManifiestoDesecho detalleOriginal = (DetalleManifiestoDesecho) SerializationUtils.clone(detalle);
						buscarHistorialDetalleManifiesto(detalleOriginal, detalle);
					}
				}
			}else {
				detalleManifiesto.setNumeroObservacion(declaracionGeneradorRetceBean.getNumeroObservacion());
			}
			
			//ya no va documento por cada manifiesto
			//if(documentoManifiestoUnicoOriginal != null && documentoManifiestoUnico.getContenidoDocumento()!=null)
			//	listaDocsOriginalesGestores.add(documentoManifiestoUnicoOriginal);
		}
		
		documentoManifiestoUnico = new Documento();
		detalleManifiesto = new Manifiesto();
		detalleManifiestoDesecho = new DetalleManifiestoDesecho();
		
		JsfUtil.addCallbackParam("addManifiestoGestor");
	}
	
	public void cancelarManifiestoGestor() {
		if(editarManifiestoGestor)
			listaManifiestosGestores.add(detalleManifiestoGestorOriginal);
		
		documentoManifiestoUnicoOriginal = null;
		documentoManifiestoUnico = new Documento();
		detalleManifiesto = new Manifiesto();
		detalleManifiestoDesecho = new DetalleManifiestoDesecho();
		listaParcialDetallesManifiestosEliminar = new ArrayList<>();
	}
	
	//desechos asociados a manifiestos
	public void aceptar() {
		cancelar();
		JsfUtil.addCallbackParam("addDatosGenerales");
	}
	
	public void cancelar() {
		desechoSeleccionado = null;
		detalleManifiestoDesecho = new DetalleManifiestoDesecho();
	}
	
	public void nuevoDesechoManifiesto() {
		detalleManifiestoDesecho = new DetalleManifiestoDesecho();
		desechoSeleccionado = new DesechoPeligroso();
		editarManifiestoDesecho = false;
	}
	
	public void agregarDesechoManifiesto() {
		if (desechoSeleccionado == null || desechoSeleccionado.getId() == null) {
			JsfUtil.addMessageError("Debe seleccionar el desecho generado. ");
			return;
		}
		detalleManifiestoDesecho.setDesechoPeligroso(desechoSeleccionado);

		if (!detalleManifiestoDesecho.getTipoUnidad().getId().equals(declaracionGeneradorRetceBean.getTipoUnidadTonelada().getId()))
			detalleManifiestoDesecho.setCantidadToneladas(detalleManifiestoDesecho.getCantidad() * factorKgT);
		else
			detalleManifiestoDesecho.setCantidadToneladas(detalleManifiestoDesecho.getCantidad());
		
		if(!desechoSeleccionado.isDesechoES_04() && !desechoSeleccionado.isDesechoES_06()) {
			detalleManifiestoDesecho.setCantidadEnUnidades(null);
		}

		if (!listaDetallesManifiestosDesechos
				.contains(detalleManifiestoDesecho))
			listaDetallesManifiestosDesechos.add(detalleManifiestoDesecho);

		desechoSeleccionado = new DesechoPeligroso();
		Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String widget = params.get("widgetVar");
		if(widget != null && !widget.isEmpty()){
			RequestContext.getCurrentInstance().execute("PF('"+widget+"').hide();");	
		}
	}

	public void editarDesechoManifiesto(DetalleManifiestoDesecho desecho) {
		detalleManifiestoDesecho = desecho;
		desechoSeleccionado = desecho.getDesechoPeligroso();
		ponerUnidad();
		editarManifiestoDesecho = true;
	}

	public void eliminarDesechoManifiesto(DetalleManifiestoDesecho desecho) {
		try {
			if (desecho.getId() != null) {
				desecho.setEstado(false);
				listaParcialDetallesManifiestosEliminar.add(desecho);
			}
			listaDetallesManifiestosDesechos.remove(desecho);
		} catch (Exception e) {

		}
	}
	
	//empresas gestoras
	public void aceptarEmpresaGestora() {
		transporteEmpresaGestora.setEmpresaGestora(prestadorServicioSeleccionado);
		if(!listaEmpresasGestoras.contains(transporteEmpresaGestora))
			listaEmpresasGestoras.add(transporteEmpresaGestora);
		
		if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0) {
			if(transporteEmpresaGestora.getId() == null) {
				transporteEmpresaGestora.setNumeroObservacion(declaracionGeneradorRetceBean.getNumeroObservacion());
			}
		}
		
		cancelarEmpresaGestora();
		JsfUtil.addCallbackParam("addEmpresasGestoras");
	}
	
	public void cancelarEmpresaGestora() {
		prestadorServicioSeleccionado = null;
		transporteEmpresaGestora = new TransporteEmpresasGestoras();
	}
	
	public void eliminarEmpresaGestora(TransporteEmpresasGestoras empresa) {
		try {
			if (empresa.getId() != null) {
				if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0 && 
						!declaracionGeneradorRetceBean.getNumeroObservacion().equals(empresa.getNumeroObservacion())) 
					listaEmpresasGestorasOriginales.add((TransporteEmpresasGestoras) SerializationUtils.clone(empresa));
				
				empresa.setEstado(false);
				listaEmpresasGestorasEliminar.add(empresa);
			}
			listaEmpresasGestoras.remove(empresa);
		} catch (Exception e) {

		}
	}

	public void cancelarMedioPropio() {
		panelMedioPropioVisible = false;
		
		if(transporteMediosPropios.getId() != null && declaracionGeneradorRetceBean.getNumeroObservacion() > 0) {
			
			TransporteMediosPropios itemHistorial = transporteFacade.getTransporteMedioPropioHistorial(transporteMediosPropios.getId(), declaracionGeneradorRetceBean.getNumeroObservacion());
			if(itemHistorial == null){
				transporteMediosPropiosHistorial = (TransporteMediosPropios) SerializationUtils.clone(transporteMediosPropios);
				transporteMediosPropiosHistorial.setId(null);
				transporteMediosPropiosHistorial.setHistorial(true);
				transporteMediosPropiosHistorial.setIdRegistroOriginal(transporteMediosPropios.getId());
				transporteMediosPropiosHistorial.setNumeroObservacion(declaracionGeneradorRetceBean.getNumeroObservacion());
			}
			
			transporteMediosPropiosEliminar = transporteMediosPropios;
			transporteMediosPropiosEliminar.setEstado(false);
			
			listaManifiestosEliminar.addAll(listaManifiestos);
			
			for (Manifiesto manifiesto : listaManifiestos) {
				Manifiesto manifiestoOriginal = (Manifiesto) SerializationUtils.clone(manifiesto);
				buscarHistorialManifiesto(manifiestoOriginal, manifiesto, true);
			}
			
			if(listaDocsManifiestosMediosPropios != null && listaDocsManifiestosMediosPropios.size() > 0) {
				for (Documento documento : listaDocsManifiestosMediosPropios) {
					listaDocsManifiestosEliminar.add(documento);
				}
			}
		}
		
		transporteMediosPropios = new TransporteMediosPropios();
		listaManifiestos = new ArrayList<>();
		resumenManifiestoMedioPropio = new ArrayList<>();
		documentoAutorizacion = new Documento();
		tipoResolucion = new DetalleCatalogoGeneral();
	}
	
	public void cancelarGestores() {
		panelGestorVisible = false;
		
		if(transporteGestorAmbiental.getId() != null && declaracionGeneradorRetceBean.getNumeroObservacion() > 0) {
			
			TransporteGestorAmbiental itemHistorial = transporteFacade.getTransporteGestorAmbientalHistorial(transporteGestorAmbiental.getId(), declaracionGeneradorRetceBean.getNumeroObservacion());
			if(itemHistorial == null){
				transporteGestorAmbientalHistorial = (TransporteGestorAmbiental) SerializationUtils.clone(transporteGestorAmbiental);
				transporteGestorAmbientalHistorial.setId(null);
				transporteGestorAmbientalHistorial.setHistorial(true);
				transporteGestorAmbientalHistorial.setIdRegistroOriginal(transporteMediosPropios.getId());
				transporteGestorAmbientalHistorial.setNumeroObservacion(declaracionGeneradorRetceBean.getNumeroObservacion());
			}
			
			transporteGestorAmbientalEliminar = transporteGestorAmbiental;
			transporteGestorAmbientalEliminar.setEstado(false);
			
			listaEmpresasGestorasOriginales = new ArrayList<>();
			for (TransporteEmpresasGestoras empresa : listaEmpresasGestoras) {
				if(!declaracionGeneradorRetceBean.getNumeroObservacion().equals(empresa.getNumeroObservacion())) 
					listaEmpresasGestorasOriginales.add((TransporteEmpresasGestoras) SerializationUtils.clone(empresa));
			}

			listaEmpresasGestorasEliminar.addAll(listaEmpresasGestoras);
			
			listaManifiestosEliminar.addAll(listaManifiestosGestores);
			
			for (Manifiesto manifiesto : listaManifiestosGestores) {
				Manifiesto manifiestoOriginal = (Manifiesto) SerializationUtils.clone(manifiesto);
				buscarHistorialManifiesto(manifiestoOriginal, manifiesto, true);
			}
			
			if(listaDocsManifiestosGestor != null && listaDocsManifiestosGestor.size() > 0) {
				for (Documento documento : listaDocsManifiestosGestor) {
					listaDocsManifiestosEliminar.add(documento);
				}
			}
		}
		
		transporteGestorAmbiental = new TransporteGestorAmbiental();
		listaEmpresasGestoras = new ArrayList<>();
		listaManifiestosGestores = new ArrayList<>();
		resumenManifiestoGestores = new ArrayList<>();

	}
	
	public List<ResumenManifiesto> calcularSumatoriaTotal(List<Manifiesto> listaManifiestosFinal) {
		
		List<DetalleManifiestoDesecho> listaManifiestosDesechosFinal = new ArrayList<>();
		
		List<ResumenManifiesto> listaResumen = new ArrayList<>();
		
		if(listaManifiestosFinal != null && listaManifiestosFinal.size() > 0){
			for (Manifiesto manifiesto : listaManifiestosFinal) {
				listaManifiestosDesechosFinal.addAll(manifiesto.getListaManifiestoDesechos());
			}
			
			for (DetalleManifiestoDesecho manifiestoDesecho : listaManifiestosDesechosFinal) {
				Boolean enResumen = false;
				for(ResumenManifiesto resumen : listaResumen){
					if(resumen.getDesechoPeligroso().getId().equals(manifiestoDesecho.getDesechoPeligroso().getId())){
						enResumen = true;
						Double total = resumen.getTotalDesecho() + manifiestoDesecho.getCantidadToneladas();
						resumen.setTotalDesecho(total);
						break;
					}
				}
				
				if (!enResumen) {
					ResumenManifiesto newResumen = new ResumenManifiesto();
					newResumen.setDesechoPeligroso(manifiestoDesecho.getDesechoPeligroso());
					newResumen.setTotalDesecho(manifiestoDesecho.getCantidadToneladas());
					
					listaResumen.add(newResumen);
				}
			}
		}
		
		return listaResumen;
	}
	
	public void uploadFileAutorizacion(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoAutorizacion = new Documento();
		documentoAutorizacion.setId(null);
		documentoAutorizacion.setContenidoDocumento(contenidoDocumento);
		documentoAutorizacion.setNombre(event.getFile().getFileName());
		documentoAutorizacion.setMime("application/pdf");
		documentoAutorizacion.setExtesion(".pdf");
				
		JsfUtil.addMessageInfo("Documento subido exitosamente");
	}
	
	public void uploadFileManifiestoUnico_(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoManifiestoUnico = new Documento();
		documentoManifiestoUnico.setId(null);
		documentoManifiestoUnico.setContenidoDocumento(contenidoDocumento);
		documentoManifiestoUnico.setNombre(event.getFile().getFileName());
		documentoManifiestoUnico.setMime("application/pdf");
		documentoManifiestoUnico.setExtesion(".pdf");
				
		JsfUtil.addMessageInfo("Documento subido exitosamente");
	}
	
	public void validateTransporte(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = validateDataTransporte();
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public List<FacesMessage> validateDataTransporte() {
		List<FacesMessage> errorMessages = new ArrayList<>();
		List<DesechoPeligroso> listadesechos = new ArrayList<>();
		
		if(!panelMedioPropioVisible && !panelGestorVisible){
			errorMessages.add(new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"Debe ingresar una o las dos opciones de transporte.", null));
		}
		
		if(panelMedioPropioVisible){				
			if (documentoAutorizacion.getId() == null && documentoAutorizacion.getContenidoDocumento() == null)
				errorMessages.add(new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								"El campo Anexo es requerido.", null));
			
			if(listaManifiestos.size() == 0)
				errorMessages.add(new FacesMessage(
						FacesMessage.SEVERITY_ERROR,
						"Debe ingresar al menos un manifiesto en la sección medios propios.", null));
			else {
				for (Manifiesto manifiesto : listaManifiestos) {
					List<DetalleManifiestoDesecho> detallesManifiesto = manifiesto.getListaManifiestoDesechos();
					
					if(detallesManifiesto.size() == 0){
						errorMessages.add(new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								"Debe ingresar al menos un desecho en el manifiesto N° " + manifiesto.getNumeroManifiesto() + " .", null));
					}else{
						for (DetalleManifiestoDesecho objManiefiesto : detallesManifiesto) {
							if(!listadesechos.contains(objManiefiesto.getDesechoPeligroso())){
								listadesechos.add(objManiefiesto.getDesechoPeligroso());	
							}
						}
					}
				}
			}
			
			if (listaDocsManifiestosMediosPropios.size() == 0) {
				errorMessages.add(new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								"Debe ingresar los manifiestos únicos de la sección Medios Propios.", null));
			}
		}
		
		if(panelGestorVisible){			
			if(listaEmpresasGestoras.size() == 0)
				errorMessages.add(new FacesMessage(
						FacesMessage.SEVERITY_ERROR,
						"Debe ingresar al menos una empresa gestora.", null));
			
			if(listaManifiestosGestores.size() == 0)
				errorMessages.add(new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								"Debe ingresar al menos un manifiesto en la sección gestor ambiental.", null));
			else {
				for (Manifiesto manifiesto : listaManifiestosGestores) {
					List<DetalleManifiestoDesecho> detallesManifiesto = manifiesto.getListaManifiestoDesechos();
					
					if(detallesManifiesto.size() == 0){
						errorMessages.add(new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								"Debe ingresar al menos un desecho en el manifiesto N° " + manifiesto.getNumeroManifiesto() + " .", null));
					}else{
						for (DetalleManifiestoDesecho objManiefiesto : detallesManifiesto) {
							if(!listadesechos.contains(objManiefiesto.getDesechoPeligroso())){
								listadesechos.add(objManiefiesto.getDesechoPeligroso());	
							}
						}
					}
				}
			}
			
			if (listaDocsManifiestosGestor.size() == 0) {
				errorMessages.add(new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								"Debe ingresar los manifiestos únicos de la sección Gestor Ambiental.", null));
			}
		}
		// para validar que este ingresada la informacion de todos los desechos
	    	if(getDesechosPendientes().size() != listadesechos.size()) {
				errorMessages.add(new FacesMessage(
						FacesMessage.SEVERITY_ERROR,
						"Para cotinuar debe completar la información de todos los desechos peligrosos del RGD.", null));
	    	}
		
		return errorMessages;
	}
	
	public Boolean guardarTransporte(){
		try{
			if(panelMedioPropioVisible){
				if(documentoAutorizacion.getContenidoDocumento()!=null){
					documentoAutorizacion.setIdTable(generadorDesechosRetce.getId());
					documentoAutorizacion.setNombreTabla(GeneradorDesechosPeligrososRetce.class.getSimpleName());
					documentoAutorizacion.setDescripcion("Autorización transporte medio propio");
					documentoAutorizacion.setEstado(true);               
					Documento documentoSave = documentosFacade.guardarDocumentoAlfrescoSinProyecto(
									generadorDesechosRetce.getCodigoGenerador(),
									"GENERADOR_DESECHOS",
									generadorDesechosRetce.getId().longValue(),
									documentoAutorizacion,
									TipoDocumentoSistema.DOCUMENTO_AUTORIZACION_TRANSPORTE_MEDIO_PROPIO,
									null);
					
					if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0 && documentoAutorizacionOriginal != null)
						documentoAutorizacionOriginal.setIdHistorico(documentoSave.getId());
		        }
				
				transporteMediosPropios.setIdGeneradorRetce(generadorDesechosRetce.getId());
				
				if(transporteMediosPropiosOriginal != null) {
					if(!transporteMediosPropiosOriginal.equalsObject(transporteMediosPropios)) 
						guardarHistorialMediosPropios(transporteMediosPropiosOriginal, transporteMediosPropios);
				} else if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0) {
					transporteMediosPropios.setNumeroObservacion(declaracionGeneradorRetceBean.getNumeroObservacion());
				}
				
				transporteFacade.guardarTransporteMediosPropios(transporteMediosPropios);
				
				for (Manifiesto manifiesto : listaManifiestos) {
					List<DetalleManifiestoDesecho> detallesManifiesto = manifiesto.getListaManifiestoDesechos();
					
					manifiesto.setTransporteMediosPropios(transporteMediosPropios);
					manifiestoFacade.guardarManifiesto(manifiesto);
					
					
					for (DetalleManifiestoDesecho detalleManifiestoDesecho : detallesManifiesto) {
						detalleManifiestoDesecho.setIdManifiesto(manifiesto.getId());
					}
					
					manifiestoFacade.guardarDetallesManifiesto(detallesManifiesto);
					manifiesto.setListaManifiestoDesechos(detallesManifiesto);
					
				}
				
				List<Documento> documentosGuardar = new ArrayList<>();
				documentosGuardar.addAll(listaDocsManifiestosMediosPropios);
				listaDocsManifiestosMediosPropios = new ArrayList<>();
				for(Documento documento : documentosGuardar) {
					if(documento.getContenidoDocumento()!=null) {
						documento.setIdTable(transporteMediosPropios.getId());
						documento.setNombreTabla(Manifiesto.class.getSimpleName());
						documento.setDescripcion("Manifiesto único medio propio");
						documento.setEstado(true);               
						documento = documentosFacade.guardarDocumentoAlfrescoSinProyecto(
										generadorDesechosRetce.getCodigoGenerador(),
										"GENERADOR_DESECHOS",
										generadorDesechosRetce.getId().longValue(),
										documento,
										TipoDocumentoSistema.DOCUMENTO_MANIFIESTO_UNICO,
										null);
						
						for (Documento documentoOrg : listaDocsOriginalesMedioPropio) {
							if(documentoOrg.getIdTable().equals(transporteMediosPropios.getId())){
								documentoOrg.setIdHistorico(documento.getId());
								documentoOrg.setNumeroNotificacion(declaracionGeneradorRetceBean.getNumeroObservacion());
								documentosFacade.actualizarDocumento(documentoOrg);
							}
						}
			        }
					listaDocsManifiestosMediosPropios.add(documento);
				}
				
			}
			
			if(panelGestorVisible){
				for (TransporteEmpresasGestoras empresaGestora : listaEmpresasGestoras) {					
					empresaGestora.setIdGeneradorRetce(generadorDesechosRetce.getId());
					empresaGestora.setTipoActividad(tipoActividad);
					transporteFacade.guardarTransporteEmpresaGestora(empresaGestora);
				}
				
				
				transporteGestorAmbiental.setIdGeneradorRetce(generadorDesechosRetce.getId());
				transporteFacade.guardarTransporteGestorAmbiental(transporteGestorAmbiental);
				
				for (Manifiesto manifiesto : listaManifiestosGestores) {
					List<DetalleManifiestoDesecho> detallesManifiesto = manifiesto.getListaManifiestoDesechos();
					
					manifiesto.setTransporteGestorAmbiental(transporteGestorAmbiental);
					manifiestoFacade.guardarManifiesto(manifiesto);
					
					
					for (DetalleManifiestoDesecho detalleManifiestoDesecho : detallesManifiesto) {
						detalleManifiestoDesecho.setIdManifiesto(manifiesto.getId());
					}
					
					manifiestoFacade.guardarDetallesManifiesto(detallesManifiesto);
					manifiesto.setListaManifiestoDesechos(detallesManifiesto);
				}
				
				List<Documento> documentosGuardar = new ArrayList<>();
				documentosGuardar.addAll(listaDocsManifiestosGestor);
				listaDocsManifiestosGestor = new ArrayList<>();
				for(Documento documento : documentosGuardar) {
					if(documento != null && documento.getContenidoDocumento()!=null){
						documento.setIdTable(transporteGestorAmbiental.getId());
						documento.setNombreTabla(Manifiesto.class.getSimpleName());
						documento.setDescripcion("Manifiesto único gestor ambiental");
						documento.setEstado(true);               
						documento = documentosFacade.guardarDocumentoAlfrescoSinProyecto(
										generadorDesechosRetce.getCodigoGenerador(),
										"GENERADOR_DESECHOS",
										generadorDesechosRetce.getId().longValue(),
										documento,
										TipoDocumentoSistema.DOCUMENTO_MANIFIESTO_UNICO,
										null); 
						
						for (Documento documentoOrg : listaDocsOriginalesGestores) {
							if(documentoOrg.getIdTable().equals(transporteGestorAmbiental.getId())){
								documentoOrg.setIdHistorico(documento.getId());
								documentoOrg.setNumeroNotificacion(declaracionGeneradorRetceBean.getNumeroObservacion());
								documentosFacade.actualizarDocumento(documentoOrg);
							}
						}
			        }
					listaDocsManifiestosGestor.add(documento);
				}
				
			}
			
			//eliminacion fuera para guardar los datos cuando se elimina toda la seccion
			if(listaManifiestosEliminar.size() > 0)
				manifiestoFacade.eliminarManifiesto(listaManifiestosEliminar);
			
			if(listaDetallesManifiestosDesechosEliminar.size() > 0)
				manifiestoFacade.eliminarDetallesManifiesto(listaDetallesManifiestosDesechosEliminar);
			
			if(listaEmpresasGestorasEliminar.size() > 0)
				transporteFacade.eliminarTransporteEmpresasGestoras(listaEmpresasGestorasEliminar);
			
			if(listaManifiestosGestoresEliminar.size() > 0)
				manifiestoFacade.eliminarManifiesto(listaManifiestosGestoresEliminar);
			
			if(listaDetallesManifiestosDesechosEliminar.size() > 0)
				manifiestoFacade.eliminarDetallesManifiesto(listaDetallesManifiestosDesechosEliminar);
			
			if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0) {
				guardarHistorial();
			}
			
			if(listaDocsManifiestosEliminar != null && listaDocsManifiestosEliminar.size() > 0) {
				for (Documento documentoExportacion : listaDocsManifiestosEliminar) {
					documentosFacade.eliminarDocumento(documentoExportacion);
				}
			}
			
			listaEmpresasGestorasEliminar = new ArrayList<>();
			listaManifiestosGestoresEliminar = new ArrayList<>();
			listaDetallesManifiestosDesechosEliminar = new ArrayList<>();
			listaDocsManifiestosEliminar = new ArrayList<>();
			
			return true;
		} catch (Exception e) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
            return false;
        }
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
	
	
	public void guardarHistorialMediosPropios(TransporteMediosPropios medioPropioOriginal, TransporteMediosPropios medioPropio) {
		if(medioPropio.getNumeroObservacion() == null || 
				medioPropio.getNumeroObservacion() != declaracionGeneradorRetceBean.getNumeroObservacion()) {
			TransporteMediosPropios itemHistorial = transporteFacade.getTransporteMedioPropioHistorial(medioPropio.getId(), declaracionGeneradorRetceBean.getNumeroObservacion());
			if(itemHistorial == null){
				medioPropioOriginal.setId(null);
				medioPropioOriginal.setHistorial(true);
				medioPropioOriginal.setIdRegistroOriginal(medioPropio.getId());
				medioPropioOriginal.setNumeroObservacion(declaracionGeneradorRetceBean.getNumeroObservacion());
				transporteFacade.guardarTransporteMediosPropios(medioPropioOriginal);
			}
		}
	}
	
	public void guardarHistorial() {
		if(transporteMediosPropiosHistorial != null) {
			transporteFacade.guardarTransporteMediosPropios(transporteMediosPropiosHistorial);
		}
		
		if(transporteMediosPropiosEliminar != null)
			transporteFacade.guardarTransporteMediosPropios(transporteMediosPropiosEliminar);
		
		if(transporteGestorAmbientalHistorial != null) {
			transporteFacade.guardarTransporteGestorAmbiental(transporteGestorAmbientalHistorial);
		}
		
		if(transporteGestorAmbientalEliminar != null)
			transporteFacade.guardarTransporteGestorAmbiental(transporteGestorAmbientalEliminar);
		
		if(documentoAutorizacionOriginal != null && 
				documentoAutorizacionOriginal.getIdHistorico() != null){
			documentoAutorizacionOriginal.setNumeroNotificacion(declaracionGeneradorRetceBean.getNumeroObservacion());
			documentosFacade.actualizarDocumento(documentoAutorizacionOriginal);
		}
		
		if(listaManifiestosOriginales.size() > 0){
			manifiestoFacade.guardarManifiestos(listaManifiestosOriginales); 
		}
		
		if(listaDetallesManifiestoOriginales.size() > 0) {
			manifiestoFacade.guardarDetallesManifiesto(listaDetallesManifiestoOriginales);
		}
		
		if(listaEmpresasGestorasOriginales.size() > 0) {
			for (TransporteEmpresasGestoras empresa : listaEmpresasGestorasOriginales) {
				Integer idOriginal = empresa.getId();
				empresa.setId(null);
				empresa.setHistorial(true);
				empresa.setIdRegistroOriginal(idOriginal);
				empresa.setNumeroObservacion(declaracionGeneradorRetceBean.getNumeroObservacion());
				transporteFacade.guardarTransporteEmpresaGestora(empresa);
			}
		}
		
		transporteMediosPropiosHistorial = null;
		transporteMediosPropiosOriginal = null;
		documentoAutorizacionOriginal = null;
		listaManifiestosOriginales = new ArrayList<>();
		listaDetallesManifiestoOriginales = new ArrayList<>();
		listaEmpresasGestorasOriginales = new ArrayList<>();
		listaDocsOriginalesMedioPropio = new ArrayList<>();
		listaDocsOriginalesGestores = new ArrayList<>();
	}
	
	public void cargarDatosHistorial(Integer numeroObservacion) {
		if (transporteMediosPropios != null && transporteMediosPropios.getId() != null) {
			if(transporteMediosPropios.getNumeroObservacion() != null && 
					transporteMediosPropios.getNumeroObservacion().equals(numeroObservacion)) {
				transporteMediosPropios.setNuevoEnModificacion(true);
			} else {
				List<TransporteMediosPropios> historialesMediosPropios = transporteFacade.getHistorialTransporteMedioPropio(transporteMediosPropios.getId());
				transporteMediosPropios.setListaHistorial(historialesMediosPropios);
			}
			
			List<Documento> documentosHistorial = new ArrayList<>();
			if (documentosAutorizacion.size() > 0) {
				for (Documento documento : documentosAutorizacion) {
					if(documento.getIdHistorico() != null) {
						documentosHistorial.add(documento);
					}
				}
			}
			
			listaDocumentosAutorizacionHistorial = documentosHistorial;
		} else {
			//buscar eliminados
			listaMediosPropiosHistorialEliminado = transporteFacade.getHistorialTransporteMedioPropioEliminado(generadorDesechosRetce.getId());
		}
		if (transporteGestorAmbiental == null || transporteGestorAmbiental.getId() == null) {
			//buscar eliminados
			listaGestorHistorialEliminado = transporteFacade.getHistorialGestorAmbientalEliminado(generadorDesechosRetce.getId());
		}
		
		mostrarHistorialManifiestoMP = false;
		mostrarHistorialManifiestoGA = false;
		if (listaManifiestos != null && listaManifiestos.size() > 0) {
			getManifiestoHistorial(numeroObservacion, 1);
		}

		if (listaManifiestosGestores != null && listaManifiestosGestores.size() > 0) {
			getManifiestoHistorial(numeroObservacion, 2);
		}
		
		mostrarHistorialEmpresaGestora = false;
		if (listaEmpresasGestoras != null && listaEmpresasGestoras.size() > 0) {
			for (TransporteEmpresasGestoras empresa : listaEmpresasGestoras) {
				if(empresa.getNumeroObservacion() != null && empresa.getNumeroObservacion().equals(numeroObservacion)){
					empresa.setNuevoEnModificacion(true);
					mostrarHistorialEmpresaGestora = true;
				} 
			}
			
			historialEmpresasEliminadas = transporteFacade
					.getHistorialEmpresasGestorasPorRgdRetce(generadorDesechosRetce.getId(), tipoActividad.getId());
		}
		
		//buscar manifiestos eliminados
		if (transporteMediosPropios != null && transporteMediosPropios.getId() != null) {
			historialManifiestosMPEliminados = manifiestoFacade.getManifiestosEliminadosPorMedioPropio(transporteMediosPropios.getId());
			
			if(historialManifiestosMPEliminados != null) {
				for (Manifiesto manifiesto : historialManifiestosMPEliminados) {
					List<DetalleManifiestoDesecho> detalle = manifiestoFacade.getDetalleManifiestoPorIdManifiesto(manifiesto.getIdRegistroOriginal());
					manifiesto.setListaManifiestoDesechos(detalle);
					
					List<Documento> documentoManifiesto = documentosFacade.documentoXTablaIdXIdDoc(
									manifiesto.getIdRegistroOriginal(),
									Manifiesto.class.getSimpleName(),
									TipoDocumentoSistema.DOCUMENTO_MANIFIESTO_UNICO);
					if (documentoManifiesto.size() > 0)
						manifiesto.setManifiestoUnico(documentoManifiesto.get(0));
				}
			}
		}
		
		if (transporteGestorAmbiental != null && transporteGestorAmbiental.getId() != null) {
			historialManifiestosGAEliminados = manifiestoFacade.getManifiestosEliminadosPorGestorAmbiental(transporteGestorAmbiental.getId());
			
			if(historialManifiestosGAEliminados != null) {
				for (Manifiesto manifiesto : historialManifiestosGAEliminados) {
					List<DetalleManifiestoDesecho> detalle = manifiestoFacade.getDetalleManifiestoPorIdManifiesto(manifiesto.getIdRegistroOriginal());
					manifiesto.setListaManifiestoDesechos(detalle);
					
					List<Documento> documentoManifiesto = documentosFacade.documentoXTablaIdXIdDoc(
							manifiesto.getIdRegistroOriginal(),
							Manifiesto.class.getSimpleName(),
							TipoDocumentoSistema.DOCUMENTO_MANIFIESTO_UNICO);
					if (documentoManifiesto.size() > 0)
						manifiesto.setManifiestoUnico(documentoManifiesto.get(0));
				}
			}
		}
	}
	
	public void getManifiestoHistorial(Integer numeroObservacion, Integer tipo) {
		Boolean mostrarHistorialManifiesto = false;
		List<Manifiesto> manifiestos = (tipo == 1) ? listaManifiestos : listaManifiestosGestores;
		for (Manifiesto manifiesto : manifiestos) {
			if(manifiesto.getNumeroObservacion() != null && 
					manifiesto.getNumeroObservacion().equals(numeroObservacion)) {
				manifiesto.setNuevoEnModificacion(true);
				mostrarHistorialManifiesto = true;
			} else {
				List<Manifiesto> historialManifiesto = manifiestoFacade.getManifiestoHistorial(manifiesto.getId());
				if(historialManifiesto != null) {
					manifiesto.setListaHistorial(historialManifiesto);
					mostrarHistorialManifiesto = true;
				}
			}
			
			List<DetalleManifiestoDesecho> historialDetalle = new ArrayList<DetalleManifiestoDesecho>();		
			for (DetalleManifiestoDesecho detalle : manifiesto.getListaManifiestoDesechos()) {
				if(detalle.getNumeroObservacion() != null && 
						detalle.getNumeroObservacion().equals(numeroObservacion)) {
					detalle.setNuevoEnModificacion(true);
					mostrarHistorialManifiesto = true;
					historialDetalle.add(detalle);
				} else {
					List<DetalleManifiestoDesecho> historialDet = manifiestoFacade.getDetalleManifiestoHistorial(detalle.getId());
					if(historialDet != null) {
						historialDetalle.addAll(historialDet);
						mostrarHistorialManifiesto = true;
					}
				}
			}
			
			manifiesto.setListaHistorialDetalle(historialDetalle);
			
			List<DetalleManifiestoDesecho> detallesEliminados =  manifiestoFacade.getDetallesEliminadosPorIdManifiesto(manifiesto.getId());
			if(detallesEliminados != null) {
				manifiesto.setListaDetallesEliminados(detallesEliminados);
				mostrarHistorialManifiesto = true;
			}
			
			List<Documento> documentoManifiesto = documentosFacade.documentoXTablaIdXIdDoc(
					manifiesto.getId(),
					Manifiesto.class.getSimpleName(),
					TipoDocumentoSistema.DOCUMENTO_MANIFIESTO_UNICO);
			List<Documento> documentosHistorial = new ArrayList<>();
			if (documentoManifiesto.size() > 0) {
				for (Documento documento : documentoManifiesto) {
					if(documento.getIdHistorico() != null) {
						documentosHistorial.add(documento);
						mostrarHistorialManifiesto = true;
					}
				}
			}
			
			manifiesto.setListaHistorialDocumentos(documentosHistorial);
		}
		
		if(tipo == 1)
			mostrarHistorialManifiestoMP = mostrarHistorialManifiesto;
		else
			mostrarHistorialManifiestoGA = mostrarHistorialManifiesto;
	}
	
	public void verHistorialManifiesto(Manifiesto manifiesto) {
		manifiestoSeleccionado = manifiesto;
	}
	
	public void verHistorialDocumentos() {
		JsfUtil.getBean(GeneradorDesechosPeligrososVerController.class).setListaDocumentosHistorial(listaDocumentosAutorizacionHistorial);
	}
	
	public void verManifiestosEliminados(Integer tipo) {
		switch (tipo) {
		case 1:
			historialManifiestosEliminados = historialManifiestosMPEliminados;
			break;
		case 2:
			historialManifiestosEliminados = historialManifiestosGAEliminados;
			break;
		default:
			historialManifiestosEliminados = new ArrayList<>();
			break;
		}
		
	}
	
	public void eliminarDocumento(String faseDocumento, Documento documentoUnico) {
		try {
			Boolean agregarOriginal = false;
			if (documentoUnico.getId() != null) {
				if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0 && 
						!declaracionGeneradorRetceBean.getNumeroObservacion().equals(documentoUnico.getNumeroNotificacion())) 
					agregarOriginal = true;
				
				documentoUnico.setEstado(false);
				listaDocsManifiestosEliminar.add(documentoUnico);
			}
			
			switch (faseDocumento) {
			case "1":
				listaDocsManifiestosMediosPropios.remove(documentoUnico);
				
				if(agregarOriginal)
					listaDocsOriginalesMedioPropio.add((Documento) SerializationUtils.clone(documentoUnico));
				break;
			case "2":
				listaDocsManifiestosGestor.remove(documentoUnico);
				
				if(agregarOriginal)
					listaDocsOriginalesGestores.add((Documento) SerializationUtils.clone(documentoUnico));
				break;
			default:
				break;
			}

		} catch (Exception e) {

		}
	}
	
	public void uploadFileManifiestoUnico(FileUploadEvent event) {
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
			if(!listaDocsManifiestosMediosPropios.contains(nuevoDocumento))
				listaDocsManifiestosMediosPropios.add(nuevoDocumento);
			break;
		case "2":
			if(!listaDocsManifiestosGestor.contains(nuevoDocumento))
				listaDocsManifiestosGestor.add(nuevoDocumento);
			break;
		default:
			break;
		}

		nuevoDocumento = new Documento();
	}
	
    public static double redondearDecimales(double valorInicial, int numeroDecimales) {
        double parteEntera, resultado;
        resultado = valorInicial;
        parteEntera = Math.floor(resultado);
        resultado=(resultado-parteEntera)*Math.pow(10, numeroDecimales);
        resultado=Math.round(resultado);
        resultado=(resultado/Math.pow(10, numeroDecimales))+parteEntera;
        return resultado;
    }
}
