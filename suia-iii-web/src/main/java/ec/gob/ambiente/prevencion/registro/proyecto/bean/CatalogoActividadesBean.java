package ec.gob.ambiente.prevencion.registro.proyecto.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.catalogocategorias.facade.CatalogoCategoriasFacade;
import ec.gob.ambiente.suia.comparator.OrdenarCategoriaSistemaPorRangoComparator;
import ec.gob.ambiente.suia.domain.CatalogoCategoriaPublico;
import ec.gob.ambiente.suia.domain.CatalogoCategoriaSistema;
import ec.gob.ambiente.suia.domain.Categoria;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validaPago.PagoConfiguracionesUtil;


@ManagedBean
@ViewScoped
public class CatalogoActividadesBean implements Serializable {

	private static final long serialVersionUID = 3335588779800342913L;

	public static final String TYPE_FOLDER = "folder";
	public static final String TYPE_DOCUMENT = "document";

	@EJB
	private CatalogoCategoriasFacade catalogoCategoriasFacade;

	@Setter
	@Getter
	private TreeNode catalogo;

	@Setter
	@Getter
	private TreeNode catalogoSeleccionado;

	@Setter
	@Getter
	private CatalogoCategoriaPublico actividadSeleccionada;

	/**** pago 20160303 ****/
	@ManagedProperty(value = "#{loginBean}")
	@Setter
	@Getter
	private LoginBean loginBean;
	
	@Setter
	@Getter
	private Organizacion organizacion= new Organizacion();
	
	@Inject 
	private PagoConfiguracionesUtil pagoUtil;
	
	@EJB
    private OrganizacionFacade organizacionFacade;
	/***** fin código pago *****/
	
	@Setter
	@Getter
	private CatalogoCategoriaSistema actividadSistemaSeleccionada;

	@Setter
	private String filter;

	@Getter
	private TipoSector tipoSector;

	@Getter
	private boolean mostrarRangos;

	@Getter
	private List<CatalogoCategoriaSistema> categoriasSistemaCandidatas;

	@Setter
	@ManagedProperty(value = "#{registroProyectoBean}")
	private RegistroProyectoBean registroProyectoBean;
	
	@Getter
	@Setter
	private boolean mostrarActualizar = false, buscarProyecto;
	
	@Getter
	@Setter
	private boolean mostrarMetalicos = false;
	
	@Getter
	@Setter
	private String documentName;
	
	@Getter
	@Setter
	private Documento documento = new Documento();

	public String getSectorHidrocarburos() {
		tipoSector = catalogoCategoriasFacade.getTipoSector(TipoSector.TIPO_SECTOR_HIDROCARBUROS);
		init();
		return tipoSector.getNombre();
	}

	public String getSectorMineria() {
		tipoSector = catalogoCategoriasFacade.getTipoSector(TipoSector.TIPO_SECTOR_MINERIA);
		init();
		return tipoSector.getNombre();
	}

	public String getSectorOtrosSectores() {
		tipoSector = catalogoCategoriasFacade.getTipoSector(TipoSector.TIPO_SECTOR_OTROS);
		init();
		return tipoSector.getNombre();
	}

	public String getSectorElectrico() {
		tipoSector = catalogoCategoriasFacade.getTipoSector(TipoSector.TIPO_SECTOR_ELECTRICO);
		init();
		return tipoSector.getNombre();
	}
	
	public String getSectorTelecomunicaciones() {
		tipoSector = catalogoCategoriasFacade.getTipoSector(TipoSector.TIPO_SECTOR_TELECOMUNICACIONES);
		init();
		return tipoSector.getNombre();
	}
	
	public String getSectorSaneamiento() {
		tipoSector = catalogoCategoriasFacade.getTipoSector(TipoSector.TIPO_SECTOR_SANEAMIENTO);
		init();
		return tipoSector.getNombre();
	}

	public String getFilter() {
		return filter == null ? "" : filter;
	}

	public void init() {
		setCatalogo(generarArbol());
	}

	public TreeNode generarArbol() {
		List<CatalogoCategoriaPublico> catalogoCategoriasPublicas = catalogoCategoriasFacade
				.buscarCatalogoCategoriasPublicasPadres(getFilter(), tipoSector);
		TreeNode root = new DefaultTreeNode();
		if (catalogoCategoriasPublicas != null)
			for (CatalogoCategoriaPublico catalogoCategoriaPublico : catalogoCategoriasPublicas) {
				fillTreeCategorias(catalogoCategoriaPublico, root, true);
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
			fillTreeCategorias((CatalogoCategoriaPublico) tree.getData(), tree, false);
		}
	}

	public void onNodeSelect(NodeSelectEvent event) {
		markAsSelected((CatalogoCategoriaPublico) event.getTreeNode().getData(), null);
		updateComponents();
	}

