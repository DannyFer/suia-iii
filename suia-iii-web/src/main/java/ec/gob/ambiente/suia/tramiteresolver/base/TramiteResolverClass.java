package ec.gob.ambiente.suia.tramiteresolver.base;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.tramiteresolver.classes.LabelModel;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;

public abstract class TramiteResolverClass<T> implements CompleteOperation {

	public static final String EXPRESSION_PARAMETER_TRAMITE = "EXPRESSION_PARAMETER_TRAMITE";

	
	@Getter
	protected T tramite;

	private Usuario usuario;

	@Getter
	private String proponente;
	
	/**
	 * Variables para contacto
	 */
	@Getter
	private String email;
	
	@Getter
	private String numeroTelefonico;
	
	@Getter
	private String celular;

	@Getter
	private List<UbicacionesGeografica> ubicacionProponente;

	@Getter
	private List<LabelModel> camposTramite;

	public abstract String getTramiteAsString();

	public abstract String getNombreVariableResolverTramite();

	public abstract void setTramite(Object valueVariableResolverTramite) throws Exception;

	public abstract Usuario getUsuarioProponente();

	public abstract List<LabelModel> getCamposMostrarTramite();

	public abstract String getUrlToShowTramite();

	public List<LabelModel> getCamposMostrarTramiteValidated() {
		if (isDataResolved())
			return getCamposMostrarTramite();
		return null;
	}

	public void viewTramite() {
		endOperation(null);
	}

	public void initProponente() throws Exception {
		if (isDataResolved()) {
			this.usuario = getUsuarioProponente();
			cargarProponente();
		}
	}

	public void initTramite() {
		if (isDataResolved()) {
			this.camposTramite = getCamposMostrarTramite();
		}
	}

	private void cargarProponente() throws Exception {
		OrganizacionFacade organizacionFacade = getFachada(OrganizacionFacade.class);
		//Cris F: aumento de contacto Facade para contactos
		ContactoFacade contactoFacade = getFachada(ContactoFacade.class);
		UbicacionGeograficaFacade ubicacionGeograficaFacade = getFachada(UbicacionGeograficaFacade.class);

		UbicacionesGeografica ubicacionesGeografica = null;
		if (usuario.getPersona() != null) {
			if (usuario.getPersona().getIdNacionalidad() != null) {
				ubicacionesGeografica = ubicacionGeograficaFacade.ubicacionCompleta(usuario.getPersona()
						.getIdUbicacionGeografica());
				proponente = usuario.getPersona().getNombre();
				/**
				 * CF: aumento para visualización de contactos 
				 */
				List<Contacto> listaContactos = contactoFacade.buscarPorPersona(usuario.getPersona());
				for(Contacto contacto : listaContactos){
					if(contacto.getFormasContacto().getId() == FormasContacto.EMAIL){
						email = contacto.getValor();
					}
					
					if(contacto.getFormasContacto().getId() == FormasContacto.TELEFONO){
						numeroTelefonico = contacto.getValor();
					}
					
					if(contacto.getFormasContacto().getId() == FormasContacto.CELULAR){
						celular = contacto.getValor();
					}
				}				
			} else {
				Organizacion organizacion = organizacionFacade.buscarPorPersona(usuario.getPersona());
				proponente = organizacion.getNombre();
				ubicacionesGeografica = ubicacionGeograficaFacade.ubicacionCompleta(organizacion
						.getIdUbicacionGeografica());
				
				/**
				 * CF: aumento para visualización de contactos 
				 */
				List<Contacto> listaContactos = contactoFacade.buscarPorOrganizacion(organizacion);
				
				for(Contacto contacto : listaContactos){
					if(contacto.getFormasContacto().getId() == FormasContacto.EMAIL){
						email = contacto.getValor();
					}
					
					if(contacto.getFormasContacto().getId() == FormasContacto.TELEFONO){
						numeroTelefonico = contacto.getValor();
					}
					
					if(contacto.getFormasContacto().getId() == FormasContacto.CELULAR){
						celular = contacto.getValor();
					}
				}
				
			}
			ubicacionProponente = new ArrayList<UbicacionesGeografica>();
			ubicacionProponente.add(ubicacionesGeografica);
			
		}

	}

	@SuppressWarnings("hiding")
	public <T> T getFachada(Class<T> type) {
		return BeanLocator.getInstance(type);
	}

	public boolean isDataResolved() {
		return tramite != null;
	}
}
