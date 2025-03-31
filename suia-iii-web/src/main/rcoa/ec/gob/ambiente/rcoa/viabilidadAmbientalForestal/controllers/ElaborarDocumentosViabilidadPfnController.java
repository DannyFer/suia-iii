package ec.gob.ambiente.rcoa.viabilidadAmbientalForestal.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.mapa.webservices.GenerarMapaViabilidadPfnWS;
import ec.gob.ambiente.mapa.webservices.GenerarMapaViabilidadPfnWS_Service;
import ec.gob.ambiente.mapa.webservices.ResponseCertificado;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.controllers.UtilViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.DocumentosViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ObservacionesViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.AfectacionCoberturaVegetal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DocumentoViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ObservacionesViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ViabilidadCoa;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ElaborarDocumentosViabilidadPfnController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Logger LOG = Logger.getLogger(ElaborarDocumentosViabilidadPfnController.class);
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@ManagedProperty(value = "#{oficioRevisionViabilidadPfnBean}")
	@Getter
	@Setter
	private OficioRevisionViabilidadPfnBean oficioRevisionViabilidadPfnBean;
	
	@ManagedProperty(value = "#{informeRevisionViabilidadPfnBean}")
	@Getter
	@Setter
	private InformeRevisionViabilidadPfnBean informeRevisionViabilidadPfnBean;
	
	@EJB
	private DocumentosViabilidadFacade documentosFacade;
	@EJB
	private ViabilidadCoaFacade viabilidadCoaFacade;
	@EJB
    private UsuarioFacade usuarioFacade;
	@EJB
    private ProcesoFacade procesoFacade;
	@EJB
    private ObservacionesViabilidadFacade observacionesViabilidadFacade;
	@EJB
    private DocumentosCoaFacade documentoCoaFacade;
	
	
	@Getter
	@Setter
	private ViabilidadCoa viabilidadProyecto;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
	@Setter
	private DocumentoViabilidad documentoInformeAlfresco, informeFirmaManual;
	
	@Getter
	@Setter
	private TipoDocumentoSistema tipoInforme;
	
	@Getter
	@Setter
	private Boolean informeGuardado, token, documentoDescargado, subido;
	
	@Getter
	@Setter
	private Boolean esInformeViabilidad, esProduccion, habilitarFirma;

	@Getter
	@Setter
	private String nombreTipoInforme, nombreTipoOficio, nombreDocumentoFirmado, seccionObservaciones, claseObservaciones, marcoLegal;

	@Getter
	@Setter
	private String urlInforme, nombreInforme, urlAlfresco;

	@Getter
	@Setter
	private byte[] archivoInforme;
	
	@Getter
	@Setter
	private Integer idTarea, numeroRevision;
	
	@Setter
    @Getter
    private int activeIndex = 0;
	
	private GenerarMapaViabilidadPfnWS_Service wsMapas;

	@PostConstruct
	private void iniciar() {
		try {

		esProduccion = Constantes.getPropertyAsBoolean("ambiente.produccion") ;
		
		informeGuardado = false;
		subido = false;
		documentoDescargado = false;
		habilitarFirma = false;
		verificaToken();
		
		idTarea = (int)bandejaTareasBean.getTarea().getTaskId();
		
		numeroRevision = Integer.valueOf((String) oficioRevisionViabilidadPfnBean.getVariables().get("numeroRevisionInformacion"));
		
		nombreTipoInforme = "Informe";
		nombreTipoOficio = "Oficio";
		
		proyectoLicenciaCoa = oficioRevisionViabilidadPfnBean.getProyectoLicenciaCoa();
		viabilidadProyecto = viabilidadCoaFacade.getViabilidadForestalPorProyecto(proyectoLicenciaCoa.getId());
		
		esInformeViabilidad = informeRevisionViabilidadPfnBean.getEsInformeViabilidad();
		
		if(informeRevisionViabilidadPfnBean.getEjecutarSeleccion()) {
			seleccionarTipoPronunciamiento();
		}
		
		validarServicios();
		
		generarInforme(true);
		
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/pages/rcoa/viabilidadAmbientalForestal/elaborarDocumentos.jsf");
	}
	
	public boolean verificaToken() {
		if(esProduccion) {
			token = true;
			return token;
		}
		
		token = false;
		if (JsfUtil.getLoggedUser().getToken() != null
				&& JsfUtil.getLoggedUser().getToken())
			token = true;
		return token;
	}

	public void guardarToken() {
		Usuario usuario = JsfUtil.getLoggedUser();
		usuario.setToken(token);
		try {
			usuarioFacade.guardar(usuario);
			verificaToken();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void generarInforme(Boolean marcaAgua) {
		informeRevisionViabilidadPfnBean.generarInforme(marcaAgua);
		
		urlInforme = informeRevisionViabilidadPfnBean.getInformeInspeccion().getInformePath();
		nombreInforme = informeRevisionViabilidadPfnBean.getInformeInspeccion().getNombreReporte();
		archivoInforme = informeRevisionViabilidadPfnBean.getInformeInspeccion().getArchivoInforme();
	}
	
	public StreamedContent getStream(Integer tipo) throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		
		switch (tipo) {
		case 1:
			if (archivoInforme != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(
						archivoInforme), "application/octet-stream");
				content.setName(informeRevisionViabilidadPfnBean.getInformeInspeccion().getNombreFichero());
			}
			break;
		case 2:
			if (oficioRevisionViabilidadPfnBean.getOficioRevision().getArchivoOficio() != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(
						oficioRevisionViabilidadPfnBean.getOficioRevision().getArchivoOficio()), "application/octet-stream");
				content.setName(oficioRevisionViabilidadPfnBean.getOficioRevision().getNombreFichero());
			}
			break;
		default:
			break;
		}
		
		return content;
	}
	
	public void siguiente() {
        setActiveIndex(1);
        habilitarFirma = false;
	}
	
	public void atras() {
        setActiveIndex(0);
        habilitarFirma = false;
	}
	
	public void seleccionarTipoPronunciamiento() {
		nombreTipoInforme = (esInformeViabilidad) ? "Informe de viabilidad" : "Informe de observaciones";
		nombreTipoOficio = (esInformeViabilidad) ? "Memorando" : "Oficio de observaciones";
		
		seccionObservaciones = "Revision informe y oficio de observaciones PFN";
		claseObservaciones = "revisionInformeOficioObservacionesPfn_" + numeroRevision;
		
		tipoInforme = TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_INFORME_OBSERVACIONES;
		oficioRevisionViabilidadPfnBean.setTipoOficio(TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_OFICIO_OBSERVACIONES);
		if(esInformeViabilidad) {
			tipoInforme = TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_INFORME_VIABILIDAD;
			oficioRevisionViabilidadPfnBean.setTipoOficio(TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_MEMORANDO_VIABILIDAD);
			
			seccionObservaciones = "Revision informe y memorando de viabilidad PFN";
			claseObservaciones = "revisionInformeMemoViabilidadPfn_" + numeroRevision;
		}
		
		informeRevisionViabilidadPfnBean.setNombreTipoInforme(nombreTipoInforme);
		informeRevisionViabilidadPfnBean.setEsInformeViabilidad(esInformeViabilidad);
		generarInforme(true);
		
		oficioRevisionViabilidadPfnBean.setNombreTipoOficio(nombreTipoOficio);
		oficioRevisionViabilidadPfnBean.generarOficio(true);
		
		RequestContext.getCurrentInstance().update("form:tab:pnlDocumentoOficio");
	}
	
	public Boolean validarObservacionesPc() {
		Boolean existe = false;
		try {
			List<ObservacionesViabilidad> observacionesInforme = observacionesViabilidadFacade.listarPorIdClaseNombreClaseNoCorregidas(
							viabilidadProyecto.getId(), "revisionInformeViabilidadPfnPc");
			
			if(observacionesInforme.size() > 0) {
				existe = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return existe;
	}
	
	public void validateDatosIngresoInforme(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		if(informeRevisionViabilidadPfnBean.getInformeInspeccion().getAntecedentes() == null 
				|| informeRevisionViabilidadPfnBean.getInformeInspeccion().getAntecedentes().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El campo 'Antecedentes' es requerido.", null));
		
		if(informeRevisionViabilidadPfnBean.getInformeInspeccion().getMarcoLegal() == null 
				|| informeRevisionViabilidadPfnBean.getInformeInspeccion().getMarcoLegal().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El campo 'Marco Legal' es requerido.", null));
		
		if(informeRevisionViabilidadPfnBean.getInformeInspeccion().getObjetivo() == null 
				|| informeRevisionViabilidadPfnBean.getInformeInspeccion().getObjetivo().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El campo 'Objetivo' es requerido.", null));
		
		if(viabilidadProyecto.getRequiereInspeccionTecnica()) {
			if (informeRevisionViabilidadPfnBean.getListaDelegadosOperador() == null 
					|| informeRevisionViabilidadPfnBean.getListaDelegadosOperador().isEmpty())
				errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El campo 'Delegados del operador para inspección' es requerido.", null));
			
			if (informeRevisionViabilidadPfnBean.getListaTecnicosDelegados() == null 
					|| informeRevisionViabilidadPfnBean.getListaTecnicosDelegados().isEmpty())
				errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El campo 'Equipo técnico delegado para la inspección' es requerido.", null));
			
			if (informeRevisionViabilidadPfnBean.getListaCoordenadas() == null 
					|| informeRevisionViabilidadPfnBean.getListaCoordenadas().isEmpty())
				errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El campo 'Coordenadas de sitios de referencia' es requerido.", null));
			
			if(informeRevisionViabilidadPfnBean.getInformeInspeccion().getEsMuestreoCenso() != null && 
					informeRevisionViabilidadPfnBean.getInformeInspeccion().getEsMuestreoCenso()) {
				if (informeRevisionViabilidadPfnBean.getListaEspeciesMuestreoCenso() == null 
						|| informeRevisionViabilidadPfnBean.getListaEspeciesMuestreoCenso().isEmpty())
					errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El campo 'Muestreo o censo' es requerido.", null));
			}
			
			if(informeRevisionViabilidadPfnBean.getInformeInspeccion().getEsCaracterizacionCualitativa() != null && 
					informeRevisionViabilidadPfnBean.getInformeInspeccion().getEsCaracterizacionCualitativa()) {
				if (informeRevisionViabilidadPfnBean.getListaEspeciesCaracterizacionCualitativa() == null 
						|| informeRevisionViabilidadPfnBean.getListaEspeciesCaracterizacionCualitativa().isEmpty())
					errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El campo 'Caracterización cualitativa' es requerido.", null));
			}
			
			if (informeRevisionViabilidadPfnBean.getListaFotosRecorrido() == null 
					|| informeRevisionViabilidadPfnBean.getListaFotosRecorrido().isEmpty()) {
				errorMessages.add(new FacesMessage(
								FacesMessage.SEVERITY_ERROR, "El campo 'Fotografías del recorrido' es requerido.", null));
			}
		}
		
		if(informeRevisionViabilidadPfnBean.getListaAfectacionCoberturaVegetal() != null 
				&& informeRevisionViabilidadPfnBean.getListaAfectacionCoberturaVegetal().size() > 0) {
			for (AfectacionCoberturaVegetal item : informeRevisionViabilidadPfnBean.getListaAfectacionCoberturaVegetal()) {
				if(item.getSuperficieDescripcion() == null || item.getSuperficieDescripcion().isEmpty()
					|| item.getAfectacion() == null) {
					errorMessages.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "Complete la información de la sección 'Tipo de cobertura vegetal'.", null));
					break;
				}
			}
		}
		
		if(informeRevisionViabilidadPfnBean.getListaAfectacionEcosistemas() != null 
				&& informeRevisionViabilidadPfnBean.getListaAfectacionEcosistemas().size() > 0) {
			for (AfectacionCoberturaVegetal item : informeRevisionViabilidadPfnBean.getListaAfectacionEcosistemas()) {
				if(item.getSuperficieDescripcion() == null || item.getSuperficieDescripcion().isEmpty()
						|| item.getAfectacion() == null) {
					errorMessages.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "Complete la información de la sección 'Ecosistemas del área del proyecto'.", null));
					break;
				}
			}
		}
		
		if(informeRevisionViabilidadPfnBean.getListaAfectacionConvenios() != null 
				&& informeRevisionViabilidadPfnBean.getListaAfectacionConvenios().size() > 0) {
			for (AfectacionCoberturaVegetal item : informeRevisionViabilidadPfnBean.getListaAfectacionConvenios()) {
				if(item.getAfectacion() == null) {
					errorMessages.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "Complete la información de la sección 'Afectación a áreas bajo convenios'.", null));
					break;
				}
			}
		}
		
		if(informeRevisionViabilidadPfnBean.getInformeInspeccion().getHabilitarUnidadesHidrograficas() != null 
				&& informeRevisionViabilidadPfnBean.getInformeInspeccion().getHabilitarUnidadesHidrograficas()) {
			if (informeRevisionViabilidadPfnBean.getListaUnidadesHidrografica() == null 
					|| informeRevisionViabilidadPfnBean.getListaUnidadesHidrografica().isEmpty()) {
				errorMessages.add(new FacesMessage(
								FacesMessage.SEVERITY_ERROR, "El campo 'Identificación de unidades hidrográficas' es requerido.", null));
			}
		}
		
		if(informeRevisionViabilidadPfnBean.getInformeInspeccion().getHabilitarDescripcionActividades() != null
				&& informeRevisionViabilidadPfnBean.getInformeInspeccion().getHabilitarDescripcionActividades()) {
			if(informeRevisionViabilidadPfnBean.getInformeInspeccion().getDescripcionActividades() == null 
					|| informeRevisionViabilidadPfnBean.getInformeInspeccion().getDescripcionActividades().isEmpty())
				errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El campo 'Descripción de actividades del proyecto' es requerido.", null));
		}
		
		if(informeRevisionViabilidadPfnBean.getInformeInspeccion().getConclusiones() == null 
				|| informeRevisionViabilidadPfnBean.getInformeInspeccion().getConclusiones().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El campo 'Conclusiones' es requerido.", null));
		
		if(informeRevisionViabilidadPfnBean.getInformeInspeccion().getRecomendaciones()== null 
				|| informeRevisionViabilidadPfnBean.getInformeInspeccion().getRecomendaciones().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El campo 'Recomendaciones' es requerido.", null));

		if(context.getMessageList().size() > 0 || errorMessages.size() > 0)
			informeGuardado = false;
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public void validateDatosIngresoOficio(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		if(oficioRevisionViabilidadPfnBean.getOficioRevision().getAntecedentes() == null || 
				oficioRevisionViabilidadPfnBean.getOficioRevision().getAntecedentes().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Antecedente' es requerido", null));
		
		if(oficioRevisionViabilidadPfnBean.getOficioRevision().getMarcoLegal()== null || 
				oficioRevisionViabilidadPfnBean.getOficioRevision().getMarcoLegal().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Marco Legal' es requerido", null));
		
		if(oficioRevisionViabilidadPfnBean.getOficioRevision().getConclusiones() == null || 
				oficioRevisionViabilidadPfnBean.getOficioRevision().getConclusiones().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Conclusiones/Observaciones' es requerido", null));
		
		if(oficioRevisionViabilidadPfnBean.getOficioRevision().getRecomendaciones()== null || 
				oficioRevisionViabilidadPfnBean.getOficioRevision().getRecomendaciones().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Recomendaciones' es requerido", null));
		
		if(!esInformeViabilidad && (oficioRevisionViabilidadPfnBean.getOficioRevision().getPronunciamiento()== null || 
				oficioRevisionViabilidadPfnBean.getOficioRevision().getPronunciamiento().isEmpty()))
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"El campo 'Pronunciamiento' es requerido", null));
		
		if(context.getMessageList().size() > 0 || errorMessages.size() > 0)
			habilitarFirma = false;
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public void guardarInforme() {
		try {
			informeRevisionViabilidadPfnBean.guardarDatosInforme();
			
			generarInforme(true);
			
			informeGuardado = true;
			
			RequestContext.getCurrentInstance().update("form:tab:pnlInforme");
			
			JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public StreamedContent descargar() throws IOException {
		DefaultStreamedContent content = null;
		try {
			
			byte[] documentoContent = informeRevisionViabilidadPfnBean.getInformeInspeccion().getArchivoInforme();
			
			if (documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(informeRevisionViabilidadPfnBean.getInformeInspeccion().getNombreFichero());
			} else
				JsfUtil.addMessageError("Error al generar el archivo");
			
			documentoDescargado = true;
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void guardarOficio() {
		try {
			oficioRevisionViabilidadPfnBean.guardarOficio();
			
			oficioRevisionViabilidadPfnBean.generarOficio(true);
			
			habilitarFirma = true;
			
			RequestContext.getCurrentInstance().update("form:tab:pnlDocumentoOficio");
			
			JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
		} catch (Exception e) {
			habilitarFirma = false;
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	public void guardarDocumentosFirma() {
		try {
			
			List<DocumentoViabilidad> documentoImagenMapa = documentosFacade
					.getDocumentoXTablaIdXIdDoc(informeRevisionViabilidadPfnBean.getInformeInspeccion().getId(), 
							TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_IMAGEN_MAPA_RECORRIDO, "Informe viabilidad PFN");
			
			if(viabilidadProyecto.getRequiereInspeccionTecnica()
					&& (documentoImagenMapa == null || documentoImagenMapa.size() == 0)) {
				DocumentoViabilidad documentoMapa = generarNuevoMapa();
				
				if(documentoMapa == null || documentoMapa.getId() == null) {
					JsfUtil.addMessageError("Error al generar el documento del informe.");
					return;
				}
			}
			
			informeRevisionViabilidadPfnBean.guardarDatosInforme();
			oficioRevisionViabilidadPfnBean.guardarOficio();
			
			generarInforme(false);
			
			String nroInforme = informeRevisionViabilidadPfnBean.getInformeInspeccion().getNumeroInforme();
			
			documentoInformeAlfresco = new DocumentoViabilidad();
			documentoInformeAlfresco.setNombre(nombreTipoInforme + " " + UtilViabilidad.getFileNameEscaped(nroInforme.replace("/", "-")) + ".pdf");
			documentoInformeAlfresco.setContenidoDocumento(informeRevisionViabilidadPfnBean.getInformeInspeccion().getArchivoInforme());
			documentoInformeAlfresco.setMime("application/pdf");
			documentoInformeAlfresco.setIdTipoDocumento(tipoInforme.getIdTipoDocumento());
			documentoInformeAlfresco.setIdViabilidad(viabilidadProyecto.getId());

			documentoInformeAlfresco = documentosFacade.guardarDocumentoProceso(
					proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
					"VIABILIDAD_AMBIENTAL", documentoInformeAlfresco, 2, JsfUtil.getCurrentProcessInstanceId());
			
			if(documentoInformeAlfresco.getId() != null) {
				String documentOffice = documentosFacade.direccionDescarga(documentoInformeAlfresco);
				urlAlfresco = DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre());
			} else {
				LOG.error("No se encontro el informe forestal");
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
				return;
			}
			
			RequestContext.getCurrentInstance().update("formDialogs:");
	        RequestContext context = RequestContext.getCurrentInstance();
	        context.execute("PF('signDialog').show();");
	        
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			return;
		}
	}
	
	public void uploadListenerDocumentos(FileUploadEvent event) {
		if(documentoDescargado) {
	        byte[] contenidoDocumento = event.getFile().getContents();
	        
	        nombreDocumentoFirmado = event.getFile().getFileName();
	        
	        informeFirmaManual = new DocumentoViabilidad();
	        informeFirmaManual.setId(null);
	        informeFirmaManual.setContenidoDocumento(contenidoDocumento);
	        informeFirmaManual.setNombre(event.getFile().getFileName());
	        informeFirmaManual.setMime("application/pdf");
	        informeFirmaManual.setIdViabilidad(viabilidadProyecto.getId());
	        informeFirmaManual.setIdTipoDocumento(tipoInforme.getIdTipoDocumento());
	        
	        subido = true;
		} else{
			JsfUtil.addMessageError("No ha realizado la descarga del documento");
		}
    }

	public void finalizar() {
		try {
			
			if(subido){
				documentoInformeAlfresco = documentosFacade.guardarDocumentoProceso(
						proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
						"VIABILIDAD_AMBIENTAL", informeFirmaManual, 2, JsfUtil.getCurrentProcessInstanceId());
			}
			
			if (token) {
				String idAlfresco = documentoInformeAlfresco.getIdAlfresco();

				if (!documentosFacade.verificarFirmaVersion(idAlfresco)) {
					JsfUtil.addMessageError("El informe no está firmado electrónicamente.");
					return;
				}
			} else if (!token && !subido) {
				JsfUtil.addMessageError("Debe adjuntar el informe firmado.");
				return;
			}
			
			try {
				Map<String, Object> parametros = new HashMap<>();
				
				if(esInformeViabilidad) {
					parametros.put("requiereAclaratoria", false);
				} else {
					parametros.put("requiereAclaratoria", true);
				}
				
				parametros.put("autoridadAmbiental", oficioRevisionViabilidadPfnBean.getUsuarioAutoridad().getNombre());
				
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros); 
				
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);

				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
				
			} catch (Exception e) {
				e.printStackTrace();
				JsfUtil.addMessageError("Ocurrió un error al enviar la tarea.");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
	}
	
	public boolean validarServicios()
	{
		boolean estado=false;
		try {			
			wsMapas = new GenerarMapaViabilidadPfnWS_Service(new URL(Constantes.getGenerarMapaViabilidadWS()));
			estado=true;
		} catch (WebServiceException e) {
			estado=false;
			e.printStackTrace();
			System.out.println("Servicio no disponible ---> "+Constantes.getGenerarMapaViabilidadWS());			
		} catch (MalformedURLException e) {
			estado=false;
		}
		return estado;
	}
	
	public StreamedContent generarMapaRecorrido () {
		DefaultStreamedContent content = null;
		
		try {
			String idAlfresco = null;
			String nombreDocumento = "Mapa proyecto.pdf";
			if(viabilidadProyecto.getRequiereInspeccionTecnica()) {
				DocumentoViabilidad documentoMapa = generarNuevoMapa();
				idAlfresco = documentoMapa.getIdAlfresco();
				nombreDocumento = documentoMapa.getNombre();
			} else {
				List<DocumentosCOA> docsMapas = documentoCoaFacade.documentoXTablaIdXIdDoc(proyectoLicenciaCoa.getId(), TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_MAPA, "ProyectoLicenciaCoa");
				if (docsMapas.size() > 0) {
					idAlfresco = docsMapas.get(0).getIdAlfresco();
				}
			}
			
			if (idAlfresco != null) {
				byte[] documentoContent = null;
				documentoContent = documentosFacade.descargar(idAlfresco);
				
				if (documentoContent != null) {
					content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
					content.setName(nombreDocumento);
				} else
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return content;
	}
	
	public DocumentoViabilidad generarNuevoMapa() throws Exception {
		ResponseCertificado resCer = new ResponseCertificado();
		
		GenerarMapaViabilidadPfnWS oj = wsMapas.getGenerarMapaViabilidadPfnWSPort();
		((BindingProvider)oj).getRequestContext().put("javax.xml.ws.client.connectionTimeout", 60000); //1 minuto conexion
		((BindingProvider)oj).getRequestContext().put("javax.xml.ws.client.receiveTimeout", 180000); //3 minutos respuesta
		resCer = oj.generarMapaRecorrido(proyectoLicenciaCoa.getCodigoUnicoAmbiental());
		
		if (resCer.getWorkspaceAlfresco() == null) {
			JsfUtil.addMessageError("Ocurrió un error al generar el mapa.");
		} else {
			String[] idsDocumentos = resCer.getWorkspaceAlfresco().split("#");
			
			DocumentoViabilidad documentoMapa = new DocumentoViabilidad();
			documentoMapa.setId(null);
	        documentoMapa.setIdAlfresco(idsDocumentos[0]);
	        documentoMapa.setNombre("Mapa recorrido.pdf");
	        documentoMapa.setMime("application/pdf");
	        documentoMapa.setIdViabilidad(viabilidadProyecto.getId());
	        documentoMapa.setIdTabla(informeRevisionViabilidadPfnBean.getInformeInspeccion().getId());
	        documentoMapa.setNombreTabla("Informe viabilidad PFN");
	        documentoMapa.setIdTipoDocumento(TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_MAPA_RECORRIDO.getIdTipoDocumento());
	        
	        documentoMapa = documentosFacade.guardar(documentoMapa);
	        
	        DocumentoViabilidad documentoImagenMapa = new DocumentoViabilidad();
			documentoImagenMapa.setId(null);
	        documentoImagenMapa.setIdAlfresco(idsDocumentos[1]);
	        documentoImagenMapa.setNombre("Imagen mapa recorrido.pdf");
	        documentoImagenMapa.setMime("application/pdf");
	        documentoImagenMapa.setIdViabilidad(viabilidadProyecto.getId());
	        documentoImagenMapa.setIdTabla(informeRevisionViabilidadPfnBean.getInformeInspeccion().getId());
	        documentoImagenMapa.setNombreTabla("Informe viabilidad PFN");
	        documentoImagenMapa.setIdTipoDocumento(TipoDocumentoSistema.RCOA_VIABILIDAD_PFN_II_IMAGEN_MAPA_RECORRIDO.getIdTipoDocumento());
	        
	        documentoImagenMapa = documentosFacade.guardar(documentoImagenMapa);
	        
	        if(documentoMapa != null && documentoMapa.getId() != null
	        		&& documentoImagenMapa != null && documentoImagenMapa.getId() != null) {
	        	return documentoMapa;
	        }
		}
		
		return null;
	}
	
}
