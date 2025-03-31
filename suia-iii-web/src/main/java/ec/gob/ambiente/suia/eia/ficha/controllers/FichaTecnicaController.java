package ec.gob.ambiente.suia.eia.ficha.controllers;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.eia.ficha.bean.FichaTecnicaBean;
import ec.gob.ambiente.suia.fichatecnica.facade.FichaTecnicaFacade;

@ManagedBean
@ViewScoped
public class FichaTecnicaController implements Serializable {

	private static final long serialVersionUID = -5341403828883666176L;
	@Getter
	@Setter
	@ManagedProperty(value = "#{fichaTecnicaBean}")
	private FichaTecnicaBean fichaTecnicaBean;
//	private static final String MINIMO_COORDENADAS = "Se requiere mínimo 4 puntos";
//	private static final String ADJUNTO="Se debe adjuntar la carta de compromiso";
	@EJB
	private FichaTecnicaFacade fichaTecnicaFacade;
	
//	public void agregarCoordenada() {
//		CoordenadaGeneral coordenadaGeneral=new CoordenadaGeneral(new BigDecimal(0), new BigDecimal(0));
//		coordenadaGeneral.setEditar(false);
//		fichaTecnicaBean.setCoordenadaGeneral(coordenadaGeneral);
//
//	}
//
//	public void seleccionarFicha(CoordenadaGeneral coordenada){
//			coordenada.setEditar(true);
//			getFichaTecnicaBean().setCoordenadaGeneral(coordenada);
//	}
//	
//	public void eliminarCoordenada(CoordenadaGeneral coordenada) {
//		fichaTecnicaBean.getCoordenadas().remove(coordenada);
//		if(coordenada.getId()!=null){
//			coordenada.setEstado(false);
//			fichaTecnicaBean.getCoordenadasBorradas().add(coordenada);
//		}
//		
//	}
//
//	public String guardarCoordenada() {
//		if (validar()) {
//			try {
//                                Map<String, EiaOpciones> mapOpciones = (Map<String, EiaOpciones>) JsfUtil.devolverObjetoSession(Constantes.SESSION_OPCIONES_EIA);
//				fichaTecnicaFacade.guardar(fichaTecnicaBean.getFichaTecnica(),fichaTecnicaBean.getCoordenadas(),fichaTecnicaBean.getDocumento(),
//											fichaTecnicaBean.getCoordenadasBorradas(),fichaTecnicaBean.getDocumentoTarea(),
//                                                                                        mapOpciones.get(EiaOpciones.FICHA_TECNICA_HIDRO));
//				JsfUtil.addMessageInfo(JsfUtil.REGISTRO_ACTUALIZADO);
//				limpiar();
//				return JsfUtil.actionNavigateTo("/pages/eia/default.jsf");
//
//			} catch (Exception ex) {
//				JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO);
//			}
//		} 
//		return null;
//	}
//
//	public void limpiar() {
//		fichaTecnicaBean.setFichaTecnica(new FichaTecnica());
//		fichaTecnicaBean.setCoordenadas(
//				new ArrayList<CoordenadaGeneral>());
//		fichaTecnicaBean.getCoordenadas()
//				.add(new CoordenadaGeneral(new BigDecimal(0), new BigDecimal(0)));
//		fichaTecnicaBean.setDocumento(new Documento());
//		fichaTecnicaBean.getDocumento().setEditar(false);
//		fichaTecnicaBean.getDocumento().setEstado(false);
//	}
//	
//	public void asignarListaCoordenadas(){
//		RequestContext context = RequestContext.getCurrentInstance();
//		try{
//			if(!fichaTecnicaBean.getCoordenadaGeneral().isEditar())
//				fichaTecnicaBean.getCoordenadas().add(fichaTecnicaBean.getCoordenadaGeneral());
//				context.addCallbackParam("coordenadaIn", true);
//		}catch(Exception ex){
//			context.addCallbackParam("coordenadaIn", false);
//		}
//	}
//	
//	public void uploadListener(FileUploadEvent event) {
//		fichaTecnicaBean.setCarta(event.getFile());
//		fichaTecnicaBean.getFichaTecnica().setContenidoArchivo(fichaTecnicaBean.getCarta().getContents());
//		fichaTecnicaBean.getFichaTecnica().setNombreArchivo(fichaTecnicaBean.getCarta().getFileName());
//		fichaTecnicaBean.getFichaTecnica().setTipoContenido(fichaTecnicaBean.getCarta().getContentType());
//	}
//		
//	
//	public boolean validar() {
//		boolean valor = true;
//		if (fichaTecnicaBean.getCoordenadas().size() < 4) {
//			System.out.println(fichaTecnicaBean.getCoordenadas().size());
//			JsfUtil.addMessageError(MINIMO_COORDENADAS);
//			return false;
//		}
//		
//		if (fichaTecnicaBean.getFichaTecnica().getNombreArchivo()==null) {
//			JsfUtil.addMessageError(ADJUNTO);
//			return false;
//		}
//		
//		return valor;
//	}
//	
//	public String cancelar() {
//		fichaTecnicaBean.setCoordenadas(new ArrayList<CoordenadaGeneral>());
//		return JsfUtil.actionNavigateTo("/pages/eia/ficha/fichaTecnica.jsf");
//	}
//	
//	
//	public void handleFileUpload(FileUploadEvent event) {
//		if (fichaTecnicaBean.getFichaTecnica().getNombreArchivo() != null) {
//			fichaTecnicaBean.getDocumento().setEstado(false);
//		}
//		fichaTecnicaBean.getFichaTecnica().setContenidoArchivo(event.getFile().getContents());
//		fichaTecnicaBean.getFichaTecnica().setTipoContenido(event.getFile().getContentType());
//		fichaTecnicaBean.getFichaTecnica().setNombreArchivo(event.getFile().getFileName());
//		fichaTecnicaBean.setVisibleDocumento(false);
//		FacesMessage message = new FacesMessage("Archivo cargado con éxito: ",
//				event.getFile().getFileName());
//		FacesContext.getCurrentInstance().addMessage(null, message);
//	}
//	
//	public StreamedContent descargar() throws CmisAlfrescoException {  
//	    fichaTecnicaBean.setFichaTecnica(fichaTecnicaFacade.descargar(fichaTecnicaBean.getDocumento()));
//	    InputStream is = new ByteArrayInputStream( fichaTecnicaBean.getFichaTecnica().getContenidoArchivo());
//	    fichaTecnicaBean.setFile(new DefaultStreamedContent(is, fichaTecnicaBean.getFichaTecnica().getTipoContenido(), fichaTecnicaBean.getFichaTecnica().getNombreArchivo()));
//	    return fichaTecnicaBean.getFile();
//	    }
	 
}
