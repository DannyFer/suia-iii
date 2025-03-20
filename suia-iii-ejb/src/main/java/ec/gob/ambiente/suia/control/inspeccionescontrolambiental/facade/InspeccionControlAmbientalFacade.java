/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.control.inspeccionescontrolambiental.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.SolicitudInspeccionControlAmbiental;
import ec.gob.ambiente.suia.domain.SolicitudInspeccionRespuestaAclaratoria;
import ec.gob.ambiente.suia.domain.TipoArea;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.dto.ProyectoCustom;
import ec.gob.ambiente.suia.entidadResponsable.facade.EntidadResponsableFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.Constantes;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author carlos.pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: carlos.pupo, Fecha: 30/10/2015]
 *          </p>
 */
@Stateless
public class InspeccionControlAmbientalFacade {

	public static final String CODE_PREFIX = "SUIA-";

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private SecuenciasFacade secuenciasFacade;

	@EJB
	private DocumentosFacade documentosFacade;

	@EJB
	private AreaFacade areaFacade;

	@EJB
	private EntidadResponsableFacade entidadResponsableFacade;

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	public String generarNumeroSolicitud() {
		try {
			return Constantes.SIGLAS_INSTITUCION + "-SOL-ICA-" + secuenciasFacade.getCurrentYear() + "-"
					+ secuenciasFacade.getNextValueDedicateSequence("SOLICITUD_INSPECCION_CONTROL_AMBIENTAL");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public void guardar(SolicitudInspeccionControlAmbiental solicitudInspeccionControlAmbiental) {
		crudServiceBean.saveOrUpdate(solicitudInspeccionControlAmbiental);
	}

	public SolicitudInspeccionControlAmbiental get(Integer id) throws Exception {
		return crudServiceBean.find(SolicitudInspeccionControlAmbiental.class, id);
	}

	public SolicitudInspeccionControlAmbiental get(String solicitud) throws Exception {
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("solicitud", solicitud);
			SolicitudInspeccionControlAmbiental solicitudInspeccionControlAmbiental = crudServiceBean
					.findByNamedQuerySingleResult(SolicitudInspeccionControlAmbiental.GET_BY_SOLICITUD, parameters);
			if (solicitudInspeccionControlAmbiental != null) {
				solicitudInspeccionControlAmbiental.getAreaResponsable().getAreaAbbreviation();
			}
			return solicitudInspeccionControlAmbiental;
		} catch (Exception e) {
			throw new Exception("No se puede cargar la solicitud de inspeccion de control ambiental.", e);
		}
	}

	public SolicitudInspeccionControlAmbiental cargarSolicitudFullPorId(Integer id) throws Exception {
		SolicitudInspeccionControlAmbiental solicitud = get(id);
		if (solicitud.getRespuestasAclaratorias() != null) {
			for (SolicitudInspeccionRespuestaAclaratoria respuesta : solicitud.getRespuestasAclaratorias()) {
				respuesta.getDocumento();
			}
		}
		return solicitud;
	}

	public SolicitudInspeccionControlAmbiental cargarSolicitudParaDocumentoPorId(Integer id) throws Exception {
		SolicitudInspeccionControlAmbiental solicitud = get(id);
		return solicitud;
	}

	public SolicitudInspeccionControlAmbiental iniciarProceso(Usuario usuario, Usuario tecnico,
			ProyectoCustom proyectoCustom, Class<?> tramiteResolver, String motivoSolicitud)
					throws ServiceException, JbpmException {
		SolicitudInspeccionControlAmbiental solicitudInspeccionControlAmbiental = new SolicitudInspeccionControlAmbiental();

		ProyectoLicenciamientoAmbiental proyecto = proyectoLicenciamientoAmbientalFacade
				.buscarProyectosLicenciamientoAmbientalPorId(Integer.parseInt(proyectoCustom.getId()));
		solicitudInspeccionControlAmbiental.setProyecto(proyecto);
		solicitudInspeccionControlAmbiental.setUsuario(usuario);
		solicitudInspeccionControlAmbiental.setTecnicoPlanificado(tecnico);
//		solicitudInspeccionControlAmbiental.setAreaResponsable(usuario.getArea());
		solicitudInspeccionControlAmbiental.setAreaResponsable(proyecto.getAreaResponsable());
		solicitudInspeccionControlAmbiental.setDescripcion(motivoSolicitud);

		iniciarProceso(solicitudInspeccionControlAmbiental, tramiteResolver);

		return solicitudInspeccionControlAmbiental;
	}

	public void iniciarProceso(SolicitudInspeccionControlAmbiental solicitudInspeccionControlAmbiental,
			Class<?> tramiteResolver) throws ServiceException, JbpmException {
		String numeroSolicitud = generarNumeroSolicitud();

		solicitudInspeccionControlAmbiental.setSolicitud(numeroSolicitud);

		guardar(solicitudInspeccionControlAmbiental);

		boolean plantaCentral = solicitudInspeccionControlAmbiental.getAreaResponsable().getTipoArea().getSiglas()
				.equals(Constantes.getRoleAreaName("area.plantacentral"));

		Usuario subsecretaria = null;
		Usuario director = null;
		Usuario coordinador = solicitudInspeccionControlAmbiental.getUsuario();

		subsecretaria = areaFacade.getSubsecretariaCalidadAmbiental();
		if (plantaCentral)
			director = areaFacade.getDirectorPlantaCentral();
		else {
			if (!solicitudInspeccionControlAmbiental.getAreaResponsable().getTipoArea().getId()
					.equals(TipoArea.TIPO_AREA_ENTE_ACREDITADO))
				director = areaFacade.getDirectorProvincial(solicitudInspeccionControlAmbiental.getAreaResponsable());
			else
				director = areaFacade.getDirectorPlantaCentral();
		}

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("sujetoControl", solicitudInspeccionControlAmbiental.getProyecto().getUsuario().getNombre());
		params.put("subsecretaria", subsecretaria.getNombre());
		params.put("director", director.getNombre());
		params.put("coordinador", coordinador.getNombre());
		if (solicitudInspeccionControlAmbiental.getTecnicoPlanificado() != null)
		    params.put("tecnico", solicitudInspeccionControlAmbiental.getTecnicoPlanificado().getNombre());
		params.put(Constantes.ID_PROYECTO, solicitudInspeccionControlAmbiental.getProyecto().getId());
		params.put(Constantes.CODIGO_PROYECTO, solicitudInspeccionControlAmbiental.getProyecto().getCodigo());
		params.put(Constantes.USUARIO_VISTA_MIS_PROCESOS, coordinador.getNombre());
		params.put("esDireccionProvincial", !plantaCentral);
		params.put("esInspeccionCronograma",
				solicitudInspeccionControlAmbiental.getCronogramaAnualInspeccionesControlAmbiental() != null);
		params.put(SolicitudInspeccionControlAmbiental.VARIABLE_ID_SOLICITUD,
				solicitudInspeccionControlAmbiental.getId());
		params.put(SolicitudInspeccionControlAmbiental.VARIABLE_NUMERO_SOLICITUD,
				solicitudInspeccionControlAmbiental.getSolicitud());
		params.put(Constantes.VARIABLE_PROCESO_TRAMITE_RESOLVER, tramiteResolver.getName());

		procesoFacade.iniciarProceso(coordinador, Constantes.NOMBRE_PROCESO_INSPECCIONES_CONTROL_AMBIENTAL,
				solicitudInspeccionControlAmbiental.getProyecto().getCodigo(), params);
	}

	public SolicitudInspeccionRespuestaAclaratoria getRespuesta(SolicitudInspeccionControlAmbiental solicitud,
			int vecesBandejaTecnico) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("idSolicitud", solicitud.getId());
		params.put("bandejaTecnico", vecesBandejaTecnico);
		return crudServiceBean.findByNamedQuerySingleResult(
				SolicitudInspeccionRespuestaAclaratoria.GET_BY_SOLICITUD_VECES_BANDEJA, params);
	}

	@SuppressWarnings("unchecked")
	public List<SolicitudInspeccionRespuestaAclaratoria> getRespuestasPorSolicitud(
			SolicitudInspeccionControlAmbiental solicitud) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("idSolicitud", solicitud.getId());
		return (List<SolicitudInspeccionRespuestaAclaratoria>) crudServiceBean
				.findByNamedQuery(SolicitudInspeccionRespuestaAclaratoria.GET_BY_SOLICITUD, params);
	}
}
