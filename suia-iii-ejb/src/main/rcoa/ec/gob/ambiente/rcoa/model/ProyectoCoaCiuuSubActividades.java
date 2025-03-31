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
import javax.persistence.Table;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "project_licencing_coa_ciuu_subcategories", schema = "coa_mae")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "prls_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "prls_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "prls_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "prls_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "prls_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prls_status = 'TRUE'")
public class ProyectoCoaCiuuSubActividades extends EntidadAuditable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4984247105405578978L;

	@Getter
	@Setter
	@Id
	@Column(name = "prls_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "prli_id")
	@ForeignKey(name = "prli_id__")
	@Getter
	@Setter
	private ProyectoLicenciaCuaCiuu proyectoLicenciaCuaCiuu;
	
	@ManyToOne
	@JoinColumn(name = "cosu_id")
	@ForeignKey(name = "cosu_id")
	@Getter
	@Setter
	private SubActividades subActividad;
	
	@Getter
	@Setter
	@Column(name = "prls_answer")
	private Boolean respuesta;
}
