package ec.gob.ambiente.suia.templates.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.reportes.facade.ReportTemplatesFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ReportTemplatesBean implements Serializable {

	private static final long serialVersionUID = -4286934233485511963L;

	@EJB
	private ReportTemplatesFacade reportTemplatesFacade;

	private List<PlantillaReporte> plantillaReportes;
	
	@Getter
	private List<TipoDocumento> tiposDocumento;

	@Setter
	private PlantillaReporte plantillaReporte;

	@PostConstruct
	private void init() {
		try {
			plantillaReportes = reportTemplatesFacade.getPlantillaReportes();
			tiposDocumento = reportTemplatesFacade.getTiposDocumento();
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrión un error al cargar las plantillas.");
			e.printStackTrace();
		}
	}

	public List<PlantillaReporte> getPlantillaReportes() {
		return plantillaReportes == null ? plantillaReportes = new ArrayList<>() : plantillaReportes;
	}

	public PlantillaReporte getPlantillaReporte() {
		return plantillaReporte == null ? plantillaReporte = new PlantillaReporte() : plantillaReporte;
	}
}
