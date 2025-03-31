/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.SustanciaQuimicaPeligrosaFacade;
import ec.gob.ambiente.suia.domain.SustanciaQuimicaPeligrosa;

/**
 * <b> Clase para la creacion del arbol de sustancias quimicas peligrosas. </b>
 * 
 * @author Javier Lucero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Javier Lucero $, $Date: 15/06/2015 $]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class AgregarSustanciasQuimicasBean implements Serializable {

	private static final long serialVersionUID = 178842802411381223L;

	public static final String TYPE_FOLDER = "folder";
	public static final String TYPE_DOCUMENT = "document";

	@EJB
	private SustanciaQuimicaPeligrosaFacade sustanciaQuimicaPeligrosaFacade;

	@Setter
	@Getter
	private TreeNode catalogo;

	@Setter
	@Getter
	private TreeNode catalogoSeleccionado;

	@Setter
	private SustanciaQuimicaPeligrosa sustanciaSeleccionada;

	@Getter
	protected String nombreFormulario;

	@PostConstruct
	public void inicializar() {
		nombreFormulario = "form";
	}

	/**
	 * @return the desechoSeleccionado
	 */
	public SustanciaQuimicaPeligrosa getSustanciaSeleccionada() {
		if (sustanciaSeleccionada == null) {
			sustanciaSeleccionada = new SustanciaQuimicaPeligrosa();
		}
		return sustanciaSeleccionada;
	}

	@Setter
	private String filter;

	public String getFilter() {
		return filter == null ? "" : filter;
	}

	public void init() {
		setCatalogo(generarArbol());
	}

	public TreeNode generarArbol() {
		List<SustanciaQuimicaPeligrosa> sustancias = sustanciaQuimicaPeligrosaFacade
				.buscarSustanciaQuimicaPadres(getFilter());
		TreeNode root = new DefaultTreeNode();
		if (sustancias != null) {
			for (SustanciaQuimicaPeligrosa sustanciaQuimicaPeligrosa : sustancias) {
				fillTreeSustancias(sustanciaQuimicaPeligrosa, root, true);
			}
		}

		return root;
	}

	public void reset() {
		filter = null;
		init();
	}

	public void onNodeExpand(NodeExpandEvent event) {
		List<TreeNode> nodes = event.getTreeNode().getChildren();
		for (TreeNode tree : nodes) {
			fillTreeSustancias((SustanciaQuimicaPeligrosa) tree.getData(), tree, false);
		}
	}

	public void onNodeSelect(NodeSelectEvent event) {
		setSustanciaSeleccionada((SustanciaQuimicaPeligrosa) event.getTreeNode().getData());
	}

	private void fillTreeSustancias(SustanciaQuimicaPeligrosa sustanciaQuimicaPeligrosa, TreeNode root, boolean include) {
		TreeNode parent = null;
		if (include)
			parent = new DefaultTreeNode(getTipoNotod(sustanciaQuimicaPeligrosa), sustanciaQuimicaPeligrosa, root);
		else
			parent = root;
		if (parent.getType().equals(TYPE_FOLDER)) {
			parent.setSelectable(false);
			List<SustanciaQuimicaPeligrosa> sustancia = sustanciaQuimicaPeligrosaFacade
					.buscarSustanciaQuimicaPorPadre(sustanciaQuimicaPeligrosa);
			for (SustanciaQuimicaPeligrosa quimicaPeligrosa : sustancia) {
				new DefaultTreeNode(getTipoNotod(quimicaPeligrosa), quimicaPeligrosa, parent);
			}
		}
	}

	private String getTipoNotod(SustanciaQuimicaPeligrosa sustanciaQuimicaPeligrosa) {
		return sustanciaQuimicaPeligrosa.isSustanciaFinal() ? TYPE_DOCUMENT : TYPE_FOLDER;
	}
}
