package ec.gob.ambiente.prevencion.certificado.ambiental.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.EncriptarUtil;
import ec.gob.ambiente.suia.utils.JsfUtil;

public class GenerarQRCertificadoAmbiental {
	
	public static final Integer tipo_suia_iii = 1;
	public static final Integer tipo_iv_categorias = 2;
	public static final Integer tipo_suia_rcoa = 3;
	
	
	public static List<String> getCodigoQrUrl(Boolean generarQrValido, String codigoProyecto, Integer tipoProyecto) {
		
		List<String> resultado = new  ArrayList<>();	
		try {		
			String qRContent = "Documento sin validez";					
			
			if(generarQrValido) {
							
				final String secretKey = "PassSistema5U14*-+/";
				String parametroOriginal = codigoProyecto + ";" + tipoProyecto;
				String parametro = EncriptarUtil.encriptar(parametroOriginal, secretKey);
				try {
					parametro = URLEncoder.encode(parametro, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
						    
				qRContent = JsfUtil.actionNavigateTo("/validacion/validarCertificadoAmbiental.jsf", "p=" + parametro);
						
				qRContent = Constantes.getUrlCodigoCertificadoQR() + qRContent;
			}

			String nombreImagen = codigoProyecto.replace("/", "-") + "-qr-firma.png";
			String imageFileSrc = nombreImagen;
			JsfUtil.writeQRCode(qRContent, imageFileSrc, 128, 128);
			
			
			resultado.add(qRContent);
			resultado.add(imageFileSrc);
			
		} catch (Exception e) {
			e.printStackTrace();
		}	

		return resultado;
	}
}
