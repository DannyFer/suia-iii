
package ec.gob.ambiente.suia.administracion.controllers;

import ec.gob.ambiente.cliente.ConsultaRucCedula;
import ec.gob.ambiente.suia.administracion.bean.PromotorBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.AreaUsuarioFacade;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.FormasContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.ImpedidosFacade;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.domain.enums.TipoImpedidoEnum;
import ec.gob.ambiente.suia.domain.enums.TipoMensajeMailEnum;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.registrocivil.consultacedula.Cedula;
import ec.gov.sri.wsconsultacontribuyente.ContribuyenteCompleto;
import lombok.Getter;
import lombok.Setter;

import org.primefaces.context.DefaultRequestContext;
import org.primefaces.context.RequestContext;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import ec.gob.ambiente.rcoa.facade.UsersBlacklistFacade;

/**
 * @author ishmael
 */
@ManagedBean
@ViewScoped
public class PromotorController implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -8083094528006538436L;
    private static final String SOLO_NUMEROS = "return numbersonly(this, event);";
    private static final String TAMANIO_250 = "250";
    private static final String TAMANIO_50 = "50";
    private static final String TAMANIO_15 = "15";
    private static final String TAMANIO_10 = "10";
    private static final String TAMANIO_13 = "13";
    private static final String NADA = "";
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(PromotorController.class);
    private static final String PERSONA_NATURAL = "N";
    private static final String PERSONA_JURIDICA = "J";
    private static final String GENERO_MASCULINO = "HOMBRE";
    // private static final String REQUERIDO = "\" es requerido.";
    private static final String ID_EMPRESA_MIXTA = "8";
    private static final String SIN_SERVICIO = "Sin servicio";
    private final ConsultaRucCedula consultaRucCedula = new ConsultaRucCedula(Constantes.getUrlWsRegistroCivilSri());
    @EJB
    private UsuarioFacade usuarioFacade;
    
    @EJB
    private AreaFacade areaFacade;
    
    @EJB
    private ImpedidosFacade impedidosFacade;
    @EJB
    private FormasContactoFacade formasContactoFacade;
    @EJB
    private ContactoFacade contactoFacade;
    @EJB
    private AreaUsuarioFacade areaUsuarioFacade;
    @EJB
    private UsersBlacklistFacade userblacklistfacade;    
    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
    @Getter
    @Setter
    private PromotorBean promotorBean;
    @Getter
    @Setter
    private boolean eligioPasaporte = true;

    @PostConstruct
    public void inicio() {
        promotorBean = new PromotorBean();
        cambioTipoPersona();
        promotorBean.setTipoPersona(PERSONA_NATURAL);
    }

    public void cambioTipoPersona() {
        promotorBean.setScriptTamanio(TAMANIO_250);
        promotorBean.setScriptTamanioDocumento(TAMANIO_10);
        promotorBean.setScriptNumerosDocumento(SOLO_NUMEROS);
        promotorBean.setUsuario(new Usuario());
        promotorBean.setPersona(new Persona());
        promotorBean.setOrganizacion(new Organizacion());
        promotorBean.setContacto(new Contacto());
        promotorBean.setListaContactoObligatorios(new ArrayList<Contacto>());
        promotorBean.setListaContactoOpcionales(new ArrayList<Contacto>());
        promotorBean.getPersona().setTitulo("Proponente");
    }

    public void validarCedula() {
        try {
        	Boolean validacion = userblacklistfacade.listaUsuarios(promotorBean.getUsuario().getPin());
            promotorBean.getPersona().setNombre("");
            promotorBean.setIdTipoTrato("0");
            promotorBean.getPersona().setTitulo("Proponente");
            promotorBean.setIdNacionalidad("0");
            promotorBean.getPersona().setGenero("0");
            promotorBean.setDeshabilitarRegistro(false);
            if (!validacion)
            {
                if (promotorBean.getUsuario().getPin() != null
                        && !promotorBean.getUsuario().getPin().isEmpty()) {
                    promotorBean.setCausas(impedidosFacade
                            .listarNumeroDocumentoTiposImpedimento(promotorBean
                                            .getUsuario().getPin(),
                                    TipoImpedidoEnum.CONSULTOR));
                    validarUsuarioRegistrado(promotorBean.getUsuario().getPin());
                    validarImpedido();
                    if (!("Pasaporte")
                            .equals(promotorBean.getUsuario().getDocuId())
                            && !promotorBean.isDeshabilitarRegistro()) {
                        if (("Cédula")
                                .equals(promotorBean.getUsuario().getDocuId())) {
                            if (promotorBean.getUsuario().getPin().length() == 10) {
                                Cedula cedula = consultaRucCedula
                                        .obtenerPorCedulaRC(
                                                Constantes.USUARIO_WS_MAE_SRI_RC,
                                                Constantes.PASSWORD_WS_MAE_SRI_RC,
                                                promotorBean.getUsuario().getPin());
                                cargarDatosWsCedula(cedula);
                            } else {
                                promotorBean.setDeshabilitarRegistro(true);
                                JsfUtil.addMessageError("La cédula debe tener 10 dígitos.");
                            }
                        } else {
                            if (promotorBean.getUsuario().getPin().length() == 13) {
                                ContribuyenteCompleto contribuyenteCompleto = consultaRucCedula
                                        .obtenerPorRucSRI(
                                                Constantes.USUARIO_WS_MAE_SRI_RC,
                                                Constantes.PASSWORD_WS_MAE_SRI_RC,
                                                promotorBean.getUsuario().getPin());
    							if (contribuyenteCompleto == null) {
    								JsfUtil.addMessageError("RUC inválido");
    								return;
    							}
                                if (!contribuyenteCompleto.getCodEstado().equals("PAS") || Constantes.getPermitirRUCPasivo()) {
                                    cargarDatosWsRucPersonaNatural(contribuyenteCompleto);
                                } else {
                                    promotorBean.setDeshabilitarRegistro(true);
                                    JsfUtil.addMessageError("El estado de su RUC es PASIVO. Si desea registrarse con el mismo debe activarlo en el SRI.");
                                }
                            } else {
                                promotorBean.setDeshabilitarRegistro(true);
                                JsfUtil.addMessageError("El RUC debe tener 13 dígitos.");
                            }
                        }
                    } else {
                        promotorBean.setWsEncontrado(false);
                    }
                } else {
                    promotorBean.setDeshabilitarRegistro(true);
                    JsfUtil.addMessageError("El campo 'Cédula / RUC / Pasaporte' es requerido.");
                }        	
            }
            else
            {
                promotorBean.setDeshabilitarRegistro(true);
                JsfUtil.addMessageError("El campo 'Cédula / RUC / Pasaporte' no puede ser registrado.");
            }
        } catch (ServiceException e) {
            LOG.error(e, e);
        }
    }

    private void resetCambioTipoDocumento() {
        promotorBean.getPersona().setNombre(null);
        promotorBean.getPersona().setGenero(null);
        promotorBean.setIdTipoTrato(null);
        promotorBean.setWsEncontrado(false);
        promotorBean.getPersona().setPin(null);
        promotorBean.getUsuario().setPin(null);
    }

    private void cargarDatosWsCedula(Cedula cedula) {
        if (cedula != null
                && cedula.getError().equals(Constantes.NO_ERROR_WS_MAE_SRI_RC)) {
            promotorBean.getPersona().setNombre(cedula.getNombre());
            promotorBean.getPersona().setGenero(
                    cedula.getGenero().equals("MUJER") ? "FEMENINO"
                            : "MASCULINO");
            cargarTratamiento(cedula);
            
            promotorBean.setWsEncontrado(true);
        } else {
            promotorBean.setWsEncontrado(false);
            JsfUtil.addMessageError(cedula != null ? "Debe ingresar un número de cédula válido."
                    : SIN_SERVICIO);
        }
    }

    private void cargarDatosWsRucPersonaNatural(
            ContribuyenteCompleto contribuyenteCompleto) {
        if (contribuyenteCompleto != null) {
            if (contribuyenteCompleto.getRazonSocial() != null) {
                promotorBean.getPersona().setPin(
                        contribuyenteCompleto.getNumeroRuc());
                promotorBean.getPersona().setNombre(
                        contribuyenteCompleto.getRazonSocial());
            } else {
                promotorBean.setDeshabilitarRegistro(true);
                JsfUtil.addMessageError("RUC no encontrado.");
            }
            promotorBean.setWsEncontrado(true);
        } else {
            promotorBean.setWsEncontrado(false);
            JsfUtil.addMessageError(SIN_SERVICIO);
        }
    }

    private void cargarTratamiento(Cedula cedula) {
        if (cedula.getGenero().equals(GENERO_MASCULINO)) {
            promotorBean.setIdTipoTrato("1");
        } else if (("CASADO").equals(cedula.getEstadoCivil())) {
            promotorBean.setIdTipoTrato("2");
        } else {
            promotorBean.setIdTipoTrato("3");
        }
    }

    public void validarRuc() {
        try {
        	Boolean validacion = userblacklistfacade.listaUsuarios(promotorBean.getUsuario().getPin());
            promotorBean.getOrganizacion().setNombre("");
            promotorBean.getOrganizacion().setNombreComercial("");
            promotorBean.getPersona().setPin("");
            promotorBean.getPersona().setNombre("");
            promotorBean.getPersona().setPosicion("");
            promotorBean.setIdTipoOrganizacion("0");
            promotorBean.setDeshabilitarRegistro(false);
            if (!validacion)
            {
                if (promotorBean.getUsuario().getPin() != null
                        && !promotorBean.getUsuario().getPin().isEmpty()) {
                    if (promotorBean.getUsuario().getPin().length() == 13) {
                        promotorBean.setCausas(impedidosFacade
                                .listarNumeroDocumentoTiposImpedimento(promotorBean
                                                .getOrganizacion().getRuc(),
                                        TipoImpedidoEnum.CONSULTOR));
                        validarUsuarioRegistrado(promotorBean.getUsuario().getPin());
                        validarImpedido();
                        if (!promotorBean.isDeshabilitarRegistro()) {
                            ContribuyenteCompleto contribuyenteCompleto = consultaRucCedula
                                    .obtenerPorRucSRI(
                                            Constantes.USUARIO_WS_MAE_SRI_RC,
                                            Constantes.PASSWORD_WS_MAE_SRI_RC,
                                            promotorBean.getUsuario().getPin());
                            cargarDatosWsRuc(contribuyenteCompleto, promotorBean
                                    .getUsuario().getPin());
                        }
                    } else {
                        promotorBean.setDeshabilitarRegistro(true);
                        JsfUtil.addMessageError("Debe ingresar un número de RUC válido.");
                    }
                } else {
                    promotorBean.setDeshabilitarRegistro(true);
                    JsfUtil.addMessageError("El RUC debe tener 13 dígitos.");
                }
            }
            else
            {
                promotorBean.setDeshabilitarRegistro(true);
                JsfUtil.addMessageError("El RUC no puede ser registrado.");           	
            }
        } catch (ServiceException e) {
            LOG.error(e, e);
        }
    }

    private void cargarDatosWsRuc(ContribuyenteCompleto contribuyenteCompleto,
                                  String pin) {
        if (contribuyenteCompleto != null) {
            if (contribuyenteCompleto.getTipoContribuyente() != null) {
                if (contribuyenteCompleto.getTipoContribuyente()
                        .getUltimoNivel().equals("PERSONAS NATURALES")) {
                    promotorBean.setWsEncontrado(false);
                    promotorBean.getUsuario().setNombre(pin);
                    promotorBean.getOrganizacion().setRuc(pin);
                    JsfUtil.addMessageError("RUC no pertenece a persona jurídica.");
                    return;
                }
                promotorBean.getOrganizacion().setRuc(
                        contribuyenteCompleto.getNumeroRuc());
                promotorBean.getOrganizacion().setNombre(
                        contribuyenteCompleto.getRazonSocial());
                promotorBean.getOrganizacion().setNombreComercial(
                        contribuyenteCompleto.getNombreComercial());
                promotorBean.getOrganizacion().setCargoRepresentante(
                        contribuyenteCompleto.getRepresentanteLegal()
                                .getCargo());
                promotorBean.getPersona().setPin(
                        contribuyenteCompleto.getRepresentanteLegal()
                                .getIdentificacion());
                promotorBean.getPersona().setNombre(
                        contribuyenteCompleto.getRepresentanteLegal()
                                .getNombre());
                promotorBean.setWsEncontrado(true);
            } else {
                promotorBean.setDeshabilitarRegistro(true);
                JsfUtil.addMessageError("RUC no encontrado.");
            }
        } else {
            promotorBean.getUsuario().setNombre(pin);
            promotorBean.getOrganizacion().setRuc(pin);
            promotorBean.setWsEncontrado(false);
            JsfUtil.addMessageError(SIN_SERVICIO);
        }
    }

    private void validarUsuarioRegistrado(final String pin)
            throws ServiceException {
        if (usuarioFacade.buscarUsuario(pin) != null) {
            promotorBean.setDeshabilitarRegistro(true);
            JsfUtil.addMessageError("Usuario ya se encuentra registrado.");
        }
    }

    private boolean validarMailRegistrado(String valor) {
        boolean perteneceAOtroUsuario = true;
        try {
            List<Contacto> listaMailRegistrados = contactoFacade
                    .buscarMail(valor);
            if (listaMailRegistrados != null && !listaMailRegistrados.isEmpty()) {
                return perteneceAOtroUsuario;
            } else {
                return !perteneceAOtroUsuario;
            }
        } catch (ServiceException e) {
            LOG.info("El error al validar correo electrónico.", e);
            return perteneceAOtroUsuario;
        }
    }

    private void validarImpedido() {
        if (promotorBean.getCausas() != null
                && !promotorBean.getCausas().isEmpty()) {
            promotorBean.setDeshabilitarRegistro(true);
            JsfUtil.addMessageError(promotorBean.getCausas());
        }
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
                JsfUtil.addMessageError("La información de contacto ya ha sido ingresada.");
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
            if (validarMailRegistrado(promotorBean.getContacto().getValor())) {
                JsfUtil.addMessageError("El correo electrónico ingresado ya se encuentra registrado.");
                return;
            }
        }

        if (promotorBean.getTipoPersona().equals(PERSONA_NATURAL)) {
            promotorBean.getContacto().setPersona(promotorBean.getPersona());
        } else {
            promotorBean.getContacto().setOrganizacion(
                    promotorBean.getOrganizacion());
        }
        promotorBean.getListaContactoOpcionales().add(
                promotorBean.getContacto());
        promotorBean.setContacto(new Contacto());
        promotorBean.setIdFormaContacto(null);
    }

    private boolean validarMail(String email) {
        boolean valor = false;
        valor = !JsfUtil.validarMail(email);
        return valor;
    }

    public void removerContacto(Contacto contacto) {
        promotorBean.getListaContactoOpcionales().remove(contacto);
        promotorBean.setIdFormaContacto(null);
    }

    public void guardar() {
        try {
            promotorBean.getUsuario().setTempPassword(
                    JsfUtil.generatePassword());
            promotorBean.getUsuario().setPassword(
                    JsfUtil.claveEncriptadaSHA1(promotorBean.getUsuario()
                            .getTempPassword()));
            promotorBean.getUsuario().setFechaCreacion(new Date());
            promotorBean.getPersona().setUbicacionesGeografica(
                    new UbicacionesGeografica(Integer.valueOf(promotorBean
                            .getIdParroquia())));
            if (promotorBean.getTipoPersona().equals(PERSONA_NATURAL)) {
                promotorBean.getPersona().setTipoTratos(
                        new TipoTratos(Integer.valueOf(promotorBean
                                .getIdTipoTrato())));
                promotorBean.getPersona().setNacionalidad(
                        new Nacionalidad(Integer.valueOf(promotorBean
                                .getIdNacionalidad())));
                promotorBean.getUsuario().setNombre(
                        promotorBean.getUsuario().getPin());
                promotorBean.getPersona().setPin(promotorBean.getUsuario().getPin());
                promotorBean.getUsuario().setPersona(promotorBean.getPersona());
                promotorBean.getPersona().setContactos(
                        promotorBean.getListaContactoObligatorios());
                for (Contacto c : promotorBean.getListaContactoOpcionales()) {
                    promotorBean.getPersona().getContactos().add(c);
                }
//            	if (promotorBean.getUsuario().getArea()==null){
//            		promotorBean.getUsuario().setArea(areaFacade.getArea(233));
//            	}else{
//            		promotorBean.getUsuario().setArea(promotorBean.getUsuario().getArea());
//            	}
                usuarioFacade.guardar(
                        promotorBean.getUsuario(),
                        cargarMail(promotorBean.getPersona().getNombre(),
                                promotorBean.getUsuario().getNombre(),
                                promotorBean.getUsuario().getTempPassword(),
                                promotorBean.getEmail()), true);
                
                //codigo nuevo para guardar en la tabla area_usuario
                AreaUsuario areaUsuario = new AreaUsuario();
                areaUsuario.setArea(areaFacade.getArea(233));
                areaUsuario.setUsuario(promotorBean.getUsuario());
                areaUsuario.setEstado(true);
                areaUsuario.setPrincipal(true);
                
                areaUsuarioFacade.guardar(areaUsuario, promotorBean.getUsuario());
                
                promotorBean.getUsuario().getListaAreaUsuario().add(areaUsuario);
                
            } else {
                promotorBean.getOrganizacion().setContactos(
                        promotorBean.getListaContactoObligatorios());
                for (Contacto c : promotorBean.getListaContactoOpcionales()) {
                    c.setOrganizacion(promotorBean.getOrganizacion());
                    promotorBean.getOrganizacion().getContactos().add(c);
                }
                promotorBean.getUsuario().setNombre(
                        promotorBean.getOrganizacion().getRuc());
                promotorBean.getPersona().setOrganizaciones(
                        new ArrayList<Organizacion>());
                promotorBean.getPersona().getOrganizaciones()
                        .add(promotorBean.getOrganizacion());
                promotorBean.getPersona().setTipoTratos(new TipoTratos(4));
                promotorBean.getUsuario().setPersona(promotorBean.getPersona());
                promotorBean.getOrganizacion().setParticipacionEstado(
                        promotorBean.getIdTipoOrganizacion().equals(
                                ID_EMPRESA_MIXTA) ? promotorBean
                                .getOrganizacion().getParticipacionEstado()
                                : null);
                promotorBean.getOrganizacion().setTipoOrganizacion(
                        new TipoOrganizacion(Integer.valueOf(promotorBean
                                .getIdTipoOrganizacion())));
                promotorBean.getOrganizacion().setIdUbicacionGeografica(
                        Integer.valueOf(promotorBean.getIdParroquia()));
                promotorBean.getOrganizacion().setPersona(
                        promotorBean.getPersona());
                promotorBean.getOrganizacion().setCalificado(false);

//                if (promotorBean.getUsuario().getArea()==null){
//            		promotorBean.getUsuario().setArea(areaFacade.getArea(233));
//            	}else{
//            		promotorBean.getUsuario().setArea(promotorBean.getUsuario().getArea());
//            	}
            	
                usuarioFacade.guardar(
                        promotorBean.getUsuario(),
                        cargarMail(promotorBean.getPersona().getNombre(),
                                promotorBean.getOrganizacion().getRuc(),
                                promotorBean.getUsuario().getTempPassword(),
                                promotorBean.getEmail()), true);
                
                AreaUsuario areaUsuario = new AreaUsuario();
                areaUsuario.setArea(areaFacade.getArea(233));
                areaUsuario.setUsuario(promotorBean.getUsuario());
                areaUsuario.setEstado(true);
                areaUsuario.setPrincipal(true);
                
                areaUsuarioFacade.guardar(areaUsuario, promotorBean.getUsuario());
                promotorBean.getUsuario().getListaAreaUsuario().add(areaUsuario);
            }
			try {
				
				if (promotorBean.getTipoPersona().equals(PERSONA_NATURAL)) {
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
                	
                	usuarioFacade.insertarRolesUsuarioVerde(promotorBean.getUsuario().getNombre(), 25);
                	
                }else{
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
                			promotorBean.getOrganizacion().getTipoOrganizacion().getId().toString(),Integer.parseInt(promotorBean.getIdParroquia()),promotorBean.getUsuario().getListaAreaUsuario().get(0).getArea()
                			, promotorBean.getUsuario().getPassword(),listacontactos,token,estadoUsuario,funcionario,plantacentral,encargado
                			);
                	
                	usuarioFacade.insertarRolesUsuarioVerde(promotorBean.getUsuario().getNombre(), 25);
                	
//                	usuarioFacade.ejecutarMigracionUsuario();
                }
				
//				usuarioFacade.ejecutarMigracionUsuario();
			} catch (Exception e) {
				LOG.error(
						"Error al migrar los datos del usuario al registrarse.",
						e);
			}
            JsfUtil.redirectTo("/usuarioRegistrado.jsf");
        } catch (NumberFormatException e) {
            LOG.error(e, e);
        } catch (ServiceException e) {
            LOG.error(e, e);
            JsfUtil.addMessageError(e.getMessage());
        } catch (RuntimeException e) {
            LOG.error(e, e);
		JsfUtil.addMessageError("Error al realizar la operación. Por favor intente más tarde");
        }

    }

    private NotificacionesMails cargarMail(final String nombres,
                                           final String login, final String clave, final String email) {
    	 NotificacionesMails nm = new NotificacionesMails();
         nm.setAsunto("Aprobación registro de usuario");
         StringBuilder mensaje = new StringBuilder();
         mensaje.append("<p>Estimado/a Se&ntilde;or/a ").append(nombres)
                 .append("</p>").append("<br/>");
         mensaje.append("<p>Confirmamos que su solicitud de registro de usuario en el sistema SUIA fue aprobada con los siguientes datos: </p>");
         mensaje.append("<ul><li>Nombre de usuario: ").append(login)
                 .append("</li>");
         mensaje.append("<li>Contrase&ntilde;a: ").append(clave).append("</li>")
                 .append("</ul>");
         mensaje.append(
                 "<p>Te compartimos los videos tutoriales para mejor uso del sistema")
                 .append("</p>");
//         mensaje.append(" <font size=\"20\" color=\"red\"> <ul> <li> <font size=\"14\" color=\"red\"> <a href=\"http://maetransparente.ambiente.gob.ec/documentacion/cursos/RegulaAmbien/ReseteoClave.mp4\" target=\"_blank\"> Tutorial reseto de claves")
//         .append("</a></li></font>");
         mensaje.append("<ul> <li>  <font size=\"14\" color=\"red\"> <a href=\"http://maetransparente.ambiente.gob.ec/documentacion/cursos/RegulaAmbien/TomarCoordenadas.mp4\" target=\"_blank\"> Toma de coordenadas")
         .append("</a></li></font>");
         mensaje.append("<li>  <font size=\"14\" color=\"red\"> <a href=\"http://maetransparente.ambiente.gob.ec/documentacion/cursos/RegulaAmbien/CreacionProyecto.mp4\" target=\"_blank\"> Tutorial registro del proyecto")
         .append("</a></li></font>");
         mensaje.append("</ul></font><br/>");
                                
         mensaje.append("<br/><p>Saludos,").append("</p>").append("<br/>");
         mensaje.append("<p>Ministerio del Ambiente y Agua").append("</p>")
                 .append("<br/>");
         nm.setContenido(mensaje.toString());
         nm.setEmail(email);
         nm.setTipoMensaje(TipoMensajeMailEnum.TEXT_HTML.getNemonico());
        return nm;
    }

    public void validarRegistro() {
        promotorBean.setListaContactoObligatorios(new ArrayList<Contacto>());
        FormasContacto fcTelf = formasContactoFacade
                .buscarPorNombre("*TELÉFONO");
        Contacto cTelf = new Contacto();
        cTelf.setFormasContacto(fcTelf);
        cTelf.setEstado(true);
        cTelf.setValor(promotorBean.getTelefono());
        FormasContacto fcCel = formasContactoFacade.buscarPorNombre("*CELULAR");
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
        FormasContacto fcEma = formasContactoFacade.buscarPorNombre("*EMAIL");
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
        promotorBean.getListaContactoObligatorios().add(cTelf);
        promotorBean.getListaContactoObligatorios().add(cCel);
        promotorBean.getListaContactoObligatorios().add(cDir);
        promotorBean.getListaContactoObligatorios().add(cEma);

        // Validaciones
        List<String> listaMensajes = new ArrayList<String>();
        if (usuarioFacade.buscarUsuario(promotorBean.getUsuario().getPin()) != null) {
            promotorBean.setDeshabilitarRegistro(true);
            JsfUtil.addMessageError("Usuario ya se encuentra registrado.");
            return;
        }

        if (promotorBean.getTipoPersona().equals(PERSONA_NATURAL)) {
            if (validarPersonaNatural() & validarComunes()) {
                DefaultRequestContext.getCurrentInstance().execute(
                        "PF('dialogGuardar').show();");
            } else {
                JsfUtil.addMessageError(listaMensajes);
                RequestContext.getCurrentInstance().update("msgs");
            }
        }

        if (promotorBean.getTipoPersona().equals(PERSONA_JURIDICA)) {
            if (validarPersonaJuridica() & validarComunes()) {
                DefaultRequestContext.getCurrentInstance().execute(
                        "PF('dialogGuardar').show();");
            } else {
                JsfUtil.addMessageError(listaMensajes);
                RequestContext.getCurrentInstance().update("msgs");
            }
        }
    }

    public boolean validarPersonaNatural() {
        List<String> listaMensajes = new ArrayList<String>();
        if (promotorBean.getUsuario().getPin() != null
                && promotorBean.getUsuario().getPin().isEmpty()) {
            listaMensajes
                    .add("El campo 'Cédula / RUC / Pasaporte' es requerido.");
        } /* else {
            if (!promotorBean.getUsuario().getDocuId().equals("Pasaporte") && !JsfUtil.validarCedulaORUC(promotorBean.getUsuario().getPin())) {
                listaMensajes
                        .add("El campo 'Cédula / RUC / Pasaporte' no es válido.");
            }
        }*/
        if (promotorBean.getPersona().getNombre() == null
                || promotorBean.getPersona().getNombre().isEmpty()) {
            listaMensajes.add("El campo 'Nombres y Apellidos' es requerido.");
        }
        if (promotorBean.getIdTipoTrato().isEmpty()) {
            listaMensajes.add("El campo 'Tratamiento' es requerido.");
        }
        if (promotorBean.getPersona().getTitulo() == null
                || promotorBean.getPersona().getTitulo().isEmpty()) {
            listaMensajes.add("El campo 'Título Académico' es requerido.");
        }
        if (promotorBean.getIdNacionalidad().isEmpty()) {
            listaMensajes.add("El campo 'Nacionalidad' es requerido.");
        }
        if (promotorBean.getPersona().getGenero() == null
                || promotorBean.getPersona().getGenero().isEmpty()) {
            listaMensajes.add("El campo 'Género' es requerido.");
        }
        if (promotorBean.getIdFormaContacto() != null
                && !promotorBean.getIdFormaContacto().equals("")) {
            if (!promotorBean.getIdFormaContacto().equals("0")) {
                listaMensajes
                        .add("Debe llenar el campo seleccionado de 'Información de Contacto Adicional'.");
            }
        }
        if (listaMensajes.isEmpty()) {
            return true;
        } else {
            JsfUtil.addMessageError(listaMensajes);
            return false;
        }
    }

    public boolean validarPersonaJuridica() {
        List<String> listaMensajes = new ArrayList<String>();
        if (promotorBean.getUsuario().getPin() != null
                && promotorBean.getUsuario().getPin().isEmpty()) {
            listaMensajes.add("El campo 'RUC' es requerido.");
        } 
//        else {
//            if (!JsfUtil.validarCedulaORUC(promotorBean.getPersona().getPin())) {
//                listaMensajes.add("El campo 'RUC' no es válido.");
//            }
//        }
        if (promotorBean.getOrganizacion().getNombre() == null
                || promotorBean.getOrganizacion().getNombre().isEmpty()) {
            listaMensajes.add("El campo 'Nombre organización' es requerido.");
        }
        if (promotorBean.getPersona().getPin() == null
                || promotorBean.getPersona().getPin().isEmpty()) {
            listaMensajes
                    .add("El campo 'Cédula / RUC Representante' es requerido.");
        }
        if (promotorBean.getPersona().getNombre() == null
                || promotorBean.getPersona().getNombre().isEmpty()) {
            listaMensajes
                    .add("El campo 'Nombres y apellidos del representante' es requerido.");
        }
        if (promotorBean.getPersona().getPosicion().equals("")) {
            listaMensajes.add("El campo 'Cargo Representante' es requerido.");
        }
        if (promotorBean.getIdTipoOrganizacion().isEmpty()) {
            listaMensajes.add("El campo 'Tipo de Organización' es requerido.");
        }
        if (promotorBean.getIdFormaContacto() != null
                && !promotorBean.getIdFormaContacto().equals("")) {
            if (!promotorBean.getIdFormaContacto().equals("0")) {
                listaMensajes
                        .add("Debe llenar el campo seleccionado de 'Información de Contacto Adicional'.");
            }
        }
        if (listaMensajes.isEmpty()) {
            return true;
        } else {
            JsfUtil.addMessageError(listaMensajes);
            return false;
        }
    }

    public boolean validarComunes() {
        List<String> listaMensajes = new ArrayList<String>();
        if (promotorBean.getTelefono() == null
                || promotorBean.getTelefono().equals("")) {
            listaMensajes.add("El campo 'Teléfono' es requerido.");
        }
        if (promotorBean.getCelular() == null
                || promotorBean.getCelular().equals("")) {
            listaMensajes.add("El campo 'Celular' es requerido.");
        }
        if (promotorBean.getEmail() == null
                || promotorBean.getEmail().equals("")) {
            listaMensajes.add("El campo 'Correo electrónico' es requerido.");
        }
        if (!promotorBean.getEmail().equals("")
                && validarMail(promotorBean.getEmail())) {
            listaMensajes
                    .add("La dirección de correo ingresada es incorrecta, por favor verifique.");
        }
        if (!promotorBean.getEmail().isEmpty()
                && !promotorBean.getEmail().equals("")) {
            if (validarMailRegistrado(promotorBean.getEmail())) {
                listaMensajes
                        .add("El correo electrónico ingresado ya se encuentra registrado.");
            }
        }

        if (promotorBean.getDireccion() == null
                || promotorBean.getDireccion().equals("")) {
            listaMensajes.add("El campo 'Dirección' es requerido.");
        }
        if (promotorBean.getListaContactoObligatorios() == null
                || promotorBean.getListaContactoObligatorios().isEmpty()) {
            listaMensajes.add("Debe ingresar la información de Contacto.");
        }
        if (promotorBean.getIdParroquia() == null
                || promotorBean.getIdParroquia().isEmpty()) {
            listaMensajes.add("Los campos de 'Ubicación' son requeridos.");
        }
        if (!promotorBean.isAceptaTerminos()) {
            listaMensajes.add("Debe aceptar las condiciones de uso.");
        }
        if (listaMensajes.isEmpty()) {
            return true;
        } else {
            JsfUtil.addMessageError(listaMensajes);
            return false;
        }
    }

    public void cancelar() {
        loginBean.cerrarSession();
    }

    public void validarNumeros() {
        if (getPromotorBean().getIdFormaContacto() != null && !getPromotorBean().getIdFormaContacto().trim().isEmpty()) {
            int  opcion = new Integer(getPromotorBean()
                    .getIdFormaContacto());

            switch (opcion) {
                case FormasContacto.CELULAR:
                    getPromotorBean().setScriptNumeros(SOLO_NUMEROS);
                    getPromotorBean().setScriptTamanio(TAMANIO_15);
                    break;
                case FormasContacto.TELEFONO:
                    getPromotorBean().setScriptNumeros(SOLO_NUMEROS);
                    getPromotorBean().setScriptTamanio(TAMANIO_15);
                    break;
                case FormasContacto.FAX:
                    getPromotorBean().setScriptNumeros(SOLO_NUMEROS);
                    getPromotorBean().setScriptTamanio(TAMANIO_15);
                    break;
                case FormasContacto.DIRECCION:
                    getPromotorBean().setScriptNumeros(NADA);
                    getPromotorBean().setScriptTamanio(TAMANIO_250);
                    break;
                case FormasContacto.EMAIL:
                    getPromotorBean().setScriptNumeros(NADA);
                    getPromotorBean().setScriptTamanio(TAMANIO_50);
                    break;
                case FormasContacto.POBOX:
                    getPromotorBean().setScriptNumeros(NADA);
                    getPromotorBean().setScriptTamanio(TAMANIO_250);
                    break;
                case FormasContacto.URL:
                    getPromotorBean().setScriptNumeros(NADA);
                    getPromotorBean().setScriptTamanio(TAMANIO_250);
                    break;
                case FormasContacto.POSTFIX_ZIP:
                    getPromotorBean().setScriptNumeros(NADA);
                    getPromotorBean().setScriptTamanio(TAMANIO_250);
                    break;
                default:
                    throw new IllegalStateException();
            }
        }
    }

    public void validarIngresoCampoDocumento() {
        String opcion = getPromotorBean().getUsuario().getDocuId();
        switch (opcion) {
            case "Cédula":
                getPromotorBean().setScriptNumerosDocumento(SOLO_NUMEROS);
                getPromotorBean().setScriptTamanioDocumento(TAMANIO_10);
                eligioPasaporte = true;
                break;
            case "RUC":
                getPromotorBean().setScriptNumerosDocumento(SOLO_NUMEROS);
                getPromotorBean().setScriptTamanioDocumento(TAMANIO_13);
                eligioPasaporte = true;
                break;
            case "Pasaporte":
                getPromotorBean().setScriptNumerosDocumento(NADA);
                getPromotorBean().setScriptTamanioDocumento(TAMANIO_15);
                eligioPasaporte = false;
                break;
            default:
                throw new IllegalStateException();
        }
        resetCambioTipoDocumento();
    }

}
