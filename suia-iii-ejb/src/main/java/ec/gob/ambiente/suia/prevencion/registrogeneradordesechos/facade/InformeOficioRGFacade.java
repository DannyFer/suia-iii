/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.GeneradorDesechosPeligrosos;
import ec.gob.ambiente.suia.domain.InformeTecnicoRegistroGeneradorDesechos;
import ec.gob.ambiente.suia.domain.OficioEmisionRegistroGeneradorDesechos;
import ec.gob.ambiente.suia.domain.OficioObservacionRegistroGeneradorDesechos;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.utils.Constantes;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 16/03/2015]
 *          </p>
 */
@Stateless
public class InformeOficioRGFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private SecuenciasFacade secuenciasFacade;

	public String generarCodigoInforme(GeneradorDesechosPeligrosos generadorDesechosPeligrosos) {
		try {
			return Constantes.SIGLAS_INSTITUCION + "-"
					+ secuenciasFacade.getCurrentYear()
					+ "-"
					+ generadorDesechosPeligrosos.getAreaResponsable().getAreaAbbreviation()
					+ "-"
					+ secuenciasFacade.getSecuenciaParaInformeTecnicoAreaResponsable(generadorDesechosPeligrosos
							.getAreaResponsable());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public String generarCodigoOficioEmisionActualizacion(GeneradorDesechosPeligrosos generadorDesechosPeligrosos) {
		try {
			Area areaResponsable = new Area();
			if (generadorDesechosPeligrosos.getAreaResponsable().getArea() == null) {
				areaResponsable = generadorDesechosPeligrosos.getAreaResponsable();
			} else {
				areaResponsable = generadorDesechosPeligrosos.getAreaResponsable().getArea();
			}
			return Constantes.SIGLAS_INSTITUCION + "-"
				+ secuenciasFacade.getCurrentYear()
				+ "-"
				+ generadorDesechosPeligrosos.getAreaResponsable().getAreaAbbreviation()
				+ "-"
				+ secuenciasFacade.getSecuenciaParaOficioAreaResponsable(areaResponsable);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public String generarCodigoOficioObservacion(GeneradorDesechosPeligrosos generadorDesechosPeligrosos) {
		try {
			return Constantes.SIGLAS_INSTITUCION + "-"
					+ secuenciasFacade.getCurrentYear()
					+ "-"
					+ generadorDesechosPeligrosos.getAreaResponsable().getAreaAbbreviation()
					+ "-"
					+ secuenciasFacade.getSecuenciaParaOficioAreaResponsable(generadorDesechosPeligrosos.getAreaResponsable());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public void guardarInforme(InformeTecnicoRegistroGeneradorDesechos informe, GeneradorDesechosPeligrosos generador,
			Usuario usuario) {
		informe.setGeneradorDesechosPeligrosos(generador);
		informe.setEvaluador(usuario);
		TipoDocumento tipoDocumento = new TipoDocumento();
		tipoDocumento.setId(TipoDocumentoSistema.TIPO_INFORME_TECNICO_RGD.getIdTipoDocumento());
		informe.setTipoDocumento(tipoDocumento);
		crudServiceBean.saveOrUpdate(informe);
	}

	public void guardarOficioEmision(OficioEmisionRegistroGeneradorDesechos oficio,
			GeneradorDesechosPeligrosos generador, Usuario usuario) {
		oficio.setGeneradorDesechosPeligrosos(generador);
		oficio.setEvaluador(usuario);
		TipoDocumento tipoDocumento = new TipoDocumento();
		tipoDocumento.setId(TipoDocumentoSistema.TIPO_OFICIO_EMISION_RGD.getIdTipoDocumento());
		oficio.setTipoDocumento(tipoDocumento);
		crudServiceBean.saveOrUpdate(oficio);
	}

	public void guardarOficioObservacion(OficioObservacionRegistroGeneradorDesechos oficio,
			GeneradorDesechosPeligrosos generador, Usuario usuario) {
		oficio.setGeneradorDesechosPeligrosos(generador);
		oficio.setEvaluador(usuario);
		TipoDocumento tipoDocumento = new TipoDocumento();
		tipoDocumento.setId(TipoDocumentoSistema.TIPO_OFICIO_OBSERVACION_RGD.getIdTipoDocumento());
		oficio.setTipoDocumento(tipoDocumento);
		crudServiceBean.saveOrUpdate(oficio);
	}

	public PlantillaReporte getPlantillaReporte(TipoDocumentoSistema tipoDocumento) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("p_tipoDocumentoId", tipoDocumento.getIdTipoDocumento());
		return crudServiceBean.findByNamedQuerySingleResult(PlantillaReporte.OBTENER_PLANTILLA_POR_INFORME, parameters);
	}

	public InformeTecnicoRegistroGeneradorDesechos getInforme(GeneradorDesechosPeligrosos generador,
			long idInstanciaProceso, int contadorBandejaTecnico) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idGeneradorDesechosPeligrosos", generador.getId());
		parameters.put("idTipoDocumento", TipoDocumentoSistema.TIPO_INFORME_TECNICO_RGD.getIdTipoDocumento());
		parameters.put("idInstanciaProceso", idInstanciaProceso);
		parameters.put("contadorBandejaTecnico", contadorBandejaTecnico);
		return crudServiceBean.findByNamedQuerySingleResult(
				InformeTecnicoRegistroGeneradorDesechos.GET_BY_REGISTRO_GENERADOR, parameters);
	}

	public OficioEmisionRegistroGeneradorDesechos getOficioEmisionActualizacion(GeneradorDesechosPeligrosos generador,
			long idInstanciaProceso, int contadorBandejaTecnico) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idGeneradorDesechosPeligrosos", generador.getId());
		parameters.put("idTipoDocumento", TipoDocumentoSistema.TIPO_OFICIO_EMISION_RGD.getIdTipoDocumento());
		parameters.put("idInstanciaProceso", idInstanciaProceso);
		parameters.put("contadorBandejaTecnico", contadorBandejaTecnico);
		return crudServiceBean.findByNamedQuerySingleResult(
				OficioEmisionRegistroGeneradorDesechos.GET_BY_REGISTRO_GENERADOR, parameters);
	}

	public OficioObservacionRegistroGeneradorDesechos getOficioObservacion(GeneradorDesechosPeligrosos generador,
			long idInstanciaProceso, int contadorBandejaTecnico) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idGeneradorDesechosPeligrosos", generador.getId());
		parameters.put("idTipoDocumento", TipoDocumentoSistema.TIPO_OFICIO_OBSERVACION_RGD.getIdTipoDocumento());
		parameters.put("idInstanciaProceso", idInstanciaProceso);
		parameters.put("contadorBandejaTecnico", contadorBandejaTecnico);
		return crudServiceBean.findByNamedQuerySingleResult(
				OficioObservacionRegistroGeneradorDesechos.GET_BY_REGISTRO_GENERADOR, parameters);
	}
	
	public void guardarInforme(InformeTecnicoRegistroGeneradorDesechos informe) {
		crudServiceBean.saveOrUpdate(informe);
	}
	
	public void guardarOficioEmision(OficioEmisionRegistroGeneradorDesechos oficio) {
		crudServiceBean.saveOrUpdate(oficio);
	}

	public void guardarOficioObservacion(OficioObservacionRegistroGeneradorDesechos oficio) {
		crudServiceBean.saveOrUpdate(oficio);
	}
}
