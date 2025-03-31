package ec.gob.ambiente.suia.dto;

import lombok.Data;
import ec.gob.ambiente.suia.domain.Documento;

@Data
public class DocumentoReemplazable {

	private Documento documento;

	private String workSpaceId;

	private String nombre;

	public DocumentoReemplazable(Documento documento, String workSpaceId, String nombre) {
		this.documento = documento;
		this.workSpaceId = workSpaceId;
		this.nombre = nombre;
	}

	public DocumentoReemplazable() {

	}

	public String getExtension() {
		try {
			return nombre.substring(nombre.lastIndexOf(".") + 1).toLowerCase();
		} catch (Exception e) {
			return "";
		}
	}
}
