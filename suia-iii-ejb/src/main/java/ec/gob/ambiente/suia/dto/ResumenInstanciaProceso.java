package ec.gob.ambiente.suia.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

import org.jbpm.process.audit.ProcessInstanceLog;

@Data
public class ResumenInstanciaProceso implements Serializable {

	private static final long serialVersionUID = -5301520148349025811L;

	private Long processInstanceId;

	private String tramite;

	private String usuarioAsociado;

	private String processId;
	
	private Integer estado;

	private Date inicio;

	private Date fin;

	private boolean tramiteResolved;

	private String mostrarResumenRgd;
	
	private boolean deshabilitarDocumentos;
	
	private boolean deshabilitarTareas;
	private boolean digitalizado;

	public ResumenInstanciaProceso(ProcessInstanceLog processInstanceLog, String tramite, String usuarioAsociado) {
		this.processInstanceId = processInstanceLog.getProcessInstanceId();
		this.processId = processInstanceLog.getProcessId();
		this.tramite = tramite;
		this.usuarioAsociado = usuarioAsociado;
		this.estado = processInstanceLog.getStatus();
		this.inicio = processInstanceLog.getStart();
		this.fin = processInstanceLog.getEnd();
	}

	public ResumenInstanciaProceso(ProcessInstanceLog processInstanceLog, String tramite) {
		this(processInstanceLog, tramite, null);
	}

	public ResumenInstanciaProceso() {

	}

	public String getEstadoString() {
		if (estado == null)
			return "";
		if (estado == 0)
			return "No iniciado";
		if (estado == 1)
			return "En curso";
		if (estado == 2) {
			if(!digitalizado)
				return "Completado";
			else 
				return "Completado (Digitalizado)";
		}
		if (estado == 3)
			return "Abortado";
		return estado.toString();
	}
}
