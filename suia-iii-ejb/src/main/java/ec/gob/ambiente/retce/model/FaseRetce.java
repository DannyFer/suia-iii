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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the fase database table.
 * 
 */
@Entity
@Table(name="fase", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "fase_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "fase_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "fase_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "fase_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "fase_user_update")) })
@NamedQueries({
	@NamedQuery(name="FaseRetce.findAll", query="SELECT m FROM FaseRetce m")
})
public class FaseRetce extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="fase_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="fase_description")
	private String descripcion;

	@Getter
	@Setter
	@Column(name="fase_orden")
	private Integer orden;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="sety_id")
	private TipoSector tipoSector;
	
	@Getter
	@Setter
	@Column(name="fase_frecuency")
	private String frecuencia;


}