/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CoordenadaGeneral;
import ec.gob.ambiente.suia.domain.FichaAmbientalMineria;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.EjecutarSentenciasNativas;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.lang.SerializationUtils;

/**
 *
 * @author christian
 */
@LocalBean
@Stateless
public class CaracteristicasAreaInfluenciaMineriaFacade {

    @EJB
    private CrudServiceBean crudServiceBean;
    @EJB
    private EjecutarSentenciasNativas ejecutarSentenciasNativas;

    public void guardar(final FichaAmbientalMineria fichaAmbientalMineria, List<CoordenadaGeneral> listaCoordenadaGeneral) throws ServiceException {
        try {
            FichaAmbientalMineria ficha = crudServiceBean.saveOrUpdate(fichaAmbientalMineria);
            StringBuilder sql = new StringBuilder();
            sql.append(CoordenadaGeneral.SQL_ACTUALIZA).append(" SET geco_status = false").append(" WHERE geco_table_id = ").append(ficha.getId()).append(" AND geco_table_class = '").append(ficha.getClass().getSimpleName()).append("'");
            ejecutarSentenciasNativas.ejecutarSentenciasNativasDml(sql.toString());
            for (CoordenadaGeneral c : listaCoordenadaGeneral) {
                c.setIdTable(ficha.getId());
                c.setNombreTabla(ficha.getClass().getSimpleName());
            }
            crudServiceBean.saveOrUpdate(listaCoordenadaGeneral);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        } catch (SQLException e) {
            throw new ServiceException(e);
        }
    }

