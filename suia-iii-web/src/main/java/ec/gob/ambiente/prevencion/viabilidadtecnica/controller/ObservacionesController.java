/*
 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.prevencion.viabilidadtecnica.controller;

import ec.gob.ambiente.prevencion.viabilidadtecnica.bean.ObservacionesBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.DocumentosTareasProceso;
import ec.gob.ambiente.suia.domain.ObservacionesFormularios;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.observaciones.facade.ObservacionesFacade;
import ec.gob.ambiente.suia.reasignacion.controllers.AsignacionMasivaController;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author christian
 */
@RequestScoped
@ManagedBean(name = "observacionesVTController")
public class ObservacionesController implements Serializable {

	private static final long serialVersionUID = -8187506297503594856L;
	private static final Logger LOG = Logger
			.getLogger(AsignacionMasivaController.class);
	@EJB
	private ObservacionesFacade observacionesFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{observacionesBean}")
	private ObservacionesBean observaciones;

	@Getter
	@Setter
	private Map<String, ObservacionesBean> listaObservacionesBB;

	@Getter
	@Setter
	private boolean adicionado;

	@Getter
	@Setter
	private File archivo;
	
	@Setter
	@Getter	
	private Documento anexo;

	@EJB
	private ObservacionesFacade obsevacionesFacade;

	@EJB
	private DocumentosFacade documentosFacade;
	
	@PostConstruct
	public void init() {
		System.out.println("||||||||||||||||||||||||||||sxxxxdsdfsersdfsadf");
		cargarDatos();
	}

	private void cargarDatos() {
		listaObservacionesBB = new HashMap<>();
	}

	/**
	 * @param idFormulario
	 * @return
	 */
	public String obtenerIdSeccion(String idFormulario) {
		ObservacionesBean observacion = listaObservacionesBB.get(idFormulario);
		String sec = "";
		for (String s : observacion.getSeccion()) {
			sec += "-" + s;
		}
		sec = sec.replace("/", "_");
		sec = sec.replace(" ", "_");
		sec = sec.replace(".", "_");
		sec = sec.replace("*", "full");
		return "pnlObservaciones-" + observacion.getNombreClase()
				+ observacion.getIdClase().toString() + sec;
	}

	/**
	 * @param idFormulario
	 * @param nombreClase
	 * @param idClase
	 * @param secciones
	 */
	public String cargarDatosIniciales(String idFormulario, String nombreClase,
			String idClase, String secciones) {
		if (!adicionado) {
			cargarObservaciones(idFormulario, nombreClase,
					Integer.parseInt(idClase), secciones);
		}
		return "label-" + obtenerIdSeccion(idFormulario);
	}

	/**
	 * @param idFormulario
	 * @param nombreClase
	 * @param idClase
	 * @param secciones
	 */
	public void cargarObservaciones(String idFormulario, String nombreClase,
			Integer idClase, String... secciones) {
		try {
			ObservacionesBean observacion = new ObservacionesBean();
			observacion.setNombreClase(nombreClase);
			observacion.setIdClase(idClase);
			observacion.setSeccion(secciones);

			observacion.setListaObservaciones(observacionesFacade
					.listarPorIdClaseNombreClase(idClase, nombreClase,
							secciones));
			if (observacion.getListaObservaciones() != null
					&& !observacion.getListaObservaciones().isEmpty()) {
				for (ObservacionesFormularios ob : observacion
						.getListaObservaciones()) {
					if (ob.getIdUsuario().equals(
							JsfUtil.getLoggedUser().getId())) {
						ob.setDisabled(false);
					}
				}
			}
			for (ObservacionesFormularios ob : observacion
					.getListaObservaciones()) {
				List<ObservacionesFormularios> elementos = new ArrayList<>();
				String seccion = ob.getSeccionFormulario();
				if (observacion.getMapaSecciones().containsKey(seccion)) {
					elementos = observacion.getMapaSecciones().get(
							ob.getSeccionFormulario());
				}
				elementos.add(ob);
				observacion.getMapaSecciones().put(seccion, elementos);
			}
			for (String seccion : secciones) {
				if (!observacion.getMapaSecciones().containsKey(seccion)
						&& !seccion.equals("*")) {
					observacion.getMapaSecciones().put(seccion,
							new ArrayList<ObservacionesFormularios>());
				}
			}
			for (String s : observacion.getMapaSecciones().keySet()) {
				reasignarIndice(observacion, s);
			}

			listaObservacionesBB.put(idFormulario, observacion);
		} catch (ServiceException e) {
			LOG.error(e, e);
		} catch (RuntimeException e) {
			LOG.error(e, e);
		}
	}

