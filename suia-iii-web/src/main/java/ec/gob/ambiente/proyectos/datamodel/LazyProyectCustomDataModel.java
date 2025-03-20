package ec.gob.ambiente.proyectos.datamodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import ec.gob.ambiente.rcoa.digitalizacion.facade.AutorizacionAdministrativaAmbientalFacade;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.dto.ProyectoCustom;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.Proyecto4CategoriasFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;

public class LazyProyectCustomDataModel extends LazyDataModel<ProyectoCustom> {

	private static final long serialVersionUID = -3479713500589851595L;
	private AutorizacionAdministrativaAmbientalFacade autorizacionAdministrativaAmbientalFacade = (AutorizacionAdministrativaAmbientalFacade) BeanLocator.getInstance(AutorizacionAdministrativaAmbientalFacade.class);

	private Usuario usuario;
	private String inecProvincia;
	private boolean rolAdmin;   
	private boolean rolDP;
	private boolean rolEnte;
	private boolean rolPatrimonio;
	private boolean rolViabilidadNofavorable;
	private boolean rolDigitalizador;
    
    public LazyProyectCustomDataModel(Usuario usuario) {
    	
    	rolAdmin = (Usuario.isUserInRole(usuario, "admin") || Usuario.isUserInRole(usuario, "CONSULTA MINISTERIO DEL AMBIENTE"));    	
        rolDP=!rolAdmin  && Usuario.isUserInRole(usuario, "CONSULTA DIRECCION PROVINCIAL");
        rolEnte=!rolDP && Usuario.isUserInRole(usuario, "CONSULTA ENTE ACREDITADO");
        rolPatrimonio=!rolEnte && Usuario.isUserInRole(usuario, "CONSULTA PATRIMONIO NATURAL");
        rolViabilidadNofavorable=!rolEnte && Usuario.isUserInRole(usuario, "CONSULTA VIABILIDAD NO FAVORABLE");
        rolDigitalizador=!rolEnte && (Usuario.isUserInRole(usuario, "TÉCNICO RESPONSABLE DE CALIDAD") || Usuario.isUserInRole(usuario, "TÉCNICO DIGITALIZADOR"));
        this.usuario = usuario;
        if(usuario.getListaAreaUsuario().get(0).getArea().getUbicacionesGeografica() != null)
        inecProvincia=usuario.getListaAreaUsuario().get(0).getArea().getUbicacionesGeografica().getCodificacionInec().substring(0,2);
    }

	@Override
	public Object getRowKey(ProyectoCustom proyectoCustom) {
		return proyectoCustom.getId();
	}
	
	ProyectoLicenciamientoAmbientalFacade proyectoInternoFacade = BeanLocator.getInstance(ProyectoLicenciamientoAmbientalFacade.class);
    Proyecto4CategoriasFacade proyecto4CategoriasFacade = BeanLocator.getInstance(Proyecto4CategoriasFacade.class);

