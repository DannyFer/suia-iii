package ec.gob.ambiente.suia.prevencion.categoria2.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ActividadProcesoPma;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;
import ec.gob.ambiente.suia.domain.ImpactoAmbientalPma;
import ec.gob.ambiente.suia.domain.MatrizFactorImpacto;
import ec.gob.ambiente.suia.domain.MatrizFactorImpactoOtros;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.EjecutarSentenciasNativas;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author karla.carvajal
 *
 */
@Stateless
public class ImpactoAmbientalPmaServiceBean {

    @EJB
    private CrudServiceBean crudServiceBean;
    @EJB
    private EjecutarSentenciasNativas ejecutarSentenciasNativas;

    @SuppressWarnings("unchecked")
    public List<ImpactoAmbientalPma> getImpactosAmbientalesPorFichaId(Integer idFicha) throws ServiceException {
        StringBuilder sql = new StringBuilder();
        sql.append("select i.* from suia_iii.fapma_environmental_impacts i, suia_iii.fapma_process_activity a, suia_iii.commercial_activity_catalog ac,");
        sql.append(" suia_iii.sectors_classifications_phases sf, suia_iii.phases f where i.fapa_id = a.fapa_id AND a.coac_id = ac.coac_id AND ac.secp_id = sf.secp_id AND sf.phas_id = f.phas_id");
        sql.append(" and i.cafa_id=").append(idFicha).append(" ORDER BY f.phas_name");
        return crudServiceBean.findNativeQuery(sql.toString(), ImpactoAmbientalPma.class);
    }

    @SuppressWarnings("unchecked")
    public List<MatrizFactorImpactoOtros> getImpactosAmbientalesPorFichaIdOtros(Integer idFicha) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("idFicha", idFicha);
        return (List<MatrizFactorImpactoOtros>) crudServiceBean.findByNamedQuery(MatrizFactorImpactoOtros.LISTAR_POR_FICHA_ID, params);
    }
    
    public void guardarImpactoAmbiental_(List<ActividadProcesoPma> listaActividadProcesoPma, List<MatrizFactorImpactoOtros> listaImpactoAmbientalPmaOtros, FichaAmbientalPma fichaAmbientalPma) throws ServiceException {
        try {
            String sql = "DELETE FROM suia_iii.matrix_impact_factor WHERE faen_id IN ("
                    + "SELECT faen_id  FROM suia_iii.fapma_environmental_impacts WHERE cafa_id = " + fichaAmbientalPma.getId() + ")";
            ejecutarSentenciasNativas.ejecutarSentenciasNativasDml(sql);
            sql = "DELETE FROM suia_iii.fapma_environmental_impacts WHERE cafa_id = " + fichaAmbientalPma.getId();
            ejecutarSentenciasNativas.ejecutarSentenciasNativasDml(sql);
            sql = "DELETE FROM suia_iii.other_matrix_impact_factor WHERE cafa_id = " + fichaAmbientalPma.getId();
            ejecutarSentenciasNativas.ejecutarSentenciasNativasDml(sql);
            for (ActividadProcesoPma a : listaActividadProcesoPma) {
                List<MatrizFactorImpacto> listaH = a.getListaMatrizFactorImpacto();
                ImpactoAmbientalPma imp = new ImpactoAmbientalPma();
                imp.setActividadProcesoPma(a);
                imp.setFichaAmbientalPma(fichaAmbientalPma);
                ImpactoAmbientalPma impPersist = crudServiceBean.saveOrUpdate(imp);
                for (MatrizFactorImpacto mfi : listaH) {
                    mfi.setImpactoAmbientalPma(impPersist);
                    crudServiceBean.saveOrUpdate(mfi);
                }
            }
            fichaAmbientalPma.setValidarPrincipalesImpactosAmbientales(true);
            crudServiceBean.saveOrUpdate(fichaAmbientalPma);
            if (listaImpactoAmbientalPmaOtros != null) {
                crudServiceBean.saveOrUpdate(listaImpactoAmbientalPmaOtros);
            }
//        crudServiceBean.saveOrUpdate(listaImpactoAmbientalPma);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        } catch (SQLException e) {
            throw new ServiceException(e);
        }
    }

    public void eliminarImpactoAmbiental(ImpactoAmbientalPma impactoAmbientalPma) {
        crudServiceBean.delete(impactoAmbientalPma);
    }

    public void guardarImpactoAmbiental(List<ActividadProcesoPma> listaActividadProcesoPma, 
    		List<MatrizFactorImpactoOtros> listaImpactoAmbientalPmaOtros, FichaAmbientalPma fichaAmbientalPma,
    		List<MatrizFactorImpacto> listaImpactoEliminados, List<MatrizFactorImpactoOtros> listaImpactosOtrosEliminados) throws ServiceException {
        try {
            for (ActividadProcesoPma a : listaActividadProcesoPma) {
            	ImpactoAmbientalPma impPersist = new ImpactoAmbientalPma();
                List<MatrizFactorImpacto> listaH = a.getListaMatrizFactorImpacto();
                for (MatrizFactorImpacto mfiAux : listaH) {
                	if(mfiAux.getImpactoAmbientalPma() != null){
                		impPersist = mfiAux.getImpactoAmbientalPma();
                		break;
                	}
                }
                
                if(impPersist.getId() == null){
                	ImpactoAmbientalPma imp = new ImpactoAmbientalPma();
                    imp.setActividadProcesoPma(a);
                    imp.setFichaAmbientalPma(fichaAmbientalPma);
                    impPersist = crudServiceBean.saveOrUpdate(imp);
                }                
                
                for (MatrizFactorImpacto mfi : listaH) {
                    mfi.setImpactoAmbientalPma(impPersist);
                    crudServiceBean.saveOrUpdate(mfi);
                }
            }
            fichaAmbientalPma.setValidarPrincipalesImpactosAmbientales(true);
            crudServiceBean.saveOrUpdate(fichaAmbientalPma);
            if (listaImpactoAmbientalPmaOtros != null) {
                crudServiceBean.saveOrUpdate(listaImpactoAmbientalPmaOtros);
            }
            
            if (listaImpactoEliminados != null && listaImpactoEliminados.size() > 0) {
                crudServiceBean.delete(listaImpactoEliminados);
            }
            if (listaImpactosOtrosEliminados != null && listaImpactosOtrosEliminados.size() > 0) {
                crudServiceBean.delete(listaImpactosOtrosEliminados);
            }
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        } 
    }
    
    public void guardarMatrizFactorImpacto(List<MatrizFactorImpacto> listaMatriz) {
        crudServiceBean.saveOrUpdate(listaMatriz);
    }
    
    public void guardarMatrizFactorOtroImpacto(List<MatrizFactorImpactoOtros> listaMatriz) {
        crudServiceBean.saveOrUpdate(listaMatriz);
    }
    
}
