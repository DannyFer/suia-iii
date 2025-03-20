package ec.gob.ambiente.suia.eia.ficha.controllers;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.catalogos.facade.CatalogoComponenteParticipacionFacade;
import ec.gob.ambiente.suia.comun.bean.CargarCoordenadasBean;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ProyectoLicenciaAmbientalFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.classes.CoordinatesWrapper;
import lombok.Getter;
import lombok.Setter;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.catalogos.mineria.facade.FaseMineraFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.coordenadageneral.facade.CoordenadaGeneralFacade;
import ec.gob.ambiente.suia.dto.EntityPersona;
import ec.gob.ambiente.suia.eia.facade.EstudioImpactoAmbientalFacade;
import ec.gob.ambiente.suia.eia.ficha.bean.EstudioImpactoAmbientalBean;
import ec.gob.ambiente.suia.eia.ficha.bean.FichaTecnicaBeanEia;
import ec.gob.ambiente.suia.eia.ficha.facade.ConsultorCalificadoFacade;
import ec.gob.ambiente.suia.eia.ficha.facade.ConsultorNoCalificadoFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.fichatecnica.facade.FichaTecnicaFacade;
import ec.gob.ambiente.suia.persona.facade.PersonaFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;


/**
 * @author lili
 */
@ManagedBean
@ViewScoped
public class FichaTecnicaControllerEia implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 3454694308122541752L;

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(FichaTecnicaControllerEia.class);

	@Getter
	@Setter
	private FichaTecnicaBeanEia fichaTecnicaBeanEia;

	@Getter
	@Setter
	private Documento documentoGeneral,documentoTituloMinero,documentoRegistroCalificacionMinero,documentoVigenciaDerechosMineros;

	@Getter
	@Setter
	private EstudioImpactoAmbiental estudio, estudioHistorico, estudioOriginal;

	@EJB
	private FichaTecnicaFacade fichaTecnicaFacade;

	@EJB
	private OrganizacionFacade organizacionFacade;

	@EJB
	private ConsultorCalificadoFacade consultorCalificadoFacade;

	@EJB
	private ConsultorNoCalificadoFacade consultorNoCalificadoFacade;

	@EJB
	private EstudioImpactoAmbientalFacade estudioImpactoAmbientalFacade;

	@EJB
	private ValidacionSeccionesFacade validacionSeccionesFacade;

	@EJB
	private DocumentosFacade documentosFacade;

	@EJB
	private PersonaFacade personaFacade;

	@EJB
	private FaseMineraFacade faseMineraFacade;

	@Getter
	private boolean existeDocumentoAdjuntoGeneral,existeDocumentoTituloMinero,existeDocumentoRegistroCalificacionMinero,existeDocumentoVigenciaDerechosMineros,esMineriaNoMetalicos;

	@EJB
	private CoordenadaGeneralFacade coordenadaGeneralFacade;

	@EJB
	private CatalogoComponenteParticipacionFacade catalogoComponenteParticipacionFacade;

	@EJB
	private ProyectoLicenciaAmbientalFacade proyectoLicenciaAmbientalFacade;
	
	@EJB
	private ProcesoFacade procesoFacade;

	@Getter
	@Setter
	private List<CatalogoComponenteParticipacion> listaCatalogoComponenteParticipacion;

	@Getter
	@Setter
	private List<ProyectoBloque> listaProyectoBloques;

	@Getter
	@Setter
	private boolean usuarioJuridico;

	@Getter
	@Setter
	private Documento cartaAceptacion;

	@Getter
	@Setter
	private Boolean existeCartaAceptacion;

	@Getter
	@Setter
	private Organizacion organizacion;

	@Getter
	@Setter
	private ZonasFase zonasFase;

	@Getter
	@Setter
	private List<ZonasFase> ListaZonasFases;

	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
	
	@ManagedProperty(value = "#{proyectosBean}")
	@Getter
	@Setter
	private ProyectosBean proyectosBean;
	
	@ManagedProperty(value = "#{estudioImpactoAmbientalBean}")
	@Getter
	@Setter
	private EstudioImpactoAmbientalBean estudioImpactoAmbientalBean;
	
	/**
	 * Cris F: Variables para historico
	 */
	
	private boolean existeObservaciones;
	
	@Getter
	@Setter
	private Documento documentoGeneralOriginal, cartaAceptacionOriginal, documentoTituloMineroOriginal, documentoRegistroCalificacionMineroOriginal, documentoVigenciaDerechosMinerosOriginal;
	
	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyecto;
	
	private Map<String, Object> processVariables;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
	
	private Integer numeroNotificaciones;
		
	@Getter
    @Setter
	public List<ConsultorNoCalificado> listaConsultoresNoCalificadosOriginales, listaConsultoresNoCalificadosEliminadas, consultoresNoCalificados;
	
	@Getter
	@Setter
	private List<CoordenadaGeneral> listaCoordenaGeneralesOriginales, listaCoordenadasGeneralesEliminadas, listaCoordenadas, listaCoordenadasVisualizacion;
	
	private String promotor;
	
	private Documento documentoGeneralHistorico, documentoTituloMineroH, documentoRegistroCalificacionMineroH, documentoVigenciaDerechosMinerosH, cartaAceptacionHistorico;
	@Getter
	private List<ConsultorNoCalificado> listaConsultorNoCalificadoHistorico;
	
	@Getter
	private List<EstudioImpactoAmbiental> listaHistorialEstudio, listaEstudioBeneficioHistorial, listaEstudioExplotacionHistorico;
		
	@Getter
	@Setter
	private List<Documento> listaDocumentosOriginales, documentoGeneralOriginalList, documentoTituloMineroOriginalList, documentoRegistroCalificacionMineroOriginalList,
			documentoVigenciaDerechosMinerosOriginalList, cartaAceptacionOriginalList;

	@Getter
	private List<Documento> documentosOriginales;
	
	@Getter
	@Setter
	private Integer totalConsultoresNoCalificadosModificados;
		
	@PostConstruct
	public void init() throws CmisAlfrescoException, JbpmException {
		
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
		
		documentoGeneral = new Documento();
		documentoTituloMinero = new Documento();
		documentoRegistroCalificacionMinero = new Documento();
		documentoVigenciaDerechosMineros = new Documento();
		this.existeDocumentoAdjuntoGeneral = false;
		this.existeDocumentoTituloMinero = false;
		this.existeDocumentoRegistroCalificacionMinero = false;
		this.existeDocumentoVigenciaDerechosMineros = false;
		cartaAceptacion = new Documento();
		this.existeCartaAceptacion = false;
		setFichaTecnicaBeanEia(new FichaTecnicaBeanEia());
		getFichaTecnicaBeanEia().iniciar();
		
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        estudio=(EstudioImpactoAmbiental) request.getSession().getAttribute("estudio");        
//		estudio = estudioImpactoAmbientalFacade.obtenerEIAPorId(((EstudioImpactoAmbiental) JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT)).getId());
        
        //Cristina Flores aumento de método para historico
        if(numeroNotificaciones > 0)
        	existeObservaciones = true;
      	//Fin CF
        
        esMineriaNoMetalicos=estudio.getProyectoLicenciamientoAmbiental().getCatalogoCategoria().getCodigo().equals("21.02.03.02") && estudio.getResumenEjecutivo()==null?true:false;
        
		getFichaTecnicaBeanEia().setEstudioImpactoAmbiental(estudio);
		cargarConsultoresNoCalificados(getFichaTecnicaBeanEia().getEstudioImpactoAmbiental());
		cargarConsultorCalificado();
		getFichaTecnicaBeanEia().setProyecto(getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().getProyectoLicenciamientoAmbiental());
		Organizacion org = new Organizacion();
		try {
			org = organizacionFacade.buscarPorRuc(loginBean.getUsuario().getNombre());
			if (!(org == null)){
				//walter--------
//				if (org.getCalificado() != null && org.getCalificado()){
//					consultoresUnicoCalificado(org.getId());
//				}else{
					consultoresUnicoCalificado(0);
//				}
				//walter---fin-----
			}else{
				consultoresUnicoCalificado(0);
			}
			
		} catch (ServiceException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
//		consultoresCalificados();
		validarSector();
		cargarClausulaConfidencialidad();
		
		//Cris F: inicializacion de listas.
		documentoGeneralOriginalList = new ArrayList<Documento>();
		documentoTituloMineroOriginalList = new ArrayList<Documento>();
		documentoRegistroCalificacionMineroOriginalList = new ArrayList<Documento>();
		documentoVigenciaDerechosMinerosOriginalList = new ArrayList<Documento>();
		cartaAceptacionOriginalList = new ArrayList<Documento>();		
		
		this.cargarAdjuntosEIA(TipoDocumentoSistema.FICHA_TECNICA_GEN);
		
		
		
		getFichaTecnicaBeanEia().setFasesMinerasSeleccionadas(JsfUtil.devuelveVector(getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().getFaseMinera()));
		accionFasesMineras();
		cargarFasesMineras();
		cargarCoordenadas(getFichaTecnicaBeanEia().getEstudioImpactoAmbiental());
		
		//Cristina Flores aumento para guardar en historico
		if(existeObservaciones){
			documentoGeneralOriginal = new Documento();
			cartaAceptacionOriginal = new Documento();
			documentoTituloMineroOriginal = new Documento();
			documentoRegistroCalificacionMineroOriginal = new Documento();
			documentoVigenciaDerechosMinerosOriginal = new Documento();
			
			listaHistorialEstudio = new ArrayList<EstudioImpactoAmbiental>();
			listaEstudioBeneficioHistorial = new ArrayList<EstudioImpactoAmbiental>();
			listaEstudioExplotacionHistorico = new ArrayList<EstudioImpactoAmbiental>();
			
			cargarRegistroAnterior();
			if(estudioOriginal != null){
				cargarConsultoresNoCalificadosVisualizacion();
			}
		}
		
		this.zonasFase = estudio.getZonasFase();
		if(estudio.getProyectoLicenciamientoAmbiental().getTipoSector().getId().equals(TipoSector.TIPO_SECTOR_HIDROCARBUROS)){
			listaCatalogoComponenteParticipacion = catalogoComponenteParticipacionFacade.obtenerListaCatalogoLibroRojo();
		}else {
			listaCatalogoComponenteParticipacion = catalogoComponenteParticipacionFacade.obtenerListaCatalogoLibroRojoNotHidrocarburo();
		}
		try {
			ListaZonasFases = proyectoLicenciaAmbientalFacade.getListaZonas();
			listaProyectoBloques = proyectoLicenciaAmbientalFacade.getProyectosBloquesPorIdProyecto(fichaTecnicaBeanEia.getProyecto().getId());
		}catch (Exception e){LOG.error(JsfUtil.MESSAGE_INFO_NO_RESULTADOS);}
		try {
			//estudio.setListaformaCoordenadasEIAs(new ArrayList<FormaCoordenadasEIA>());
			cargarCoodenadasParaEIA();
			Organizacion organizacion = organizacionFacade.buscarPorPersona(fichaTecnicaBeanEia.getProyecto().getUsuario().getPersona(), fichaTecnicaBeanEia.getProyecto().getUsuario().getNombre());
			if(organizacion == null) {
				usuarioJuridico = false;
			}
			else {
				usuarioJuridico = true;
			}
		}catch (Exception e){LOG.error(JsfUtil.MESSAGE_INFO_NO_RESULTADOS);}
		
	}

	public void guardar() {
		if (validarFichaTecnica()) {
			try {
				List<FormaCoordenadasEIA> listaformaCoordenadasEIAs = new ArrayList<>();
				if(fichaTecnicaBeanEia.getProyecto().getTipoSector().getId().equals(TipoSector.TIPO_SECTOR_HIDROCARBUROS)){
					 listaformaCoordenadasEIAs = cargarFormasCoordenadasEIA();
					getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().setListaformaCoordenadasEIAs(listaformaCoordenadasEIAs);
					getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().setTipoProyeccionParaCoordenada(JsfUtil.getBean(CargarCoordenadasBean.class).
							getTipoProyeccion().getNombre());
				}
				getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().setZonasFase(this.zonasFase);
				if (!getFichaTecnicaBeanEia().getConsultorNoCalificadosSeleccionados().isEmpty()) {
					if (getFichaTecnicaBeanEia().getVerFichaTecnicaMineria()) {
						if (getFichaTecnicaBeanEia().getFasesMinerasSeleccionadas() != null) {
							getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().setFaseMinera(
									JsfUtil.transformaVector(getFichaTecnicaBeanEia().getFasesMinerasSeleccionadas()));
						}
						if (getFichaTecnicaBeanEia().getVerExplotacion()) {
							if (!getFichaTecnicaBeanEia().getListaCoordenadasGenerales().isEmpty()) {
								if(existeObservaciones){
									fichaTecnicaFacade.guardarHistorico(getFichaTecnicaBeanEia().getEstudioImpactoAmbiental(),
											estudioHistorico,
											getFichaTecnicaBeanEia().getConsultorNoCalificadosSeleccionados(), getFichaTecnicaBeanEia()
											.getConsultoresNoCalificadosEliminados(), getFichaTecnicaBeanEia().getListaCoordenadasGenerales(),
											getFichaTecnicaBeanEia().getCoordenadasGeneralEliminadas(), numeroNotificaciones, esMineriaNoMetalicos);
								}else{
									fichaTecnicaFacade.guardar(getFichaTecnicaBeanEia().getEstudioImpactoAmbiental(),
										getFichaTecnicaBeanEia().getConsultorNoCalificadosSeleccionados(), getFichaTecnicaBeanEia()
												.getConsultoresNoCalificadosEliminados(), getFichaTecnicaBeanEia().getListaCoordenadasGenerales(),
										getFichaTecnicaBeanEia().getCoordenadasGeneralEliminadas(), listaformaCoordenadasEIAs);
								}
								JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
								salvarDocumentos();
								validacionSeccionesFacade.guardarValidacionSeccion("EIA", "fichaTecnicaEIA", fichaTecnicaBeanEia
										.getEstudioImpactoAmbiental().getId().toString());
								JsfUtil.redirectTo("/prevencion/licenciamiento-ambiental/eia/ficha/fichaTecnicaEIA.jsf");
									
							}
							return;
						}
					}
					if(existeObservaciones){
						fichaTecnicaFacade.guardarHistorico(getFichaTecnicaBeanEia().getEstudioImpactoAmbiental(),
								estudioHistorico,
								getFichaTecnicaBeanEia().getConsultorNoCalificadosSeleccionados(), getFichaTecnicaBeanEia()
								.getConsultoresNoCalificadosEliminados(), getFichaTecnicaBeanEia().getListaCoordenadasGenerales(),
								getFichaTecnicaBeanEia().getCoordenadasGeneralEliminadas(), numeroNotificaciones, esMineriaNoMetalicos);
					}else{
						fichaTecnicaFacade.guardar(getFichaTecnicaBeanEia().getEstudioImpactoAmbiental(),
								getFichaTecnicaBeanEia().getConsultorNoCalificadosSeleccionados(), getFichaTecnicaBeanEia()
										.getConsultoresNoCalificadosEliminados(), listaformaCoordenadasEIAs);
					}
					JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
					salvarDocumentos();
					validacionSeccionesFacade.guardarValidacionSeccion("EIA", "fichaTecnicaEIA", fichaTecnicaBeanEia
							.getEstudioImpactoAmbiental().getId().toString());	
//					JsfUtil.redirectTo("/prevencion/licenciamiento-ambiental/eia/ficha/fichaTecnicaEIA.jsf");
					
				}
				JsfUtil.redirectTo("/prevencion/licenciamiento-ambiental/eia/ficha/fichaTecnicaEIA.jsf");
			} catch (Exception e) {
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				LOG.error("Error al guardar ficha tecnica en estudio de impacto ambiental", e);
			}
		}
	}
	
	public List<FormaCoordenadasEIA> cargarFormasCoordenadasEIA() {
		List<FormaCoordenadasEIA> listaformaCoordenadasEIAs = new ArrayList<>();
		Iterator<CoordinatesWrapper> coords = JsfUtil.getBean(CargarCoordenadasBean.class).getCoordinatesWrappers()
				.iterator();
		while (coords.hasNext()) {
			CoordinatesWrapper coordinatesWrapper = coords.next();
			FormaCoordenadasEIA formaC = new FormaCoordenadasEIA();
			formaC.setTipoForma(coordinatesWrapper.getTipoForma());
			formaC.setCoordenadas(coordinatesWrapper.getCoordenadas());
			formaC.setEstudioImpactoAmbiental(getFichaTecnicaBeanEia().getEstudioImpactoAmbiental());
			listaformaCoordenadasEIAs.add(formaC);
		}
		if (listaformaCoordenadasEIAs!= null && !listaformaCoordenadasEIAs.isEmpty()) {
			return listaformaCoordenadasEIAs;
		} else {
			return null;
		}
	}
	public void cargarCoodenadasParaEIA()throws Exception{
		List<CoordinatesWrapper> listaCoord = new ArrayList<>();
		List<FormaCoordenadasEIA> listaF = fichaTecnicaFacade.getListaFormas(this.estudio.getId());
		//System.out.println(listaF.size());
		for(FormaCoordenadasEIA formaC : listaF){
			CoordinatesWrapper coordG = new CoordinatesWrapper();
			coordG.setTipoForma(formaC.getTipoForma());
			coordG.setCoordenadas(formaC.getCoordenadas());
			listaCoord.add(coordG);
		}
		JsfUtil.getBean(CargarCoordenadasBean.class).setCoordinatesWrappers(listaCoord);
		JsfUtil.getBean(CargarCoordenadasBean.class).setTipoProyeccionByName(this.estudio.getTipoProyeccionParaCoordenada());
	}

	/**
	 * Carga catalogo de fases mineras
	 */
	public void cargarFasesMineras() {
		try {
			List<FaseMinera> catalogoFasesMineras = faseMineraFacade.listarTodasFasesMineras();
			for (FaseMinera faseMinera : catalogoFasesMineras) {
				getFichaTecnicaBeanEia().getListaFasesMineras().add(new SelectItem(faseMinera.getId().toString(), faseMinera.getNombre()));
			}
		} catch (ServiceException e) {
			LOG.error("Error al cargar catalogo de fases mineras.", e);
		}
	}

	public void accionTieneFasesMineras() {
		if (!getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().getTieneFasesMineras()) {
			getFichaTecnicaBeanEia().setFasesMinerasSeleccionadas(null);
			getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().setFaseMinera(null);
			getFichaTecnicaBeanEia().setVerBeneficio(false);
			getFichaTecnicaBeanEia().setVerExplotacion(false);
		}
	}

	/**
	 *
	 */
	public void accionFasesMineras() {
		getFichaTecnicaBeanEia().setVerBeneficio(false);
		getFichaTecnicaBeanEia().setVerExplotacion(false);
		String[] lista = getFichaTecnicaBeanEia().getFasesMinerasSeleccionadas();
		for (String string : lista) {
			if (string.equals(FaseMinera.BENEFICIO)) {
				getFichaTecnicaBeanEia().setVerBeneficio(true);
			}
			if (string.equals(FaseMinera.EXPLOTACION)) {
				getFichaTecnicaBeanEia().setVerExplotacion(true);
			}
		}
	}

	public boolean validarFichaTecnica() {
		List<String> listaMensajes = new ArrayList<String>();
		if (this.documentoGeneral.getNombre() == null && !fichaTecnicaBeanEia.getProyecto().getTipoSector().getId().equals(TipoSector.TIPO_SECTOR_HIDROCARBUROS)) {
			listaMensajes.add("El campo 'Adjuntar plano de implantación' es requerido.");
		}
		if (this.cartaAceptacion.getNombre() == null && fichaTecnicaBeanEia.getProyecto().getTipoSector().getId().equals(TipoSector.TIPO_SECTOR_HIDROCARBUROS)) {
			listaMensajes.add("El campo 'Adjuntar carta de aceptación' es requerido.");
		}
		if(esMineriaNoMetalicos)
		{
			if (this.documentoTituloMinero.getNombre() == null) {
				listaMensajes.add("El campo 'Adjuntar Título Minero' es requerido.");
			}
			if (this.documentoRegistroCalificacionMinero.getNombre() == null) {
				listaMensajes.add("El campo 'Adjuntar Registro Calificación Minero' es requerido.");
			}
			if (this.documentoVigenciaDerechosMineros.getNombre() == null) {
				listaMensajes.add("El campo 'Adjuntar Vigencia Derechos Mineros' es requerido.");
			}
		}
		
		if (getFichaTecnicaBeanEia().getConsultorSeleccionado() == null) {
			listaMensajes.add("El campo 'Consultor calificado' es requerido.");
		} else {
			getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().setConsultor(
					getFichaTecnicaBeanEia().getConsultorSeleccionado());
		}
		if (getFichaTecnicaBeanEia().getConsultorNoCalificadosSeleccionados().isEmpty()) {
			listaMensajes.add("El campo 'Equipo consultor' es requerido.");
		}
		if (getFichaTecnicaBeanEia().getVerFichaTecnicaMineria()) {
			validarFichaTecnicaMineria();
		}
		if (!getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().getAceptaClausula()) {
			listaMensajes.add("Debe aceptar la cláusula de responsabilidad.");
		}
		if (listaMensajes.isEmpty()) {
			return true;
		} else {
			JsfUtil.addMessageError(listaMensajes);
			return false;
		}
	}

	public boolean validarFichaTecnicaMineria() {
		List<String> listaMensajes = new ArrayList<String>();
		if (getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().getTieneFasesMineras() == null) {
			listaMensajes.add("El campo 'Tiene fases mineras' es requerido.");
		} else {
			if (getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().getTieneFasesMineras()) {
				if (getFichaTecnicaBeanEia().getFasesMinerasSeleccionadas() == null) {
					listaMensajes.add("El campo 'Fases mineras' es requerido.");
				} else {
					if (getFichaTecnicaBeanEia().getVerBeneficio()) {
						validarBeneficio();
					}
					if (getFichaTecnicaBeanEia().getVerExplotacion()) {
						validarExplotacion();
					}
				}
			} else {
				getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().setFaseMinera(null);
				limpiarFasesMineras();
			}
		}
		if (listaMensajes.isEmpty()) {
			return true;
		} else {
			JsfUtil.addMessageError(listaMensajes);
			return false;
		}
	}

	public boolean validarExplotacion() {
		List<String> listaMensajes = new ArrayList<String>();
		if (getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().getMetodoDeExplotacion().equals(""))
			listaMensajes.add("El campo 'Método de explotación' es requerido.");
		if (getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().getNumeroDeFrentesExplotacion().equals(""))
			listaMensajes.add("El campo 'Número de frentes' es requerido.");
		if (getFichaTecnicaBeanEia().getListaCoordenadasGenerales().isEmpty())
			listaMensajes.add("El campo 'Coordenadas' es requerido.");
		if (getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().getVolumenDeExplotacion().equals(""))
			listaMensajes.add("El campo 'Volumen de explotación' es requerido.");
		if (getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().getUnidadMedidaVolumen().equals(""))
			listaMensajes.add("El campo 'Unidad medida volumen' es requerido.");
		if (getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().getTieneConstruccionEscombreras().equals(""))
			listaMensajes.add("El campo 'Construcción de escombreras' es requerido.");
		if (getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().getLocalizacionEscombreras().equals(""))
			listaMensajes.add("El campo 'Localización de la escombrera' es requerido.");
		if (getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().getCapacidadEscombrera().equals(""))
			listaMensajes.add("El campo 'Capacidad de la escombrera' es requerido.");
		if (getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().getEstadoExplotacion().equals(""))
			listaMensajes.add("El campo 'Estado de la escombrera' es requerido.");
		if (listaMensajes.isEmpty()) {
			return true;
		} else {
			JsfUtil.addMessageError(listaMensajes);
			return false;
		}
	}

	public boolean validarBeneficio() {
		List<String> listaMensajes = new ArrayList<String>();
		if (getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().getActividadBeneficio().equals(""))
			listaMensajes.add("El campo 'Actividades' es requerido.");
		if (getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().getRecuperacionMetalica().equals(""))
			listaMensajes.add("El campo 'Recuperación metálica' es requerido.");
		if (getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().getTieneConstruccionRelaves().equals(""))
			listaMensajes.add("El campo 'Construcción de relaveras' es requerido.");
		if (getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().getLocalizacionRelaves().equals(""))
			listaMensajes.add("El campo 'Localización y disposición de relaveras' es requerido.");
		if (getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().getCapacidad().equals(""))
			listaMensajes.add("El campo 'Capacidad' es requerido.");
		if (getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().getEstadoBeneficio().equals(""))
			listaMensajes.add("El campo 'Estado de beneficio' es requerido.");
		if (listaMensajes.isEmpty()) {
			return true;
		} else {
			JsfUtil.addMessageError(listaMensajes);
			return false;
		}
	}

	public void limpiarFasesMineras() {
		getFichaTecnicaBeanEia().setVerBeneficio(false);
		getFichaTecnicaBeanEia().setVerExplotacion(false);
		getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().setMetodoDeExplotacion(null);
		getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().setVolumenDeExplotacion(null);
		getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().setTieneConstruccionEscombreras(null);
		getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().setLocalizacionEscombreras(null);
		getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().setEstadoExplotacion(null);
		getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().setActividadBeneficio(null);
		getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().setRecuperacionMetalica(null);
		getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().setTieneConstruccionRelaves(null);
		getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().setLocalizacionRelaves(null);
		getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().setCapacidad(null);
		getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().setEstadoBeneficio(null);
		getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().setCapacidadEscombrera(null);
		getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().setNumeroDeFrentesExplotacion(null);
	}

	public void validarSector() {
		getFichaTecnicaBeanEia().setVerFichaTecnicaMineria(isSector(TipoSector.TIPO_SECTOR_MINERIA));
	}

	private boolean isSector(int sectorId) {
		return getFichaTecnicaBeanEia().getProyecto().getTipoSector().getId().equals(sectorId);
	}

	public void cargarConsultorCalificado() {
		try {
			Consultor consultor = (Consultor)consultorCalificadoFacade.buscarConsultor(estudio.getIdConsultor());
			
			getFichaTecnicaBeanEia().setConsultorSeleccionado(consultor);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	public void cargarConsultoresNoCalificados(EstudioImpactoAmbiental estudio) {
		try {
			List<ConsultorNoCalificado> consultoresNoCalificadosEnBase = consultorNoCalificadoFacade
					.listaConsultoresNoCalificadosPorEia(estudio);
			if (!consultoresNoCalificadosEnBase.isEmpty() && consultoresNoCalificadosEnBase != null) {
				getFichaTecnicaBeanEia().setConsultorNoCalificadosSeleccionados(consultoresNoCalificadosEnBase);
			}
		} catch (Exception e) {
			LOG.info("Error al cargar consultor no calificado.", e);
		}
	}

	/**
	 * Método para cargar coordenadas que se encuentren guardadas para el EIA
	 *
	 * @param estudio
	 */
	public void cargarCoordenadas(EstudioImpactoAmbiental estudio) {
		try {			
			List<CoordenadaGeneral> coordenadaGeneralesEnBase = coordenadaGeneralFacade.coordenadasGeneralXTablaId(estudio.getId(), EstudioImpactoAmbiental.PROCESS_NAME);
			if (!coordenadaGeneralesEnBase.isEmpty() && coordenadaGeneralesEnBase != null) {
				fichaTecnicaBeanEia.setListaCoordenadasGenerales(coordenadaGeneralesEnBase);
				
				if(existeObservaciones){
//					if (!promotor.equals(loginBean.getNombreUsuario())) {
						cargarCoordenadasVisualizacion(coordenadaGeneralesEnBase);
//					}
				}
			}
		} catch (Exception e) {
			LOG.info("Error al cargar consultor no calificado.", e);
		}
	}

	public void cancelar() {
		JsfUtil.redirectTo("/prevencion/licenciamiento-ambiental/eia/ficha/fichaTecnicaEIA.jsf");
	}

	/**
	 * Obtiene el nombre del proponente
	 *
	 * @return
	 */
	public String getProponente() {
		String label = getFichaTecnicaBeanEia().getProyecto().getUsuario().getPersona().getNombre();
		try {
			Organizacion organizacion = organizacionFacade.buscarPorPersona(getFichaTecnicaBeanEia().getProyecto()
					.getUsuario().getPersona(), getFichaTecnicaBeanEia().getProyecto().getUsuario().getNombre());
			if (organizacion != null) {
				return organizacion.getNombre();
			}
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		return label;
	}

	public boolean isLabelAdicionar() {
		return getFichaTecnicaBeanEia().getConsultorSeleccionado() != null;
	}

	/**
	 * Lista de todos los consultores calificados
	 */
	public void consultoresCalificados() {
		try {
			getFichaTecnicaBeanEia().setListaConsultoresCalificados(consultorCalificadoFacade.consultoresCalificados());
		} catch (ServiceException e) {
			LOG.info("Error al consultar consultores calificados.", e);
		}
	}
	
	public void consultoresUnicoCalificado(Integer id) {
		try {
			getFichaTecnicaBeanEia().setListaConsultoresCalificados(consultorCalificadoFacade.consultoresUnicoCalificados(id));
		} catch (ServiceException e) {
			LOG.info("Error al consultar consultores calificados.", e);
		}
	}

	/**
	 * Seleccionar consultor de la lista de consultores calificados
	 */
	public void seleccionarConsultor(Consultor consultor) {
		getFichaTecnicaBeanEia().setConsultorSeleccionado(null);
		getFichaTecnicaBeanEia().setConsultorSeleccionado(consultor);		
		JsfUtil.addCallbackParam("consultorCalificado");
	}

	public void limpiarConsultorNoCalificado() {
		seleccionarConsultorNoCalificado(new ConsultorNoCalificado());
	}

	public void seleccionarConsultorNoCalificado(ConsultorNoCalificado consultorNoCalificado) {
		getFichaTecnicaBeanEia().setConsultorNoCalificado(consultorNoCalificado);
	}

	/**
	 * Agregar consultor no calificado a la lista de seleccionados
	 */
	public void agregarConsultorNoCalificado() {
		if (!getFichaTecnicaBeanEia().getConsultorNoCalificadosSeleccionados().contains(
				getFichaTecnicaBeanEia().getConsultorNoCalificado())) {
			getFichaTecnicaBeanEia().getConsultorNoCalificadosSeleccionados().add(
					getFichaTecnicaBeanEia().getConsultorNoCalificado());
			getFichaTecnicaBeanEia().setConsultorNoCalificado(null);
		}
		JsfUtil.addCallbackParam("addConsultorNoCalificado");
	}

	/**
	 * Eliminar consultor no calificado
	 *
	 * @param consultorNoCalificado
	 */
	public void eliminarConsultorNoCalificado(ConsultorNoCalificado consultorNoCalificado) {
		getFichaTecnicaBeanEia().getConsultorNoCalificadosSeleccionados().remove(consultorNoCalificado);
		if(consultorNoCalificado.getId() != null)
			getFichaTecnicaBeanEia().getConsultoresNoCalificadosEliminados().add(consultorNoCalificado);
	}

	public void cargarClausulaConfidencialidad() {
		try {
			EntityPersona representante = personaFacade.getPersonaRepresentanteProyecto(getFichaTecnicaBeanEia().getProyecto());
			String clausula = new StringBuilder()
					.append("Yo, ")
					.append(representante.getNombre())
					.append(" con cédula de identidad ")
					.append(representante.getPin())
					.append(". Declaro bajo juramento que los especialistas que constan en el equipo técnico de la consultora")
					.append("")
					.append(", son los que formaron parte de la estructuración del estudio en cada uno de sus componentes. En caso de falsedad o modificación a lo mencionado, asumimos solidariamente las responsabilidades y sanciones determinadas por la ley.")
					.toString();
			getFichaTecnicaBeanEia().setClausula(clausula);
		} catch (ServiceException e) {

		}
	}

	/**
	 * Método para agregar coordenadas
	 */
	public void agregarCoordenada() {
		if (!getFichaTecnicaBeanEia().getListaCoordenadasGenerales().contains(getFichaTecnicaBeanEia().getCoordenadaGeneral())) {
			getFichaTecnicaBeanEia().getListaCoordenadasGenerales().add(getFichaTecnicaBeanEia().getCoordenadaGeneral());
			getFichaTecnicaBeanEia().setCoordenadaGeneral(null);
		}
		JsfUtil.addCallbackParam("addCordenadas");
	}

	/**
	 * Método para limpiar coordenada general
	 */
	public void limpiarCoordenada() {
		seleccionarCoordenada(new CoordenadaGeneral());
	}

	/**
	 * Método para seleccionar una coordenada
	 *
	 * @param coordenadaGeneral
	 */
	public void seleccionarCoordenada(CoordenadaGeneral coordenadaGeneral) {
		getFichaTecnicaBeanEia().setCoordenadaGeneral(coordenadaGeneral);
	}

	/**
	 * Eliminar coordenada general
	 *
	 * @param coordenadaGeneral
	 */
	public void eliminarCoordenada(CoordenadaGeneral coordenadaGeneral) {
		getFichaTecnicaBeanEia().getListaCoordenadasGenerales().remove(coordenadaGeneral);
		if(coordenadaGeneral.getId() != null)
			getFichaTecnicaBeanEia().getCoordenadasGeneralEliminadas().add(coordenadaGeneral);
	}

	/**
	 * Guarda el documento cargado
	 */
	public void salvarDocumentos() {
		try {
			documentoGeneral.setIdTable(this.estudio.getId());
			documentoGeneral.setDescripcion("Documento General");
			documentoGeneral.setEstado(true);
			if (documentoGeneral.getContenidoDocumento() != null) {
				Documento documento = documentosFacade.guardarDocumentoAlfresco(
						this.estudio.getProyectoLicenciamientoAmbiental().getCodigo(), Constantes.CARPETA_EIA, 0L,
						documentoGeneral, TipoDocumentoSistema.FICHA_TECNICA_GEN, null);
				
				if(existeObservaciones){
					if(documentoGeneralHistorico != null){
						documentoGeneralHistorico.setIdHistorico(documento.getId());
						documentoGeneralHistorico.setNumeroNotificacion(numeroNotificaciones);
						documentosFacade.actualizarDocumento(documentoGeneralHistorico);
					}
				}
			}
			cartaAceptacion.setIdTable(this.estudio.getId());
			cartaAceptacion.setDescripcion("Carta Aceptacion");
			cartaAceptacion.setEstado(true);
			if (cartaAceptacion.getContenidoDocumento() != null) {
				Documento documento = documentosFacade.guardarDocumentoAlfresco(
						this.estudio.getProyectoLicenciamientoAmbiental().getCodigo(), Constantes.CARPETA_EIA, 0L,
						cartaAceptacion, TipoDocumentoSistema.FICHA_TECNICA_GEN, null);
				
				if(existeObservaciones){
					if(cartaAceptacionHistorico != null){
						cartaAceptacionHistorico.setIdHistorico(documento.getId());
						documentosFacade.actualizarDocumento(cartaAceptacionHistorico);
					}
				}
			}
			if(esMineriaNoMetalicos){
				documentoTituloMinero.setIdTable(this.estudio.getId());
				documentoTituloMinero.setDescripcion("Titulo Minero");
				documentoTituloMinero.setEstado(true);
				if (documentoTituloMinero.getContenidoDocumento() != null) {
					Documento documento = documentosFacade.guardarDocumentoAlfresco(
							this.estudio.getProyectoLicenciamientoAmbiental().getCodigo(), Constantes.CARPETA_EIA, 0L,
							documentoTituloMinero, TipoDocumentoSistema.FICHA_TECNICA_GEN, null);
					
					if(existeObservaciones){
						if(documentoTituloMineroH != null){
							documentoTituloMineroH.setIdHistorico(documento.getId());
							documentoTituloMineroH.setNumeroNotificacion(numeroNotificaciones);
							documentosFacade.actualizarDocumento(documentoTituloMineroH);
						}
					}
				}
				documentoRegistroCalificacionMinero.setIdTable(this.estudio.getId());
				documentoRegistroCalificacionMinero.setDescripcion("Registro Calificacion Pequeño Minero");
				documentoRegistroCalificacionMinero.setEstado(true);
				if (documentoRegistroCalificacionMinero.getContenidoDocumento() != null) {
					Documento documento = documentosFacade.guardarDocumentoAlfresco(
							this.estudio.getProyectoLicenciamientoAmbiental().getCodigo(), Constantes.CARPETA_EIA, 0L,
							documentoRegistroCalificacionMinero, TipoDocumentoSistema.FICHA_TECNICA_GEN, null);
					
					if(existeObservaciones){
						if(documentoRegistroCalificacionMineroH != null){
							documentoRegistroCalificacionMineroH.setIdHistorico(documento.getId());
							documentoRegistroCalificacionMineroH.setNumeroNotificacion(numeroNotificaciones);
							documentosFacade.actualizarDocumento(documentoRegistroCalificacionMineroH);
						}
					}
				}
				documentoVigenciaDerechosMineros.setIdTable(this.estudio.getId());
				documentoVigenciaDerechosMineros.setDescripcion("Vigencia Derechos Mineros");
				documentoVigenciaDerechosMineros.setEstado(true);
				if (documentoVigenciaDerechosMineros.getContenidoDocumento() != null) {
					Documento documento = documentosFacade.guardarDocumentoAlfresco(
							this.estudio.getProyectoLicenciamientoAmbiental().getCodigo(), Constantes.CARPETA_EIA, 0L,
							documentoVigenciaDerechosMineros, TipoDocumentoSistema.FICHA_TECNICA_GEN, null);
					
					if(existeObservaciones){
						if(documentoVigenciaDerechosMinerosH != null){
							documentoVigenciaDerechosMinerosH.setIdHistorico(documento.getId());
							documentoVigenciaDerechosMinerosH.setNumeroNotificacion(numeroNotificaciones);
							documentosFacade.actualizarDocumento(documentoVigenciaDerechosMinerosH);
						}
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void uploadListenerDocumentoGeneral(FileUploadEvent event) {
		documentoGeneral = this.uploadListener(event, EstudioImpactoAmbiental.class, "pdf");
		this.existeDocumentoAdjuntoGeneral = true;
	}

	public void uploadListenerDocumentoAceptacion(FileUploadEvent event) {
		cartaAceptacion = this.uploadListener(event, EstudioImpactoAmbiental.class, "pdf");
		this.existeCartaAceptacion = true;
	}
	
	public void uploadListenerDocumentoTituloMinero(FileUploadEvent event) {
		documentoTituloMinero = this.uploadListener(event, EstudioImpactoAmbiental.class, "pdf");
		this.existeDocumentoTituloMinero = true;
	}
	
	public void uploadListenerDocumentoRegistroCalificacionMinero(FileUploadEvent event) {
		documentoRegistroCalificacionMinero = this.uploadListener(event, EstudioImpactoAmbiental.class, "pdf");
		this.existeDocumentoRegistroCalificacionMinero = true;
	}
	
	public void uploadListenerDocumentoVigenciaDerechosMineros(FileUploadEvent event) {
		documentoVigenciaDerechosMineros = this.uploadListener(event, EstudioImpactoAmbiental.class, "pdf");
		this.existeDocumentoVigenciaDerechosMineros= true;
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

	public StreamedContent getStreamContent(int indice) throws Exception {
		DefaultStreamedContent content = null;
		try {
		switch (indice) {
			case 0: {
					this.documentoGeneral = this.descargarAlfresco(this.documentoGeneral);
						if (documentoGeneral != null && documentoGeneral.getNombre() != null
								&& documentoGeneral.getContenidoDocumento() != null) {
							content = new DefaultStreamedContent(new ByteArrayInputStream(documentoGeneral.getContenidoDocumento()));
							content.setName(documentoGeneral.getNombre());
						} else
							JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
						break;
					}
			case 1:	{
					this.cartaAceptacion = this.descargarAlfresco(this.cartaAceptacion);
						if (cartaAceptacion != null && cartaAceptacion.getNombre() != null
								&& cartaAceptacion.getContenidoDocumento() != null) {
							content = new DefaultStreamedContent(new ByteArrayInputStream(cartaAceptacion.getContenidoDocumento()));
							content.setName(cartaAceptacion.getNombre());
						} else
							JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
						break;
					}
			case 2:	{
				this.documentoTituloMinero = this.descargarAlfresco(this.documentoTituloMinero);
					if (documentoTituloMinero != null && documentoTituloMinero.getNombre() != null
							&& documentoTituloMinero.getContenidoDocumento() != null) {
						content = new DefaultStreamedContent(new ByteArrayInputStream(documentoTituloMinero.getContenidoDocumento()));
						content.setName(documentoTituloMinero.getNombre());
					} else
						JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
					break;
				}
			case 3:	{
				this.documentoRegistroCalificacionMinero = this.descargarAlfresco(this.documentoRegistroCalificacionMinero);
					if (documentoRegistroCalificacionMinero != null && documentoRegistroCalificacionMinero.getNombre() != null
							&& documentoRegistroCalificacionMinero.getContenidoDocumento() != null) {
						content = new DefaultStreamedContent(new ByteArrayInputStream(documentoRegistroCalificacionMinero.getContenidoDocumento()));
						content.setName(documentoRegistroCalificacionMinero.getNombre());
					} else
						JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
					break;
				}
			case 4:	{
				this.documentoVigenciaDerechosMineros = this.descargarAlfresco(this.documentoVigenciaDerechosMineros);
					if (documentoVigenciaDerechosMineros != null && documentoVigenciaDerechosMineros.getNombre() != null
							&& documentoVigenciaDerechosMineros.getContenidoDocumento() != null) {
						content = new DefaultStreamedContent(new ByteArrayInputStream(documentoVigenciaDerechosMineros.getContenidoDocumento()));
						content.setName(documentoVigenciaDerechosMineros.getNombre());
					} else
						JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
					break;
				}
			default:
				LOG.error("Indice de archivo adjunto no esperado para descargar el adjunto");
				JsfUtil.addMessageError("Ocurrió un error al tratar de descargar el archivo, por favor comunicarse con mesa de ayuda.");
				break;
		}
		}catch (Exception exception) {
			LOG.error(JsfUtil.MESSAGE_ERROR_ALFRESCO, exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
		}
		return content;
	}

	private void cargarAdjuntosEIA(TipoDocumentoSistema tipoDocumento) throws CmisAlfrescoException {
//		List<Documento> documentosXEIA = documentosFacade.documentoXTablaIdXIdDoc(this.estudio.getId(),
//				"EstudioImpactoAmbiental", tipoDocumento);
		
		List<Documento> documentosXEIA = documentosFacade.documentosTodosXTablaIdXIdDoc(this.estudio.getId(),
				"EstudioImpactoAmbiental", tipoDocumento);	
		
		for(int i = 0; i < documentosXEIA.size(); i++) {
			if(documentosXEIA.get(i).getDescripcion().equals("Documento General")){
				if(!existeDocumentoAdjuntoGeneral){
					this.documentoGeneral = documentosXEIA.get(i);
					this.existeDocumentoAdjuntoGeneral = true;
					if(existeObservaciones){
						documentoGeneralHistorico = validarDocumentoHistorico(documentoGeneral, documentosXEIA);
//						if(!promotor.equals(loginBean.getNombreUsuario())){
							consultarDocumentosOriginales(documentoGeneral.getId(), documentosXEIA);
//						}
					}
				}
			}
			if(documentosXEIA.get(i).getDescripcion().equals("Carta Aceptacion")){
				if(!existeCartaAceptacion){
					this.cartaAceptacion = documentosXEIA.get(i);
					this.existeCartaAceptacion = true;
					if(existeObservaciones){
						cartaAceptacionHistorico = validarDocumentoHistorico(cartaAceptacion, documentosXEIA);
//						if(!promotor.equals(loginBean.getNombreUsuario())){
							consultarDocumentosOriginales(cartaAceptacion.getId(), documentosXEIA);
//						}
					}
				}
			}
			if(documentosXEIA.get(i).getDescripcion().equals("Titulo Minero")){
				if(!existeDocumentoTituloMinero && esMineriaNoMetalicos){
					this.documentoTituloMinero = documentosXEIA.get(i);
					this.existeDocumentoTituloMinero = true;
					if(existeObservaciones){
						documentoTituloMineroH = validarDocumentoHistorico(documentoTituloMinero, documentosXEIA);
//						if(!promotor.equals(loginBean.getNombreUsuario())){
							consultarDocumentosOriginales(documentoTituloMinero.getId(), documentosXEIA);
//						}
					}
				}
			}
			if(documentosXEIA.get(i).getDescripcion().equals("Registro Calificacion Pequeño Minero")){
				if(!existeDocumentoRegistroCalificacionMinero && esMineriaNoMetalicos){
					this.documentoRegistroCalificacionMinero = documentosXEIA.get(i);
					this.existeDocumentoRegistroCalificacionMinero = true;
					if(existeObservaciones){
						documentoRegistroCalificacionMineroH = validarDocumentoHistorico(documentoRegistroCalificacionMinero, documentosXEIA);
//						if(!promotor.equals(loginBean.getNombreUsuario())){
							consultarDocumentosOriginales(documentoRegistroCalificacionMinero.getId(), documentosXEIA);
//						}
					}
				}
			}
			if(documentosXEIA.get(i).getDescripcion().equals("Vigencia Derechos Mineros")){
				if(!existeDocumentoVigenciaDerechosMineros && esMineriaNoMetalicos){
					this.documentoVigenciaDerechosMineros = documentosXEIA.get(i);
					this.existeDocumentoVigenciaDerechosMineros = true;
					if(existeObservaciones){
						documentoVigenciaDerechosMinerosH = validarDocumentoHistorico(documentoVigenciaDerechosMineros, documentosXEIA);
//						if(!promotor.equals(loginBean.getNombreUsuario())){
							consultarDocumentosOriginales(documentoVigenciaDerechosMineros.getId(), documentosXEIA);
//						}
					}
				}
			}
		}
	}

	public Documento descargarAlfresco(Documento documento) throws CmisAlfrescoException {
		byte[] documentoContenido = null;
		if (documento != null && documento.getIdAlfresco() != null)
			documentoContenido = documentosFacade.descargar(documento.getIdAlfresco());
		if (documentoContenido != null)
			documento.setContenidoDocumento(documentoContenido);
		return documento;
	}
	
	/**
	 * Cristina Flores aumento de metódo para guardar el historico.
	 */	
	private void cargarRegistroAnterior(){
		try {
			if(validarGuardarHistoricoEA()){
				estudioHistorico = estudio.clone();
				
				Consultor consultor = (Consultor)consultorCalificadoFacade.buscarConsultor(estudio.getIdConsultor());
				estudioHistorico.setConsultor(consultor);
				estudioHistorico.setIdHistorico(estudio.getId());
				estudioHistorico.setNumeroNotificacion(numeroNotificaciones);
			}else{
				estudioHistorico = null;
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}	
	
	private Documento validarDocumentoHistorico(Documento documentoIngresado, List<Documento> documentosXEIA){
		try {
			List<Documento> documentosList = new ArrayList<>();
			for (Documento documento : documentosXEIA) {
				if (documento.getIdHistorico() != null && documento.getIdHistorico().equals(documentoIngresado.getId())
						&& documento.getNumeroNotificacion().equals(numeroNotificaciones)) {
					documentosList.add(documento);
				}
			}

			if (documentosList != null && !documentosList.isEmpty()) {
				return documentosList.get(0);
			} else {
				return documentoIngresado;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return documentoIngresado = new Documento();
		}		
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
	private boolean validarGuardarHistoricoEA(){		
		try {
			List<EstudioImpactoAmbiental> lista = estudioImpactoAmbientalFacade.busquedaRegistrosHistorico(estudio.getId());			
			/**
			 * Si el tamaño de la lista es el mismo que el número de notificiaciones entonces no se guarda
			 * Si la lista es nula entonces se guarda
			 * Si el tamaño de la lista es menor que el número de notificaciones se guarda
			 */
			
			if(lista != null && !lista.isEmpty()){
				obtenerHistoricoEstudio(lista);
				mostrarExplotacionModificado(lista);
				mostrarBeneficioModificado(lista);
				if (lista.size() == numeroNotificaciones) {
					estudioOriginal = lista.get(0);
				}
				
				if(numeroNotificaciones > lista.size())
					return true; // se guarda
				else
					return false;  //no se guarda
			}else {
				return true; // se guarda
			}				
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}		
	}
	
	/**
	 * MarielaG
	 * Consultar los consultores no calificados originales
	 */
	public void cargarConsultoresNoCalificadosOriginales() {
		try {
			listaConsultoresNoCalificadosOriginales = new ArrayList<>();
			
			List<ConsultorNoCalificado> consultoresModificados = consultorNoCalificadoFacade.buscarConsultoresNoCalificadosModificados(
							getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().getId());
			
			if(!consultoresModificados.isEmpty() && consultoresModificados != null){
				List<ConsultorNoCalificado> consultoresSeleccionados = getFichaTecnicaBeanEia().getConsultorNoCalificadosSeleccionados();
				if(!consultoresSeleccionados.isEmpty() && consultoresSeleccionados != null){
					for (ConsultorNoCalificado consultorOriginal : consultoresSeleccionados) {
						//si es un consultor que se ingreso originalmente
						//o si fue modificado en la modificacion anterior para ver originales ingresados en la modificacion anterior
						if (consultorOriginal.getNumeroNotificacion() == null ||
								!consultorOriginal.getNumeroNotificacion().equals(numeroNotificaciones)) {
							boolean agregarItemLista = true;
							//validar si el consultor seleccionado ha sido modificado (si tiene un registro historico)
							//para no ingresarlo en la lista de originales
							for (ConsultorNoCalificado consultorModificado : consultoresModificados) {
								if (consultorModificado.getIdHistorico() != null
										&& consultorModificado.getIdHistorico().equals(consultorOriginal.getId())) {
									agregarItemLista = false;
									break;
								}
							}
							if (agregarItemLista) {
								listaConsultoresNoCalificadosOriginales.add(consultorOriginal);
							}
						}
					}
				}
				
				for (ConsultorNoCalificado consultorModificado : consultoresModificados) {
					//si los consultores tienen idHistorico fueron modificados
					if (consultorModificado.getIdHistorico() != null) {
						if (!listaConsultoresNoCalificadosOriginales.contains(consultorModificado)) {
							listaConsultoresNoCalificadosOriginales.add(consultorModificado);
						}
					}
				}
			}else{
				//CF
				listaConsultoresNoCalificadosOriginales = new ArrayList<ConsultorNoCalificado>();
			}
		} catch (Exception e) {
			LOG.info("Error al cargar consultor no calificado.", e);
		}
	}
	
	/**
	 * MarielaG
	 * Descargar archivos originales 
	 * Cris F: modificado para que el switch reconozca strings
	 */
	public StreamedContent getStreamContentOriginal(Documento documento) throws Exception {
		DefaultStreamedContent content = null;
		try {
		switch (documento.getDescripcion()) {
			case "Documento General": {
					this.documentoGeneralOriginal = this.descargarAlfresco(documento);
						if (documentoGeneralOriginal != null && documentoGeneralOriginal.getNombre() != null
								&& documentoGeneralOriginal.getContenidoDocumento() != null) {
							content = new DefaultStreamedContent(new ByteArrayInputStream(documentoGeneralOriginal.getContenidoDocumento()));
							content.setName(documentoGeneralOriginal.getNombre());
						} else
							JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
						break;
					}
			case "Carta Aceptacion":	{
					this.cartaAceptacionOriginal = this.descargarAlfresco(this.cartaAceptacionOriginal);
						if (cartaAceptacionOriginal != null && cartaAceptacionOriginal.getNombre() != null
								&& cartaAceptacionOriginal.getContenidoDocumento() != null) {
							content = new DefaultStreamedContent(new ByteArrayInputStream(cartaAceptacionOriginal.getContenidoDocumento()));
							content.setName(cartaAceptacionOriginal.getNombre());
						} else
							JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
						break;
					}
			case "Titulo Minero":	{
				this.documentoTituloMineroOriginal = this.descargarAlfresco(this.documentoTituloMineroOriginal);
					if (documentoTituloMineroOriginal != null && documentoTituloMineroOriginal.getNombre() != null
							&& documentoTituloMineroOriginal.getContenidoDocumento() != null) {
						content = new DefaultStreamedContent(new ByteArrayInputStream(documentoTituloMineroOriginal.getContenidoDocumento()));
						content.setName(documentoTituloMineroOriginal.getNombre());
					} else
						JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
					break;
				}
			case "Registro Calificacion Pequeño Minero":	{
				this.documentoRegistroCalificacionMineroOriginal = this.descargarAlfresco(this.documentoRegistroCalificacionMineroOriginal);
					if (documentoRegistroCalificacionMineroOriginal != null && documentoRegistroCalificacionMineroOriginal.getNombre() != null
							&& documentoRegistroCalificacionMineroOriginal.getContenidoDocumento() != null) {
						content = new DefaultStreamedContent(new ByteArrayInputStream(documentoRegistroCalificacionMineroOriginal.getContenidoDocumento()));
						content.setName(documentoRegistroCalificacionMineroOriginal.getNombre());
					} else
						JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
					break;
				}
			case "Vigencia Derechos Mineros":	{
				this.documentoVigenciaDerechosMinerosOriginal = this.descargarAlfresco(this.documentoVigenciaDerechosMinerosOriginal);
					if (documentoVigenciaDerechosMinerosOriginal != null && documentoVigenciaDerechosMinerosOriginal.getNombre() != null
							&& documentoVigenciaDerechosMinerosOriginal.getContenidoDocumento() != null) {
						content = new DefaultStreamedContent(new ByteArrayInputStream(documentoVigenciaDerechosMinerosOriginal.getContenidoDocumento()));
						content.setName(documentoVigenciaDerechosMinerosOriginal.getNombre());
					} else
						JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
					break;
				}
			default:
				LOG.error("Indice de archivo adjunto no esperado para descargar el adjunto");
				JsfUtil.addMessageError("Ocurrió un error al tratar de descargar el archivo, por favor comunicarse con mesa de ayuda.");
				break;
		}
		}catch (Exception exception) {
			LOG.error(JsfUtil.MESSAGE_ERROR_ALFRESCO, exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
		}
		return content;
	}
	
	/**
	 * Cris F: Historico
	 */	
	
	private void cargarCoordenadasVisualizacion(List<CoordenadaGeneral> coordenadaGeneralesEnBase){
		
		try {
			listaCoordenadas = new ArrayList<CoordenadaGeneral>();
			listaCoordenadas = coordenadaGeneralFacade.coordenadaGeneralConHistorico(estudio.getId(), EstudioImpactoAmbiental.PROCESS_NAME);
			
			if(listaCoordenadas != null && !listaCoordenadas.isEmpty()){
				
				List<CoordenadaGeneral> coordenadasOriginales = new ArrayList<CoordenadaGeneral>();
				List<CoordenadaGeneral> coordenadasEliminadas = new ArrayList<CoordenadaGeneral>();
				int totalModificados = 0;
				
				for(CoordenadaGeneral coordenadaBdd : coordenadaGeneralesEnBase){
					if(coordenadaBdd.getNumeroNotificacion() == null){
						boolean agregar = true;
						for(CoordenadaGeneral coordenadaHistorico : listaCoordenadas){
							if(coordenadaHistorico.getIdHistorico() != null && coordenadaHistorico.getNumeroNotificacion() == numeroNotificaciones 
									&& coordenadaHistorico.getIdHistorico().equals(coordenadaBdd.getId())){
								agregar = false;
								coordenadaBdd.setRegistroModificado(true);
								
								if(!agregar){
									coordenadasOriginales.add(coordenadaHistorico);
								}								
								break;							
							}
						}
						
					}else if(coordenadaBdd.getNumeroNotificacion() == numeroNotificaciones){
						totalModificados++;
						if(coordenadaBdd.getIdHistorico() == null){
							coordenadaBdd.setNuevoEnModificacion(true);
						}else{
							coordenadaBdd.setRegistroModificado(true);
							if(!coordenadasOriginales.contains(coordenadaBdd)){
								coordenadasOriginales.add(coordenadaBdd);							
							}
						}
					}								
					
				}
				
				for(CoordenadaGeneral coordenadaEliminada : listaCoordenadas){
					//Eliminados
					if(coordenadaEliminada.getIdHistorico() != null && coordenadaEliminada.getNumeroNotificacion() != null && 
							coordenadaEliminada.getNumeroNotificacion().equals(numeroNotificaciones)){
						boolean existe = false;
						for(CoordenadaGeneral coordenadaActual : coordenadaGeneralesEnBase){
							if(coordenadaActual.getId().equals(coordenadaEliminada.getIdHistorico())){
								existe = true;
								break;
							}
						}
						
						if(!existe){
							coordenadasEliminadas.add(coordenadaEliminada);
						}
					}
				}
				
				if(totalModificados > 0){
					this.listaCoordenaGeneralesOriginales = coordenadasOriginales;
				}
				listaCoordenadasGeneralesEliminadas = coordenadasEliminadas;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}	
		
	/**
	 * Cris F: Obtener lista de documentos originales 
	 */
	
	/**
	 * MarielaG
	 * Consultar los documentos originales
	 */
	private void consultarDocumentosOriginales(Integer idDocumento, List<Documento> documentosList){		
		try {			
			if (documentosList != null && !documentosList.isEmpty() && documentosList.size() > 1) {
				while (idDocumento > 0) {
					idDocumento = recuperarHistoricos(idDocumento, documentosList);
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
	private int recuperarHistoricos(Integer idDocumento, List<Documento> documentosList) {
		try {
			int nextDocument = 0;
			for (Documento documento : documentosList) {
				if (documento.getIdHistorico() != null && documento.getIdHistorico().equals(idDocumento)) {
					nextDocument = documento.getId();

					switch (documento.getDescripcion()) {
					case "Documento General":
						documentoGeneralOriginalList.add(0, documento);
						break;
					case "Titulo Minero":
						documentoTituloMineroOriginalList.add(0, documento);
						break;
					case "Registro Calificacion Pequeño Minero":
						documentoRegistroCalificacionMineroOriginalList.add(0, documento);
						break;					
					case "Vigencia Derechos Mineros":
						documentoVigenciaDerechosMinerosOriginalList.add(0, documento);
						break;					
					case "Carta Aceptacion":
						cartaAceptacionOriginalList.add(0, documento);
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

	public void obtenerListaElementos(String nombre) throws Exception {
		documentosOriginales = new ArrayList<Documento>();
		try {			
			if(nombre.equals("Documento General"))
				documentosOriginales = this.documentoGeneralOriginalList;
			else if(nombre.equals("Carta Aceptacion"))
				documentosOriginales = this.cartaAceptacionOriginalList;
			else if(nombre.equals("Titulo Minero"))
				documentosOriginales = this.documentoTituloMineroOriginalList;
			else if(nombre.equals("Registro Calificacion Pequeño Minero"))
				documentosOriginales = this.documentoRegistroCalificacionMineroOriginalList;
			else if(nombre.equals("Vigencia Derechos Mineros"))
				documentosOriginales = this.documentoVigenciaDerechosMinerosOriginalList;
			else
				documentosOriginales = new ArrayList<Documento>();
		}catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private void obtenerHistoricoEstudio(List<EstudioImpactoAmbiental> lista){
		try{
			listaHistorialEstudio = new ArrayList<EstudioImpactoAmbiental>();
			for(EstudioImpactoAmbiental estudioHistorico : lista){
				Consultor consultor = (Consultor)consultorCalificadoFacade.buscarConsultor(estudio.getIdConsultor());
				Consultor consultorHistorico = (Consultor)consultorCalificadoFacade.buscarConsultor(estudioHistorico.getIdConsultor());
								
				if(consultor != null && consultorHistorico != null && 
						!consultor.equals(consultorHistorico)){
					estudioHistorico.setConsultor(consultorHistorico);
					listaHistorialEstudio.add(0, estudioHistorico);
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void mostrarBeneficioModificado(List<EstudioImpactoAmbiental> lista){
		try {
			listaEstudioBeneficioHistorial = new ArrayList<EstudioImpactoAmbiental>();
			for(EstudioImpactoAmbiental estudioBeneficio : lista){
				
				if(((estudio.getActividadBeneficio() == null && estudioBeneficio.getActividadBeneficio() == null) || 
								estudio.getActividadBeneficio() != null && estudioBeneficio.getActividadBeneficio() != null && 
								estudio.getActividadBeneficio().equals(estudioBeneficio.getActividadBeneficio())) && 
						((estudio.getRecuperacionMetalica() == null && estudioBeneficio.getRecuperacionMetalica() == null) || 
								estudio.getRecuperacionMetalica() != null && estudioBeneficio.getRecuperacionMetalica() != null && 
								estudio.getRecuperacionMetalica().equals(estudioBeneficio.getRecuperacionMetalica())) && 
						((estudio.getTieneConstruccionRelaves() == null && estudioBeneficio.getTieneConstruccionRelaves() == null) || 
								estudio.getTieneConstruccionRelaves() != null && estudioBeneficio.getTieneConstruccionRelaves() != null && 
								estudio.getTieneConstruccionRelaves().equals(estudioBeneficio.getTieneConstruccionRelaves())) && 
						((estudio.getLocalizacionRelaves() == null && estudioBeneficio.getLocalizacionRelaves() == null) || 
								estudio.getLocalizacionRelaves() != null && estudioBeneficio.getLocalizacionRelaves() != null && 
								estudio.getLocalizacionRelaves().equals(estudioBeneficio.getLocalizacionRelaves())) && 
						((estudio.getCapacidad() == null && estudioBeneficio.getCapacidad() == null) || 
								estudio.getCapacidad() != null && estudioBeneficio.getCapacidad() != null && 
								estudio.getCapacidad().equals(estudioBeneficio.getCapacidad())) && 
						((estudio.getEstadoBeneficio() == null && estudioBeneficio.getEstadoBeneficio() == null) || 
								estudio.getEstadoBeneficio() != null && estudioBeneficio.getEstadoBeneficio() != null && 
								estudio.getEstadoBeneficio().equals(estudioBeneficio.getEstadoBeneficio())) 
								){
					//es igual
				}else{
					listaEstudioBeneficioHistorial.add(estudioBeneficio);
				}
			}			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void mostrarExplotacionModificado(List<EstudioImpactoAmbiental> lista){
		try {
			listaEstudioExplotacionHistorico = new ArrayList<EstudioImpactoAmbiental>();
			for(EstudioImpactoAmbiental estudioExplotacion : lista){
				
				if(((estudio.getMetodoDeExplotacion() == null && estudioExplotacion.getMetodoDeExplotacion() == null) || 
								estudio.getMetodoDeExplotacion() != null && estudioExplotacion.getMetodoDeExplotacion() != null && 
								estudio.getMetodoDeExplotacion().equals(estudioExplotacion.getMetodoDeExplotacion())) && 
						((estudio.getNumeroDeFrentesExplotacion() == null && estudioExplotacion.getNumeroDeFrentesExplotacion() == null) || 
								estudio.getNumeroDeFrentesExplotacion() != null && estudioExplotacion.getNumeroDeFrentesExplotacion() != null && 
								estudio.getNumeroDeFrentesExplotacion().equals(estudioExplotacion.getNumeroDeFrentesExplotacion())) && 
						((estudio.getVolumenDeExplotacion() == null && estudioExplotacion.getVolumenDeExplotacion() == null) || 
								estudio.getVolumenDeExplotacion() != null && estudioExplotacion.getVolumenDeExplotacion() != null && 
								estudio.getVolumenDeExplotacion().equals(estudioExplotacion.getVolumenDeExplotacion())) && 
						((estudio.getUnidadMedidaVolumen() == null && estudioExplotacion.getUnidadMedidaVolumen() == null) || 
								estudio.getUnidadMedidaVolumen() != null && estudioExplotacion.getUnidadMedidaVolumen() != null && 
								estudio.getUnidadMedidaVolumen().equals(estudioExplotacion.getUnidadMedidaVolumen())) && 
						((estudio.getTieneConstruccionEscombreras() == null && estudioExplotacion.getTieneConstruccionEscombreras() == null) ||
								estudio.getTieneConstruccionEscombreras() != null && estudioExplotacion.getTieneConstruccionEscombreras() != null && 
								estudio.getTieneConstruccionEscombreras().equals(estudioExplotacion.getTieneConstruccionEscombreras())) && 
						((estudio.getLocalizacionEscombreras() == null && estudioExplotacion.getLocalizacionEscombreras() == null) || 
								estudio.getLocalizacionEscombreras() != null && estudioExplotacion.getLocalizacionEscombreras() != null && 
								estudio.getLocalizacionEscombreras().equals(estudioExplotacion.getLocalizacionEscombreras())) && 
						((estudio.getCapacidadEscombrera() == null && estudioExplotacion.getCapacidadEscombrera() == null) || 
								estudio.getCapacidadEscombrera() != null && estudioExplotacion.getCapacidadEscombrera() != null && 
								estudio.getCapacidadEscombrera().equals(estudioExplotacion.getCapacidadEscombrera())) && 
						((estudio.getEstadoExplotacion() == null && estudioExplotacion.getEstadoExplotacion() == null) || 
								estudio.getEstadoExplotacion() != null && estudioExplotacion.getEstadoExplotacion() != null && 
								estudio.getEstadoExplotacion().equals(estudioExplotacion.getEstadoExplotacion()))					
								){
					//son iguales
				}else{
					listaEstudioExplotacionHistorico.add(estudioExplotacion);
				}					
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	private void cargarConsultoresNoCalificadosVisualizacion(){		
		try {			
			List<ConsultorNoCalificado> consultoresOriginales = new ArrayList<ConsultorNoCalificado>();
			List<ConsultorNoCalificado> consultoresEliminados = new ArrayList<ConsultorNoCalificado>();	
 			listaConsultoresNoCalificadosEliminadas = new ArrayList<ConsultorNoCalificado>();	
			
			listaConsultoresNoCalificadosOriginales = new ArrayList<ConsultorNoCalificado>();
			List<ConsultorNoCalificado> consultoresSeleccionados = getFichaTecnicaBeanEia().getConsultorNoCalificadosSeleccionados();
			consultoresNoCalificados = consultorNoCalificadoFacade.buscarConsultoresNoCalificadosModificados(
					getFichaTecnicaBeanEia().getEstudioImpactoAmbiental().getId());			
			
			if(consultoresSeleccionados != null && !consultoresSeleccionados.isEmpty()){
				
				totalConsultoresNoCalificadosModificados = 0;
				
				for(ConsultorNoCalificado consultor : consultoresSeleccionados){
					if(consultor.getNumeroNotificacion() == null){
						boolean agregar = true;
						for(ConsultorNoCalificado consultorHistorico : consultoresNoCalificados){
							if(consultorHistorico.getIdHistorico() != null && consultorHistorico.getNumeroNotificacion() == numeroNotificaciones && 
									consultorHistorico.getIdHistorico().equals(consultor.getId())){
								agregar = false;
								consultor.setRegistroModificado(true);
								if(!agregar){
									consultoresOriginales.add(consultor);
								}
								break;
							}
						}
					}else if(consultor.getNumeroNotificacion() == numeroNotificaciones){
						totalConsultoresNoCalificadosModificados++;
						if(consultor.getIdHistorico() == null){
							consultor.setNuevoEnModificacion(true);
						}else{
							consultor.setRegistroModificado(true);
							if(!consultoresOriginales.contains(consultor)){
								consultoresOriginales.add(consultor);
							}
						}						
					}					
				}
				
				for(ConsultorNoCalificado consultorEliminado : consultoresNoCalificados){
					if(consultorEliminado.getIdHistorico() != null && consultorEliminado.getNumeroNotificacion() != null){
						boolean existe = false;
						for(ConsultorNoCalificado consultorActual : consultoresSeleccionados){
							if(consultorActual.getId().equals(consultorEliminado.getIdHistorico())){
								existe = true;
								break;
							}							
						}
						if(!existe){
							consultoresEliminados.add(consultorEliminado);
						}
					}
				}
				
				if(totalConsultoresNoCalificadosModificados > 0){
					this.listaConsultoresNoCalificadosOriginales = consultoresOriginales;
				}
				
				listaConsultoresNoCalificadosEliminadas = consultoresEliminados;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}


	public void mostrarOriginalConsultorNoCalificado(ConsultorNoCalificado consultor) {
		listaConsultorNoCalificadoHistorico = new ArrayList<ConsultorNoCalificado>();
		for(ConsultorNoCalificado consultorHistorico : consultoresNoCalificados){
			if(consultorHistorico.getIdHistorico() != null && consultorHistorico.getIdHistorico().equals(consultor.getId())){
				listaConsultorNoCalificadoHistorico.add(consultorHistorico);
			}
		}		
	}
	
	public void mostrarEliminados(){
		listaConsultorNoCalificadoHistorico = new ArrayList<ConsultorNoCalificado>();
		listaConsultorNoCalificadoHistorico = listaConsultoresNoCalificadosEliminadas;
	}
	
	public void mostrarHistoricoCoordenadasEliminadas(){
		listaCoordenadasVisualizacion = new ArrayList<CoordenadaGeneral>();
		
		listaCoordenadasVisualizacion = listaCoordenadasGeneralesEliminadas;	
	}
	
	public void mostrarHistoricoCoordenadas(CoordenadaGeneral coordenada){
		listaCoordenadasVisualizacion = new ArrayList<CoordenadaGeneral>();
		
		for(CoordenadaGeneral coordenadaVisualizacion : listaCoordenaGeneralesOriginales){
			if(coordenadaVisualizacion.getIdHistorico() != null && coordenadaVisualizacion.getIdHistorico().equals(coordenada.getId())){
				listaCoordenadasVisualizacion.add(coordenadaVisualizacion);
			}
		}		
	}
	
	
}
