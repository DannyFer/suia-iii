package ec.gob.ambiente.prevencion.registrogeneradordesechos.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.prevencion.registrogeneradordesechos.controller.RegistroGeneradorDesechoController;
import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.comun.bean.AdicionarDesechosPeligrososBean;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.GeneradorDesechosDesechoPeligrosoDatosGenerales;
import ec.gob.ambiente.suia.domain.GeneradorDesechosDesechoPeligrosoPuntoGeneracion;
import ec.gob.ambiente.suia.domain.PuntoGeneracion;
import ec.gob.ambiente.suia.domain.TipoEstadoFisico;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class DatosDesechosBean extends RegistroGeneradorBaseBean {

	private static final long serialVersionUID = 4210245906204787729L;

	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;

	private GeneradorDesechosDesechoPeligrosoDatosGenerales desechoPeligrosoDatosGenerales;

	@Setter
	private List<GeneradorDesechosDesechoPeligrosoDatosGenerales> desechosPeligrososDatosGenerales;

	@Getter
	private List<TipoEstadoFisico> tiposEstadosFisicos;

	@PostConstruct
	private void initDatos() {
		tiposEstadosFisicos = registroGeneradorDesechosFacade.getTiposEstadosFisicos();
	}

	public void aceptar() {
		desechoPeligrosoDatosGenerales.setCategoriaDesechoPeligroso(JsfUtil
				.getBean(CategoriaDesechoPeligrosoBean.class).getCategoriaDesechoSeleccionada());

		desechoPeligrosoDatosGenerales.setGeneradorDesechosDesechoPeligrosoPuntosGeneracion(null);
		if (desechoPeligrosoDatosGenerales.getGeneradorDesechosDesechoPeligrosoPuntosGeneracion() == null)
			desechoPeligrosoDatosGenerales
					.setGeneradorDesechosDesechoPeligrosoPuntosGeneracion(new ArrayList<GeneradorDesechosDesechoPeligrosoPuntoGeneracion>());

		List<PuntoGeneracion> puntosSeleccionados = JsfUtil.getBean(PuntosGeneracionBean.class)
				.getPuntosGeneracionSeleccionados();
		for (PuntoGeneracion puntoGeneracion : puntosSeleccionados) {
			desechoPeligrosoDatosGenerales.getGeneradorDesechosDesechoPeligrosoPuntosGeneracion().add(
					new GeneradorDesechosDesechoPeligrosoPuntoGeneracion(puntoGeneracion));
		}

		if (!desechoPeligrosoDatosGenerales.getDesechoPeligroso().isDesechoES_04()
				&& !desechoPeligrosoDatosGenerales.getDesechoPeligroso().isDesechoES_06())
			desechoPeligrosoDatosGenerales.setCantidadUnidades(0);

		if (!getDesechosPeligrososDatosGenerales().contains(desechoPeligrosoDatosGenerales))
			getDesechosPeligrososDatosGenerales().add(desechoPeligrosoDatosGenerales);
		/**
		 * autor: vear
		 * fecha: 25/02/2016
		 * objetivo: persistir solo el paso 5 del wizard, como objeto no como lista
		 */
		try {
			JsfUtil.getBean(RegistroGeneradorDesechoController.class).guardarPaso5(desechoPeligrosoDatosGenerales);
		} catch (CmisAlfrescoException e) {
			e.printStackTrace();
		}
		////////////////////////
		cancelar();
		JsfUtil.addCallbackParam("addDatosGenerales");
	}

	public void eliminar(GeneradorDesechosDesechoPeligrosoDatosGenerales datosGenerales) {
		getDesechosPeligrososDatosGenerales().remove(datosGenerales);
		JsfUtil.getBean(AdicionarDesechosPeligrososBean.class).setModificacionesEliminaciones(true);
		/**
		 * autor: vear
		 * fecha: 03/03/2016
		 * objetivo: eliminar de la lista y de la bdd tambien
		 */
		JsfUtil.getBean(RegistroGeneradorDesechoController.class).eliminarDesechoPeligrosoDatosGenerales(datosGenerales);
	}

	public void editar(GeneradorDesechosDesechoPeligrosoDatosGenerales datosGenerales) {
		this.desechoPeligrosoDatosGenerales = datosGenerales;
		this.setEditar(true);
		JsfUtil.getBean(CategoriaDesechoPeligrosoBean.class).setCategoriaDesechoSeleccionada(
				desechoPeligrosoDatosGenerales.getCategoriaDesechoPeligroso());
		List<PuntoGeneracion> puntosSeleccionados = new ArrayList<>();
		for (GeneradorDesechosDesechoPeligrosoPuntoGeneracion punto : desechoPeligrosoDatosGenerales
				.getGeneradorDesechosDesechoPeligrosoPuntosGeneracion()) {
			puntosSeleccionados.add(punto.getPuntoGeneracion());
		}
		JsfUtil.getBean(PuntosGeneracionBean.class).setPuntosGeneracionSeleccionados(puntosSeleccionados);
		JsfUtil.getBean(AdicionarDesechosPeligrososBean.class).setModificacionesEliminaciones(true);
	}

	public void cancelar() {
		desechoPeligrosoDatosGenerales = null;
		JsfUtil.getBean(PuntosGeneracionBean.class).reset();
		JsfUtil.getBean(CategoriaDesechoPeligrosoBean.class).resetSelection();
	}

	public void validateData(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		if (JsfUtil.getBean(CategoriaDesechoPeligrosoBean.class).getCategoriaDesechoSeleccionada() == null) {
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Debe seleccionar la categoría del desecho.", null));
		}
		if (JsfUtil.getBean(PuntosGeneracionBean.class).getPuntosGeneracionSeleccionados().isEmpty()) {
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Debe seleccionar, al menos, un punto de generación.", null));
		}
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}

	public void validateDatosDesechos(FacesContext context, UIComponent validate, Object value) {
		if (getDesechosPeligrososDatosGenerales().isEmpty()
				|| getDesechosPeligrososDatosGenerales().size() < JsfUtil
						.getBean(AdicionarDesechosPeligrososBean.class).getDesechosSeleccionados().size())
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Debe completar, para todos los desechos seleccionados, los datos asociados.", null));
	}

	public List<DesechoPeligroso> getDesechosPeligrosoDisponibles() {
		List<DesechoPeligroso> result = new ArrayList<>();
		List<DesechoPeligroso> selected = JsfUtil.getBean(AdicionarDesechosPeligrososBean.class)
				.getDesechosSeleccionados();
		result.addAll(selected);
		for (GeneradorDesechosDesechoPeligrosoDatosGenerales desechoPeligrosoDatosGenerales : getDesechosPeligrososDatosGenerales()) {
			if (isEditar() && desechoPeligrosoDatosGenerales.equals(this.desechoPeligrosoDatosGenerales))
				continue;
			if (desechoPeligrosoDatosGenerales.getDesechoPeligroso() != null)
				result.remove(desechoPeligrosoDatosGenerales.getDesechoPeligroso());
		}
		return result;
	}

	public GeneradorDesechosDesechoPeligrosoDatosGenerales getDesechoPeligrosoDatosGenerales() {
		return desechoPeligrosoDatosGenerales == null ? desechoPeligrosoDatosGenerales = new GeneradorDesechosDesechoPeligrosoDatosGenerales()
				: desechoPeligrosoDatosGenerales;
	}

	public List<GeneradorDesechosDesechoPeligrosoDatosGenerales> getDesechosPeligrososDatosGenerales() {
		return desechosPeligrososDatosGenerales == null ? desechosPeligrososDatosGenerales = new ArrayList<>()
				: desechosPeligrososDatosGenerales;
	}

	public void validacionesDesechos(DesechoPeligroso desechoPeligroso) {
		GeneradorDesechosDesechoPeligrosoDatosGenerales datosGeneralesToDelete = null;
		for (GeneradorDesechosDesechoPeligrosoDatosGenerales datosGenerales : getDesechosPeligrososDatosGenerales()) {
			if (datosGenerales.getDesechoPeligroso().equals(desechoPeligroso)) {
				datosGeneralesToDelete = datosGenerales;
				break;
			}
		}
		if (datosGeneralesToDelete != null)
			getDesechosPeligrososDatosGenerales().remove(datosGeneralesToDelete);
	}

	public GeneradorDesechosDesechoPeligrosoDatosGenerales getGeneradorDesechosDesechoPeligrosoDatosGeneralesPorDesecho(
			DesechoPeligroso desechoPeligroso) {
		GeneradorDesechosDesechoPeligrosoDatosGenerales resultado = new GeneradorDesechosDesechoPeligrosoDatosGenerales();
		for (GeneradorDesechosDesechoPeligrosoDatosGenerales datosGenerales : getDesechosPeligrososDatosGenerales()) {
			if (datosGenerales.getDesechoPeligroso().equals(desechoPeligroso)) {
				resultado = datosGenerales;
				break;
			}
		}
		return resultado;
	}

}
