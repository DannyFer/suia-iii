package ec.gob.ambiente.proyectos.datamodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import ec.gob.ambiente.rcoa.facade.ProcesosArchivadosFacade;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.dto.ProyectoCustom;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.Proyecto4CategoriasFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;

public class LazyProjectArchivedDataModel extends LazyDataModel<ProyectoCustom> {

	private static final long serialVersionUID = -3479713500589851595L;

	private Usuario usuario;
	private boolean rolAdmin;   
	private boolean rolDP;
	private boolean rolEnte;
	private boolean rolPatrimonio;
	private boolean esSujetoDeControl;
	private boolean soloOperador;
    
    public LazyProjectArchivedDataModel(Usuario usuario) {
    	
    	rolAdmin = (Usuario.isUserInRole(usuario, "admin") || Usuario.isUserInRole(usuario, "CONSULTA MINISTERIO DEL AMBIENTE"));    	
        rolDP=!rolAdmin  && Usuario.isUserInRole(usuario, "CONSULTA DIRECCION PROVINCIAL");
        rolEnte=!rolDP && Usuario.isUserInRole(usuario, "CONSULTA ENTE ACREDITADO");
        rolPatrimonio=!rolEnte && Usuario.isUserInRole(usuario, "CONSULTA PATRIMONIO NATURAL");
        esSujetoDeControl = Usuario.isUserInRole(usuario, "sujeto de control");
        
        this.usuario = usuario;
    }

	@Override
	public Object getRowKey(ProyectoCustom proyectoCustom) {
		return proyectoCustom.getId();
	}
	
	ProyectoLicenciamientoAmbientalFacade proyectoInternoFacade = BeanLocator.getInstance(ProyectoLicenciamientoAmbientalFacade.class);
    Proyecto4CategoriasFacade proyecto4CategoriasFacade = BeanLocator.getInstance(Proyecto4CategoriasFacade.class);
    ProcesosArchivadosFacade procesosArchivadosFacade = BeanLocator.getInstance(ProcesosArchivadosFacade.class);

    @Override
    public List<ProyectoCustom> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {

    	if(!rolAdmin && !rolDP && !rolEnte && !rolPatrimonio) {
    		if(esSujetoDeControl) {
    			soloOperador = true;
    		} else {
    			return new ArrayList<ProyectoCustom>();
    		}
    	}
    	
    	Integer conteoRcoa= 0;
    	Integer conteoTotal= 0;
    	
    	List<ProyectoCustom> proyectoCustoms = new ArrayList<ProyectoCustom>();
        List<ProyectoCustom> proyectosCoa = new ArrayList<ProyectoCustom>();
        
        proyectosCoa = procesosArchivadosFacade.listarProyectosArchivadosRcoa(filters, sortField, sortOrder.name(), usuario, soloOperador, rolAdmin, rolEnte, rolPatrimonio, false, 0, 0);
        conteoRcoa = proyectosCoa.size();
        
        
    	conteoTotal = conteoRcoa;
    	this.setRowCount(conteoTotal);
    	    	
    	if(conteoRcoa > 0 && proyectoCustoms.size() < pageSize) {
			Integer pageSizeInterno = pageSize - proyectoCustoms.size();
			Integer firstInterno = first;
			if (firstInterno < 0)
				firstInterno = 0;
        	
        	proyectosCoa = procesosArchivadosFacade.listarProyectosArchivadosRcoa(filters, sortField, sortOrder.name(), usuario, soloOperador, rolAdmin, rolEnte, rolPatrimonio, true, pageSizeInterno, firstInterno);
        	
    		proyectoCustoms.addAll(proyectosCoa);
    	}
    	
    	return proyectoCustoms;
    	
    }    
    
}
