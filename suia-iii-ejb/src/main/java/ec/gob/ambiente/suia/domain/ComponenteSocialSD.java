package ec.gob.ambiente.suia.domain;

import javax.persistence.*;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the scdr_environmental_record_social_component database table.
 * 
 */
@Entity
@Table(name="scdr_environmental_record_social_component", schema="suia_iii")

@AttributeOverrides({@AttributeOverride(name = "estado", column = @Column(name = "ersc_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "ersc_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "ersc_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "ersc_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "ersc_user_update"))})

@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "ersc_status = 'TRUE'")
public class ComponenteSocialSD extends EntidadAuditable {
	private static final long serialVersionUID = 1L;

	@Id
	@Getter
	@Setter
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ersc_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="ersc_areas_ethnic_groups")
	private String asentamientosGruposEtnicos;

	@Getter
	@Setter
	@Column(name="ersc_communities")
	private String comunidades;

	@Getter
	@Setter
	@Column(name="ersc_cultural_elements")
	private String elementosCulturales;

	@Getter
	@Setter
	@Column(name="ersc_farms")
	private String predios;

	@Getter
	@Setter
	@Column(name="ersc_infrastructure")
	private String infraestructura;

	@Getter
	@Setter
	@Column(name="ersc_populations")
	private String poblaciones;

	@Getter
	@Setter
	@Column(name="ersc_public_health_infrastructure")
	private String infraestructuraSaludPublica;

	@Getter
	@Setter
	@Column(name = "scdr_id")
	private Integer perforacionExplorativa;

	public ComponenteSocialSD() {
	}

	

}