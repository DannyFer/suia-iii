package ec.gob.ambiente.suia.pocs.controllers;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@RequestScoped
public class FileUploadController {
	
	@Getter
	@Setter
	private UploadedFile file;

	public void handleFileUpload(FileUploadEvent event) {
		UploadedFile file = event.getFile();
		JsfUtil.addMessageInfo(file.getFileName());
	}
}
