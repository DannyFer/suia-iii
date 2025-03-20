package ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.model.OficioResolucionAmbiental;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

/**
 * <b> FACADE </b>
 *
 * @author Luis Lema
 * @version Revision: 1.0
 */

@Stateless
public class OficioResolucionAmbientalFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private InformeOficioResolucionAmbientalFacade informeOficioResolucionAmbientalFacade;
	
	@SuppressWarnings("unchecked")
	public List<OficioResolucionAmbiental> getByIdResolucionAmbiental(Integer idResolucionAmbiental) {
		List<OficioResolucionAmbiental> result = new ArrayList<OficioResolucionAmbiental>();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT iva FROM OficioResolucionAmbiental iva WHERE iva.resolucionAmbiental.id=:idResolucionAmbiental");
		sql.setParameter("idResolucionAmbiental", idResolucionAmbiental);
		if (sql.getResultList().size() > 0)
			result = (List<OficioResolucionAmbiental>) sql.getResultList();
		return result;
	}
	
	public OficioResolucionAmbiental getById(Integer idOficioResolucionAmbiental) {
		OficioResolucionAmbiental result = new OficioResolucionAmbiental();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT iva FROM OficioResolucionAmbiental iva WHERE iva.id=:idOficioResolucionAmbiental");
		sql.setParameter("idOficioResolucionAmbiental", idOficioResolucionAmbiental);
		if (sql.getResultList().size() > 0)
			result = (OficioResolucionAmbiental) sql.getResultList().get(0);
		return result;
	}
	
	public OficioResolucionAmbiental guardar(OficioResolucionAmbiental oficioResolucionAmbiental) throws Exception{
		if(oficioResolucionAmbiental.getCodigoReporte() == null) {
			Integer tipoDocumento = oficioResolucionAmbiental.getTipoUsuarioAlmacena().getId();
			String codigoReporte = "";
			switch (tipoDocumento) {
			case 64: //CatalogoTipoCoaEnum.RCOA_LA_RESOLUCION
				codigoReporte = informeOficioResolucionAmbientalFacade.generarCodigoResolucionAmbiental(oficioResolucionAmbiental.getAreaFirma());
				break;
			default:
				codigoReporte = informeOficioResolucionAmbientalFacade.generarCodigoMemorandoAmbiental(oficioResolucionAmbiental.getAreaFirma());
				break;
			}
			
			oficioResolucionAmbiental.setCodigoReporte(codigoReporte);
		}
		return crudServiceBean.saveOrUpdate(oficioResolucionAmbiental);
	}
	
	@SuppressWarnings("unchecked")
	public List<OficioResolucionAmbiental> getByIdResolucionAmbientalAndCategoria(Integer idResolucionAmbiental, Integer idCategoria) {
		List<OficioResolucionAmbiental> result = new ArrayList<OficioResolucionAmbiental>();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT iva FROM OficioResolucionAmbiental iva "
				+ "WHERE iva.resolucionAmbiental.id = :idResolucionAmbiental and iva.tipoUsuarioAlmacena.id = :idCategoria order by fechaCreacion desc");
		sql.setParameter("idResolucionAmbiental", idResolucionAmbiental);
		sql.setParameter("idCategoria", idCategoria);
		result = (List<OficioResolucionAmbiental>) sql.getResultList();			
		return result;
	}
	
	public OficioResolucionAmbiental getByIdResolucionAmbientalByIdTipoDocumento(Integer idResolucionAmbiental, Integer idCategoria) {
		OficioResolucionAmbiental result = new OficioResolucionAmbiental();
		Query sql = crudServiceBean.getEntityManager().createQuery("SELECT iva FROM OficioResolucionAmbiental iva "
				+ "WHERE iva.resolucionAmbiental.id = :idResolucionAmbiental and iva.tipoUsuarioAlmacena.id = :idCategoria order by fechaCreacion desc");
		sql.setParameter("idResolucionAmbiental", idResolucionAmbiental);
		sql.setParameter("idCategoria", idCategoria);
		if (sql.getResultList().size() > 0){
			result =(OficioResolucionAmbiental) sql.getResultList().get(0);
		}			
		return result;	
	}

}
