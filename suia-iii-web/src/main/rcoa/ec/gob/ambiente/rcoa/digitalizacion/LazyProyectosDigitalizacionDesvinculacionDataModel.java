package ec.gob.ambiente.rcoa.digitalizacion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.facade.DesvinculacionAutorizacionesAdministrativasFacade;
import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.model.AutorizacionAdministrativa;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.JsfUtil;


public class LazyProyectosDigitalizacionDesvinculacionDataModel extends LazyDataModel<AutorizacionAdministrativa>  {
	
	private static final long serialVersionUID = 1L;
	private DesvinculacionAutorizacionesAdministrativasFacade desvinculacionAutorizacionesAdministrativasFacade = BeanLocator.getInstance(DesvinculacionAutorizacionesAdministrativasFacade.class);

	private boolean esTecnico;
	private String areaTecnicoCalidad;

    @Override
    public List<AutorizacionAdministrativa> load(int first, int pageSize, String sortField,
                                     SortOrder sortOrder, Map<String, Object> filters) {
        List<AutorizacionAdministrativa> listaProyectos = new ArrayList<AutorizacionAdministrativa>();
        String codigo = "", resolucion="", nombreProyecto="", fecha="", sector="", tipoProyecto="";

		String identificacion = null;
		if(!esTecnico){
			identificacion = JsfUtil.getLoggedUser().getNombre();
		}
		
        if (filters != null) {
            if (filters.containsKey("codigo")) {
                codigo = (String) filters.get("codigo");
            }
            if (filters.containsKey("resolucion")) {
            	resolucion = (String) filters.get("resolucion");
            }
            if (filters.containsKey("fechaResolucion")) {
            	fecha = (String) filters.get("fechaResolucion");
            }
            if (filters.containsKey("nombre")) {
            	nombreProyecto = (String) filters.get("nombre");
            }
            if (filters.containsKey("sector")) {
            	sector = (String) filters.get("sector");
            }
            if (filters.containsKey("categoria")) {
            	tipoProyecto = (String) filters.get("categoria");
            }
            Integer listado_interno = 0;
       		listado_interno = desvinculacionAutorizacionesAdministrativasFacade.contarRegistros(identificacion, codigo, resolucion, fecha, nombreProyecto, sector, tipoProyecto, areaTecnicoCalidad);
        	Integer total = listado_interno;
        	listaProyectos = desvinculacionAutorizacionesAdministrativasFacade.getProyectosDigitalizadosLazy(first, pageSize, identificacion, codigo, resolucion, fecha, nombreProyecto, sector, tipoProyecto, areaTecnicoCalidad);

        	this.setRowCount(total);
        }
        return listaProyectos;
    }

    public LazyProyectosDigitalizacionDesvinculacionDataModel(boolean tecnico, String areasId) {
    	esTecnico = tecnico;
    	areaTecnicoCalidad = areasId;
	}
    
    @Override
	public Object getRowKey(AutorizacionAdministrativa autorizacion) {
		return autorizacion.getId();
	}
}