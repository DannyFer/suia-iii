/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
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

import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * <b> Clase que representa a la modalidad disposición final. </b>
 * 
 * @author Jonathan Guerrero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Jonathan Guerrero $, $Date: 22/06/2015 $]
 *          </p>
 */
@Entity
@Table(name = "modality_final_disposal", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "mofid_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "mofid_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "mofid_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "mofid_ureator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "mofid_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "mofid_status = 'TRUE'")
public class ModalidadDisposicionFinal extends EntidadAuditable {

	/**
	* 
	*/
	private static final long serialVersionUID = -8903221180612147389L;

	@Id
	@Column(name = "mofid_id")
	@SequenceGenerator(name = "MODALITY_FINAL_DISPOSAL_GENERATOR", sequenceName = "seq_mofid_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MODALITY_FINAL_DISPOSAL_GENERATOR")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "mofid_map")
	@ForeignKey(name = "fk_modality_final_disposal_mofid_map_documents_docu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	private Documento planoPlanta;

	@Column(name = "mofid_authorized_company_name")
	@Getter
	@Setter
	private String nombreEmpresaAutorizada;

	@Getter
	@Setter
	@OneToMany(mappedBy = "modalidadDisposicionFinal")
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<DetalleCapacidadConfinamientoDesechoPeligroso> detalleCapacidadConfinamientoDesechoPeligrosos;

	@Getter
	@Setter
	@OneToMany(mappedBy = "modalidadDisposicionFinal")
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<DetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligroso> detalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligrosos;

	

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "mofid_requirements")
	@ForeignKey(name = "fk_modality_final_disposal_mofid_requirements_documents_docu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "mofid_status = 'TRUE'")
	private Documento requisitos;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "mofid_attached")
	@ForeignKey(name = "fk_modality_final_disposal_mofid_attached_documents_docu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "mofid_status = 'TRUE'")
	private Documento anexos;
	
	@Getter
	@Setter
	@Column(name = "mofid_contracted_transport")
	private boolean transporteContratado;

	@Getter
	@Setter
	@Column(name = "mofid_own_transport")
	private boolean transportePropio;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "apte_id")
	@ForeignKey(name = "fk_modality_final_disposal_apte_id_approval_requirements_apte_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "apte_status = 'TRUE'")
	private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;
}
