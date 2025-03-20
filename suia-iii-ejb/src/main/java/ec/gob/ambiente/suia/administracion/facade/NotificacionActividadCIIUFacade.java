/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.NotificacionActividadCIIU;

/**
 * 
 * @author ishmael
 */
@Stateless
public class NotificacionActividadCIIUFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	public NotificacionActividadCIIU getById(Integer id) {
		NotificacionActividadCIIU result = new NotificacionActividadCIIU();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT nac FROM NotificacionActividadCIIU nac WHERE nac.id=:id");
		sql.setParameter("id", id);
		if (!sql.getResultList().isEmpty())
			result = (NotificacionActividadCIIU) sql.getResultList().get(0);
		return result;
	}
	
	public NotificacionActividadCIIU guardar(NotificacionActividadCIIU notificacionActividadCIIU){
		return crudServiceBean.saveOrUpdate(notificacionActividadCIIU);
	}
}
