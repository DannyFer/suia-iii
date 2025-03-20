package ec.gob.ambiente.suia.bandeja.controllers;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;

import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
public class BandejaUpdaterController implements Serializable{

	private static final long serialVersionUID = -4733899637796045350L;

	public void update() {
		JsfUtil.getBean(BandejaTareasController.class).init();
		JsfUtil.getBean(NotificacionesController.class).init();
	}
	
	public int getInterval() {
		return Constantes.getTrayUpdateInterval();
	}
}
