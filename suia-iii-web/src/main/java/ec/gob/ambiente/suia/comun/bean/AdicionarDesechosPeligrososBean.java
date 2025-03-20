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
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.suia.desechoPeligroso.facade.DesechoPeligrosoFacade;
import ec.gob.ambiente.suia.domain.CategoriaFuenteDesechoPeligroso;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.FuenteDesechoPeligroso;
import ec.gob.ambiente.suia.domain.PoliticaDesechosActividad;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

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
public class AdicionarDesechosPeligrososBean implements Serializable {

	private static final long serialVersionUID = 178842802411381223L;

	public static final String TYPE_FOLDER = "folder";
	public static final String TYPE_DOCUMENT = "document";

	@Setter
	protected CompleteOperation completeOperationOnDelete;

	@Setter
	protected CompleteOperation completeOperationOnAdd;

	@EJB
	private DesechoPeligrosoFacade desechoPeligrosoFacade;

	@Setter
	@Getter
	private TreeNode catalogo;

	@Setter
	@Getter
	private TreeNode catalogoSeleccionado;

	@Setter
	private List<DesechoPeligroso> desechosSeleccionados;

	@Getter
	protected String nombreFormulario;

	@Getter
	protected String dialogoDesecho;

	@Getter
	protected String filtroDesecho;

	@Getter
	protected String arbolCatalogoDesecho;

	@Getter
	protected String desechoContainer;

	@Getter
	protected String tableDesechos;

	@Getter
	protected String desechosContainerGeneral;

	@Getter
	@Setter
	protected Boolean modificacionesEliminaciones;

	@PostConstruct
	public void inicializar() {
		nombreFormulario = "form";
		dialogoDesecho = "seleccionarDesecho";
		filtroDesecho = "filtroDesecho";
		arbolCatalogoDesecho = "arbolCatalogoDesecho";
		desechoContainer = "desechoContainer";
		tableDesechos = "tableDesechos";
		desechosContainerGeneral = "desechosContainerGeneral";
	}

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
		setCatalogo(generarArbol(null));
	}

	public void init(Integer idDesecho) {
		setCatalogo(generarArbol(idDesecho));
	}

	public TreeNode generarArbol(Integer idDesecho) {
		TreeNode root = new DefaultTreeNode();
		if (idDesecho == null || idDesecho == 0) {
			if (getFilter().trim().isEmpty()) {
				List<CategoriaFuenteDesechoPeligroso> categorias = desechoPeligrosoFacade
						.buscarCategoriasFuentesDesechosPeligroso();
				List<CategoriaFuenteDesechoPeligroso> categoriasGenerar = new ArrayList<CategoriaFuenteDesechoPeligroso>();
				if (categorias != null) {
					CategoriaFuenteDesechoPeligroso categoriaFuentesEspecificas = new CategoriaFuenteDesechoPeligroso();
					categoriaFuentesEspecificas.setNombre(CategoriaFuenteDesechoPeligroso.NOMBRE_FUENTES_ESPECIFICAS);
					categoriaFuentesEspecificas.setCodigo(CategoriaFuenteDesechoPeligroso.CODIGO_ESPECIFICAS);

					for (CategoriaFuenteDesechoPeligroso categoria : categorias) {
						if (!categoria.getCodigo().equals(CategoriaFuenteDesechoPeligroso.CODIGO_NO_ESPECIFICA)
								&& !categoria.getCodigo().equals(CategoriaFuenteDesechoPeligroso.CODIGO_ESPECIALES))
							categoriaFuentesEspecificas.getCategoriasFuenteDesechoPeligroso().add(categoria);
						else
							categoriasGenerar.add(categoria);
					}
					if (!categoriaFuentesEspecificas.getCategoriasFuenteDesechoPeligroso().isEmpty())
						categoriasGenerar.add(0, categoriaFuentesEspecificas);

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
			List<PoliticaDesechosActividad> lista=desechoPeligrosoFacade.politicaDesechosActividad(idDesecho);
            if(lista.size()>0)
            {
                for(PoliticaDesechosActividad desecho: lista)
                {
                    new DefaultTreeNode(getTipoNotod(desecho.getDesechoPeligroso()), desecho.getDesechoPeligroso(), root);
                }                                
            }
//			DesechoPeligroso desecho = desechoPeligrosoFacade.get(idDesecho);
//			new DefaultTreeNode(getTipoNotod(desecho), desecho, root);
		}
		return root;
	}

	public void reset(Integer id) {
		filter = null;
		init(id);
	}
	
	public void reset() {
		reset(null);
	}

	public void onNodeExpand(NodeExpandEvent event) {
		List<TreeNode> nodes = event.getTreeNode().getChildren();
		for (TreeNode tree : nodes) {
			fillTree((EntidadBase) tree.getData(), tree, false);
		}
	}

	public void onNodeSelect(NodeSelectEvent event) {
		DesechoPeligroso desecho = (DesechoPeligroso) event.getTreeNode().getData();
		if (!getDesechosSeleccionados().contains(desecho)) {
			getDesechosSeleccionados().add(desecho);
			if (completeOperationOnAdd != null)
				completeOperationOnAdd.endOperation(desecho);
		}
	}

	public void onNodeUnselect(NodeUnselectEvent event) {
		eliminarDesecho((DesechoPeligroso) event.getTreeNode().getData());
	}

	public void eliminarDesecho(DesechoPeligroso desecho) {
		if (getDesechosSeleccionados().contains(desecho)) {
			getDesechosSeleccionados().remove(desecho);
			modificacionesEliminaciones = true;
			if (completeOperationOnDelete != null)
				completeOperationOnDelete.endOperation(desecho);
		}
	}

	public void validateDesechos(FacesContext context, UIComponent validate, Object value) {
		if (getDesechosSeleccionados().isEmpty())
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Debe seleccionar, al menos, un desecho especial/peligroso.", null));
	}

	private void fillTree(EntidadBase entidadBase, TreeNode root, boolean include) {
		TreeNode parent = null;
		if (include)
			parent = new DefaultTreeNode(getTipoNotod(entidadBase), entidadBase, root);
		else
			parent = root;
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

	private String getTipoNotod(EntidadBase entidadBase) {
		return entidadBase instanceof DesechoPeligroso ? TYPE_DOCUMENT : TYPE_FOLDER;
	}
}
