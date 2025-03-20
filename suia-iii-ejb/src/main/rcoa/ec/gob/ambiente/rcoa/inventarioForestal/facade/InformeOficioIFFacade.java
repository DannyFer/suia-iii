package ec.gob.ambiente.rcoa.inventarioForestal.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.facade.TipoResolucionFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.TipoResolucion;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class InformeOficioIFFacade {
	

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private SecuenciasFacade secuenciasFacade;
	
	@EJB
	private TipoResolucionFacade tipoResolucionFacade;
	
	public static final String NOMBRE_DESCRIPCION_PC = "Planta Central";
	public static final String NOMBRE_DESCRIPCION_DP = "Direccion Provincial";
	
	public String generarCodigoDocumentoInventarioForestal(ProyectoLicenciaCoa proyectoLicenciaCoa, String nombreInforme) {
		try {
			TipoResolucion tipoResolucion = new TipoResolucion();
			if (proyectoLicenciaCoa.getAreaInventarioForestal().getAreaAbbreviation() == "DNPCA") {
				tipoResolucion = tipoResolucionFacade.buscarNombreDescripcion(nombreInforme, NOMBRE_DESCRIPCION_PC);
			} else {
				tipoResolucion = tipoResolucionFacade.buscarNombreDescripcion(nombreInforme, NOMBRE_DESCRIPCION_DP);
			}
			Integer numero = (tipoResolucion.getSequencial() == null) ? 1 : (Integer) tipoResolucion.getSequencial()+1;
			tipoResolucion.setSequencial(numero);
			String secuencia="";
			for (int i=numero.toString().length(); i <6;i++) {
				secuencia=secuencia+"0";					
			}				
			String codigo = tipoResolucion.getAbreviacion() +secuencia+ Integer.toString(numero);
			return codigo;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	//MAAE-SUIA-OTQE-DZDG-2020-000105  
	public String generarCodigoInspeccion(Area area) {
		String anioActual=secuenciasFacade.getCurrentYear();		
		String nombreSecuencia="INFORME_INSPECCION_DESCRIPCION_"+anioActual+"_"+area.getAreaAbbreviation();
		
		try {
			return Constantes.SIGLAS_INSTITUCION + "-SUIA-"
					+ area.getAreaAbbreviation()
					+ "-"
					+ anioActual
					+ "-"
					+ secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 4);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	//MAAE-SUIA-OTQE-DZDG-2020-000105
	public String generarCodigoInformeTecnicoDescripcion(Area area) {
		String anioActual=secuenciasFacade.getCurrentYear();		
		String nombreSecuencia="INFORME_TECNICO_DESCRIPCION_"+anioActual+"_"+area.getAreaAbbreviation();
		
		try {
			return Constantes.SIGLAS_INSTITUCION + "-SUIA-"
					+ area.getAreaAbbreviation()
					+ "-"
					+ anioActual
					+ "-"
					+ secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 4);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	 
	//MAAE-SUIA- OTQE-DZDG-2020-000115-O  OFICIO_PRONUNCIAMIENTO_DESCRIPCION
	public String generarCodigoOficioPronunciamiento(Area area) {
		String anioActual=secuenciasFacade.getCurrentYear();		
		String nombreSecuencia="INFORME_TECNICO_DESCRIPCION_"+anioActual+"_"+area.getAreaAbbreviation();
		
		try {
			return Constantes.SIGLAS_INSTITUCION + "-SUIA-"
					+ area.getAreaAbbreviation()
					+ "-"
					+ anioActual
					+ "-"
					+ secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 4)
					+ "-O";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}