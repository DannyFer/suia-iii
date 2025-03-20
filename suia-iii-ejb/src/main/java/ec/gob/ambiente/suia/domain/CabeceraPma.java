package ec.gob.ambiente.suia.domain;

import java.util.List;

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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ec.gob.ambiente.suia.domain.CatalogoGeneral;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the fapma_head_pma database table.
 * 
 */
@Entity
@Table(name = "fapma_head_pma", schema = "suia_iii")
@NamedQuery(name = "CabeceraPma.findAll", query = "SELECT f FROM CabeceraPma f")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "fahe_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "fahe_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "fahe_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "fahe_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "fahe_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "fahe_status = 'TRUE'")
public class CabeceraPma extends EntidadAuditable {
	private static final long serialVersionUID = 1L;	
	
	public static final String SEQUENCE_CODE = "fapma_head_pma_fahe_id_seq";

	@Id
	@Getter
	@Setter
	@Column(name = "fahe_id")	
	@SequenceGenerator(name = "CATII_FAHE_FAHEID_GENERATOR", sequenceName = "fapma_head_pma_fahe_id_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CATII_FAHE_FAHEID_GENERATOR")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "fahe_objectives")
	private String objetivos;

	@Getter
	@Setter
	@Column(name = "fahe_place")
	private String lugar;

	@Getter
	@Setter
	@Column(name = "fahe_responsible")
	private String responsable;

	// bi-directional many-to-one association to CatiiFapma
	@Getter
	@Setter
	@ManyToOne
	@ForeignKey(name = "catii_fapma_fapma_head_pma_fk")
	@JoinColumn(name = "cafa_id")
	private FichaAmbientalPma fichaAmbientalPma;

	// bi-directional many-to-one association to GeneralCatalog
	@Getter
	@Setter
	@ManyToOne	
	@ForeignKey(name = "general_catalogs_fapma_head_pma_fk")
	@JoinColumn(name = "geca_id")
	private CatalogoGeneral catalogoGeneral;

	// bi-directional many-to-one association to FapmaProgram
	@Getter
	@Setter
	@OneToMany(mappedBy = "cabeceraPma")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "fapr_status = 'TRUE'")
	private List<Programa> programas;

	// bi-directional many-to-one association to FapmaValuedSchedule
//	@Getter
//	@Setter
//	@OneToMany(mappedBy = "cabeceraPma")
//	@LazyCollection(LazyCollectionOption.FALSE)
//	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "fava_status = 'TRUE'")
//	private List<CronogramaValoradoPma> cronogramaValoradoPmas;

	// bi-directional many-to-one association to FapmaDetailPma
	@Getter
	@Setter
	@OneToMany(mappedBy = "cabeceraPma")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "fade_status = 'TRUE'")
	private List<DetalleCabeceraPma> fapmaDetailPmas;

}