package ec.gob.ambiente.suia.domain;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.kie.api.task.model.TaskSummary;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import ec.gob.ambiente.suia.domain.PagoConfiguraciones;

@Table(name = "flows", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "flow_status")) })

@NamedQueries({
		@NamedQuery(name = Flujo.FIND_BY_ID_PROCESO, query = "SELECT f FROM Flujo f WHERE f.idProceso= :idProceso and estado = true") 
		})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "flow_status = 'TRUE'")
@Entity
public class Flujo extends EntidadBase {

	public static final Integer ID_FLUJO_CATEGORIA_DOS = 6;
	
	private static final long serialVersionUID = 3319681958346104080L;
	
	public static final String FIND_BY_ID_PROCESO = "ec.gob.ambiente.suia.domain.find_by_id_proceso";

	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "FLUJO_ID_GENERATOR", sequenceName="seq_flow_id", schema="suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FLUJO_ID_GENERATOR")
	@Column(name = "flow_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "flow_key")
	private String idProceso;

	@Getter
	@Setter
	@Column(name = "flow_name")
	private String nombreFlujo;

	@OneToMany(mappedBy = "flujo")
	@Getter
	@Setter
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<CategoriaFlujo> categoriaFlujos;

	@Getter
	@Setter
	@Transient
	private String estadoProceso;

	@Getter
	@Setter
	@Transient
	private List<TaskSummary> tareas;

	@Getter
	@Setter
	private String urlInicioFlujo;

	@Getter
	@Setter
	@Transient
	private boolean iniciaFlujo;

	@Getter
	@Setter
	@Transient
	private Long processInstanceId;
	
	@OneToMany(mappedBy = "flujoId", fetch = FetchType.LAZY)
	@Getter
	@Setter
	private List<PagoConfiguraciones> pagoConfiguracionesList;
	
	@Getter
	@Setter
	@Column(name = "flow_order")
	private Integer orden;
	
	@Getter
	@Setter
	@Column(name = "flow_system_version")
	private Integer versionSistema;
	
	@Getter
	@Setter
	@Column(name = "flow_docu_table_name")
	private String tablaDocumento;
	
	@Getter
	@Setter
	@Column(name = "flow_docu_table_prefix")
	private String prefijoTablaDocumento;
	
	@Getter
	@Setter
	@Transient
	private boolean verDocumentos;

	@Getter
	@Setter
	@Transient
	private Boolean flujoValido;
}
