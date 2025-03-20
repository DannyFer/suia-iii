package ec.gob.ambiente.suia.bandeja.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import ec.gob.ambiente.suia.domain.EnvioNotificacionesMail;
import ec.gob.ambiente.suia.notificaciones.facade.EnvioNotificacionesMailFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.JsfUtil;

public class LazyEnvioNotificationesRevisadasDataModel extends LazyDataModel<EnvioNotificacionesMail> {
	private static final long serialVersionUID = -3479713500589851595L;

	private EnvioNotificacionesMailFacade envioNotificacionesMailFacade = BeanLocator.getInstance(EnvioNotificacionesMailFacade.class);

    @Override
    public List<EnvioNotificacionesMail> load(int first, int pageSize, String sortField,
                                     SortOrder sortOrder, Map<String, Object> filters) {
        List<EnvioNotificacionesMail> listaNotificaciones = new ArrayList<EnvioNotificacionesMail>();
        String codigo = "";
        if (filters != null) {
            if (filters.containsKey("codigoProyecto")) {
                codigo = (String) filters.get("codigoProyecto");
            }
        }
        Integer listado_interno = 0;
   		listado_interno = envioNotificacionesMailFacade.contarRegistros(JsfUtil.getLoggedUser(), true);
    	Integer total = listado_interno;
    	listaNotificaciones = envioNotificacionesMailFacade.buscarNotificacionesEnviadas(first, pageSize,JsfUtil.getLoggedUser(),codigo, true);
    	this.setRowCount(total);
        return listaNotificaciones;
    }
    
	public LazyEnvioNotificationesRevisadasDataModel(String nombreUsuario) {

	}
}