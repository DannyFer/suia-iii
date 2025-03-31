package ec.gob.ambiente.prevencion.actualizacionPma.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.suia.domain.AspectoAmbientalPMA;
import ec.gob.ambiente.suia.domain.ComponentePMA;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.Normativas;
import ec.gob.ambiente.suia.domain.ParametrosNormativas;
import ec.gob.ambiente.suia.domain.ParametrosPlanMonitoreo;
import ec.gob.ambiente.suia.domain.PlanMonitoreoEia;
import ec.gob.ambiente.suia.domain.TablasNormativas;
import ec.gob.ambiente.suia.domain.TablasPlanMonitoreo;
import ec.gob.ambiente.suia.domain.enums.Periodicidad;
import ec.gob.ambiente.suia.eia.pma.planMonitoreo.facade.NormativasFacade;
import ec.gob.ambiente.suia.eia.pma.planMonitoreo.facade.ParametrosNormativaFacade;
import ec.gob.ambiente.suia.eia.pma.planMonitoreo.facade.PlanMonitoreoEiaFacade;
import ec.gob.ambiente.suia.eia.pma.planMonitoreo.facade.TablasNormativasFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;

/**
 * @author Oscar Campana
 */
@ManagedBean
@ViewScoped
public class PlanMonitoreoActualizacionController implements Serializable {

	private static final long serialVersionUID = 1572523482381028668L;
//	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
//			.getLogger(PlanMonitoreoActualizacionController.class);

	@EJB
	private ValidacionSeccionesFacade validacionesSeccionesFacade;

	private EstudioImpactoAmbiental estudioImpactoAmbiental;

	@EJB
	private PlanMonitoreoEiaFacade planMonitoreoFacade;

	@Getter
	@Setter
	private List<ComponentePMA> componentes;

	@Getter
	@Setter
	private List<AspectoAmbientalPMA> aspectoAmbientalLista;

	@EJB
	private NormativasFacade normativasFacade;

	@EJB
	private TablasNormativasFacade tablasNormativasFacade;

	@EJB
	private ParametrosNormativaFacade parametrosNormativaFacade;

	@Getter
	@Setter
	PlanMonitoreoEia planMonitoreoEia;

	@Getter
	@Setter
	TablasNormativas tablaNormativas;

	@Getter
	@Setter
	TablasPlanMonitoreo tablasPlanMonitoreo;

	@Getter
	@Setter
	ParametrosNormativas parametrosNormativas;

	@Getter
	@Setter
	List<Normativas> listaNormativas;

	@Getter
	@Setter
	List<ParametrosNormativas> listaParametrosNormativas;

	@Getter
	@Setter
	List<PlanMonitoreoEia> listaPlanMonitoreoEia;

	@Getter
	@Setter
	List<TablasNormativas> listaTablasNormativas;

	@Getter
	@Setter
	List<TablasPlanMonitoreo> listaTablasPlanMonitoreo;

	@Getter
	@Setter
	List<ParametrosNormativas> listaParametrosNormativa;

	@Getter
	@Setter
	List<ParametrosPlanMonitoreo> listaParametrosPlanMonitoreo;

	@Getter
	@Setter
	List<PlanMonitoreoEia> listaPlanMonitoreoEiaEliminados;

	@EJB
	private PlanMonitoreoEiaFacade planMonitoreoEiaFacade;

	@Setter
	@Getter
	private Periodicidad[] periodicidad = Periodicidad.values();

	@Setter
	@Getter
	private ComponentePMA componente;

	@Setter
	@Getter
	private AspectoAmbientalPMA aspectoAmbiental;

	@Setter
	@Getter
	private boolean isEditing;

	@Setter
	@Getter
	private boolean isExPost;

	@Setter
	@Getter
	private boolean isEditingTablas;

	private boolean agregarTablas;

	@Setter
	@Getter
	Normativas normativa;

	/**
	 * Metodo que se ejecuta automaticamente al cargar la pagina
	 * 
	 * @throws CmisAlfrescoException
	 * @throws ServiceException
	 */
	@PostConstruct
	private void postInit() throws CmisAlfrescoException, ServiceException {
		
		//estudioImpactoAmbiental = (EstudioImpactoAmbiental) JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT);

		this.listaTablasPlanMonitoreo = new ArrayList<TablasPlanMonitoreo>();
		this.listaParametrosPlanMonitoreo = new ArrayList<ParametrosPlanMonitoreo>();
		this.componentes = new ArrayList<ComponentePMA>();

		cargarDatos();
		// evaluarTipoestudio();

	}

	/**
	 * Evalua el tipo de estudio
	 */
	private void evaluarTipoestudio() {
		if (this.estudioImpactoAmbiental.getProyectoLicenciamientoAmbiental().getTipoEstudio().getId().equals(1)) {
			this.isExPost = false;
		} else {
			this.isExPost = true;
		}

	}

