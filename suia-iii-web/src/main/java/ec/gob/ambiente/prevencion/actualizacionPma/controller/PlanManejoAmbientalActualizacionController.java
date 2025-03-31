package ec.gob.ambiente.prevencion.actualizacionPma.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.context.RequestContext;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.domain.AspectoAmbiental;
import ec.gob.ambiente.suia.domain.Componente;
import ec.gob.ambiente.suia.domain.DetalleEvaluacionAspectoAmbiental;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;
import ec.gob.ambiente.suia.domain.Normativas;
import ec.gob.ambiente.suia.domain.PlanManejoAmbientalEIA;
import ec.gob.ambiente.suia.domain.PlanManejoAmbientalEIADetalle;
import ec.gob.ambiente.suia.domain.enums.Periodicidad;
import ec.gob.ambiente.suia.eia.facade.EstudioImpactoAmbientalFacade;
import ec.gob.ambiente.suia.eia.ficha.bean.EstudioImpactoAmbientalBean;
import ec.gob.ambiente.suia.eia.impactoAmbiental.facade.ImpactoAmbientalFacade;
import ec.gob.ambiente.suia.eia.pma.facade.PlanManejoAmbientalEIADetalleFacade;
import ec.gob.ambiente.suia.eia.pma.facade.PlanManejoAmbientalEIAFacade;
import ec.gob.ambiente.suia.eia.pma.planMonitoreo.facade.PlanMonitoreoEiaFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;

