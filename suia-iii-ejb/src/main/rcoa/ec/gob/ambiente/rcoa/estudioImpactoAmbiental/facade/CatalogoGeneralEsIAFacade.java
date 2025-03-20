package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.CatalogoGeneralEsIA;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class CatalogoGeneralEsIAFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	public CatalogoGeneralEsIA buscarPorCodigo(String codigo){
		try {
			
			Query query = crudServiceBean
					.getEntityManager()
					.createQuery("SELECT c FROM CatalogoGeneralEsIA c where c.estado = true and c.codigo = :codigo")
					.setParameter("codigo", codigo);

			CatalogoGeneralEsIA resultado = (CatalogoGeneralEsIA) query.getSingleResult();
			return resultado;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
			return null;
	}
	

	

}


