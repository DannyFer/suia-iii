/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ProyectoCamaronera;

/**
 *
 * @author Santiago
 */
@Stateless
public class ProyectosCamaronerasFacade {

    
    @EJB
    private CrudServiceBean crudServiceBean;

    @SuppressWarnings("unchecked")
	public List<ProyectoCamaronera> buscarProyectoPorId(Integer id){
    	try {
            Query sql = crudServiceBean.getEntityManager()
            		.createQuery("select pc from ProyectoCamaronera pc where pc.proyectoLicenciamientoAmbiental.id=:pren_id");
            sql.setParameter("pren_id", id);
            if (sql.getResultList().size() > 0) {
                return sql.getResultList();
            } else {
                return null;
               // throw new ServiceException("Error al recuperar la organización a la que pertenece la persona.");
            }
        } catch (Exception e) {
            
        }
		return null;
    }
}
