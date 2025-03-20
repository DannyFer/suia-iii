/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */

package ec.gob.ambiente.prevencion.categoria2.bean;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Articulo;
import ec.gob.ambiente.suia.domain.CatalogoGeneral;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.dto.EntityNormativa;
import ec.gob.ambiente.suia.dto.EntityNormativaDetalle;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilFichaMineria;

/**
 * <b> Clase para mostrar las normativas con sus respectivos articulos. </b>
 * 
 * @author carlos.pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: carlos.pupo, Fecha: 12/05/2015]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class MarcoLegalPmaImprimirBean implements Serializable {

	private static final long serialVersionUID = -1319139968498140336L;

	private static final Logger LOG = Logger.getLogger(MarcoLegalPmaImprimirBean.class);

	@EJB
	private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;

	@Getter
	@Setter
	private List<CatalogoGeneral> articulos;

	@Getter
	@Setter
	private FichaAmbientalPma fichaAmbientalPma;

	@PostConstruct
	public void init() {
		articulos = new ArrayList<CatalogoGeneral>();
		cargarNormativa();
	}

	public void cargarNormativa() {
		try {
			if (articulos.isEmpty()) {
				ProyectoLicenciamientoAmbiental proyectoActivo = proyectoLicenciamientoAmbientalFacade
						.buscarProyectosLicenciamientoAmbientalPorId(proyectosBean.getProyecto().getId());
				fichaAmbientalPma = fichaAmbientalPmaFacade.getFichaAmbientalPorCodigoProyecto(proyectoActivo
						.getCodigo());

				articulos = fichaAmbientalPmaFacade.getArticulosCatalogoNativeQuery(proyectoActivo
						.getCatalogoCategoria().getTipoSubsector().getId(), Constantes.ARTICULO_REGISTRO,null);
			}

		} catch (Exception e) {
			LOG.error("No se puede cargar las Normativas.", e);
			JsfUtil.addMessageError("No se puede cargar las Normativas.");
		}

	}


	public void cargarNormativaLicenciamiento() {
		try {
			if (articulos.isEmpty()) {
				ProyectoLicenciamientoAmbiental proyectoActivo = proyectoLicenciamientoAmbientalFacade
						.buscarProyectosLicenciamientoAmbientalPorId(proyectosBean.getProyecto().getId());
				fichaAmbientalPma = fichaAmbientalPmaFacade.getFichaAmbientalPorCodigoProyecto(proyectoActivo
						.getCodigo());

				articulos = fichaAmbientalPmaFacade.getArticulosCatalogoNativeQuery(proyectoActivo
						.getCatalogoCategoria().getTipoSubsector().getId(), Constantes.ARTICULO_LICENCIAMIENTO,null);
			}

		} catch (Exception e) {
			LOG.error("No se puede cargar las Normativas.", e);
			JsfUtil.addMessageError("No se puede cargar las Normativas.");
		}

	}

	public void actualizarFicha() {
		try {
			fichaAmbientalPmaFacade.guardarSoloFicha(fichaAmbientalPma);
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		} catch (Exception e) {
			LOG.error("No se puede actualizar la Ficha.", e);
			JsfUtil.addMessageError("No se puede actualizar la Ficha.");
		}

	}

	public File exportarPdf() {
		File file = null;
		try {
			EntityNormativa entityNormativa = new EntityNormativa();

			String[] columnas = { " " };
			String htmlTablas = "";
			String[] ordenColumnas = { "Articulo" };

			for (CatalogoGeneral catalogoGeneral : articulos) {
				List<EntityNormativaDetalle> detalles = new ArrayList<EntityNormativaDetalle>();
				for (Articulo articulo : catalogoGeneral.getArticulos()) {
					detalles.add(new EntityNormativaDetalle(articulo.getArticulo()));
				}
				htmlTablas += UtilFichaMineria.devolverDetalle(catalogoGeneral.getDescripcion(), columnas, detalles,
						ordenColumnas, "justify");
			}

			entityNormativa.setDetalleNormativa(htmlTablas);
            UtilFichaMineria.setAreaResponsable(proyectosBean.getProyecto().getAreaResponsable());
			file = UtilFichaMineria.generarFichero(
					UtilFichaMineria.extraeHtml(JsfUtil.devolverPathReportesHtml("normativa.html")), entityNormativa,
					"Normativas", false,proyectosBean.getProyecto().getAreaResponsable(), null);

		} catch (Exception e) {
			LOG.error("No se puede exportar el archivo.", e);
			JsfUtil.addMessageError("No se puede exportar el archivo.");
		}
		return file;

	}


	
}