package ec.gob.ambiente.prevencion.registrogeneradordesechos.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import ec.gob.ambiente.prevencion.registrogeneradordesechos.controller.RegistroGeneradorDesechoController;
import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.comun.bean.AdicionarDesechosPeligrososBean;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.GeneradorDesechosEliminador;
import ec.gob.ambiente.suia.domain.GeneradorDesechosRecolector;
import ec.gob.ambiente.suia.domain.PrestadorServiciosDesechoPeligroso;
import ec.gob.ambiente.suia.domain.PuntoEliminacion;
import ec.gob.ambiente.suia.domain.PuntoEliminacionPrestadorServicioDesechoPeligroso;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class EliminacionDesechosInstalacionBean extends RegistroGeneradorBaseBean {

	private static final long serialVersionUID = -1321645588667861384L;

	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;

	@Getter
	@Setter
	private boolean tipoPermisoLicencia = true;

	@Setter
	private List<PuntoEliminacion> puntosEliminacion;

	@Setter
	private PuntoEliminacion puntoEliminacion;

	public List<DesechoPeligroso> getDesechosPeligrosoDisponibles() {
		List<DesechoPeligroso> result = new ArrayList<>();
		List<DesechoPeligroso> selected = getDesechosDisponiblesEliminacionDentroInstalacion();
		result.addAll(selected);
		for (PuntoEliminacion puntoEliminacion : getPuntosEliminacion()) {
			if (isEditar() && puntoEliminacion.equals(this.puntoEliminacion))
				continue;
			if (puntoEliminacion.getDesechoPeligroso() != null
					&& getDesechosAEliminarDentroInstalacion().contains(puntoEliminacion.getDesechoPeligroso()))
				result.remove(puntoEliminacion.getDesechoPeligroso());
		}
		return result;
	}

	public List<DesechoPeligroso> getDesechosDisponiblesEliminacionDentroInstalacion() {
		if (JsfUtil.getBean(RecoleccionTransporteDesechosBean.class) == null
				|| JsfUtil.getBean(RecoleccionTransporteDesechosBean.class).getDesechosATransportar() == null
				|| JsfUtil.getBean(RecoleccionTransporteDesechosBean.class).getDesechosATransportar().isEmpty())
			return JsfUtil.getBean(AdicionarDesechosPeligrososBean.class).getDesechosSeleccionados();
		else {
			List<DesechoPeligroso> result = new ArrayList<DesechoPeligroso>();
			List<DesechoPeligroso> desechosSeleccionados = JsfUtil.getBean(AdicionarDesechosPeligrososBean.class)
					.getDesechosSeleccionados();
			int longitud = desechosSeleccionados.size();
			List<DesechoPeligroso> desechosATransporta = JsfUtil.getBean(RecoleccionTransporteDesechosBean.class)
					.getDesechosATransportar();
			for (int i = 0; i < longitud; i++) {
				if (!desechosATransporta.contains(desechosSeleccionados.get(i))
						|| (isEditar() && getPuntoEliminacion().getDesechoPeligroso() != null && getPuntoEliminacion()
						.getDesechoPeligroso().equals(desechosSeleccionados.get(i)))) {
					result.add(desechosSeleccionados.get(i));
				}
			}
			return result;
		}
	}

	public List<PuntoEliminacion> getPuntosEliminacion() {
		return puntosEliminacion == null ? puntosEliminacion = new ArrayList<PuntoEliminacion>() : puntosEliminacion;
	}

	public PuntoEliminacion getPuntoEliminacion() {
		return puntoEliminacion == null ? puntoEliminacion = new PuntoEliminacion() : puntoEliminacion;
	}

	public void aceptar() {
		if (JsfUtil.getBean(TipoEliminacionDesechoBean.class).getTipoEliminacionDesechoSeleccionada() != null) {
			puntoEliminacion.setTipoEliminacionDesecho(JsfUtil.getBean(TipoEliminacionDesechoBean.class)
					.getTipoEliminacionDesechoSeleccionada());
			puntoEliminacion.setTextoAsociadoOpcionOtro(JsfUtil.getBean(TipoEliminacionDesechoBean.class)
					.getTextoAsociadoOpcionOtro());
		}

		if (JsfUtil.getBean(PermisoAmbientalBean.class).getPuntoEliminacionPrestadorServicioLista() != null) {
			if (JsfUtil.getBean(PermisoAmbientalBean.class).getPuntoEliminacionPrestadorServicioLista().size() == 1) {
				JsfUtil.getBean(PermisoAmbientalBean.class).getPuntoEliminacionPrestadorServicioLista().get(0)
						.setCantidadToneladas(puntoEliminacion.getCantidadAnualToneladas());
				if (puntoEliminacion.getDesechoPeligroso().isDesechoES_04()
						|| puntoEliminacion.getDesechoPeligroso().isDesechoES_06())
					JsfUtil.getBean(PermisoAmbientalBean.class).getPuntoEliminacionPrestadorServicioLista().get(0)
							.setCantidadUnidades(puntoEliminacion.getCantidadAnualUnidades());
			}
			puntoEliminacion.setPuntoEliminacionPrestadorServicioDesechoPeligrosos(JsfUtil.getBean(
					PermisoAmbientalBean.class).getPuntoEliminacionPrestadorServicioLista());
		}

		if (!getPuntosEliminacion().contains(puntoEliminacion))
			getPuntosEliminacion().add(puntoEliminacion);

		/*LLamar guardarpaso10*/
		try {
			JsfUtil.getBean(RegistroGeneradorDesechoController.class).guardarpaso10(puntoEliminacion);
		} catch (Exception e) {
			e.printStackTrace();
		}

		cancelar();
		JsfUtil.addCallbackParam("addPuntoEliminacion");

		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
	}

	public void editar(PuntoEliminacion puntoEliminacion) {
		this.puntoEliminacion = puntoEliminacion;
		setEditar(true);

		if (puntoEliminacion.getTipoEliminacionDesecho() != null) {
			JsfUtil.getBean(TipoEliminacionDesechoBean.class).setTipoEliminacionDesechoSeleccionada(
					puntoEliminacion.getTipoEliminacionDesecho());
		}
		if (puntoEliminacion.getTextoAsociadoOpcionOtro() != null) {
			JsfUtil.getBean(TipoEliminacionDesechoBean.class).setTextoAsociadoOpcionOtro(
					puntoEliminacion.getTextoAsociadoOpcionOtro());
		}
		if (puntoEliminacion.getPuntoEliminacionPrestadorServicioDesechoPeligrosos() != null) {
			JsfUtil.getBean(PermisoAmbientalBean.class).setPuntoEliminacionPrestadorServicioLista(
					puntoEliminacion.getPuntoEliminacionPrestadorServicioDesechoPeligrosos());

			List<PrestadorServiciosDesechoPeligroso> auxiliar = new ArrayList<PrestadorServiciosDesechoPeligroso>();
			for (PuntoEliminacionPrestadorServicioDesechoPeligroso puntoEliminacionPrestadorServicio : puntoEliminacion
					.getPuntoEliminacionPrestadorServicioDesechoPeligrosos()) {
				auxiliar.add(puntoEliminacionPrestadorServicio.getPrestadorServiciosDesechoPeligroso());
			}
			JsfUtil.getBean(PermisoAmbientalBean.class).setPrestadoresServiciosSeleccionados(auxiliar);
		}
	}

	public void cancelar() {
		puntoEliminacion = null;
		JsfUtil.getBean(TipoEliminacionDesechoBean.class).resetSelection();
		JsfUtil.getBean(PermisoAmbientalBean.class).resetSelection();
	}

	public void eliminar(PuntoEliminacion punto) {
		if (getPuntosEliminacion().contains(punto)){
			getPuntosEliminacion().remove(punto);
			JsfUtil.getBean(AdicionarDesechosPeligrososBean.class).setModificacionesEliminaciones(true);
		}
		/*paso 10 eliminar */
		JsfUtil.getBean(RegistroGeneradorDesechoController.class).eliminarPuntoEliminacion(punto);

	}

	public void validacionesDesechos(DesechoPeligroso desechoPeligroso) {
		PuntoEliminacion puntoEliminacionToDelete = null;
		for (PuntoEliminacion puntoEliminacion : getPuntosEliminacion()) {
			if (puntoEliminacion.getDesechoPeligroso().equals(desechoPeligroso)) {
				puntoEliminacionToDelete = puntoEliminacion;
				break;
			}
		}
		if (puntoEliminacionToDelete != null)
			getPuntosEliminacion().remove(puntoEliminacionToDelete);
	}

	public void validateEliminacionDesechosInstalacion(FacesContext context, UIComponent validate, Object value) {
		if (getPuntosEliminacion().isEmpty())
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Debe definir, al menos, un punto de eliminación.", null));
	}

	public void cleanAllData() {
		GeneradorDesechosRecolector generadorDesechosRecolectorToDelete = null;
		for (GeneradorDesechosRecolector generadorDesechosRecolector : JsfUtil.getBean(
				RecoleccionTransporteDesechosBean.class).getGeneradoresDesechosRecolectores()) {
			if (generadorDesechosRecolector.getDesechoPeligroso().equals(getPuntoEliminacion().getDesechoPeligroso())) {
				generadorDesechosRecolectorToDelete = generadorDesechosRecolector;
				break;
			}
		}
		if (generadorDesechosRecolectorToDelete != null)
			JsfUtil.getBean(RecoleccionTransporteDesechosBean.class).getGeneradoresDesechosRecolectores()
					.remove(generadorDesechosRecolectorToDelete);

		GeneradorDesechosEliminador generadorDesechosEliminadorToDelete = null;
		for (GeneradorDesechosEliminador generadorDesechosEliminador : JsfUtil.getBean(
				EliminacionDesechosFueraInstalacionBean.class).getGeneradoresDesechosEliminadores()) {
			if (generadorDesechosEliminador.getDesechoPeligroso().equals(getPuntoEliminacion().getDesechoPeligroso())) {
				generadorDesechosEliminadorToDelete = generadorDesechosEliminador;
				break;
			}
		}
		if (generadorDesechosEliminadorToDelete != null)
			JsfUtil.getBean(EliminacionDesechosFueraInstalacionBean.class).getGeneradoresDesechosEliminadores()
					.remove(generadorDesechosEliminadorToDelete);
	}

	public List<DesechoPeligroso> getDesechosAEliminarDentroInstalacion() {
		int longitud = getPuntosEliminacion().size();
		List<DesechoPeligroso> result = new ArrayList<DesechoPeligroso>();

		Map<DesechoPeligroso, Double> cantidadUnidades = getCantidadesDesechosAEliminarDentroInstalacionUnidades();
		Map<DesechoPeligroso, Double> cantidadToneladas = getCantidadesDesechosAEliminarDentroInstalacionToneladas();

		for (int i = 0; i < longitud; i++) {
			if (getPuntosEliminacion().get(i).getDesechoPeligroso() != null
					&& (!result.contains(getPuntosEliminacion().get(i).getDesechoPeligroso()))
					&& (getPuntosEliminacion().get(i).getDesechoPeligroso().isDesechoES_04() || getPuntosEliminacion()
					.get(i).getDesechoPeligroso().isDesechoES_06())
					&& (cantidadUnidades.get(getPuntosEliminacion().get(i).getDesechoPeligroso()).equals(JsfUtil
					.getBean(DatosDesechosBean.class)
					.getGeneradorDesechosDesechoPeligrosoDatosGeneralesPorDesecho(
							getPuntosEliminacion().get(i).getDesechoPeligroso()).getCantidadUnidades()))
					&& (cantidadToneladas.get(getPuntosEliminacion().get(i).getDesechoPeligroso()).equals(JsfUtil
					.getBean(DatosDesechosBean.class)
					.getGeneradorDesechosDesechoPeligrosoDatosGeneralesPorDesecho(
							getPuntosEliminacion().get(i).getDesechoPeligroso()).getCantidadToneladas())))
				result.add(getPuntosEliminacion().get(i).getDesechoPeligroso());
			else if (getPuntosEliminacion().get(i).getDesechoPeligroso() != null
					&& (!result.contains(getPuntosEliminacion().get(i).getDesechoPeligroso()))
					&& !(getPuntosEliminacion().get(i).getDesechoPeligroso().isDesechoES_04() || getPuntosEliminacion()
					.get(i).getDesechoPeligroso().isDesechoES_06())
					&& (cantidadToneladas.get(getPuntosEliminacion().get(i).getDesechoPeligroso()).equals(JsfUtil
					.getBean(DatosDesechosBean.class)
					.getGeneradorDesechosDesechoPeligrosoDatosGeneralesPorDesecho(
							getPuntosEliminacion().get(i).getDesechoPeligroso()).getCantidadToneladas())))
				result.add(getPuntosEliminacion().get(i).getDesechoPeligroso());
		}
		return result;
	}

	public Map<DesechoPeligroso, Double> getCantidadesDesechosAEliminarDentroInstalacionUnidades() {
		Map<DesechoPeligroso, Double> cantidadUnidades = new HashMap<DesechoPeligroso, Double>();
		for (PuntoEliminacion punto : getPuntosEliminacion()) {
			if (cantidadUnidades.get(punto.getDesechoPeligroso()) != null) {
				if (isEditar() && getPuntoEliminacion().equals(punto)){
					JsfUtil.getBean(AdicionarDesechosPeligrososBean.class).setModificacionesEliminaciones(true);
					continue;
				}
				cantidadUnidades.put(punto.getDesechoPeligroso(),
						punto.getCantidadAnualUnidades() + cantidadUnidades.get(punto.getDesechoPeligroso()));
			} else {
				if (isEditar() && getPuntoEliminacion().equals(punto)) {
					cantidadUnidades.put(punto.getDesechoPeligroso(), 0.0d);
					JsfUtil.getBean(AdicionarDesechosPeligrososBean.class).setModificacionesEliminaciones(true);
					continue;
				}
				cantidadUnidades.put(punto.getDesechoPeligroso(), punto.getCantidadAnualUnidades());
			}
		}
		return cantidadUnidades;
	}

	public Map<DesechoPeligroso, Double> getCantidadesDesechosAEliminarDentroInstalacionToneladas() {
		Map<DesechoPeligroso, Double> cantidadToneladas = new HashMap<DesechoPeligroso, Double>();
		for (PuntoEliminacion punto : getPuntosEliminacion()) {
			if (cantidadToneladas.get(punto.getDesechoPeligroso()) != null) {
				if (isEditar() && getPuntoEliminacion().equals(punto))
					continue;
				cantidadToneladas.put(punto.getDesechoPeligroso(), punto.getCantidadAnualToneladas()
						+ cantidadToneladas.get(punto.getDesechoPeligroso()));
			} else {
				if (isEditar() && getPuntoEliminacion().equals(punto)) {
					cantidadToneladas.put(punto.getDesechoPeligroso(), 0.0d);
					continue;
				}
				cantidadToneladas.put(punto.getDesechoPeligroso(), punto.getCantidadAnualToneladas());
			}
		}
		return cantidadToneladas;
	}

	public void validateData(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();

		/*if (JsfUtil.getBean(TipoEliminacionDesechoBean.class).getTipoEliminacionDesechoSeleccionada() == null) {
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Tipo de eliminación o disposición final' es requerido.", null));
		}*/

		if (JsfUtil.getBean(PermisoAmbientalBean.class).getPrestadoresServiciosSeleccionados().isEmpty()) {
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Debe seleccionar, al menos, un permiso ambiental.", null));
		}

		if (puntoEliminacion.getDesechoPeligroso() != null) {

			validateCantidadesPuntoEliminacion(errorMessages);

			if (JsfUtil.getBean(PermisoAmbientalBean.class).getPuntoEliminacionPrestadorServicioLista().size() > 1) {
				int cantidadInvalida = 0;
				double cantidadToneladas = 0.0d;
				double cantidadUnidades = 0.0d;
				for (PuntoEliminacionPrestadorServicioDesechoPeligroso punto : JsfUtil.getBean(
						PermisoAmbientalBean.class).getPuntoEliminacionPrestadorServicioLista()) {
					if ((puntoEliminacion.getDesechoPeligroso().isDesechoES_04() || puntoEliminacion
							.getDesechoPeligroso().isDesechoES_06())
							&& (punto.getCantidadToneladas() != 0.0d || punto.getCantidadUnidades() != 0.0d)) {
						if (isEditar() && puntoEliminacion.equals(punto.getPuntoEliminacion()))
							continue;
						cantidadToneladas += punto.getCantidadToneladas();
						cantidadUnidades += punto.getCantidadUnidades();
					} else if (!(puntoEliminacion.getDesechoPeligroso().isDesechoES_04() || puntoEliminacion
							.getDesechoPeligroso().isDesechoES_06()) && punto.getCantidadToneladas() != 0.0d) {
						if (isEditar() && puntoEliminacion.equals(punto.getPuntoEliminacion()))
							continue;
						cantidadToneladas += punto.getCantidadToneladas();
					} else
						cantidadInvalida += 1;
				}

				if (cantidadInvalida > 0)
					errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Debe ingresar para cada permiso ambiental, al menos, una cantidad.", null));

				if (cantidadInvalida == 0
						&& (puntoEliminacion.getDesechoPeligroso().isDesechoES_04() || puntoEliminacion
						.getDesechoPeligroso().isDesechoES_06())
						&& (cantidadUnidades != puntoEliminacion.getCantidadAnualUnidades() || cantidadToneladas != puntoEliminacion
						.getCantidadAnualToneladas())) {
					errorMessages
							.add(new FacesMessage(
									FacesMessage.SEVERITY_ERROR,
									"La suma de las cantidades transportadas con cada permiso, debe ser igual a la cantidades manejadas en el punto de eliminación.",
									null));
				} else if (cantidadInvalida == 0
						&& !(puntoEliminacion.getDesechoPeligroso().isDesechoES_04() || puntoEliminacion
						.getDesechoPeligroso().isDesechoES_06())
						&& (cantidadToneladas != puntoEliminacion.getCantidadAnualToneladas())) {
					errorMessages
							.add(new FacesMessage(
									FacesMessage.SEVERITY_ERROR,
									"La suma de las cantidades en toneladas transportadas con cada permiso, debe ser igual a la cantidad  manejada en el punto de eliminación.",
									null));
				}
			}

		}

		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}

	public void validateCantidadesPuntoEliminacion(List<FacesMessage> errorMessages) {
		Map<DesechoPeligroso, Double> cantidadUnidades = getCantidadesDesechosAEliminarDentroInstalacionUnidades();
		Map<DesechoPeligroso, Double> cantidadToneladas = getCantidadesDesechosAEliminarDentroInstalacionToneladas();

		if ((puntoEliminacion.getDesechoPeligroso().isDesechoES_04() || puntoEliminacion.getDesechoPeligroso()
				.isDesechoES_06())
				&& ((cantidadUnidades.get(puntoEliminacion.getDesechoPeligroso()) != null ? puntoEliminacion
				.getCantidadAnualUnidades() + cantidadUnidades.get(puntoEliminacion.getDesechoPeligroso())
				: puntoEliminacion.getCantidadAnualUnidades()) > JsfUtil
				.getBean(DatosDesechosBean.class)
				.getGeneradorDesechosDesechoPeligrosoDatosGeneralesPorDesecho(
						puntoEliminacion.getDesechoPeligroso()).getCantidadUnidades())) {
			errorMessages
					.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"El campo 'Cantidad (Unidades)', supera el límite permitido según los datos correspondientes a ese desecho, ingresados en el paso 'Datos asociados a los desechos'.",
							null));
		} else if ((cantidadToneladas.get(puntoEliminacion.getDesechoPeligroso()) != null ? puntoEliminacion
				.getCantidadAnualToneladas() + cantidadToneladas.get(puntoEliminacion.getDesechoPeligroso())
				: puntoEliminacion.getCantidadAnualToneladas()) > JsfUtil.getBean(DatosDesechosBean.class)
				.getGeneradorDesechosDesechoPeligrosoDatosGeneralesPorDesecho(puntoEliminacion.getDesechoPeligroso())
				.getCantidadToneladas()) {
			errorMessages
					.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"El campo 'Cantidad (toneladas)', supera el límite permitido según los datos correspondientes a ese desecho, ingresados en el paso 'Datos asociados a los desechos'.",
							null));
		}
	}

}
