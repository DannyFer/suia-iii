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

import org.apache.commons.lang.SerializationUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.primefaces.model.Visibility;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.cliente.ConsultaRucCedula;
import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.prevencion.registrogeneradordesechos.bean.TipoEliminacionDesechoBean;
import ec.gob.ambiente.retce.model.CatalogoMetodoEstimacion;
import ec.gob.ambiente.retce.model.CatalogoSustanciasRetce;
import ec.gob.ambiente.retce.model.DatosLaboratorio;
import ec.gob.ambiente.retce.model.DesechoAutogestion;
import ec.gob.ambiente.retce.model.DesechoExportacion;
import ec.gob.ambiente.retce.model.DesechoGeneradoEliminacion;
import ec.gob.ambiente.retce.model.DetalleCatalogoGeneral;
import ec.gob.ambiente.retce.model.DesechoEliminacionAutogestion;
import ec.gob.ambiente.retce.model.DetalleManifiestoDesecho;
import ec.gob.ambiente.retce.model.DisposicionFueraInstalacion;
import ec.gob.ambiente.retce.model.EliminacionFueraInstalacion;
import ec.gob.ambiente.retce.model.GeneradorDesechosPeligrososRetce;
import ec.gob.ambiente.retce.model.IdentificacionDesecho;
import ec.gob.ambiente.retce.model.SubstanciasRetce;
import ec.gob.ambiente.retce.services.CatalogoMetodoEstimacionFacade;
import ec.gob.ambiente.retce.services.CatalogoSustanciasRetceFacade;
import ec.gob.ambiente.retce.services.DatosLaboratorioFacade;
import ec.gob.ambiente.retce.services.DesechoAutogestionFacade;
import ec.gob.ambiente.retce.services.DetalleCatalogoGeneralFacade;
import ec.gob.ambiente.retce.services.GeneradorDesechosPeligrososFacade;
import ec.gob.ambiente.retce.services.IdentificacionDesechosFacade;
import ec.gob.ambiente.retce.services.SubstanciasRetceFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.desechoPeligroso.facade.DesechoPeligrosoFacade;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gov.sri.wsconsultacontribuyente.ContribuyenteCompleto;

