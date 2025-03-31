package ec.gob.ambiente.retce.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;


/**
 * The persistent class for the monitoring_point_characteristics database table.
 * 
 */
@Entity
@Table(name="monitoring_point_characteristics", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "mpch_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "mpch_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "mpch_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "mpch_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "mpch_user_update")) })
public class CaracteristicasPuntoMonitoreo extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="mpch_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="mpch_description")
	private String descripcion;

	@Getter
	@Setter
	@Column(name="mpch_order")
	private Integer orden;

	@Getter
	@Setter
	@Column(name="mpch_param")
	private String parametro;
	
	@Getter
	@Setter
	@ManyToOne
    @JoinColumn(name="sety_id")
	private TipoSector tipoSector;

}