/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.AprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.domain.ModalidadGestionDesechos;
import ec.gob.ambiente.suia.domain.TipoEliminacionDesecho;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;

/**
 * 
 * <b> Clase para seleccionar el tipo de eliminacion. </b>
 * 
 * @author Javier Lucero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Javier Lucero $, $Date: 14/09/2015 $]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class TipoEliminacionDesechoModalBean implements Serializable {

	private static final long serialVersionUID = 178842802411381223L;

	public static final String TYPE_FOLDER = "folder";
	public static final String TYPE_DOCUMENT = "document";

	@EJB
	private AprobacionRequisitosTecnicosFacade aprobacionRequisitosTecnicosFacade;

	@Setter
	@Getter
	@ManagedProperty(value = "#{aprobacionRequisitosTecnicosBean}")
	private AprobacionRequisitosTecnicosBean aprobacionRequisitosTecnicosBean;

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

	@Setter
	@Getter
	private boolean incineracion;

	@Setter
	@Getter
	private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;

	public String getFilter() {
		return filter == null ? "" : filter;
	}

	public void init() {
		setCatalogo(generarArbol());
	}

	@PostConstruct
	public void inicializar() {
		aprobacionRequisitosTecnicos = aprobacionRequisitosTecnicosBean.getAprobacionRequisitosTecnicos();
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
		List<TipoEliminacionDesecho> tiposEliminacion = aprobacionRequisitosTecnicosFacade
				.buscarTipoEliminacionDesechosPadres(getFilter(), obtenerIdModalidades());
		TreeNode root = new DefaultTreeNode();
		if (tiposEliminacion != null) {

			List<String> modalidades = obtenerModalidades();

			for (TipoEliminacionDesecho tipoEliminacion : tiposEliminacion) {
				if (tipoEliminacion.getNombre().equals(TipoEliminacionDesecho.TIPO_ELIMINACION_NO_APLICA)) {

					continue;
				}
				if (existeModalidad(tipoEliminacion, modalidades)) {
					continue;
				}

				fillTreeDesechos(tipoEliminacion, root, true);
			}

			if (isIncineracion()) {
				TipoEliminacionDesecho tipoEliminacionIncineracion = new TipoEliminacionDesecho();
				tipoEliminacionIncineracion.setNombre("Incineración");
				TreeNode parent = null;
				parent = new DefaultTreeNode(TYPE_FOLDER, tipoEliminacionIncineracion, root);
				parent.setSelectable(false);
				List<TipoEliminacionDesecho> tiposEliminacionIncineracion = aprobacionRequisitosTecnicosFacade
						.buscarTipoEliminacionDesechoPorModalidad(ModalidadGestionDesechos.ID_MODALIDAD_INCINERACION);
				for (TipoEliminacionDesecho incineracion : tiposEliminacionIncineracion) {
					new DefaultTreeNode(getTipoNotod(incineracion), incineracion, parent);
				}

			}

		}
		return root;
	}

	public boolean existeModalidad(TipoEliminacionDesecho tipo, List<String> modalidades) {
		boolean existe = true;
		for (String modalidad : modalidades) {
			if (tipo.getNombre().equals(modalidad)) {
				existe = false;
			}
		}

		if (getFilter() != null && !getFilter().isEmpty()) {
			existe = false;
		}
		return existe;
	}

	public List<Integer> obtenerIdModalidades() {
		List<Integer> modalidades = new ArrayList<Integer>();
		for (ModalidadGestionDesechos modalidad : aprobacionRequisitosTecnicos.getModalidades()) {
			modalidades.add(modalidad.getId());
		}
		return modalidades;
	}

	public List<String> obtenerModalidades() {
		List<String> modalidades = new ArrayList<>();
		for (ModalidadGestionDesechos modalidad : aprobacionRequisitosTecnicos.getModalidades()) {
			if (ModalidadGestionDesechos.ID_MODALIDAD_RECICLAJE == modalidad.getId()) {
				modalidades.add("Reciclado");
			}
			if (ModalidadGestionDesechos.ID_MODALIDAD_DISPOSICION_FINAL == modalidad.getId()) {
				modalidades.add("Disposición final");
			}
			if (ModalidadGestionDesechos.ID_MODALIDAD_COPROCESAMIENTO == modalidad.getId()) {
				modalidades.add("Coprocesamiento");
			}
			if (ModalidadGestionDesechos.ID_MODALIDAD_TRATAMIENTO == modalidad.getId()) {
				modalidades.add("Tratamiento");
			}
			if (ModalidadGestionDesechos.ID_MODALIDAD_REUSO == modalidad.getId()) {
				modalidades.add("Reutilización");
			}

			if (ModalidadGestionDesechos.ID_OTROS == modalidad.getId()) {
				modalidades.add("Desensamblaje o desmantelamiento");
			}

			if (ModalidadGestionDesechos.ID_MODALIDAD_INCINERACION == modalidad.getId()) {
				modalidades.add("Incineración");
				incineracion = true;

			}
		}
		return modalidades;

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
		if (completeOperationOnAdd != null) {
			completeOperationOnAdd.endOperation(tipoEliminacionDesecho);
		}
	}

	private void fillTreeDesechos(TipoEliminacionDesecho tipoEliminacionDesecho, TreeNode root, boolean include) {

		TreeNode parent = null;
		if (include) {
			parent = new DefaultTreeNode(getTipoNotod(tipoEliminacionDesecho), tipoEliminacionDesecho, root);
		} else {
			parent = root;
		}
		if (parent.getType().equals(TYPE_FOLDER)) {
			parent.setSelectable(false);
			List<TipoEliminacionDesecho> tiposEliminacion = aprobacionRequisitosTecnicosFacade
					.buscarTipoEliminacionDesechoPorPadre(tipoEliminacionDesecho);
			for (TipoEliminacionDesecho tipoEliminacion : tiposEliminacion) {
				if ("Tratamiento térmico".equalsIgnoreCase(tipoEliminacionDesecho.getNombre())
						&& "Incineración (oxidación térmica)".equalsIgnoreCase(tipoEliminacion.getNombre())) {
					continue;
				} else {
					new DefaultTreeNode(getTipoNotod(tipoEliminacion), tipoEliminacion, parent);
				}

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
				if (code.equals(string)|| code.equals("OR1")) {
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}
}
