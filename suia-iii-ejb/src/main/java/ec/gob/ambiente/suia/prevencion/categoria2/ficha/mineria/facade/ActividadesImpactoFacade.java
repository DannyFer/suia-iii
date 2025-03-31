package ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ActividadesImpactoProyecto;
import ec.gob.ambiente.suia.domain.Actividades;
import ec.gob.ambiente.suia.dto.EntityActividad;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.usuario.service.UsuarioServiceBean;

/**
 * *
 * <p/>
 * <b> Facade para la entidad CargaTrabajo</b>
 * 
 * @author bburbano
 * @version Revision: 1.0
 * fecha 2019-04-01
 */
@LocalBean
@Stateless
public class ActividadesImpactoFacade {

	@EJB
	private UsuarioServiceBean usuarioServiceBean;

	@EJB
	private CrudServiceBean crudServiceBean;


	public void guardar(ActividadesImpactoProyecto actividadesImpactoProyecto) throws ServiceException {
		crudServiceBean.saveOrUpdate(actividadesImpactoProyecto);
	}

	public void guardarList(List<ActividadesImpactoProyecto> actividadesImpactoProyecto) throws ServiceException {
		crudServiceBean.saveOrUpdate(actividadesImpactoProyecto);
	}

	@SuppressWarnings("unchecked")
	public List<EntityActividad> obtenerActividdaesProyecto(Integer proyectoId) throws ServiceException {
		List<EntityActividad> lista = new ArrayList<EntityActividad>(); 
		Integer index =0;
		Integer padre = 0;
		EntityActividad  objecto = new EntityActividad();
		List<ActividadesImpactoProyecto> listaArray = new ArrayList<ActividadesImpactoProyecto>();
        try {
        	String sqlPproyecto="select a.acti_id, a.acti_parent_id, b.prai_id  "+
        						"from suia_iii.activities a left join suia_iii.scdr_projects_activities_impact b on a.acti_id = b.acti_id and b.scdr_id = "+proyectoId+
        						" where a.acti_parent_id is not null and a.acti_status = true "+
        						" order by a.acti_parent_id, a.acti_id "; 
    		Query queryProyecto =  crudServiceBean.getEntityManager().createNativeQuery(sqlPproyecto);
    		List<Object>  resultPro = new ArrayList<Object>();
    		resultPro= queryProyecto.getResultList();
    		if (resultPro!=null) {
    			for(Object a: resultPro)
    			{
    				ActividadesImpactoProyecto objActividadProyecto = new ActividadesImpactoProyecto(); 
    				if(((Object[]) a)[2] != null ){
    					Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
    					parameters.put("proyectoId", proyectoId);
    					parameters.put("codigoId", Integer.valueOf(((Object[]) a)[2].toString()));
    					objActividadProyecto = (ActividadesImpactoProyecto) crudServiceBean
    			                .findByNamedQuery(ActividadesImpactoProyecto.IMPACTO_PORPROYECTOPORID, parameters).get(0);
    				}else{
            			List<Actividades> listaActividad = crudServiceBean
            					.getEntityManager().createQuery("SELECT a FROM Actividades a where a.id = "+((Object[]) a)[0].toString())
            					.getResultList();
            			if (listaActividad != null && !listaActividad.isEmpty()) {
            				objActividadProyecto.setActividad(listaActividad.get(0));
            				objActividadProyecto.setAgua(false);
            				objActividadProyecto.setSuelo(false);
            				objActividadProyecto.setAire(false);
            				objActividadProyecto.setSocial(false);
            				objActividadProyecto.setBiotico(false);
            				objActividadProyecto.setEstado(true);
            				objActividadProyecto.setPerforacionExplorativa(proyectoId);
            			}
    				}
    				if(padre != objActividadProyecto.getActividad().getActividad().getId()){
    					if (padre != 0 ){
    	        			lista.add(index,objecto);
    	    				objecto = new EntityActividad();
    	        			index = index +1;
    					}
    					listaArray = new ArrayList<ActividadesImpactoProyecto>();
    					listaArray.add(objActividadProyecto);
    				}

    				if(padre != 0 && padre == objActividadProyecto.getActividad().getActividad().getId()){
    					listaArray.add(objActividadProyecto);
    				}
					padre = objActividadProyecto.getActividad().getActividad().getId();
    				objecto.setActividad(objActividadProyecto.getActividad().getActividad().getNombre());
    				objecto.setSubactividades(listaArray);
    			}
    			if (padre != 0 ){
        			lista.add(index,objecto);
    				objecto = new EntityActividad();
        			index = index +1;
				}
    		}
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
        return lista;
	}
}