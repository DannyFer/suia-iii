package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.CatalogoFasesCoa;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class CatalogoFasesCoaFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;

	public List<CatalogoFasesCoa> obtenerListaCatalogoFases() {
		Query sql=crudServiceBean.getEntityManager().createQuery("Select p from CatalogoFasesCoa p where p.estado=true");
		return  sql.getResultList();	
	}

	public List<CatalogoFasesCoa> obtenerFasesPorSector(Integer sectorId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("tipoSectorId", sectorId);
		
		List<CatalogoFasesCoa> lista = (List<CatalogoFasesCoa>) crudServiceBean
					.findByNamedQuery(
							CatalogoFasesCoa.GET_FASES_POR_SECTOR,
							parameters);
		if(lista.size() > 0 ){
			return lista;
		}
		return  new ArrayList<CatalogoFasesCoa>();
	}


	public CatalogoFasesCoa obtenerFasesPorId(Integer faseId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("tipoSectorId", faseId);

		Query sql=crudServiceBean.getEntityManager().
				createQuery("Select p from CatalogoFasesCoa p where p.estado=true and id = :faseId").
				setParameter("faseId", faseId);
		
		return  (CatalogoFasesCoa) sql.getSingleResult();
	}
}