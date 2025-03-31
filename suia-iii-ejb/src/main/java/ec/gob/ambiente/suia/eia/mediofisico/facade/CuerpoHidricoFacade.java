package ec.gob.ambiente.suia.eia.mediofisico.facade;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CoordenadaGeneral;
import ec.gob.ambiente.suia.domain.CuerpoHidrico;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.UsoCuerpoHidrico;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.eia.mediofisico.service.CuerpoHidricoService;
import ec.gob.ambiente.suia.exceptions.ServiceException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class CuerpoHidricoFacade {
    @EJB
    private CrudServiceBean crudServiceBean;

    @EJB
    private CuerpoHidricoService cuerpoHidricoService;

    public void guardar(List<CuerpoHidrico> listaCuerpoHidricos, List<EntidadAuditable> cuerpoHidricosBorradas) throws ServiceException {
        try {

            for (CuerpoHidrico cuerpoHidrico : listaCuerpoHidricos) {
                List<CoordenadaGeneral> listaCoordenadas = new ArrayList<CoordenadaGeneral>();
                listaCoordenadas.addAll(cuerpoHidrico.getCoordenadaGeneralList());
                cuerpoHidrico.setCoordenadaGeneralList( new ArrayList<CoordenadaGeneral>());
                List<UsoCuerpoHidrico> listaUsosCuerposHidricos = new ArrayList<UsoCuerpoHidrico>();
                listaUsosCuerposHidricos.addAll(cuerpoHidrico.getUsosCuerposHidricos());
                crudServiceBean.saveOrUpdate(cuerpoHidrico);

                for (CoordenadaGeneral c : listaCoordenadas) {
                    c.setCuerpoHidrico(cuerpoHidrico);
                    c.setIdTable(cuerpoHidrico.getId());
                    c.setNombreTabla(CuerpoHidrico.class.getSimpleName());
                    crudServiceBean.saveOrUpdate(c);
                }
                for (UsoCuerpoHidrico u : listaUsosCuerposHidricos) {
                    u.setCuerpoHidrico(cuerpoHidrico);
                    crudServiceBean.saveOrUpdate(u);
                }
            }
            for (EntidadAuditable obj : cuerpoHidricosBorradas) {
                obj.setEstado(false);
                crudServiceBean.saveOrUpdate(obj);
            }

        } catch (Exception ex) {
            throw new ServiceException(ex);
        }
    }

    public List<CuerpoHidrico> cuerpoHidricoXEiaId(EstudioImpactoAmbiental estudioImpactoAmbiental) throws ServiceException {
        List<CuerpoHidrico> cuerpos = cuerpoHidricoService.cuerpoHidricoXEiaId(estudioImpactoAmbiental);

        List<CuerpoHidrico> result = new ArrayList<CuerpoHidrico>();
        for (CuerpoHidrico cuerpoHidrico : cuerpos) {

            cuerpoHidrico.setCoordenadaGeneralList(listarCoordenadaGeneral(CuerpoHidrico.class.getSimpleName(), cuerpoHidrico.getId()));
            cuerpoHidrico.setUsosCuerposHidricos(listarUsosCuerposHidricos(cuerpoHidrico));

            result.add(cuerpoHidrico);
        }

        return result;
    }

    public List<CoordenadaGeneral> listarCoordenadaGeneral(String nombreTabla, Integer idTabla) throws ServiceException {
        return cuerpoHidricoService.listarCoordenadaGeneral(nombreTabla, idTabla);
    }

    public List<UsoCuerpoHidrico> listarUsosCuerposHidricos(CuerpoHidrico cuerpoHidrico) throws ServiceException {
        return cuerpoHidricoService.listarUsoCuerpoHidrico(cuerpoHidrico);
    }

}
