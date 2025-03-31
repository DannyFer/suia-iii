/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos;

import java.util.ArrayList;
import java.util.List;

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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * <b> Clase que representa a la modalidad incineracion. </b>
 * 
 * @author vero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: vero $, $Date: 10/06/2015 $]
 *          </p>
 */
@Entity
@Table(name = "modality_incineration", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "moin_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "moin_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "moin_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "moin_ureator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "moin_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "moin_status = 'TRUE'")
public class ModalidadIncineracion extends EntidadAuditable {

	/**
	* 
	*/
	private static final long serialVersionUID = -1148646903615828148L;

	@Id
	@Column(name = "moin_id")
	@SequenceGenerator(name = "MODALITY_INCINERATION_GENERATOR", sequenceName = "seq_moin_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MODALITY_INCINERATION_GENERATOR")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@ManyToOne()
	@JoinColumn(name = "moin_map")
	@ForeignKey(name = "fk_modality_incineration_moin_map_documents_docu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	private Documento DocumentoPlano;

	@Getter
	@Setter
	@ManyToOne()
	@JoinColumn(name = "moin_fuel")
	@ForeignKey(name = "fk_modality_incineration_moin_fuel_documents_docu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	private Documento documentoCombustible;

	@Getter
	@Setter
	@ManyToOne()
	@JoinColumn(name = "moin_testing_protocol")
	@ForeignKey(name = "fk_modality_incineration_moin_testing_protocol_documents_docu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	private Documento documentoProtocoloPruebas;

	@Getter
	@Setter
	@ManyToOne()
	@JoinColumn(name = "moin_procedure")
	@ForeignKey(name = "fk_modality_incineration_moin_procedure_documents_docu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	private Documento documentoProcedimiento;

	@Getter
	@Setter
	@ManyToOne()
	@JoinColumn(name = "moin_requirements")
	@ForeignKey(name = "fk_modality_incineration_moin_requirements_documents_docu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	private Documento documentoRequisitos;

	@Getter
	@Setter
	@ManyToOne()
	@JoinColumn(name = "moin_biological_waste_incineration")
	@ForeignKey(name = "fk_modality_incineration_moin_biological_waste_incineration_documents_docu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	private Documento documentoDesechosBiologicosIncineracion;

	@Getter
	@Setter
	@ManyToOne()
	@JoinColumn(name = "moin_biological_waste_incineration_protocol")
	@ForeignKey(name = "fk_modality_incineration_moin_biological_waste_incineration_protocol_documents_docu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	private Documento documentoDesechosBiologicosProtocoloIncineracion;

	@Getter
	@Setter
	@ManyToOne()
	@JoinColumn(name = "moin_test_waste")
	@ForeignKey(name = "fk_modality_incineration_moin_test_waste_documents_docu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	private Documento documentoPruebas;

	@Getter
	@Setter
	@OneToMany(mappedBy = "modalidadIncineracion")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "moiw_status = 'TRUE'")
	private List<ModalidadIncineracionDesecho> modalidadIncineracionDesechos;

	@Getter
	@Setter
	@OneToMany(mappedBy = "modalidadIncineracion")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "miwp_status = 'TRUE'")
	private List<ModalidadIncineracionDesechoProcesar> modalidadIncineracionDesechoProcesados;

	@Getter
	@Setter
	@OneToMany(mappedBy = "modalidadIncineracion")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "moif_status = 'TRUE'")
	private List<ModalidadIncineracionFormulacion> modalidadIncineracionFormulaciones;

	@Getter
	@Setter
	@Column(name = "moin_authorized_company_name")
	private String nombreEmpresaAutorizada;

	@Getter
	@Setter
	@Column(name = "moin_contracted_transport")
	private boolean transporteContratado;

	@Getter
	@Setter
	@Column(name = "moin_own_transport")
	private boolean transportePropio;

	@Getter
	@Setter
	@ManyToOne()
	@JoinColumn(name = "apte_id")
	@ForeignKey(name = "fk_modality_treatment_apte_id_approval_requirements_apte_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "apte_status = 'TRUE'")
	private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;

	@Getter
	@Setter
	@Column(name = "moin_treat_infectious_biological_waste")
	private Boolean trataDesechosBiologicosInfecciosos;

	@Getter
	@Setter
	@Column(name = "moin_moisture_percentage")
	private Double porcentajeHumedad;

	@Getter
	@Setter
	@Column(name = "moin_chlorine_percentage")
	private Double porcentajeCloro;

	@Getter
	@Setter
	@Column(name = "moin_technical_justification")
	private String justificacionTecnica;

	public List<DesechoPeligroso> getDesechosAsociadosModalidad() {
		List<DesechoPeligroso> desechos = new ArrayList<DesechoPeligroso>();
		if (modalidadIncineracionDesechos != null) {
			for (ModalidadIncineracionDesecho desechoModalidad : modalidadIncineracionDesechos) {
				desechos.add(desechoModalidad.getDesecho());
			}
		}
		return desechos;
	}

}
