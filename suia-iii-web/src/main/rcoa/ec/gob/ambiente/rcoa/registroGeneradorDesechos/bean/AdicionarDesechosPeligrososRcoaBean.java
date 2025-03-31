package ec.gob.ambiente.rcoa.registroGeneradorDesechos.bean;

import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpServletResponse;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.digitalizacion.facade.AutorizacionAdministrativaAmbientalFacade;
import ec.gob.ambiente.rcoa.digitalizacion.facade.ProyectoAsociadoDigitalizacionFacade;
import ec.gob.ambiente.rcoa.digitalizacion.model.AutorizacionAdministrativaAmbiental;
import ec.gob.ambiente.rcoa.digitalizacion.model.ProyectoAsociadoDigitalizacion;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.ActividadDesechoFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.DesechosRegistroGeneradorRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.DocumentosRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.PuntoGeneracionRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosProyectosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DesechosRegistroGeneradorRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DocumentosRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.PuntoGeneracionRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.PuntoRecuperacionRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosProyectosRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosRcoa;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.desechoPeligroso.facade.DesechoPeligrosoFacade;
import ec.gob.ambiente.suia.domain.CategoriaFuenteDesechoPeligroso;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.FuenteDesechoPeligroso;
import ec.gob.ambiente.suia.domain.PoliticaDesechosActividad;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@SessionScoped
public class AdicionarDesechosPeligrososRcoaBean implements Serializable {

	private static final long serialVersionUID = 178842802411381223L;

	public static final String TYPE_FOLDER = "folder";
	public static final String TYPE_DOCUMENT = "document";
	
	@EJB
	private DesechoPeligrosoFacade desechoPeligrosoFacade;
	@EJB
	private ActividadDesechoFacade actividadDesechoFacade;
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private RegistroGeneradorDesechosRcoaFacade registroGeneradorDesechosRcoaFacade;
	@EJB
	private PuntoGeneracionRgdRcoaFacade puntoGeneracionRgdRcoaFacade;
	@EJB
	private RegistroGeneradorDesechosProyectosRcoaFacade registroGeneradorDesechosProyectosRcoaFacade;
	@EJB
	private DesechosRegistroGeneradorRcoaFacade desechosRegistroGeneradorRcoaFacade;
	@EJB
	private DocumentosRgdRcoaFacade documentosRgdRcoaFacade;
	@EJB
	private RegistroGeneradorDesechosRcoaFacade registroGeneradorFacade;
	@EJB
	private AutorizacionAdministrativaAmbientalFacade autorizacionAdministrativaAmbientalFacade;
	
	@EJB
	private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;
	

	@Setter
	protected CompleteOperation completeOperationOnDelete;

	@Setter
	protected CompleteOperation completeOperationOnAdd;	

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
	
	@Getter
	@Setter
	private List<DesechosRegistroGeneradorRcoa> desechosRcoaSeleccionados, desechosRcoaEliminados;
	
	@Getter
	@Setter
	private String tramite;
	
	private Map<String, Object> variables;

	@ManagedProperty(value = "#{bandejaTareasBean}")
	@Getter
	@Setter
	private BandejaTareasBean bandejaTareasBean;
	
	@Getter
	@Setter
	private DesechosRegistroGeneradorRcoa objetoSeleccionado;
	
    @Getter
    @Setter
    private List<PuntoGeneracionRgdRcoa> listaOrigenGeneracion;
    
    @Getter
    @Setter
    private Integer idOrigenGeneracion, proyectoId;
    
    @Getter
    @Setter
    private List<String> listaGeneracionId;
    
    private RegistroGeneradorDesechosRcoa registroGeneradorDesechos;
    
    private List<DesechosRegistroGeneradorRcoa> listaDesechosSeleccionados;
    
    @Getter
    @Setter
	private String mensajeGestionPropia, tipoPermisoRGD;
    
    @Getter
	@Setter
	private List<String> listaNombresGeneracion;
    
    @Getter
	@Setter
	private List<DocumentosRgdRcoa> listaDocumentosGestion;
	
