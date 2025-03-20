/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.eia.inventarioforestal.controller;

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

import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import lombok.Getter;
import lombok.Setter;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.catalogos.facade.CatalogoLibroRojoFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.CatalogoLibroRojo;
import ec.gob.ambiente.suia.domain.CoordenadaGeneral;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.EspeciesBajoCategoriaAmenaza;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.InventarioForestal;
import ec.gob.ambiente.suia.domain.InventarioForestalIndice;
import ec.gob.ambiente.suia.domain.InventarioForestalPuntos;
import ec.gob.ambiente.suia.domain.InventarioForestalVolumen;
import ec.gob.ambiente.suia.domain.enums.TipoIndice;
import ec.gob.ambiente.suia.eia.inventarioforestal.bean.InventarioForestalBean;
import ec.gob.ambiente.suia.eia.inventarioforestal.facade.InventarioForestalFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;

/**
 * @author martin
 */
@ManagedBean
@ViewScoped
public class InventarioForestalController implements Serializable {

	private static final long serialVersionUID = 4128346690041487900L;

	@EJB
	private InventarioForestalFacade inventarioForestalFacade;

	@Getter
	@Setter
	private InventarioForestalBean inventarioForestalBean;

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(InventarioForestalController.class);

	@Getter
	@Setter
	private Double montoTotalValoracion;

	@Getter
	@Setter
	private Boolean tieneEspeciesCategoriaAmenaza;

	@Getter
	@Setter
	private boolean proyectoEstrategico;

	@Getter
	@Setter
	private boolean tieneInventarioForestal;

	@EJB
	private DocumentosFacade documentosFacade;

	@EJB
	private CatalogoLibroRojoFacade catalogoLibroRojoFacade;

	@EJB
	private ValidacionSeccionesFacade validacionSeccionesFacade;

	@Getter
	@Setter
	private List<CatalogoLibroRojo> listaCatalogoLibroRojo;

	private static final int DOCUMENTO_GENERAL = 0;
	private static final int TABLA_DE_VOLUMEN = 1;
	private static final int INDICE_DE_VALOR_IMPORTANCIA = 2;
	
	/**
	 * CF: Aumento de variables
	 */
	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	
	@ManagedProperty(value = "#{proyectosBean}")
	@Getter
	@Setter
	private ProyectosBean proyectosBean;
	
	@EJB
	private ProcesoFacade procesoFacade;
	private Map<String, Object> processVariables;
	private Integer numeroNotificaciones;
	private boolean existeObservaciones;
	private String promotor;
	
	@Getter
	@Setter
	private Integer totalEspeciesModificadas;
	
	@Getter
	@Setter
	private boolean infoGeneralChanged, resultadosChanged, valoracionChanged, esPuntoNuevo;
	
	@Getter
    @Setter
    private List<Documento> listaDocumentosHistorico, listaDocumentoGeneralHistorico, listaAdjuntoVolumenHistorico, listaAdjuntoIndiceHistorico;

	@SuppressWarnings("rawtypes")
	@PostConstruct
	public void inicio() throws JbpmException {
		
		/**
		 * Cristina Flores obtener variables
		 */
		processVariables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getProcessId());
		String numNotificaciones = (String) processVariables.get("cantidadNotificaciones");
		if(numNotificaciones != null){
			numeroNotificaciones = Integer.valueOf(numNotificaciones);
		}else{
			numeroNotificaciones = 0;
		}		
		promotor = (String) processVariables.get("u_Promotor");
		if(numeroNotificaciones > 0)
			existeObservaciones = true;
		
		//Fin CF			
		