@ManagedBean
@ViewScoped
public class PlanManejoAmbientalActualizacionController implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -971306035001252479L;

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(PlanManejoAmbientalActualizacionController.class);

	@EJB
	private ValidacionSeccionesFacade validacionesSeccionesFacade;

	@Getter
	@Setter
	private EstudioImpactoAmbiental estudioImpactoAmbiental;

	@EJB
	private PlanMonitoreoEiaFacade planMonitoreoFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{estudioImpactoAmbientalBean}")
	private EstudioImpactoAmbientalBean eiaBean;

	@EJB
	private ImpactoAmbientalFacade impactoAmbientalFacade;

	@Getter
	@Setter
	private List<Componente> componentes;

	@Getter
	@Setter
	private List<AspectoAmbiental> aspectoAmbientalLista;

	@Getter
	@Setter
	private String tipoTabla;

	/*************************/
	// ///////////

	@EJB
	private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@Getter
	@Setter
	PlanManejoAmbientalEIA planManejoAmbientalEIA;

	@Getter
	@Setter
	PlanManejoAmbientalEIADetalle planManejoAmbientalEIADetalle;

	@Getter
	@Setter
	List<Normativas> listaNormativas;

	@Getter
	@Setter
	List<PlanManejoAmbientalEIA> listaPlanManejoAmbientalEIA;

	@Getter
	@Setter
	List<PlanManejoAmbientalEIADetalle> listaPlanManejoAmbientalEIADetallePrevencion;

	@Getter
	@Setter
	List<PlanManejoAmbientalEIADetalle> listaPlanManejoAmbientalEIADetallePrevencionCambio;

	@Getter
	@Setter
	List<PlanManejoAmbientalEIADetalle> listaPlanManejoAmbientalEIADetallleEliminados;

	@Getter
	@Setter
	List<PlanManejoAmbientalEIADetalle> listaPlanManejoAmbientalEIADetalleContingencias;

	@Getter
	@Setter
	List<PlanManejoAmbientalEIADetalle> listaPlanManejoAmbientalEIADetalleContingenciasCambio;

	@Getter
	@Setter
	List<PlanManejoAmbientalEIADetalle> listaPlanManejoAmbientalEIADetalleComunicacion;

	@Getter
	@Setter
	List<PlanManejoAmbientalEIADetalle> listaPlanManejoAmbientalEIADetalleComunicacionCambio;

	@Getter
	@Setter
	List<PlanManejoAmbientalEIADetalle> listaPlanManejoAmbientalEIADetalleSeguridad;

	@Getter
	@Setter
	List<PlanManejoAmbientalEIADetalle> listaPlanManejoAmbientalEIADetalleSeguridadCambio;

	@Getter
	@Setter
	List<PlanManejoAmbientalEIADetalle> listaPlanManejoAmbientalEIADetalleManejo;

	@Getter
	@Setter
	List<PlanManejoAmbientalEIADetalle> listaPlanManejoAmbientalEIADetalleManejoCambio;

	@Getter
	@Setter
	List<PlanManejoAmbientalEIADetalle> listaPlanManejoAmbientalEIADetalleRelaciones;

	@Getter
	@Setter
	List<PlanManejoAmbientalEIADetalle> listaPlanManejoAmbientalEIADetalleRelacionesCambio;

	@Getter
	@Setter
	List<PlanManejoAmbientalEIADetalle> listaPlanManejoAmbientalEIADetalleRehabilitacion;

	@Getter
	@Setter
	List<PlanManejoAmbientalEIADetalle> listaPlanManejoAmbientalEIADetalleRehabilitacionCambio;

	@Getter
	@Setter
	List<PlanManejoAmbientalEIADetalle> listaPlanManejoAmbientalEIADetalleAbandono;

	@Getter
	@Setter
	List<PlanManejoAmbientalEIADetalle> listaPlanManejoAmbientalEIADetalleAbandonoCambio;

	@Getter
	@Setter
	List<PlanManejoAmbientalEIA> listaPlanManejoAmbientalEIAEliminados;

	@Getter
	@Setter
	List<PlanManejoAmbientalEIADetalle> listaPlanManejoAmbientalEIAEIADetalle;

	@Getter
	@Setter
	List<DetalleEvaluacionAspectoAmbiental> listaDetalleEvaluacionAspectoAmbiental;

	@Getter
	@Setter
	DetalleEvaluacionAspectoAmbiental detalleEvaluacionAspectoAmbiental;

	@EJB
	private PlanManejoAmbientalEIAFacade planManejoAmbientalEIAFacade;

	@EJB
	private PlanManejoAmbientalEIADetalleFacade planManejoAmbientalEIADetalleFacade;

	@EJB
	private ValidacionSeccionesFacade validacionSeccionesFacade;

	@EJB
	private EstudioImpactoAmbientalFacade estudioImpactoAmbientalFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;

	@Getter
	@Setter
	private FichaAmbientalPma fichaAmbientalPma;

	@Setter
	@Getter
	private Periodicidad[] periodicidad = Periodicidad.values();

	@Setter
	@Getter
	private Componente componente;

	@Setter
	@Getter
	private AspectoAmbiental aspectoAmbiental;

	@Setter
	@Getter
	private boolean isEditing;

	@Setter
	@Getter
	private boolean opcion2;



	// //******************/////////////////////////

	@PostConstruct
	private void postInit() throws Exception {
		estudioImpactoAmbiental = (EstudioImpactoAmbiental) JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT);
		//this.isEditing=false;

		this.listaPlanManejoAmbientalEIAEIADetalle = new ArrayList<PlanManejoAmbientalEIADetalle>();
		this.componentes = new ArrayList<Componente>();

		cargarDatos();
		cargarListasCambios();

	}

	private void cargarDatos() throws Exception {

		this.isEditing = false;
		this.opcion2 = false;

		this.planManejoAmbientalEIA = new PlanManejoAmbientalEIA();

		this.listaDetalleEvaluacionAspectoAmbiental = impactoAmbientalFacade
				.getDetalleEvaluacionAspectoAmbientalPorEIA(estudioImpactoAmbiental);

		estudioImpactoAmbiental = (EstudioImpactoAmbiental) JsfUtil
				.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT);

		this.listaPlanManejoAmbientalEIA = new ArrayList<PlanManejoAmbientalEIA>();

		this.listaPlanManejoAmbientalEIAEliminados = new ArrayList<PlanManejoAmbientalEIA>();

		this.listaPlanManejoAmbientalEIADetallleEliminados = new ArrayList<PlanManejoAmbientalEIADetalle>();

		this.listaPlanManejoAmbientalEIADetallePrevencion = new ArrayList<PlanManejoAmbientalEIADetalle>();
		this.listaPlanManejoAmbientalEIADetalleContingencias = new ArrayList<PlanManejoAmbientalEIADetalle>();
		this.listaPlanManejoAmbientalEIADetalleComunicacion = new ArrayList<PlanManejoAmbientalEIADetalle>();
		this.listaPlanManejoAmbientalEIADetalleSeguridad = new ArrayList<PlanManejoAmbientalEIADetalle>();
		this.listaPlanManejoAmbientalEIADetalleManejo = new ArrayList<PlanManejoAmbientalEIADetalle>();
		this.listaPlanManejoAmbientalEIADetalleRelaciones = new ArrayList<PlanManejoAmbientalEIADetalle>();
		this.listaPlanManejoAmbientalEIADetalleRehabilitacion = new ArrayList<PlanManejoAmbientalEIADetalle>();
		this.listaPlanManejoAmbientalEIADetalleAbandono = new ArrayList<PlanManejoAmbientalEIADetalle>();

		this.listaPlanManejoAmbientalEIADetallePrevencionCambio = new ArrayList<PlanManejoAmbientalEIADetalle>();
		this.listaPlanManejoAmbientalEIADetalleContingenciasCambio = new ArrayList<PlanManejoAmbientalEIADetalle>();
		this.listaPlanManejoAmbientalEIADetalleComunicacionCambio = new ArrayList<PlanManejoAmbientalEIADetalle>();
		this.listaPlanManejoAmbientalEIADetalleSeguridadCambio = new ArrayList<PlanManejoAmbientalEIADetalle>();
		this.listaPlanManejoAmbientalEIADetalleManejoCambio = new ArrayList<PlanManejoAmbientalEIADetalle>();
		this.listaPlanManejoAmbientalEIADetalleRelacionesCambio = new ArrayList<PlanManejoAmbientalEIADetalle>();
		this.listaPlanManejoAmbientalEIADetalleRehabilitacionCambio = new ArrayList<PlanManejoAmbientalEIADetalle>();
		this.listaPlanManejoAmbientalEIADetalleAbandonoCambio = new ArrayList<PlanManejoAmbientalEIADetalle>();

		try {
			this.listaPlanManejoAmbientalEIA = planManejoAmbientalEIAFacade.listarPorEIA(estudioImpactoAmbiental);

			if (this.listaPlanManejoAmbientalEIA.size() > 0) {

				this.planManejoAmbientalEIA = this.listaPlanManejoAmbientalEIA.get(0);

				this.listaPlanManejoAmbientalEIADetallePrevencion = planManejoAmbientalEIADetalleFacade
						.listarPorTipo(this.planManejoAmbientalEIA, "11");
				this.listaPlanManejoAmbientalEIADetalleContingencias = planManejoAmbientalEIADetalleFacade
						.listarPorTipo(this.planManejoAmbientalEIA, "12");
				this.listaPlanManejoAmbientalEIADetalleComunicacion = planManejoAmbientalEIADetalleFacade
						.listarPorTipo(this.planManejoAmbientalEIA, "13");
				this.listaPlanManejoAmbientalEIADetalleSeguridad = planManejoAmbientalEIADetalleFacade
						.listarPorTipo(this.planManejoAmbientalEIA, "14");
				this.listaPlanManejoAmbientalEIADetalleManejo = planManejoAmbientalEIADetalleFacade
						.listarPorTipo(this.planManejoAmbientalEIA, "15");
				this.listaPlanManejoAmbientalEIADetalleRelaciones = planManejoAmbientalEIADetalleFacade
						.listarPorTipo(	this.planManejoAmbientalEIA, "16");
				this.listaPlanManejoAmbientalEIADetalleRehabilitacion = planManejoAmbientalEIADetalleFacade
						.listarPorTipo(this.planManejoAmbientalEIA, "17");
				this.listaPlanManejoAmbientalEIADetalleAbandono = planManejoAmbientalEIADetalleFacade
						.listarPorTipo(this.planManejoAmbientalEIA, "18");
			}
		} catch (Exception e) {
			LOG.error(e, e);
		}
	}

	public void guardar(String tipo) throws Exception {

			try {

				this.planManejoAmbientalEIA.setPlanManejoAmbientalEIADetalle(this
						.generalistaPlanManejoAmbientalEIAEIADetalle(this.planManejoAmbientalEIA));
				cargarListasCambios();
				this.eliminarDetalle();
				planManejoAmbientalEIAFacade.guardar(this.planManejoAmbientalEIA, null);

				cargarDatos();

				actualizarEstadoValidacionSeccion();

				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			} catch (ServiceException e) {
				LOG.error(e, e);
				JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + " " + e.getMessage());
			}
			catch (RuntimeException e) {
				LOG.error(e, e);
				JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + " " + e.getMessage());
			}


	}


	public boolean validarListaPlanManejoAmbientalEIAEIADetalle() {

		if (listaPlanManejoAmbientalEIADetallePrevencion.size() == 0) {
			JsfUtil.addMessageError("Se debe ingresar almenos 1 registro en cada uno de los planes de manejo ambiental.");
			return false;
		}
		if (listaPlanManejoAmbientalEIADetalleContingencias.size() == 0) {
			JsfUtil.addMessageError("Se debe ingresar almenos 1 registro en cada uno de los planes de manejo ambiental.");
			return false;
		}
		if (listaPlanManejoAmbientalEIADetalleComunicacion.size() == 0) {
			JsfUtil.addMessageError("Se debe ingresar almenos 1 registro en cada uno de los planes de manejo ambiental.");
			return false;

		}

		if (listaPlanManejoAmbientalEIADetalleSeguridad.size() == 0) {
			JsfUtil.addMessageError("Se debe ingresar almenos 1 registro en cada uno de los planes de manejo ambiental.");
			return false;
		}

		if (listaPlanManejoAmbientalEIADetalleManejo.size() == 0) {
			JsfUtil.addMessageError("Se debe ingresar almenos 1 registro en cada uno de los planes de manejo ambiental.");
			return false;
		}
		if (listaPlanManejoAmbientalEIADetalleRelaciones.size() == 0) {
			JsfUtil.addMessageError("Se debe ingresar almenos 1 registro en cada uno de los planes de manejo ambiental.");
			return false;

		}
		if (listaPlanManejoAmbientalEIADetalleRehabilitacion.size() == 0) {
			JsfUtil.addMessageError("Se debe ingresar almenos 1 registro en cada uno de los planes de manejo ambiental.");
			return false;
		}
		if (listaPlanManejoAmbientalEIADetalleAbandono.size() == 0) {
			JsfUtil.addMessageError("Se debe ingresar al menos 1 registro en cada uno de los planes de manejo ambiental.");
			return false;
		}

		return true;
	}


	/**
	 * Eliminar detalle
	 */
	private void eliminarDetalle()
	{
		planManejoAmbientalEIADetalleFacade.eliminar(this.listaPlanManejoAmbientalEIADetallleEliminados);

	}

	/**
	 * Generar lista de Planes de manejo ambiental
	 *
	 * @param pmaEIA
	 * @return
	 */
	public List<PlanManejoAmbientalEIADetalle> generalistaPlanManejoAmbientalEIAEIADetalle(PlanManejoAmbientalEIA pmaEIA) {

		List<PlanManejoAmbientalEIADetalle> detalleGeneral = new ArrayList<PlanManejoAmbientalEIADetalle>();

		for (PlanManejoAmbientalEIADetalle detalle : this.listaPlanManejoAmbientalEIADetallePrevencion) {
			detalle.setPlanManejoAmbientalEIA(pmaEIA);
			detalleGeneral.add(detalle);
		}
		for (PlanManejoAmbientalEIADetalle detalle : listaPlanManejoAmbientalEIADetalleContingencias) {
			detalle.setPlanManejoAmbientalEIA(pmaEIA);
			detalleGeneral.add(detalle);
		}
		for (PlanManejoAmbientalEIADetalle detalle : listaPlanManejoAmbientalEIADetalleComunicacion) {
			detalle.setPlanManejoAmbientalEIA(pmaEIA);
			detalleGeneral.add(detalle);
		}
		for (PlanManejoAmbientalEIADetalle detalle : listaPlanManejoAmbientalEIADetalleSeguridad) {
			detalle.setPlanManejoAmbientalEIA(pmaEIA);
			detalleGeneral.add(detalle);
		}
		for (PlanManejoAmbientalEIADetalle detalle : listaPlanManejoAmbientalEIADetalleManejo) {
			detalle.setPlanManejoAmbientalEIA(pmaEIA);
			detalleGeneral.add(detalle);
		}
		for (PlanManejoAmbientalEIADetalle detalle : listaPlanManejoAmbientalEIADetalleRelaciones) {
			detalle.setPlanManejoAmbientalEIA(pmaEIA);
			detalleGeneral.add(detalle);
		}
		for (PlanManejoAmbientalEIADetalle detalle : listaPlanManejoAmbientalEIADetalleRehabilitacion) {
			detalle.setPlanManejoAmbientalEIA(pmaEIA);
			detalleGeneral.add(detalle);
		}
		for (PlanManejoAmbientalEIADetalle detalle : listaPlanManejoAmbientalEIADetalleAbandono) {
			detalle.setPlanManejoAmbientalEIA(pmaEIA);
			detalleGeneral.add(detalle);
		}

		return detalleGeneral;
	}

	/**
	 * Asigna la sección válida
	 *
	 * @throws ServiceException
	 * @throws CmisAlfrescoException
	 */
	private void actualizarEstadoValidacionSeccion() throws ServiceException, CmisAlfrescoException {
		validacionesSeccionesFacade.guardarValidacionSeccion("EIA", "planManejoAmbiental", estudioImpactoAmbiental
				.getId().toString());

	}


	public void editarPlanManejoAmbientalEIADetalle(String tipoTabla,
													PlanManejoAmbientalEIADetalle planManejoAmbientalEIADetalle) {
		this.tipoTabla = tipoTabla;
		if (tipoTabla.equals("11")) {
			this.opcion2 = false;
		}
		if (tipoTabla.equals("12")) {
			this.opcion2 = true;
		}
		if (tipoTabla.equals("13")) {
			this.opcion2 = false;
		}
		if (tipoTabla.equals("14")) {
			this.opcion2 = false;
		}
		if (tipoTabla.equals("15")) {
			this.opcion2 = false;
		}
		if (tipoTabla.equals("16")) {
			this.opcion2 = false;
		}
		if (tipoTabla.equals("17")) {
			this.opcion2 = false;
		}
		if (tipoTabla.equals("18")) {
			this.opcion2 = false;
		}

		//this.planManejoAmbientalEIADetalle = new PlanManejoAmbientalEIADetalle();

		this.planManejoAmbientalEIADetalle = planManejoAmbientalEIADetalle;
		this.isEditing = true;

	}

	/**
	 * Inicialización de variables del Plan Manejo Ambiental
	 */
	public void inicializarPlanManejoAmbiental(String tipo) {

		if (tipo.equals("11")) {
			opcion2 = false;
			this.tipoTabla = tipo;

		}
		if (tipo.equals("12")) {
			opcion2 = true;
			this.tipoTabla = tipo;
		}
		if (tipo.equals("13")) {
			opcion2 = false;
			this.tipoTabla = tipo;
		}
		if (tipo.equals("14")) {
			opcion2 = false;
			this.tipoTabla = tipo;
		}
		if (tipo.equals("15")) {
			opcion2 = false;
			this.tipoTabla = tipo;
		}
		if (tipo.equals("16")) {
			opcion2 = false;
			this.tipoTabla = tipo;
		}
		if (tipo.equals("17")) {
			opcion2 = false;
			this.tipoTabla = tipo;
		}
		if (tipo.equals("18")) {
			opcion2 = false;
			this.tipoTabla = tipo;
		}

		this.planManejoAmbientalEIADetalle = new PlanManejoAmbientalEIADetalle();
		this.planManejoAmbientalEIADetalle.setDetalleEvaluacionLista(null);

		this.isEditing = false;
		JsfUtil.addCallbackParam("planManejoAmbiental");
	}

	/**
	 * Agregar Plan Manejo Ambiental
	 */
	public void agregarPlanManejoAmbientalEIADetallePrevencion() {

		this.planManejoAmbientalEIADetalle.setTipoPlan(tipoTabla);
		//if(!this.isEditing){
		this.planManejoAmbientalEIADetalle.setCambio(true);
		//}
		if (tipoTabla.equals("11")) {
			if (!this.isEditing) {
				this.listaPlanManejoAmbientalEIADetallePrevencion.add(this.planManejoAmbientalEIADetalle);
			}
		}
		if (tipoTabla.equals("12")) {
			if (!this.isEditing) {
				this.listaPlanManejoAmbientalEIADetalleContingencias.add(this.planManejoAmbientalEIADetalle);
			}
		}
		if (tipoTabla.equals("13")) {
			if (!this.isEditing) {
				this.listaPlanManejoAmbientalEIADetalleComunicacion.add(this.planManejoAmbientalEIADetalle);
			}
		}
		if (tipoTabla.equals("14")) {
			if (!this.isEditing) {
				this.listaPlanManejoAmbientalEIADetalleSeguridad.add(this.planManejoAmbientalEIADetalle);
			}
		}
		if (tipoTabla.equals("15")) {
			if (!this.isEditing) {
				this.listaPlanManejoAmbientalEIADetalleManejo.add(this.planManejoAmbientalEIADetalle);
			}
		}
		if (tipoTabla.equals("16")) {
			if (!this.isEditing) {
				this.listaPlanManejoAmbientalEIADetalleRelaciones.add(this.planManejoAmbientalEIADetalle);
			}
		}
		if (tipoTabla.equals("17")) {
			if (!this.isEditing) {
				this.listaPlanManejoAmbientalEIADetalleRehabilitacion.add(this.planManejoAmbientalEIADetalle);
			}
		}
		if (tipoTabla.equals("18")) {
			if (!this.isEditing) {
				this.listaPlanManejoAmbientalEIADetalleAbandono.add(this.planManejoAmbientalEIADetalle);
			}
		}

		JsfUtil.addCallbackParam("planManejoAmbiental");
		RequestContext.getCurrentInstance().execute(
				"PF('dlg3').hide();");
	}


	public void removerPlanManejoAmbiental(String tipoTabla, PlanManejoAmbientalEIADetalle planManejoAmbientalEIADetalle) {
		this.isEditing = false;

		if (tipoTabla.equals("11")) {
			this.listaPlanManejoAmbientalEIADetallePrevencion.remove(planManejoAmbientalEIADetalle);
			this.listaPlanManejoAmbientalEIADetallleEliminados.add(planManejoAmbientalEIADetalle);
		}
		if (tipoTabla.equals("12")) {
			this.listaPlanManejoAmbientalEIADetalleContingencias.remove(planManejoAmbientalEIADetalle);
			this.listaPlanManejoAmbientalEIADetallleEliminados.add(planManejoAmbientalEIADetalle);
		}
		if (tipoTabla.equals("13")) {
			this.listaPlanManejoAmbientalEIADetalleComunicacion.remove(planManejoAmbientalEIADetalle);
			this.listaPlanManejoAmbientalEIADetallleEliminados.add(planManejoAmbientalEIADetalle);
		}
		if (tipoTabla.equals("14")) {
			this.listaPlanManejoAmbientalEIADetalleSeguridad.remove(planManejoAmbientalEIADetalle);
			this.listaPlanManejoAmbientalEIADetallleEliminados.add(planManejoAmbientalEIADetalle);
		}
		if (tipoTabla.equals("15")) {
			this.listaPlanManejoAmbientalEIADetalleManejo.remove(planManejoAmbientalEIADetalle);
			this.listaPlanManejoAmbientalEIADetallleEliminados.add(planManejoAmbientalEIADetalle);
		}
		if (tipoTabla.equals("16")) {
			this.listaPlanManejoAmbientalEIADetalleRelaciones.remove(planManejoAmbientalEIADetalle);
			this.listaPlanManejoAmbientalEIADetallleEliminados.add(planManejoAmbientalEIADetalle);
		}
		if (tipoTabla.equals("17")) {
			this.listaPlanManejoAmbientalEIADetalleRehabilitacion.remove(planManejoAmbientalEIADetalle);
			this.listaPlanManejoAmbientalEIADetallleEliminados.add(planManejoAmbientalEIADetalle);
		}
		if (tipoTabla.equals("18")) {
			this.listaPlanManejoAmbientalEIADetalleAbandono.remove(planManejoAmbientalEIADetalle);
			this.listaPlanManejoAmbientalEIADetallleEliminados.add(planManejoAmbientalEIADetalle);
		}
	}

	public void cancelar() {
		JsfUtil.redirectTo("/prevencion/licenciamiento-ambiental/eia/pma/planManejoAmbiental.jsf");
	}

	public void cargarListasCambios(){
		for (PlanManejoAmbientalEIADetalle detalle : this.listaPlanManejoAmbientalEIADetallePrevencion) {
			if(detalle.isCambio())listaPlanManejoAmbientalEIADetallePrevencionCambio.add(detalle);
		}
		for (PlanManejoAmbientalEIADetalle detalle : listaPlanManejoAmbientalEIADetalleContingencias) {
			if(detalle.isCambio())listaPlanManejoAmbientalEIADetalleContingenciasCambio.add(detalle);
		}
		for (PlanManejoAmbientalEIADetalle detalle : listaPlanManejoAmbientalEIADetalleComunicacion) {
			if(detalle.isCambio())listaPlanManejoAmbientalEIADetalleComunicacionCambio.add(detalle);
		}
		for (PlanManejoAmbientalEIADetalle detalle : listaPlanManejoAmbientalEIADetalleSeguridad) {
			if(detalle.isCambio())listaPlanManejoAmbientalEIADetalleSeguridadCambio.add(detalle);
		}
		for (PlanManejoAmbientalEIADetalle detalle : listaPlanManejoAmbientalEIADetalleManejo) {
			if(detalle.isCambio())listaPlanManejoAmbientalEIADetalleManejoCambio.add(detalle);
		}
		for (PlanManejoAmbientalEIADetalle detalle : listaPlanManejoAmbientalEIADetalleRelaciones) {
			if(detalle.isCambio())listaPlanManejoAmbientalEIADetalleRelacionesCambio.add(detalle);
		}
		for (PlanManejoAmbientalEIADetalle detalle : listaPlanManejoAmbientalEIADetalleRehabilitacion) {
			if(detalle.isCambio())listaPlanManejoAmbientalEIADetalleRehabilitacionCambio.add(detalle);
		}
		for (PlanManejoAmbientalEIADetalle detalle : listaPlanManejoAmbientalEIADetalleAbandono) {
			if(detalle.isCambio()) listaPlanManejoAmbientalEIADetalleAbandonoCambio.add(detalle);
		}
	}

	public boolean existeCambiosPrevencion(){
		return listaPlanManejoAmbientalEIADetallePrevencionCambio.size()==0;
	}

	public boolean existeCambiosContingencias(){
		return listaPlanManejoAmbientalEIADetalleContingenciasCambio.size()==0;
	}

	public boolean existeCambiosComunicacion(){
		return listaPlanManejoAmbientalEIADetalleComunicacionCambio.size()==0;
	}

	public boolean existeCambiosSeguridad(){
		return listaPlanManejoAmbientalEIADetalleSeguridadCambio.size()==0;
	}

	public boolean existeCambiosManejo(){
		return listaPlanManejoAmbientalEIADetalleManejoCambio.size()==0;
	}

	public boolean existeCambiosRelaciones(){
		return listaPlanManejoAmbientalEIADetalleRelacionesCambio.size()==0;
	}

	public boolean existeCambiosRehabilitacion(){
		return listaPlanManejoAmbientalEIADetalleRehabilitacionCambio.size()==0;
	}

	public boolean existeCambiosAbandono(){
		return listaPlanManejoAmbientalEIADetalleAbandonoCambio.size()==0;
	}

	public void finalizar(){
		for (PlanManejoAmbientalEIADetalle detalle : this.listaPlanManejoAmbientalEIADetallePrevencion) {
			detalle.setCambio(false);
		}
		for (PlanManejoAmbientalEIADetalle detalle : listaPlanManejoAmbientalEIADetalleContingencias) {
			detalle.setCambio(false);
		}
		for (PlanManejoAmbientalEIADetalle detalle : listaPlanManejoAmbientalEIADetalleComunicacion) {
			detalle.setCambio(false);
		}
		for (PlanManejoAmbientalEIADetalle detalle : listaPlanManejoAmbientalEIADetalleSeguridad) {
			detalle.setCambio(false);
		}
		for (PlanManejoAmbientalEIADetalle detalle : listaPlanManejoAmbientalEIADetalleManejo) {
			detalle.setCambio(false);
		}
		for (PlanManejoAmbientalEIADetalle detalle : listaPlanManejoAmbientalEIADetalleRelaciones) {
			detalle.setCambio(false);
		}
		for (PlanManejoAmbientalEIADetalle detalle : listaPlanManejoAmbientalEIADetalleRehabilitacion) {
			detalle.setCambio(false);
		}
		for (PlanManejoAmbientalEIADetalle detalle : listaPlanManejoAmbientalEIADetalleAbandono) {
			detalle.setCambio(false);
		}
		this.planManejoAmbientalEIA.setPlanManejoAmbientalEIADetalle(this
				.generalistaPlanManejoAmbientalEIAEIADetalle(this.planManejoAmbientalEIA));
		this.eliminarDetalle();
		try {
			planManejoAmbientalEIAFacade.guardar(this.planManejoAmbientalEIA, null);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

}
