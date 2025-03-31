/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import lombok.Getter;
import ec.gob.ambiente.suia.administracion.facade.FormasContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.MenuFacade;
import ec.gob.ambiente.suia.administracion.facade.NacionalidadFacade;
import ec.gob.ambiente.suia.administracion.facade.TipoImpedimentoFacade;
import ec.gob.ambiente.suia.administracion.facade.TipoOrganizacionFacade;
import ec.gob.ambiente.suia.administracion.facade.TipoTratosFacade;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.Menu;
import ec.gob.ambiente.suia.domain.Nacionalidad;
import ec.gob.ambiente.suia.domain.TipoImpedimento;
import ec.gob.ambiente.suia.domain.TipoOrganizacion;
import ec.gob.ambiente.suia.domain.TipoTratos;
import ec.gob.ambiente.suia.exceptions.ServiceException;

/**
 * 
 * @author christian
 */
@ManagedBean
@ViewScoped
public class CombosController implements Serializable {

	/**
     *
     */
	private static final long serialVersionUID = 8479068132047949475L;
	@EJB
	private MenuFacade menuFacade;
	@EJB
	private NacionalidadFacade nacionalidadFacade;
	@EJB
	private TipoOrganizacionFacade tipoOrganizacionFacade;
	@EJB
	private FormasContactoFacade formasContactoFacade;
	@EJB
	private TipoTratosFacade tipoTratosFacade;
	@EJB
	private TipoImpedimentoFacade tipoImpedimentoFacade;

	private List<SelectItem> listaMenuPadres;
	@Getter
	private List<SelectItem> listaNacionalidades;
	@Getter
	private List<SelectItem> listaTipoOrganizacion;
	@Getter
	private List<SelectItem> listaFormasContacto;
	@Getter
	private List<SelectItem> listaFormasContactoPromotor;
	@Getter
	private List<SelectItem> listaTipoTratos;
	@Getter
	private List<SelectItem> listaTipoDocumento;
	@Getter
	private List<SelectItem> listaGenero;
	@Getter
	private List<SelectItem> listaEmpresaMixta;
	@Getter
	private List<SelectItem> listaTipoImpedimento;

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(CombosController.class);
	private static final SelectItem SELECCIONE = new SelectItem(null,
			"Seleccione");
	private static final String CEDULA = "Cédula";
	private static final String RUC = "RUC";
	private static final String PASAPORTE = "Pasaporte";
	private static final String MASCULINO = "MASCULINO";
	private static final String FEMENINO = "FEMENINO";
	//private static final String IGUAL_MAS_PARTICIPACION = "Igual o más que el 70% de participación del Estado";
	//private static final String IGUAL_MAS_PARTICIPACION = "Empresas Mixtas cuando su capital suscrito pertenezca por lo menos a las dos terceras partes a entidades de Derecho Público";
	public static final String IGUAL_MAS_PARTICIPACION = "EMPRESAS MIXTAS CUANDO SU CAPITAL SUSCRITO PERTENEZCA POR LO MENOS A LAS DOS TERCERAS PARTES A ENTIDADES DE DERECHO PÚBLICO.";
	//private static final String MENOS_PARTICIPACION = "Menos del 70% de participación del Estado";
	//private static final String MENOS_PARTICIPACION = "Empresas Mixtas cuando su capital suscrito sea menor a las dos terceras partes a entidades de Derecho Público";
	private static final String MENOS_PARTICIPACION = "EMPRESAS MIXTAS CUANDO SU CAPITAL SUSCRITO SEA MENOR A LAS DOS TERCERAS PARTES A ENTIDADES DE DERECHO PÚBLICO.";

	@PostConstruct
	public void cargarCombos() {
		cargarListaFormasContacto();
		cargarListaFormasContactoRegistroPromotor();
		cargarListaGenero();
		cargarListaNacionalidades();
		cargarListaTipoDocumento();
		cargarListaTipoOrganizacion();
		cargarListaTipoTratos();
		cargarEmpresaMixta();
		cargarListaTipoImpedimento();
	}

	private void cargarListaNacionalidades() {
		listaNacionalidades = new ArrayList<SelectItem>();
		listaNacionalidades.add(SELECCIONE);
		try {
			List<Nacionalidad> listaDatos = nacionalidadFacade
					.buscarPorEstado(true);
			for (Nacionalidad n : listaDatos) {
				listaNacionalidades.add(new SelectItem(n.getId(), n
						.getDescripcion()));
			}
		} catch (ServiceException e) {
			LOG.error(e, e);
		}
	}

