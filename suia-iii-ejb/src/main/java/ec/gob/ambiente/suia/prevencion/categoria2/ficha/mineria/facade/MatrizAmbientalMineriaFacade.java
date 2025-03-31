/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ActividadMinera;
import ec.gob.ambiente.suia.domain.ActividadProcesoPma;
import ec.gob.ambiente.suia.domain.FichaAmbientalMineria;
import ec.gob.ambiente.suia.domain.ImpactoAmbientalPma;
import ec.gob.ambiente.suia.domain.MatrizAmbientalMineria;
import ec.gob.ambiente.suia.domain.MatrizFactorImpacto;
import ec.gob.ambiente.suia.domain.MatrizFactorImpactoOtros;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.EjecutarSentenciasNativas;

/**
 *
 * @author christian
 */
@LocalBean
@Stateless
public class MatrizAmbientalMineriaFacade {

    @EJB
    private CrudServiceBean crudServiceBean;
    @EJB
    private EjecutarSentenciasNativas ejecutarSentenciasNativas;
    @EJB
    private FichaAmbientalMineriaFacade fichaAmbientalMineriaFacade;

    public void guardar(final List<MatrizAmbientalMineria> listaMatrizAmbientalMineria, final FichaAmbientalMineria fichaAmbientalMineria) throws ServiceException {
        try {
            String sql = "DELETE FROM suia_iii.matrix_impact_factor WHERE miem_id IN ("
                    + "SELECT miem_id  FROM suia_iii.mining_environmental_matrix WHERE mien_id = " + fichaAmbientalMineria.getId() + ")";
            ejecutarSentenciasNativas.ejecutarSentenciasNativasDml(sql);
            sql = "DELETE FROM suia_iii.mining_environmental_matrix WHERE mien_id = " + fichaAmbientalMineria.getId();
            ejecutarSentenciasNativas.ejecutarSentenciasNativasDml(sql);
            for (MatrizAmbientalMineria m : listaMatrizAmbientalMineria) {
                ActividadMinera a = m.getActividadMinera();
                crudServiceBean.saveOrUpdate(m);
                m.setActividadMinera(a);
                guardarHijo(m);
            }
            fichaAmbientalMineriaFacade.guardarFicha(fichaAmbientalMineria);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        } catch (SQLException e) {
            throw new ServiceException(e);
        }
    }

    private void guardarHijo(MatrizAmbientalMineria matrizAmbientalMineria) throws ServiceException {
        try {
            if (matrizAmbientalMineria.getActividadMinera().getListaMatrizFactorImpacto() != null && !matrizAmbientalMineria.getActividadMinera().getListaMatrizFactorImpacto().isEmpty()) {
                List<MatrizFactorImpacto> lista = matrizAmbientalMineria.getActividadMinera().getListaMatrizFactorImpacto();
                for (MatrizFactorImpacto m : lista) {
                    m.setMatrizAmbientalMineria(matrizAmbientalMineria);
                }
                crudServiceBean.saveOrUpdate(lista);
            }
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    @SuppressWarnings("unchecked")
	public List<MatrizAmbientalMineria> listarPorFicha(final FichaAmbientalMineria fichaAmbientalMineria) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idFicha", fichaAmbientalMineria.getId());
            return (List<MatrizAmbientalMineria>) crudServiceBean.findByNamedQuery(MatrizAmbientalMineria.LISTAR_POR_FICHA, params);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    @SuppressWarnings("unchecked")
	public List<MatrizFactorImpacto> listarPorMatriz(final Integer idMatrizAmbientalMineria) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idMatriz", idMatrizAmbientalMineria);
            return (List<MatrizFactorImpacto>) crudServiceBean.findByNamedQuery(MatrizFactorImpacto.LISTAR_POR_MATRIZ, params);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    
    public void guardar(final List<MatrizAmbientalMineria> listaMatrizAmbientalMineria, final FichaAmbientalMineria fichaAmbientalMineria,
    		List<MatrizFactorImpacto> listaImpactoEliminados) throws ServiceException {
        try {
            for (MatrizAmbientalMineria m : listaMatrizAmbientalMineria) {
                ActividadMinera a = m.getActividadMinera();
                
                MatrizAmbientalMineria impPersist = new MatrizAmbientalMineria();
                List<MatrizFactorImpacto> listaH = a.getListaMatrizFactorImpacto();
                for (MatrizFactorImpacto mfiAux : listaH) {
                	if(mfiAux.getMatrizAmbientalMineria() != null){
                		impPersist = mfiAux.getMatrizAmbientalMineria();
                		break;
                	}
                }
                
                if(impPersist.getId() == null){
                	MatrizAmbientalMineria imp = new MatrizAmbientalMineria();
                    imp.setActividadMinera(a);
                    imp.setFichaAmbientalMineria(fichaAmbientalMineria);
                    impPersist = crudServiceBean.saveOrUpdate(imp);
                } 
                
                for (MatrizFactorImpacto mfi : listaH) {
                    mfi.setMatrizAmbientalMineria(impPersist);
                    crudServiceBean.saveOrUpdate(mfi);
                }
            }
            
            fichaAmbientalMineriaFacade.guardarFicha(fichaAmbientalMineria);            
            
            if (listaImpactoEliminados != null && listaImpactoEliminados.size() > 0) {
                crudServiceBean.delete(listaImpactoEliminados);
            }
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    
    public void guardarMatrizFactorImpacto(List<MatrizFactorImpacto> listaMatriz) {
        crudServiceBean.saveOrUpdate(listaMatriz);
    }
    
}
