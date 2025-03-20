package ec.gob.ambiente.control.retce.controllers;

import index.Intersecado_capa;
import index.Intersecado_entrada;
import index.Intersecado_resultado;
import index.Reproyeccion_entrada;
import index.Reproyeccion_resultado;
import index.SVA_Reproyeccion_IntersecadoPortTypeProxy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
import ec.gob.ambiente.cliente.ConsultaRucCedula;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.retce.model.CatalogoMetodoEstimacion;
import ec.gob.ambiente.retce.model.CatalogoSustanciasRetce;
import ec.gob.ambiente.retce.model.ConsumoAgua;
import ec.gob.ambiente.retce.model.ConsumoCombustible;
import ec.gob.ambiente.retce.model.ConsumoEnergia;
import ec.gob.ambiente.retce.model.ConsumoRecursos;
import ec.gob.ambiente.retce.model.DatosLaboratorio;
import ec.gob.ambiente.retce.model.DetalleCatalogoGeneral;
import ec.gob.ambiente.retce.model.GeneradorDesechosPeligrososRetce;
import ec.gob.ambiente.retce.model.InformacionProyecto;
import ec.gob.ambiente.retce.model.SubstanciasRetce;
import ec.gob.ambiente.retce.model.TecnicoResponsable;
import ec.gob.ambiente.retce.model.TipoProcesoConsumo;
import ec.gob.ambiente.retce.services.CatalogoMetodoEstimacionFacade;
import ec.gob.ambiente.retce.services.CatalogoSustanciasRetceFacade;
import ec.gob.ambiente.retce.services.ConsumoRecursosFacade;
import ec.gob.ambiente.retce.services.DatosLaboratorioFacade;
import ec.gob.ambiente.retce.services.DetalleCatalogoGeneralFacade;
import ec.gob.ambiente.retce.services.InformacionProyectoFacade;
import ec.gob.ambiente.retce.services.SubstanciasRetceFacade;
import ec.gob.ambiente.retce.services.TecnicoResponsableFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.comun.bean.WizardBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gov.sri.wsconsultacontribuyente.ContribuyenteCompleto;

