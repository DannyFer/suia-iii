/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.prevencion.tdr.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.tdr.bean.IngresarTDRBean;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.CatalogoGeneral;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.ProyectoGeneralCatalogo;
import ec.gob.ambiente.suia.domain.TdrEiaLicencia;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.prevencion.tdr.facade.TdrFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectos.service.ProyectoGeneralCatalogoServiceBean;
import ec.gob.ambiente.suia.reportes.DocumentoPDFPlantillaHtml;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author Frank Torres
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Frank Torres, Fecha: 22/01/2015]
 *          </p>
 */
@RequestScoped
@ManagedBean
public class IngresarTDRController implements Serializable {
	private static final long serialVersionUID = -35234343553353577L;

	private static final Logger LOGGER = Logger
			.getLogger(IngresarTDRController.class);

	@ManagedProperty(value = "#{loginBean}")
	@Setter
	private LoginBean loginBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{ingresarTDRBean}")
	private IngresarTDRBean ingresarTDRBean;

	@EJB
	private ProyectoGeneralCatalogoServiceBean proyectoGeneralCatalogoServiceBean;

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
	private TaskBeanFacade taskBeanFacade;

	@EJB
	UsuarioFacade usuarioFacade;

	@EJB
	private TdrFacade tdrFacade;

	public void sendAction() {

	}

	public void guardar() {

	}

	public void guardarComentario() {

	}

	public void guardarFichaTecnicaSimple() {
		if (ingresarTDRBean.getTdrEia().getTdelRequiredForestInventory() != null) {
			if (ingresarTDRBean.getTdrEia().getTdelRequiredForestInventory()) {
				ingresarTDRBean.getTdrEia().setTdelMethodologyForest("");
			} else
				ingresarTDRBean.getTdrEia().setTdelDetailEconomicAspects("");
		}

		if (ingresarTDRBean.getTdrEia().getTdelRequiredContributions() != null) {
			if (ingresarTDRBean.getTdrEia().getTdelRequiredContributions()) {
				ingresarTDRBean.getTdrEia().setTdelCapitalContributions("");
				ingresarTDRBean.getTdrEia().setTdelProjectDescription("");
			} else
				ingresarTDRBean.getTdrEia()
						.setTdelAssessmentEnvironmentalServices("");
		}

		if (ingresarTDRBean.getTdrEia().getTdelRequiredPassiveEnvironmental() != null) {
			if (ingresarTDRBean.getTdrEia()
					.getTdelRequiredPassiveEnvironmental()) {
				ingresarTDRBean.getTdrEia()
						.setTdelIdentificationMethodology("");
			} else
				ingresarTDRBean.getTdrEia()
						.setTdelIdentificationContaminatesSites("");
		}
		// crudServiceBean.saveOrUpdate(ingresarTDRBean.getTdrEia());
		tdrFacade.guardarTdrEia(ingresarTDRBean.getTdrEia());

		JsfUtil.addMessageInfo("Datos guardados correctamente.");

	}

	public boolean validarMarcoLegal() {
		for (CatalogoGeneral catalogoGeneral : ingresarTDRBean
				.getListaMarcoLegal()) {
			if (catalogoGeneral.isSeleccionado()) {
				return true;
			}
		}
		return false;
	}

