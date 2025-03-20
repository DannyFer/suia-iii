package ec.gob.ambiente.prevencion.licenciamientoambiental.controller;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.jbpm.facade.TaskBeanFacade;
import ec.gob.ambiente.prevencion.licenciamientoambiental.informes.bean.InformeTecnicoGeneralLABean;
import ec.gob.ambiente.prevencion.tdr.controller.RevisarDocumentacionGeneralController;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.InformeTecnicoGeneralLA;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.eia.facade.LicenciaAmbientalFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
import ec.gob.ambiente.suia.prevencion.licenciamientoAmbiental.facade.InformeOficioFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class FirmarDocumentacionJuridicoEiaController implements Serializable {
    private static final Logger LOGGER = Logger
            .getLogger(RevisarDocumentacionGeneralController.class);
    private static final long serialVersionUID = -875087443147320594L;

    @EJB
    private ProcesoFacade procesoFacade;
    @EJB
    private DocumentosFacade documentosFacade;
    @EJB
    private LicenciaAmbientalFacade licenciaAmbientalFacade;

    @EJB
    private InformeOficioFacade informeOficioFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;

    @EJB(lookup = Constantes.JBPM_EJB_TASK_BEAN)
    private TaskBeanFacade taskBeanFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @ManagedProperty(value = "#{proyectosBean}")
    @Getter
    @Setter
    private ProyectosBean proyectosBean;

    @Getter
    @Setter
    private Documento documento;

    private String documentOffice = "";

    @Getter
    @Setter
    private boolean mostrarInforme;

    private int docJuridicoVersion;


    @Getter
    @Setter
    private boolean subido;
    @Getter
    @Setter
    private boolean token;

	@Getter
	private boolean esPronunciamientoAprobacion;
    @EJB
    private UsuarioFacade usuarioFacade;
//    private UbicacionGeograficaFacade ubicacionGeograficaFacade;
    
    @EJB
	ContactoFacade contactoFacade;
	List<Contacto> listContacto = new ArrayList<Contacto>();

	@Getter
	@Setter
	private Boolean descargaroficio=false;

    @PostConstruct
    public void init() {
        try {

			this.esPronunciamientoAprobacion=Boolean
					.parseBoolean(JsfUtil.getCurrentTask().getVariable("esPronunciamientoAprobacion").toString());

            token = loginBean.getUsuario().getToken() != null && loginBean.getUsuario().getToken();
            InformeTecnicoGeneralLABean informeTecnicoGeneralLABean = (InformeTecnicoGeneralLABean) JsfUtil.getBean(InformeTecnicoGeneralLABean.class);
            informeTecnicoGeneralLABean.configuracionGeneral();
            informeTecnicoGeneralLABean.visualizarOficio(token);
            informeTecnicoGeneralLABean.subirDocuemntoAlfresco();
            if (token) {
                documentOffice = documentosFacade.direccionDescarga(InformeTecnicoGeneralLA.class.getSimpleName()
                        , proyectosBean.getProyecto().getId(), TipoDocumentoSistema.TIPO_INFORME_TECNICO_GENERAL_LA);
            }
            List<Documento> documentos = documentosFacade.documentoXTablaIdXIdDoc(proyectosBean.getProyecto().getId(),
                    InformeTecnicoGeneralLA.class.getSimpleName(), TipoDocumentoSistema.TIPO_INFORME_TECNICO_GENERAL_LA);
            if (documentos.size() > 0) {
                documento = documentos.get(0);

                String id = documento.getIdAlfresco();
                this.docJuridicoVersion = Integer.parseInt(id.substring(id.length()-1));
            } else {
                JsfUtil.addMessageError("Error al realizar la operación.");

            }
        } catch (Exception e) {
            LOGGER.error(e);
            JsfUtil.addMessageError("Error al cargar los datos. Por favor intente más tarde.");
        }

    }

    public String firmarDocumento() {
        try {


            DigitalSign firmaE = new DigitalSign();
            return firmaE.sign(documentOffice, loginBean.getUsuario().getNombre()); // loginBean.getUsuario()
        } catch (Throwable e) {
            LOGGER.error(e);
            JsfUtil.addMessageError("Error al realizar la operación.");
            return "";
        }
    }

    public boolean verificaToken (){
		token = false;
		if (JsfUtil.getLoggedUser().getToken() != null && JsfUtil.getLoggedUser().getToken())
			token = true;
		return token;
	}

	
	public void guardarToken(){
		Usuario usuario= JsfUtil.getLoggedUser();
		usuario.setToken(token);
		try {
			usuarioFacade.guardar(usuario);
			verificaToken ();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
	public String completarTarea() {
        try {
            if (documento != null) {
                if (subido) {
//                    documentosFacade.replaceDocumentContent(documento);
                    licenciaAmbientalFacade.ingresarPronunciamiento(documento,
                            proyectosBean.getProyecto().getId(), proyectosBean.getProyecto().getCodigo(),
                            bandejaTareasBean.getProcessId(), bandejaTareasBean.getTarea().getTaskId(), TipoDocumentoSistema.TIPO_INFORME_TECNICO_GENERAL_LA);

                }
            }
            InformeTecnicoGeneralLA informeTecnicoGeneralLA = informeOficioFacade
                    .obtenerInformeTecnicoLAGeneralPorProyectoId(TipoDocumentoSistema.TIPO_INFORME_TECNICO_GENERAL_LA.getIdTipoDocumento(),
                            proyectosBean.getProyecto().getId());
            //informeTecnicoGeneralLA.setFinalizado(true);
            informeOficioFacade.guardarInformeTecnicoGeneralLA(informeTecnicoGeneralLA);

            String idAlfresco = documento.getIdAlfresco();

            idAlfresco = documento.getIdAlfresco().substring(0,idAlfresco.length()-1);
            idAlfresco= idAlfresco+""+(++this.docJuridicoVersion);

            documento.setIdAlfresco(idAlfresco);

            if (!token || documentosFacade.verificarFirmaVersion(idAlfresco)) {
                try {				
    				if (proyectosBean.getProyecto().getAreaResponsable().getTipoArea().getId()!=3){
    					Map<String, Object> variables = procesoFacade
    					        .recuperarVariablesProceso(loginBean.getUsuario(), bandejaTareasBean.getTarea()
    					                .getProcessInstanceId());
    					Boolean intersecaBP = Boolean.parseBoolean((String) variables.get("intersecaBP"));
    	                Boolean intersecaSNAP = Boolean.parseBoolean((String) variables.get("intersecaSNAP"));
    	                NotificacionAutoridadesController mail_a = new NotificacionAutoridadesController();
    	                List<Usuario> usuarios_contactocoor= new ArrayList<Usuario>();
    	                List<Usuario> usuarios_contactoautamb= new ArrayList<Usuario>();
    	                List<Usuario> usuarios_contactoBP= new ArrayList<Usuario>();
    	                List<Usuario> usuarios_contactoSNAP= new ArrayList<Usuario>();
    	                UbicacionesGeografica canton = new UbicacionesGeografica();
    	                UbicacionesGeografica provincias = new UbicacionesGeografica();
    	                UbicacionesGeografica parroquia = new UbicacionesGeografica();    	                    	               
    	                parroquia = proyectosBean.getProyecto()
						        .getProyectoUbicacionesGeograficas().get(0)
						        .getUbicacionesGeografica();
						canton = parroquia.getUbicacionesGeografica();
						provincias = canton.getUbicacionesGeografica();
    	                
    	                if (!proyectosBean.getProyecto().getAreaResponsable().getAreaAbbreviation().equals("DNPCA")){	                		                
    	                usuarios_contactocoor=usuarioFacade.buscarUsuariosPorRolYArea("COORDINADOR PROVINCIAL DE REGISTRO", proyectosBean.getProyecto().getAreaResponsable());
    	                usuarios_contactoautamb=usuarioFacade.buscarUsuariosPorRolYArea("AUTORIDAD AMBIENTAL", proyectosBean.getProyecto().getAreaResponsable());
    	                }
    	                List<Usuario> usuarios_planta_central= new ArrayList<Usuario>();
    	                List<Usuario> usuarios_PC_biodiversidad= new ArrayList<Usuario>();
    	                List<Usuario> usuarios_PC_forestal= new ArrayList<Usuario>();
    	                List<Usuario> usuarios_General= new ArrayList<Usuario>();
    	                usuarios_planta_central= usuarioFacade.buscarUsuarioPorRol("NOTIFICACIONES PLANTA CENTRAL");    	                   	                    	               
    	                usuarios_General= usuarioFacade.buscarUsuarioPorRol("NOTIFICACIONES GENERAL");    	                   	                    	               
    	                if (intersecaBP ){
//    	                	COORDINADOR FORESTAL
    	                	usuarios_contactoBP.addAll((List<Usuario>) usuarioFacade.buscarUsuariosPorRolYArea("COORDINADOR FORESTAL", proyectosBean.getProyecto().getAreaResponsable()));
    	                	usuarios_PC_forestal=usuarioFacade.buscarUsuarioPorRol("NOTIFICACIONES FORESTAL");
    	                }
    	                if (intersecaSNAP){
//    	                	COORDINADOR BIODIVERSIDAD	                	
    	                	usuarios_contactoSNAP.addAll((List<Usuario>) usuarioFacade.buscarUsuariosPorRolYArea("COORDINADOR BIODIVERSIDAD", proyectosBean.getProyecto().getAreaResponsable()));
    	                	 usuarios_PC_biodiversidad= usuarioFacade.buscarUsuarioPorRol("NOTIFICACIONES BIODIVERSIDAD");
    	                }
    	                
						try {
							for (int i = 0; i < usuarios_contactocoor.size(); i++) {
								listContacto = contactoFacade.buscarPorPersona(usuarios_contactocoor.get(i).getPersona());
								for (int j = 0; j < listContacto.size(); j++) {
									if (listContacto.get(j).getFormasContacto()
											.getId().equals(5)) {										
										mail_a.sendEmailAutoridades(" Licencia "," la Licencia ",listContacto.get(j).getValor(),"Registro final del proyecto","Este correo fue enviado usando JavaMail",proyectosBean.getProyecto().getCodigo(),proyectosBean.getProyecto().getNombre(),proyectosBean.getProyecto().getCatalogoCategoria().getCodigo()+ ","	+ proyectosBean.getProyecto().getCatalogoCategoria().getDescripcion(),provincias.getNombre(), usuarios_contactocoor.get(i), loginBean.getUsuario());
										Thread.sleep(2000);
									}
								}
							}
							listContacto = new ArrayList<Contacto>();
							for (int i = 0; i < usuarios_planta_central.size(); i++) {
								listContacto = contactoFacade.buscarPorPersona(usuarios_planta_central.get(i).getPersona());
								for (int j = 0; j < listContacto.size(); j++) {
									if (listContacto.get(j).getFormasContacto()
											.getId().equals(5)) {				
										mail_a.sendEmailAutoridades(" Licencia "," la Licencia ",listContacto.get(j).getValor(),
										"Registro final del proyecto",
										"Este correo fue enviado usando JavaMail",
										proyectosBean.getProyecto().getCodigo(), proyectosBean.getProyecto().getNombre(), proyectosBean.getProyecto().getCatalogoCategoria().getDescripcion(),provincias.getNombre(), usuarios_planta_central.get(i), loginBean.getUsuario());
										Thread.sleep(2000);
									}
								}																    						    						
						}
						listContacto = new ArrayList<Contacto>();							
							for (int i = 0; i < usuarios_PC_biodiversidad.size(); i++) {  
								listContacto = contactoFacade.buscarPorPersona(usuarios_PC_biodiversidad.get(i).getPersona());
								for (int j = 0; j < listContacto.size(); j++) {
									if (listContacto.get(j).getFormasContacto().getId().equals(5)) {
										mail_a.sendEmailAutoridades(" Licencia "," la Licencia ",listContacto.get(j).getValor(),
										"Registro final del proyecto",
										"Este correo fue enviado usando JavaMail",
										proyectosBean.getProyecto().getCodigo(), proyectosBean.getProyecto().getNombre(), proyectosBean.getProyecto().getCatalogoCategoria().getDescripcion(),provincias.getNombre(), usuarios_PC_biodiversidad.get(i), loginBean.getUsuario());
										Thread.sleep(2000);
									}
								}		    							    							
						}
							listContacto = new ArrayList<Contacto>();	
							for (int i = 0; i < usuarios_PC_forestal.size(); i++) {   
								listContacto = contactoFacade.buscarPorPersona(usuarios_PC_forestal.get(i).getPersona());
								for (int j = 0; j < listContacto.size(); j++) {
									if (listContacto.get(j).getFormasContacto().getId().equals(5)) {
										mail_a.sendEmailAutoridades(" Licencia "," la Licencia ",listContacto.get(j).getValor(),
										"Registro final del proyecto",
										"Este correo fue enviado usando JavaMail",
										proyectosBean.getProyecto().getCodigo(), proyectosBean.getProyecto().getNombre(), proyectosBean.getProyecto().getCatalogoCategoria().getDescripcion(),provincias.getNombre(), usuarios_PC_forestal.get(i), loginBean.getUsuario());
										Thread.sleep(2000);
									}
								}
						}
							listContacto = new ArrayList<Contacto>();
							
							for (int i = 0; i < usuarios_contactoautamb.size(); i++) {    
								listContacto = contactoFacade.buscarPorPersona(usuarios_contactoautamb.get(i).getPersona());
								for (int j = 0; j < listContacto.size(); j++) {
									if (listContacto.get(j).getFormasContacto().getId().equals(5)) {
										mail_a.sendEmailAutoridades(" Licencia "," la Licencia ",listContacto.get(j).getValor(),
										"Registro final del proyecto",
										"Este correo fue enviado usando JavaMail",
										proyectosBean.getProyecto().getCodigo(), proyectosBean.getProyecto().getNombre(), proyectosBean.getProyecto().getCatalogoCategoria().getDescripcion(),provincias.getNombre(), usuarios_contactoautamb.get(i), loginBean.getUsuario());
										Thread.sleep(2000);
									}
								}							    							
						}
							listContacto = new ArrayList<Contacto>();
							for (int i = 0; i < usuarios_contactoBP.size(); i++) {
								listContacto = contactoFacade.buscarPorPersona(usuarios_contactoBP.get(i).getPersona());
								for (int j = 0; j < listContacto.size(); j++) {
									if (listContacto.get(j).getFormasContacto().getId().equals(5)) {
										mail_a.sendEmailAutoridades(" Licencia "," la Licencia ",listContacto.get(j).getValor(),
										"Registro final del proyecto",
										"Este correo fue enviado usando JavaMail",
										proyectosBean.getProyecto().getCodigo(), proyectosBean.getProyecto().getNombre(), proyectosBean.getProyecto().getCatalogoCategoria().getDescripcion(),provincias.getNombre(), usuarios_contactoBP.get(i), loginBean.getUsuario());
										Thread.sleep(2000);
									}
								}    							
						}
							listContacto = new ArrayList<Contacto>();							
							for (int i = 0; i < usuarios_contactoSNAP.size(); i++) {
								listContacto = contactoFacade.buscarPorPersona(usuarios_contactoSNAP.get(i).getPersona());
								for (int j = 0; j < listContacto.size(); j++) {
									if (listContacto.get(j).getFormasContacto().getId().equals(5)) {
										mail_a.sendEmailAutoridades(" Licencia "," la Licencia ",listContacto.get(j).getValor(),
										"Registro final del proyecto",
										"Este correo fue enviado usando JavaMail",
										proyectosBean.getProyecto().getCodigo(), proyectosBean.getProyecto().getNombre(), proyectosBean.getProyecto().getCatalogoCategoria().getDescripcion(),provincias.getNombre(), usuarios_contactoSNAP.get(i), loginBean.getUsuario());
										Thread.sleep(2000);
									}
								}
						}	
							listContacto = new ArrayList<Contacto>();							
							for (int i = 0; i < usuarios_General.size(); i++) {
								listContacto = contactoFacade.buscarPorPersona(usuarios_General.get(i).getPersona());
								for (int j = 0; j < listContacto.size(); j++) {
									if (listContacto.get(j).getFormasContacto()
											.getId().equals(5)) {										
										mail_a.sendEmailAutoridades(" Licencia "," la Licencia ",listContacto.get(j).getValor(),"Registro final del proyecto","Este correo fue enviado usando JavaMail",proyectosBean.getProyecto().getCodigo(),proyectosBean.getProyecto().getNombre(),proyectosBean.getProyecto().getCatalogoCategoria().getCodigo()+ ","	+ proyectosBean.getProyecto().getCatalogoCategoria().getDescripcion(),provincias.getNombre(), usuarios_General.get(i), loginBean.getUsuario());
										Thread.sleep(2000);
									}
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
    			}
                taskBeanFacade.approveTask(loginBean.getNombreUsuario(),
                        bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null, loginBean.getPassword(),
                        Constantes.getUrlBusinessCentral(), Constantes.getRemoteApiTimeout(), Constantes.getNotificationService());
                this.documentosFacade.actualizarDocumento(documento);
                JsfUtil.addMessageInfo("Se realizó correctamente la operación.");
                procesoFacade.envioSeguimientoLicenciaAmbiental(loginBean.getUsuario(), bandejaTareasBean.getProcessId());
                return JsfUtil.actionNavigateTo("/bandeja/bandejaTareas.jsf");
            } else {
                JsfUtil.addMessageError("El documento no está firmado.");
                return "";
            }
        } catch (JbpmException e) {
            LOGGER.error(e);
            JsfUtil.addMessageError("Error al realizar la operación.");
        } catch (Exception e) {
            LOGGER.error(e);
            JsfUtil.addMessageError("Error al realizar la operación.");
        }
        return "";
    }

    public void validarTareaBpm() {

        String url = "/prevencion/licenciamiento-ambiental/documentos/firmarDocumentacionJuridico.jsf";

        JsfUtil.validarPaginasUrlTareasBpm(bandejaTareasBean.getTarea(), url);
    }


    public StreamedContent getStream() throws Exception {
        DefaultStreamedContent content = new DefaultStreamedContent();
        if (documento != null) {
            if (documento.getContenidoDocumento() == null) {
                documento.setContenidoDocumento(documentosFacade.descargar(documento.getIdAlfresco()));
            }
            content = new DefaultStreamedContent(new ByteArrayInputStream(
                    documento.getContenidoDocumento()), "application/pdf");
            content.setName(documento.getNombre());
        }
        return content;

    }

    public void uploadListenerDocumentos(FileUploadEvent event) {
		if (descargaroficio==true){
        byte[] contenidoDocumento = event.getFile().getContents();
        documento.setContenidoDocumento(contenidoDocumento);
        documento.setNombre(event.getFile().getFileName());
        subido = true;
			JsfUtil.addMessageInfo("Documento subido exitosamente");
	} else {
		JsfUtil.addMessageError("Debes descargar el documento");
	}
	}

	public void descargarOficio() {
		if (documento != null) {
			descargaroficio = true;
		}

}
}
