package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the consultants database table.
 * 
 */
@Entity
@Table(name = "consultants", schema = "suia_iii")
@NamedQuery(name = "Consultor.findAll", query = "SELECT c FROM Consultor c where c.usuarioCalificado=0")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "cons_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "cons_date_creation")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "cons_date_modification")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "cons_user_creation")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "cons_user_modification")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "cons_status = 'TRUE'")
public class Consultor extends EntidadAuditable {

	private static final long serialVersionUID = 776730403244678828L;

	public static final String FIND_ALL = "ec.com.magmasoft.business.domain.Consultor.findAll";

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "CONSULTANTS_CONSID_GENERATOR", sequenceName = "SEQ_CONS_ID")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CONSULTANTS_CONSID_GENERATOR")
	@Column(name = "cons_id", unique = true, nullable = false)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "cons_address", length = 2147483647)
	private String direccion;

	@Getter
	@Setter
	@Column(name = "cons_category", length = 1)
	private String categoria;

	@Getter
	@Setter
	@Column(name = "cons_city", length = 255)
	private String ciudad;

	@Getter
	@Setter
	@Column(name = "cons_email", length = 255)
	private String email;

	@Getter
	@Setter
	@Column(name = "cons_name", length = 2147483647)
	private String nombre;

	@Getter
	@Setter
	@Column(name = "cons_observations", length = 2147483647)
	private String observaciones;

	@Getter
	@Setter
	@Column(name = "cons_phone", length = 255)
	private String telefono;

	@Getter
	@Setter
	@Column(name = "cons_record", length = 25)
	private String registro;

	@Getter
	@Setter
	@Column(name = "cons_ruc", length = 13)
	private String ruc;

	@Getter
	@Setter
	@Column(name = "cons_type_consultant", length = 1)
	private String tipoConsultor;

	@Getter
	@Setter
	@Column(name = "user_id")
	private Integer usuario;
	
	@Getter
	@Setter
	@Column(name = "cons_qualified")
	private Integer usuarioCalificado;
	
	@Getter
	@Setter
	@Column(name = "cons_status_certificate")
	private Integer estadoCertificado;  //1=vigente, 2=caducado, 3=suspendido, 4= revocado
}