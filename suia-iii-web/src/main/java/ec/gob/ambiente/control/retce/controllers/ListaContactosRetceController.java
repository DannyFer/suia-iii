package ec.gob.ambiente.control.retce.controllers;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import ec.gob.ambiente.cliente.ConsultaRucCedula;
import ec.gob.ambiente.retce.model.TecnicoResponsable;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.registrocivil.consultacedula.Cedula;
import ec.gov.sri.wsconsultacontribuyente.ContribuyenteCompleto;

@ManagedBean
@ViewScoped
public class ListaContactosRetceController implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(ListaContactosRetceController.class);	
	
	@EJB
	private OrganizacionFacade organizacionFacade;
	
    @EJB
    private ContactoFacade contactoFacade;

	@EJB
	private UsuarioFacade usuarioFacade;
	
	private final ConsultaRucCedula consultaRucCedula = new ConsultaRucCedula(Constantes.getUrlWsRegistroCivilSri());
	
    @PostConstruct
	public void init() {

	}
    
    public TecnicoResponsable validarCedulaListener(String cedulaRuc)
	{
    	TecnicoResponsable tecnicoResponsable = new TecnicoResponsable(); 
    	if(JsfUtil.validarCedulaORUC(cedulaRuc))
		{	
			try {
				if(cedulaRuc.length()==10){
					Usuario usuario =usuarioFacade.buscarUsuario(cedulaRuc);
					if(usuario!=null)
					{
						tecnicoResponsable.setIdentificador(usuario.getNombre());
						tecnicoResponsable.setNombre(usuario.getPersona().getNombre());
						List<Contacto> listaContacto = contactoFacade.buscarPorPersona(usuario.getPersona());					
						for (Contacto contacto : listaContacto) {
							switch (contacto.getFormasContacto().getId()) {
							case FormasContacto.EMAIL:
								tecnicoResponsable.setCorreo(contacto.getValor());
								break;
							case FormasContacto.TELEFONO:
								tecnicoResponsable.setTelefono(contacto.getValor());
								break;
							case FormasContacto.CELULAR:
								tecnicoResponsable.setCelular(contacto.getValor());
								break;
							default:
								break;
							}
						}						
						return tecnicoResponsable;
					}
					
					Cedula cedula = consultaRucCedula
			                .obtenerPorCedulaRC(
			                        Constantes.USUARIO_WS_MAE_SRI_RC,
			                        Constantes.PASSWORD_WS_MAE_SRI_RC,
			                        cedulaRuc);
					tecnicoResponsable.setIdentificador(cedula.getCedula());
					tecnicoResponsable.setNombre(cedula.getNombre());
				}else if(cedulaRuc.length()==13){
					Organizacion orga=organizacionFacade.buscarPorRuc(cedulaRuc);
					if(orga!=null)
					{
						tecnicoResponsable.setIdentificador(orga.getRuc());
						tecnicoResponsable.setNombre(orga.getNombre());
						List<Contacto> listaCcontacto = contactoFacade.buscarPorOrganizacion(orga);	
						for (Contacto contacto : listaCcontacto) {
							switch (contacto.getFormasContacto().getId()) {
							case FormasContacto.EMAIL:
								tecnicoResponsable.setCorreo(contacto.getValor());
								break;
							case FormasContacto.TELEFONO:
								tecnicoResponsable.setTelefono(contacto.getValor());
								break;
							case FormasContacto.CELULAR:
								tecnicoResponsable.setCelular(contacto.getValor());
								break;
							default:
								break;
							}
						}
						return tecnicoResponsable;
					}
					
					ContribuyenteCompleto contribuyenteCompleto = consultaRucCedula
			                .obtenerPorRucSRI(
			                        Constantes.USUARIO_WS_MAE_SRI_RC,
			                        Constantes.PASSWORD_WS_MAE_SRI_RC,
			                        cedulaRuc);
					if(contribuyenteCompleto!=null)
					{
						tecnicoResponsable.setIdentificador(contribuyenteCompleto.getNumeroRuc());
						tecnicoResponsable.setNombre(contribuyenteCompleto.getRazonSocial());
						tecnicoResponsable.setCorreo(contribuyenteCompleto.getEmail());
						tecnicoResponsable.setTelefono(contribuyenteCompleto.getTelefonoDomicilio());					
					}else
					{
						JsfUtil.addMessageError("Error al validar Ruc, Servicio Web No Disponible");
					}
				}
				return tecnicoResponsable;
			} catch (Exception e) {
				JsfUtil.addMessageError("Error al validar Cédula o Ruc");
				LOG.error(e.getMessage());
				return tecnicoResponsable;
			}
		}
    	return tecnicoResponsable;
	}
}
