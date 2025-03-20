package ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;

@Stateless
public class ActividadDesechoFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@SuppressWarnings("unchecked")
	public List<DesechoPeligroso> buscarDesechosPorActividad(Integer id){
		
		List<DesechoPeligroso> lista = new ArrayList<DesechoPeligroso>();
		
		try {

			lista = (List<DesechoPeligroso>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT a FROM ActividadDesecho a where a.estado = true and a.catalogoCiiu.id = :id")
					.setParameter("id", id).getResultList();			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return lista;		
	}
	
	@SuppressWarnings("unchecked")
	public List<DesechoPeligroso> buscarDesechosPorCodigoActividad(String codigo){
		
		List<DesechoPeligroso> lista = new ArrayList<DesechoPeligroso>();
		
		try {

			lista = (List<DesechoPeligroso>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT distinct a.desechoPeligroso  FROM ActividadDesecho a where a.estado = true and a.codigoCuatroDigitos = :codigo or "
							+ "a.codigoCincoDigitos = :codigo or a.codigoSeisDigitos = :codigo")
					.setParameter("codigo", codigo).getResultList();		
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return lista;		
	}

}
