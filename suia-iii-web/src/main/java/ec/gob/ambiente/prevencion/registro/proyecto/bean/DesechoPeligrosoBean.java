/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.prevencion.registro.proyecto.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.proyectodesechopeligroso.facade.ProyectoDesechoPeligrosoFacade;

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
public class DesechoPeligrosoBean implements Serializable {

	private static final long serialVersionUID = 178842802411381223L;

	public static final String TYPE_FOLDER = "folder";
	public static final String TYPE_DOCUMENT = "document";

	@EJB
	private ProyectoDesechoPeligrosoFacade proyectoDesechoPeligrosoFacade;

	@Setter
	@Getter
	private TreeNode catalogo;

	@Setter
	@Getter
	private TreeNode catalogoSeleccionado;

	@Setter
	private List<DesechoPeligroso> desechosSeleccionados;

	public List<DesechoPeligroso> getDesechosSeleccionados() {
		if (desechosSeleccionados == null)
			desechosSeleccionados = new ArrayList<DesechoPeligroso>();
		return desechosSeleccionados;
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
		List<DesechoPeligroso> desechos = proyectoDesechoPeligrosoFacade.buscarDesechoPeligrososPadres(getFilter());
		TreeNode root = new DefaultTreeNode();
		if (desechos != null)
			for (DesechoPeligroso desecho : desechos) {
				fillTreeDesechos(desecho, root, true);
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
			fillTreeDesechos((DesechoPeligroso) tree.getData(), tree, false);
		}
	}

	public void onNodeSelect(NodeSelectEvent event) {
		if (!getDesechosSeleccionados().contains((DesechoPeligroso) event.getTreeNode().getData()))
			getDesechosSeleccionados().add((DesechoPeligroso) event.getTreeNode().getData());
	}

	public void onNodeUnselect(NodeUnselectEvent event) {
		eliminarDesecho((DesechoPeligroso) event.getTreeNode().getData());
	}

	public void eliminarDesecho(DesechoPeligroso desecho) {
		if (getDesechosSeleccionados().contains(desecho))
			getDesechosSeleccionados().remove(desecho);
	}

	private void fillTreeDesechos(DesechoPeligroso desechoPeligroso, TreeNode root, boolean include) {
		TreeNode parent = null;
		if (include)
			parent = new DefaultTreeNode(getTipoNotod(desechoPeligroso), desechoPeligroso, root);
		else
			parent = root;
		if (parent.getType().equals(TYPE_FOLDER)) {
			parent.setSelectable(false);
			List<DesechoPeligroso> desechos = proyectoDesechoPeligrosoFacade
					.buscarDesechoPeligrososPorPadre(desechoPeligroso);
			for (DesechoPeligroso desecho : desechos) {
				new DefaultTreeNode(getTipoNotod(desecho), desecho, parent);
			}
		}
	}

	private String getTipoNotod(DesechoPeligroso desechoPeligroso) {
		return TYPE_DOCUMENT;
	}
}
