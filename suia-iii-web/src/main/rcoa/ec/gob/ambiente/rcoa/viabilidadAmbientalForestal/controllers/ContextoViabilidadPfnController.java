package ec.gob.ambiente.rcoa.viabilidadAmbientalForestal.controllers;

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

import org.primefaces.context.RequestContext;

import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.bandeja.BandejaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ContextoViabilidadPfnController {
	
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private BandejaFacade bandejaFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private ContactoFacade contactoFacade;
	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;

    @Getter
    @Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
	
    private Map<String, Object> variables;
	
	@Getter
	@Setter
	private Usuario usuarioTecnico;
	
	@Getter
	@Setter
	private List<UbicacionesGeografica> ubicacionTecnico;
	
	@Getter
	@Setter
	private String etiqueta, nombreTecnico, correo, numeroTelefonico, celular;
	
	@Getter
	@Setter
	private Boolean verExtra;
	
	@PostConstruct
	private void init(){
		try{
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			
			verExtra = true;
			
			String tarea = bandejaFacade.getNombreTarea(JsfUtil.getCurrentTask().getTaskId());
			if (tarea.equals("revisarInformacionApoyoForestal") || tarea.equals("elaborarDocumentosApoyoForestal")) {
				String usrTecnico = (String) variables.get("tecnicoResponsable");
				usuarioTecnico = usuarioFacade.buscarUsuario(usrTecnico);
				
				etiqueta = "Técnico responsable";
				nombreTecnico = usuarioTecnico.getPersona().getNombre();
			} else if(tarea.equals("elaborarDocumentosForestal")) {
				if(variables.containsKey("tecnicoApoyo")) {
					String usrTecnico = (String) variables.get("tecnicoApoyo");
					usuarioTecnico = usuarioFacade.buscarUsuario(usrTecnico);
					
					etiqueta = "Técnico apoyo";
					nombreTecnico = usuarioTecnico.getPersona().getNombre();
				} else {
					verExtra = false;
				}
			}
			
		}catch(Exception e){
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}
	
	public void visualizarInformacion() {
		try {
			List<Contacto> listaContactosPersona = contactoFacade.buscarPorPersona(usuarioTecnico.getPersona());
	
			for (Contacto contacto : listaContactosPersona) {
				if (contacto.getFormasContacto().getId() == FormasContacto.EMAIL) {
					correo = contacto.getValor();
				}
	
				if (contacto.getFormasContacto().getId() == FormasContacto.TELEFONO) {
					numeroTelefonico = contacto.getValor();
				}
	
				if (contacto.getFormasContacto().getId() == FormasContacto.CELULAR) {
					celular = contacto.getValor();
				}
			}
			
			ubicacionTecnico = new ArrayList<UbicacionesGeografica>();

			UbicacionesGeografica ubicacion = ubicacionGeograficaFacade
									.ubicacionCompleta(usuarioTecnico.getPersona().getIdUbicacionGeografica());
			
			ubicacionTecnico.add(ubicacion);
			
			RequestContext context = RequestContext.getCurrentInstance();
			context.update("form:pnlExtraTecnico");
	    	context.execute("PF('dlgInfoTecnico').show();");
		}catch(Exception e){
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos.");
		}
	}
	
}