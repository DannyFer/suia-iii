/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos;

import ec.gob.ambiente.suia.domain.Documento;
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.SustanciaQuimicaPeligrosa;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * <b> Clase entidad de transporte de sustancias químicas peligrosas. </b>
 * 
 * @author Jonathan Guerrero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Jonathan Guerrero $, $Date: 08/06/2015 $]
 *          </p>
 */
@Entity
@Table(name = "dangerous_chemistry_substances_transportation", schema = "suia_iii")
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "datr_status = 'TRUE'")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "datr_status")) })
@NamedQueries(@NamedQuery(name = SustanciaQuimicaPeligrosaTransporte.LISTAR_POR_APROBACION_REQUISITOS_TECNICOS, query = "SELECT d FROM SustanciaQuimicaPeligrosaTransporte d WHERE d.idAprobacionRequisitosTecnicos = :idAprobacionRequisitosTecnicos AND d.estado = TRUE"))
public class SustanciaQuimicaPeligrosaTransporte extends EntidadBase {

	/**
	* 
	*/
	private static final long serialVersionUID = 6699207686437022843L;

	private static final String PAQUETE_CLASE = "ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.";
	public static final String LISTAR_POR_APROBACION_REQUISITOS_TECNICOS = PAQUETE_CLASE
			+ "obtenerPorAprobacionRequisitosTecnico";

	@Id
	@Column(name = "datr_id")
	@SequenceGenerator(name = "DANGEROUS_CHEMISTRY_SUBSTANCES_TRANSPORTATION_GENERATOR", sequenceName = "seq_datr_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DANGEROUS_CHEMISTRY_SUBSTANCES_TRANSPORTATION_GENERATOR")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "datr_dang_chem_subs_id")
	private SustanciaQuimicaPeligrosa sustanciaQuimicaPeligrosa;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "datr_requirements_vehicle_id")
	private RequisitosVehiculo requisitosVehiculo;

	@Getter
	@Setter
	@ManyToOne()
	@JoinColumn(name = "apte_id")
	@ForeignKey(name = "fk_dangerous_chemistry_substances_transportation_id_approval_requirements_apte_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "apte_status = 'TRUE'")
	private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;

	@Getter
	@Setter
	@Column(name = "apte_id", insertable = false, updatable = false)
	private Integer idAprobacionRequisitosTecnicos;

	@Getter
	@Setter
	@OneToMany(mappedBy = "sustanciaQuimicaPeligrosaTransporte", fetch = FetchType.LAZY)
	private List<SustanciaQuimicaPeligrosaTransporteUbicacionGeografica> sustanciasUbicacion;

	@Getter
	@Setter
	@Column(name = "datr_container_packing_type")
	private String tipoEmbalajeEnvase;

	@Getter
	@Setter
	@Column(name = "datr_country")
	private String pais;

	@Getter
	@Setter
	@Column(name = "datr_another_country")
	private boolean otroPais;
	
	@Getter
	@Setter
	@Column(name = "datr_national_destination")
	private boolean destinoNivelNacional;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "gelo_id")
	private UbicacionesGeografica provinciaOrigen;

	@Setter
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "datr_manual_id")
	@LazyCollection(LazyCollectionOption.FALSE)
	@ForeignKey(name = "fk_dangerous_chemistry_substances_transportation_manual_id_documents_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	private Documento documentoManualOperaciones;

	public Documento getDocumentoManualOperaciones() {

		if (this.documentoManualOperaciones == null) {
			return new Documento();
		} else {
			return this.documentoManualOperaciones;
		}
	}

	@Getter
	@Setter
	@Transient
	private int indice;

}
