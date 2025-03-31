package ec.gob.ambiente.rcoa.emisionLicenciaAmbiental;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.facade.ExpedienteBPCFacade;
import ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.model.ExpedienteBPC;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class PreviewFirmaResolucionController extends DocumentoReporteResolucionMemoController implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@EJB
	private ExpedienteBPCFacade expedienteBPCFacade;
	
	@Setter
    @Getter
	private String urlFormulario;
	
	@Setter
    @Getter
	private Boolean resolucionFisica = false;
	
	@Getter
	@Setter
	private ExpedienteBPC expedienteBPC = new ExpedienteBPC();
	
	@PostConstruct
	public void init() {
		try {
			
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId());
			idProyecto = Integer.valueOf((String)variables.get(Constantes.ID_PROYECTO));

			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
			
			expedienteBPC = new ExpedienteBPC();
			expedienteBPC = expedienteBPCFacade.getByProyectoLicenciaCoa(proyectoLicenciaCoa.getId());
//			if ((expedienteBPC != null) && (expedienteBPC.getId() != null) && (expedienteBPC.getTieneResolucionFisica()))
//			{
//				resolucionFisica = true;
//			}
//			else
//			{
//				resolucionFisica = false;
//			}
//			
//			if (!resolucionFisica)
//			{
				Area areaTramite = proyectoLicenciaCoa.getAreaResponsable();
				
				if(areaTramite.getTipoArea().getSiglas().equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_PC)
						|| areaTramite.getTipoArea().getSiglas().equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_EA)) {
					urlFormulario = "/pages/rcoa/emisionLicenciaAmbiental/firmaResolucionLicencia.xhtml";
				} else {
					urlFormulario = "/pages/rcoa/emisionLicenciaAmbiental/firmaZonalResolucionLicencia.xhtml";
				}				
//			}
//			else
//			{
//				urlFormulario = "/pages/rcoa/emisionLicenciaAmbiental/firmaZonalResolucionLicencia.xhtml";
//			}

			
		} catch (Exception e) {
			JsfUtil.addMessageError("Error visualizar resolucion / pronunciamiento");
			e.printStackTrace();
		}
	}
}
