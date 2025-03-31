/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean;

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

import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.AprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.desechoPeligroso.facade.DesechoPeligrosoFacade;
import ec.gob.ambiente.suia.domain.CategoriaFuenteDesechoPeligroso;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.FuenteDesechoPeligroso;
import ec.gob.ambiente.suia.domain.GeneradorDesechosPeligrosos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * <b> Clase para agregar un solo desecho seleccionado. </b>
 * 
 * @author Javier Lucero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Javier Lucero $, $Date: 12/06/2015 $]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class AgregarDesechoPeligroso implements Serializable {

	private static final long serialVersionUID = 178842802411381223L;

	public static final String TYPE_FOLDER = "folder";
	public static final String TYPE_DOCUMENT = "document";
	public static final String DESECHO_ESPECIAL = "ES";

	@EJB
	private DesechoPeligrosoFacade desechoPeligrosoFacade;

	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;

	@Setter
	@Getter
	private TreeNode catalogo;

	@Setter
	@Getter
	private TreeNode catalogoSeleccionado;

	@Setter
	private DesechoPeligroso desechoSeleccionado;

	@Getter
	protected String nombreFormulario;

	@Setter
	@Getter
	private boolean mostrarDesechosB;

	@Getter
	@Setter
	private boolean revisar;

	@Getter
	@Setter
	private String codigoGenerador;

	@EJB
	private AprobacionRequisitosTecnicosFacade aprobacionRequisitosTecnicosFacade;

	@Getter
	@Setter
	private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;

	@PostConstruct
	public void inicializar() {
		try {
			aprobacionRequisitosTecnicos = aprobacionRequisitosTecnicosFacade
                    .recuperarCrearAprobacionRequisitosTecnicos(JsfUtil.getCurrentProcessInstanceId(),
							JsfUtil.getLoggedUser());
		} catch (ServiceException e) {
			e.printStackTrace();
		}

		nombreFormulario = "form";
	}

	/**
	 * @return the desechoSeleccionado
	 */
	public DesechoPeligroso getDesechoSeleccionado() {
		if (desechoSeleccionado == null) {
			desechoSeleccionado = new DesechoPeligroso();
		}
		return desechoSeleccionado;
	}

	@Setter
	private String filter;

	public String getFilter() {
		return filter == null ? "" : filter;
	}

	public void init() {
		setCatalogo(generarArbol(null));
	}

	public void init(Integer idDesecho) {
		setCatalogo(generarArbol(idDesecho));
	}

	public TreeNode generarArbol(Integer idDesecho) {
		TreeNode root = new DefaultTreeNode();
		if (idDesecho == null || idDesecho == 0 || idDesecho == -1) {
			if (getFilter().trim().isEmpty()) {
				List<CategoriaFuenteDesechoPeligroso> categorias = desechoPeligrosoFacade
						.buscarCategoriasFuentesDesechosPeligroso();
				List<CategoriaFuenteDesechoPeligroso> categoriasGenerar = new ArrayList<CategoriaFuenteDesechoPeligroso>();
				if (categorias != null) {
					CategoriaFuenteDesechoPeligroso categoriaFuentesEspecificas = new CategoriaFuenteDesechoPeligroso();
					categoriaFuentesEspecificas.setNombre(CategoriaFuenteDesechoPeligroso.NOMBRE_FUENTES_ESPECIFICAS);
					categoriaFuentesEspecificas.setCodigo(CategoriaFuenteDesechoPeligroso.CODIGO_ESPECIFICAS);
					CategoriaFuenteDesechoPeligroso categoriaAux = null;
					for (CategoriaFuenteDesechoPeligroso categoria : categorias) {
						if (DESECHO_ESPECIAL.equalsIgnoreCase(categoria.getCodigo()) && mostrarDesechosB) {
							categoriaAux = categoria;
							break;
						}
						if (idDesecho != null && idDesecho == -1
								&& categoria.getCodigo().equals(CategoriaFuenteDesechoPeligroso.CODIGO_ESPECIALES)) {
							continue;
						}

						if (!categoria.getCodigo().equals(CategoriaFuenteDesechoPeligroso.CODIGO_NO_ESPECIFICA)
								&& !categoria.getCodigo().equals(CategoriaFuenteDesechoPeligroso.CODIGO_ESPECIALES)) {
							categoriaFuentesEspecificas.getCategoriasFuenteDesechoPeligroso().add(categoria);
						} else {
							categoriasGenerar.add(categoria);
						}
					}
					if (!categoriaFuentesEspecificas.getCategoriasFuenteDesechoPeligroso().isEmpty()) {
						categoriasGenerar.add(0, categoriaFuentesEspecificas);
					}

					for (CategoriaFuenteDesechoPeligroso categoria : categoriasGenerar) {
						fillTree(categoria, root, true);
					}
				}
			} else {
				List<DesechoPeligroso> desechos = desechoPeligrosoFacade
						.buscarDesechosPeligrosoPorDescripcion(getFilter());
				for (DesechoPeligroso desecho : desechos) {
					new DefaultTreeNode(getTipoNotod(desecho), desecho, root);
				}
			}
		} else {
			DesechoPeligroso desecho = desechoPeligrosoFacade.get(idDesecho);
			new DefaultTreeNode(getTipoNotod(desecho), desecho, root);
		}
		return root;
	}

	private void fillTree(EntidadBase entidadBase, TreeNode root, boolean include) {
		TreeNode parent = null;
		if (include) {
			parent = new DefaultTreeNode(getTipoNotod(entidadBase), entidadBase, root);
		} else {
			parent = root;
		}
		if (parent.getType().equals(TYPE_FOLDER)) {
			parent.setSelectable(false);
			if (entidadBase instanceof CategoriaFuenteDesechoPeligroso) {
				CategoriaFuenteDesechoPeligroso categoria = (CategoriaFuenteDesechoPeligroso) entidadBase;
				if (categoria.getCodigo().equals(CategoriaFuenteDesechoPeligroso.CODIGO_ESPECIFICAS)) {
					for (CategoriaFuenteDesechoPeligroso subCategoria : categoria.getCategoriasFuenteDesechoPeligroso()) {
						new DefaultTreeNode(getTipoNotod(subCategoria), subCategoria, parent);
					}
				} else {
					List<FuenteDesechoPeligroso> fuentes = desechoPeligrosoFacade
							.buscarFuentesDesechosPeligrosoPorCategeria(categoria);
					for (FuenteDesechoPeligroso fuente : fuentes) {
						List<DesechoPeligroso> desechos = desechoPeligrosoFacade
								.buscarDesechosPeligrosoPorFuente(fuente);
						for (DesechoPeligroso desecho : desechos) {
							new DefaultTreeNode(getTipoNotod(desecho), desecho, parent);
						}
					}
				}
			}
			if (entidadBase instanceof FuenteDesechoPeligroso) {
				List<DesechoPeligroso> desechos = desechoPeligrosoFacade
						.buscarDesechosPeligrosoPorFuente((FuenteDesechoPeligroso) entidadBase);
				for (DesechoPeligroso desecho : desechos) {
					new DefaultTreeNode(getTipoNotod(desecho), desecho, parent);
				}
			}
		}
	}

	public void reset() {
		filter = null;
		init();
	}

	public void onNodeExpand(NodeExpandEvent event) {
		List<TreeNode> nodes = event.getTreeNode().getChildren();
		for (TreeNode tree : nodes) {
			fillTree((EntidadBase) tree.getData(), tree, false);
		}
	}

	public void onNodeSelect(NodeSelectEvent event) {
		setDesechoSeleccionado((DesechoPeligroso) event.getTreeNode().getData());
		if (isRevisar()) {
			List<GeneradorDesechosPeligrosos> generadores = registroGeneradorDesechosFacade
					.getGeneradoresFinalizadosPorDesecho(getDesechoSeleccionado());
			if (generadores != null && !generadores.isEmpty()) {

				//setCodigoGenerador("FUCK YOU AKANE");

				int pos = 0;
				boolean found = false;
				GeneradorDesechosPeligrosos currentGen;
				while (!found && pos< generadores.size()){
					currentGen = generadores.get(pos);
					if(currentGen.getProyecto()!=null && this.aprobacionRequisitosTecnicos.getProyecto().equalsIgnoreCase(currentGen.getProyecto().getCodigo())){
						setCodigoGenerador(currentGen.getCodigo());
						found = true;
					}
					else
						pos++;

				}
			}
		}
	}

	private String getTipoNotod(EntidadBase entidadBase) {
		return entidadBase instanceof DesechoPeligroso ? TYPE_DOCUMENT : TYPE_FOLDER;
	}
}
