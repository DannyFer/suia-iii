package ec.gob.ambiente.rcoa.sustancias.quimicas.controllers;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;

@ManagedBean
@ViewScoped
public class ReporteDeclaracionRSQController {

	@Getter
	private String reporteLink;

	@Getter
	private String reporteLinkTecnico;

	@PostConstruct
	public void init() {
		reporteLink = Constantes.getReportePentahoRSQDeclaracionesOperador() + "&txt_usuario="
				+ JsfUtil.getLoggedUser().getNombre();
		reporteLinkTecnico = Constantes.getReportePentahoRSQDeclaracionesTecnico();
	}

}