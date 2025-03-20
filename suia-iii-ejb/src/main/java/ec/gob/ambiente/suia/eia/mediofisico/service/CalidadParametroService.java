/**
 * Copyright (c) 2015 MAGMASOFT (Innovando tecnologia)
 * Todos los derechos reservados.
 * Este software es confidencial y debe usarlo de acorde con los términos de uso.
 */
package ec.gob.ambiente.suia.eia.mediofisico.service;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CalidadParametro;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.FactorPma;
import ec.gob.ambiente.suia.domain.ResultadoAnalisis;
import ec.gob.ambiente.suia.exceptions.ServiceException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase que
 *
 * @author Juan Gabriel Guzmán.
 * @version 1.0
 */
@Stateless
public class CalidadParametroService {

    @EJB
    private CrudServiceBean crudServiceBean;

    public List<CalidadParametro> getCalidadParametro(EstudioImpactoAmbiental estudio, FactorPma componente) throws ServiceException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("paramEia", estudio);
        params.put("paramComponente", componente);
        try {
            List<CalidadParametro> calidadSuelos=  (List<CalidadParametro>)crudServiceBean.findByNamedQuery(CalidadParametro.FIND_BY_EIA_AND_COMPONENTE, params);
            for (CalidadParametro calidadSuelo : calidadSuelos){
                calidadSuelo.getParametroNormativas().getTablaNormativas().getNormativa().toString();
                List<ResultadoAnalisis> resultados = calidadSuelo.getResultadosAnalisis();
                for(ResultadoAnalisis resultado : resultados){
                    resultado.getMuestra().getCoordenadaGeneral();
                }
            }
            return calidadSuelos;

        } catch (RuntimeException e) {
            throw new ServiceException("No se pudo recuparar la lista de Calidad Parametro", e);
        }
    }


}
