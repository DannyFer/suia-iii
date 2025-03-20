/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CatalogoTipoMaterial;
import ec.gob.ambiente.suia.domain.FichaAmbientalMineria;
import ec.gob.ambiente.suia.domain.TipoMaterial;
import ec.gob.ambiente.suia.exceptions.ServiceException;

/**
 *
 * @author christian
 */
@LocalBean
@Stateless
public class CaracteristicasGeneralesMineriaFacade {

    @EJB
    private CrudServiceBean crudServiceBean;

    @SuppressWarnings("unchecked")
	public List<CatalogoTipoMaterial> listarPorTipoMaterial(final TipoMaterial tipoMaterial) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idTipoMaterial", tipoMaterial.getId());
            return (List<CatalogoTipoMaterial>) crudServiceBean.findByNamedQuery(CatalogoTipoMaterial.LISTAR_POR_TIPO_MATERIAL, params);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    @SuppressWarnings("unchecked")
	public List<CatalogoTipoMaterial> listarPorTipoMaterialFichaAmbiental(final FichaAmbientalMineria fichaAmbientalMineria) throws ServiceException {
        try {
            String jpaQl = "SELECT c FROM CatalogoTipoMaterial c WHERE c.id IN (" + fichaAmbientalMineria.getCatalogoTipoMaterial() + ")";
            return crudServiceBean.getEntityManager().createQuery(jpaQl).getResultList();
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    
    public CatalogoTipoMaterial obtenerPorId(Integer id) {
        return crudServiceBean.find(CatalogoTipoMaterial.class, id);
    }

}
