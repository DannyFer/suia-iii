package ec.gob.ambiente.suia.templates.controller;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.reportes.facade.ReportTemplatesFacade;
import ec.gob.ambiente.suia.templates.bean.ReportTemplatesBean;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
public class ReportTemplatesController implements Serializable {

	private static final long serialVersionUID = -9150788922851124957L;

	@EJB
	private ReportTemplatesFacade reportTemplatesFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{reportTemplatesBean}")
	private ReportTemplatesBean reportTemplatesBean;

	public void adicionar() {
		reportTemplatesBean.setPlantillaReporte(null);
	}

	public void aceptar() {
		reportTemplatesFacade.guardar(reportTemplatesBean.getPlantillaReporte());
		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		JsfUtil.addCallbackParam("addTemplate");
	}

	public void eliminar() {
		reportTemplatesFacade.eliminar(reportTemplatesBean.getPlantillaReporte());
		reportTemplatesBean.setPlantillaReporte(null);
		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
	}
}
