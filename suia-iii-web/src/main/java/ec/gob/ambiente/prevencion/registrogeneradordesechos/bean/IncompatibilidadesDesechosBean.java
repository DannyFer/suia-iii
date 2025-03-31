package ec.gob.ambiente.prevencion.registrogeneradordesechos.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import lombok.Setter;
import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.suia.comun.bean.AdicionarDesechosPeligrososBean;
import ec.gob.ambiente.suia.comun.bean.AdicionarIncompatibilidadesBean;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.IncompatibilidadDesechoPeligroso;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class IncompatibilidadesDesechosBean extends RegistroGeneradorBaseBean {

	private static final long serialVersionUID = 8605555555249627413L;

	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;

	@Setter
	private Map<DesechoPeligroso, List<IncompatibilidadDesechoPeligroso>> incompatibilidadesDesechos;

	@PostConstruct
	private void initIncompatibilidades() {
	}

	public void aceptar(DesechoPeligroso desechoPeligroso, IncompatibilidadDesechoPeligroso incompatibilidad) {
		getIncompatibilidadesDesechos().get(desechoPeligroso).add(incompatibilidad);
	}

	public void addIncompatibilidades(final DesechoPeligroso desechoPeligroso) {
		JsfUtil.getBean(AdicionarIncompatibilidadesBean.class).reset(cargarIncompatibilidadCaracteristica(desechoPeligroso));		
		JsfUtil.getBean(AdicionarIncompatibilidadesBean.class).setCompleteOperationOnAdd(new CompleteOperation() {

			@Override
			public Object endOperation(Object object) {
				IncompatibilidadDesechoPeligroso incompatibilidad = (IncompatibilidadDesechoPeligroso) object;
				if (!getIncompatibilidadesDesechos().get(desechoPeligroso).contains(incompatibilidad))
					getIncompatibilidadesDesechos().get(desechoPeligroso).add(incompatibilidad);
				return null;
			}
		});
	}

	public void eliminar(DesechoPeligroso desechoPeligroso, IncompatibilidadDesechoPeligroso incompatibilidad) {
		getIncompatibilidadesDesechos().get(desechoPeligroso).remove(incompatibilidad);
		JsfUtil.getBean(AdicionarDesechosPeligrososBean.class).setModificacionesEliminaciones(true);
	}

	public void validacionesDesechos(DesechoPeligroso desechoPeligroso) {
		if (getIncompatibilidadesDesechos().containsKey(desechoPeligroso))
			getIncompatibilidadesDesechos().remove(desechoPeligroso);
	}

	public void validateIncompatibilidades(FacesContext context, UIComponent validate, Object value) {
		Iterator<DesechoPeligroso> iterator = getIncompatibilidadesDesechos().keySet().iterator();
		while (iterator.hasNext()) {
			DesechoPeligroso desechoPeligroso = iterator.next();
			if (getIncompatibilidadesDesechos().get(desechoPeligroso).isEmpty())
				throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Debe seleccionar, para todos los desechos, las incompatibilidades asociadas.", null));
		}
	}

	public Map<DesechoPeligroso, List<IncompatibilidadDesechoPeligroso>> getIncompatibilidadesDesechos() {
		if (incompatibilidadesDesechos == null) {
			incompatibilidadesDesechos = new HashMap<DesechoPeligroso, List<IncompatibilidadDesechoPeligroso>>();
		}
		List<DesechoPeligroso> selected = JsfUtil.getBean(AdicionarDesechosPeligrososBean.class)
				.getDesechosSeleccionados();
		if (selected.size() > incompatibilidadesDesechos.size()) {
			for (DesechoPeligroso desechoPeligroso : selected) {
				if (!incompatibilidadesDesechos.containsKey(desechoPeligroso))
					incompatibilidadesDesechos.put(desechoPeligroso, new ArrayList<IncompatibilidadDesechoPeligroso>());
			}
		}
		return incompatibilidadesDesechos;
	}

	public IncompatibilidadDesechoPeligroso cargarIncompatibilidadCaracteristica(DesechoPeligroso desechoPeligroso) {
		if (!getIncompatibilidadesDesechos().get(desechoPeligroso).isEmpty())
			return getIncompatibilidadesDesechos().get(desechoPeligroso).get(0).getIncompatibilidadDesechoPeligroso();
		return null;
	}
}