	public void updateComponents() {
		registroProyectoBean.getProyecto().setMinerosArtesanales(false);
		registroProyectoBean.getProyecto().setConcesionesMinerasMultiples(false);
		registroProyectoBean.setMostrarAreaAltitud(true);
		registroProyectoBean.setMostrarUbicacionGeografica(true);

	}

	public void markAsSelected(CatalogoCategoriaPublico catalogoCategoriaPublico,
			CatalogoCategoriaSistema catalogoCategoriaSistema) {
		setActividadSeleccionada(catalogoCategoriaPublico);
		setActividadSistemaSeleccionada(catalogoCategoriaSistema);

		List<CatalogoCategoriaSistema> categoriasSistema = catalogoCategoriasFacade
				.listarCatalogoCategoriasPorCategoriaPublica(getActividadSeleccionada());

		mostrarRangos = false;
		
		String codigo = categoriasSistema.get(0).getCodigo();
		if (codigo.equals("21.02.02.01")) {
			mostrarMetalicos = true;
		}
		
		/**** pago 20160303****/
		if(categoriasSistema.get(0).getTipoLicenciamiento()!=null) {
			if(!categoriasSistema.isEmpty()) {
				if(categoriasSistema.get(0).getCategoria().getId()==2) {
					Float costoT = pagoUtil.validaPago(loginBean.getUsuario(),categoriasSistema.get(0));
					categoriasSistema.get(0).getTipoLicenciamiento().setCosto(costoT);
				}
			}
			
		}
		/**** pago 20160303****/

		if (categoriasSistema != null && !categoriasSistema.isEmpty()) {
			if (categoriasSistema.size() == 1) {
				setActividadSistemaSeleccionada(categoriasSistema.get(0));
			} else {
				mostrarRangos = true;
				categoriasSistemaCandidatas = new ArrayList<CatalogoCategoriaSistema>();
				for (CatalogoCategoriaSistema catalogoSistema : categoriasSistema) {
					if (catalogoSistema.getRango() != null)
						categoriasSistemaCandidatas.add(catalogoSistema);
				}
				Collections.sort(categoriasSistemaCandidatas, new OrdenarCategoriaSistemaPorRangoComparator());
			}
			/*
			 * cris
			 */
			if(getActividadSistemaSeleccionada() != null && (getActividadSistemaSeleccionada().getCodigo().equals("21.02.03.05") || 
					getActividadSistemaSeleccionada().getCodigo().equals("21.02.04.03")
					|| getActividadSistemaSeleccionada().getCodigo().equals("21.02.05.03") || 
					getActividadSistemaSeleccionada().getCodigo().equals("21.02.02.03"))){
				mostrarActualizar = true;
			}else{
				mostrarActualizar = false;
			}
			/*
			 * fin cris
			 */
			
			
		} else
			JsfUtil.addMessageError("No es posible seleccionar esta actividad. Contacte con Mesa de Ayuda.");
	}

	public boolean isCategoriaI() {
		try {
			return getActividadSistemaSeleccionada().getCategoria().getId().intValue() == Categoria.CATEGORIA_I;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isCategoriaIII_IV() {
		try {
			return getActividadSistemaSeleccionada().getCategoria().getId().intValue() == Categoria.CATEGORIA_III
					|| getActividadSistemaSeleccionada().getCategoria().getId().intValue() == Categoria.CATEGORIA_IV;
		} catch (Exception e) {
			return false;
		}
	}

	public String getUrlMaeTransparente() {
		return Constantes.getLinkMaeTransparente();
	}

	private void fillTreeCategorias(CatalogoCategoriaPublico catalogoCategoriaPublico, TreeNode root, boolean include) {
		TreeNode parent = null;
		if (include)
			parent = new DefaultTreeNode(getNodeType(catalogoCategoriaPublico), catalogoCategoriaPublico, root);
		else
			parent = root;
		if (parent.getType().equals(TYPE_FOLDER)) {
			parent.setSelectable(false);
			List<CatalogoCategoriaPublico> categorias = catalogoCategoriasFacade
					.buscarCatalogoCategoriasPublicasPorPadres(catalogoCategoriaPublico);
			for (CatalogoCategoriaPublico categoria : categorias) {
				new DefaultTreeNode(getNodeType(categoria), categoria, parent);
			}
		}
	}

	private String getNodeType(CatalogoCategoriaPublico catalogoCategoriaPublico) {
		return catalogoCategoriaPublico.isCategoriaFinal() ? TYPE_DOCUMENT : TYPE_FOLDER;
	}
	
	public void subir(FileUploadEvent file) {
    	String[] split=file.getFile().getContentType().split("/");
    	String extension = "."+split[split.length-1];
    	documento.setNombre(file.getFile().getFileName());
    	documento.setMime(file.getFile().getContentType());
    	documento.setContenidoDocumento(file.getFile().getContents());
    	documento.setExtesion(extension);
    	
    	setDocumentName(file.getFile().getFileName());
	}
	
}
	
