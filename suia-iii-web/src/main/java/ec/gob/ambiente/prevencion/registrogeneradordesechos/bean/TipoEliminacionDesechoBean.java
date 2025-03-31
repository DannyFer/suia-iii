/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.prevencion.registrogeneradordesechos.bean;

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

import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.suia.domain.TipoEliminacionDesecho;
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
public class TipoEliminacionDesechoBean implements Serializable {

	private static final long serialVersionUID = 178842802411381223L;

	public static final String TYPE_FOLDER = "folder";
	public static final String TYPE_DOCUMENT = "document";

	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;

	@Setter
	@Getter
	private TreeNode catalogo;

	@Setter
	@Getter
	private TreeNode catalogoSeleccionado;

	@Getter
	@Setter
	private TipoEliminacionDesecho tipoEliminacionDesechoSeleccionada;

	@Setter
	private String filter;

	@Getter
	protected String dialogSeleccionarTipoEliminacionDesecho;

	@Getter
	protected String filtroTipoEliminacionDesecho;

	@Getter
	protected String filterButtonTipoEliminacion;

	@Getter
	protected String arbolCatalogoTipoEliminacionDesecho;

	@Getter
	protected String textoAdicionalOtroSeleccionadoContainer;

	@Getter
	protected String textoAdicionalOtroId;

	@Getter
	protected String tipoEliminacionDesechoContainer;

	@Getter
	protected String tipoEliminacionDesechoBtn;

	@Getter
	protected String tipoEliminacionDesechoBtnLabel;

	@Setter
	private CompleteOperation completeOperationOnAdd;

	@Getter
	@Setter
	private String textoAsociadoOpcionOtro;

	@Setter
	@Getter
	private boolean otroSeleccionado;

	public String getFilter() {
		return filter == null ? "" : filter;
	}

	public void init() {
		setCatalogo(generarArbol());
	}

	@PostConstruct
	public void inicializar() {
		dialogSeleccionarTipoEliminacionDesecho = "seleccionarTipoEliminacionDesecho";
		filtroTipoEliminacionDesecho = "filtroTipoEliminacionDesecho";
		filterButtonTipoEliminacion = "filterButtonTipoEliminacion";
		arbolCatalogoTipoEliminacionDesecho = "arbolCatalogoTipoEliminacionDesecho";
		tipoEliminacionDesechoContainer = "tipoEliminacionDesechoContainer";
		textoAdicionalOtroSeleccionadoContainer = "textoAdicionalOtroSeleccionadoContainer";
		tipoEliminacionDesechoBtn = "tipoEliminacionDesechoBtn";
		tipoEliminacionDesechoBtnLabel = "tipoEliminacionDesechoBtnLabel";
		textoAdicionalOtroId = "textoAdicionalOtroId";
	}

	public TreeNode generarArbol() {
		List<TipoEliminacionDesecho> tiposEliminacion = registroGeneradorDesechosFacade
				.buscarTipoEliminacionDesechosPadres(getFilter());
		TreeNode root = new DefaultTreeNode();
		if (tiposEliminacion != null)
			for (TipoEliminacionDesecho tipoEliminacion : tiposEliminacion) {
				if (tipoEliminacion.getNombre().equals(TipoEliminacionDesecho.TIPO_ELIMINACION_NO_APLICA))
					continue;
				fillTreeDesechos(tipoEliminacion, root, true);
			}
		return root;
	}

	public void reset() {
		resetSelection();
		filter = null;
		init();
	}

	public void resetSelection() {
		tipoEliminacionDesechoSeleccionada = null;
		otroSeleccionado = false;
		textoAsociadoOpcionOtro = null;
	}

	public void onNodeExpand(NodeExpandEvent event) {
		List<TreeNode> nodes = event.getTreeNode().getChildren();
		for (TreeNode tree : nodes) {
			fillTreeDesechos((TipoEliminacionDesecho) tree.getData(), tree, false);
		}
	}

	public void onNodeSelect(NodeSelectEvent event) {
		TipoEliminacionDesecho tipoEliminacionDesecho = (TipoEliminacionDesecho) event.getTreeNode().getData();
		setTipoEliminacionDesechoSeleccionada(tipoEliminacionDesecho);
		if (completeOperationOnAdd != null)
			completeOperationOnAdd.endOperation(tipoEliminacionDesecho);
	}

	private void fillTreeDesechos(TipoEliminacionDesecho tipoEliminacionDesecho, TreeNode root, boolean include) {
		TreeNode parent = null;
		if (include)
			parent = new DefaultTreeNode(getTipoNotod(tipoEliminacionDesecho), tipoEliminacionDesecho, root);
		else
			parent = root;
		if (parent.getType().equals(TYPE_FOLDER)) {
			parent.setSelectable(false);
			List<TipoEliminacionDesecho> tiposEliminacion = registroGeneradorDesechosFacade
					.buscarTipoEliminacionDesechoPorPadre(tipoEliminacionDesecho);
			for (TipoEliminacionDesecho tipoEliminacion : tiposEliminacion) {
				new DefaultTreeNode(getTipoNotod(tipoEliminacion), tipoEliminacion, parent);
			}
		}
	}

	private String getTipoNotod(TipoEliminacionDesecho tipoEliminacionDesecho) {
		return tipoEliminacionDesecho.isTipoEliminacionFinal() ? TYPE_DOCUMENT : TYPE_FOLDER;
	}

	public boolean isClaveIgual(String[] values) {
		try {
			String code = getTipoEliminacionDesechoSeleccionada().getClave();
			for (String string : values) {
				if (code.equals(string))
					return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}
}
