package ec.gob.ambiente.rcoa.dto;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.rcoa.model.DocumentoCertificadoAmbiental;
import ec.gob.ambiente.rcoa.model.DocumentoRegistroAmbiental;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.DocumentoPqua;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DocumentosRgdRcoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DocumentosSustanciasQuimicasRcoa;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.dto.TaskSummaryCustom;

public class DocumentoTareaDTO {

	@Getter
    @Setter
    private String idAlfresco;
	
	@Getter
    @Setter
    private String nombreDocumento;

    @Getter
    @Setter
    private TaskSummaryCustom tarea;

    @Getter
    @Setter
    private DocumentosCOA documentoCoa;

    @Getter
    @Setter
    private DocumentoRegistroAmbiental documentoRegistroAmbiental;
    
    @Getter
    @Setter
    private DocumentoCertificadoAmbiental documentoCertificadoAmbiental;
    
    @Getter
    @Setter
    private Documento documentoPronunciamientoRetce;
    
    @Getter
    @Setter
    private DocumentosRgdRcoa documentoRgdRcoa;

    @Getter
    @Setter
    private DocumentoPqua documentoPlaguicidas;
    
    @Getter
    @Setter
    private String numeroTramite;
    
    @Getter
    @Setter
    private Boolean procesado;
    
    @Getter
    @Setter
    private Integer idOficio;
    
    @Getter
    @Setter
    private DocumentosSustanciasQuimicasRcoa documentoImportacion;
    
    @Getter
    @Setter
    private Boolean autorizacion;

}
