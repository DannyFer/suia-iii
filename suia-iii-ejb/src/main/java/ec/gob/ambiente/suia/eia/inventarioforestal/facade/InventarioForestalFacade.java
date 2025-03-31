/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.eia.inventarioforestal.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.domain.CoordenadaGeneral;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.InventarioForestal;
import ec.gob.ambiente.suia.eia.inventarioforestal.service.InventarioForestalService;
import ec.gob.ambiente.suia.exceptions.ServiceException;

/**
 * @author ishmael
 */
@Stateless
public class InventarioForestalFacade {

    @EJB
    private InventarioForestalService inventarioForestalService;

    @SuppressWarnings("rawtypes")
    public void guardar(InventarioForestal inventarioForestal,
                        List<CoordenadaGeneral> listaCoordenada, List listaRemover)
            throws ServiceException {
        inventarioForestalService.guardar(inventarioForestal, listaCoordenada,
                listaRemover);
    }

    public void guardar(InventarioForestal inventarioForestal)
            throws ServiceException {
        inventarioForestalService.guardar(inventarioForestal);
    }

    public InventarioForestal obtenetInventarioForestal(
            EstudioImpactoAmbiental estudioImpactoAmbiental)
            throws ServiceException {
        return inventarioForestalService
                .obtenetInventarioForestal(estudioImpactoAmbiental);
    }

    public List<CoordenadaGeneral> listarCoordenadaGeneral(String nombreTabla,
                                                           Integer idTabla) throws ServiceException {
        return inventarioForestalService.listarCoordenadaGeneral(nombreTabla,
                idTabla);
    }

    public Double obtenerMontoTotalValoracion(
            EstudioImpactoAmbiental estudioImpactoAmbiental)
            throws ServiceException {
        InventarioForestal inventario = obtenetInventarioForestal(estudioImpactoAmbiental);
        if (inventario == null || inventario.getCapturaDeCarbono() == null || inventario.getTurismoBellezaEscenica() == null ||
                inventario.getRecursoAgua() == null || inventario.getProductosForestalesMaderablesYNoMaderables() == null ||
                inventario.getPlantasMedicinales() == null || inventario.getPlantasOrnamentales()
                == null || inventario.getArtesanias() == null) {
            return 0.0d;
        } else {
            Double montoTotalValoracion = inventario.getCapturaDeCarbono()
                    + inventario.getTurismoBellezaEscenica()
                    + inventario.getRecursoAgua()
                    + inventario.getProductosForestalesMaderablesYNoMaderables()
                    + inventario.getPlantasMedicinales()
                    + inventario.getPlantasOrnamentales()
                    + inventario.getArtesanias();
            return montoTotalValoracion;
        }
    }
    
    /**
     * CF: Historico
     */
    
    @SuppressWarnings("rawtypes")
    public void guardarHistorico(InventarioForestal inventarioForestal,
                        List<CoordenadaGeneral> listaCoordenada, List listaRemover, Integer numeroNotificacion)
            throws ServiceException {
        inventarioForestalService.guardarHistorico(inventarioForestal, listaCoordenada,
                listaRemover, numeroNotificacion);
    }

    public void guardarHistorico(InventarioForestal inventarioForestal)
            throws ServiceException {
        inventarioForestalService.guardarHistorico(inventarioForestal);
    }
    
	public void guardarNuevoHistorico(InventarioForestal inventarioForestal,
			List<CoordenadaGeneral> listaCoordenada, Integer numeroNotificacion) throws ServiceException {
		inventarioForestalService.guardarNuevoHistorico(inventarioForestal,
				listaCoordenada, numeroNotificacion);
	}
    
    
    /**
     * Consulta el inventario original
     * @author mariela.guano
     * @param idInventario
     * @param numeroNotificacion
     * @return
     * @throws ServiceException
     */
    public List<InventarioForestal> obtenerInventarioOriginal(Integer idInventario, Integer numeroNotificacion)
            throws ServiceException {
        return inventarioForestalService.obtenerHistorial(idInventario, numeroNotificacion);
    }
    
    public InventarioForestal obtenerInventarioForestalEnBdd(EstudioImpactoAmbiental estudioImpactoAmbiental) throws ServiceException {
        return inventarioForestalService.obtenerInventarioForestalEnBdd(estudioImpactoAmbiental);
    }
    
	public List<CoordenadaGeneral> listarCoordenadaGeneralPuntoEliminado(String nombreTabla, Integer idTabla) throws ServiceException {
		return inventarioForestalService.listarCoordenadaGeneralEnBdd(nombreTabla, idTabla);
	}
}
