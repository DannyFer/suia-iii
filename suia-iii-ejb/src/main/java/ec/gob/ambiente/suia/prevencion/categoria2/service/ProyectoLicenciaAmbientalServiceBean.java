package ec.gob.ambiente.suia.prevencion.categoria2.service;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.exceptions.ServiceException;

import org.apache.log4j.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author karla.carvajal
 */
@Stateless
public class ProyectoLicenciaAmbientalServiceBean {

    private static final Logger LOG = Logger.getLogger(ProyectoLicenciaAmbientalServiceBean.class);

    @EJB
    private CrudServiceBean crudServiceBean;

    public ProyectoLicenciamientoAmbiental getProyectoPorCodigo(String codigo) {
        try {
            ProyectoLicenciamientoAmbiental proyecto = (ProyectoLicenciamientoAmbiental) crudServiceBean
                    .getEntityManager()
                    .createQuery("From ProyectoLicenciamientoAmbiental p where p.codigo =:codigo and p.estado = true")
                    .setParameter("codigo", codigo).getSingleResult();
            
            // Validación para evitar NullPointerException
            if (proyecto.getUsuario() != null && proyecto.getUsuario().getPersona() != null) {
                proyecto.getUsuario().getPersona().getId();
            }
            return proyecto;
        } catch (NoResultException e) {
            return null; // Retorna null si no se encuentra el proyecto
        } catch (Exception e) {
            e.printStackTrace(); // Agrega un log para posibles errores no esperados
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Contacto> getContactosPorPersona(Integer idPersona) {
        try {
            List<Contacto> contactos = crudServiceBean.getEntityManager()
                    .createQuery("From Contacto c where c.persona.id =:id").setParameter("id", idPersona)
                    .getResultList();
            FormasContacto fc = new FormasContacto();
            for (Contacto c : contactos) {
                c.getFormasContacto().hashCode();
                fc.setContactoList(c.getFormasContacto().getContactoList());
                fc.setEstado(c.getFormasContacto().getEstado());
                fc.setId(c.getFormasContacto().getId());
                fc.setNombre(c.getFormasContacto().getNombre());
                fc.setOrden(c.getFormasContacto().getOrden());
                c.setFormasContacto(fc);
            }
            return contactos;
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public UbicacionesGeografica getUbicacionProyectoPorIdProyecto(Integer idProyecto) {
        List<UbicacionesGeografica> ubicacionProyecto = (List<UbicacionesGeografica>) crudServiceBean
                .getEntityManager()
                .createQuery(
                        "select u.ubicacionesGeografica From ProyectoUbicacionGeografica u where u.idProyecto =:idProyecto and u.estado = true")
                .setParameter("idProyecto", idProyecto).getResultList();

        if (ubicacionProyecto != null && !ubicacionProyecto.isEmpty()) {
            return ubicacionProyecto.get(0);
        } else {
            return null;
        }
    }

    public ProyectoLicenciamientoAmbiental getProyectoPorId(Integer idProyecto) {
        ProyectoLicenciamientoAmbiental proyecto = (ProyectoLicenciamientoAmbiental) crudServiceBean.getEntityManager()
                .createQuery("From ProyectoLicenciamientoAmbiental p where p.id =:id and p.estado = true")
                .setParameter("id", idProyecto).getSingleResult();
        proyecto.getUsuario();
        try {
            proyecto.getCatalogoCategoria().getTipoSubsector().getId();
        } catch (Exception e) {
        }
        return proyecto;
    }

    public ProyectoLicenciamientoAmbiental getProyectoAreaPorId(Integer idProyecto) {
        ProyectoLicenciamientoAmbiental proyecto = (ProyectoLicenciamientoAmbiental) crudServiceBean.getEntityManager()
                .createQuery("From ProyectoLicenciamientoAmbiental p where p.id =:id and p.estado = true")
                .setParameter("id", idProyecto).getSingleResult();
        proyecto.getUsuario();
        try {
            proyecto.getAreaResponsable().getTipoArea().getId();
        } catch (Exception e) {
        }
        return proyecto;
    }


    public List<ConcesionMinera> getConcesionesMineraPorIdProyecto(Integer idProyecto) throws ServiceException {
        List<ConcesionMinera> concesionesMinera = (List<ConcesionMinera>) crudServiceBean
                .getEntityManager()
                .createQuery(
                        "select p.concesionesMineras From ProyectoLicenciamientoAmbiental p where p.id =:idProyecto and p.estado = true")
                .setParameter("idProyecto", idProyecto).getResultList();

        if (concesionesMinera != null && !concesionesMinera.isEmpty()) {
            return concesionesMinera;
        } else {
            throw new ServiceException("Error al recuperar las concesiones mineras del proyecto.");
        }
    }

    public List<ProyectoBloque> getProyectosBloques(Integer idProyecto) throws ServiceException {
        List<ProyectoBloque> proyBloques = (List<ProyectoBloque>) crudServiceBean
                .getEntityManager()
                .createQuery(
                        "select p.proyectoBloques From ProyectoLicenciamientoAmbiental p where p.id =:idProyecto and p.estado = true")
                .setParameter("idProyecto", idProyecto).getResultList();

        if (proyBloques != null && !proyBloques.isEmpty()) {
            return proyBloques;
        } else {
            throw new ServiceException("Error al recuperar los bloques del proyecto.");
        }
    }

    public List<ZonasFase> getZonasFase()throws ServiceException {
        List<ZonasFase> zonasDeFase = (List<ZonasFase>) crudServiceBean
                .getEntityManager()
                .createQuery(
                        "select z From ZonasFase z").getResultList();
        if (zonasDeFase != null && !zonasDeFase.isEmpty()) {
            return zonasDeFase;
        } else {
            throw new ServiceException("Error al recuperar las zonas.");
        }
    }

    public int getTotalRegistroAmbientalPorProyectoNativeQuery(final Integer idProyecto) {
        int total = 0;
        try {
            StringBuilder sql = new StringBuilder();

            sql.append("SELECT cafa_id");
            sql.append(" FROM suia_iii.catii_fapma ca");
            sql.append(" WHERE ca.pren_id = :idProyecto");
            sql.append(" AND ca.cafa_finalized = true or cafa_license_number is not null;");

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("idProyecto", idProyecto);
            List<Object> result = crudServiceBean.findByNativeQuery(sql.toString(), parameters);

            total = result.size();
        } catch (Exception e) {
            LOG.error("Error al obtener las recepciones con los desechos.", e);
        }
        return total;
    }

    public int getTotalRegistroAmbientalPorProyectoMineroNativeQuery(final Integer idProyecto) {
        int total = 0;
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT mi.mien_id");
            sql.append(" FROM suia_iii.mining_enviromental_record mi");
            sql.append(" WHERE mi.pren_id = :idProyecto");
            sql.append(" AND mi.mien_finalized = true;");

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("idProyecto", idProyecto);
            List<Object> result = crudServiceBean.findByNativeQuery(sql.toString(), parameters);

            total = result.size();
        } catch (Exception e) {
            LOG.error("Error al obtener las recepciones con los desechos.", e);
        }
        return total;
    }

    public int getTotalLicenciaAmbientalPorProyectoNativeQuery(final Integer idProyecto) {
        int total = 0;
        try {
            StringBuilder sql = new StringBuilder();

            sql.append("SELECT li.lice_id");
            sql.append(" FROM suia_iii.licensing li");
            sql.append(" INNER JOIN suia_iii.technical_report_environmental_licensing_general te");
            sql.append(" ON li.lice_id = te.lice_id");
            sql.append(" WHERE li.pren_id = :idProyecto");
            sql.append(" AND te.trel_finalized = true;");

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("idProyecto", idProyecto);
            List<Object> result = crudServiceBean.findByNativeQuery(sql.toString(), parameters);

            total = result.size();
        } catch (Exception e) {
            LOG.error("Error al obtener las recepciones con los desechos.", e);
        }
        return total;
    }

    /***
     * Método que verifica si un proyecto tiene un RGD en curso.
     * @Autor Denis Linares
     * @param idProyecto
     * @return
     */
    public boolean existeRgdEnCurso(Integer idProyecto) {

        try {
            StringBuilder sql = new StringBuilder();

            sql.append("SELECT p.pren_id");
            sql.append(" FROM suia_iii.projects_environmental_licensing p");
            sql.append(" INNER JOIN suia_iii.hazardous_wastes_generators h on h.pren_id=p.pren_id ");
            sql.append(" WHERE p.pren_id = :idProyecto");
            sql.append(" AND p.pren_status = true");
            sql.append(" AND p.pren_is_rgd_in_course = true");
            sql.append(" AND h.hwge_status=true");
            sql.append(" AND hwge_finalized =false;");
             
            

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("idProyecto", idProyecto);

            List<Object> result = crudServiceBean.findByNativeQuery(sql.toString(), parameters);

            return !result.isEmpty();

        } catch (Exception e) {
            LOG.error("Error al obtener los generadores de desechos asociados al proyecto con id = "+idProyecto+".", e);
        }
        return false;
    }
        
	public int getTotalCertificadoAmbientalPorProyectoNativeQuery(final Integer idProyecto) {
        int total = 0;
        try {
            StringBuilder sql = new StringBuilder();

            sql.append("SELECT ca.ence_id");
            sql.append(" FROM suia_iii.environmental_certificates_registration ca");
            sql.append(" WHERE ca.pren_id = :idProyecto");
            sql.append(" AND ca.ence_status = true;");
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("idProyecto", idProyecto);
            List<Object> result = crudServiceBean.findByNativeQuery(sql.toString(), parameters);
            total = result.size();
        } catch (Exception e) {
            LOG.error("Error al obtener getTotalCertificadoAmbientalPorProyectoNativeQuery.", e);
        }
        return total;
    }
}