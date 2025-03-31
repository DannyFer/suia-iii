package ec.gob.ambiente.rcoa.registroGeneradorDesechos.controllers;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.ProyectoLicenciamientoAmbientalBean;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.reportes.UtilDocumento;

@ManagedBean
@ViewScoped
public class EmisionGeneradorVariasActividadesController {
	
	
	@Getter
	@Setter
	private Documento documentoRuc;
	
	@Getter
	@Setter
	private List<ProyectoLicenciamientoAmbientalBean> listaProyectosSeleccionados;
	
	@Getter
	@Setter
	private Integer tipoProyecto;
	
	
	
	@PostConstruct
	public void init(){
		try {
			listaProyectosSeleccionados = new ArrayList<>();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void fileUploadRuc(FileUploadEvent event) {
	}
	
	public StreamedContent getDocumentoRuc(Documento objDocumento){		
		return descargarDocumento(objDocumento);
	}
	
	private StreamedContent descargarDocumento(Documento documento){
		try {
			if(documento!=null){
				if (documento.getExtesion().contains(".pdf")){
					if(documento.getContenidoDocumento()==null)
					return UtilDocumento.getStreamedContent(documento);
				}else{
					DefaultStreamedContent content = null;
					if(documento.getContenidoDocumento() == null){
					}					
					if (documento.getNombre() != null && documento.getContenidoDocumento() != null) {
						content = new DefaultStreamedContent(new ByteArrayInputStream(documento.getContenidoDocumento()));
						content.setName(documento.getNombre());
						return content;
					}
				}
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}
	
	

}
