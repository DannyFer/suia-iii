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
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;


@Entity
@Table(name = "project_licencing_coa_ciuu_trd", schema = "coa_mae")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "plct_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "plct_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "plct_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "plct_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "plct_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "plct_status = 'TRUE'")
public class ProyectoLicenciaCoaCiiuResiduos  extends EntidadAuditable {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="plct_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="plct_wada_key")
	private String clave;

	@Getter
	@Setter
	@Column(name="plct_wada_description")
	private String descripcion;

	@Getter
	@Setter
	@Column(name="plct_annual_amount")
	private BigDecimal cantidadAnual;

	@ManyToOne
	@JoinColumn(name = "prli_id")
	@ForeignKey(name = "fk_prli_id")
	@Getter
	@Setter
	private ProyectoLicenciaCuaCiuu proyectoLicenciaCuaCiuu;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "wada_id")	
	private DesechoPeligroso desechoPeligroso;

	@Getter
	@Setter
	@Transient
	private Boolean residuoSeleccionado = false;

}