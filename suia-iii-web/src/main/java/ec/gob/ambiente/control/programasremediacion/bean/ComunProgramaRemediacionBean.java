package ec.gob.ambiente.control.programasremediacion.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ec.gob.ambiente.suia.catalogocategorias.facade.CatalogoCategoriasFacade;
import ec.gob.ambiente.suia.domain.CatalogoCategoriaSistema;

@ManagedBean
@ViewScoped
public class ComunProgramaRemediacionBean implements Serializable {

	private static final long serialVersionUID = -3526371287213629686L;

	@Setter
	@Getter
	private String usuario;

	@Setter
	@Getter
	private Boolean observaciones;

	@Setter
	@Getter
	private TreeNode catalogo;

	@Setter
	@Getter
	private TreeNode catalogoSeleccionado;

	@Setter
	@Getter
	private String catalogoActivo;

	@Setter
	@Getter
	private String filter = "";

	@EJB
	private CatalogoCategoriasFacade catalogoCategoriasFacade;

	@PostConstruct
	public void init() {
		observaciones = false;
		usuario = "";

		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String area = params.get("area");
		if (area != null && !area.isEmpty()) {
			usuario = "u_tecnico_a_" + area;

		}

		catalogo = createDocument();

	}

	public void actualizarCatalogo(String sCatalogo) {
		this.catalogoActivo = sCatalogo;
	}

	public void buscarCatalogo() {
		this.catalogo = createDocument();
	}

	public void limpiarBuscarCatalogo() {
		filter = "";
		this.catalogo = createDocument();
		// catalogoActivo = "";
	}

	/**
	 * Obtener el lel catalogo y los hijos directos de un nodo, creando la raiz
	 * 
	 * @param catalogoCategorias
	 * @param padre
	 * @return
	 */
	private TreeNode crearCatalogo(CatalogoCategoriaSistema catalogoCategorias, TreeNode padre) {
		TreeNode root = (TreeNode) new DefaultTreeNode(catalogoCategorias, padre);

		for (CatalogoCategoriaSistema cat : catalogoCategoriasFacade
				.buscarCatalogoCategoriasPorPadres(catalogoCategorias)) {
			new DefaultTreeNode(cat, root);

		}

		return root;
	}

	/**
	 * Obtiene el catalogo de hijos sin crear la raiz
	 * 
	 * @param catalogoCategorias
	 * @param padre
	 * @return
	 */
	private TreeNode crearCatalogoHijos(CatalogoCategoriaSistema catalogoCategorias, TreeNode padre) {

		for (CatalogoCategoriaSistema cat : catalogoCategoriasFacade
				.buscarCatalogoCategoriasPorPadres(catalogoCategorias)) {

			System.out.println(cat);
			new DefaultTreeNode(cat, padre);

		}

		return padre;
	}

	public TreeNode createDocument() {

		List<CatalogoCategoriaSistema> catalogoCategorias = catalogoCategoriasFacade
				.buscarCatalogoCategoriasPadres(filter);

		TreeNode categorias = (TreeNode) new DefaultTreeNode();
		for (CatalogoCategoriaSistema cat : catalogoCategorias) {
			if (!buscarPadre(catalogoCategorias, cat))
				crearCatalogo(cat, categorias);
		}
		return categorias;
	}

	private boolean buscarPadre(List<CatalogoCategoriaSistema> lista, CatalogoCategoriaSistema cat) {

		if (cat.getCategoriaSistema() == null) {
			return false;
		}

		else if (lista.contains(cat.getCategoriaSistema())) {
			return true;

		} else {

			return buscarPadre(lista, cat.getCategoriaSistema());
		}
	}

	public void onNodeExpand(NodeExpandEvent event) {
		// CatalogoCategorias actual = (CatalogoCategorias) event.getTreeNode()
		// .getData();
		// actual = crearCatalogoHijos(actual, event.getTreeNode().getParent());

		for (TreeNode hijo : event.getTreeNode().getChildren()) {
			crearCatalogoHijos((CatalogoCategoriaSistema) hijo.getData(), hijo);
		}

	}

	public void onNodeCollapse(NodeCollapseEvent event) {
		System.out.println(event.getTreeNode().toString());
	}

	public void onNodeSelect(NodeSelectEvent event) {
		System.out.println(event.getTreeNode().toString());
	}

	public void onNodeUnselect(NodeUnselectEvent event) {
		System.out.println(event.getTreeNode().toString());
	}

}
