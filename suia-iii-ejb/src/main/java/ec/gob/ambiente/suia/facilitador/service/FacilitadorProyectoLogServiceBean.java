package ec.gob.ambiente.suia.facilitador.service;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.FacilitadorProyectoLog;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Usuario;

@Stateless
public class FacilitadorProyectoLogServiceBean {
    @EJB
    private CrudServiceBean crudServiceBean;

    @SuppressWarnings("unchecked")
    public Integer buscarRechazos(Usuario usuario) {
        GregorianCalendar calToday = new GregorianCalendar();
        GregorianCalendar oneYearAgoTodayAtMidnight = new GregorianCalendar(
                calToday.get(Calendar.YEAR) - 1, calToday.get(Calendar.MONTH), calToday.get(Calendar.DATE));
        List<FacilitadorProyectoLog> usuarios = (List<FacilitadorProyectoLog>) crudServiceBean
                .getEntityManager()
                .createQuery(
                        " SELECT fp.id FROM FacilitadorProyectoLog fp"
                                + " where fp.usuario.id =:usuario and fp.fechaNegacion > :fechaNegacion"
                ).setParameter("fechaNegacion", oneYearAgoTodayAtMidnight.getTime())
                .setParameter("usuario", usuario.getId()).getResultList();
        return usuarios.size();

    }

    public FacilitadorProyectoLog guardar(FacilitadorProyectoLog facilitadorProyectoLog) {
        return crudServiceBean.saveOrUpdate(facilitadorProyectoLog);

    }

    @SuppressWarnings("unchecked")
    public List<FacilitadorProyectoLog> listarFacilitadoresRechazadosProyecto(ProyectoLicenciamientoAmbiental proyecto) {

        List<FacilitadorProyectoLog> facilitadorProyectos = (List<FacilitadorProyectoLog>) crudServiceBean
                .getEntityManager()
                .createQuery(
                        " SELECT fp FROM FacilitadorProyectoLog fp"
                                + " where fp.proyecto.id = :idProyecto"
                ).setParameter("idProyecto", proyecto.getId()).getResultList();
        return facilitadorProyectos;

    }
}
