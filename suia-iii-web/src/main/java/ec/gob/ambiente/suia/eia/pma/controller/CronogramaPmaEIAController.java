package ec.gob.ambiente.suia.eia.pma.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.CronogramaPmaEia;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.PlanManejoAmbiental;
import ec.gob.ambiente.suia.domain.PlanManejoAmbientalEIA;
import ec.gob.ambiente.suia.domain.TipoPlanManejoAmbiental;
import ec.gob.ambiente.suia.eia.facade.EstudioImpactoAmbientalFacade;
import ec.gob.ambiente.suia.eia.pma.facade.PlanManejoAmbientalEIAFacade;
import ec.gob.ambiente.suia.eia.pma.facade.TipoPlanManejoAmbientalFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;

@ManagedBean
@ViewScoped
public class CronogramaPmaEIAController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -971306444001252479L;

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(CronogramaPmaEIAController.class);

	@EJB
	private ValidacionSeccionesFacade validacionesSeccionesFacade;

	private EstudioImpactoAmbiental estudioImpactoAmbiental;

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@Getter
	@Setter
	List<PlanManejoAmbientalEIA> listaPlanManejoAmbientalEIA;

	@EJB
	private PlanManejoAmbientalEIAFacade planManejoAmbientalEIAFacade;

	@EJB
	private TipoPlanManejoAmbientalFacade tipoPlanManejoAmbientalFacade;
	
	@EJB
	private EstudioImpactoAmbientalFacade estudioImpactoAmbientalFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;

	@Getter
	@Setter
	private PlanManejoAmbientalEIA planManejoAmbientalEIA;

	@Getter
	@Setter
	private Double total;
	
	// ////////////////////////Variables//////////////////////////////////

	private Map<String, Object> processVariables;
	private Integer numeroNotificaciones;
	private boolean existeObservaciones;
	private String promotor;

	@EJB
	private ProcesoFacade procesoFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	
	@Getter
	@Setter
	private List<CronogramaPmaEia> listaCronogramaOriginal, listaCronogramaSeleccionado;

