/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.prevencion.registrogeneradordesechos.bean;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ec.gob.ambiente.suia.domain.CategoriaDesechoPeligroso;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 16/03/2015]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class CategoriaDesechoPeligrosoBean implements Serializable {

	private static final long serialVersionUID = 178842802411381223L;

	public static final String TYPE_FOLDER = "folder";
	public static final String TYPE_DOCUMENT = "document";

	@EJB
	private RegistroGeneradorDesechosFacade categoriaDesechoPeligrosoFacade;

	@Setter
	@Getter
	private TreeNode catalogo;

	@Setter
	@Getter
	private TreeNode catalogoSeleccionado;

	@Getter
	@Setter
	private CategoriaDesechoPeligroso categoriaDesechoSeleccionada;

	@Setter
	private String filter;

	public String getFilter() {
		return filter == null ? "" : filter;
	}

	public void init() {
		setCatalogo(generarArbol());
	}

	public TreeNode generarArbol() {
		List<CategoriaDesechoPeligroso> desechos = categoriaDesechoPeligrosoFacade
				.buscarCategoriaDesechoPeligrososPadres(getFilter());
		TreeNode root = new DefaultTreeNode();
		if (desechos != null)
			for (CategoriaDesechoPeligroso desecho : desechos) {
				fillTreeDesechos(desecho, root, true);
			}
		return root;
	}

	public void reset() {
		resetSelection();
		filter = null;
		init();
	}

	public void resetSelection() {
		categoriaDesechoSeleccionada = null;
	}

	public void onNodeExpand(NodeExpandEvent event) {
		List<TreeNode> nodes = event.getTreeNode().getChildren();
		for (TreeNode tree : nodes) {
			fillTreeDesechos((CategoriaDesechoPeligroso) tree.getData(), tree, false);
		}
	}

	public void onNodeSelect(NodeSelectEvent event) {
		setCategoriaDesechoSeleccionada((CategoriaDesechoPeligroso) event.getTreeNode().getData());
	}

	private void fillTreeDesechos(CategoriaDesechoPeligroso categoriaDesechoPeligroso, TreeNode root, boolean include) {
		TreeNode parent = null;
		if (include)
			parent = new DefaultTreeNode(getTipoNotod(categoriaDesechoPeligroso), categoriaDesechoPeligroso, root);
		else
			parent = root;
		if (parent.getType().equals(TYPE_FOLDER)) {
			parent.setSelectable(false);
			List<CategoriaDesechoPeligroso> desechos = categoriaDesechoPeligrosoFacade
					.buscarCategoriaDesechoPeligrososPorPadre(categoriaDesechoPeligroso);
			for (CategoriaDesechoPeligroso desecho : desechos) {
				new DefaultTreeNode(getTipoNotod(desecho), desecho, parent);
			}
		}
	}

	private String getTipoNotod(CategoriaDesechoPeligroso categoriaDesechoPeligroso) {
		return categoriaDesechoPeligroso.isCategoriaDesechoFinal() ? TYPE_DOCUMENT : TYPE_FOLDER;
	}
}
