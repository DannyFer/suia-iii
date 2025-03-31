package ec.gob.ambiente.rcoa.digitalizacion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.facade.ActualizacionAutorizacionesAdministrativasFacade;
import ec.gob.ambiente.rcoa.digitalizacion.model.AutorizacionAdministrativaAmbiental;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.JsfUtil;


public class LazyProyectosDigitalizacionActualizacionDataModel extends LazyDataModel<AutorizacionAdministrativaAmbiental>  {
	
	private static final long serialVersionUID = 1L;
	private ActualizacionAutorizacionesAdministrativasFacade actualizacionAutorizacionesAdministrativasFacade = BeanLocator.getInstance(ActualizacionAutorizacionesAdministrativasFacade.class);

	private boolean esTecnico;
	private String areaTecnicoCalidad;

    @Override
    public List<AutorizacionAdministrativaAmbiental> load(int first, int pageSize, String sortField,
                                     SortOrder sortOrder, Map<String, Object> filters) {
        List<AutorizacionAdministrativaAmbiental> listaProyectos = new ArrayList<AutorizacionAdministrativaAmbiental>();
        String codigo = "", nombreProyecto="",  sector="", tipoProyecto="";

		String identificacion = null;
		if(!esTecnico){
			identificacion = JsfUtil.getLoggedUser().getNombre();
		}
		
        if (filters != null) {
            if (filters.containsKey("codigoProyecto")) {
                codigo = (String) filters.get("codigoProyecto");
            }
            if (filters.containsKey("nombreProyecto")) {
            	nombreProyecto = (String) filters.get("nombreProyecto");
            }
            if (filters.containsKey("tipoSector.nombre")) {
            	sector = (String) filters.get("tipoSector.nombre");
            }
            if (filters.containsKey("autorizacionAdministrativaAmbiental")) {
            	tipoProyecto = (String) filters.get("autorizacionAdministrativaAmbiental");
            }
            Integer listado_interno = 0;
       		listado_interno = actualizacionAutorizacionesAdministrativasFacade.contarRegistros(identificacion, codigo, nombreProyecto, sector, tipoProyecto, areaTecnicoCalidad);
        	Integer total = listado_interno;
        	listaProyectos = actualizacionAutorizacionesAdministrativasFacade.getProyectosDigitalizadosLazy(first, pageSize, identificacion, codigo, nombreProyecto, sector, tipoProyecto, areaTecnicoCalidad);

        	this.setRowCount(total);
        }
        return listaProyectos;
    }

    public LazyProyectosDigitalizacionActualizacionDataModel(boolean tecnico, String areasId) {
    	esTecnico = tecnico;
    	areaTecnicoCalidad = areasId;
	}
    
    @Override
	public Object getRowKey(AutorizacionAdministrativaAmbiental autorizacion) {
		return autorizacion.getId();
	}
}