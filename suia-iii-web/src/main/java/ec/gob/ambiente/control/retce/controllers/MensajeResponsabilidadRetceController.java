package ec.gob.ambiente.control.retce.controllers;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.login.bean.LoginBean;

@ManagedBean
@ViewScoped
public class MensajeResponsabilidadRetceController implements Serializable {

	private static final long serialVersionUID = 1L;
	

	@EJB
	private OrganizacionFacade organizacionFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	private String representanteLegal, cedulaRepresentanteLegal, tipoDocumentoRepresentante; 
	
    @PostConstruct
	public void init() {

	}
    
	public String mensajeResponsabilidadRetce(String usuariocreacion){
		try {
			String razonSocial = "", mensaje = "";
			if(usuariocreacion.length() == 13){
				Organizacion organizacion = organizacionFacade.buscarPorRuc(usuariocreacion);				
				if(organizacion != null && organizacion.getNombre() != null){
					razonSocial = organizacion.getNombre();
					representanteLegal = organizacion.getPersona().getNombre();
					cedulaRepresentanteLegal = organizacion.getPersona().getPin();
				}else{
					razonSocial = loginBean.getUsuario().getPersona().getNombre();
					representanteLegal = loginBean.getUsuario().getPersona().getNombre();
					cedulaRepresentanteLegal =  loginBean.getUsuario().getNombre();
					}
					tipoDocumentoRepresentante = "con cédula de identidad";
			}else{
				Persona persona = loginBean.getUsuario().getPersona();
				if(persona != null && persona.getNombre() != null){
					razonSocial = persona.getNombre();
					representanteLegal = persona.getNombre();
					cedulaRepresentanteLegal =  persona.getPin();
					tipoDocumentoRepresentante = "con cédula de identidad";
				}
			}
			mensaje="Yo, "+representanteLegal+" "+tipoDocumentoRepresentante+" "+ cedulaRepresentanteLegal+","
					+ "	declaro que toda la información ingresada al sistema corresponde a la realidad y que reconozco la responsabilidad que genera"
					+ "	el ocultamiento de información o que esta sea incorrecta o falsa, para el cumplimiento de los mecanismos de control y"
					+ "	seguimiento que induzca al cometimiento de errores a la Autoridad Ambiental Competente, en atención a lo que establece el"
					+ " numeral 5 del artículo 318 del Código Orgánico del Ambiente que señala: \"Infracciones muy graves. [....] 5. El suministro de información"
					+ " incorrecta o que no corresponda a la verdad de los hechos o las personas en la obtención de una autorización administrativa"
					+ " o para el cumplimiento de los mecanismos de control y seguimiento que induzca al cometimiento de errores a la Autoridad Ambiental"
					+ " Competente. Para esta infracción se aplicará, según corresponda, la sanción contenida en el numeral 5 del artículo 320 [...]\".";
			return mensaje;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
