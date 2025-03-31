package ec.gob.ambiente.rcoa.emisionLicenciaAmbiental.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.facade.TipoResolucionFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class InformeOficioResolucionAmbientalFacade {
	

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private SecuenciasFacade secuenciasFacade;
	
	@EJB
	private TipoResolucionFacade tipoResolucionFacade;
	
	public static final String NOMBRE_DESCRIPCION_PC = "Planta Central";
	public static final String NOMBRE_DESCRIPCION_DP = "Direccion Provincial";
	
	public String generarCodigoResolucionAmbiental(Area area) throws Exception {
		
		String codigo = null;
		String nomenclatura = null;
		String nombreSecuencia = null;
		
		String tipoArea = area.getTipoArea().getSiglas().toUpperCase();
		
		String anioActual=secuenciasFacade.getCurrentYear();
		
		switch (tipoArea) {
		case Constantes.SIGLAS_TIPO_AREA_PC:
			String siglasArea = (area.getArea() != null) ? area.getArea().getAreaAbbreviation() : area.getAreaAbbreviation();
			nomenclatura = Constantes.SIGLAS_INSTITUCION + "-"
					+ Constantes.SIGLAS_SUIA + "-LA-"
					+ siglasArea + "-" + anioActual;
			break;
		case Constantes.SIGLAS_TIPO_AREA_OT:
			nomenclatura = Constantes.SIGLAS_INSTITUCION + "-"
					+ Constantes.SIGLAS_SUIA + "-LA-"
					+ area.getArea().getAreaAbbreviation() + "-"
					+ anioActual;
			break;
		case Constantes.SIGLAS_TIPO_AREA_EA:
			nomenclatura = area.getAreaAbbreviation() + "-"
					+ Constantes.SIGLAS_SUIA + "-LA-"
					+ anioActual;
			break;
		default:
			//DP, ZONALES
			nomenclatura = Constantes.SIGLAS_INSTITUCION + "-"
					+ Constantes.SIGLAS_SUIA + "-LA-"
					+ area.getAreaAbbreviation() + "-" + anioActual;
			break;
		}
		
		nombreSecuencia = "LA_" + nomenclatura;
		
		String valorSecuencial = secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 5);
		
		codigo = nomenclatura + "-" + valorSecuencial;
		
		return codigo;
	}

	
	public String generarCodigoMemorandoAmbiental(Area area) throws Exception {
		String codigo = null;
		String nomenclatura = null;
		String nombreSecuencia = null;
		
		String tipoArea = area.getTipoArea().getSiglas().toUpperCase();
		String anioActual=secuenciasFacade.getCurrentYear();
		
		switch (tipoArea) {
		case Constantes.SIGLAS_TIPO_AREA_PC:
			String siglasArea = (area.getArea() != null) ? area.getArea().getAreaAbbreviation() : area.getAreaAbbreviation();
			nomenclatura = Constantes.SIGLAS_INSTITUCION + "-"
					+ Constantes.SIGLAS_SUIA + "-"
					+ siglasArea + "-" + anioActual;
			break;
		case Constantes.SIGLAS_TIPO_AREA_OT:
			nomenclatura = Constantes.SIGLAS_INSTITUCION + "-"
					+ Constantes.SIGLAS_SUIA + "-"
					+ area.getArea().getAreaAbbreviation() + "-"
					+ anioActual;
			break;
		case Constantes.SIGLAS_TIPO_AREA_EA:
			nomenclatura = area.getAreaAbbreviation() + "-"
					+ Constantes.SIGLAS_SUIA + "-"
					+ anioActual;
			break;
		default:
			//DP, ZONALES
			nomenclatura = Constantes.SIGLAS_INSTITUCION + "-"
					+ Constantes.SIGLAS_SUIA + "-" + area.getAreaAbbreviation()
					+ "-" + anioActual;
			break;
		}
		
		nombreSecuencia = "M_" + nomenclatura;
		
		String valorSecuencial = secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 5);
		
		codigo = nomenclatura + "-" + valorSecuencial + "-M";
		
		return codigo;
	}
}