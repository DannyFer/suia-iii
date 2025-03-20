package ec.gob.ambiente.proyectos.datamodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import ec.gob.ambiente.suia.administracion.facade.RolFacade;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.dto.ProyectoCustom;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.Proyecto4CategoriasFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;

public class LazyProyectPCDataModel extends LazyDataModel<ProyectoCustom> {

	private static final long serialVersionUID = -3479713500589851595L;
	
	private Usuario usuario;
	private String inecProvincia;
	private boolean rolAdmin;   
	private boolean rolDP;
	private boolean rolEnte;
	private boolean rolPatrimonio;
	private boolean rolViabilidadNofavorable;	
	
	private String sectorUsuario;
    
    public LazyProyectPCDataModel(Usuario usuario, String sector) {
    	
        this.usuario = usuario;
        if(usuario.getListaAreaUsuario().get(0).getArea().getUbicacionesGeografica() != null)
        inecProvincia=usuario.getListaAreaUsuario().get(0).getArea().getUbicacionesGeografica().getCodificacionInec().substring(0,2);    
        rolAdmin = true;
        
        sectorUsuario = sector;
    }

	@Override
	public Object getRowKey(ProyectoCustom proyectoCustom) {
		return proyectoCustom.getId();
	}
	
	ProyectoLicenciamientoAmbientalFacade proyectoInternoFacade = BeanLocator.getInstance(ProyectoLicenciamientoAmbientalFacade.class);
    Proyecto4CategoriasFacade proyecto4CategoriasFacade = BeanLocator.getInstance(Proyecto4CategoriasFacade.class);

    @Override
    public List<ProyectoCustom> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
    	   	
    	Integer conteoInterno = 0;
    	Integer conteoExterno= 0;
    	Integer conteoRcoa= 0;
    	Integer conteoTotal= 0;
        List<ProyectoCustom> proyectoCustoms = new ArrayList<ProyectoCustom>();
        List<ProyectoCustom> proyectosCoa = new ArrayList<ProyectoCustom>();

        String codigo = "";
        String nombre = "";
        String sector = "";
        String responsableSiglas="";
        String categoriaNombrePublico="";
        
        sector = sectorUsuario;
        
        if (filters != null) {

            if (filters.containsKey("codigo")) {
                codigo = (String) filters.get("codigo");
            }
            if (filters.containsKey("nombre")) {
                nombre = (String) filters.get("nombre");
            }
//            if (filters.containsKey("sector")) {
//            	sector = (String) filters.get("sector");
//            }
            if (filters.containsKey("responsableSiglas")) {
            	responsableSiglas = (String) filters.get("responsableSiglas");
            }
            if (filters.containsKey("categoriaNombrePublico")) {
            	categoriaNombrePublico = (String) filters.get("categoriaNombrePublico");
            }
        }        
        
        conteoInterno = proyectoInternoFacade.contarProyectosLicenciamientoAmbientalPC(usuario, rolAdmin, nombre, codigo, sector, responsableSiglas,categoriaNombrePublico);
        if(responsableSiglas.equals("")||responsableSiglas.contains("N/D"))
        {
        	conteoExterno=proyecto4CategoriasFacade.contarProyectosPC(codigo, nombre, sector, categoriaNombrePublico);
        }
        
        proyectosCoa = proyectoInternoFacade.listarProyectosCoaPC(nombre, codigo, sector, responsableSiglas, categoriaNombrePublico, usuario, rolAdmin, rolEnte, rolPatrimonio, false, 0, 0);
        conteoRcoa = proyectosCoa.size();
        
        
    	conteoTotal = conteoExterno + conteoInterno + conteoRcoa;
    	this.setRowCount(conteoTotal);
    	
    	//Se cargan primero los proyectos externos luego los internos
    	//Si el inicio esta en el listado externo, traer solo proyectos externos
    	if(conteoExterno > 0 && first <= conteoExterno)
    	{       		
    		proyectoCustoms = proyecto4CategoriasFacade.listarProyectosPC(codigo, nombre, sector, categoriaNombrePublico, pageSize, first);            
    	}
    	
    	//si la pagina no esta completa, completar con proyectos internos
    	//si ya no hay proyectos externos, traer solo proyectos internos
    	if(conteoInterno > 0 && proyectoCustoms.size() < pageSize)
    	{	
    		List<ProyectoCustom> proyectoCustomsInternos=new ArrayList<ProyectoCustom>();
        	Integer	pageSizeInterno=pageSize - proyectoCustoms.size();
        	Integer firstInterno = first - conteoExterno;
        	if(firstInterno<0)
        		firstInterno=0;
        	        	
        	
        	proyectoCustomsInternos = proyectoInternoFacade.listarProyectosLicenciamientoAmbientalPaginadoPC(usuario, rolAdmin, firstInterno, pageSizeInterno, nombre, codigo, sector, responsableSiglas,categoriaNombrePublico);        		
                    	
    		//Si el proyecto se encuentra en las dos listas, eliminar del listado Interno
    		if(proyectoCustoms.size() > 0 && proyectoCustomsInternos.size() > 0){
    			List<ProyectoCustom> proyectoCustomsRemove=new ArrayList<ProyectoCustom>();
    			for (ProyectoCustom proyectoInterno : proyectoCustomsInternos) {
        			for (ProyectoCustom proyectoExterno : proyectoCustoms) {
        				if(proyectoExterno.getCodigo().compareTo(proyectoInterno.getCodigo())==0)
        					proyectoCustomsRemove.add(proyectoInterno);
        			}
    			}
    			for (ProyectoCustom proyecto : proyectoCustomsRemove) {
    				proyectoCustomsInternos.remove(proyecto);
    			}
    		}    		
    		proyectoCustoms.addAll(proyectoCustomsInternos);
    	}
    	
    	if(conteoRcoa > 0 && proyectoCustoms.size() < pageSize) {
        	Integer	pageSizeInterno=pageSize - proyectoCustoms.size();
        	Integer firstInterno = first - conteoExterno - conteoInterno;
        	if(firstInterno<0)
        		firstInterno=0;
        	
        	proyectosCoa = proyectoInternoFacade.listarProyectosCoaPC(nombre, codigo, sector, responsableSiglas, categoriaNombrePublico, usuario, rolAdmin, rolEnte, rolPatrimonio, true, pageSizeInterno, firstInterno);
        	
    		proyectoCustoms.addAll(proyectosCoa);
    	}
    	
    	return proyectoCustoms;
    	
    }    
    
}
