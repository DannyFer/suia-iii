package ec.gob.ambiente.rcoa.sustancias.quimicas.controllers;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.rcoa.enums.TipoInformeOficioEnum;
import ec.gob.ambiente.rcoa.facade.GestionarProductosQuimicosProyectoAmbientalFacade;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.model.GestionarProductosQuimicosProyectoAmbiental;
import ec.gob.ambiente.rcoa.sustancias.quimicas.bean.MesBean;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.ActividadSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.DeclaracionSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.DetalleSolicitudImportacionRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.DocumentosRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.InformesOficiosRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.PermisoDeclaracionRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.RegistroSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.SolicitudImportacionRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.ActividadSustancia;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.CaracteristicaActividad;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DeclaracionSustanciaQuimica;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DetalleSolicitudImportacionRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DocumentosSustanciasQuimicasRcoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.InformeOficioRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.PermisoDeclaracionRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.RegistroSustanciaQuimica;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.SolicitudImportacionRSQ;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.SustanciaQuimicaPeligrosa;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class EmpresasActivasRSQController {
	
	private static final Logger LOG = Logger.getLogger(EmpresasActivasRSQController.class);
	
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private RegistroSustanciaQuimicaFacade registroSustanciaQuimicaFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;	
	@EJB
	private ActividadSustanciaQuimicaFacade actividadSustanciaQuimicaFacade;
	@EJB
	private GestionarProductosQuimicosProyectoAmbientalFacade gestionarProductosQuimicosProyectoAmbientalFacade;	
	@EJB
	private DocumentosRSQFacade documentosFacade;
	@EJB
	private InformesOficiosRSQFacade informesOficiosRSQFacade;
	@EJB
	private PermisoDeclaracionRSQFacade permisoDeclaracionRSQFacade;
	@EJB
	private DeclaracionSustanciaQuimicaFacade declaracionSustanciaQuimicaFacade;
	@EJB
	private SolicitudImportacionRSQFacade solicitudImportacionRSQFacade;
	@EJB
	private DetalleSolicitudImportacionRSQFacade detalleSolicitudImportacionRSQFacade;
	
	
	@Getter
	@Setter
	private List<RegistroSustanciaQuimica> listaRegistros;
	
	@Getter
	@Setter
	private RegistroSustanciaQuimica registro;
	
	@Getter
	@Setter
	private Boolean tieneRepresentante = false;
	
	@Getter
	@Setter
	private String nombreOperador, nombreRepresentante;
	
	@Getter
	@Setter
	private List<SustanciaQuimicaPeligrosa> listaSustancias;
	
	@Getter
	@Setter
	private List<SustanciaQuimicaPeligrosa> sustancias;
	
	@Getter
	@Setter
	private List<PermisoDeclaracionRSQ> listaSustanciasQuimicasPermiso;
	
	@Getter
	@Setter
	private List<MesBean> listaMeses;
	
	@Getter
	@Setter
	private List<String> anioList;
	
	@Getter
	@Setter
	private List<DocumentosSustanciasQuimicasRcoa> listaDocumentos;
	
	@Getter
	@Setter
	private boolean esModificacion = false;
	
	@Getter
	@Setter
	private boolean mostrarValidar = true;	
	
	private String mesLista[] = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
	
	@Getter
	@Setter
	private ActividadSustancia actividadSustancia;	
		
	@Getter
	@Setter
	private PermisoDeclaracionRSQ permisoDeclaracionRSQ, permisoDeclaracionRSQAux;
	
	@Getter
	@Setter
	private List<PermisoDeclaracionRSQ> listaPermisosEliminar, listaPermisosAux;
	
	@Getter
	@Setter
	private Boolean modificacionSus = false;
	
	@Getter
	@Setter
	private SustanciaQuimicaPeligrosa sustanciaPermiso;
	
	@Getter
	@Setter
	private Double cupoP;
	
	@Getter
	@Setter
	private Double stockP;
	
	@Getter
	@Setter
	private Integer mesP, anioP;
	
	@Getter
	@Setter
	private Boolean importacion, declaracion, guia;
	
	@Getter
	@Setter
	private Boolean deshabilitarFecha = false;
	
	@Getter
	@Setter
	private Double totalCupo;               //valorAmpliacion, 
	
	@Getter
	@Setter
	private DocumentosSustanciasQuimicasRcoa documentoRespaldoAmpliacion;
	
	@Getter
	@Setter
	private Boolean guardarAmpliacion = false;
	
	@Getter
	@Setter
	private PermisoDeclaracionRSQ permisoAnterior;
	
	@Getter
	@Setter
	private Date fechaInicial;
	
	@PostConstruct
	public void init(){
		try {
			
			listaRegistros = new ArrayList<>();
			
			listaRegistros = registroSustanciaQuimicaFacade.obtenerRegistrosCompletos();			
			
			listaSustancias = registroSustanciaQuimicaFacade.listaSustanciasQuimicas();
			
			listaSustanciasQuimicasPermiso = new ArrayList<PermisoDeclaracionRSQ>();
			
			listaMeses = new ArrayList<MesBean>();
			
			int j = 1;
			for(String mes : mesLista){
				MesBean mesAdd = new MesBean();				
				mesAdd.setId(j++);
				mesAdd.setMes(mes);
				listaMeses.add(mesAdd);
			}
			/**
			 * cambio para que se tome el anio una vez se cambie la fecha de 
			 */
			anioList = new ArrayList<String>();
			int anioActual=JsfUtil.getCurrentYear();	
			anioList.add(String.valueOf(anioActual-1));
			anioList.add(String.valueOf(anioActual));
			anioList.add(String.valueOf(anioActual+1));
			
						
			Calendar fechaInicialCalendarioC = Calendar.getInstance();
			fechaInicialCalendarioC.set(Calendar.DATE, 1);
			fechaInicialCalendarioC.set(Calendar.MONTH, 0);
			
			fechaInicial = fechaInicialCalendarioC.getTime();
						
			iniciarInformacion();
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e);
		}		
	}	
	
	public void iniciarInformacion(){
			
		listaDocumentos = new ArrayList<DocumentosSustanciasQuimicasRcoa>();		
		registro = new RegistroSustanciaQuimica();
		permisoDeclaracionRSQ = new PermisoDeclaracionRSQ();	
		listaSustanciasQuimicasPermiso = new ArrayList<PermisoDeclaracionRSQ>();
		listaPermisosEliminar = new ArrayList<PermisoDeclaracionRSQ>();
		listaPermisosAux = new ArrayList<PermisoDeclaracionRSQ>();
		permisoDeclaracionRSQAux = new PermisoDeclaracionRSQ();
		
		tieneRepresentante = false;
		nombreOperador = "";
		esModificacion = false;
		mostrarValidar = true;			
		deshabilitarFecha = false;
		
		RequestContext.getCurrentInstance().update("formEmpresa:dlgEmpresa");	
	}
	
	public boolean busquedaUsuario(String usuario){	
		try {
			tieneRepresentante = false;
			Usuario operador = usuarioFacade.buscarUsuario(usuario);
			if(operador != null){
				
				if(usuario != null && usuario.length() == 13){				
					Organizacion org = organizacionFacade.buscarPorRuc(usuario);
					if(org != null){		
						nombreOperador = org.getNombre();
						tieneRepresentante = true;
						registro.setNombreRepLegal(org.getPersona().getNombre());
						registro.setIdentificacionRepLegal(org.getPersona().getPin());
						registro.setUsuario(operador);
						return true;
					}else{						
						if(operador.getPersona() != null){
							nombreOperador = operador.getPersona().getNombre();
							registro.setNombreRepLegal(nombreOperador);
							registro.setIdentificacionRepLegal(operador.getNombre());
							registro.setUsuario(operador);
							return true;
						}else{
							JsfUtil.addMessageError("El usuario no tiene un nombre asociado al usuario");
							return false;
						}
					}				
				}else{
					nombreOperador = operador.getPersona().getNombre();
					registro.setNombreRepLegal(nombreOperador);
					registro.setIdentificacionRepLegal(operador.getNombre());	
					registro.setUsuario(operador);
					return true;
				}				
			}else{
				JsfUtil.addMessageError("El usuario no se encuentra registrado en la base de datos de Regularización y Control Ambiental.");
				return false;
			}					
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e);
			return false;
		}
	}
			
	public void nuevoRegistroSustancia(boolean mensaje){
				
		if(!mensaje){
			if(permisoDeclaracionRSQ.getId() != null){
				int id = permisoDeclaracionRSQ.getId();				
				permisoDeclaracionRSQ.setId(id);
			}
			
			setPermisoDeclaracionRSQ(permisoDeclaracionRSQAux);
			
			sustanciaPermiso = permisoDeclaracionRSQ.getSustanciaQuimica();
			cupoP = permisoDeclaracionRSQ.getCupo();		
			mesP = permisoDeclaracionRSQ.getMes();
			stockP = permisoDeclaracionRSQ.getStock();
			importacion = permisoDeclaracionRSQ.getLicenciaImportacion();
			declaracion = permisoDeclaracionRSQ.getDeclaracionSustancias();
			guia = permisoDeclaracionRSQ.getGuiasRuta();			
			
			modificacionSus = false;
			listaSustancias = registroSustanciaQuimicaFacade.listaSustanciasQuimicas();
			List<SustanciaQuimicaPeligrosa> listaSustanciasAux = new ArrayList<SustanciaQuimicaPeligrosa>();
			listaSustanciasAux.addAll(listaSustancias);
			
			for(PermisoDeclaracionRSQ permiso : listaSustanciasQuimicasPermiso){
				for(SustanciaQuimicaPeligrosa sustancia : listaSustanciasAux){
					if(permiso.getSustanciaQuimica().equals(sustancia)){
						listaSustancias.remove(sustancia);
					}
				}		
				
				if(permiso.getSustanciaQuimica().equals(permisoDeclaracionRSQ.getSustanciaQuimica())){
					permiso.setSustanciaQuimica(sustanciaPermiso);
					permiso.setCupo(cupoP);
					permiso.setStock(stockP);
					permiso.setMes(mesP);
					permiso.setAnio(anioP);
					permiso.setLicenciaImportacion(importacion);
					permiso.setDeclaracionSustancias(declaracion);
					permiso.setGuiasRuta(guia);
				}
			}				
			
			permisoDeclaracionRSQ = new PermisoDeclaracionRSQ();
			permisoDeclaracionRSQAux = new PermisoDeclaracionRSQ();
			
			RequestContext.getCurrentInstance().update("formEmpresa:tblSustancias");
		}else{	
			
			for(PermisoDeclaracionRSQ permiso : listaSustanciasQuimicasPermiso){
				if(permiso.equals(permisoDeclaracionRSQ)){					
					permiso.setSustanciaQuimica(sustanciaPermiso);
					permiso.setCupo(cupoP);
					permiso.setStock(stockP);
					permiso.setMes(mesP);
					permiso.setAnio(anioP);
					permiso.setLicenciaImportacion(importacion);
					permiso.setDeclaracionSustancias(declaracion);
					permiso.setGuiasRuta(guia);
				}
			}
						
			permisoDeclaracionRSQ = new PermisoDeclaracionRSQ();
			
			modificacionSus = false;
			RequestContext.getCurrentInstance().update("formEmpresa:tblSustancias");			
		}		
		validarMes();
	}		
	
	public void seleccionarSustancia(PermisoDeclaracionRSQ sus){
		modificacionSus = true;
		permisoDeclaracionRSQ = new PermisoDeclaracionRSQ();
		permisoDeclaracionRSQAux = new PermisoDeclaracionRSQ();
		listaPermisosAux = new ArrayList<PermisoDeclaracionRSQ>();
		listaPermisosAux.add(sus);
		
		PermisoDeclaracionRSQ permisoClone = (PermisoDeclaracionRSQ)SerializationUtils.clone(sus);
		permisoClone.setId(null);
		
		setPermisoDeclaracionRSQAux(permisoClone);
		setPermisoDeclaracionRSQ(sus);		
		
		PermisoDeclaracionRSQ permisoActual = new PermisoDeclaracionRSQ();
		if(sus.getId() != null){
			permisoActual = permisoDeclaracionRSQFacade.obtenerPermisoPorId(sus.getId());
			if(permisoActual !=null && sus.getAnio() != null && permisoActual.getAnio() < sus.getAnio()){
				permisoDeclaracionRSQ.setStock(sus.getStockActual());
			}else{
				permisoDeclaracionRSQ.setStock(sus.getStock());
			}
		}else{
			List<RegistroSustanciaQuimica> listaRegistros = registroSustanciaQuimicaFacade.obtenerListaRegistroPorCodigo(registro.getNumeroAplicacion());
			
			if(listaRegistros != null && !listaRegistros.isEmpty()){
				for(RegistroSustanciaQuimica rsq : listaRegistros){
					DeclaracionSustanciaQuimica ultimaDeclaracion = declaracionSustanciaQuimicaFacade.obtenerUltimaDeclaracionEnviadaPorRSQ(rsq, sus.getSustanciaQuimica());
					if(ultimaDeclaracion != null && ultimaDeclaracion.getId() != null && ultimaDeclaracion.getCantidadFin() != null){
						permisoDeclaracionRSQ.setStock(ultimaDeclaracion.getCantidadFin());
					}
				}
			}		
		}
		
		sustanciaPermiso = sus.getSustanciaQuimica();
		cupoP = sus.getCupo();		
		mesP = sus.getMes();
		anioP = sus.getAnio();
		stockP = sus.getStock();
		importacion = sus.getLicenciaImportacion();
		declaracion = sus.getDeclaracionSustancias();
		guia = sus.getGuiasRuta();
		validarMes();
	}
	
	public void nuevoRegistro(){
		try {
			iniciarInformacion();		
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void validateInformacionPermiso(FacesContext context, UIComponent validate, Object value) throws RemoteException {
		
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		if(modificacionSus){
			List<PermisoDeclaracionRSQ> listaPermisosAux = new ArrayList<PermisoDeclaracionRSQ>();
			listaPermisosAux.addAll(listaSustanciasQuimicasPermiso);
			listaPermisosAux.remove(permisoDeclaracionRSQ);
			
			for(PermisoDeclaracionRSQ permiso : listaPermisosAux){
				if(permiso.getSustanciaQuimica().equals(permisoDeclaracionRSQ.getSustanciaQuimica())){
					errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "La sustancia química ya se encuentra ingresada.", null));
					break;
				}
			}
		}else{
			for(PermisoDeclaracionRSQ permiso : listaSustanciasQuimicasPermiso){
				if(permiso.getSustanciaQuimica().equals(permisoDeclaracionRSQ.getSustanciaQuimica())){
					errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "La sustancia química ya se encuentra ingresada.", null));
					break;
				}
			}
		}		
		
		if(permisoDeclaracionRSQ.getSustanciaQuimica() == null){
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe seleccionar una sustancia química", null));
		}
		
		if(permisoDeclaracionRSQ.getLicenciaImportacion() != null && permisoDeclaracionRSQ.getLicenciaImportacion()){
			if(permisoDeclaracionRSQ.getCupo() == null || permisoDeclaracionRSQ.getCupo() == 0){
				errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe ingresar un valor para el cupo", null));
			}
		}		
		
		if(permisoDeclaracionRSQ.getMes() == null){
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe seleccionar el primer mes de declaración", null));
		}
		
		if(permisoDeclaracionRSQ.getAnio() == null){
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe seleccionar el año", null));
		}
		
		if(permisoDeclaracionRSQ.getEditarStock() != null && permisoDeclaracionRSQ.getStock() == null && permisoDeclaracionRSQ.getEditarStock()){
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe ingresar un valor para el stock", null));
		}
						
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public void validateInformacionSustancias(FacesContext context, UIComponent validate, Object value) throws RemoteException {
		List<FacesMessage> errorMessages = new ArrayList<>();
		if(listaSustanciasQuimicasPermiso == null || listaSustanciasQuimicasPermiso.isEmpty()){
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe ingresar sustancias químicas", null));
		}
		
		if(registro.getUsuarioCreacion() == null || registro.getUsuarioCreacion().isEmpty()){
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El campo RCU/Cédula es requerido", null));
		}
		
		if(registro.getNumeroAplicacion() == null || registro.getNumeroAplicacion().isEmpty()){
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El campo Codigo RSQ es requerido", null));
		}
		
		if(registro.getUsuario() == null){
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe validar el campo RUC/Cédula", null));
		}	
		
		if(esModificacion){
			List<RegistroSustanciaQuimica> listaRegistrosAux = new ArrayList<RegistroSustanciaQuimica>();
			listaRegistrosAux.addAll(listaRegistros);
			listaRegistrosAux.remove(registro);
					
		}else{
			for(RegistroSustanciaQuimica res : listaRegistros){
				if(res.getNumeroAplicacion().equals(registro.getNumeroAplicacion())){
					errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El código RSQ ingresado ya se encuentra registrado", null));	
					break;
				}
			}	
		}		
		
		for(PermisoDeclaracionRSQ per : listaSustanciasQuimicasPermiso){
			if(per.getMes() == null || per.getAnio() == null){
				errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ingrese la información de las sustancias químicas de la lista", null));
				break;
			}
		}
		
		if(validarModificacionFecha()){
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Se realizó una modificación en la fecha de vigencia, por favor modificar el primer mes de declaración de cada sustancia.", null));			
		}
		
		List<FacesMessage> errorMessagesSus = new ArrayList<>();
		errorMessagesSus.addAll(validarIngresoDeclaracion());
		
		if(!errorMessagesSus.isEmpty()){
			errorMessages.addAll(errorMessagesSus);
		}			
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);		
	}
	
	public void guardarPermisoValidacion(){
		if(validacionPermisoDeclaracion()){
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('mensajeDialog').show();");	
		}else{
			guardarRegistroPermiso();
			RequestContext context = RequestContext.getCurrentInstance();
			context.update(":formEmpresa:tblSustancias");
			RequestContext.getCurrentInstance().update("formEmpresa:tblSustancias");
		}
	}	
	
	public boolean validacionPermisoDeclaracion(){
		
		RegistroSustanciaQuimica registroAux = registroSustanciaQuimicaFacade.obtenerRegistroPorId(registro.getId());	
		
		if(registroAux != null && registroAux.getId() != null){
			
			/**
			 * Se revisa si tiene registrado permisos si es el caso se realiza la validación de las declaraciones 
			 * en caso de que no tenga permisos entonces no se realizará la validación y
			 * no se tomará en cuenta lo del año 2022 ya que no tenía la estructura actual del sistema de declaración.
			 */
			List<PermisoDeclaracionRSQ> listaPermisos = permisoDeclaracionRSQFacade.obtenerPermisosPorRegistro(registroAux);
			
			if(listaPermisos != null && !listaPermisos.isEmpty()){
				Calendar fechaRegistro = Calendar.getInstance();
				fechaRegistro.setTime(registroAux.getVigenciaDesde());
				int anioR = fechaRegistro.get(Calendar.YEAR);	
				
				Calendar fechaActual = Calendar.getInstance();
				int anioA = fechaActual.get(Calendar.YEAR);
				
				List<DeclaracionSustanciaQuimica> listaDeclaraciones = new ArrayList<DeclaracionSustanciaQuimica>();
				if(anioR < permisoDeclaracionRSQ.getAnio()){
					listaDeclaraciones = declaracionSustanciaQuimicaFacade
							.buscarPorSustanciaDeclaracionEnviada(registroAux,
									permisoDeclaracionRSQ.getSustanciaQuimica(),
									anioR, 12);	 
					
				}else if(anioR == permisoDeclaracionRSQ.getAnio()){
					if(anioR < anioA){
						listaDeclaraciones = declaracionSustanciaQuimicaFacade
								.buscarPorSustanciaDeclaracionEnviada(registroAux,
										permisoDeclaracionRSQ.getSustanciaQuimica(),
										anioR, 12);	
					}else if(anioR == anioA){
						return false;
					}
				}
				
				if(listaDeclaraciones.isEmpty() && !permisoDeclaracionRSQ.getDeclaracionSustancias()){
					return true;
				}else{
					return false;
				}
			}else{
				return false;
			}			
		}else{
			return false;
		}		
	}
	
	public void guardarRegistroPermiso(){	
		
		String tipo = "";
		permisoDeclaracionRSQ.setRealizaImportacion(false);
		if(permisoDeclaracionRSQ.getDeclaracionSustancias()){
			tipo += "Declaracion mensual de sustancias ";
		}
		
		if(permisoDeclaracionRSQ.getLicenciaImportacion()){
			tipo += "Licencia de importación ";
			permisoDeclaracionRSQ.setRealizaImportacion(true);
		}
		
		if(permisoDeclaracionRSQ.getGuiasRuta()){
			tipo += "Guías de ruta";
		}
		
		if(permisoDeclaracionRSQ.getStock() == null){
			permisoDeclaracionRSQ.setStock(0.0);
		}
		
		permisoDeclaracionRSQ.setTipoActivacion(tipo);
		if(!modificacionSus){
			listaSustanciasQuimicasPermiso.add(permisoDeclaracionRSQ);
		}
		permisoDeclaracionRSQ = new PermisoDeclaracionRSQ();
		modificacionSus = false;
	}
	
	public void eliminarSustancia(PermisoDeclaracionRSQ permiso){
		if(existeDeclaracionImportacionSustancia(permiso))
			return;
		
		
		if(permiso.getId() != null){
			listaPermisosEliminar.add(permiso);
		}
		
		listaSustanciasQuimicasPermiso.remove(permiso);
	}
	
	public boolean existeDeclaracionImportacionSustancia(PermisoDeclaracionRSQ permiso){
		try {
			
			List<DeclaracionSustanciaQuimica> listaDeclaraciones = new ArrayList<>();
			listaDeclaraciones = declaracionSustanciaQuimicaFacade.obtenerPorRSQSustancia(registro, permiso.getSustanciaQuimica());
			
			List<SolicitudImportacionRSQ> listaImp = solicitudImportacionRSQFacade.listaSolicitudesAutorizadasPorRSQ(registro.getId(), permiso.getSustanciaQuimica().getId());
			
			
			if (listaDeclaraciones != null && !listaDeclaraciones.isEmpty() || (listaImp != null && !listaImp.isEmpty()) ) {				
				JsfUtil.addMessageError("No se puede eliminar el registro de la sustancia química ya que tiene declaraciones o importaciones terminadas o en proceso asociadas");
				return true;
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;		
	}
	
	public void validarCedula(){
		try {
			boolean existeCedula = false;
			
			Usuario usuarioIngresado = new Usuario();
			if(!esModificacion){
				if(registro.getUsuarioCreacion() == null || registro.getUsuarioCreacion().isEmpty()){
					JsfUtil.addMessageError("Debe ingresar un número de Cédula o Ruc");
					return;
				}
				
				usuarioIngresado = usuarioFacade.buscarUsuarioSujetoControl(registro.getUsuarioCreacion());
			}else{
				if(registro.getUsuario() != null){
					usuarioIngresado = usuarioFacade.buscarUsuarioPorId(registro.getUsuario().getId());
				}else{
					usuarioIngresado = usuarioFacade.buscarUsuarioSujetoControl(registro.getUsuarioCreacion());
				}				
			}			
			
			if(usuarioIngresado != null){
				registro.setUsuario(usuarioIngresado);
				if(registro.getUsuario() != null){
					
					Usuario usuario = usuarioFacade.buscarUsuarioPorId(registro.getUsuario().getId());
					existeCedula = busquedaUsuario(usuario.getNombre());
					registro.setUsuarioCreacion(usuario.getNombre());
					
				}else if(!registro.getUsuarioCreacion().isEmpty()){
					
					existeCedula = busquedaUsuario(registro.getUsuarioCreacion());
					
				}else{
					JsfUtil.addMessageError("Debe ingresar un número de Cédula o Ruc");
				}
				
				if(existeCedula && esModificacion){
					mostrarValidar = false;
				}if(!existeCedula && esModificacion){
					mostrarValidar = true;					
				}
			}else{
				JsfUtil.addMessageError("El usuario no se encuentra registrado en la base de datos de Regularización y Control Ambiental, o es personal de MAATE");
				registro.setUsuario(null);
				setNombreOperador("");
				tieneRepresentante = false;
				registro.setNombreRepLegal("");
			}		
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void uploadDocumento(FileUploadEvent event){		
		DocumentosSustanciasQuimicasRcoa documento = new DocumentosSustanciasQuimicasRcoa();
		documento = this.uploadListener(event, DocumentosSustanciasQuimicasRcoa.class, "pdf");
		documento.setFechaCreacion(new Date());
		TipoDocumento tipoDocumento = new TipoDocumento();
		tipoDocumento.setId(TipoDocumentoSistema.RCOA_RSQ_OFICIO_APROBADO.getIdTipoDocumento());
		documento.setTipoDocumento(tipoDocumento);
		documento.setNombreTabla("InformeOficioRSQ");
		listaDocumentos.add(documento);
	}
	
	private DocumentosSustanciasQuimicasRcoa uploadListener(FileUploadEvent event, Class<?> clazz, String extension) {
		byte[] contenidoDocumento = event.getFile().getContents();
		DocumentosSustanciasQuimicasRcoa documento = crearDocumento(contenidoDocumento, clazz, extension);
		documento.setNombre(event.getFile().getFileName());
		return documento;
	}
	
	public DocumentosSustanciasQuimicasRcoa crearDocumento(byte[] contenidoDocumento, Class<?> clazz, String extension) {
		DocumentosSustanciasQuimicasRcoa documento = new DocumentosSustanciasQuimicasRcoa();
		documento.setContenidoDocumento(contenidoDocumento);		
		documento.setExtesion("." + extension);

		documento.setMime("application/pdf");
		return documento;
	}
	
	public void validarMes(){
		try {
			int mesInt = 0;
			listaMeses = new ArrayList<MesBean>();
			anioList = new ArrayList<String>();
			if(registro.getVigenciaDesde() != null){
				Date fechaVigencia = registro.getVigenciaDesde();
				
				Calendar fechaVigenciaC = Calendar.getInstance();
				fechaVigenciaC.setTime(fechaVigencia);
				
				mesInt = fechaVigenciaC.get(Calendar.MONTH);
				int anio = fechaVigenciaC.get(Calendar.YEAR);
				anioList.add(String.valueOf(anio));
				
				permisoDeclaracionRSQ.setAnio(anio);
			}
									
			int j = mesInt + 1;
			while (j >= mesInt && j<=12) {
				MesBean mesAdd = new MesBean();				
				mesAdd.setId(j);
				mesAdd.setMes(JsfUtil.devuelveMes(j - 1));
				listaMeses.add(mesAdd);	
				j++;
			}			
						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void seleccionarRegistro(RegistroSustanciaQuimica item){
		try {
			esModificacion = true;
			registro = new RegistroSustanciaQuimica();
			registro = registroSustanciaQuimicaFacade.obtenerRegistroPorId(item.getId());
					
			registro.setNombreOperador(item.getNombreOperador());
			
			validarCedula();
			
			if(item.getVigenciaDesde() != null){
				deshabilitarFecha = false;
			}else{
				deshabilitarFecha = true;
			}
			/**
			 * Primero vamos a validar si existen los permisos para importanción, declaración y guias
			 */
			listaSustanciasQuimicasPermiso = new ArrayList<>();
			listaSustanciasQuimicasPermiso = permisoDeclaracionRSQFacade.obtenerPermisosPorRegistro(item);
			for(PermisoDeclaracionRSQ permisoIng : listaSustanciasQuimicasPermiso){
				permisoIng.setRealizaImportacion(false);
				String tipo = "";
				if(permisoIng.getDeclaracionSustancias()){
					tipo += "Declaracion mensual de sustancias ";
				}
				
				if(permisoIng.getLicenciaImportacion()){
					tipo += "Licencia de importación ";
					permisoIng.setRealizaImportacion(true);
				}
				
				if(permisoIng.getGuiasRuta()){
					tipo += "Guías de ruta";
				}
				
				if(permisoIng.getStock() == null){
					permisoDeclaracionRSQ.setStock(0.0);
				}
				
				permisoIng.setTipoActivacion(tipo);
			}
			
			if(listaSustanciasQuimicasPermiso.isEmpty()){
				
				if(registro.getProyectoLicenciaCoa() != null){
					/**
					 * Se obtienen las sustancias aprobadas cuando se realizo el registro de la información preliminar
					 */
					
					List<GestionarProductosQuimicosProyectoAmbiental> listaProductosQ = gestionarProductosQuimicosProyectoAmbientalFacade.listaSustanciasQuimicas(registro.getProyectoLicenciaCoa());
					
					if(listaProductosQ != null && !listaProductosQ.isEmpty()){
						for(GestionarProductosQuimicosProyectoAmbiental sus : listaProductosQ){
							PermisoDeclaracionRSQ permisoImp = new PermisoDeclaracionRSQ();
							permisoImp.setSustanciaQuimica(sus.getSustanciaquimica());
							
							List<ActividadSustancia> listaActividadesImp = new ArrayList<ActividadSustancia>();
							
							listaActividadesImp = actividadSustanciaQuimicaFacade.obtenerActividadesSeleccionadasPorProductosQuimicosImportacion(sus);
							if(listaActividadesImp != null){
								permisoImp.setCupo(listaActividadesImp.get(0).getCupo());
							}
														
							listaSustanciasQuimicasPermiso.add(permisoImp);
						}						
					}
				}else{
					List<ActividadSustancia> listaActividades = actividadSustanciaQuimicaFacade.obtenerActividadesPorRSQ(item);
					
					List<SustanciaQuimicaPeligrosa> listaSustancias = new ArrayList<>();
					if(listaActividades != null && !listaActividades.isEmpty()){
						for(ActividadSustancia actividad : listaActividades){
							if(!listaSustancias.contains(actividad.getGestionarProductosQuimicosProyectoAmbiental().getSustanciaquimica())){
								listaSustancias.add(actividad.getGestionarProductosQuimicosProyectoAmbiental().getSustanciaquimica());
							}								
						}
						
						if(!listaSustancias.isEmpty()){
							for(SustanciaQuimicaPeligrosa sus : listaSustancias){
								PermisoDeclaracionRSQ permisoImp = new PermisoDeclaracionRSQ();
								permisoImp.setSustanciaQuimica(sus);
								
								List<ActividadSustancia> listaActividadesImp = new ArrayList<ActividadSustancia>();
								
								listaActividadesImp = actividadSustanciaQuimicaFacade.obtenerActividadesImportacion(item, sus);
								if(listaActividadesImp != null){
									permisoImp.setCupo(listaActividadesImp.get(0).getCupo());
								}
								
								listaSustanciasQuimicasPermiso.add(permisoImp);
							}							
						}
					}
				}
			}
			
			/**
			 * vamos a buscar todos los RSQ aunque estén desactivados para poder obtener todos los documentos asociados al código RSQ
			 */
			
			List<RegistroSustanciaQuimica> listaTodosRSQ = registroSustanciaQuimicaFacade.obtenerTodosRegistrosPorCodigo(item.getNumeroAplicacion());
			listaDocumentos = new ArrayList<DocumentosSustanciasQuimicasRcoa>();
			for(RegistroSustanciaQuimica reg : listaTodosRSQ){
				InformeOficioRSQ informe = informesOficiosRSQFacade.obtenerPorRSQ(reg, TipoInformeOficioEnum.OFICIO_PRONUNCIAMIENTO, 1);
				
				if(informe != null && informe.getId() != null){
					List<DocumentosSustanciasQuimicasRcoa> listaDocumentosAux  = new ArrayList<DocumentosSustanciasQuimicasRcoa>();
					listaDocumentosAux = documentosFacade.obtenerListDocumentoPorTipo(TipoDocumentoSistema.RCOA_RSQ_OFICIO_APROBADO, "InformeOficioRSQ", informe.getId());
					listaDocumentos.addAll(listaDocumentosAux);
				}	
			}												
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e);
		}
	}
	
	
	public boolean validarModificacionFecha(){
		List<String> observaciones = new ArrayList<String>();
		
		Date fechaVigencia = registro.getVigenciaDesde();
		Calendar calendarioV = Calendar.getInstance();
		calendarioV.setTime(fechaVigencia);
		
		int mesV = calendarioV.get(Calendar.MONTH) + 1;
		
		for(PermisoDeclaracionRSQ sus : listaSustanciasQuimicasPermiso){
						
			if(sus.getMes() != null && sus.getMes() < mesV){
				observaciones.add("mod");
			}
		}
		
		if(observaciones.isEmpty()){
			return false;
		}else{
			return true;
		}
		
	}
	
	/**
	 * Se valida si se modificó el mes cuando ya se ha iniciado el proceso de declaración de sustancias químicas.
	 * @return
	 */
	public List<FacesMessage> validarIngresoDeclaracion(){
		List<FacesMessage> errorMessages = new ArrayList<>();
		try {
			
			for(PermisoDeclaracionRSQ permiso : listaSustanciasQuimicasPermiso){
				
				PermisoDeclaracionRSQ permisoAnt = new PermisoDeclaracionRSQ();
				permisoAnt = permisoDeclaracionRSQFacade.obtenerPermisoPorId(permiso.getId());
				
				List<DeclaracionSustanciaQuimica> listaDeclaraciones = new ArrayList<>();
				listaDeclaraciones = declaracionSustanciaQuimicaFacade.obtenerPorRSQSustanciaAnio(registro, permiso.getSustanciaQuimica(), permiso.getAnio());
				
				if (listaDeclaraciones != null && !listaDeclaraciones.isEmpty()) {
					if (permiso.getMes() != permisoAnt.getMes()) {
						errorMessages.add(new FacesMessage(
										FacesMessage.SEVERITY_ERROR,
										"El operador inició el proceso de declaración mensual para la sustancia "
												+ permiso.getSustanciaQuimica().getDescripcion()
												+ ", no es posible modificar la información registrada.",null));
					}
				}				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		return errorMessages;
	}
	
	public void guardarRegistro(){
		try {				
			
			boolean anioMod = false;
			if(!esModificacion){				
				guardar();
			}else{
				
				RegistroSustanciaQuimica registroAux = registroSustanciaQuimicaFacade.obtenerRegistroPorId(registro.getId());				
				
				Calendar fechaRegistro = Calendar.getInstance();
				fechaRegistro.setTime(registroAux.getVigenciaHasta());
				
				Calendar fechaActual = Calendar.getInstance();
								
				int anioR = fechaRegistro.get(Calendar.YEAR);
				int anioA = fechaActual.get(Calendar.YEAR);
				
				if(anioA > anioR){
					anioMod = true;
				}			
				
				if(anioMod){
					registroAux.setEstado(false);
					registroSustanciaQuimicaFacade.guardar(registroAux, JsfUtil.getLoggedUser());
					
					registro.setId(null);
					
					for(PermisoDeclaracionRSQ permiso : listaSustanciasQuimicasPermiso){
						permiso.setId(null);
					}
										
					guardar();
				}else{					
					modificar();					
				}				
			}		
			esModificacion = false;
			RequestContext context = RequestContext.getCurrentInstance();
			context.update(":form:tableEmpresas");
			
			JsfUtil.redirectTo("/pages/rcoa/sustanciasQuimicas/empresasActivasRSQ.jsf");
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void guardar(){		
		try {
			
			/**
			 * Segun RF: Vigencia hasta: Se muestra la fecha del último día del año en curso.
			 */
			Calendar fechaAprobacion = Calendar.getInstance();
			fechaAprobacion.set(Calendar.MONTH, 11);
			fechaAprobacion.set(Calendar.DATE, 31);

			Date lastDayOfMonth = fechaAprobacion.getTime();
			registro.setVigenciaHasta(lastDayOfMonth);
			
			CatalogoGeneralCoa pronunciamiento = new CatalogoGeneralCoa();
			pronunciamiento.setId(58);
			registro.setTipoPronunciamiento(pronunciamiento);
			registro.setEstadoImportacionDeclaracion(true);
			
			registroSustanciaQuimicaFacade.guardarRSQ(registro, registro.getUsuario());		
			
			/**
			 * busca si el número se encuentra entre los registros que no tienen usuario en el suia y lo desactiva para que ingrese el nuevo
			 */
			RegistroSustanciaQuimica registroConsumo = registroSustanciaQuimicaFacade.obtenerRegistroPorCodigoConsumo(registro.getNumeroAplicacion());
			
			if(registroConsumo != null && registroConsumo.getId() != null){
				registroConsumo.setEstado(false);
				registroSustanciaQuimicaFacade.guardarRSQ(registroConsumo, registro.getUsuario());	
			}			
			
			/**
			 * Se recorre los permisos, pero cuando es importanción se debe crear registros en otras tablas también
			 */
			
			for(PermisoDeclaracionRSQ sus : listaSustanciasQuimicasPermiso){
				
				if(sus.getLicenciaImportacion()){
					GestionarProductosQuimicosProyectoAmbiental gpq = new GestionarProductosQuimicosProyectoAmbiental();
					gpq.setSustanciaquimica(sus.getSustanciaQuimica());
					gpq.setEstado(true);
					gpq.setFechaCreacion(new Date());
					gpq.setControlSustancia(false);
					
					gestionarProductosQuimicosProyectoAmbientalFacade.guardar(gpq);	
					
					CaracteristicaActividad caracteristica = new CaracteristicaActividad();
					caracteristica.setId(1);
					ActividadSustancia actividad = new ActividadSustancia();
					actividad.setActividadSeleccionada(true);
					actividad.setCupo(sus.getCupo());
					actividad.setCupoControl(sus.getCupo());
					actividad.setGestionarProductosQuimicosProyectoAmbiental(gpq);
					actividad.setCaracteristicaActividad(caracteristica);
					actividad.setFechaCreacion(new Date());
					actividad.setEstado(true);
					actividad.setRegistroSustanciaQuimica(registro);
					actividadSustanciaQuimicaFacade.guardar(actividad, JsfUtil.getLoggedUser());					
				}
				
				sus.setFechaCreacion(new Date());
				sus.setEstado(true);
				sus.setUsuarioCreacion(JsfUtil.getLoggedUser().getNombre());
				sus.setRegistroSustanciaQuimica(registro);
				
				permisoDeclaracionRSQFacade.guardar(sus);					
			}	
			
			
			for(PermisoDeclaracionRSQ sus : listaPermisosEliminar){
				sus.setEstado(false);
				permisoDeclaracionRSQFacade.guardar(sus);
			}
			
			for(DocumentosSustanciasQuimicasRcoa doc : listaDocumentos){
				
				InformeOficioRSQ oficio = new InformeOficioRSQ(registro,TipoInformeOficioEnum.OFICIO_PRONUNCIAMIENTO,1,null);
				informesOficiosRSQFacade.guardarInformeRSQ(oficio);
								
				documentosFacade.guardarDocumento(registro.getNumeroAplicacion(), doc, oficio.getId());   
			}	
			
			JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);	
			
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('dlgEmpresa').hide();");
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void modificar(){
		try {
			
			registroSustanciaQuimicaFacade.guardarRSQ(registro, registro.getUsuario());
			
			for(PermisoDeclaracionRSQ sus : listaSustanciasQuimicasPermiso){
				if(sus.getId() != null){
					if(sus.getLicenciaImportacion()){
						
						List<ActividadSustancia> listaActividadesImp = new ArrayList<ActividadSustancia>();						
						listaActividadesImp = actividadSustanciaQuimicaFacade.obtenerActividadesImportacion(registro, sus.getSustanciaQuimica());
						
						if(listaActividadesImp != null && !listaActividadesImp.isEmpty()){
							ActividadSustancia actividad = new ActividadSustancia();
							actividad = listaActividadesImp.get(0);
							if(sus.getEditarCupo()){
								actividad.setCupo(sus.getCupo());
								actividad.setCupoControl(sus.getCupo());
								actividad.setUsuarioModificacion(JsfUtil.getLoggedUser().getNombre());
								actividad.setFechaModificacion(new Date());
							}		
							
							if(sus.getAmpliacion() != null && sus.getAmpliacion()){
								actividad.setCupo(sus.getCupo());								
								actividad.setUsuarioModificacion(JsfUtil.getLoggedUser().getNombre());
								actividad.setFechaModificacion(new Date());
								
								double saldoAnterior = actividad.getCupoControl();
								double sumaSaldo = saldoAnterior + sus.getAmpliacionCupo();
								actividad.setCupoControl(sumaSaldo);
								
								List<SolicitudImportacionRSQ> listaSolicitudesAux =  solicitudImportacionRSQFacade.listaSolicitudesTotalPorRSQ(registro.getId(), sus.getSustanciaQuimica().getId());
								
								if(listaSolicitudesAux != null && !listaSolicitudesAux.isEmpty()){
									DetalleSolicitudImportacionRSQ detalleAnterior = detalleSolicitudImportacionRSQFacade.buscarPorSolicitud(listaSolicitudesAux.get(0));
									double cupoAnterior = detalleAnterior.getCupoCantidad().doubleValue();
									double suma = cupoAnterior + sus.getAmpliacionCupo();
									
									detalleAnterior.setCupoCantidad(new BigDecimal(suma));
									
									detalleSolicitudImportacionRSQFacade.save(detalleAnterior, JsfUtil.getLoggedUser());
								}								
							}
							
							actividadSustanciaQuimicaFacade.guardar(actividad, JsfUtil.getLoggedUser());
							
						}else{
							GestionarProductosQuimicosProyectoAmbiental gpq = new GestionarProductosQuimicosProyectoAmbiental();
							gpq.setSustanciaquimica(sus.getSustanciaQuimica());
							gpq.setEstado(true);
							gpq.setFechaCreacion(new Date());
							gpq.setControlSustancia(false);
							
							gestionarProductosQuimicosProyectoAmbientalFacade.guardar(gpq);	
							
							CaracteristicaActividad caracteristica = new CaracteristicaActividad();
							caracteristica.setId(1);
							ActividadSustancia actividad = new ActividadSustancia();
							actividad.setActividadSeleccionada(true);
							actividad.setCupo(sus.getCupo());
							actividad.setCupoControl(sus.getCupo());
							actividad.setGestionarProductosQuimicosProyectoAmbiental(gpq);
							actividad.setCaracteristicaActividad(caracteristica);
							actividad.setFechaCreacion(new Date());
							actividad.setEstado(true);
							actividad.setRegistroSustanciaQuimica(registro);
							actividadSustanciaQuimicaFacade.guardar(actividad, JsfUtil.getLoggedUser());	
						}						
					}
					
					if(sus.getAmpliacion() != null && sus.getAmpliacion()){
						
						permisoAnterior = sus.getPermisoAnterior();
						permisoAnterior.setId(null);
						permisoAnterior.setIdPadre(sus.getId());					
						
						permisoDeclaracionRSQFacade.guardar(permisoAnterior);
						
						sus.setCupoAnterior(permisoAnterior.getCupo());
//						permisoDeclaracionRSQ.setAmpliacionCupo(valorAmpliacion);
//						sus.setCupo(totalCupo);
						
						if(sus.getDocumentoRespaldo().getContenidoDocumento() != null && sus.getDocumentoRespaldo().getId() == null){
							documentosFacade.guardarDocumento(registro.getNumeroAplicacion(), sus.getDocumentoRespaldo(), permisoDeclaracionRSQ.getId());   
						}
						sus.setAmpliacion(false);
						sus.setPermisoAnterior(null);
					}					
					
					/**
					 * solución que solo funciona para el primer mes de declaracion.
					 */
					if(sus.getDeclaracionSustancias()){
						DeclaracionSustanciaQuimica declaracion = declaracionSustanciaQuimicaFacade.buscarPorSustanciaPeligrosa(registro, sus.getSustanciaQuimica(), sus.getAnio(), sus.getMes());
						if(declaracion != null && declaracion.getId() != null){
							if(!declaracion.getEstadoDeclaracion().getId().equals(241)){
								declaracion.setCantidadInicio(sus.getStock());
								declaracionSustanciaQuimicaFacade.guardar(declaracion, JsfUtil.getLoggedUser());
							}							
						}						
					}
					
					sus.setFechaModificacion(new Date());
					sus.setUsuarioModificacion(JsfUtil.getLoggedUser().getNombre());
					permisoDeclaracionRSQFacade.guardar(sus);	
					
				}else{
					if(sus.getLicenciaImportacion()){
						
						List<ActividadSustancia> listaActividadesImp = new ArrayList<ActividadSustancia>();						
						listaActividadesImp = actividadSustanciaQuimicaFacade.obtenerActividadesImportacion(registro, sus.getSustanciaQuimica());
						
						if(listaActividadesImp != null && !listaActividadesImp.isEmpty()){
							ActividadSustancia actividad = new ActividadSustancia();
							actividad = listaActividadesImp.get(0);
							if(sus.getEditarCupo()){
								actividad.setCupo(sus.getCupo());
								actividad.setCupoControl(sus.getCupo());
								actividad.setUsuarioModificacion(JsfUtil.getLoggedUser().getNombre());
								actividad.setFechaModificacion(new Date());
							}
							
							actividadSustanciaQuimicaFacade.guardar(actividad, JsfUtil.getLoggedUser());
							
						}else{
							GestionarProductosQuimicosProyectoAmbiental gpq = new GestionarProductosQuimicosProyectoAmbiental();
							gpq.setSustanciaquimica(sus.getSustanciaQuimica());
							gpq.setEstado(true);
							gpq.setFechaCreacion(new Date());
							gpq.setControlSustancia(false);
							
							gestionarProductosQuimicosProyectoAmbientalFacade.guardar(gpq);	
							
							CaracteristicaActividad caracteristica = new CaracteristicaActividad();
							caracteristica.setId(1);
							ActividadSustancia actividad = new ActividadSustancia();
							actividad.setActividadSeleccionada(true);
							actividad.setCupo(sus.getCupo());
							actividad.setCupoControl(sus.getCupo());
							actividad.setGestionarProductosQuimicosProyectoAmbiental(gpq);
							actividad.setCaracteristicaActividad(caracteristica);
							actividad.setFechaCreacion(new Date());
							actividad.setEstado(true);
							actividad.setRegistroSustanciaQuimica(registro);
							actividadSustanciaQuimicaFacade.guardar(actividad, JsfUtil.getLoggedUser());
						}											
					}
					
					sus.setFechaCreacion(new Date());
					sus.setEstado(true);
					sus.setUsuarioCreacion(JsfUtil.getLoggedUser().getNombre());
					sus.setRegistroSustanciaQuimica(registro);
					
					permisoDeclaracionRSQFacade.guardar(sus);	
				}				
			}
			
			for(PermisoDeclaracionRSQ sus : listaPermisosEliminar){
				sus.setEstado(false);
				permisoDeclaracionRSQFacade.guardar(sus);
			}
			
			for(DocumentosSustanciasQuimicasRcoa doc : listaDocumentos){
				
				InformeOficioRSQ oficio = new InformeOficioRSQ();
				oficio = informesOficiosRSQFacade.obtenerPorRSQ(registro, TipoInformeOficioEnum.OFICIO_PRONUNCIAMIENTO, 1);
				
				if(oficio == null || oficio.getId() == null){
					oficio = new InformeOficioRSQ(registro,TipoInformeOficioEnum.OFICIO_PRONUNCIAMIENTO,1,null);
					informesOficiosRSQFacade.guardarInformeRSQ(oficio);
				}
				
				if(doc.getId() == null){									
					documentosFacade.guardarDocumento(registro.getNumeroAplicacion(), doc, oficio.getId());  
				}				 
			}	
			
			guardarAmpliacion=false;
			JsfUtil.addMessageInfo(JsfUtil.REGISTRO_ACTUALIZADO);					
			JsfUtil.redirectTo("/pages/rcoa/sustanciasQuimicas/empresasActivasRSQ.jsf");
			
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('dlgEmpresa').hide();");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void eliminar(RegistroSustanciaQuimica registro){
		try {
			
			if(validarExistenciaDeclaracionImportacion(registro)){
				RegistroSustanciaQuimica registroAux = registroSustanciaQuimicaFacade.obtenerRegistroPorId(registro.getId());
				
				registroAux.setEstado(false);
				registroSustanciaQuimicaFacade.guardar(registroAux, JsfUtil.getLoggedUser());
				
				JsfUtil.addMessageInfo("Se eliminó el registro seleccionado.");			
				init();			
				JsfUtil.redirectTo("/pages/rcoa/sustanciasQuimicas/empresasActivasRSQ.jsf");
			}else{
				JsfUtil.addMessageInfo("No se puede eliminar el Registro de Sustancias Químicas ya que tiene declaraciones o importaciones terminadas o en proceso asociadas");		
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean validarExistenciaDeclaracionImportacion(RegistroSustanciaQuimica registro){
		
		List<DeclaracionSustanciaQuimica> lista = declaracionSustanciaQuimicaFacade.obtenerPorRSQ(registro);
		
		List<SolicitudImportacionRSQ> listaImp = solicitudImportacionRSQFacade.listaPorRSQ(registro.getId());
		
		if(lista.isEmpty() && listaImp == null){
			return true;
		}
		
		return false;
	}
	
	public StreamedContent getDocumentoDownload(DocumentosSustanciasQuimicasRcoa documento){		
		try {
			if (documento != null && documento.getContenidoDocumento() !=null) {
				StreamedContent streamedContent = new DefaultStreamedContent(new ByteArrayInputStream(documento.getContenidoDocumento()),documento.getMime(),documento.getNombre());
				return streamedContent;
			}
		} catch (Exception e) {			
			e.printStackTrace();
		}
		return null;
	}
	
	public String mes(int mes){
		return JsfUtil.devuelveMes(mes - 1);
	}
	
	public void seleccionarSustancia(SustanciaQuimicaPeligrosa sustancia){
		if(modificacionSus){			
			for(PermisoDeclaracionRSQ permiso : listaPermisosAux){
				if(permiso.getSustanciaQuimica().equals(permisoDeclaracionRSQ.getSustanciaQuimica())){
					JsfUtil.addMessageError("La sustancia química ya se encuentra ingresada.");
					permisoDeclaracionRSQ.setSustanciaQuimica(null);
					break;
				}
			}
		}else{
			for(PermisoDeclaracionRSQ permiso : listaSustanciasQuimicasPermiso){
				if(permiso.getSustanciaQuimica().equals(permisoDeclaracionRSQ.getSustanciaQuimica())){
					JsfUtil.addMessageError("La sustancia química ya se encuentra ingresada.");
					permisoDeclaracionRSQ.setSustanciaQuimica(null);
					break;
				}
			}
		}		
	}
	
	public void validateInformacionAumento(FacesContext context, UIComponent validate, Object value) throws RemoteException {
		
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		if(permisoDeclaracionRSQ.getAmpliacionCupo() == null || permisoDeclaracionRSQ.getAmpliacionCupo() == 0){
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe ingresar un valor para la cantidad solicitada", null));
		}
		
		if(documentoRespaldoAmpliacion == null || documentoRespaldoAmpliacion.getContenidoDocumento() == null){
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe adjuntar el documento de respaldo", null));
		}
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public void ampliacionSustancia(PermisoDeclaracionRSQ permiso){
		
		permisoDeclaracionRSQ = new PermisoDeclaracionRSQ();
		permisoAnterior = (PermisoDeclaracionRSQ)SerializationUtils.clone(permiso);
		
		setPermisoDeclaracionRSQAux(permisoAnterior);
		permisoDeclaracionRSQAux.setId(null);
		
		permiso.setPermisoAnterior(permisoAnterior);
		setPermisoDeclaracionRSQ(permiso);		
		
//		valorAmpliacion = permiso.getAmpliacionCupo();
		
		List<DocumentosSustanciasQuimicasRcoa> listaDocumentosAux = documentosFacade.obtenerListDocumentoPorTipo(TipoDocumentoSistema.RCOA_RSQ_DECLARACION_DOCUMENTO_RESPALDO, "DocumentoRespaldoAmpliacionRSQ", permiso.getId());
		
		if(listaDocumentosAux != null && !listaDocumentosAux.isEmpty()){
//			documentoRespaldoAmpliacion = listaDocumentosAux.get(0);
			permisoDeclaracionRSQ.setDocumentoRespaldo(listaDocumentosAux.get(0));
		}		
		
	}
	
	public void uploadDocumentoRespaldo(FileUploadEvent event){		
		documentoRespaldoAmpliacion = new DocumentosSustanciasQuimicasRcoa();
		documentoRespaldoAmpliacion = this.uploadListener(event, DocumentosSustanciasQuimicasRcoa.class, "pdf");
		documentoRespaldoAmpliacion.setFechaCreacion(new Date());
		TipoDocumento tipoDocumento = new TipoDocumento();
		tipoDocumento.setId(TipoDocumentoSistema.RCOA_RSQ_DECLARACION_DOCUMENTO_RESPALDO.getIdTipoDocumento());
		documentoRespaldoAmpliacion.setTipoDocumento(tipoDocumento);
		documentoRespaldoAmpliacion.setNombreTabla("DocumentoRespaldoAmpliacionRSQ");
		permisoDeclaracionRSQ.setDocumentoRespaldo(documentoRespaldoAmpliacion);
	}
	
	/**
	 * metodo para la suma que aparece al momento de poner guardar
	 */
	public void guardarInfoAmpliacion(){
		totalCupo = Double.parseDouble("0");
		
		if(permisoDeclaracionRSQ.getCupo() != null){
			totalCupo = permisoDeclaracionRSQ.getAmpliacionCupo() + permisoDeclaracionRSQ.getCupo();
		}else{
			totalCupo = permisoDeclaracionRSQ.getAmpliacionCupo();
		}		
		
		RequestContext context = RequestContext.getCurrentInstance();
		context.update(":formMensajeCupo:mensajeCupoDialog");
		context.execute("PF('mensajeCupoDialog').show();");	
	}
	
	/**
	 * Actualización de información
	 */
	public void guardarInformacionAmpliacion(){
		permisoDeclaracionRSQ.setAmpliacion(true);
		
		permisoDeclaracionRSQ.setCupo(totalCupo);
		RequestContext context = RequestContext.getCurrentInstance();
		context.update(":formEmpresa:tblSustancias");
		JsfUtil.addMessageInfo(JsfUtil.REGISTRO_ACTUALIZADO);	
		documentoRespaldoAmpliacion = new DocumentosSustanciasQuimicasRcoa();
	}
	
	public void limpiarAmpliacion(){
		
		for(PermisoDeclaracionRSQ permiso : listaSustanciasQuimicasPermiso){
			if(permiso.getId().equals(permisoDeclaracionRSQ.getId())){
				permiso.setCupo(permisoDeclaracionRSQAux.getCupo());
				permiso.setAmpliacion(permisoDeclaracionRSQAux.getAmpliacion());
				permiso.setAmpliacionCupo(permisoDeclaracionRSQAux.getAmpliacionCupo());
				permiso.setDocumentoRespaldo(permisoDeclaracionRSQAux.getDocumentoRespaldo());
			}
		}	
	}
	

}
