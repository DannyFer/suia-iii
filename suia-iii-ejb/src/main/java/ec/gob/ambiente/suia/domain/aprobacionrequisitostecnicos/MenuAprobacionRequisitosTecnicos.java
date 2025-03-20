/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos;

import lombok.Getter;
import lombok.Setter;

/**
 * <b> Incluir aqui la descripcion de la clase. </b>
 * 
 * @author vero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: vero $, $Date: 11/06/2015 $]
 *          </p>
 */
public class MenuAprobacionRequisitosTecnicos {

	@Getter
	@Setter
	private String page;

	@Getter
	@Setter
	private String titulo;

	public MenuAprobacionRequisitosTecnicos(String page, String titulo) {
		this.page = page;
		this.titulo = titulo;
	}

}
