/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.comun.bean;

import java.io.Serializable;
import java.util.ArrayList;
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

import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.suia.desechoPeligroso.facade.IncompatibilidadDesechoFacade;
import ec.gob.ambiente.suia.domain.IncompatibilidadDesechoPeligroso;

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
public class AdicionarIncompatibilidadesBean implements Serializable {

	private static final long serialVersionUID = -7910273728495519004L;

	public static final String TYPE_FOLDER = "folder";
	public static final String TYPE_DOCUMENT = "document";

	@Setter
	private CompleteOperation completeOperationOnAdd;

	@EJB
	private IncompatibilidadDesechoFacade incompatibilidadDesechoFacade;

	@Setter
	@Getter
	private TreeNode catalogo;

	@Setter
	@Getter
	private TreeNode catalogoSeleccionado;

	@Getter
	@Setter
	private String formId;

	@Getter
	@Setter
	private String containerToUpdateId;

	@Getter
	@Setter
	private String modalWidgetVar;

	@Getter
	@Setter
	private IncompatibilidadDesechoPeligroso incompatibilidadCaracteristica;

	@PostConstruct
	public void inicializar() {
		formId = "form";
		containerToUpdateId = "incompatibilidadesSeleccionadasContainer";
		modalWidgetVar = "incompatibilidadesSeleccionadasAdd";
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
		List<IncompatibilidadDesechoPeligroso> incompatibilidades = incompatibilidadDesechoFacade
				.buscarIncompatibilidadDesechoPeligrososPadres(getFilter());
		if (incompatibilidadCaracteristica != null) {
			List<IncompatibilidadDesechoPeligroso> caracteristica = new ArrayList<IncompatibilidadDesechoPeligroso>();
			for (IncompatibilidadDesechoPeligroso incompatibilidadDesechoPeligroso : incompatibilidades) {
				if (incompatibilidadDesechoPeligroso.equals(incompatibilidadCaracteristica)) {
					caracteristica.add(incompatibilidadDesechoPeligroso);
					break;
				}
			}
			incompatibilidades.clear();
			incompatibilidades.addAll(caracteristica);
		}
		TreeNode root = new DefaultTreeNode();
		if (incompatibilidades != null)
			for (IncompatibilidadDesechoPeligroso incompatibilidad : incompatibilidades) {
				fillTreeIncompatibilidades(incompatibilidad, root, true);
			}
		return root;
	}

	public void reset(IncompatibilidadDesechoPeligroso incompatibilidadCaracteristica) {
		filter = null;
		this.incompatibilidadCaracteristica = incompatibilidadCaracteristica;
		init();
	}

	public void onNodeExpand(NodeExpandEvent event) {
		List<TreeNode> nodes = event.getTreeNode().getChildren();
		for (TreeNode tree : nodes) {
			fillTreeIncompatibilidades((IncompatibilidadDesechoPeligroso) tree.getData(), tree, false);
		}
	}

	public void onNodeSelect(NodeSelectEvent event) {
		IncompatibilidadDesechoPeligroso incompatibilidad = (IncompatibilidadDesechoPeligroso) event.getTreeNode()
				.getData();
		if (completeOperationOnAdd != null)
			completeOperationOnAdd.endOperation(incompatibilidad);
	}

	private void fillTreeIncompatibilidades(IncompatibilidadDesechoPeligroso incompatibilidad, TreeNode root,
			boolean include) {
		TreeNode parent = null;
		if (include)
			parent = new DefaultTreeNode(getTipoNodo(incompatibilidad), incompatibilidad, root);
		else
			parent = root;
		if (parent.getType().equals(TYPE_FOLDER)) {
			parent.setSelectable(false);
			List<IncompatibilidadDesechoPeligroso> incompatibilidades = incompatibilidadDesechoFacade
					.buscarIncompatibilidadDesechoPeligrososPorPadre(incompatibilidad);
			for (IncompatibilidadDesechoPeligroso idp : incompatibilidades) {
				new DefaultTreeNode(getTipoNodo(idp), idp, parent);
			}
		}
	}

	private String getTipoNodo(IncompatibilidadDesechoPeligroso incompatibilidad) {
		return incompatibilidad.isIncompatibilidadDesechoFinal() ? TYPE_DOCUMENT : TYPE_FOLDER;
	}
}
