package ec.gob.ambiente.rcoa.registroGeneradorDesechos.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import ec.gob.ambiente.rcoa.model.CatalogoCIUU;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;

/**
 * The persistent class for the waste_ciuu database table.
 * 
 */
@Entity
@Table(name = "waste_ciuu", schema = "coa_waste_generator_record")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "waci_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "waci_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "waci_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "waci_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "waci_user_update")) })
@NamedQuery(name = "ActividadDesecho.findAll", query = "SELECT a FROM ActividadDesecho a")
public class ActividadDesecho extends EntidadAuditable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "waci_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "caci_id")
	private CatalogoCIUU catalogoCiiu;

	@Getter
	@Setter
	@Column(name = "waci_economic_activity_four_digit")
	private String actividadEconomicaCuatroDigitos;

	@Getter
	@Setter
	@Column(name = "waci_economic_activity_six_digit")
	private String actividadEconomicaSeisDigitos;

	@Getter
	@Setter
	@Column(name = "waci_four_digit_code")
	private String codigoCuatroDigitos;

	@Getter
	@Setter
	@Column(name = "waci_scrap_code")
	private String codigoDesecho;

	@Getter
	@Setter
	@Column(name = "waci_scrap_description")
	private String descripcionDesecho;

	@Getter
	@Setter
	@Column(name = "waci_crtib")
	private String crtib;

	@Getter
	@Setter
	@Column(name = "waci_six_digit_code")
	private String codigoSeisDigitos;

	@Getter
	@Setter
	@Column(name = "waci_five_digit_code")
	private String codigoCincoDigitos;

	@Getter
	@Setter
	@Column(name = "waci_economic_activity_five_digit")
	private String actividadEconomicaCincoDigitos;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "wada_id")
	private DesechoPeligroso desechoPeligroso;

	public ActividadDesecho() {
	}

}