@ManagedBean
@ViewScoped
public class AutogestionDesechosBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final ConsultaRucCedula consultaRucCedula = new ConsultaRucCedula(Constantes.getUrlWsRegistroCivilSri());

	@Getter 
	@Setter 
	@ManagedProperty(value = "#{loginBean}") 
	private LoginBean loginBean;
	
	@ManagedProperty(value = "#{declaracionGeneradorRetceBean}")
	@Getter
	@Setter
	private DeclaracionGeneradorRetceBean declaracionGeneradorRetceBean;
	
	@EJB
	private DetalleCatalogoGeneralFacade detalleCatalogoGeneralFacade;
	
	@EJB
	private DesechoPeligrosoFacade desechoPeligrosoFacade;
	
	@EJB
	private GeneradorDesechosPeligrososFacade generadorDesechosPeligrososFacade;
	
	@EJB
	private DesechoAutogestionFacade desechoAutogestionFacade;
	
	@EJB
	private DocumentosFacade documentosFacade;
	
	@EJB
	private CatalogoSustanciasRetceFacade catalogoSustanciasRetceFacade;
	
	@EJB
	private CatalogoMetodoEstimacionFacade catalogoMetodoEstimacionFacade;
	
	@EJB 
	private DatosLaboratorioFacade datosLaboratorioFacade; 
	
	@EJB
	private SubstanciasRetceFacade substanciasRetceFacade;

	@EJB
	private IdentificacionDesechosFacade identificacionDesechosFacade;

	@Getter
	@Setter
	private List<DetalleCatalogoGeneral> listaTipoUnidad;

	@Getter
	@Setter
	private List<DetalleCatalogoGeneral> listaTipoDesechoGenerado;
	
	@Getter
	@Setter
	private List<DesechoAutogestion> listaDesechosAutogestion, listaDesechosAutogestionEliminar, listaDesechosAutogestionOriginales;

	@Getter
	@Setter
	private List<DesechoEliminacionAutogestion> listaDesechosEliminacionAutogestion, listaEliminacionAutogestionOriginales, listaTotalEliminacionAutogestionOriginales; 

	@Getter
	@Setter
	private List<DesechoEliminacionAutogestion> listaDesechosEliminadosPorAutogestionEliminar;
	
	@Getter
	@Setter
	private List<DesechoGeneradoEliminacion> listaDesechosGeneradosPorEliminacion, listaDesechosGeneradosOriginales, listaTotalDesechosGeneradosOriginales;
	
	@Getter
	@Setter
	private List<DesechoGeneradoEliminacion> listaDesechosGeneradosPorEliminacionEliminar;
	
	@Getter
	@Setter
	private List<DesechoPeligroso> listaDesechosGenerados;
	
	@Getter
	@Setter
	private List<SubstanciasRetce> listaSustanciasDesecho, listaSustanciasDesechoEliminar, listaSustanciasDesechoOriginales, listaTotalSustanciasOriginales;
	
	@Getter
	@Setter
	private List<CatalogoSustanciasRetce> listaSustanciasRetce;
	
	@Getter
	@Setter
	private List<CatalogoMetodoEstimacion> listaMetodoEstimacion;
	
	@Getter
	@Setter
	private List<DetalleManifiestoDesecho> listaDesechoManifiestosEliminar = new ArrayList<>();
	@Getter
	@Setter
	private List<DesechoExportacion> listaDesechosExportacionEliminar = new ArrayList<>();
	@Getter
	@Setter
	private List<EliminacionFueraInstalacion> listaDesechosEliminacionEliminar = new ArrayList<>();
	@Getter
	@Setter
	private List<DisposicionFueraInstalacion> listaDesechosDisposicionEliminar = new ArrayList<>();
	
	@Getter
	@Setter
	private List<DatosLaboratorio> listaLaboratoriosOriginales, listaTotalLaboratoriosOriginales;
	
	@Getter
	@Setter
	private List<Documento> listaAutorizaciones, listaInformesOriginales, listaTotalInformesOriginales;

	@Getter
	@Setter
	private DesechoPeligroso desechoSeleccionado;
	
	@Getter
	@Setter
	private DesechoPeligroso desechoGeneradoSeleccionado;

	@Getter
	@Setter
	private DesechoAutogestion desechoAutogestion;

	@Getter
	@Setter
	private DesechoEliminacionAutogestion desechoEliminadoPorAutogestion;

	@Getter
	@Setter
	private DesechoGeneradoEliminacion residuoNoPeligroso;

	@Getter
	@Setter
	private DesechoGeneradoEliminacion desechoGeneradoPorEliminacion;
	
	@Getter
	@Setter
	private Documento documentoAutorizacion, informeMonitoreo, documentoAutorizacionOriginal, informeMonitoreoOriginal;
	
	@Getter
	@Setter
	private GeneradorDesechosPeligrososRetce generadorDesechosRetce, generadorDesechosRetceHistorial;
	
	@Getter
	@Setter
	private SubstanciasRetce sustanciaRetce;

	@Getter
	@Setter
	private String tipoDesechoPeligroso = "tipodesecho.peligroso";

	@Getter
	@Setter
	private boolean editar, habilitarUnidad=true;
	
	@Getter
	@Setter
	private boolean editarTipoEliminacion, editarSustancia;

	@Getter
	private boolean panelAdicionarVisible;
	
	@Getter
	private boolean pnlTipoEliminacionVisible, pnlSustanciaVisible;
	
	@Getter
	@Setter
	private boolean habilitarBtnSiguiente;
	
	@Setter
	private String filter;
	
	@Setter
	protected CompleteOperation completeOperationOnDelete;
	
	@Setter
	protected CompleteOperation completeOperationOnAdd;
	
	@Setter
	@Getter
	private TreeNode catalogo;
	
	public static final String TYPE_FOLDER = "folder";
	public static final String TYPE_DOCUMENT = "document";
	
	@Getter
	@Setter
	private Integer ordenMedicionDirecta = 1;

	@PostConstruct
	private void initDatos() {
		desechoSeleccionado = null;

		desechoAutogestion = new DesechoAutogestion();
		desechoEliminadoPorAutogestion = new DesechoEliminacionAutogestion();
		residuoNoPeligroso = new DesechoGeneradoEliminacion();
		desechoGeneradoPorEliminacion = new DesechoGeneradoEliminacion();
		
		inicializacionObj();

		panelAdicionarVisible = false;
		pnlTipoEliminacionVisible = false;
		pnlSustanciaVisible = false;
		editar = false;
		editarTipoEliminacion = false;
		habilitarBtnSiguiente = false;

		listaTipoUnidad = JsfUtil.getBean(IdentificacionDesechosBean.class).getListaTipoUnidad();

		listaTipoDesechoGenerado = detalleCatalogoGeneralFacade.findByCatalogoGeneralString("generador.tipo_desecho");
		
		listaSustanciasRetce = catalogoSustanciasRetceFacade.findAll();
		
		listaMetodoEstimacion = catalogoMetodoEstimacionFacade.findAll();
		
		generadorDesechosRetce = declaracionGeneradorRetceBean.getGeneradorDesechosRetce();		
	}
	
	public void inicializacionObj() {
		listaDesechosAutogestion = new ArrayList<>();
		listaDesechosGenerados = new ArrayList<>();
		listaDesechosEliminacionAutogestion = new ArrayList<>();
		listaDesechosGeneradosPorEliminacion = new ArrayList<>();
		listaDesechosAutogestionEliminar = new ArrayList<>();
		listaDesechosEliminadosPorAutogestionEliminar = new ArrayList<>();
		listaDesechosGeneradosPorEliminacionEliminar = new ArrayList<>();
		listaSustanciasDesecho = new ArrayList<>();
		listaSustanciasDesechoEliminar = new ArrayList<>();
		listaDesechosGeneradosOriginales = new ArrayList<>();
		listaEliminacionAutogestionOriginales = new ArrayList<>();
		listaSustanciasDesechoOriginales = new ArrayList<>();
		listaLaboratoriosOriginales = new ArrayList<>();
		listaInformesOriginales = new ArrayList<>();
		listaTotalDesechosGeneradosOriginales = new ArrayList<>();
		listaTotalEliminacionAutogestionOriginales = new ArrayList<>();
		listaTotalSustanciasOriginales = new ArrayList<>();
		listaTotalLaboratoriosOriginales = new ArrayList<>();
		listaTotalInformesOriginales = new ArrayList<>();
		listaDesechosAutogestionOriginales = new ArrayList<>();
		
		documentoAutorizacion = new Documento();
	}
	
	public void cargarDatos() {
		inicializacionObj();
		
		if (generadorDesechosRetce != null && generadorDesechosRetce.getId() != null && generadorDesechosRetce.getRealizaAutogestion() != null) {
			if (generadorDesechosRetce.getRealizaAutogestion()) {
				listaDesechosAutogestion = desechoAutogestionFacade.getListaDesechosAutogestion(generadorDesechosRetce.getId());

				if (listaDesechosAutogestion != null && listaDesechosAutogestion.size() > 0)
					habilitarBtnSiguiente = true;
				else
					listaDesechosAutogestion = new ArrayList<>();

				listaAutorizaciones = documentosFacade.documentoXTablaIdXIdDoc(
								generadorDesechosRetce.getId(),
								GeneradorDesechosPeligrososRetce.class.getSimpleName(),
								TipoDocumentoSistema.DOCUMENTO_AUTORIZACION_AUTOGESTION);
				if (listaAutorizaciones.size() > 0) {
					documentoAutorizacion = listaAutorizaciones.get(0);
					documentoAutorizacionOriginal = (Documento) SerializationUtils.clone(documentoAutorizacion);
				} else
					documentoAutorizacion = new Documento();
			} else {
				habilitarBtnSiguiente = true;
			}
			
			if(habilitarBtnSiguiente) {
				List<FacesMessage> errorMessages = validateData();
				if (!errorMessages.isEmpty())
					habilitarBtnSiguiente = false;
			}
			
			if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0) 
				generadorDesechosRetceHistorial = generadorDesechosPeligrososFacade.getRgdRetceHistorialPorID(generadorDesechosRetce.getId(), declaracionGeneradorRetceBean.getNumeroObservacion());
		}
	}

	public void toggleHandle(ToggleEvent event) {
		if (event.getVisibility().equals(Visibility.HIDDEN)) {
			panelAdicionarVisible = false;
			editar = false;
		} else
			panelAdicionarVisible = true;
		
		if (!editar) {
			desechoSeleccionado = null;
			JsfUtil.getBean(TipoEliminacionDesechoBean.class).resetSelection();
			desechoAutogestion = new DesechoAutogestion();
			listaDesechosEliminacionAutogestion = new ArrayList<>();
			listaSustanciasDesecho = new ArrayList<>();
			desechoAutogestionOriginal = null;
		}
	}
	
	public void nuevoTipoEliminacion() {
		editarTipoEliminacion = false;
		habilitarUnidad=true;
		JsfUtil.getBean(TipoEliminacionDesechoBean.class).reset();
		desechoEliminadoPorAutogestion = new DesechoEliminacionAutogestion();
		listaDesechosGeneradosPorEliminacion = new ArrayList<>();
		// para inicializar la unidad
		IdentificacionDesecho objIdentificacionDesecho = identificacionDesechosFacade.getIdentificacionDesechosPorRgdRetcePorDesecho(generadorDesechosRetce.getId(), desechoSeleccionado.getId());
		if(objIdentificacionDesecho != null && objIdentificacionDesecho.getTipoUnidad() != null){
			desechoEliminadoPorAutogestion.setTipoUnidad(objIdentificacionDesecho.getTipoUnidad());
			habilitarUnidad=false;
		}
		desechoGeneradoPorEliminacion = new DesechoGeneradoEliminacion();
		/// validacion para quitar rencauche cuando no sea neumaticos
	    Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String descDesecho = params.get("desechoDescripcion");
		if(descDesecho != null && !(descDesecho.toLowerCase().contains("neumaticos") || descDesecho.toLowerCase().contains("neumáticos"))){
			TreeNode catalogo = JsfUtil.getBean(TipoEliminacionDesechoBean.class).getCatalogo();
			for (TreeNode tree : catalogo.getChildren()) {
				if(tree.getType().equals("folder")){
					for (TreeNode tree_1 : tree.getChildren()) {
						if(tree_1.getType().equals("document")){
							if(tree_1.getData().toString().equals("Reencauche")){
								tree.getChildren().remove(tree_1);
								break;
							}
						}
					}
				}
			}
		}
	}
	
	public List<DesechoPeligroso> getDesechosPendientes() {
		List<DesechoPeligroso> result = new ArrayList<>();
		List<DesechoPeligroso> desechosRegistrados = new ArrayList<>();
		
		List<IdentificacionDesecho> listaIdentificacionDesechos = JsfUtil.getBean(IdentificacionDesechosBean.class).getListaIdentificacionDesechos();
		for (IdentificacionDesecho identificacionDesecho : listaIdentificacionDesechos) {
			if(identificacionDesecho.getCantidadTotalToneladas() > 0) //solo desechos que tengan valor mayor a 0 en identificacion
				desechosRegistrados.add(identificacionDesecho.getDesechoPeligroso());
		}

		result.addAll(desechosRegistrados);

		if(listaDesechosAutogestion != null) {
			for (DesechoAutogestion desechoGestion : listaDesechosAutogestion) {
				if (editar && desechoGestion.getDesechoPeligroso().equals(desechoSeleccionado))
					continue;

				if (desechoGestion.getDesechoPeligroso() != null)
					result.remove(desechoGestion.getDesechoPeligroso());
			}
		}

		return result;
	}
	
	public List<DesechoPeligroso> getDesechosRgd(){
		List<DesechoPeligroso> result = new ArrayList<>();
		List<DesechoPeligroso> desechosRegistrados = declaracionGeneradorRetceBean.getListaDesechosGenerador();
		
		result.addAll(desechosRegistrados);
		
		return result;
	}
	
	public List<CatalogoSustanciasRetce> getSustanciasPendientes() {
		List<CatalogoSustanciasRetce> result = new ArrayList<>();

		result.addAll(listaSustanciasRetce);

		if(listaSustanciasDesecho != null) {
			for (SubstanciasRetce desechoSustancia : listaSustanciasDesecho) {
				if (editarSustancia && desechoSustancia.getCatologSustanciasRetce().equals(sustanciaRetce.getCatologSustanciasRetce()))
					continue;

				if (desechoSustancia.getCatologSustanciasRetce() != null)
					result.remove(desechoSustancia.getCatologSustanciasRetce());
			}
		}

		return result;
	}

	//tipos de eliminación o disposición final
	public void agregarTipoEliminacion() {
		desechoEliminadoPorAutogestion.setTipoEliminacion(JsfUtil.getBean(
				TipoEliminacionDesechoBean.class)
				.getTipoEliminacionDesechoSeleccionada());
		
		if(desechoEliminadoPorAutogestion.getTipoEliminacion() == null) {
			JsfUtil.addMessageError("Debe escoger un tipo de eliminación para el desecho seleccionado. ");
			return;
		}
		
		if(desechoEliminadoPorAutogestion.getGeneraDesecho()) {
			if(listaDesechosGeneradosPorEliminacion.size() == 0) {
				JsfUtil.addMessageError("Debe ingresar los desechos generados posterior al tratamiento realizado. ");
				return;
			}

			desechoEliminadoPorAutogestion.setListaDesechosGeneradosPorEliminacion(listaDesechosGeneradosPorEliminacion);
		}
		
		listaDesechosEliminacionAutogestion.add(desechoEliminadoPorAutogestion);

		if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0) {
			if(desechoEliminadoPorAutogestion.getId() != null) {
				if(!desechoEliminadoPorAutogestionOriginal.equalsObject(desechoEliminadoPorAutogestion)) 
					buscarHistorialTipoEliminacion(desechoEliminadoPorAutogestionOriginal, desechoEliminadoPorAutogestion, false);
			}else {
				desechoEliminadoPorAutogestion.setNumeroObservacion(declaracionGeneradorRetceBean.getNumeroObservacion());
			}
		}

		JsfUtil.getBean(TipoEliminacionDesechoBean.class).resetSelection();
		desechoEliminadoPorAutogestion = new DesechoEliminacionAutogestion();
		desechoGeneradoPorEliminacion = new DesechoGeneradoEliminacion();
		desechoEliminadoPorAutogestionOriginal = null;
		
		JsfUtil.addCallbackParam("addTipoEliminacion");
	}

	private DesechoEliminacionAutogestion desechoEliminadoPorAutogestionOriginal;
	public void editarTipo(DesechoEliminacionAutogestion tipoEliminacion) {
		desechoEliminadoPorAutogestionOriginal = (DesechoEliminacionAutogestion) SerializationUtils.clone(tipoEliminacion);
		desechoEliminadoPorAutogestion = tipoEliminacion;
		JsfUtil.getBean(TipoEliminacionDesechoBean.class)
				.setTipoEliminacionDesechoSeleccionada(
						tipoEliminacion.getTipoEliminacion());
		listaDesechosGeneradosPorEliminacion = desechoEliminadoPorAutogestion.getListaDesechosGeneradosPorEliminacion();
		if(listaDesechosGeneradosPorEliminacion == null)
			listaDesechosGeneradosPorEliminacion = new ArrayList<>();
		
		editarTipoEliminacion = true;
		listaDesechosEliminacionAutogestion.remove(tipoEliminacion);
		
		try {
			JsfUtil.addCallbackParam("addTipoEliminacion");
		} catch (Exception e) {
			JsfUtil.addMessageError("Error al cargar la información");
		}
	}

	public void eliminarTipo(DesechoEliminacionAutogestion tipoEliminacion) {
		try {
			if (tipoEliminacion.getId() != null) {
				if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0) {
					DesechoEliminacionAutogestion desechoEliminacionOriginal = (DesechoEliminacionAutogestion) SerializationUtils.clone(tipoEliminacion);
					buscarHistorialTipoEliminacion(desechoEliminacionOriginal, tipoEliminacion, true);
				}
				
				tipoEliminacion.setEstado(false);
				listaDesechosEliminadosPorAutogestionEliminar.add(tipoEliminacion);
			}
			listaDesechosEliminacionAutogestion.remove(tipoEliminacion);
		} catch (Exception e) {

		}
	}
	
	public void cancelarTipoEliminacion() {
		if(editarTipoEliminacion)
			listaDesechosEliminacionAutogestion.add(desechoEliminadoPorAutogestionOriginal);
		
		JsfUtil.getBean(TipoEliminacionDesechoBean.class).resetSelection();
		desechoEliminadoPorAutogestion = new DesechoEliminacionAutogestion();
		desechoGeneradoPorEliminacion = new DesechoGeneradoEliminacion();
		desechoEliminadoPorAutogestionOriginal = null;
		listaDesechosGeneradosOriginales = new ArrayList<>();
		listaEliminacionAutogestionOriginales = new ArrayList<>();
	}

	public void buscarHistorialTipoEliminacion(DesechoEliminacionAutogestion desechoEliminacionOriginal, DesechoEliminacionAutogestion desechoEliminadoPorAutogestion, Boolean eliminacion) {
		if(desechoEliminadoPorAutogestion.getNumeroObservacion() == null || 
				!desechoEliminadoPorAutogestion.getNumeroObservacion().equals(declaracionGeneradorRetceBean.getNumeroObservacion())) {
			DesechoEliminacionAutogestion desechoHistorial = desechoAutogestionFacade.getDesechoEliminacionHistorial(desechoEliminadoPorAutogestion.getId(), declaracionGeneradorRetceBean.getNumeroObservacion());
			if(desechoHistorial == null){
				Boolean tieneHistorial = false;
				for (DesechoEliminacionAutogestion desechoEliminacion : listaEliminacionAutogestionOriginales) {
					if(desechoEliminacion.getIdRegistroOriginal().equals(desechoEliminadoPorAutogestion.getId())) {
						tieneHistorial = true;
						break;
					}
				}
				
				if(!tieneHistorial){
					desechoEliminacionOriginal.setId(null);
					desechoEliminacionOriginal.setHistorial(true);
					desechoEliminacionOriginal.setIdRegistroOriginal(desechoEliminadoPorAutogestion.getId());
					desechoEliminacionOriginal.setNumeroObservacion(declaracionGeneradorRetceBean.getNumeroObservacion());
					listaEliminacionAutogestionOriginales.add(desechoEliminacionOriginal);
				}
				
				
				if(eliminacion) {
					for (DesechoGeneradoEliminacion desechoGenerado : desechoEliminadoPorAutogestion.getListaDesechosGeneradosPorEliminacion()) {
						DesechoGeneradoEliminacion desechoGeneradoEliminadoOriginal = (DesechoGeneradoEliminacion) SerializationUtils.clone(desechoGenerado);
						buscarHistorialDesechoGenerado(desechoGeneradoEliminadoOriginal, desechoGenerado);
					}
				}
			}
		}
	}
	
	//desechos generados en tipo eliminacion
	public void nuevoDesechoGenerado() {
		desechoGeneradoPorEliminacion = new DesechoGeneradoEliminacion();
		desechoGeneradoSeleccionado = new DesechoPeligroso();
	}
	
	public void agregarDesechoGenerado() {
		if(desechoGeneradoPorEliminacion.getTipoDesechoGenerado() == null){
			JsfUtil.addMessageError("Debe seleccionar el tipo de desecho generado. ");
			return;
		}
		if(!desechoGeneradoPorEliminacion.getTipoDesechoGenerado().getCodigo().equals(tipoDesechoPeligroso)) 
			desechoGeneradoPorEliminacion.setDesechoPeligroso(null);
		
		if (!listaDesechosGeneradosPorEliminacion.contains(desechoGeneradoPorEliminacion))
			listaDesechosGeneradosPorEliminacion.add(desechoGeneradoPorEliminacion);
		
		if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0) {
			if(desechoGeneradoPorEliminacion.getId() != null) {
				if(!desechoGeneradoEliminacionOriginal.equalsObject(desechoGeneradoPorEliminacion)) 
					buscarHistorialDesechoGenerado(desechoGeneradoEliminacionOriginal, desechoGeneradoPorEliminacion);
			} else 
				desechoGeneradoPorEliminacion.setNumeroObservacion(declaracionGeneradorRetceBean.getNumeroObservacion());
		}
		
		desechoGeneradoEliminacionOriginal = new DesechoGeneradoEliminacion();
		
		JsfUtil.addCallbackParam("addDesechoGenerado");
	}
	
	DesechoGeneradoEliminacion desechoGeneradoEliminacionOriginal;
	public void editarDesechoGenerado(DesechoGeneradoEliminacion desechoGenerado) {
		desechoGeneradoEliminacionOriginal = (DesechoGeneradoEliminacion) SerializationUtils.clone(desechoGenerado);
		desechoGeneradoPorEliminacion = desechoGenerado;
		desechoGeneradoSeleccionado = desechoGenerado.getDesechoPeligroso();
	}

	public void eliminarDesechoGenerado(DesechoGeneradoEliminacion desechoGenerado) {
		try {
			if (desechoGenerado.getId() != null) {
				if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0) {
					DesechoGeneradoEliminacion desechoGeneradoEliminadoOriginal = (DesechoGeneradoEliminacion) SerializationUtils.clone(desechoGenerado);
					buscarHistorialDesechoGenerado(desechoGeneradoEliminadoOriginal, desechoGenerado);
				}
				
				desechoGenerado.setEstado(false);
				listaDesechosGeneradosPorEliminacionEliminar.add(desechoGenerado);				
			}
			listaDesechosGeneradosPorEliminacion.remove(desechoGenerado);
		} catch (Exception e) {

		}
	}
	
	public void verDesechosGenerados(DesechoEliminacionAutogestion desecho){
		desechoEliminadoPorAutogestion = desecho;
		listaDesechosGeneradosPorEliminacion = desecho.getListaDesechosGeneradosPorEliminacion();
	}
	
	public void buscarHistorialDesechoGenerado(DesechoGeneradoEliminacion desechoGeneradoEliminacionOriginal, DesechoGeneradoEliminacion desechoGeneradoPorEliminacion) {
		if(desechoGeneradoPorEliminacion.getNumeroObservacion() == null || 
				!desechoGeneradoPorEliminacion.getNumeroObservacion().equals(declaracionGeneradorRetceBean.getNumeroObservacion())) {
			DesechoGeneradoEliminacion desechoHistorial = desechoAutogestionFacade.getDesechoGeneradoHistorial(desechoGeneradoPorEliminacion.getId(), declaracionGeneradorRetceBean.getNumeroObservacion());
			if(desechoHistorial == null){
				Boolean tieneHistorialDesechoGenerado = false;
				for (DesechoGeneradoEliminacion desechoGenerado : listaDesechosGeneradosOriginales) {
					if(desechoGenerado.getIdRegistroOriginal().equals(desechoGeneradoPorEliminacion.getId())) {
						tieneHistorialDesechoGenerado = true;
						break;
					}
				}
				
				if(!tieneHistorialDesechoGenerado){
					desechoGeneradoEliminacionOriginal.setId(null);
					desechoGeneradoEliminacionOriginal.setHistorial(true);
					desechoGeneradoEliminacionOriginal.setIdRegistroOriginal(desechoGeneradoPorEliminacion.getId());
					desechoGeneradoEliminacionOriginal.setNumeroObservacion(declaracionGeneradorRetceBean.getNumeroObservacion());
					listaDesechosGeneradosOriginales.add(desechoGeneradoEliminacionOriginal);
				}
			}
		}
	}
	
	//desechos que se realiza autogestion
	public void agregarEliminacionAutogestion() {
		
		if(desechoSeleccionado == null){
			JsfUtil.addMessageError("Debe seleccionar un desecho. ");
			return;
		}
		
		Double totalToneladas = 0.0;
		for (DesechoEliminacionAutogestion desechoEliminacion : listaDesechosEliminacionAutogestion) {
			totalToneladas += declaracionGeneradorRetceBean.getCantidadToneladas(desechoEliminacion.getCantidad(), desechoEliminacion.getTipoUnidad().getId());
		}
		
		desechoAutogestion.setDesechoPeligroso(desechoSeleccionado);
		desechoAutogestion.setListaDesechosEliminacionAutogestion(listaDesechosEliminacionAutogestion);
		desechoAutogestion.setListaSustanciasRetce(listaSustanciasDesecho);
		desechoAutogestion.setTotalToneladas(totalToneladas);
		
		listaDesechosAutogestion.add(desechoAutogestion);
		listaTotalDesechosGeneradosOriginales.addAll(listaDesechosGeneradosOriginales);
		listaTotalEliminacionAutogestionOriginales.addAll(listaEliminacionAutogestionOriginales);
		listaTotalSustanciasOriginales.addAll(listaSustanciasDesechoOriginales);
		listaTotalLaboratoriosOriginales.addAll(listaLaboratoriosOriginales);
		listaTotalInformesOriginales.addAll(listaInformesOriginales);
		
		verificarCambios();
		
		if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0) {
			if(desechoAutogestion.getId() != null) {
				if(!desechoAutogestionOriginal.getTotalToneladas().equals(desechoAutogestion.getTotalToneladas()))
					buscarHistorialDesechoAutogestion(desechoAutogestionOriginal, desechoAutogestion, false);
			} else 
				desechoGeneradoPorEliminacion.setNumeroObservacion(declaracionGeneradorRetceBean.getNumeroObservacion());
		}
		
		desechoSeleccionado = null;
		JsfUtil.getBean(TipoEliminacionDesechoBean.class).resetSelection();
		desechoAutogestion = new DesechoAutogestion();
		listaDesechosEliminacionAutogestion = new ArrayList<>();
		listaSustanciasDesecho = new ArrayList<>();
		listaDesechosGeneradosOriginales = new ArrayList<>();
		listaEliminacionAutogestionOriginales = new ArrayList<>();
		listaSustanciasDesechoOriginales = new ArrayList<>();
		listaLaboratoriosOriginales = new ArrayList<>();
		listaInformesOriginales = new ArrayList<>();
		desechoAutogestionOriginal = null;
		
		JsfUtil.addCallbackParam("addAutogestion");
	}
	
	private DesechoAutogestion desechoAutogestionOriginal;
	public void editarDesechoAutogestion(DesechoAutogestion desechoGestion){
		desechoAutogestionOriginal = (DesechoAutogestion) SerializationUtils.clone(desechoGestion);
		desechoAutogestion = desechoGestion;
		
		desechoSeleccionado = desechoGestion.getDesechoPeligroso();
		
		listaDesechosEliminacionAutogestion = desechoGestion.getListaDesechosEliminacionAutogestion();
		listaSustanciasDesecho = desechoGestion.getListaSustanciasRetce();
		
		editar = true;
		listaDesechosAutogestion.remove(desechoGestion);
	}
	
	public void cancelar() {
		if(editar)
			listaDesechosAutogestion.add(desechoAutogestionOriginal);
		
		desechoSeleccionado = null;
		JsfUtil.getBean(TipoEliminacionDesechoBean.class).resetSelection();
		desechoAutogestion = new DesechoAutogestion();
		listaDesechosEliminacionAutogestion = new ArrayList<>();
		listaSustanciasDesecho = new ArrayList<>();
		desechoAutogestionOriginal = null;
		
		listaDesechosGeneradosOriginales = new ArrayList<>();
		listaEliminacionAutogestionOriginales = new ArrayList<>();
		listaSustanciasDesechoOriginales = new ArrayList<>();
		listaLaboratoriosOriginales = new ArrayList<>();
		listaInformesOriginales = new ArrayList<>();
	}
	
	public void eliminarDesechoAutogestion(DesechoAutogestion desechoGestion){
		try {
			if (desechoGestion.getId() != null) {
				if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0) {
					getHistorialDesechoEliminado(desechoGestion);
				}
				
				desechoGestion.setEstado(false);
				listaDesechosAutogestionEliminar.add(desechoGestion);
			}
			
			listaDesechosAutogestion.remove(desechoGestion);
			
		} catch (Exception e) {

		}
	}
	
	public void getHistorialDesechoEliminado(DesechoAutogestion desechoGestion) {
		DesechoAutogestion desechoOriginal = (DesechoAutogestion) SerializationUtils.clone(desechoGestion);
		buscarHistorialDesechoAutogestion(desechoOriginal, desechoGestion, true);
		
		listaTotalDesechosGeneradosOriginales.addAll(listaDesechosGeneradosOriginales);
		listaTotalEliminacionAutogestionOriginales.addAll(listaEliminacionAutogestionOriginales);
		listaTotalSustanciasOriginales.addAll(listaSustanciasDesechoOriginales);
		listaTotalLaboratoriosOriginales.addAll(listaLaboratoriosOriginales);
		
		listaDesechosGeneradosOriginales = new ArrayList<>();
		listaEliminacionAutogestionOriginales = new ArrayList<>();
		listaSustanciasDesechoOriginales = new ArrayList<>();
		listaLaboratoriosOriginales = new ArrayList<>();
	}
	
	public void buscarHistorialDesechoAutogestion(DesechoAutogestion desechoAutogestionOriginal, DesechoAutogestion desechoAutogestion, Boolean eliminacion) {
		if(desechoAutogestion.getNumeroObservacion() == null || 
				!desechoAutogestion.getNumeroObservacion().equals(declaracionGeneradorRetceBean.getNumeroObservacion())) {
			DesechoAutogestion desechoHistorial = desechoAutogestionFacade.getDesechoAutogestionHistorial(desechoAutogestion.getId(), declaracionGeneradorRetceBean.getNumeroObservacion());
			if(desechoHistorial == null){
				Boolean tieneHistorial = false;
				for (DesechoAutogestion desechoEliminacion : listaDesechosAutogestionOriginales) {
					if(desechoEliminacion.getIdRegistroOriginal().equals(desechoAutogestion.getId())) {
						tieneHistorial = true;
						break;
					}
				}
				
				if(!tieneHistorial) { 
					desechoAutogestionOriginal.setId(null);
					desechoAutogestionOriginal.setHistorial(true);
					desechoAutogestionOriginal.setIdRegistroOriginal(desechoAutogestion.getId());
					desechoAutogestionOriginal.setNumeroObservacion(declaracionGeneradorRetceBean.getNumeroObservacion());
					listaDesechosAutogestionOriginales.add(desechoAutogestionOriginal);
				}
				
				if(eliminacion) {
					for (DesechoEliminacionAutogestion desechoPeligroso : desechoAutogestion.getListaDesechosEliminacionAutogestion()) {
						DesechoEliminacionAutogestion desechoEliminacionOriginal = (DesechoEliminacionAutogestion) SerializationUtils.clone(desechoPeligroso);
						buscarHistorialTipoEliminacion(desechoEliminacionOriginal, desechoPeligroso, true);
						
						
					}
					
					for (SubstanciasRetce sustancia : desechoAutogestion.getListaSustanciasRetce()) {
						SubstanciasRetce sustanciaOriginal = (SubstanciasRetce) SerializationUtils.clone(sustancia);
						buscarHistorialSustanciaRetce(sustanciaOriginal, sustancia, true);
					}
				}
			}
		}
	}

	public void aceptar() {
		JsfUtil.addCallbackParam("addTipoEliminacion");
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
	
	public void validarCantidadEliminada() {
		if (desechoEliminadoPorAutogestion.getTipoUnidad() != null && 
				desechoEliminadoPorAutogestion.getCantidad() != null) {
			Boolean permitir = declaracionGeneradorRetceBean
					.validarCantidadReporteDesecho(
							desechoEliminadoPorAutogestion.getCantidad(),
							desechoSeleccionado.getId(),
							desechoEliminadoPorAutogestion.getTipoUnidad().getId());

			if (!permitir) {
				desechoEliminadoPorAutogestion.setCantidad(null);
			}
		}
	}
	
	public void validateAutogestion(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = validateData();
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public List<FacesMessage> validateData() {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		GeneradorDesechosPeligrososRetce generadorDesechosRetce = declaracionGeneradorRetceBean.getGeneradorDesechosRetce();
		if (generadorDesechosRetce.getRealizaAutogestion() != null && 
				generadorDesechosRetce.getRealizaAutogestion()) {
			if (documentoAutorizacion.getId() == null && documentoAutorizacion.getContenidoDocumento() == null)
				errorMessages.add(new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								"Debe adjuntar el documento de la autorización en formato .PDF tamaño permitido 20Mb.", null));
			
			if(listaDesechosAutogestion.size() == 0)
				errorMessages.add(new FacesMessage(
						FacesMessage.SEVERITY_ERROR,
						"Debe ingresar la información de autogestión en uno o varios desechos.", null));
		}
		
		return errorMessages;
	}

	public Boolean guardarAutogestionDesechos() {
		try{
			
			if(generadorDesechosRetce.getRealizaAutogestion()){
				for (DesechoAutogestion desechoAutogestion : listaDesechosAutogestion) {
					desechoAutogestion.setIdGeneradorRetce(generadorDesechosRetce.getId());
					List<DesechoEliminacionAutogestion> listaDesechosEliminacion = desechoAutogestion.getListaDesechosEliminacionAutogestion();
					List<SubstanciasRetce> listaSustanciasDesecho = desechoAutogestion.getListaSustanciasRetce();
					
					desechoAutogestionFacade.guardarDesechoAutogestion(desechoAutogestion);
					
					for (DesechoEliminacionAutogestion desechoEliminacionAutogestion : listaDesechosEliminacion) {
						List<DesechoGeneradoEliminacion> listaDesechosGenerados = desechoEliminacionAutogestion.getListaDesechosGeneradosPorEliminacion();
						
						desechoEliminacionAutogestion.setIdDesechoAutogestion(desechoAutogestion.getId());
						desechoAutogestionFacade.guardarDesechoEliminacionAutogestion(desechoEliminacionAutogestion);
						
						if(listaDesechosGenerados != null && listaDesechosGenerados.size() > 0){
							for (DesechoGeneradoEliminacion desechoGeneradoEliminacion : listaDesechosGenerados) {
								desechoGeneradoEliminacion.setIdDesechoEliminacionAutogestion(desechoEliminacionAutogestion.getId());
							}
							
							desechoAutogestionFacade.guardarDesechoGeneradoEliminacionAutogestion(listaDesechosGenerados);
							
							desechoEliminacionAutogestion.setListaDesechosGeneradosPorEliminacion(listaDesechosGenerados);
						}
					}
					
					desechoAutogestion.setListaDesechosEliminacionAutogestion(listaDesechosEliminacion);
					
					for (SubstanciasRetce substanciasRetce : listaSustanciasDesecho) {
						substanciasRetce.setIdDesechoAutogestion(desechoAutogestion.getId());
						
						Boolean saveLaboratorio = false;
						DatosLaboratorio lab = new DatosLaboratorio();
						if(substanciasRetce.getDatosLaboratorio() != null) {
							DatosLaboratorio laboratorio = substanciasRetce.getDatosLaboratorio();
							Documento documentoInforme = laboratorio.getDocumentoLaboratorio();
							lab = datosLaboratorioFacade.saveLaboratorioEmisiones(laboratorio, loginBean.getUsuario());
							substanciasRetce.setDatosLaboratorio(lab);
							saveLaboratorio = true;
							
							if(documentoInforme != null && documentoInforme.getContenidoDocumento()!=null){
								documentoInforme.setIdTable(laboratorio.getId());
								documentoInforme.setNombreTabla(GeneradorDesechosPeligrososRetce.class.getSimpleName());
								documentoInforme.setDescripcion("Informe de monitoreo emitido por el laboratorio");
								documentoInforme.setEstado(true);               
								Documento documentoInformeSave = documentosFacade.guardarDocumentoAlfrescoSinProyecto(
												generadorDesechosRetce.getCodigoGenerador(),
												"GENERADOR_DESECHOS",
												laboratorio.getId().longValue(),
												documentoInforme,
												TipoDocumentoSistema.TIPO_DOCUMENTO_INFORME_LABORATORIO,
												null);
								
								lab.setDocumentoLaboratorio(documentoInformeSave);
								
								for (Documento documento : listaTotalInformesOriginales) {
									if(documento.getIdTable().equals(laboratorio.getId())){
										documento.setIdHistorico(documentoInformeSave.getId());
										documento.setNumeroNotificacion(declaracionGeneradorRetceBean.getNumeroObservacion());
										documentosFacade.actualizarDocumento(documento);
									}
								}
					        }
						}
						
						substanciasRetceFacade.save(substanciasRetce, loginBean.getUsuario());
						
						if(saveLaboratorio)
							substanciasRetce.setDatosLaboratorio(lab);
					}
					
					desechoAutogestion.setListaSustanciasRetce(listaSustanciasDesecho);
				}
				
				if(documentoAutorizacion.getContenidoDocumento()!=null){
					documentoAutorizacion.setIdTable(generadorDesechosRetce.getId());
					documentoAutorizacion.setNombreTabla(GeneradorDesechosPeligrososRetce.class.getSimpleName());
					documentoAutorizacion.setDescripcion("Autorización autogestión");
					documentoAutorizacion.setEstado(true);               
					Documento documentoSave = documentosFacade.guardarDocumentoAlfrescoSinProyecto(
									generadorDesechosRetce.getCodigoGenerador(),
									"GENERADOR_DESECHOS",
									generadorDesechosRetce.getId().longValue(),
									documentoAutorizacion,
									TipoDocumentoSistema.DOCUMENTO_AUTORIZACION_AUTOGESTION,
									null);
					if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0 && documentoAutorizacionOriginal != null)
						documentoAutorizacionOriginal.setIdHistorico(documentoSave.getId());
		        }
			} else {
				if (documentoAutorizacion != null && documentoAutorizacion.getIdAlfresco() != null) {
					documentoAutorizacion.setEstado(false);
					documentosFacade.eliminarDocumento(documentoAutorizacion);
				}
			}
			
			if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0) {
				guardarHistorial();
			}
			
			generadorDesechosPeligrososFacade.guardarRgdRetce(generadorDesechosRetce);
			
			if(listaDesechosAutogestionEliminar.size() > 0){
				desechoAutogestionFacade.eliminarDesechosAutogestion(listaDesechosAutogestionEliminar);
			}
			
			if(listaDesechosEliminadosPorAutogestionEliminar.size() > 0){
				desechoAutogestionFacade.eliminarDesechosEliminacionAutogestion(listaDesechosEliminadosPorAutogestionEliminar);
			}
			
			if(listaDesechosGeneradosPorEliminacionEliminar.size() > 0)
				desechoAutogestionFacade.eliminarDesechosGenerados(listaDesechosGeneradosPorEliminacionEliminar);
			
			listaDesechosAutogestionEliminar = new ArrayList<>();
			listaDesechosEliminadosPorAutogestionEliminar = new ArrayList<>();
			listaDesechosGeneradosPorEliminacionEliminar = new ArrayList<>();
			listaTotalInformesOriginales = new ArrayList<>();
			
			declaracionGeneradorRetceBean.eliminarDatosAsociados();
			
			return true;
			
		} catch (Exception e) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
            return false;
        }
	}
	
	public List<DesechoPeligroso> getDesechosPeligrososSinAutogestion(){
		List<DesechoPeligroso> result = new ArrayList<>();		
		
		List<IdentificacionDesecho> listaIdentificacionDesechos = JsfUtil.getBean(IdentificacionDesechosBean.class).getListaIdentificacionDesechos();
		for (IdentificacionDesecho identificacionDesecho : listaIdentificacionDesechos) {
			if(identificacionDesecho.getCantidadTotalToneladas() > 0){
				DesechoAutogestion desechoAutogestion = getDesechoAutogestion(identificacionDesecho.getDesechoPeligroso());
				
				if (desechoAutogestion != null) {
					if(desechoAutogestion.getTotalToneladas() < identificacionDesecho.getCantidadTotalToneladas()){
						result.add(identificacionDesecho.getDesechoPeligroso());
					}
				} else {
					result.add(identificacionDesecho.getDesechoPeligroso());
				}
			}
		}
		
		return result;
	}
	
	public List<DesechoPeligroso> getDesechosNoAutogestion(){
		List<DesechoPeligroso> result = new ArrayList<>();		
		
		List<IdentificacionDesecho> listaIdentificacionDesechos = JsfUtil.getBean(IdentificacionDesechosBean.class).getListaIdentificacionDesechos();
		for (IdentificacionDesecho identificacionDesecho : listaIdentificacionDesechos) {
			if(identificacionDesecho.getCantidadTotalToneladas() > 0){
				DesechoAutogestion desechoAutogestion = getDesechoAutogestion(identificacionDesecho.getDesechoPeligroso());
				
				if (desechoAutogestion == null) {
					result.add(identificacionDesecho.getDesechoPeligroso());
				}
			}
		}
		
		return result;
	}
	
	public DesechoAutogestion getDesechoAutogestion(DesechoPeligroso desecho){		
		for (DesechoAutogestion desechoAutogestion : listaDesechosAutogestion) {
			if(desecho.getId().equals(desechoAutogestion.getDesechoPeligroso().getId())){
				return desechoAutogestion;
			}
		}
		
		return null;
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
	
	public void nuevaSustancia() {
		editarSustancia = false;

		sustanciaRetce = new SubstanciasRetce();
		sustanciaRetce.setDatosLaboratorio(new DatosLaboratorio());
		informeMonitoreo = new Documento();
	}	
	
	public void agregarSustancia() {
		if(sustanciaRetce.getCatalogoMetodoEstimacion().getOrden().equals(ordenMedicionDirecta)){
			sustanciaRetce.getDatosLaboratorio().setDocumentoLaboratorio(informeMonitoreo);
		} else 
			sustanciaRetce.setDatosLaboratorio(null);
		
		if (!listaSustanciasDesecho.contains(sustanciaRetce))
			listaSustanciasDesecho.add(sustanciaRetce);
		
		if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0) {
			if(sustanciaRetce.getId() != null) {
				if(!equalsSustancia(substanciasRetceOriginal, sustanciaRetce))
					buscarHistorialSustanciaRetce(substanciasRetceOriginal, sustanciaRetce, false);
			} else 
				sustanciaRetce.setNumeroObservacion(declaracionGeneradorRetceBean.getNumeroObservacion());
			
			if(sustanciaRetce.getDatosLaboratorio() != null){
				if(sustanciaRetce.getDatosLaboratorio().getId() != null){
					//buscar historial de laboratorio
					if(!equalsLaboratorio(datosLaboratorioOriginal, sustanciaRetce.getDatosLaboratorio()))
						buscarHistorialLaboratorio(datosLaboratorioOriginal, sustanciaRetce.getDatosLaboratorio());
				} else {
					sustanciaRetce.getDatosLaboratorio().setNumeroObservacion(declaracionGeneradorRetceBean.getNumeroObservacion());
					sustanciaRetce.getDatosLaboratorio().getDocumentoLaboratorio().setNumeroNotificacion(declaracionGeneradorRetceBean.getNumeroObservacion());
				}
			} 
			
			if(informeMonitoreoOriginal != null)
				listaInformesOriginales.add(informeMonitoreoOriginal);
		}

		informeMonitoreoOriginal = null;
		sustanciaRetce = new SubstanciasRetce();
		substanciasRetceOriginal = new SubstanciasRetce();
		
		JsfUtil.addCallbackParam("addSustancia");
	}
	
	public boolean equalsSustancia(Object objOriginal, Object obj) {
  		if (obj == null)
  			return false;
  		SubstanciasRetce base = (SubstanciasRetce) obj;
  		SubstanciasRetce original = (SubstanciasRetce) objOriginal;
  		if (original.getId() == null && base.getId() == null)
  			return super.equals(obj);
  		if (original.getId() == null || base.getId() == null)
  			return false;
  		
  		if (original.getId().equals(base.getId())
  				&& original.getCatologSustanciasRetce().getId().equals(base.getCatologSustanciasRetce().getId())
  				&& original.getReporteToneladaAnio().equals(base.getReporteToneladaAnio())
  				&& original.getCatalogoMetodoEstimacion().getId().equals(base.getCatalogoMetodoEstimacion().getId())
  			)
  			return true;
  		else
  			return false;
  	}
	
	public boolean equalsLaboratorio(Object objOriginal, Object obj) {
  		if (obj == null)
  			return false;
  		DatosLaboratorio base = (DatosLaboratorio) obj;
  		DatosLaboratorio original = (DatosLaboratorio) objOriginal;
  		if (original.getId() == null && base.getId() == null)
  			return super.equals(obj);
  		if (original.getId() == null || base.getId() == null)
  			return false;
  		
  		if (original.getId().equals(base.getId())
  				&& original.getRuc().equals(base.getRuc())
  				&& original.getNumeroRegistroSAE().equals(base.getNumeroRegistroSAE())
  				&& original.getFechaVigenciaRegistro().equals(base.getFechaVigenciaRegistro())
  			)
  			return true;
  		else
  			return false;
  	}
	
	private	SubstanciasRetce substanciasRetceOriginal = new SubstanciasRetce();
	private DatosLaboratorio datosLaboratorioOriginal;
	public void editarSustancia(SubstanciasRetce sustancia){
		substanciasRetceOriginal = (SubstanciasRetce) SerializationUtils.clone(sustancia);
		sustanciaRetce = sustancia;
		
		if(sustanciaRetce.getCatalogoMetodoEstimacion().getOrden().equals(ordenMedicionDirecta)){
			informeMonitoreo = sustancia.getDatosLaboratorio().getDocumentoLaboratorio();
			
			if(informeMonitoreo == null && sustanciaRetce.getId() != null){				
				List<Documento> documentosManifiesto = documentosFacade.documentoXTablaIdXIdDoc(
						sustancia.getDatosLaboratorio().getId(),
						GeneradorDesechosPeligrososRetce.class.getSimpleName(),
						TipoDocumentoSistema.TIPO_DOCUMENTO_INFORME_LABORATORIO);
				if (documentosManifiesto.size() > 0) {
					informeMonitoreo = documentosManifiesto.get(0);
					informeMonitoreoOriginal = (Documento) SerializationUtils.clone(documentosManifiesto.get(0));
				}
			}
			
			datosLaboratorioOriginal = (DatosLaboratorio) SerializationUtils.clone(sustancia.getDatosLaboratorio());
		} 
		
		editarSustancia = true;
		listaSustanciasDesecho.remove(sustancia);
		
		try {
			JsfUtil.addCallbackParam("addSustancia");
		} catch (Exception e) {
			JsfUtil.addMessageError("Error al cargar la información");
		}
	}
	
	public void eliminarSustancia(SubstanciasRetce sustancia){
		try {
			if (sustancia.getId() != null) {
				if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0) {
					SubstanciasRetce sustanciaOriginal = (SubstanciasRetce) SerializationUtils.clone(sustancia);
					buscarHistorialSustanciaRetce(sustanciaOriginal, sustancia, true);
				}
				
				sustancia.setEstado(false);
				listaSustanciasDesechoEliminar.add(sustancia);
			}
			listaSustanciasDesecho.remove(sustancia);
		} catch (Exception e) {

		}
	}
	
	public void cancelarSustancia(){
		if(editarSustancia)
			listaSustanciasDesecho.add(substanciasRetceOriginal);
		
		informeMonitoreoOriginal = null;
		sustanciaRetce = new SubstanciasRetce();
		substanciasRetceOriginal = new SubstanciasRetce();
	}
	
	public void cambiarEstimacion() {
		if(sustanciaRetce.getCatalogoMetodoEstimacion().getOrden().equals(ordenMedicionDirecta)) {
			sustanciaRetce.setDatosLaboratorio(new DatosLaboratorio());
		} else 
			sustanciaRetce.setDatosLaboratorio(null);
	}
	
	public void verDatosLaboratorio(SubstanciasRetce sustancia){
		sustanciaRetce = sustancia;
		
		List<Documento> documentosManifiesto = documentosFacade.documentoXTablaIdXIdDoc(
				sustancia.getDatosLaboratorio().getId(),
				GeneradorDesechosPeligrososRetce.class.getSimpleName(),
				TipoDocumentoSistema.TIPO_DOCUMENTO_INFORME_LABORATORIO);
		if (documentosManifiesto.size() > 0)
			sustanciaRetce.getDatosLaboratorio().setDocumentoLaboratorio(documentosManifiesto.get(0));
	}
	
	public void buscarHistorialSustanciaRetce(SubstanciasRetce sustanciaRetceOriginal, SubstanciasRetce sustanciaRetce, Boolean eliminacion) {
		if(sustanciaRetce.getNumeroObservacion() == null || 
				!sustanciaRetce.getNumeroObservacion().equals(declaracionGeneradorRetceBean.getNumeroObservacion())) {
			SubstanciasRetce desechoHistorial = substanciasRetceFacade.getSustanciaHistorialPorID(sustanciaRetce.getId(), declaracionGeneradorRetceBean.getNumeroObservacion());
			if(desechoHistorial == null){
				Boolean tieneHistorial = false;
				for (SubstanciasRetce sustancia : listaSustanciasDesechoOriginales) {
					if(sustancia.getIdRegistroOriginal().equals(sustanciaRetce.getId())) {
						tieneHistorial = true;
						break;
					}
				}
				
				if(!tieneHistorial){
					sustanciaRetceOriginal.setId(null);
					sustanciaRetceOriginal.setHistorial(true);
					sustanciaRetceOriginal.setIdRegistroOriginal(sustanciaRetce.getId());
					sustanciaRetceOriginal.setNumeroObservacion(declaracionGeneradorRetceBean.getNumeroObservacion());
					listaSustanciasDesechoOriginales.add(sustanciaRetceOriginal);
				}
				
				if(eliminacion) {
					if(sustanciaRetce.getDatosLaboratorio() != null){
						if(sustanciaRetce.getDatosLaboratorio().getId() != null){
							DatosLaboratorio laboratorioOriginal = (DatosLaboratorio) SerializationUtils.clone(sustanciaRetce.getDatosLaboratorio());
							buscarHistorialLaboratorio(laboratorioOriginal, sustanciaRetce.getDatosLaboratorio());
						}
					}
				}
			}
		}
	}
	
	public void buscarHistorialLaboratorio(DatosLaboratorio laboratorioOriginal, DatosLaboratorio datosLaboratorio) {
		if(datosLaboratorio.getNumeroObservacion() == null || 
				!datosLaboratorio.getNumeroObservacion().equals(declaracionGeneradorRetceBean.getNumeroObservacion())) {
			DatosLaboratorio laboratorioHistorial = datosLaboratorioFacade.getLaboratorioHistorialPorID(datosLaboratorio.getId(), declaracionGeneradorRetceBean.getNumeroObservacion());
			if(laboratorioHistorial == null){
				Boolean tieneHistorial = false;
				for (DatosLaboratorio laboratorio : listaLaboratoriosOriginales) {
					if(laboratorio.getIdRegistroOriginal().equals(datosLaboratorio.getId())) {
						tieneHistorial = true;
						break;
					}
				}
				
				if(!tieneHistorial){
					laboratorioOriginal.setId(null);
					laboratorioOriginal.setHistorial(true);
					laboratorioOriginal.setIdRegistroOriginal(datosLaboratorio.getId());
					laboratorioOriginal.setNumeroObservacion(declaracionGeneradorRetceBean.getNumeroObservacion());
					listaLaboratoriosOriginales.add(laboratorioOriginal);
				}
			}
		}
	}
	
	public void validarCedula() {
        try {           
            if (sustanciaRetce.getDatosLaboratorio().getRuc() != null
                    && !sustanciaRetce.getDatosLaboratorio().getRuc().isEmpty()) {

                if (sustanciaRetce.getDatosLaboratorio().getRuc().length() == 13) {
                    ContribuyenteCompleto contribuyenteCompleto = consultaRucCedula
                            .obtenerPorRucSRI(Constantes.USUARIO_WS_MAE_SRI_RC,
                                    Constantes.PASSWORD_WS_MAE_SRI_RC,
                                    sustanciaRetce.getDatosLaboratorio().getRuc());
                    if (contribuyenteCompleto == null) {
                        JsfUtil.addMessageError("RUC inválido");
                        return;
                    }
                    if (!contribuyenteCompleto.getCodEstado().equals("PAS")
                            || Constantes.getPermitirRUCPasivo()) {
                        cargarDatosWsRucPersonaNatural(contribuyenteCompleto);
                    } else {
                        JsfUtil.addMessageError("El estado de su RUC es PASIVO. Si desea registrarse con el mismo debe activarlo en el SRI.");
                    }
                } else {
                    JsfUtil.addMessageError("El RUC debe tener 13 dígitos.");
                }

            } else {
                JsfUtil.addMessageError("El campo 'Cédula / RUC / Pasaporte' es requerido.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	private void cargarDatosWsRucPersonaNatural(
            ContribuyenteCompleto contribuyenteCompleto) {
        if (contribuyenteCompleto != null) {
            if (contribuyenteCompleto.getRazonSocial() != null) {               
            	sustanciaRetce.getDatosLaboratorio().setNombre(contribuyenteCompleto.getRazonSocial());

            } else {
                JsfUtil.addMessageError("RUC no encontrado.");
            }
        } else {
            JsfUtil.addMessageError("Sin Servicio");
        }
    }

	public void uploadInformeMonitoreo(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		informeMonitoreo = new Documento();
		informeMonitoreo.setId(null);
		informeMonitoreo.setContenidoDocumento(contenidoDocumento);
		informeMonitoreo.setNombre(event.getFile().getFileName());
		informeMonitoreo.setMime("application/pdf");
		informeMonitoreo.setExtesion(".pdf");
				
		JsfUtil.addMessageInfo("Documento subido exitosamente");
	}
	
	public void validateLaboratorio(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		if(sustanciaRetce.getCatalogoMetodoEstimacion() != null && sustanciaRetce.getCatalogoMetodoEstimacion().getOrden().equals(ordenMedicionDirecta)){
			if (informeMonitoreo.getId() == null && informeMonitoreo.getContenidoDocumento() == null)
				errorMessages.add(new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								"Debe adjuntar el informe de laboratorio en formato .PDF tamaño permitido 20Mb.", null));
			
		}
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public void limpiarInfoDesecho() {
		desechoAutogestion = new DesechoAutogestion();
		
		listaDesechosEliminacionAutogestion = new ArrayList<>();
		listaSustanciasDesecho = new ArrayList<>();
	}
	
	public void actualizarRealizaAutogestion() {
		if(!generadorDesechosRetce.getRealizaAutogestion()) {
			if(listaDesechosAutogestion.size() > 0){
				for (DesechoAutogestion desechoAutogestion : listaDesechosAutogestion) {
					if(desechoAutogestion.getId() != null && !listaDesechosAutogestionEliminar.contains(desechoAutogestion)) {
//						listaDesechosAutogestionEliminar.add(desechoAutogestion);
						if(declaracionGeneradorRetceBean.getNumeroObservacion() > 0) {
							getHistorialDesechoEliminado(desechoAutogestion);
						}
						
						desechoAutogestion.setEstado(false);
						listaDesechosAutogestionEliminar.add(desechoAutogestion);
					}
				}
			}
			
			generadorDesechosRetce.setNumeroAutorizacion(null);
		} else {
			listaDesechosAutogestionEliminar = new ArrayList<>();
			cargarDatos();
		}
	}
	
	public void verificarCambios() {
		if (desechoAutogestion != null) {
			if (desechoAutogestionOriginal != null) {
				if(desechoAutogestionOriginal.getTotalToneladas().equals(desechoAutogestion.getTotalToneladas()))
					return;
			} 
			
			List<IdentificacionDesecho> listaIdentificacionDesechos = JsfUtil.getBean(IdentificacionDesechosBean.class).getListaIdentificacionDesechos();
			for (IdentificacionDesecho identificacionDesecho : listaIdentificacionDesechos) {
				if (identificacionDesecho.getDesechoPeligroso().getId().equals(desechoAutogestion.getDesechoPeligroso().getId())) {
					if (desechoAutogestion.getTotalToneladas() >= identificacionDesecho.getCantidadTotalToneladas()) {
						declaracionGeneradorRetceBean.validarDatosRelacionados(2, desechoAutogestion.getDesechoPeligroso().getId());
					} else {
						declaracionGeneradorRetceBean.validarDatosRelacionados(10, desechoAutogestion.getDesechoPeligroso().getId());
					}
				}
			}
		}
	}
	
	
	public void guardarHistorial() {
		GeneradorDesechosPeligrososRetce generadorDesechosOriginal = generadorDesechosPeligrososFacade.getRgdRetcePorID(generadorDesechosRetce.getId());
		if(generadorDesechosRetceHistorial == null 
				&& (!generadorDesechosOriginal.getRealizaAutogestion().equals(generadorDesechosRetce.getRealizaAutogestion()) ||
						(generadorDesechosOriginal.getNumeroAutorizacion() != null && generadorDesechosRetce.getNumeroAutorizacion() != null && 
								!generadorDesechosOriginal.getNumeroAutorizacion().equals(generadorDesechosRetce.getNumeroAutorizacion())) ||
						(generadorDesechosOriginal.getNumeroAutorizacion() != null && generadorDesechosRetce.getNumeroAutorizacion() == null) ||
						(generadorDesechosOriginal.getNumeroAutorizacion() == null && generadorDesechosRetce.getNumeroAutorizacion() != null)
						)){
			generadorDesechosOriginal.setId(null);
			generadorDesechosOriginal.setHistorial(true);
			generadorDesechosOriginal.setIdRegistroOriginal(generadorDesechosRetce.getId());
			generadorDesechosOriginal.setNumeroObservacion(declaracionGeneradorRetceBean.getNumeroObservacion());
			
			generadorDesechosPeligrososFacade.guardarRgdRetce(generadorDesechosOriginal);
		}
		
		if(listaDesechosAutogestionOriginales.size() > 0)
			desechoAutogestionFacade.guardarDesechosAutogestion(listaDesechosAutogestionOriginales);
		
		if(listaTotalDesechosGeneradosOriginales.size() > 0){
			desechoAutogestionFacade.guardarDesechoGeneradoEliminacionAutogestion(listaTotalDesechosGeneradosOriginales); 
		}
		
		if(listaTotalEliminacionAutogestionOriginales.size() > 0)
			desechoAutogestionFacade.guardarDesechosEliminacionAutogestion(listaTotalEliminacionAutogestionOriginales);
		
		if(listaTotalSustanciasOriginales.size() >0)
			substanciasRetceFacade.guardarSustancias(listaTotalSustanciasOriginales);
		
		if(listaTotalLaboratoriosOriginales.size() > 0)
			datosLaboratorioFacade.guardarLaboratorios(listaTotalLaboratoriosOriginales);
		
		if(documentoAutorizacionOriginal != null && 
				documentoAutorizacionOriginal.getIdHistorico() != null){
			documentoAutorizacionOriginal.setNumeroNotificacion(declaracionGeneradorRetceBean.getNumeroObservacion());
			documentosFacade.actualizarDocumento(documentoAutorizacionOriginal);
		}
		
		listaTotalDesechosGeneradosOriginales = new ArrayList<>();
		listaTotalEliminacionAutogestionOriginales = new ArrayList<>();
		listaTotalSustanciasOriginales = new ArrayList<>();
		listaTotalLaboratoriosOriginales = new ArrayList<>();
		documentoAutorizacionOriginal = null;
	}
}
