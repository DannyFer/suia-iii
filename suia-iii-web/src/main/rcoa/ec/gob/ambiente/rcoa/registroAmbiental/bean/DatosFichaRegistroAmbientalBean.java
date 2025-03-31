package ec.gob.ambiente.rcoa.registroAmbiental.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import ec.gob.ambiente.rcoa.facade.FasesRegistroAmbientalCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.RegistroAmbientalCoaFacade;
import ec.gob.ambiente.rcoa.model.FasesRegistroAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.RegistroAmbientalRcoa;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@SessionScoped
public class DatosFichaRegistroAmbientalBean implements Serializable {
	private static final long serialVersionUID = -7945028814622611935L;

	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private RegistroAmbientalCoaFacade registroAmbientalCoaFacade;
	@EJB
	private FasesRegistroAmbientalCoaFacade fasesRegistroAmbientalCoaFacade;
	
	@Getter
	private List<SelectItem> listaNormativa;

	@Getter
	@Setter
	private boolean validarMarcoLegal, existeConstruccion, seRevisoFicha;
	
	@Getter
	@Setter
	private RegistroAmbientalRcoa registroAmbientalRcoa;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
    @Setter
    private List<FasesRegistroAmbiental> listaFasesRegistroAmbiental;
	
	@Getter
	@Setter
	private String tramite;

	public void cargarDatosregistroambiental(){
		seRevisoFicha=false;
		proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
		registroAmbientalRcoa = registroAmbientalCoaFacade.obtenerRegistroAmbientalPorProyecto(proyectoLicenciaCoa);
		if (registroAmbientalRcoa == null){
			registroAmbientalRcoa = new RegistroAmbientalRcoa();
		}else{
			validarMarcoLegal = registroAmbientalRcoa.isAceptacion();
		}
		listaFasesRegistroAmbiental = fasesRegistroAmbientalCoaFacade.obtenerfasesPorRegistroAmbiental(registroAmbientalRcoa);
		existeConstruccion = false;
		// verifico si escogio la fase de Construcción, Rehabilitación y/o Ampliación 
		for (FasesRegistroAmbiental objFases : listaFasesRegistroAmbiental) {
			if(objFases.getFasesCoa().getId().toString().equals("1")){
				existeConstruccion = true;
			}
		}
	}
	
	public void caragrNormativa() {
		listaNormativa = new ArrayList<SelectItem>();
		listaNormativa.add(new SelectItem("1", "Constitución de la República del Ecuador"));
		listaNormativa.add(new SelectItem("2", "Código Orgánico del Ambiente"));
		listaNormativa.add(new SelectItem("3", "Reglamento al Código Orgánico del Ambiente"));
	}
}