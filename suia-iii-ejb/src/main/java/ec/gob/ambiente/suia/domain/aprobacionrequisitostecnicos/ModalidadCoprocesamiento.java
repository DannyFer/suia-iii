/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.*;

import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * <b> Clase que representa a la modalidad coprocesamiento. </b>
 * 
 * @author Jonathan Guerrero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Jonathan Guerrero $, $Date: 30/06/2015 $]
 *          </p>
 */
@Entity
@Table(name = "modality_coprocessing", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "mocop_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "mocop_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "mocop_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "mocop_ureator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "mocop_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "mocop_status = 'TRUE'")
@Proxy( lazy=false )
public class ModalidadCoprocesamiento extends EntidadAuditable {

	/**
     *
     */
	private static final long serialVersionUID = -8903221180612147389L;

	@Id
	@Column(name = "mocop_id")
	@SequenceGenerator(name = "MODALITY_COPROCESSING_GENERATOR", sequenceName = "seq_mocop_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MODALITY_COPROCESSING_GENERATOR")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "mocop_map")
	@ForeignKey(name = "fk_modality_coprocessing_mocop_map_documents_docu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	private Documento planoPlanta;

	@Getter
	@Setter
	@Column(name = "mocop_contracted_transport")
	private boolean transporteContratado;

	@Getter
	@Setter
	@Column(name = "mocop_own_transport")
	private boolean transportePropio;

	@Getter
	@Setter
	@Column(name = "mocop_authorized_company_name")
	private String nombreEmpresaAutorizada;


	@Column(name = "mocop_waste_type_formulation")
	@Getter
	@Setter
	private String tipoDesechoFormulacion;

	@Getter
	@Setter
	@OneToMany(mappedBy = "modalidadCoprocesamiento", fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	//@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "mocw_status = 'TRUE'")
	private List<ModalidadCoprocesamientoDesecho> modalidadCoprocesamientoDesechos;//Hereeee

	@Getter
	@Setter
	@OneToMany(mappedBy = "modalidadCoprocesamiento", fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	//@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "mowp_status = 'TRUE'")
	private List<ModalidadCoprocesamientoDesechoProcesar> modalidadDesechoProcesados;

	@Getter
	@Setter
	@OneToMany(mappedBy = "modalidadCoprocesamiento")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "mocf_status = 'TRUE'")
	private List<ModalidadCoprocesamientoFormulacion> modalidadCoprocesamientoFormulaciones;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "mocop_waste_feed_sys")
	@ForeignKey(name = "fk_modality_coprocessing_mocop_waste_feed_sys_docu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "mofid_status = 'TRUE'")
	private Documento sistemaAlimentacionDesechosYOperacionesActividad;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "mocop_requirements")
	@ForeignKey(name = "fk_modality_coprocessing_mocop_requirements_documents_docu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "mocop_status = 'TRUE'")
	private Documento requisitosCoprocesamientoDesechoPeligroso;

	@Column(name = "mocop_technical_justification")
	@Getter
	@Setter
	private String justificacionTecnica;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "mocop_executive_resume")
	@ForeignKey(name = "fk_modality_coprocessing_mocop_executive_resume_documents_docu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "mocop_status = 'TRUE'")
	private Documento resumenEjecutivoProtocoloPruebas;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "mocop_test_protocol_process")
	@ForeignKey(name = "fk_modality_coprocess_mocop_test_protocol_process_docu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "mocop_status = 'TRUE'")
	private Documento procedimientoProtocoloPrueba;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "mocop_test_protocol_requirements")
	@ForeignKey(name = "fk_modality_coprocess_mocop_test_protocol_requirements_docu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "mocop_status = 'TRUE'")
	private Documento requisitosProtocoloPrueba;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "apte_id")
	@ForeignKey(name = "fk_modality_coprocess_apte_id_approval_requirements_apte_id")
	private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;

	@Getter
	@Setter
	@Column(name = "mocop_incineration_cement_kilns")
	private boolean incineraHornosCementeros;

	public List<DesechoPeligroso> getDesechosAsociadosModalidad() {
		List<DesechoPeligroso> desechos = new ArrayList<DesechoPeligroso>();
		DesechoPeligroso desecho = null;
		if (modalidadCoprocesamientoDesechos != null) {
			for (ModalidadCoprocesamientoDesecho desechoModalidad : modalidadCoprocesamientoDesechos) {
				desecho = desechoModalidad.getDesecho();
				desecho.setId(desechoModalidad.getDesecho().getId());
				desechos.add(desecho);

			}
		}
		return desechos;
	}
}
