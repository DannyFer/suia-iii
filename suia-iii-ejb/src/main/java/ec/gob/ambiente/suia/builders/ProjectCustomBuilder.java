/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.builders;

import java.text.SimpleDateFormat;
import java.util.Date;

import ec.fugu.ambiente.consultoring.projects.ProyectoLicIntegacionVo;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.dto.ProyectoCustom;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: 13/03/2015]
 *          </p>
 */
public class ProjectCustomBuilder {

	private ProyectoCustom proyectoCustom;
	
	private static final String LABEL_NO_DISPONIBLE_ABBR = "N/D";
	private static final String LABEL_NO_DISPONIBLE = "No disponible";

	public ProjectCustomBuilder() {
		proyectoCustom = new ProyectoCustom();
	}

	public ProyectoCustom build() {
		return proyectoCustom;
	}

	public ProjectCustomBuilder fromSuiaIII(ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental) {
		proyectoCustom.setSourceType(ProyectoCustom.SOURCE_TYPE_INTERNAL);
		proyectoCustom.setId(proyectoLicenciamientoAmbiental.getId().toString());
		try {
			proyectoCustom.setCategoria(proyectoLicenciamientoAmbiental.getCatalogoCategoria().getCategoria()
					.getCodigo());
			proyectoCustom.setCategoriaNombrePublico(proyectoLicenciamientoAmbiental.getCatalogoCategoria()
					.getCategoria().getNombrePublico());
		} catch (Exception e) {
		}
		proyectoCustom.setCodigo(proyectoLicenciamientoAmbiental.getCodigo());
		proyectoCustom.setNombre(proyectoLicenciamientoAmbiental.getNombre());
		proyectoCustom.setSector(proyectoLicenciamientoAmbiental.getTipoSector().toString());

		Date registro = proyectoLicenciamientoAmbiental.getFechaRegistro();
		String registroString = "";
		if (registro != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			registroString = dateFormat.format(registro);
		}
		proyectoCustom.setRegistro(registroString);
		try {
			proyectoCustom.setResponsableSiglas(proyectoLicenciamientoAmbiental.getAreaResponsable()
					.getAreaAbbreviation());
			proyectoCustom.setResponsable(proyectoLicenciamientoAmbiental.getAreaResponsable()
					.getAreaName());
		} catch (Exception e) {
			proyectoCustom.setResponsableSiglas(LABEL_NO_DISPONIBLE_ABBR);
			proyectoCustom.setResponsable(LABEL_NO_DISPONIBLE);
		}
		return this;
	}

	public ProjectCustomBuilder fromProyectoSuia(ProyectoLicIntegacionVo proyectoLicIntegacionVo) {
		proyectoCustom.setSourceType(ProyectoCustom.SOURCE_TYPE_EXTERNAL_SUIA);
		proyectoCustom.setId(proyectoLicIntegacionVo.getId());
		proyectoCustom.setCategoria(proyectoLicIntegacionVo.getCategoria());
		proyectoCustom.setCategoriaNombrePublico(getCategoriaNombrePublico(proyectoCustom.getCategoria()));
		proyectoCustom.setCodigo(proyectoLicIntegacionVo.getId());
		proyectoCustom.setNombre(proyectoLicIntegacionVo.getNombre());
		String sector = proyectoLicIntegacionVo.getSector();
		try {
			sector = sector.substring(0, 1).toUpperCase() + sector.substring(1);
		} catch (Exception e) {
		}
		proyectoCustom.setSector(sector);

		Date registro = proyectoLicIntegacionVo.getFechaRegistro().toGregorianCalendar().getTime();
		String registroString = "";
		if (registro != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			registroString = dateFormat.format(registro);
		}
		proyectoCustom.setRegistro(registroString);
		try {
			proyectoCustom.setResponsableSiglas(LABEL_NO_DISPONIBLE_ABBR);
			proyectoCustom.setResponsable(LABEL_NO_DISPONIBLE);
		} catch (Exception e) {
			proyectoCustom.setResponsableSiglas(LABEL_NO_DISPONIBLE_ABBR);
			proyectoCustom.setResponsable(LABEL_NO_DISPONIBLE);
		}
		return this;
	}

	private String getCategoriaNombrePublico(String categoria) {
		if (categoria == null)
			return "";
		if (categoria.equals("I"))
			return "Certificado Ambiental";
		if (categoria.equals("II"))
			return "Registro Ambiental";
		else
			return "Licencia Ambiental";
	}
}
