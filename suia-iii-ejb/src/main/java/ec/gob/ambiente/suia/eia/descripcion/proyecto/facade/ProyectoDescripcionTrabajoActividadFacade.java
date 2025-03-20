/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.eia.descripcion.proyecto.facade;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.DetalleProyectoDescripcionTrabajoActividad;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.ProyectoDescripcionTrabajoActividad;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 *
 * @author christian
 */
@LocalBean
@Stateless
public class ProyectoDescripcionTrabajoActividadFacade {

    @EJB
    private CrudServiceBean crudServiceBean;

    public List<ProyectoDescripcionTrabajoActividad> listarPorEIA(final EstudioImpactoAmbiental estudioImpactoAmbiental) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idEstudioImpactoAmbiental", estudioImpactoAmbiental.getId());
            List<ProyectoDescripcionTrabajoActividad> lista = (List<ProyectoDescripcionTrabajoActividad>) crudServiceBean.findByNamedQuery(ProyectoDescripcionTrabajoActividad.LISTAR_POR_EIA, params);
            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    public void guardarPrimeraVez(final List<ProyectoDescripcionTrabajoActividad> listaProyecto) throws ServiceException {
        crudServiceBean.saveOrUpdate(listaProyecto);
    }

    public List<DetalleProyectoDescripcionTrabajoActividad> listarPorProyecto(final ProyectoDescripcionTrabajoActividad proyectoDescripcionTrabajoActividad) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idProyecto", proyectoDescripcionTrabajoActividad.getId());
            List<DetalleProyectoDescripcionTrabajoActividad> lista = (List<DetalleProyectoDescripcionTrabajoActividad>) crudServiceBean.findByNamedQuery(DetalleProyectoDescripcionTrabajoActividad.LISTAR_POR_PROYECTO, params);
            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    public void guardar(final ProyectoDescripcionTrabajoActividad proyectoDescripcionTrabajoActividad, final List<DetalleProyectoDescripcionTrabajoActividad> listaDetalleEliminado) throws ServiceException {
        try {
            crudServiceBean.saveOrUpdate(proyectoDescripcionTrabajoActividad);
            crudServiceBean.saveOrUpdate(listaDetalleEliminado);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
}
