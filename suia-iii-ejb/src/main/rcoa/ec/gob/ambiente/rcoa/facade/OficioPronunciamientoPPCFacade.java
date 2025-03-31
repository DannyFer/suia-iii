package ec.gob.ambiente.rcoa.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.model.OficioPronunciamientoPPC;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class OficioPronunciamientoPPCFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	@EJB
	private SecuenciasFacade secuenciasFacade;
	
	public OficioPronunciamientoPPC guardiarOficioArchivo(OficioPronunciamientoPPC obj){
		if(obj.getCodigoDocumento() != null){
			obj = crudServiceBean.saveOrUpdate(obj);
		}else{
			obj.setCodigoDocumento(generarCodigoArchivo(obj.getArea()));
			obj = crudServiceBean.saveOrUpdate(obj);
		}
		return obj;
	}
	
	@SuppressWarnings("unchecked")
	public List<OficioPronunciamientoPPC> buscarPorRegistro(Integer id){
		
		List<OficioPronunciamientoPPC> lista = new ArrayList<OficioPronunciamientoPPC>();
		try {
			Query sql = crudServiceBean.getEntityManager().createQuery("SELECT o FROM OficioPronunciamientoPPC o "
					+ "WHERE o.registroAmbiental.id = :id and o.estado = true order by 1 desc");
			sql.setParameter("id", id);
			
			lista = (List<OficioPronunciamientoPPC>)sql.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return lista;
	}
	
	public String generarCodigoArchivo(Area area){
		String codigo = null;
		
		try {
			
			String nombreSecuencia = "RA_PPC_OFICIO_ARCHIVO" + area.getAreaAbbreviation() + "-"+secuenciasFacade.getCurrentYear();
			String abreviaturaArea = area.getAreaAbbreviation();
			
			if(area.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT)){
				abreviaturaArea = area.getArea().getAreaAbbreviation();
			}
						
			codigo = Constantes.SIGLAS_INSTITUCION + "-"+ Constantes.SIGLAS_SUIA + "-" + abreviaturaArea + "-" + secuenciasFacade.getCurrentYear() + "-" 
					+ secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 4)
					+ "-O";
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return codigo;
	}


}