	public void guardarDescripcionProyecto() {
		Boolean listo = true;
		List<CatalogoGeneral> marcoLegalSeleccionados = new ArrayList<CatalogoGeneral>();
		List<ProyectoGeneralCatalogo> marcoLegalSeleccionadosG = new ArrayList<ProyectoGeneralCatalogo>();

		for (CatalogoGeneral catalogoGeneral : ingresarTDRBean
				.getListaMarcoLegal()) {
			if (catalogoGeneral.isSeleccionado()) {
				marcoLegalSeleccionados.add(catalogoGeneral);
				ProyectoGeneralCatalogo p = new ProyectoGeneralCatalogo();

				p.setCatalogoGeneral(catalogoGeneral);
				p.setTdrEiaLicencia(ingresarTDRBean.getTdrEia());
				p.setProyectoLicenciamientoAmbiental(ingresarTDRBean
						.getTdrEia().getProyecto());

				marcoLegalSeleccionadosG.add(p);
			}
		}
		if (marcoLegalSeleccionados.size() == 0) {
			JsfUtil.addMessageError("Debe seleccionar al menos un elemento en Marco Legal y Administrativo.");
			listo = false;
		}
		if (ingresarTDRBean.getImageLocationFile() != null) {

			// ingresarTDRBean.setTdrEia(crudServiceBean.saveOrUpdate(ingresarTDRBean.getTdrEia()));
			ingresarTDRBean.setTdrEia(tdrFacade.guardarTdrEia(ingresarTDRBean
					.getTdrEia()));

			try {
				System.out
						.println("--------------------------------------------------------");
				String folderFileName = "ImageLocationFile";

				tdrFacade.eliminarDocumentoTdr(folderFileName, ingresarTDRBean
						.getTdrEia().getId(), 1);

				tdrFacade.ingresarTdrAdjunto(ingresarTDRBean
						.getImageLocationFile(), ingresarTDRBean.getTdrEia()
						.getId(), folderFileName, bandejaTareasBean
						.getProcessId(), bandejaTareasBean.getTarea().getTaskId());
				System.out
						.println("--------------------------------------------------------");
			} catch (Exception e) {
				LOGGER.error("Error al subir la imagen al alfresco", e);
				JsfUtil.addMessageError("Error al subir la imagen al alfresco.");
				listo = false;
			}
			//
			// try {
			// String folderName;
			// folderName = UtilAlfresco.generarEstructuraCarpetas("TDR",
			// "InformeTecnnico", ingresarTDRBean.getTdrEia().getId()
			// .toString());
			// String folderId;
			// folderId = alfrescoServiceBean.createFolderStructure(
			// folderName, Constantes.ROOT_ID);
			//
			// MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
			// File imagen = ingresarTDRBean.getImageLocationFile();
			// byte[] data = Files.readAllBytes(Paths.get(imagen
			// .getAbsolutePath()));
			//
			// Document documentCreate = alfrescoServiceBean.fileSaveStream(
			// data, imagen.getName(), folderId, "EIA", folderName,
			// ingresarTDRBean.getTdrEia().getId());
			//
			// crudServiceBean.saveOrUpdate(UtilAlfresco.crearDocumento(
			// documentCreate.getName(), mimeTypesMap
			// .getContentType(imagen),
			// documentCreate.getId(), ingresarTDRBean.getTdrEia()
			// .getId(), mimeTypesMap.getContentType(imagen),
			// imagen.getName(), TdrEiaLicencia.class.getSimpleName(),
			// 1));
			// } catch (Exception e) {
			// }

		} else {
			// LOGGER.error("La imagen de localización es obligatoria.");
			// listo = false;
			// JsfUtil.addMessageError("La imagen de localización es obligatoria.");
		}
		if (listo) {
			// crudServiceBean.saveOrUpdate(ingresarTDRBean.getTdrEia());
			tdrFacade.guardarTdrEia(ingresarTDRBean.getTdrEia());
			proyectoGeneralCatalogoServiceBean.guardarProyectoGeneralCatalogo(
					marcoLegalSeleccionadosG, ingresarTDRBean.getTdrEia()
							.getProyecto().getId(), 27);
			// List<ProyectoGeneralCatalogo> catalogosActuales =
			// proyectoGeneralCatalogoServiceBean
			// .listarProyectoGeneralCatalogoPorProyectoCategoria(
			// ingresarTDRBean.getTdrEia().getProyecto().getId(),
			// 27);
			// for (ProyectoGeneralCatalogo proyectoGeneralCatalogo :
			// catalogosActuales) {
			// crudServiceBean.delete(proyectoGeneralCatalogo);
			// }
			// for (ProyectoGeneralCatalogo proyectoGeneralCatalogo :
			// marcoLegalSeleccionadosG) {
			// crudServiceBean.saveOrUpdate(proyectoGeneralCatalogo);
			// }
			JsfUtil.addMessageInfo("Datos guardados correctamente.");

			if (ingresarTDRBean.validarTdr()) {
				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('iniciarProceso').show();");
			}
		}

	}

