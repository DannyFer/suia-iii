package ec.gob.ambiente.rcoa.preliminar.contoller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import ec.gob.ambiente.suia.utils.EncriptarUtil;
import ec.gob.ambiente.suia.utils.JsfUtil;

public class GenerarQRCertificadoInterseccion {
	
	public static final Integer tipo_suia_iii = 1;
	public static final Integer tipo_iv_categorias = 2;
	public static final Integer tipo_suia_rcoa = 3;
	
	
	public static List<String> getCodigoQrUrl(Boolean generarQrValido, String codigoProyecto, String codigoOficio, Integer tipoProyecto, Integer nroActualizacion) {
		String qRContent = "Documento sin validez";
		if(generarQrValido) {
			FacesContext context = FacesContext.getCurrentInstance();
			HttpServletRequest req = (HttpServletRequest) context.getExternalContext().getRequest();
			String urlFull = req.getRequestURL().toString();
			String url = urlFull.substring(0, urlFull.indexOf(req.getContextPath()) + req.getContextPath().length());
			
			final String secretKey = "PassSistema5U14*-+/";
			String parametroOriginal = codigoProyecto + ";" + codigoOficio + ";" + tipoProyecto + ";" + nroActualizacion;
			String parametro = EncriptarUtil.encriptar(parametroOriginal, secretKey);
			try {
				parametro = URLEncoder.encode(parametro, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		
		    //String decryptedParam = JsfUtil.decrypt(URLDecoder.decode(parametro, "UTF-8"), secretKey) ;
		    
			qRContent = JsfUtil.actionNavigateTo("/validacion/validarCertificado.jsf", "p=" + parametro);
					
			qRContent = url + qRContent;
		}

		String nombreImagen = codigoProyecto.replace("/", "-") + "-qr-firma.png";
		String imageFileSrc = nombreImagen;
		JsfUtil.writeQRCode(qRContent, imageFileSrc, 128, 128);
		
		List<String> resultado = new  ArrayList<>();
		resultado.add(qRContent);
		resultado.add(imageFileSrc);

		return resultado;
	}
	
	public static List<String> getCodigoQrUrlContent(Boolean generarQrValido, String codigoProyecto, String codigoOficio, Integer tipoProyecto, Integer nroActualizacion) {
		
		List<String> resultado = getCodigoQrUrl(generarQrValido, codigoProyecto, codigoOficio, tipoProyecto, nroActualizacion);
		
		Path pathQr = Paths.get(resultado.get(1));
		
		try {
			Files.delete(pathQr);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return resultado;
	}
}
