package ec.gob.ambiente.suia.eia.definicionAreaEstudio.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.eia.facade.EstudioImpactoAmbientalFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;

@ManagedBean
@ViewScoped
public class DefinicionAreaEstudioBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1921747409061085097L;

	@EJB
	EstudioImpactoAmbientalFacade estudioImpactoAmbientalFacade;
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private ValidacionSeccionesFacade validacionSeccionesFacade;

	@ManagedProperty(value = "#{proyectosBean}")
	@Getter
	@Setter
	private ProyectosBean proyectosBean;

	@Getter
	@Setter
	private EstudioImpactoAmbiental estudioImpactoAmbiental, estudioHistorico, estudioOriginal;
	
	@Getter
	@Setter
	private boolean existeObservaciones;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
	
	private Map<String, Object> processVariables;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
	private Integer numeroNotificaciones;

	@PostConstruct
	public void init() throws JbpmException {

		// estudioImpactoAmbiental = estudioImpactoAmbientalFacade
		// .obtenerPorIdProceso(1556L, "FINAL");
		// // en la implementacion se tiene que comentar arriba y aplicar lo
		// de
		// abajo

		// estudioImpactoAmbiental = estudioImpactoAmbientalFacade
		// .obtenerPorProyectoTipo(proyectosBean.getProyecto(),
		// "FINAL");
		
		processVariables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getProcessId());
		
		String numNotificaciones = (String) processVariables.get("cantidadNotificaciones");
		if(numNotificaciones != null){
			numeroNotificaciones = Integer.valueOf(numNotificaciones);
		}else{
			numeroNotificaciones = 0;
		}	
//		String promotor = (String) processVariables.get("u_Promotor");

		//estudioImpactoAmbiental = estudioImpactoAmbientalFacade.obtenerEIAPorId(((EstudioImpactoAmbiental) JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT)).getId());
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		estudioImpactoAmbiental=(EstudioImpactoAmbiental) request.getSession().getAttribute("estudio");

		if(numeroNotificaciones > 0){
			existeObservaciones = true;
//			if(promotor.equals(loginBean.getNombreUsuario())){
				consultarRegistroAnterior();
//			} else {
				consultarHistorico();
//			}
		}
		
	}

	public void guardar() {
		try {
			estudioImpactoAmbiental = estudioImpactoAmbientalFacade
					.guardar(estudioImpactoAmbiental);
			validacionSeccionesFacade.guardarValidacionSeccion("EIA",
					"definicionAreaEstudio", estudioImpactoAmbiental.getId()
							.toString());
			
			if(existeObservaciones){
				if(validarGuardarHistoricoEA())
					estudioImpactoAmbientalFacade.guardar(estudioHistorico);
			}
			
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			JsfUtil.redirectTo("/prevencion/licenciamiento-ambiental/eia/definicionAreaEstudio/definicionAreaEstudio.jsf");
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void cancelar() {
		JsfUtil.redirectTo("/prevencion/licenciamiento-ambiental/eia/definicionAreaEstudio/definicionAreaEstudio.jsf");
	}
	
	/**
	 * Cristina Flores aumento de metódo para guardar el historico.
	 */	
	private void consultarRegistroAnterior(){
		try {			
			estudioHistorico = estudioImpactoAmbiental.clone();
			estudioHistorico.setFechaModificacion(null);
			estudioHistorico.setIdHistorico(estudioImpactoAmbiental.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	private boolean validarGuardarHistoricoEA(){		
		try {
			List<EstudioImpactoAmbiental> lista = estudioImpactoAmbientalFacade.busquedaRegistrosHistorico(estudioImpactoAmbiental.getId());
		
			/**
			 * Si el tamaño de la lista es el mismo que el número de notificiaciones entonces no se guarda
			 * Si la lista es nula entonces se guarda
			 * Si el tamaño de la lista es menor que el número de notificaciones se guarda
			 */
			
			if(lista != null && !lista.isEmpty()){
				if(numeroNotificaciones > lista.size())
					return true; // se guarda
				else
					return false;  //no se guarda
			}else {
				if(estudioImpactoAmbiental.getDefinicionAreaEstudio().equals(estudioHistorico.getDefinicionAreaEstudio()))
					return false;
				else
					return true; //se guarda porque cambiaron los campos
			}				
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}		
	}
	
	/**
	 * MarielaG
	 * Consultar el estudio ingresado antes de las correcciones
	 * Cris F: Cambio de método para que aparezca todos los historicos.
	 */
	@Getter
	private List<EstudioImpactoAmbiental> listaEstudioOriginales;
	@Getter
	private Boolean mostrarTabla;
	private void consultarHistorico() {
		try {
			
			mostrarTabla = false;
			List<EstudioImpactoAmbiental> listaHistorial =  estudioImpactoAmbientalFacade.busquedaRegistrosHistorico(estudioImpactoAmbiental.getId());
			listaEstudioOriginales = new ArrayList<EstudioImpactoAmbiental>();
			
			if(listaHistorial != null && !listaHistorial.isEmpty()){
				
				for(EstudioImpactoAmbiental estudioH : listaHistorial){
					if(estudioH.getDefinicionAreaEstudio() == null && estudioImpactoAmbiental.getDefinicionAreaEstudio() != null){
						estudioH.setDefinicionAreaEstudio("");
						listaEstudioOriginales.add(0, estudioH);
					}else if(estudioH.getDefinicionAreaEstudio() == null && estudioImpactoAmbiental.getDefinicionAreaEstudio() == null){
						listaEstudioOriginales = new ArrayList<EstudioImpactoAmbiental>();
					}
					else{
						if(!estudioH.getDefinicionAreaEstudio().equals(estudioImpactoAmbiental.getDefinicionAreaEstudio())){
							listaEstudioOriginales.add(0, estudioH);
						}
					}
				}				
			}
			
			if(listaEstudioOriginales.isEmpty())
				mostrarTabla = false;
			else
				mostrarTabla = true;
			
//			List<EstudioImpactoAmbiental> lista = estudioImpactoAmbientalFacade
//					.busquedaRegistrosHistorico(estudioImpactoAmbiental.getId());
//			if (lista != null && !lista.isEmpty() && lista.size() == numeroNotificaciones) {
//				estudioOriginal = lista.get(0);
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
