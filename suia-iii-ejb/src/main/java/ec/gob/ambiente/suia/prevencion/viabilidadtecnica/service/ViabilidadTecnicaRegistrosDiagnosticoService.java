package ec.gob.ambiente.suia.prevencion.viabilidadtecnica.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.EstudioViabilidadTecnicaDiagnostico;
import ec.gob.ambiente.suia.domain.ViabilidadTecnicaParametrosDiagnostico;
import ec.gob.ambiente.suia.domain.ViabilidadTecnicaRegistrosDiagnostico;

@Stateless
public class ViabilidadTecnicaRegistrosDiagnosticoService implements Serializable {


	private static final long serialVersionUID = 423752620433293767L;
	@EJB
	private CrudServiceBean crudServiceBean;

	//---------------------------------------------------------------------------------------------------------				
	public void ingresarViabilidadTecnicaRegistrosDiagnostico(String id, String valorParametro,
			EstudioViabilidadTecnicaDiagnostico estudioViabilidadTecnicaDiagnostico,
			ViabilidadTecnicaParametrosDiagnostico viabilidadTecnicaParametrosDiagnostico,
			ViabilidadTecnicaRegistrosDiagnostico viabilidadTecnicaRegistrosDiagnostico)
	{
		
		// Esto es para cargar el Parámetro de la Tabla technical_viability_diagnostic_parameters a través del ID
    	Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", Integer.parseInt(id));
		viabilidadTecnicaParametrosDiagnostico = (ViabilidadTecnicaParametrosDiagnostico) crudServiceBean.findByNamedQuerySingleResult(ViabilidadTecnicaParametrosDiagnostico.OBTENER_POR_ID, params);
		
		// Ahora le asigno el valor del elemento de perforacion que viene de la página
		viabilidadTecnicaRegistrosDiagnostico.setValorElementoPerforacion(Double.parseDouble(valorParametro));
		
		// Ahora le asigno el valor del Parámetro ViabilidadTecnicaParametrosDiagnostico
		viabilidadTecnicaRegistrosDiagnostico.setViabilidadTecnicaParametrosDiagnostico(viabilidadTecnicaParametrosDiagnostico);
		
		// Persisto la Viabilidad Tecnica Diagnóstico
		crudServiceBean.saveOrUpdate(estudioViabilidadTecnicaDiagnostico);
		
		// Ahora le asigno el valor del Estudio de Viabilidad Técnica Diagnóstico
		viabilidadTecnicaRegistrosDiagnostico.setEstudioViabilidadTecnicaDiagnostico(estudioViabilidadTecnicaDiagnostico);

		// Persisto la Viabilidad Tecnica Registro Diagnóstico
		crudServiceBean.saveOrUpdate(viabilidadTecnicaRegistrosDiagnostico);
	}
	//---------------------------------------------------------------------------------------------------------				
	public String buscarRegistrosViabilidadTecnica(String idEstudio, String idParametro)
	{
    	String resultado = "";
    	ViabilidadTecnicaParametrosDiagnostico viabilidadTecnicaParametrosDiagnostico;
    	ViabilidadTecnicaRegistrosDiagnostico viabilidadTecnicaRegistrosDiagnostico;
    	
    	EstudioViabilidadTecnicaDiagnostico estudioViabilidadTecnicaDiagnostico = new EstudioViabilidadTecnicaDiagnostico();
    	Map<String, Object> param = new HashMap<String, Object>();
		
		param.put("id", Integer.parseInt(idParametro));
		viabilidadTecnicaParametrosDiagnostico = (ViabilidadTecnicaParametrosDiagnostico) crudServiceBean.findByNamedQuerySingleResult(ViabilidadTecnicaParametrosDiagnostico.OBTENER_POR_ID, param);

		param.clear();
		param.put("Id", Integer.parseInt(idEstudio));
		estudioViabilidadTecnicaDiagnostico = (EstudioViabilidadTecnicaDiagnostico) crudServiceBean.findByNamedQuerySingleResult(EstudioViabilidadTecnicaDiagnostico.OBTENER_ESTUDIO_VIABILIDAD_POR_ID, param);

		param.clear();
		
		param.put("idViabilidad", estudioViabilidadTecnicaDiagnostico);
		param.put("idParametro", viabilidadTecnicaParametrosDiagnostico);
		viabilidadTecnicaRegistrosDiagnostico = (ViabilidadTecnicaRegistrosDiagnostico) crudServiceBean.findByNamedQuerySingleResult(ViabilidadTecnicaRegistrosDiagnostico.OBTENER_POR_ID_VIABILIDAD, param);
		resultado = String.valueOf(viabilidadTecnicaRegistrosDiagnostico.getValorElementoPerforacion());
		return resultado;
	}
	//---------------------------------------------------------------------------------------------------------
	
}
