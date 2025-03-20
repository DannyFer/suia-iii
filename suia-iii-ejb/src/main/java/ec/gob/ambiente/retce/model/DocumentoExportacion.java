package ec.gob.ambiente.retce.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;

public class DocumentoExportacion  implements Serializable {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private String tipoDocumento;
	
	@Getter
	@Setter
	private Documento documento;
	
	@Getter
	@Setter
	private TipoDocumentoSistema tipo;
}