	@PostConstruct
	public void inicializar() throws JbpmException {
		String codigoCiiu = "";
		String codigoCiuBuscar = "";
		Integer nivel = 0;
		mensajeGestionPropia = "La gestión propia corresponde a la eliminación (tratamiento) y/o disposición final de residuos o desechos peligrosos y/o especiales "
				+ "generados en el marco de una obra, proyecto o actividad sujeta a regularización ambiental, "
				+ "dentro de la misma facilidad o instalación de dicho proyecto, obra o actividad. "
				+ "Esta gestión debe estar autorizada como parte de la autorización administrativa ambiental del proyecto, obra o actividad";
		
		variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
		tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
		tipoPermisoRGD =(String)variables.get(Constantes.VARIABLE_TIPO_RGD);
		proyectoId = Integer.valueOf(((String)variables.get("idProyectoDigitalizacion") == null)?"0":(String)variables.get("idProyectoDigitalizacion"));
		if(tipoPermisoRGD != null && tipoPermisoRGD.equals(Constantes.TIPO_RGD_AAA)){
			mensajeGestionPropia += "; por lo tanto, deberá adjuntar el oficio de aprobación de Requisitos Técnicos, así como el "
					+ "oficio de aprobación del estudio de impacto ambiental y/o plan de manejo ambiental que avale dicha gestión";
		}
		mensajeGestionPropia += ".";
		ProyectoLicenciaCoa proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
		List<RegistroGeneradorDesechosProyectosRcoa> lista = new ArrayList<RegistroGeneradorDesechosProyectosRcoa>();
		boolean digitalizacion = false;
		if(proyectoId > 0){			
			AutorizacionAdministrativaAmbiental autorizacionAdministrativa = autorizacionAdministrativaAmbientalFacade.obtenerAAAPorId(proyectoId);
			registroGeneradorDesechos = registroGeneradorFacade.buscarRGDPorProyectoDigitalizado(proyectoId);
			if(autorizacionAdministrativa != null && autorizacionAdministrativa.getId() != null){
				codigoCiiu = autorizacionAdministrativa.getCatalogoCIUU().getCodigo();
				nivel = autorizacionAdministrativa.getCatalogoCIUU().getNivel();
			}
			digitalizacion = true;
		}
		// si es de digitalizacion ya no busco el proyecto aosciado
		if(!digitalizacion){
//			if(registroGeneradorDesechos == null || registroGeneradorDesechos.getId() == null){
				if(proyecto != null && proyecto.getId() != null){
					lista = registroGeneradorDesechosProyectosRcoaFacade.buscarPorProyectoRcoa(proyecto.getId()); 
				}else{
					ProyectoLicenciamientoAmbiental proyectoSuia = new ProyectoLicenciamientoAmbiental();
					proyectoSuia = proyectoLicenciamientoAmbientalFacade.buscarProyectoPorCodigoCompleto(tramite);
					if(proyectoSuia != null && proyectoSuia.getId() != null){
						lista = registroGeneradorDesechosProyectosRcoaFacade.buscarPorProyectoSuia(proyectoSuia.getId());
					}
				}
				
				if(lista != null && !lista.isEmpty()){
					registroGeneradorDesechos = lista.get(0).getRegistroGeneradorDesechosRcoa();
				}
//			}		
		}
		
		
		desechosRcoaSeleccionados = new ArrayList<DesechosRegistroGeneradorRcoa>();
		desechosRcoaEliminados = new ArrayList<DesechosRegistroGeneradorRcoa>();
				
		listaDesechosSeleccionados = new ArrayList<DesechosRegistroGeneradorRcoa>();
		
		if(registroGeneradorDesechos != null && registroGeneradorDesechos.getId() != null){
			desechosRcoaSeleccionados = new ArrayList<DesechosRegistroGeneradorRcoa>();
			listaDesechosSeleccionados = desechosRegistroGeneradorRcoaFacade.buscarPorRegistroGenerador(registroGeneradorDesechos);
			
			desechosRcoaSeleccionados.addAll(listaDesechosSeleccionados);
		}
		
		nombreFormulario = "form";
		dialogoDesecho = "seleccionarDesecho";
		filtroDesecho = "filtroDesecho";
		arbolCatalogoDesecho = "arbolCatalogoDesecho";
		desechoContainer = "desechoContainer";
		tableDesechos = "tableDesechos";
		desechosContainerGeneral = "desechosContainerGeneral";			
		
		//se busca la actividad principal del proyecto.
		List<ProyectoLicenciaCuaCiuu> listaProyectoActividades = new ArrayList<ProyectoLicenciaCuaCiuu>();
		if(proyecto != null && proyecto.getId() != null)
			listaProyectoActividades = registroGeneradorDesechosRcoaFacade.buscarActividadesCiuPrincipal(proyecto);

		for(int i = 0; i<= listaProyectoActividades.size() - 1; i++){
			if(i==0){
				codigoCiiu = listaProyectoActividades.get(i).getCatalogoCIUU().getCodigo();
				nivel = listaProyectoActividades.get(i).getCatalogoCIUU().getNivel();
			}
		}
		
		if(nivel.equals(6)){
			codigoCiuBuscar = codigoCiiu.substring(0, codigoCiiu.length()-1);		
		}else if(nivel.equals(7)){
			String[] parts = codigoCiiu.split("\\.");	
			String part1 = parts[0]; 
			String part2 = parts[1]; 
			
			String parte2Aux = part2.substring(0, part2.length()-1);
			
			codigoCiuBuscar = part1+"."+parte2Aux;
		}
		System.out.println("codigo ciiu 5 nivel: " + codigoCiuBuscar);
		
		
		if(desechosRcoaSeleccionados == null || desechosRcoaSeleccionados.isEmpty()){
			desechosRcoaSeleccionados = new ArrayList<DesechosRegistroGeneradorRcoa>();			

			List<DesechoPeligroso> listaDesechosPorActividad = new ArrayList<DesechoPeligroso>();
			
			listaDesechosPorActividad = actividadDesechoFacade.buscarDesechosPorCodigoActividad(codigoCiuBuscar);
			
			if(listaDesechosPorActividad != null && !listaDesechosPorActividad.isEmpty()){
				desechosRcoaSeleccionados = new ArrayList<DesechosRegistroGeneradorRcoa>();				
				
				for(DesechoPeligroso desecho : listaDesechosPorActividad){					
					DesechosRegistroGeneradorRcoa desechoRcoa = new DesechosRegistroGeneradorRcoa();
					desechoRcoa.setDesechoPeligroso(desecho);
					desechoRcoa.setAgregadoPorOperador(false);							
					
					desechosRcoaSeleccionados.add(desechoRcoa);
				}
			}
		} else {
			List<DesechoPeligroso> listaDesechosPorActividad = actividadDesechoFacade.buscarDesechosPorCodigoActividad(codigoCiuBuscar);
			if(listaDesechosPorActividad != null && !listaDesechosPorActividad.isEmpty()){
				if(listaDesechosPorActividad.size() != desechosRcoaSeleccionados.size()) {
					for(DesechosRegistroGeneradorRcoa item : desechosRcoaSeleccionados) {
						listaDesechosPorActividad.remove(item.getDesechoPeligroso());
					}
					
					if(listaDesechosPorActividad != null && !listaDesechosPorActividad.isEmpty()){
						for(DesechoPeligroso desecho : listaDesechosPorActividad){
							DesechosRegistroGeneradorRcoa desechoRcoa = new DesechosRegistroGeneradorRcoa();
							desechoRcoa.setDesechoPeligroso(desecho);
							desechoRcoa.setAgregadoPorOperador(false);
							
							desechosRcoaSeleccionados.add(desechoRcoa);
						}
					}
				}
			}
		}
		
		listaOrigenGeneracion = puntoGeneracionRgdRcoaFacade.findAll();
		
		objetoSeleccionado = new DesechosRegistroGeneradorRcoa();
		
		listaGeneracionId = new ArrayList<String>();
		
		listaNombresGeneracion = new ArrayList<String>();
		
	}

