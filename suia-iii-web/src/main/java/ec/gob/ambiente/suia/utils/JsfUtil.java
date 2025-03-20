/* 
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.utils;

import index.Intersecado_capa;
import index.Intersecado_entrada;
import index.Intersecado_resultado;
import index.Reproyeccion_entrada;
import index.Reproyeccion_resultado;
import index.SVA_Reproyeccion_IntersecadoPortTypeProxy;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.Collator;
import java.text.Format;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.MimetypesFileTypeMap;
import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DocumentosRgdRcoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DocumentosSustanciasQuimicasRcoa;
import ec.gob.ambiente.rcoa.util.NumeroTextoUtil;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.dto.TaskSummaryCustom;
import ec.gob.ambiente.suia.login.bean.LoginBean;


/**
 * @author magmasoft
 * 
 *         JSF Util bean para funcionalidades comunes
 */
public final class JsfUtil {

	public static final String MESSAGE_ERROR_INICIAR_PROCESO = "Ocurrió un error al iniciar el proceso.";
	public static final String MESSAGE_ERROR_COMPLETAR_PROCESO = "Ocurrió un error al completar el proceso.";
	public static final String MESSAGE_ERROR_INICIAR_TAREA = "Ocurrió un error al iniciar la tarea.";
	public static final String MESSAGE_ERROR_COMPLETAR_TAREA = "Ocurrió un error al completar la tarea.";
	public static final String MESSAGE_ERROR_COMPLETAR_TAREA_1 = "No existe un usuario con el rol coordinador para atender esta solicitud, Por favor comuníquese con mesa de ayuda.";
	public static final String MESSAGE_ERROR_CARGAR_DATOS = "Ocurrió un error al cargar los datos.";
	public static final String MESSAGE_ERROR_COMPLETAR_OPERACION = "Ocurrió un error al completar la operación.";
	public static final String MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA = "Ocurrió un error al completar la operación. Por favor comuníquese con Mesa de Ayuda.";
	public static final String MESSAGE_ERROR_ALFRESCO = "Ocurrió un error al descargar el documento del alfresco.";
	public static final String MESSAGE_ERROR_DOCUMENTO_NO_FIRMADO = ": el documento no está firmado electrónicamente.";
	public static final String MESSAGE_EXISTEN_ESPECIES = "No se puede eliminar el Punto de muestreo ya que existen especies relacionadas.";
	public static final String MESSAGE_PUNTOS_IGUALES = "Por favor, seleccione puntos de muestra distintos para comparar.";
	public static final String MESSAGE_EXISTE_COMPARACION = "Ya existe esta comparación. Por favor, seleccione una combinación de puntos diferente.";

	public static final String MESSAGE_INFO_PROCESO_COMPLETADO = "El proceso se ha completado satisfactoriamente.";
	public static final String MESSAGE_INFO_TAREA_COMPLETADA = "La tarea se ha completado satisfactoriamente.";
	public static final String MESSAGE_INFO_OPERACION_SATISFACTORIA = "La operación se realizó satisfactoriamente.";
	public static final String MESSAGE_ERROR_ANEXO_EXISTENTE = "El anexo seleccionado ya se encuentra registrado.";

	public static final String MESSAGE_INFO_NO_RESULTADOS = "No se encontraron resultados para la búsqueda.";
	public static final String MESSAGE_INFO_NO_RESULTADOSDESCONCENTRADO = "No tiene permisos para reasignar actividades fuera de su área";

	public static final String NAVIGATION_TO_BANDEJA = "/bandeja/bandejaTareas.jsf";
	public static final String MESSAGE_NO_EXISTE_DOCUMENTO = "Los documentos solicitados aún no han sido adjuntados.";

	public static final String STRING_SEPARATOR = ";;";
	public static final String ERROR_ACTUALIZAR_REGISTRO = "Error al actualizar el registro";
	public static final String ERROR_RESPONSABLE_AREA_DUPLICADO = "Ya existe un usuario asignado como responsable del área: ";
	public static final String REGISTRO_ACTUALIZADO = "Registro actualizado.";
	public static final String REGISTRO_GUARDADO = "La operación se realizó satisfactoriamente.";
	public static final String ERROR_GUARDAR_REGISTRO = "Error, la operación no se realizó satisfactoriamente. Por favor inténtelo más tarde.";
	public static final String ERROR_INICIALIZAR_DATOS = "Ocurrio un error al inicializar los datos. Por favor inténtelo más tarde.";

	public static final String MESSAGE_INFO_PROYECTO_AJENO = "Estimado usuario, por el momento su proyecto no está habilitado para iniciar el proceso de Emisión de Registro Generador. Para mayor información sobre cómo continuar su trámite, por favor contacte con mesa de ayuda";
	public static final String MESSAGE_COORDENADAS_EXISTENTES = "Las coordenadas que desea ingresar, ya fueron ingresadas con anterioridad; verifique ";

	private static Random random = new Random();
	private static final int TAMANIO_PASSWORD = 10;

	public static HttpServletRequest getRequest() {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		return request;
	}

	public static String getStartPage() {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest req = (HttpServletRequest) context.getExternalContext().getRequest();
		String url = req.getRequestURL().toString();
		return url.substring(0, url.indexOf(req.getContextPath()) + req.getContextPath().length());
	}

	/**
	 * Muestra un mensaje de informaci�n
	 * 
	 * @param message
	 *            texto del mensaje
	 */
	public static void addMessageInfo(String message) {
		addMessage(message, FacesMessage.SEVERITY_INFO);
	}

	/**
	 * Muestra un mensaje de advertencia
	 * 
	 * @param message
	 *            texto del mensaje
	 */
	public static void addMessageWarning(String message) {
		addMessage(message, FacesMessage.SEVERITY_WARN);
	}

	/**
	 * Muestra un mensaje de error
	 * 
	 * @param message
	 *            texto del mensaje
	 */
	public static void addMessageError(String message) {
		addMessage(message, FacesMessage.SEVERITY_ERROR);
	}
	
	public static void addMessageError() {
		addMessage(MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA, FacesMessage.SEVERITY_ERROR);
	}

	/**
	 * Muestra un mensaje de error en un hilo diferente
	 * 
	 * @param message
	 *            texto del mensaje
	 * @param delay
	 *            sleep en milisegundos
	 */
	public static void addMessageError(final String message, long delay) {
		try {
			new Timer().schedule(new TimerTask() {

				@Override
				public void run() {
					addMessageError(message);
				}
			}, delay);
		} catch (Exception e) {
		}
	}

