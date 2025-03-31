package ec.gob.ambiente.suia.catalogos.mineria.service;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.FaseMinera;
import ec.gob.ambiente.suia.exceptions.ServiceException;

@Stateless
public class FaseMineraService implements Serializable{
	
	private static final long serialVersionUID = -6719522108444048554L;
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	
	public FaseMinera faseMinera(int id) throws ServiceException{
		return crudServiceBean.find(FaseMinera.class, id);
	}
	
	@SuppressWarnings("unchecked")
    public List<FaseMinera> listarTodasFasesMineras() throws ServiceException {
        List<FaseMinera> lista = null;
        try {
            lista = (List<FaseMinera>) crudServiceBean.findByNamedQuery(FaseMinera.FIND_ALL, null);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
        return lista;
    }
}
