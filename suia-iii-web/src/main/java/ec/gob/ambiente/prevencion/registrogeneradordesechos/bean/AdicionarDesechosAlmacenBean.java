package ec.gob.ambiente.prevencion.registrogeneradordesechos.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Setter;
import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.suia.comun.bean.AdicionarDesechosPeligrososBean;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class AdicionarDesechosAlmacenBean implements Serializable {

	private static final long serialVersionUID = -8901182093152618331L;

	private List<DesechoPeligroso> desechos;

	@Setter
	private CompleteOperation completeOperationOnAccept;

	public void init() {
		desechos = new ArrayList<DesechoPeligroso>(JsfUtil.getBean(AdicionarDesechosPeligrososBean.class)
				.getDesechosSeleccionados());
	}

	public void aceptar() {
		if (completeOperationOnAccept != null)
			completeOperationOnAccept.endOperation(getDesechosPeligrososSeleccionadas());
	}

	public List<DesechoPeligroso> getDesechos() {
		return desechos;
	}

	public void reset() {
		init();
		for (DesechoPeligroso desechoPeligroso : getDesechos()) {
			desechoPeligroso.setSeleccionado(false);
		}
	}

	private List<DesechoPeligroso> getDesechosPeligrososSeleccionadas() {
		List<DesechoPeligroso> result = new ArrayList<DesechoPeligroso>();
		if (desechos != null) {
			for (DesechoPeligroso desecho : desechos) {
				if (desecho.isSeleccionado()) {
					result.add(desecho);
				}
			}
		}
		return result;
	}

	public void setDesechosPeligrososSeleccionadas(List<DesechoPeligroso> desechosPeligrosos) {
		for (DesechoPeligroso desecho : desechos) {
			desecho.setSeleccionado(false);
		}
		for (DesechoPeligroso desecho : desechosPeligrosos) {
			for (DesechoPeligroso desechoPeligroso : desechos) {
				if (desechoPeligroso.equals(desecho))
					desechoPeligroso.setSeleccionado(true);
			}
		}
	}
}
