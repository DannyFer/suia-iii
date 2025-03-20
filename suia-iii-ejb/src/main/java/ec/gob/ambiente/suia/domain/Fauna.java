/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import java.util.Collection;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import ec.gob.ambiente.suia.dto.EntityAdjunto;
import javax.persistence.Transient;

/**
 * 
 * @author christian
 */
@Entity
@Table(name = "fauna", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "faun_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "faun_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "faun_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "faun_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "faun_user_update")) })
@NamedQueries({ @NamedQuery(name = Fauna.BUSCAR_POR_EIA_GRUPO_TAXONOMICO, query = "SELECT f FROM Fauna f WHERE f.estudioImpactoAmbiental = :estudioImpactoAmbiental AND f.catalogoGruposTaxonomicos = :catalogoGruposTaxonomicos") })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "faun_status = 'TRUE'")
public class Fauna extends EntidadAuditable {

	private static final long serialVersionUID = 5020093786660229055L;
	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String BUSCAR_POR_EIA_GRUPO_TAXONOMICO = PAQUETE + "Fauna.findByEiaAndGrupoTaxonomico";

	@Id
	@Basic(optional = false)
	@Getter
	@Setter
	@SequenceGenerator(name = "FAUNA_ID_GENERATOR", initialValue = 1, sequenceName = "seq_faun_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FAUNA_ID_GENERATOR")
	@Column(name = "faun_id", nullable = false)
	private Integer id;
	@Getter
	@Setter
	@JoinColumn(name = "geca_taxonomic_groups_id", referencedColumnName = "geca_id")
	@ForeignKey(name = "fk_fauna_geca_taxonomic_groups_id_general_catalogs_geca_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private CatalogoGeneral catalogoGruposTaxonomicos;
	@Getter
	@Setter
	@JoinColumn(name = "geca_zoogeographic_floor_id", referencedColumnName = "geca_id")
	@ForeignKey(name = "fk_fauna_geca_zoogeographic_floor_id_general_catalogs_geca_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private CatalogoGeneral catalogoPisoZoogeografico;
	@Getter
	@Setter
	@JoinColumn(name = "geca_ictiohydrographic_areas_id", referencedColumnName = "geca_id")
	@ForeignKey(name = "fk_fauna_geca_ictiohydrographic_areas_id_general_catalogs_geca_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private CatalogoGeneral catalogoZonasIctiohidrograficas;
	@Getter
	@Setter
	@JoinColumn(name = "geca_vegetation_cover_id", referencedColumnName = "geca_id")
	@ForeignKey(name = "fk_fauna_geca_vegetation_cover_id_general_catalogs_geca_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private CatalogoGeneral catalogoCoberturaVegetal;
	@Getter
	@Setter
	@JoinColumn(name = "faun_sampling_rate_id", referencedColumnName = "geca_id")
	@ForeignKey(name = "fk_faun_sampling_rate_id_general_catalogs_geca_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private CatalogoGeneral catalogoTipoMuestreo;
	@Getter
	@Setter
	@Column(name = "faun_sampling_rate_id", insertable = false, updatable = false)
	private Integer idCatalogoTipoMuestro;
	@Getter
	@Setter
	@Column(name = "geca_zoogeographic_floor_id", insertable = false, updatable = false)
	private Integer idCatalogoPisoZoogeografico;
	@Getter
	@Setter
	@Column(name = "geca_ictiohydrographic_areas_id", insertable = false, updatable = false)
	private Integer idCatalogoZonasIctiohidrograficas;
	@Getter
	@Setter
	@Column(name = "geca_vegetation_cover_id", insertable = false, updatable = false)
	private Integer idCatalogoCoberturaVegetal;
	@Getter
	@Setter
	@Column(name = "faun_sampling_effort", length = 100)
	private String esfuerzoMuestreo;
	@Getter
	@Setter
	@OneToMany(mappedBy = "fauna", fetch = FetchType.LAZY)
	private Collection<PuntosMuestreo> puntosMuestreoCollection;
	@Getter
	@Setter
	@OneToMany(mappedBy = "faunId", fetch = FetchType.LAZY)
	private Collection<DetalleFaunaSumaEspecies> detalleFaunaSumaEspeciesCollection;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "eist_id")
	@ForeignKey(name = "fk_environmental_impact_studieseist_id_faunaeist_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "eist_status = 'TRUE'")
	@Getter
	@Setter
	private EstudioImpactoAmbiental estudioImpactoAmbiental;

	@Getter
	@Setter
	@Transient
	private EntityAdjunto adjunto;

	public Fauna() {
		super();
	}

	public Fauna(Integer id) {
		super();
		this.id = id;
	}
}
