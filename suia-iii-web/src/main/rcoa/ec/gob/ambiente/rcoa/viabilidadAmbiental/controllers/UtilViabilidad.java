package ec.gob.ambiente.rcoa.viabilidadAmbiental.controllers;

import java.io.File;
import java.security.Security;
import java.util.ArrayList;

import javax.ejb.EJB;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.security.PdfPKCS7;

import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;

public final class UtilViabilidad {
	
	@EJB
	private OrganizacionFacade organizacionFacade;
	
	public static String getFileNameEscaped(String file) {
		String result = file.replaceAll("Ñ", "N");
		result = result.replaceAll("Á", "A");
		result = result.replaceAll("É", "E");
		result = result.replaceAll("Í", "I");
		result = result.replaceAll("Ó", "O");
		result = result.replaceAll("Ú", "U");
		return result;
	}
	
	public static Boolean verificarFirmaUsuario(File file, String usuario) {
        try {
        	Security.addProvider(new BouncyCastleProvider());
        	PdfReader reader = new PdfReader(file.getAbsolutePath());
            AcroFields af = reader.getAcroFields();
            ArrayList<String> names = af.getSignatureNames();
			if(names.size() == 0)
				return false;
			else {
				for (String name : names) {
					PdfPKCS7 pk = af.verifySignature(name);
					String razon = pk.getReason();
					String[] arrayRazon = razon.split("-");
					if(arrayRazon.length > 1) {
						String cedula = arrayRazon[1];
						if(cedula.equals(usuario))
							return true;
					}
				}
				return false;
			}
        } catch (Exception e) {
        	e.printStackTrace();
            return false;
        } 
    }

}
