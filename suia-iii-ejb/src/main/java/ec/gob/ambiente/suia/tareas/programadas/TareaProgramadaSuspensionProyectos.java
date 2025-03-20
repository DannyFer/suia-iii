/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.tareas.programadas;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.AprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoSuspendidoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;

/**
 *
 * @author santiago.flores
 */
@LocalBean
@Singleton
public class TareaProgramadaSuspensionProyectos {

	@EJB
	private ProcesoSuspendidoFacade procesoSuspendidoFacade;
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB 
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
	
	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;
	
	@EJB
	private AprobacionRequisitosTecnicosFacade aprobacionRequisitosTecnicosFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	public List<String[]>listaCodigosProyectos = new ArrayList<String[]>();
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Schedule(second = "00", minute = "*/05", hour = "00-01", dayOfWeek = "Mon-Sun", dayOfMonth = "*", month = "*", year = "*", info = "Desactivación 90 días",persistent = true)
	public void ejecutarProyectosNoFinalizanRegistro() {
//		procesoSuspendidoFacade.obtenerProyectosNoFinalizaRegistro();
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Schedule(second = "00", minute = "*/20", hour = "00-01", dayOfWeek = "Mon-Sun", dayOfMonth = "*", month = "*", year = "*", info = "Desactivación 90 días",persistent = true)
	public void ejecutarProyectosRegistroAmbiental90Dias() {
//		procesoSuspendidoFacade.obtenerProyectosMayor90DiasRegistro();
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Schedule(second = "00", minute = "*/35", hour = "00-01", dayOfWeek = "Mon-Sun", dayOfMonth = "*", month = "*", year = "*", info = "Desactivación 90 días",persistent = true)
	public void ejecutarProyectosViabilidad90Dias() {
//		procesoSuspendidoFacade.obtenerProyectosMayor90DiasViabilidad();
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Schedule(second = "00", minute = "*/50", hour = "00-01", dayOfWeek = "Mon-Sun", dayOfMonth = "*", month = "*", year = "*", info = "Desactivación 90 días",persistent = true)
	public void ejecutarProyectosLicenciaAmbiental90Dias() {
//		procesoSuspendidoFacade.obtenerProyectosMayor90DiasLicencia();
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Schedule(second = "00", minute = "*/05", hour = "01-02", dayOfWeek = "Mon-Sun", dayOfMonth = "*", month = "*", year = "*", info = "Desactivación 90 días",persistent = true)
	public void ejecutarProyectosEvaluacionSocial90Dias() {
//		procesoSuspendidoFacade.obtenerProyectosMayor90DiasEvaluacionSocial();
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Schedule(second = "00", minute = "*/20", hour = "01-02", dayOfWeek = "Mon-Sun", dayOfMonth = "*", month = "*", year = "*", info = "Desactivación 90 días",persistent = true)
	public void ejecutarProyectosART90Dias() {
//		procesoSuspendidoFacade.obtenerProyectosMayor90DiasART();
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Schedule(second = "00", minute = "*/35", hour = "01-02", dayOfWeek = "Mon-Sun", dayOfMonth = "*", month = "*", year = "*", info = "Desactivación 90 días",persistent = true)
	public void ejecutarProyectosDescargaPronunciamientoLicencia() {
//		procesoSuspendidoFacade.obtenerProyectosMayor30DiasDescargarPronunciamientoPagos();
	} 
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Schedule(second = "00", minute = "50", hour = "01-02", dayOfWeek = "Mon-Sun", dayOfMonth = "*", month = "*", year = "*", info = "Desactivación 90 días",persistent = true)
	public void notificacionAntesDeArchivarProyectos() {
//		procesoSuspendidoFacade.notifcarProyectos();
	}
	
}
