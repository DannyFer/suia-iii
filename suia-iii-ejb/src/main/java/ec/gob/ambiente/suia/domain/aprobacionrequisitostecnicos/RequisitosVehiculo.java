package ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name = "requirements_vehicle", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "reve_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "reve_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "reve_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "reve_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "reve_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "reve_status = 'TRUE'")
public class RequisitosVehiculo extends EntidadAuditable {

	/**
	* 
	*/
	private static final long serialVersionUID = -9093561499577523378L;

	@Id
	@Column(name = "reve_id")
	@SequenceGenerator(name = "REQUIREMENTS_VEHICLE_GENERATOR", sequenceName = "seq_reve_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REQUIREMENTS_VEHICLE_GENERATOR")
	@Getter
	@Setter
	private Integer id;

	@Column(name = "reve_plate_number", length = 8)
	@Getter
	@Setter
	private String numeroPlaca;

	@Column(name = "reve_engine_number")
	@Getter
	@Setter
	private String numeroMotor;

	@Column(name = "reve_chassis_number")
	@Getter
	@Setter
	private String numeroChasis;

	@Column(name = "reve_class")
	@Getter
	@Setter
	private String clase;

	@Column(name = "reve_type")
	@Getter
	@Setter
	private String tipo;

	@Column(name = "reve_year_manufacture")
	@Getter
	@Setter
	private String anioFabriacacion;

	@Column(name = "reve_cylinder_capacity")
	@Getter
	@Setter
	private String cilindraje;

	@Column(name = "reve_carrying_capacity")
	@Getter
	@Setter
	private double capacidadTransporte;

	@Column(name = "reve_gross_weight_vehicle")
	@Getter
	@Setter
	private String pesoBrutoVehicular;

	@Setter
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "reve_picture_id")
	@LazyCollection(LazyCollectionOption.FALSE)
	@ForeignKey(name = "fk_requirements_vehicle_foto_id_documents_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	private Documento documentoFoto;

	@Setter
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "reve_certificate_technical_inspection_id")
	@LazyCollection(LazyCollectionOption.FALSE)
	@ForeignKey(name = "fk_requirements_vehicle_cert_id_documents_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	private Documento documentoCertificadoInspeccionTecnicaVehicular;

	@Setter
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "reve_certificate_tank_calibration_id")
	@LazyCollection(LazyCollectionOption.FALSE)
	@ForeignKey(name = "fk_requirements_vehicle_cert_tank_calibration_id_documents_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	private Documento documentoCertificadoCalibracionTanque;

	@Setter
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "reve_matricula_id")
	@LazyCollection(LazyCollectionOption.FALSE)
	@ForeignKey(name = "fk_reve_matricula_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	private Documento documentoMatricula;

//	@Getter
//	@Setter
//	@LazyCollection(LazyCollectionOption.FALSE)
//	@OneToMany(mappedBy = "requisitosVehiculo")
//	private List<SustanciaQuimicaPeligrosaTransporte> sustanciaQuimicaPeligrosaTransportes;

	@Getter
	@Setter
	@ManyToOne()
	@JoinColumn(name = "apte_id")
	@ForeignKey(name = "fk_requirements_vehicle_apte_id_approval_requirements_apte_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "apte_status = 'TRUE'")
	private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof RequisitosVehiculo)) {
			return false;
		}
		RequisitosVehiculo other = (RequisitosVehiculo) obj;
		if (((this.numeroPlaca == null) && (other.numeroPlaca != null))
				|| ((this.numeroPlaca != null) && !this.numeroPlaca.equals(other.numeroPlaca))) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.numeroPlaca;
	}

	public Documento getDocumentoMatricula() {

		if (this.documentoMatricula == null) {
			return new Documento();
		} else {
			return this.documentoMatricula;
		}
	}

	public Documento getDocumentoCertificadoCalibracionTanque() {

		if (this.documentoCertificadoCalibracionTanque == null) {
			return new Documento();
		} else {
			return this.documentoCertificadoCalibracionTanque;
		}
	}

	public Documento getDocumentoCertificadoInspeccionTecnicaVehicular() {

		if (this.documentoCertificadoInspeccionTecnicaVehicular == null) {
			return new Documento();
		} else {
			return this.documentoCertificadoInspeccionTecnicaVehicular;
		}
	}

	public Documento getDocumentoFoto() {

		if (this.documentoFoto == null) {
			return new Documento();
		} else {
			return this.documentoFoto;
		}
	}

}
