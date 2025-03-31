package ec.gob.ambiente.suia.prevencion.participacionsocial.service;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.PreguntasFacilitadoresAmbientales;
import ec.gob.ambiente.suia.exceptions.ServiceException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author frank torres
 */
@Stateless
public class PreguntasFacilitadoresAmbientalesService implements Serializable {

    private static final long serialVersionUID = 895159447371391693L;
    @EJB
    private CrudServiceBean crudServiceBean;

    public PreguntasFacilitadoresAmbientales guardar(PreguntasFacilitadoresAmbientales preguntasFacilitadoresAmbientales) {
        return crudServiceBean.saveOrUpdate(preguntasFacilitadoresAmbientales);
    }

    @SuppressWarnings("unchecked")
    public List<PreguntasFacilitadoresAmbientales> obtenerPreguntasPorParticipacion(Integer idParticipacion) throws ServiceException {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("idParticipacion", idParticipacion);

        return (List<PreguntasFacilitadoresAmbientales>) crudServiceBean
                .findByNamedQuery(PreguntasFacilitadoresAmbientales.OBTENER_COMENTARIOS_ID,
                        parameters);

    }


    @SuppressWarnings("unchecked")
    public void guardarPreguntasPorParticipacion( List<PreguntasFacilitadoresAmbientales> preguntas) throws ServiceException {
         crudServiceBean.saveOrUpdate(preguntas);
    }

}