	public void limpiar() {
		// System.out.println("limpiando ========================");
	}

	public void adicionarAnexos() {
		Boolean listo = true;

		List<CatalogoGeneral> anexosSeleccionados = new ArrayList<CatalogoGeneral>();
		List<ProyectoGeneralCatalogo> anexosSeleccionadosG = new ArrayList<ProyectoGeneralCatalogo>();

		for (CatalogoGeneral catalogoGeneral : ingresarTDRBean.getListaAnexos()) {
			if (catalogoGeneral.isSeleccionado()) {
				anexosSeleccionados.add(catalogoGeneral);

				ProyectoGeneralCatalogo p = new ProyectoGeneralCatalogo();

				p.setCatalogoGeneral(catalogoGeneral);
				p.setTdrEiaLicencia(ingresarTDRBean.getTdrEia());
				p.setProyectoLicenciamientoAmbiental(ingresarTDRBean
						.getTdrEia().getProyecto());

				anexosSeleccionadosG.add(p);

			}
		}

		List<CatalogoGeneral> mapasTematicoSeleccionados = new ArrayList<CatalogoGeneral>();
		List<ProyectoGeneralCatalogo> mapasSeleccionadosG = new ArrayList<ProyectoGeneralCatalogo>();

		for (CatalogoGeneral catalogoGeneral : ingresarTDRBean
				.getListaMapasTematico()) {
			if (catalogoGeneral.isSeleccionado()) {
				mapasTematicoSeleccionados.add(catalogoGeneral);
				ProyectoGeneralCatalogo p = new ProyectoGeneralCatalogo();

				p.setCatalogoGeneral(catalogoGeneral);
				p.setTdrEiaLicencia(ingresarTDRBean.getTdrEia());
				p.setProyectoLicenciamientoAmbiental(ingresarTDRBean
						.getTdrEia().getProyecto());

				mapasSeleccionadosG.add(p);
			}
		}

		if (mapasTematicoSeleccionados.size() == 0) {

			JsfUtil.addMessageError("Debe seleccionar al menos un elemento en el Listado de Mapas Temáticos.");
			listo = false;
		}
		if (anexosSeleccionados.size() == 0) {

			JsfUtil.addMessageError("Debe seleccionar al menos un elemento en el Listado de Anexos.");
			listo = false;
		}
		if (listo) {

			// crudServiceBean.saveOrUpdate(ingresarTDRBean.getTdrEia());
			tdrFacade.guardarTdrEia(ingresarTDRBean.getTdrEia());
			proyectoGeneralCatalogoServiceBean.guardarProyectoGeneralCatalogo(
					mapasSeleccionadosG, ingresarTDRBean.getTdrEia()
							.getProyecto().getId(), 28);

			proyectoGeneralCatalogoServiceBean.guardarProyectoGeneralCatalogo(
					anexosSeleccionadosG, ingresarTDRBean.getTdrEia()
							.getProyecto().getId(), 29);
			// List<ProyectoGeneralCatalogo> catalogosActuales =
			// proyectoGeneralCatalogoServiceBean
			// .listarProyectoGeneralCatalogoPorProyectoCategoria(
			// ingresarTDRBean.getTdrEia().getProyecto().getId(),
			// 28);
			// for (ProyectoGeneralCatalogo proyectoGeneralCatalogo :
			// catalogosActuales) {
			// crudServiceBean.delete(proyectoGeneralCatalogo);
			// }
			// for (ProyectoGeneralCatalogo proyectoGeneralCatalogo :
			// mapasSeleccionadosG) {
			// crudServiceBean.saveOrUpdate(proyectoGeneralCatalogo);
			// }

			// List<ProyectoGeneralCatalogo> catalogosActualesA =
			// proyectoGeneralCatalogoServiceBean
			// .listarProyectoGeneralCatalogoPorProyectoCategoria(
			// ingresarTDRBean.getTdrEia().getProyecto().getId(),
			// 29);
			// for (ProyectoGeneralCatalogo proyectoGeneralCatalogo :
			// catalogosActualesA) {
			// crudServiceBean.delete(proyectoGeneralCatalogo);
			// }
			// for (ProyectoGeneralCatalogo proyectoGeneralCatalogo :
			// anexosSeleccionadosG) {
			// crudServiceBean.saveOrUpdate(proyectoGeneralCatalogo);
			// }

			JsfUtil.addMessageInfo("Datos guardados correctamente.");
		}

		if (ingresarTDRBean.validarTdr()) {
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('iniciarProceso').show();");
		}
	}

