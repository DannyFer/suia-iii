package ec.gob.ambiente.rcoa.digitalizacion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.facade.AutorizacionesAdministrativasFacade;
import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.model.AutorizacionAdministrativa;
import ec.gob.ambiente.rcoa.digitalizacion.facade.AutorizacionAdministrativaAmbientalFacade;
import ec.gob.ambiente.rcoa.digitalizacion.model.AutorizacionAdministrativaAmbiental;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.JsfUtil;


public class LazyProyectosDigitalizacionDataModel extends LazyDataModel<AutorizacionAdministrativa>  {
	
	private static final long serialVersionUID = 1L;
	private AutorizacionesAdministrativasFacade autorizacionesAdministrativasFacade = BeanLocator.getInstance(AutorizacionesAdministrativasFacade.class);
	private AutorizacionAdministrativaAmbientalFacade autorizacionAdministrativaAmbientalFacade = BeanLocator.getInstance(AutorizacionAdministrativaAmbientalFacade.class);

	private Integer idPrincipal;
	private boolean esTecnico;

    @Override
    public List<AutorizacionAdministrativa> load(int first, int pageSize, String sortField,
                                     SortOrder sortOrder, Map<String, Object> filters) {
        List<AutorizacionAdministrativa> listaNotificaciones = new ArrayList<AutorizacionAdministrativa>();
        String codigo = "", nombreProyecto="", fecha="", sector="", tipoProyecto="";

		String identificacion = null;
		if(!esTecnico){
			identificacion = JsfUtil.getLoggedUser().getNombre();
		}
		// si es para asociar un proyecto se busca en base a la identificacion del operador del proyecto principal
		if(idPrincipal != null){
			AutorizacionAdministrativaAmbiental autorizacionPrincipal = autorizacionAdministrativaAmbientalFacade.obtenerAAAPorId(idPrincipal);
			if(autorizacionPrincipal != null && autorizacionPrincipal.getId() != null){
				identificacion = autorizacionPrincipal.getUsuario().getNombre();
			}
		}
		
        if (filters != null) {
            if (filters.containsKey("codigo")) {
                codigo = (String) filters.get("codigo");
            }
            if (filters.containsKey("nombre")) {
            	nombreProyecto = (String) filters.get("nombre");
            }
            if (filters.containsKey("fecha")) {
            	fecha = (String) filters.get("fecha");
            }
            if (filters.containsKey("sector")) {
            	sector = (String) filters.get("sector");
            }
            if (filters.containsKey("categoria")) {
            	tipoProyecto = (String) filters.get("categoria");
            }
        }

        Integer totalSector = autorizacionesAdministrativasFacade.contarProyectosSectorSubSector(identificacion, codigo, nombreProyecto, sector, tipoProyecto, fecha, idPrincipal);
        Integer totalCuatroCat = autorizacionesAdministrativasFacade.contarProyectosCuatroCategorias(identificacion, codigo, nombreProyecto, sector, tipoProyecto, fecha, idPrincipal);
        Integer totalSuiaiiiAntes2018 = autorizacionesAdministrativasFacade.contarProyectosSuiaiiiAntes2018(identificacion, codigo, nombreProyecto, sector, tipoProyecto, fecha, idPrincipal);
        Integer totalSuiaiii2018Adelante = autorizacionesAdministrativasFacade.contarProyectosSuiaiii2018(identificacion, codigo, nombreProyecto, sector, tipoProyecto, fecha, idPrincipal);
        Integer totalRcoa = autorizacionesAdministrativasFacade.contarProyectosRcoa(identificacion, codigo, nombreProyecto, sector, tipoProyecto, fecha, idPrincipal);
        Integer totalDigitalizados = autorizacionesAdministrativasFacade.contarProyectosDigitalizados(identificacion, codigo, nombreProyecto, sector, tipoProyecto, fecha, idPrincipal);

        Integer total = totalSector + totalCuatroCat + totalSuiaiiiAntes2018 + totalSuiaiii2018Adelante + totalRcoa + totalDigitalizados;
        Integer valorParcial = totalSector;

		if (totalSector > 0 && first <= totalSector) {
			listaNotificaciones.addAll(autorizacionesAdministrativasFacade.getProyectos4CategoriasLazy(1, first, pageSize, nombreProyecto, "", identificacion, false, codigo, sector, tipoProyecto, fecha, idPrincipal));
		}

		valorParcial += totalCuatroCat;
		if (totalCuatroCat > 0 && listaNotificaciones.size() < pageSize && first <= valorParcial) {
			Integer inicioCuatroCat = (first - totalSector);
			if (inicioCuatroCat < 0) {
				 inicioCuatroCat = 0;
			}

			listaNotificaciones.addAll(autorizacionesAdministrativasFacade.getProyectos4CategoriasLazy(2, inicioCuatroCat, pageSize - listaNotificaciones.size(), nombreProyecto, "", identificacion, false, codigo, sector, tipoProyecto, fecha, idPrincipal));
		}

		valorParcial += totalSuiaiiiAntes2018;
		if (totalSuiaiiiAntes2018 > 0 && listaNotificaciones.size() < pageSize && first <= valorParcial) {
			Integer inicioSuiaiiiAntes2018 = (first - totalCuatroCat - totalSector);
			if (inicioSuiaiiiAntes2018 < 0) {
				inicioSuiaiiiAntes2018 = 0;
			}

			listaNotificaciones.addAll(autorizacionesAdministrativasFacade.getProyectos4CategoriasLazy(3, inicioSuiaiiiAntes2018, pageSize - listaNotificaciones.size(),nombreProyecto, "", identificacion, false, codigo,sector, tipoProyecto, fecha, idPrincipal));
		}
		
		valorParcial += totalSuiaiii2018Adelante;
		if (totalSuiaiii2018Adelante > 0 && listaNotificaciones.size() < pageSize && first <= valorParcial) {
			Integer inicioSuia2018Adelante = (first - totalSuiaiiiAntes2018 - totalCuatroCat - totalSector);
			if (inicioSuia2018Adelante < 0) {
				inicioSuia2018Adelante = 0;
			}

			listaNotificaciones.addAll(autorizacionesAdministrativasFacade.getProyectos4CategoriasLazy(4, inicioSuia2018Adelante, pageSize - listaNotificaciones.size(),nombreProyecto, "", identificacion, false, codigo,sector, tipoProyecto, fecha, idPrincipal));
		}

		valorParcial += totalRcoa;
		if (totalRcoa > 0 && listaNotificaciones.size() < pageSize && first <= valorParcial) {
			Integer inicioRcoa = (first - totalSuiaiii2018Adelante - totalSuiaiiiAntes2018 - totalCuatroCat - totalSector);
			if (inicioRcoa < 0) {
				inicioRcoa = 0;
			}

			listaNotificaciones.addAll(autorizacionesAdministrativasFacade.getProyectos4CategoriasLazy(5, inicioRcoa, pageSize - listaNotificaciones.size(),nombreProyecto, "", identificacion, false, codigo,sector, tipoProyecto, fecha, idPrincipal));
		}
		
		valorParcial += totalDigitalizados;
		if (totalDigitalizados > 0 && listaNotificaciones.size() < pageSize && first <= valorParcial) {
			Integer inicioDigitalizados = (first - totalRcoa - totalSuiaiii2018Adelante - totalSuiaiiiAntes2018 - totalCuatroCat - totalSector);
			if (inicioDigitalizados < 0) {
				inicioDigitalizados = 0;
			}

			listaNotificaciones.addAll(autorizacionesAdministrativasFacade.getProyectos4CategoriasLazy(6, inicioDigitalizados, pageSize - listaNotificaciones.size(),nombreProyecto, "", identificacion, false, codigo,sector, tipoProyecto, fecha, idPrincipal));
		}
		
    	this.setRowCount(total);
        return listaNotificaciones;
    }

    public LazyProyectosDigitalizacionDataModel(Integer idProyectoprincipal, boolean tecnico) {
    	idPrincipal = idProyectoprincipal;
    	esTecnico = tecnico;
	}
    
    @Override
	public Object getRowKey(AutorizacionAdministrativa autorizacion) {
		return autorizacion.getId();
	}
}
