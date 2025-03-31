package ec.gob.ambiente.rcoa.model;

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

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "codes_resolutions", schema = "coa_mae")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "core_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "core_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "core_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "core_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "core_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "core_status = 'TRUE'")
public class CodigosResoluciones extends EntidadAuditable{
	
	@Getter
	@Setter
	@Id
	@Column(name = "core_id")
	@SequenceGenerator(name = "PROJECTS_ENVIRONMENTAL_LICENSING_CORE_ID_GENERATOR", sequenceName = "codes_resolutions_core_id_seq", schema = "coa_mae", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROJECTS_ENVIRONMENTAL_LICENSING_CORE_ID_GENERATOR")
	private Integer id;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "tyre_id")
	@ForeignKey(name = "fk_tyre_id")
	private TipoResolucion tipoResolucion;
	
	@Getter
	@Setter
	@Column(name = "core_incremental")
	private int secuencialResolucion;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "area_id")
	@ForeignKey(name = "fk_area_id")
	private Area areaResponsable;
	
	@Getter
	@Setter
	@Column(name = "core_year")
	private int anios;
	
	@Getter
	@Setter
	@Column(name = "core_observation_bd", length = 255)
	private String observacionDB;
		

}
