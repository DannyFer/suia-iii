package ec.gob.ambiente.rcoa.model;

import java.math.BigDecimal;

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
@Table(name = "coordinates_project_licencing_coa", schema = "coa_mae")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "coor_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "coor_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "coor_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "coor_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "coor_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "coor_status = 'TRUE'")
public class CoordenadasProyecto extends EntidadAuditable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7818158131024582612L;


	@Getter
	@Setter
	@Id
	@Column(name = "coor_id")
	@SequenceGenerator(name = "PROJECTS_ENVIRONMENTAL_LICENSING_COOR_ID_GENERATOR", sequenceName = "coordinates_project_licencing_coa_coor_id_seq", schema = "coa_mae", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROJECTS_ENVIRONMENTAL_LICENSING_COOR_ID_GENERATOR")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "coor_description", length = 255)
	private String descripcionCoordenadas;	
	
	@Getter
	@Setter
	@Column(name = "coor_order")
	private int ordenCoordenada;	
	
	@Getter
	@Setter
	@Column(name = "coor_x")
	private BigDecimal  x;

	@Getter
	@Setter
	@Column(name = "coor_y")
	private BigDecimal  y;
	
	@Getter
	@Setter
	@Column(name = "prsh_type")
	private int tipoCoordenada;
	
	@Getter
	@Setter
	@Column(name = "coor_observation_bd", length = 255)
	private String observacionDB;	
	
	@ManyToOne
	@JoinColumn(name = "prsh_id")
	@ForeignKey(name = "fk_prsh_id")
	@Getter
	@Setter
	private ProyectoLicenciaAmbientalCoaShape proyectoLicenciaAmbientalCoaShape;
	
	@Getter
	@Setter
	@Column(name = "coor_update_number")
	private Integer numeroActualizaciones;
	
	@Getter
	@Setter
	@Column(name = "coor_geographic_area")
	private Integer areaGeografica;
	
}