		inventarioForestalBean = new InventarioForestalBean();
		EstudioImpactoAmbiental es = (EstudioImpactoAmbiental) JsfUtil
				.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT);
		inventarioForestalBean.setEstudioImpactoAmbiental(es);

		if (es != null && es.getProyectoLicenciamientoAmbiental() != null) {
			es.getProyectoLicenciamientoAmbiental().getCatalogoCategoria()
					.getEstrategico();
		}

		inventarioForestalBean.setListaEntidadesRemover(new ArrayList<List>());
		tieneEspeciesCategoriaAmenaza = false;

		listaCatalogoLibroRojo = new ArrayList<CatalogoLibroRojo>();
		//MarielaG lista para historial de documentos
		listaDocumentoGeneralHistorico = new ArrayList<Documento>();
		listaAdjuntoVolumenHistorico = new ArrayList<Documento>();
		listaAdjuntoIndiceHistorico = new ArrayList<Documento>();

		cargarCatalogoLibroRojo();
		recuperarInventarioForestal();
		calcularMontoTotalValoracion();
		
		//MarielaG para buscar informacion original
        if(existeObservaciones){			
//			if(!promotor.equals(loginBean.getNombreUsuario())){
				consultarInventarioOriginal();
				consultarPuntosInventarioOriginales();
				consultarEspeciesCitesOriginales();
//			}
		}
	}

	private void recuperarInventarioForestal() {
		try {
			inventarioForestalBean.setInventarioForestal(inventarioForestalFacade.obtenerInventarioForestalEnBdd(inventarioForestalBean.getEstudioImpactoAmbiental()));
			
			if (inventarioForestalBean.getInventarioForestal().getId() != null) {

				List<InventarioForestalPuntos> listaInventarioForestalPuntos = new ArrayList<InventarioForestalPuntos>();
				inventarioForestalBean.setListaInventarioPuntos(inventarioForestalBean.getInventarioForestal().getInventarioForestalPuntosList());
				for(InventarioForestalPuntos invPuntos : inventarioForestalBean.getInventarioForestal().getInventarioForestalPuntosList()){
					if(invPuntos.getIdHistorico() == null){
						listaInventarioForestalPuntos.add(invPuntos);
					}
				}	

				List<EspeciesBajoCategoriaAmenaza> listaEspeciesBajoCategoriaAmenaza = new ArrayList<EspeciesBajoCategoriaAmenaza>();
				inventarioForestalBean.setListaEspeciesAmenazadas(inventarioForestalBean.getInventarioForestal().getEspeciesBajoCategoriaAmenazaList());
				for(EspeciesBajoCategoriaAmenaza especie : inventarioForestalBean.getInventarioForestal().getEspeciesBajoCategoriaAmenazaList()){
					if(especie.getIdHistorico() == null){
						listaEspeciesBajoCategoriaAmenaza.add(especie);
					}
				}
				
				inventarioForestalBean.setListaInventarioForestalPuntos(listaInventarioForestalPuntos); //listaInventarioForestalPuntosMod
				// inventarioForestalBean.setListaInventarioForestalVolumen(inventarioForestalBean.getInventarioForestal().getInventarioForestalVolumenList());
				// inventarioForestalBean.setListaInventarioForestalIndice(inventarioForestalBean.getInventarioForestal().getInventarioForestalIndiceList());
				inventarioForestalBean.setListaEspeciesBajoCategoriaAmenazas(listaEspeciesBajoCategoriaAmenaza);
				inventarioForestalBean.setListaCoordenadas(inventarioForestalFacade	.listarCoordenadaGeneral(InventarioForestal.class.getSimpleName(),
										inventarioForestalBean.getInventarioForestal().getId()));

				if (!inventarioForestalBean.getListaEspeciesBajoCategoriaAmenazas().isEmpty())
					tieneEspeciesCategoriaAmenaza = true;

				if (inventarioForestalBean.getInventarioForestal().getRemocionVegetal())
					proyectoEstrategico = inventarioForestalBean.getEstudioImpactoAmbiental().getProyectoLicenciamientoAmbiental().getCatalogoCategoria().getEstrategico();

				inventarioForestalBean.getInventarioForestal().setInventarioForestalPuntosList(null);
				// inventarioForestalBean.getInventarioForestal().setInventarioForestalIndiceList(null);
				// inventarioForestalBean.getInventarioForestal().setInventarioForestalVolumenList(null);
				inventarioForestalBean.getInventarioForestal().setEspeciesBajoCategoriaAmenazaList(null);

				reasignarIndicePunto();
				// reasignarIndiceVolumen();
				// reasignarIndiceTblIndice();
				reasignarIndiceTblEspecie();
				cargarAdjuntosEIA(TipoDocumentoSistema.INVENTARIO_FORESTAL);
				cargarAdjuntosEIA(TipoDocumentoSistema.TABLA_DE_VOLUMEN);
				cargarAdjuntosEIA(TipoDocumentoSistema.INDICE_DE_VALOR_IMPORTANCIA);

			} else {

				inventarioForestalBean
						.setInventarioForestal(new InventarioForestal());
				inventarioForestalBean
						.setListaInventarioForestalPuntos(new ArrayList<InventarioForestalPuntos>());
				// inventarioForestalBean.setListaInventarioForestalVolumen(new
				// ArrayList<InventarioForestalVolumen>());
				// inventarioForestalBean.setListaInventarioForestalIndice(new
				// ArrayList<InventarioForestalIndice>());
				inventarioForestalBean
						.setListaEspeciesBajoCategoriaAmenazas(new ArrayList<EspeciesBajoCategoriaAmenaza>());
				inventarioForestalBean
						.setListaCoordenadas(new ArrayList<CoordenadaGeneral>());

				inventarioForestalBean.getInventarioForestal()
						.setCapturaDeCarbono(0.0);
				inventarioForestalBean.getInventarioForestal()
						.setTurismoBellezaEscenica(0.0);
				inventarioForestalBean.getInventarioForestal().setRecursoAgua(
						0.0);
				inventarioForestalBean.getInventarioForestal()
						.setProductosForestalesMaderablesYNoMaderables(0.0);
				inventarioForestalBean.getInventarioForestal()
						.setPlantasMedicinales(0.0);
				inventarioForestalBean.getInventarioForestal()
						.setPlantasOrnamentales(0.0);
				inventarioForestalBean.getInventarioForestal().setArtesanias(
						0.0);
				inventarioForestalBean.getInventarioForestal()
						.setRemocionVegetal(false);
				inventarioForestalBean.getInventarioForestal()
						.setTieneInventarioForestal(false);
				
				inventarioForestalBean.setListaInventarioPuntos(new ArrayList<InventarioForestalPuntos>());
				inventarioForestalBean.setListaEspeciesAmenazadas(new ArrayList<EspeciesBajoCategoriaAmenaza>());

			}
		} catch (Exception e) {
			LOG.error(e, e);
		}
	}

	public void tieneRemocionVegetal() {
		if (inventarioForestalBean.getEstudioImpactoAmbiental() != null)
			proyectoEstrategico = inventarioForestalBean
					.getEstudioImpactoAmbiental()
					.getProyectoLicenciamientoAmbiental()
					.getCatalogoCategoria().getEstrategico();
	}

	/**
	 *
	 */
	public void agregarPunto() {
		inventarioForestalBean
				.setInventarioForestalPunto(new InventarioForestalPuntos());
		inventarioForestalBean
				.setListaCoordenadas(new ArrayList<CoordenadaGeneral>());
	}

	/**
	 * @throws ServiceException 
	 *
	 */
	public void seleccionarPunto(
			InventarioForestalPuntos inventarioForestalPunto) throws ServiceException {
		inventarioForestalPunto.setEditar(true);
		inventarioForestalBean
				.setInventarioForestalPunto(inventarioForestalPunto);
		
		//MarielaG
		//para recuperar solo coordenadas actuales
		List<CoordenadaGeneral> coordenadasPunto = new ArrayList<>();
		List<CoordenadaGeneral> coordenadasEnBase = inventarioForestalPunto.getCoordenadasGeneral();
		for(CoordenadaGeneral coordenada : coordenadasEnBase){
			if(coordenada.getIdHistorico() == null)
				coordenadasPunto.add(coordenada);
		}
		inventarioForestalBean.setListaCoordenadas(coordenadasPunto);
		
		inventarioForestalBean.setListaCoordenadasOriginal(new ArrayList<CoordenadaGeneral>());
		
		//MarielaG
		//para recuperar solo informacion original
		if (existeObservaciones) {
//			if (!promotor.equals(loginBean.getNombreUsuario())) {
				consultarPuntoOriginal(inventarioForestalPunto);
				consultarCoordenadasOriginales(coordenadasEnBase);
//			}
		}
	}

	/**
	 *
	 *
	 */
	@SuppressWarnings("unchecked")
	public void removerPunto(InventarioForestalPuntos inventarioForestalPunto) {
		try {
			inventarioForestalBean.getListaInventarioForestalPuntos().remove(
					inventarioForestalPunto.getIndice());
			if (inventarioForestalPunto.getId() != null) {
				inventarioForestalPunto.setEstado(false);
				inventarioForestalBean.getListaEntidadesRemover().add(
						inventarioForestalPunto);
				inventarioForestalBean.getListaEntidadesRemover().addAll(
						inventarioForestalPunto.getCoordenadasGeneral());
			}

		} catch (Exception e) {
			JsfUtil.addMessageError(e.getMessage());
			LOG.error(e, e);
		}
	}

	/**
	 *
	 */
	public void guardarPunto() {
		try {
			validarGuardarCoordenadas();
			if (!inventarioForestalBean.getInventarioForestalPunto().isEditar()) {
				//validarGuardarCoordenadas();
				inventarioForestalBean.getInventarioForestalPunto()
						.setInventarioForestal(
								inventarioForestalBean.getInventarioForestal());
				inventarioForestalBean.getInventarioForestalPunto()
						.setCoordenadasGeneral(
								inventarioForestalBean.getListaCoordenadas());
				inventarioForestalBean.getListaInventarioForestalPuntos().add(
						inventarioForestalBean.getInventarioForestalPunto());
				validacionSeccionesFacade.guardarValidacionSeccion("EIA",
						"inventarioForestal", inventarioForestalBean
								.getEstudioImpactoAmbiental().getId()
								.toString());
			}
			JsfUtil.addCallbackParam("addPuntoMuestreo");
			inventarioForestalBean
					.setListaCoordenadas(new ArrayList<CoordenadaGeneral>());
		} catch (Exception e) {
			JsfUtil.addMessageError(e.getMessage());
			LOG.error(e, e);
		}
	}

	/**
	 *
	 */
	public void cerrarPunto() {
		try {

			JsfUtil.addCallbackParam("addPuntoMuestreo");
			inventarioForestalBean
					.setListaCoordenadas(new ArrayList<CoordenadaGeneral>());

		} catch (Exception e) {
			JsfUtil.addMessageError(e.getMessage());
			LOG.error(e, e);
		}
	}

	private void reasignarIndicePunto() {
		int i = 0;
		for (InventarioForestalPuntos in : inventarioForestalBean
				.getListaInventarioForestalPuntos()) {
			in.setIndice(i);
			i++;
		}
	}

	/**
	 *
	 */
	public void agregarCoordenada() {
		inventarioForestalBean.setCoordenadaGeneral(new CoordenadaGeneral());
	}

	/**
	 * @param coordenada
	 */
	public void seleccionarCoordenada(CoordenadaGeneral coordenada) {
		coordenada.setEditar(true);
		inventarioForestalBean.setCoordenadaGeneral(coordenada);
	}

	/**
	 * @param coordenada
	 */
	@SuppressWarnings("unchecked")
	public void removerCoordenada(CoordenadaGeneral coordenada) {
		try {
			inventarioForestalBean.getListaCoordenadas().remove(
					coordenada);
			if (coordenada.getId() != null) {
				coordenada.setEstado(false);
				inventarioForestalBean.getListaEntidadesRemover().add(
						coordenada);
			}

		} catch (Exception e) {
			JsfUtil.addMessageError(e.getMessage());
			LOG.error(e, e);
		}
	}

	/**
	 *
	 */
	public void guardarCoordenada() {
		try {

			if (!inventarioForestalBean.getCoordenadaGeneral().isEditar()) {
				inventarioForestalBean.getListaCoordenadas().add(
						inventarioForestalBean.getCoordenadaGeneral());
			}

			JsfUtil.addCallbackParam("addCoordenada");
		} catch (Exception e) {
			JsfUtil.addMessageError(e.getMessage());
			LOG.error(e, e);
		}
	}

	/**
	 *
	 */
	public void agregarVolumen() {
		inventarioForestalBean
				.setInventarioForestalVolumen(new InventarioForestalVolumen());
	}

	/**
	 * @param volumen
	 */
	public void seleccionarVolumen(InventarioForestalVolumen volumen) {
		volumen.setEditar(true);
		inventarioForestalBean.setInventarioForestalVolumen(volumen);
	}

	/**
	 * @param volumen
	 */
	@SuppressWarnings("unchecked")
	public void removerVolumen(InventarioForestalVolumen volumen) {
		try {
			inventarioForestalBean.getListaInventarioForestalVolumen().remove(
					volumen.getIndice());
			if (volumen.getId() != null) {
				volumen.setEstado(false);
				inventarioForestalBean.getListaEntidadesRemover().add(volumen);
			}
			reasignarIndiceVolumen();
		} catch (Exception e) {
			JsfUtil.addMessageError(e.getMessage());
			LOG.error(e, e);
		}
	}

	/**
	 *
	 */
	public void guardarVolumen() {
		try {
			inventarioForestalBean.getInventarioForestalVolumen()
					.setInventarioForestal(
							inventarioForestalBean.getInventarioForestal());
			if (!inventarioForestalBean.getInventarioForestalVolumen()
					.isEditar()) {
				inventarioForestalBean.getListaInventarioForestalVolumen().add(
						inventarioForestalBean.getInventarioForestalVolumen());
			}
			reasignarIndiceVolumen();
			JsfUtil.addCallbackParam("addVolumen");

		} catch (Exception e) {
			JsfUtil.addMessageError(e.getMessage());
			LOG.error(e, e);
		}
	}

	private void reasignarIndiceVolumen() {
		int i = 0;
		for (InventarioForestalVolumen v : inventarioForestalBean
				.getListaInventarioForestalVolumen()) {
			v.setIndice(i);
			v.setNumero(i + 1);
			i++;
		}
	}

	/**
	 *
	 */
	public void agregarIndice() {
		inventarioForestalBean
				.setInventarioForestalIndice(new InventarioForestalIndice());
	}

	/**
	 * @param indice
	 */
	public void seleccionarIndice(InventarioForestalIndice indice) {
		indice.setEditar(true);
		inventarioForestalBean.setInventarioForestalIndice(indice);
	}

	/**
	 * @param indice
	 */
	@SuppressWarnings("unchecked")
	public void removerIndice(InventarioForestalIndice indice) {
		try {
			inventarioForestalBean.getListaInventarioForestalIndice().remove(
					indice.getIndice());
			if (indice.getId() != null) {
				indice.setEstado(false);
				inventarioForestalBean.getListaEntidadesRemover().add(indice);
			}
			reasignarIndiceTblIndice();
		} catch (Exception e) {
			JsfUtil.addMessageError(e.getMessage());
			LOG.error(e, e);
		}
	}

	/**
	 *
	 */
	public void guardarIndice() {
		try {
			inventarioForestalBean.getInventarioForestalIndice()
					.setIndiceValorImportancia(
							inventarioForestalBean
									.getInventarioForestalIndice()
									.getDensidadRelativa()
									+ inventarioForestalBean
									.getInventarioForestalIndice()
									.getDominanciaRelativa());
			inventarioForestalBean.getInventarioForestalIndice()
					.setInventarioForestal(
							inventarioForestalBean.getInventarioForestal());
			if (!inventarioForestalBean.getInventarioForestalIndice()
					.isEditar()) {
				inventarioForestalBean.getListaInventarioForestalIndice().add(
						inventarioForestalBean.getInventarioForestalIndice());
			}
			reasignarIndiceTblIndice();
			JsfUtil.addCallbackParam("addIndice");
		} catch (Exception e) {
			JsfUtil.addMessageError(e.getMessage());
			LOG.error(e, e);
		}
	}

	private void reasignarIndiceTblIndice() {
		int i = 0;
		for (InventarioForestalIndice in : inventarioForestalBean
				.getListaInventarioForestalIndice()) {
			in.setIndice(i);
			i++;
		}
	}

	/**
	 *
	 */
	public void agregarEspecie() {
		// cargarCatalogoLibroRojo();
		inventarioForestalBean
				.setEspecieCategoriaAmenaza(new EspeciesBajoCategoriaAmenaza());
	}

	/**
	 * @param especie
	 */
	public void seleccionarEspecie(EspeciesBajoCategoriaAmenaza especie) {
		especie.setEditar(true);
		inventarioForestalBean.setEspecieCategoriaAmenaza(especie);
	}

	/**
	 * @param especie
	 */
	@SuppressWarnings("unchecked")
	public void removerEspecie(EspeciesBajoCategoriaAmenaza especie) {
		try {
			inventarioForestalBean.getListaEspeciesBajoCategoriaAmenazas()
					.remove(especie.getIndice());
			if (especie.getId() != null) {
				especie.setEstado(false);
				inventarioForestalBean.getListaEntidadesRemover().add(especie);
			}
			reasignarIndiceTblEspecie();
		} catch (Exception e) {
			JsfUtil.addMessageError(e.getMessage());
			LOG.error(e, e);
		}
	}

	/**
	 *
	 */
	public void guardarEspecie() {
		try {
			RequestContext context = RequestContext.getCurrentInstance();
			inventarioForestalBean.getEspecieCategoriaAmenaza()
					.setInventarioForestal(
							inventarioForestalBean.getInventarioForestal());
			if (!inventarioForestalBean.getEspecieCategoriaAmenaza().isEditar()) {
				inventarioForestalBean.getListaEspeciesBajoCategoriaAmenazas()
						.add(inventarioForestalBean
								.getEspecieCategoriaAmenaza());
			}
			reasignarIndiceTblEspecie();
			context.addCallbackParam("addEspecie", true);
		} catch (Exception e) {
			JsfUtil.addMessageError(e.getMessage());
			LOG.error(e, e);
		}
	}

	private void reasignarIndiceTblEspecie() {
		int i = 0;
		for (EspeciesBajoCategoriaAmenaza es : inventarioForestalBean
				.getListaEspeciesBajoCategoriaAmenazas()) {
			es.setIndice(i);
			i++;
		}
	}

	private void cargarCatalogoLibroRojo() {

		if (listaCatalogoLibroRojo.isEmpty()) {
			listaCatalogoLibroRojo.addAll(catalogoLibroRojoFacade
					.obtenerListaCatalogoLibroRojo());
		}
	}

	/**
	 *
	 */
	public void guardar() {
		try {
			if (!inventarioForestalBean.getInventarioForestal().getTieneInventarioForestal()) {
				inventarioForestalFacade.guardar(inventarioForestalBean.getInventarioForestal());
				validacionSeccionesFacade.guardarValidacionSeccion("EIA","inventarioForestal", inventarioForestalBean.getEstudioImpactoAmbiental().getId().toString());
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
				return;
			}
			
			inventarioForestalBean.getInventarioForestal().setEstudioImpactoAmbiental(inventarioForestalBean.getEstudioImpactoAmbiental());
			inventarioForestalBean.getInventarioForestal().setInventarioForestalPuntosList(inventarioForestalBean.getListaInventarioForestalPuntos());
			// inventarioForestalBean.getInventarioForestal().setInventarioForestalVolumenList(inventarioForestalBean.getListaInventarioForestalVolumen());
			// inventarioForestalBean.getInventarioForestal().setInventarioForestalIndiceList(inventarioForestalBean.getListaInventarioForestalIndice());
			inventarioForestalBean.getInventarioForestal().setEspeciesBajoCategoriaAmenazaList(inventarioForestalBean.getListaEspeciesBajoCategoriaAmenazas());

			
			if(existeObservaciones){
				if (inventarioForestalBean.getInventarioForestal().getId() != null) {
				inventarioForestalFacade.guardarHistorico(
						inventarioForestalBean.getInventarioForestal(),
						inventarioForestalBean.getListaCoordenadas(),
						inventarioForestalBean.getListaEntidadesRemover(),numeroNotificaciones);
				}else{
					inventarioForestalFacade.guardarNuevoHistorico(
							inventarioForestalBean.getInventarioForestal(),
							inventarioForestalBean.getListaCoordenadas(), numeroNotificaciones);
				}
			}else{
				inventarioForestalFacade.guardar(
						inventarioForestalBean.getInventarioForestal(),
						inventarioForestalBean.getListaCoordenadas(),
						inventarioForestalBean.getListaEntidadesRemover());
			}

			this.salvarDocumento();
			this.salvarAdjuntoVolumen();
			this.salvarAdjuntoIndice();
			// this.guardarPunto();

			recuperarInventarioForestal();
			validacionSeccionesFacade.guardarValidacionSeccion("EIA","inventarioForestal", inventarioForestalBean.getEstudioImpactoAmbiental().getId().toString());
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			JsfUtil.redirectTo("/prevencion/licenciamiento-ambiental/eia/inventarioForestal/inventarioForestal.jsf?id=8");
			
		} catch (ServiceException e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			LOG.error(
					"Error guardando el estudio de impacto ambiental. Inventario forestal",
					e);
		}
	}

	private void validarGuardarCoordenadas() throws ServiceException {

		if (inventarioForestalBean.getListaCoordenadas() == null
				|| inventarioForestalBean.getListaCoordenadas().isEmpty()) {
			throw new ServiceException("Debe ingresar las coordenadas.");
		} else {
			if (inventarioForestalBean.getListaCoordenadas().size() < 4) {
				throw new ServiceException("Debe ingresar 4 coordenadas.");
			}
		}

	}

	public void calcularMontoTotalValoracion() {

		montoTotalValoracion = inventarioForestalBean.getInventarioForestal()
				.getCapturaDeCarbono()
				+ inventarioForestalBean.getInventarioForestal()
				.getTurismoBellezaEscenica()
				+ inventarioForestalBean.getInventarioForestal()
				.getRecursoAgua()
				+ inventarioForestalBean.getInventarioForestal()
				.getProductosForestalesMaderablesYNoMaderables()
				+ inventarioForestalBean.getInventarioForestal()
				.getPlantasMedicinales()
				+ inventarioForestalBean.getInventarioForestal()
				.getPlantasOrnamentales()
				+ inventarioForestalBean.getInventarioForestal()
				.getArtesanias();
	}

	public String cargarTipoIndice(Integer indice) {
		return TipoIndice.values()[indice].getDescripcion();
	}

	public StreamedContent getStreamContent(Integer tipoDocumento)
			throws Exception {

		Documento documento = null;

		switch (tipoDocumento) {
			case DOCUMENTO_GENERAL:
				inventarioForestalBean
						.setDocumentoGeneral(descargarAlfresco(inventarioForestalBean
								.getDocumentoGeneral()));
				documento = inventarioForestalBean.getDocumentoGeneral();
				break;
			case TABLA_DE_VOLUMEN:
				inventarioForestalBean
						.setAdjuntosVolumen(descargarAlfresco(inventarioForestalBean
								.getAdjuntosVolumen()));
				documento = inventarioForestalBean.getAdjuntosVolumen();
				break;
			case INDICE_DE_VALOR_IMPORTANCIA:
				inventarioForestalBean
						.setAdjuntosIndice(descargarAlfresco(inventarioForestalBean
								.getAdjuntosIndice()));
				documento = inventarioForestalBean.getAdjuntosIndice();
				break;
		}

		DefaultStreamedContent content = null;

		try {
			if (documento != null && documento.getNombre() != null
					&& documento.getContenidoDocumento() != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(
						documento.getContenidoDocumento()));
				content.setName(documento.getNombre());

			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception exception) {
			LOG.error(JsfUtil.MESSAGE_ERROR_ALFRESCO, exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
		}
		return content;
	}

	public void uploadListenerDocumentos(FileUploadEvent event) {
		inventarioForestalBean.setDocumentoGeneral(this.uploadListener(event,
				InventarioForestal.class, "pdf"));
	}

	public void uploadListenerAdjuntoVolumen(FileUploadEvent event) {
		inventarioForestalBean.setAdjuntosVolumen(this.uploadListenerAdjunto(
				event, InventarioForestal.class));
	}

	public void uploadListenerAdjuntoIndice(FileUploadEvent event) {
		inventarioForestalBean.setAdjuntosIndice(this.uploadListenerAdjunto(
				event, InventarioForestal.class));
	}

	private Documento uploadListener(FileUploadEvent event, Class<?> clazz,
									 String extension) {
		byte[] contenidoDocumento = event.getFile().getContents();
		Documento documento = crearDocumento(contenidoDocumento, clazz,
				extension);
		documento.setNombre(event.getFile().getFileName());
		return documento;
	}

	/**
	 * Crea el documento
	 *
	 * @param contenidoDocumento
	 *            arreglo de bytes
	 * @param clazz
	 *            Clase a la cual se va a ligar al documento
	 * @param extension
	 *            extension del archivo
	 * @return Objeto de tipo Documento
	 */
	public Documento crearDocumento(byte[] contenidoDocumento, Class<?> clazz,
									String extension) {
		Documento documento = new Documento();
		documento.setContenidoDocumento(contenidoDocumento);
		documento.setNombreTabla(clazz.getSimpleName());
		documento.setIdTable(0);
		documento.setExtesion("." + extension);

		documento.setMime(extension == "pdf" ? "application/pdf"
				: "application/vnd.ms-excel");
		return documento;
	}

	public void salvarDocumento() {
		try {

			if (inventarioForestalBean.getAdjuntosVolumen() != null
					&& inventarioForestalBean.getAdjuntosVolumen().getContenidoDocumento() != null) {
				
				inventarioForestalBean.getAdjuntosVolumen().setIdTable(inventarioForestalBean.getEstudioImpactoAmbiental().getId());
				inventarioForestalBean.getAdjuntosVolumen().setDescripcion("Adjunto Tabla de Volumen");
				inventarioForestalBean.getAdjuntosVolumen().setEstado(true);
				
				boolean cambioDocumento = false;
	        	if(inventarioForestalBean.getAdjuntosVolumen().getId() == null)
	        		cambioDocumento = true;
				
				Documento documentoA = documentosFacade.guardarDocumentoAlfresco(
						inventarioForestalBean.getEstudioImpactoAmbiental().getProyectoLicenciamientoAmbiental().getCodigo(), Constantes.CARPETA_EIA, 0L,
						inventarioForestalBean.getAdjuntosVolumen(),
						TipoDocumentoSistema.TABLA_DE_VOLUMEN, null);
				
				if(inventarioForestalBean.getAdjuntoVolumenHistorico() != null && cambioDocumento == true){
					inventarioForestalBean.getAdjuntoVolumenHistorico().setIdHistorico(documentoA.getId());
					inventarioForestalBean.getAdjuntoVolumenHistorico().setNumeroNotificacion(numeroNotificaciones);
					documentosFacade.actualizarDocumento(inventarioForestalBean.getAdjuntoVolumenHistorico());
				}
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void salvarAdjuntoVolumen() {
		try {

			if (inventarioForestalBean.getDocumentoGeneral() != null
					&& inventarioForestalBean.getDocumentoGeneral()
					.getContenidoDocumento() != null) {
				inventarioForestalBean.getDocumentoGeneral().setIdTable(
						inventarioForestalBean.getEstudioImpactoAmbiental()
								.getId());
				inventarioForestalBean.getDocumentoGeneral().setDescripcion(
						"Volumen");
				inventarioForestalBean.getDocumentoGeneral().setEstado(true);
				
				boolean cambioDocumento = false;
	        	if(inventarioForestalBean.getDocumentoGeneral().getId() == null)
	        		cambioDocumento = true;				
				
				Documento documentoA = documentosFacade.guardarDocumentoAlfresco(
						inventarioForestalBean.getEstudioImpactoAmbiental()
								.getProyectoLicenciamientoAmbiental()
								.getCodigo(), Constantes.CARPETA_EIA, 0L,
						inventarioForestalBean.getDocumentoGeneral(),
						TipoDocumentoSistema.INVENTARIO_FORESTAL, null);
				
				if(existeObservaciones){
					if(inventarioForestalBean.getDocumentoGeneralHistorico() != null && cambioDocumento == true){
						inventarioForestalBean.getDocumentoGeneralHistorico().setIdHistorico(documentoA.getId());
						inventarioForestalBean.getDocumentoGeneralHistorico().setNumeroNotificacion(numeroNotificaciones);
						documentosFacade.actualizarDocumento(inventarioForestalBean.getDocumentoGeneralHistorico());
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void salvarAdjuntoIndice() {
		try {
			if (inventarioForestalBean.getAdjuntosIndice() != null
					&& inventarioForestalBean.getAdjuntosIndice()
					.getContenidoDocumento() != null) {
				inventarioForestalBean.getAdjuntosIndice().setIdTable(
						inventarioForestalBean.getEstudioImpactoAmbiental()
								.getId());
				inventarioForestalBean.getAdjuntosIndice().setDescripcion(
						"Indice");
				inventarioForestalBean.getAdjuntosIndice().setEstado(true);
				
				boolean cambioDocumento = false;
	        	if(inventarioForestalBean.getAdjuntosIndice().getId() == null)
	        		cambioDocumento = true;	
				
				Documento documentoA = documentosFacade.guardarDocumentoAlfresco(
						inventarioForestalBean.getEstudioImpactoAmbiental()
								.getProyectoLicenciamientoAmbiental()
								.getCodigo(), Constantes.CARPETA_EIA, 0L,
						inventarioForestalBean.getAdjuntosIndice(),
						TipoDocumentoSistema.INDICE_DE_VALOR_IMPORTANCIA, null);
				
				if(existeObservaciones){					
					if(inventarioForestalBean.getAdjuntoIndiceHistorico() != null && cambioDocumento == true){
						inventarioForestalBean.getAdjuntoIndiceHistorico().setIdHistorico(documentoA.getId());
						inventarioForestalBean.getAdjuntoIndiceHistorico().setNumeroNotificacion(numeroNotificaciones);
						documentosFacade.actualizarDocumento(inventarioForestalBean.getAdjuntoIndiceHistorico());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Descarga documento desde el Alfresco
	 *
	 * @param documento
	 * @return
	 * @throws CmisAlfrescoException
	 */
	public Documento descargarAlfresco(Documento documento)
			throws CmisAlfrescoException {
		byte[] documentoContenido = null;
		if (documento != null && documento.getIdAlfresco() != null)
			documentoContenido = documentosFacade
					.descargar(documento.getIdAlfresco());
		if (documentoContenido != null)
			documento.setContenidoDocumento(documentoContenido);
		return documento;
	}

	/**
	 * @param tipoDocumento
	 * @throws CmisAlfrescoException
	 */
	private void cargarAdjuntosEIA(TipoDocumentoSistema tipoDocumento)
			throws CmisAlfrescoException {		
		List<Documento> documentosXEIA = documentosFacade
		.documentosTodosXTablaIdXIdDoc(inventarioForestalBean
				.getEstudioImpactoAmbiental().getId(),
				InventarioForestal.class.getSimpleName(), tipoDocumento);

		if (documentosXEIA.size() > 0) {
			//MARIELAG para recuperar el archivo original
			if(existeObservaciones){				
//				if(!promotor.equals(loginBean.getNombreUsuario())){
					consultarDocumentosOriginales(documentosXEIA.get(0).getId(), documentosXEIA, tipoDocumento);
//				}
			}

			switch (tipoDocumento.getIdTipoDocumento()) {
				case 301:
					inventarioForestalBean.setDocumentoGeneral(documentosXEIA.get(0));
					inventarioForestalBean.setDocumentoGeneralId(documentosXEIA.get(0).getId());
					if(existeObservaciones)
						inventarioForestalBean.setDocumentoGeneralHistorico(validarDocumentoHistorico(inventarioForestalBean.getDocumentoGeneral(), documentosXEIA, tipoDocumento));
					break;
				case 1010:
					inventarioForestalBean.setAdjuntosVolumen(documentosXEIA.get(0));
					inventarioForestalBean.setAdjuntosVolumenId(documentosXEIA.get(0).getId());
					if(existeObservaciones)
						inventarioForestalBean.setAdjuntoVolumenHistorico(validarDocumentoHistorico(inventarioForestalBean.getAdjuntosVolumen(), documentosXEIA, tipoDocumento));
					break;
				case 1011:
					inventarioForestalBean.setAdjuntosIndice(documentosXEIA.get(0));
					inventarioForestalBean.setAdjuntosIndiceId(documentosXEIA.get(0).getId());
					if(existeObservaciones)
						inventarioForestalBean.setAdjuntoIndiceHistorico(validarDocumentoHistorico(inventarioForestalBean.getAdjuntosIndice(), documentosXEIA, tipoDocumento));
					break;
			}
		}
	}

	public void descargarPlantilla(Integer tipoPlantilla)
			throws CmisAlfrescoException, IOException {

		String nombrePlantilla = "";
		switch (tipoPlantilla) {
			case TABLA_DE_VOLUMEN:
				nombrePlantilla = "Plantilla_Tabla_Volumen.xls";
				break;
			case INDICE_DE_VALOR_IMPORTANCIA:
				nombrePlantilla = "Plantilla_Tabla_De_Indices_De_Valor_De_Importancia.xls";
				break;
		}

		JsfUtil.descargarMimeType(
				documentosFacade.descargarDocumentoPorNombre(nombrePlantilla),
				nombrePlantilla, "xls", "application/vnd.ms-excel");

	}

	private Documento uploadListenerAdjunto(FileUploadEvent event,
											Class<?> clazz) {

		String nombre = event.getFile().getFileName();
		int dot = nombre.lastIndexOf('.');
		String extension = (dot == -1) ? "" : nombre.substring(dot + 1);

		byte[] contenidoDocumento = event.getFile().getContents();
		String mime = event.getFile().getContentType();
		Documento documento = crearAjunto(contenidoDocumento, clazz, extension,
				mime);
		documento.setNombre(nombre);
		return documento;
	}

	public Documento crearAjunto(byte[] contenidoDocumento, Class<?> clazz,
								 String extension, String mime) {
		Documento documento = new Documento();
		documento.setContenidoDocumento(contenidoDocumento);
		documento.setNombreTabla(clazz.getSimpleName());
		documento.setIdTable(0);
		documento.setExtesion("." + extension);
		documento.setMime(mime);

		return documento;
	}

	public String nombreCientificoEspecie(EspeciesBajoCategoriaAmenaza especie) {

		return especie.getCatalogoLibroRojo().getId() == -1 ? especie
				.getOtroNombreCientifico() : especie.getCatalogoLibroRojo()
				.getNombre();

	}

	public void validateAdjunto(FacesContext context, UIComponent validate,
								Object value) {
		StringBuilder functionJs = new StringBuilder();
		List<FacesMessage> mensajes = new ArrayList<>();
		if (inventarioForestalBean.getDocumentoGeneral() == null) {// NO VALIDO
			FacesMessage mensajeValidacionDocumento = new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"El campo 'Documento del inventario forestal' es requerido.",
					null);
			mensajes.add(mensajeValidacionDocumento);
			functionJs
					.append("highlightComponent('frmDatos:fileUploadDocumentoGeneral');");
		} else {
			functionJs
					.append("removeHighLightComponent('frmDatos:fileUploadDocumentoGeneral');");
		}
		RequestContext.getCurrentInstance().execute(functionJs.toString());
		if (!mensajes.isEmpty())
			throw new ValidatorException(mensajes);
	}

	public void validateAdjuntoVolumen(FacesContext context,
									   UIComponent validate, Object value) {
		StringBuilder functionJs = new StringBuilder();
		List<FacesMessage> mensajes = new ArrayList<>();
		if (inventarioForestalBean.getAdjuntosVolumen() == null) {// NO VALIDO
			FacesMessage mensajeValidacionDocumento = new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"El campo 'Adjuntar tabla de volumen' es requerido.", null);
			mensajes.add(mensajeValidacionDocumento);
			functionJs
					.append("highlightComponent('frmDatos:fileUploadVolumen');");
		} else {
			functionJs
					.append("removeHighLightComponent('frmDatos:fileUploadVolumen');");
		}
		RequestContext.getCurrentInstance().execute(functionJs.toString());
		if (!mensajes.isEmpty())
			throw new ValidatorException(mensajes);
	}

	public void validateAdjuntoIndice(FacesContext context,
									  UIComponent validate, Object value) {
		StringBuilder functionJs = new StringBuilder();
		List<FacesMessage> mensajes = new ArrayList<>();
		if (inventarioForestalBean.getAdjuntosIndice() == null) {// NO VALIDO
			FacesMessage mensajeValidacionDocumento = new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"El campo 'Adjuntar tabla de índices de valor de importancia' es requerido.",
					null);
			mensajes.add(mensajeValidacionDocumento);
			functionJs
					.append("highlightComponent('frmDatos:fileUploadIndice');");
		} else {
			functionJs
					.append("removeHighLightComponent('frmDatos:fileUploadIndice');");
		}
		RequestContext.getCurrentInstance().execute(functionJs.toString());
		if (!mensajes.isEmpty())
			throw new ValidatorException(mensajes);
	}

	public void validatePuntosMuestreo(FacesContext context,
									   UIComponent validate, Object value) {
		StringBuilder functionJs = new StringBuilder();
		List<FacesMessage> mensajes = new ArrayList<>();
		if (inventarioForestalBean.getListaInventarioForestalPuntos().isEmpty()) {// NO
			// VALIDO
			FacesMessage mensajeValidacionDocumento = new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"Los puntos de muestreo son obligatorios.", null);
			mensajes.add(mensajeValidacionDocumento);
			functionJs
					.append("highlightComponent('frmDatos:panelGridPuntos');");
		} else {
			functionJs
					.append("removeHighLightComponent('frmDatos:panelGridPuntos');");
		}
		RequestContext.getCurrentInstance().execute(functionJs.toString());
		if (!mensajes.isEmpty())
			throw new ValidatorException(mensajes);
	}

	public void validateValoracionEconomica(FacesContext context,
											UIComponent validate, Object value) {
		StringBuilder functionJs = new StringBuilder();
		List<FacesMessage> mensajes = new ArrayList<>();
		if (montoTotalValoracion == 0
				&& inventarioForestalBean.getInventarioForestal()
				.getRemocionVegetal()) {// NO VALIDO
			FacesMessage mensajeValidacionDocumento = new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"El monto total de valoración no debe ser 0", null);
			mensajes.add(mensajeValidacionDocumento);
			functionJs
					.append("highlightComponent('frmDatos:montoTotalValoracion');");
		} else {
			functionJs
					.append("removeHighLightComponent('frmDatos:montoTotalValoracion');");
		}
		RequestContext.getCurrentInstance().execute(functionJs.toString());
		if (!mensajes.isEmpty())
			throw new ValidatorException(mensajes);
	}
	
	Documento documentoIngresado;
	private Documento validarDocumentoHistorico(Documento documentoIngresado, List<Documento> documentosXEIA, TipoDocumentoSistema tipoDocumento){		
		try {
			List<Documento> documentosList = new ArrayList<>();
			for(Documento documento : documentosXEIA){
				if(documento.getIdHistorico() != null && 
						documento.getIdHistorico().equals(documentoIngresado.getId()) && 
						documento.getNumeroNotificacion().equals(numeroNotificaciones)){
					documentosList.add(documento);
				}
			}			
			
			if(documentosList != null && !documentosList.isEmpty()){		        
				return documentosList.get(0);
			}else{
				return documentoIngresado;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return documentoIngresado = new Documento();
		}		
	}
	
	/**
	 * MarielaG
	 * Consultar los documentos originales
	 */
	private void consultarDocumentosOriginales(Integer idDocumento, List<Documento> documentosList, TipoDocumentoSistema tipoDocumento){		
		try {			
			if (documentosList != null && !documentosList.isEmpty() && documentosList.size() > 1) {
				while (idDocumento > 0) {
					idDocumento = recuperarHistoricos(idDocumento, documentosList, tipoDocumento);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * MarielaG
	 * Consultar los documentos historicos en bucle
	 */
	private int recuperarHistoricos(Integer idDocumento, List<Documento> documentosList, TipoDocumentoSistema tipoDocumento) {
		try {
			int nextDocument = 0;
			for (Documento documento : documentosList) {
				if (documento.getIdHistorico() != null && documento.getIdHistorico().equals(idDocumento)) {
					nextDocument = documento.getId();

					switch (tipoDocumento.getIdTipoDocumento()) {
					case 301:
						listaDocumentoGeneralHistorico.add(0, documento);
						break;
					case 1010:
						listaAdjuntoVolumenHistorico.add(0, documento);
						break;
					case 1011:
						listaAdjuntoIndiceHistorico.add(0, documento);
						break;
					}
				}
			}

			return nextDocument;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * MarielaG
	 * Consultar el inventario forestal ingresado antes de las correcciones
	 */
	private void consultarInventarioOriginal() {
		try {
			infoGeneralChanged = false;
			
			List<InventarioForestal> listaInfoGeneral = new ArrayList<>();
			List<InventarioForestal> listaResultados = new ArrayList<>();
			List<InventarioForestal> listaValoracion = new ArrayList<>();
			List<InventarioForestal> listaJustificacionEspecies = new ArrayList<>();
			
			List<InventarioForestal> lista = inventarioForestalFacade.obtenerInventarioOriginal(inventarioForestalBean.getInventarioForestal().getId(), numeroNotificaciones); 
					
			if (lista != null && !lista.isEmpty()) {
				for(InventarioForestal inventario : lista){
					if(inventario.getNumeroNotificacion() == numeroNotificaciones)
						inventarioForestalBean.setInventarioForestalOriginal(lista.get(0));
					
					//cambios informacion general
					if (!inventarioForestalBean.getInventarioForestal().getAreaInversionProyecto()
							.equals(inventario.getAreaInversionProyecto()) ||
							!inventarioForestalBean.getInventarioForestal().getAreaPorRemocionDeCoverturaVejetal()
							.equals(inventario.getAreaPorRemocionDeCoverturaVejetal()) ||
							!inventarioForestalBean.getInventarioForestal().getSuperficieMuestreo()
							.equals(inventario.getSuperficieMuestreo())) {
						infoGeneralChanged = true;
						listaInfoGeneral.add(inventario);
					}
					
					//cambios resultados
					if(!inventarioForestalBean.getInventarioForestal().getPromedioAb().equals(inventario.getPromedioAb()) ||
							!inventarioForestalBean.getInventarioForestal().getVolumenTotal().equals(inventario.getVolumenTotal()) ||
							!inventarioForestalBean.getInventarioForestal().getVolumenComercial().equals(inventario.getVolumenComercial()) || 
							!inventarioForestalBean.getInventarioForestal().getVolumenTotalPromedio().equals(inventario.getVolumenTotalPromedio()) || 
							!inventarioForestalBean.getInventarioForestal().getVolumenComercialPromedio().equals(inventario.getVolumenComercialPromedio()) ||
							!inventarioForestalBean.getInventarioForestal().getVolumenTotalExtrapolado().equals(inventario.getVolumenTotalExtrapolado()) || 
							!inventarioForestalBean.getInventarioForestal().getVolumenComercialExtrapolado().equals(inventario.getVolumenComercialExtrapolado())){
						resultadosChanged = true;
						listaResultados.add(inventario);
					}
					
					//cambios valores
					if(!inventarioForestalBean.getInventarioForestal().getCapturaDeCarbono().equals(inventario.getCapturaDeCarbono()) || 
							!inventarioForestalBean.getInventarioForestal().getTurismoBellezaEscenica().equals(inventario.getTurismoBellezaEscenica()) || 
							!inventarioForestalBean.getInventarioForestal().getRecursoAgua().equals(inventario.getRecursoAgua()) || 
							!inventarioForestalBean.getInventarioForestal().getProductosForestalesMaderablesYNoMaderables().equals(inventario.getProductosForestalesMaderablesYNoMaderables()) || 
							!inventarioForestalBean.getInventarioForestal().getPlantasMedicinales().equals(inventario.getPlantasMedicinales()) || 
							!inventarioForestalBean.getInventarioForestal().getPlantasOrnamentales().equals(inventario.getPlantasOrnamentales()) || 
							!inventarioForestalBean.getInventarioForestal().getArtesanias().equals(inventario.getArtesanias())){
						valoracionChanged = true;
						calcularMontoTotalValoracionHistorial(inventario);
						listaValoracion.add(inventario);
					}
					if(inventarioForestalBean.getInventarioForestal().getJustificacionEspeciesBajoCategoriaAmenaza() != null && inventario.getJustificacionEspeciesBajoCategoriaAmenaza() != null && 
							!inventarioForestalBean.getInventarioForestal().getJustificacionEspeciesBajoCategoriaAmenaza().equals(inventario.getJustificacionEspeciesBajoCategoriaAmenaza())){						
						listaJustificacionEspecies.add(inventario);
					}					
				}
				
				inventarioForestalBean.setInfoGeneralHistorial(listaInfoGeneral);
				inventarioForestalBean.setResultadosHistorial(listaResultados);
				inventarioForestalBean.setValoracionHistorial(listaValoracion);
				
				
				inventarioForestalBean.setJustificacionHistorial(listaJustificacionEspecies);
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * MarielaG
	 * Consultar los puntos del inventario forestal ingresados antes de las correcciones
	 */
	private void consultarPuntosInventarioOriginales(){
		List<InventarioForestalPuntos> puntosOriginales = new ArrayList<InventarioForestalPuntos>();
		List<InventarioForestalPuntos> puntosEliminados = new ArrayList<>();
		
		int totalPuntosModificados = 0;
		List<InventarioForestalPuntos> puntosInventarioBdd = inventarioForestalBean.getListaInventarioPuntos();
		for(InventarioForestalPuntos puntoBdd : puntosInventarioBdd){
			if(puntoBdd.getNumeroNotificacion() == null || 
					!puntoBdd.getNumeroNotificacion().equals(numeroNotificaciones)){
    			boolean agregarItemLista = true;
    			//buscar si tiene historial
				for (InventarioForestalPuntos puntoHistorico : puntosInventarioBdd) {
					if (puntoHistorico.getIdHistorico() != null  
							&& puntoHistorico.getIdHistorico().equals(puntoBdd.getId())) {
						//si existe un registro historico, no se agrega a la lista en este paso
						agregarItemLista = false;
						puntoBdd.setRegistroModificado(true);
						break;
					}
				}
				if (agregarItemLista) {
					puntosOriginales.add(puntoBdd);
				}
			} else {
				totalPuntosModificados++;
    			//es una modificacion
    			if(puntoBdd.getIdHistorico() == null && puntoBdd.getNumeroNotificacion() == numeroNotificaciones){
    				//es un registro nuevo
    				//no ingresa en el lista de originales
    				puntoBdd.setNuevoEnModificacion(true);
    			}else{
    				puntoBdd.setRegistroModificado(true);
    				if(!puntosOriginales.contains(puntoBdd)){
    					puntosOriginales.add(puntoBdd);
    				}
    			}
    		}
			
			//para consultar eliminados
			if (puntoBdd.getIdHistorico() != null
					&& puntoBdd.getNumeroNotificacion() != null) {
				boolean existePunto = false;
				for (InventarioForestalPuntos itemActual : this.inventarioForestalBean.getListaInventarioForestalPuntos()) {
					if (itemActual.getId().equals(puntoBdd.getIdHistorico())) {
						existePunto = true;
						break;
					}
				}

				if (!existePunto) {
					puntosEliminados.add(puntoBdd);
				}
			}
		}
		
		if (totalPuntosModificados > 0)
			inventarioForestalBean.setListaInventarioForestalPuntosOriginales(puntosOriginales);
		
		inventarioForestalBean.setListaInventarioForestalPuntosEliminados(puntosEliminados);
	}
	
	/**
	 * MarielaG
	 * Recuperar el punto original del punto seleccionado
	 */
	private void consultarPuntoOriginal(InventarioForestalPuntos inventarioPunto){
		esPuntoNuevo = false;

		List<InventarioForestalPuntos> listaPuntos = new ArrayList<>();
		inventarioForestalBean.setListaInventarioPuntosHistorial(listaPuntos);
		
		//si es un registro que se ingreso originalmente
		//o si fue modificado en la modificacion anterior para ver originales ingresados en la modificacion anterior
		if (inventarioPunto.getNumeroNotificacion() == null ||
				!inventarioPunto.getNumeroNotificacion().equals(numeroNotificaciones)) {
			if(inventarioForestalBean.getListaInventarioForestalPuntosOriginales() != null){
				for (InventarioForestalPuntos puntoOriginal : inventarioForestalBean.getListaInventarioForestalPuntosOriginales()) {
					if (puntoOriginal.getIdHistorico() != null
							&& puntoOriginal.getIdHistorico().equals(inventarioPunto.getId())) {

						if (!inventarioPunto.getCodigoParcela().equals(puntoOriginal.getCodigoParcela()) ||
								!inventarioPunto.getAreaParcela().equals(puntoOriginal.getAreaParcela()) || 
								!inventarioPunto.getIndiceShannon().equals(puntoOriginal.getIndiceShannon()) || 
								!inventarioPunto.getIndiceSimpson().equals(puntoOriginal.getIndiceSimpson())) {
							listaPuntos.add(puntoOriginal);
						}
					}
				}
				
				inventarioForestalBean.setListaInventarioPuntosHistorial(listaPuntos);
			}			
		} else {
			if (inventarioPunto.getIdHistorico() == null && inventarioPunto.getNumeroNotificacion() == numeroNotificaciones) {
				// punto nuevo no tiene original
				esPuntoNuevo = true;
			}
		}
	}
	
	/**
	 * MarielaG
	 * Consultar las coordenadas originales por punto seleccionado
	 * @throws ServiceException 
	 */
	public void consultarCoordenadasOriginales(List<CoordenadaGeneral> listaCoordenadasEnBase) throws ServiceException {
		List<CoordenadaGeneral> listaCoordenadasOriginales = new ArrayList<>();
		List<CoordenadaGeneral> coordenadasEliminadas = new ArrayList<>();

		int totalCoordenadasModificadas = 0;

		for (CoordenadaGeneral coordenada : listaCoordenadasEnBase) {
			// si es un registro que se ingreso originalmente
			// o si fue modificado en la modificacion anterior para ver originales ingresados en la modificacion anterior
			if (coordenada.getNumeroNotificacion() == null
					|| !coordenada.getNumeroNotificacion().equals(numeroNotificaciones)) {
				boolean agregarItemLista = true;
				// validar si el registro seleccionado ha sido modificado (si
				// tiene un registrohistorico)
				// para no ingresarlo en la lista de originales
				for (CoordenadaGeneral coordenadaModificada : listaCoordenadasEnBase) {
					if (coordenadaModificada.getIdHistorico() != null
							&& coordenadaModificada.getIdHistorico().equals(coordenada.getId())) {
						agregarItemLista = false;
						coordenada.setRegistroModificado(true);
						break;
					}
				}
				if (agregarItemLista) {
					listaCoordenadasOriginales.add(coordenada);
				}
			} else {
				totalCoordenadasModificadas++;
				// es una modificacion
				if (coordenada.getIdHistorico() == null && coordenada.getNumeroNotificacion() == numeroNotificaciones) {
					// es un registro nuevo
					// no ingresa en el lista de originales
					coordenada.setNuevoEnModificacion(true);
				} else {
					coordenada.setRegistroModificado(true);
					if (!listaCoordenadasOriginales.contains(coordenada)) {
						listaCoordenadasOriginales.add(coordenada);
					}
				}
			}

			// para consultar eliminados
			if (coordenada.getIdHistorico() != null
					&& coordenada.getNumeroNotificacion() != null) {
				boolean existePunto = false;
				for (CoordenadaGeneral itemActual : inventarioForestalBean.getListaCoordenadas()) {
					if (itemActual.getId().equals(coordenada.getIdHistorico())) {
						existePunto = true;
						break;
					}
				}

				if (!existePunto) {
					coordenadasEliminadas.add(coordenada);
				}
			}
		}

		if (totalCoordenadasModificadas > 0) {
			inventarioForestalBean.setListaCoordenadasOriginal(listaCoordenadasOriginales);
		}

		inventarioForestalBean.setListaCoordenadasEliminadasBdd(coordenadasEliminadas);

	}
	
	/**
	 * MarielaG
	 * Consultar las especies ingresados antes de las correcciones
	 */
	private void consultarEspeciesCitesOriginales(){
		List<EspeciesBajoCategoriaAmenaza> especiesOriginales = new ArrayList<EspeciesBajoCategoriaAmenaza>();
		List<EspeciesBajoCategoriaAmenaza> especiesEliminadas = new ArrayList<>();
		
		totalEspeciesModificadas = 0;
		List<EspeciesBajoCategoriaAmenaza> especiesBdd = inventarioForestalBean.getListaEspeciesAmenazadas();
		for(EspeciesBajoCategoriaAmenaza especie : especiesBdd){
			//si es un registro que se ingreso originalmente
			//o si fue modificado en la modificacion anterior para ver originales ingresados en la modificacion anterior
			if(especie.getNumeroNotificacion() == null ||
				!especie.getNumeroNotificacion().equals(numeroNotificaciones)){
    			boolean agregarItemLista = true;
    			//buscar si tiene historial
				for (EspeciesBajoCategoriaAmenaza puntoHistorico : especiesBdd) {
					if (puntoHistorico.getIdHistorico() != null 
							&& puntoHistorico.getIdHistorico().equals(especie.getId())) {
						//si existe un registro historico, no se agrega a la lista en este paso
						agregarItemLista = false;
						especie.setRegistroModificado(true);
						break;
					}
				}
				if (agregarItemLista) {
					especiesOriginales.add(especie);
				}
			} else {
				totalEspeciesModificadas++;
    			//es una modificacion
    			if(especie.getIdHistorico() == null && especie.getNumeroNotificacion() == numeroNotificaciones){
    				//es un registro nuevo
    				//no ingresa en el lista de originales
    				especie.setNuevoEnModificacion(true);
    			}else{
    				especie.setRegistroModificado(true);
    				if(!especiesOriginales.contains(especie)){
    					especiesOriginales.add(especie);
    				}
    			}
    		}
			
			//para consultar eliminados
			if (especie.getIdHistorico() != null
					&& especie.getNumeroNotificacion() != null) {
				boolean existePunto = false;
				for (EspeciesBajoCategoriaAmenaza itemActual : inventarioForestalBean.getListaEspeciesBajoCategoriaAmenazas()) {
					if (itemActual.getId().equals(especie.getIdHistorico())) {
						existePunto = true;
						break;
					}
				}

				if (!existePunto) {
					especiesEliminadas.add(especie);
				}
			}
		}
		
		if (totalEspeciesModificadas > 0)
			inventarioForestalBean.setListaEspeciesCitesOriginal(especiesOriginales);
		
		inventarioForestalBean.setListaEspeciesCitesEliminadasBdd(especiesEliminadas);
	}
	
	/**
	 * MarielaG para calcular el monto total de los registros originales
	 */
	private void calcularMontoTotalValoracionHistorial(InventarioForestal inventario) {
		Double montoTotalValoracionOriginal = inventario.getCapturaDeCarbono()
				+ inventario.getTurismoBellezaEscenica()
				+ inventario.getRecursoAgua()
				+ inventario.getProductosForestalesMaderablesYNoMaderables()
				+ inventario.getPlantasMedicinales()
				+ inventario.getPlantasOrnamentales()
				+ inventario.getArtesanias();
		
		inventario.setTotalValoracion(montoTotalValoracionOriginal);
	}
	
	/**
	 * MarielaG para mostrar el historial de los documentos
	 */
	public void fillHistorialDocumentos(Integer tipoDocumento) {
		listaDocumentosHistorico = new ArrayList<>();
		
		switch (tipoDocumento) {
			case DOCUMENTO_GENERAL:
				listaDocumentosHistorico = listaDocumentoGeneralHistorico;
				break;
			case TABLA_DE_VOLUMEN:
				listaDocumentosHistorico = listaAdjuntoVolumenHistorico;
				break;
			case INDICE_DE_VALOR_IMPORTANCIA:
				listaDocumentosHistorico = listaAdjuntoIndiceHistorico;
				break;
		}
	}
	
	/**
	 * MarielaG
	 * Para descargar documentos originales
	 */
	public StreamedContent getStreamContentOriginal(Documento documento) throws Exception {
        DefaultStreamedContent content = null;
        try {
            inventarioForestalBean.setDocumento(this.descargarAlfresco(documento));
            if (inventarioForestalBean.getDocumento() != null && inventarioForestalBean.getDocumento().getNombre() != null && inventarioForestalBean.getDocumento().getContenidoDocumento() != null) {
                content = new DefaultStreamedContent(new ByteArrayInputStream(inventarioForestalBean.getDocumento().getContenidoDocumento()));
                content.setName(inventarioForestalBean.getDocumento().getNombre());
            } else
                JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

        } catch (Exception exception) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
        }
        return content;
    }
	
	/**
	 * MarielaG
	 * Para mostrar el historial de cambios de distancias
	 */
	public void mostrarEspeciesOriginales(EspeciesBajoCategoriaAmenaza especie) {
		List<EspeciesBajoCategoriaAmenaza> especiesHistorial = new ArrayList<>();
		
		for(EspeciesBajoCategoriaAmenaza especieOriginal : inventarioForestalBean.getListaEspeciesCitesOriginal()){
			if(especieOriginal.getIdHistorico() != null && especie.getId().equals(especieOriginal.getIdHistorico())){
				especiesHistorial.add(especieOriginal);
			}
		}
		
		inventarioForestalBean.setEspeciesHistorial(especiesHistorial);
	}
	
	/**
	 * MarielaG Para mostrar las especies eliminadas
	 */
	public void fillEspeciesEliminadas() {
		inventarioForestalBean.setEspeciesHistorial(inventarioForestalBean.getListaEspeciesCitesEliminadasBdd());
	}
	
	/**
	 * MarielaG
	 * Para mostrar el historial de cambios de coordenadas
	 */
	public void mostrarCoordenadaHistorial(CoordenadaGeneral coordenada) {
		List<CoordenadaGeneral> listaCoordenadasHistorial = new ArrayList<>();
		
		for(CoordenadaGeneral coordenadaOriginal : inventarioForestalBean.getListaCoordenadasOriginal()){
			if(coordenadaOriginal.getIdHistorico() != null && coordenada.getId().equals(coordenadaOriginal.getIdHistorico())){
				listaCoordenadasHistorial.add(coordenadaOriginal);
			}
		}
		
		inventarioForestalBean.setListaCoordenadasHistorial(listaCoordenadasHistorial);
	}
	
	/**
	 * MarielaG Para mostrar las coordenadas eliminadas
	 */
	public void fillCoordenadasEliminadas() {
		inventarioForestalBean.setListaCoordenadasHistorial(inventarioForestalBean.getListaCoordenadasEliminadasBdd());
	}
	
	public void seleccionarPuntoEliminado(InventarioForestalPuntos inventarioForestalPunto) throws ServiceException {
		inventarioForestalBean.setInventarioForestalPunto(inventarioForestalPunto);
		
		List<InventarioForestalPuntos> listaPuntos = new ArrayList<>();
		inventarioForestalBean.setListaInventarioPuntosHistorial(listaPuntos);
		
		List<CoordenadaGeneral> coordenadasPunto = inventarioForestalFacade.listarCoordenadaGeneralPuntoEliminado(
				InventarioForestalPuntos.class.getSimpleName(), inventarioForestalPunto.getIdHistorico());
		
		inventarioForestalBean.setListaCoordenadas(coordenadasPunto);
	}
}
