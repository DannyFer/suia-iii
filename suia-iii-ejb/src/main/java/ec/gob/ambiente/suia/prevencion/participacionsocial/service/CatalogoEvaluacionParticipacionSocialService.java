package ec.gob.ambiente.suia.prevencion.participacionsocial.service;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CatalogoEvaluacionParticipacionSocial;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class CatalogoEvaluacionParticipacionSocialService {
    @EJB
    UsuarioFacade usuarioFacade;
    @EJB
    private CrudServiceBean crudServiceBean;

    public CatalogoEvaluacionParticipacionSocial guardar(CatalogoEvaluacionParticipacionSocial catalogoEvaluacionParticipacionSocial) {
        return crudServiceBean.saveOrUpdate(catalogoEvaluacionParticipacionSocial);
    }

    //-----------------------------
    @SuppressWarnings("unchecked")
    public List<CatalogoEvaluacionParticipacionSocial> buscarCatalogosEvaluacionPSPorGrupo(String grupo) {

        List<CatalogoEvaluacionParticipacionSocial> catalogoEvaluacionParticipacionSociales = crudServiceBean
                .getEntityManager()
                .createQuery(
                        " SELECT cp FROM CatalogoEvaluacionParticipacionSocial cp"
                                + " where cp.grupo =:grupo ")
                .setParameter("grupo", grupo).getResultList();

        return catalogoEvaluacionParticipacionSociales;


    }


}
