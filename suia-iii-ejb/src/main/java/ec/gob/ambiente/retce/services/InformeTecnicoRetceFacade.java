package ec.gob.ambiente.retce.services;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.InformeTecnicoRetce;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class InformeTecnicoRetceFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private SecuenciasFacade secuenciasFacade;
	
	public PlantillaReporte getPlantillaReporte(TipoDocumentoSistema tipoDocumento) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("p_tipoDocumentoId", tipoDocumento.getIdTipoDocumento());
		return crudServiceBean.findByNamedQuerySingleResult(PlantillaReporte.OBTENER_PLANTILLA_POR_INFORME, parameters);
	}
	
	@Deprecated
	public String generarCodigoInforme(TipoDocumentoSistema tipoDocumento) {
		String codigoNomenclatura=null;
		String nombreSecuencia=null;
		if(tipoDocumento==TipoDocumentoSistema.INFORME_TECNICO_GENERADOR){
			codigoNomenclatura=Constantes.SIGLAS_INSTITUCION + "-RETCE-GEN";
			nombreSecuencia="RETCE_INFORME_TECNICO_GENERADOR";
		}else if (tipoDocumento==TipoDocumentoSistema.INFORME_TECNICO_DESCARGA_LIQUIDA) {
			codigoNomenclatura=Constantes.SIGLAS_INSTITUCION + "-RETCE-DL";
			nombreSecuencia="RETCE_INFORME_TECNICO_DESCARGA_LIQUIDA";
		}else if(tipoDocumento==TipoDocumentoSistema.INFORME_TECNICO_EMISIONES_ATMOSFERICAS){
			codigoNomenclatura=Constantes.SIGLAS_INSTITUCION + "-RETCE-EA";
			nombreSecuencia="RETCE_INFORME_TECNICO_EMISION_ATMOSFERICA";
		}else{
			return "";
		}
		
		
		try {
			return codigoNomenclatura+"-"
					+ secuenciasFacade.getCurrentYear()
					+ "-"
					+ secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 6);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public InformeTecnicoRetce getInforme(String codTramite,TipoDocumentoSistema tipoDocumento) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("codTramite", codTramite);
		parameters.put("idTipoDocumento", tipoDocumento.getIdTipoDocumento());

		return crudServiceBean.findByNamedQuerySingleResult(InformeTecnicoRetce.GET_POR_CODIGO_TRAMITE_TIPO, parameters);
	}
	
	public InformeTecnicoRetce getInforme(String codTramite,TipoDocumentoSistema tipoDocumento,Integer numeroRevision) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("codTramite", codTramite);
		parameters.put("idTipoDocumento", tipoDocumento.getIdTipoDocumento());
		parameters.put("numeroRevision", numeroRevision);
		return crudServiceBean.findByNamedQuerySingleResult(InformeTecnicoRetce.GET_POR_CODIGO_TRAMITE_TIPO_REVISION, parameters);
	}
	
	@Deprecated
	public void guardarInforme(InformeTecnicoRetce informe) {
		crudServiceBean.saveOrUpdate(informe);
	}
	
	private String generarCodigo(Area area) {
		String anioActual=secuenciasFacade.getCurrentYear();
		String codigoNomenclatura=Constantes.SIGLAS_INSTITUCION + "-"+area.getAreaAbbreviation()+"-RETCE";
		String nombreSecuencia="RETCE_INFORME_TECNICO_"+anioActual+"_"+area.getAreaAbbreviation();
		
		try {
			return codigoNomenclatura+"-"
					+ secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 4)					
					+ "-"
					+ anioActual;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public void guardar(InformeTecnicoRetce obj,Area area, Usuario usuario) {
		if(obj.getId()==null){
			obj.setUsuarioCreacion(usuario.getNombre());
			obj.setFechaCreacion(new Date());
			obj.setNumeroInforme(generarCodigo(area));						
		}
		else{
			obj.setUsuarioModificacion(usuario.getNombre());
			obj.setFechaModificacion(new Date());			
		}
		obj.setFechaInforme(new Date());
		crudServiceBean.saveOrUpdate(obj);
	}
}
