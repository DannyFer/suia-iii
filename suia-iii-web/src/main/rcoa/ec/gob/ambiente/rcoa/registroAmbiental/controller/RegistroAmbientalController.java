package ec.gob.ambiente.rcoa.registroAmbiental.controller;


import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ec.gob.ambiente.rcoa.model.FasesRegistroAmbiental;
import ec.gob.ambiente.rcoa.viabilidadTecnica.model.FaseViabilidadTecnicaRcoa;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class RegistroAmbientalController {
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{faseRegistroAmbientalController}")
    private FaseRegistroAmbientalController faseRegistroAmbientalController;

	@Getter
    @Setter
    private String urlAnterior, urlSiguiente;

	@Getter
    @Setter
	private boolean pmaConstruccion, pmaOperacion, pmaCierre;
	
	@Getter
	@Setter
	private boolean esActividadRelleno = false, pmaViabilidad;

	@PostConstruct
	private void init(){
        pmaConstruccion=false;
        pmaOperacion = false;
        pmaCierre=false;

        esActividadRelleno = faseRegistroAmbientalController.getEsActividadRelleno();
        pmaViabilidad = faseRegistroAmbientalController.getPmaViabilidad();
        if(esActividadRelleno){
        	faseRegistroAmbientalController.cargarFasesViabilidadProyecto();
    		// verifico si escogio la fase de Construcción, Rehabilitación y/o Ampliación 
    		for (FaseViabilidadTecnicaRcoa objFases : faseRegistroAmbientalController.getListaFasesRegistroAmbientalViabilidad()) {
    			if(objFases.getFasesCoa().getId().toString().equals("1")){
    				pmaConstruccion = true;
    			}else if(objFases.getFasesCoa().getId().toString().equals("2")){
    				pmaOperacion = true;
    			}else if(objFases.getFasesCoa().getId().toString().equals("3")){
    				pmaCierre = true;
    			}
    		}
        }else{
        	faseRegistroAmbientalController.cargarFasesProyecto();
    		// verifico si escogio la fase de Construcción, Rehabilitación y/o Ampliación 
    		for (FasesRegistroAmbiental objFases : faseRegistroAmbientalController.getListaFasesRegistroAmbiental()) {
    			if(objFases.getFasesCoa().getId().toString().equals("1")){
    				pmaConstruccion = true;
    			}else if(objFases.getFasesCoa().getId().toString().equals("2")){
    				pmaOperacion = true;
    			}else if(objFases.getFasesCoa().getId().toString().equals("3")){
    				pmaCierre = true;
    			}
    		}
    		if(pmaConstruccion || pmaOperacion){
    			//pmaCierre = faseRegistroAmbientalController.validarActividadConPmaCierre();
    			// si no tiene pma construccion deshabilita el menu
    			if(faseRegistroAmbientalController.validarActividadSinPmaConstruccion())
    				pmaConstruccion=false;
    		}
        }
	}
	
	public String menuActivo(String menuLink){
        FacesContext ctx = FacesContext.getCurrentInstance();
        String urls = ctx.getViewRoot().getViewId();
        if(urls.contains(menuLink)){
        	return "bold";
        } else {
    		return "";
        }
	}
	
	private void buscarUrlAnterior(){
        FacesContext ctx = FacesContext.getCurrentInstance();
        String urls = ctx.getViewRoot().getViewId();
        pmaConstruccion=false;
        pmaOperacion = false;
   
     // verifico si escogio la fase de Construcción, Rehabilitación y/o Ampliación 
        if(esActividadRelleno){
        	faseRegistroAmbientalController.cargarFasesViabilidadProyecto();
        	for (FaseViabilidadTecnicaRcoa objFases : faseRegistroAmbientalController.getListaFasesRegistroAmbientalViabilidad()) {
    			if(objFases.getFasesCoa().getId().toString().equals("1")){
    				pmaConstruccion = true;
    			}else if(objFases.getFasesCoa().getId().toString().equals("2")){
    				pmaOperacion = true;
    			}else if(objFases.getFasesCoa().getId().toString().equals("3")){
    				pmaCierre = true;
    			}
    		}
        }else{
        	faseRegistroAmbientalController.cargarFasesProyecto();
        	for (FasesRegistroAmbiental objFases : faseRegistroAmbientalController.getListaFasesRegistroAmbiental()) {
    			if(objFases.getFasesCoa().getId().toString().equals("1")){
    				pmaConstruccion = true;
    			}else if(objFases.getFasesCoa().getId().toString().equals("2")){
    				pmaOperacion = true;
    			}else if(objFases.getFasesCoa().getId().toString().equals("3")){
    				pmaCierre = true;
    			}
    		}
    		if(pmaConstruccion || pmaOperacion){
    			//pmaCierre = faseRegistroAmbientalController.validarActividadConPmaCierre();
    			// si no tiene pma construccion deshabilita el menu
    			if(faseRegistroAmbientalController.validarActividadSinPmaConstruccion())
    				pmaConstruccion=false;
    		}
        }
        
		if(urls.contains("default")){
        	urlAnterior = "/pages/rcoa/registroAmbiental/default.jsf";
        } else if(urls.contains("descripcionProyecto")){
        	urlAnterior = "/pages/rcoa/registroAmbiental/default.jsf";
        }else  if(urls.contains("planManejoAmbientalConstruccion")){
        	if(esActividadRelleno)
        		urlAnterior = "/pages/rcoa/registroAmbiental/descripcionProyectoViabilidad.jsf";
        	else
        		urlAnterior = "/pages/rcoa/registroAmbiental/descripcionProyecto.jsf";
        }else  if(urls.contains("planManejoAmbientalOperacion")){
        	if(esActividadRelleno){
        		faseRegistroAmbientalController.validateDataViabilidad();
        		 if(pmaConstruccion){
        			 if(pmaViabilidad)
        				 urlAnterior = "/pages/rcoa/registroAmbiental/planManejoAmbientalConstruccionViabilidad.jsf";
        			 else
        				 urlAnterior = "/pages/rcoa/registroAmbiental/planManejoAmbientalConstruccion.jsf";
             	}else{
                 	urlAnterior = "/pages/rcoa/registroAmbiental/descripcionProyectoViabilidad.jsf";
             	}
        	}else{
        		faseRegistroAmbientalController.validateData();
                if(pmaConstruccion){
                	urlAnterior = "/pages/rcoa/registroAmbiental/planManejoAmbientalConstruccion.jsf";            		
            	}else{
                	urlAnterior = "/pages/rcoa/registroAmbiental/descripcionProyecto.jsf";
            	}
        	}        	
            
        }else  if(urls.contains("planManejoAmbientalCierre")){
        	if(esActividadRelleno){
        		faseRegistroAmbientalController.validateDataViabilidad();

       		 if(pmaOperacion){
       			if(pmaViabilidad)
                	urlAnterior = "/pages/rcoa/registroAmbiental/planManejoAmbientalOperacionViabilidad.jsf";
       			else
       				urlAnterior = "/pages/rcoa/registroAmbiental/planManejoAmbientalOperacion.jsf";
       		 }else if(pmaConstruccion){
       			if(pmaViabilidad)
       				urlAnterior = "/pages/rcoa/registroAmbiental/planManejoAmbientalConstruccionViabilidad.jsf";
       			else
       				urlAnterior = "/pages/rcoa/registroAmbiental/planManejoAmbientalConstruccion.jsf";
           	}else{
               	urlAnterior = "/pages/rcoa/registroAmbiental/descripcionProyectoViabilidad.jsf";
           	}
        	}else{
        		faseRegistroAmbientalController.validateData();
        		 if(pmaOperacion){
                 	urlAnterior = "/pages/rcoa/registroAmbiental/planManejoAmbientalOperacion.jsf";
        		 }else if(pmaConstruccion){
                	urlAnterior = "/pages/rcoa/registroAmbiental/planManejoAmbientalConstruccion.jsf";            		
            	}else{
                	urlAnterior = "/pages/rcoa/registroAmbiental/descripcionProyecto.jsf";
            	}
        	}
        }else  if(urls.contains("participacionCiudadana")){
        	if(esActividadRelleno){
        		faseRegistroAmbientalController.validateDataViabilidad();
        		if(pmaOperacion){
        			if(pmaViabilidad)
        				urlAnterior = "/pages/rcoa/registroAmbiental/planManejoAmbientalOperacionViabilidad.jsf";
        			else
        				urlAnterior = "/pages/rcoa/registroAmbiental/planManejoAmbientalOperacion.jsf";
            	}else if(pmaConstruccion){
            		if(pmaViabilidad)
            			urlAnterior = "/pages/rcoa/registroAmbiental/planManejoAmbientalConstruccionViabilidad.jsf";
            		else
            			urlAnterior = "/pages/rcoa/registroAmbiental/planManejoAmbientalConstruccion.jsf";
            	}else{
                	urlAnterior = "/pages/rcoa/registroAmbiental/descripcionProyectoViabilidad.jsf";
            	}
        	}else{
        		faseRegistroAmbientalController.validateData();
                if(pmaOperacion){
                	urlAnterior = "/pages/rcoa/registroAmbiental/planManejoAmbientalOperacion.jsf";
            	}else if(pmaConstruccion){
                	urlAnterior = "/pages/rcoa/registroAmbiental/planManejoAmbientalConstruccion.jsf";
            	}else{
                	urlAnterior = "/pages/rcoa/registroAmbiental/descripcionProyecto.jsf";
            	}
        	}                 
        }else  if(urls.contains("finalizarRegistro")){
        	if(esActividadRelleno){
        		faseRegistroAmbientalController.validateDataViabilidad();
        		 if(pmaOperacion){
        			 if(pmaViabilidad)
        				 urlAnterior = "/pages/rcoa/registroAmbiental/planManejoAmbientalOperacionViabilidad.jsf";
        			 else
        				 urlAnterior = "/pages/rcoa/registroAmbiental/planManejoAmbientalOperacion.jsf";
             	}else if(pmaConstruccion){
             		if(pmaViabilidad)
             			urlAnterior = "/pages/rcoa/registroAmbiental/planManejoAmbientalConstruccionViabilidad.jsf";
             		else
             			urlAnterior = "/pages/rcoa/registroAmbiental/planManejoAmbientalConstruccion.jsf";
             	}else{
                 	urlAnterior = "/pages/rcoa/registroAmbiental/descripcionProyectoViabilidad.jsf";
             	}        		
        	}else{
        		faseRegistroAmbientalController.validateData();
			if(pmaCierre){
				urlAnterior = "/pages/rcoa/registroAmbiental/planManejoAmbientalCierre.jsf";
			}else if(pmaOperacion){
		        	urlAnterior = "/pages/rcoa/registroAmbiental/planManejoAmbientalOperacion.jsf";
			}else if(pmaConstruccion){
				urlAnterior = "/pages/rcoa/registroAmbiental/planManejoAmbientalConstruccion.jsf";
			}else{
				urlAnterior = "/pages/rcoa/registroAmbiental/descripcionProyecto.jsf";
			}
	       	}
	}
	}
	
	public void buscarUrlSiguiente(){
        FacesContext ctx = FacesContext.getCurrentInstance();
        String urls = ctx.getViewRoot().getViewId();
        pmaConstruccion=false;
        pmaOperacion = false;
        
        if(esActividadRelleno){
        	faseRegistroAmbientalController.cargarFasesViabilidadProyecto();
        	for (FaseViabilidadTecnicaRcoa objFases : faseRegistroAmbientalController.getListaFasesRegistroAmbientalViabilidad()) {
    			if(objFases.getFasesCoa().getId().toString().equals("1")){
    				pmaConstruccion = true;
    			}else if(objFases.getFasesCoa().getId().toString().equals("2")){
    				pmaOperacion = true;
    			}else if(objFases.getFasesCoa().getId().toString().equals("3")){
    				pmaCierre = true;
    			}
    		}
        }else{
        	faseRegistroAmbientalController.cargarFasesProyecto();
        	for (FasesRegistroAmbiental objFases : faseRegistroAmbientalController.getListaFasesRegistroAmbiental()) {
    			if(objFases.getFasesCoa().getId().toString().equals("1")){
    				pmaConstruccion = true;
    			}else if(objFases.getFasesCoa().getId().toString().equals("2")){
    				pmaOperacion = true;
    			}else if(objFases.getFasesCoa().getId().toString().equals("3")){
    				pmaCierre = true;
    			}
    		}
    		if(pmaConstruccion || pmaOperacion){
    			//pmaCierre = faseRegistroAmbientalController.validarActividadConPmaCierre();
    			// si no tiene pma construccion deshabilita el menu
    			if(faseRegistroAmbientalController.validarActividadSinPmaConstruccion())
    				pmaConstruccion=false;
    		}
        }
		// verifico si escogio la fase de Construcción, Rehabilitación y/o Ampliación 
		
		if(urls.contains("default")){
			if(esActividadRelleno){
				urlSiguiente = "/pages/rcoa/registroAmbiental/descripcionProyectoViabilidad.jsf";
			}else{
				urlSiguiente = "/pages/rcoa/registroAmbiental/descripcionProyecto.jsf";
			}        	
        } else if(urls.contains("descripcionProyecto")){
        	if(pmaConstruccion){
        		if(!esActividadRelleno)
        			urlSiguiente = "/pages/rcoa/registroAmbiental/planManejoAmbientalConstruccion.jsf";
        		else{
        			if(pmaViabilidad)
        				urlSiguiente = "/pages/rcoa/registroAmbiental/planManejoAmbientalConstruccionViabilidad.jsf";
        			else
        				urlSiguiente = "/pages/rcoa/registroAmbiental/planManejoAmbientalConstruccion.jsf";
        		}
        	}else if(pmaOperacion){
        		if(!esActividadRelleno)
        			urlSiguiente = "/pages/rcoa/registroAmbiental/planManejoAmbientalOperacion.jsf";
        		else{
        			if(pmaViabilidad)
        				urlSiguiente = "/pages/rcoa/registroAmbiental/planManejoAmbientalOperacionViabilidad.jsf";
        			else
        				urlSiguiente = "/pages/rcoa/registroAmbiental/planManejoAmbientalOperacion.jsf";
        		}
        	}else{
    			JsfUtil.addMessageError("Debe completar la información de las fases del proyecto.");
            	urlSiguiente = "/pages/rcoa/registroAmbiental/default.jsf";
        	}
        }else  if(urls.contains("planManejoAmbientalConstruccion")){
        	if(esActividadRelleno){
        		faseRegistroAmbientalController.validateDataViabilidad();
        		if(pmaOperacion){
        			if(pmaViabilidad)
        				urlSiguiente = "/pages/rcoa/registroAmbiental/planManejoAmbientalOperacionViabilidad.jsf";
        			else
        				urlSiguiente = "/pages/rcoa/registroAmbiental/planManejoAmbientalOperacion.jsf";
            	}else{
                	urlSiguiente = "/pages/rcoa/registroAmbiental/finalizarRegistro.jsf";
            	}        		
        	}else{
        		faseRegistroAmbientalController.validateData();
                if(pmaOperacion){
                	urlSiguiente = "/pages/rcoa/registroAmbiental/planManejoAmbientalOperacion.jsf";
            	}else{
                	urlSiguiente = "/pages/rcoa/registroAmbiental/finalizarRegistro.jsf";
            	}
        	}            
        }else  if(urls.contains("planManejoAmbientalOperacion")){
        	if(pmaCierre)
        		urlSiguiente = "/pages/rcoa/registroAmbiental/planManejoAmbientalCierre.jsf";
        	else
        		urlSiguiente = "/pages/rcoa/registroAmbiental/finalizarRegistro.jsf";
        }else  if(urls.contains("participacionCiudadana")){
        	urlSiguiente = "/pages/rcoa/registroAmbiental/finalizarRegistro.jsf";
        }else  if(urls.contains("planManejoAmbientalCierre")){
        		urlSiguiente = "/pages/rcoa/registroAmbiental/finalizarRegistro.jsf";
        }else  if(urls.contains("finalizarRegistro")){
        	urlSiguiente = "/pages/rcoa/registroAmbiental/finalizarRegistro.jsf";
        }
	}
	
	public void siguiente(){
		try{
			buscarUrlSiguiente();
			JsfUtil.redirectTo(urlSiguiente);
		}catch(Exception e){
			JsfUtil.addMessageError("No se puede actualizar la Ficha.");
		}
	}

	public void atras(){
		try{
			buscarUrlAnterior();
			JsfUtil.redirectTo(urlAnterior);
		}catch(Exception e){
			JsfUtil.addMessageError("No se puede actualizar la Ficha.");
		}
	}
}
