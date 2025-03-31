/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.service;

import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.AprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicosModalidad;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.Constantes;

/**
 * <b> Clase que contiene los servicios del proceso aprobación de requisitos
 * técnicos. </b>
 * 
 * @author vero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: vero $, $Date: 11/06/2015 $]
 *          </p>
 */
@Stateless
public class AprobacionRequisitosTecnicosService {

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private ProcesoFacade proceso;

	@EJB
	private AreaFacade areaFacade;

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoFacade;

	/***
	 * 
	 * <b> Recupera el registro del proceso de aprobación requisitos técnicos .
	 * </b>
	 * <p>
	 * [Author: vero, Date: 24/06/2015]
	 * </p>
	 * 
	 * @param proyecto
	 * @return
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public AprobacionRequisitosTecnicos getAprobacionRequisitosTecnicos(String proyecto) throws ServiceException {
		List<AprobacionRequisitosTecnicos> lista = null;
		try {
			lista = crudServiceBean.getEntityManager()
					.createQuery("From AprobacionRequisitosTecnicos m where proyecto=:proy order by id desc")
					.setParameter("proy", proyecto).getResultList();
			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos = lista.get(0);
				aprobacionRequisitosTecnicos.getAprobacionModalidades().size();
				//
				aprobacionRequisitosTecnicos
						.setAprobacionModalidades(getAprobacionRequisitosTecnicosModalidad(aprobacionRequisitosTecnicos));
				//
				if (aprobacionRequisitosTecnicos.getAreaResponsable() != null) {
					aprobacionRequisitosTecnicos.getAreaResponsable().getAreaName();
				}
				return aprobacionRequisitosTecnicos;
			}
		} catch (Exception e) {
			throw new ServiceException("Ocurrió un problema al recuperar los datos", e);
		}

	}
	
	@SuppressWarnings("unchecked")
	public AprobacionRequisitosTecnicos findArtByProyectoLicenciaAmbiental(String proyecto) throws ServiceException {
		List<AprobacionRequisitosTecnicos> lista = null;
		try {
			lista = crudServiceBean.getEntityManager()
					.createQuery("From AprobacionRequisitosTecnicos m where proyecto=:proy")
					.setParameter("proy", proyecto).getResultList();
			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos = lista.get(0);
				return aprobacionRequisitosTecnicos;
			}
		} catch (Exception e) {
			throw new ServiceException("Ocurrió un problema al recuperar los datos", e);
		}

	}
	
	@SuppressWarnings("unchecked")
	public List<AprobacionRequisitosTecnicos> findArtByProyectoLicenciaAmbientalList(String proyecto) throws ServiceException {
		List<AprobacionRequisitosTecnicos> lista = null;
		try {
			lista = crudServiceBean.getEntityManager()
					.createQuery("From AprobacionRequisitosTecnicos m where proyecto=:proy")
					.setParameter("proy", proyecto).getResultList();
			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista;
			}
		} catch (Exception e) {
			throw new ServiceException("Ocurrió un problema al recuperar los datos", e);
		}

	}

	@SuppressWarnings("unchecked")
	public AprobacionRequisitosTecnicos getAprobacionRequisitosTecnicosPorSolicitud(String solicitud)
			throws ServiceException {
		List<AprobacionRequisitosTecnicos> lista = null;
		try {
			lista = crudServiceBean.getEntityManager()
					.createQuery("From AprobacionRequisitosTecnicos m where solicitud=:sol")
					.setParameter("sol", solicitud).getResultList();
			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos = lista.get(0);
				aprobacionRequisitosTecnicos.getAprobacionModalidades().size();
				//
				aprobacionRequisitosTecnicos
						.setAprobacionModalidades(getAprobacionRequisitosTecnicosModalidad(aprobacionRequisitosTecnicos));
				//
				if (aprobacionRequisitosTecnicos.getAreaResponsable() != null) {
					aprobacionRequisitosTecnicos.getAreaResponsable().getAreaName();
				}
				return aprobacionRequisitosTecnicos;
			}
		} catch (Exception e) {
			throw new ServiceException("Ocurrió un problema al recuperar los datos", e);
		}

	}
	
	@SuppressWarnings("unchecked")
	public AprobacionRequisitosTecnicos getAprobacionRequisitosTecnicosBySolicitud(String solicitud)
			throws ServiceException {
		List<AprobacionRequisitosTecnicos> lista = null;
		try {
			lista = crudServiceBean.getEntityManager()
					.createQuery("From AprobacionRequisitosTecnicos m where solicitud=:sol")
					.setParameter("sol", solicitud).getResultList();
			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos = lista.get(0);
				return aprobacionRequisitosTecnicos;
			}
		} catch (Exception e) {
			throw new ServiceException("Ocurrió un problema al recuperar los datos", e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<AprobacionRequisitosTecnicosModalidad> getAprobacionRequisitosTecnicosModalidad(
			AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) {
		return crudServiceBean
				.getEntityManager()
				.createQuery(
						"From AprobacionRequisitosTecnicosModalidad m where m.aprobacionRequisitosTecnicos=:aprobacion order by m.modalidad.id")
				.setParameter("aprobacion", aprobacionRequisitosTecnicos).getResultList();
	}

	public Area getAreaResponsable(long idProceso, Usuario usuario) throws ServiceException {
		try {
			Map<String, Object> parametros = proceso.recuperarVariablesProceso(usuario, idProceso);
			String area = (String) parametros.get(AprobacionRequisitosTecnicosFacade.NOMBRE_VARIABLE_AREA_RESPONSABLE);
			if (area == null || area.isEmpty()) {
				return null;
			} else {
				Integer codigoArea = Integer.valueOf(area);
				return areaFacade.getArea(codigoArea);
			}
		} catch (JbpmException e) {
			throw new ServiceException("Error al recuperar las variables del proceso", e);
		}

	}

	public ProyectoLicenciamientoAmbiental buscarProyecto(String codigoProyecto) throws Exception {
		ProyectoLicenciamientoAmbiental proy = proyectoFacade.getProyectoPorCodigo(codigoProyecto);
		if (proy != null) {
			proy.getPrimeraProvincia();
		}
		return proy;
	}

	public boolean isProcesoInicadoRegistroProyectoRequisitosPrevios(long idProceso, Usuario usuario)
			throws ServiceException {

		try {
			Map<String, Object> parametros = proceso.recuperarVariablesProceso(usuario, idProceso);
			String valor = (String) parametros
					.get(AprobacionRequisitosTecnicosFacade.NOMBRE_VARIABLE_PROCESO_INICIO_DESDE_REGISTRO);
			if (valor == null || valor.isEmpty()) {
				return false;
			} else {
				return Boolean.valueOf(valor);
			}
		} catch (JbpmException e) {
			throw new ServiceException("Error al recuperar las variables del proceso", e);
		}

	}

	public boolean isProcesoIniciadoVoluntariamente(long idProceso, Usuario usuario) throws ServiceException {
		return !isProcesoInicadoRegistroProyectoRequisitosPrevios(idProceso, usuario);
	}

	public String getRequestBodyWS(String rol, Area areaResponsable) {
		return "rol=" + rol + "&area=" + areaResponsable.getAreaName();
	}

	public Integer getTipoEstudio(long idProceso, Usuario usuario) throws ServiceException {
		try {
			Map<String, Object> parametros = proceso.recuperarVariablesProceso(usuario, idProceso);
			String valor = (String) parametros.get(AprobacionRequisitosTecnicosFacade.NOMBRE_VARIABLE_TIPO_ESTUDIO);
			if (valor == null || valor.isEmpty()) {
				return null;
			} else {
				return Integer.valueOf(valor);
			}

		} catch (JbpmException e) {
			throw new ServiceException("Error al recuperar las variables del proceso", e);
		}

	}

	public String getProyecto(long idProceso, Usuario usuario) throws ServiceException {
		try {
			Map<String, Object> parametros = proceso.recuperarVariablesProceso(usuario, idProceso);
			return (String) parametros.get(Constantes.CODIGO_PROYECTO);
		} catch (JbpmException e) {
			throw new ServiceException("Error al recuperar las variables del proceso", e);
		}

	}

	public String getNombreProyecto(long idProceso, Usuario usuario) throws ServiceException {
		try {
			Map<String, Object> parametros = proceso.recuperarVariablesProceso(usuario, idProceso);
			return (String) parametros.get(AprobacionRequisitosTecnicosFacade.NOMBRE_PROYECTO);
		} catch (JbpmException e) {
			throw new ServiceException("Error al recuperar las variables del proceso", e);
		}

	}
	
//	---WR
	@SuppressWarnings("unchecked")
	public AprobacionRequisitosTecnicos getAprobacionRequisitosTecnicosPorId(Integer id)
			throws ServiceException {
		List<AprobacionRequisitosTecnicos> lista = null;
		try {
			lista = crudServiceBean.getEntityManager()
					.createQuery("From AprobacionRequisitosTecnicos m where m.id=:id")
					.setParameter("id", id).getResultList();
			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos = lista.get(0);
				aprobacionRequisitosTecnicos.getAprobacionModalidades().size();
				//
				aprobacionRequisitosTecnicos
						.setAprobacionModalidades(getAprobacionRequisitosTecnicosModalidad(aprobacionRequisitosTecnicos));
				//
				if (aprobacionRequisitosTecnicos.getAreaResponsable() != null) {
					aprobacionRequisitosTecnicos.getAreaResponsable().getAreaName();
				}
				return aprobacionRequisitosTecnicos;
			}
		} catch (Exception e) {
			throw new ServiceException("Ocurrió un problema al recuperar los datos", e);
		}

	}
}
