package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * <b> Entity que representa la tabla documents. </b>
 * 
 * @author Jonathan Guerrero
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Jonathan Guerrero, Fecha: 07/02/2015]
 *          </p>
 */
@Entity
@Table(name = "documents_process_tasks", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "dota_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "dota_status = 'TRUE'")
public class DocumentosTareasProceso extends EntidadBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2861294541308075038L;

	@Id
	@SequenceGenerator(name = "DOCUMENTS_PROCESS_TASKS_DOTAID_GENERATOR", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DOCUMENTS_PROCESS_TASKS_DOTAID_GENERATOR")
	@Column(name = "dota_id")
	@Getter
	@Setter
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "docu_id")
	@ForeignKey(name = "fk_documents_process_tasksdocu_id_documents_process_tasksdocu_id")
	@Getter
	@Setter
	private Documento documento;
	
	@Column(name = "dota_task_id")
	@Getter
	@Setter
	private long idTarea;

	@Column(name = "proc_inst_id")
	@Getter
	@Setter
	private long processInstanceId;

	@Column(name = "docu_key")
	@Getter
	@Setter
	private String llaveDocumento;
}