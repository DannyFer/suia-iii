/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.control.inspeccionescontrolambiental.facade;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.InformeTecnicoInspeccionesControlAmbiental;
import ec.gob.ambiente.suia.domain.OficioObservacionInspeccionesControlAmbiental;
import ec.gob.ambiente.suia.domain.OficioPronunciamientoInspeccionesControlAmbiental;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.SolicitudInspeccionControlAmbiental;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
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
public class InformeOficioICAFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private SecuenciasFacade secuenciasFacade;

	public String generarCodigoInforme(SolicitudInspeccionControlAmbiental solicitudInspeccionControlAmbiental) {
		try {
			return Constantes.SIGLAS_INSTITUCION + "-" + secuenciasFacade.getCurrentYear() + "-"
					+ solicitudInspeccionControlAmbiental.getAreaResponsable().getAreaAbbreviation() + "-"
					+ secuenciasFacade.getSecuenciaParaInformeTecnicoAreaResponsable(
							solicitudInspeccionControlAmbiental.getAreaResponsable());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public String generarCodigoOficioPronunciamiento(
			SolicitudInspeccionControlAmbiental solicitudInspeccionControlAmbiental) {
		try {
			return Constantes.SIGLAS_INSTITUCION + "-" + secuenciasFacade.getCurrentYear() + "-"
					+ solicitudInspeccionControlAmbiental.getAreaResponsable().getAreaAbbreviation() + "-"
					+ secuenciasFacade.getSecuenciaParaOficioAreaResponsable(
							solicitudInspeccionControlAmbiental.getAreaResponsable());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public String generarCodigoOficioObservacion(
			SolicitudInspeccionControlAmbiental solicitudInspeccionControlAmbiental) {
		try {
			return Constantes.SIGLAS_INSTITUCION + "-" + secuenciasFacade.getCurrentYear() + "-"
					+ solicitudInspeccionControlAmbiental.getAreaResponsable().getAreaAbbreviation() + "-"
					+ secuenciasFacade.getSecuenciaParaOficioAreaResponsable(
							solicitudInspeccionControlAmbiental.getAreaResponsable());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public void guardarInforme(InformeTecnicoInspeccionesControlAmbiental informe,
			SolicitudInspeccionControlAmbiental solicitud, Usuario usuario) {
		informe.setSolicitudInspeccionControlAmbiental(solicitud);
		informe.setEvaluador(usuario);
		TipoDocumento tipoDocumento = new TipoDocumento();
		tipoDocumento.setId(TipoDocumentoSistema.TIPO_INFORME_TECNICO_ICA.getIdTipoDocumento());
		informe.setTipoDocumento(tipoDocumento);
		crudServiceBean.saveOrUpdate(informe);
	}

	public void guardarOficioPronunciamiento(OficioPronunciamientoInspeccionesControlAmbiental oficio,
			SolicitudInspeccionControlAmbiental solicitud, Usuario usuario) {
		oficio.setSolicitudInspeccionControlAmbiental(solicitud);
		oficio.setEvaluador(usuario);
		TipoDocumento tipoDocumento = new TipoDocumento();
		tipoDocumento.setId(TipoDocumentoSistema.TIPO_OFICIO_PRONUNCIAMIENTO_ICA.getIdTipoDocumento());
		oficio.setTipoDocumento(tipoDocumento);
		crudServiceBean.saveOrUpdate(oficio);
	}

	public void guardarOficioObservacion(OficioObservacionInspeccionesControlAmbiental oficio,
			SolicitudInspeccionControlAmbiental solicitud, Usuario usuario) {
		oficio.setSolicitudInspeccionControlAmbiental(solicitud);
		oficio.setEvaluador(usuario);
		TipoDocumento tipoDocumento = new TipoDocumento();
		tipoDocumento.setId(TipoDocumentoSistema.TIPO_OFICIO_OBSERVACION_ICA.getIdTipoDocumento());
		oficio.setTipoDocumento(tipoDocumento);
		crudServiceBean.saveOrUpdate(oficio);
	}

	public PlantillaReporte getPlantillaReporte(TipoDocumentoSistema tipoDocumento) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("p_tipoDocumentoId", tipoDocumento.getIdTipoDocumento());
		return crudServiceBean.findByNamedQuerySingleResult(PlantillaReporte.OBTENER_PLANTILLA_POR_INFORME, parameters);
	}

	public InformeTecnicoInspeccionesControlAmbiental getInforme(SolicitudInspeccionControlAmbiental solicitud,
			long idInstanciaProceso, int contadorBandejaTecnico) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idSolicitudInspeccionControlAmbiental", solicitud.getId());
		parameters.put("idTipoDocumento", TipoDocumentoSistema.TIPO_INFORME_TECNICO_ICA.getIdTipoDocumento());
		parameters.put("idInstanciaProceso", idInstanciaProceso);
		parameters.put("contadorBandejaTecnico", contadorBandejaTecnico);
		return crudServiceBean.findByNamedQuerySingleResult(
				InformeTecnicoInspeccionesControlAmbiental.GET_BY_SOLICITUD_INSPECCION, parameters);
	}

	public OficioPronunciamientoInspeccionesControlAmbiental getOficioPronunciamiento(
			SolicitudInspeccionControlAmbiental solicitud, long idInstanciaProceso, int contadorBandejaTecnico) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idSolicitudInspeccionControlAmbiental", solicitud.getId());
		parameters.put("idTipoDocumento", TipoDocumentoSistema.TIPO_OFICIO_PRONUNCIAMIENTO_ICA.getIdTipoDocumento());
		parameters.put("idInstanciaProceso", idInstanciaProceso);
		parameters.put("contadorBandejaTecnico", contadorBandejaTecnico);
		return crudServiceBean.findByNamedQuerySingleResult(
				OficioPronunciamientoInspeccionesControlAmbiental.GET_BY_SOLICITUD_INSPECCION, parameters);
	}

	public OficioObservacionInspeccionesControlAmbiental getOficioObservacion(
			SolicitudInspeccionControlAmbiental solicitud, long idInstanciaProceso, int contadorBandejaTecnico) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idSolicitudInspeccionControlAmbiental", solicitud.getId());
		parameters.put("idTipoDocumento", TipoDocumentoSistema.TIPO_OFICIO_OBSERVACION_ICA.getIdTipoDocumento());
		parameters.put("idInstanciaProceso", idInstanciaProceso);
		parameters.put("contadorBandejaTecnico", contadorBandejaTecnico);
		return crudServiceBean.findByNamedQuerySingleResult(
				OficioObservacionInspeccionesControlAmbiental.GET_BY_SOLICITUD_INSPECCION, parameters);
	}
}
