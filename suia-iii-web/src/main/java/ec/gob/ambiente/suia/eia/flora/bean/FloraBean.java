package ec.gob.ambiente.suia.eia.flora.bean;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.component.tabview.Tab;
import org.primefaces.component.tabview.TabView;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

import ec.gob.ambiente.suia.catalogos.facade.CatalogoGeneralFacade;
import ec.gob.ambiente.suia.domain.CatalogoGeneral;
import ec.gob.ambiente.suia.domain.CoordenadaGeneral;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.FloraEspecie;
import ec.gob.ambiente.suia.domain.PuntosMuestreoFlora;
import ec.gob.ambiente.suia.domain.TipoCatalogo;
import ec.gob.ambiente.suia.eia.flora.controller.FloraController;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean(name = "floraBean")
@ViewScoped
public class FloraBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8232894674050508986L;

	private static final Logger LOG = Logger.getLogger(FloraBean.class);

	// Campos

	@Getter
	@Setter
	private Integer eiaId;
	@Getter
	@Setter
	private Integer id;
	@Getter
	@Setter
	private Boolean aplicaFlora;
	@Getter
	@Setter
	private CatalogoGeneral tipoMuestreo;
	@Getter
	@Setter
	private CatalogoGeneral metodologia;
	@Getter
	@Setter
	private String esfuerzoMuestreo;
	@Getter
	@Setter
	private FloraCualitativoBean floraCualitativoBean;
	@Getter
	@Setter
	private FloraCualitativoEspecieBean floraCualitativoEspecieBean;
	@Getter
	@Setter
	private FloraCuantitativoBean floraCuantitativoBean;
	@Getter
	@Setter
	private FloraCuantitativoEspecieBean floraCuantitativoEspecieBean;

	// Listas
	@Getter
	@Setter
	private List<CatalogoGeneral> tiposMuestreo;
	@Getter
	@Setter
	private List<CatalogoGeneral> metodologias;
	@Getter
	@Setter
	private List<FloraCualitativoBean> floraCualitativos;
	@Getter
	@Setter
	private List<CatalogoGeneral> tiposVegetacion;
	@Getter
	@Setter
	private List<FloraCualitativoEspecieBean> floraCualitativosEspecie;
	@Getter
	@Setter
	private List<FloraCuantitativoEspecieBean> floraCuantitativosEspecie;
	@Getter
	@Setter
	private List<CatalogoGeneral> habitos;
	@Getter
	@Setter
	private List<CatalogoGeneral> estadosIndividuo;
	@Getter
	@Setter
	private List<CatalogoGeneral> usos;
	@Getter
	@Setter
	private List<CatalogoGeneral> origenes;
	@Getter
	@Setter
	private List<CatalogoGeneral> listaCities;
	@Getter
	@Setter
	private List<CatalogoGeneral> listaLibroRojo;
	@Getter
	@Setter
	private List<CatalogoGeneral> uicnsInternacional;
	@Getter
	@Setter
	private List<CatalogoGeneral> nivelesIdentificacion;
	@Getter
	@Setter
	private List<FloraCuantitativoBean> floraCuantitativos;
	@Getter
	@Setter
	private List<FloraAgrupadoEspecieBean> listaAgrupadoPorEspecie;

	@Getter
	@Setter
	private List<List<Object>> eliminados;

	// Archivos

	@Getter
	@Setter
	private byte[] anexoMetodologia;
	@Getter
	@Setter
	private String anexoMetodologiaContentType;
	@Getter
	@Setter
	private String anexoMetodologiaName;

	// Contador
	int contadorCualitativo = 1;
	int contadorCuantitativo = 1;
	int contadorFlora = 1;
	int contadorFlora2 = 1;

	@Getter
	@Setter
	boolean archivoEditado = false;

	// EJBS
	@EJB
	CatalogoGeneralFacade catalogoGeneralFacade;

	@Inject
	FloraController floraController;

	@PostConstruct
	void init() {
		if (tiposMuestreo == null) {
			tiposMuestreo = cargarCatalogos(TipoCatalogo.TIPO_MUESTREO, null);
		}
		if (metodologias == null) {
			metodologias = cargarCatalogos(TipoCatalogo.METODOLOGIA, null);
		}
		if (floraCualitativos == null) {
			floraCualitativos = new ArrayList<FloraCualitativoBean>();
		}
		if (floraCualitativosEspecie == null) {
			floraCualitativosEspecie = new ArrayList<FloraCualitativoEspecieBean>();
		}
		if (floraCuantitativosEspecie == null) {
			floraCuantitativosEspecie = new ArrayList<FloraCuantitativoEspecieBean>();
		}
		if (floraCuantitativos == null) {
			floraCuantitativos = new ArrayList<FloraCuantitativoBean>();
		}
		if (tiposVegetacion == null) {
			tiposVegetacion = cargarCatalogos(TipoCatalogo.TIPO_VEGETACION,
					null);
		}

		if (habitos == null) {
			habitos = cargarCatalogos(TipoCatalogo.HABITO, null);
		}
		if (estadosIndividuo == null) {
			estadosIndividuo = cargarCatalogos(TipoCatalogo.ESTADO_INDIVIDUO,
					null);
		}
		if (usos == null) {
			usos = cargarCatalogos(TipoCatalogo.USO,
					CatalogoGeneral.FLORA_CODIGO);
		}
		if (origenes == null) {
			origenes = cargarCatalogos(TipoCatalogo.ORIGEN, null);
		}
		if (uicnsInternacional == null) {
			uicnsInternacional = cargarCatalogos(
					TipoCatalogo.UICN_INTERNACIONAL, null);
		}
		if (nivelesIdentificacion == null) {
			nivelesIdentificacion = cargarCatalogos(
					TipoCatalogo.NIVEL_IDENTIFICACION, null);
		}
		if (listaCities == null) {
			listaCities = cargarCatalogos(TipoCatalogo.CITIES, null);
		}
		if (listaLibroRojo == null) {
			listaLibroRojo = cargarCatalogos(TipoCatalogo.LIBRO_ROJO,
					CatalogoGeneral.FLORA_CODIGO);
		}
		if (floraCualitativoBean == null) {
			floraCualitativoBean = new FloraCualitativoBean();
		}
		if (floraCualitativoEspecieBean == null) {
			floraCualitativoEspecieBean = new FloraCualitativoEspecieBean();
		}
		if (floraCuantitativoBean == null) {
			floraCuantitativoBean = new FloraCuantitativoBean();
		}
		if (floraCuantitativoEspecieBean == null) {
			floraCuantitativoEspecieBean = new FloraCuantitativoEspecieBean();
		}
		if (eliminados == null) {
			eliminados = new ArrayList<List<Object>>();
		}
		if (listaAgrupadoPorEspecie == null) {
			listaAgrupadoPorEspecie = new ArrayList<FloraAgrupadoEspecieBean>();
		}

		try {
			EstudioImpactoAmbiental eia = (EstudioImpactoAmbiental) JsfUtil
					.devolverObjetoSession(Constantes.SESSION_EIA_OBJECT);
			setEiaId(eia.getId());

			FloraBean floraLlena = floraController.cargarDatosFlora(getEiaId());
			setMetodologia(floraLlena.getMetodologia());
			setAplicaFlora(true);
			setEsfuerzoMuestreo(floraLlena.getEsfuerzoMuestreo());
			setId(floraLlena.getId());
			setTipoMuestreo(floraLlena.getTipoMuestreo());
			setAnexoMetodologia(floraLlena.getAnexoMetodologia());
			setAnexoMetodologiaContentType(floraLlena
					.getAnexoMetodologiaContentType());
			setAnexoMetodologiaName(floraLlena.getAnexoMetodologiaName());

			floraLlena = floraController.cargarDatosPuntoMuestreo(floraLlena
					.getId());
			setFloraCualitativos(floraLlena.getFloraCualitativos());
			setFloraCuantitativos(floraLlena.getFloraCuantitativos());
			setFloraCualitativosEspecie(floraLlena
					.getFloraCualitativosEspecie());
			setFloraCuantitativosEspecie(floraLlena
					.getFloraCuantitativosEspecie());

			mostrarFormPrincipal();
			mostrarTabPrincipal();
			agruparPorEspecie();

		} catch (NumberFormatException e) {
			LOG.error(e.getMessage());
			JsfUtil.addMessageError("Error al cargar los datos de Flora ");
		} catch (Exception e) {
			LOG.error(e.getMessage());
			JsfUtil.addMessageError("Error al cargar los datos de Flora ");
		}
	}

	public List<CatalogoGeneral> cargarCatalogos(int tipoCatalogoId,
			String codigo) {
		try {
			if (codigo != null) {
				return catalogoGeneralFacade.obtenerCatalogoXTipoXCodigo(
						tipoCatalogoId, codigo);
			} else {
				return catalogoGeneralFacade
						.obtenerCatalogoXTipo(tipoCatalogoId);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			JsfUtil.addMessageError("Error al cargar el catalogo "
					+ tipoCatalogoId);
			return null;
		}
	}

	public void mostrarFormPrincipal() {
		if (getAplicaFlora()) {
			UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();

			HtmlPanelGrid pgFormPrincipal = (HtmlPanelGrid) root
					.findComponent(":frm_inicio_flora:pg_form_principal");
			pgFormPrincipal.setStyle("display: inline");
		} else {
			UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();

			HtmlPanelGrid pgFormPrincipal = (HtmlPanelGrid) root
					.findComponent(":frm_inicio_flora:pg_form_principal");
			pgFormPrincipal.setStyle("display: none");
		}
	}

	public void mostrarTabPrincipal() {
		if (getAplicaFlora()) {
			UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
			TabView tabTipoMuestreo = (TabView) root
					.findComponent(":tab_tipo_muestreo");
			tabTipoMuestreo.setStyle("display: inline");

			Tab tabCuantitativo = (Tab) tabTipoMuestreo
					.findComponent("tab_h_cuantitativo");
			Tab tabCualitativo = (Tab) tabTipoMuestreo
					.findComponent("tab_h_cualitativo");

			switch (getTipoMuestreo().getId()) {
			case CatalogoGeneral.CUALITATIVO:
				tabCualitativo.setDisabled(false);
				tabTipoMuestreo.setActiveIndex(0);
				tabCuantitativo.setDisabled(true);
				break;
			case CatalogoGeneral.CUANTITATIVO:
				tabCuantitativo.setDisabled(false);
				tabTipoMuestreo.setActiveIndex(1);
				tabCualitativo.setDisabled(true);
				break;
			case CatalogoGeneral.CUALITATIVO_CUANTITATIVO:
				tabCualitativo.setDisabled(false);
				tabCuantitativo.setDisabled(false);
				tabTipoMuestreo.setActiveIndex(0);
				break;
			default:
				tabCualitativo.setDisabled(false);
				tabCuantitativo.setDisabled(false);
				break;
			}

			SelectOneMenu cmbTipoMuestreo = (SelectOneMenu) root
					.findComponent(":frm_inicio_flora:cmb_tipo_muestreo");
			cmbTipoMuestreo.setDisabled(false);
		} else {
			UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
			TabView tabTipoMuestreo = (TabView) root
					.findComponent(":frm_inicio_flora:tab_tipo_muestreo");
			tabTipoMuestreo.setStyle("display: none");

			Tab tabCuantitativo = (Tab) tabTipoMuestreo
					.findComponent("tab_h_cuantitativo");
			Tab tabCualitativo = (Tab) tabTipoMuestreo
					.findComponent("tab_h_cualitativo");

			tabCualitativo.setDisabled(true);
			tabCuantitativo.setDisabled(true);
		}
	}

	public void agregarPuntoMuestreoCualitativo() {
		if (floraCualitativoBean.isEditar()) {
			getFloraCualitativos().remove(floraCualitativoBean);
		} else {
			floraCualitativoBean.setContador(contadorFlora);
			contadorFlora++;
		}
		getFloraCualitativos().add(floraCualitativoBean);
		floraCualitativoBean = new FloraCualitativoBean();
		RequestContext.getCurrentInstance().addCallbackParam("puntoIn", true);
	}

	public void eliminarPuntoMuestreoCualitativo(
			FloraCualitativoBean floraCualitativoBean) {
		getFloraCualitativos().remove(floraCualitativoBean);
		if (floraCualitativoBean.getId() != null) {
			List<Object> objectos = new ArrayList<Object>();
			objectos.add(PuntosMuestreoFlora.class);
			objectos.add(floraCualitativoBean.getId());
			eliminados.add(objectos);
			if (floraCualitativoBean.getP1Id() != null) {
				objectos = new ArrayList<Object>();
				objectos.add(CoordenadaGeneral.class);
				objectos.add(floraCualitativoBean.getP1Id());
				eliminados.add(objectos);
			}
			if (floraCualitativoBean.getP2Id() != null) {
				objectos = new ArrayList<Object>();
				objectos.add(CoordenadaGeneral.class);
				objectos.add(floraCualitativoBean.getP2Id());
				eliminados.add(objectos);
			}
			if (floraCualitativoBean.getP3Id() != null) {
				objectos = new ArrayList<Object>();
				objectos.add(CoordenadaGeneral.class);
				objectos.add(floraCualitativoBean.getP3Id());
				eliminados.add(objectos);
			}
			if (floraCuantitativoBean.getP4Id() != null) {
				objectos = new ArrayList<Object>();
				objectos.add(CoordenadaGeneral.class);
				objectos.add(floraCualitativoBean.getP4Id());
				eliminados.add(objectos);
			}

		}
	}

	public void editarPuntoMuestreoCualitativo(
			FloraCualitativoBean floraCualitativoBean) {
		floraCualitativoBean.setEditar(true);
		setFloraCualitativoBean(floraCualitativoBean);
	}

	public void agregarPuntoEspecieCualitativo() {
		if (floraCualitativoEspecieBean.isEditar()) {
			getFloraCualitativosEspecie().remove(floraCualitativoEspecieBean);
		} else {
			floraCualitativoEspecieBean.setContador(contadorCualitativo);
			contadorCualitativo++;
		}
		getFloraCualitativosEspecie().add(floraCualitativoEspecieBean);
		floraCualitativoEspecieBean = new FloraCualitativoEspecieBean();
		RequestContext.getCurrentInstance().addCallbackParam("puntoIn", true);
	}

	public void eliminarPuntoEspecieCualitativo(
			FloraCualitativoEspecieBean floraCualitativoEspecieBean) {
		getFloraCualitativosEspecie().remove(floraCualitativoEspecieBean);
		if (floraCualitativoEspecieBean.getId() != null) {
			List<Object> objectos = new ArrayList<Object>();
			objectos.add(FloraEspecie.class);
			objectos.add(floraCualitativoEspecieBean.getId());
			eliminados.add(objectos);
		}
	}

	public void editarPuntoEspecieCualitativo(
			FloraCualitativoEspecieBean floraCualitativoEspecieBean) {
		floraCualitativoEspecieBean.setEditar(true);
		setFloraCualitativoEspecieBean(floraCualitativoEspecieBean);
	}

	public void agregarPuntoMuestreoCuantitativo() {
		if (floraCuantitativoBean.isEditar()) {
			getFloraCuantitativos().remove(floraCuantitativoBean);
		} else {
			floraCuantitativoBean.setContador(contadorFlora2);
			contadorCualitativo++;
		}
		getFloraCuantitativos().add(floraCuantitativoBean);
		floraCuantitativoBean = new FloraCuantitativoBean();
		RequestContext.getCurrentInstance().addCallbackParam("puntoIn", true);
	}

	public void eliminarPuntoMuestreoCuantitativo(
			FloraCuantitativoBean floraCuantitativoBean) {
		getFloraCuantitativos().remove(floraCuantitativoBean);
		if (floraCuantitativoBean.getId() != null) {
			List<Object> objectos = new ArrayList<Object>();
			objectos.add(PuntosMuestreoFlora.class);
			objectos.add(floraCuantitativoBean.getId());
			eliminados.add(objectos);
			if (floraCuantitativoBean.getP1Id() != null) {
				objectos = new ArrayList<Object>();
				objectos.add(CoordenadaGeneral.class);
				objectos.add(floraCuantitativoBean.getP1Id());
				eliminados.add(objectos);
			}
			if (floraCuantitativoBean.getP2Id() != null) {
				objectos = new ArrayList<Object>();
				objectos.add(CoordenadaGeneral.class);
				objectos.add(floraCuantitativoBean.getP2Id());
				eliminados.add(objectos);
			}
			if (floraCuantitativoBean.getP3Id() != null) {
				objectos = new ArrayList<Object>();
				objectos.add(CoordenadaGeneral.class);
				objectos.add(floraCuantitativoBean.getP3Id());
				eliminados.add(objectos);
			}
			if (floraCuantitativoBean.getP4Id() != null) {
				objectos = new ArrayList<Object>();
				objectos.add(CoordenadaGeneral.class);
				objectos.add(floraCuantitativoBean.getP4Id());
				eliminados.add(objectos);
			}
		}
	}

	public void editarPuntoMuestreoCuantitativo(
			FloraCuantitativoBean floraCuantitativoBean) {
		floraCuantitativoBean.setEditar(true);
		setFloraCuantitativoBean(floraCuantitativoBean);
	}

	public void agregarPuntoEspecieCuantitativo() {
		if (floraCuantitativoEspecieBean.isEditar()) {
			getFloraCuantitativosEspecie().remove(floraCuantitativoEspecieBean);
		} else {
			floraCuantitativoEspecieBean.setContador(contadorCuantitativo);
			contadorCuantitativo++;
		}
		getFloraCuantitativosEspecie().add(floraCuantitativoEspecieBean);
		floraCuantitativoEspecieBean = new FloraCuantitativoEspecieBean();
		RequestContext.getCurrentInstance().addCallbackParam("puntoIn", true);
	}

	public void eliminarPuntoEspecieCuantitativo(
			FloraCuantitativoEspecieBean floraCuantitativoEspecieBean) {
		getFloraCuantitativosEspecie().remove(floraCuantitativoEspecieBean);
		if (floraCuantitativoEspecieBean.getId() != null) {
			List<Object> objectos = new ArrayList<Object>();
			objectos.add(FloraEspecie.class);
			objectos.add(floraCuantitativoEspecieBean.getId());
			eliminados.add(objectos);
		}
	}

	public void editarPuntoEspecieCuantitativo(
			FloraCuantitativoEspecieBean floraCuantitativoEspecieBean) {
		floraCuantitativoEspecieBean.setEditar(true);
		setFloraCuantitativoEspecieBean(floraCuantitativoEspecieBean);
	}

	public void guardarCualitativo() {
		try {
			floraController.guardarFloraCuanlitativo(this);
			JsfUtil.addMessageInfo("Grabado exitoso");
		} catch (Exception e) {
			JsfUtil.addMessageError("Error al grabar el registro");
		}
	}

	public String guardarCuantitativo() {
		try {
			floraController.guardarFloraCuantitativo(this);
			JsfUtil.addMessageInfo("Grabado exitoso");
			return JsfUtil.actionNavigateTo("/pages/eia/default.jsf");
		} catch (Exception e) {
			JsfUtil.addMessageError("Error al grabar el registro");
			return null;
		}
	}

	public String dateToString(java.util.Date fecha) {
		try {
			DateFormat fechaFormat = new SimpleDateFormat("yyyy/MM/dd");
			return fechaFormat.format(fecha);
		} catch (Exception e) {
			return null;
		}
	}

	public void handleFileUpload(FileUploadEvent event) {
		if (getAnexoMetodologia() != null) {
			setArchivoEditado(true);
		}
		setAnexoMetodologia(event.getFile().getContents());
		setAnexoMetodologiaContentType(event.getFile().getContentType());
		setAnexoMetodologiaName(event.getFile().getFileName());
		FacesMessage message = new FacesMessage("Archivo cargado con éxito: ",
				event.getFile().getFileName());
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public void handleFileUploadEspecie(FileUploadEvent event) {
		if (getFloraCualitativoEspecieBean().getFotoEspecie() != null) {
			getFloraCualitativoEspecieBean().setArchivoEditado(true);
		}
		getFloraCualitativoEspecieBean().setFotoEspecie(
				event.getFile().getContents());
		getFloraCualitativoEspecieBean().setFotoEspecieContentType(
				event.getFile().getContentType());
		getFloraCualitativoEspecieBean().setFotoEspecieName(
				event.getFile().getFileName());
		FacesMessage message = new FacesMessage("Archivo cargado con éxito: ",
				event.getFile().getFileName());
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public void handleFileUploadEspecieCuantitativo(FileUploadEvent event) {
		if (getFloraCuantitativoEspecieBean().getFotoEspecie() != null) {
			getFloraCuantitativoEspecieBean().setArchivoEditado(true);
		}
		getFloraCuantitativoEspecieBean().setFotoEspecie(
				event.getFile().getContents());
		getFloraCuantitativoEspecieBean().setFotoEspecieContentType(
				event.getFile().getContentType());
		getFloraCuantitativoEspecieBean().setFotoEspecieName(
				event.getFile().getFileName());
		FacesMessage message = new FacesMessage("Archivo cargado con éxito: ",
				event.getFile().getFileName());
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public String cancelar() {
		return JsfUtil.actionNavigateTo("/pages/eia/default.jsf");
	}

	public void agruparPorEspecie() {
		FloraAgrupadoEspecieBean agrupadoEspecie = null;
		Object[] tmpEspecies = floraCuantitativosEspecie.toArray();

		int i = 1;
		int j = 0;
		for (Object especie : tmpEspecies) {
			if (especie != null) {
				agrupadoEspecie = new FloraAgrupadoEspecieBean();
				String tmpEspecie = ((FloraCuantitativoEspecieBean) especie)
						.getEspecie();
				for (Object fEspecie : tmpEspecies) {
					if (fEspecie != null) {
						FloraCuantitativoEspecieBean especie2 = (FloraCuantitativoEspecieBean) fEspecie;
						if (tmpEspecie.equals(especie2.getEspecie())) {

							agrupadoEspecie.setId(null);
							agrupadoEspecie.setContador(i);
							if (!agrupadoEspecie.getPuntoMuestreo().contains(
									especie2.getFloraCuantitativo()
											.getPuntoMuestreo())) {
								agrupadoEspecie
										.setPuntoMuestreo(agrupadoEspecie
												.getPuntoMuestreo()
												+ " - "
												+ especie2
														.getFloraCuantitativo()
														.getPuntoMuestreo());
							}
							agrupadoEspecie.setFamilia(especie2.getFamilia());
							agrupadoEspecie.setGenero(especie2.getGenero());
							agrupadoEspecie.setEspecie(especie2.getEspecie());
							agrupadoEspecie.setNombreCientifico(especie2
									.getNombreCientifico());
							agrupadoEspecie.setNombreComun(especie2
									.getNombreComun());

							if (!agrupadoEspecie.getTipoVegetacion().contains(
									especie2.getTipoVegetacion()
											.getDescripcion())) {
								agrupadoEspecie
										.setTipoVegetacion(agrupadoEspecie
												.getTipoVegetacion()
												+ " - "
												+ especie2.getTipoVegetacion()
														.getDescripcion());
							}

							if (!agrupadoEspecie.getOrigen().contains(
									especie2.getOrigen().getDescripcion())) {
								agrupadoEspecie
										.setOrigen(agrupadoEspecie.getOrigen()
												+ " - "
												+ especie2.getOrigen()
														.getDescripcion());
							}

							if (!agrupadoEspecie.getUicnInternacional()
									.contains(
											especie2.getUicnInternacional()
													.getDescripcion())) {
								agrupadoEspecie
										.setUicnInternacional(agrupadoEspecie
												.getUicnInternacional()
												+ " - "
												+ especie2
														.getUicnInternacional()
														.getDescripcion());
							}

							if (!agrupadoEspecie.getCities().contains(
									especie2.getCities().getDescripcion())) {
								agrupadoEspecie
										.setCities(agrupadoEspecie.getCities()
												+ " - "
												+ especie2.getCities()
														.getDescripcion());
							}

							if (!agrupadoEspecie.getLibroRojo().contains(
									especie2.getLibroRojo().getDescripcion())) {
								agrupadoEspecie.setLibroRojo(agrupadoEspecie
										.getLibroRojo()
										+ " - "
										+ especie2.getLibroRojo()
												.getDescripcion());
								agrupadoEspecie.setSumDap(agrupadoEspecie
										.getSumDap() + especie2.getDap());
							}

							tmpEspecies[j] = null;
							j++;
						}
					}
				}
				i++;

				listaAgrupadoPorEspecie.add(agrupadoEspecie);
			}
		}
	}
	
	public void handleFileUploadAgrupado(FileUploadEvent event) {
		FacesMessage message = new FacesMessage("Archivo cargado con éxito: ",
				event.getFile().getFileName());
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

}
