package com.captcha.botdetect.examples.jsf.facelets_captcha.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import lombok.Getter;
import lombok.Setter;

import com.captcha.botdetect.web.jsf.JsfCaptcha;

import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.AuditarUsuario;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
import ec.gob.ambiente.suia.persona.facade.PersonaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean(name = "jsfFaceletsCaptchaExample")
@RequestScoped
@ViewScoped
public class FaceletsCaptcha {

	private String captchaCode;
	private JsfCaptcha captcha;
	private boolean correctLabelVisible, incorrectLabelVisible;

	private String nombreUsuario;

	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	 @Getter
	    @Setter
	    @ManagedProperty(value = "#{loginBean}")
	    private LoginBean loginBean;
	 
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private OrganizacionFacade organizacionFacade;
	
	@EJB
	private PersonaFacade personaFacade;

	@EJB
	private ContactoFacade contactoFacade;
	
	@EJB 
	private CrudServiceBean crudServiceBean;

	public FaceletsCaptcha() {
	}

	public String getCaptchaCode() {
		return captchaCode;
	}

	public void setCaptchaCode(String captchaCode) {
		this.captchaCode = captchaCode;
	}

	public JsfCaptcha getCaptcha() {
		return captcha;
	}

	public void setCaptcha(JsfCaptcha captcha) {
		this.captcha = captcha;
	}

	public boolean isCorrectLabelVisible() {
		return correctLabelVisible;
	}

	public boolean isIncorrectLabelVisible() {
		return incorrectLabelVisible;
	}

	public void validate() throws ServiceException, InterruptedException {
		boolean user=false;
		if (loginBean.getNombreUsuario()!=null ){
			if (loginBean.getNombreUsuario().length()>0){
				if (!(loginBean.getNombreUsuario().equals(nombreUsuario))){
					user=true;
				}
			}
		}
		if (!user){
			Usuario usuarioConsultado = usuarioFacade.buscarUsuario(nombreUsuario);
			Persona persona = new Persona();
			if (usuarioConsultado != null) {
				boolean isHuman = captcha.validate(captchaCode);
				if (isHuman) {
					correctLabelVisible = true;
					incorrectLabelVisible = false;
				} else {
					correctLabelVisible = false;
					incorrectLabelVisible = true;				
					JsfUtil.addMessageError("Código de seguridad incorrecto");
				}
				
				List<Contacto> contactos =new ArrayList<Contacto>();
				if (correctLabelVisible) {
					Map<String, Object> parameters=new HashMap<String, Object>(); 
					
//					parameters.put("persona", usuarioConsultado.getPersona());		
					if (organizacionFacade.buscarPorRuc(nombreUsuario) !=null){						
					parameters.put("organizacion", organizacionFacade.buscarPorRuc(nombreUsuario));
					contactos = (List<Contacto>) crudServiceBean.findByNamedQuery(Contacto.FIND_BY_ORGANIZATION,parameters);
					if(contactos.size()==0){
						Map<String, Object> param=new HashMap<String, Object>();
						param.put("organizacion", organizacionFacade.buscarPorRuc(nombreUsuario));
						contactos=(List<Contacto>)crudServiceBean.findByNamedQuery(Contacto.FIND_BY_ORGANIZATION, param);
					}
				}else{
					parameters.put("persona", usuarioConsultado.getPersona());	
					contactos = (List<Contacto>) crudServiceBean.findByNamedQuery(Contacto.FIND_BY_PERSON,parameters);
				}
					
				
					String email = null;
					String nombreProponente = null;
					String cedulaProponente = null;

					for (Contacto contacto : contactos) {
						if (contacto.getFormasContacto().getId().equals(FormasContacto.EMAIL)) {
							email = contacto.getValor();
							break;
						}
					}
					
					Organizacion organizacion =organizacionFacade.buscarPorRuc(nombreUsuario);
					if (organizacion!=null){
						nombreProponente= organizacion.getNombre();
						cedulaProponente= organizacion.getRuc();
					}else{
						persona =personaFacade.buscarPersonaPorUsuario(usuarioConsultado.getId());
						nombreProponente=persona.getNombre();
						cedulaProponente=persona.getPin();
					}

					if (usuarioConsultado != null) {
						String usuarioEncrip = JsfUtil
								.claveEncriptadaSHA1(nombreUsuario);
						FacesContext context = FacesContext.getCurrentInstance();
						HttpServletRequest req = (HttpServletRequest) context
								.getExternalContext().getRequest();
						String urld = req.getRequestURL().toString();
						String urlFinal = urld.substring(0,
								urld.indexOf(req.getContextPath())
										+ req.getContextPath().length());
						String claveCapchaEncrip =null;
						claveCapchaEncrip=JsfUtil
								.claveEncriptadaSHA1(captchaCode);
						String url = urlFinal + "/recuperarNuevaClave.jsf";
						usuarioConsultado.setUsuarioCodigoCapcha(url + "?key="
								+ usuarioEncrip + "&code=" + claveCapchaEncrip);
						usuarioConsultado.setEstadoCodigoCapcha(false);
						usuarioConsultado.setFechaLinkExpiracion(new Date());
						
						usuarioFacade.modificar(usuarioConsultado);
						AuditarUsuario auditarUsuario= new AuditarUsuario();
                    	auditarUsuario.setDescripcion("cambio de clave url: "+usuarioConsultado.getUsuarioCodigoCapcha());
                    	auditarUsuario.setPasswordActual(email);
                    	auditarUsuario.setEstado(true);
                    	auditarUsuario.setFechaActualizacion(new Date());
                    	auditarUsuario.setUsuario(usuarioConsultado.getId()); 
                    	usuarioFacade.modificarAuditoriAUsuario(auditarUsuario);
                    	
                    	
						NotificacionAutoridadesController mail_a = new NotificacionAutoridadesController();
						mail_a.sendEmailCambioClaves(email,
								"SUIA- Cambio de Contraseña",
								"Este correo fue enviado usando JavaMail",
								nombreProponente, cedulaProponente,
								usuarioConsultado, usuarioConsultado);
						JsfUtil.redirectTo("/claverReseteada.jsf");
						Thread.sleep(2000);
					}
				}
				loginBean.setNombreUsuario("");
				setNombreUsuario("");
				this.captchaCode = "";
			} else {
				this.captchaCode = "";
				JsfUtil.addMessageError("Por favor verificar el usuario ingresado ya que no se encuentra registrado.");
			}
		}else{
			JsfUtil.addMessageError("El usuario que ingreso no corresponde al solicitado");
		}
		
//	
	}
}
