package ec.gob.ambiente.suia.prevencion.participacionsocial.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.EvaluacionParticipacionSocial;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;

@Stateless
public class EvaluacionParticipacionSocialService {
    @EJB
    UsuarioFacade usuarioFacade;
    @EJB
    private CrudServiceBean crudServiceBean;

    public EvaluacionParticipacionSocial guardar(EvaluacionParticipacionSocial evaluacionParticipacionSocial) {
        return crudServiceBean.saveOrUpdate(evaluacionParticipacionSocial);
    }
    public void guardarLista(List<EvaluacionParticipacionSocial> evaluacionParticipacionSocial) {
       crudServiceBean.saveOrUpdate(evaluacionParticipacionSocial);
    }

    @SuppressWarnings("unchecked")
    public List<EvaluacionParticipacionSocial> buscarEvaluacionParticipacionSocial(Integer idProyecto) {

        List<EvaluacionParticipacionSocial> evaluacionParticipacionSocial = crudServiceBean
                .getEntityManager()
                .createQuery(
                        " SELECT ev FROM EvaluacionParticipacionSocial ev"
                                + " where ev.participacionSocialAmbiental.proyectoLicenciamientoAmbiental.id =:idProyetco")
                .setParameter("idProyetco", idProyecto).getResultList();

        return evaluacionParticipacionSocial;


    }


}