	/**
	 * Carga datos y consultas iniciales
	 * 
	 * @throws CmisAlfrescoException
	 * @throws ServiceException
	 */
	private void cargarDatos() throws CmisAlfrescoException, ServiceException {

		this.isEditing = false;
		this.isEditingTablas = false;
		this.isExPost = false;
		this.agregarTablas = false;

		this.planMonitoreoEia = new PlanMonitoreoEia();

		this.tablaNormativas = new TablasNormativas();

		this.normativa = new Normativas();

		listaPlanMonitoreoEia = new ArrayList<PlanMonitoreoEia>();
		listaPlanMonitoreoEiaEliminados = new ArrayList<PlanMonitoreoEia>();

		listaParametrosNormativas = new ArrayList<ParametrosNormativas>();

		this.listaNormativas = new ArrayList<Normativas>();

		/*
		 * this.listaNormativas = this.normativasFacade.listar(this.estudioImpactoAmbiental
		 * .getProyectoLicenciamientoAmbiental().getTipoSector().getId(), this.estudioImpactoAmbiental
		 * .getProyectoLicenciamientoAmbiental().getTipoEstudio().getId());
		 * 
		 * for (Normativas nor : this.listaNormativas) { nor.setEtiqueta(nor.getDescripcion() + " - " +
		 * nor.getComponenteFisico()); }
		 * 
		 * try { this.listaPlanMonitoreoEia = planMonitoreoEiaFacade.listarPorEIA(estudioImpactoAmbiental);
		 * 
		 * } catch (Exception e) { LOG.error(e, e); }
		 */
	}

	/**
	 * Metodo de guardado general
	 * 
	 * @throws CmisAlfrescoException
	 */
	public void guardar() throws CmisAlfrescoException {

		/*
		 * try { planMonitoreoEiaFacade.guardar(listaPlanMonitoreoEia, listaPlanMonitoreoEiaEliminados); cargarDatos();
		 * 
		 * if (this.listaPlanMonitoreoEia.size() > 0) { this.actualizarEstadoValidacionSeccion(); }
		 * JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA); } catch (ServiceException e) {
		 * LOG.error(e, e); JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + " " + e.getMessage()); } catch
		 * (RuntimeException e) { LOG.error(e, e); JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + " " +
		 * e.getMessage()); }
		 */
	}

	/**
	 * Asigna la seccion valida
	 * 
	 * @throws ServiceException
	 * @throws CmisAlfrescoException
	 */

	private void actualizarEstadoValidacionSeccion() throws ServiceException, CmisAlfrescoException {
		validacionesSeccionesFacade.guardarValidacionSeccion("EIA", "planMonitoreo", estudioImpactoAmbiental.getId()
				.toString());

	}

	/**
	 * Prepara la edicion de un plan de monitoreo
	 * 
	 * @param planMonitoreo
	 */
	public void editarPlanMonitoreo(PlanMonitoreoEia planMonitoreo) {

		/*
		 * this.planMonitoreoEia = planMonitoreo; this.isEditing = true;
		 */
	}

	/**
	 * Prepara la edicion de una tabla
	 * 
	 * @param tablasPlanMonitoreo
	 * @throws ServiceException
	 */
	public void editarTabla(TablasPlanMonitoreo tablasPlanMonitoreo) throws ServiceException {
		/*
		 * this.tablasPlanMonitoreo = tablasPlanMonitoreo; this.isEditingTablas = true; this.listaParametrosNormativa =
		 * this.parametrosNormativaFacade.listarPorTabla(this.tablasPlanMonitoreo .getIdReferencia());
		 * JsfUtil.addCallbackParam("planMonitoreoP");
		 */
	}

	/**
	 * Inicializacion de variables y objetos del formulario Detalle para Adicionar
	 */
	public void inicializarPlanMonitoreo() {

		// this.agregarTablas = true;
		this.planMonitoreoEia = new PlanMonitoreoEia();

		/*
		 * this.planMonitoreoEia.setEia(this.estudioImpactoAmbiental); this.planMonitoreoEia.setNormativas(new
		 * Normativas()); this.planMonitoreoEia.setTablasPlanMonitoreo(new ArrayList<TablasPlanMonitoreo>());
		 * this.planMonitoreoEia.setComponente(""); this.planMonitoreoEia.setTipoComponente("");
		 * 
		 * this.componentes = new ArrayList<ComponentePMA>(); this.componente = new ComponentePMA();
		 * this.aspectoAmbiental = new AspectoAmbientalPMA(); this.aspectoAmbientalLista = new
		 * ArrayList<AspectoAmbientalPMA>();
		 */

		listaTablasPlanMonitoreo = new ArrayList<TablasPlanMonitoreo>();

		// this.isEditing = false;
		// JsfUtil.addCallbackParam("planMonitoreo");

	}

	/**
	 * Agregar un plan de monitoreo
	 */
	public void agregarPlanMonitoreo() {
		/*
		 * if (!this.isEditing) { this.listaPlanMonitoreoEia.add(this.planMonitoreoEia); }
		 * JsfUtil.addCallbackParam("planMonitoreo");
		 */
	}

