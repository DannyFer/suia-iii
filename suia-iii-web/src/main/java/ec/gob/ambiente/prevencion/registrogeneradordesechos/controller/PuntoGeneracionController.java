package ec.gob.ambiente.prevencion.registrogeneradordesechos.controller;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;

import ec.gob.ambiente.suia.domain.PuntoGeneracion;

@ManagedBean
public class PuntoGeneracionController implements Serializable {

	private static final long serialVersionUID = -5237894957079552906L;

	public void quitarPuntoGeneracion(PuntoGeneracion punto) {
		punto.setSeleccionado(false);
	}

}
