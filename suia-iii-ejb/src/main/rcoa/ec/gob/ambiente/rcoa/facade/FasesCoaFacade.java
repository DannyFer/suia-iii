package ec.gob.ambiente.rcoa.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;


import ec.gob.ambiente.rcoa.model.FasesCoa;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class FasesCoaFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	public FasesCoa guardar(FasesCoa fase) {        
        	return crudServiceBean.saveOrUpdate(fase);        
    }
	
	@SuppressWarnings("unchecked")
	public List<FasesCoa> listarFases()
	{
		Query sql=crudServiceBean.getEntityManager().createQuery("Select p from FasesCoa p where p.estado=true and p.codigo not in ('CA') order by p.descripcion ");
		List<FasesCoa> obj = (List<FasesCoa>) sql.getResultList();
		if(obj.size()>0)
			return obj;
		return  new ArrayList<>();
	}

}