    public void guardar(final FichaAmbientalMineria fichaAmbientalMineria) throws ServiceException {
        try {
            crudServiceBean.saveOrUpdate(fichaAmbientalMineria);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    
    //Cris F: cambio del método de guardar
    @EJB
    private FichaAmbientalMineriaFacade fichaAmbientalMineriaFacade;
    public void guardarHistorial(final FichaAmbientalMineria fichaAmbientalMineria) throws ServiceException {
        try {
        	
        	FichaAmbientalMineria fichaAmbientalMineriaBdd = fichaAmbientalMineriaFacade.obtenerPorId(fichaAmbientalMineria.getId());
        	
        	if(fichaAmbientalMineriaBdd.getAreaInfluencia() != null){
        		if(!compararParaHistorial(fichaAmbientalMineria, fichaAmbientalMineriaBdd)){
        			FichaAmbientalMineria fichaHistorial = (FichaAmbientalMineria)SerializationUtils.clone(fichaAmbientalMineriaBdd);
            		fichaHistorial.setId(null);
            		fichaHistorial.setFechaHistorico(new Date());
            		fichaHistorial.setIdRegistroOriginal(fichaAmbientalMineriaBdd.getId());
            		crudServiceBean.saveOrUpdate(fichaHistorial);
        		}
        	}
        	
            crudServiceBean.saveOrUpdate(fichaAmbientalMineria);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    
    private boolean compararParaHistorial(FichaAmbientalMineria fichaAmbientalMineria, FichaAmbientalMineria fichaAmbientalMineriaBdd){
    	try {
    		
    		if(((fichaAmbientalMineria.getAreaInfluencia() == null && fichaAmbientalMineriaBdd.getAreaInfluencia() == null) ||
    				(fichaAmbientalMineria.getAreaInfluencia() != null && fichaAmbientalMineriaBdd.getAreaInfluencia() != null && 
    				fichaAmbientalMineria.getAreaInfluencia().equals(fichaAmbientalMineriaBdd.getAreaInfluencia()))) && 
    			((fichaAmbientalMineria.getPendienteSuelo() == null && fichaAmbientalMineriaBdd.getPendienteSuelo() == null) ||
    				(fichaAmbientalMineria.getPendienteSuelo() != null && fichaAmbientalMineriaBdd.getPendienteSuelo() != null && 
    				fichaAmbientalMineria.getPendienteSuelo().equals(fichaAmbientalMineriaBdd.getPendienteSuelo()))) && 
    			((fichaAmbientalMineria.getTipoSuelo() == null && fichaAmbientalMineriaBdd.getTipoSuelo() == null) || 
    				(fichaAmbientalMineria.getTipoSuelo() != null && fichaAmbientalMineriaBdd.getTipoSuelo() != null && 
    				fichaAmbientalMineria.getTipoSuelo().equals(fichaAmbientalMineriaBdd.getTipoSuelo()))) && 
    			((fichaAmbientalMineria.getCalidadSuelo() == null && fichaAmbientalMineriaBdd.getCalidadSuelo() == null) || 
    				(fichaAmbientalMineria.getCalidadSuelo() != null && fichaAmbientalMineriaBdd.getCalidadSuelo() != null &&
    				fichaAmbientalMineria.getCalidadSuelo().equals(fichaAmbientalMineriaBdd.getCalidadSuelo()))) && 
    			((fichaAmbientalMineria.getPermeabilidadSuelo() == null && fichaAmbientalMineriaBdd.getPermeabilidadSuelo() == null) ||
    				(fichaAmbientalMineria.getPermeabilidadSuelo() != null && fichaAmbientalMineriaBdd.getPermeabilidadSuelo() != null &&
    				fichaAmbientalMineria.getPermeabilidadSuelo().equals(fichaAmbientalMineriaBdd.getPermeabilidadSuelo()))) && 
    			((fichaAmbientalMineria.getCondicionesDrenaje() == null && fichaAmbientalMineriaBdd.getCondicionesDrenaje() == null) ||
    				(fichaAmbientalMineria.getCondicionesDrenaje() != null && fichaAmbientalMineriaBdd.getCondicionesDrenaje() != null && 
    				fichaAmbientalMineria.getCondicionesDrenaje().equals(fichaAmbientalMineriaBdd.getCondicionesDrenaje()))) && 
    			((fichaAmbientalMineria.getRecursosHidricos() == null && fichaAmbientalMineriaBdd.getRecursosHidricos() == null) ||
    				(fichaAmbientalMineria.getRecursosHidricos() != null && fichaAmbientalMineriaBdd.getRecursosHidricos() != null && 
    				fichaAmbientalMineria.getRecursosHidricos().equals(fichaAmbientalMineriaBdd.getRecursosHidricos()))) && 
    			((fichaAmbientalMineria.getNivelFreatico() == null && fichaAmbientalMineriaBdd.getNivelFreatico() == null) ||
    				(fichaAmbientalMineria.getNivelFreatico() != null && fichaAmbientalMineriaBdd.getNivelFreatico() != null && 
    				fichaAmbientalMineria.getNivelFreatico().equals(fichaAmbientalMineriaBdd.getNivelFreatico()))) && 
    			((fichaAmbientalMineria.getPrecipitacionesAgua() == null && fichaAmbientalMineriaBdd.getPrecipitacionesAgua() == null) ||
    				(fichaAmbientalMineria.getPrecipitacionesAgua() != null && fichaAmbientalMineriaBdd.getPrecipitacionesAgua() != null && 
    				fichaAmbientalMineria.getPrecipitacionesAgua().equals(fichaAmbientalMineriaBdd.getPrecipitacionesAgua()))) &&
    			((fichaAmbientalMineria.getCaracteristicasAgua() == null && fichaAmbientalMineriaBdd.getCaracteristicasAgua() == null) || 
    				(fichaAmbientalMineria.getCaracteristicasAgua() != null && fichaAmbientalMineriaBdd.getCaracteristicasAgua() != null && 
    				fichaAmbientalMineria.getCaracteristicasAgua().equals(fichaAmbientalMineriaBdd.getCaracteristicasAgua()))) && 
    			((fichaAmbientalMineria.getCaracteristicasAire() == null && fichaAmbientalMineriaBdd.getCaracteristicasAire() == null) ||
    				(fichaAmbientalMineria.getCaracteristicasAire() != null && fichaAmbientalMineriaBdd.getCaracteristicasAire() != null && 
    				fichaAmbientalMineria.getCaracteristicasAire().equals(fichaAmbientalMineriaBdd.getCaracteristicasAire()))) && 
    			((fichaAmbientalMineria.getIdRecirculacionAire() == null && fichaAmbientalMineriaBdd.getIdRecirculacionAire() == null) ||
    				(fichaAmbientalMineria.getIdRecirculacionAire() != null && fichaAmbientalMineriaBdd.getRecirculacionAire() != null && 
    				fichaAmbientalMineria.getIdRecirculacionAire().equals(fichaAmbientalMineriaBdd.getIdRecirculacionAire()))) && 
    			((fichaAmbientalMineria.getRuido() == null && fichaAmbientalMineriaBdd.getRuido() == null) ||
    				(fichaAmbientalMineria.getRuido() != null && fichaAmbientalMineriaBdd.getRuido() != null && 
    				fichaAmbientalMineria.getRuido().equals(fichaAmbientalMineriaBdd.getRuido()))) &&     			
    			((fichaAmbientalMineria.getClima() == null && fichaAmbientalMineriaBdd.getClima() == null) || 
    				(fichaAmbientalMineria.getClima() != null && fichaAmbientalMineriaBdd.getClima() != null && 
    				fichaAmbientalMineria.getClima().equals(fichaAmbientalMineriaBdd.getClima()))) && 
    			((fichaAmbientalMineria.getDescripcionFuentesContaminacionAgua() == null && fichaAmbientalMineriaBdd.getDescripcionFuentesContaminacionAgua() == null) ||
    				(fichaAmbientalMineria.getDescripcionFuentesContaminacionAgua() != null & fichaAmbientalMineriaBdd.getDescripcionFuentesContaminacionAgua() != null && 
    				fichaAmbientalMineria.getDescripcionFuentesContaminacionAgua().equals(fichaAmbientalMineriaBdd.getDescripcionFuentesContaminacionAgua()))) && 
    			((fichaAmbientalMineria.getDescripcionFuentesRuido() == null && fichaAmbientalMineriaBdd.getDescripcionFuentesRuido() == null) ||
    				(fichaAmbientalMineria.getDescripcionFuentesRuido() != null && fichaAmbientalMineriaBdd.getDescripcionFuentesRuido() != null && 
    				fichaAmbientalMineria.getDescripcionFuentesRuido().equals(fichaAmbientalMineriaBdd.getDescripcionFuentesRuido()))) && 
    			((fichaAmbientalMineria.getOtrosAreaInfluencia() == null && fichaAmbientalMineriaBdd.getOtrosAreaInfluencia() == null) ||
    				(fichaAmbientalMineria.getOtrosAreaInfluencia() != null && fichaAmbientalMineriaBdd.getOtrosAreaInfluencia() != null &&
    				fichaAmbientalMineria.getOtrosAreaInfluencia().equals(fichaAmbientalMineriaBdd.getOtrosAreaInfluencia()))) && 
    			((fichaAmbientalMineria.getOtrosCalidadSuelo() == null && fichaAmbientalMineriaBdd.getOtrosCalidadSuelo() == null) ||
    				(fichaAmbientalMineria.getOtrosCalidadSuelo() != null && fichaAmbientalMineriaBdd.getOtrosCalidadSuelo() != null && 
    				fichaAmbientalMineria.getOtrosCalidadSuelo().equals(fichaAmbientalMineriaBdd.getOtrosCalidadSuelo())))
    				){
    			return true;
    		}else{
    			return false;
    		}
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
    }

}
