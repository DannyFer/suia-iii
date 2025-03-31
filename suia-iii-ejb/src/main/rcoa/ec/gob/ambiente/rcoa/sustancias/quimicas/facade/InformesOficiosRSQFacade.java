package ec.gob.ambiente.rcoa.sustancias.quimicas.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import ec.gob.ambiente.rcoa.enums.TipoInformeOficioEnum;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.InformeOficioRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.RegistroSustanciaQuimica;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class InformesOficiosRSQFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
    private SecuenciasFacade secuenciasFacade;
	
	private static final Logger LOG = Logger.getLogger(InformesOficiosRSQFacade.class);	
			
	public void guardarInforme(InformeOficioRSQ obj) {
		if(obj.getCodigo()==null) {
			obj.setCodigo(generarCodigo(obj.getArea(),obj.getTipo()));
		}
		crudServiceBean.saveOrUpdate(obj);
	}
	
	public InformeOficioRSQ obtenerPorRSQArea(RegistroSustanciaQuimica registroSustanciaQuimica,TipoInformeOficioEnum tipo,int numero,Area area) {				
		try{
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM InformeOficioRSQ o WHERE o.estado=true and o.registroSustanciaQuimica.id=:idRSQ and o.tipo.id=:idTipo and o.area.id=:idArea and o.numero=:numero ORDER BY 1 DESC");
			query.setParameter("idRSQ", registroSustanciaQuimica.getId());
			query.setParameter("idTipo", tipo.getId());
			query.setParameter("idArea", area.getId());
			query.setParameter("numero", numero);
			query.setMaxResults(1);			
			return (InformeOficioRSQ)query.getSingleResult();
		}catch (NoResultException e) {
			// TODO: handle exception
		}catch (Exception e) {
			LOG.error(e.getMessage());
		}		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<InformeOficioRSQ> obtenerPorRSQLista(RegistroSustanciaQuimica registroSustanciaQuimica,TipoInformeOficioEnum tipo,int numero) {				
		List<InformeOficioRSQ> lista=new ArrayList<>();
		try{
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM InformeOficioRSQ o WHERE o.estado=true and o.registroSustanciaQuimica.id=:idRSQ and o.tipo.id=:idTipo and o.numero=:numero ORDER BY 1");
			query.setParameter("idRSQ", registroSustanciaQuimica.getId());
			query.setParameter("idTipo", tipo.getId());			
			query.setParameter("numero", numero);
				
			lista =(List<InformeOficioRSQ>)query.getResultList();
		}catch (NoResultException e) {
			// TODO: handle exception
		}catch (Exception e) {
			LOG.error(e.getMessage());
		}		
		return lista;
	}
	
	public InformeOficioRSQ obtenerPorRSQ(RegistroSustanciaQuimica registroSustanciaQuimica,TipoInformeOficioEnum tipo,int numero) {				
				try{
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM InformeOficioRSQ o WHERE o.estado=true and o.registroSustanciaQuimica.id=:idRSQ and o.tipo.id=:idTipo and o.numero=:numero ORDER BY 1 DESC");
			query.setParameter("idRSQ", registroSustanciaQuimica.getId());
			query.setParameter("idTipo", tipo.getId());			
			query.setParameter("numero", numero);
			query.setMaxResults(1);
			return (InformeOficioRSQ)query.getSingleResult();
		}catch (NoResultException e) {
			// TODO: handle exception
		}catch (Exception e) {
			LOG.error(e.getMessage());
		}		
		return null;
	}
    
	public PlantillaReporte obtenerPlantillaReporte(Integer tipoDocumentoId) {
		try {
			Map<String, Object> parametros = new HashMap();
			parametros.put("p_tipoDocumentoId", tipoDocumentoId);

			List<PlantillaReporte> lista = this.crudServiceBean.findByNamedQueryGeneric(PlantillaReporte.OBTENER_PLANTILLA_POR_INFORME, parametros);
			if ((lista != null) && (!lista.isEmpty())) {
				return (PlantillaReporte) lista.get(0);
			}
		} catch (RuntimeException e) {
			throw new RuntimeException(e);
		}
		return null;
	}
	
	private String generarCodigo(Area area,CatalogoGeneralCoa tipo) {
		try {
			String anio=secuenciasFacade.getCurrentYear();
			String codigo="";
			String tipoInformeOficio="";
			
			String siglasArea=area.getTipoArea().getId().intValue()==1?"DMPNP":area.getAreaAbbreviation();
			String siglasAreaInforme=area.getTipoArea().getId().intValue()==1?"UPDPNP-DMPNP":"UCA"+area.getAreaAbbreviation();			
			String siglasAreaOficio=area.getTipoArea().getId().intValue()==1?"DMPNP-SCA":area.getArea().getAreaAbbreviation();
			
			if(tipo.getId().intValue()==TipoInformeOficioEnum.INFORME_INSPECCION.getId()) {
				tipoInformeOficio="INFORME_TECNICO";
				codigo=Constantes.SIGLAS_INSTITUCION + "-"+anio+"-RSQ-"+siglasAreaInforme;
			}else if(tipo.getId().intValue()==TipoInformeOficioEnum.INFORME_TECNICO.getId()) {
				tipoInformeOficio="INFORME_TECNICO";
				codigo=Constantes.SIGLAS_INSTITUCION + "-"+anio+"-RSQ-"+siglasAreaInforme;
			}else if(tipo.getId().intValue()==TipoInformeOficioEnum.OFICIO_PRONUNCIAMIENTO.getId()) {
				tipoInformeOficio="OFICIO_PRONUNCIAMIENTO";
				codigo=Constantes.SIGLAS_INSTITUCION + "-"+anio+"-RSQ-"+siglasAreaOficio;
			}else {
				return null;
			}
			String nombreSecuencia="RSQ_"+tipoInformeOficio+"_"+siglasArea+"_"+anio;
	
			return codigo				
					+ "-"
					+ secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia,4);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<InformeOficioRSQ> obtenerPorRSQListaPorTipo(RegistroSustanciaQuimica registroSustanciaQuimica,TipoInformeOficioEnum tipo) {				
		List<InformeOficioRSQ> lista=new ArrayList<>();
		try{
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM InformeOficioRSQ o WHERE o.estado=true and o.registroSustanciaQuimica.id=:idRSQ and o.tipo.id=:idTipo ORDER BY 1 desc");
			query.setParameter("idRSQ", registroSustanciaQuimica.getId());
			query.setParameter("idTipo", tipo.getId());
				
			lista =(List<InformeOficioRSQ>)query.getResultList();
		}catch (NoResultException e) {
			// TODO: handle exception
		}catch (Exception e) {
			LOG.error(e.getMessage());
		}		
		return lista;
	}
	
	public void guardarInformeRSQ(InformeOficioRSQ obj) {		
		crudServiceBean.saveOrUpdate(obj);
	}
}