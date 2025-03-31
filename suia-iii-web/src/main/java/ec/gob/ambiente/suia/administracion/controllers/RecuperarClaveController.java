package ec.gob.ambiente.suia.administracion.controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.administracion.bean.PromotorBean;
import ec.gob.ambiente.suia.administracion.bean.RecuperarClaveBean;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.domain.AuditarUsuario;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.NotificacionesMails;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.RolUsuario;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoMensajeMailEnum;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * @author ishmael
 */
@ManagedBean
@ViewScoped
public class RecuperarClaveController implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -7718951738716211642L;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(RecuperarClaveController.class);
    @EJB
    private UsuarioFacade usuarioFacade;
    @EJB
    private ContactoFacade contactoFacade;
    @EJB
    private OrganizacionFacade organizacionFacade;
    @Getter
    @Setter
    private RecuperarClaveBean recuperarClaveBean;
    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
    
    @Getter
    @Setter
    private PromotorBean promotorBean;
    

    @Getter
    private String completeURL;
    
    @Getter
    private String usuarioSH1;
   @PostConstruct
public void inicio() {	   
	        recuperarClaveBean = new RecuperarClaveBean();
	        completeURL = "";
	        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	        StringBuffer requestURL = request.getRequestURL();
	        if (request.getQueryString() != null) {
	            requestURL.append("?").append(request.getQueryString());
	        }
	        completeURL = requestURL.toString();
	        if (completeURL.contains("key=")){
	        	int key=completeURL.indexOf("key=");
		        int code=completeURL.indexOf("&code=");
		        usuarioSH1=completeURL.substring(key+4, code);
		        try {
		        	Usuario usuarioConsultado = usuarioFacade.buscarUsuarioUrl(completeURL);
			        Date nuevaFecha = new Date();
			        Calendar cal = Calendar.getInstance(); 
	                cal.setTime(usuarioConsultado.getFechaLinkExpiracion()); 
	                cal.add(Calendar.MINUTE, 15);
	                Integer HoraActual=nuevaFecha.getHours();
	                Integer minutoActual= nuevaFecha.getMinutes();              
	                Integer hora=cal.getTime().getHours();
	                Integer minuto= cal.getTime().getMinutes();
	                
	                nuevaFecha = cal.getTime();                
	                if (!usuarioConsultado.getEstadoCodigoCapcha()){
	                	if (HoraActual>=hora && minutoActual>=minuto){
				        	JsfUtil.redirectTo("/linkClaveCaducada.jsf");
				        }
	                }else{
	                	JsfUtil.redirectTo("/claveCambiada.jsf");
	                }
			        
			        
				} catch (Exception e) {
					// TODO: handle exception
					JsfUtil.redirectTo("/claveCambiada.jsf");
				}
		        
	        }
	        
	    
    }

    public synchronized void recuperar() {
        try {
            if (!validarCampos()) {
                return;
            }
            recuperarClaveBean.setUsuario(usuarioFacade
                    .buscarUsuario(recuperarClaveBean.getLogin()));
            if (recuperarClaveBean.getUsuario() != null) {
                Persona persona = recuperarClaveBean.getUsuario().getPersona();
                Organizacion organizacion = organizacionFacade
                        .buscarPorPersona(persona,recuperarClaveBean.getUsuario().getNombre());
                List<Contacto> listaContacto;
                if (organizacion != null) {
                    listaContacto = contactoFacade
                            .buscarPorOrganizacion(organizacion);
                } else {
                    listaContacto = contactoFacade.buscarPorPersona(persona);
                }
                if (validarMail(listaContacto, recuperarClaveBean.getMail())) {
                    recuperarClaveBean.getUsuario().setTempPassword(
                            JsfUtil.generatePassword());
                    recuperarClaveBean.getUsuario().setPassword(
                            JsfUtil.claveEncriptadaSHA1(recuperarClaveBean
                                    .getUsuario().getTempPassword()));
                    usuarioFacade.modificar(
                            recuperarClaveBean.getUsuario(),
                            cargarMails(recuperarClaveBean.getUsuario()
                                            .getNombre(), recuperarClaveBean
                                            .getUsuario().getTempPassword(),
                                    recuperarClaveBean.getMail()));
                    
                    if(recuperarClaveBean.getUsuario().getRolUsuarios().size()>0){
                    for (RolUsuario rolUsuario : recuperarClaveBean.getUsuario().getRolUsuarios()) {
						if(rolUsuario.getRol().getNombre().equals("TAXA-AUTORIDAD FISCALÍA")){
							usuarioFacade.notificarFiscal(cargarMailsFiscal(recuperarClaveBean.getUsuario()
                                    .getNombre(), recuperarClaveBean.getUsuario()
                                    .getPersona().getNombre(), Constantes.getNotificacionesFiscalia(),recuperarClaveBean.getMail(),
                                    recuperarClaveBean.getUsuario().getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica().getNombre()));
							}
						}
					}
                    
                    // ejecuta la migracion de usuarios
                    try {
                    	usuarioFacade.actualizarContraseniaVerde(recuperarClaveBean.getUsuario().getPassword(), recuperarClaveBean.getUsuario().getNombre());
//                        usuarioFacade.ejecutarMigracionUsuario();
                    } catch (Exception e) {
                        LOG.error("Error al migrar el usuario.", e);
                    }
                    JsfUtil.redirectTo("/claveRecuperada.jsf");
                } else {
                    JsfUtil.addMessageError("El correo electrónico ingresado no coincide con el registrado en el sistema.");
                }
            } else {
                JsfUtil.addMessageError("Usuario no encontrado.");
            }
        } catch (ServiceException e) {
            JsfUtil.addMessageError("Error al realizar la operación. Por favor intente más tarde.");
            LOG.error(e, e);
        } catch (RuntimeException e) {
            LOG.error(e, e);
            JsfUtil.addMessageError("Error al realizar la operación. Por favor intente más tarde.");
        }
    }

    
    
    public synchronized void recuperarNuevaContrasenia() {
        try {           	
                Persona persona = recuperarClaveBean.getUsuario().getPersona();
                Organizacion organizacion = organizacionFacade
                        .buscarPorPersona(persona,recuperarClaveBean.getUsuario().getNombre());
                List<Contacto> listaContacto;
                if (organizacion != null) {
                    listaContacto = contactoFacade
                            .buscarPorOrganizacion(organizacion);
                } else {
                    listaContacto = contactoFacade.buscarPorPersona(persona);
                }
                
                promotorBean.getUsuario().setFechaRenovarPassusuario(new Date());
                promotorBean.getUsuario()
                .setPassword(
                        JsfUtil.claveEncriptadaSHA1(promotorBean
                                .getPassword()));
                usuarioFacade.modificar(promotorBean.getUsuario());                                
           
        } catch (ServiceException e) {
            JsfUtil.addMessageError("Error al realizar la operación. Por favor intente más tarde.");
            LOG.error(e, e);
        } catch (RuntimeException e) {
            LOG.error(e, e);
            JsfUtil.addMessageError("Error al realizar la operación. Por favor intente más tarde.");
        }
    }

    
    
    private boolean validarMail(List<Contacto> contactos, String mail) {
        for (Contacto contacto : contactos) {
            if (FormasContacto.EMAIL == contacto.getFormasContacto().getId()
                    .intValue()) {
                if (mail.equals(contacto.getValor())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean verificarCorreoValido(String email) {
        boolean valor = false;
        valor = !JsfUtil.validarMail(email);
        return valor;
    }

    private boolean validarCampos() {
        List<String> listaMensajes = new ArrayList<String>();
        if (recuperarClaveBean.getLogin() == null
                || recuperarClaveBean.getLogin().equals("")) {
            listaMensajes.add("El campo 'Ingrese usuario' es requerido.");
        }
        if (recuperarClaveBean.getMail() == null
                || recuperarClaveBean.getMail().equals("")) {
            listaMensajes
                    .add("El campo 'Ingrese Correo eletrónico' es requerido.");
        }
        if (!recuperarClaveBean.getMail().equals("")
                && verificarCorreoValido(recuperarClaveBean.getMail())) {
            listaMensajes
                    .add("La dirección de correo ingresada es incorrecta, por favor verifique.");
        }
        if (listaMensajes.isEmpty()) {
            return true;
        } else {
            JsfUtil.addMessageError(listaMensajes);
            return false;
        }
    }

    private NotificacionesMails cargarMails(final String login,
                                            final String clave, final String email) {
        NotificacionesMails nm = new NotificacionesMails();
        nm.setAsunto("Recuperar Contraseña");
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("<p>Estimado/a Se&ntilde;or/a ").append("</p>")
                .append("<br/> <br/>");
        mensaje.append("<p>Confirmamos que su solicitud de recuperar contrase&ntilde;a  en el sistema SUIA fue aprobada con los siguientes datos: </p><br/>");
        mensaje.append("<ul><li>Nombre de usuario: ").append(login)
                .append("</li>");
        mensaje.append("<li>Contrase&ntilde;a: ").append(clave).append("</li>")
                .append("</ul><br/>");
        mensaje.append(
                "<p>Una vez que haya ingresado al sistema es necesario que realice el cambio de contrase&ntilde;a, accediendo a su perfil de usuario, el mismo se encuentra en la parte izquierda de la segunda barra de la pantalla. Ver imagen 1")
                .append("</p><br/>");
        mensaje.append(
                "<p>Si usted desea realizar el registro de su proyecto, debe acceder al men&uacute; superior \"Proyectos\" y hacer clic en \"Registrar Proyecto\". Ver imagen 2")
                .append("</p>");
        mensaje.append("<br/><img src=\"")
                .append(Constantes.getHttpSuiaImagenInfoMail())
                .append("\" ><br/>");
        mensaje.append("<br/><p>Saludos,").append("</p>").append("<br/>");
        mensaje.append("<p>Ministerio del Ambiente y Agua").append("</p>")
                .append("<br/>");
        mensaje.append("<img src=\"")
                .append(Constantes.getHttpSuiaImagenFooterMail())
                .append("\" height=\"110\" width=\"750\" alt=\"Smiley face\">");
        nm.setContenido(mensaje.toString());
        nm.setEmail(email);
        nm.setTipoMensaje(TipoMensajeMailEnum.TEXT_HTML.getNemonico());
        return nm;
    }

	private NotificacionesMails cargarMailsFiscal(final String cedula,
			final String nombre, final String email, final String emailFiscal, final String provincia) {
		NotificacionesMails nm = new NotificacionesMails();
		nm.setAsunto("Informativo de Contraseña");
		StringBuilder mensaje = new StringBuilder();
		mensaje.append("<p>Estimado/a Se&ntilde;or/a ").append("</p>");
		mensaje.append("<p>Se informa que el usuario:"+cedula+" perteneciente a "+nombre+", obtuvo satisfactoriamente su nueva contrase&ntilde;a, al correo "+emailFiscal+" ,ubicado en la provincia de "+provincia+".</p><br/>");
		mensaje.append("<br/><p>Saludos,").append("</p>").append("<br/>");
		mensaje.append("<p>Ministerio del Ambiente y Agua").append("</p>")
				.append("<br/>");
		mensaje.append("<img src=\"")
				.append(Constantes.getHttpSuiaImagenFooterMail())
				.append("\" height=\"110\" width=\"750\" alt=\"Smiley face\">");
		nm.setContenido(mensaje.toString());
		nm.setEmail(email);
		nm.setTipoMensaje(TipoMensajeMailEnum.TEXT_HTML.getNemonico());
		return nm;
	}
    
    public void cancelar() {
        loginBean.cerrarSession();
    }
    
    public void cancelarclave() {
        JsfUtil.redirectTo("/start.jsf");
    }
    
    public synchronized void recuperarPassword() {
        Usuario usuario= new Usuario();
        Usuario usuarios= new Usuario();
        usuario=usuarioFacade.buscarUsuario(recuperarClaveBean.getLogin());
        usuarios=usuarioFacade.buscarUsuario(recuperarClaveBean.getLogin());
        try {
        	if (usuario!=null && usuarios!=null){
        		String passAnterior= usuario.getPassword();
//        		if (recuperarClaveBean.getPass().contains("HolaJus2018@") || recuperarClaveBean.getPass().contains("Pepi@to221.") || recuperarClaveBean.getPass().contains("234Ds().1")){
//        			JsfUtil.addMessageInfo("Debe contener al menos una letra mayúscula,minuscula,número y caracteres especiales y un mínimo 8 caracteres, las claves de ejemplo no pueden ser ingresadas");	
//        		}else{        			        		
            if(usuario.getUsuarioCodigoCapcha()!=null && usuario.getEstadoCodigoCapcha()!=null){
                if(usuario.getUsuarioCodigoCapcha().equals(completeURL) && !usuario.getEstadoCodigoCapcha()){
                    String nombreUsuarioSH1=JsfUtil.claveEncriptadaSHA1(usuario.getNombre());
                        if (usuarioSH1.equals(nombreUsuarioSH1)) {
                            usuario.setEstadoCodigoCapcha(true);
                            usuario.setPassword(JsfUtil.claveEncriptadaSHA1(recuperarClaveBean.getPass()));
                            Date nuevaFecha = new Date();
//                        	if (usuario.getFechaExpiracionUsuario()!=null){
//            	            	
//            	                Calendar cal = Calendar.getInstance(); 
//            	                cal.setTime(usuario.getFechaExpiracionUsuario()); 
//            	                cal.add(Calendar.DATE, 90);
//            	                nuevaFecha = cal.getTime();
//                        	}else{
                        		Calendar cal = Calendar.getInstance(); 
            	                cal.setTime(nuevaFecha); 
            	                cal.add(Calendar.DATE, 90);
            	                nuevaFecha = cal.getTime();
//                        	}
                        	usuario.setFechaExpiracionUsuario(nuevaFecha);
                            
                            AuditarUsuario auditarUsuario= new AuditarUsuario();
                        	auditarUsuario.setDescripcion("cambio de contraseña");
                        	auditarUsuario.setEstado(true);
                        	auditarUsuario.setFechaActualizacion(new Date());
                        	auditarUsuario.setPasswordAnterior(passAnterior);
                        	auditarUsuario.setPasswordActual(JsfUtil.claveEncriptadaSHA1(recuperarClaveBean.getPass()));
                        	auditarUsuario.setUsuario(usuario.getId());         
                        	if (!(usuarios.getPassword().equals(JsfUtil.claveEncriptadaSHA1(recuperarClaveBean.getPass())))){
                        		if (validar(recuperarClaveBean.getPass())){                        		
		                        	usuarioFacade.modificar(usuario);
		                        	usuarioFacade.modificarAuditoriAUsuario(auditarUsuario);
		                        	 
		                            JsfUtil.redirectTo("/claveExitosaCambio.jsf");
		                            try {
		                                usuarioFacade.actualizarContraseniaVerde(usuario.getPassword(), usuario.getNombre());
		                            } catch (Exception e) {
		                                LOG.error("Error al migrar el usuario.", e);
		                            }
                        		}
                        	}else{
                        		JsfUtil.addMessageError("La contraseña no puede ser igual que la anterior");
                        	}
                            
                        } else {
                            JsfUtil.addMessageError("Usuario no coincide con el enviado.");
                        }
                    }else {
                        JsfUtil.addMessageError("Usted ya realizo el cambio de contraseña.");
                        JsfUtil.redirectTo("/start.jsf");
                    }
                }else {
                    JsfUtil.addMessageError("Usuario no coincide con el enviado.");
                }   
//        	}
        }else{
        	 JsfUtil.addMessageError("Usuario no coincide con el enviado.");
        }
        } catch (Exception e) {
            JsfUtil.addMessageInfo("Actualización realizada correctamente.");
        }        
    }

    private boolean validar(String password) {
		String pas1 = "", pas2 = "";
		boolean resultado=false;
		Integer cadenaPass= password.length();
		if (cadenaPass>7){				
				pas1 = password;				
//				Pattern pat = Pattern.compile("^(?=.*\\d)(?=.*[\\u0021-\\u002b\\u003c-\\u0040])(?=.*[A-Z])(?=.*[a-z])\\S{8,16}$");
				Pattern pat = Pattern.compile("^(?=\\w*\\d)(?=\\w*[A-Z])(?=\\w*[a-z])\\S{8,16}$");
				 Matcher mat = pat.matcher(password);
				 if (mat.matches()) {
						resultado=true;
				} else{
					resultado=false;
					JsfUtil.addMessageInfo("Debe tener al menos un dígito, una minúscula, una mayúscula y un mínimo 8 caracteres");			
				}			
		}else{
			resultado=false;
			JsfUtil.addMessageError("Por favor ingresar mínimo 8 caracteres en el ingreso de la contraseña");		
		}
		return resultado;
	}

}
