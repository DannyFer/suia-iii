package ec.gob.ambiente.suia.sistemaIntegrado.facade;

import java.math.BigInteger;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.SistemasIntegrales;

/**
 * 
 * @author Santiago Flores
 */
@Stateless
public class SistemaIntegradoFacade {

    @EJB
    private CrudServiceBean crudServiceBean;
    
    public void guadarSistemasIntegrados(SistemasIntegrales sistemasIntegrales){
        crudServiceBean.saveOrUpdate(sistemasIntegrales);
    }
    public void guardarListaSistInt(List<SistemasIntegrales> listSist){
        crudServiceBean.saveOrUpdate(listSist);
    }
    
    public void eliminarSistemaIntegrado(SistemasIntegrales sistemasIntegrales){
        crudServiceBean.delete(sistemasIntegrales);
    }

public BigInteger obtenerSecuencia(String nombreSecuencia, String nombreSchema){
        return (BigInteger) crudServiceBean.getSecuenceNextValue(nombreSecuencia,nombreSchema);
    }

    
}