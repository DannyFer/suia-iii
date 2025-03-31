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
 * <b> Clase que representa a la modalidad reuso. </b>
 * 
 * @author vero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: vero $, $Date: 10/06/2015 $]
 *          </p>
 */
@Entity
@Table(name = "modality_reuse", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "moreu_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "moreu_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "moreu_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "moreu_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "moreu_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "moreu_status = 'TRUE'")
public class ModalidadReuso extends EntidadAuditable {

	/**
	* 
	*/
	private static final long serialVersionUID = -1148646903615828148L;

	@Id
	@Column(name = "moreu_id")
	@SequenceGenerator(name = "MODALITY_REUSE_GENERATOR", sequenceName = "seq_moreu_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MODALITY_REUSE_GENERATOR")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@OneToMany(mappedBy = "modalidadReuso")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "twmru_status = 'TRUE'")
	private List<TipoManejoDesechosModalidadReuso> tiposManejoDesechosModalidadReuso;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "moreu_map")
	@LazyCollection(LazyCollectionOption.FALSE)
	@ForeignKey(name = "fk_modality_recycling_moreu_map_documents_docu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	private Documento documentoPlano;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "moreu_requirements")
	@LazyCollection(LazyCollectionOption.FALSE)
	@ForeignKey(name = "fk_modality_recycling_moreu_requirements_documents_docu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	private Documento documentoRequisitos;

	@Column(name = "moreu_authorized_company_name")
	@Getter
	@Setter
	private String nombreEmpresaAutorizada;

	@Getter
	@Setter
	@Column(name = "moreu_contracted_transport")
	private boolean transporteContratado;

	@Getter
	@Setter
	@Column(name = "moreu_own_transport")
	private boolean transportePropio;

	@Getter
	@Setter
	@ManyToOne()
	@JoinColumn(name = "apte_id")
	@ForeignKey(name = "fk_modality_reuse_apte_id_approval_requirements_apte_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "apte_status = 'TRUE'")
	private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;

	@Getter
	@Setter
	@OneToMany(mappedBy = "modalidadReuso")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "morw_status = 'TRUE'")
	private List<DesechoModalidadReuso> listaDesechos;

	public List<DesechoPeligroso> getDesechosAsociadosModalidad() {
		List<DesechoPeligroso> desechos = new ArrayList<DesechoPeligroso>();
		if (listaDesechos != null) {
			for (DesechoModalidadReuso desechoModalidad : listaDesechos) {
				desechos.add(desechoModalidad.getDesecho());
			}
		}
		return desechos;
	}

}