@ManagedBean
@ViewScoped
public class ConsumoRecursosController implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private final ConsultaRucCedula consultaRucCedula = new ConsultaRucCedula(Constantes.getUrlWsRegistroCivilSri());
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
	
	@ManagedProperty(value = "#{wizardBean}")
    @Getter
    @Setter
    private WizardBean wizardBean;
	
	@EJB
	private DetalleCatalogoGeneralFacade detalleCatalogoGeneralFacade;
	
	@EJB
    private TecnicoResponsableFacade tecnicoResponsableFacade;
	
	@EJB
	private DocumentosFacade documentosFacade;
	
	@EJB
	private InformacionProyectoFacade informacionProyectoFacade;
	
	@EJB
	private ConsumoRecursosFacade consumoRecursosFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private OrganizacionFacade organizacionFacade;
	
	@EJB
	private CatalogoSustanciasRetceFacade catalogoSustanciasRetceFacade;
	
	@EJB
	private CatalogoMetodoEstimacionFacade catalogoMetodoEstimacionFacade;
	
	@EJB 
	private DatosLaboratorioFacade datosLaboratorioFacade;
	
	@EJB
	private SubstanciasRetceFacade substanciasRetceFacade;
	
	@Getter
	@Setter
	private List<ConsumoCombustible> listaConsumoCombustibles, listaConsumoCombustiblesEliminar;
	
	@Getter
	@Setter
	private List<ConsumoEnergia> listaConsumoEnergia, listaConsumoEnergiaEliminar;
	
	@Getter
	@Setter
	private List<ConsumoAgua> listaConsumoAgua, listaConsumoAguaEliminar;
	
	@Getter
	@Setter
	private List<DetalleCatalogoGeneral> listaTipoCombustible, listaTipoProceso, listaTipoSuministro, listaTipoFuente;
	
	@Getter
	@Setter
	private List<Documento> listaDocumentosMediosVerificacion;
	
	@Getter
	@Setter
	private List<CatalogoSustanciasRetce> listaSustanciasRetce;
	
	@Getter
	@Setter
	private List<SubstanciasRetce> listaSustanciasEnergia, listaSustanciasEnergiaEliminar;
	
	@Getter
	@Setter
	private List<CatalogoMetodoEstimacion> listaMetodoEstimacion;
	
	@Getter
	@Setter
	private List<ConsumoRecursos> listaConsumosRecursos;
	
	@Getter
	@Setter
	private List<InformacionProyecto> listaInformacionBasica;
	
	@Getter
	@Setter
	private InformacionProyecto informacionProyecto;
	
	@Getter
	@Setter
	private ConsumoRecursos consumoRecursos;
	
	@Getter
	@Setter
	private ConsumoCombustible consumoCombustible, consumoCombustibleOriginal;
	
	@Getter
	@Setter
	private ConsumoEnergia consumoEnergia, consumoEnergiaOriginal;
	
	@Getter
	@Setter
	private ConsumoAgua consumoAgua, consumoAguaOriginal;
	
	@Getter
	@Setter
	private TecnicoResponsable tecnicoResponsable;
	
	@Getter
	@Setter
	private Documento documentoMedio, informeMonitoreo, documentoCalculo, resolucionAprovechamientoAgua;
	
	@Getter
	@Setter
	private SubstanciasRetce sustanciaRetce;
	
	@Getter
	@Setter
	private DetalleCatalogoGeneral fuenteSeleccionada;
	
	@Getter
	@Setter
	private Boolean aceptarCondiciones;
	
	@Getter
	@Setter
	private String codOtroTipoCombustible, codOtroTipoSuministro, nombreProponente, cedulaProponente, mensajeResponsabilidad;
	
	@Getter
	@Setter
	private Boolean editarCombustible, editarEnergia, editarAgua, editarSustancia;
	
	@Getter
	@Setter
	private Boolean panelCombustibleVisible, panelEnergiaVisible, panelAguaVisible;
	
	@Getter
	@Setter
	private Boolean showLaboratorio, showCalculo, habilitarSiguiente, verFormulario;
	
	@Getter
	@Setter
	private Integer totalColsSustancias;
	
	@Getter
	@Setter
	private Integer ordenMedicionDirecta = 1;

	@PostConstruct
	private void iniciar() {
		JsfUtil.getLoggedUser().getNombre();
		try {
			Integer idInformacionBasica =(Integer)(JsfUtil.devolverObjetoSession(InformacionProyecto.class.getSimpleName() + "_consumo"));
			Integer idConsumo =(Integer)(JsfUtil.devolverObjetoSession(ConsumoRecursos.class.getSimpleName()));

			if (idInformacionBasica != null) {
				informacionProyecto = informacionProyectoFacade.findById(idInformacionBasica);
				JsfUtil.cargarObjetoSession(InformacionProyecto.class.getSimpleName() + "_consumo", null);
			} else if (idConsumo != null) {
				consumoRecursos =  consumoRecursosFacade.getById(idConsumo);
				if(consumoRecursos != null)
					informacionProyecto = consumoRecursos.getInformacionProyecto();
				
				JsfUtil.cargarObjetoSession(ConsumoRecursos.class.getSimpleName(), null);
			} else {
				return;
			}
			
			cargarInicio();
			cargarInfoConsumos();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void cargarInicio() {
		tecnicoResponsable = (consumoRecursos.getTecnicoResponsable() == null) ? new TecnicoResponsable() : consumoRecursos.getTecnicoResponsable();
		consumoRecursos = (consumoRecursos == null)  ? new ConsumoRecursos() : consumoRecursos;
		consumoCombustible = new ConsumoCombustible();
		informeMonitoreo = new Documento();
		
		aceptarCondiciones = false;
		editarCombustible= false;
		editarEnergia= false;
		editarAgua= false;
		editarSustancia= false;
		panelEnergiaVisible = false;
		habilitarSiguiente = true;
		
		listaConsumosRecursos = new ArrayList<>();
		listaConsumoCombustibles = new ArrayList<>();
		listaConsumoCombustiblesEliminar = new ArrayList<>();
		listaConsumoEnergia = new ArrayList<>();
		listaConsumoEnergiaEliminar = new ArrayList<>();
		listaSustanciasEnergia = new ArrayList<>();
		listaSustanciasEnergiaEliminar = new ArrayList<>();
		listaConsumoAgua = new ArrayList<>();
		listaConsumoAguaEliminar = new ArrayList<>();
		
		codOtroTipoCombustible = "tipocombustible.otro";
		codOtroTipoSuministro = "tiposuministro.otros";
		nombreProponente = loginBean.getUsuario().getPersona().getNombre();
		cedulaProponente = loginBean.getUsuario().getPersona().getPin();
		MensajeResponsabilidadRetceController mensajeResponsabilidadRetceController = JsfUtil.getBean(MensajeResponsabilidadRetceController.class);
		mensajeResponsabilidad = mensajeResponsabilidadRetceController.mensajeResponsabilidadRetce(informacionProyecto.getUsuarioCreacion());
		listaTipoCombustible = detalleCatalogoGeneralFacade.findByCatalogoGeneralString("consumo.tipo_combustible");
		listaTipoProceso = detalleCatalogoGeneralFacade.findByCatalogoGeneralString("consumo.tipo_proceso");
		listaTipoSuministro = detalleCatalogoGeneralFacade.findByCatalogoGeneralString("consumo.tipo_suministro");
		listaTipoFuente = detalleCatalogoGeneralFacade.findByCatalogoGeneralString("consumo.tipo_fuente");
		listaMetodoEstimacion = catalogoMetodoEstimacionFacade.findAll();
	}
	
	private void cargarInfoConsumos() {
		listaConsumosRecursos = consumoRecursosFacade.getConsumosByInformacionProyecto(informacionProyecto.getId());
		
		cargarCombustibles();
	}
	
	public void eliminarConsumo(ConsumoRecursos consumo) {
		consumo.setEstado(false);
		consumoRecursosFacade.eliminarConsumo(consumo);
	}
	
	public String btnSiguiente() {
        String currentStep = wizardBean.getCurrentStep();
        try {
	        if (currentStep == null || currentStep.equals("paso2")) {
	        	guardarCombustible();
	        	cargarConsumoEnergia();
	        } else if (currentStep.equals("paso3")){
	        	guardarEnergia();
	        	cargarAprovechamientoAgua();
	        } else if (currentStep.equals("paso4")) {
	        	guardarAprovechamientoAgua();
	        } 
		} catch (Exception e) {
	        JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
	    }

        return null;

    }
	
	public String btnAtras() {
        return null;
    }
		
	public void guardar() {
        try {
            String currentStep = wizardBean.getCurrentStep();
            Boolean guardadoExitoso = true;
            
            if (currentStep == null) {
            	guardarCombustible();
            } else if (currentStep.equals("paso2")) {
            	guardarCombustible();
            } else if (currentStep.equals("paso3")) {
            	guardarEnergia();
            } else if (currentStep.equals("paso4")) {
            	guardarAprovechamientoAgua();
            } 
            
            if(guardadoExitoso){
	            JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
            }

        } catch (Exception e) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
        }
    }
	
	// INFORMACIÓN DEL TÉCNICO
	public void validarCedulaListener() {
		
		JsfUtil.getBean(InformacionBasicaController.class).setTecnicoResponsable(new TecnicoResponsable());
		JsfUtil.getBean(InformacionBasicaController.class).getTecnicoResponsable().setIdentificador(tecnicoResponsable.getIdentificador());
		
		JsfUtil.getBean(InformacionBasicaController.class).validarCedulaListener();
		
		tecnicoResponsable = new TecnicoResponsable();
		tecnicoResponsable = JsfUtil.getBean(InformacionBasicaController.class).getTecnicoResponsable();
		
		if (tecnicoResponsable.getIdentificador() == null) {
			return;
		}
	}
	
	public void validateConsumoRecursos(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		if(consumoRecursos.getId() == null || 
				((listaConsumoCombustibles == null || listaConsumoCombustibles.size() == 0) && 
				(listaConsumoEnergia == null || listaConsumoEnergia.size() == 0) && 
				(listaConsumoAgua == null || listaConsumoAgua.size() == 0)))
			errorMessages.add(new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"Debe ingresar información en al menos un tipo de consumo", null));
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}

	public String aceptar() {
		try{			
			tecnicoResponsableFacade.save(tecnicoResponsable, loginBean.getUsuario());			
			
			consumoRecursos.setTecnicoResponsable(tecnicoResponsable);
			consumoRecursos.setRegistroFinalizado(true);
			consumoRecursos.setFechaTramite(new Date());
			consumoRecursosFacade.guardarConsumo(consumoRecursos);
			
			JsfUtil.addMessageInfo("Reporte de consumo enviado exitosamente");
			JsfUtil.cargarObjetoSession(InformacionProyecto.class.getSimpleName(), informacionProyecto.getId());
			return JsfUtil.actionNavigateTo("/control/retce/consumoRecursos.jsf");
		} catch (Exception e) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
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
	
	public void uploadFileDocumento(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoMedio = new Documento();
		documentoMedio.setId(null);
		documentoMedio.setContenidoDocumento(contenidoDocumento);
		documentoMedio.setNombre(event.getFile().getFileName());
		documentoMedio.setMime("application/pdf");
		documentoMedio.setExtesion(".pdf");
	}
	
	public void agregarDocumento(){
		if(documentoMedio != null && documentoMedio.getContenidoDocumento() != null) {
			listaDocumentosMediosVerificacion.add(documentoMedio);
			
			JsfUtil.addMessageInfo("Documento subido exitosamente");
		}
		documentoMedio = new Documento();
	}
	
	//PARA CONSUMO DE COMBUSTIBLES
	public void toggleHandleCombustibles(ToggleEvent event) {
		if (event.getVisibility().equals(Visibility.HIDDEN)) {			
			panelCombustibleVisible = false;
			editarCombustible = false;
		} else
			panelCombustibleVisible = true;
		
		if (!editarCombustible) {
			consumoCombustible = new ConsumoCombustible();
			listaDocumentosMediosVerificacion = new ArrayList<>();
		}
	}
	
	public List<DetalleCatalogoGeneral> getTipoCombustibleDisponile() {
		List<DetalleCatalogoGeneral> result = new ArrayList<>();
		result.addAll(listaTipoCombustible);

		if(listaConsumoCombustibles != null) {
			for (ConsumoCombustible desechoGestion : listaConsumoCombustibles) {
				if(desechoGestion != null) {
					if (editarCombustible && desechoGestion.getTipoCombustible().equals(consumoCombustible.getTipoCombustible()))
						continue;
	
					if (desechoGestion.getTipoCombustible() != null && !desechoGestion.getTipoCombustible().getCodigo().equals(codOtroTipoCombustible))
						result.remove(desechoGestion.getTipoCombustible());
				}
			}
		}

		return result;
	}
	
	public void seleccionarTipoCombustible() {
		if(consumoCombustible.getTipoCombustible() != null) {
			if(consumoCombustible.getTipoCombustible().getParametro() != null)
				consumoCombustible.setValorDensidad(Double.parseDouble(consumoCombustible.getTipoCombustible().getParametro()));
			else
				consumoCombustible.setValorDensidad(null);
			
			consumoCombustible.setValorAnualMetrosCubicos(null);
			consumoCombustible.setValorAnualToneladas(null);
		} 
	}
	
	public void calcularConsumoToneladas() {
			if(consumoCombustible.getValorDensidad() != null && consumoCombustible.getValorAnualMetrosCubicos() != null)
				consumoCombustible.setValorAnualToneladas(consumoCombustible.getValorDensidad() * consumoCombustible.getValorAnualMetrosCubicos());
			else 
				consumoCombustible.setValorAnualToneladas(null);
	}
	
	public void agregarCombustible() {
		consumoCombustible.setListaMediosVerificacion(listaDocumentosMediosVerificacion);
		listaConsumoCombustibles.add(consumoCombustible);

		consumoCombustible = new ConsumoCombustible();
		consumoCombustibleOriginal = null;
		listaDocumentosMediosVerificacion = new ArrayList<>();
		editarEnergia = false;
		
		JsfUtil.addCallbackParam("addCombustible");
	}

	public void editarCombustible(ConsumoCombustible tipoCombustible) {
		consumoCombustibleOriginal = (ConsumoCombustible) SerializationUtils.clone(tipoCombustible);
		consumoCombustible = tipoCombustible;
		
		if(tipoCombustible.getListaMediosVerificacion() != null)
			listaDocumentosMediosVerificacion = tipoCombustible.getListaMediosVerificacion();
		else
			listaDocumentosMediosVerificacion = new ArrayList<>();
		
		editarCombustible = true;
		
		listaConsumoCombustibles.remove(tipoCombustible);
	}

	public void eliminarCombustible(ConsumoCombustible tipoCombustible) {
		try {
			if (tipoCombustible.getId() != null) {
				tipoCombustible.setEstado(false);
				listaConsumoCombustiblesEliminar.add(tipoCombustible);
			}
			listaConsumoCombustibles.remove(tipoCombustible);
		} catch (Exception e) {

		}
	}
	
	public void cancelarCombustible() {
		if(editarCombustible)
			listaConsumoCombustibles.add(consumoCombustibleOriginal);
		
		consumoCombustible = new ConsumoCombustible();
		consumoCombustibleOriginal = null;
		listaDocumentosMediosVerificacion = new ArrayList<>();
		editarEnergia = false;
	}
	
	public void guardarCombustible() throws ServiceException, CmisAlfrescoException {
		if(listaConsumoCombustibles.size() > 0){
			
			for (ConsumoCombustible consumo : listaConsumoCombustibles) {
				
				consumo.setIdConsumoRecurso(consumoRecursos.getId());
				consumoRecursosFacade.guardarConsumoCombustible(consumo);
				List<DetalleCatalogoGeneral> listaTiposProcesoBase = consumoRecursosFacade.getTiposProcesoConsumo(consumo.getId(), 1);				
				
				for (DetalleCatalogoGeneral tipo : consumo.getListaProcesos()) {
					if(listaTiposProcesoBase != null && listaTiposProcesoBase.size() > 0 && listaTiposProcesoBase.contains(tipo)){
						continue;
					}
					
					TipoProcesoConsumo tipoProceso = new TipoProcesoConsumo();
					tipoProceso.setIdConsumoCombustible(consumo.getId());
					tipoProceso.setTipoProceso(tipo);
					consumoRecursosFacade.guardarTipoProceso(tipoProceso);
				}
				
				if(consumo.getListaMediosVerificacion() != null && consumo.getListaMediosVerificacion().size() > 0){
					for (Documento documento : consumo.getListaMediosVerificacion()) {
						if(documento != null && documento.getContenidoDocumento()!=null){
							documento.setIdTable(consumo.getId());
							documento.setNombreTabla(ConsumoCombustible.class.getSimpleName());
							documento.setDescripcion("Medio de verificacion");
							documento.setEstado(true);               
							documentosFacade.guardarDocumentoAlfrescoSinProyecto(
											informacionProyecto.getCodigoRetce(),
											"CONSUMO_RECURSOS",
											consumo.getId().longValue(),
											documento,
											TipoDocumentoSistema.DOCUMENTO_MEDIO_VERIFICACION,
											null);
				        }
					}
				}
			}
		}
		
		consumoRecursosFacade.eliminarConsumoCombustible(listaConsumoCombustiblesEliminar);
		listaConsumoCombustiblesEliminar = new ArrayList<>();
	}
	
	public void cargarCombustibles() {
		listaConsumoCombustiblesEliminar = new ArrayList<>();
		
		if(consumoRecursos != null && consumoRecursos.getId() != null) {
			listaConsumoCombustibles = consumoRecursosFacade.getConsumoCombustible(consumoRecursos.getId());
			
			if(listaConsumoCombustibles != null){
				for (ConsumoCombustible consumo : listaConsumoCombustibles) {
					List<DetalleCatalogoGeneral> listaProcesos = new ArrayList<>();
					List<TipoProcesoConsumo> tiposProceso = consumo.getListaTipoProcesos();
					for (TipoProcesoConsumo tipoProcesoConsumo : tiposProceso) {
						listaProcesos.add(tipoProcesoConsumo.getTipoProceso());
					}
					
					consumo.setListaProcesos(listaProcesos);

					List<Documento> documentos = documentosFacade
							.documentoXTablaIdXIdDoc(
									consumo.getId(),
									ConsumoCombustible.class.getSimpleName(),
									TipoDocumentoSistema.DOCUMENTO_MEDIO_VERIFICACION);
					if (documentos.size() > 0)
						consumo.setListaMediosVerificacion(documentos);
				}
			} else 
				listaConsumoCombustibles = new ArrayList<>();
		}
	}
	
	//PARA CONSUMO DE ENERGIA
	public void toggleHandleEnergia(ToggleEvent event) {
		if (event.getVisibility().equals(Visibility.HIDDEN)) {
			panelEnergiaVisible = false;
			editarEnergia = false;
		} else
			panelEnergiaVisible = true;

		if (!editarEnergia) {
			consumoEnergia = new ConsumoEnergia();
			listaDocumentosMediosVerificacion = new ArrayList<>();
		}
	}
	
	public List<DetalleCatalogoGeneral> getTipoSuministroDisponile() {
		List<DetalleCatalogoGeneral> result = new ArrayList<>();
		result.addAll(listaTipoSuministro);

		if(listaConsumoEnergia != null) {
			for (ConsumoEnergia consumo : listaConsumoEnergia) {
				if (editarEnergia && consumo.getTipoSuministro().equals(consumoEnergia.getTipoSuministro()))
					continue;

				if (consumo.getTipoSuministro() != null && !consumo.getTipoSuministro().getCodigo().equals(codOtroTipoSuministro))
					result.remove(consumo.getTipoSuministro());
			}
		}

		return result;
	}
	
	public void cambiarSuministro() {
		listaSustanciasEnergia = new ArrayList<>();
	}
	
	public void agregarEnergia() {
		consumoEnergia.setListaMediosVerificacion(listaDocumentosMediosVerificacion);
		consumoEnergia.setListaSustanciasRetce(listaSustanciasEnergia);
		listaConsumoEnergia.add(consumoEnergia);

		consumoEnergia = new ConsumoEnergia();
		consumoEnergiaOriginal = null;
		listaDocumentosMediosVerificacion = new ArrayList<>();
		listaSustanciasEnergia = new ArrayList<>();
		editarEnergia = false;
		
		JsfUtil.addCallbackParam("addEnergia");
	}

	public void editarEnergia(ConsumoEnergia consumoEnergiaElectrica) {
		consumoEnergiaOriginal = (ConsumoEnergia) SerializationUtils.clone(consumoEnergiaElectrica);
		consumoEnergia = consumoEnergiaElectrica;
		
		if(consumoEnergiaElectrica.getListaMediosVerificacion() != null)
			listaDocumentosMediosVerificacion = consumoEnergiaElectrica.getListaMediosVerificacion();
		else
			listaDocumentosMediosVerificacion = new ArrayList<>();
		
		listaSustanciasEnergia = consumoEnergiaElectrica.getListaSustanciasRetce();
		
		editarEnergia = true;
		
		listaConsumoEnergia.remove(consumoEnergiaElectrica);
	}

	public void eliminarEnergia(ConsumoEnergia energiaElectrica) {
		try {
			if (energiaElectrica.getId() != null) {
				energiaElectrica.setEstado(false);
				listaConsumoEnergiaEliminar.add(energiaElectrica);
			}
			
			if(energiaElectrica.getListaSustanciasRetce() != null) {
				for (SubstanciasRetce sustancia : energiaElectrica.getListaSustanciasRetce()) {
					if (sustancia.getId() != null) {
						sustancia.setEstado(false);
						listaSustanciasEnergiaEliminar.add(sustancia);
					}
				}
			}
			
			listaConsumoEnergia.remove(energiaElectrica);
		} catch (Exception e) {

		}
	}
	
	public void cancelarEnergia() {
		if(editarEnergia)
			listaConsumoEnergia.add(consumoEnergiaOriginal);
		
		consumoEnergia = new ConsumoEnergia();
		listaSustanciasEnergia = new ArrayList<>();
		editarEnergia = false;
	}
	
	public void cambiarEstimacion() {
		if(sustanciaRetce.getCatalogoMetodoEstimacion().getOrden().equals(ordenMedicionDirecta)) {
			sustanciaRetce.setDatosLaboratorio(new DatosLaboratorio());
		} else 
			sustanciaRetce.setDatosLaboratorio(null);
	}
	
	public List<SubstanciasRetce> getListaSustanciasRetceEnergia(){
		List<SubstanciasRetce> listaSustanciasRetceEnergia = new ArrayList<>();
		showLaboratorio = false;
		showCalculo = false;
		
		for (ConsumoEnergia consumo : listaConsumoEnergia) {
			if(consumo != null && consumo.getListaSustanciasRetce() != null) {
				listaSustanciasRetceEnergia.addAll(consumo.getListaSustanciasRetce());
				
				for (SubstanciasRetce substanciasRetce : listaSustanciasRetceEnergia) {
					if(substanciasRetce.getCatalogoMetodoEstimacion().getOrden().equals(ordenMedicionDirecta)){
						showLaboratorio = true;
					}
					if(!substanciasRetce.getCatalogoMetodoEstimacion().getOrden().equals(ordenMedicionDirecta)){
						showCalculo = true;
					}
				}
			}
		}
		
		if(showLaboratorio && showCalculo)
			totalColsSustancias = 5;
		else if(!showLaboratorio && !showCalculo)
			totalColsSustancias = 3;
		else 
			totalColsSustancias = 4;
		
		
		return listaSustanciasRetceEnergia;
	}
	
	public void guardarEnergia() throws ServiceException, CmisAlfrescoException {
		if(listaConsumoEnergia.size() > 0){
			
			for (ConsumoEnergia consumo : listaConsumoEnergia) {
				
				List<SubstanciasRetce> listaSustanciasConsumo = consumo.getListaSustanciasRetce();
				
				consumo.setIdConsumoRecurso(consumoRecursos.getId());
				consumoRecursosFacade.guardarConsumoEnergia(consumo);
				List<DetalleCatalogoGeneral> listaTiposProcesoBase = consumoRecursosFacade.getTiposProcesoConsumo(consumo.getId(), 2);				
				
				for (DetalleCatalogoGeneral tipo : consumo.getListaProcesos()) {
					if(listaTiposProcesoBase != null && listaTiposProcesoBase.size() > 0 && listaTiposProcesoBase.contains(tipo)){
						continue;
					}
					
					TipoProcesoConsumo tipoProceso = new TipoProcesoConsumo();
					tipoProceso.setIdConsumoElectrico(consumo.getId());
					tipoProceso.setTipoProceso(tipo);
					consumoRecursosFacade.guardarTipoProceso(tipoProceso);
				}
				
				if(consumo.getListaMediosVerificacion() != null && consumo.getListaMediosVerificacion().size() > 0){
					for (Documento documento : consumo.getListaMediosVerificacion()) {
						if(documento != null && documento.getContenidoDocumento()!=null){
							documento.setIdTable(consumo.getId());
							documento.setNombreTabla(ConsumoEnergia.class.getSimpleName());
							documento.setDescripcion("Medio de verificacion");
							documento.setEstado(true);               
							documentosFacade.guardarDocumentoAlfrescoSinProyecto(
											informacionProyecto.getCodigoRetce(),
											"CONSUMO_RECURSOS",
											consumo.getId().longValue(),
											documento,
											TipoDocumentoSistema.DOCUMENTO_MEDIO_VERIFICACION,
											null);
				        }
					}
				}
				
				for (SubstanciasRetce substanciasRetce : listaSustanciasConsumo) {
					substanciasRetce.setConsumoEnergia(consumo);

					Boolean saveLaboratorio = false;
					DatosLaboratorio lab = new DatosLaboratorio();
					if (substanciasRetce.getDatosLaboratorio() != null) {
						DatosLaboratorio laboratorio = substanciasRetce.getDatosLaboratorio();
						Documento documentoInforme = laboratorio.getDocumentoLaboratorio();
						lab = datosLaboratorioFacade.saveLaboratorioEmisiones(laboratorio, loginBean.getUsuario());
						substanciasRetce.setDatosLaboratorio(lab);
						saveLaboratorio = true;

						if (documentoInforme != null && documentoInforme.getContenidoDocumento() != null) {
							documentoInforme.setIdTable(laboratorio.getId());
							documentoInforme.setNombreTabla(GeneradorDesechosPeligrososRetce.class.getSimpleName());
							documentoInforme.setDescripcion("Informe de monitoreo emitido por el laboratorio");
							documentoInforme.setEstado(true);
							Documento documentoInformeSave = documentosFacade.guardarDocumentoAlfrescoSinProyecto(
											informacionProyecto.getCodigoRetce(),
											"CONSUMO_RECURSOS",
											laboratorio.getId().longValue(),
											documentoInforme,
											TipoDocumentoSistema.TIPO_DOCUMENTO_INFORME_LABORATORIO,
											null);

							lab.setDocumentoLaboratorio(documentoInformeSave);
						}
					}

					Documento respaldo = substanciasRetce.getDocumento();
					substanciasRetce.setDocumento(null);
					substanciasRetceFacade.save(substanciasRetce, loginBean.getUsuario());

					if (respaldo != null
							&& respaldo.getContenidoDocumento() != null) {
						respaldo.setIdTable(substanciasRetce.getId());
						respaldo.setNombreTabla(ConsumoEnergia.class.getSimpleName());
						respaldo.setDescripcion("Respaldo sustancia RETCE");
						respaldo.setEstado(true);
						Documento respaldoSave = documentosFacade.guardarDocumentoAlfrescoSinProyecto(
										informacionProyecto.getCodigoRetce(),
										"CONSUMO_RECURSOS",
										substanciasRetce.getId().longValue(),
										respaldo,
										TipoDocumentoSistema.TIPO_DOCUMENTO_RESPALDO_LABORATORIO,
										null);
						
						substanciasRetce.setDocumento(respaldoSave);
						substanciasRetceFacade.save(substanciasRetce, loginBean.getUsuario());
					} else if (respaldo != null && respaldo.getId() != null) {
						substanciasRetce.setDocumento(respaldo);
						substanciasRetceFacade.save(substanciasRetce, loginBean.getUsuario());
					}

					if (saveLaboratorio)
						substanciasRetce.setDatosLaboratorio(lab);
				}
			}
		}
		
		consumoRecursosFacade.eliminarConsumoEnergia(listaConsumoEnergiaEliminar);
		consumoRecursosFacade.eliminarSustancia(listaSustanciasEnergiaEliminar);
		
		listaConsumoEnergiaEliminar = new ArrayList<>();
		listaSustanciasEnergiaEliminar = new ArrayList<>();
	}
	
	public void cargarConsumoEnergia() {
		listaSustanciasRetce = catalogoSustanciasRetceFacade.findAll();
		listaConsumoEnergiaEliminar = new ArrayList<>();
		listaSustanciasEnergiaEliminar = new ArrayList<>();
		
		if(consumoRecursos != null && consumoRecursos.getId() != null) {
			listaConsumoEnergia = consumoRecursosFacade.getConsumoEnergia(consumoRecursos.getId());
			
			if(listaConsumoEnergia != null){
				for (ConsumoEnergia consumo : listaConsumoEnergia) {
					List<DetalleCatalogoGeneral> listaProcesos = new ArrayList<>();
					List<TipoProcesoConsumo> tiposProceso = consumo.getListaTipoProcesos();
					for (TipoProcesoConsumo tipoProcesoConsumo : tiposProceso) {
						listaProcesos.add(tipoProcesoConsumo.getTipoProceso());
					}
					
					consumo.setListaProcesos(listaProcesos);

					List<Documento> documentos = documentosFacade
							.documentoXTablaIdXIdDoc(
									consumo.getId(),
									ConsumoEnergia.class.getSimpleName(),
									TipoDocumentoSistema.DOCUMENTO_MEDIO_VERIFICACION);
					if (documentos.size() > 0)
						consumo.setListaMediosVerificacion(documentos);
				}
			} else
				listaConsumoEnergia = new ArrayList<>();
		}
	}
	
	public void nuevaSustancia() {
		sustanciaRetce = new SubstanciasRetce();
		sustanciaRetce.setDatosLaboratorio(new DatosLaboratorio());
		informeMonitoreo = new Documento();
		documentoCalculo = new Documento();
	}
	
	public List<CatalogoSustanciasRetce> getSustanciasPendientes() {
		List<CatalogoSustanciasRetce> result = new ArrayList<>();

		result.addAll(listaSustanciasRetce);

		if(listaSustanciasEnergia != null) {
			for (SubstanciasRetce desechoSustancia : listaSustanciasEnergia) {
				if (editarSustancia && desechoSustancia.getCatologSustanciasRetce().equals(sustanciaRetce.getCatologSustanciasRetce()))
					continue;

				if (desechoSustancia.getCatologSustanciasRetce() != null)
					result.remove(desechoSustancia.getCatologSustanciasRetce());
			}
		}

		return result;
	}
	
	public void agregarSustancia() {
		if(sustanciaRetce.getCatalogoMetodoEstimacion().getOrden().equals(ordenMedicionDirecta)){
			sustanciaRetce.getDatosLaboratorio().setDocumentoLaboratorio(informeMonitoreo);
			sustanciaRetce.setDocumento(null);
		} else {
			sustanciaRetce.setDatosLaboratorio(null);
			sustanciaRetce.setDocumento(documentoCalculo);
		}
		
		if (!listaSustanciasEnergia.contains(sustanciaRetce))
			listaSustanciasEnergia.add(sustanciaRetce);

		cancelarSustancia();
		JsfUtil.addCallbackParam("addSustancia");
	}
	
	public void editarSustancia(SubstanciasRetce sustancia){
		sustanciaRetce = sustancia;
		
		if(sustanciaRetce.getCatalogoMetodoEstimacion().getOrden().equals(ordenMedicionDirecta)){
			informeMonitoreo = sustancia.getDatosLaboratorio().getDocumentoLaboratorio();
			
			if(informeMonitoreo == null && sustanciaRetce.getId() != null){				
				List<Documento> documentosManifiesto = documentosFacade.documentoXTablaIdXIdDoc(
						sustancia.getDatosLaboratorio().getId(),
						GeneradorDesechosPeligrososRetce.class.getSimpleName(),
						TipoDocumentoSistema.TIPO_DOCUMENTO_INFORME_LABORATORIO);
				if (documentosManifiesto.size() > 0)
					informeMonitoreo = documentosManifiesto.get(0);
			}
		} else {
			documentoCalculo = (sustancia.getDocumento() != null) ? sustancia.getDocumento() : new Documento();
		}
		
		editarSustancia = true;
		try {
			JsfUtil.addCallbackParam("addSustancia");
		} catch (Exception e) {
			JsfUtil.addMessageError("Error al cargar la información");
		}
	}
	
	public void eliminarSustancia(SubstanciasRetce sustancia){
		try {
			if (sustancia.getId() != null) {
				sustancia.setEstado(false);
				listaSustanciasEnergiaEliminar.add(sustancia);
			}
			listaSustanciasEnergia.remove(sustancia);
		} catch (Exception e) {

		}
	}
	
	public void cancelarSustancia(){
		sustanciaRetce = new SubstanciasRetce();
		editarSustancia = false;
	}
	
	public void uploadFileDocumentoCalculo(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoCalculo = new Documento();
		documentoCalculo.setId(null);
		documentoCalculo.setContenidoDocumento(contenidoDocumento);
		documentoCalculo.setNombre(event.getFile().getFileName());
		documentoCalculo.setMime("application/pdf");
		documentoCalculo.setExtesion(".pdf");
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
	
	public void validateDocumentoCalculo(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		if(sustanciaRetce.getCatalogoMetodoEstimacion() != null && !sustanciaRetce.getCatalogoMetodoEstimacion().getOrden().equals(ordenMedicionDirecta)){
			if (documentoCalculo == null || (documentoCalculo.getId() == null && documentoCalculo.getContenidoDocumento() == null))
				errorMessages.add(new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								"Debe adjuntar el documento de respaldo del cálculo en formato .PDF tamaño permitido 20Mb.", null));
			
		}
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
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
	
	public Double getTotalConsumoEnergia(){
		Double total =  0.0;
		if(listaConsumoEnergia != null && listaConsumoEnergia.size() > 0){
			for (ConsumoEnergia consumo : listaConsumoEnergia) {
				total += consumo.getValorAnual();
			}
		}
		
		return total;
	}
	
	//PARA APROVECHAMIENTO DE AGUA
	public void toggleHandleConsumoAgua(ToggleEvent event) {
		if (event.getVisibility().equals(Visibility.HIDDEN)) {
			panelAguaVisible = false;
			editarAgua = false;
		} else
			panelAguaVisible = true;
		
		if (!editarAgua) {
			consumoAgua = new ConsumoAgua();
			listaDocumentosMediosVerificacion = new ArrayList<>();
			resolucionAprovechamientoAgua = new Documento();
		}
	}
	
	public void agregarAgua() {
		consumoAgua.setListaMediosVerificacion(listaDocumentosMediosVerificacion);
		consumoAgua.setTipoFuente(fuenteSeleccionada);
		
		if(consumoAgua.getTipoFuente().getParametro().equals("1"))
			consumoAgua.setResolucionAprovechamiento(resolucionAprovechamientoAgua);
		else
			consumoAgua.setResolucionAprovechamiento(null);
		
		listaConsumoAgua.add(consumoAgua);

		consumoAgua = new ConsumoAgua();
		fuenteSeleccionada = null;
		consumoAguaOriginal = null;
		resolucionAprovechamientoAgua = null;
		listaDocumentosMediosVerificacion = new ArrayList<>();
		editarAgua = false;
		
		JsfUtil.addCallbackParam("addAgua");
	}

	public void editarAgua(ConsumoAgua aprovechamientoAgua) {
		consumoAguaOriginal = (ConsumoAgua) SerializationUtils.clone(aprovechamientoAgua);
		consumoAgua = aprovechamientoAgua;
		fuenteSeleccionada = consumoAgua.getTipoFuente();
		
		if(aprovechamientoAgua.getListaMediosVerificacion() != null)
			listaDocumentosMediosVerificacion = aprovechamientoAgua.getListaMediosVerificacion();
		else
			listaDocumentosMediosVerificacion = new ArrayList<>();
		
		if(consumoAgua.getTipoFuente().getParametro().equals("1"))
			resolucionAprovechamientoAgua = consumoAgua.getResolucionAprovechamiento();
		
		editarAgua = true;
		
		listaConsumoAgua.remove(aprovechamientoAgua);
	}

	public void eliminarAgua(ConsumoAgua consumo) {
		try {
			if (consumo.getId() != null) {
				consumo.setEstado(false);
				listaConsumoAguaEliminar.add(consumo);
			}
			listaConsumoAgua.remove(consumo);
		} catch (Exception e) {

		}
	}
	
	public void cancelarAgua() {
		if(editarAgua)
			listaConsumoAgua.add(consumoAguaOriginal);
		
		consumoAgua = new ConsumoAgua();
		fuenteSeleccionada = null;;
		consumoAguaOriginal = null;
		resolucionAprovechamientoAgua = null;
		listaDocumentosMediosVerificacion = new ArrayList<>();
		editarAgua = false;
	}
	
	public void validateConsumoAgua(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		if(fuenteSeleccionada != null && fuenteSeleccionada.getParametro().equals("1")){
			if (resolucionAprovechamientoAgua.getId() == null && resolucionAprovechamientoAgua.getContenidoDocumento() == null)
				errorMessages.add(new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								"Debe adjuntar el documento de resolución en formato .PDF tamaño permitido 20Mb.", null));
			//validar coordenada
			if(consumoAgua.getCoordenadaX() != null && consumoAgua.getCoordenadaY() != null) {
				String coordenadaX = consumoAgua.getCoordenadaX().toString().replace(",", ".");
				String coordenadaY = consumoAgua.getCoordenadaY().toString().replace(",", ".");
				
				String mensaje = JsfUtil.validarCoordenadaPunto17S(coordenadaX, coordenadaY, consumoRecursos.getCodigoTramite());
				
				if(mensaje != null)
					errorMessages.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR, mensaje, null));
			}
		}
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public void guardarAprovechamientoAgua() throws ServiceException, CmisAlfrescoException {
		if(listaConsumoAgua.size() > 0){
			
			for (ConsumoAgua consumo : listaConsumoAgua) {
				
				consumo.setIdConsumoRecurso(consumoRecursos.getId());
				consumoRecursosFacade.guardarConsumoAgua(consumo);
				List<DetalleCatalogoGeneral> listaTiposProcesoBase = consumoRecursosFacade.getTiposProcesoConsumo(consumo.getId(), 3);				
				
				for (DetalleCatalogoGeneral tipo : consumo.getListaProcesos()) {
					if(listaTiposProcesoBase != null && listaTiposProcesoBase.size() > 0 && listaTiposProcesoBase.contains(tipo)){
						continue;
					}
					
					TipoProcesoConsumo tipoProceso = new TipoProcesoConsumo();
					tipoProceso.setIdConsumoAgua(consumo.getId());
					tipoProceso.setTipoProceso(tipo);
					consumoRecursosFacade.guardarTipoProceso(tipoProceso);
				}
				
				if(consumo.getListaMediosVerificacion() != null && consumo.getListaMediosVerificacion().size() > 0){
					for (Documento documento : consumo.getListaMediosVerificacion()) {
						if(documento != null && documento.getContenidoDocumento()!=null){
							documento.setIdTable(consumo.getId());
							documento.setNombreTabla(ConsumoAgua.class.getSimpleName());
							documento.setDescripcion("Medio de verificacion");
							documento.setEstado(true);               
							documentosFacade.guardarDocumentoAlfrescoSinProyecto(
											informacionProyecto.getCodigoRetce(),
											"CONSUMO_RECURSOS",
											consumo.getId().longValue(),
											documento,
											TipoDocumentoSistema.DOCUMENTO_MEDIO_VERIFICACION,
											null);
				        }
					}
				}
				
				if(consumo.getTipoFuente().getParametro().equals("1")) {
					if(consumo.getResolucionAprovechamiento() != null && consumo.getResolucionAprovechamiento().getContenidoDocumento()!=null){
						consumo.getResolucionAprovechamiento().setIdTable(consumo.getId());
						consumo.getResolucionAprovechamiento().setNombreTabla(ConsumoAgua.class.getSimpleName());
						consumo.getResolucionAprovechamiento().setDescripcion("Resolución aprovechamiento de agua");
						consumo.getResolucionAprovechamiento().setEstado(true);               
						documentosFacade.guardarDocumentoAlfrescoSinProyecto(
										informacionProyecto.getCodigoRetce(),
										"CONSUMO_RECURSOS",
										consumo.getId().longValue(),
										consumo.getResolucionAprovechamiento(),
										TipoDocumentoSistema.RESOLUCION_APROVECHAMIENTO_AGUA,
										null);
			        }
				}
			}
		}
		
		consumoRecursosFacade.eliminarAprovechamientoAgua(listaConsumoAguaEliminar);
		listaConsumoAguaEliminar = new ArrayList<>();
	}
	
	public void cargarAprovechamientoAgua() {
		listaConsumoAguaEliminar = new ArrayList<>();
		
		if(consumoRecursos != null && consumoRecursos.getId() != null) {
			listaConsumoAgua = consumoRecursosFacade.getAprovechamientoAgua(consumoRecursos.getId());
			
			if(listaConsumoAgua != null){
				for (ConsumoAgua consumo : listaConsumoAgua) {
					List<DetalleCatalogoGeneral> listaProcesos = new ArrayList<>();
					List<TipoProcesoConsumo> tiposProceso = consumo.getListaTipoProcesos();
					for (TipoProcesoConsumo tipoProcesoConsumo : tiposProceso) {
						listaProcesos.add(tipoProcesoConsumo.getTipoProceso());
					}
					
					consumo.setListaProcesos(listaProcesos);

					List<Documento> documentos = documentosFacade
							.documentoXTablaIdXIdDoc(
									consumo.getId(),
									ConsumoAgua.class.getSimpleName(),
									TipoDocumentoSistema.DOCUMENTO_MEDIO_VERIFICACION);
					if (documentos.size() > 0)
						consumo.setListaMediosVerificacion(documentos);
					
					if(consumo.getTipoFuente().getParametro().equals("1")) {
						List<Documento> resolucion = documentosFacade
								.documentoXTablaIdXIdDoc(
										consumo.getId(),
										ConsumoAgua.class.getSimpleName(),
										TipoDocumentoSistema.RESOLUCION_APROVECHAMIENTO_AGUA);
						if (resolucion.size() > 0)
							consumo.setResolucionAprovechamiento(resolucion.get(0));
					}
				}
			} else
				listaConsumoAgua = new ArrayList<>();
		}
	}
	
	public Double getTotalConsumoAgua(){
		Double total =  0.0;
		if(listaConsumoAgua != null && listaConsumoAgua.size() > 0){
			for (ConsumoAgua consumo : listaConsumoAgua) {
				total += consumo.getConsumoAnual();
			}
		}
		
		return total;
	}
	
	public void verFuentesAprovechamiento(ConsumoAgua aprovechamientoAgua) {
		consumoAgua = aprovechamientoAgua;
	}
	
	public void uploadFileResolucion(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		resolucionAprovechamientoAgua = new Documento();
		resolucionAprovechamientoAgua.setId(null);
		resolucionAprovechamientoAgua.setContenidoDocumento(contenidoDocumento);
		resolucionAprovechamientoAgua.setNombre(event.getFile().getFileName());
		resolucionAprovechamientoAgua.setMime("application/pdf");
		resolucionAprovechamientoAgua.setExtesion(".pdf");
				
		JsfUtil.addMessageInfo("Documento subido exitosamente");
	}
	
	public void validarData() {
		if (informacionProyecto == null) {
			JsfUtil.redirectTo("/control/retce/informacionBasica.jsf");
		}
	}
}
