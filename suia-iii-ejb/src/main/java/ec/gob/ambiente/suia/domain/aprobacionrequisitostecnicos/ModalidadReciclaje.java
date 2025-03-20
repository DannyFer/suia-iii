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
 * <b> Clase que representa a la modalidad reciclaje. </b>
 * 
 * @author vero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: vero $, $Date: 10/06/2015 $]
 *          </p>
 */
@Entity
@Table(name = "modality_recycling", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "morec_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "morec_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "morec_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "modec_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "modec_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "morec_status = 'TRUE'")
public class ModalidadReciclaje extends EntidadAuditable {

	/**
	* 
	*/
	private static final long serialVersionUID = -1148646903615828148L;

	@Id
	@Column(name = "modec_id")
	@SequenceGenerator(name = "MODALITY_RECYCLING_GENERATOR", sequenceName = "seq_modec_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MODALITY_RECYCLING_GENERATOR")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@OneToMany(mappedBy = "modalidadReciclaje")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "twmr_status = 'TRUE'")
	private List<TipoManejoDesechosModalidadReciclaje> tiposManejoDesechosModalidadReciclaje;

	@Getter
	@Setter
	@OneToMany(mappedBy = "modalidadReciclaje")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "morw_status = 'TRUE'")
	private List<DesechoModalidadReciclaje> listaDesechos;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "modec_map")
	@LazyCollection(LazyCollectionOption.FALSE)
	@ForeignKey(name = "fk_modality_recycling_modec_map_documents_docu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	private Documento documentoPlano;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "modec_requirements")
	@LazyCollection(LazyCollectionOption.FALSE)
	@ForeignKey(name = "fk_modality_recycling_modec_requirements_documents_docu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	private Documento DocumentoRequisitos;

	@Column(name = "modec_authorized_company_name")
	@Getter
	@Setter
	private String nombreEmpresaAutorizada;

	@Getter
	@Setter
	@ManyToOne()
	@JoinColumn(name = "apte_id")
	@ForeignKey(name = "fk_modality_recycling_apte_id_approval_requirements_apte_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "apte_status = 'TRUE'")
	private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;

	@Getter
	@Setter
	@Column(name = "modec_contracted_transport")
	private boolean transporteContratado;

	@Getter
	@Setter
	@Column(name = "modec_own_transport")
	private boolean transportePropio;

	public List<DesechoPeligroso> getDesechosAsociadosModalidad() {
		List<DesechoPeligroso> desechos = new ArrayList<DesechoPeligroso>();
		if (listaDesechos != null) {
			for (DesechoModalidadReciclaje desechoModalidad : listaDesechos) {
				desechos.add(desechoModalidad.getDesecho());
			}
		}
		return desechos;
	}

}
