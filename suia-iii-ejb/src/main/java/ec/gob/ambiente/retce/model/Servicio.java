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
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
/**
 * The persistent class for the retce.services database table.
 * 
 */
@Entity
@Table(name="services", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "ser_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "ser_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "ser_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "ser_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "ser_user_update")) })
public class Servicio extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="ser_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name="ser_description")
	private String descripcion;
	
	@Getter
	@Setter
	@Column(name="ser_order")
	private Integer orden;	
	
	@Getter
	@Setter
	@Column(name="ser_param")
	private String parametro;
		
	@Getter
	@Setter
	@Transient
	//@ManyToOne
	//@JoinColumn(name="peri_id")
	private CatalogoPeriodicidad catalogoPeriodicidad;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="serv_id")
	private CatalogoServicios catalogoServicios;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="proj_id")
	private InformacionProyecto informacionProyecto;

}