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
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * <b> Clase que representa a la modalidad tratamiento. </b>
 * 
 * @author vero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: vero $, $Date: 10/06/2015 $]
 *          </p>
 */
@Entity
@Table(name = "modality_treatment", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "motr_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "motr_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "motr_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "motr_ureator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "motr_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "motr_status = 'TRUE'")
public class ModalidadTratamiento extends EntidadAuditable {

	/**
	* 
	*/
	private static final long serialVersionUID = -1148646903615828148L;

	@Id
	@Column(name = "motr_id")
	@SequenceGenerator(name = "MODALITY_TREATMENT_GENERATOR", sequenceName = "seq_motr_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MODALITY_TREATMENT_GENERATOR")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@OneToMany(mappedBy = "modalidadTratamiento")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "motw_status = 'TRUE'")
	private List<DesechoModalidadTratamiento> listaDesechos;

	@Getter
	@Setter
	@ManyToOne()
	@JoinColumn(name = "motr_map")
	@ForeignKey(name = "fk_modality_treatment_motr_map_documents_docu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	private Documento DocumentoPlano;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "motr_requirements")
	@LazyCollection(LazyCollectionOption.FALSE)
	@ForeignKey(name = "fk_treatment_treatment_motr_requirements_documents_docu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	private Documento documentoRequisitos;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "motr_requirementsProductProcess")
	@LazyCollection(LazyCollectionOption.FALSE)
	@ForeignKey(name = "fk_modality_treatment_motr_requirements_documents_docu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	private Documento documentoRequisitosProductoProceso;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "motr_biological_waste")
	@LazyCollection(LazyCollectionOption.FALSE)
	@ForeignKey(name = "fk_modality_treatment_motr_biological_waste_documents_docu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	private Documento documentoDesechosBiologicos;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "motr_test")
	@LazyCollection(LazyCollectionOption.FALSE)
	@ForeignKey(name = "fk_modality_treatment_motr_test_waste_documents_docu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	private Documento documentoPruebas;

	@Column(name = "motr_authorized_company_name")
	@Getter
	@Setter
	private String nombreEmpresaAutorizada;

	@Column(name = "motr_contracted_transport")
	@Getter
	@Setter
	private boolean transporteContratado;

	@Column(name = "motr_own_transport")
	@Getter
	@Setter
	private boolean transportePropio;

	@Getter
	@Setter
	@ManyToOne()
	@JoinColumn(name = "apte_id")
	@ForeignKey(name = "fk_modality_treatment_apte_id_approval_requirements_apte_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "apte_status = 'TRUE'")
	private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;

	@Column(name = "motr_treat_infectious_biological_waste")
	@Getter
	@Setter
	private Boolean trataDesechosBiologicosInfecciosos;

	public List<DesechoPeligroso> getDesechosAsociadosModalidad() {
		List<DesechoPeligroso> desechos = new ArrayList<DesechoPeligroso>();
		if (listaDesechos != null) {
			for (DesechoModalidadTratamiento desechoModalidad : listaDesechos) {
				desechos.add(desechoModalidad.getDesecho());
			}
		}
		return desechos;
	}

}
