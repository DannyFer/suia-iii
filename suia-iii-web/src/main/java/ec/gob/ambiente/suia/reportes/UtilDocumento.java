package ec.gob.ambiente.suia.reportes;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ViewScoped
@ManagedBean
public class UtilDocumento {
	private static final Logger LOG = Logger.getLogger(UtilDocumento.class);

	public static void descargarExcel(byte[] bytes, String nombreReporte) {
		try {
			descargarFile(bytes, nombreReporte, "application/vnd.ms-excel", ".xls");

		} catch (Exception e) {
			LOG.error("Error al descargar el excel" + nombreReporte, e);
		}
	}

	public static URL getRecursoImage(String nombreImagen) {
		ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext()
				.getContext();
		try {
			return servletContext.getResource("/resources/images/" + nombreImagen);
		} catch (MalformedURLException e) {
			LOG.error("Error al recuperar la imagen " + nombreImagen, e);
			return null;
		}
	}

	public static void descargarPDF(byte[] bytes, String nombreReporte) {
		try {
			descargarFile(bytes, nombreReporte, "application/pdf", ".pdf");

		} catch (Exception e) {
			LOG.error("Error al descargar el pdf" + nombreReporte, e);
		}
	}

	public static void descargarFile(byte[] bytes, String nombreArchivoConExtension) {
		try {
			descargarFile(bytes, nombreArchivoConExtension, getMimePorExtencion(nombreArchivoConExtension));
		} catch (Exception e) {
			LOG.error("Error al descargar el pdf" + nombreArchivoConExtension, e);
		}
	}

	private static void descargarFile(byte[] bytes, String nombreReporte, String tipoContenido, String extencion)
			throws Exception {
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
				.getResponse();
		OutputStream out;
		try {
			out = response.getOutputStream();

			response.setContentType(tipoContenido);
			response.setHeader("Content-Disposition", "attachment;filename=\"" + nombreReporte + extencion + "\"");
			response.setDateHeader("Expires", 0);
			out.write(bytes);			
			out.flush();

			FacesContext.getCurrentInstance().responseComplete();
		} catch (IOException e) {
			LOG.error("Error al descargar el archivo", e);
		}
	}
	
	private static void descargarFileVue(byte[] bytes, String nombreReporte, String tipoContenido, String extencion)
			throws Exception {
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
				.getResponse();
		OutputStream out;
		try {
			out = response.getOutputStream();

			response.setContentType(tipoContenido);
			response.setHeader("Content-Disposition", "attachment;filename=\"" + nombreReporte + extencion + "\"");
			response.setDateHeader("Expires", 0);
			out.write(bytes);
			out.close();
			out.flush();

			FacesContext.getCurrentInstance().responseComplete();
		} catch (IOException e) {
			LOG.error("Error al descargar el archivo", e);
		}
	}

