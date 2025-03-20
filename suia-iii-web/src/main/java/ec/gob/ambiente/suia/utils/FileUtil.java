/* 
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.primefaces.context.PrimeFacesContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * @author magmasoft
 * 
 *         JSF Util bean para funcionalidades comunes
 */
public final class FileUtil {

	private final static String tmpDir = "/tmp/maefiles/";

	/**
	 * No permite instanciar la clase
	 */
	private FileUtil() {
	}

	public static String addTmpFile(File file, String taskId, String fileName,
			String formKey) {
		// File outFile = new File(tmpDir + "taskId" + File.pathSeparator
		// + fileName);

		String path = tmpDir + taskId + "/" + formKey + "/" + fileName;
		try {
			FileInputStream in = new FileInputStream(file);
			copyFile(path, in);

			return path;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return "";

	}

	public static String addTmpFile(FileUploadEvent event, String taskId,
			String fileName, String formKey) {

		try {
			String path = tmpDir + taskId + "/" + formKey + "/" + fileName;

			copyFile(path, event.getFile().getInputstream());

			return path;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static Map<String, File> readTmpFile(String taskId) {
		String path_dir = tmpDir + taskId + "/";
		Map<String, File> output = new HashMap<String, File>();
		File baseDir = new File(path_dir);

		if (baseDir.isDirectory()) {
			File[] subdir = baseDir.listFiles();
			for (int x = 0; x < subdir.length; x++) {

				if (subdir[x].isDirectory()) {

					File[] subfiles = subdir[x].listFiles();
					for (int i = 0; i < subfiles.length; i++) {
						if (subfiles[i].isFile()) {
							output.put(subdir[x].getName(), subfiles[i]);
							// JsfUtil.addMessageInfo(subdir[x].getName());
						}
					}

				}
			}
		}
		return output;

	}

	public static StreamedContent downloadTmpFile(String taskId, String formKey) {

		try {
			StreamedContent file_download = null;
			String path_dir = tmpDir + taskId + "/" + formKey + "/";

			File baseDir = new File(path_dir);
			if (baseDir.isDirectory()) {
				File[] subdir = baseDir.listFiles();
				for (int i = 0; i < subdir.length; i++) {
					if (subdir[i].isFile()) {
						file_download = new DefaultStreamedContent(
								new FileInputStream(subdir[i]),
								new MimetypesFileTypeMap()
										.getContentType(subdir[i]),
								subdir[i].getName());

						return file_download;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void deleteTmpFile(String taskId, String formKey) {

		String path_dir = tmpDir + taskId + "/" + formKey + "/";

		File baseDir = new File(path_dir);
		if (baseDir.isDirectory()) {
			File[] subdir = baseDir.listFiles();
			for (int i = 0; i < subdir.length; i++) {
				if (subdir[i].isFile()) {
//					System.out.println(subdir[i].getAbsolutePath());
					subdir[i].delete();
				}
			}
		}
	}

	public static File download(String taskId, String formKey) {

		String path_dir = tmpDir + taskId + "/" + formKey + "/";

		File baseDir = new File(path_dir);
		if (baseDir.isDirectory()) {
			File[] subdir = baseDir.listFiles();
			for (int i = 0; i < subdir.length; i++) {
				if (subdir[i].isFile()) {
					return subdir[i];

				}
			}
		}
		return null;
	}

	public static void downloadFile(String taskId, String formKey) {

		String path_dir = tmpDir + taskId + "/" + formKey + "/";

		File baseDir = new File(path_dir);
		if (baseDir.isDirectory()) {
			File[] subdir = baseDir.listFiles();
			for (int i = 0; i < subdir.length; i++) {
				if (subdir[i].isFile()) {
					try {
						File file = subdir[i];

						FacesContext fc = FacesContext.getCurrentInstance();
						ExternalContext ec = fc.getExternalContext();
						ec.responseReset();

						ec.setResponseContentType(new MimetypesFileTypeMap()
								.getContentType(file));
						ec.setResponseHeader("Content-Disposition",
								"attachment; filename=\"" + file.getName()
										+ "\"");

						OutputStream output;

						output = ec.getResponseOutputStream();

						InputStream inputStream = new FileInputStream(file);

						byte[] bytesBuffer = new byte[2048];
						int bytesRead;
						while ((bytesRead = inputStream.read(bytesBuffer)) > 0) {
							output.write(bytesBuffer, 0, bytesRead);
						}

						output.flush();

						inputStream.close();
						output.close();
						fc.responseComplete();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static void downloadFilePF(String taskId, String formKey) {

		String path_dir = tmpDir + taskId + "/" + formKey + "/";

		File baseDir = new File(path_dir);
		if (baseDir.isDirectory()) {
			File[] subdir = baseDir.listFiles();
			for (int i = 0; i < subdir.length; i++) {
				if (subdir[i].isFile()) {
					try {
						File file = subdir[i];
						PrimeFacesContext pc = (PrimeFacesContext) PrimeFacesContext
								.getCurrentInstance();
						ExternalContext ecpf = pc.getExternalContext();
						ecpf.responseReset();

						ecpf.setResponseContentType(new MimetypesFileTypeMap()
								.getContentType(file));
						ecpf.setResponseHeader("Content-Disposition",
								"attachment; filename=\"" + file.getName()
										+ "\"");

						OutputStream output;

						output = ecpf.getResponseOutputStream();

						InputStream inputStream = new FileInputStream(file);

						byte[] bytesBuffer = new byte[2048];
						int bytesRead;
						while ((bytesRead = inputStream.read(bytesBuffer)) > 0) {
							output.write(bytesBuffer, 0, bytesRead);
						}

						output.flush();

						inputStream.close();
						output.close();
						pc.responseComplete();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static DefaultStreamedContent initializeFile(String taskId) {

		try {

			String path_dir = tmpDir + taskId + "/";
			String path = path_dir + "tmp.file";
			File baseDir = new File(path_dir);
			if (!baseDir.exists()) {
				baseDir.mkdirs();
			}

			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			InputStream stream = ((ServletContext) FacesContext
					.getCurrentInstance().getExternalContext().getContext())
					.getResourceAsStream(path);
			return new DefaultStreamedContent(stream, "", file.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void borrarDirectorio(File f) {
		if (f.isDirectory()) {
			File[] ficheros = f.listFiles();

			for (int x = 0; x < ficheros.length; x++) {
				borrarDirectorio(ficheros[x]);
			}
		}

		f.delete();

	}

	/**
	 * Copia a un fichero un InputStream
	 * 
	 * @param fileName
	 * @param in
	 */
	private static void copyFile(String fileName, InputStream in) {
		try {
			// write the inputStream to a FileOutputStream
			File inf = new File(fileName);
			if (inf.getParentFile().exists()) {
				borrarDirectorio(inf.getParentFile());

			}
			inf.getParentFile().mkdirs();

			OutputStream out = new FileOutputStream(inf);

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
