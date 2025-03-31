package ec.gob.ambiente.suia.facilitador.service;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.FacilitadorProyecto;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.exceptions.ServiceException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

@SuppressWarnings("unchecked")
@Stateless
public class FacilitadorProyectoServiceBean {
    @EJB
    private CrudServiceBean crudServiceBean;


    public FacilitadorProyecto guardar(FacilitadorProyecto facilitadorProyecto) {
        return crudServiceBean.saveOrUpdate(facilitadorProyecto);

    }

    public FacilitadorProyecto guardarNoExiste(FacilitadorProyecto facilitadorProyecto) {
        List<FacilitadorProyecto> facilitadorProyectos = (List<FacilitadorProyecto>) crudServiceBean
                .getEntityManager()
                .createQuery(
                        " SELECT fp FROM FacilitadorProyecto fp"
                                + " where fp.proyecto.id = :idProyecto and fp.usuario.id = :idUsuario "
                ).setParameter("idProyecto", facilitadorProyecto.getProyecto().getId()).
                        setParameter("idUsuario", facilitadorProyecto.getUsuario().getId()).getResultList();
        if (facilitadorProyectos.size() > 0) {
            return facilitadorProyectos.get(0);
        } else {
            return crudServiceBean.saveOrUpdate(facilitadorProyecto);
        }

    }

    @SuppressWarnings("unchecked")
    public List<FacilitadorProyecto> listarFacilitadoresProyecto(ProyectoLicenciamientoAmbiental proyecto) {

        List<FacilitadorProyecto> facilitadorProyectos = (List<FacilitadorProyecto>) crudServiceBean
                .getEntityManager()
                .createQuery(
                        " SELECT fp FROM FacilitadorProyecto fp"
                                + " where fp.proyecto.id = :idProyecto order by 1 asc"
                ).setParameter("idProyecto", proyecto.getId()).getResultList();
        return facilitadorProyectos;

    }

    @SuppressWarnings("unchecked")
    public FacilitadorProyecto obtenerFacilitadorResponsable(ProyectoLicenciamientoAmbiental proyecto) throws ServiceException {

        List<FacilitadorProyecto> facilitadorProyectos = (List<FacilitadorProyecto>) crudServiceBean
                .getEntityManager()
                .createQuery(
                        " SELECT fp FROM FacilitadorProyecto fp"
                                + " where fp.proyecto.id = :idProyecto and fp.facilitadorEncargado = true"
                ).setParameter("idProyecto", proyecto.getId()).getResultList();
        if (facilitadorProyectos.size() > 0) {
            return facilitadorProyectos.get(0);
        }
        throw new ServiceException("No se encuentra el facilitador por defecto asignado.");

    }
}