	private void cargarListaTipoOrganizacion() {
		listaTipoOrganizacion = new ArrayList<SelectItem>();
		listaTipoOrganizacion.add(SELECCIONE);
		try {
			List<TipoOrganizacion> listaDatos = tipoOrganizacionFacade
					.buscarPorEstado(true);
			for (TipoOrganizacion to : listaDatos) {
				listaTipoOrganizacion.add(new SelectItem(to.getId(), to
						.getNombre()));
			}
		} catch (ServiceException e) {
			LOG.error(e, e);
		}
	}

	private void cargarListaFormasContacto() {
		listaFormasContacto = new ArrayList<SelectItem>();
		listaFormasContacto.add(SELECCIONE);
		try {
			List<FormasContacto> listaDatos = formasContactoFacade
					.buscarPorEstado(true);
			for (FormasContacto fc : listaDatos) {
				listaFormasContacto.add(new SelectItem(fc.getId(), fc
						.getNombre()));
			}
		} catch (ServiceException e) {
			LOG.error(e, e);
		}
	}

	private void cargarListaFormasContactoRegistroPromotor() {
		listaFormasContactoPromotor = new ArrayList<SelectItem>();
		listaFormasContactoPromotor.add(SELECCIONE);
		try {
			List<FormasContacto> listaDatos = formasContactoFacade
					.buscarPorEstado(true);
			for (FormasContacto fc : listaDatos) {
				switch (fc.getNombre()) {
				case "*TELÉFONO":
					break;
				case "*TEL├ëFONO":
					break;
				case "*DIRECCI├ôN":
					break;
				case "*DIRECCIÓN":
					break;
				case "*EMAIL":
					fc.setNombre("CORREO ELECTRÓNICO");
					listaFormasContactoPromotor.add(new SelectItem(fc.getId(),
							fc.getNombre()));
					break;
				case "*CELULAR":
					break;
				default:
					listaFormasContactoPromotor.add(new SelectItem(fc.getId(),
							fc.getNombre()));
					break;
				}
			}
		} catch (ServiceException e) {
			LOG.error(e, e);
		}
	}

	private void cargarListaTipoTratos() {
		listaTipoTratos = new ArrayList<SelectItem>();
		listaTipoTratos.add(SELECCIONE);
		try {
			List<TipoTratos> listaDatos = tipoTratosFacade
					.buscarPorEstado(true);
			for (TipoTratos tt : listaDatos) {
				listaTipoTratos.add(new SelectItem(tt.getId(), tt.getNombre()));
			}
		} catch (ServiceException e) {
			LOG.error(e, e);
		}
	}

	private void cargarListaTipoDocumento() {
		listaTipoDocumento = new ArrayList<SelectItem>();
		listaTipoDocumento.add(new SelectItem(CEDULA, CEDULA));
		listaTipoDocumento.add(new SelectItem(RUC, RUC));
		listaTipoDocumento.add(new SelectItem(PASAPORTE, PASAPORTE));
	}

	private void cargarListaGenero() {
		listaGenero = new ArrayList<SelectItem>();
		listaGenero.add(SELECCIONE);
		listaGenero.add(new SelectItem(MASCULINO, "Masculino"));
		listaGenero.add(new SelectItem(FEMENINO, "Femenino"));
	}

	private void cargarEmpresaMixta() {
		listaEmpresaMixta = new ArrayList<SelectItem>();
		listaEmpresaMixta.add(new SelectItem(IGUAL_MAS_PARTICIPACION,
				IGUAL_MAS_PARTICIPACION));
		listaEmpresaMixta.add(new SelectItem(MENOS_PARTICIPACION,
				MENOS_PARTICIPACION));
	}

	private void cargarListaTipoImpedimento() {
		listaTipoImpedimento = new ArrayList<SelectItem>();
		listaTipoImpedimento.add(SELECCIONE);
		try {
			List<TipoImpedimento> listaDatos = tipoImpedimentoFacade
					.listarTipoImpedimentosActivos();
			for (TipoImpedimento tt : listaDatos) {
				listaTipoImpedimento.add(new SelectItem(tt.getId(), tt
						.getNombre()));
			}
		} catch (ServiceException e) {
			LOG.error(e, e);
		}
	}

	/**
	 * @return the listaMenuPadres
	 */
	public List<SelectItem> getListaMenuPadres() {
		listaMenuPadres = new ArrayList<SelectItem>();
		listaMenuPadres.add(SELECCIONE);
		try {
			List<Menu> listaDatos = menuFacade.listarNodoFinalFalse();
			for (Menu m : listaDatos) {
				listaMenuPadres.add(new SelectItem(m.getId(), m.getNombre()));
			}
		} catch (ServiceException e) {
			LOG.error(e, e);
		}
		return listaMenuPadres;
	}

}
