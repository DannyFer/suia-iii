package ec.gob.ambiente.prevencion.viabilidadtecnica.controller;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.domain.EstudioViabilidadTecnicaDiagnostico;
import ec.gob.ambiente.suia.domain.ViabilidadTecnicaParametrosDiagnostico;
import ec.gob.ambiente.suia.domain.ViabilidadTecnicaRegistrosDiagnostico;
import ec.gob.ambiente.suia.prevencion.viabilidadtecnica.facade.ViabilidadTecnicaRegistrosDiagnosticoFacade;

@Stateless
public class ViabilidadTecnicaRegistrosDiagnosticoController implements Serializable {

	@EJB
	private ViabilidadTecnicaRegistrosDiagnosticoFacade viabilidadTecnicaRegistrosDiagnosticoFacade;

	private static final long serialVersionUID = -3684889201189500216L;

	//---------------------------------------------------------------------------------------------------------					
	public void ingresarViabilidadTecnicaRegistrosDiagnostico(String id, String valorParametro,
			EstudioViabilidadTecnicaDiagnostico estudioViabilidadTecnicaDiagnostico,
			ViabilidadTecnicaParametrosDiagnostico viabilidadTecnicaParametrosDiagnostico,
			ViabilidadTecnicaRegistrosDiagnostico viabilidadTecnicaRegistrosDiagnostico)
	{
		viabilidadTecnicaRegistrosDiagnosticoFacade.ingresarViabilidadTecnicaRegistrosDiagnostico(id, valorParametro, estudioViabilidadTecnicaDiagnostico, viabilidadTecnicaParametrosDiagnostico, viabilidadTecnicaRegistrosDiagnostico);
	}
	//---------------------------------------------------------------------------------------------------------							
	public String buscarRegistrosViabilidadTecnica(String idEstudio, String idParametro)
	{
    	return viabilidadTecnicaRegistrosDiagnosticoFacade.buscarRegistrosViabilidadTecnica(idEstudio, idParametro);
	}
	//---------------------------------------------------------------------------------------------------------				

	
}
