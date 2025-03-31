package ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model.ResolucionAmbiental;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosRcoa;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.Constantes;

/**
 * <b> FACADE </b>
 *
 * @author Luis Lema
 * @version Revision: 1.0
 */

@Stateless
public class ResolucionAmbientalFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public String dblinkBpmsSuiaiii=Constantes.getDblinkBpmsSuiaiii();
	
	public ResolucionAmbiental getByIdRegistroPreliminar(Integer idRegistroPreliminar) {
		ResolucionAmbiental result = new ResolucionAmbiental();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT iva FROM ResolucionAmbiental iva WHERE iva.proyectoLicenciaCoa.id=:idRegistroPreliminar");
		sql.setParameter("idRegistroPreliminar", idRegistroPreliminar);
		if (sql.getResultList().size() > 0)
			result = (ResolucionAmbiental) sql.getResultList().get(0);
		return result;
	}
	
	public ResolucionAmbiental getByIdRegistroFlujoByPass(Integer idRegistroPreliminar, Integer idflujo) {
		ResolucionAmbiental result = new ResolucionAmbiental();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT iva FROM ResolucionAmbiental iva WHERE iva.proyectoLicenciaCoa.id=:idRegistroPreliminar and iva.flujo=:idflujo");
		sql.setParameter("idRegistroPreliminar", idRegistroPreliminar);
		sql.setParameter("idflujo", idflujo);
		if (sql.getResultList().size() > 0)
			result = (ResolucionAmbiental) sql.getResultList().get(0);
		return result;
	}	
	
	public Boolean validaRGDFinalizado(RegistroGeneradorDesechosRcoa rgd) throws ServiceException {
		Boolean finalized = false;
		try {
			Integer estado = 0;
			String sql="select * "
				+ "from dblink('"+dblinkBpmsSuiaiii+"','select status from processinstancelog p "
				+ "where p.processinstanceid in "
				+ "(select processinstanceid from variableinstancelog where value = ''"+rgd.getCodigo()+"'' order by processinstanceid desc)') as (estado integer)";
			Query q = crudServiceBean.getEntityManager().createNativeQuery(sql);		
			if(q.getResultList().size()>0) 
				estado = ((Integer) q.getResultList().get(0)).intValue();
			
			if (estado == 2) {
				finalized = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return finalized;
	}
	
	public ResolucionAmbiental getByCodigoUnicoAmbiental(String idRegistroPreliminar) {
		ResolucionAmbiental result = new ResolucionAmbiental();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT iva FROM ResolucionAmbiental iva WHERE iva.proyectoLicenciaCoa.codigoUnicoAmbiental=:idRegistroPreliminar and iva.flujo = 1");
		sql.setParameter("idRegistroPreliminar", idRegistroPreliminar);
		if (sql.getResultList().size() > 0)
			result = (ResolucionAmbiental) sql.getResultList().get(0);
		return result;
	}
	
	public ResolucionAmbiental getByCodigoResolucion(String codeResolution) {
		ResolucionAmbiental result = new ResolucionAmbiental();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT iva FROM ResolucionAmbiental iva WHERE iva.numeroResolucion=:idRegistroPreliminar and iva.estado=true");
		sql.setParameter("idRegistroPreliminar", codeResolution);
		if (sql.getResultList().size() > 0)
			result = (ResolucionAmbiental) sql.getResultList().get(0);
		return result;
	}
	
	public ResolucionAmbiental guardar(ResolucionAmbiental resolucionAmbiental){
		return crudServiceBean.saveOrUpdate(resolucionAmbiental);
	}

}
