package ec.gob.ambiente.suia.prevencion.participacionsocial.service;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Consultor;
import ec.gob.ambiente.suia.domain.ParticipacionSocialAmbiental;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class ParticipacionSocialService {
    /**
     *
     */
    private static final long serialVersionUID = 2413378444626938762L;
    @EJB
    UsuarioFacade usuarioFacade;
    @EJB
    private CrudServiceBean crudServiceBean;

    public ParticipacionSocialAmbiental guardar(ParticipacionSocialAmbiental participacionSocialAmbiental) {
        return crudServiceBean.saveOrUpdate(participacionSocialAmbiental);
    }

    @SuppressWarnings("unchecked")
    public List<Consultor> obtenerConsultores() {
        return (List<Consultor>) crudServiceBean.findAll(Consultor.class);
    }


    @SuppressWarnings("unchecked")
    public ParticipacionSocialAmbiental buscarCrearParticipacionSocialAmbiental(ProyectoLicenciamientoAmbiental proyecto) {

        List<ParticipacionSocialAmbiental> participacionSocialAmbientales = crudServiceBean
                .getEntityManager()
                .createQuery(
                        " SELECT p FROM ParticipacionSocialAmbiental p"
                                + " where p.proyectoLicenciamientoAmbiental.id =:idProyecto ")
                .setParameter("idProyecto", proyecto.getId()).getResultList();
        if (participacionSocialAmbientales.size() > 0) {
            return participacionSocialAmbientales.get(0);
        } else {
            ParticipacionSocialAmbiental participacionSocialAmbiental = new ParticipacionSocialAmbiental();
            participacionSocialAmbiental.setProyectoLicenciamientoAmbiental(proyecto);
            crudServiceBean.saveOrUpdate(participacionSocialAmbiental);
            return participacionSocialAmbiental;
        }

    }

    @SuppressWarnings("unchecked")
    public ParticipacionSocialAmbiental getProyectoParticipacionSocialByProjectId(ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental) {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("prenId", proyectoLicenciamientoAmbiental.getId());
        try {
            List<ParticipacionSocialAmbiental> result = (List<ParticipacionSocialAmbiental>) crudServiceBean
                    .findByNamedQuery(
                            ParticipacionSocialAmbiental.FIND_BY_PROJECT,
                            params);

            return (ParticipacionSocialAmbiental) result.get(0);
        } catch (Exception e) {
            return null;
        }
    }


    public PlantillaReporte obtenerPlantillaReporte(Integer tipoDocumentoId) {
        try {
            Map<String, Object> parametros = new HashMap<String, Object>();
            parametros.put("p_tipoDocumentoId", tipoDocumentoId);

            List<PlantillaReporte> lista = crudServiceBean
                    .findByNamedQueryGeneric(
                            PlantillaReporte.OBTENER_PLANTILLA_POR_INFORME,
                            parametros);
            if (lista != null && !lista.isEmpty()) {
                return lista.get(0);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
//
//	public InformeTecnicoEia obtenerInformeTecnicoEiaPorEstudio(
//			TipoDocumentoSistema tipoDocumento, Integer estudioImpactoAmbientalId) {
//		try {
//			Map<String, Object> parametros = new HashMap<String, Object>();
//			parametros.put("p_tipoDocumentoId", tipoDocumento.getIdTipoDocumento());
//			parametros.put("p_estudioImpactoAmbientalId", estudioImpactoAmbientalId);
//
//			List<InformeTecnicoEia> lista = crudServiceBean
//					.findByNamedQueryGeneric(
//							InformeTecnicoEia.OBTENER_INFORME_TECNICO_EIA_POR_ESTUDIO_TIPO,
//							parametros);
//			if (lista != null && !lista.isEmpty()) {
//				return lista.get(0);
//			}
//		} catch (RuntimeException e) {
//			throw new RuntimeException(e);
//		}
//
//		return null;
//	}

}
