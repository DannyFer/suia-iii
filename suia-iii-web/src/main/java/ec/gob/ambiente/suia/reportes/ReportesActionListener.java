package ec.gob.ambiente.suia.reportes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.servlet.http.HttpServletResponse;
import ec.gob.ambiente.suia.reportes.facade.ReportesFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;

public class ReportesActionListener implements ActionListener {

	public static final String REPORTE_URL = "reporteUrl";
	public static final String REPORTE_PARAMETROS = "reporteParametros";
	public static final String REPORTE_SUBREPORTES = "reporteSubReportes";
	public static final String REPORTE_TIPO = "reporteTipo";
	public static final String REPORTE_SUBREPORTES_URL = "reporteSubReportesUrl";
	public static final String REPORTE_IMAGES = "reporteImages";

	@SuppressWarnings("unchecked")
	public void processAction(ActionEvent e) throws AbortProcessingException {

		String reporteUrl = null;
		Map<String, Object> reporteParametros = new HashMap<String, Object>();

		List<UIComponent> componentes = e.getComponent().getChildren();

		List<String> reportes = new ArrayList<String>();
		List<String> reportesUrl = new ArrayList<String>();
		List<String> reportesImages = new ArrayList<String>();

		for (UIComponent c : componentes) {
			if (c instanceof UIParameter) {
				UIParameter param = (UIParameter) c;
				if (param.getName().equals(REPORTE_URL))
					reporteUrl = (String) param.getValue();
				else if (param.getName().equals(REPORTE_PARAMETROS))
					reporteParametros.putAll((Map<String, Object>) param.getValue());
				else if (param.getName().equals(REPORTE_TIPO))
					reporteParametros.put(REPORTE_TIPO, param.getValue());
				else if (param.getName().equals(REPORTE_SUBREPORTES)) {
					String subReportes = (String) param.getValue();
					if (subReportes.indexOf(",") != -1) {
						String[] paramReportes = subReportes.split(",");
						for (String string : paramReportes)
							reportes.add(string.trim());
					} else {
						reportes.add(subReportes.trim());
					}
				} else if (param.getName().equals(REPORTE_SUBREPORTES_URL)) {
					String subReportes = (String) param.getValue();
					if (subReportes.indexOf(",") != -1) {
						String[] paramReportes = subReportes.split(",");
						for (String string : paramReportes)
							reportesUrl.add(string.trim());
					} else {
						reportesUrl.add(subReportes.trim());
					}
				}

				else if (param.getName().equals(REPORTE_IMAGES)) {
					String images = (String) param.getValue();
					if (images.indexOf(",") != -1) {
						String[] imagesReportes = images.split(",");
						for (String string : imagesReportes)
							reportesImages.add(string.trim());
					} else {
						reportesImages.add(images.trim());
					}
				}

			}
		}

		if (reportes.size() != reportesUrl.size())
			throw new IllegalArgumentException(
					"La cantidad de subreportes no coincide con el total de url para los mismos");

		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
		ReportesFacade reportesUtil = (ReportesFacade) BeanLocator.getInstance(ReportesFacade.class);
		reportesUtil.generarReportePDF(response, reporteParametros, reporteUrl, reportes, reportesUrl, reportesImages);
		context.responseComplete();
	}
}