    @Override
    public List<ProyectoCustom> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {

    	if(!rolAdmin && !rolDP && !rolEnte && !rolPatrimonio && !rolViabilidadNofavorable && !rolDigitalizador)
    		return new ArrayList<ProyectoCustom>();
    	
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
        
        if (filters != null) {

            if (filters.containsKey("codigo")) {
                codigo = (String) filters.get("codigo");
            }
            if (filters.containsKey("nombre")) {
                nombre = (String) filters.get("nombre");
            }
            if (filters.containsKey("sector")) {
            	sector = (String) filters.get("sector");
            }
            if (filters.containsKey("responsableSiglas")) {
            	responsableSiglas = (String) filters.get("responsableSiglas");
            }
            if (filters.containsKey("categoriaNombrePublico")) {
            	categoriaNombrePublico = (String) filters.get("categoriaNombrePublico");
            }
        }
        
        if(rolViabilidadNofavorable) {
        	//lista solos los proyectos rcoa que tienen viabilidad no favorable
        	proyectosCoa = proyectoInternoFacade.listarProyectosRcoaViablidadNoFavorable(nombre, codigo, sector, responsableSiglas, categoriaNombrePublico, usuario, false, 0, 0);
            conteoRcoa = proyectosCoa.size();
            
            
        	conteoTotal = conteoRcoa;
        	this.setRowCount(conteoTotal);
        	
        	if(conteoRcoa > 0 && proyectoCustoms.size() < pageSize) {
            	Integer	pageSizeInterno=pageSize - proyectoCustoms.size();
            	Integer firstInterno = first;
            	if(firstInterno<0)
            		firstInterno=0;
            	
            	proyectosCoa = proyectoInternoFacade.listarProyectosRcoaViablidadNoFavorable(nombre, codigo, sector, responsableSiglas, categoriaNombrePublico, usuario, true, pageSizeInterno, firstInterno);
            	
        		proyectoCustoms.addAll(proyectosCoa);
        	}
        	
//        	return proyectoCustoms;
        }
        
        if(rolAdmin){
        	conteoInterno = proyectoInternoFacade.contarProyectosLicenciamientoAmbiental(usuario, rolAdmin, nombre, codigo, sector, responsableSiglas,categoriaNombrePublico);
        	if(responsableSiglas.equals("")||responsableSiglas.contains("N/D"))
        	{
        		conteoExterno=proyecto4CategoriasFacade.contarProyectos(codigo, nombre, sector, categoriaNombrePublico);
        	}
        }else{
        	conteoInterno = proyectoInternoFacade.contarProyectosLicenciamientoAmbiental(nombre, codigo, sector, responsableSiglas,categoriaNombrePublico,usuario,rolEnte,rolPatrimonio);

        	if(responsableSiglas.equals("")||responsableSiglas.contains("N/D")){
        		if(rolDP){         		
             		conteoExterno=proyecto4CategoriasFacade.contarProyectos(codigo, nombre, sector, categoriaNombrePublico,inecProvincia);
                }else if(rolEnte){         		
             		conteoExterno=proyecto4CategoriasFacade.contarProyectosEnte(codigo, nombre, sector, categoriaNombrePublico,inecProvincia);
                }else if(rolPatrimonio){         		
             		conteoExterno=proyecto4CategoriasFacade.contarProyectosPatrimonio(codigo, nombre, sector,categoriaNombrePublico);
                }
        	}
        	 
        }     
        
        proyectosCoa = proyectoInternoFacade.listarProyectosCoa(nombre, codigo, sector, responsableSiglas, categoriaNombrePublico, usuario, rolAdmin, rolEnte, rolPatrimonio, false, 0, 0);

        List<ProyectoCustom> proyectosDigi = autorizacionAdministrativaAmbientalFacade.listarProyectosDigitalizados(nombre, codigo, sector, responsableSiglas, categoriaNombrePublico, usuario, rolAdmin, rolEnte, rolPatrimonio, false, 0, 0);
        if(proyectosDigi.size() > 0){
        	proyectosCoa.addAll(proyectosDigi);
        }
        conteoRcoa = proyectosCoa.size();
        
    	conteoTotal = conteoExterno + conteoInterno + conteoRcoa;
    	this.setRowCount(conteoTotal);
    	
    	//Se cargan primero los proyectos externos luego los internos
    	//Si el inicio esta en el listado externo, traer solo proyectos externos
    	if(conteoExterno > 0 && first <= conteoExterno)
    	{    		
    		if(rolAdmin){
    			proyectoCustoms = proyecto4CategoriasFacade.listarProyectos(codigo, nombre, sector, categoriaNombrePublico, pageSize, first);
            }else if(rolDP){
            	proyectoCustoms = proyecto4CategoriasFacade.listarProyectos(codigo, nombre, sector, categoriaNombrePublico,inecProvincia, pageSize, first);
            }else if(rolEnte){
            	proyectoCustoms = proyecto4CategoriasFacade.listarProyectosEnte(codigo, nombre, sector, categoriaNombrePublico,inecProvincia, pageSize, first);
            }else if(rolPatrimonio){
            	proyectoCustoms = proyecto4CategoriasFacade.listarProyectosPatrimonio(codigo, nombre, sector, categoriaNombrePublico, pageSize, first);
            }
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
        	        	
        	if(rolAdmin){
        		proyectoCustomsInternos = proyectoInternoFacade.listarProyectosLicenciamientoAmbientalPaginado(usuario, rolAdmin, firstInterno, pageSizeInterno, nombre, codigo, sector, responsableSiglas,categoriaNombrePublico);        		
            }else {
            	proyectoCustomsInternos = proyectoInternoFacade.listarProyectosLicenciamientoAmbientalPaginado(firstInterno, pageSizeInterno,nombre, codigo, sector, responsableSiglas,categoriaNombrePublico,usuario,rolEnte,rolPatrimonio);
            }
        	
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
        	
        	proyectosCoa = proyectoInternoFacade.listarProyectosCoa(nombre, codigo, sector, responsableSiglas, categoriaNombrePublico, usuario, rolAdmin, rolEnte, rolPatrimonio, true, pageSizeInterno, firstInterno);

            proyectosDigi = autorizacionAdministrativaAmbientalFacade.listarProyectosDigitalizados(nombre, codigo, sector, responsableSiglas, categoriaNombrePublico, usuario, rolAdmin, rolEnte, rolPatrimonio, false, 0, 0);
            // verifico si ya hay un registro para mostrar
            List<ProyectoCustom> proyectosDigEliminar = new ArrayList<ProyectoCustom>();
            if(proyectosDigi != null && proyectosDigi.size() > 0){
            	for (ProyectoCustom itemDig : proyectosDigi) {
            		//para opcion digitalizados
//            		itemDig.setSourceType(ProyectoCustom.SOURCE_TYPE_DIGITALIZACION);
        			itemDig.setCategoria("Digitalizacion");
            		for (ProyectoCustom item : proyectoCustoms) {
            			if(itemDig.getCodigo().equals(item.getCodigo())){
//                			item.setSourceType(ProyectoCustom.SOURCE_TYPE_DIGITALIZACION);
                			item.setCategoria("Digitalizacion");
                			proyectosDigEliminar.add(itemDig);
            			}
            		}
        		}	
            }
            if(proyectosDigEliminar.size()>0){
            	proyectosDigi.removeAll(proyectosDigEliminar);
            }
    		//listado de proyectos digitalizados
    		proyectoCustoms.addAll(proyectosCoa);
    		proyectoCustoms.addAll(proyectosDigi);
    	}
    	
    	return proyectoCustoms;
    	
    }    
    
}