//////////////////////////////////////////////////////////////////////////

	@PostConstruct
	private void postInit() throws Exception {

		
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
		
		
		//estudioImpactoAmbiental = estudioImpactoAmbientalFacade.obtenerEIAPorId(((EstudioImpactoAmbiental) JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT)).getId());

		estudioImpactoAmbiental = estudioImpactoAmbientalFacade.obtenerPorProyecto(proyectosBean.getProyecto());

		cargarDatos();
		totaliza();
	}

	@SuppressWarnings("unchecked")
	private void cargarDatos() throws Exception {

		this.planManejoAmbientalEIA = new PlanManejoAmbientalEIA();
		this.planManejoAmbientalEIA.setCronogramaPmaEIA(new ArrayList<CronogramaPmaEia>());
		this.total=0.0;
		
		
		//estudioImpactoAmbiental = (EstudioImpactoAmbiental) JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT);

		this.listaPlanManejoAmbientalEIA = new ArrayList<PlanManejoAmbientalEIA>();

		try {
			this.listaPlanManejoAmbientalEIA = planManejoAmbientalEIAFacade.listarPorEIA(estudioImpactoAmbiental);

			if (this.listaPlanManejoAmbientalEIA.size() > 0) {
				this.planManejoAmbientalEIA = this.listaPlanManejoAmbientalEIA.get(0);
			}
			if (this.planManejoAmbientalEIA.getCronogramaPmaEIA().size() == 0) {
				this.planManejoAmbientalEIA.setCronogramaPmaEIA(armarCronograma());
			}else{
				List<CronogramaPmaEia> listaCronograma = new ArrayList<CronogramaPmaEia>();
				List<CronogramaPmaEia> listaCronogramaBdd = planManejoAmbientalEIA.getCronogramaPmaEIA();
				for(CronogramaPmaEia cronograma : planManejoAmbientalEIA.getCronogramaPmaEIA()){
					if(cronograma.getIdHistorico() == null){
						listaCronograma.add(cronograma);
					}
				}
				this.planManejoAmbientalEIA.setCronogramaPmaEIA(listaCronograma);
				
				//MarielaG para buscar informacion original
				if (existeObservaciones) {
//					if (!promotor.equals(loginBean.getNombreUsuario())) {
						consultarCronogramaOriginal(listaCronogramaBdd);
//					}
				}
			}
			
			Collections.sort(this.planManejoAmbientalEIA.getCronogramaPmaEIA(), new Comparator() {

				@Override
				public int compare(Object o1, Object o2) {
					return new Integer(((CronogramaPmaEia)o1).getOrden()).compareTo(new Integer(((CronogramaPmaEia)o2).getOrden()));
				}
			});
			
			
		} catch (Exception e) {
			LOG.error(e, e);
		}
	}

	@SuppressWarnings("unchecked")
	private List<CronogramaPmaEia> armarCronograma() throws Exception {
		//Integer orden =1;
		List<CronogramaPmaEia> cronograma = new ArrayList<CronogramaPmaEia>();
		for (TipoPlanManejoAmbiental tipo : this.tipoPlanManejoAmbientalFacade
				.obtenerListaTipoPlanManejoAmbientalPorTipo("LICENCIA_AMBIENTAL")) {
			CronogramaPmaEia crono = new CronogramaPmaEia();
			crono.setEstado(true);
			crono.setPmaEIA(this.planManejoAmbientalEIA);
			crono.setTipoPlanManejoAmbiental(tipo);
			crono.setMes1(false);
			crono.setMes2(false);
			crono.setMes3(false);
			crono.setMes4(false);
			crono.setMes5(false);
			crono.setMes6(false);
			crono.setMes7(false);
			crono.setMes8(false);
			crono.setMes9(false);
			crono.setMes10(false);
			crono.setMes11(false);
			crono.setMes12(false);
			crono.setOrden(tipo.getId());
			crono.setPresupuesto(0.0);
			cronograma.add(crono);
			
			
		}
		
		Collections.sort(cronograma, new Comparator() {

			@Override
			public int compare(Object o1, Object o2) {
				return new Integer(((CronogramaPmaEia)o1).getOrden()).compareTo(new Integer(((CronogramaPmaEia)o2).getOrden()));
			}
		});
		
		return cronograma;
	}

	/**
	 * Guarda la lista de Análisis de Riesgo ingresadas y el documento al Alfresco
	 * 
	 * @throws Exception
	 */
	public void guardar(String tipo) throws Exception {
		try {
			
			//MarielaG para validar antes de guardar si existe el plan de manejo ambiental
			if (this.planManejoAmbientalEIA.getId() == null) {
				List<PlanManejoAmbientalEIA> listaPlanManejoAmbientalEIA = planManejoAmbientalEIAFacade.listarPorEIA(estudioImpactoAmbiental);
				if (listaPlanManejoAmbientalEIA.size() > 0) {
					this.planManejoAmbientalEIA = this.listaPlanManejoAmbientalEIA.get(0);
				}
			}

			if (this.planManejoAmbientalEIA.getId() == null) {
				// this.planManejoAmbientalEIA = new PlanManejoAmbientalEIA();
				this.planManejoAmbientalEIA.setEia(estudioImpactoAmbiental);
				this.planManejoAmbientalEIA.setTipo("EIA");
				this.planManejoAmbientalEIA.setPmaReferencia(null);
				this.planManejoAmbientalEIA.setFechaRegistro(new Date());
			}
			
			for(CronogramaPmaEia crono : this.planManejoAmbientalEIA.getCronogramaPmaEIA())
			{
				if(crono.getPresupuesto()==0.0)
				{
					JsfUtil.addMessageError("El presupuesto de cada uno de los planes de manejo ambiental debe ser mayor a 0");
					return;
				}
				if(!crono.getMes1()&& !crono.getMes2()&& !crono.getMes3()&& !crono.getMes4()&& !crono.getMes5()&& !crono.getMes6()&& !crono.getMes7()
						&& !crono.getMes8()&& !crono.getMes9()&& !crono.getMes10()&& !crono.getMes11()&& !crono.getMes12())
				{
					JsfUtil.addMessageError("Los planes de manejo ambiental deben ser ejecutados en al menos 1 mes cada uno");
					return;
				}
			}
			
			if(existeObservaciones){
				planManejoAmbientalEIAFacade.guardarHistorico(this.planManejoAmbientalEIA, null, numeroNotificaciones);
			}else{
				planManejoAmbientalEIAFacade.guardar(this.planManejoAmbientalEIA, null);
			}

			cargarDatos();
			totaliza();
			
			actualizarEstadoValidacionSeccion();
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		} catch (ServiceException e) {
			LOG.error(e, e);
			JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + " " + e.getMessage());
		} catch (RuntimeException e) {
			LOG.error(e, e);
			JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + " " + e.getMessage());
		}

	}

	/**
	 * Asigna la sección válida
	 * 
	 * @throws ServiceException
	 * @throws CmisAlfrescoException
	 */
	private void actualizarEstadoValidacionSeccion() throws ServiceException, CmisAlfrescoException {
		validacionesSeccionesFacade.guardarValidacionSeccion("EIA", "cronogramaPma", estudioImpactoAmbiental
				.getId().toString());

	}
	
	public void cancelar() {
		JsfUtil.redirectTo("/prevencion/licenciamiento-ambiental/eia/pma/cronogramaPma.jsf");
	}
	
	public void totaliza() {	
		
		total = 0.0;
		
		for(CronogramaPmaEia crono: this.planManejoAmbientalEIA.getCronogramaPmaEIA())
		{
			total+=crono.getPresupuesto();
		}
	}

	/**
	 * MarielaG
	 * Consultar el cronograma ingresado antes de las correcciones
	 */
	private void consultarCronogramaOriginal(List<CronogramaPmaEia>  listaCronogramaBdd) {
		try {
			List<CronogramaPmaEia> cronogramasOriginales = new ArrayList<CronogramaPmaEia>();
			int totalModificados = 0;
			
			for(CronogramaPmaEia cronogramaBdd : listaCronogramaBdd){
				if(cronogramaBdd.getNumeroNotificacion() == null ||
						!cronogramaBdd.getNumeroNotificacion().equals(numeroNotificaciones)){
					boolean agregarItemLista = true;
	    			//buscar si tiene historial
					for (CronogramaPmaEia cronogramaHistorico : listaCronogramaBdd) {
						if (cronogramaHistorico.getIdHistorico() != null  
								&& cronogramaHistorico.getIdHistorico().equals(cronogramaBdd.getId())) {
							//si existe un registro historico, no se agrega a la lista en este paso
							agregarItemLista = false;
							cronogramaBdd.setRegistroModificado(true);
							break;
						}
					}
					if (agregarItemLista) {
						cronogramasOriginales.add(cronogramaBdd);
					}
				} else {
					totalModificados++;
	    			//es una modificacion
	    			if(cronogramaBdd.getIdHistorico() == null && cronogramaBdd.getNumeroNotificacion().equals(numeroNotificaciones)){
	    				//es un registro nuevo
	    				//no ingresa en el lista de originales
	    			}else{
	    				cronogramaBdd.setRegistroModificado(true);
	    				if(!cronogramasOriginales.contains(cronogramaBdd)){
	    					cronogramasOriginales.add(cronogramaBdd);
	    				}
	    			}
	    		}
			}
			
			if (totalModificados > 0){
				listaCronogramaOriginal = cronogramasOriginales;
			}
			 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void mostrarRegistroOriginal(CronogramaPmaEia cronogramaActual) {
		listaCronogramaSeleccionado = new ArrayList<CronogramaPmaEia>();
		
		for(CronogramaPmaEia cronogramaOriginal : listaCronogramaOriginal){
			if(cronogramaOriginal.getIdHistorico() != null && cronogramaActual.getId().equals(cronogramaOriginal.getIdHistorico())){
				listaCronogramaSeleccionado.add(cronogramaOriginal);
			}
		}
	}
}
