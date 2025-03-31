package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.retce.model.CaracteristicasPuntoMonitoreo;
import ec.gob.ambiente.retce.model.CaracteristicasPuntoMonitoreoTabla;
import ec.gob.ambiente.retce.model.ParametrosDescargasLiquidas;
import ec.gob.ambiente.retce.model.ParametrosTablas;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class ParametrosDescargasLiquidasFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public ParametrosDescargasLiquidas findById(Integer id){
		try {
			ParametrosDescargasLiquidas parametroDescargas = (ParametrosDescargasLiquidas) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM ParametrosDescargasLiquidas o where o.id = :id")
					.setParameter("id", id).getSingleResult();
			return parametroDescargas;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void save(ParametrosDescargasLiquidas obj, Usuario usuario){
		if(obj.getId()==null){
			obj.setUsuarioCreacion(usuario.getNombre());
			obj.setFechaCreacion(new Date());			
		}
		else{
			obj.setUsuarioModificacion(usuario.getNombre());
			obj.setFechaModificacion(new Date());			
		}
		crudServiceBean.saveOrUpdate(obj);
	}
	
	@SuppressWarnings("unchecked")
	public List<ParametrosDescargasLiquidas> findAll(){
		List<ParametrosDescargasLiquidas> lista = new ArrayList<ParametrosDescargasLiquidas>();
		try {
			lista = (ArrayList<ParametrosDescargasLiquidas>) crudServiceBean.getEntityManager()
					.createQuery("SELECT o FROM ParametrosDescargasLiquidas o where o.estado = true")
					.getResultList();
			return lista;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<ParametrosTablas> findParametrosByTabla(CaracteristicasPuntoMonitoreoTabla tabla){
		List<ParametrosTablas> lista = new ArrayList<ParametrosTablas>();
		try {
			lista = (ArrayList<ParametrosTablas>) crudServiceBean.getEntityManager()
					.createQuery("SELECT o FROM ParametrosTablas o where o.estado = true and o.tabla.id = :idTabla")
					.setParameter("idTabla",tabla.getId())
					.getResultList();
			
			Query query =  crudServiceBean.getEntityManager().createQuery(
					"SELECT distinct o.parametro FROM ParametrosTablas o where o.estado = true and o.tabla.id = :idTabla");
			query.setParameter("idTabla",tabla.getId());
							
			List<ParametrosDescargasLiquidas> listaTablas = (List<ParametrosDescargasLiquidas>) query.getResultList();
			if(listaTablas != null && listaTablas.size() > 0 && listaTablas.size() < lista.size()) {
				for (ParametrosTablas parametro : lista) {
					Integer totalParam = 0;
					for (ParametrosTablas item : lista) {
						if(parametro.getParametro().getId().equals(item.getParametro().getId())) {
							totalParam++;
						}
					}
					
					String nuevoNombre = parametro.getParametro().getNombre();
					if(totalParam > 1) {
						nuevoNombre += " (" + parametro.getDestinoDescarga() + ")";
					}
					
					parametro.setNombreParametro(nuevoNombre);
				}
			}
			
			return lista;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return lista;
	}

}