	public static void descargarPDF(File archivoTemporal) {
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
				.getResponse();
		OutputStream out;
		try {
			out = response.getOutputStream();

			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment;filename=\"" + archivoTemporal.getName() + "\"");
			response.setDateHeader("Expires", 0);
			InputStream inputStream = new FileInputStream(archivoTemporal.getAbsolutePath());
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = inputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			inputStream.close();
			out.flush();

			FacesContext.getCurrentInstance().responseComplete();
		} catch (IOException e) {
			LOG.error("Error al descargar el pdf" + archivoTemporal.getName(), e);
		}
	}

	public static Map<String, String> cargarImageneArea(Area areaResponsable) {

		Map<String, String> resultados = new HashMap<>();
		resultados.put("pie_ci", "pie_ci.png");
		resultados.put("logo_mae_pie", "logo_mae_pie.png");
		resultados.put("logo_mae", "logo_mae.png");
		String nombre_pie = null;
		String nombre_logo = null;
		URL logo = null;
		AlfrescoServiceBean alfrescoServiceBean = new AlfrescoServiceBean();
		URL pie = null;
		if (areaResponsable != null) {
			nombre_pie = "pie__" + areaResponsable.getAreaAbbreviation().replace("/", "_") + ".png";

			nombre_logo = "logo__" + areaResponsable.getAreaAbbreviation().replace("/", "_") + ".png";
			alfrescoServiceBean = JsfUtil.getBean(AlfrescoServiceBean.class);
			pie = getRecursoImage("ente/" + nombre_pie);
			if (pie != null) {
				resultados.put("pie_ci", "ente/" + nombre_pie);
			}
			logo = getRecursoImage("ente/" + nombre_logo);
			if (logo != null) {
				resultados.put("logo_mae_pie", "ente/" + nombre_logo);
			}
		}

		return resultados;
	}

	public static Documento generateDocumentPDFFromUpload(byte[] contenidoDocumento, String nombre) {
		try {

			Documento documento = new Documento();
			documento.setContenidoDocumento(contenidoDocumento);
			documento.setExtesion(".pdf");
			documento.setMime("application/pdf");
			documento.setNombre(nombre);
			return documento;
		} catch (Exception e) {
			return null;
		}
	}

	public static Documento generateDocumentXLSFromUpload(byte[] contenidoDocumento, String nombre) {
		try {
			Documento documento = new Documento();
			documento.setContenidoDocumento(contenidoDocumento);
			documento.setExtesion(".xls");
			documento.setMime("application/xls");
			documento.setNombre(nombre);
			return documento;
		} catch (Exception e) {
			return null;
		}
	}

	public static Documento generateDocumentZipFromUpload(byte[] contenidoDocumento, String nombre) {
		try {
			Documento documento = new Documento();
			documento.setContenidoDocumento(contenidoDocumento);
			documento.setExtesion(".zip");
			documento.setMime("application/zip");
			documento.setNombre(nombre);
			return documento;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static Documento generateDocumentPngFromUpload(byte[] contenidoDocumento, String nombre) {
		try {
			Documento documento = new Documento();
			documento.setContenidoDocumento(contenidoDocumento);
			documento.setExtesion(".png");
			documento.setMime("image/png");
			documento.setNombre(nombre);
			return documento;
		} catch (Exception e) {
			return null;
		}
	}

	public static Documento generateDocumentZipRarFromUpload(byte[] contenidoDocumento, String nombreConExtension) {
		try {
			Documento documento = new Documento();
			documento.setContenidoDocumento(contenidoDocumento);
			documento.setMime(getMimePorExtencion(nombreConExtension));
			documento.setExtesion(getExtension(nombreConExtension));
			documento.setNombre(nombreConExtension);
			return documento;
		} catch (Exception e) {
			LOG.error(e, e);
			return null;
		}
	}

	public static void descargarZipRar(byte[] bytes, String nombreArchivoConExtension) {
		try {
			descargarFile(bytes, nombreArchivoConExtension, getMimePorExtencion(nombreArchivoConExtension));

		} catch (Exception e) {
			LOG.error("Error al descargar el pdf" + nombreArchivoConExtension, e);
		}
	}

	private static void descargarFile(byte[] bytes, String nombreConExtencion, String tipoContenido) throws Exception {
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
				.getResponse();
		OutputStream out = response.getOutputStream();
		response.setContentType(tipoContenido);
		response.setHeader("Content-Disposition", "attachment;filename=\"" + nombreConExtencion + "\"");
		response.setDateHeader("Expires", 0);
		out.write(bytes);
		out.flush();
		FacesContext.getCurrentInstance().responseComplete();
	}

	private static String getMimePorExtencion(String nombreConExtension) throws ServiceException {
		if (nombreConExtension.contains("zip")) {

			return "application/zip";
		} else if (nombreConExtension.contains("rar")) {

			return "application/rar";
		} else if (nombreConExtension.contains("pdf")) {

			return "application/pdf";
		}
		throw new ServiceException("Error extensión no encontrada.");
	}

	private static String getExtension(String nombreConExtension) throws ServiceException {
		if (nombreConExtension.contains("zip")) {
			return ".zip";
		} else if (nombreConExtension.contains("rar")) {
			return ".rar";
		}
		throw new ServiceException("Error extensión no encontrada.");
	}
	
	public static StreamedContent getStreamedContent(Documento documento){		
		try {
			if (documento != null && documento.getContenidoDocumento()!=null) {
				StreamedContent streamedContent = new DefaultStreamedContent(new ByteArrayInputStream(documento.getContenidoDocumento()),documento.getMime(),documento.getNombre());
				return streamedContent;
			}
		} catch (Exception e) {			
			e.printStackTrace();
		}
		return null;	
	}
}