/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.tareas.programadas;

import ec.gob.ambiente.suia.administracion.facade.ImpedidosFacade;
import ec.gob.ambiente.suia.domain.Impedido;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author christian
 */
@LocalBean
@Singleton
public class ImpedidosCron {

    @EJB
    private ImpedidosFacade impedidosFacade;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(ImpedidosCron.class);

    private String cambiarEstadoFecha() {
        String retorno;
        try {
            List<Impedido> lista = impedidosFacade.listarFechaNotNull();
            List<Impedido> listaActualizar = new ArrayList<Impedido>();
            Date fechaSistema = new Date();
            for (Impedido imp : lista) {
                if (fechaSistema.after(imp.getFechaInactivo())) {
                    imp.setEstado(false);
                    listaActualizar.add(imp);
                }
            }
            impedidosFacade.saveOrUpdate(listaActualizar);
            retorno = "ok proceso impedidos ejecutado con éxito";
        } catch (ServiceException e) {
            retorno = "error proceso impedidos " + e.getMessage();
            LOG.error(e, e);
        }
        return retorno;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Schedule(dayOfWeek = "*", hour = "2", minute = "10")
    public void ejecutarProceso() {
//        LOG.info(cambiarEstadoFecha());
    }

}
