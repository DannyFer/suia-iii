package ec.gob.ambiente.suia.reportes.pentaho;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ec.gob.ambiente.suia.domain.RolUsuario;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class ReportesPentahoController {

	@Getter
	@Setter
	private String linkReportePentahoRegistroLicencia="";

	@Getter
	@Setter
	private String linkReportePentahoCertificado="";

	@Getter
	@Setter
	private String linkReportePentahoProyectoArchivado="";

	@Getter
	@Setter
	private String linkReportePentahoCargaTrabajo="";

	//Cris F: Aumento de variable para reporte de procesos aceptados y rechazados
	@Getter
	@Setter String linkReportePentahoProcesosAceptadosRechazados = "";

	@PostConstruct
	private void init(){
		verReportes();
	}

	private void verReportes(){
		Usuario usuario= new Usuario();
		usuario=JsfUtil.getLoggedUser();
	    String area="";
	    area=usuario.getListaAreaUsuario().get(0).getArea().getId().toString();
		if(usuario.getListaAreaUsuario().get(0).getArea().getId()==257){
			area="00000";
		}
		linkReportePentahoRegistroLicencia=Constantes.getReportePentahoRegistroLicencia()+area;
		linkReportePentahoCertificado=Constantes.getReportePentahoCertificado()+area;
		linkReportePentahoProyectoArchivado=Constantes.getReportePentahoProyectosArchivados()+area;
		/**
		 * Para mostrar el reporte de control para ver todo cuando el usuario tiene el rol REPORTE CARGA CONTROL
		 * Byron Burbano
		 * 2019-09-25
		 */
		boolean mostrarTodos= false;
		for (RolUsuario objRolUsuario : usuario.getRolUsuarios()) {
			if (objRolUsuario.getRol().getNombre().toUpperCase().contains("CARGA CONTROL")){
				mostrarTodos= true;
				break;
			}
		}
		if (mostrarTodos) {
			linkReportePentahoCargaTrabajo=Constantes.getReportePentahoCargaTrabajoAdmin()+"&txt_usuario=";	
		}else{
			linkReportePentahoCargaTrabajo=Constantes.getReportePentahoCargaTrabajo()+"&txt_usuario="+usuario.getNombre();
		}
		if(usuario.getListaAreaUsuario().get(0).getArea().getId()==257 || usuario.getListaAreaUsuario().get(0).getArea().getTipoArea().getId()==5){
			linkReportePentahoRegistroLicencia=Constantes.getReportePentahoRegistroLicenciaPlantaCentral();
			linkReportePentahoCertificado=Constantes.getReportePentahoCertificadoPlantaCentral();
			linkReportePentahoProyectoArchivado=Constantes.getReportePentahoProyectosArchivadosPlantaCentral();
		}else if (usuario.getListaAreaUsuario().get(0).getArea().getTipoArea().getId()==3) {
			linkReportePentahoRegistroLicencia=Constantes.getReportePentahoRegistroLicenciaEnteAcreditado()+area;
			linkReportePentahoCertificado=Constantes.getReportePentahoCertificadoEnteAcreditado()+area;
			linkReportePentahoProyectoArchivado=Constantes.getReportePentahoProyectosArchivadosEntesAcreditados()+area;
		}
		linkReportePentahoProcesosAceptadosRechazados = Constantes.getReportePentahoProcesosRechazadosAceptados();
	} 
}
