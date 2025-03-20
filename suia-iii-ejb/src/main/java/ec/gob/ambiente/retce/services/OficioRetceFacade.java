package ec.gob.ambiente.retce.services;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.OficioPronunciamientoRetce;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class OficioRetceFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private SecuenciasFacade secuenciasFacade;
	
	public PlantillaReporte getPlantillaReporte(TipoDocumentoSistema tipoDocumento) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("p_tipoDocumentoId", tipoDocumento.getIdTipoDocumento());
		return crudServiceBean.findByNamedQuerySingleResult(PlantillaReporte.OBTENER_PLANTILLA_POR_INFORME, parameters);
	}
	
	@SuppressWarnings("unchecked")
	public OficioPronunciamientoRetce getOficio(String codTramite,TipoDocumentoSistema tipoDocumento) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("codTramite", codTramite);
		parameters.put("idTipoDocumento", tipoDocumento.getIdTipoDocumento());
		List<OficioPronunciamientoRetce> lista = (List<OficioPronunciamientoRetce>) crudServiceBean.findByNamedQuery(OficioPronunciamientoRetce.GET_POR_CODIGO_TRAMITE_TIPO, parameters);
		if (lista == null || lista.isEmpty()) {
			return null;
		} else {
			return lista.get(0);
		}
	}
	
	public OficioPronunciamientoRetce getOficio(String codTramite,TipoDocumentoSistema tipoDocumento,Integer numeroRevision) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("codTramite", codTramite);
		parameters.put("idTipoDocumento", tipoDocumento.getIdTipoDocumento());
		parameters.put("numeroRevision", numeroRevision);
		return crudServiceBean.findByNamedQuerySingleResult(OficioPronunciamientoRetce.GET_POR_CODIGO_TRAMITE_TIPO_REVISION, parameters);
		
	}
	
	private String generarCodigo(Area area) {
		String anioActual=secuenciasFacade.getCurrentYear();
		String areaAbbr=area.getTipoArea().getId()==1?"PLANTA_CENTRAL":area.getAreaAbbreviation();
		String codigoNomenclatura=Constantes.SIGLAS_INSTITUCION+"-"+area.getAreaAbbreviation()+"-RETCE";
		String nombreSecuencia="RETCE_OFICIO_PRONUNCIAMIENTO_"+anioActual+"_"+areaAbbr;
		
		try {
			return codigoNomenclatura+"-"
					+ anioActual				
					+ "-"
					+ secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 4)
					+ "-O";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public void guardar(OficioPronunciamientoRetce obj, Area area, Usuario usuario){		
		if(obj.getId()==null){
			obj.setUsuarioCreacion(usuario.getNombre());
			obj.setFechaCreacion(new Date());			
		}
		else{
			obj.setUsuarioModificacion(usuario.getNombre());
			obj.setFechaModificacion(new Date());			
		}		
		obj.setFechaOficio(new Date());
		
		if(area!=null){
			if(obj.getNumeroOficio()==null || obj.getNumeroOficio().isEmpty()){
				obj.setNumeroOficio(generarCodigo(area));
			}else if(area.getTipoArea().getId()==1){
				String areaAbbr=obj.getNumeroOficio().substring(4,obj.getNumeroOficio().indexOf("-RETCE"));				
				if(area.getAreaAbbreviation().compareTo(areaAbbr)!=0){
					obj.setNumeroOficio(obj.getNumeroOficio().replace(areaAbbr, area.getAreaAbbreviation()));
				}
			}
		}			
		
		crudServiceBean.saveOrUpdate(obj);
	}
	
	public void actualizar(OficioPronunciamientoRetce obj){		
		crudServiceBean.saveOrUpdate(obj);
	}
	
	@SuppressWarnings("unchecked")
	public List<OficioPronunciamientoRetce> getOficiosObservacionSubsanados(String codTramite) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("codTramite", codTramite);

		List<OficioPronunciamientoRetce> lista = (List<OficioPronunciamientoRetce>) crudServiceBean.findByNamedQuery(OficioPronunciamientoRetce.GET_SUBSANADOS_POR_CODIGO_TRAMITE, parameters);
		if (lista == null || lista.isEmpty()) {
			return null;
		} else {
			return lista;
		}
	}
	
	public OficioPronunciamientoRetce getOficioPorInforme(Integer idInforme) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idInforme", idInforme);
		return crudServiceBean.findByNamedQuerySingleResult(OficioPronunciamientoRetce.GET_POR_ID_INFORME, parameters);

	}
}
