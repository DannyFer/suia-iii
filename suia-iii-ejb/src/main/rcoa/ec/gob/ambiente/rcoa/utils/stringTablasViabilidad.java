package ec.gob.ambiente.rcoa.utils;

import java.util.List;

import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.EspeciesInformeForestal;

public class stringTablasViabilidad {
	/**
	 * Ivan, Dibuja la table de censo para usar en informe de factibilidad e informe de viabilidad
	 * @param listaEspeciesCenso
	 * @return
	 */
	public static String getTablaAreaBasalCenso(List<EspeciesInformeForestal> listaEspeciesCenso) {  
		StringBuilder stringBuilder = new StringBuilder();
		if(listaEspeciesCenso != null && !listaEspeciesCenso.isEmpty()) {
			stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" width=\"100%\"  style=\"width: 100% border-collapse:collapse;font-size:10px;\">");
			stringBuilder.append("<tr><th colspan=\"11\">Tabla de cálculo del área basal y volumen</th></tr>");
			stringBuilder.append("<tr><th rowspan=\"2\">N° de individuo</th><th colspan=\"3\">Taxonomí­a</th><th rowspan=\"2\">Nombre común</th>"
					+ "<th rowspan=\"2\" style=\"width: 100% border-collapse:collapse;font-size:8px;\">Diámetro de la altura del pecho DAP (m)</th>"
					+ "<th rowspan=\"2\">Altura Total HT (m)</th><th rowspan=\"2\">Altura comercial HC (m)</th><th rowspan=\"2\">Área basal AB (m2)</th>"
					+ "<th rowspan=\"2\">Volumen total Vt (m3)</th><th rowspan=\"2\">Volumen comercial Vc (m3)</th></tr>");
			
			stringBuilder.append("<tr><th>Familia</th><th>Género</th><th>Especie</th></tr>");
			
			int nro = 1;
			double sumatoriaBasal=0;
			double sumatoriaVolumen=0;
			double sumatoriaComercial=0;
			for (EspeciesInformeForestal registro : listaEspeciesCenso) {
				stringBuilder.append("<tr>");
				stringBuilder.append("<td>" + nro + "</td>");
				stringBuilder.append("<td style=\"font-size:8px;\">");
				stringBuilder.append(registro.getFamiliaEspecie().getHiclScientificName());
				stringBuilder.append("</td>");
				stringBuilder.append("<td style=\"font-size:8px;\">");
				stringBuilder.append(registro.getGeneroEspecie().getHiclScientificName());
				stringBuilder.append("</td>");
				stringBuilder.append("<td style=\"font-size:8px;\">");
				stringBuilder.append((registro.getEspecie() != null) ? registro.getEspecie().getSptaScientificName() : "");
				stringBuilder.append("</td>");
				stringBuilder.append("<td style=\"font-size:8px;\">");
				stringBuilder.append(registro.getNombreComun());
				stringBuilder.append("</td>");
				stringBuilder.append("<td style=\"font-size:8px;\">");
				stringBuilder.append(registro.getAlturaDap());
				stringBuilder.append("</td>");
				stringBuilder.append("<td style=\"font-size:8px;\">");
				stringBuilder.append(registro.getAlturaTotal());
				stringBuilder.append("</td>");
				stringBuilder.append("<td style=\"font-size:8px;\">");
				stringBuilder.append(registro.getAlturaComercial());
				stringBuilder.append("</td>");
				stringBuilder.append("<td style=\"font-size:8px;\">");
				stringBuilder.append(registro.getAreaBasal());
				stringBuilder.append("</td>");
				stringBuilder.append("<td style=\"font-size:8px;\">");
				stringBuilder.append(registro.getVolumenTotal());
				stringBuilder.append("</td>");
				stringBuilder.append("<td style=\"font-size:8px;\">");
				stringBuilder.append(registro.getVolumenComercial());
				stringBuilder.append("</td>");
				stringBuilder.append("</tr>");
				sumatoriaBasal=(double) sumatoriaBasal+ (double)registro.getAreaBasal();
				sumatoriaVolumen=(double)sumatoriaVolumen+ (double) registro.getVolumenTotal();
				sumatoriaComercial=(double)sumatoriaComercial+(double)registro.getVolumenComercial();
				nro++;
			}
			stringBuilder.append("</table><br />");
			stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" width=\"100%\"  style=\"width: 100% border-collapse:collapse;font-size:10px;\">");
			stringBuilder.append("<tr><th width=\"33%\">Sumatoria del área basal del Censo</th>");
			stringBuilder.append("<th width=\"33%\">Sumatoria del volumen total del Censo</th>");
			stringBuilder.append("<th width=\"34%\">Sumatoria del volumen comercial del Censo</th></tr>");
			stringBuilder.append("<tr>");
			stringBuilder.append("<td style=\"font-size:10px;\">");
			stringBuilder.append(sumatoriaBasal);
			stringBuilder.append("</td>");
			stringBuilder.append("<td style=\"font-size:10px;\">");
			stringBuilder.append(sumatoriaVolumen);
			stringBuilder.append("</td>");
			stringBuilder.append("<td style=\"font-size:10px;\">");
			stringBuilder.append(sumatoriaComercial);
			stringBuilder.append("</td></tr>");
			stringBuilder.append("</table><br />");
		}
		
		return stringBuilder.toString();
	}

}
