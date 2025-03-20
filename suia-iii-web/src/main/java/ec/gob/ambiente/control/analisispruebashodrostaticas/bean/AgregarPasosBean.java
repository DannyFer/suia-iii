package ec.gob.ambiente.control.analisispruebashodrostaticas.bean;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import lombok.Getter;
import lombok.Setter;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author Jonathan Guerrero
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Jonathan Guerrero, Fecha: 23/12/2014]
 *          </p>
 */
@SessionScoped
@ManagedBean
public class AgregarPasosBean {

	@Setter
	@Getter
	private List<String> pasos;

	@Setter
	@Getter
	private List<String> numeroPasos;

	public AgregarPasosBean() {
		pasos = new ArrayList<String>();
		numeroPasos = new ArrayList<String>();
	}

	public void agregarPaso() {
		pasos.add("");
		numeroPasos.add("");
	}
}