	public void inicializarRegREP(RegistroGeneradorDesechosRcoa registroGenerador) throws JbpmException {
		registroGeneradorDesechos = registroGenerador;
		desechosRcoaSeleccionados = new ArrayList<DesechosRegistroGeneradorRcoa>();
		desechosRcoaEliminados = new ArrayList<DesechosRegistroGeneradorRcoa>();
				
		listaDesechosSeleccionados = new ArrayList<DesechosRegistroGeneradorRcoa>();
		
		if(registroGeneradorDesechos != null && registroGeneradorDesechos.getId() != null){
			desechosRcoaSeleccionados = new ArrayList<DesechosRegistroGeneradorRcoa>();
			listaDesechosSeleccionados = desechosRegistroGeneradorRcoaFacade.buscarPorRegistroGenerador(registroGeneradorDesechos);
			
			desechosRcoaSeleccionados.addAll(listaDesechosSeleccionados);
		}
		
		nombreFormulario = "form";
		dialogoDesecho = "seleccionarDesecho";
		filtroDesecho = "filtroDesecho";
		arbolCatalogoDesecho = "arbolCatalogoDesecho";
		desechoContainer = "desechoContainer";
		tableDesechos = "tableDesechos";
		desechosContainerGeneral = "desechosContainerGeneral";			
		if(desechosRcoaSeleccionados == null || desechosRcoaSeleccionados.isEmpty()){
			desechosRcoaSeleccionados = new ArrayList<DesechosRegistroGeneradorRcoa>();			

		
		}		
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
	
	private DesechoPeligroso desechoSeleccionado;
	public void ingresarDesecho(){
		DesechosRegistroGeneradorRcoa desechoRcoa = new DesechosRegistroGeneradorRcoa();
		desechoRcoa.setDesechoPeligroso(desechoSeleccionado);
		desechoRcoa.setSeleccionarDesecho(true);
		desechoRcoa.setAgregadoPorOperador(true);
		
		desechosRcoaSeleccionados.add(desechoRcoa);
		
		if (completeOperationOnAdd != null)
			completeOperationOnAdd.endOperation(desechoSeleccionado);
		
		RequestContext context = RequestContext.getCurrentInstance(); 
		context.update(":form:desechoContainer");
        context.update(":form:tableDesechos"); 
        context.execute("PF('dialogDesechoRepetido').hide()");        
	}
	

	public void onNodeSelect(NodeSelectEvent event) {
		boolean existe = false;
		desechoSeleccionado = new DesechoPeligroso();
		
		DesechoPeligroso desecho = (DesechoPeligroso) event.getTreeNode().getData();	
		desechoSeleccionado = desecho;
				
		if(desechosRcoaSeleccionados != null && !desechosRcoaSeleccionados.isEmpty()){
						
			for(DesechosRegistroGeneradorRcoa desechoAux : desechosRcoaSeleccionados){
				if(desechoAux.getDesechoPeligroso() != null){
					if(desechoAux.getDesechoPeligroso().equals(desecho)){
						existe = true;
						break;
					}
				}
			}
		}
			
			if(!existe){				
				DesechosRegistroGeneradorRcoa desechoRcoa = new DesechosRegistroGeneradorRcoa();
				desechoRcoa.setDesechoPeligroso(desecho);
				desechoRcoa.setSeleccionarDesecho(true);
				desechoRcoa.setAgregadoPorOperador(true);
				
				desechosRcoaSeleccionados.add(desechoRcoa);
				
				if (completeOperationOnAdd != null)
					completeOperationOnAdd.endOperation(desecho);
			}else{
				RequestContext context = RequestContext.getCurrentInstance();  
		        context.execute("PF('dialogDesechoRepetido').show()");
			}				
	}

	public void onNodeUnselect(NodeUnselectEvent event) {
		eliminarDesecho((DesechosRegistroGeneradorRcoa) event.getTreeNode().getData());
	}

	public void eliminarDesecho(DesechosRegistroGeneradorRcoa desecho) {
		if (getDesechosRcoaSeleccionados().contains(desecho)) {
			getDesechosRcoaSeleccionados().remove(desecho);
			
			if(desecho.getId() != null){
				desechosRcoaEliminados.add(desecho);
			}
			
			modificacionesEliminaciones = true;
			if (completeOperationOnDelete != null)
				completeOperationOnDelete.endOperation(desecho);
		}
	}

	public void validateDesechos(FacesContext context, UIComponent validate, Object value) {
		if (getDesechosRcoaSeleccionados().isEmpty())
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
	
	public void otraGeneracionListener(DesechosRegistroGeneradorRcoa desecho){
//		if(desecho.getPuntoGeneracionRgdRcoa().getNombre().equals("Otro")){
//			desecho.setOtroGeneracionVer(true);
//		}
		
	}
	
	public void cambiarAToneladas(DesechosRegistroGeneradorRcoa desecho){
		try {
			if(desecho.getCantidadKilos() != null){
				BigDecimal toneladas = desecho.getCantidadKilos().divide(new BigDecimal(1000));
				desecho.setCantidadToneladas(toneladas);
				//a.divide(b,10,RoundingMode.HALF_UP);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void uploadDocumento(FileUploadEvent event){
		DocumentosRgdRcoa documento = new DocumentosRgdRcoa();
		documento = this.uploadListener(event, RegistroGeneradorDesechosRcoa.class, "pdf");
		documento.setSubido(false);
		objetoSeleccionado.setDocumentoGenera(documento);
	}
	
	public void uploadDocumentoGestion(FileUploadEvent event){
		DocumentosRgdRcoa documento = this.uploadListener(event, RegistroGeneradorDesechosRcoa.class, "pdf");
		if(objetoSeleccionado.getDocumentosGestion() == null)
			objetoSeleccionado.setDocumentosGestion(new ArrayList<DocumentosRgdRcoa>());
		objetoSeleccionado.getDocumentosGestion().add(documento);
		RequestContext context = RequestContext.getCurrentInstance();
        context.update("tblDocumentos");
	}
	
	public void mostrarDocumentosGestionPropia(DesechosRegistroGeneradorRcoa registroDesecho){
		objetoSeleccionado = registroDesecho;
		if(objetoSeleccionado.getDocumentosGestion() == null)
			objetoSeleccionado.setDocumentosGestion(new ArrayList<DocumentosRgdRcoa>());
		listaDocumentosGestion = objetoSeleccionado.getDocumentosGestion();
		if(listaDocumentosGestion == null)
			listaDocumentosGestion = new ArrayList<DocumentosRgdRcoa>();
	}
	
	public void eliminarAdjuntoGestion(DocumentosRgdRcoa documentoFestion){
		if(objetoSeleccionado.getDocumentosGestion() != null)
			objetoSeleccionado.getDocumentosGestion().remove(documentoFestion);
	}
	
	public void limpiarDocumentosGestion(DesechosRegistroGeneradorRcoa registroDesecho ){
		objetoSeleccionado = registroDesecho;
		objetoSeleccionado.setDocumentosGestion(new ArrayList<DocumentosRgdRcoa>());
	}
	

	public void descargarDocumento(DocumentosRgdRcoa documentoFestion) {
		try {
			if (documentoFestion != null) {
				byte[] archivo = null;
				if(documentoFestion.getIdAlfresco() != null)
					archivo = documentosRgdRcoaFacade.descargar(documentoFestion.getIdAlfresco());
				else if(documentoFestion.getContenidoDocumento()!=null)
					archivo = documentoFestion.getContenidoDocumento();
				descargarZipRar(archivo, documentoFestion.getNombre());
			}
		} catch (Exception e) {
			JsfUtil.addMessageError("Error al descargar el pdf" + documentoFestion.getNombre());
			e.printStackTrace();
		}
	}

	public static void descargarZipRar(byte[] bytes, String nombreArchivoConExtension) {
		try {
			descargarFile(bytes, nombreArchivoConExtension, getMimePorExtencion(nombreArchivoConExtension));

		} catch (Exception e) {
			JsfUtil.addMessageError("Error al descargar el pdf" + nombreArchivoConExtension);
		}
	}

	private static String getMimePorExtencion(String nombreConExtension) throws ServiceException {
		if (nombreConExtension.contains("zip")) {

			return "application/zip";
		} else if (nombreConExtension.contains("rar")) {

			return "application/rar";
		} else if (nombreConExtension.contains("pdf")) {

			return "application/pdf";
		}
		throw new ServiceException("Error extensión no encontrada.");
	}
	
	private static void descargarFile(byte[] bytes, String nombreConExtencion, String tipoContenido) throws Exception {
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
				.getResponse();
		OutputStream out = response.getOutputStream();
		response.setContentType(tipoContenido);
		response.setHeader("Content-Disposition", "attachment;filename=\"" + nombreConExtencion + "\"");
		response.setDateHeader("Expires", 0);
		out.write(bytes);
		out.flush();
		FacesContext.getCurrentInstance().responseComplete();
	}
	
	private DocumentosRgdRcoa uploadListener(FileUploadEvent event, Class<?> clazz, String extension) {
		byte[] contenidoDocumento = event.getFile().getContents();
		DocumentosRgdRcoa documento = crearDocumento(contenidoDocumento, clazz, extension);
		documento.setNombre(event.getFile().getFileName());
		return documento;
	}
	
	public DocumentosRgdRcoa crearDocumento(byte[] contenidoDocumento, Class<?> clazz, String extension) {
		DocumentosRgdRcoa documento = new DocumentosRgdRcoa();
		documento.setContenidoDocumento(contenidoDocumento);		
		documento.setExtesion("." + extension);

		documento.setMime(extension == "pdf" ? "application/pdf" : "application/vnd.ms-excel");
		return documento;
	}
	
	
	public void seleccion(DesechosRegistroGeneradorRcoa objeto){
		objetoSeleccionado = new DesechosRegistroGeneradorRcoa();		
		setObjetoSeleccionado(objeto);		
	}	
	
	//ANTERIORCHECBOX
	public void origenGeneracionListener(DesechosRegistroGeneradorRcoa desecho){
						
		if(desecho.getPuntoGeneracionIdList() != null){
			
			PuntoGeneracionRgdRcoa generacionOtro = puntoGeneracionRgdRcoaFacade.findByNombre("OT");
			
			if(!desecho.getPuntoGeneracionIdList().isEmpty()){
				PuntoGeneracionRgdRcoa generacionNuevo = puntoGeneracionRgdRcoaFacade.findById(Integer.valueOf(desecho.getPuntoGeneracionIdList().get(0)));
				desecho.setNombreOrigen(generacionNuevo.getNombre());
			}else{
				desecho.setNombreOrigen("Seleccione..");
			}			
			
			boolean existe = false;
			
			for(String id : desecho.getPuntoGeneracionIdList()){
				if(id.equals(String.valueOf(generacionOtro.getId()))){
					existe = true;
					break;
				}
			}
			
			if(existe){
				if(desecho.getOtroGeneracion() == null){
					JsfUtil.addMessageError("Ingrese otro generación de desechos");
					return;
				}
			}
			
			
			String nombres = "";
			for(String id : desecho.getPuntoGeneracionIdList()){
				PuntoGeneracionRgdRcoa generacionNuevo = puntoGeneracionRgdRcoaFacade.findById(Integer.valueOf(id));
				
				nombres += generacionNuevo.getClave() + "-" + generacionNuevo.getNombre() + "<br />";			
			}

			desecho.setNombresGeneracion(nombres);			
			
			desecho.setOtroGeneracionVer(existe);
			
			RequestContext context = RequestContext.getCurrentInstance();  
	        context.update("pnlOtraGeneracion");
	        context.update("menu");
	        context.update("pnlListaGeneracion");
	        
	        context.execute("PF('seleccionarGeneracion').hide()");
		}			
	}	
	
	public void adjuntarGestionInternaListener(DesechosRegistroGeneradorRcoa desecho){
		if(!desecho.getGestionInterna()){
//			desecho.setDocumentoGestion(new DocumentosRgdRcoa());
		}
	}
	
	public void eliminarCamposNoGenera(DesechosRegistroGeneradorRcoa desecho){
		
		desecho.setCantidadKilos(null);
		desecho.setCantidadToneladas(null);
		desecho.setGestionInterna(false);
		desecho.setPuntoGeneracionRgdRcoaList(new ArrayList<PuntoGeneracionRgdRcoa>());
		desecho.setIdGeneracion(null);
		desecho.setOtroGeneracion(null);
		desecho.setOtroGeneracionVer(false);
		desecho.setPuntoGeneracionIdList(new ArrayList<String>());
		desecho.setNombreOrigen("Seleccione..");
		desecho.setDescripcionDesecho(null);
		desecho.setNombresGeneracion(null);		
		
		desecho.setCantidadUnidades(null);
		desecho.setSistemaGestionIndividual(null);
		
		RequestContext context = RequestContext.getCurrentInstance();  
        context.update(":form:desechoContainer");
        context.update(":form:tableDesechos");
        context.update(":form");    
	}
	
	public void origenesGeneracionListener(){
		
		if(listaGeneracionId != null){
			
			PuntoGeneracionRgdRcoa generacionOtro = puntoGeneracionRgdRcoaFacade.findByNombre("OT");		
			
			boolean existe = false;
			
			for(String id : listaGeneracionId){
				if(id.equals(String.valueOf(generacionOtro.getId()))){
					existe = true;
					break;
				}
			}
			
			if(existe){
				if(objetoSeleccionado.getOtroGeneracion() == null){
					if(tipoPermisoRGD == null || !tipoPermisoRGD.equals(Constantes.TIPO_RGD_AAA)){
						JsfUtil.addMessageError("Ingrese otro generación de desechos");
						return;
					}
				}
			}
			
			String nombres = "";
			for(String id : listaGeneracionId){
				PuntoGeneracionRgdRcoa generacionNuevo = puntoGeneracionRgdRcoaFacade.findById(Integer.valueOf(id));
				if(id.equals(String.valueOf(generacionOtro.getId()))){
					nombres += generacionNuevo.getClave() + "-" + generacionNuevo.getNombre() + (objetoSeleccionado.getOtroGeneracion() == null ? "" : "-" +objetoSeleccionado.getOtroGeneracion())  +"<br />";
				}else{
					nombres += generacionNuevo.getClave() + "-" + generacionNuevo.getNombre() + "<br />";
				}
			}

			objetoSeleccionado.setNombresGeneracion(nombres);			
			
			objetoSeleccionado.setOtroGeneracionVer(existe);
			objetoSeleccionado.setPuntoGeneracionIdList(listaGeneracionId);
			
			RequestContext context = RequestContext.getCurrentInstance();  
//	        context.update("pnlOtraGeneracion");
	        context.update("menu");
	        context.update("pnlListaGeneracion");
	        context.update("pnlBtnGeneracion");  
	        
	        context.execute("PF('seleccionarGeneracion').hide()");
	        
	        listaGeneracionId = new ArrayList<String>();
		}			
	}	
	
	public void cargarOrigenGeneracion(DesechosRegistroGeneradorRcoa desecho){
		try {
			// elimino los origenes de generacion que no fueron sleccionados en puntos de generacion 
			if(tipoPermisoRGD != null && tipoPermisoRGD.equals(Constantes.TIPO_RGD_AAA)){
				listarOrigenesGeneracion();
			}
			
			listaGeneracionId = new ArrayList<String>();
			listaGeneracionId.addAll(desecho.getPuntoGeneracionIdList());
			
			PuntoGeneracionRgdRcoa generacionOtro = puntoGeneracionRgdRcoaFacade.findByNombre("OT");
			
			boolean existe = false;
			
			for(String id : listaGeneracionId){
				if(id.equals(String.valueOf(generacionOtro.getId()))){
					existe = true;
					break;
				}
			}
			
			if(existe){
				desecho.setOtroGeneracionVer(true);
			}else
				desecho.setOtroGeneracionVer(false);
			
			RequestContext context = RequestContext.getCurrentInstance();  
			context.execute("PF('seleccionarGeneracion').show()");
			
			context.update("seleccionarGeneracion");
			context.update("menu");
			context.update("pnlOtraGeneracion");
			context.update("otraGeneracion");			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void listarOrigenesGeneracion(){
		listaOrigenGeneracion = puntoGeneracionRgdRcoaFacade.findAll();
		List<PuntoGeneracionRgdRcoa> listaGeneracionSeleccionados = new ArrayList<PuntoGeneracionRgdRcoa>();
		List<PuntoRecuperacionRgdRcoa> listaPuntosRecuperacion = JsfUtil.getBean(PuntosRecuperacionRgdBean.class).getPuntosRecuperacion();
		for(PuntoRecuperacionRgdRcoa punto : listaPuntosRecuperacion){
			if(punto.getPuntoGeneracion() != null){
				if(!listaGeneracionSeleccionados.contains(punto.getPuntoGeneracion()))
					listaGeneracionSeleccionados.add(punto.getPuntoGeneracion());
			}
		}
		List<PuntoGeneracionRgdRcoa> listaGeneracionEliminar = new ArrayList<PuntoGeneracionRgdRcoa>();
		for (PuntoGeneracionRgdRcoa puntoGeneracionOrigen : listaOrigenGeneracion) {
			boolean existePunto = false;
			for (PuntoGeneracionRgdRcoa puntoGeneracionSeleccionado : listaGeneracionSeleccionados) {
				if(puntoGeneracionOrigen.equals(puntoGeneracionSeleccionado)){
					existePunto = true;
					break;
				}
			}
			// si no  existe lo elimino de la lista de origenes  
			if(!existePunto)
				listaGeneracionEliminar.add(puntoGeneracionOrigen);
		}
		listaOrigenGeneracion.removeAll(listaGeneracionEliminar);
	}
	
	public void habilitarOtro(){
		try {
			
			PuntoGeneracionRgdRcoa generacionOtro = puntoGeneracionRgdRcoaFacade.findByNombre("OT");
			
			boolean existe = false;
			for(String id : listaGeneracionId){
				if(id.equals(String.valueOf(generacionOtro.getId()))){
					existe = true;
					break;
				}
			}
			
			objetoSeleccionado.setOtroGeneracionVer(existe);
			RequestContext context = RequestContext.getCurrentInstance();
			context.update("pnlOtraGeneracion");
			context.update("otraGeneracion");	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	

}
