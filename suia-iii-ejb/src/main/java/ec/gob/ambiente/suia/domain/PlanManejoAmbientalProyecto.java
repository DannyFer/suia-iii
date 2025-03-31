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
@Table(name = "scdr_environmental_management_projects", catalog = "", schema = "suia_iii")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "enmp_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "enmp_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "enmp_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "enmp_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "enmp_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "enmp_status = 'TRUE'")


public class PlanManejoAmbientalProyecto extends EntidadAuditable implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1341855400865720424L;
		
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@Column(name = "enmp_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "enmp_proposed_measures")
	private String medidasPropuestas;
	    
	@Getter
	@Setter
	@Column(name = "enmp_indicator")
	private String indicador;
	
	@Getter
	@Setter
	@Column(name = "enmp_means_verification")
	private String verificacionMedios;
	
	@Getter
	@Setter
	@Column(name = "enmp_frequency")
	private String frecuencia;
	
	@Getter
	@Setter
	@Column(name = "enmp_description")
	private String descripcion;
	
	@Getter
	@Setter
	@Column(name = "enmp_calification")
	private Double calificacion;
	
	@Getter
	@Setter
	@Column(name = "scdr_id")
	private Integer perforacionExplorativa;
	
	@Getter
	@Setter
	@Column(name = "enma_parent_id")
	private Integer padreId;
	
	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.EAGER)
	@ForeignKey(name = "fk_environmental_management_projects_nvironmental_management_projects")
	@JoinColumn(name = "enma_id")
	private PlanManejoAmbiental020 planManejoAmbiental020;
	
	
}
