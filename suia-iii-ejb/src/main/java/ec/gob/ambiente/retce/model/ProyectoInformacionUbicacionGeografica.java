package ec.gob.ambiente.retce.model;

import java.io.Serializable;
import java.util.Date;

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

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
/**
 * The persistent class for the retce.services database table.
 * 
 */
@Entity
@Table(name="project_information_locations", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "pilo_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "pilo_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "pilo_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "pilo_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "pilo_user_update")) })
public class ProyectoInformacionUbicacionGeografica extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="pilo_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "proj_id")
	private Integer idProyecto;

	@Getter
	@Setter
	@Column(name="pilo_order")
	private Integer orden;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "gelo_id")
	@ForeignKey(name = "fk_project_information_locationsgelo_id_geographical_locationsgelo_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "gelo_status = 'TRUE'")
	private UbicacionesGeografica ubicacionesGeografica;
}