/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.control.aprobacionpuntosmonitoreo.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.PuntoDescargaLiquida;
import ec.gob.ambiente.suia.domain.PuntoEmision;
import ec.gob.ambiente.suia.domain.PuntoInmision;
import ec.gob.ambiente.suia.domain.PuntoMonitoreo;
import ec.gob.ambiente.suia.domain.TipoCombustible;
import ec.gob.ambiente.suia.domain.TipoFuente;
import ec.gob.ambiente.suia.domain.HusoHorario;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 29/12/2014]
 *          </p>
 */
@Stateless
public class AprobacionPuntosMonitoreoFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	@SuppressWarnings("unchecked")
	public List<PuntoDescargaLiquida> getPuntosDescargaLiquida() {
		return (List<PuntoDescargaLiquida>) crudServiceBean.findAll(PuntoDescargaLiquida.class);
	}

	public List<PuntoDescargaLiquida> getPuntosDescargaLiquida(List<Integer> identificadores) {
		List<PuntoDescargaLiquida> resultado = new ArrayList<PuntoDescargaLiquida>();
		for (int i = 0; i < identificadores.size(); i++) {
			PuntoDescargaLiquida punto = getPuntoDescargaLiquida(identificadores.get(i));
			if (punto != null)
				resultado.add(punto);
		}
		return resultado;
	}

	@SuppressWarnings("unchecked")
	public List<PuntoEmision> getPuntosEmision() {
		return (List<PuntoEmision>) crudServiceBean.findAll(PuntoEmision.class);
	}

	public List<PuntoEmision> getPuntosEmision(List<Integer> identificadores) {
		List<PuntoEmision> resultado = new ArrayList<PuntoEmision>();
		for (int i = 0; i < identificadores.size(); i++) {
			PuntoEmision punto = getPuntoEmision(identificadores.get(i));
			if (punto != null)
				resultado.add(punto);
		}
		return resultado;
	}

	@SuppressWarnings("unchecked")
	public List<PuntoInmision> getPuntosInmision() {
		return (List<PuntoInmision>) crudServiceBean.findAll(PuntoInmision.class);
	}

	public List<PuntoInmision> getPuntosInmision(List<Integer> identificadores) {
		List<PuntoInmision> resultado = new ArrayList<PuntoInmision>();
		for (int i = 0; i < identificadores.size(); i++) {
			PuntoInmision punto = getPuntoInmision(identificadores.get(i));
			if (punto != null)
				resultado.add(punto);
		}
		return resultado;
	}

	@SuppressWarnings("unchecked")
	public List<HusoHorario> getHusosHorario() {
		return (List<HusoHorario>) crudServiceBean.findAll(HusoHorario.class);
	}

	@SuppressWarnings("unchecked")
	public List<TipoCombustible> getTiposCombustible() {
		return (List<TipoCombustible>) crudServiceBean.findAll(TipoCombustible.class);
	}

	@SuppressWarnings("unchecked")
	public List<TipoFuente> getTiposFuente() {
		return (List<TipoFuente>) crudServiceBean.findAll(TipoFuente.class);
	}

	public void saveOrUpdate(EntidadBase objeto) {
		crudServiceBean.saveOrUpdate(objeto);
	}

	public void delete(EntidadBase objeto) {
		crudServiceBean.delete(objeto);
	}

	public PuntoMonitoreo getPuntoMonitoreo(int id) {
		return crudServiceBean.find(PuntoMonitoreo.class, id);
	}

	public PuntoDescargaLiquida getPuntoDescargaLiquida(int id) {
		return crudServiceBean.find(PuntoDescargaLiquida.class, id);
	}

	public PuntoInmision getPuntoInmision(int id) {
		return crudServiceBean.find(PuntoInmision.class, id);
	}

	public PuntoEmision getPuntoEmision(int id) {
		return crudServiceBean.find(PuntoEmision.class, id);
	}
}
