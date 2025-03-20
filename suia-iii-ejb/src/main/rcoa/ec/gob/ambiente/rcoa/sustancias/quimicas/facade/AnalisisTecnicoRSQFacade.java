package ec.gob.ambiente.rcoa.sustancias.quimicas.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import ec.gob.ambiente.rcoa.enums.CatalogoTipoCoaEnum;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.ActividadSustancia;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.AnalisisTecnicoRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.InformeOficioRSQ;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class AnalisisTecnicoRSQFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private DocumentosFacade documentosFacade;
	
	private static final Logger LOG = Logger.getLogger(AnalisisTecnicoRSQFacade.class);	
			
	public void guardar(AnalisisTecnicoRSQ obj) {
		crudServiceBean.saveOrUpdate(obj);
	}
	
	@SuppressWarnings("unchecked")
	public List<AnalisisTecnicoRSQ> obtenerPorInformeOficio(InformeOficioRSQ informeOficio,CatalogoTipoCoaEnum tipo) {
		List<AnalisisTecnicoRSQ> lista=new ArrayList<AnalisisTecnicoRSQ>();
		
		try{
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM AnalisisTecnicoRSQ o WHERE o.estado=true and o.informeOficioRSQ.id=:idInfOfi and o.tipo.id=:idTipo ORDER BY 1");
			query.setParameter("idInfOfi", informeOficio.getId());
			query.setParameter("idTipo", tipo.getId());
			lista=(List<AnalisisTecnicoRSQ>)query.getResultList();
		}catch (NoResultException e) {
			// TODO: handle exception
		}catch (Exception e) {
			LOG.error(e.getMessage());
		}		
		return lista;
	}
	
}