/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.prevencion.categoria2.v2.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.kie.api.task.model.TaskSummary;
import org.primefaces.context.RequestContext;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.categoria2.bean.MarcoLegalPmaImprimirBean;
import ec.gob.ambiente.prevencion.categoria2.controllers.FichaAmbientalPmaController;
import ec.gob.ambiente.prevencion.categoria2.controllers.ImpresionFichaGeneralController;
import ec.gob.ambiente.prevencion.categoriaii.mineria.controller.FichaMineriaController;
import ec.gob.ambiente.prevencion.categoriaii.mineria.controller.ImpresionFichaMineriaController;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.AprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.EliminacionDesechoFacade;
import ec.gob.ambiente.suia.domain.CatalogoCategoriaSistema;
import ec.gob.ambiente.suia.domain.Categoria;
import ec.gob.ambiente.suia.domain.CategoriaIILicencia;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.FichaAmbientalMineria;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TipoEstudio;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.v2.CategoriaIIFacadeV2;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.FichaAmbientalMineria020Facade;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.FichaAmbientalMineriaFacade;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.reportes.DocumentoPDFPlantillaHtml;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.survey.SurveyResponseFacade;
import ec.gob.ambiente.suia.tareas.programadas.EnvioMailCliente;
import ec.gob.ambiente.suia.tramiteresolver.RegistroGeneradorTramiteResolver;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilFichaMineria;
import ec.gob.ambiente.suia.utils.ConexionBpms;

/**
 * @author frank torres
 */
@ManagedBean
@ViewScoped
public class FichaAmbientalGeneralFinalizarControllerV2 implements Serializable {

	/**
     *
     */
	private static final long serialVersionUID = 4192708780238873820L;

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(FichaAmbientalGeneralFinalizarControllerV2.class);

	private static final String MENSAJE_LISTA = "mensaje.listaproyecto";
	@Getter
	@Setter
	ProyectoLicenciamientoAmbiental proyecto;
	@Getter
	@Setter
	private FichaAmbientalMineria fichaAmbientalMineria;
	@ManagedProperty(value = "#{bandejaTareasBean}")
	@Getter
	@Setter
	private BandejaTareasBean bandejaTareasBean;
	@ManagedProperty(value = "#{proyectosBean}")
	@Getter
	@Setter
	private ProyectosBean proyectosBean;
	@EJB
	private CategoriaIIFacadeV2 categoriaIIFacade;
	@EJB
	private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;
	@EJB
	private FichaAmbientalMineriaFacade fichaAmbientalMineriaFacade;
	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;
	@EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
	private TaskBeanFacade taskBeanFacade;

	@EJB
	private AprobacionRequisitosTecnicosFacade aprobacionRequisitosTecnicosFacade;

	@EJB
	private EliminacionDesechoFacade eliminacionDesechoFacade;

    @EJB
    private ConexionBpms conexionBpms;
    
    @EJB
    private DocumentosCoaFacade documentosRcoaFacade;

	@Getter
	@Setter
	private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;
	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	@Getter
	@Setter
	private FichaAmbientalPma fichaAmbiental;
	@Getter
	@Setter
	private Boolean descargarFicha = false;
	@Getter
	@Setter
	private Boolean descargarRegistro = false;
	@Getter
	@Setter
	private String mensaje;

	@Getter
	@Setter
	private String mensajeFinalizar;

	@Getter
	@Setter
	private Boolean completado = false;

	@Getter
	@Setter
	private Boolean existeGenerador = false;

	@Getter
	@Setter
	private Object[] result;
	
	@EJB
	private EnvioMailCliente envioMailCliente;
	
	@EJB
    private ProcesoFacade procesoFacade;
	@EJB
    private UsuarioFacade usuarioFacade;@EJB
    private UbicacionGeograficaFacade ubicacionGeograficaFacade;
	
	/***** módulo de encuesta *****/
	private Organizacion organizacion;
	@EJB
	private OrganizacionFacade orgaFacade;
	@EJB
	private SurveyResponseFacade surveyResponseFacade;
	@Getter
    @Setter
    public String surveyLink = Constantes.getPropertyAsString("suia.survey.link");
	
