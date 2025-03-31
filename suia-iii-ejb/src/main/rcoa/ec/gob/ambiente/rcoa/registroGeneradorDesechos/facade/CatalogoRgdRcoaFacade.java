package ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.CatalogoRgdRcoa;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class CatalogoRgdRcoaFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public CatalogoRgdRcoa findByCodigo(String codigo){
		try {
			CatalogoRgdRcoa catalogo = (CatalogoRgdRcoa) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM CatalogoRgdRcoa o where o.codigo = :codigo and o.estado = true")
					.setParameter("codigo", codigo)					
					.getSingleResult();
			
			return catalogo;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}

}