	/**
	 * @param observacionesBB
	 * @param seccion
	 */
	public void reasignarIndice(ObservacionesBean observacionesBB,
			String seccion) {
		if (observacionesBB.getMapaSecciones() != null
				&& !observacionesBB.getMapaSecciones().isEmpty()) {
			if (observacionesBB.getMapaSecciones().get(seccion) != null
					&& !observacionesBB.getMapaSecciones().get(seccion)
							.isEmpty()) {
				int i = 0;
				for (ObservacionesFormularios ob : observacionesBB
						.getMapaSecciones().get(seccion)) {
					ob.setIndice(i);
					i++;
				}
			}
		}

	}

	/**
	 * @param idFormulario
	 * @param idClase
	 * @param nombreClase
	 * @param seccion
	 */
	public void agregarObservacion(String idFormulario, String idClase,
			String nombreClase, String seccion) {
		ObservacionesFormularios ob = new ObservacionesFormularios();
		ob.setCampo("");
		ob.setDescripcion("");
		ob.setIdClase(new Integer(idClase));
		ob.setNombreClase(nombreClase);
		ob.setSeccionFormulario(seccion);
		ob.setUsuario(JsfUtil.getLoggedUser());
		ob.setFechaRegistro(new Date());
		ob.setDisabled(false);
		if (!listaObservacionesBB.get(idFormulario).getMapaSecciones()
				.containsKey(seccion)) {
			listaObservacionesBB.get(idFormulario).getMapaSecciones()
					.put(seccion, new ArrayList<ObservacionesFormularios>());
		}

		listaObservacionesBB.get(idFormulario).getMapaSecciones().get(seccion)
				.add(ob);
		listaObservacionesBB.get(idFormulario).getListaObservaciones().add(ob);
		reasignarIndice(listaObservacionesBB.get(idFormulario), seccion);
		adicionado = true;
	}

