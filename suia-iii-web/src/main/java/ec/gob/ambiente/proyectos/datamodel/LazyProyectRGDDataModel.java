package ec.gob.ambiente.proyectos.datamodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import ec.gob.ambiente.suia.domain.GeneradorDesechosPeligrosos;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;

public class LazyProyectRGDDataModel extends LazyDataModel<GeneradorDesechosPeligrosos> {

	private static final long serialVersionUID = -1L;
	
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade = BeanLocator.getInstance(RegistroGeneradorDesechosFacade.class);
	private Usuario usuario;
	boolean admin;
    public LazyProyectRGDDataModel(Usuario usuario) {//AUTORIDAD AMBIENTAL MAE  TÉCNICO ANALISTA MAE
    	admin = (Usuario.isUserInRole(usuario, "admin") || Usuario.isUserInRole(usuario, "CONSULTA MINISTERIO DEL AMBIENTE Y AGUA"));
    	this.usuario = usuario;
    }

	@Override
	public Object getRowKey(GeneradorDesechosPeligrosos proyectoRgd) {
		return proyectoRgd.getId();
	}

    @Override
    public List<GeneradorDesechosPeligrosos> load(int first, int pageSize, String sortField,
                                     SortOrder sortOrder, Map<String, Object> filters) {
        List<GeneradorDesechosPeligrosos> proyectosRGD = new ArrayList<GeneradorDesechosPeligrosos>();
        String codigo = "";
        String proyecto = "";
        String usuarioCreacion = "";
        String responsableSiglas="";
        if (filters != null) {
            if (filters.containsKey("solicitud")) {
                codigo = (String) filters.get("solicitud");
            }
            if (filters.containsKey("codigo")) {
            	proyecto = (String) filters.get("codigo");
            }
            if (filters.containsKey("usuarioCreacion")) {
            	usuarioCreacion = (String) filters.get("usuarioCreacion");
            }
            if (filters.containsKey("areaAbreviacion")) {
            	responsableSiglas = (String) filters.get("areaAbreviacion");
            }
        }
        Integer listado_interno = 0;
   		listado_interno = registroGeneradorDesechosFacade.contarRegistrosGeneradorDesechos(this.usuario,admin);			
    	Integer total = listado_interno;
   		proyectosRGD = registroGeneradorDesechosFacade.buscarRegistrosGeneradorDesechos(first, pageSize,this.usuario,admin,codigo,proyecto,usuarioCreacion,responsableSiglas);
    	this.setRowCount(total);
        return proyectosRGD;
    }
}
