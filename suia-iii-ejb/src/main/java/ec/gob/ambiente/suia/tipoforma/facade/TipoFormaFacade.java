package ec.gob.ambiente.suia.tipoforma.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.TipoForma;
import ec.gob.ambiente.suia.tipoforma.service.TipoFormaService;

@Stateless
public class TipoFormaFacade {
	@EJB
	private TipoFormaService tipoFormaService;
	
	@EJB
	private CrudServiceBean crudServiceBean;

	public List<TipoForma> listarTiposForma() {
		return tipoFormaService.listasTipoFormas();
	}
	
	@SuppressWarnings("unchecked")
	public TipoForma buscarPorNombre(String nombre){
		
		TipoForma forma = new TipoForma();
		try {
						
			Query sql = crudServiceBean
					.getEntityManager()
					.createQuery("Select t from TipoForma t where t.estado=true and UPPER(t.nombre) = :nombre");	
			
			sql.setParameter("nombre", nombre.toUpperCase());
			
			List<TipoForma> lista = (List<TipoForma>) sql.getResultList();

			if (lista != null && !lista.isEmpty()){
				forma = lista.get(0);
			}
						
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return forma;
	}
	
	@SuppressWarnings("unchecked")
	public TipoForma buscarPorId(Integer id){
		
		TipoForma forma = new TipoForma();
		try {
						
			Query sql = crudServiceBean
					.getEntityManager()
					.createQuery("Select t from TipoForma t where t.estado=true and t.id = :id");	
			
			sql.setParameter("id", id);
			
			List<TipoForma> lista = (List<TipoForma>) sql.getResultList();

			if (lista != null && !lista.isEmpty()){
				forma = lista.get(0);
			}
						
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return forma;
	}
		
}
