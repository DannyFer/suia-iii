package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
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

import org.apache.commons.lang.SerializationUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.rcoa.enums.CatalogoTipoCoaEnum;
import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.CultivoFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.DocumentoPquaFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.DosisCultivoFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.PlagaCultivoFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.PlagaFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.ProductoPquaFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.ProyectoPlaguicidasDetalleFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.ProyectoPlaguicidasFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.Cultivo;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.DocumentoPqua;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.DosisCultivo;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.Plaga;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.PlagaCultivo;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.ProductoPqua;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.ProyectoPlaguicidas;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.ProyectoPlaguicidasDetalle;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class AdministrarProductoSgaPmaBean {
	
	@EJB
	private ProductoPquaFacade productoPquaFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private CatalogoCoaFacade catalogoCoaFacade;
	@EJB
	private DocumentoPquaFacade documentosFacade;
	@EJB
    private ProyectoPlaguicidasDetalleFacade proyectoPlaguicidasDetalleFacade;
	@EJB
	private CultivoFacade cultivoFacade;
	@EJB
	private PlagaFacade plagaFacade;
	@EJB
    private PlagaCultivoFacade plagaCultivoFacade;
	@EJB
    private DosisCultivoFacade dosisCultivoFacade;
    @EJB
    private ProyectoPlaguicidasFacade proyectoPlaguicidasFacade;
	
	@Getter
	@Setter
	private List<ProductoPqua> listaProductosAprobados;
	@Getter
	@Setter
	private List<CatalogoGeneralCoa> listaColor, listaTipoProducto, listaTipoCategoria, listaUnidades;
	@Getter
	@Setter
	private List<DocumentoPqua> listaDocumentosJustificacion, listaDocumentosJustificacionEliminar;
	@Getter
	@Setter
	private List<ProyectoPlaguicidasDetalle> listaDetalleProyectoPlaguicidas, listaDetalleEliminar;
	@Getter
	@Setter
	private List<Cultivo> listaCultivos;
	@Getter
	@Setter
	private List<Plaga> listaPlagas;
	@Getter
	@Setter
	private List<PlagaCultivo> listaPlagaCultivo, listaPlagaCultivoEliminar;
	@Getter
	@Setter
	private List<DosisCultivo> listaDosisCultivo, listaDosisCultivoEliminar;
	@Getter
	@Setter
	private List<String> listaNombresCultivos, listaNombresPlagas;
	
	@Getter
	@Setter
	private ProductoPqua productoSeleccionado, productoEdicion;
	@Getter
	@Setter
	private ProyectoPlaguicidasDetalle detalleProyectoPlaguicidas, detalleProyectoSeleccionado;
	@Getter
	@Setter
	private PlagaCultivo nuevoPlagaCultivo;
	@Getter
	@Setter
	private DosisCultivo nuevaDosisCultivo;
	
	@Getter
	@Setter
	private String tituloVentana, cedulaOperador;
	@Getter
	@Setter
	private Boolean habilitarCultivo, editarDetalle;
	
	@PostConstruct
	public void init(){
		
	}
	
	public void cargarProductosSgaPma() {
		try {
			
			cargarProductos();
			
			listaColor = catalogoCoaFacade.obtenerCatalogo(CatalogoTipoCoaEnum.PQUA_COLOR_FRANJA);
			listaTipoProducto = catalogoCoaFacade.obtenerCatalogo(CatalogoTipoCoaEnum.PQUA_TIPO_PRODUCTO);
			listaTipoCategoria = catalogoCoaFacade.obtenerCatalogo(CatalogoTipoCoaEnum.PQUA_TIPO_CATEGORIA);
			listaUnidades = catalogoCoaFacade.obtenerCatalogo(CatalogoTipoCoaEnum.PQUA_UNIDADES);
			
			listaCultivos = cultivoFacade.listaCultivos();
			listaPlagas = plagaFacade.listaPlagas();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void cargarProductos() {
		listaProductosAprobados = productoPquaFacade.getProductoPquaAprobados();
		List<ProductoPqua> listaProductos = productoPquaFacade.getProductosFisicos();
		listaProductosAprobados.addAll(listaProductos);
	}
	
	public void buscarCedula(){
		Usuario operador = usuarioFacade.buscarUsuario(productoSeleccionado.getCedulaRuc());
		if(operador != null && operador.getId() != null){
			List<String> infoOperador = usuarioFacade.recuperarNombreOperador(operador);
			
			
			String representante = infoOperador.get(1).equals("") ? null : infoOperador.get(1);
			
			productoSeleccionado.setNombreOperador(infoOperador.get(0));
			productoSeleccionado.setRepresentanteLegal(representante);
		}else{
			JsfUtil.addMessageError("Usuario no encontrado, por favor solicitar el registro del usuario");
			productoSeleccionado.setNombreOperador(null);
			productoSeleccionado.setRepresentanteLegal(null);
		}
	}
	
	public void validarColorEtiqueta() {
		CatalogoGeneralCoa colorFranja = null;
		for (CatalogoGeneralCoa item : listaColor) {
			if(item.getNombre().toLowerCase().equals(productoSeleccionado.getCategoriaFinal().getValor().toLowerCase())) {
				colorFranja = item;
				break;
			}
		}
		
		productoSeleccionado.setColorFranjaFinal(colorFranja);
	}
	
	public void agregarProducto(){
		tituloVentana = "Adicionar productos actualizados SGA";
		productoSeleccionado = new ProductoPqua();
		
		listaDetalleProyectoPlaguicidas = new ArrayList<>();
		listaDocumentosJustificacion = new ArrayList<DocumentoPqua>();
		
		listaDetalleEliminar = new ArrayList<>();
		listaPlagaCultivoEliminar = new ArrayList<>();
		listaDosisCultivoEliminar = new ArrayList<>();
		listaDocumentosJustificacionEliminar = new ArrayList<DocumentoPqua>();
	}
	
	public void editarProducto(ProductoPqua producto) throws CmisAlfrescoException{
		tituloVentana = "Editar productos actualizados SGA";
		productoEdicion = (ProductoPqua) SerializationUtils.clone(producto);
		productoSeleccionado = producto;

		if(producto.getAprobacionFisica()) {
			listaDetalleProyectoPlaguicidas = proyectoPlaguicidasDetalleFacade.getDetallePorProducto(producto.getId());
		} else {
			ProyectoPlaguicidas proyectoPlaguicidas = proyectoPlaguicidasFacade.getPorProducto(producto.getId());
			listaDetalleProyectoPlaguicidas = proyectoPlaguicidasDetalleFacade.getDetallePorProyecto(proyectoPlaguicidas.getId());
		}
		
		listaDocumentosJustificacion = documentosFacade.documentoPorTablaIdPorIdDoc(productoSeleccionado.getId(), 
				TipoDocumentoSistema.PQUA_JUSTIFICACION_PRODUCTO, "JustificacionRegistroModificacionProducto");
		if (listaDocumentosJustificacion == null) {
			listaDocumentosJustificacion = new ArrayList<DocumentoPqua>();
		}
		
		listaDetalleEliminar = new ArrayList<>();
		listaPlagaCultivoEliminar = new ArrayList<>();
		listaDosisCultivoEliminar = new ArrayList<>();
		listaDocumentosJustificacionEliminar = new ArrayList<DocumentoPqua>();
	}
	
	public void eliminarProducto(ProductoPqua producto){
		productoPquaFacade.eliminarProducto(producto);
		
		listaProductosAprobados.remove(producto);
	}
	
	public void aceptarProducto() {
		try {
			DocumentoPqua documentoJustificacion = productoSeleccionado.getDocumentoJustificacion();
			
			if(productoSeleccionado.getComposicionProducto() != null 
					&& productoSeleccionado.getValor() != null 
					&& productoSeleccionado.getUnidadMedida() != null){
				String[] composicionList = productoSeleccionado.getComposicionProducto().split(";");
				String[] valorList = productoSeleccionado.getValor().split(";");
				String[] unidadList = productoSeleccionado.getUnidadMedida().split(";");
				
				if(composicionList.length == valorList.length && composicionList.length == unidadList.length) {
				} else {
					JsfUtil.addMessageError("El valor de las columnas Composición del producto, Valor y Unidad de medida deben tener igual número de elementos.");
					return;
				}
			}
			
			if(productoSeleccionado.getProductoActualizado() == null) {
				productoSeleccionado.setProductoActualizado(false);
			}
			
			if(productoSeleccionado.getId() == null) {
				productoSeleccionado.setAprobacionFisica(true);
			}
			
			productoPquaFacade.guardar(productoSeleccionado);
			
			ProyectoPlaguicidas proyectoPlaguicidas = proyectoPlaguicidasFacade.getPorProducto(productoSeleccionado.getId());
			
			for (ProyectoPlaguicidasDetalle detalle : listaDetalleProyectoPlaguicidas) {
				
				List<PlagaCultivo> listaPlagasCultivo = new ArrayList<>();
				List<DosisCultivo> listaDosisCultivo = new ArrayList<>();
				
				listaPlagasCultivo.addAll(detalle.getListaPlagaCultivo());
				listaDosisCultivo.addAll(detalle.getListaDosisCultivo());
				
				if(!productoSeleccionado.getAprobacionFisica()) {
					detalle.setProyecto(proyectoPlaguicidas);
				}
				
				detalle.setProducto(productoSeleccionado);
				proyectoPlaguicidasDetalleFacade.guardar(detalle);
				
				for (PlagaCultivo plagaCultivo : listaPlagasCultivo) {
					plagaCultivo.setIdDetalleProyecto(detalle.getId());
					
					plagaCultivoFacade.guardar(plagaCultivo);
				}
				
				for (DosisCultivo dosisCultivo : listaDosisCultivo) {
					dosisCultivo.setIdDetalleProyecto(detalle.getId());
					
					dosisCultivoFacade.guardar(dosisCultivo);
				}
				
			}
			
			for (ProyectoPlaguicidasDetalle detalle : listaDetalleEliminar) {
				proyectoPlaguicidasDetalleFacade.eliminar(detalle);
			}
			
			for (PlagaCultivo detalle : listaPlagaCultivoEliminar) {
				plagaCultivoFacade.guardar(detalle);
			}
			
			for (DosisCultivo detalle : listaDosisCultivoEliminar) {
				dosisCultivoFacade.guardar(detalle);
			}
			
			for (DocumentoPqua detalle : listaDocumentosJustificacionEliminar) {
				documentosFacade.guardar(detalle);
			}
			
			listaDetalleProyectoPlaguicidas = proyectoPlaguicidasDetalleFacade.getDetallePorProducto(productoSeleccionado.getId());
			listaDetalleEliminar = new ArrayList<>();
			listaPlagaCultivoEliminar = new ArrayList<>();
			listaDosisCultivoEliminar = new ArrayList<>();
			listaDocumentosJustificacionEliminar = new ArrayList<>();
			
			documentoJustificacion.setIdTabla(productoSeleccionado.getId());
			documentoJustificacion.setNombreTabla("JustificacionRegistroModificacionProducto");
			
			documentosFacade.guardarDocumentoAlfresco("DOCUMENTOS_SGA_PLAGUICIDAS", "PRODUCTO_" + productoSeleccionado.getId(), 
					0L, documentoJustificacion, TipoDocumentoSistema.PQUA_JUSTIFICACION_PRODUCTO);
			
			cargarProductos();
	
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			
			JsfUtil.addCallbackParam("addProducto");
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public void limpiarProducto() {
		cargarProductos();
		
		productoSeleccionado = new ProductoPqua();
		
		listaDetalleProyectoPlaguicidas = new ArrayList<>();
		
		listaDetalleEliminar = new ArrayList<>();
		listaPlagaCultivoEliminar = new ArrayList<>();
		listaDosisCultivoEliminar = new ArrayList<>();
		listaDocumentosJustificacionEliminar = new ArrayList<DocumentoPqua>();
	}	
	
	public void uploadJustificacion(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		DocumentoPqua documentoJustificacion = new DocumentoPqua();
		documentoJustificacion.setContenidoDocumento(contenidoDocumento);
		documentoJustificacion.setNombre(event.getFile().getFileName());
		documentoJustificacion.setMime("application/pdf");
		documentoJustificacion.setIdTipoDocumento(TipoDocumentoSistema.PQUA_JUSTIFICACION_PRODUCTO.getIdTipoDocumento());

		productoSeleccionado.setDocumentoJustificacion(documentoJustificacion);
	}
	
	public void eliminarDocumento(DocumentoPqua documento) {
		if(documento.getId() != null) {
			documento.setEstado(false);
			listaDocumentosJustificacionEliminar.add(documento);
		}
		
		listaDocumentosJustificacion.remove(documento);
	}
	
	public void validateProducto(FacesContext context, UIComponent validate, Object value) throws RemoteException {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		if(productoSeleccionado != null) {
			if(productoSeleccionado.getNombreOperador() == null 
					|| productoSeleccionado.getNombreOperador() == ""){
				errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El campo 'Nombre Operador' es requerido.", null));
			}

			if(productoSeleccionado.getDocumentoJustificacion() == null 
				|| productoSeleccionado.getDocumentoJustificacion().getContenidoDocumento() == null) {
				errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El campo 'Documento de justificación' es requerido.", null));
			}
			
			if(listaDetalleProyectoPlaguicidas == null || listaDetalleProyectoPlaguicidas.size() == 0){
				errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe ingresar información en la sección Cultivos y Plagas.", null));
			}
		}
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}

	public StreamedContent descargarDocumento(DocumentoPqua documento) throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			
			if (documento != null) {
				if (documento.getContenidoDocumento() != null) {
					documentoContent = documento.getContenidoDocumento();
				} else if(documento.getIdAlfresco() != null) {
					documentoContent = documentosFacade.descargar(documento.getIdAlfresco());
				}
			}
			
			if (documento != null && documento.getNombre() != null
					&& documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documento.getNombre());
				
				return content;
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}

	//seccion AREGAR CULTIVOS Y PLAGAS
	public void agregarDetalle() {
		habilitarCultivo = true;
		editarDetalle = false;
		
		nuevoPlagaCultivo = new PlagaCultivo();
		nuevaDosisCultivo = new DosisCultivo();
		
		listaPlagaCultivo = new ArrayList<>();
		listaDosisCultivo = new ArrayList<>();
		
		detalleProyectoPlaguicidas = new ProyectoPlaguicidasDetalle();
		detalleProyectoPlaguicidas.setRegistroFisico(true);
		
		RequestContext.getCurrentInstance().update("pnlButtonsEtiqueta");
	}

	public void editarDetalle(ProyectoPlaguicidasDetalle detalle) {
		habilitarCultivo = false;
		editarDetalle = true;
		
		detalleProyectoSeleccionado = (ProyectoPlaguicidasDetalle) SerializationUtils.clone(detalle);
		
		detalleProyectoPlaguicidas = detalle;
		listaPlagaCultivo = detalle.getListaPlagaCultivo();
		listaDosisCultivo = detalle.getListaDosisCultivo();
		
		String nombre = detalleProyectoPlaguicidas.getCultivo().getNombreCientifico();
		listaNombresCultivos = Arrays.asList(nombre.split(";"));
		
		nuevoPlagaCultivo = new PlagaCultivo();
		nuevaDosisCultivo = new DosisCultivo();
		
		listaDetalleProyectoPlaguicidas.remove(detalle);
	}
	
	public void eliminarDetalle(ProyectoPlaguicidasDetalle detalle) {
		if(detalle.getId() != null) {
			detalle.setEstado(false);
			listaDetalleEliminar.add(detalle);
		}
		
		listaDetalleProyectoPlaguicidas.remove(detalle);
	}
	
	public void recuperarNombresCientificos() {
		String nombre = detalleProyectoPlaguicidas.getCultivo().getNombreCientifico();
		listaNombresCultivos = Arrays.asList(nombre.split(";"));
		
		detalleProyectoPlaguicidas.setNombreCientificoCultivo(null);
		nuevoPlagaCultivo = new PlagaCultivo();
		listaNombresPlagas = new ArrayList<>();
	}
	
	public void recuperarNombresComunes() {
		
		listaNombresPlagas = new ArrayList<>();
		if(nuevoPlagaCultivo.getPlaga() != null) {
			String nombre = nuevoPlagaCultivo.getPlaga().getNombreComun();
			listaNombresPlagas = Arrays.asList(nombre.split(";"));
		}
		
		nuevoPlagaCultivo.setNombreComunPlaga(null);
	}
	
	public void aceptarPlaga() {
		habilitarCultivo = false;
		
		nuevoPlagaCultivo.setRegistroFisico(true);
		
		listaPlagaCultivo.add(nuevoPlagaCultivo);
		
		nuevoPlagaCultivo = new PlagaCultivo();
		listaNombresPlagas = new ArrayList<>();
	}
	
	public void aceptarDosis() {
		habilitarCultivo = false;
		
		nuevaDosisCultivo.setRegistroFisico(true);
		
		listaDosisCultivo.add(nuevaDosisCultivo);
		
		nuevaDosisCultivo = new DosisCultivo();
	}
	
	public void eliminarPlaga(PlagaCultivo plaga) {
		if(plaga.getId() != null) {
			plaga.setEstado(false);
			listaPlagaCultivoEliminar.add(plaga);
		}
		
		listaPlagaCultivo.remove(plaga);
	}
	
	public void eliminarDosis(DosisCultivo dosis) {
		if(dosis.getId() != null) {
			dosis.setEstado(false);
			listaDosisCultivoEliminar.add(dosis);
		}
		
		listaDosisCultivo.remove(dosis);
	}
	
	public void validatePlagasDosis(FacesContext context, UIComponent validate, Object value) throws RemoteException {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		if(listaPlagaCultivo == null 
				|| listaPlagaCultivo.size() == 0){
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe ingresar información de plagas", null));
		}
		
		if(listaDosisCultivo == null 
				|| listaDosisCultivo.size() == 0){
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe ingresar información de dosis", null));
		}
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}

	public void aceptarAdicionarCultivo() {
		
		detalleProyectoPlaguicidas.setListaPlagaCultivo(listaPlagaCultivo);
		detalleProyectoPlaguicidas.setListaDosisCultivo(listaDosisCultivo);
		
		listaDetalleProyectoPlaguicidas.add(detalleProyectoPlaguicidas);
		
		habilitarCultivo = true;
		
		detalleProyectoPlaguicidas = new ProyectoPlaguicidasDetalle();
		nuevoPlagaCultivo = new PlagaCultivo();
		nuevaDosisCultivo = new DosisCultivo();
		
		listaPlagaCultivo = new ArrayList<>();
		listaDosisCultivo = new ArrayList<>();
	}
	
	public void limpiarAdicionarCultivo() {
		habilitarCultivo = true;
		
		if(editarDetalle) {
			listaDetalleProyectoPlaguicidas.add(detalleProyectoSeleccionado);
		}
		
		detalleProyectoPlaguicidas = new ProyectoPlaguicidasDetalle();
		nuevoPlagaCultivo = new PlagaCultivo();
		nuevaDosisCultivo = new DosisCultivo();
		
		listaPlagaCultivo = new ArrayList<>();
		listaDosisCultivo = new ArrayList<>();
		
		listaNombresCultivos = new ArrayList<>();
		listaNombresPlagas = new ArrayList<>();
	}

	//fin seccion AREGAR CULTIVOS Y PLAGAS
}
