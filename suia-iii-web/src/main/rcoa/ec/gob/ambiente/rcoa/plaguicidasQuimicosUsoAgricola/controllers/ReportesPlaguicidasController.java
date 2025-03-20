package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.controllers;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import ec.gob.ambiente.suia.utils.Constantes;

@ManagedBean
@ViewScoped
public class ReportesPlaguicidasController {

	@Getter
	private String linkTramitesAtendidos;

	@PostConstruct
	public void init() {
		linkTramitesAtendidos = Constantes.getReportePentahoPquaTramitesAtendidos();
	}

}