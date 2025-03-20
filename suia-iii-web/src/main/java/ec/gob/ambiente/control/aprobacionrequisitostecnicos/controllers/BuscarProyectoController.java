/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.control.aprobacionrequisitostecnicos.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean.AprobacionRequisitosTecnicosBean;
import ec.gob.ambiente.prevencion.registro.proyecto.bean.VerProyectoBean;
import ec.gob.ambiente.services.MaeLicenseResponse;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.AprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.BuscarProyectoFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.dto.ProyectoCustom;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.tramiteresolver.AprobacionRequisitosTecnicosTramiteResolver;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.ResourcesUtil;

/**
 * <b> Controlador de la pagina buscar proyecto. </b>
 *
 * @author vero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: vero $, $Date: 10/06/2015 $]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class BuscarProyectoController {

	private static final Logger LOG = Logger.getLogger(BuscarProyectoController.class);

	@EJB
	private AprobacionRequisitosTecnicosFacade aprobacionRequisitosTecnicosFacade;;

	@EJB
	private BuscarProyectoFacade buscarProyectoFacade;
	
	@EJB
	private DocumentosFacade documentosFacade;
	
	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoFacade;
	
	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{verProyectoBean}")
	private VerProyectoBean verProyectoBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{aprobacionRequisitosTecnicosBean}")
	private AprobacionRequisitosTecnicosBean aprobacionRequisitosTecnicosBean;
	
	@Setter
	@Getter
	private MaeLicenseResponse proyectoFinal;
	
	@Getter
	@Setter
	private UbicacionesGeografica provincia, canton, ubicacion;

	@Setter
	@Getter
	private List<UbicacionesGeografica> provincias, cantones;
	
	@Setter
	@Getter
	private ProyectoCustom proyecto;

	@Setter
	@Getter
	private String numeroPermisoAmbiental;
	
	@Getter
	@Setter
	private String mensaje;

	@Getter
	@Setter
	private String pathImagen;

	@Setter
	@Getter
	private String proyectoConLicenciaFisica;

	@Getter
	@Setter
	private String nombreProyecto;
	
	@Setter
	@Getter
	private String codigoTramite;

	@Setter
	@Getter
	private Boolean habilitarPanelLicenciaFisica;

	@Setter
	@Getter
	private boolean habilitarInicioProceso;

	@Setter
	@Getter
	private boolean habilitaPanelProyectoEncontrado;

	@Setter
	@Getter
	private boolean proyectoBuscado;

	@Setter
	@Getter
	private boolean habilitarPanelBusqueda;

	@Setter
	@Getter
	private boolean habilitarPanelProyectoNoEncontrado;

	@Setter
	@Getter
	private boolean volverBuscarProyecto;
	
	@Setter
	@Getter
	private Date fechaRegistro;
	
	@Setter
	@Getter
	private Documento documentoLicencia;

	@Setter
	@Getter
	private UploadedFile file;

	private static final String MENSAJE_APROBACION_REQUISITOS_TECNICOS = "mensaje.aprobacion.requisitos.tecnicos";
	private static final String IMAGEN_INICIO_PROCESO_ART = "/resources/images/mensajes/inicio_proceso_art.png";

	@PostConstruct
	public void init() {
		habilitarInicioProceso = false;
		provincias = ubicacionGeograficaFacade.getProvincias();
		documentoLicencia = new Documento();
		habilitarPanelBusqueda = true;
		habilitarPanelLicenciaFisica = null;
	}
	
	public void validarCodigo() throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	    habilitarInicioProceso = false;
	    habilitaPanelProyectoEncontrado = false;
	    ProyectoCustom proyectoEncontrado = null;

	    ProyectoCustom proyectoRcoa = proyectoLicenciamientoFacade.buscaProyectoRCOA(codigoTramite);
	    if (proyectoEncontrado == null && proyectoRcoa != null) {
		    if (!proyectoRcoa.getCedulaProponente().equals(loginBean.getUsuario().getNombre())) {
		        JsfUtil.addMessageError("El proyecto no le pertenece al operador");
		        return;
		    }
		    if (!"true".equals(proyectoRcoa.getEstado()) || !"true".equals(proyectoRcoa.getFinalizado())) {
		        JsfUtil.addMessageError("El proyecto aún no ha finalizado o se encuentra archivado");
		        return;
		    }
		    if ("1".equals(proyectoRcoa.getCategoria()) || "2".equals(proyectoRcoa.getCategoria())) {
		        JsfUtil.addMessageError("El módulo de ART solo está habilitado para Licencias Ambientales");
		        return;
		    }
		    proyectoEncontrado = proyectoRcoa;
	    }

	    ProyectoCustom proyectoLicencia = proyectoLicenciamientoFacade.buscaProyectoLicencia(codigoTramite);
	    if (proyectoEncontrado == null && proyectoLicencia != null) {
		    if (!proyectoLicencia.getCedulaProponente().equals(loginBean.getUsuario().getNombre())) {
		        JsfUtil.addMessageError("El proyecto no le pertenece al operador");
		        return;
		    }
		    if (!"true".equals(proyectoLicencia.getEstado()) || !"true".equals(proyectoLicencia.getFinalizado())) {
		        JsfUtil.addMessageError("El proyecto aún no ha finalizado o se encuentra archivado");
		        return;
		    }
		    if ("1".equals(proyectoLicencia.getCategoria()) || "2".equals(proyectoLicencia.getCategoria())) {
		        JsfUtil.addMessageError("El módulo de ART solo está habilitado para Licencias Ambientales");
		        return;
		    }
		    proyectoEncontrado = proyectoLicencia;
	    }

	    ProyectoCustom proyectoDigital = proyectoLicenciamientoFacade.buscaProyectoDigital(codigoTramite);
	    if (proyectoEncontrado == null && proyectoDigital != null) {
		    if (!proyectoDigital.getCedulaProponente().equals(loginBean.getUsuario().getNombre())) {
		        JsfUtil.addMessageError("El proyecto no le pertenece al operador");
		        return;
		    }
		    if (!"true".equals(proyectoDigital.getEstado()) || !"true".equals(proyectoDigital.getFinalizado())) {
		        JsfUtil.addMessageError("El proyecto aún no ha finalizado o se encuentra archivado");
		        return;
		    }
		    if ("1".equals(proyectoDigital.getCategoria()) || "2".equals(proyectoDigital.getCategoria())) {
		        JsfUtil.addMessageError("El módulo de ART solo está habilitado para Licencias Ambientales");
		        return;
		    }
		    proyectoEncontrado = proyectoDigital;
	    }

	    if (proyectoEncontrado != null) {
	        proyecto = proyectoEncontrado;
	        fechaRegistro = dateFormat.parse(proyecto.getRegistro());
	        habilitaPanelProyectoEncontrado = true;
	        habilitarInicioProceso = true;
	        JsfUtil.addMessageInfo("Proyecto encontrado: " + proyectoEncontrado.getNombre());
	    } else {
	        JsfUtil.addMessageError("No se encontró ningún proyecto con el código proporcionado");
	    }
	}

	
	public MaeLicenseResponse convertirAProyectoFinal(ProyectoCustom proyecto) throws ServiceException {
		Integer geloId = Integer.valueOf(proyecto.getUbicacion());
		ubicacion = ubicacionGeograficaFacade.buscarPorId(geloId);
	    MaeLicenseResponse proyectoFinal = new MaeLicenseResponse();
	    proyectoFinal.setCodigoProyecto(codigoTramite);
	    proyectoFinal.setNombreProyecto(proyecto.getNombre());
	    proyectoFinal.setFechaProyecto(proyecto.getRegistro());
	    proyectoFinal.setCodigoInecProvincia(ubicacion.getCodificacionInec());
	    	    
	    return proyectoFinal;
	}
	
	public void iniciarProceso() {
		try {
			proyectoFinal = new MaeLicenseResponse();
			proyectoFinal = convertirAProyectoFinal(proyecto);
			proyectoFinal.setLicencia(false);
			
			aprobacionRequisitosTecnicosFacade.iniciarProceso(loginBean.getUsuario(), proyectoFinal, 
					loginBean.getUsuario().getNombre(), AprobacionRequisitosTecnicosTramiteResolver.class, 
					Boolean.valueOf(proyectoConLicenciaFisica), documentoLicencia, fechaRegistro);
			
			setMensaje(ResourcesUtil.getMessageResourceString(MENSAJE_APROBACION_REQUISITOS_TECNICOS));
			setPathImagen(IMAGEN_INICIO_PROCESO_ART);
			RequestContext.getCurrentInstance().execute("PF('continuarDialog').show();");
		} catch (ServiceException e) {
			LOG.error(e);
			JsfUtil.addMessageError("Ocurrió un error al iniciar el proceso aprobación requisitos técnicos.");
		}
	}

	public void continuar() {
		JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
	}

	public void listenerLicenciaFisica() {
		habilitarPanelLicenciaFisica = new Boolean(proyectoConLicenciaFisica);
		if (habilitarPanelLicenciaFisica) {
			habilitarInicioProceso = true;
		}
	}

	public void handleFileUpload(FileUploadEvent event) {
		file = event.getFile();
		setDocumentoLicencia(UtilDocumento.generateDocumentPDFFromUpload(file.getContents(), file.getFileName()));
	}

	public void volverPanelBuscar() {
		habilitarPanelBusqueda = true;
		proyectoBuscado = false;
		habilitaPanelProyectoEncontrado = false;
		habilitarPanelLicenciaFisica = null;
		habilitarInicioProceso = false;
		proyectoConLicenciaFisica = null;
		numeroPermisoAmbiental = null;
		fechaRegistro = null;
		volverBuscarProyecto = false;
	}

	public void aceptarPreguntaLicenciaFisica() {
		this.numeroPermisoAmbiental = "";
		this.fechaRegistro = null;
		volverBuscarProyecto = (!(new Boolean(proyectoConLicenciaFisica)));
		habilitarInicioProceso = false;
		habilitaPanelProyectoEncontrado = false;
		habilitarPanelBusqueda = false;
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('preguntaLicenciaFisicaWdgt').hide();");
	}
	
	public void seleccionarProvincia() {
		cantones = new ArrayList<UbicacionesGeografica>();
		canton = null;

		if (provincia != null) {
			cantones = ubicacionGeograficaFacade.getUbicacionPorPadre(provincia);
		}
	}
}