	@EJB
	ContactoFacade contactoFacade;
	List<Contacto> listContacto = new ArrayList<Contacto>();
	/***** fin módulo de encuesta *****/

	@EJB
	ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
	
	@EJB
	private FichaAmbientalMineria020Facade fichaAmbientalMineria020Facade;
	
	@Setter
    @Getter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
    
    
	@PostConstruct
	private void cargarDatos() {
		cargarDatosBandeja();
		completado = validarFichaCompletada();
	}

	private void cargarDatosBandeja() {
		proyecto = proyectosBean.getProyecto();
		if(proyectosBean.getProyectoRcoa() != null && proyectosBean.getProyectoRcoa().getId() != null)
			proyectoLicenciaCoa = proyectosBean.getProyectoRcoa();
			
		mensaje = getNotaResponsabilidadInformacionRegistroProyecto(loginBean.getUsuario());
	}

	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(),
				"/prevencion/categoria2/v2/fichaAmbiental/enviarFicha.jsf");
	}

    /**
	 * Descripcion:	funcion para generar la ficha tecnica 				
	 * fecha:		2018-10-18
	 */
	public byte[] generarFichaRegistroAmbiental() {
		try {
			long idTask = bandejaTareasBean.getTarea().getTaskId();
			long idProcessInstance = bandejaTareasBean.getTarea().getProcessInstanceId();

			byte[] archivoLicencia = null;

			// Si el proyecto no es Mineria
			String codigoMineriaSondeo=fichaAmbientalMineria020Facade.cargarPerforacionExplorativa(proyecto).getCodeUpdate();
			if(codigoMineriaSondeo==null)
				codigoMineriaSondeo="0";
			
			if (!(proyecto.getCatalogoCategoria().getCodigo().equals("21.02.01.01") || proyecto.getCatalogoCategoria().getCodigo().equals("21.02.01.02") || proyecto.getCatalogoCategoria().getCodigo().equals("21.02.03.05") || proyecto.getCatalogoCategoria().getCodigo().equals("21.02.04.03") || proyecto.getCatalogoCategoria().getCodigo().equals("21.02.05.03") || proyecto.getCatalogoCategoria().getCodigo().equals("21.02.02.03") || codigoMineriaSondeo.equals("21.02.02.01") || codigoMineriaSondeo.equals("21.02.03.06"))) {
				
				archivoLicencia = categoriaIIFacade.recuperarDocumentoCategoriaII(proyecto.getId(),TipoDocumentoSistema.TIPO_DOCUMENTO_REGISTRO_AMBIENTAL);				
				if(archivoLicencia==null)
				{
					ingresarFichaGeneral(idTask, idProcessInstance,false);
					archivoLicencia = categoriaIIFacade.recuperarDocumentoCategoriaII(proyecto.getId(),TipoDocumentoSistema.TIPO_DOCUMENTO_REGISTRO_AMBIENTAL);
				}
					
			} else {
//				archivoLicencia = categoriaIIFacade.recuperarDocumentoCategoriaII(proyecto.getId(),TipoDocumentoSistema.TIPO_DOCUMENTO_FICHA_MINERIA);
				if(archivoLicencia==null)
				{
					FichaMineriaController fichaMineriaController = JsfUtil.getBean(FichaMineriaController.class);
					ImpresionFichaMineriaController impresionFichaMineriaController = JsfUtil.getBean(ImpresionFichaMineriaController.class);					
	                categoriaIIFacade.ingresarDocumentoCategoriaII(impresionFichaMineriaController.cargarDatosPdfFile(), proyecto.getId(), proyecto.getCodigo(),idProcessInstance, idTask, TipoDocumentoSistema.TIPO_DOCUMENTO_FICHA_MINERIA, "Ficha Mineria");
	                archivoLicencia = categoriaIIFacade.recuperarDocumentoCategoriaII(proyecto.getId(),TipoDocumentoSistema.TIPO_DOCUMENTO_FICHA_MINERIA);
				}
				
			}
			
			return archivoLicencia;

		} catch (Exception e) {
			LOG.error("Error al generar la ficha Registro del Ambiental", e);
			return null;
		}
	}
	
	public byte[] generarFichaRegistroAmbientalRCOA() {
		try {			
			byte[] archivoLicencia = null;
			archivoLicencia = generarFicha020RCOA();
			return archivoLicencia;
		} catch (Exception e) {
			LOG.error("Error al generar la ficha Registro del Ambiental", e);
			return null;
		}
	}
	
	public byte[] generarFicha020RCOA() {
		try{
			ImpresionFichaMineriaController impresionFichaMineriaController = JsfUtil.getBean(ImpresionFichaMineriaController.class);
			File file = impresionFichaMineriaController.cargarDatosPdfFileRCOA();
			
			long idProcessInstance = bandejaTareasBean.getTarea().getProcessInstanceId();
			
			MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
	        byte[] data = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
	        
			DocumentosCOA documentoFicha = new DocumentosCOA(); 
			documentoFicha.setContenidoDocumento(data);
			documentoFicha.setNombreDocumento("Ficha Mineria.pdf");
			documentoFicha.setExtencionDocumento(mimeTypesMap.getContentType(file));		
			documentoFicha.setTipo(mimeTypesMap.getContentType(file));
			documentoFicha.setNombreTabla(CategoriaIILicencia.class.getSimpleName());
			documentoFicha.setIdTabla(proyectoLicenciaCoa.getId());
			documentoFicha.setProyectoLicenciaCoa(proyectoLicenciaCoa);
			
			documentoFicha = documentosRcoaFacade.guardarDocumentoAlfresco(
					proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
					"LicenciaAmbientalCategoriaII", idProcessInstance,
					documentoFicha, TipoDocumentoSistema.TIPO_DOCUMENTO_FICHA_MINERIA);
			
			return data;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	@EJB
	private DocumentosCoaFacade documentosFacade;
	@EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
    AlfrescoServiceBean alfrescoServiceBean;
	
	public void descargarFichaRegistroAmbiental() {
	        
	        try {
	            byte[] archivoLicencia = null;
	            
	            if(proyectosBean.getProyectoRcoa() != null && proyectosBean.getProyectoRcoa().getId() != null)
	            {	
	            	List<DocumentosCOA> listaDocumentos;
	            	try {
	            		listaDocumentos = documentosFacade.documentoXTablaIdXIdDoc(proyectosBean.getProyectoRcoa().getId(), TipoDocumentoSistema.TIPO_DOCUMENTO_FICHA_MINERIA,CategoriaIILicencia.class.getSimpleName());
	            		if (listaDocumentos.size() > 0) {
	            			archivoLicencia=alfrescoServiceBean.downloadDocumentById(listaDocumentos.get(0).getIdAlfresco());
	            		}
	            	} catch (CmisAlfrescoException e) {
	            	}
	            }	            
	            else if (!(proyecto.getCatalogoCategoria().getCodigo().equals("21.02.01.01")|| proyecto.getCatalogoCategoria().getCodigo().equals("21.02.01.02") || proyecto.getCatalogoCategoria().getCodigo().equals("21.02.03.05") || proyecto.getCatalogoCategoria().getCodigo().equals("21.02.04.03") || proyecto.getCatalogoCategoria().getCodigo().equals("21.02.05.03") || proyecto.getCatalogoCategoria().getCodigo().equals("21.02.02.03"))) {
	            	archivoLicencia = categoriaIIFacade.recuperarDocumentoCategoriaII(proyecto.getId(), TipoDocumentoSistema.TIPO_DOCUMENTO_REGISTRO_AMBIENTAL);
	            	
	            }else{
	            	archivoLicencia = categoriaIIFacade.recuperarDocumentoCategoriaII(proyecto.getId(), TipoDocumentoSistema.TIPO_DOCUMENTO_FICHA_MINERIA);	            	
	            }
	            
	            if(archivoLicencia==null)
            	{
	            	archivoLicencia=generarFichaRegistroAmbiental();
            	}
	            
	            if (archivoLicencia != null) {
	            	String nombre=proyecto.getCodigo()==null?proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental():proyecto.getCodigo();
	                UtilFichaMineria.descargar(archivoLicencia,"Registro_Ambiental_" + nombre);
	                descargarFicha = true;
	            }	

	        } catch (Exception e) {
	            LOG.error("Error al descargar la ficha Registro del Ambiental", e);
	        }
	    }

	private void ingresarFichaGeneral(long idTask, long idProcessInstance,boolean marcaAgua) throws Exception {
		ImpresionFichaGeneralController impresionFichaGeneralController = JsfUtil
				.getBean(ImpresionFichaGeneralController.class);
		MarcoLegalPmaImprimirBean marcoLegalPmaBean = JsfUtil.getBean(MarcoLegalPmaImprimirBean.class);
		File archivoTmp = impresionFichaGeneralController.imprimirFichaPdf();
		File archivoArticulosTmp = marcoLegalPmaBean.exportarPdf();
		List<File> listaFiles = new ArrayList<File>();
		listaFiles.add(archivoTmp);
		listaFiles.add(archivoArticulosTmp);
		FileOutputStream file;
		File archivocacatenadoTmp;
		if(marcaAgua)
		{
			File archivoUnirTmp = UtilFichaMineria.unirPdf(listaFiles, "Ficha_Ambiental");                
	        archivocacatenadoTmp= JsfUtil.fileMarcaAgua(archivoUnirTmp, "BORRADOR",BaseColor.GRAY);
		}else{
			archivocacatenadoTmp= UtilFichaMineria.unirPdf(listaFiles, "Ficha_Ambiental"); 
		}
		
		Path path = Paths.get(archivocacatenadoTmp.getAbsolutePath());
		byte[] archivo = Files.readAllBytes(path);
		File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(archivocacatenadoTmp.getName()));
		file = new FileOutputStream(archivoFinal);
		file.write(archivo);
		file.close();
		categoriaIIFacade.ingresarDocumentoCategoriaII(archivoFinal, proyectosBean.getProyecto().getId(), proyectosBean
				.getProyecto().getCodigo(), idProcessInstance, idTask,
				TipoDocumentoSistema.TIPO_DOCUMENTO_REGISTRO_AMBIENTAL, "Registro Ambiental");
	}

	public void descargarLicencia() {
		try {
			byte[] archivoRegistro = categoriaIIFacade.recuperarDocumentoCategoriaII(proyecto.getId(),TipoDocumentoSistema.TIPO_DOCUMENTO_RESOLUCION_REGISTRO_AMBIENTAL);
			if(archivoRegistro==null)
			{
				guardarRegistroAmbientalDocumento(1);
				archivoRegistro = categoriaIIFacade.recuperarDocumentoCategoriaII(proyecto.getId(),TipoDocumentoSistema.TIPO_DOCUMENTO_RESOLUCION_REGISTRO_AMBIENTAL);
			}
			
			if (archivoRegistro != null) {
				UtilFichaMineria.descargar(archivoRegistro, "Resolución_Registro_Ambiental_" + proyecto.getCodigo());
				descargarRegistro = true;
			}
			
		} catch (Exception e) {
			LOG.error("Error al descargar el Registro Ambiental", e);
		}
	}

	private void guardarRegistroAmbientalDocumento(Integer i) {
		try {
			FichaAmbientalPmaController fichaAmbientalPmaController = JsfUtil.getBean(FichaAmbientalPmaController.class);
			if(fichaAmbientalPmaController.subirLicenciaAmbiental(true,i) == null){
				descargarRegistro = false;
			}
		} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al realizar la operación. Por favor intente más tarde.");
		}
	}

	public void redireccionarBandeja() {
		if (descargarFicha && descargarRegistro) {
			setMensajeFinalizar("- Estimado Usuario ha culminado satisfactoriamente la obtención de su Registro Ambiental, por favor dirigirse al listado de proyectos en resumen de etapas y documentos adjuntos, se descargará la Resolución Ambiental original.                     ");
			guardarRegistroAmbientalDocumento(2);
			if(proyectosBean.getProyectoRcoa() != null && proyectosBean.getProyectoRcoa().getId() != null)
			{
//				fichaAmbientalMineria = new FichaAmbientalMineria();
//				fichaAmbientalMineria.setProyectoLicenciamientoAmbiental(null);
//				fichaAmbientalMineria.setProyectoLicenciaCoa(proyectoLicenciaCoa);
//				fichaAmbientalMineria.setFinalizado(true);
//				try {
//					fichaAmbientalMineriaFacade.guardarFicha(fichaAmbientalMineria);
//				} catch (ServiceException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
			else if(descargarRegistro){
			try {				
				
				if (proyecto.getAreaResponsable().getTipoArea().getId()!=3){
					Map<String, Object> variables = procesoFacade
					        .recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
					                .getProcessInstanceId());
					Boolean intersecaBP = Boolean.parseBoolean((String) variables.get("intersecaBP"));
	                Boolean intersecaSNAP = Boolean.parseBoolean((String) variables.get("intersecaSNAP"));
	                NotificacionAutoridadesController mail_a = new NotificacionAutoridadesController();
	                List<Usuario> usuarios_contactocoor= new ArrayList<Usuario>();
	                List<Usuario> usuarios_contactoautamb= new ArrayList<Usuario>();
	                List<Usuario> usuarios_General= new ArrayList<Usuario>();
	                UbicacionesGeografica canton = new UbicacionesGeografica();
	                UbicacionesGeografica provincias = new UbicacionesGeografica();
	                UbicacionesGeografica parroquia = new UbicacionesGeografica();
	                usuarios_General= usuarioFacade.buscarUsuarioPorRol("NOTIFICACIONES GENERAL");    	                   	                    	               
	                
	                try {
	                	parroquia = proyecto
		                        .getProyectoUbicacionesGeograficas().get(0)
		                        .getUbicacionesGeografica();
						canton = ubicacionGeograficaFacade.buscarPorId(parroquia
						        .getUbicacionesGeografica().getId());
						provincias = ubicacionGeograficaFacade.buscarPorId(canton
		                        .getUbicacionesGeografica().getId());
					} catch (ServiceException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	                
	                
	                if (!proyecto.getAreaResponsable().getAreaAbbreviation().equals("DNPCA")){	                		                
	                usuarios_contactocoor= usuarioFacade.buscarUsuariosPorRolYArea("COORDINADOR PROVINCIAL DE REGISTRO", proyecto.getAreaResponsable());
	                usuarios_contactoautamb=usuarioFacade.buscarUsuariosPorRolYArea("AUTORIDAD AMBIENTAL", proyecto.getAreaResponsable());
	                }
							try {
								for (int i = 0; i < usuarios_contactocoor.size(); i++) {
									
									try {
										listContacto = contactoFacade.buscarPorPersona(usuarios_contactocoor.get(i).getPersona());
										for (int j = 0; j < listContacto.size(); j++) {
											if (listContacto.get(j).getFormasContacto().getId().equals(5)){
												mail_a.sendEmailAutoridades(" Registro "," el Registro ",
														listContacto.get(j).getValor(),
														"Registro final del proyecto",
														"Este correo fue enviado usando JavaMail",
														proyecto.getCodigo(), proyecto.getNombre(), proyecto.getCatalogoCategoria().getDescripcion(),provincias.getNombre(), usuarios_contactocoor.get(i), loginBean.getUsuario());
												Thread.sleep(2000);
											}																					
										}
									} catch (ServiceException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
								listContacto= new ArrayList<Contacto>();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						
							try {
								for (int i = 0; i < usuarios_contactoautamb.size(); i++) {
									try {
										listContacto = contactoFacade.buscarPorPersona(usuarios_contactoautamb.get(i).getPersona());
										for (int j = 0; j < listContacto.size(); j++) {	
											if (listContacto.get(j).getFormasContacto().getId().equals(5)){
												mail_a.sendEmailAutoridades(" Registro "," el Registro ",
														listContacto.get(j).getValor(),
														"Registro final del proyecto",
														"Este correo fue enviado usando JavaMail",
														proyecto.getCodigo(), proyecto.getNombre(), proyecto.getCatalogoCategoria().getDescripcion(),provincias.getNombre(), usuarios_contactoautamb.get(i), loginBean.getUsuario());
												Thread.sleep(2000);
											}
										}
									} catch (ServiceException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}																	
								}
								listContacto= new ArrayList<Contacto>();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							try {
								for (int i = 0; i < usuarios_General.size(); i++) {
									try {
										listContacto = contactoFacade.buscarPorPersona(usuarios_General.get(i).getPersona());
										for (int j = 0; j < listContacto.size(); j++) {	
											if (listContacto.get(j).getFormasContacto().getId().equals(5)){
												mail_a.sendEmailAutoridades(" Registro "," el Registro ",
														listContacto.get(j).getValor(),
														"Registro final del proyecto",
														"Este correo fue enviado usando JavaMail",
														proyecto.getCodigo(), proyecto.getNombre(), proyecto.getCatalogoCategoria().getDescripcion(),provincias.getNombre(), usuarios_General.get(i), loginBean.getUsuario());
												Thread.sleep(2000);
											}
										}
										
									} catch (ServiceException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}																	
								}
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}						
							
					}
				
			} catch (JbpmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}		
			
		//	if(generaDesechosEspeciales() || validarGenerador()) { //vear: 17/03/2016: comentado para que se finalice el proyecto
                if (proyecto.getTipoSector().getId().equals(TipoSector.TIPO_SECTOR_MINERIA)){
					try {
//						fichaAmbientalMineriaFacade.cambiarEstadoFinalizacion(fichaAmbiental.getProyectoLicenciamientoAmbiental().getId());
						fichaAmbientalMineriaFacade.cambiarEstadoFinalizacion(proyectosBean.getProyecto().getId());
					} catch (ServiceException e) {
						e.printStackTrace();
					}
				}
				else{
					FichaAmbientalPma fichaFinalizar= fichaAmbientalPmaFacade.getFichaAmbientalPorIdProyecto(proyectosBean.getProyecto().getId());
					fichaAmbiental.setNumeroOficio(fichaFinalizar.getNumeroOficio());
					fichaAmbiental.setNumeroLicencia(fichaFinalizar.getNumeroLicencia());
					fichaAmbientalPmaFacade.cambiarEstadoFinalizacion(fichaAmbiental);
				}


				setMensajeFinalizar(getMensajeFinalizar()
						+ "- Para iniciar el proceso Registro/Actualización de Generador de Desechos Peligrosos "
						+ "y/o Especiales debe ir a la bandeja de tareas.");
//			}
			RequestContext.getCurrentInstance().execute("PF('continuarDialog').show();");
			}
		} else {
			JsfUtil.addMessageError("Debes descargar los documentos para ir a la bandeja de tareas.");
		}
	}

	public String direccionar() {

		try {
			taskBeanFacade.approveTask(loginBean.getNombreUsuario(), bandejaTareasBean.getTarea().getTaskId(),
					bandejaTareasBean.getTarea().getProcessInstanceId(), null, loginBean.getPassword(),
					Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(),
					Constantes.getNotificationService());

			boolean saltarRGD = false;
			
			if(proyectosBean.getProyectoRcoa() != null && proyectosBean.getProyectoRcoa().getId() != null)
			{
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
				return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
			}
			if (proyecto.getTipoSector().getId().equals(TipoSector.TIPO_SECTOR_HIDROCARBUROS)
					&& (proyecto.getCatalogoCategoria().getCategoria().getId().equals(Categoria.CATEGORIA_III) || proyecto
							.getCatalogoCategoria().getCategoria().getId().equals(Categoria.CATEGORIA_IV))) {
				saltarRGD = true;
			}

			if (!saltarRGD && (proyecto.getGeneraDesechos() || validarGenerador()) && result == null) {
				String codigoMineriaSondeo=fichaAmbientalMineria020Facade.cargarPerforacionExplorativa(proyecto).getCodeUpdate();
				if(!proyecto.getCatalogoCategoria().getCodigo().equals("21.02.02.03") && !proyecto.getCatalogoCategoria().getCodigo().equals("21.02.03.05")
						&& !codigoMineriaSondeo.equals("21.02.02.01") && !codigoMineriaSondeo.equals("21.02.03.06"))
				{
					result = registroGeneradorDesechosFacade.iniciarEmisionProcesoRegistroGenerador(
							JsfUtil.getLoggedUser(), RegistroGeneradorTramiteResolver.class, proyecto);
					proyecto.setRgdEncurso(true);
					proyectoLicenciamientoAmbientalFacade.actualizarProyecto(proyecto);
				}	
			}
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
		} catch (Exception e) {
			LOG.error("Error no se puede enviar", e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}
		return "";

	}

	public void cambiarEstadoTarea(){
		// si la tarea actual es "Descargar documentos de Registro Ambiental " cambio el estado de latarea a exited para que no se muestre en las tareas por las que paso el proceso
		if(bandejaTareasBean.getTarea().getTaskName().toLowerCase().contains("descargar documentos de registro ambiental")){
			Long processInstanceId = bandejaTareasBean.getProcessId();
			conexionBpms.updateStatusBamTaskSummary(processInstanceId, bandejaTareasBean.getTarea().getTaskId(), JsfUtil.getLoggedUser(), "Exited");
		}
	}
	
	/**
	 * <b> Metodo que valida si existen desechos sin codigo de generador. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 18/09/2015]
	 * </p>
	 *
	 * @return boolean: true existe el desecho sin codigo
	 * @throws ServiceException
	 *             : Excepcion
	 */
	public boolean validarGenerador() {
		try {
			aprobacionRequisitosTecnicos = aprobacionRequisitosTecnicosFacade.getAprobacionRequisitosTecnicos(proyecto.getCodigo());
			boolean existe = false;
			if (aprobacionRequisitosTecnicos != null && aprobacionRequisitosTecnicos.isGestion()) {
				existe = eliminacionDesechoFacade.validarDesechoSinGeneradorPorProyecto(aprobacionRequisitosTecnicos.getProyecto(), aprobacionRequisitosTecnicos.getSolicitud());
			}
			return existe;
		} catch (ServiceException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean generaDesechosEspeciales() {
		return proyecto.getGeneraDesechos() != null && proyecto.getGeneraDesechos();
	}

	public boolean proyectoExante() {
		return proyecto.getTipoEstudio()!=null?proyecto.getTipoEstudio().getId().equals(TipoEstudio.ESTUDIO_EX_ANTE):true;
	}

	public boolean proyectoExpost() {
		return proyecto.getTipoEstudio()!=null?proyecto.getTipoEstudio().getId().equals(TipoEstudio.ESTUDIO_EX_POST):false;
	}

	private String getNotaResponsabilidadInformacionRegistroProyecto(Usuario persona) {
		String[] parametros = { persona.getPersona().getNombre(), persona.getPin() };
		return DocumentoPDFPlantillaHtml.getPlantillaConParametros("nota_responsabilidad_certificado_interseccion",
				parametros);
	}

	public Boolean validarFichaCompletada() {
		if((proyectosBean.getProyectoRcoa() == null || proyectosBean.getProyectoRcoa().getId() == null) && (proyectosBean.getProyecto() != null && proyectosBean.getProyecto().getId() != null))
		{
			CatalogoCategoriaSistema catalogo = proyectosBean.getProyecto().getCatalogoCategoria();
			fichaAmbiental = fichaAmbientalPmaFacade.getFichaAmbientalPorIdProyecto(proyectosBean.getProyecto().getId());
			if (catalogo.getCodigo().equals(Constantes.SECTOR_HIDROCARBURO_CODIGO)) {
				fichaAmbiental.setValidarDescripcionAreaImplantacion(true);
			}
			try {
				return fichaAmbiental.getValidarDatosGenerales() != null
						&& fichaAmbiental.getValidarMarcoLegalReferencial() != null
						&& fichaAmbiental.getValidarDescripcionProyectoObraActividad() != null
						&& fichaAmbiental.getValidarDescripcionProceso() != null
						&& fichaAmbiental.getValidarDescripcionAreaImplantacion() != null
						&& fichaAmbiental.getValidarPrincipalesImpactosAmbientales() != null
						// && fichaAmbiental.getValidarPlanManejoAmbiental() != null
						// && fichaAmbiental
						// .getValidarCronogramaConstruccionOperacionProyecto() !=
						// null
						&& fichaAmbiental.getValidarCronogramaValoradoPlanManejoAmbiental() != null
						&& fichaAmbiental.getValidarDatosGenerales() && fichaAmbiental.getValidarMarcoLegalReferencial()
						&& fichaAmbiental.getValidarDescripcionProyectoObraActividad()
						&& fichaAmbiental.getValidarDescripcionProceso()
						&& fichaAmbiental.getValidarDescripcionAreaImplantacion()
						&& fichaAmbiental.getValidarPrincipalesImpactosAmbientales()
						// && fichaAmbiental.getValidarPlanManejoAmbiental()
						// && fichaAmbiental
						// .getValidarCronogramaConstruccionOperacionProyecto()
						&& fichaAmbiental.getValidarCronogramaValoradoPlanManejoAmbiental();
			} catch (Exception e) {
				return false;
			}
		}
		else
			return true;

		// return false;
	}

	public boolean mostrar30() {
		boolean a = proyectoExpost() && (generaDesechosEspeciales() || validarGenerador());
		return a;
	}

	public boolean mostrar60() {
		boolean a = proyectoExante() && (generaDesechosEspeciales() || validarGenerador());
		return a;
	}

	public boolean mostrar00() {
		boolean a = false;
		if(proyectosBean.getProyectoRcoa() != null && proyectosBean.getProyectoRcoa().getId() != null)
		{
			a=false;
		}
		else
		{
		if(proyecto.getTipoEstudio().getId().equals(TipoEstudio.AUDITORIA_FINES_LICENCIAMIENTO) ||
				proyecto.getTipoEstudio().getId().equals(TipoEstudio.ESTUDIO_EMISION_INCLUSION_AMBIENTAL))
			a = (generaDesechosEspeciales() || validarGenerador());
		}
		return a;
	}

	/***** módulo de Encuesta de Satisfacción de Servicios *****/
	@Setter
	@Getter
	private boolean showSurvey = false;
	
	public void showDialogSurvey() {
		showSurvey = true;
	}
	
	// metodo para crear url de la encuesta
		public String urlLinkSurvey() {
			String url = surveyLink;
			String usuarioUrl = proyecto.getUsuario()==null?proyectosBean.getProyectoRcoa().getUsuario().getNombre():proyecto.getUsuario().getNombre();
			String proyectoUrl = proyecto.getCodigo()==null?proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental():proyecto.getCodigo();
			String appUlr = "suia";
			String tipoPerUrl = (organizacion!=null)?"juridico":"natural";
			String tipoUsr = "externo";
			url = url + "/faces/index.xhtml?" 
					+ "usrid=" + usuarioUrl + "&app=" + appUlr + "&project=" + proyectoUrl 
					+ "&tipoper=" + tipoPerUrl + "&tipouser=" + tipoUsr;
			return url;
		}
	
	// método para mostrar el enlace de la encuesta	
	public boolean showLinkSurvey() {
		if(proyectosBean.getProyectoRcoa() != null && proyectosBean.getProyectoRcoa().getId() != null)
		{
			// si el proyecto tiene registrada la encuesta
			if(surveyResponseFacade.findByProject(proyectosBean.getProyectoRcoa().getCodigoUnicoAmbiental())) {
				return false;
			} else {
				return true;
			}			
		}
		// validar que no sea Ente Acreditado arty_id=3
		if(proyecto.getAreaResponsable().getTipoArea().getId() != 3) {
			// si el proyecto es Categoría I
			if(proyecto.getCatalogoCategoria().getCategoria().getDescripcion().equals("Categoría II") 
					|| proyecto.getCatalogoCategoria().getCategoria().getDescripcion().equals("Categoría I")){
				// si el proyecto tiene registrada la encuesta
				if(surveyResponseFacade.findByProject(proyecto.getCodigo())) {
					return false;
				} else {
					return true;
				}
			}
		}
		
		return false;
	}
	/****** fin módulo encuesta ******/
	
}
