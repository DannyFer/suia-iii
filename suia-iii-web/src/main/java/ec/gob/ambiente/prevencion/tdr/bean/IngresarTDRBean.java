package ec.gob.ambiente.prevencion.tdr.bean;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.catalogos.facade.CatalogoGeneralFacade;
import ec.gob.ambiente.suia.catalogos.facade.MedioBioticoTdrFacade;
import ec.gob.ambiente.suia.catalogos.facade.MedioFisicoFacade;
import ec.gob.ambiente.suia.catalogos.facade.MedioSocialFacade;
import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoInterseccionFacade;
import ec.gob.ambiente.suia.comun.bean.CargarCoordenadasBean;
import ec.gob.ambiente.suia.coordenadageneral.facade.CoordenadaGeneralFacade;
import ec.gob.ambiente.suia.domain.Bloque;
import ec.gob.ambiente.suia.domain.CatalogoGeneral;
import ec.gob.ambiente.suia.domain.Consultor;
import ec.gob.ambiente.suia.domain.Coordenada;
import ec.gob.ambiente.suia.domain.CoordenadaGeneral;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.EquipoTecnico;
import ec.gob.ambiente.suia.domain.MedioBioticoTdr;
import ec.gob.ambiente.suia.domain.MedioFisico;
import ec.gob.ambiente.suia.domain.MedioSocial;
import ec.gob.ambiente.suia.domain.ObservacionTdrEiaLiciencia;
import ec.gob.ambiente.suia.domain.ProyectoBloque;
import ec.gob.ambiente.suia.domain.ProyectoGeneralCatalogo;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TdrEiaLicencia;
import ec.gob.ambiente.suia.domain.TipoForma;
import ec.gob.ambiente.suia.equipotecnico.facade.EquipoTecnicoFacade;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.observaciontdreialiciencia.facade.ObservacionTdrEiaFacade;
import ec.gob.ambiente.suia.prevencion.tdr.facade.TdrFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.classes.CoordinatesWrapper;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.proyectos.service.ProyectoGeneralCatalogoServiceBean;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class IngresarTDRBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5085022920397883926L;

	private static final Logger log = Logger.getLogger(IngresarTDRBean.class);

	@Setter
	@Getter
	private String area;

	@Setter
	@Getter
	private String registro;

	@Setter
	@Getter
	private String numeroResolucion;

	@Setter
	@Getter
	private String urlPage;

	@Setter
	@Getter
	private String fileNameImageLocation = "Escoger un archivo";

	@Setter
	@Getter
	private File imageLocationFile;

	@Setter
	@Getter
	private StreamedContent image = null;

	@Getter
	@Setter
	private Documento imagenAlfresco;

	@Setter
	@Getter
	TdrEiaLicencia tdrEia;

	@Setter
	@Getter
	EquipoTecnico tecnicoActivo;

	@Setter
	@Getter
	private MedioFisico medioFisico;

	@Setter
	@Getter
	private MedioBioticoTdr medioBiotico;

	@Setter
	@Getter
	private MedioSocial medioSocial;

	@Setter
	@Getter
	private ProyectoLicenciamientoAmbiental proyectoActivo;

	@Getter
	@Setter
	ObservacionTdrEiaLiciencia observacionTdrEiaLiciencia;

	@Setter
	@Getter
	private List<Bloque> listaBloques;

	@Getter
	@Setter
	private List<Documento> documentos;

	@Setter
	@Getter
	private List<EquipoTecnico> equipoTecnicoLista;

	@Setter
	@Getter
	private List<MedioFisico> mediosFisicosSeleccionados;

	@Setter
	@Getter
	private List<MedioBioticoTdr> mediosBioticosSeleccionados;

	@Setter
	@Getter
	private List<MedioSocial> mediosSocialesSeleccionados;

	@Setter
	@Getter
	private List<Consultor> listaConsultores = new ArrayList<Consultor>();

	@Setter
	@Getter
	private List<Coordenada> coordinatesList = new ArrayList<Coordenada>();

	@Setter
	@Getter
	private List<CatalogoGeneral> listaMapasTematico = new ArrayList<CatalogoGeneral>();

	@Setter
	@Getter
	private List<CatalogoGeneral> listaMarcoLegal = new ArrayList<CatalogoGeneral>();

	@Setter
	@Getter
	private List<CatalogoGeneral> listaAnexos = new ArrayList<CatalogoGeneral>();

	@Setter
	@Getter
	private List<CatalogoGeneral> catalogoMedioFisico;

	@Setter
	@Getter
	private List<CatalogoGeneral> catalogoMedioBiotico;

	@Setter
	@Getter
	private List<CatalogoGeneral> catalogoMedioSocial;

	@EJB
	@Getter
	@Setter
	ObservacionTdrEiaFacade observacionTdrEiaFacade;

	@EJB
	private TdrFacade tdrFacade;

	@EJB
	private ProyectoGeneralCatalogoServiceBean proyectoGeneralCatalogoServiceBean;

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoFacade;

	@EJB
	EquipoTecnicoFacade equipoTecnicoFacade;

	@EJB
	MedioFisicoFacade medioFisicoFacade;

	@EJB
	MedioBioticoTdrFacade medioBioticoFacade;

	@EJB
	MedioSocialFacade medioSocialFacade;

	@EJB
	CatalogoGeneralFacade catalogoGeneralFacade;

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private CoordenadaGeneralFacade coordenadaGeneralFacade;

	@EJB
	private CertificadoInterseccionFacade certificadoInterseccionService;
	// @EJB
	// CatalogoCategoriasFlujoFacade catalogoCategoriasFlujoFacade;

	// @EJB
	// CrudServiceBean crudServiceBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{cargarCoordenadasBean}")
	private CargarCoordenadasBean cargarCoordenadasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{ingresarObservacionesTDRBean}")
	private IngresarObservacionesTDRBean ingresarObservacionesTDRBean;

	@Getter
	@Setter
	private boolean corregir;

	@Getter
	@Setter
	private boolean procesoIniciado;

	@Getter
	@Setter
	private boolean siNoInventarioForestalRadio;

	@Getter
	@Setter
	private boolean siNoAportesCapitalNaturalRadio;

	@Getter
	@Setter
	private boolean siNoIdentifSitiosContamOFuentesContaminacRadio;

	@Getter
	@Setter
	private boolean habilita;

	@Getter
	@Setter
	private boolean interseca;

	private Boolean corregir_par;

	private Map<String, Object> variables;

	private Boolean cumpleCriterios;

	private String promotor;

	@PostConstruct
	public void init() {
		inicializar();
		asignarProyectoActivo();

		try {
			tdrEia = tdrFacade.getTdrEiaLicenciaPorIdProyecto(proyectoActivo
					.getId());
		} catch (Exception e) {
			log.error("Error al consultar tdrEia", e);
		}
		if (tdrEia == null) {
			tdrEia = new TdrEiaLicencia();
			tdrEia.setProyecto(proyectoActivo);
			tdrFacade.saveOrUpdate(tdrEia);
		} else {
			equipoTecnicoLista = equipoTecnicoFacade
					.getEquipoTecnicoPorIdTdr(tdrEia.getId());
			mediosFisicosSeleccionados = medioFisicoFacade
					.getMediosFisicosSeleccionados(tdrEia.getId());
			mediosBioticosSeleccionados = medioBioticoFacade
					.getMediosBioticosSeleccionados(tdrEia.getId());
			mediosSocialesSeleccionados = medioSocialFacade
					.getMediosSocialesSeleccionados(tdrEia.getId());

			listaBloques = new ArrayList<Bloque>();
			proyectoActivo.getProyectoBloques().size();
			log.info(proyectoActivo.getProyectoBloques().size());

			for (ProyectoBloque b : proyectoActivo.getProyectoBloques()) {
				listaBloques.add(b.getBloque());
			}
		}

		evaluarObservaciones();

		if (!corregir && area == null && procesoIniciado) {
			JsfUtil.addMessageError("Ha ocurrido un error.");//
			JsfUtil.redirectTo("/proyectos/listadoProyectos.jsf");
		}
		if (area == null && proyectosBean != null
				&& proyectosBean.getProyecto() != null
				&& proyectosBean.getProyecto().getId() != null) {

		}

		documentos = tdrFacade.recuperarDocumentosTdr(tdrEia.getClass()
				.getSimpleName(), tdrEia.getId());
		if (!documentos.isEmpty()) {
			imagenAlfresco = documentos.get(0);
			this.fileNameImageLocation = imagenAlfresco.getNombre();
		}

		cargarMarcoLegal();
		cargarCoordenadas();
		cargarMapasTematicos();
		cargarAnexos();
		listaConsultores = tdrFacade.obtenerConsultores();
		observaciones();
		cargarCatalogosMedios();

		System.out.println(procesoIniciado);
		System.out.println(corregir);
		System.out.println(corregir_par);
		System.out.println(area);
	}

	/**
	 * Metodo para inicializar variables
	 */
	public void inicializar() {
		Map<String, String> params = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap();
		String area_tmp = params.get("area");
		String analizar_tmp = params.get("analizar");
		area = "";
		interseca = false;

		corregir = false;
		procesoIniciado = false;
		corregir_par = false;
		urlPage = "fichaTecnica/registerTdr1.xhtml";
		cargarCoordenadasBean.setUpdateComponentRoute("@(.coordenadas)");
		cargarCoordenadasBean.setModalLoadFile(false);
		equipoTecnicoLista = new ArrayList<EquipoTecnico>();
		tecnicoActivo = new EquipoTecnico();
		observacionTdrEiaLiciencia = new ObservacionTdrEiaLiciencia();
		tdrEia = new TdrEiaLicencia();
		variables = new HashMap<String, Object>();
		cumpleCriterios = false;
		promotor = "";
		documentos = new ArrayList<Documento>();
		catalogoMedioFisico = new ArrayList<CatalogoGeneral>();
		catalogoMedioBiotico = new ArrayList<CatalogoGeneral>();
		catalogoMedioSocial = new ArrayList<CatalogoGeneral>();
		mediosFisicosSeleccionados = new ArrayList<MedioFisico>();
		mediosBioticosSeleccionados = new ArrayList<MedioBioticoTdr>();
		mediosSocialesSeleccionados = new ArrayList<MedioSocial>();
		medioFisico = new MedioFisico();
		medioBiotico = new MedioBioticoTdr();
		medioSocial = new MedioSocial();

		if (area_tmp != null && !area_tmp.isEmpty()) {
			area = area_tmp;

		}
		// System.out.println("..Antes..");

		if ((analizar_tmp != null && !analizar_tmp.isEmpty())
				|| !area.isEmpty()) {
			procesoIniciado = true;

		}
		// System.out.println(procesoIniciado);
		// System.out.println(analizar_tmp);
		if (params.containsKey("corregir")) {
			corregir_par = Boolean.parseBoolean(params.get("corregir"));
			corregir = corregir_par;
		}

		// if (area != null) {
		// System.out
		// .println(".............. ...................................................................");
		// System.out.println(area);
		//
		// }
	}

	public void asignarProyectoActivo() {
		try {
			// Comprobar si existe un proyecto activo seleccionado y no se
			// solicita por area
			if (proyectosBean.getProyecto() != null && !procesoIniciado
					&& !corregir) { 
				proyectoActivo = proyectosBean.getProyecto();
				System.out.println(proyectoActivo.getId());
			} else if (bandejaTareasBean.getTarea() != null
					&& (procesoIniciado || corregir)) { 
				Map<String, Object> variables = procesoFacade
						.recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
								.getProcessInstanceId());
				Integer idProyecto = Integer.parseInt((String) variables
						.get(Constantes.ID_PROYECTO));
				promotor = "";
				cumpleCriterios = true;
				if (variables.containsKey("u_Promotor")) {
					promotor = (String) variables.get("u_Promotor");
				} 
				System.out.println(idProyecto);
				proyectoActivo = proyectoFacade
						.buscarProyectosLicenciamientoAmbientalPorId(idProyecto);
			} else {  
				// /redireccionar
			}
			interseca = certificadoInterseccionService
					.getValueInterseccionProyectoIntersecaCapas(proyectoActivo);
 
		} catch (Exception e) {
			log.error("Error al inicializar el proyecto", e);
		}
	}

	public void evaluarObservaciones() {
		if (corregir_par
				|| (proyectoActivo == null && area != null && !area.isEmpty())) {

			if (corregir_par && variables.containsKey("cumpleCriterios")) {
				cumpleCriterios = Boolean.parseBoolean((String) variables
						.get("cumpleCriterios"));
				if (promotor.equals(loginBean.getNombreUsuario())
						&& !cumpleCriterios) {
					corregir = true;
				}
			}
		}
	}

	public void cargarMarcoLegal() {
		try {
			listaMarcoLegal = catalogoGeneralFacade.obtenerCatalogoXTipo(27);

			List<ProyectoGeneralCatalogo> catalogosActuales = proyectoGeneralCatalogoServiceBean
					.listarProyectoGeneralCatalogoPorProyectoCategoria(
							proyectoActivo.getId(), 27);

			for (CatalogoGeneral cat : listaMarcoLegal) {
				for (ProyectoGeneralCatalogo proyectoGeneralCatalogo : catalogosActuales) {
					if (cat.equals(proyectoGeneralCatalogo.getCatalogoGeneral())) {
						cat.setSeleccionado(true);
					}
				}
			}

		} catch (Exception e1) {
			listaMarcoLegal = new ArrayList<CatalogoGeneral>();
		}
	}

	public void cargarCoordenadas() {
		List<CoordenadaGeneral> cooredenadasGeneral = coordenadaGeneralFacade
				.coordenadasGeneralXTablaId(tdrEia.getId(), tdrEia.getClass()
						.getSimpleName());
		Map<TipoForma, List<Coordenada>> coordenadas_tmp = new HashMap<TipoForma, List<Coordenada>>();
		TipoForma tipoForma = new TipoForma();
		for (CoordenadaGeneral coordenadaGeneral : cooredenadasGeneral) {
			tipoForma = coordenadaGeneral.getTipoForma();
			Coordenada coordenada_tmp = coordenadaGeneralFacade
					.convertirCoordenadaGeneralCordenada(coordenadaGeneral,
							proyectoActivo);
			if (coordenadas_tmp.containsKey(tipoForma)) {
				coordenadas_tmp.get(tipoForma).add(coordenada_tmp);
			} else {
				List<Coordenada> lista_temporal = new ArrayList<>();
				lista_temporal.add(coordenada_tmp);
				coordenadas_tmp.put(tipoForma, lista_temporal);
			}

		}
		List<CoordinatesWrapper> coordinatesWrappers = new ArrayList<CoordinatesWrapper>();

		Iterator<TipoForma> it = coordenadas_tmp.keySet().iterator();
		while (it.hasNext()) {

			TipoForma clave = it.next();
			if (clave.getId() == 1) {

				for (Coordenada coord : coordenadas_tmp.get(clave)) {
					System.out.println("...........a.aa.............aaa.");
					CoordinatesWrapper coordinatesWrapper = new CoordinatesWrapper();
					coordinatesWrapper.setTipoForma(clave);

					List<Coordenada> lista_temporal = new ArrayList<>();
					lista_temporal.add(coord);
					coordinatesWrapper.setCoordenadas(lista_temporal);
					coordinatesWrappers.add(coordinatesWrapper);
				}
			} else {

				CoordinatesWrapper coordinatesWrapper = new CoordinatesWrapper();
				coordinatesWrapper.setTipoForma(clave);
				coordinatesWrapper.setCoordenadas(coordenadas_tmp.get(clave));
				coordinatesWrappers.add(coordinatesWrapper);
			}

		}
		cargarCoordenadasBean.setCoordinatesWrappers(coordinatesWrappers);
	}

	public void cargarMapasTematicos() {
		try {
			listaMapasTematico = catalogoGeneralFacade.obtenerCatalogoXTipo(28);

			List<ProyectoGeneralCatalogo> catalogosActualesmt = proyectoGeneralCatalogoServiceBean
					.listarProyectoGeneralCatalogoPorProyectoCategoria(
							proyectoActivo.getId(), 28);

			for (CatalogoGeneral cat : listaMapasTematico) {
				for (ProyectoGeneralCatalogo proyectoGeneralCatalogo : catalogosActualesmt) {
					if (cat.equals(proyectoGeneralCatalogo.getCatalogoGeneral())) {
						cat.setSeleccionado(true);
					}
				}
			}
		} catch (Exception e) {
			listaMapasTematico = new ArrayList<CatalogoGeneral>();
		}
	}

	public void cargarAnexos() {
		try {

			listaAnexos = catalogoGeneralFacade.obtenerCatalogoXTipo(29);

			List<ProyectoGeneralCatalogo> catalogosActualesa = proyectoGeneralCatalogoServiceBean
					.listarProyectoGeneralCatalogoPorProyectoCategoria(
							proyectoActivo.getId(), 29);

			for (CatalogoGeneral cat : listaAnexos) {
				for (ProyectoGeneralCatalogo proyectoGeneralCatalogo : catalogosActualesa) {
					if (cat.equals(proyectoGeneralCatalogo.getCatalogoGeneral())) {
						cat.setSeleccionado(true);
					}
				}
			}
		} catch (Exception e) {
			listaAnexos = new ArrayList<CatalogoGeneral>();
		}
	}

	public void cargarCatalogosMedios() {
		try {
			catalogoMedioFisico = catalogoGeneralFacade
					.obtenerCatalogoMedioFisicoXIdProyecto(getProyectoActivo()
							.getId());

			catalogoMedioBiotico = catalogoGeneralFacade
					.obtenerCatalogoMedioBioticoXIdProyecto(getProyectoActivo()
							.getId());

			catalogoMedioSocial = catalogoGeneralFacade
					.obtenerCatalogoMedioSocialXIdProyecto(getProyectoActivo()
							.getId());
		} catch (Exception e) {
			log.error("Error al cargar ", e);
		}
	}

	public void observaciones() {
		if (procesoIniciado && area != null && !area.isEmpty()) {
			// ingresarObservacionesTDRBean.inicializarValores(tdrEia,
			// componente);
			observacionTdrEiaLiciencia = observacionTdrEiaFacade
					.getObservacionTdrEiaLicienciaPorIdTdrComponente(
							tdrEia.getId(), area);

			if (observacionTdrEiaLiciencia == null) {
				observacionTdrEiaLiciencia = new ObservacionTdrEiaLiciencia();
				observacionTdrEiaLiciencia.setComponente(area);
				observacionTdrEiaLiciencia.setTdrEia(tdrEia);
				observacionTdrEiaLiciencia = observacionTdrEiaFacade
						.guardarObservacionTdrEia(observacionTdrEiaLiciencia);
			} else {
				try {
					if (observacionTdrEiaLiciencia.getObs_antecedentes()) {
						JsfUtil.redirectTo("/prevencion/tdr/elaborarInformeTecnicoArea.jsf?area="
								+ area.trim());
					}
				} catch (Exception e) {
				}
			}
		}
	}

	public void actualizarPagina(String url) {
		try {
			tdrEia = tdrFacade.getTdrEiaLicenciaPorIdProyecto(proyectoActivo
					.getId());
		} catch (Exception e) {
		}
		if (tdrEia == null) {
			tdrEia = new TdrEiaLicencia();
			tdrEia.setProyecto(proyectoActivo);

		}
		this.urlPage = url;
	}

	public void actualizarProvinciaActiva() {

	}

	public void handleFileUpload(FileUploadEvent event) {

	}

	public void editarEquipoTecnico(EquipoTecnico equipo) {
		this.tecnicoActivo = equipo;
	}

	public void eliminarEquipoTecnico(EquipoTecnico equipo) {
		this.equipoTecnicoLista.remove(equipo);
		try {
			equipoTecnicoFacade.eliminarEquipoTecnico(equipo);
		} catch (Exception e) {
			log.error("Error al eliminar tecnico", e);
		}
	}

	public void adicionarEquipoTecnico() {
		if (equipoTecnicoLista.contains(this.tecnicoActivo)) {
			this.equipoTecnicoLista.set(
					this.equipoTecnicoLista.lastIndexOf(this.tecnicoActivo),
					tecnicoActivo);
		} else {
			tecnicoActivo.setTdrEiaLicencia(this.tdrEia);
			this.equipoTecnicoLista.add(this.tecnicoActivo);
		}
		try {
			equipoTecnicoFacade.adicionarEquipoTecnico(tecnicoActivo);
			RequestContext context = RequestContext.getCurrentInstance();
			JsfUtil.addMessageInfo("El equipo técnico ha sido guardado correctamente.");
			context.addCallbackParam("sucess", true);
		} catch (Exception e) {
			log.error("Error al adicionar equipo tecnico", e);
		}
		limpiarEquipoTecnico();
	}

	public void limpiarEquipoTecnico() {
		this.tecnicoActivo = new EquipoTecnico();
	}

	/**
	 * Metodo para guardar medio fisico
	 */
	public void adicionarMedio() {
		log.info("CREAR.....");
		try {
			medioFisico.setTdrEiaLicencia(getTdrEia());
			medioFisico.setProyectoLicenciamientoAmbiental(getProyectoActivo());

			if (habilita) {
				medioFisico.setAplicable("Si");
			} else {
				medioFisico.setAplicable("No");
				medioFisico.setTipoInformacion("Ninguna");
			}
			log.info("habilita: " + habilita);
			log.info("Descripcion Edit: "
					+ getMedioFisico().getCatalogoGeneral().getDescripcion());
			log.info("Aplicable Edit: " + getMedioFisico().getAplicable());

			// mediosFisicosSeleccionados.add(medioFisico);
			medioFisicoFacade.adicionarMedioFisico(medioFisico);
			mediosFisicosSeleccionados = medioFisicoFacade
					.getMediosFisicosSeleccionados(tdrEia.getId());
			JsfUtil.addMessageInfo("La información ha sido guardada correctamente.");
		} catch (Exception e) {
			log.info("Error al guardar medio", e);
			JsfUtil.addMessageError("La información No ha sido guardada.");
		}
	}

	public void limpiarMedioFisico() {
		log.info("LIMPIAR....");
		this.medioFisico = new MedioFisico();
		log.info("Proy: " + medioFisico.getAplicable());
	}

	public void seleccionarMedioFisico(MedioFisico medioFisico) {
		this.medioFisico = medioFisico;
		if (this.medioFisico.getAplicable().equals("Si")) {
			habilita = true;
		} else {
			habilita = false;
		}
	}

	public void eliminarMedioFisico(MedioFisico medioFisico) {
		this.mediosFisicosSeleccionados.remove(medioFisico);
		try {
			medioFisicoFacade.eliminarMedioFisico(medioFisico);
		} catch (Exception e) {
			log.error("Error al eliminar fisico", e);
		}
	}

	/**
	 * Metodo para guardar medio biotico
	 */
	public void adicionarMedioBiotico() {
		log.info("CREAR.....");
		try {
			medioBiotico.setTdrEiaLicencia(getTdrEia());
			medioBiotico
					.setProyectoLicenciamientoAmbiental(getProyectoActivo());

			if (habilita) {
				medioBiotico.setAplicable("Si");
			} else {
				medioBiotico.setAplicable("No");
				medioBiotico.setTipoInformacion("Ninguna");
			}
			log.info("habilita: " + habilita);
			log.info("DescripcionMB: "
					+ getMedioBiotico().getCatalogoGeneral().getDescripcion());
			log.info("AplicableMB: " + getMedioBiotico().getAplicable());

			// mediosFisicosSeleccionados.add(proyectoGeneralCatalogo);
			medioBioticoFacade.adicionarMedioBiotico(medioBiotico);

			mediosBioticosSeleccionados = medioBioticoFacade
					.getMediosBioticosSeleccionados(tdrEia.getId());
			JsfUtil.addMessageInfo("La información ha sido guardada correctamente.");
		} catch (Exception e) {
			log.info("Error al guardar medio biotico", e);
			JsfUtil.addMessageError("La información No ha sido guardada.");
		}
	}

	public void limpiarMedioBiotico() {
		log.info("LIMPIAR biotico....");
		this.medioBiotico = new MedioBioticoTdr();
		log.info("Proy: " + medioBiotico.getAplicable());
	}

	public void seleccionarMedioBiotico(MedioBioticoTdr medioBiotico) {
		this.medioBiotico = medioBiotico;
		if (this.medioBiotico.getAplicable().equals("Si")) {
			habilita = true;
		} else {
			habilita = false;
		}
	}

	public void eliminarMedioBiotico(MedioBioticoTdr medioBiotico) {
		this.mediosBioticosSeleccionados.remove(medioBiotico);
		try {
			medioBioticoFacade.eliminarMedioBiotico(medioBiotico);
			JsfUtil.addMessageInfo("El registro ha sido eliminado correctamente.");
		} catch (Exception e) {
			log.error("Error al eliminar medio biotico", e);
		}
	}

	/**
	 * Metodo para guardar medio social
	 */
	public void adicionarMedioSocial() {
		log.info("CREAR social.....");
		try {
			medioSocial.setTdrEiaLicencia(getTdrEia());
			medioSocial.setProyectoLicenciamientoAmbiental(getProyectoActivo());

			if (habilita) {
				medioSocial.setAplicable("Si");
			} else {
				medioSocial.setAplicable("No");
				medioSocial.setTipoInformacion("Ninguna");
			}
			log.info("habilita: " + habilita);
			log.info("Descripcion Edit: "
					+ getMedioSocial().getCatalogoGeneral().getDescripcion());
			log.info("Aplicable Edit: " + getMedioSocial().getAplicable());

			// mediosFisicosSeleccionados.add(proyectoGeneralCatalogo);
			medioSocialFacade.adicionarmedioSocial(medioSocial);

			mediosSocialesSeleccionados = medioSocialFacade
					.getMediosSocialesSeleccionados(tdrEia.getId());

			JsfUtil.addMessageInfo("La información ha sido guardada correctamente.");
		} catch (Exception e) {
			log.info("Error al guardar medio", e);
			JsfUtil.addMessageError("La información No ha sido guardada.");
		}
	}

	public void limpiarMedioSocial() {
		log.info("LIMPIAR social....");
		this.medioSocial = new MedioSocial();
		log.info("Proy: " + medioSocial.getAplicable());
	}

	public void seleccionarMedioSocial(MedioSocial medioSocial) {
		this.medioSocial = medioSocial;
		if (this.medioSocial.getAplicable().equals("Si")) {
			habilita = true;
		} else {
			habilita = false;
		}
	}

	public void eliminarMedioSocial(MedioSocial medioSocial) {
		this.mediosSocialesSeleccionados.remove(medioSocial);
		try {
			medioSocialFacade.eliminarmedioSocial(medioSocial);
			JsfUtil.addMessageInfo("El registro ha sido eliminado correctamente.");
		} catch (Exception e) {
			log.error("Error al eliminar medio biotico", e);
		}
	}

	public void adicionarFichaTecnica() {
		tdrFacade.saveOrUpdate(tdrEia);
		List<CoordenadaGeneral> cooredenadasGeneral = coordenadaGeneralFacade
				.coordenadasGeneralXTablaId(tdrEia.getId(), tdrEia.getClass()
						.getSimpleName());

		if (cargarCoordenadasBean.getCoordinatesWrappers().size() > 0
				|| cooredenadasGeneral.size() > 0) {

			List<CoordenadaGeneral> coordenas_general = new ArrayList<CoordenadaGeneral>();

			for (CoordenadaGeneral coordenadaGeneral : cooredenadasGeneral) {
				coordenadaGeneralFacade.eliminar(coordenadaGeneral);
			}
			for (CoordinatesWrapper coord : cargarCoordenadasBean
					.getCoordinatesWrappers()) {

				for (Coordenada coordenada : coord.getCoordenadas()) {
					CoordenadaGeneral cg = coordenadaGeneralFacade
							.convertirCoordenadaCordenadaGeneral(coordenada,
									tdrEia.getId(), tdrEia.getClass()
											.getSimpleName(), coord
											.getTipoForma());
					coordenadaGeneralFacade.guardar(cg);
					coordenas_general.add(cg);
				}

			}
			JsfUtil.addMessageInfo("Datos guardados correctamente.");
		} else {

			JsfUtil.addMessageError("Debe seleccionar las coordenadas.");
		}
	}

	public void guardarFichaTecnicaSimple() {
		tdrFacade.saveOrUpdate(tdrEia);
		JsfUtil.addMessageInfo("Datos guardados correctamente.");
	}

	public void adicionarAnexos() {
		Boolean listo = true;
		//
		List<CatalogoGeneral> anexosSeleccionados = new ArrayList<CatalogoGeneral>();

		for (CatalogoGeneral catalogoGeneral : listaAnexos) {
			if (catalogoGeneral.isSeleccionado()) {
				anexosSeleccionados.add(catalogoGeneral);
			}
		}

		List<CatalogoGeneral> mapasTematicoSeleccionados = new ArrayList<CatalogoGeneral>();

		for (CatalogoGeneral catalogoGeneral : listaMapasTematico) {
			if (catalogoGeneral.isSeleccionado()) {
				mapasTematicoSeleccionados.add(catalogoGeneral);
				listo = false;
			}
		}

		if (listo) {
			tdrFacade.saveOrUpdate(tdrEia);
			JsfUtil.addMessageInfo("Datos guardados correctamente.");
		}
		if (validarTdr()) {
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('iniciarProceso').show();");
		}
	}

	public void seleccionarMarcoLegal() {
		Boolean listo = true;

		List<CatalogoGeneral> marcosLegalSeleccionados = new ArrayList<CatalogoGeneral>();

		for (CatalogoGeneral catalogoGeneral : listaMarcoLegal) {
			if (catalogoGeneral.isSeleccionado()) {
				marcosLegalSeleccionados.add(catalogoGeneral);
			}
		}

		if (listo) {
			tdrFacade.saveOrUpdate(tdrEia);
			JsfUtil.addMessageInfo("Datos guardados correctamente.");
		}
	}

	public void handleFileUploadImageLocation(FileUploadEvent event) {

		imageLocationFile = JsfUtil.upload(event);
		fileNameImageLocation = imageLocationFile.getName();
		try {
			image = new DefaultStreamedContent(
					event.getFile().getInputstream(), "image/jpeg");
		} catch (IOException e) {
			System.out.println("NMaloooo");
		}

	}

	public void limpiarFormulario(Integer formulario) {
		switch (formulario) {
		case 1:
			tdrEia.setConsId(null);
			tdrEia.setTdelExecutionTime(null);
			tdrEia.setTdelReferentialInfluenceArea(null);

			break;
		case 2:
			tdrEia.setTdelBackground(null);
			tdrEia.setTdelGeneralGoals(null);
			tdrEia.setTdelSpecificsGoals(null);
			tdrEia.setTdelTechnicalScope(null);
			break;
		case 3:
			this.tdrEia.setTdelGeneralMethodology(null);
			this.tdrEia.setTdelDeterminationReferenceArea(null);
			this.tdrEia.setTdelMethodologyPhysicalAbiotic(null);
			this.tdrEia.setTdelMethodologyBiotic(null);
			this.tdrEia.setTdelMethodologySocioeconomicCultural(null);
			break;
		case 4:
			this.tdrEia.setTdelAdministrativeFramework(null);
			this.tdrEia.setTdelCharacteristicsProject(null);
			this.tdrEia.setTdelTypeInputs(null);
			this.tdrEia.setTdelAlternativesAnalysis(null);
			this.fileNameImageLocation = "Escoger un archivo";
			this.imageLocationFile = null;
			break;
		case 5:
			this.tdrEia.setTdelDefinitionAreaInfluence(null);
			this.tdrEia.setTdelMethodologyInfluenceArea(null);
			this.tdrEia.setTdelMethodologyDirectInfluenceArea(null);
			this.tdrEia.setTdelMethodologyIndirectSocialInfluenceArea(null);
			this.tdrEia.setTdelSensitiveAreas(null);
			this.tdrEia.setTdelMethodologyPhysicalSensitivity(null);
			this.tdrEia.setTdelMethodologySensitivityBiotic(null);
			this.tdrEia.setTdelMethodologySensitivitySocioeconomic(null);
			break;
		case 6:

			this.tdrEia.setTdelImpactAssessmentMethodology(null);
			this.tdrEia.setTdelRiskAnalysisMethodology(null);
			break;
		case 7:
			this.tdrEia.setTdelAssessmentEnvironmentalServices(null);
			this.tdrEia.setTdelIdentificationContaminatesSites(null);
			this.tdrEia.setTdelRequiredContributions(false);
			this.tdrEia.setTdelCapitalContributions(null);
			this.tdrEia.setTdelProjectDescription(null);
			this.tdrEia.setTdelRequiredPassiveEnvironmental(false);
			this.tdrEia.setTdelIdentificationMethodology(null);
			// this.tdrEia.setTdelImpactAssessmentMethodology(null);
			// this.tdrEia.setTdelRiskAnalysisMethodology(null);
			break;
		case 8:
			this.tdrEia.setTdelEnvironmentalManagementPlan(null);
			break;
		case 9:
			this.tdrEia.setTdelMonitoringPlan(null);
			break;
		case 10:
			this.tdrEia.setTdelDetailEconomicAspects(null);
			this.tdrEia.setTdelRequiredForestInventory(false);
			this.tdrEia.setTdelMethodologyForest(null);
			break;
		case 11:
			this.tdrEia.setTdelMapping(null);
			this.tdrEia.setTdelOtherThematicMaps(null);
			this.tdrEia.setTdelOtherAnnexes(null);

			break;

		default:
			break;
		}

	}

	public boolean validarTdr() {
		if (procesoIniciado) {
			return false;
		}
		boolean listo = false;
		try {

			if (tdrEia.getConsId() == null
					|| tdrEia.getTdelExecutionTime() == null
					|| tdrEia.getTdelReferentialInfluenceArea() == null
			/* || cargarCoordenadasBean.getCoordenadas().size() == 0 */) {
				return false;
			}

			if (tdrEia.getTdelBackground() == null
					|| tdrEia.getTdelGeneralGoals() == null
					|| tdrEia.getTdelSpecificsGoals() == null
					|| tdrEia.getTdelTechnicalScope() == null) {
				return false;
			}
			if (this.tdrEia.getTdelGeneralMethodology() == null
					|| this.tdrEia.getTdelDeterminationReferenceArea() == null
					|| this.tdrEia.getTdelMethodologyPhysicalAbiotic() == null
					|| this.tdrEia.getTdelMethodologyBiotic() == null
					|| this.tdrEia.getTdelMethodologySocioeconomicCultural() == null) {
				return false;
			}

			if (this.tdrEia.getTdelAdministrativeFramework() == null
					|| this.tdrEia.getTdelCharacteristicsProject() == null
					|| this.tdrEia.getTdelTypeInputs() == null
					|| this.tdrEia.getTdelAlternativesAnalysis() == null
					|| (this.imagenAlfresco == null && this.imageLocationFile == null)) {
				return false;
			}

			if (/*
				 * this.tdrEia.getTdelDefinitionAreaInfluence() == null ||
				 */this.tdrEia.getTdelMethodologyInfluenceArea() == null
					|| this.tdrEia.getTdelMethodologyDirectInfluenceArea() == null
					|| this.tdrEia
							.getTdelMethodologyIndirectSocialInfluenceArea() == null
					|| this.tdrEia.getTdelSensitiveAreas() == null
					|| this.tdrEia.getTdelMethodologyPhysicalSensitivity() == null
					|| this.tdrEia.getTdelMethodologySensitivityBiotic() == null
					|| this.tdrEia.getTdelMethodologySensitivitySocioeconomic() == null) {
				return false;
			}

			if (this.tdrEia.getTdelAssessmentEnvironmentalServices() == null
					|| this.tdrEia.getTdelIdentificationContaminatesSites() == null
					|| this.tdrEia.getTdelRequiredContributions() == null
					|| this.tdrEia.getTdelCapitalContributions() == null
					|| this.tdrEia.getTdelProjectDescription() == null
					|| this.tdrEia.getTdelRequiredPassiveEnvironmental() == null
					|| this.tdrEia.getTdelIdentificationMethodology() == null) {
				return false;
			}

			if (this.tdrEia.getTdelImpactAssessmentMethodology() == null
					|| this.tdrEia.getTdelRiskAnalysisMethodology() == null) {
				return false;
			}

			if (this.tdrEia.getTdelEnvironmentalManagementPlan() == null) {
				return false;
			}
			if (this.tdrEia.getTdelMonitoringPlan() == null) {
				return false;
			}

			if (this.tdrEia.getTdelDetailEconomicAspects() == null
					|| this.tdrEia.getTdelRequiredForestInventory() == null
					|| this.tdrEia.getTdelMethodologyForest() == null) {
				return false;
			}

			if (this.tdrEia.getTdelMapping() == null
					|| this.tdrEia.getTdelOtherThematicMaps() == null
					|| this.tdrEia.getTdelOtherAnnexes() == null) {
				return false;
			}

			for (CatalogoGeneral catalogoGeneral : this.listaAnexos) {
				if (catalogoGeneral.isSeleccionado()) {
					listo = true;
					break;
				}
			}
			listo = false;
			for (CatalogoGeneral catalogoGeneral : listaMapasTematico) {
				if (catalogoGeneral.isSeleccionado()) {
					listo = true;
					break;
				}
			}

		} catch (Exception e) {
			return false;
		}

		return listo;

	}
}