	/**
	 * Muestra una lista de mensajes de error
	 * 
	 * @param messages
	 *            lista de mensajes
	 */
	public static void addMessageError(List<String> messages) {
		for (String message : messages) {
			addMessage(message, FacesMessage.SEVERITY_ERROR);
		}
	}

	/**
	 * Adiciona un mensaje al contexto
	 * 
	 * @param message
	 *            el texto a mostrar
	 * @param severity
	 *            el tipo de mensaje
	 */
	private static void addMessage(String message, Severity severity) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, message, null));
	}

	/**
	 * Adiciona un mensaje al contexto
	 * 
	 * @param message
	 *            el texto a mostrar
	 */
	public static void addMessageErrorForComponent(String component, String message) {
		FacesContext.getCurrentInstance().addMessage(component,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
	}

	/**
	 * Adiciona un mensaje al contexto desde un bundle
	 */
	public static String getMessageFromBundle(String bundleName, String key, Object... params) {
		String messageBundleName = bundleName;
		if (messageBundleName == null) {
			messageBundleName = FacesContext.getCurrentInstance().getApplication().getMessageBundle();
		}
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		ResourceBundle bundle = null;
		try {
			bundle = ResourceBundle.getBundle(messageBundleName, locale);
		} catch (Exception e) {
			bundle = FacesContext.getCurrentInstance().getApplication()
					.getResourceBundle(FacesContext.getCurrentInstance(), messageBundleName);
		}
		try {
			return MessageFormat.format(bundle.getString(key), params);
		} catch (Exception e) {
			return '!' + key + '!';
		}
	}

	/**
	 * Conforma el return de parametro que recibe un action para que se ejecute con redireccionamiento
	 * 
	 * @param url
	 *            faces-view a la que se pretende navegar
	 * @return url con el parametro de redireccion
	 */
	public static String actionNavigateTo(String url) {
		return url + "?faces-redirect=true";
	}

	/**
	 * Conforma el return de parametro que recibe un action para que se ejecute con redireccionamiento
	 * 
	 * @param url
	 *            faces-view a la que se pretende navegar
	 * @return url con el parametro de redireccion
	 */
	public static String actionNavigateTo(String url, String... params) {
		String add = "";
		if (params != null) {
			for (String string : params) {
				add += string + "&";
			}
			add = add.substring(0, add.length() - 1);
		}
		return url + "?faces-redirect=true" + (add.isEmpty() ? add : "&" + add);
	}

	public static String getRequestParameter(String key) {
		return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(key) == null ? ""
				: FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(key);
	}

	/**
	 * Navega a la bandeja de trabajo
	 * 
	 * @return url con el parametro de la bandeja
	 */
	public static String actionNavigateToBandeja() {
		return actionNavigateTo(NAVIGATION_TO_BANDEJA);
	}

	/**
	 * Obliga a redireccionar a una pagina
	 * 
	 * @param url
	 *            es la faces-view a la que se pretende redireccionar
	 */
	public static void redirectTo(String url) {
		try {
			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
			externalContext.redirect(externalContext.getRequestContextPath() + url);
		} catch (IOException e) {
			Logger.getLogger("").log(Level.SEVERE, e.getMessage());
		}
	}
	
	/**
	 * Obliga a redireccionar a la bandeja de tareas
	 * 
	 * @param url
	 *            es la faces-view a la que se pretende redireccionar
	 */
	public static void redirectToBandeja() {
		redirectTo(actionNavigateToBandeja());
	}

	/**
	 * Obtiene un managed bean del contexto
	 * 
	 * @return managed bean registrado con name
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(Class<T> beanType) {
		String customName = null;
		try {
			customName = beanType.getAnnotation(ManagedBean.class).annotationType().getDeclaredMethod("name")
					.invoke(beanType.getAnnotation(ManagedBean.class)).toString();
		} catch (Exception e) {

		}
		String standardBeanName = (beanType.getSimpleName().charAt(0) + "").toLowerCase()
				+ beanType.getSimpleName().substring(1);

		if (customName != null && !customName.isEmpty())
			standardBeanName = customName;

		ELContext elContext = FacesContext.getCurrentInstance().getELContext();
		return (T) FacesContext.getCurrentInstance().getApplication().getELResolver().getValue(elContext, null,
				standardBeanName);
	}

	/**
	 * Retorna un string en forma de lista html
	 * 
	 * @param value
	 *            string a convertir
	 * @return string con la lista
	 */
	public static String getStringAsHtmlUL(String value, boolean sort) {
		String[] arr = value.split(JsfUtil.STRING_SEPARATOR);
		if (sort) {
			Arrays.sort(arr);
		}
		int size = 0;
		String list = "<ul>";
		for (String string : arr) {
			if (!string.trim().isEmpty()) {
				size++;
				list += "<li>" + string.trim() + "</li>";
			}
		}
		list += "</ul>";
		if (size <= 1) {
			list = list.replaceAll("\\<.*?>", "");
		}
		return list.trim();
	}

	/**
	 * Retorna un map con key a partir de un enum, y con valores nulos
	 * 
	 * @param enumClass
	 *            enum para generar las key
	 * 
	 * @return mapa inicializado
	 */
	public static Map<String, String> generateEmptyMapString(final Class<?> enumClass) {
		final Object constants[] = enumClass.getEnumConstants();
		final Map<String, String> map = new ConcurrentHashMap<String, String>();
		for (final Object object : constants) {
			map.put(((Enum<?>) object).name(), "");
		}
		return map;
	}

	/**
	 * Pasar parametros al metodo con EL directamente, ejemplo: <h:dataTabla value="#{bean.data}" var="variable"> ....
	 * <h:commandButton value="OK" actionListener="#{controller.actionToDo(variable)}" />
	 */
	@Deprecated
	public static Object seleccionar(ActionEvent evt, String llave) {
		Map<String, Object> id = evt.getComponent().getAttributes();
		return id.get(llave);
	}

	/**
	 * Devuelve una cadena encriptada en md5
	 * 
	 * @param password
	 * @return
	 */
	public static String claveEncriptadaMd5(String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(password.getBytes());
			BigInteger number = new BigInteger(1, messageDigest);
			String hashtext = number.toString(16);
			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}
			return hashtext;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Devuelve una cadena encriptada en sha1
	 * 
	 * @param password
	 * @return
	 */
	public static String claveEncriptadaSHA1(String password) {
		try {
			byte[] buffer = password.getBytes();
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(buffer);
			byte[] digest = md.digest();
			String valorHash = "";
			for (byte aux : digest) {
				int b = aux & 0xff;
				if (Integer.toHexString(b).length() == 1) {
					valorHash += "0";
				}
				valorHash += Integer.toHexString(b);
			}
			return valorHash;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Genera una cadena de caracteres aleatorea
	 * 
	 * @param onlyChars
	 * @return
	 */
	public static synchronized String generatePassword(boolean... onlyChars) {
		String passwd = "";
		boolean isChar = onlyChars == null ? false : onlyChars.length > 0 ? onlyChars[0] : false;
		for (char c : complete("" + (int) (random.nextDouble() * 99999999), TAMANIO_PASSWORD, '0', true)
				.toCharArray()) {
			int value = (int) (Integer.parseInt("" + c) + Math.round(Math.random() * 120));
			char cc = (char) value;
			if (Character.isLetter(cc) & Character.isDefined(cc) & !Character.isWhitespace(cc)) {
				passwd += cc;
			} else {
				value = (int) (isChar ? Math.round(Math.random() * 25) + 65 : value);
				passwd += isChar ? (char) value : c;
			}
		}
		return passwd;
	}

	/**
	 * Permite complementar una determinada cadena de texto con un caracter especificado
	 * 
	 * @param data
	 *            Cadena de texto original
	 * @param length
	 *            longitud deseada
	 * @param complete
	 *            caracter con el cual se completara la cadena
	 * @param reverse
	 *            indica si la cadena se complementara al fina(false) o al inicio(true)
	 * @return cadena complementada, si la longitid es menor a la cadena original se retornara la original sin ccambios
	 */
	public static synchronized String complete(String data, final int length, final char complete,
			final boolean reverse) {
		final int size = data.length();
		StringBuilder build = new StringBuilder();
		if (reverse) {
			for (int i = size; i < length; i++) {
				build.append(complete);
			}
			build.append(data);
		} else {
			build.append(data);
			for (int i = size; i < length; i++) {
				build.append(complete);
			}
		}
		return build.toString();
	}

	/**
	 * Carga objeto a session
	 * 
	 * @param nombre
	 * @param object
	 */
	public static void cargarObjetoSession(final String nombre, final Object object) {
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getSessionMap().put(nombre, object);
	}

	/**
	 * Devuelve un objeto cargado a session
	 * 
	 * @param nombre
	 * @return object
	 */
	public static Object devolverObjetoSession(final String nombre) {
		HttpServletRequest request = getRequest();
		return request.getSession().getAttribute(nombre);
	}

	/**
	 * Devuelve un objeto cargado a session y lo elimina
	 * 
	 * @param nombre
	 * @return object
	 */
	public static Object devolverEliminarObjetoSession(final String nombre) {
		HttpServletRequest request = getRequest();
		Object object = request.getSession().getAttribute(nombre);
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getSessionMap().remove(nombre);
		return object;
	}
	
	/**
	    * Recupera el objeto de ssion y dependiendo de la bandera lo elimina al ser extraido
	    *
	    * @param nombreClave
	    * @param tipoRespuesta
	    * @param eliminarAlRecuperar
	    * @param <T>
	    * @return
	    */
	   public static <T> T recuperarObjetoDeSession(String nombreClave, Class<T> tipoRespuesta, boolean eliminarAlRecuperar) {
	       ExternalContext contexto = FacesContext.getCurrentInstance().getExternalContext();
	       Object repuesta = contexto.getSessionMap().get(nombreClave);
	       if (NumeroTextoUtil.esVacio(repuesta)) {
	           return null;
	       }

	       if (eliminarAlRecuperar) {
	    	   eliminarObjetoSession(nombreClave);
	       }
	       return (T) repuesta;
	   }

	public static void eliminarObjetoSession(final String... nombre) {
		FacesContext context = FacesContext.getCurrentInstance();
		for (String s : nombre) {
			context.getExternalContext().getSessionMap().remove(s);
		}
	}

	/**
	 * Valida si una pagina tiene permisos de acceso desde una session iniciada.
	 * 
	 * @param pagina
	 */
	public static void validarPagina(final String pagina) {
		@SuppressWarnings("unchecked")
		List<String> listaPermisos = (List<String>) devolverObjetoSession("listaPermisos");
		if (listaPermisos != null && !listaPermisos.isEmpty()) {
			if (!listaPermisos.contains(pagina)) {
				// redirectTo("/errors/permisos.jsf");
			}
		}
	}

	/**
	 * 
	 * <b> Compara 2 cadenas ignorando mayusculas y tildes. </b>
	 * 
	 * @author Carlos Pupo
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: Carlos Pupo, Fecha: 23/01/2015]
	 *          </p>
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static boolean comparePrimaryStrings(String s1, String s2) {
		Collator collator = Collator.getInstance();
		collator.setStrength(Collator.PRIMARY);
		return collator.equals(s1, s2);
	}

	/**
	 * 
	 * <b> Verifica si una cadena esta entre una de las opciones ignorando mayusculas y tildes. </b>
	 * 
	 * @author Carlos Pupo
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: Carlos Pupo, Fecha: 23/01/2015]
	 *          </p>
	 * @param string
	 * @param strings
	 * @return
	 */
	public static boolean isStringInPrimaryStrings(String string, String[] strings) {
		for (String s : strings) {
			if (comparePrimaryStrings(string, s)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * <b> Convierte una cadena a una de la opciones, compara ignorando mayusculas y tildes, si no es encontrada,
	 * retorna la misma cadena </b>
	 * 
	 * @author Carlos Pupo
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: Carlos Pupo, Fecha: 23/01/2015]
	 *          </p>
	 * @param string
	 * @param strings
	 * @return
	 */
	public static String getStringAsAnyPrimaryStrings(String string, String[] strings) {
		for (String s : strings) {
			if (comparePrimaryStrings(string, s)) {
				return s;
			}
		}
		return string;
	}

	/**
	 * 
	 * <b> Genera una lista a partir de argumentos variables. </b>
	 * 
	 * @author Carlos Pupo
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: Carlos Pupo, Fecha: 26/01/2015]
	 *          </p>
	 * @param objects
	 * @return
	 */
	@SafeVarargs
	public static <T> List<T> getAsList(T... objects) {
		return Arrays.asList(objects);
	}

	/**
	 * 
	 * <b> Obtiene el usuario autenticado, si no no ha iniciado session retorna null. </b>
	 * 
	 * @author Carlos Pupo
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: Carlos Pupo, Fecha: 30/01/2015]
	 *          </p>
	 * @return
	 */
	public static Usuario getLoggedUser() {
		LoginBean instance = getBean(LoginBean.class);
		if (instance.getUsuario().isPersisted()) {
			instance.getUsuario().setPassword(instance.getPassword());
			return instance.getUsuario();
		}
		return null;
	}

	/**
	 * 
	 * <b> Obtiene el process instance id actual. </b>
	 * 
	 * @author Carlos Pupo
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: Carlos Pupo, Fecha: 09/03/2015]
	 *          </p>
	 * @return
	 */
	public static long getCurrentProcessInstanceId() {
		BandejaTareasBean instance = getBean(BandejaTareasBean.class);
		return instance.getProcessId();
	}

	/**
	 * 
	 * <b> Obtiene la tarea actual. </b>
	 * 
	 * @author Carlos Pupo
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: Carlos Pupo, Fecha: 09/03/2015]
	 *          </p>
	 * @return
	 */
	public static TaskSummaryCustom getCurrentTask() {
		BandejaTareasBean instance = getBean(BandejaTareasBean.class);
		return instance.getTarea();
	}

	/**
	 * 
	 * <b> Envia parametro request para modals. </b>
	 * 
	 * @author Carlos Pupo
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: Carlos Pupo, Fecha: 19/02/2015]
	 *          </p>
	 * @return
	 */
	public static void addCallbackParam(String name) {
		RequestContext.getCurrentInstance().addCallbackParam(name, true);
	}

	/**
	 * Retorna un File del event
	 * 
	 * @author Frank Torres
	 * @param event
	 * @return
	 */
	public static File upload(FileUploadEvent event) {
		File folder = new File(System.getProperty("java.io.tmpdir"));
		UploadedFile arq = event.getFile();
		String fileName = validateFileName(arq.getFileName());
		try {
			InputStream input = arq.getInputstream();
			File file = new File(folder, fileName);
			OutputStream out = new FileOutputStream(file);
			int read = 0;
			byte[] bytes = new byte[1024 * 1024 * 130];
			while ((read = input.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			input.close();
			out.flush();
			out.close();
			return file;
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return null;
		}
	}

	public static String validateFileName(String fileName) {
		if (fileName.indexOf(":") > -1) {
			return fileName.replace(":", "");
		}
		return fileName;
	}

	/**
	 * ordena una lista por cualquier propiedad
	 * 
	 * @author Christian
	 * @param lista
	 * @param propiedad
	 * 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void ordenarLista(List lista, final String propiedad) {
		Collections.sort(lista, new Comparator() {

			@Override
			public int compare(Object obj1, Object obj2) {

				Class clase = obj1.getClass();
				String getter = "get" + Character.toUpperCase(propiedad.charAt(0)) + propiedad.substring(1);
				try {

					Method getPropiedad = clase.getMethod(getter);

					Object propiedad1 = getPropiedad.invoke(obj1);
					Object propiedad2 = getPropiedad.invoke(obj2);

					if (propiedad1 instanceof Comparable && propiedad2 instanceof Comparable) {
						Comparable prop1 = (Comparable) propiedad1;
						Comparable prop2 = (Comparable) propiedad2;
						return prop1.compareTo(prop2);
					} else {
						if (propiedad1.equals(propiedad2)) {
							return 0;
						} else {
							return 1;
						}

					}

				} catch (NoSuchMethodException e) {
					Logger.getLogger("").log(Level.SEVERE, e.getMessage());
				} catch (SecurityException e) {
					Logger.getLogger("").log(Level.SEVERE, e.getMessage());
				} catch (IllegalAccessException e) {
					Logger.getLogger("").log(Level.SEVERE, e.getMessage());
				} catch (IllegalArgumentException e) {
					Logger.getLogger("").log(Level.SEVERE, e.getMessage());
				} catch (InvocationTargetException e) {
					Logger.getLogger("").log(Level.SEVERE, e.getMessage());
				}
				return 0;
			}
		});
	}

	/**
	 * Devuelve una cadena separada por comas mandando un vector
	 * 
	 * @param datos
	 * @return
	 */
	public static String transformaVector(String[] datos) {
		int i = 1;
		String concatena = "";
		for (String l : datos) {
			if (i != datos.length) {
				concatena = concatena.concat(l.concat(","));
			} else {
				concatena = concatena.concat(l + "");
			}
			i++;
		}
		return concatena;
	}

	/**
	 * Devuelve un vector mandando una cadena con comas
	 * 
	 * @param datos
	 * @return
	 */
	public static String[] devuelveVector(String datos) {
		try {
			StringTokenizer tokens = new StringTokenizer(datos, ",");
			int numeroTokens = tokens.countTokens();
			String[] devuelve = new String[numeroTokens];
			int i = 0;
			while (tokens.hasMoreTokens()) {
				devuelve[i] = tokens.nextToken();
				i++;
			}
			return devuelve;
		} catch (Exception e) {
			return new String[0];
		}
	}

	/**
	 * Devuelve la extensión del nombre de un archivo
	 * 
	 * @param archivo
	 * @return
	 */
	public static String devuelveExtension(String archivo) {
		StringTokenizer st = new StringTokenizer(archivo, ".");
		String extension = "";
		while (st.hasMoreTokens()) {
			extension = st.nextToken();
		}
		return extension;
	}

	/**
	 * 
	 * <b> Retorna la fecha en formato castellano String. </b>
	 * 
	 * @author Juan Carlos Gras
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: Juan Carlos Gras, Fecha: 19/03/2015]
	 *          </p>
	 * @return
	 * @return
	 */
	public static String getDateFormat(Integer day, Integer month, Integer year) {
		SimpleDateFormat formateador = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("es_ES"));
		Calendar calendar = new GregorianCalendar(year, month, day);
		return formateador.format(calendar.getTime());
	}

	/**
	 * 
	 * <b> Retorna la fecha en formato castellano String. </b>
	 * 
	 * @author Juan Carlos Gras
	 * @version Revision: 1.0
	 *          <p>
	 *          [Autor: Juan Carlos Gras, Fecha: 19/03/2015]
	 *          </p>
	 * @return
	 * @return
	 */
	public static String getDateFormat(Date date) {
		Calendar fecha = Calendar.getInstance();
		fecha.setTime(date);
		return devuelveDiaSemana(fecha.get(Calendar.DAY_OF_WEEK)) + " " + fecha.get(Calendar.DAY_OF_MONTH) + " de "
				+ devuelveMes(fecha.get(Calendar.MONTH)) + " " + fecha.get(Calendar.YEAR);
	}
	
	
	public static String getSimpleDateFormat(Date date) {
		SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");		
		return formateador.format(date);
	}

	public static String getFromBundle(String bundlePath, String key) {
		return ResourceBundle.getBundle(bundlePath, new Locale("es")).getString(key);
	}

	public static void descargarPdf(final byte[] bytes, String nombrePdf) throws IOException {
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
				.getResponse();
		response.setHeader("Content-Type", "application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=" + nombrePdf + ".pdf");
		OutputStream out = response.getOutputStream();
		out.write(bytes);
		out.close();
		FacesContext.getCurrentInstance().responseComplete();
	}

	public static void descargarMimeType(final byte[] bytes, String nombreArchivo, String extension, String mime)
			throws IOException {
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
				.getResponse();
		response.setHeader("Content-Type", mime);
		response.setHeader("Content-Disposition", "attachment; filename=" + nombreArchivo + "." + extension);
		OutputStream out = response.getOutputStream();
		out.write(bytes);
		out.close();
		FacesContext.getCurrentInstance().responseComplete();
	}

	public static String devolverPathReportesHtml(final String reporteHtml) {
		return getRequest().getSession().getServletContext().getRealPath("/reportesHtml/" + reporteHtml);
	}

	public static String devolverContexto(final String pathArchivo) {
		return getRequest().getContextPath() + pathArchivo;
	}

	public static String devuelveFechaEnLetrasSinHora(Date fechaParametro) {
		Calendar fecha = Calendar.getInstance();
		fecha.setTime(fechaParametro);
		return devuelveDiaSemana(fecha.get(Calendar.DAY_OF_WEEK)) + ", " + fecha.get(Calendar.DAY_OF_MONTH) + " de "
				+ devuelveMes(fecha.get(Calendar.MONTH)) + " " + fecha.get(Calendar.YEAR);
	}

	public static String devuelveFechaEnLetrasConHora(Date fechaParametro) {
		Calendar fecha = Calendar.getInstance();
		fecha.setTime(fechaParametro);
		String hora = String.format("%02d:%02d", fecha.get(Calendar.HOUR_OF_DAY), fecha.get(Calendar.MINUTE));
		return fecha.get(Calendar.DAY_OF_MONTH) + " días del mes de "
				+ devuelveMes(fecha.get(Calendar.MONTH)) + " de " + fecha.get(Calendar.YEAR)+", a las " +  hora;
	}
	
	public static String devuelveFechaEnLetrasSinHora1(Date fechaParametro) {
		Calendar fecha = Calendar.getInstance();
		fecha.setTime(fechaParametro);
		return  fecha.get(Calendar.DAY_OF_MONTH) + " de "
				+ devuelveMes(fecha.get(Calendar.MONTH)) + " " + fecha.get(Calendar.YEAR);
	}

	public static String devuelveFechaConHora(Date date) {
		Calendar fecha = Calendar.getInstance();
		fecha.setTime(date);
		String hora = String.format("%02d-%02d", fecha.get(Calendar.HOUR_OF_DAY), fecha.get(Calendar.MINUTE));
		return fecha.get(Calendar.DAY_OF_MONTH) + "-" + devuelveMes(fecha.get(Calendar.MONTH)) + "-" + fecha.get(Calendar.YEAR) + "-" + hora;
	}

	private static String devuelveDiaSemana(int dia) {
		switch (dia) {
		case 1:
			return "domingo";
		case 2:
			return "lunes";
		case 3:
			return "martes";
		case 4:
			return "miércoles";
		case 5:
			return "jueves";
		case 6:
			return "viernes";
		case 7:
			return "sabado";
		default:
			return "";

		}
	}

	public static String devuelveMes(int mes) {
		switch (mes) {
		case 0:
			return "enero";
		case 1:
			return "febrero";
		case 2:
			return "marzo";
		case 3:
			return "abril";
		case 4:
			return "mayo";
		case 5:
			return "junio";
		case 6:
			return "julio";
		case 7:
			return "agosto";
		case 8:
			return "septiembre";
		case 9:
			return "octubre";
		case 10:
			return "noviembre";
		case 11:
			return "diciembre";
		default:
			return "";
		}
	}

	public static boolean validarMail(String email) {
		boolean valido = false;
		Pattern patronEmail = Pattern.compile(
				"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
		Matcher mEmail = patronEmail.matcher(email);
		if (mEmail.matches()) {
			valido = true;
		}
		return valido;
	}

	public static void validarPaginasUrlTareasBpm(final TaskSummaryCustom taskSummaryCustom, final String paginaJsf) {
		if (taskSummaryCustom == null) {
			redirectTo("/bandeja/bandejaTareas.jsf");
		} else {
			if (taskSummaryCustom.getTaskSummary() == null) {
				redirectTo("/bandeja/bandejaTareas.jsf");
			} else {
				String urlTarea = taskSummaryCustom.getTaskSummary().getDescription();
				if (!urlTarea.equals(paginaJsf)) {
					redirectTo("/bandeja/bandejaTareas.jsf");
				}
			}
		}
	}

	public static long devuelveFechaTruncadaMilisegundos(Date fecha, int valorTruncado) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(fecha);
		cal.set(Calendar.HOUR_OF_DAY, valorTruncado);
		cal.set(Calendar.MINUTE, valorTruncado);
		cal.set(Calendar.SECOND, valorTruncado);
		cal.set(Calendar.MILLISECOND, valorTruncado);
		return cal.getTime().getTime();
	}

	/**
	 * Valida RUC o Cedula
	 * 
	 * @param numero
	 * @return
	 */
	public static boolean validarCedulaORUC(String numero) {
		try {
			Long.valueOf(numero);			
		} catch (NumberFormatException nfe){
			System.out.println("Error al validarCedulaORUC: <"+numero+"> debe contener solo numeros");			
			return false;
		}
		int suma = 0;
		int residuo = 0;
		boolean privada = false;
		boolean publica = false;
		boolean natural = false;
		int numeroProvincias = 24;
		int digitoVerificador = 0;
		int modulo = 11;

		int d1, d2, d3, d4, d5, d6, d7, d8, d9, d10;
		int p1, p2, p3, p4, p5, p6, p7, p8, p9;

		d1 = d2 = d3 = d4 = d5 = d6 = d7 = d8 = d9 = d10 = 0;
		p1 = p2 = p3 = p4 = p5 = p6 = p7 = p8 = p9 = 0;

		if (numero.length() < 10) {
			return false;
		}

		// Los primeros dos digitos corresponden al codigo de la provincia
		int provincia = Integer.parseInt(numero.substring(0, 2));

		if (provincia <= 0 || provincia > numeroProvincias || provincia == 30) {
			return false;
		}

		// Almacena los digitos de la cedula en variables.
		d1 = Integer.parseInt(numero.substring(0, 1));
		d2 = Integer.parseInt(numero.substring(1, 2));
		d3 = Integer.parseInt(numero.substring(2, 3));
		d4 = Integer.parseInt(numero.substring(3, 4));
		d5 = Integer.parseInt(numero.substring(4, 5));
		d6 = Integer.parseInt(numero.substring(5, 6));
		d7 = Integer.parseInt(numero.substring(6, 7));
		d8 = Integer.parseInt(numero.substring(7, 8));
		d9 = Integer.parseInt(numero.substring(8, 9));
		d10 = Integer.parseInt(numero.substring(9, 10));

		// El tercer digito es:
		// 9 para sociedades privadas y extranjeros
		// 6 para sociedades publicas
		// menor que 6 (0,1,2,3,4,5) para personas naturales
		if (d3 == 7 || d3 == 8) {
			return false;
		}

		// Solo para personas naturales (modulo 10)
		if (d3 < 6) {
			natural = true;
			modulo = 10;
			p1 = d1 * 2;
			if (p1 >= 10) {
				p1 -= 9;
			}
			p2 = d2 * 1;
			if (p2 >= 10) {
				p2 -= 9;
			}
			p3 = d3 * 2;
			if (p3 >= 10) {
				p3 -= 9;
			}
			p4 = d4 * 1;
			if (p4 >= 10) {
				p4 -= 9;
			}
			p5 = d5 * 2;
			if (p5 >= 10) {
				p5 -= 9;
			}
			p6 = d6 * 1;
			if (p6 >= 10) {
				p6 -= 9;
			}
			p7 = d7 * 2;
			if (p7 >= 10) {
				p7 -= 9;
			}
			p8 = d8 * 1;
			if (p8 >= 10) {
				p8 -= 9;
			}
			p9 = d9 * 2;
			if (p9 >= 10) {
				p9 -= 9;
			}
		}

		// Solo para sociedades publicas (modulo 11)
		// Aqui el digito verficador esta en la posicion 9, en las otras 2
		// en la pos. 10
		if (d3 == 6) {
			publica = true;
			p1 = d1 * 3;
			p2 = d2 * 2;
			p3 = d3 * 7;
			p4 = d4 * 6;
			p5 = d5 * 5;
			p6 = d6 * 4;
			p7 = d7 * 3;
			p8 = d8 * 2;
			p9 = 0;
		}

		/*
		 * Solo para entidades privadas (modulo 11)
		 */
		if (d3 == 9) {
			privada = true;
			p1 = d1 * 4;
			p2 = d2 * 3;
			p3 = d3 * 2;
			p4 = d4 * 7;
			p5 = d5 * 6;
			p6 = d6 * 5;
			p7 = d7 * 4;
			p8 = d8 * 3;
			p9 = d9 * 2;
		}

		suma = p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9;
		residuo = suma % modulo;

		// Si residuo=0, dig.ver.=0, caso contrario 10 - residuo
		digitoVerificador = residuo == 0 ? 0 : modulo - residuo;
		int longitud = numero.length();
		// ahora comparamos el elemento de la posicion 10 con el dig. ver.
		if (publica) {
			if (digitoVerificador != d9) {
				return false;
			}
			/*
			 * El ruc de las empresas del sector publico terminan con 0001
			 */
			if (!numero.substring(9, longitud).equals("0001")) {
				return false;
			}
		}

		if (privada) {
			if (digitoVerificador != d10) {
				return false;
			}
			if (!numero.substring(10, longitud).equals("001")) {
				return false;
			}
		}

		if (natural) {
			if (digitoVerificador != d10) {
				return false;
			}
			if (numero.length() > 10 && !numero.substring(10, longitud).equals("001")) {
				return false;
			}
		}
		return true;
	}

	public static String devolverPathImagenEnte(String imagen) {
		return getRequest().getSession().getServletContext().getRealPath("/resources/images/ente/" + imagen);
	}

	public static String devolverPathImagenMae() {
		return getRequest().getSession().getServletContext().getRealPath("/resources/images/logo_mae_pie.png");
	}

	public static String rellenarCeros(String cadena, int tamanio) {
		for (int i = cadena.length(); i < tamanio; i++) {
			cadena = "0" + cadena;
		}
		return cadena;
	}

	public static int getCurrentYear() {
		return Calendar.getInstance().get(1);
	}

	public static int getDayFromDate(Date fecha) {
		Calendar c = Calendar.getInstance();
		c.setTime(fecha);
		return c.get(7);
	}

	public static int getMonthFromDate(Date fecha) {
		Calendar c = Calendar.getInstance();
		c.setTime(fecha);
		return c.get(2);
	}

	public static int getYearFromDate(Date fecha) {
		Calendar c = Calendar.getInstance();
		c.setTime(fecha);
		return c.get(1);
	}

	public static String getRelativeCurrentPage() {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest req = (HttpServletRequest) context.getExternalContext().getRequest();
		String url = req.getRequestURL().toString();
		return url.substring(url.indexOf(req.getContextPath()) + "/suia-iii".length());
	}

	public static UIComponent createComponent(String name) {
		return FacesContext.getCurrentInstance().getApplication().createComponent(name);
	}

	public static MethodExpression createMethodExpression(String expression, Class<?> returnType) {
		FacesContext context = FacesContext.getCurrentInstance();
		return context.getApplication().getExpressionFactory().createMethodExpression(context.getELContext(),
				expression, returnType, new Class[0]);
	}

	public static String obtenerArticuloSegunTratamiento(String tratamiento) {
		String articulo = "el/la";
		if (tratamiento != null && !tratamiento.trim().isEmpty()) {
			if (tratamiento.contains("a"))
				articulo = "la";
			else
				articulo = "el";
		}
		return articulo;
	}

	public static String obtenerCargoUsuario(Usuario usuario) {
		String cargo = "";
		if (usuario.getPersona() != null && usuario.getPersona().getPosicion() != null)
			cargo = usuario.getPersona().getPosicion();
		return cargo;
	}

	public static File fileMarcaAguaRCoaHorizontal(File file,String texto,BaseColor color) {
        try {
            String nombre = file.getAbsolutePath();
            PdfReader reader = new PdfReader(nombre);
            int n = reader.getNumberOfPages();

            // Create a stamper that will copy the document to a new file
            PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(
                    file.getAbsolutePath() + ".tmp"));
            int i = 1;
            PdfContentByte under;

            BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA,
                    BaseFont.WINANSI, BaseFont.EMBEDDED);

            while (i <= n) {
                // Watermark under the existing page
                under = stamp.getOverContent(i);        
                under.beginText();
                under.setFontAndSize(bf, 48);
                under.setColorFill(color);
                // under.showText("Borrador ");
                under.showTextAligned(1, texto, 400, 300, 45);
                under.endText();
                i++;
            }

            stamp.close();
            File borrador = new File(file.getAbsolutePath() + ".tmp");
            borrador.renameTo(file);
            return new File(nombre);
        } catch (Exception de) {

            return file;
        }

    }

	public static File fileMarcaAguaRCoa(File file,String texto,BaseColor color) {
        try {
            String nombre = file.getAbsolutePath();
            PdfReader reader = new PdfReader(nombre);
            int n = reader.getNumberOfPages();

            // Create a stamper that will copy the document to a new file
            PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(
                    file.getAbsolutePath() + ".tmp"));
            int i = 1;
            PdfContentByte under;

            BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA,
                    BaseFont.WINANSI, BaseFont.EMBEDDED);

            while (i <= n) {
                // Watermark under the existing page
                under = stamp.getOverContent(i);        
                under.beginText();
                under.setFontAndSize(bf, 48);
                under.setColorFill(color);
                // under.showText("Borrador ");
                under.showTextAligned(1, texto, 300, 500, 45);
                under.endText();
                i++;
            }

            stamp.close();
            File borrador = new File(file.getAbsolutePath() + ".tmp");
            borrador.renameTo(file);
            return new File(nombre);
        } catch (Exception de) {

            return file;
        }

    }
	
	public static File fileMarcaAgua(File file,String texto,BaseColor color) {
        try {
            String nombre = file.getAbsolutePath();
            PdfReader reader = new PdfReader(nombre);
            int n = reader.getNumberOfPages();

            // Create a stamper that will copy the document to a new file
            PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(
                    file.getAbsolutePath() + ".tmp"));
            int i = 1;
            PdfContentByte under;
            PdfContentByte over;

            BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA,
                    BaseFont.WINANSI, BaseFont.EMBEDDED);

            while (i <= n) {
                // Watermark under the existing page
                under = stamp.getUnderContent(i);
                
                // Text over the existing page
                over = stamp.getOverContent(i);           
                under.beginText();
                under.setFontAndSize(bf, 48);
                under.setColorFill(color);
                // under.showText("Borrador ");
                under.showTextAligned(1, texto, 300, 500, 45);
                under.endText();
                                
                i++;
            }

            stamp.close();
            File borrador = new File(file.getAbsolutePath() + ".tmp");
            borrador.renameTo(file);
            return new File(nombre);
        } catch (Exception de) {

            return file;
        }

    }
	public static void uploadApdoDocument(File file,Documento document){
		Path path = Paths.get(file.getAbsolutePath());
		try {
			document.setContenidoDocumento(Files.readAllBytes(path));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
		document.setMime(mimeTypesMap.getContentType(file));
		document.setNombre(file.getName());
		String[] split=document.getNombre().split("\\.");
		document.setExtesion("."+split[split.length-1]);
	}
	
	public static void uploadApdoDocumentRGD(File file,DocumentosRgdRcoa document){
		Path path = Paths.get(file.getAbsolutePath());
		try {
			document.setContenidoDocumento(Files.readAllBytes(path));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
		document.setMime(mimeTypesMap.getContentType(file));
		document.setNombre(file.getName());
		String[] split=document.getNombre().split("\\.");
		document.setExtesion("."+split[split.length-1]);
	}
	
	public static void uploadDocumentoRSQ(File file,DocumentosSustanciasQuimicasRcoa documento){
		Path path = Paths.get(file.getAbsolutePath());
		try {
			documento.setContenidoDocumento(Files.readAllBytes(path));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
		documento.setMime(mimeTypesMap.getContentType(file));
		documento.setNombre(file.getName());
		String[] split=documento.getNombre().split("\\.");
		documento.setExtesion("."+split[split.length-1]);
		
		if(documento.getExtesion().contains("pdf")) {
			documento.setMime("application/pdf");
		}
	}
	
	
	//Devuelve las siglas apartir de una cadena ejemplo Santiago Flores SF
	public static String devuelveSiglas(String nombre){
		String user = nombre;
		String iniciales = "";
		StringTokenizer st = new StringTokenizer(user);
		while (st.hasMoreTokens()) {
		iniciales = iniciales + st.nextToken().charAt(0);
		}
		return iniciales;
	}
	
	/**
	 * Crear imagen codigo QR
	 * @param content contenido
	 * @param fileSrc ruta de la imagen
	 */	
	public static void writeQRCode(String content,String fileSrc,int width,int height) {
	    QRCodeWriter writer = new QRCodeWriter();
	    
	    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); // create an empty image
	    int white = 255 << 16 | 255 << 8 | 255;
	    int black = 0;
	    try {
	    	Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
	    	hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
	    	hints.put(EncodeHintType.MARGIN, 0); /* default = 4 */
	    	
	        BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
	        
	        for (int i = 0; i < width; i++) {
	            for (int j = 0; j < height; j++) {
	                image.setRGB(i, j, bitMatrix.get(i, j) ? black : white); // set pixel one by one
	            }
	        }
	 
	        try {
	            ImageIO.write(image, "png", new File(fileSrc)); // save QR image to disk
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	 
	    } catch (Exception e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	}
	
	public static String getSenderIp() {				
		return getRequest().getRemoteAddr();
	}

	public static String getNombreOperador(Usuario usuarioOperador, Organizacion organizacion) {
		try {
			String nombreOperador = "";
			if (usuarioOperador.getPersona().getOrganizaciones().size() == 0) {
				nombreOperador = usuarioOperador.getPersona().getNombre();
			} else {
				nombreOperador = organizacion.getNombre();
			}

			return nombreOperador;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String devuelveDiaMesEnLetras(Date fechaParametro) {
		Calendar fecha = Calendar.getInstance();
		fecha.setTime(fechaParametro);
		return fecha.get(Calendar.DAY_OF_MONTH) + " de " + devuelveMes(fecha.get(Calendar.MONTH));
	}
	
	public static String validarCoordenadaPunto17S(String coordenadaX, String coordenadaY, String tramite) {

		String coordUbicacion = coordenadaX + " " + coordenadaY + " 17S";

		try {
			SVA_Reproyeccion_IntersecadoPortTypeProxy ws = new SVA_Reproyeccion_IntersecadoPortTypeProxy();
			
			Reproyeccion_resultado[] resultadoReproyeccion;
			Reproyeccion_entrada reproyeccion_entrada = new Reproyeccion_entrada(
					Constantes.getUserWebServicesSnap(),
					tramite, "WGS84", coordUbicacion);
			
			resultadoReproyeccion = ws.reproyeccion(reproyeccion_entrada);
			
			if (resultadoReproyeccion[0].getCoordenada()[0].getError() == null) {
				String[] coordenadasReproyectadas = resultadoReproyeccion[0].getCoordenada()[0].getXy().split(" ");
                
				coordUbicacion = coordenadasReproyectadas[0] + " " + coordenadasReproyectadas[1];
				
				Intersecado_resultado[] resultadoInterseccion;
				Intersecado_entrada inter = new Intersecado_entrada(
						Constantes.getUserWebServicesSnap(), 
						tramite, "pu", coordUbicacion, "dp");
				
				resultadoInterseccion = ws.interseccion(inter);
				
				if (resultadoInterseccion[0].getInformacion().getError() != null) {
					return resultadoInterseccion[0].getInformacion().getError().toString();
				} else {
					for (Intersecado_capa intersecado_capa : resultadoInterseccion[0].getCapa()) {
						if (intersecado_capa.getError() != null) {
							return intersecado_capa.getError().toString();
						}
					}
				}
			} else {
				return "Error en la reproyección de las coordenadas.";
			}
		} catch (Exception e) {
			return "Error en la verificación de las coordenadas.";
		}

		return null;
    }
	
	public static Date sumarDiasAFecha(Date fecha, int dias){
	      if (dias==0) return fecha;
	      Calendar calendar = Calendar.getInstance();
	      calendar.setTime(fecha); 
	      calendar.add(Calendar.DAY_OF_YEAR, dias);  
	      return calendar.getTime(); 
	}
	
	public static boolean getTokenUser() {
		if(Constantes.getAmbienteProduccion())
			return true;
		return getLoggedUser().getToken()!=null?getLoggedUser().getToken():false;
	}
	
	public static Date getFecha(String fecha,String formato) {
		try {
			return new SimpleDateFormat(formato).parse(fecha);
		} catch (ParseException e) {
			return null;
		}  
		
	}
	
	/**
	 * Metodo agrega marca de agua sobre el contenido del archivo en orientacion HORIZONTAL
	 */
	public static File fileMarcaAguaOverH(File file,String texto,BaseColor color) {
        try {
            String nombre = file.getAbsolutePath();
            PdfReader reader = new PdfReader(nombre);
            int n = reader.getNumberOfPages();

            // Create a stamper that will copy the document to a new file
            PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(
                    file.getAbsolutePath() + ".tmp"));
            int i = 1;
            PdfContentByte under;
            PdfContentByte over;

            BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA,
                    BaseFont.WINANSI, BaseFont.EMBEDDED);

            while (i <= n) {
                // Watermark under the existing page
                under = stamp.getUnderContent(i);
                
                // Text over the existing page
                over = stamp.getOverContent(i);           
                over.beginText();
                over.setFontAndSize(bf, 56);
                over.setColorFill(color);
                // under.showText("Borrador ");
                over.showTextAligned(1, texto, 600, 400, 45);
                over.endText();
                                
                i++;
            }

            stamp.close();
            File borrador = new File(file.getAbsolutePath() + ".tmp");
            borrador.renameTo(file);
            return new File(nombre);
        } catch (Exception de) {

            return file;
        }

    }
	
	public static String devuelveFechaEnLetras(Date fechaParametro) {
		Calendar fecha = Calendar.getInstance();
		fecha.setTime(fechaParametro);
		return  fecha.get(Calendar.DAY_OF_MONTH) + " de "
				+ devuelveMes(fecha.get(Calendar.MONTH)) + " de " + fecha.get(Calendar.YEAR);
	}
	/**
	 * listToString Retorna un String con los valores separados por ";"
	 * @param lista
	 * @return
	 */
	public static String listToString(List<String> lista)
	{
		if(lista!=null&&!lista.isEmpty())
		{
			String ret=lista.toString();		
			//ret=ret.replace(", ", ";");
			ret=ret.replace("[", "");
			ret=ret.replace("]", "");
			return ret;
		}
		return null;
	}
	public static Timestamp fechaHoraActual() {
    	Calendar cal = Calendar.getInstance();
    	Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
    	
    	return timestamp;
    }
	
	public static String devuelveFechaConFormato(Date fecha, String formato) {
        String fechaString = "";
        try {
            Format formatter = new SimpleDateFormat(formato);
            fechaString = formatter.format(fecha);
        } catch (Exception ex) {
        	System.out.println("Error al cambiar el formato a fecha formato :: {0} Mensaje: "+ex.getMessage());
        }
        
        return fechaString;
    }
	/**
	 * Recibe nombre de usuario y retorna las siglas
	 * @param nombreUsuario
	 * @return
	 */
	public static String getSiglas(String nombreUsuario) {
		String siglas="";
		StringTokenizer st = new StringTokenizer(nombreUsuario);
        while (st.hasMoreTokens()) {
        	siglas = siglas + st.nextToken().charAt(0);
        }
		return siglas;
	}
}
