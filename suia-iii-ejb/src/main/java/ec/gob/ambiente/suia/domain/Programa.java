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
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the fapma_programs database table.
 * 
 */
@Entity
@Table(name = "fapma_programs", schema = "suia_iii")
@NamedQuery(name = "Programa.findAll", query = "SELECT f FROM Programa f")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "fapr_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "fapr_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "fapr_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "fapr_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "fapr_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "fapr_status = 'TRUE'")
public class Programa extends EntidadAuditable {
	private static final long serialVersionUID = 1L;

	@Id
	@Getter
	@Setter
	@Column(name = "fapr_id")
	@SequenceGenerator(name = "CATII_FAPR_PROGRAMS_FAPRID_GENERATOR", sequenceName = "catii_fapr_fapr_id_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CATII_FAPR_PROGRAMS_FAPRID_GENERATOR")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "fapr_description")
	private String descripcion;

	// bi-directional many-to-one association to FapmaHeadPma
	@Getter
	@Setter
	@ManyToOne
	@LazyCollection(LazyCollectionOption.FALSE)
	@ForeignKey(name = "fapma_head_pma_fapma_programs_fk")
	@JoinColumn(name = "fahe_id")	
	private CabeceraPma cabeceraPma;

}