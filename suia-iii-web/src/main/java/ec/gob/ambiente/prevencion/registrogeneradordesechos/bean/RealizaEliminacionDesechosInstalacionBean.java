package ec.gob.ambiente.prevencion.registrogeneradordesechos.bean;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;

@ManagedBean
@ViewScoped
public class RealizaEliminacionDesechosInstalacionBean implements Serializable {

	private static final long serialVersionUID = 5561057859820889311L;

	@Getter
	private boolean eliminaDesechosDentroInstalacion = true;

	public void setEliminaDesechosDentroInstalacion(boolean eliminaDesechosDentroInstalacion) {
		this.eliminaDesechosDentroInstalacion = eliminaDesechosDentroInstalacion;
	}

	public void cleanAllData() {
		JsfUtil.getBean(EliminacionDesechosInstalacionBean.class).cancelar();
		JsfUtil.getBean(EliminacionDesechosInstalacionBean.class).setPuntosEliminacion(null);
		JsfUtil.getBean(RecoleccionTransporteDesechosBean.class).PuedeEliminarcancelar();
		JsfUtil.getBean(RecoleccionTransporteDesechosBean.class).setGeneradoresDesechosRecolectores(null);
		JsfUtil.getBean(EliminacionDesechosFueraInstalacionBean.class).cancelar();
		JsfUtil.getBean(EliminacionDesechosFueraInstalacionBean.class).setGeneradoresDesechosEliminadores(null);
	}

}
