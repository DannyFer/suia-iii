package ec.gob.ambiente.rcoa.simulador.controller;



import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import ec.gob.ambiente.cliente.ConsultaRucCedula;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.registrocivil.consultacedula.Cedula;
import ec.gov.sri.wsconsultacontribuyente.ContribuyenteCompleto;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class InicioSimuladorController implements Serializable  {
	

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(InicioSimuladorController.class);
	
	private final ConsultaRucCedula consultaRucCedula= new ConsultaRucCedula(Constantes.getUrlWsRegistroCivilSri());
	
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	
	@Getter
    @Setter
    private String rucCedula;
	
	@Getter
    @Setter
    private String nombreUsuario;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

	@PostConstruct
	private void inicio() {
		rucCedula="";
		try {

		} catch (Exception e) {
		}
	}
	
	public void ingresar() {
		loginBean=new LoginBean();
		
		Usuario userSistema = usuarioFacade.buscarUsuario(rucCedula);
		
		if(userSistema != null && userSistema.getId() != null) {
			try {
				if (userSistema.getPersona() != null) {
					if (organizacionFacade.tieneOrganizacionPorPersona(userSistema.getPersona())) {
						Organizacion organizacion = organizacionFacade
								.buscarPorPersona(userSistema.getPersona(), userSistema.getNombre());
						if (organizacion != null
								&& organizacion.getNombre() != null
								&& organizacion.getRuc().trim().equals(userSistema.getNombre())) {
							nombreUsuario = organizacion.getNombre();
						}
					} else if (userSistema.getPersona().getNombre() != null && !userSistema.getPersona().getNombre().trim().isEmpty()) {
						nombreUsuario = userSistema.getPersona().getNombre().trim();
					}
				}
				
				JsfUtil.cargarObjetoSession("rucCedula", rucCedula);
				JsfUtil.cargarObjetoSession("nombreUsuario", nombreUsuario);
				JsfUtil.redirectTo("/pages/rcoa/simulador/registroProyectoSimulador.jsf");
			} catch (ServiceException e) {
				LOG.error("Error al obtener la organización de la persona.", e);
			}
		} else {
			if (validarCedulaRuc(rucCedula)) {
				JsfUtil.cargarObjetoSession("rucCedula", rucCedula);
				JsfUtil.cargarObjetoSession("nombreUsuario", nombreUsuario);
				JsfUtil.redirectTo("/pages/rcoa/simulador/registroProyectoSimulador.jsf");
			 }else {
				 LOG.error("Debe ingresar un RUC o una Cédula validos");
	             JsfUtil.addMessageError("Debe ingresdar un ruc o una cedula validos");
	         }
		}
	}
	
	public boolean validarCedulaRuc(String cedulaRuc)
	{		
		try {
			if(JsfUtil.validarCedulaORUC(cedulaRuc))
			{	
			
				if(cedulaRuc.length()==10){
	
					
					Cedula cedula = consultaRucCedula
			                .obtenerPorCedulaRC(
			                        Constantes.USUARIO_WS_MAE_SRI_RC,
			                        Constantes.PASSWORD_WS_MAE_SRI_RC,
			                        cedulaRuc);
					nombreUsuario=cedula.getNombre();
					return true;
				}else if(cedulaRuc.length()==13){

					
					ContribuyenteCompleto contribuyenteCompleto = consultaRucCedula
			                .obtenerPorRucSRI(
			                        Constantes.USUARIO_WS_MAE_SRI_RC,
			                        Constantes.PASSWORD_WS_MAE_SRI_RC,
			                        cedulaRuc);
					
					if(contribuyenteCompleto==null)
					{
						JsfUtil.addMessageError("Error al validar Ruc, Servicio Web No Disponible");
						return false;
					}
					else {
						nombreUsuario=contribuyenteCompleto.getRazonSocial();
						return true;
					}
				}				
			
			}
			return false;
		} catch (Exception e) {
			JsfUtil.addMessageError("Error al validar Cédula o Ruc");
			LOG.error(e.getMessage());
			return false;
		}
		
	}
}
