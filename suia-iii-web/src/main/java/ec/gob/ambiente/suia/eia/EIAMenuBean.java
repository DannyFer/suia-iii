package ec.gob.ambiente.suia.eia;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.TipoSector;

@ManagedBean
@ViewScoped
public class EIAMenuBean {
	
	@Getter
	@Setter
	private boolean proyectoHidrocarburos;
		
	@Getter
	private boolean esMineriaNoMetalicos;

	@Getter
	@Setter
	private EstudioImpactoAmbiental estudio;
	
	@Getter
	private boolean esExante;

	@PostConstruct
	public void init(){
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        estudio=(EstudioImpactoAmbiental) request.getSession().getAttribute("estudio");
		
		if(estudio.getProyectoLicenciamientoAmbiental().getTipoSector().getId().equals(TipoSector.TIPO_SECTOR_HIDROCARBUROS)){
			proyectoHidrocarburos = true;

		}		
		esMineriaNoMetalicos=estudio.getProyectoLicenciamientoAmbiental().getCatalogoCategoria().getCodigo().equals("21.02.03.02") && estudio.getResumenEjecutivo()==null?true:false;

		if(estudio.getProyectoLicenciamientoAmbiental().getTipoEstudio().getNombre().equals("Ex-ante"))
			esExante = true;
		else
			esExante = false;
	}
}