	public void descargarArchivo() {
		DocumentoPDFPlantillaHtml.descargarArchivo("tdr",
				cargarTDRProponente(), "tdr_hidrocarburos", true, "TDR");
	}

	private TdrEiaLicencia getTDRFull() {
		TdrEiaLicencia tdrFull = tdrFacade
				.getTdrEiaLicenciaPorIdProyectoFull(ingresarTDRBean.getTdrEia()
						.getProyecto().getId());
		return tdrFull;
	}

	public String[] cargarTDRProponente() {
		TdrEiaLicencia tdrFull = getTDRFull();
		Usuario usuario = null;
		Organizacion organizacion = null;
		try {
			usuario = usuarioFacade.buscarUsuarioPorIdFull(tdrFull
					.getProyecto().getUsuario().getId());
			organizacion = usuario.getPersona().getOrganizaciones().get(0);
		} catch (ServiceException e) {
		}

		String[] parametros = new String[39];
		parametros[0] = tdrFull.getProyecto().getCodigo();
		parametros[1] = tdrFull.getProyecto().getNombre();
		parametros[2] = tdrFull.getProyecto().getCatalogoCategoria().getFase() == null ? ""
				: ingresarTDRBean.getTdrEia().getProyecto().getCatalogoCategoria()
						.getFase().getNombre();
		parametros[3] = new Double(tdrFull.getProyecto().getArea()).toString()
				+ " " + tdrFull.getProyecto().getUnidad();
		parametros[4] = usuario.getPersona().getOrganizaciones().size() > 0 ? tdrFull
				.getProyecto().getUsuario().getPersona().getOrganizaciones()
				.get(0).getNombre()
				: "";
		parametros[5] = usuario.getPersona().getOrganizaciones().size() > 0 ? tdrFull
				.getProyecto().getUsuario().getPersona().getOrganizaciones()
				.get(0).getPersona().getNombre()
				: "";
		parametros[6] = tdrFull.getTdelExecutionTime().toString();
		parametros[7] = tdrFull.getProyecto()
				.getProyectoUbicacionesGeograficas().size() > 0 ? DocumentoPDFPlantillaHtml
				.crearTablaUbicacion(tdrFull.getProyecto()
						.getProyectoUbicacionesGeograficas()) : "";
		parametros[8] = tdrFull.getProyecto().getProyectoBloques().size() > 0 ? DocumentoPDFPlantillaHtml
				.crearTablaBloques(tdrFull.getProyecto().getProyectoBloques())
				: "";

		// Ubicacion
		if (usuario.getPersona().getUbicacionesGeografica() != null) {
			parametros[9] = usuario.getPersona().getUbicacionesGeografica()
					.getNombre();
			parametros[10] = usuario.getPersona().getUbicacionesGeografica()
					.getUbicacionesGeografica().getNombre();
			parametros[11] = usuario.getPersona().getUbicacionesGeografica()
					.getUbicacionesGeografica().getUbicacionesGeografica()
					.getNombre();
		}
		if (organizacion != null) {
			for (Contacto c : organizacion.getContactos()) {
				if (c.getFormasContacto().getId() == 1) {// telefono
					parametros[12] = c.getValor();
				} else if (c.getFormasContacto().getId() == 4) {// Direccion
					parametros[13] = c.getValor();
				} else if (c.getFormasContacto().getId() == 6) {// Email
					parametros[14] = c.getValor();
				}
			}
		}
		// organizacion.getIdUbicacionGeografica()
		// parametros[15] = "Equipo";//
		// tdr.getTechnicalTeams().size()>0?DocumentoPDFPlantillaHtml.crearTabaEquipoTecnico(tdr.getTechnicalTeams()):"";
		parametros[15] = tdrFull.getEquipoTecnico().size() > 0 ? DocumentoPDFPlantillaHtml
				.crearTabaEquipoTecnico(tdrFull.getEquipoTecnico()) : "";

		parametros[16] = tdrFull.getTdelBackground();
		parametros[17] = tdrFull.getTdelGeneralGoals();
		parametros[18] = tdrFull.getTdelSpecificsGoals();
		parametros[19] = tdrFull.getTdelTechnicalScope();
		parametros[20] = tdrFull.getTdelGeneralMethodology();
		parametros[21] = tdrFull.getTdelDeterminationReferenceArea();
		parametros[22] = tdrFull.getTdelMethodologyPhysicalAbiotic();
		parametros[23] = tdrFull.getTdelMethodologyBiotic();
		parametros[24] = tdrFull.getTdelMethodologySocioeconomicCultural();
		// Cap 4
		parametros[25] = tdrFull.getTdelAdministrativeFramework();
		parametros[26] = tdrFull.getTdelCharacteristicsProject();
		parametros[27] = "";// Localizacion
		parametros[28] = tdrFull.getTdelAlternativesAnalysis();
		parametros[29] = tdrFull.getTdelTypeInputs();
		// Cap 5
		parametros[30] = tdrFull.getTdelTypeInputs();
		// Cap 6
		parametros[31] = tdrFull.getTdelImpactAssessmentMethodology();
		parametros[32] = tdrFull.getTdelRiskAnalysisMethodology();
		// Cap 7
		// parametros[26] = tdrFull.getTdelImpactAssessmentMethodology();
		// parametros[27] = tdrFull.getTdelRiskAnalysisMethodology();
		// Cap 9
		parametros[33] = tdrFull.getTdelMonitoringPlan();
		// Cap 10
		String requiere = tdrFull.getTdelRequiredForestInventory() ? "Si requiere inventario forestal"
				: "No requiere inventario forestal, Justificacion:";
		parametros[34] = requiere + tdrFull.getTdelMethodologyForest();
		// Cap 11
		parametros[35] = tdrFull.getTdelMapping();
		parametros[36] = getStringElementos(getCatalogosSeleccionados(28));
		parametros[37] = getStringElementos(getCatalogosSeleccionados(29));
		;
		parametros[38] = tdrFull.getTdelOtherThematicMaps();
		return parametros;

	}

	private List<ProyectoGeneralCatalogo> getCatalogosSeleccionados(
			Integer catalogo) {
		return proyectoGeneralCatalogoServiceBean
				.listarProyectoGeneralCatalogoPorProyectoCategoria(
						ingresarTDRBean.getProyectoActivo().getId(), catalogo);
	}

	private String getStringElementos(List<ProyectoGeneralCatalogo> catalogo) {
		String elementos = "";
		for (ProyectoGeneralCatalogo proyectoGeneralCatalogo : catalogo) {
			elementos = elementos
					+ " "
					+ proyectoGeneralCatalogo.getCatalogoGeneral()
							.getDescripcion();
		}
		return elementos;
	}

}
