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
 * The persistent class for the fapma_detail_pma database table.
 * 
 */
@Entity
@Table(name = "fapma_detail_pma", schema = "suia_iii")
@NamedQuery(name = "DetalleCabeceraPma.findAll", query = "SELECT f FROM DetalleCabeceraPma f")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "fade_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "fade_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "fade_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "fade_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "fade_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "fade_status = 'TRUE'")
public class DetalleCabeceraPma extends EntidadAuditable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "fade_id")
	@SequenceGenerator(name = "CATII_DECA_FADEID_GENERATOR", sequenceName = "catii_deca_fade_id_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CATII_DECA_FADEID_GENERATOR")
	@Getter
	@Setter
	private Integer id;

	@Column(name = "fade_aspect")
	@Getter
	@Setter
	private String aspecto;

	@Column(name = "fade_impact")
	@Getter
	@Setter
	private String impacto;

	@Column(name = "fade_indicators")
	@Getter
	@Setter
	private String indicadores;

	@Column(name = "fade_measures")
	@Getter
	@Setter
	private String medidas;

	@Column(name = "fade_middle_verification")
	@Getter
	@Setter
	private String verificacionIntermedia;

	@Column(name = "fade_months")
	@Getter
	@Setter
	private Integer meses;

	// bi-directional many-to-one association to FapmaHeadPma
	@ManyToOne
	@LazyCollection(LazyCollectionOption.FALSE)
	@ForeignKey(name = "fapma_head_pma_fapma_detail_pma_fk")
	@JoinColumn(name = "fahe_id")
	@Getter
	@Setter
	private CabeceraPma cabeceraPma;
	
}