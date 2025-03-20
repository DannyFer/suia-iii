package ec.gob.ambiente.prevencion.registrogeneradordesechos.controller;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;

import ec.gob.ambiente.prevencion.registrogeneradordesechos.bean.MedidasSeguridadBean;
import ec.gob.ambiente.suia.domain.TipoMedidaSeguridad;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
public class MedidasSeguridadController implements Serializable {

	private static final long serialVersionUID = -5237894957079552906L;

	public void quitarMedidaSeguridad(TipoMedidaSeguridad tipoMedidaSeguridad) {
		tipoMedidaSeguridad.setSeleccionado(false);
		if (tipoMedidaSeguridad.getId().equals(TipoMedidaSeguridad.TIPO_MEDIDA_SEGURIDAD_OTRO)) {
			JsfUtil.getBean(MedidasSeguridadBean.class).setOtroSeleccionado(false);
			JsfUtil.getBean(MedidasSeguridadBean.class).setOtro(null);
		}
	}

}
