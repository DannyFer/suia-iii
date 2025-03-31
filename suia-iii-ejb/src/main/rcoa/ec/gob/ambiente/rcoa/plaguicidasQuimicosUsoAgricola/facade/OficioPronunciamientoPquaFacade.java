package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.OficioPronunciamientoPqua;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class OficioPronunciamientoPquaFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	@EJB
	private SecuenciasFacade secuenciasFacade;
	
	private String generarCodigo(Area area) {
		try {			
			
			String nombreSecuencia = "OFICIO_PRONUNCIAMIENTO_PQUA_"
					+ secuenciasFacade.getCurrentYear() + "_"
					+ area.getAreaAbbreviation();
			
			String valorSecuencial = secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 3);
			
			String secuencia = "";
			secuencia = Constantes.SIGLAS_INSTITUCION + "-" + area.getArea().getAreaAbbreviation() + "-SGA-" + secuenciasFacade.getCurrentYear();
				
			return secuencia + "-" + valorSecuencial + "-O";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public OficioPronunciamientoPqua guardar(OficioPronunciamientoPqua oficio, Area area){
		if(oficio.getNumeroOficio()==null)
			oficio.setNumeroOficio(generarCodigo(area));		
		return crudServiceBean.saveOrUpdate(oficio);
	}
	
	public OficioPronunciamientoPqua guardar(OficioPronunciamientoPqua oficio) {
		return crudServiceBean.saveOrUpdate(oficio);
	}

	@SuppressWarnings("unchecked")
	public OficioPronunciamientoPqua getPorProyectoRevision(Integer idProyecto, Integer nroRevision)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idProyecto", idProyecto);
		parameters.put("numeroRevision", nroRevision);
		
		List<OficioPronunciamientoPqua> lista = (List<OficioPronunciamientoPqua>) crudServiceBean
					.findByNamedQuery(OficioPronunciamientoPqua.GET_POR_PROYECTO_REVISION, parameters);
		if(lista != null && lista.size() > 0 ){
			return lista.get(0);
		}
		
		return  null;
	}
	
	@SuppressWarnings("unchecked")
	public List<OficioPronunciamientoPqua> getPorProyecto(Integer idProyecto)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idProyecto", idProyecto);
		
		List<OficioPronunciamientoPqua> lista = (List<OficioPronunciamientoPqua>) crudServiceBean
					.findByNamedQuery(OficioPronunciamientoPqua.GET_POR_PROYECTO_ULTIMO_FIRMADO, parameters);
		if(lista != null && lista.size() > 0 ){
			return lista;
		}
		
		return  null;
	}
	
	@SuppressWarnings("unchecked")
	public OficioPronunciamientoPqua getPorIdOficio(Integer idOficio)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idOficio", idOficio);
		
		List<OficioPronunciamientoPqua> lista = (List<OficioPronunciamientoPqua>) crudServiceBean
					.findByNamedQuery(OficioPronunciamientoPqua.GET_POR_ID, parameters);
		if(lista != null && lista.size() > 0 ){
			return lista.get(0);
		}
		
		return  null;
	}
}