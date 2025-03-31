package ec.gob.ambiente.suia.domain;


import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name = "scdr_environmental_management", catalog = "", schema = "suia_iii")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "enma_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "enma_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "enma_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "enma_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "enma_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "enma_status = 'TRUE'")


public class PlanManejoAmbiental020 extends EntidadAuditable implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1341855400865720424L;
		
	@Getter
	@Setter
	@Id
	@Column(name = "enma_id")
	@SequenceGenerator(name = "ENMA_GENERATOR", initialValue = 1, sequenceName = "seq_enma_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ENMA_GENERATOR")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "enma_proposed_measures")
	private String medidasPropuestas;
	    
	@Getter
	@Setter
	@Column(name = "enma_indicator")
	private String indicador;
	
	@Getter
	@Setter
	@Column(name = "enma_means_verification")
	private String verificacionMedios;
	
	@Getter
	@Setter
	@Column(name = "enma_frequency")
	private String frecuencia;
	
	@Getter
	@Setter
	@Column(name = "enma_description")
	private String descripcion;
	
	@Getter
	@Setter
	@Column(name = "enma_order")
	private Integer orden;
	
	@Getter
	@Setter
	@Column(name = "enma_editable")
	private boolean editable;
	
	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.EAGER)
	@ForeignKey(name = "fk_parent_id")
	@JoinColumn(name = "enma_parent_id")
	private PlanManejoAmbiental020 planManejoAmbiental020;
	
	@Getter
    @Setter
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "planManejoAmbiental020")
    private List<PlanManejoAmbiental020> planManejoList;
	
}
