package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.cliente.ConsultaRucCedula;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.DocumentosImpactoEstudioFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.EquipoConsultorEIACoaFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformacionProyectoEIACoaFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.TipoComponenteParticipacionCoaFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.DocumentoEstudioImpacto;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.EquipoConsultor;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformacionProyectoEia;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.TipoComponenteParticipacion;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.model.SubActividades;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Consultor;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.EntityCertificadoParticipacionRCOA;
import ec.gob.ambiente.suia.eia.ficha.facade.ConsultorCalificadoFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarPdf;
import ec.gob.registrocivil.consultacedula.Cedula;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class DescargarGuiasEIAController {

	private final Logger LOG = Logger.getLogger(DescargarGuiasEIAController.class);
	
    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @Getter
    @Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private DocumentosFacade documentoGuiaFacade;
	@EJB
	private ConsultorCalificadoFacade consultorCalificadoFacade;
	@EJB
	private TipoComponenteParticipacionCoaFacade tipoComponenteParticipacionCoaFacade;
	@EJB
	private InformacionProyectoEIACoaFacade informacionProyectoEIACoaFacade;
	@EJB
	private EquipoConsultorEIACoaFacade equipoConsultorEIACoaFacade;
	@EJB
	private PlantillaReporteFacade plantillaReporteFacade;
	@EJB
	private DocumentosImpactoEstudioFacade documentosImpactoEstudioFacade;
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
    @Setter
	private List<Consultor> listaConsultoresCalificados;
	
	@Getter
    @Setter
	private List<EquipoConsultor> listaEquipoConsultor, listaEquipoConsultorEliminado;

	@Getter
	@Setter
	private List<TipoComponenteParticipacion> listaComponenteParticipacion;

	@Getter
	@Setter
	private TipoComponenteParticipacion componenteParticipacionPrincipal;
	
	@Getter
    @Setter
	private Consultor consultorSeleccionado;
	
	@Getter
    @Setter
	private EquipoConsultor consultorNoCalificado;
	
	@Getter
    @Setter
	private InformacionProyectoEia informacionProyectoEia;
	
	@Getter
    @Setter
	private DocumentoEstudioImpacto documentoManual, documetoCertificadoParticipacion;
	
	@Getter
    @Setter
	private String labelEquipoConsultor="";

	@Getter
    @Setter
	private boolean token, descargarGuia, subido=false, descargarCertificadoParticipacion =false, validarDatos, mostrarPnlEquipo, firmaSoloToken, ambienteProduccion;
	
    private Map<String, Object> variables;
	private String tramite, nombreGuia;
	private byte[] guia_byte;

	private final ConsultaRucCedula consultaRucCedula = new ConsultaRucCedula(Constantes.getUrlWsRegistroCivilSri());
	
	@Getter
    @Setter
    public static String tipoAmbiente = Constantes.getPropertyAsString("ambiente.produccion");
	
	@Getter
	@Setter
	private EquipoConsultor companiaConsultora;
	
	@Getter
	@Setter
	private Boolean existeNormativaGuias;
	
	@Getter
	@Setter
	private String zonaCamaronera;
	
	@Getter
	@Setter
	private Boolean esActividadCamaronera = false;
	
	@Getter
	@Setter
	private DocumentoEstudioImpacto documentoDocCamaroneraPlaya, documentoDocCamaroneraAlta;
	
	@PostConstruct
	private void init(){
		try{
			firmaSoloToken = Constantes.getPropertyAsBoolean("ambiente.produccion");
			nombreGuia = "Elaboracion de Estudio de Impacto Ambiental.pdf";
			descargarCertificadoParticipacion = false;
			descargarGuia = false;
			labelEquipoConsultor = "Información Consultor";
			consultorSeleccionado = new Consultor();
			consultorNoCalificado = new EquipoConsultor();
			listaEquipoConsultor = new ArrayList<EquipoConsultor>();
			listaEquipoConsultorEliminado = new ArrayList<EquipoConsultor>();
			documetoCertificadoParticipacion = null;
			mostrarPnlEquipo = false;
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
			listaConsultoresCalificados = consultorCalificadoFacade.consultoresCalificadosVigentes();
			listaComponenteParticipacion = tipoComponenteParticipacionCoaFacade.obtenerListaCatalogoComponenteParticipacion();
			componenteParticipacionPrincipal = new TipoComponenteParticipacion();
			for (TipoComponenteParticipacion objParticipacion : listaComponenteParticipacion) {
				if(objParticipacion.getNombre().toLowerCase().contains("principal")){
					componenteParticipacionPrincipal = objParticipacion;
				}
			}
			informacionProyectoEia = informacionProyectoEIACoaFacade.obtenerInformacionProyectoEIAPorProyecto(proyectoLicenciaCoa);
			if(informacionProyectoEia == null){
				informacionProyectoEia = new InformacionProyectoEia();
				informacionProyectoEia.setProyectoLicenciaCoa(proyectoLicenciaCoa);
				informacionProyectoEia.setEstado(true);
				informacionProyectoEia.setCodigo(proyectoLicenciaCoa.getCodigoUnicoAmbiental());
				informacionProyectoEIACoaFacade.guardar(informacionProyectoEia);
			}else{
				//cargo el consultor seleccionado
				if(informacionProyectoEia.getConsultor() != null){
					consultorSeleccionado = informacionProyectoEia.getConsultor();
					labelEquipoConsultor = consultorSeleccionado.getTipoConsultor().equals("J")?"Equipo Consultor":"Información Consultor";	
					
					if(consultorSeleccionado.getTipoConsultor().equals("J")){
						mostrarPnlEquipo = true;
						labelEquipoConsultor = "Equipo Consultor";
						List<EquipoConsultor> lista = new ArrayList<>();
						lista = equipoConsultorEIACoaFacade.obtenerInformacionProyectoEIAPorProyectoPorIdConsultor(informacionProyectoEia, consultorSeleccionado.getId());
						if(lista != null && !lista.isEmpty()){
							companiaConsultora = lista.get(0);
							
							Organizacion organizacion = organizacionFacade.buscarPorRuc(consultorSeleccionado.getRuc());
							
							companiaConsultora.setNombreRepresentante(organizacion.getPersona().getNombre());
							companiaConsultora.setCedulaRepresentante(organizacion.getPersona().getPin());
							
							documetoCertificadoParticipacion = documentosImpactoEstudioFacade.documentoXTablaIdXIdDoc(companiaConsultora.getId(), EquipoConsultor.class.getSimpleName(), TipoDocumentoSistema.EIA_CERTIFICADO_PARTICIPACION_CONSULTOR);
							
							if(documetoCertificadoParticipacion != null){
								companiaConsultora.setAdjuntoCertificado(documetoCertificadoParticipacion);
								companiaConsultora.setDocumentoFirmado(true);
							}
						}
						
					}
				}
				// cargo el equipo consultor
				cargarDocumentosParticipacion();
			}
			
			validarActividadCamaronera();
			
			if(loginBean.getUsuario().getId() == null){
				JsfUtil.redirectTo("/start.js");
				return;
			}
			guia_byte = documentoGuiaFacade.descargarDocumentoPorNombre(nombreGuia);
			ambienteProduccion = Boolean.parseBoolean(tipoAmbiente);
			if(ambienteProduccion){
				token = true;
			}
			
			existeNormativaGuias = Constantes.getPropertyAsBoolean("rcoa.existe.normativa.guias.esia");
			
			if(variables.get("requiereIngresoPlan") == null){
				Map<String, Object> parametros = new HashMap<>();
				parametros.put("requiereIngresoPlan", true);
				
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
			}
			
			if(esActividadCamaronera){
				if(zonaCamaronera.equals("MIXTA")){
					documentoDocCamaroneraAlta = documentosImpactoEstudioFacade.documentoXTablaIdXIdDoc(informacionProyectoEia.getId(), "RA_CONCESION_MINERA_ALTA", TipoDocumentoSistema.RCOA_DOCUMENTO_TITULO_CONCESION_CAMARONERA);
					documentoDocCamaroneraPlaya = documentosImpactoEstudioFacade.documentoXTablaIdXIdDoc(informacionProyectoEia.getId(), "RA_CONCESION_MINERA_PLAYA", TipoDocumentoSistema.RCOA_DOCUMENTO_TITULO_CONCESION_CAMARONERA);
										
				}else if(zonaCamaronera.equals("ALTA")){
					documentoDocCamaroneraAlta = documentosImpactoEstudioFacade.documentoXTablaIdXIdDoc(informacionProyectoEia.getId(), "RA_CONCESION_MINERA_ALTA", TipoDocumentoSistema.RCOA_DOCUMENTO_TITULO_CONCESION_CAMARONERA);
				}else{
					documentoDocCamaroneraPlaya = documentosImpactoEstudioFacade.documentoXTablaIdXIdDoc(informacionProyectoEia.getId(), "RA_CONCESION_MINERA_PLAYA", TipoDocumentoSistema.RCOA_DOCUMENTO_TITULO_CONCESION_CAMARONERA);
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}

	public void cargarDocumentosParticipacion(){
		listaEquipoConsultor = equipoConsultorEIACoaFacade.obtenerInformacionProyectoEIAPorProyecto(informacionProyectoEia);
		if(companiaConsultora != null){
			listaEquipoConsultor.remove(companiaConsultora);
		}
		
		for (EquipoConsultor objEquipo : listaEquipoConsultor) {
			objEquipo.setSeleccionado(true);
			try {
				documetoCertificadoParticipacion = documentosImpactoEstudioFacade.documentoXTablaIdXIdDoc(objEquipo.getId(), EquipoConsultor.class.getSimpleName(), TipoDocumentoSistema.EIA_CERTIFICADO_PARTICIPACION_CONSULTOR);
			} catch (CmisAlfrescoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(documetoCertificadoParticipacion != null){
				objEquipo.setAdjuntoCertificado(documetoCertificadoParticipacion);
				objEquipo.setDocumentoFirmado(true);
			}
		}	
	}
	
	public boolean existeConsultorSeleccionado() {
		return consultorSeleccionado != null;
	}
	
	/**
	 * Seleccionar consultor de la lista de consultores calificados
	 */
	public void seleccionarConsultor(Consultor consultor) {
		try {
			mostrarPnlEquipo = false;
			labelEquipoConsultor = "Información Consultor";
			consultorSeleccionado = consultor;
			// verifico si el consultor seleccionado es persona natural o juridica
			if(consultorSeleccionado.getTipoConsultor().equals("J")){
				mostrarPnlEquipo = true;
				labelEquipoConsultor = "Equipo Consultor";
				// busco si ya estaba ingresado uno anteriormente y lo borro
				for (EquipoConsultor objEquipo : listaEquipoConsultor) {
					if(objEquipo.getConsultor() != null && objEquipo.getConsultor().equals(consultor)){
						listaEquipoConsultor.remove(objEquipo);
					}
				}
			}else{
				listaEquipoConsultor = new ArrayList<EquipoConsultor>();
			}
			
			
			consultorNoCalificado = new EquipoConsultor();
			consultorNoCalificado.setDocumentoIdentificacion(consultorSeleccionado.getRuc());
			consultorNoCalificado.setNombre(consultorSeleccionado.getNombre());
			consultorNoCalificado.setCodigoMae(consultorSeleccionado.getRegistro());
			consultorNoCalificado.setConsultor(consultorSeleccionado);
			consultorNoCalificado.setTipoComponente(componenteParticipacionPrincipal);
			
			if(consultorSeleccionado.getTipoConsultor().equals("J")){
				Organizacion organizacion = organizacionFacade.buscarPorRuc(consultorSeleccionado.getRuc());
				
				consultorNoCalificado.setNombreRepresentante(organizacion.getPersona().getNombre());
				consultorNoCalificado.setCedulaRepresentante(organizacion.getPersona().getPin());
				companiaConsultora = consultorNoCalificado;
			}else{
				companiaConsultora = null;
			}
			
			documentoManual = null;
			
			RequestContext context = RequestContext.getCurrentInstance();
			
			if(consultorSeleccionado.getTipoConsultor().equals("J")){
				context.execute("PF('consultorNoCalificadoCom').show();");
			}else{
				context.execute("PF('consultorNoCalificado').show();");
			}
	        
		    JsfUtil.addCallbackParam("consultorCalificado");
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public boolean consultorPrincipal(Consultor objConsultor){
		if(objConsultor != null){
			return consultorSeleccionado.equals(objConsultor);
		}else{
			return false;
		}
	}
	
	/**
	 * Agregar consultor no calificado a la lista del equipo consultor
	 */
	public void agregarConsultorNoCalificado() {
		
		if(consultorNoCalificado.getAdjuntoCertificado() == null ){
			JsfUtil.addMessageError("Falta firmar el Certificado de Participación en el Estudio de Impacto Ambiental");
			return;
		}
		
		if(!consultorNoCalificado.isSeleccionado()){
			for (EquipoConsultor objEquipo : listaEquipoConsultor) {
				if(consultorNoCalificado.getConsultor() != null && objEquipo.getConsultor() != null && consultorNoCalificado.getConsultor().equals(objEquipo.getConsultor())){
					JsfUtil.addMessageError("El consultor ya existe.");
					return;
				}
				
				if(objEquipo.getDocumentoIdentificacion().equals(consultorNoCalificado.getDocumentoIdentificacion())){
					JsfUtil.addMessageError("El consultor ya existe.");
					return;
				}
			}
			consultorNoCalificado.setSeleccionado(true);
			listaEquipoConsultor.add(consultorNoCalificado);
		}else{
			for (EquipoConsultor objEquipo : listaEquipoConsultor) {
				if(consultorNoCalificado.getConsultor().equals(objEquipo.getConsultor())){
					objEquipo = consultorNoCalificado;
				}
			}
		}		
		
		if(consultorNoCalificado.getAdjuntoCertificado() == null ){
			JsfUtil.addMessageError("Falta firmar el Certificado de Participación en el Estudio de Impacto Ambiental");
			return;
		} else if(!consultorNoCalificado.isDocumentoFirmado()) {
			if (consultorNoCalificado.getAdjuntoCertificado().getAlfrescoId() != null) {
				if(!documentosImpactoEstudioFacade.verificarFirmaVersion(consultorNoCalificado.getAdjuntoCertificado().getAlfrescoId())) {
					JsfUtil.addMessageError("Falta firmar el certificado de participación en el estudio de impacto ambiental");
					return;
				}
			} else {
				//para documentos de firma manual si la variable objEquipo.getDocumentoFirmado() no es true es xq solo se genero y no se subio el firmado
				JsfUtil.addMessageError("Falta firmar el certificado de participación en el estudio de impacto ambiental");
				return;
			}
		}
		
		JsfUtil.addCallbackParam("addConsultorNoCalificado");
	}

	public void limpiarConsultorNoCalificado() {
		consultorNoCalificado = new EquipoConsultor();
	}

	public void seleccionarConsultorNoCalificado(EquipoConsultor consultor) {
		consultorNoCalificado = consultor;
		if(consultorNoCalificado.getAdjuntoCertificado() != null ){
			documentoManual = consultorNoCalificado.getAdjuntoCertificado();
		}
	}

	public void eliminarConsultorNoCalificado(EquipoConsultor consultor) {
		if(consultor != null){
			listaEquipoConsultor.remove(consultor);
			listaEquipoConsultorEliminado.add(consultor);
		}
	}
	
	public void enviar(){
		try{
			if(guardar(false)){
				try {
					procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),bandejaTareasBean.getTarea().getTaskId(),bandejaTareasBean.getTarea().getProcessInstanceId(), null);
					JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
					JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
				} catch (JbpmException e) {
					e.printStackTrace();
					JsfUtil.addMessageInfo(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
				}
			}
		}catch(Exception e){
			LOG.error("Ocurrió un error al guardar la informacion", e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
    public boolean validarDatos(){
		boolean valido = true;
		//valido que se seleccione el consultor calificado
		if(consultorSeleccionado == null || consultorSeleccionado.getId() == null){
			JsfUtil.addMessageError("El campo consultor calificado es requerido.");
			valido = false;
		}else{
			// valido que si el consultor calificado no es persona natural ingrese el equipo consultor
			if(consultorSeleccionado.getTipoConsultor().equals("J")){
				if(listaEquipoConsultor == null || listaEquipoConsultor.size() == 0){
					JsfUtil.addMessageError("El equipo consultor es requerido.");
					valido = false;
				}
			}else{
				if(listaEquipoConsultor == null || listaEquipoConsultor.size() == 0){
					JsfUtil.addMessageError("Debe ingresar la información del consultor.");
					valido = false;
				}
			}
		}
		if(existeNormativaGuias && !descargarGuia){
			JsfUtil.addMessageError("Debe descargar las Guías Referenciales para la elaboración del Estudio de Impacto Ambiental");
			valido = false;
		}
		for (EquipoConsultor objEquipo : listaEquipoConsultor) {
			if(objEquipo.getAdjuntoCertificado() == null ){
				JsfUtil.addMessageError("Falta firmar el certificado de participación en el estudio de impacto ambiental del consultor "+objEquipo.getNombre());
				valido = false;
			} else if(!objEquipo.isDocumentoFirmado()) {
				if (objEquipo.getAdjuntoCertificado().getAlfrescoId() != null) {
					if(!documentosImpactoEstudioFacade.verificarFirmaVersion(objEquipo.getAdjuntoCertificado().getAlfrescoId())) {
						JsfUtil.addMessageError("Falta firmar el certificado de participación en el estudio de impacto ambiental del consultor "+objEquipo.getNombre());
						valido = false;
					}
				} else {
					//para documentos de firma manual si la variable objEquipo.getDocumentoFirmado() no es true es xq solo se genero y no se subio el firmado
					JsfUtil.addMessageError("Falta firmar el certificado de participación en el estudio de impacto ambiental del consultor "+objEquipo.getNombre());
					valido = false;
				}
			}
		}
		
		if(esActividadCamaronera){
			if(zonaCamaronera.equals("MIXTA")){
				if(informacionProyectoEia.getConcesionCamaroneraAlta()== null || informacionProyectoEia.getConcesionCamaroneraAlta().isEmpty()){
					JsfUtil.addMessageError("Debe ingresar el número del acuerdo de la concesión camaronera de Tierras Privadas o Zonas Altas");
					valido = false;
				}
				
				if(documentoDocCamaroneraAlta == null || documentoDocCamaroneraAlta.getAlfrescoId() == null){
					JsfUtil.addMessageError("Adjunte el documento de Autorización Administrativa o Título de Concesión camaronera Tierras Privadas o Zonas Altas");
					valido = false;
				}
				
				if(informacionProyectoEia.getConcesionCamaroneraPlaya()== null || informacionProyectoEia.getConcesionCamaroneraPlaya().isEmpty()){
					JsfUtil.addMessageError("Debe ingresar el número del acuerdo de al concesión camaronera de Zona de Playa y Bahía");
					valido = false;
				}
				
				if(documentoDocCamaroneraPlaya == null || documentoDocCamaroneraPlaya.getAlfrescoId()== null){
					JsfUtil.addMessageError("Adjunte el documento de Autorización Administrativa o Título de Concesión camaronera Zona de Playa y Bahía");
					valido = false;
				}
				
				
			}else if(zonaCamaronera.equals("ALTA")){
				if(informacionProyectoEia.getConcesionCamaroneraAlta()== null || informacionProyectoEia.getConcesionCamaroneraAlta().isEmpty()){
					JsfUtil.addMessageError("Debe ingresar el número del acuerdo de al concesión camaronera");
					valido = false;
				}
				
				if(documentoDocCamaroneraAlta == null || documentoDocCamaroneraAlta.getAlfrescoId() == null){
					JsfUtil.addMessageError("Adjunte el documento de Autorización Administrativa o Título de Concesión camaronera");
					valido = false;
				}
			}else{
				if(informacionProyectoEia.getConcesionCamaroneraPlaya()== null || informacionProyectoEia.getConcesionCamaroneraPlaya().isEmpty()){
					JsfUtil.addMessageError("Debe ingresar el número del acuerdo de al concesión camaronera");
					valido = false;
				}
				
				if(documentoDocCamaroneraPlaya == null || documentoDocCamaroneraPlaya.getAlfrescoId() == null){
					JsfUtil.addMessageError("Adjunte el documento de Autorización Administrativa o Título de Concesión camaronera");
					valido = false;
				}
			}
		}
		
		return valido;
	}

	public boolean guardar(Boolean mostrarMensaje) throws Exception{
		validarDatos = false;
		boolean modificacionArchivo = false;
		if(!mostrarMensaje && !validarDatos()) {
			return false;
		}
		
		if(consultorSeleccionado == null || consultorSeleccionado.getId() == null){
			JsfUtil.addMessageError("El campo consultor calificado es requerido.");
			return false;
		}
		
		informacionProyectoEia.setConsultor(consultorSeleccionado);
		informacionProyectoEIACoaFacade.guardar(informacionProyectoEia);
		if(consultorSeleccionado.getTipoConsultor().equals("J")){
			
			companiaConsultora.setEstado(true);
			companiaConsultora.setInformacionProyectoEia(informacionProyectoEia);
			equipoConsultorEIACoaFacade.guardar(companiaConsultora);
			
			if(companiaConsultora.getAdjuntoCertificado() != null){
				if(companiaConsultora.getAdjuntoCertificado().getId() != null){
					companiaConsultora.getAdjuntoCertificado().setIdTable(companiaConsultora.getId());
					documentosImpactoEstudioFacade.guardar(companiaConsultora.getAdjuntoCertificado());
				}else if(companiaConsultora.getAdjuntoCertificado().getContenidoDocumento() != null){
					documentosImpactoEstudioFacade.ingresarDocumento(companiaConsultora.getAdjuntoCertificado(), companiaConsultora.getId(), proyectoLicenciaCoa.getCodigoUnicoAmbiental(), TipoDocumentoSistema.EIA_CERTIFICADO_PARTICIPACION_CONSULTOR, "certificado de participacion del consultor "+proyectoLicenciaCoa.getCodigoUnicoAmbiental()+"-"+companiaConsultora.getDocumentoIdentificacion()+".pdf", EquipoConsultor.class.getSimpleName(), bandejaTareasBean.getProcessId());	
				}
				modificacionArchivo = true;
			}
			
			// guardo el equipò consultor
			for (EquipoConsultor equipo : listaEquipoConsultor) {
				equipo.setEstado(true);
				equipo.setInformacionProyectoEia(informacionProyectoEia);
				equipoConsultorEIACoaFacade.guardar(equipo);
				if(equipo.getAdjuntoCertificado() != null && equipo.getAdjuntoCertificado().getContenidoDocumento() != null){
					if(equipo.getAdjuntoCertificado().getId() != null){
						equipo.getAdjuntoCertificado().setIdTable(equipo.getId());
						documentosImpactoEstudioFacade.guardar(equipo.getAdjuntoCertificado());
					}else{
						documentosImpactoEstudioFacade.ingresarDocumento(equipo.getAdjuntoCertificado(), equipo.getId(), proyectoLicenciaCoa.getCodigoUnicoAmbiental(), TipoDocumentoSistema.EIA_CERTIFICADO_PARTICIPACION_CONSULTOR, "certificado de participacion del consultor "+proyectoLicenciaCoa.getCodigoUnicoAmbiental()+"-"+equipo.getDocumentoIdentificacion()+".pdf", EquipoConsultor.class.getSimpleName(), bandejaTareasBean.getProcessId());	
					}
					modificacionArchivo = true;
				}
			}
			// guardo el equipo consultor eliminado
			for (EquipoConsultor equipo : listaEquipoConsultorEliminado) {
				if(equipo.getId() != null){
					equipo.setEstado(false);
					equipoConsultorEIACoaFacade.guardar(equipo);
				}
			}
		}else{
			for (EquipoConsultor equipo : listaEquipoConsultor) {
				// elimino el equipò consultor
				equipoConsultorEIACoaFacade.desactivarEquipoConsultor(informacionProyectoEia, loginBean.getUsuario().getNombre());

					equipo.setEstado(true);
					equipo.setInformacionProyectoEia(informacionProyectoEia);
					equipoConsultorEIACoaFacade.guardar(equipo);
					if(equipo.getAdjuntoCertificado() != null){
						if(equipo.getAdjuntoCertificado().getId() != null){
							equipo.getAdjuntoCertificado().setIdTable(equipo.getId());
							documentosImpactoEstudioFacade.guardar(equipo.getAdjuntoCertificado());
						}else if(equipo.getAdjuntoCertificado().getContenidoDocumento() != null) {
							documentosImpactoEstudioFacade.ingresarDocumento(equipo.getAdjuntoCertificado(), equipo.getId(), proyectoLicenciaCoa.getCodigoUnicoAmbiental(), TipoDocumentoSistema.EIA_CERTIFICADO_PARTICIPACION_CONSULTOR, "certificado de participacion del consultor "+proyectoLicenciaCoa.getCodigoUnicoAmbiental()+"-"+equipo.getDocumentoIdentificacion()+".pdf", EquipoConsultor.class.getSimpleName(), bandejaTareasBean.getProcessId());
						}
						modificacionArchivo = true;
					}

			}
			for (EquipoConsultor equipo : listaEquipoConsultorEliminado) {
				if(equipo.getId() != null){
					equipo.setEstado(false);
					equipoConsultorEIACoaFacade.guardar(equipo);
				}
			}
		}
		guardarDocCamaroneras();
		validarDatos = true;
		cargarDocumentosParticipacion();
		if(mostrarMensaje)
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
					
		
		return validarDatos;
	}
	
	public void validarCedula (){
		if(consultorNoCalificado.getDocumentoIdentificacion().length() > 10 ){
			JsfUtil.addMessageError("El numero de documento no corresponde a un numero de cédula.");
			consultorNoCalificado = new EquipoConsultor();
			return;
		}
		if(consultorNoCalificado.getDocumentoIdentificacion() == null || consultorNoCalificado.getDocumentoIdentificacion().isEmpty()){
			consultorNoCalificado = new EquipoConsultor();
			JsfUtil.addMessageError("El numero de documento es obligatorio.");
			return;
		}
		try{
			Consultor objConsultor = consultorCalificadoFacade.buscarConsultorPorRuc(consultorNoCalificado.getDocumentoIdentificacion());
			if(objConsultor == null || objConsultor.getId() == null){

			}else{
				consultorNoCalificado.setConsultor(objConsultor);
				consultorNoCalificado.setCodigoMae(objConsultor.getRegistro());
			}
			
			Cedula cedula = consultaRucCedula.obtenerPorCedulaRC(Constantes.USUARIO_WS_MAE_SRI_RC,
					Constantes.PASSWORD_WS_MAE_SRI_RC, consultorNoCalificado.getDocumentoIdentificacion());
			if (cedula != null && cedula.getError().equals(Constantes.NO_ERROR_WS_MAE_SRI_RC)) {
				consultorNoCalificado.setNombre(cedula.getNombre());
				consultorNoCalificado.setFormacion(cedula.getProfesion());
			}else{
				JsfUtil.addMessageInfo("No se encontro un consultor con el numero de ruc "+consultorNoCalificado.getDocumentoIdentificacion());
			}
		}catch(Exception e){
			JsfUtil.addMessageInfo("No se encontro un consultor con el numero de ruc "+consultorNoCalificado.getDocumentoIdentificacion());
		}
	}
	
	public StreamedContent getGuia() throws Exception {
		DefaultStreamedContent content = null;
		try {
			if (guia_byte != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(guia_byte));
				content.setName(nombreGuia);
				descargarGuia = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}
	
	public StreamedContent getDocumentoCertificadoConsultor() {
		try{
			PlantillaReporte plantillaReporte = new PlantillaReporte();
			File certificadoParticipacionTmpPdf = null;
			String nombreReporte = "";
			
			if(consultorNoCalificado != null && consultorNoCalificado.getConsultor() != null){

				if(consultorSeleccionado != null && consultorSeleccionado.getTipoConsultor().equals("J")){			
					if(consultorNoCalificado.getConsultor().getTipoConsultor().equals("J")){
						plantillaReporte = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.EIA_CERTIFICADO_PARTICIPACION_CONSULTOR_COMPAÑIA);
					}else{
						plantillaReporte = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.EIA_CERTIFICADO_PARTICIPACION_EQUIPO_CONSULTOR);
					}
				}else{		
					plantillaReporte = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.EIA_CERTIFICADO_PARTICIPACION_CONSULTOR);
				}
				
				nombreReporte = "certificado_participacion_"+consultorNoCalificado.getDocumentoIdentificacion();
				
				EntityCertificadoParticipacionRCOA entityInforme = new EntityCertificadoParticipacionRCOA();
				entityInforme.setNombreProyecto(proyectoLicenciaCoa.getNombreProyecto());
				entityInforme.setCodigoProyecto(proyectoLicenciaCoa.getCodigoUnicoAmbiental());
				entityInforme.setNombreOperador(consultorNoCalificado.getNombre());
				entityInforme.setNumeroDocumento(consultorNoCalificado.getDocumentoIdentificacion());
				entityInforme.setNumeroOficio(consultorNoCalificado.getCodigoMae());
				entityInforme.setFormacion(consultorNoCalificado.getFormacion());
				entityInforme.setComponente(consultorNoCalificado.getTipoComponente().getNombre());
				if(proyectoLicenciaCoa.getAreaResponsable().getTipoArea().getId().equals(3))
					entityInforme.setAreaResponsable(proyectoLicenciaCoa.getAreaResponsable().getAreaAbbreviation());
				List<UbicacionesGeografica> ubicacion = proyectoLicenciaCoaUbicacionFacade.buscarPorProyecto(proyectoLicenciaCoa);
				String canton ="";
				if(ubicacion != null && ubicacion.size() > 0){
					canton = ubicacion.get(0).getUbicacionesGeografica().getNombre();
				}
				Locale espanol = new Locale("es","ES");
				SimpleDateFormat fecha= new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", espanol);
				entityInforme.setFechaCompleta(canton+", "+fecha.format(new Date()));
				
				if(consultorNoCalificado.getConsultor().getTipoConsultor().equals("J")){
					entityInforme.setRepresentante(consultorNoCalificado.getNombreRepresentante());
					entityInforme.setCedulaRepresentante(consultorNoCalificado.getCedulaRepresentante());
				}
	
				certificadoParticipacionTmpPdf = UtilGenerarPdf.generarFichero(plantillaReporte.getHtmlPlantilla(), nombreReporte, false, entityInforme);
				if(certificadoParticipacionTmpPdf != null){
					DocumentoEstudioImpacto objDocumento = new DocumentoEstudioImpacto();
					Path path = Paths.get(certificadoParticipacionTmpPdf.getAbsolutePath());
					byte[] archivoInforme = Files.readAllBytes(path);
					objDocumento.setContenidoDocumento(archivoInforme);
					consultorNoCalificado.setAdjuntoCertificado(objDocumento);
				}
			}else{
								
				plantillaReporte = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.EIA_CERTIFICADO_PARTICIPACION_EQUIPO_CONSULTOR);
								
				nombreReporte = "certificado_participacion_"+consultorNoCalificado.getDocumentoIdentificacion();
				
				EntityCertificadoParticipacionRCOA entityInforme = new EntityCertificadoParticipacionRCOA();
				entityInforme.setNombreProyecto(proyectoLicenciaCoa.getNombreProyecto());
				entityInforme.setCodigoProyecto(proyectoLicenciaCoa.getCodigoUnicoAmbiental());
				entityInforme.setNombreOperador(consultorNoCalificado.getNombre());
				entityInforme.setNumeroDocumento(consultorNoCalificado.getDocumentoIdentificacion());
				entityInforme.setNumeroOficio(consultorNoCalificado.getCodigoMae());
				entityInforme.setFormacion(consultorNoCalificado.getFormacion());
				if(consultorNoCalificado.getTipoComponente() != null)
					entityInforme.setComponente(consultorNoCalificado.getTipoComponente().getNombre());
				if(proyectoLicenciaCoa.getAreaResponsable().getTipoArea().getId().equals(3))
					entityInforme.setAreaResponsable(proyectoLicenciaCoa.getAreaResponsable().getAreaAbbreviation());
				List<UbicacionesGeografica> ubicacion = proyectoLicenciaCoaUbicacionFacade.buscarPorProyecto(proyectoLicenciaCoa);
				String canton ="";
				if(ubicacion != null && ubicacion.size() > 0){
					canton = ubicacion.get(0).getUbicacionesGeografica().getNombre();
				}
				Locale espanol = new Locale("es","ES");
				SimpleDateFormat fecha= new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", espanol);
				entityInforme.setFechaCompleta(canton+", "+fecha.format(new Date()));

				certificadoParticipacionTmpPdf = UtilGenerarPdf.generarFichero(plantillaReporte.getHtmlPlantilla(), nombreReporte, false, entityInforme);
				if(certificadoParticipacionTmpPdf != null){
					DocumentoEstudioImpacto objDocumento = new DocumentoEstudioImpacto();
					Path path = Paths.get(certificadoParticipacionTmpPdf.getAbsolutePath());
					byte[] archivoInforme = Files.readAllBytes(path);
					objDocumento.setContenidoDocumento(archivoInforme);
					consultorNoCalificado.setAdjuntoCertificado(objDocumento);
				}
			}
			
			
			if(certificadoParticipacionTmpPdf != null){
				descargarCertificadoParticipacion = true;
				return new DefaultStreamedContent(new FileInputStream(certificadoParticipacionTmpPdf), "application/pdf", nombreReporte+".pdf");
			}
		}catch(IOException e){
			LOG.error("Ocurrió un error durante la firma del certificado", e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}catch(Exception e){
			LOG.error("Ocurrió un error durante la firma del certificado", e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		return null;
	}
	
	/**
	 * firma electronica
	 */
	public boolean verificaToken() {
		if(firmaSoloToken) {
			token = true;
			return token;
		}
		
		token = false;
		Usuario usuario = usuarioFacade.buscarUsuario(consultorNoCalificado.getDocumentoIdentificacion());
		if (usuario != null && usuario.getToken() != null && usuario.getToken())
			token = true;
		return token;
	}

	public void guardarToken() {
		Usuario usuario = usuarioFacade.buscarUsuario(consultorNoCalificado.getDocumentoIdentificacion());
		if(usuario != null) {
			usuario.setToken(token);
			try {
				usuarioFacade.guardar(usuario);
				verificaToken();
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public String firmarDocumento() {
		try {
			if(consultorNoCalificado.getAdjuntoCertificado() == null && consultorNoCalificado.getDocumentoIdentificacion() != null)
				getDocumentoCertificadoConsultor();
			
			if(consultorNoCalificado.getAdjuntoCertificado() != null && consultorNoCalificado.getAdjuntoCertificado().getId() == null) {
				Integer idTablaClass = (consultorNoCalificado.getId() != null) ? consultorNoCalificado.getId() : proyectoLicenciaCoa.getId();
				documetoCertificadoParticipacion = documentosImpactoEstudioFacade.ingresarDocumento(consultorNoCalificado.getAdjuntoCertificado(), idTablaClass, proyectoLicenciaCoa.getCodigoUnicoAmbiental(), TipoDocumentoSistema.EIA_CERTIFICADO_PARTICIPACION_CONSULTOR, "certificado de participacion del consultor"+consultorNoCalificado.getDocumentoIdentificacion()+".pdf", EquipoConsultor.class.getSimpleName(), bandejaTareasBean.getProcessId());
			}else{
				Integer idTablaClass = (consultorNoCalificado.getId() != null) ? consultorNoCalificado.getId() : proyectoLicenciaCoa.getId();
				documetoCertificadoParticipacion = documentosImpactoEstudioFacade.documentoXTablaIdXIdDoc(idTablaClass, EquipoConsultor.class.getSimpleName(), TipoDocumentoSistema.EIA_CERTIFICADO_PARTICIPACION_CONSULTOR);
			}
			if(documetoCertificadoParticipacion != null ) {
				consultorNoCalificado.setAdjuntoCertificado(documetoCertificadoParticipacion);
				String documentOffice = documentosImpactoEstudioFacade.direccionDescarga(documetoCertificadoParticipacion);
				
				if(consultorNoCalificado.getConsultor() != null && consultorNoCalificado.getConsultor().getTipoConsultor().equals("J")){
					companiaConsultora.setAdjuntoCertificado(documetoCertificadoParticipacion);					
					return DigitalSign.sign(documentOffice, consultorNoCalificado.getCedulaRepresentante()); 
					
				}else{
					return DigitalSign.sign(documentOffice, consultorNoCalificado.getDocumentoIdentificacion()); 
				}
				
			}
		} catch (Exception exception) {
			LOG.error("Ocurrió un error durante la firma del certificado", exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		return "";
	}	

	public void uploadListenerDocumentos(FileUploadEvent event) {
		if (descargarCertificadoParticipacion == true) {
			documentoManual=new DocumentoEstudioImpacto();
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoManual.setContenidoDocumento(contenidoDocumento);
			documentoManual.setNombre(event.getFile().getFileName());
			documentoManual.setExtesion(".pdf");		
			documentoManual.setMime("application/pdf");
			documentoManual.setNombreTabla(ProyectoLicenciaCoa.class.getSimpleName());
			documentoManual.setIdTable(proyectoLicenciaCoa.getId());
			subido = true;
			
			JsfUtil.addMessageInfo("Documento subido exitosamente");
			consultorNoCalificado.setAdjuntoCertificado(documentoManual);
			consultorNoCalificado.setDocumentoFirmado(true);
			
			if(consultorNoCalificado.getConsultor() != null && consultorNoCalificado.getConsultor().getTipoConsultor().equals("J")){
				companiaConsultora.setAdjuntoCertificado(documentoManual);
				consultorNoCalificado.setDocumentoFirmado(true);
			}
		} else{
			JsfUtil.addMessageError("No ha descargado el documento para la firma");
		}

	}
	
	public void abrirFirma() {
		documentoManual = null;
		descargarCertificadoParticipacion = false;
		if(consultorNoCalificado.getAdjuntoCertificado() != null && consultorNoCalificado.isDocumentoFirmado())
			documentoManual = consultorNoCalificado.getAdjuntoCertificado();
		
		verificaToken();
		
	    JsfUtil.addCallbackParam("consultorCompleto");
	}
	
	public StreamedContent descargar(DocumentoEstudioImpacto documentoDescarga) throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			
			if (documentoDescarga != null && documentoDescarga.getAlfrescoId() != null) {
				documentoContent = documentosImpactoEstudioFacade.descargar(documentoDescarga.getAlfrescoId());
			} else if (documentoDescarga.getContenidoDocumento() != null) {
				documentoContent = documentoDescarga.getContenidoDocumento();
			}
			
			if (documentoDescarga != null && documentoDescarga.getNombre() != null
					&& documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documentoDescarga.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void validarCedulaRepresentante (){
		if(consultorNoCalificado.getCedulaRepresentante().length() > 13 ){
			JsfUtil.addMessageError("El numero de documento no corresponde a un numero de cédula.");			
			return;
		}
		if(consultorNoCalificado.getCedulaRepresentante() == null || consultorNoCalificado.getCedulaRepresentante().isEmpty()){			
			JsfUtil.addMessageError("El numero de documento es obligatorio.");
			return;
		}
		try{
			
			Cedula cedula = consultaRucCedula.obtenerPorCedulaRC(Constantes.USUARIO_WS_MAE_SRI_RC,
			Constantes.PASSWORD_WS_MAE_SRI_RC, consultorNoCalificado.getCedulaRepresentante());
			if (cedula != null && cedula.getError().equals(Constantes.NO_ERROR_WS_MAE_SRI_RC)) {
				consultorNoCalificado.setNombreRepresentante(cedula.getNombre());
			}else{
				JsfUtil.addMessageInfo("No se encontro el numero de cedula/ruc "+consultorNoCalificado.getCedulaRepresentante());
			}
		}catch(Exception e){
			JsfUtil.addMessageInfo("No se encontro el numero de cedula/ruc "+consultorNoCalificado.getCedulaRepresentante());
		}
	}
	
	public boolean verificarRepresentante(String cedula){
		if(cedula.isEmpty() || cedula.equals("")){
			return false;
		}else
			return true;
	}
	
	public void cerrarFirmar(){
		RequestContext context = RequestContext.getCurrentInstance();
		
		if(consultorNoCalificado != null && consultorNoCalificado.getConsultor() == null){
			context.execute("PF('consultorNoCalificado').show();");			
		}else{
			if(consultorNoCalificado.getConsultor().getTipoConsultor().equals("J")){
				context.execute("PF('consultorNoCalificadoCom').show();");
			}else{			
				context.execute("PF('consultorNoCalificado').show();");
			}
		}
		
		
	}
	
	public StreamedContent descargarCertificadoCom(DocumentoEstudioImpacto documentoDescarga) throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			
			if (documentoDescarga != null && documentoDescarga.getAlfrescoId() != null) {
				documentoContent = documentosImpactoEstudioFacade.descargar(documentoDescarga.getAlfrescoId());
			} else if (documentoDescarga.getContenidoDocumento() != null) {
				documentoContent = documentoDescarga.getContenidoDocumento();
			}
			
			if (documentoDescarga != null && documentoDescarga.getNombre() != null
					&& documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documentoDescarga.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void borrarCompania(){
		consultorSeleccionado = new Consultor();
		companiaConsultora = null;
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('consultorNoCalificadoCom').hide();");
	}
	
	public void seleccionarConsultorCom(Consultor consultor) {
		try {
			mostrarPnlEquipo = false;
			labelEquipoConsultor = "Información Consultor";
			consultorSeleccionado = consultor;
			// verifico si el consultor seleccionado es persona natural o juridica
			if(consultorSeleccionado.getTipoConsultor().equals("J")){
				mostrarPnlEquipo = true;
				labelEquipoConsultor = "Equipo Consultor";
				// busco si ya estaba ingresado uno anteriormente y lo borro				
				listaEquipoConsultorEliminado.addAll(listaEquipoConsultor);
				listaEquipoConsultor = new ArrayList<EquipoConsultor>();

			}else{
				listaEquipoConsultor = new ArrayList<EquipoConsultor>();
			}
			
			
			consultorNoCalificado = new EquipoConsultor();
			consultorNoCalificado.setDocumentoIdentificacion(consultorSeleccionado.getRuc());
			consultorNoCalificado.setNombre(consultorSeleccionado.getNombre());
			consultorNoCalificado.setCodigoMae(consultorSeleccionado.getRegistro());
			consultorNoCalificado.setConsultor(consultorSeleccionado);
			consultorNoCalificado.setTipoComponente(componenteParticipacionPrincipal);
			
			if(consultorSeleccionado.getTipoConsultor().equals("J")){
				Organizacion organizacion = organizacionFacade.buscarPorRuc(consultorSeleccionado.getRuc());
				
				if(organizacion != null) {
					consultorNoCalificado.setNombreRepresentante(organizacion.getPersona().getNombre());
					consultorNoCalificado.setCedulaRepresentante(organizacion.getPersona().getPin());
					companiaConsultora = consultorNoCalificado;
				} else {
					JsfUtil.addMessageError("La compañía consultora "+consultorNoCalificado.getNombre()+" no se encuentra registrada en el sistema de Regularización");
					
					consultorSeleccionado = new Consultor();
					consultorNoCalificado = new EquipoConsultor();
					
					JsfUtil.addCallbackParam("consultorCalificado");
					return;
				}
			}else{
				companiaConsultora = null;
			}
			
			documentoManual = null;
			
			RequestContext context = RequestContext.getCurrentInstance();
			
			if(consultorSeleccionado.getTipoConsultor().equals("J")){
				context.execute("PF('consultorNoCalificadoCom').show();");
			}else{
				context.execute("PF('consultorNoCalificado').show();");
			}
	        
		    JsfUtil.addCallbackParam("consultorCalificado");
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void agregarConsultorNoCalificadoCom() {
				
		if(!consultorNoCalificado.isSeleccionado()){
			
			consultorNoCalificado.setSeleccionado(true);
			companiaConsultora = consultorNoCalificado;		
		}
		
		if(consultorNoCalificado.getAdjuntoCertificado() == null ){
			JsfUtil.addMessageError("Falta firmar el Certificado de Participación en el Estudio de Impacto Ambiental");
			return;
		} else if(!consultorNoCalificado.isDocumentoFirmado()) {
			if (consultorNoCalificado.getAdjuntoCertificado().getAlfrescoId() != null) {
				if(!documentosImpactoEstudioFacade.verificarFirmaVersion(consultorNoCalificado.getAdjuntoCertificado().getAlfrescoId())) {
					JsfUtil.addMessageError("Falta firmar el Certificado de Participación en el Estudio de Impacto Ambiental");
					return;
				}
			} else {
				//para documentos de firma manual si la variable objEquipo.getDocumentoFirmado() no es true es xq solo se genero y no se subio el firmado
				JsfUtil.addMessageError("Falta firmar el Certificado de Participación en el Estudio de Impacto Ambiental");
				return;
			}
		}
		
		JsfUtil.addCallbackParam("addConsultorNoCalificado");
	}
	
	public void abrirConsultor(Consultor consultor){
		if(consultor.getTipoConsultor().equals("J")){
			seleccionarConsultorCom(consultor);		
		}else{
			seleccionarConsultor(consultor);
		}		
	}
	
	public void eliminarEmpresa(){
		try {
			
			if(!listaEquipoConsultor.isEmpty()){
				JsfUtil.addMessageError("No se puede eliminar la compañía porque tiene registrado el equipo consultor.");
				return;
			}
					
			if(companiaConsultora.getId() != null){
				listaEquipoConsultorEliminado.add(companiaConsultora);
			}		
			
			for(EquipoConsultor equipoEliminado : listaEquipoConsultor){
				if(equipoEliminado.getId() != null){
					listaEquipoConsultor.add(equipoEliminado);
				}
			}
			
			companiaConsultora = null;
			
			listaEquipoConsultor = new ArrayList<>();	
			consultorSeleccionado = new Consultor();		
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void validarEquipoConsultor(){
		try {
			if(!listaEquipoConsultor.isEmpty()){
				JsfUtil.addMessageError("No se puede editar la compañía porque tiene registrado el equipo consultor.");
				return;
			}
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('consultorDiag').show();");
			context.execute("PF('tblConsultores').clearFilters()");			
			 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public StreamedContent descargarDocumentoCertificadoConsultor() {
		try{
			DefaultStreamedContent content = new DefaultStreamedContent();
			byte[] contenido = null;
			if (consultorNoCalificado.getAdjuntoCertificado() != null) {
				if (consultorNoCalificado.getAdjuntoCertificado().getAlfrescoId() != null) {
					contenido = documentosImpactoEstudioFacade.descargar(consultorNoCalificado.getAdjuntoCertificado().getAlfrescoId());
				} else 
					contenido = consultorNoCalificado.getAdjuntoCertificado().getContenidoDocumento();
				
				content = new DefaultStreamedContent(new ByteArrayInputStream(contenido), ".pdf");
				content.setName("certificado_participacion_"+consultorNoCalificado.getDocumentoIdentificacion());
				
			} else {
				content = null;
				JsfUtil.addMessageError("Documento no generado.");
			}
			
			return content;
		}catch(Exception e){
			LOG.error("Ocurrió un error durante la descarga del documento", e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		return null;
	}
	
	public void validarActividadCamaronera(){
		try {
			
			List<ProyectoLicenciaCuaCiuu> listaActividadesCiuu = new ArrayList<ProyectoLicenciaCuaCiuu>();
			listaActividadesCiuu = proyectoLicenciaCuaCiuuFacade.actividadesPorProyecto(proyectoLicenciaCoa);
			
			for(ProyectoLicenciaCuaCiuu actividad : listaActividadesCiuu){
				
				if(Constantes.getActividadesCamaroneras().contains(actividad.getCatalogoCIUU().getCodigo())){
					SubActividades subActividad1=actividad.getSubActividad();
					if(subActividad1.getSubActividades().getRequiereValidacionCoordenadas().equals(1)){
						if(proyectoLicenciaCoa.getConcesionCamaronera() == null && proyectoLicenciaCoa.getZona_camaronera() != null){
							esActividadCamaronera = true;	
							setZonaCamaronera(proyectoLicenciaCoa.getZona_camaronera());
						}						
					}					
				}				
			}						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void uploadListenerDocCamaronera(FileUploadEvent event) {		
		documentoDocCamaroneraAlta = new DocumentoEstudioImpacto();
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoDocCamaroneraAlta.setContenidoDocumento(contenidoDocumento);
		documentoDocCamaroneraAlta.setNombre(event.getFile().getFileName());
		documentoDocCamaroneraAlta.setExtesion(".pdf");
		documentoDocCamaroneraAlta.setMime("application/pdf");
		documentoDocCamaroneraAlta.setNombreTabla("RA_CONCESION_MINERA_ALTA");		
	}
	
	public void uploadListenerDocCamaroneraPlaya(FileUploadEvent event) {		
		documentoDocCamaroneraPlaya = new DocumentoEstudioImpacto();
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoDocCamaroneraPlaya.setContenidoDocumento(contenidoDocumento);
		documentoDocCamaroneraPlaya.setNombre(event.getFile().getFileName());
		documentoDocCamaroneraPlaya.setExtesion(".pdf");
		documentoDocCamaroneraPlaya.setMime("application/pdf");
		documentoDocCamaroneraPlaya.setNombreTabla("RA_CONCESION_MINERA_PLAYA");
	}
	
	public void guardarDocCamaroneras(){
		
		if (documentoDocCamaroneraAlta != null && documentoDocCamaroneraAlta.getContenidoDocumento() != null && documentoDocCamaroneraAlta.getId() == null) {
			documentoDocCamaroneraAlta.setIdTable(informacionProyectoEia.getId());
			try {
				documentoDocCamaroneraAlta = documentosImpactoEstudioFacade.guardarDocumentoAlfrescoCA(proyectoLicenciaCoa.getCodigoUnicoAmbiental(), "ESTUDIO_IMPACTO_AMBIENTAL",  
						documentoDocCamaroneraAlta, TipoDocumentoSistema.RCOA_DOCUMENTO_TITULO_CONCESION_CAMARONERA);
			} catch (ServiceException | CmisAlfrescoException e) {
				e.printStackTrace();
			}
		}
		
		if (documentoDocCamaroneraPlaya != null && documentoDocCamaroneraPlaya.getContenidoDocumento() != null && documentoDocCamaroneraPlaya.getId()== null) {
			documentoDocCamaroneraPlaya.setIdTable(informacionProyectoEia.getId());
			try {
				documentoDocCamaroneraPlaya = documentosImpactoEstudioFacade.guardarDocumentoAlfrescoCA(proyectoLicenciaCoa.getCodigoUnicoAmbiental(), "ESTUDIO_IMPACTO_AMBIENTAL",  
						documentoDocCamaroneraPlaya, TipoDocumentoSistema.RCOA_DOCUMENTO_TITULO_CONCESION_CAMARONERA);
			} catch (ServiceException | CmisAlfrescoException e) {
				e.printStackTrace();
			}
		}
	}
				
}