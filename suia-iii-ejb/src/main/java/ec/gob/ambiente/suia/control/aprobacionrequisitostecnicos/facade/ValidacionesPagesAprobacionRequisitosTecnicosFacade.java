/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.DesechoEspecialRecoleccion;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;

/**
 * <b> Incluir aqui la descripcion de la clase. </b>
 * 
 * @author vero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: vero $, $Date: 11/09/2015 $]
 *          </p>
 */
@Stateless
public class ValidacionesPagesAprobacionRequisitosTecnicosFacade {

	@EJB
	private InformacionPatioManiobrasFacade informacionPatioManiobrasFacade;

	@EJB
	private AprobacionRequisitosTecnicosFacade aprobacionRequisitosTecnicosFacade;

	@EJB
	private RequisitosVehiculoFacade requisitosVehiculoFacade;

	@EJB
	private RequisitosConductorFacade requisitosConductorFacade;

	@EJB
	private ValidacionSeccionesFacade validacionSeccionesFacade;

	@EJB
	private DesechoPeligrosoTransporteFacade desechoPeligrosoTransportacionFacade;

	public boolean isNumVehiculosPatioDiferenteRequisitosVehiculoConductores(
			AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) throws ServiceException {
		if (informacionPatioManiobrasFacade.getInformacionPatioManiobra(aprobacionRequisitosTecnicos) != null) {
			/*Integer numeroVehiculosPatio = informacionPatioManiobrasFacade.getInformacionPatioManiobra(
					aprobacionRequisitosTecnicos).getNumeroVehiculos();
			if (!numeroVehiculosPatio.equals(requisitosVehiculoFacade
					.getNumeroVehiculosRegistrados(aprobacionRequisitosTecnicos))
					|| !numeroVehiculosPatio.equals(requisitosConductorFacade
							.getNumeroConductores(aprobacionRequisitosTecnicos))) {
				return true;
			}*/
			return false;
		} else {
			return false;
		}

	}

	private boolean isNumVehiculosPatioDiferenteRequisitosVehiculo(
			AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) throws ServiceException {
		Integer numeroVehiculosPatio = informacionPatioManiobrasFacade.getInformacionPatioManiobra(
				aprobacionRequisitosTecnicos).getNumeroVehiculos();
		if (!numeroVehiculosPatio.equals(requisitosVehiculoFacade
				.getNumeroVehiculosRegistrados(aprobacionRequisitosTecnicos))) {
			return true;
		}
		return false;

	}

	private boolean isNumVehiculosPatioDiferenteRequisitosConductores(
			AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) throws ServiceException {
		Integer numeroVehiculosPatio = informacionPatioManiobrasFacade.getInformacionPatioManiobra(
				aprobacionRequisitosTecnicos).getNumeroVehiculos();
		if (!numeroVehiculosPatio.equals(requisitosConductorFacade.getNumeroConductores(aprobacionRequisitosTecnicos))) {
			return true;
		}
		return false;

	}

	public void guardarComoPaginasIncompletasRequisitosVehiculoConductor(
			AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos)
			throws ServiceException {
		if (isNumVehiculosPatioDiferenteRequisitosVehiculo(aprobacionRequisitosTecnicos)) {
			if (requisitosVehiculoFacade.isPageRequitosVehiculoRequerida(aprobacionRequisitosTecnicos)) {
				requisitosVehiculoFacade.guardarPaginaComoInCompleta(aprobacionRequisitosTecnicos);
			}
		}
		if (isNumVehiculosPatioDiferenteRequisitosConductores(aprobacionRequisitosTecnicos)) {
			if (requisitosConductorFacade.isPageRequitosConductorRequerida(aprobacionRequisitosTecnicos)) {
				requisitosConductorFacade.guardarPaginaComoIncompleta(aprobacionRequisitosTecnicos);
			}
		}
	}

	public void guardarComoPaginasCompletasRequisitosVehiculoConductor(
			AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos, long idProceso, Usuario usuario)
			throws ServiceException {
		requisitosVehiculoFacade.guardarPaginaComoCompleta(aprobacionRequisitosTecnicos);
		requisitosConductorFacade.guardarPaginaComoCompleta(aprobacionRequisitosTecnicos);

	}

	public boolean isPageRecoleccionTransporteDesechosVisible(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos)
			throws ServiceException {
		List<DesechoEspecialRecoleccion> lista = desechoPeligrosoTransportacionFacade
				.listaDesechoEspecialRecoleccionPorProyecto(aprobacionRequisitosTecnicos.getId());
		return !lista.isEmpty();
	}

	public boolean isPageRequisitosVehiculoRequerida(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos)
			throws ServiceException {
		return requisitosVehiculoFacade.isPageRequitosVehiculoRequerida(aprobacionRequisitosTecnicos);
	}

	public boolean isPageRequisitosConductorRequerida(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos)
			throws ServiceException {
		return requisitosConductorFacade.isPageRequitosConductorRequerida(aprobacionRequisitosTecnicos);
	}
}
