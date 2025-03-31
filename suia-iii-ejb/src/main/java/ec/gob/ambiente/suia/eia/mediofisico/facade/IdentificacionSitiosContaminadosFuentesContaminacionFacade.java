package ec.gob.ambiente.suia.eia.mediofisico.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.IdentificacionSitiosContaminadosFuentesContaminacion;
import ec.gob.ambiente.suia.eia.descripcion.proyecto.facade.EiaOpcionesFacade;
import ec.gob.ambiente.suia.eia.mediofisico.service.IdentificacionSitiosContaminadosFuentesContaminacionService;
import ec.gob.ambiente.suia.exceptions.ServiceException;

@Stateless
public class IdentificacionSitiosContaminadosFuentesContaminacionFacade {
    @EJB
    private IdentificacionSitiosContaminadosFuentesContaminacionService identificacionSitiosContaminadosFuentesContaminacionService;
    @EJB
    private CrudServiceBean crudServiceBean;
    @EJB
    private EiaOpcionesFacade eiaOpcionesFacade;

    public List<IdentificacionSitiosContaminadosFuentesContaminacion> identificacionSitiosContaminadosFuentesContaminacionsXEiaId(Integer eiaId) {
        return identificacionSitiosContaminadosFuentesContaminacionService.identificacionSitiosContaminadosFuentesContaminacionsXEiaId(eiaId);
    }
    public void guardar(List<IdentificacionSitiosContaminadosFuentesContaminacion> listaIdentificacionSitiosContaminadosFuentesContaminacion,List<IdentificacionSitiosContaminadosFuentesContaminacion> identificacionSCFCBorrados, EstudioImpactoAmbiental eia)
            throws ServiceException{
        try{

            List<IdentificacionSitiosContaminadosFuentesContaminacion> listaIdentificacionSitiosContaminados = getIdentificacionesSitiosContaminadosSuentesContaminacion(eia);
            for (IdentificacionSitiosContaminadosFuentesContaminacion identificacionSitiosContaminadosFuentesContaminacion1 : listaIdentificacionSitiosContaminados) {
                for (IdentificacionSitiosContaminadosFuentesContaminacion identificacionSitiosContaminadosFuentesContaminacion : listaIdentificacionSitiosContaminadosFuentesContaminacion) {
                if (identificacionSitiosContaminadosFuentesContaminacion1.getId() == identificacionSitiosContaminadosFuentesContaminacion.getId()){
                    identificacionSitiosContaminadosFuentesContaminacion1.setEstado(false);
                    crudServiceBean.saveOrUpdate(identificacionSitiosContaminadosFuentesContaminacion1);
                }
                }

            }


                eia.setTieneIdentificacionSitiosContaminados(true);
                eia.setJustificacionIdentificacionSitiosContaminados("");
                crudServiceBean.saveOrUpdate(eia);
                crudServiceBean.saveOrUpdate(listaIdentificacionSitiosContaminadosFuentesContaminacion);

                for (IdentificacionSitiosContaminadosFuentesContaminacion identificacionSitiosContaminadosFuentesContaminacion : identificacionSCFCBorrados) {
                    identificacionSitiosContaminadosFuentesContaminacion.setEstado(false);
                    crudServiceBean.saveOrUpdate(identificacionSitiosContaminadosFuentesContaminacion);


                }


        }catch(RuntimeException e){
            throw new ServiceException("Error al guardar",e);
        }

    }

    public void actualizarEIA(EstudioImpactoAmbiental eia, String justificacion)
            throws ServiceException{
        try{
            List<IdentificacionSitiosContaminadosFuentesContaminacion> listaIdentificacionSitiosContaminados = getIdentificacionesSitiosContaminadosSuentesContaminacion(eia);
            for (IdentificacionSitiosContaminadosFuentesContaminacion identificacionSitiosContaminadosFuentesContaminacion1 : listaIdentificacionSitiosContaminados) {
                identificacionSitiosContaminadosFuentesContaminacion1.setEstado(false);
                crudServiceBean.saveOrUpdate(identificacionSitiosContaminadosFuentesContaminacion1);


            }


            eia.setJustificacionIdentificacionSitiosContaminados(justificacion);
            eia.setTieneIdentificacionSitiosContaminados(false);
            crudServiceBean.saveOrUpdate(eia);

        }catch(RuntimeException e){
            throw new ServiceException("Error al guardar",e);
        }

    }

    public List<IdentificacionSitiosContaminadosFuentesContaminacion> getIdentificacionesSitiosContaminadosSuentesContaminacion(EstudioImpactoAmbiental estudio) {

        return crudServiceBean
                .getEntityManager()
                .createQuery(
                        " SELECT isc FROM IdentificacionSitiosContaminadosFuentesContaminacion isc"
                                + " where isc.eist_id=:eiaId and isc.estado=true")
                .setParameter("eiaId", estudio.getId()).getResultList();
    }

}
