package ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.Conductor;
import ec.gob.ambiente.suia.exceptions.ServiceException;

@Stateless
public class ConductorFacade {
	@EJB
	private CrudServiceBean crudServiceBean;

	public Conductor buscarConductoresPorCedula(String documento) {
		List<Conductor> conductores = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("documento", documento);
		conductores = (List<Conductor>) crudServiceBean.findByNamedQuery(
				Conductor.OBTENER_CONDUCTOR_CEDULA, parameters);
		if (!conductores.isEmpty())
			return  conductores.get(0);
		else
			return null;

	}
	
	public boolean guardar(Conductor datoConsultants, boolean existConsultor)  {
		  try {
		crudServiceBean.saveOrUpdate(datoConsultants);
		return true;
		  } catch (RuntimeException e) {
	        	e.printStackTrace();
				return false;
	        }
	}
	
	public List<Conductor> listconductores() throws ServiceException {
		
		List<Conductor> lista = null;
		try {
//			lista = (List<Area>) crudServiceBean.findByNamedQuery(
//					Area.LISTAR_PADRES, null);
			lista = crudServiceBean
					.getEntityManager()
					.createQuery(
							" FROM Conductor a where a.estado = true ORDER BY a.nombre")
					.getResultList();
			
//			for (Conductor area : lista) {
//				if(area.getAnio() != null)
//					area.getTipoArea().toString();
//			}
		} catch (RuntimeException e) {
			throw new ServiceException(e);
		}
		return lista;
	}
	
}
