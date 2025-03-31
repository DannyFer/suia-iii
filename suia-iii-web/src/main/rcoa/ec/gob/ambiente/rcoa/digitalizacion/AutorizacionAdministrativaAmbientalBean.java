package ec.gob.ambiente.rcoa.digitalizacion;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.model.AutorizacionAdministrativa;
import ec.gob.ambiente.rcoa.digitalizacion.model.AutorizacionAdministrativaAmbiental;
import ec.gob.ambiente.rcoa.dto.EntityLicenciaFisica;
import ec.gob.ambiente.suia.dto.EntityFichaCompletaRgd;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@SessionScoped
public class AutorizacionAdministrativaAmbientalBean {
	
	@Getter
	@Setter
	private List<AutorizacionAdministrativa> listaProyectosSeleccionados;
	
	@Getter
	@Setter
	private AutorizacionAdministrativaAmbiental autorizacionAdministrativaPrincipal;
	
	@Getter
	@Setter
	private Boolean esRegistroNuevo;
	
	@Getter
	@Setter
	private Boolean iniciarRGD;
	
	@Getter
	@Setter
	private EntityFichaCompletaRgd proyectoSeleccionado;
	
	@Getter
	@Setter
	private AutorizacionAdministrativa autorizacionAdministrativaSeleccionada;
	
	@Getter
	@Setter
	private EntityLicenciaFisica autorizacionFisicaSeleccionada;
	
	@Getter
	@Setter
	private AutorizacionAdministrativa autorizacionAdministrativaLista;

}
