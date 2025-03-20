package ec.gob.ambiente.control.retce.controllers;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.retce.model.InformacionProyecto;
import ec.gob.ambiente.retce.services.InformacionProyectoFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.login.bean.LoginBean;

@ManagedBean
@ViewScoped
public class ListaProyectosRetceController {
	
	private static final Logger LOG = Logger.getLogger(ListaProyectosRetceController.class);
	
	 @Getter
	    @Setter
	    @ManagedProperty(value = "#{bandejaTareasBean}")
	    private BandejaTareasBean bandejaTareasBean;
	 
	 @Getter
	    @Setter
	    @ManagedProperty(value = "#{loginBean}")
	    private LoginBean loginBean;
	 
	 @EJB
	 private InformacionProyectoFacade informacionProyectoFacade;
	 
	 @Getter
	 @Setter
	 private  List<InformacionProyecto> listaProyectosPorOperador;
	 
	 @PostConstruct
	 private void init(){
		 try {
			
			 listaProyectosPorOperador = informacionProyectoFacade.findByUser(loginBean.getUsuario());			 
			 
			 
		} catch (Exception e) {
			e.printStackTrace();
		}
	 }

}
