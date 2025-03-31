package ec.gob.ambiente.prevencion.registroautorizacionesadministrativas.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfSignatureAppearance;
import com.lowagie.text.pdf.PdfStamper;

import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author Frank Torres Rodriguez
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Frank Torres Rodriguez, Fecha: 23/01/2015]
 *          </p>
 */

@RequestScoped
@ManagedBean
public class FirmarOficioAprobacionController {

	@Getter
	@Setter
	private boolean observaciones;
	@EJB
	private ProcesoFacade procesoFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
	private TaskBeanFacade taskBeanFacade;

	@Setter
	@Getter
	private UploadedFile llave;

	@Setter
	@Getter
	private String token;

	private File pkfile;

	public String sendAction() {
		firmar();
		// try {
		//
		// Map<String, Object> data = new ConcurrentHashMap<String, Object>();
		// taskBeanFacade.approveTask(loginBean.getNombreUsuario(),
		// bandejaTareasBean.getTarea().getTaskId(), data,
		// Constantes.getDeploymentId(), loginBean.getPassword(),
		// Constantes.URL_BUSINESS_CENTRAL);
		//
		// JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
		// return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
		// } catch (JbpmException e) {
		// LOGGER.error("Error al enviar la información.", e);
		// JsfUtil.addMessageError("Ocurrio un error al enviar la información.");
		// }
		return "";

	}

	public void firmar() {
		try {
			String rutapdf = "/media/datos/trabajo/1.pdf";// RUTA_ARCHIVO_PDF_FIRMADO
			String rutapdf2 = "/media/datos/trabajo/2.pdf";// RUTA_ARCHIVO_PDF_FIRMADO
			String ruta = "server.p12";//por no no hacer un bean
			if (pkfile != null) {
				ruta = pkfile.getAbsolutePath();//
			}

			System.out.println(ruta);
			// RUTA_CERTIFICADO_PFX
			// String pasw = " ";

			// FileUtil.copyFile(ruta, this.llave.getInputstream());

			KeyStore ks = KeyStore.getInstance("pkcs12");
			ks.load(new FileInputStream(ruta), token.toCharArray());
			String alias = (String) ks.aliases().nextElement();
			PrivateKey key = (PrivateKey) ks.getKey(alias, token.toCharArray());
			Certificate[] chain = ks.getCertificateChain(alias);
			// Recibimos como parámetro de entrada el nombre del archivo PDF a
			// firmar
			PdfReader reader = new PdfReader(rutapdf2);
			FileOutputStream fout = new FileOutputStream(rutapdf);

			// Añadimos firma al documento PDF
			PdfStamper stp = PdfStamper.createSignature(reader, fout, '?');
			PdfSignatureAppearance sap = stp.getSignatureAppearance();
			sap.setCrypto(key, chain, null,
					PdfSignatureAppearance.WINCER_SIGNED);
			sap.setReason("Firma PKCS12");
			sap.setLocation("Imaginanet");
			// Añade la firma visible. Podemos comentarla para que no sea
			// visible.
			sap.setVisibleSignature(new Rectangle(100, 100, 200, 200), 1, null);
			stp.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void handleFileUpload(FileUploadEvent event) {
		// JsfUtil.addMessageInfo(event.getFile().getFileName());
		String ruta = "server.p12";//
		// RUTA_CERTIFICADO_PFX
		// String pasw = " ";

		try {
			copyFile(ruta, event.getFile().getInputstream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.llave = event.getFile();

	}

	private void copyFile(String fileName, InputStream in) {
		try {
			// write the inputStream to a FileOutputStream
			// pkfile = File.createTempFile(fileName, ".p12");
			pkfile = new File(fileName);

			OutputStream out = new FileOutputStream(pkfile);

			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = in.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}

			in.close();
			out.flush();
			out.close();

		} catch (IOException e) {
			JsfUtil.addMessageError(e.getMessage());
		}
	}

}
