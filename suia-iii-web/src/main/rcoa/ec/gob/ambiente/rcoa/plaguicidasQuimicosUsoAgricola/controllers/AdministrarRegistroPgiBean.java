package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.controllers;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.rcoa.enums.CatalogoTipoCoaEnum;
import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.NumeroPlanGestionIntegralFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.NumeroPlanGestionIntegral;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class AdministrarRegistroPgiBean {
	
	@EJB
	private CatalogoCoaFacade catalogoCoaFacade;
	@EJB
	private NumeroPlanGestionIntegralFacade numeroPgiFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@Getter
	@Setter
	private List<NumeroPlanGestionIntegral> listaNumeroPgi;
	@Getter
	@Setter
	private List<CatalogoGeneralCoa> listaGremio;
	
	@Getter
	@Setter
	private NumeroPlanGestionIntegral numeroPgi;
	
	@Getter
	@Setter
	private String cedulaOperador;
	
	@PostConstruct
	public void init(){
		
	}
	
	public void cargarRegistroPgi() {
		numeroPgi = new NumeroPlanGestionIntegral();
		
		listaGremio = catalogoCoaFacade.obtenerCatalogo(CatalogoTipoCoaEnum.PQUA_GREMIO);
		
		cargarPlanes();
	}
	
	private void cargarPlanes() {
		listaNumeroPgi = numeroPgiFacade.listaNumeroPlan();
		
		List<NumeroPlanGestionIntegral> listaOperadoresSga = numeroPgiFacade.listaOperadoresSga();
		listaNumeroPgi.addAll(0, listaOperadoresSga);
	}
	
	public void buscarCedula(){
		Usuario operador = usuarioFacade.buscarUsuario(cedulaOperador);
		if(operador != null && operador.getId() != null){
			Boolean existe = false;
			for (NumeroPlanGestionIntegral pgi : listaNumeroPgi) {
				if(pgi.getOperador().getId().equals(operador.getId())) {
					 existe = true;
					 break;
				}
			}
			
			if(existe) {
				JsfUtil.addMessageError("Ya existe un registro para el operador ingresado");
				numeroPgi.setOperador(null);
				cedulaOperador = null;
			} else {
				List<String> infoOperador = usuarioFacade.recuperarNombreOperador(operador);
				
				numeroPgi.setOperador(operador);
				numeroPgi.setNombreOperador(infoOperador.get(0));
			}
		}else{
			JsfUtil.addMessageError("Usuario no encontrado, por favor solicitar el registro del usuario");
			numeroPgi.setOperador(null);
			cedulaOperador = null;
		}
	}
	
	public void agregarPgi(){
		numeroPgi = new NumeroPlanGestionIntegral();
		numeroPgi.setEsOperadorSga(false);
		
		cedulaOperador = null;
	}
	
	public void editarPgi(NumeroPlanGestionIntegral pgi){
		numeroPgi = pgi;
		cedulaOperador = numeroPgi.getOperador().getNombre();
	}
	
	public void eliminarPgi(NumeroPlanGestionIntegral pgi){
		pgi.setEstado(false);
		numeroPgiFacade.guardar(pgi);
		
		cargarPlanes();
	}
	
	public void limpiarPgi() {
		numeroPgi = new NumeroPlanGestionIntegral();
		cedulaOperador = null;
	}

	public void aceptarPgi() {
		try {
			numeroPgiFacade.guardar(numeroPgi);
			
			cargarPlanes();
			
			numeroPgi = new NumeroPlanGestionIntegral();
			cedulaOperador = null;
			
			JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public void validateRegistroPgi(FacesContext context, UIComponent validate, Object value) throws RemoteException {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		if (numeroPgi != null
				&& (numeroPgi.getOperador() == null || numeroPgi.getOperador().getId() == null)) {
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe ingresar información válida del operador", null));
		}
		
		if (!errorMessages.isEmpty()) {
			throw new ValidatorException(errorMessages);
		}
	}
	
}
