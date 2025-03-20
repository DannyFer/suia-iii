package ec.gob.ambiente.rcoa.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.model.CodigosResoluciones;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.domain.Area;

@Stateless	
public class SecuenciaCertificadoAmbientalRcoaFacade {
	
	@EJB
	CodigosResolucionesFacade codigosResolucionesFacade;
	@EJB
	TipoResolucionFacade tipoResolucionFacade;
	
	
	public String getSecuenciaResolucionAreaResponsable(ProyectoLicenciaCoa proyectoActivo,String anios, Area area) throws Exception {
		
		String  nextCode;
		CodigosResoluciones codigosResoluciones= codigosResolucionesFacade.codigoCertificadoAmbiental(area, anios);
		if (codigosResoluciones.getId()==null){
			CodigosResoluciones nuevo= codigosResoluciones;			
			nuevo.setAnios(Integer.valueOf(anios));
			nuevo.setAreaResponsable(area);
			nuevo.setSecuencialResolucion(1);
			nuevo.setTipoResolucion(tipoResolucionFacade.buscartipo("CA-SUIA"));			
			codigosResolucionesFacade.guardarSecuencial(nuevo);
		}
		CodigosResoluciones aux= codigosResoluciones;
		Integer codigo=codigosResoluciones.getSecuencialResolucion()+1; 
		Integer longitud =String.valueOf(codigo).length();
		String restante="";
		if (longitud==1){
			restante="000";
		}
		if (longitud==2){
			restante="00";
		}
		if (longitud==3){
			restante="0";
		}
		String areaproyecto= area.getAreaAbbreviation();
		String secuencia=codigosResoluciones.getTipoResolucion().getAbreviacion();
		nextCode=restante+codigo+"-"+areaproyecto+"-"+codigosResoluciones.getAnios()+"-"+secuencia;
		aux.setSecuencialResolucion(codigo);
		codigosResolucionesFacade.guardarSecuencial(aux);
		return nextCode;
	}

}
