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

import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * <b> Clase entidad para el desecho peligrosos para la transportacioon. </b>
 * 
 * @author Javier Lucero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Javier Lucero $, $Date: 19/06/2015 $]
 *          </p>
 */
@Entity
@Table(name = "waste_dangerous_transportation", schema = "suia_iii")
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wadt_status = 'TRUE'")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "wadt_status")) })
@NamedQueries(@NamedQuery(name = DesechoPeligrosoTransporte.LISTAR_POR_APROBACION_REQUISITOS_TECNICOS, query = "SELECT d FROM DesechoPeligrosoTransporte d WHERE d.idAprobacionRequisitosTecnicos = :idAprobacionRequisitosTecnicos AND d.estado = TRUE"))
public class DesechoPeligrosoTransporte extends EntidadBase {

	/**
	* 
	*/
	private static final long serialVersionUID = 1L;
	private static final String PAQUETE_CLASE = "ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.";
	public static final String LISTAR_POR_APROBACION_REQUISITOS_TECNICOS = PAQUETE_CLASE
			+ "obtenerPorAprobacionRequisitosTecnicos";

	@Id
	@Column(name = "wadt_id")
	@SequenceGenerator(name = "WASTE_DANGEROUS_TRANSPORTATION_GENERATOR", sequenceName = "seq_wadt_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WASTE_DANGEROUS_TRANSPORTATION_GENERATOR")
	@Getter
	@Setter
	private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "wada_id")
	@ForeignKey(name = "waste_dangerous_waste_dangerous_transportation_fk")
	@Getter
	@Setter
	private DesechoPeligroso desechoPeligroso;
	
	@Getter
	@Setter
	@Column(name = "wada_id", insertable = false, updatable = false)
	private Integer idDesecho;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "reve_id")
	private RequisitosVehiculo requisitosVehiculo;

	@Getter
	@Setter
	@ManyToOne()
	@JoinColumn(name = "apte_id")
	@ForeignKey(name = "fk_waste_dangerous_transportation_id_approval_requirements_apte_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "apte_status = 'TRUE'")
	private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;

	@Getter
	@Setter
	@Column(name = "apte_id", insertable = false, updatable = false)
	private Integer idAprobacionRequisitosTecnicos;

	@Getter
	@Setter
	@OneToMany(mappedBy = "desechoPeligrosoTransporte", fetch = FetchType.LAZY)
	private List<DesechoPeligrosoTransporteUbicacionGeografica> desechosUbicaciones;

	@Getter
	@Setter
	@Column(name = "wadt_container_packing_type")
	private String tipoEmbalajeEnvase;
	
	@Getter
	@Setter
	@Column(name = "wadt_country")
	private String pais;
	
	@Getter
	@Setter
	@Column(name = "wadt_another_country")
	private boolean otroPais;

	@Getter
	@Setter
	@Transient
	private UbicacionesGeografica provinciaDestino;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "wadt_canton_id")
	private UbicacionesGeografica cantonDestino;
	
	@Getter
	@Setter
	@Column(name = "wadt_national_origin")
	private boolean origenNivelNacional;
	
	@Getter
	@Setter
	@Column(name = "wadt_national_destination")
	private boolean destinoNivelNacional;

	@Getter
	@Setter
	@Transient
	private int indice;
	
	@Getter
	@Setter
	@Transient
	private Integer idDesechoEspecial;
	
	

}
