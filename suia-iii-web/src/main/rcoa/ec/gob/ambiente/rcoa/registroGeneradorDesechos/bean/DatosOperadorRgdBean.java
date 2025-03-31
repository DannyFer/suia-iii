package ec.gob.ambiente.rcoa.registroGeneradorDesechos.bean;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.rcoa.dto.EntityDatosOperador;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;

@ManagedBean
@ViewScoped
public class DatosOperadorRgdBean extends RegistroGeneradorBaseRcoaBean {
	
	private static final long serialVersionUID = -5624189408694387300L;
    private static final String PERSONA_NATURAL = "N";
    private static final String PERSONA_JURIDICA = "J";

	@EJB
	private ContactoFacade contactoFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;
    
    @Getter
    @Setter
    private EntityDatosOperador datosOperador;
    
    @Getter
    @Setter
    private String titulo="Operador";

    public void buscarDatosOperador(Usuario operador) {       
    	datosOperador = new EntityDatosOperador();
    	datosOperador.setTipoPersona(PERSONA_NATURAL);
    	UbicacionesGeografica ubicacionesGeografica = new UbicacionesGeografica();
		try{
			if(operador.getNombre().length() == 10){
				List<Contacto> contacto = contactoFacade.buscarPorPersona(operador.getPersona());
				for (Contacto con : contacto) {
	                if (con.getFormasContacto().getId() == 5 && con.getEstado().equals(true)) {
	                	datosOperador.setCorreo(con.getValor());
	                	continue;
	                }
	                if(con.getFormasContacto().getId()==FormasContacto.TELEFONO  && con.getEstado().equals(true)){
	                	datosOperador.setTelefono(con.getValor());
	                	continue;
					}
					if(con.getFormasContacto().getId()== FormasContacto.CELULAR && con.getEstado().equals(true)){
						datosOperador.setCelular(con.getValor());
	                	continue;
					}
	            }
				datosOperador.setRepresentanteLegal(operador.getPersona().getNombre());
				datosOperador.setNombre(operador.getPersona().getNombre());
				ubicacionesGeografica = ubicacionGeograficaFacade.ubicacionCompleta(operador.getPersona().getIdUbicacionGeografica());
			}else{
		    	datosOperador.setTipoPersona(PERSONA_JURIDICA);
				Organizacion organizacion = organizacionFacade.buscarPorRuc(operador.getNombre());
				if(organizacion != null){
					List<Contacto> contacto = contactoFacade.buscarPorOrganizacion(organizacion);
					for (Contacto con : contacto) {
		                if (con.getFormasContacto().getId() == 5 && con.getEstado().equals(true)) {
		                	datosOperador.setCorreo(con.getValor());
		                	continue;
		                }
		                if(con.getFormasContacto().getId()==FormasContacto.TELEFONO  && con.getEstado().equals(true)){
		                	datosOperador.setTelefono(con.getValor());
		                	continue;
						}
						if(con.getFormasContacto().getId()== FormasContacto.CELULAR && con.getEstado().equals(true)){
							datosOperador.setCelular(con.getValor());
		                	continue;
						}
		            }
					datosOperador.setRepresentanteLegal(organizacion.getPersona().getNombre());
					datosOperador.setNombre(organizacion.getNombre());

					ubicacionesGeografica = ubicacionGeograficaFacade.ubicacionCompleta(organizacion.getIdUbicacionGeografica());
				}else{
					List<Contacto> contacto = contactoFacade.buscarPorPersona(operador.getPersona());
					for (Contacto con : contacto) {
		                if (con.getFormasContacto().getId() == 5 && con.getEstado().equals(true)) {
		                	datosOperador.setCorreo(con.getValor());
		                	continue;
		                }
		                if(con.getFormasContacto().getId()==FormasContacto.TELEFONO  && con.getEstado().equals(true)){
		                	datosOperador.setTelefono(con.getValor());
		                	continue;
						}
						if(con.getFormasContacto().getId()== FormasContacto.CELULAR && con.getEstado().equals(true)){
							datosOperador.setCelular(con.getValor());
		                	continue;
						}
		            }
					datosOperador.setRepresentanteLegal(operador.getPersona().getNombre());
					datosOperador.setNombre(operador.getPersona().getNombre());
					ubicacionesGeografica = ubicacionGeograficaFacade.ubicacionCompleta(operador.getPersona().getIdUbicacionGeografica());
				}
			}
			datosOperador.setListaUbicacionGeografica(new ArrayList<UbicacionesGeografica>());
			if (ubicacionesGeografica != null && ubicacionesGeografica.getId() != null) {
				datosOperador.getListaUbicacionGeografica().add(ubicacionesGeografica);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
    }

 
}
