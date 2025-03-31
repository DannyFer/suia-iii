package ec.gob.ambiente.suia.eia.marcoLegalEia.bean;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.prevencion.categoria2.bean.MarcoLegalPmaBean;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.domain.Articulo;
import ec.gob.ambiente.suia.domain.CatalogoGeneral;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.dto.EntityNormativa;
import ec.gob.ambiente.suia.dto.EntityNormativaDetalle;
import ec.gob.ambiente.suia.eia.facade.EstudioImpactoAmbientalFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilFichaMineria;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;

@ManagedBean
@ViewScoped
public class MarcoLegalEiaBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4693752523691988631L;
	private static final Logger LOG = Logger.getLogger(MarcoLegalPmaBean.class);

	@EJB
	private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;

	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

	@EJB
	private EstudioImpactoAmbientalFacade estudioImpactoAmbientalFacade;

	@EJB
	private ValidacionSeccionesFacade validacionSeccionesFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;

	@Getter
	@Setter
	private List<CatalogoGeneral> catalogoGenerals;

	@Getter
	@Setter
	private List<CatalogoGeneral> catalogoEia;

	@Getter
	@Setter
	private FichaAmbientalPma fichaAmbientalPma;

	@Getter
	@Setter
	private EstudioImpactoAmbiental estudioImpactoAmbiental;
	
	@Getter
    private boolean esMineriaNoMetalicos;

	@PostConstruct
	public void init() {
		catalogoEia = new ArrayList<CatalogoGeneral>();
		cargarNormativaLicenciamiento();
	}

	/**
	 * 
	 * <b> Metodo para cargar las normativas de licenciamiento ambiental con sus
	 * respectivos articulos segun el sector del proyeto . </b>
	 * <p>
	 * [Author: Reynier Arias, Date: 26/06/2015]
	 * </p>
	 * 
	 */
	public void cargarNormativaLicenciamiento() {
		try {
			if (catalogoEia.isEmpty()) {
				// ProyectoLicenciamientoAmbiental proyectoActivo =
				// proyectoLicenciamientoAmbientalFacade
				// .buscarProyectosLicenciamientoAmbientalPorId(proyectosBean.getProyecto().getId());
				
				HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
				estudioImpactoAmbiental=(EstudioImpactoAmbiental) request.getSession().getAttribute("estudio");
//				estudioImpactoAmbiental = estudioImpactoAmbientalFacade.obtenerEIAPorId(((EstudioImpactoAmbiental) JsfUtil.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT)).getId());

				ProyectoLicenciamientoAmbiental proyectoActivo = proyectoLicenciamientoAmbientalFacade
						.buscarProyectosLicenciamientoAmbientalPorId(proyectosBean
								.getProyecto().getId());
				fichaAmbientalPma = fichaAmbientalPmaFacade
						.getFichaAmbientalPorCodigoProyecto(proyectoActivo
								.getCodigo());
				catalogoEia = new ArrayList<CatalogoGeneral>();
				esMineriaNoMetalicos=estudioImpactoAmbiental.getProyectoLicenciamientoAmbiental().getCatalogoCategoria().getCodigo().equals("21.02.03.02") && estudioImpactoAmbiental.getResumenEjecutivo()==null?true:false;
				
				String cacsCode=null;
				if(esMineriaNoMetalicos){
					cacsCode=estudioImpactoAmbiental.getProyectoLicenciamientoAmbiental().getCatalogoCategoria().getCodigo();
				}
				
				catalogoEia = fichaAmbientalPmaFacade
						.getArticulosCatalogoNativeQuery(proyectoActivo
								.getCatalogoCategoria().getTipoSubsector()
								.getId(), Constantes.ARTICULO_LICENCIAMIENTO,cacsCode);
			}

		} catch (Exception e) {
			LOG.error("No se puede cargar las Normativas.", e);
			JsfUtil.addMessageError("No se puede cargar las Normativas.");
		}

	}

	/**
	 * 
	 * <b> metodo para actializar el estudio EIA cuando se seleccionar el check.
	 * </b>
	 * <p>
	 * [Author: Paul Inca, Date: 29/06/2015]
	 * </p>
	 * 
	 */

	public void actualizarFichaEia() {
		try {

			if (estudioImpactoAmbiental.getTipoEstadoLegal()) {
				estudioImpactoAmbientalFacade.guardar(estudioImpactoAmbiental);
				validacionSeccionesFacade.guardarValidacionSeccion("EIA",
						"marcoLegalEia", estudioImpactoAmbiental.getId()
								.toString());
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			} else {
				JsfUtil.addMessageError("Debe aceptar las Normativas Legales que aplican a su proyecto, obra o actividad.");
			}

		} catch (Exception e) {
			LOG.error("No se puede actualizar la Ficha.", e);
			JsfUtil.addMessageError("No se puede actualizar la Ficha.");
		}

	}

	/**
	 * 
	 * <b> Metodo para exportar a pdf las normativas. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 25/03/2015]
	 * </p>
	 * 
	 * @return File : Archivo
	 */
	public File exportarPdf() {
		File file = null;
		try {
			EntityNormativa entityNormativa = new EntityNormativa();

			String[] columnas = { " " };
			String htmlTablas = "";
			String[] ordenColumnas = { "Articulo" };

			for (CatalogoGeneral catalogoGeneral : catalogoGenerals) {
				List<EntityNormativaDetalle> detalles = new ArrayList<EntityNormativaDetalle>();
				for (Articulo articulo : catalogoGeneral.getArticulos()) {

					detalles.add(new EntityNormativaDetalle(articulo
							.getArticulo()));
				}
				htmlTablas += UtilFichaMineria.devolverDetalle(
						catalogoGeneral.getDescripcion(), columnas, detalles,
						ordenColumnas, "justify");
			}

			entityNormativa.setDetalleNormativa(htmlTablas);
			file = UtilFichaMineria.generarFichero(UtilFichaMineria
					.extraeHtml(JsfUtil
							.devolverPathReportesHtml("normativa.html")),
					entityNormativa, "Normativas", false, null);

		} catch (Exception e) {
			LOG.error("No se puede exportar el archivo.", e);
			JsfUtil.addMessageError("No se puede exportar el archivo.");
		}
		return file;

	}

	public void cancelarEia() {
		JsfUtil.redirectTo("/prevencion/licenciamiento-ambiental/eia/marcoLegalEia/marcoLegalEia.jsf");

	}
	
	public void linkLineaBase()
	{
		JsfUtil.redirectTo("/prevencion/licenciamiento-ambiental/eia/adjuntos/adjuntos.jsf?id=14");		
	}

}
