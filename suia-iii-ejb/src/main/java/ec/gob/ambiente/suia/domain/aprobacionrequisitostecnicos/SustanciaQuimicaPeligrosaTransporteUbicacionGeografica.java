package ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * <b> Entity que representa la tabla clousure_plans_documents. </b>
 * 
 * @author Jonathan Guerrero
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Jonathan Guerrero, Fecha: 09/06/2015]
 *          </p>
 */
@Entity
@Table(name = "dangerous_chemistry_substances_transportation_locations", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "dtlo_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "dtlo_status = 'TRUE'")
@NamedQueries(@NamedQuery(name = SustanciaQuimicaPeligrosaTransporteUbicacionGeografica.LISTAR_POR_ID_SUSTANCIA, query = "SELECT d FROM SustanciaQuimicaPeligrosaTransporteUbicacionGeografica d WHERE d.idSustanciaQuimicaPeligrosa = :idSustanciaQuimicaPeligrosa AND d.estado = TRUE"))
public class SustanciaQuimicaPeligrosaTransporteUbicacionGeografica extends EntidadBase {

	private static final long serialVersionUID = 1274266446610658774L;
	private static final String PAQUETE_CLASE = "ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.";
	public static final String LISTAR_POR_ID_SUSTANCIA = PAQUETE_CLASE + "obtenerPorIdidSustancia";

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "DANGEROUS_CHEMISTRY_SUBSTANCES_TRANSPORTATION_LOCATIONS_ID_GENERATOR", sequenceName = "seq_dtlo_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DANGEROUS_CHEMISTRY_SUBSTANCES_TRANSPORTATION_LOCATIONS_ID_GENERATOR")
	@Column(name = "dtlo_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "gelo_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "gelo_status = 'TRUE'")
	private UbicacionesGeografica ubicacionesGeografica;
	
	@Getter
	@Setter
	@Column(name = "gelo_id", insertable = false, updatable = false)
	private Integer idUbicacionGeografica;


	@Setter
	@Getter
	@ManyToOne
	@JoinColumn(name = "datr_id")
	private SustanciaQuimicaPeligrosaTransporte sustanciaQuimicaPeligrosaTransporte;
	
	@Getter
	@Setter
	@Column(name = "datr_id", insertable = false, updatable = false)
	private Integer idSustanciaQuimicaPeligrosa;


	@Setter
	@Getter
	@Column(name = "dtlo_is_origin")
	private boolean esOrigen;
	
	@Getter
	@Setter
	@Transient
	private int indice;
	


}
