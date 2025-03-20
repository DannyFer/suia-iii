package ec.gob.ambiente.rcoa.viabilidadAmbiental.model;

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

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;


@Entity
@Table(name = "information_legal_viability", schema = "coa_viability")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "inle_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "inle_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "inle_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "inle_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "inle_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "inle_status = 'TRUE'")
public class InformacionViabilidadLegal extends EntidadAuditable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2428680527937678962L;
	@Getter
	@Setter
	@Id
	@Column(name = "inle_id")
	@SequenceGenerator(name = "PROJECTS_ENVIRONMENTAL_LICENSING_INLE_ID_GENERATOR", sequenceName = "information_legal_viability_inle_id_seq", schema = "coa_viability", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROJECTS_ENVIRONMENTAL_LICENSING_INLE_ID_GENERATOR")
	private Integer id;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "prvi_id")
	@ForeignKey(name = "fk_prvi_id")
	private ViabilidadCoa viabilidadCoa;
	
	@Getter
	@Setter
	@Column(name = "inle_legal_step")
	private Boolean pasoLegal;
	
	@Getter
	@Setter
	@Column(name = "inle_legal_resolved")
	private Boolean conflictoResuelto;
	
	@Getter
	@Setter
	@Column(name = "inle_legal_description", length = 255)
	private String descripcionJuridico;
	
	@Getter
	@Setter
	@Column(name = "inle_operator_description", length = 255)
	private String descripcionOperador;
	
	@Getter
	@Setter
	@Column(name = "inle_type_legal_operator")
	private Integer tipoLegal;
	
	@Getter
	@Setter
	@Column(name = "inle_observation_bd", length = 255)
	private String observacionDB;
	
	

}
