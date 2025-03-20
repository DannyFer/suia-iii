/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.eia.descripcion.proyecto.facade;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.EiaOpciones;
import ec.gob.ambiente.suia.domain.EiaOpcionesDetalle;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import java.util.ArrayList;
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
public class EiaOpcionesFacade {

    @EJB
    private CrudServiceBean crudServiceBean;

    private static final String ID_TIPO_SECTOR = "idTipoSector";
    private static final String ID_ESTUDIO = "idEstudio";

    public Map<String, EiaOpciones> obtenerPorTipoSector(final Integer idTipoSector) throws ServiceException {
        try {
            Map<String, EiaOpciones> devolver = new HashMap<String, EiaOpciones>();
            Map<String, Object> params = new HashMap<String, Object>();
            params.put(ID_TIPO_SECTOR, idTipoSector);
            List<EiaOpciones> lista = (List<EiaOpciones>) crudServiceBean.findByNamedQuery(EiaOpciones.LISTAR_POR_TIPO_SECTOR, params);
            for (EiaOpciones eia : lista) {
                devolver.put(eia.getNumeroIdentificacion(), eia);
            }
            return devolver;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    public void guardar(final EstudioImpactoAmbiental es, final EiaOpciones eiaOpciones) throws ServiceException {
        try {
            if (validarGuardarDetalle(es, eiaOpciones)) {
                EiaOpcionesDetalle obj = new EiaOpcionesDetalle();
                obj.setEiaOpciones(eiaOpciones);
                obj.setEstado(true);
                obj.setEstudioImpactoAmbiental(es);
                crudServiceBean.saveOrUpdate(obj);
            }
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    private boolean validarGuardarDetalle(final EstudioImpactoAmbiental es, final EiaOpciones eiaOpciones) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(ID_ESTUDIO, es.getId());
        params.put("numeroIdentificacion", eiaOpciones.getNumeroIdentificacion());
        List<EiaOpcionesDetalle> lista = (List<EiaOpcionesDetalle>) crudServiceBean.findByNamedQuery(EiaOpcionesDetalle.LISTAR_POR_ESTUDIO_OPCION, params);
        return lista == null || lista.isEmpty();
    }

    public List<String> validarEnviarEstudio(final EstudioImpactoAmbiental estudioImpactoAmbiental) throws ServiceException {
        List<String> listaMensajes = new ArrayList<String>();
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put(ID_TIPO_SECTOR, estudioImpactoAmbiental.getProyectoLicenciamientoAmbiental().getTipoSector().getId());
            List<EiaOpciones> lista = (List<EiaOpciones>) crudServiceBean.findByNamedQuery(EiaOpciones.LISTAR_POR_TIPO_SECTOR, params);
            params = new HashMap<String, Object>();
            params.put(ID_ESTUDIO, estudioImpactoAmbiental.getId());
            List<EiaOpcionesDetalle> listaDetalle = (List<EiaOpcionesDetalle>) crudServiceBean.findByNamedQuery(EiaOpcionesDetalle.LISTAR_POR_ESTUDIO, params);
            for (EiaOpciones op : lista) {
                String mensaje = devuelveObservacion(op, listaDetalle);
                if (mensaje != null) {
                    listaMensajes.add(mensaje);
                }
            }
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
        return listaMensajes;
    }

    private String devuelveObservacion(final EiaOpciones op, final List<EiaOpcionesDetalle> listaDetalle) {
        String devuelve = null;
        int bandera = 0;
        for (EiaOpcionesDetalle det : listaDetalle) {
            if (op.getId().equals(det.getIdOpciones())) {
                bandera = 1;
                break;
            }
        }
        if (bandera == 0) {
            devuelve = op.getNumeroIdentificacion() + " " + op.getNombre();
        }
        return devuelve;
    }

}