	/**
	 * Elimina un plan de monitoreo
	 * 
	 * @param planMonitoreoEia
	 *            plan de monitoreo
	 */
	public void removerPlanMonitoreo(PlanMonitoreoEia planMonitoreoEia) {

		/*
		 * this.isEditing = false; this.listaPlanMonitoreoEia.remove(planMonitoreoEia);
		 * this.listaPlanMonitoreoEiaEliminados.add(planMonitoreoEia);
		 */
	}

	/**
	 * Elimina un parametro
	 * 
	 * @param parametrosPlanMonitoreoEia
	 *            Parametros Plan de Monitoreo
	 */
	public void removerParametro(ParametrosPlanMonitoreo parametrosPlanMonitoreoEia) {

		/*
		 * this.isEditing = false;
		 * this.tablasPlanMonitoreo.getParametrosPlanMonitoreo().remove(parametrosPlanMonitoreoEia);
		 */

	}

	/**
	 * Consulta tablas por normativa
	 * 
	 * @throws ServiceException
	 */
	public void cargaTablas() throws ServiceException {

		/*
		 * if (agregarTablas) { this.listaTablasNormativas =
		 * this.tablasNormativasFacade.listarPorNormativa(this.planMonitoreoEia .getNormativas());
		 * 
		 * for (TablasNormativas tablas : this.listaTablasNormativas) { TablasPlanMonitoreo tablaPlan = new
		 * TablasPlanMonitoreo(); tablaPlan.setParametrosPlanMonitoreo(new ArrayList<ParametrosPlanMonitoreo>());
		 * tablaPlan.setDescripcion(tablas.getDescripcion()); tablaPlan.setEstado(tablas.getEstado());
		 * tablaPlan.setIdReferencia(tablas.getId()); tablaPlan.setPlanMonitoreoEia(this.planMonitoreoEia);
		 * 
		 * this.listaTablasPlanMonitoreo.add(tablaPlan); agregarTablas = false; }
		 * 
		 * this.planMonitoreoEia.setTablasPlanMonitoreo(this.listaTablasPlanMonitoreo); }
		 */
	}

	/**
	 * Consultar parametros por tablas
	 * 
	 * @throws ServiceException
	 */
	public void cargaParametros() throws ServiceException {
		/*
		 * this.listaParametrosNormativa = this.parametrosNormativaFacade.listarPorTabla(this.tablasPlanMonitoreo
		 * .getIdReferencia());
		 */
	}

	/**
	 * Agregar parametro
	 */
	public void agregarParametro() {

		ParametrosPlanMonitoreo para = new ParametrosPlanMonitoreo();
		/*
		 * if (!listaParametrosNormativas.contains(this.parametrosNormativas)) {
		 * listaParametrosNormativas.add(this.parametrosNormativas);
		 * para.setDescripcion(this.parametrosNormativas.getDescripcion());
		 * para.setTablasPlanMonitoreo(this.tablasPlanMonitoreo);
		 * this.tablasPlanMonitoreo.getParametrosPlanMonitoreo().add(para); }
		 */
	}

	public void agregarParam() {
		//JsfUtil.addCallbackParam("planMonitoreoP");
	}

	/**
	 * Consultar componentes
	 */
	public void cargarComponentes() {
		/*
		try {

			this.planMonitoreoEia.setComponente(null);
			this.planMonitoreoEia.setTablasPlanMonitoreo(new ArrayList<TablasPlanMonitoreo>());

			this.planMonitoreoEia.setComponente(this.planMonitoreoEia.getNormativas().getComponenteFisico());

			this.aspectoAmbientalLista = normativasFacade.getAspectosAmbientalesPorComponente(this.normativasFacade
					.getComponentes(this.planMonitoreoEia.getComponente()).get(0));

		} catch (Exception exception) {
			LOG.error(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION, exception);
		}
		*/
	}

	/**
	 * Consultar aspectos ambientales
	 */
	public void cargarAspectosAmbientales() {
		/*
		try {

			if (this.componente != null) {
				this.planMonitoreoEia.setComponente(componente.getNombre());
				this.aspectoAmbientalLista = normativasFacade.getAspectosAmbientalesPorComponente(this.componente);
			}
		} catch (Exception exception) {
			LOG.error(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION, exception);
		}
		*/
	}

	/**
	 * Procesar aspecto ambiental seleccionado
	 */
	public void procesarAspectosAmbientales() {
		/*
		try {

			this.cargaTablas();
			if (this.aspectoAmbiental != null) {
				this.planMonitoreoEia.setTipoComponente(aspectoAmbiental.getNombre());
			}
		} catch (Exception exception) {
			LOG.error(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION, exception);
		}
		*/
	}

	/**
	 * Metodo para redireccionar
	 */
	public void cancelar() {
		JsfUtil.redirectTo("/prevencion/actualizacionPma/planMonitoreo/planMonitoreo.jsf");
	}

}
