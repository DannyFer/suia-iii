/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.bean;

import ec.gob.ambiente.suia.domain.Menu;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.TreeNode;

/**
 * 
 * @author christian
 */
public class MenuBean implements Serializable {

	private static final long serialVersionUID = 4848666963957109131L;
	
	@Getter
	@Setter
	private Menu menu;
	@Getter
	@Setter
	private Menu menuH;
	@Getter
	@Setter
	private TreeNode root;
	@Getter
	@Setter
	private List<Menu> listaMenu;
	@Getter
	@Setter
	private String idPadre;

	public List<String> validarDatos() {
		List<String> listaErrores = new ArrayList<String>();
		if (getMenuH().getUrl() == null || getMenuH().getUrl().isEmpty()) {
			listaErrores.add("Valor Requerido: Url");
		}
		if (getMenuH().getNombre() == null || getMenuH().getNombre().isEmpty()) {
			listaErrores.add("Valor Requerido: Nombre");
		}

		if (getIdPadre() == null || getIdPadre().isEmpty()) {
			listaErrores.add("Valor Requerido: Hijo de");
		}
		if (getMenuH().getIcono() == null || getMenuH().getIcono().isEmpty()) {
			listaErrores.add("Valor Requerido: Icono");
		}

		return listaErrores;
	}
}
