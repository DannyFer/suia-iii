/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.eia.descripcion.proyecto.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.administracion.facade.AdjuntosEiaFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.DeterminacionAreaInfluenciaEIA;
import ec.gob.ambiente.suia.domain.EiaOpciones;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.dto.EntityAdjunto;
import ec.gob.ambiente.suia.exceptions.ServiceException;

/**
 *
 * @author christian
 */
@LocalBean
@Stateless
public class DeterminacionAreaInfluenciaEiaFacade {

    @EJB
    private CrudServiceBean crudServiceBean;
    @EJB
    private AdjuntosEiaFacade adjuntosEiaFacade;

    public void guardarConAdjunto(final List<DeterminacionAreaInfluenciaEIA> listaAnalisisRiesgoEia, final EstudioImpactoAmbiental estudioImpactoAmbiental, final EntityAdjunto entityAdjunto, final EiaOpciones eiaOpciones) throws ServiceException {
        try {
            crudServiceBean.saveOrUpdate(listaAnalisisRiesgoEia);
            adjuntosEiaFacade.guardarAdjunto(entityAdjunto, estudioImpactoAmbiental.getClass().getSimpleName(), estudioImpactoAmbiental.getId(), eiaOpciones, estudioImpactoAmbiental);
            for (DeterminacionAreaInfluenciaEIA a : listaAnalisisRiesgoEia) {
                if (a.getEntityAdjunto() != null) {
                    EntityAdjunto obj = a.getEntityAdjunto();
                    StringBuilder nombre = new StringBuilder();
                    nombre.append("EIA");
                    nombre.append("DAI");
                    nombre.append(a.getId());
                    nombre.append(".").append(a.getEntityAdjunto().getExtension());
                    obj.setNombre(nombre.toString());
                    adjuntosEiaFacade.guardarAdjunto(obj, a.getClass().getSimpleName(), a.getId());
                }
            }

        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public void guardar(final List<DeterminacionAreaInfluenciaEIA> listaAnalisisRiesgoEia) throws ServiceException {
        try {
            crudServiceBean.saveOrUpdate(listaAnalisisRiesgoEia);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    public List<DeterminacionAreaInfluenciaEIA> listarPorEIA(final EstudioImpactoAmbiental estudioImpactoAmbiental) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idEstudioImpactoAmbiental", estudioImpactoAmbiental.getId());
            List<DeterminacionAreaInfluenciaEIA> lista = (List<DeterminacionAreaInfluenciaEIA>) crudServiceBean.findByNamedQuery(DeterminacionAreaInfluenciaEIA.LISTAR_POR_EIA, params);
            return lista;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
}
