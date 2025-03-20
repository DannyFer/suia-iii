package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformacionProyectoEia;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.OficioPronunciamientoEsIA;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class OficioPronunciamientoEsIAFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	@EJB
	private SecuenciasFacade secuenciasFacade;
	
	private String generarCodigoOficioConsolidado(Area area) {
		try {			
			String nombreSecuencia = "ESIA_OFICIO_RESPUESTA_"
					+ secuenciasFacade.getCurrentYear() + "_"
					+ area.getAreaAbbreviation();
			
			String valorSecuencial = secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 4);
			
			String secuencia = Constantes.SIGLAS_INSTITUCION + "-" + area.getAreaAbbreviation() + "-" + secuenciasFacade.getCurrentYear();
			if(area.getTipoArea().getId()==3){
				secuencia = area.getAreaAbbreviation() + "-" + secuenciasFacade.getCurrentYear();
			}
			return secuencia + "-" + valorSecuencial + "-O";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	

	public OficioPronunciamientoEsIA guardarConsolidado(OficioPronunciamientoEsIA oficio, Area area, Integer tipoOficio){	
		if(oficio.getCodigoOficio()==null && area != null) {
			String codigo = tipoOficio.equals(1) ? generarCodigoMemorando(area) : generarCodigoOficioConsolidado(area);
			oficio.setCodigoOficio(codigo);
		}
		
		return crudServiceBean.saveOrUpdate(oficio);
	}
	
	private String generarCodigoOficio(Area area) {
		try {			
			String nombreSecuencia = "ESIA_OFICIO_RESPUESTA_"
					+ secuenciasFacade.getCurrentYear() + "_"
					+ area.getAreaAbbreviation();
			
			String valorSecuencial = secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 4);
			
			String secuencia = Constantes.SIGLAS_INSTITUCION + "-" + area.getAreaAbbreviation() + "-" + secuenciasFacade.getCurrentYear();

			return secuencia + "-" + valorSecuencial + "-O";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private String generarCodigoMemorando(Area area) {
		try {			
			String nombreSecuencia = "ESIA_MEMORANDO_"
					+ secuenciasFacade.getCurrentYear() + "_"
					+ area.getAreaAbbreviation();
			
			String valorSecuencial = secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 4);			
			
			String secuencia = Constantes.SIGLAS_INSTITUCION + "-" + area.getAreaAbbreviation() + "-" + secuenciasFacade.getCurrentYear();
			return secuencia  + "-" + valorSecuencial + "-M";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private String generarCodigoOficioArchivo(Area area) {
		try {			
			String nombreSecuencia = "ESIA_OFICIO_ARCHIVO_"
					+ secuenciasFacade.getCurrentYear() + "_"
					+ area.getAreaAbbreviation();
			
			String valorSecuencial = secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 4);
			
			String secuencia = Constantes.SIGLAS_INSTITUCION + "-" + area.getAreaAbbreviation() + "-" + secuenciasFacade.getCurrentYear();
			
			return secuencia + "-" + valorSecuencial;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public OficioPronunciamientoEsIA guardar(OficioPronunciamientoEsIA oficio, Area area, Integer tipoOficio){	
		if(oficio.getCodigoOficio()==null && area != null) {
			String codigo = tipoOficio.equals(1) ? generarCodigoMemorando(area) : generarCodigoOficio(area);
			oficio.setCodigoOficio(codigo);
		}
		
		return crudServiceBean.saveOrUpdate(oficio);
	}
	
	public OficioPronunciamientoEsIA guardar(OficioPronunciamientoEsIA OficioPronunciamientoEsIA) {        
    	return crudServiceBean.saveOrUpdate(OficioPronunciamientoEsIA);        
	}
	
	public OficioPronunciamientoEsIA guardarOficioArchivacion(OficioPronunciamientoEsIA oficio, Area area){
		
		if(oficio.getCodigoOficio()==null && area != null) {
			String codigo = generarCodigoOficioArchivo(area);
			oficio.setCodigoOficio(codigo);
		}
		
		return crudServiceBean.saveOrUpdate(oficio);
	}
	
	@SuppressWarnings("unchecked")
	public OficioPronunciamientoEsIA obtenerPronunciamientoEsIAPorEstudio(InformacionProyectoEia estudio)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idEstudio", estudio.getId());
		
		List<OficioPronunciamientoEsIA> lista = (List<OficioPronunciamientoEsIA>) crudServiceBean
					.findByNamedQuery(
							OficioPronunciamientoEsIA.GET_POR_ESTUDIO,
							parameters);
		if(lista.size() > 0 ){
			return lista.get(0);
		}
		return  null;
	}
	
	public OficioPronunciamientoEsIA obtenerOficioPronunciamiento(InformacionProyectoEia estudio,int estudioPronunciamiento,Integer numeroRevision)
	{
		try {
			Query query = crudServiceBean.getEntityManager().createQuery("SELECT o FROM OficioPronunciamientoEsIA o WHERE o.estado = true and o.informacionProyecto.id = :idEstudio and o.estudioPronunciamiento=:estudioPronunciamiento and o.numeroRevision = :numeroRevision order by id desc");
			query.setParameter("idEstudio", estudio.getId());
			query.setParameter("estudioPronunciamiento", estudioPronunciamiento);
			query.setParameter("numeroRevision", numeroRevision);
			query.setMaxResults(1);
			return (OficioPronunciamientoEsIA)query.getSingleResult();
		} catch (NoResultException nre) {
			//TODO
		} catch (Exception e) {
			e.printStackTrace();
			
		}		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public OficioPronunciamientoEsIA getPorEstudioInforme(Integer idEstudio, Integer idInforme)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idEstudio", idEstudio);
		parameters.put("idInforme", idInforme);
		List<OficioPronunciamientoEsIA> lista = (List<OficioPronunciamientoEsIA>) crudServiceBean.findByNamedQuery(OficioPronunciamientoEsIA.GET_POR_ESTUDIO_INFORME,parameters);
		if(lista.size() > 0 ){
			return lista.get(0);
		}
		return  null;
	}
	
	@SuppressWarnings("unchecked")
	public OficioPronunciamientoEsIA getPorEstudioTipoOficio(Integer idEstudio, Integer tipo)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idEstudio", idEstudio);
		parameters.put("tipoOficio", tipo);
		List<OficioPronunciamientoEsIA> lista = (List<OficioPronunciamientoEsIA>) crudServiceBean
					.findByNamedQuery(
							OficioPronunciamientoEsIA.GET_POR_ESTUDIO_TIPO_OFICIO,
							parameters);
		if(lista.size() > 0 ){
			return lista.get(0);
		}
		return  null;
	}
	
	public OficioPronunciamientoEsIA guardar(OficioPronunciamientoEsIA oficio, Area area){	
		if(oficio.getCodigoOficio()==null && area != null) {
			String codigo = generarCodigoOficioAprobacionFinal(area);
			oficio.setCodigoOficio(codigo);
		}
		
		return crudServiceBean.saveOrUpdate(oficio);
	}
	
	private String generarCodigoOficioAprobacionFinal(Area area) {
		try {			
			String nombreSecuencia = "ESIA_OFICIO_RESPUESTA_"
					+ secuenciasFacade.getCurrentYear() + "_"
					+ area.getAreaAbbreviation();
			
			String valorSecuencial = secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 4);
			
			String secuencia = Constantes.SIGLAS_INSTITUCION + "-SUIA-" + area.getAreaAbbreviation() + "-" + secuenciasFacade.getCurrentYear();
			if (area.getTipoArea().getId() == 3) {
				secuencia = area.getAreaAbbreviation() + "-" + secuenciasFacade.getCurrentYear();
			}
			return secuencia + "-" + valorSecuencial;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	///////////
	
	@SuppressWarnings("unchecked")
	public OficioPronunciamientoEsIA getPorEstudioInforme(InformacionProyectoEia estudio, Integer idInforme)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idEstudio", estudio.getId());
		parameters.put("idInforme", idInforme);
		
		List<OficioPronunciamientoEsIA> lista = (List<OficioPronunciamientoEsIA>) crudServiceBean.findByNamedQuery(OficioPronunciamientoEsIA.GET_POR_ESTUDIO_INFORME,parameters);
		if(lista.size() > 0 ){
			return lista.get(0);
		}
		return  null;
	}
	
	
	
	@SuppressWarnings("unchecked")
	public OficioPronunciamientoEsIA getPorIdEstudio(InformacionProyectoEia estudio)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idEstudio", estudio.getId());
		//parameters.put("idInforme", idInforme);
		
		List<OficioPronunciamientoEsIA> lista = (List<OficioPronunciamientoEsIA>) crudServiceBean.findByNamedQuery(OficioPronunciamientoEsIA.GET_POR_ESTUDIO,parameters);
		if(lista.size() > 0 ){
			return lista.get(0);
		}
		return  null;
	}
	
	@SuppressWarnings("unchecked")
	public List<OficioPronunciamientoEsIA> getListaPorEstudioTipoOficio(Integer idEstudio, Integer tipo)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idEstudio", idEstudio);
		parameters.put("tipoOficio", tipo);
		List<OficioPronunciamientoEsIA> lista = (List<OficioPronunciamientoEsIA>) crudServiceBean
					.findByNamedQuery(
							OficioPronunciamientoEsIA.GET_POR_ESTUDIO_TIPO_OFICIO_ASC,
							parameters);
		
		return lista;		
	}
	
	
	///////////

}