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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Filter;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name="laboratory_general", schema="retce")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "lage_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "lage_date_create")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "lage_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "lage_user_create")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "lage_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "lage_status = 'TRUE'")
public class LaboratorioGeneral extends EntidadAuditable implements Serializable{
	
	private static final long serialVersionUID = -7432991399873327906L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="lage_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="lage_ruc")
	private String ruc;
	
	@Getter
	@Setter
	@Column(name="lage_name")
	private String nombre;
	
	@Getter
	@Setter
	@Column(name="lage_registration_number_sae")
	private String resgistroSae;
	
	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="lage_date_registration_validity")
	private Date fechaVigencia;

}
