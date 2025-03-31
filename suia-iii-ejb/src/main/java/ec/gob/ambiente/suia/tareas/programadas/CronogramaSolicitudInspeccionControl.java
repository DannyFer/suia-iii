/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.tareas.programadas;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import ec.gob.ambiente.suia.administracion.facade.ImpedidosFacade;

/**
 *
 * @author carlos.pupo
 */
@LocalBean
@Singleton
public class CronogramaSolicitudInspeccionControl {

	@EJB
	private ImpedidosFacade impedidosFacade;
	//private static final Logger LOG = Logger.getLogger(CronogramaSolicitudInspeccionControl.class);

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	//@Schedule(minute = "*/15", hour = "*")
	public void ejecutarCronogramas() {
		//LOG.info("Ejecutando cronogramas de inspeccion de control ambiental");
	}

}
