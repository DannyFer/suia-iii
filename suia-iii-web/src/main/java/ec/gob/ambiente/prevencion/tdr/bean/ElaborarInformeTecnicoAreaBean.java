package ec.gob.ambiente.prevencion.tdr.bean;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ObservacionTdrEiaLiciencia;
import ec.gob.ambiente.suia.domain.TdrEiaLicencia;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.observaciontdreialiciencia.facade.ObservacionTdrEiaFacade;
import ec.gob.ambiente.suia.prevencion.tdr.facade.TdrFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ElaborarInformeTecnicoAreaBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5450631734737901215L;

	private static final Logger LOGGER = Logger
			.getLogger(ElaborarInformeTecnicoAreaBean.class);

	@Setter
	@Getter
	TdrEiaLicencia tdrEia;

	@Getter
	@Setter
	private ObservacionTdrEiaLiciencia observacionTdrEiaLiciencia;

	@Getter
	@Setter
	private String observacion;

	@Getter
	@Setter
	private String area;
	@Getter
	@Setter
	private Boolean consolidado;
	@Getter
	@Setter
	private Boolean cumpleCriterios;

	@Getter
	@Setter
	private Boolean requiereFacilitadores;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
	private TaskBeanFacade taskBeanFacade;

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private TdrFacade tdrFacade;

	@EJB
	private ObservacionTdrEiaFacade observacionTdrEiaFacade;

	@EJB
	CrudServiceBean crudServiceBean;

	@Setter
	@Getter
	Boolean requierePronunciamiento = false;
	@Setter
	@Getter
	Boolean intersecaC = false;
	@Setter
	@Getter
	Boolean requiereEquipo = false;
	@Setter
	@Getter
	String usuariosAreas = "";
	@Setter
	@Getter
	Boolean anlizarForestal = false;
	@Setter
	@Getter
	Boolean intersecaSNAP = false;

	@Setter
	@Getter
	Boolean intersecaBP = false;
	@Setter
	@Getter
	Boolean intersecaPFE = false;

	@Setter
	@Getter
	String areaActiva = "";

	@Setter
	@Getter
	List<String> usuarios;

	private byte[] fichaTecnica;

	private byte[] fichaTecnicaMJ;

	@Getter
	@Setter
	private StreamedContent fichaTecnicaD;

	@PostConstruct
	public void init() {

		try {
			consolidado = false;
			observacion = "";
			requiereFacilitadores = false;
			Map<String, String> params = FacesContext.getCurrentInstance()
					.getExternalContext().getRequestParameterMap();
			String area_tmp = params.get("area");
			if (area_tmp != null && !area_tmp.isEmpty()) {
				area = area_tmp;

				if (area.equalsIgnoreCase("General")
						|| area.equalsIgnoreCase("Social")|| area.equalsIgnoreCase("Consolidado")) {
					requiereFacilitadores = true;
				}
			}
			// seleccionar el proyecto del proceso activo
			Map<String, Object> variables = procesoFacade
					.recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
							.getProcessInstanceId());

			Integer idProyecto = Integer.parseInt((String) variables
					.get(Constantes.ID_PROYECTO));
			if (idProyecto != null) {
				tdrEia = tdrFacade.getTdrEiaLicenciaPorIdProyecto(idProyecto);
				if (tdrEia != null) {
					observacionTdrEiaLiciencia = observacionTdrEiaFacade
							.getObservacionTdrEiaLicienciaPorIdTdrComponente(
									tdrEia.getId(), area);

					String observacion_tmp = (String) variables.get("informe"
							+ area + "Observacion");
					if (observacion_tmp != null && !observacion_tmp.isEmpty()) {
						observacion = observacion_tmp;
					} else {
						String observacionGeneralTmp = (String) variables
								.get("informeObservaciones");
						if (observacionGeneralTmp != null
								&& !observacionGeneralTmp.isEmpty()) {
							observacion = observacionGeneralTmp;
						}

					}

				}
			}
			if (area.equals("Consolidado")) {
				consolidado = true;
				if (observacionTdrEiaLiciencia == null) {
					observacionTdrEiaLiciencia = new ObservacionTdrEiaLiciencia();
					observacionTdrEiaLiciencia.setComponente(area);
					observacionTdrEiaLiciencia.setTdrEia(tdrEia);
				}
				requierePronunciamiento = Boolean
						.parseBoolean((String) variables
								.get("requierePronunciamiento"));
				intersecaC = Boolean.parseBoolean((String) variables
						.get("intersecaC"));
				requiereEquipo = Boolean.parseBoolean((String) variables
						.get("requiereEquipo"));
				if (requiereEquipo) {
					// fichaTecnicaMap = new HashMap<String, StreamedContent>();
					usuariosAreas = (String) variables.get("usuariosA");
					// usuarios = (List<String>) Arrays.asList(usuariosAreas
					// .split("\\s*,\\s*"));
					String[] areasTmp = usuariosAreas.split("\\s*,\\s*");
					usuarios = new ArrayList<String>();
					for (String areaInforme : areasTmp) {
						usuarios.add(areaInforme);
						System.out.println(area_tmp);
					}

					// for (String usuarioArea : usuarios) {
					// try {
					// // cargarFichero(usuarioArea);
					// } catch (Exception e) {
					// 
					// e.printStackTrace();
					// }
					//
					// }
				} else {
					usuarios = new ArrayList<>();
				}
				if (requierePronunciamiento) {
					try {
						fichaTecnicaMJ = tdrFacade
								.recuperarInformeTdrPronunciamiento(tdrEia
										.getId());
						getStreamMJ();
					} catch (Exception e) {
						e.printStackTrace();
					}
					usuarios.add(new String("PronunciamientoMJ"));
				}

				// for (int i = 0; i < usuarios.size(); i++) {
				// }
				anlizarForestal = Boolean.parseBoolean((String) variables
						.get("anlizarForestal"));
				if (intersecaC && !requierePronunciamiento) {

					intersecaSNAP = Boolean.parseBoolean((String) variables
							.get("intersecaSNAP"));
					intersecaBP = Boolean.parseBoolean((String) variables
							.get("intersecaBP"));
					intersecaPFE = Boolean.parseBoolean((String) variables
							.get("intersecaPFE"));

					if (intersecaSNAP) {
						usuarios.add("Biodiversidad");
					}

				}
				if (intersecaBP || intersecaPFE || anlizarForestal) {
					usuarios.add("Forestal");
				}

			} else {
				if (observacionTdrEiaLiciencia == null) {

					JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
				} else {
					observacionTdrEiaLiciencia.setObs_antecedentes(true);
					if (observacionTdrEiaLiciencia.getObservacion() == null
							|| observacionTdrEiaLiciencia.getObservacion()
									.isEmpty()) {
						observacionTdrEiaLiciencia
								.setObservacion(formarObservacionesConjunta(observacionTdrEiaLiciencia));
					}
				}
			}
		} catch (JbpmException e) {
			LOGGER.error("Error al obtener la información.", e);
		}
	}

	private String formarObservacionesConjunta(
			ObservacionTdrEiaLiciencia observacionTdr) {

		String observacion = "<p>Observaciones</p>";
		observacion += observacionTdr.getFichaTecnica().isEmpty() ? "" : "<br>"
				+ observacionTdr.getFichaTecnica();
		observacion += observacionTdr.getIntroduccion().isEmpty() ? "" : "<br>"
				+ observacionTdr.getIntroduccion();
		observacion += observacionTdr.getDiagnosticoAmbientalLineaBase()
				.isEmpty() ? "" : "<br>"
				+ observacionTdr.getDiagnosticoAmbientalLineaBase();
		observacion += observacionTdr.getDescripcionProyecto().isEmpty() ? ""
				: "<br>" + observacionTdr.getDescripcionProyecto();
		observacion += observacionTdr.getDeterminacionAreaInfluencia()
				.isEmpty() ? "" : "<br>"
				+ observacionTdr.getDeterminacionAreaInfluencia();
		observacion += observacionTdr.getIdentificacionEvaluacionValoracion()
				.isEmpty() ? "" : "<br>"
				+ observacionTdr.getIdentificacionEvaluacionValoracion();
		observacion += observacionTdr.getIdentificacionSitiosContaminados()
				.isEmpty() ? "" : "<br>"
				+ observacionTdr.getIdentificacionSitiosContaminados();
		observacion += observacionTdr.getPlanManejoAmbiental().isEmpty() ? ""
				: "<br>" + observacionTdr.getPlanManejoAmbiental();
		observacion += observacionTdr.getPlanMonitoreo().isEmpty() ? ""
				: "<br>" + observacionTdr.getPlanMonitoreo();
		observacion += observacionTdr.getInventarioForestal().isEmpty() ? ""
				: "<br>" + observacionTdr.getInventarioForestal();
		observacion += observacionTdr.getAnexos().isEmpty() ? "" : "<br>"
				+ observacionTdr.getAnexos();

		return observacion;
	}

	public void cargarFichero(String areaExterna) {
		try {
			if (areaExterna.equals("PronunciamientoMJ")) {

				fichaTecnica = tdrFacade
						.recuperarInformeTdrPronunciamiento(tdrEia.getId());
			} else {
				fichaTecnica = tdrFacade.recuperarInformeTdrArea(
						tdrEia.getId(), areaExterna);
			}

			areaActiva = areaExterna;
			fichaTecnicaD = getStream();
		} catch (Exception e) {

		}
	}

	public StreamedContent getStream() throws Exception {
		DefaultStreamedContent content = null;
		if (fichaTecnica != null) {

			content = new DefaultStreamedContent(new ByteArrayInputStream(
					fichaTecnica));
			content.setName("Ficha_Tecnica_TDR_" + areaActiva + ".pdf");
		}
		return content;

	}

	public StreamedContent getStreamMJ() throws Exception {
		DefaultStreamedContent content = null;
		if (fichaTecnicaMJ != null) {

			content = new DefaultStreamedContent(new ByteArrayInputStream(
					fichaTecnicaMJ));
			content.setName("Ficha_Tecnica_TDR_MJ.pdf");
		}
		return content;

	}

	public void guardarInformeTecnico(
			ObservacionTdrEiaLiciencia obTdrEiaLiciencia) {
		crudServiceBean.saveOrUpdate(obTdrEiaLiciencia);
	}

}
