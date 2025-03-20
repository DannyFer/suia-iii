package ec.gob.ambiente.suia.prevencion.viabilidadtecnica.facade;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import ec.gob.ambiente.suia.domain.EstudioViabilidadTecnicaDiagnostico;
import ec.gob.ambiente.suia.domain.ViabilidadTecnicaParametrosDiagnostico;
import ec.gob.ambiente.suia.domain.ViabilidadTecnicaRegistrosDiagnostico;
import ec.gob.ambiente.suia.prevencion.viabilidadtecnica.service.ViabilidadTecnicaRegistrosDiagnosticoService;


@Stateless
public class ViabilidadTecnicaRegistrosDiagnosticoFacade implements Serializable 
{

	private static final long serialVersionUID = -7848402306718638884L;
	
	@EJB
	private ViabilidadTecnicaRegistrosDiagnosticoService viabilidadTecnicaRegistrosDiagnosticoService;

	//---------------------------------------------------------------------------------------------------------				
	public void ingresarViabilidadTecnicaRegistrosDiagnostico(String id, String valorParametro,
			EstudioViabilidadTecnicaDiagnostico estudioViabilidadTecnicaDiagnostico,
			ViabilidadTecnicaParametrosDiagnostico viabilidadTecnicaParametrosDiagnostico,
			ViabilidadTecnicaRegistrosDiagnostico viabilidadTecnicaRegistrosDiagnostico)
	{
		viabilidadTecnicaRegistrosDiagnosticoService.ingresarViabilidadTecnicaRegistrosDiagnostico(id, valorParametro, estudioViabilidadTecnicaDiagnostico, viabilidadTecnicaParametrosDiagnostico, viabilidadTecnicaRegistrosDiagnostico);
	}
	//---------------------------------------------------------------------------------------------------------				
	public String buscarRegistrosViabilidadTecnica(String idEstudio, String idParametro)
	{
    	return viabilidadTecnicaRegistrosDiagnosticoService.buscarRegistrosViabilidadTecnica(idEstudio, idParametro);
	}
	//---------------------------------------------------------------------------------------------------------				
}
