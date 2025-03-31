package ec.gob.ambiente.prevencion.categoriaii.mineria.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.prevencion.categoria2.v2.controller.FichaAmbientalGeneralFinalizarControllerV2;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@SessionScoped
public class VerFicha020 {

	@Setter
	@Getter
	private String pdf;
	@Setter
	@Getter
	private StreamedContent file;
	
	@PostConstruct
	public void finalizarRegistroambiental(){

			FichaAmbientalGeneralFinalizarControllerV2 fichaAmbientalGeneralFinalizarControllerV2 = JsfUtil
					.getBean(FichaAmbientalGeneralFinalizarControllerV2.class);
		try{                  
                    File files = null;
                    files=new File(JsfUtil.devolverPathReportesHtml("ficha-"+new Date().getTime()+".pdf"));
                    FileOutputStream fileOuputStream = null;
                    fileOuputStream = new FileOutputStream(files);
                    fileOuputStream.write(fichaAmbientalGeneralFinalizarControllerV2.generarFichaRegistroAmbiental());
//                    pdf=file.getAbsolutePath();
                    pdf="reportesHtml/"+files.getName();
                    file= new DefaultStreamedContent(new FileInputStream(files),"application/pdf"); 
			} catch (Exception e) {
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			}
//		}
	}
}