	/**
	 * @param idFormulario
	 * @param seccion
	 */
	public void guardar(String idFormulario, String seccion) {
		try {
			observacionesFacade.guardar(listaObservacionesBB.get(idFormulario)
					.getMapaSecciones().get(seccion));
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
		} catch (ServiceException e) {
			JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + " "
					+ e.getMessage());
			LOG.error(e, e);
		} catch (RuntimeException e) {
			JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + " "
					+ e.getMessage());
			LOG.error(e, e);
		}

		adicionado = false;
	}

	public void eliminar(String idFormulario, String seccion,
			ObservacionesFormularios observacionesFormularios) {
		ObservacionesFormularios obj = listaObservacionesBB.get(idFormulario)
				.getMapaSecciones().get(seccion)
				.get(observacionesFormularios.getIndice());
		if (obj.getId() != null) {
			obj.setEstado(false);
			guardar(idFormulario, seccion);
		}
		listaObservacionesBB.get(idFormulario).getMapaSecciones().get(seccion)
				.remove(observacionesFormularios);
		reasignarIndice(listaObservacionesBB.get(idFormulario), seccion);

	}

	public ObservacionesBean getObservacionesBB() {
		return getObservacionesBB("id");
	}

	public ObservacionesBean getObservacionesBB(String idFormulario) {
		if (listaObservacionesBB.containsKey(idFormulario)) {
			return listaObservacionesBB.get(idFormulario);
		}
		return new ObservacionesBean();
	}

	public void guardarObservaciones() {
		observaciones.getObservacionesformulario().getCampo();
		observaciones.getObservacionesformulario().setNombreClase(
				"observaciones");
		observaciones.getObservacionesformulario().setIdClase(1);
		observaciones.getObservacionesformulario().setUsuario(
				JsfUtil.getLoggedUser());
		observaciones.getObservacionesformulario().setSeccionFormulario("dos");
		observaciones.getObservacionesformulario().setFechaRegistro(new Date());
		try {
			System.out.println("obsercaciones************************"
					+ observaciones.getObservacionesformulario()
							.getDescripcion());
			observacionesFacade.guardar(observaciones
					.getObservacionesformulario());
			observaciones
					.setObservacionesformulario(new ObservacionesFormularios());
			;
			System.out
					.println("se seteraron los valores************************"
							+ observaciones.getObservacionesformulario()
									.getDescripcion());
			observaciones
					.setListaObservaciones(new ArrayList<ObservacionesFormularios>());
			observaciones.setListaObservaciones(obsevacionesFacade
					.listarPorIdClaseNombreClase(1, "observaciones"));
			JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
		} catch (ServiceException e) {
			JsfUtil.addMessageInfo(JsfUtil.ERROR_GUARDAR_REGISTRO);
		}
	}

	public void editarObservaciones() {
		observaciones.getObservacionesformulario().getCampo();
		observaciones.getObservacionesformulario().getDescripcion();
		System.out.println("editar observaciones "
				+ observaciones.getObservacionesformulario().getCampo());
		try {
			observacionesFacade.guardar(observaciones
					.getObservacionesformulario());
			observaciones.setObservacionesformulario(new ObservacionesFormularios());
			observaciones.setListaObservaciones(obsevacionesFacade
					.listarPorIdClaseNombreClase(1, "observaciones"));
			JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
		} catch (Exception e) {
			JsfUtil.addMessageInfo(JsfUtil.ERROR_ACTUALIZAR_REGISTRO);
		}
	}

	public void eliminarObservacion(ObservacionesFormularios obj) {
		obj.setEstado(false);
		try {
			observacionesFacade.guardar(obj);
			observaciones.setListaObservaciones(obsevacionesFacade
					.listarPorIdClaseNombreClase(1, "observaciones"));
			JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
		} catch (Exception e) {
			JsfUtil.addMessageInfo(JsfUtil.ERROR_ACTUALIZAR_REGISTRO);
		}

	}

	public void requiereInspeccion() {
		observaciones.getRequiereInspeccion();
		System.out.println("requiere inspeccion:  "
				+ observaciones.getRequiereInspeccion());
	}

	public void adjuntarArchivo(FileUploadEvent event) {
		try {
			if (event != null) {
				archivo = JsfUtil.upload(event);
				anexo = ingresarImagenAdjunta(archivo,TipoDocumentoSistema.VIAB_TECN_ANEX_INSPECCION_OBSERVACIONES);
				//puntoMonitoreo.setDocumento(anexoFotograficoDoc);
				JsfUtil.addMessageInfo("El archivo "
						+ event.getFile().getFileName()
						+ " fue adjuntado correctamente.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/* public Documento ingresarImagenAdjunta(File file) throws Exception 
	    {
		 	
		 	MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
	    	byte[] data = java.nio.file.Files.readAllBytes(Paths.get(file.getAbsolutePath()));
	    	Documento documento1 = new Documento();
	    	///+++++CAMBIAR TODAS ESTAS VARIABLES POR LAS CORRECTAAS
	    	//SE COLOCAN ESTOS SETEOS CON VALORES FICTICIONS SOLO POR EJEMPLO
	    	documento1.setIdTable(1);
	    	String ext = getExtension(file.getAbsolutePath());
	    	documento1.setNombre(file.getName());
	    	documento1.setExtesion(ext);
	    	documento1.setNombreTabla(ObservacionesFormularios.class.getSimpleName());
	    	documento1.setMime(mimeTypesMap.getContentType(file));
	    	documento1.setContenidoDocumento(data);
	    	DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
	    	documentoTarea.setIdTarea(1);//cambiar
	    	documentoTarea.setProcessInstanceId(2);//cambiar
	    	Long variable=(long) 1;
	    	return documentosFacade.guardarDocumentoAlfresco("1","PlanMonitoreo", variable, documento1, TipoDocumentoSistema.VIAB_TECN_ANEX_INSPECCION_OBSERVACIONES,documentoTarea);
	    	
	    }*/
	 
	    private String getExtension(String fullPath)
	    {
	        String extension = "";
	        int i = fullPath.lastIndexOf('.');
	        if (i > 0) 
	        {
	            extension = fullPath.substring(i + 1);
	        }
	        return extension;
	    }
	    
	    
	    public Documento ingresarImagenAdjunta(File file,TipoDocumentoSistema tipoDocumento) throws Exception 
	     {
	    MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
	      byte[] data = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
	      Documento documento1 = new Documento();
//	      documento1.setIdTable(proyectoLicenciamientoAmbiental.getId());
	      documento1.setIdTable(1234);      
	      String ext = getExtension(file.getAbsolutePath());
	      documento1.setNombre(file.getName());
	      documento1.setExtesion(ext);
	      documento1.setNombreTabla(ObservacionesFormularios.class.getSimpleName());
	      documento1.setMime(mimeTypesMap.getContentType(file));
	      documento1.setContenidoDocumento(data);
	      DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
//	      documentoTarea.setIdTarea(bandejaTareasBean.getTarea().getTaskId());
	      documentoTarea.setIdTarea(1234);
//	      documentoTarea.setProcessInstanceId(bandejaTareasBean.getProcessId());
	      documentoTarea.setProcessInstanceId(1234);
//	      return documentosFacade.guardarDocumentoAlfresco(proyectoLicenciamientoAmbiental.getCodigo(),"Viabilidad_Tecnica", bandejaTareasBean.getProcessId(), documento1, tipoDocumento,documentoTarea);
	      return documentosFacade.guardarDocumentoAlfresco("MAE-SOL-SUBT-2015-000001","Viabilidad_Tecnica", new Long(1234), documento1, tipoDocumento,documentoTarea);  
	     }
	    
	    

}
