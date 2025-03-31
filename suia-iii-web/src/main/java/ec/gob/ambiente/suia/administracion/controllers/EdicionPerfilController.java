/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.administracion.bean.PromotorBean;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.FormasContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.NacionalidadFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.domain.AuditarUsuario;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.TipoOrganizacion;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.persona.facade.PersonaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * @author ishmael
 */
@ManagedBean
@ViewScoped
public class EdicionPerfilController implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -7332827533098349739L;
    @EJB
    private UsuarioFacade usuarioFacade;
    @EJB
    private ContactoFacade contactoFacade;
    @EJB
    private FormasContactoFacade formasContactoFacade;
    @EJB
    private OrganizacionFacade organizacionFacade;
    @EJB
    private NacionalidadFacade nacionalidadFacade;
    
    @EJB
    private PersonaFacade personaFacade;

    @Getter
    @Setter
    private PromotorBean promotorBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{ubicacionGeograficaController}")
    private UbicacionGeograficaController ubicacionGeograficaController;

    @Getter
    @Setter
    private String correoTemporal;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(EdicionPerfilController.class);
    private static final String PERSONA_NATURAL = "N";
    private static final String PERSONA_JURIDICA = "J";
    private static final String ID_EMPRESA_MIXTA = "8";
    private static final String SOLO_NUMEROS = "return numbersonly(this, event);";
    private static final String REQUERIDO = "\" es requerido.";

    @PostConstruct
    public void inicio() {
        promotorBean = new PromotorBean();
        cargarDatosUsuario();
        promotorBean.setContacto(new Contacto());
        promotorBean.setListaContactoRemover(new ArrayList<Contacto>());
        promotorBean.setScriptNumeros("");

    }

    private void cargarDatosUsuario() {
        try {
            promotorBean.setUsuario(usuarioFacade.buscarUsuarioPorId(loginBean
                    .getUsuario().getId()));
            promotorBean.setPersona(promotorBean.getUsuario().getPersona());
            identificarTipoPersona();
        } catch (Exception e) {
            LOG.error(e, e);
        }
    }

    private void identificarTipoPersona() throws ServiceException {
        promotorBean.setOrganizacion(organizacionFacade
                .buscarPorPersona(promotorBean.getPersona(), promotorBean.getUsuario().getNombre()));
        List<Contacto> contactos = null;
        if (promotorBean.getOrganizacion() != null && promotorBean.getOrganizacion().getRuc().trim().equals(promotorBean.getUsuario().getNombre())) {
            promotorBean.setTipoPersona(PERSONA_JURIDICA);
            promotorBean.setIdParroquia(promotorBean.getOrganizacion()
                    .getIdUbicacionGeografica().toString());
            promotorBean.setIdTipoOrganizacion(promotorBean.getOrganizacion()
                    .getIdTipoOrganizacion().toString());
            contactos = contactoFacade.buscarPorOrganizacion(promotorBean
                    .getOrganizacion());
        } else {
            promotorBean.setTipoPersona(PERSONA_NATURAL);
            promotorBean.setIdParroquia(promotorBean.getPersona()
                    .getIdUbicacionGeografica().toString());
            promotorBean.setIdNacionalidad(promotorBean.getPersona()
                    .getIdNacionalidad().toString());
            promotorBean.setIdTipoTrato(promotorBean.getPersona()
                    .getIdTipoTratos().toString());
            contactos = contactoFacade.buscarPorPersona(promotorBean
                    .getPersona());
        }
        boolean multiplesCorreos = false;
        for (int i = 0; i < contactos.size(); i++) {
            switch (contactos.get(i).getFormasContacto().getNombre()) {
                case "*TELÉFONO":
                    promotorBean.setTelefono(contactos.get(i).getValor());
                    contactos.remove(i);
                    i--;
                    break;
                case "*TEL├ëFONO":
                    promotorBean.setTelefono(contactos.get(i).getValor());
                    contactos.remove(i);
                    i--;
                    break;
                case "*DIRECCI├ôN":
                    promotorBean.setDireccion(contactos.get(i).getValor());
                    contactos.remove(i);
                    i--;
                    break;
                case "*DIRECCIÓN":
                    promotorBean.setDireccion(contactos.get(i).getValor());
                    contactos.remove(i);
                    i--;
                    break;
                case "*EMAIL":
                    if (!multiplesCorreos) {
                        correoTemporal = contactos.get(i).getValor();
                        promotorBean.setEmail(contactos.get(i).getValor());
                        contactos.remove(i);
                        multiplesCorreos = true;
                        i--;
                    }
                    break;
                case "*CELULAR":
                    promotorBean.setCelular(contactos.get(i).getValor());
                    contactos.remove(i);
                    i--;
                    break;
                default:
                    break;
            }
        }
        promotorBean.setListaContactoOpcionales(contactos);
        recuperarUbicacionGeografica();
        reasignarIndice();
    }

    private void recuperarUbicacionGeografica() {
        ubicacionGeograficaController.getUbicacionGeograficaBean()
                .setIdParroquia(promotorBean.getIdParroquia());
        ubicacionGeograficaController.cargarParametros();
    }

    public void agregarContacto() {
        if (promotorBean.getContacto().getValor() == null
                || promotorBean.getContacto().getValor().isEmpty()) {
            JsfUtil.addMessageError("Debe ingresar datos para la información de contacto seleccionada.");
            return;
        }
        promotorBean.getContacto().setFormasContacto(
                new FormasContacto(Integer.valueOf(promotorBean
                        .getIdFormaContacto())));
        for (Contacto contacto : getPromotorBean().getListaContactoOpcionales()) {
            if (promotorBean.getIdFormaContacto().equals(
                    contacto.getFormasContacto().getId().toString())
                    && !promotorBean.getIdFormaContacto().equals(
                    String.valueOf(FormasContacto.EMAIL))) {
                JsfUtil.addMessageError("Esta forma de contacto ya esta ingresada");
                promotorBean.getContacto().setValor(null);
                return;
            }
        }

        if (promotorBean.getIdFormaContacto().equals(
                String.valueOf(FormasContacto.EMAIL))) {
            if (validarMail(promotorBean.getContacto().getValor())) {
                JsfUtil.addMessageError("La dirección de correo ingresada es incorrecta, por favor verifique.");
                return;
            }
//			if (validarMailRegistrado(promotorBean.getContacto().getValor())) {
//				JsfUtil.addMessageError("El correo electrónico ingresado ya se encuentra registrado.");
//				return;
//			}
        }

        try {
            Contacto contactoPersist = promotorBean.getContacto();
            if (!promotorBean.getTipoPersona().equals(PERSONA_NATURAL)) {
                contactoPersist.setOrganizacion(promotorBean.getOrganizacion());
            }
            contactoPersist.setPersona(promotorBean.getPersona());
            contactoPersist.setFormasContacto(new FormasContacto(Integer
                    .valueOf(promotorBean.getIdFormaContacto())));
            contactoFacade.guardar(contactoPersist);
            promotorBean.getListaContactoOpcionales().add(contactoPersist);
            reasignarIndice();
        } catch (ServiceException e) {
            JsfUtil.addMessageError(e.getMessage());
            LOG.error(e, e);
        } catch (NumberFormatException e) {
            JsfUtil.addMessageError(e.getMessage());
            LOG.error(e, e);
        }
        promotorBean.setContacto(new Contacto());
        promotorBean.setIdFormaContacto(null);
    }

    public void removerContacto(Contacto contacto) {
        try {
            promotorBean.getListaContactoOpcionales().remove(
                    contacto.getIndice());

            if (contacto.getId() != null) {
                contacto.setEstado(false);
                promotorBean.getListaContactoRemover().add(contacto);
            }
            reasignarIndice();
        } catch (Exception e) {
            JsfUtil.addMessageError(e.getMessage());
            LOG.error(e, e);
        }
    }

    private void reasignarIndice() {
        int i = 0;
        for (Contacto c : promotorBean.getListaContactoOpcionales()) {
            c.setIndice(i);
            i++;
        }
    }

    public void guardar() {
        try {
            if (validarRegistro()) {
                promotorBean.getUsuario().setFechaModificacion(new Date());
                promotorBean.getUsuario().setUsuarioModificacion(
                        loginBean.getUsuario().getNombre());
                promotorBean.getPersona().setUbicacionesGeografica(
                        new UbicacionesGeografica(Integer.valueOf(promotorBean
                                .getIdParroquia())));
                promotorBean.setListaContacto(new ArrayList<Contacto>());
                promotorBean
                        .setListaContactoOpcionales(new ArrayList<Contacto>());
                boolean multiplesCorreos = false;
                if (promotorBean.getTipoPersona().equals(PERSONA_NATURAL)) {
//					promotorBean.getUsuario().setNombre(
//							promotorBean.getUsuario().getPin());
                    promotorBean.getPersona().setPin(promotorBean.getUsuario().getPin());
                    promotorBean.getUsuario().setPersona(
                            promotorBean.getPersona());
                    promotorBean.getPersona()
                            .setNacionalidad(
                                    nacionalidadFacade.buscarPorId(Integer
                                            .parseInt(promotorBean
                                                    .getIdNacionalidad())));
                    for (Contacto c : promotorBean.getPersona().getContactos()) {
                        c.setPersona(promotorBean.getPersona());
                        switch (c.getFormasContacto().getNombre()) {
                            case "*TELÉFONO":
                                c.setValor(promotorBean.getTelefono());
                                contactoFacade.guardar(c);
                                break;
                            case "*TEL├ëFONO":
                                c.setValor(promotorBean.getTelefono());
                                contactoFacade.guardar(c);
                                break;
                            case "*DIRECCI├ôN":
                                c.setValor(promotorBean.getDireccion());
                                contactoFacade.guardar(c);
                                break;
                            case "*DIRECCIÓN":
                                c.setValor(promotorBean.getDireccion());
                                contactoFacade.guardar(c);
                                break;
                            case "*EMAIL":
                                if (!c.getEstado())
                                    break;
                                if (!multiplesCorreos) {
                                    if (c.getValor().equals(correoTemporal)) {
                                        c.setValor(promotorBean.getEmail());
                                        contactoFacade.actualizarMail(c);
                                        multiplesCorreos = true;
                                    }
                                } else {
                                    promotorBean.getListaContactoOpcionales()
                                            .add(c);
                                }
                                break;
                            case "*CELULAR":
                                c.setValor(promotorBean.getCelular());
                                contactoFacade.guardar(c);
                                break;
                            default:
                                break;
                        }
                    }
                    for (Contacto co : promotorBean
                            .getListaContactoOpcionales()) {
                        co.setPersona(promotorBean.getPersona());
                        promotorBean.getPersona().getContactos().add(co);
                    }
                } else {
                	boolean estadoCelular=false;
                    for (Contacto c : promotorBean.getOrganizacion()
                            .getContactos()) {
                    	
                    	System.out.println(c.getFormasContacto().getNombre());
                        switch (c.getFormasContacto().getNombre()) {
                            case "*TELÉFONO":
                                c.setValor(promotorBean.getTelefono());
                                contactoFacade.guardar(c);
                                promotorBean.getListaContacto().add(c);
                                break;
                            case "*TEL├ëFONO":
                                c.setValor(promotorBean.getTelefono());
                                promotorBean.getListaContacto().add(c);
                                contactoFacade.guardar(c);
                                break;
                            case "*DIRECCI├ôN":
                                c.setValor(promotorBean.getDireccion());
                                promotorBean.getListaContacto().add(c);
                                contactoFacade.guardar(c);
                                break;
                            case "*DIRECCIÓN":
                                c.setValor(promotorBean.getDireccion());
                                promotorBean.getListaContacto().add(c);
                                contactoFacade.guardar(c);
                                break;
                            case "*EMAIL":
                                if (!multiplesCorreos) {
                                    if (c.getValor().equals(correoTemporal)) {
                                        c.setValor(promotorBean.getEmail());
                                        contactoFacade.actualizarMail(c);
                                        multiplesCorreos = true;
                                    }
                                } else {
                                    promotorBean.getListaContactoOpcionales()
                                            .add(c);
                                }
                                break;
                            case "*CELULAR":
                                c.setValor(promotorBean.getCelular());
                                promotorBean.getListaContacto().add(c);
                                contactoFacade.guardar(c);
                                estadoCelular=true;
                                break;
                            default:
                                break;
                        }
                    }
                    if(!estadoCelular)
                    {
                    	Contacto cont = new Contacto();
                    	cont.setValor(promotorBean.getCelular());
                    	cont.setEstado(true);
                    	cont.setOrganizacion(promotorBean.getOrganizacion());
                    	cont.setFormasContacto(new FormasContacto(FormasContacto.CELULAR));
                    	contactoFacade.guardar(cont);
                    }
                    
                    if (promotorBean.getListaContactoOpcionales() == null) {
                        promotorBean
                                .setListaContactoOpcionales(new ArrayList<Contacto>());
                    }
                    for (Contacto co : promotorBean
                            .getListaContactoOpcionales()) {
                        promotorBean.getListaContacto().add(co);
                    }
                    promotorBean.getOrganizacion().setContactos(
                            promotorBean.getListaContactoOpcionales());
                    promotorBean.getUsuario().setPersona(
                            promotorBean.getPersona());
                    promotorBean.getOrganizacion().setTipoOrganizacion(
                            new TipoOrganizacion(Integer.valueOf(promotorBean
                                    .getIdTipoOrganizacion())));
                    promotorBean.getOrganizacion().setParticipacionEstado(
                            promotorBean.getIdTipoOrganizacion().equals(
                                    ID_EMPRESA_MIXTA) ? promotorBean
                                    .getOrganizacion().getParticipacionEstado()
                                    : null);
                    if(!promotorBean.getOrganizacion().getIdUbicacionGeografica().equals(Integer.valueOf(promotorBean.getIdParroquia()))
                    		&& promotorBean.getUsuario().getPersona().getOrganizaciones() != null) {
                    	for(Organizacion item : promotorBean.getUsuario().getPersona().getOrganizaciones()) {
                    		if(item.getId().equals(promotorBean.getOrganizacion().getId())) {
                    			item.setIdUbicacionGeografica(Integer.valueOf(promotorBean.getIdParroquia()));
                    			break;
                    		}
                    	}
                    }
                    promotorBean.getOrganizacion().setIdUbicacionGeografica(
                            Integer.valueOf(promotorBean.getIdParroquia()));
                    promotorBean.getOrganizacion().setPersona(
                            promotorBean.getPersona());
                    organizacionFacade
                            .modificar(promotorBean.getOrganizacion());
                }
//                AuditarUsuario auditarUsuario= new AuditarUsuario();
//            	auditarUsuario.setDescripcion("modificar datos del usuario");
//            	auditarUsuario.setEstado(true);
//            	auditarUsuario.setFechaActualizacion(new Date());           
//            	auditarUsuario.setUsuario(promotorBean.getUsuario().getId());           	
                Usuario usuarioInicial= usuarioFacade.buscarUsuario(promotorBean.getUsuario().getNombre());
                List<Contacto> contactos = new ArrayList<Contacto>();
               if (promotorBean.getTipoPersona().equals(PERSONA_NATURAL)) {
            	   contactos= contactoFacade.buscarPorPersona(promotorBean.getUsuario().getPersona());
                }else{
                	 Organizacion organizacionInicial= organizacionFacade.buscarPorRuc(promotorBean.getUsuario().getNombre());
                	 contactos= contactoFacade.buscarPorOrganizacion(organizacionInicial);
                }
               
            	
            	String contactosper="";
            	for (int i = 0; i < contactos.size(); i++) {
					contactosper+=contactos.get(i).getValor()+",";
				}
            	
//            	comentado a verificar la funcionalida - revisado por wr.
//            	Persona personaActualizada= personaFacade.buscarPersonaPorPin(promotorBean.getUsuario().getPersona().getPin());
//            	promotorBean.getUsuario().setPersona(personaActualizada);
            	
                usuarioFacade.modificar(promotorBean.getUsuario(),
                        promotorBean.getListaContactoRemover());
                
//                usuarioFacade.modificarAuditoriAUsuario(auditarUsuario);
                inicio();
                JsfUtil.addMessageInfo(JsfUtil.REGISTRO_ACTUALIZADO);
                // ejecuta la migracion de usuarios
                if (promotorBean.getTipoPersona().equals(PERSONA_NATURAL)) {
                	
                	
					AuditarUsuario auditarUsuario= new AuditarUsuario();
					
					String d="TITULO:"+ usuarioInicial.getPersona().getTitulo()+"-TIPO TRATO:"+
							promotorBean.getIdTipoTrato() +"-GENERO: "+usuarioInicial.getPersona().getGenero()+"- NACIONALIDAD:"+Integer.parseInt(promotorBean.getIdNacionalidad())
							+"-IDPARROQUIA: "+Integer.parseInt(promotorBean.getIdParroquia())+"CONTACTOS: "+contactosper;
							
		        	auditarUsuario.setDescripcion(d);
		        	auditarUsuario.setEstado(true);
		        	auditarUsuario.setFechaActualizacion(new Date());
		        	auditarUsuario.setUsuario(promotorBean.getUsuario().getId());
		        	auditarUsuario.setUsuarioMae(loginBean.getUsuario().getId());
		        	usuarioFacade.modificarAuditoriAUsuario(auditarUsuario);
		        	
                	boolean token= false;
					boolean funcionario= false;
					boolean plantacentral=false;
					boolean encargado= false;
					if (!(promotorBean.getUsuario().getToken()==null)){
						token= promotorBean.getUsuario().getToken();
					}					
                	boolean estadoUsuario =promotorBean.getUsuario().getEstado();
                	if (!(promotorBean.getUsuario().getFuncionario()==null)){
                		funcionario=promotorBean.getUsuario().getFuncionario();           		
                	}                
                	if (!(promotorBean.getUsuario().getPlantaCentral()==null)){
                		plantacentral=promotorBean.getUsuario().getPlantaCentral();        		
                	}
                	if (!(promotorBean.getUsuario().getSubrogante()==null)){
                		encargado=promotorBean.getUsuario().getSubrogante();            		
                	}               	                	
                	
                	List<Contacto> listacontactos= contactoFacade.buscarUsuarioNativeQuery(promotorBean.getUsuario().getNombre());
                	usuarioFacade.insertarPersonaVerde(promotorBean.getUsuario().getNombre(), promotorBean.getPersona().getTitulo(),
                			promotorBean.getIdTipoTrato(), promotorBean.getPersona().getNombre(), promotorBean.getPersona().getGenero(), Integer.parseInt(promotorBean.getIdNacionalidad())
                			, promotorBean.getIdTipoTrato(), Integer.parseInt(promotorBean.getIdParroquia()), promotorBean.getUsuario().getPassword(),listacontactos ,
                			promotorBean.getUsuario().getListaAreaUsuario().get(0).getArea(),token,estadoUsuario,funcionario,plantacentral,encargado);
                }else{
                	
                	Organizacion organizacionInicial= organizacionFacade.buscarPorRuc(promotorBean.getUsuario().getNombre());
                	
					AuditarUsuario auditarUsuario= new AuditarUsuario();
					
					String d="TITULO:"+ usuarioInicial.getPersona().getPosicion() + "TIPO ORGANIZACION: " +organizacionInicial.getTipoOrganizacion().getNombre() +"-IDPARROQUIA: "+Integer.parseInt(promotorBean.getIdParroquia())+"CONTACTOS: "+contactosper;
							
		        	auditarUsuario.setDescripcion(d);
		        	auditarUsuario.setEstado(true);
		        	auditarUsuario.setFechaActualizacion(new Date());
		        	auditarUsuario.setUsuario(promotorBean.getUsuario().getId());
		        	auditarUsuario.setUsuarioMae(loginBean.getUsuario().getId());
		        	usuarioFacade.modificarAuditoriAUsuario(auditarUsuario);
		        	
//                	usuarioFacade.ejecutarMigracionUsuario();
                	boolean token= false;
					boolean funcionario= false;
					boolean plantacentral=false;
					boolean encargado= false;
					if (!(promotorBean.getUsuario().getToken()==null)){
						token= promotorBean.getUsuario().getToken();
					}					
                	boolean estadoUsuario =promotorBean.getUsuario().getEstado();
                	if (!(promotorBean.getUsuario().getFuncionario()==null)){
                		funcionario=promotorBean.getUsuario().getFuncionario();           		
                	}                
                	if (!(promotorBean.getUsuario().getPlantaCentral()==null)){
                		plantacentral=promotorBean.getUsuario().getPlantaCentral();        		
                	}
                	if (!(promotorBean.getUsuario().getSubrogante()==null)){
                		encargado=promotorBean.getUsuario().getSubrogante();            		
                	}
                	List<Contacto> listacontactos= contactoFacade.buscarUsuarioNativeQuery(promotorBean.getUsuario().getNombre());
                	usuarioFacade.insertarOrganizacionVerde(
                			promotorBean.getPersona().getPin(),promotorBean.getPersona().getTitulo(),promotorBean.getPersona().getIdTipoTratos().toString()
                			,promotorBean.getPersona().getNombre(),promotorBean.getPersona().getIdTipoTratos().toString(),Integer.parseInt(promotorBean.getIdParroquia())
                			,promotorBean.getOrganizacion().getRuc(),promotorBean.getOrganizacion().getNombre(),promotorBean.getPersona().getPosicion(),
                			promotorBean.getOrganizacion().getTipoOrganizacion().getId().toString(),Integer.parseInt(promotorBean.getIdParroquia()), promotorBean.getUsuario().getListaAreaUsuario().get(0).getArea()
                			, promotorBean.getUsuario().getPassword(),listacontactos,token,estadoUsuario,funcionario,plantacentral,encargado
                			);    
                }
//                
            }
        } catch (NumberFormatException e) {
            LOG.error(e, e);
        } catch (ServiceException e) {
            LOG.error("Ocurrió un error al recuperar la información. Por favor intente más tarde.", e);
            JsfUtil.addMessageError("Ocurrió un error al recuperar la información. Por favor intente más tarde.");
        } catch (Exception e) {
            LOG.error(e, e);
        }
    }

    public boolean validarMailRegistrado(String valor) {
        boolean perteneceAOtroUsuario = true;
        try {
            List<Contacto> listaMailRegistrados = contactoFacade
                    .buscarMail(valor);
            if (listaMailRegistrados != null && !listaMailRegistrados.isEmpty()) {
                for (Contacto c : listaMailRegistrados) {
                    if (promotorBean.getTipoPersona().equals(PERSONA_NATURAL)) {
                        if (c.getPersona().getId()
                                .equals(promotorBean.getPersona().getId())) {
                            perteneceAOtroUsuario = false;
                        }
                    } else {
                        if (c.getOrganizacion().getId()
                                .equals(promotorBean.getOrganizacion().getId())) {
                            perteneceAOtroUsuario = false;
                        }
                    }
                }
                return perteneceAOtroUsuario;
            } else {
                return !perteneceAOtroUsuario;
            }
        } catch (ServiceException e) {
            LOG.info("El error al validar correo electrónico.", e);
            return perteneceAOtroUsuario;
        }
    }

    public void guardarContrasenia() {
        try {
            if (validarContrasenia()) {
//            	/*
//            	 * 
//            	 */
//            	AuditoriaSuplantacion au = new AuditoriaSuplantacion();
//                au.setUsuarioMae(loginBean.getUsuario());
//                au.setFechaSuplantacion(new Date());
//                au.setUsuarioSuplantado(loginBean.getUsuario());
//                usuarioFacade.guardarSuplantacion(au);
//            	/*
//            	 * 
//            	 */
            	promotorBean.getUsuario().setFechaRenovarPassusuario(new Date());
            	AuditarUsuario auditarUsuario= new AuditarUsuario();
            	auditarUsuario.setDescripcion("cambio de contraseña");
            	auditarUsuario.setEstado(true);
            	auditarUsuario.setUsuarioMae(loginBean.getUsuario().getId());
            	auditarUsuario.setFechaActualizacion(new Date());
            	auditarUsuario.setPasswordAnterior(JsfUtil.claveEncriptadaSHA1(promotorBean.getPasswordAnterior()));
            	auditarUsuario.setPasswordActual(JsfUtil.claveEncriptadaSHA1(promotorBean
                                        .getPassword()));
            	auditarUsuario.setUsuario(promotorBean.getUsuario().getId());
            	Date nuevaFecha = new Date();
//            	if (promotorBean.getUsuario().getFechaExpiracionUsuario()!=null){
//	            	
//	                Calendar cal = Calendar.getInstance(); 
//	                cal.setTime(promotorBean.getUsuario().getFechaExpiracionUsuario()); 
//	                cal.add(Calendar.DATE, 90);
//	                nuevaFecha = cal.getTime();
//            	}else{
            		Calendar cal = Calendar.getInstance(); 
	                cal.setTime(nuevaFecha); 
	                cal.add(Calendar.DATE, 90);
	                nuevaFecha = cal.getTime();
//            	}
            	promotorBean.getUsuario().setFechaExpiracionUsuario(nuevaFecha);
                promotorBean.getUsuario()
                        .setPassword(
                                JsfUtil.claveEncriptadaSHA1(promotorBean
                                        .getPassword()));
                
                if (!(promotorBean.getPasswordAnterior().equals(promotorBean.getPassword()))){
                	usuarioFacade.modificar(promotorBean.getUsuario());
                    usuarioFacade.modificarAuditoriAUsuario(auditarUsuario);
//                    usuarioFacade.ejecutarMigracionUsuario();
                    usuarioFacade.actualizarContraseniaVerde(promotorBean.getUsuario().getPassword(), promotorBean.getUsuario().getNombre());
                    loginBean.cerrarSessionRedireccionar("/claveCambiada.jsf");
                }else{
                	JsfUtil.addMessageError("La contraseña no puede ser igual que la anterior");
                }
                
                try {
                   
                } catch (Exception e) {
                    LOG.error("Error al ejecutar la migración de los datos al cambiar contraseña.", e);
                }

                
            }
        } catch (Exception e) {
            LOG.error(e, e);
        }
    }

    private boolean validarContrasenia() {
    	boolean resultado=false;
        String contraseniaMD5 = JsfUtil.claveEncriptadaSHA1(promotorBean
                .getPasswordAnterior());
//        if (promotorBean.getPassword().contains("HolaJus2018@") || promotorBean.getPassword().contains("Pepi@to221.") || promotorBean.getPassword().contains("234Ds().1")){
//        	resultado= false;
//        	JsfUtil.addMessageInfo("Debe contener al menos una letra mayúscula,minuscula,número y caracteres especiales y un mínimo 8 caracteres, las claves de ejemplo no pueden ser ingresadas");	
//        }else{
	        if (!contraseniaMD5.equals(promotorBean.getUsuario().getPassword())) {
	            JsfUtil.addMessageError("La contraseña actual no es correcta");
	            return false;
	        }
	        
	        
	        if (!(promotorBean.getUsuario().getPassword().equals(JsfUtil.claveEncriptadaSHA1(promotorBean.getPassword())))){
	        	if ( validar(promotorBean.getPassword()))
	            	resultado =true;
	        }else{
	        	resultado =false;
	        	JsfUtil.addMessageError("La contraseña no puede ser igual que la anterior");
	        }
//	    }
        
        return resultado;
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
    

    private boolean validarRegistro() {
        if (promotorBean.getPersona().getContactos() == null
                || promotorBean.getPersona().getContactos().isEmpty()) {
            promotorBean.getPersona().setContactos(new ArrayList<Contacto>());
            FormasContacto fcTelf = formasContactoFacade
                    .buscarPorNombre("*TELÉFONO");
            Contacto cTelf = new Contacto();
            cTelf.setFormasContacto(fcTelf);
            cTelf.setEstado(true);
            cTelf.setValor(promotorBean.getTelefono());
            FormasContacto fcCel = formasContactoFacade
                    .buscarPorNombre("*CELULAR");
            Contacto cCel = new Contacto();
            cCel.setFormasContacto(fcCel);
            cCel.setEstado(true);
            cCel.setValor(promotorBean.getCelular());
            FormasContacto fcDir = formasContactoFacade
                    .buscarPorNombre("*DIRECCIÓN");
            Contacto cDir = new Contacto();
            cDir.setFormasContacto(fcDir);
            cDir.setEstado(true);
            cDir.setValor(promotorBean.getDireccion());
            FormasContacto fcEma = formasContactoFacade
                    .buscarPorNombre("*EMAIL");
            Contacto cEma = new Contacto();
            cEma.setFormasContacto(fcEma);
            cEma.setEstado(true);
            cEma.setValor(promotorBean.getEmail());
            if (promotorBean.getTipoPersona().equals(PERSONA_NATURAL)) {
                cTelf.setPersona(promotorBean.getPersona());
                cCel.setPersona(promotorBean.getPersona());
                cDir.setPersona(promotorBean.getPersona());
                cEma.setPersona(promotorBean.getPersona());
            } else {
                cTelf.setOrganizacion(promotorBean.getOrganizacion());
                cCel.setOrganizacion(promotorBean.getOrganizacion());
                cDir.setOrganizacion(promotorBean.getOrganizacion());
                cEma.setOrganizacion(promotorBean.getOrganizacion());
            }
            promotorBean.getPersona().getContactos().add(cTelf);
            promotorBean.getPersona().getContactos().add(cCel);
            promotorBean.getPersona().getContactos().add(cDir);
            promotorBean.getPersona().getContactos().add(cEma);
        }
        List<String> mensajes = new ArrayList<String>();
        if (promotorBean.getTipoPersona().equals(PERSONA_NATURAL)
                && (promotorBean.getPersona().getTitulo() == null || promotorBean
                .getPersona().getTitulo().isEmpty())) {
            mensajes.add("El campo 'Título Académico' es requerido.");
        }
        if (promotorBean.getTipoPersona().equals(PERSONA_NATURAL)
                && (promotorBean.getIdNacionalidad() == null || promotorBean
                .getIdNacionalidad().equals(""))) {
            mensajes.add("El campo 'Nacionalidad' es requerido.");
        }
        if (promotorBean.getTelefono() == null
                || promotorBean.getTelefono().equals("")) {
            mensajes.add("El campo 'Teléfono' es requerido.");
        }
        if (promotorBean.getCelular() == null
                || promotorBean.getCelular().equals("")) {
            mensajes.add("El campo 'Celular' es requerido.");
        }
        if (promotorBean.getEmail() == null
                || promotorBean.getEmail().equals("")) {
            mensajes.add("El campo 'Correo electrónico' es requerido.");
        }
        if (!promotorBean.getEmail().equals("")
                && validarMail(promotorBean.getEmail())) {
            mensajes.add("La dirección de correo ingresada es incorrecta, por favor verifique.");
        }
//		if (validarMailRegistrado(promotorBean.getEmail())) {
//			mensajes.add("El correo electrónico ya ha sido registrado por otro usuario.");
//		}
        if (promotorBean.getTipoPersona().equals(PERSONA_JURIDICA)
                && promotorBean.getIdTipoOrganizacion() == null) {
            mensajes.add("El campo 'Tipo de Organización' es requerido.");
        }
        if (promotorBean.getTipoPersona().equals(PERSONA_NATURAL)
                && (promotorBean.getPersona().getGenero() == null || promotorBean
                .getPersona().getGenero().isEmpty())) {
            mensajes.add("El campo 'Género' es requerido.");
        }

        if (promotorBean.getTipoPersona().equals("J")
                && promotorBean.getPersona().getPosicion().equals("")) {
            mensajes.add("El campo 'Cargo Representante'" + REQUERIDO);
        }

        if (mensajes.isEmpty()) {
            return true;
        } else {
            JsfUtil.addMessageError(mensajes);
            return false;
        }

    }

    private boolean validarMail(String email) {
        boolean valor = false;
        valor = !JsfUtil.validarMail(email);
        return valor;
    }

    public void validarNumeros() {
        int opcion = new Integer(
                getPromotorBean().getIdFormaContacto() != null ? getPromotorBean()
                        .getIdFormaContacto() : "0");
        switch (opcion) {
            case FormasContacto.CELULAR:
                getPromotorBean().setScriptNumeros(SOLO_NUMEROS);
                break;
            case FormasContacto.TELEFONO:
                getPromotorBean().setScriptNumeros(SOLO_NUMEROS);
                break;
            case FormasContacto.FAX:
                getPromotorBean().setScriptNumeros(SOLO_NUMEROS);
                break;
            default:
                getPromotorBean().setScriptNumeros("");
        }
    }

}
