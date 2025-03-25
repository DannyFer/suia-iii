package ec.gob.ambiente.prevencion.categoriaii.mineria.controller;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.CategoriaIILicencia;
import ec.gob.ambiente.suia.domain.ComponenteSocialSD;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.DocumentosTareasProceso;
import ec.gob.ambiente.suia.domain.PerforacionExplorativa;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.ComponenteSocialSDFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.FichaAmbientalMineria020Facade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ComponenteSocialSDController implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@EJB
	private ComponenteSocialSDFacade componenteSocialFacade;
	
	@EJB
	private FichaAmbientalMineria020Facade fichaAmbientalMineriaFacade;
	
	@EJB
    private DocumentosCoaFacade documentosRcoaFacade;
	
	@Getter
	@Setter
	private ComponenteSocialSD componenteSocial;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{proyectosBean}")
    private ProyectosBean proyectosBean;
    
    @Getter
    @Setter
    private Integer perforacionExplorativa;
    
    @Getter
    @Setter
    private Documento documentoComunidades, documentoPredios, documentoInfraestructura, documentoInfraestructuraSalud;
    
    @Getter
    @Setter
    private Documento documentoLegal, documentoregistroFotografico, documentoMapa, documentoPoliza, poliza ,factura;
    
    @Getter
    @Setter
    private boolean documentoFichaDownload=false, facturaDownload=false, documentoResolucionDownload=false, documentoPolizaDownload=false, polizaDownload=false;
    
    @Getter
    @Setter
    private Documento documentoResolucion, documentoFicha;
    
	@EJB
	private FichaAmbientalMineria020Facade fichaAmbientalMineria020Facade;
	
	@EJB
	private DocumentosFacade documentosFacade;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{componenteFisicoController}")
    private ComponenteFisicoController componenteFisicoController;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{componenteBioticoSDController}")
    private ComponenteBioticoSDController componenteBioticoSDController;
	
	@Setter
    @Getter
	private Boolean esProyectoRcoa;
	
	@Setter
    @Getter
    private CategoriaIILicencia categoriaIILicencia;
	
	@Setter
    @Getter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	@Setter
    @Getter
	private List<DocumentosCOA> listaTituloMinero = new ArrayList<DocumentosCOA>();
	@Setter
    @Getter
	private List<DocumentosCOA> listaGarantia = new ArrayList<DocumentosCOA>();
	@Setter
    @Getter
	private List<DocumentosCOA> listaDeclaracion = new ArrayList<DocumentosCOA>();
	@Setter
    @Getter
	private List<DocumentosCOA> listaFacturas = new ArrayList<DocumentosCOA>();
	@Setter
    @Getter
	private List<DocumentosCOA> listaCartografia = new ArrayList<DocumentosCOA>();
	@Setter
    @Getter
	private List<DocumentosCOA> listaDocumentosEliminar  = new ArrayList<DocumentosCOA>();
	@Setter
    @Getter
	private DocumentosCOA areaEstudio, ubicacion, tipoClima, pisos;

	@PostConstruct
	public void init() {
		try {
			esProyectoRcoa = false;
			if (proyectosBean.getProyectoRcoa() != null	&& proyectosBean.getProyectoRcoa().getId() != null) {
				esProyectoRcoa = true;
				proyectoLicenciaCoa = proyectosBean.getProyectoRcoa();

				perforacionExplorativa = fichaAmbientalMineria020Facade.cargarPerforacionExplorativaRcoa(proyectoLicenciaCoa.getId()).getId();
			} else {
				perforacionExplorativa = fichaAmbientalMineria020Facade.cargarPerforacionExplorativa(proyectosBean.getProyecto()).getId();
			}

			componenteSocial = componenteSocialFacade.buscarComponenteSocialPorPerforacionExplorativa(perforacionExplorativa);
			if (componenteSocial == null) {
				componenteSocial = new ComponenteSocialSD();
				componenteSocial.setPerforacionExplorativa(perforacionExplorativa);
			}

			if (!esProyectoRcoa)
				recuperaAnexos();
			else
				recuperaAnexosRcoa();
			
			verAnexos();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void verAnexos() throws CmisAlfrescoException {
		listaTituloMinero = documentosRcoaFacade.documentoXTablaIdXIdDoc(proyectoLicenciaCoa.getId(),TipoDocumentoSistema.TITULO_MINERO_020,CategoriaIILicencia.class.getSimpleName());
		listaGarantia = documentosRcoaFacade.documentoXTablaIdXIdDoc(proyectoLicenciaCoa.getId(),TipoDocumentoSistema.GARANTIA_020_020,CategoriaIILicencia.class.getSimpleName());
		listaDeclaracion = documentosRcoaFacade.documentoXTablaIdXIdDoc(proyectoLicenciaCoa.getId(),TipoDocumentoSistema.DECLARACION_020,CategoriaIILicencia.class.getSimpleName());
		listaFacturas = documentosRcoaFacade.documentoXTablaIdXIdDoc(proyectoLicenciaCoa.getId(),TipoDocumentoSistema.FACTURAS_020,CategoriaIILicencia.class.getSimpleName());
		
		areaEstudio = documentosRcoaFacade.documentoXTablaIdXIdDocUno(proyectoLicenciaCoa.getId(),TipoDocumentoSistema.AREA_ESTUDIO_020,CategoriaIILicencia.class.getSimpleName());
		ubicacion = documentosRcoaFacade.documentoXTablaIdXIdDocUno(proyectoLicenciaCoa.getId(),TipoDocumentoSistema.UBICACION_020,CategoriaIILicencia.class.getSimpleName());
		tipoClima = documentosRcoaFacade.documentoXTablaIdXIdDocUno(proyectoLicenciaCoa.getId(),TipoDocumentoSistema.GEOLOGICO_020,CategoriaIILicencia.class.getSimpleName());
		pisos = documentosRcoaFacade.documentoXTablaIdXIdDocUno(proyectoLicenciaCoa.getId(),TipoDocumentoSistema.GEOMORFOLOGICO_020,CategoriaIILicencia.class.getSimpleName());
	}
	
	public void guardarDatos(){
		try {
			componenteFisicoController.guardarDatos();
			
			componenteBioticoSDController.guardarDatos();
			
			componenteSocialFacade.guardar(componenteSocial);			
			
			JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void guardarDatosAnexos(){
		try {
			long idProcessInstance = bandejaTareasBean.getTarea().getProcessInstanceId();
					
			for (DocumentosCOA documento : listaTituloMinero) {
				if (documento.getId() == null) {
					documento.setIdTabla(proyectoLicenciaCoa.getId());
					documento = documentosRcoaFacade.guardarDocumentoAlfresco(proyectoLicenciaCoa.getCodigoUnicoAmbiental(), "LicenciaAmbientalCategoriaII", idProcessInstance, documento, TipoDocumentoSistema.TITULO_MINERO_020);
				}
			}
			for (DocumentosCOA documento : listaGarantia) {
				if (documento.getId() == null) {
					documento.setIdTabla(proyectoLicenciaCoa.getId());
					documento = documentosRcoaFacade.guardarDocumentoAlfresco(proyectoLicenciaCoa.getCodigoUnicoAmbiental(), "LicenciaAmbientalCategoriaII", idProcessInstance, documento, TipoDocumentoSistema.GARANTIA_020_020);
				}
			}
			for (DocumentosCOA documento : listaDeclaracion) {
				if (documento.getId() == null) {
					documento.setIdTabla(proyectoLicenciaCoa.getId());
					documento = documentosRcoaFacade.guardarDocumentoAlfresco(proyectoLicenciaCoa.getCodigoUnicoAmbiental(), "LicenciaAmbientalCategoriaII", idProcessInstance, documento, TipoDocumentoSistema.DECLARACION_020);
				}
			}
			for (DocumentosCOA documento : listaFacturas) {
				if (documento.getId() == null) {
					documento.setIdTabla(proyectoLicenciaCoa.getId());
					documento = documentosRcoaFacade.guardarDocumentoAlfresco(proyectoLicenciaCoa.getCodigoUnicoAmbiental(), "LicenciaAmbientalCategoriaII", idProcessInstance, documento, TipoDocumentoSistema.FACTURAS_020);
				}
			}
			
			if (areaEstudio.getId() == null){
				areaEstudio.setIdTabla(proyectoLicenciaCoa.getId());
				areaEstudio = documentosRcoaFacade.guardarDocumentoAlfresco(proyectoLicenciaCoa.getCodigoUnicoAmbiental(), "LicenciaAmbientalCategoriaII", idProcessInstance, areaEstudio, TipoDocumentoSistema.AREA_ESTUDIO_020);
			}
			
			if (ubicacion.getId() == null){
				ubicacion.setIdTabla(proyectoLicenciaCoa.getId());
				ubicacion = documentosRcoaFacade.guardarDocumentoAlfresco(proyectoLicenciaCoa.getCodigoUnicoAmbiental(), "LicenciaAmbientalCategoriaII", idProcessInstance, ubicacion, TipoDocumentoSistema.UBICACION_020);
			}
			
			if (tipoClima.getId() == null){
				tipoClima.setIdTabla(proyectoLicenciaCoa.getId());
				tipoClima = documentosRcoaFacade.guardarDocumentoAlfresco(proyectoLicenciaCoa.getCodigoUnicoAmbiental(), "LicenciaAmbientalCategoriaII", idProcessInstance, tipoClima, TipoDocumentoSistema.GEOLOGICO_020);
			}
			
			if (pisos.getId() == null){
				pisos.setIdTabla(proyectoLicenciaCoa.getId());
				pisos = documentosRcoaFacade.guardarDocumentoAlfresco(proyectoLicenciaCoa.getCodigoUnicoAmbiental(), "LicenciaAmbientalCategoriaII", idProcessInstance, pisos, TipoDocumentoSistema.GEOMORFOLOGICO_020);
			}
			
			if(validarDocumentosAnexos().size() > 0){
				JsfUtil.addMessageError(validarDocumentosAnexos());
				return;
			}
						
//			if(!esProyectoRcoa)
//				salvarDocumentoAnexos();
//			else
//				salvarDocumentoAnexosRcoa();
			
			JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public StreamedContent getStreamContent(String tipo) throws Exception {
		DefaultStreamedContent content = null;
		try {

			if (tipo.equals("DocumentoComunidades")) {
				if (documentoComunidades != null && documentoComunidades.getNombre() != null
						&& documentoComunidades.getContenidoDocumento() != null) {
					content = new DefaultStreamedContent(new ByteArrayInputStream(documentoComunidades.getContenidoDocumento()));
					content.setName(documentoComunidades.getNombre());
				} else
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
			} else if (tipo.equals("DocumentoPredios")){
				if (documentoPredios != null && documentoPredios.getNombre() != null && documentoPredios.getContenidoDocumento() != null) {
					content = new DefaultStreamedContent(new ByteArrayInputStream(documentoPredios.getContenidoDocumento()));
					content.setName(documentoPredios.getNombre());
				} else
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
			}else if (tipo.equals("DocumentoInfraestructura")){
				if (documentoInfraestructura != null && documentoInfraestructura.getNombre() != null && documentoInfraestructura.getContenidoDocumento() != null) {
					content = new DefaultStreamedContent(new ByteArrayInputStream(documentoInfraestructura.getContenidoDocumento()));
					content.setName(documentoInfraestructura.getNombre());
				} else
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
			}else if (tipo.equals("DocumentoInfraestructuraSalud")){
				if (documentoInfraestructuraSalud != null && documentoInfraestructuraSalud.getNombre() != null && documentoInfraestructuraSalud.getContenidoDocumento() != null) {
					content = new DefaultStreamedContent(new ByteArrayInputStream(documentoInfraestructuraSalud.getContenidoDocumento()));
					content.setName(documentoInfraestructuraSalud.getNombre());
				} else
					JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
			}

		} catch (Exception exception) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
		}
		return content;
	}
			
	public List<String> validarDocumentos(){
		
		List<String> listaMensajes = new ArrayList<>();
    			
    	if(documentoComunidades == null || documentoComunidades.getContenidoDocumento() == null)
    		listaMensajes.add("Debe ingresar el documento para Comunidades");
    	
    	if(documentoPredios == null || documentoPredios.getContenidoDocumento() == null)
    		listaMensajes.add("Debe ingresar el documento para Predios");
    	
    	if(documentoInfraestructura == null || documentoInfraestructura.getContenidoDocumento() == null)
    		listaMensajes.add("Debe ingresar el documento para Infraestructura");
    	
    	if(documentoInfraestructuraSalud == null || documentoInfraestructuraSalud.getContenidoDocumento() == null)
    		listaMensajes.add("Debe ingresar el documento para Infraestructura de Salud Pública");
		
		return listaMensajes;
	}
	
	public List<String> validarDocumentosAnexos(){

		List<String> listaMensajes = new ArrayList<>();

		if (listaTituloMinero.isEmpty()) {
			listaMensajes.add("Debe adjuntar Título minero.");
        }
		if (listaGarantia.isEmpty()) {
			listaMensajes.add("Debe adjuntar Garantía bancaria / Póliza bancaria.");
        }
		if (listaDeclaracion.isEmpty()) {
			listaMensajes.add("Debe adjuntar Declaración Juramentada.");
        }
		if (listaFacturas.isEmpty()) {
			listaMensajes.add("Debe adjuntar Facturas de pagos administrativos.");
        }
		
		
		if (areaEstudio == null) {
			listaMensajes.add("Debe adjuntar Área de Estudio (Base).");
        }
		if (ubicacion == null) {
			listaMensajes.add("Debe adjuntar Ubicación Político Administrativa.");
        }
		if (tipoClima == null) {
			listaMensajes.add("Debe adjuntar Tipo de Clima (con las(s) estación(es) meteorológicas utilizadas).");
        }
		if (pisos == null) {
			listaMensajes.add("Debe adjuntar Pisos Bioclimáticos (con las(s) estación(es) meteorológicas utilizadas)'.");
        }

		return listaMensajes;
	}

	public void salvarDocumento() {
        try {
        	
        	long idTask = bandejaTareasBean.getTarea().getTaskId();
			long idProcessInstance = bandejaTareasBean.getTarea().getProcessInstanceId();
        	
        	//DocumentoComunidades        	
        	documentoComunidades.setIdTable(proyectosBean.getProyecto().getId());
        	documentoComunidades.setNombreTabla(CategoriaIILicencia.class.getSimpleName());
            documentoComunidades.setEstado(true);
           
            DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
            documentoTarea.setIdTarea(idTask);
            documentoTarea.setProcessInstanceId(idProcessInstance);
        	        	
            documentosFacade.guardarDocumentoAlfresco(proyectosBean.getProyecto().getCodigo(), "LicenciaAmbientalCategoriaII", idProcessInstance, 
            		documentoComunidades, TipoDocumentoSistema.DOCUMENTO_COMUNIDADES_020, documentoTarea);
            
            //Documento Predios
            documentoPredios.setIdTable(proyectosBean.getProyecto().getId());
            documentoPredios.setNombreTabla(CategoriaIILicencia.class.getSimpleName());
            documentoPredios.setEstado(true);
           
            DocumentosTareasProceso documentoTareaPredio = new DocumentosTareasProceso();
            documentoTareaPredio.setIdTarea(idTask);
            documentoTareaPredio.setProcessInstanceId(idProcessInstance);
        	        	
            documentosFacade.guardarDocumentoAlfresco(proyectosBean.getProyecto().getCodigo(), "LicenciaAmbientalCategoriaII", idProcessInstance, 
            		documentoPredios, TipoDocumentoSistema.DOCUMENTO_PREDIOS_020, documentoTareaPredio);
            
            //Documento infraestructura
            documentoInfraestructura.setIdTable(proyectosBean.getProyecto().getId());
            documentoInfraestructura.setNombreTabla(CategoriaIILicencia.class.getSimpleName());
            documentoInfraestructura.setEstado(true);
           
            DocumentosTareasProceso documentoTareaInf = new DocumentosTareasProceso();
            documentoTareaInf.setIdTarea(idTask);
            documentoTareaInf.setProcessInstanceId(idProcessInstance);
        	        	
            documentosFacade.guardarDocumentoAlfresco(proyectosBean.getProyecto().getCodigo(), "LicenciaAmbientalCategoriaII", idProcessInstance, 
            		documentoInfraestructura, TipoDocumentoSistema.DOCUMENTO_INFRAESTRUCTURA_020, documentoTareaInf);
            
            //Documento infraestructura Salud Pública
            documentoInfraestructuraSalud.setIdTable(proyectosBean.getProyecto().getId());
            documentoInfraestructuraSalud.setNombreTabla(CategoriaIILicencia.class.getSimpleName());
            documentoInfraestructuraSalud.setEstado(true);
           
            DocumentosTareasProceso documentoTareaInfSal = new DocumentosTareasProceso();
            documentoTareaInfSal.setIdTarea(idTask);
            documentoTareaInfSal.setProcessInstanceId(idProcessInstance);
        	        	
            documentosFacade.guardarDocumentoAlfresco(proyectosBean.getProyecto().getCodigo(), "LicenciaAmbientalCategoriaII", idProcessInstance, 
            		documentoInfraestructuraSalud, TipoDocumentoSistema.DOCUMENTO_INFRAESTRUCTURA_SALUD_020, documentoTareaInfSal);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	public void salvarDocumentoAnexos() {
        try {
        	
        	long idTask = bandejaTareasBean.getTarea().getTaskId();
			long idProcessInstance = bandejaTareasBean.getTarea().getProcessInstanceId();
        	      	
			documentoLegal.setIdTable(proyectosBean.getProyecto().getId());
			documentoLegal.setNombreTabla(CategoriaIILicencia.class.getSimpleName());
			documentoLegal.setEstado(true);
           
            DocumentosTareasProceso documentoTareaLegal = new DocumentosTareasProceso();
            documentoTareaLegal.setIdTarea(idTask);
            documentoTareaLegal.setProcessInstanceId(idProcessInstance);
            if(documentoLegal.getContenidoDocumento()!=null){
            	documentosFacade.guardarDocumentoAlfresco(proyectosBean.getProyecto().getCodigo(), "LicenciaAmbientalCategoriaII", idProcessInstance, documentoLegal, TipoDocumentoSistema.DOCUMENTOS_LEGALES_020, documentoTareaLegal);
            }
            
            documentoregistroFotografico.setIdTable(proyectosBean.getProyecto().getId());
            documentoregistroFotografico.setNombreTabla(CategoriaIILicencia.class.getSimpleName());
            documentoregistroFotografico.setEstado(true);
           
            DocumentosTareasProceso documentoTareaFoto = new DocumentosTareasProceso();
            documentoTareaFoto.setIdTarea(idTask);
            documentoTareaFoto.setProcessInstanceId(idProcessInstance);
            if(documentoregistroFotografico.getContenidoDocumento()!=null){
            	documentosFacade.guardarDocumentoAlfresco(proyectosBean.getProyecto().getCodigo(), "LicenciaAmbientalCategoriaII", idProcessInstance, documentoregistroFotografico, TipoDocumentoSistema.REGISTRO_FOTOGRAFICO_020, documentoTareaFoto);
            }
            

            documentoMapa.setIdTable(proyectosBean.getProyecto().getId());
            documentoMapa.setNombreTabla(CategoriaIILicencia.class.getSimpleName());
            documentoMapa.setEstado(true);
           
            DocumentosTareasProceso documentoTareaMapa = new DocumentosTareasProceso();
            documentoTareaMapa.setIdTarea(idTask);
            documentoTareaMapa.setProcessInstanceId(idProcessInstance);
            if(documentoMapa.getContenidoDocumento()!=null){
            	documentosFacade.guardarDocumentoAlfresco(proyectosBean.getProyecto().getCodigo(), "LicenciaAmbientalCategoriaII", idProcessInstance, documentoMapa, TipoDocumentoSistema.MAPAS_020, documentoTareaMapa);
            }
            
            documentoPoliza.setIdTable(proyectosBean.getProyecto().getId());
            documentoPoliza.setNombreTabla(CategoriaIILicencia.class.getSimpleName());
            documentoPoliza.setEstado(true);
           
            DocumentosTareasProceso documentoTareaPoliza = new DocumentosTareasProceso();
            documentoTareaPoliza.setIdTarea(idTask);
            documentoTareaPoliza.setProcessInstanceId(idProcessInstance);
            if(documentoPoliza.getContenidoDocumento()!=null) {
            	documentosFacade.guardarDocumentoAlfresco(proyectosBean.getProyecto().getCodigo(), "LicenciaAmbientalCategoriaII", idProcessInstance, documentoPoliza, TipoDocumentoSistema.POLIZA_GARANTIA_020, documentoTareaPoliza);
            }
            
            poliza.setIdTable(proyectosBean.getProyecto().getId());
            poliza.setNombreTabla(CategoriaIILicencia.class.getSimpleName());
            poliza.setEstado(true);
           
            DocumentosTareasProceso documentoPoliza = new DocumentosTareasProceso();
            documentoPoliza.setIdTarea(idTask);
            documentoPoliza.setProcessInstanceId(idProcessInstance);
            if(poliza.getContenidoDocumento()!=null)
            {
            	documentosFacade.guardarDocumentoAlfresco(proyectosBean.getProyecto().getCodigo(), "LicenciaAmbientalCategoriaII", idProcessInstance, 
            			poliza, TipoDocumentoSistema.POLIZA_020, documentoPoliza);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void uploadListenerDocumentosComunidades(FileUploadEvent event) {
    	documentoComunidades = this.uploadListener(event, PerforacionExplorativa.class, "pdf");
    }
    public void uploadListenerDocumentosPredios(FileUploadEvent event) {
    	documentoPredios = this.uploadListener(event, PerforacionExplorativa.class, "pdf");
    }
    public void uploadListenerDocumentosInfraestructura(FileUploadEvent event) {
    	documentoInfraestructura = this.uploadListener(event, PerforacionExplorativa.class, "pdf");
    }
    public void uploadListenerDocumentosInfraestructuraSalud(FileUploadEvent event) {
    	documentoInfraestructuraSalud = this.uploadListener(event, PerforacionExplorativa.class, "pdf");
    }
    public void uploadListenerDocumentosLegales(FileUploadEvent event) {
    	documentoLegal = this.uploadListener(event, PerforacionExplorativa.class, "pdf");
    }
    public void uploadListenerDocumentosRegistroFoto(FileUploadEvent event) {
    	documentoregistroFotografico = this.uploadListener(event, PerforacionExplorativa.class, "pdf");
    }
    public void uploadListenerDocumentosMapa(FileUploadEvent event) {
    	documentoMapa= this.uploadListener(event, PerforacionExplorativa.class, "pdf");
    }
    public void uploadListenerDocumentosPoliza(FileUploadEvent event) {
    	documentoPoliza= this.uploadListener(event, PerforacionExplorativa.class, "pdf");
    }    
    public void uploadListenerPoliza(FileUploadEvent event) {
    	poliza= this.uploadListener(event, PerforacionExplorativa.class, "pdf");
    }
    
    public void uploadTituloMinero(FileUploadEvent event) {
    	DocumentosCOA documento = new DocumentosCOA();
    	byte[] contenidoDocumento = event.getFile().getContents();
		documento.setContenidoDocumento(contenidoDocumento);
		documento.setNombreDocumento(event.getFile().getFileName());
		documento.setExtencionDocumento(".pdf");
		documento.setTipo("application/pdf");
		documento.setNombreTabla(CategoriaIILicencia.class.getSimpleName());
		documento.setIdProceso(bandejaTareasBean.getTarea().getProcessInstanceId());
		listaTituloMinero.add(documento);
		JsfUtil.addMessageInfo("Documento subido exitosamente");
    }
    public void eliminarTituloMinero(DocumentosCOA documento) {
		try {
			listaTituloMinero.remove(documento);
			listaDocumentosEliminar.add(documento);
			JsfUtil.addMessageInfo("Documento eliminado correctamente");
		} catch (Exception e) {
		}
	}
    
    public void uploadGarantia(FileUploadEvent event) {
    	DocumentosCOA documento = new DocumentosCOA();
    	byte[] contenidoDocumento = event.getFile().getContents();
		documento.setContenidoDocumento(contenidoDocumento);
		documento.setNombreDocumento(event.getFile().getFileName());
		documento.setExtencionDocumento(".pdf");
		documento.setTipo("application/pdf");
		documento.setNombreTabla(CategoriaIILicencia.class.getSimpleName());
		documento.setIdProceso(bandejaTareasBean.getTarea().getProcessInstanceId());
		listaGarantia.add(documento);
		JsfUtil.addMessageInfo("Documento subido exitosamente");
    }
    public void eliminarGarantia(DocumentosCOA documento) {
		try {
			listaGarantia.remove(documento);
			listaDocumentosEliminar.add(documento);
			JsfUtil.addMessageInfo("Documento eliminado correctamente");
		} catch (Exception e) {
		}
	}
    
    public void uploadDeclaracion(FileUploadEvent event) {
    	DocumentosCOA documento = new DocumentosCOA();
    	byte[] contenidoDocumento = event.getFile().getContents();
		documento.setContenidoDocumento(contenidoDocumento);
		documento.setNombreDocumento(event.getFile().getFileName());
		documento.setExtencionDocumento(".pdf");
		documento.setTipo("application/pdf");
		documento.setNombreTabla(CategoriaIILicencia.class.getSimpleName());
		documento.setIdProceso(bandejaTareasBean.getTarea().getProcessInstanceId());
		listaDeclaracion.add(documento);
		JsfUtil.addMessageInfo("Documento subido exitosamente");
    }
    public void eliminarDeclaracion(DocumentosCOA documento) {
		try {
			listaDeclaracion.remove(documento);
			listaDocumentosEliminar.add(documento);
			JsfUtil.addMessageInfo("Documento eliminado correctamente");
		} catch (Exception e) {
		}
	}
    
    public void uploadFacturas(FileUploadEvent event) {
    	DocumentosCOA documento = new DocumentosCOA();
    	byte[] contenidoDocumento = event.getFile().getContents();
		documento.setContenidoDocumento(contenidoDocumento);
		documento.setNombreDocumento(event.getFile().getFileName());
		documento.setExtencionDocumento(".pdf");
		documento.setTipo("application/pdf");
		documento.setNombreTabla(CategoriaIILicencia.class.getSimpleName());
		documento.setIdProceso(bandejaTareasBean.getTarea().getProcessInstanceId());
		listaFacturas.add(documento);
		JsfUtil.addMessageInfo("Documento subido exitosamente");
    }
    public void eliminarFacturas(DocumentosCOA documento) {
		try {
			listaFacturas.remove(documento);
			listaDocumentosEliminar.add(documento);
			JsfUtil.addMessageInfo("Documento eliminado correctamente");
		} catch (Exception e) {
		}
	}
    
    public StreamedContent descargarDocumento(DocumentosCOA documento) throws Exception {
    	DefaultStreamedContent content = new DefaultStreamedContent();
    	if (documento != null) {
    		if (documento.getContenidoDocumento() == null) {
    			documento.setContenidoDocumento(documentosFacade.descargar(documento.getIdAlfresco()));
    		}
    		content = new DefaultStreamedContent(new ByteArrayInputStream(documento.getContenidoDocumento()),documento.getExtencionDocumento());
    		content.setName(documento.getNombreDocumento());
    	}
    	return content;
    }
    
    public void uploadAreaEstudio (FileUploadEvent event) {
    	DocumentosCOA documento = new DocumentosCOA();
    	byte[] contenidoDocumento = event.getFile().getContents();
		documento.setContenidoDocumento(contenidoDocumento);
		documento.setNombreDocumento(event.getFile().getFileName());
		documento.setExtencionDocumento(".pdf");
		documento.setTipo("application/pdf");
		documento.setNombreTabla(CategoriaIILicencia.class.getSimpleName());
		documento.setIdProceso(bandejaTareasBean.getTarea().getProcessInstanceId());
		areaEstudio = documento;
		JsfUtil.addMessageInfo("Documento subido exitosamente");
    }
    
    public void uploadUbicacion (FileUploadEvent event) {
    	DocumentosCOA documento = new DocumentosCOA();
    	byte[] contenidoDocumento = event.getFile().getContents();
		documento.setContenidoDocumento(contenidoDocumento);
		documento.setNombreDocumento(event.getFile().getFileName());
		documento.setExtencionDocumento(".pdf");
		documento.setTipo("application/pdf");
		documento.setNombreTabla(CategoriaIILicencia.class.getSimpleName());
		documento.setIdProceso(bandejaTareasBean.getTarea().getProcessInstanceId());
		ubicacion = documento;
		JsfUtil.addMessageInfo("Documento subido exitosamente");
    }
    
    public void uploadTipoClima(FileUploadEvent event) {
    	DocumentosCOA documento = new DocumentosCOA();
    	byte[] contenidoDocumento = event.getFile().getContents();
		documento.setContenidoDocumento(contenidoDocumento);
		documento.setNombreDocumento(event.getFile().getFileName());
		documento.setExtencionDocumento(".pdf");
		documento.setTipo("application/pdf");
		documento.setNombreTabla(CategoriaIILicencia.class.getSimpleName());
		documento.setIdProceso(bandejaTareasBean.getTarea().getProcessInstanceId());
		tipoClima = documento;
		JsfUtil.addMessageInfo("Documento subido exitosamente");
    }
    
    public void uploadPisos(FileUploadEvent event) {
    	DocumentosCOA documento = new DocumentosCOA();
    	byte[] contenidoDocumento = event.getFile().getContents();
		documento.setContenidoDocumento(contenidoDocumento);
		documento.setNombreDocumento(event.getFile().getFileName());
		documento.setExtencionDocumento(".pdf");
		documento.setTipo("application/pdf");
		documento.setNombreTabla(CategoriaIILicencia.class.getSimpleName());
		documento.setIdProceso(bandejaTareasBean.getTarea().getProcessInstanceId());
		pisos = documento;
		JsfUtil.addMessageInfo("Documento subido exitosamente");
    }

    private Documento uploadListener(FileUploadEvent event, Class<?> clazz, String extension) {
        byte[] contenidoDocumento = event.getFile().getContents();
        Documento documento = crearDocumento(contenidoDocumento, clazz, extension);
        documento.setNombre(event.getFile().getFileName());
        documento.setDescripcion(event.getFile().getFileName());
        return documento;
    }

    /**
     * Crea el documento
     *
     * @param contenidoDocumento arreglo de bytes
     * @param clazz              Clase a la cual se va a ligar al documento
     * @param extension          extension del archivo
     * @return Objeto de tipo Documento
     */
    public Documento crearDocumento(byte[] contenidoDocumento, Class<?> clazz, String extension) {
        Documento documento = new Documento();
        documento.setContenidoDocumento(contenidoDocumento);
        documento.setNombreTabla(clazz.getSimpleName());
        documento.setIdTable(0);
        documento.setExtesion("." + extension);
        documento.setMime(extension == "pdf" ? "application/pdf" : "application/vnd.ms-excel");
        return documento;
    }


    public StreamedContent getStreamContentComunidades() throws Exception {
        DefaultStreamedContent content = null;
        this.documentoComunidades = descargarAlfresco(documentoComunidades);
        try {
            if (documentoComunidades != null && documentoComunidades.getNombre() != null && documentoComunidades.getContenidoDocumento() != null) {
                content = new DefaultStreamedContent(new ByteArrayInputStream(documentoComunidades.getContenidoDocumento()));
                content.setName(documentoComunidades.getNombre());
            } else
                JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

        } catch (Exception exception) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
        }
        return content;
    }
    
    public StreamedContent getStreamContentPredios() throws Exception {
        DefaultStreamedContent content = null;
        this.documentoPredios = descargarAlfresco(documentoPredios);
        try {
            if (documentoPredios != null && documentoPredios.getNombre() != null && documentoPredios.getContenidoDocumento() != null) {
                content = new DefaultStreamedContent(new ByteArrayInputStream(documentoPredios.getContenidoDocumento()));
                content.setName(documentoPredios.getNombre());

            } else
                JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);


        } catch (Exception exception) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
        }
        return content;
    }
    
    public StreamedContent getStreamContentInfraestructura() throws Exception {
        DefaultStreamedContent content = null;
        this.documentoInfraestructura = descargarAlfresco(documentoInfraestructura);
        try {
            if (documentoInfraestructura != null && documentoInfraestructura.getNombre() != null
                    && documentoInfraestructura.getContenidoDocumento() != null) {
                content = new DefaultStreamedContent(
                        new ByteArrayInputStream(
                        		documentoInfraestructura.getContenidoDocumento()));
                content.setName(documentoInfraestructura.getNombre());

            } else
                JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);


        } catch (Exception exception) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
        }
        return content;
    }
    
    public StreamedContent getStreamContentInfraestructuraSalud() throws Exception {
        DefaultStreamedContent content = null;
        this.documentoInfraestructuraSalud = descargarAlfresco(documentoInfraestructuraSalud);
        try {
            if (documentoInfraestructuraSalud != null && documentoInfraestructuraSalud.getNombre() != null
                    && documentoInfraestructuraSalud.getContenidoDocumento() != null) {
                content = new DefaultStreamedContent(
                        new ByteArrayInputStream(
                        		documentoInfraestructuraSalud.getContenidoDocumento()));
                content.setName(documentoInfraestructuraSalud.getNombre());

            } else
                JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);


        } catch (Exception exception) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
        }
        return content;
    }
    

    public Documento descargarAlfresco(Documento documento)
            throws CmisAlfrescoException {
        byte[] documentoContenido = null;
        if (documento != null && documento.getIdAlfresco() != null)
            if(!esProyectoRcoa)
            	documentoContenido = documentosFacade.descargar(documento.getIdAlfresco());
            else
            	documentoContenido = documentosRcoaFacade.descargar(documento.getIdAlfresco());
        if (documentoContenido != null)
            documento.setContenidoDocumento(documentoContenido);
        return documento;
    }
	
  //aumento visualizacion
    public StreamedContent getStreamContentDocumentoLegal() throws Exception {
        DefaultStreamedContent content = null;
        this.documentoLegal = descargarAlfresco(documentoLegal);
        try {
            if (documentoLegal != null && documentoLegal.getNombre() != null
                    && documentoLegal.getContenidoDocumento() != null) {
                content = new DefaultStreamedContent(
                        new ByteArrayInputStream(
                        		documentoLegal.getContenidoDocumento()));
                content.setName(documentoLegal.getNombre());

            } else
                JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);


        } catch (Exception exception) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
        }
        return content;
    }
    
    public StreamedContent getStreamContentDocumentoRegistroFotografico() throws Exception {
        DefaultStreamedContent content = null;
        this.documentoregistroFotografico = descargarAlfresco(documentoregistroFotografico);
        try {
            if (documentoregistroFotografico != null && documentoregistroFotografico.getNombre() != null
                    && documentoregistroFotografico.getContenidoDocumento() != null) {
                content = new DefaultStreamedContent(
                        new ByteArrayInputStream(
                        		documentoregistroFotografico.getContenidoDocumento()));
                content.setName(documentoregistroFotografico.getNombre());

            } else
                JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);


        } catch (Exception exception) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
        }
        return content;
    }
    
    public StreamedContent getStreamContentDocumentoMapa() throws Exception {
        DefaultStreamedContent content = null;
        this.documentoMapa = descargarAlfresco(documentoMapa);
        try {
            if (documentoMapa != null && documentoMapa.getNombre() != null
                    && documentoMapa.getContenidoDocumento() != null) {
                content = new DefaultStreamedContent(
                        new ByteArrayInputStream(
                        		documentoMapa.getContenidoDocumento()));
                content.setName(documentoMapa.getNombre());

            } else
                JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);


        } catch (Exception exception) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
        }
        return content;
    }
    
    public StreamedContent getStreamContentDocumentoPoliza() throws Exception {
        DefaultStreamedContent content = null;
        this.documentoPoliza = descargarAlfresco(documentoPoliza);
        try {
            if (documentoPoliza != null && documentoPoliza.getNombre() != null
                    && documentoPoliza.getContenidoDocumento() != null) {
                content = new DefaultStreamedContent(
                        new ByteArrayInputStream(
                        		documentoPoliza.getContenidoDocumento()));
                content.setName(documentoPoliza.getNombre());
                documentoPolizaDownload=true;
            } else
                JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);


        } catch (Exception exception) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
        }
        return content;
    }
    
  //nuevo documentos
    public StreamedContent getStreamContentDocumentoResolucion() throws Exception {
        DefaultStreamedContent content = null;
        this.documentoResolucion = descargarAlfresco(documentoResolucion);
        try {
            if (documentoResolucion != null && documentoResolucion.getNombre() != null
                    && documentoResolucion.getContenidoDocumento() != null) {
                content = new DefaultStreamedContent(
                        new ByteArrayInputStream(
                        		documentoResolucion.getContenidoDocumento()));
                content.setName(documentoResolucion.getNombre());
                documentoResolucionDownload=true;
            } else
                JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);


        } catch (Exception exception) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
        }
        return content;
    }
    
    public StreamedContent getStreamContentDocumentoFicha() throws Exception {
        DefaultStreamedContent content = null;
        this.documentoFicha = descargarAlfresco(documentoFicha);
        try {
            if (documentoFicha != null && documentoFicha.getNombre() != null
                    && documentoFicha.getContenidoDocumento() != null) {
                content = new DefaultStreamedContent(
                        new ByteArrayInputStream(
                        		documentoFicha.getContenidoDocumento()));
                content.setName(documentoFicha.getNombre());
                documentoFichaDownload=true;
            } else
                JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);


        } catch (Exception exception) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
        }
        return content;
    }
    
    public StreamedContent getStreamContentDocumentoPoliza020() throws Exception {
        DefaultStreamedContent content = null;
        this.poliza = descargarAlfresco(poliza);
        try {
            if (poliza != null && poliza.getNombre() != null
                    && poliza.getContenidoDocumento() != null) {
                content = new DefaultStreamedContent(
                        new ByteArrayInputStream(
                        		poliza.getContenidoDocumento()));
                content.setName(poliza.getNombre());
                polizaDownload=true;
            } else
                JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);


        } catch (Exception exception) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
        }
        return content;
    }
    
    public StreamedContent getStreamContentDocumentoFactura() throws Exception {
        DefaultStreamedContent content = null;
        this.factura = descargarAlfresco(factura);
        try {
            if (factura != null && factura.getNombre() != null
                    && factura.getContenidoDocumento() != null) {
                content = new DefaultStreamedContent(
                        new ByteArrayInputStream(
                        		factura.getContenidoDocumento()));
                content.setName(factura.getNombre());
                facturaDownload=true;
            } else
                JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);


        } catch (Exception exception) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
        }
        return content;
    }

    public boolean verificarDocumentosDownload(){
    	return documentoFichaDownload && facturaDownload && documentoResolucionDownload && documentoPolizaDownload && polizaDownload;
    }
    
    public void recuperaAnexos() {
    	List<Documento> documentoLegalList = documentosFacade.documentoXTablaIdXIdDoc(proyectosBean.getProyecto().getId(), CategoriaIILicencia.class.getSimpleName(), TipoDocumentoSistema.DOCUMENTOS_LEGALES_020);
		if(documentoLegalList != null && !documentoLegalList.isEmpty()){
			documentoLegal = documentoLegalList.get(0);
		}
		
		List<Documento> documentoregistroFotograficolList = documentosFacade.documentoXTablaIdXIdDoc(proyectosBean.getProyecto().getId(), CategoriaIILicencia.class.getSimpleName(), TipoDocumentoSistema.REGISTRO_FOTOGRAFICO_020);
		if(documentoregistroFotograficolList != null && !documentoregistroFotograficolList.isEmpty()){
			documentoregistroFotografico = documentoregistroFotograficolList.get(0);
		}
		
		List<Documento> documentoMapalList = documentosFacade.documentoXTablaIdXIdDoc(proyectosBean.getProyecto().getId(), CategoriaIILicencia.class.getSimpleName(), TipoDocumentoSistema.MAPAS_020);
		if(documentoMapalList != null && !documentoMapalList.isEmpty()){
			documentoMapa = documentoMapalList.get(0);
		}
		
		List<Documento> documentoPolizaList = documentosFacade.documentoXTablaIdXIdDoc(proyectosBean.getProyecto().getId(), CategoriaIILicencia.class.getSimpleName(), TipoDocumentoSistema.POLIZA_GARANTIA_020);
		if(documentoPolizaList != null && !documentoPolizaList.isEmpty()){
			documentoPoliza = documentoPolizaList.get(0);
		}
		
		List<Documento> polizaList = documentosFacade.documentoXTablaIdXIdDoc(proyectosBean.getProyecto().getId(), CategoriaIILicencia.class.getSimpleName(), TipoDocumentoSistema.POLIZA_020);
		if(polizaList != null && !polizaList.isEmpty()){
			poliza = polizaList.get(0);
		}
    }
    
    public void recuperaAnexosRcoa() {
    	try {
	    	List<DocumentosCOA> documentoLegalList = documentosRcoaFacade.documentoXTablaIdXIdDoc(proyectoLicenciaCoa.getId(), TipoDocumentoSistema.DOCUMENTOS_LEGALES_020, CategoriaIILicencia.class.getSimpleName());
			if(documentoLegalList != null && !documentoLegalList.isEmpty()){
				 documentoLegal = new Documento();
				 documentoLegal.setNombre(documentoLegalList.get(0).getNombreDocumento());
				 documentoLegal.setIdAlfresco(documentoLegalList.get(0).getIdAlfresco());
			}
			
			List<DocumentosCOA> documentoregistroFotograficolList = documentosRcoaFacade.documentoXTablaIdXIdDoc(proyectoLicenciaCoa.getId(), TipoDocumentoSistema.REGISTRO_FOTOGRAFICO_020, CategoriaIILicencia.class.getSimpleName());
			if(documentoregistroFotograficolList != null && !documentoregistroFotograficolList.isEmpty()){
				documentoregistroFotografico = new Documento();
				documentoregistroFotografico.setNombre(documentoregistroFotograficolList.get(0).getNombreDocumento());
				documentoregistroFotografico.setIdAlfresco(documentoregistroFotograficolList.get(0).getIdAlfresco());
			}
			
			List<DocumentosCOA> documentoMapalList = documentosRcoaFacade.documentoXTablaIdXIdDoc(proyectoLicenciaCoa.getId(), TipoDocumentoSistema.MAPAS_020, CategoriaIILicencia.class.getSimpleName());
			if(documentoMapalList != null && !documentoMapalList.isEmpty()){
				documentoMapa = new Documento();
				documentoMapa.setNombre(documentoMapalList.get(0).getNombreDocumento());
				documentoMapa.setIdAlfresco(documentoMapalList.get(0).getIdAlfresco());
			}
			
			List<DocumentosCOA> documentoPolizaList = documentosRcoaFacade.documentoXTablaIdXIdDoc(proyectoLicenciaCoa.getId(), TipoDocumentoSistema.POLIZA_GARANTIA_020, CategoriaIILicencia.class.getSimpleName());
			if(documentoPolizaList != null && !documentoPolizaList.isEmpty()){
				documentoPoliza = new Documento();
				documentoPoliza.setNombre(documentoPolizaList.get(0).getNombreDocumento());
				documentoPoliza.setIdAlfresco(documentoPolizaList.get(0).getIdAlfresco());
			}
			
			List<DocumentosCOA> polizaList = documentosRcoaFacade.documentoXTablaIdXIdDoc(proyectoLicenciaCoa.getId(), TipoDocumentoSistema.POLIZA_020, CategoriaIILicencia.class.getSimpleName());
			if(polizaList != null && !polizaList.isEmpty()){
				poliza = new Documento();
				poliza.setNombre(polizaList.get(0).getNombreDocumento());
				poliza.setIdAlfresco(polizaList.get(0).getIdAlfresco());
			}
			
			List<DocumentosCOA> documentoResolucionList = documentosRcoaFacade.documentoXTablaIdXIdDoc(proyectoLicenciaCoa.getId(), TipoDocumentoSistema.TIPO_DOCUMENTO_RESOLUCION_REGISTRO_AMBIENTAL,CategoriaIILicencia.class.getSimpleName());
			if(documentoResolucionList != null && !documentoResolucionList.isEmpty()){				
				documentoResolucion = new Documento();
				documentoResolucion.setIdAlfresco(documentoResolucionList.get(0).getIdAlfresco());
				documentoResolucion.setNombre(documentoResolucionList.get(0).getNombreDocumento());
			}
			
			List<DocumentosCOA> documentoFichaList = documentosRcoaFacade.documentoXTablaIdXIdDoc(proyectoLicenciaCoa.getId(), TipoDocumentoSistema.TIPO_DOCUMENTO_FICHA_MINERIA,CategoriaIILicencia.class.getSimpleName());
			if(documentoFichaList != null && !documentoFichaList.isEmpty()){
				documentoFicha= new Documento();
				documentoFicha.setIdAlfresco(documentoFichaList.get(0).getIdAlfresco());
				documentoFicha.setNombre(documentoFichaList.get(0).getNombreDocumento());
			}
			
			List<DocumentosCOA> documentoFacturaList = documentosRcoaFacade.documentoXTablaIdXIdDoc(proyectoLicenciaCoa.getId(), TipoDocumentoSistema.TIPO_RLA_FACTURA_PERMISO_AMBIENTAL,CategoriaIILicencia.class.getSimpleName());
			if(documentoFacturaList != null && !documentoFacturaList.isEmpty()){
				factura= new Documento();
				factura.setIdAlfresco(documentoFacturaList.get(0).getIdAlfresco());
				factura.setNombre(documentoFacturaList.get(0).getNombreDocumento());
			}
    	} catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void salvarDocumentoAnexosRcoa() {
        try {
        	
        	long idProcessInstance = bandejaTareasBean.getTarea().getProcessInstanceId();
						
            if(documentoLegal.getContenidoDocumento()!=null) {
            	DocumentosCOA documentoLegalRcoa = new DocumentosCOA(); 
    			documentoLegalRcoa.setContenidoDocumento(documentoLegal.getContenidoDocumento());
    			documentoLegalRcoa.setNombreDocumento(documentoLegal.getNombre());
    			documentoLegalRcoa.setExtencionDocumento(documentoLegal.getExtesion());		
    			documentoLegalRcoa.setTipo(documentoLegal.getMime());
    			documentoLegalRcoa.setNombreTabla(CategoriaIILicencia.class.getSimpleName());
    			documentoLegalRcoa.setIdTabla(proyectoLicenciaCoa.getId());
    			documentoLegalRcoa.setProyectoLicenciaCoa(proyectoLicenciaCoa);
    			documentoLegalRcoa.setIdProceso(idProcessInstance);
    			
    			documentoLegalRcoa = documentosRcoaFacade.guardarDocumentoAlfresco(
    					proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
    					"LicenciaAmbientalCategoriaII", idProcessInstance,
    					documentoLegalRcoa, TipoDocumentoSistema.DOCUMENTOS_LEGALES_020);
    			
    			documentoLegal.setContenidoDocumento(null);
    			documentoLegal.setIdAlfresco(documentoLegalRcoa.getIdAlfresco());
            }
            
            if(documentoregistroFotografico.getContenidoDocumento()!=null)
            {
            	DocumentosCOA documentoFotograficoRcoa = new DocumentosCOA(); 
    			documentoFotograficoRcoa.setContenidoDocumento(documentoregistroFotografico.getContenidoDocumento());
    			documentoFotograficoRcoa.setNombreDocumento(documentoregistroFotografico.getNombre());
    			documentoFotograficoRcoa.setExtencionDocumento(documentoregistroFotografico.getExtesion());		
    			documentoFotograficoRcoa.setTipo(documentoregistroFotografico.getMime());
    			documentoFotograficoRcoa.setNombreTabla(CategoriaIILicencia.class.getSimpleName());
    			documentoFotograficoRcoa.setIdTabla(proyectoLicenciaCoa.getId());
    			documentoFotograficoRcoa.setProyectoLicenciaCoa(proyectoLicenciaCoa);
    			documentoFotograficoRcoa.setIdProceso(idProcessInstance);
    			
    			documentoFotograficoRcoa = documentosRcoaFacade.guardarDocumentoAlfresco(
    					proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
    					"LicenciaAmbientalCategoriaII", idProcessInstance,
    					documentoFotograficoRcoa, TipoDocumentoSistema.REGISTRO_FOTOGRAFICO_020);
    			
    			documentoregistroFotografico.setContenidoDocumento(null);
    			documentoregistroFotografico.setIdAlfresco(documentoFotograficoRcoa.getIdAlfresco());
            }
            
           
            if(documentoMapa.getContenidoDocumento()!=null)
            {
            	 DocumentosCOA documentoMapaRcoa = new DocumentosCOA(); 
     			documentoMapaRcoa.setContenidoDocumento(documentoMapa.getContenidoDocumento());
     			documentoMapaRcoa.setNombreDocumento(documentoMapa.getNombre());
     			documentoMapaRcoa.setExtencionDocumento(documentoMapa.getExtesion());		
     			documentoMapaRcoa.setTipo(documentoMapa.getMime());
     			documentoMapaRcoa.setNombreTabla(CategoriaIILicencia.class.getSimpleName());
     			documentoMapaRcoa.setIdTabla(proyectoLicenciaCoa.getId());
     			documentoMapaRcoa.setProyectoLicenciaCoa(proyectoLicenciaCoa);
     			documentoMapaRcoa.setIdProceso(idProcessInstance);
     			
     			documentoMapaRcoa = documentosRcoaFacade.guardarDocumentoAlfresco(
     					proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
     					"LicenciaAmbientalCategoriaII", idProcessInstance,
     					documentoMapaRcoa, TipoDocumentoSistema.MAPAS_020);
     			
     			documentoMapa.setContenidoDocumento(null);
     			documentoMapa.setIdAlfresco(documentoMapaRcoa.getIdAlfresco());
            }
            
            if(documentoPoliza.getContenidoDocumento()!=null)
            {
            	DocumentosCOA documentoPolizaRcoa = new DocumentosCOA(); 
    			documentoPolizaRcoa.setContenidoDocumento(documentoPoliza.getContenidoDocumento());
    			documentoPolizaRcoa.setNombreDocumento(documentoPoliza.getNombre());
    			documentoPolizaRcoa.setExtencionDocumento(documentoPoliza.getExtesion());		
    			documentoPolizaRcoa.setTipo(documentoPoliza.getMime());
    			documentoPolizaRcoa.setNombreTabla(CategoriaIILicencia.class.getSimpleName());
    			documentoPolizaRcoa.setIdTabla(proyectoLicenciaCoa.getId());
    			documentoPolizaRcoa.setProyectoLicenciaCoa(proyectoLicenciaCoa);
    			documentoPolizaRcoa.setIdProceso(idProcessInstance);
    			
    			documentoPolizaRcoa = documentosRcoaFacade.guardarDocumentoAlfresco(
    					proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
    					"LicenciaAmbientalCategoriaII", idProcessInstance,
    					documentoPolizaRcoa, TipoDocumentoSistema.POLIZA_GARANTIA_020);
    			
    			documentoPoliza.setContenidoDocumento(null);
    			documentoPoliza.setIdAlfresco(documentoPolizaRcoa.getIdAlfresco());
            }
            
            if(poliza.getContenidoDocumento()!=null)
            {
            	DocumentosCOA polizaRcoa = new DocumentosCOA(); 
    			polizaRcoa.setContenidoDocumento(poliza.getContenidoDocumento());
    			polizaRcoa.setNombreDocumento(poliza.getNombre());
    			polizaRcoa.setExtencionDocumento(poliza.getExtesion());		
    			polizaRcoa.setTipo(poliza.getMime());
    			polizaRcoa.setNombreTabla(CategoriaIILicencia.class.getSimpleName());
    			polizaRcoa.setIdTabla(proyectoLicenciaCoa.getId());
    			polizaRcoa.setProyectoLicenciaCoa(proyectoLicenciaCoa);
    			polizaRcoa.setIdProceso(idProcessInstance);
    			
    			polizaRcoa = documentosRcoaFacade.guardarDocumentoAlfresco(
    					proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
    					"LicenciaAmbientalCategoriaII", idProcessInstance,
    					polizaRcoa, TipoDocumentoSistema.POLIZA_020);
    			
    			poliza.setContenidoDocumento(null);
    			poliza.setIdAlfresco(polizaRcoa.getIdAlfresco());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }  
}
