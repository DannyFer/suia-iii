package ec.gob.ambiente.rcoa.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.TipoResolucion;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class TipoResolucionFacade {
	
	@EJB	
	CrudServiceBean crudServiceBean;

	public TipoResolucion buscartipo(String abreviacion){

		TipoResolucion tipoResolucion = new TipoResolucion();
		Query sql =crudServiceBean.getEntityManager().createQuery("Select s From TipoResolucion s "
				+ " where abreviacion=:abreviacion ");
		sql.setParameter("abreviacion", abreviacion);	
		if(sql.getResultList().size()>0)
			tipoResolucion=(TipoResolucion) sql.getSingleResult();
		
		return tipoResolucion;
	}
	
	public TipoResolucion buscarNombreDescripcion(String nombre, String descripcion){
		TipoResolucion tipoResolucion = new TipoResolucion();
		Query sql =crudServiceBean.getEntityManager().createQuery("Select s From TipoResolucion s "
				+ " where nombre=:nombre and descripcion=:descripcion");
		sql.setParameter("nombre", nombre);	
		sql.setParameter("descripcion", descripcion);
		if(sql.getResultList().size()>0)
			tipoResolucion=(TipoResolucion) sql.getSingleResult();
		return tipoResolucion;
	}
	
	public void guardarActualizar(TipoResolucion tipoResolucion){
		 crudServiceBean.saveOrUpdate(tipoResolucion);
		}
	}
