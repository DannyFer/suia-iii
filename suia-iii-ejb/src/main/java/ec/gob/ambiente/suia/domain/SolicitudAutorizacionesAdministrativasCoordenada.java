package ec.gob.ambiente.suia.domain;

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

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * <b> Entity que representa la tabla clousure_plans_documents. </b>
 * 
 * @author frank torres
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Frank Torres, Fecha: 26/01/2015]
 *          </p>
 */
@Entity
@Table(name = "request_administrative_authorizations_geographical_locations", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "ragl_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "ragl_status = 'TRUE'")
public class SolicitudAutorizacionesAdministrativasCoordenada extends
		EntidadBase {

	private static final long serialVersionUID = 5369913803444370498L;

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "SOLICITUAUTORIZACIONESADMINISTRATIVASGEOLOCATIONSRAGL_ID_GENERATOR", sequenceName = "seq_ragl_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SOLICITUAUTORIZACIONESADMINISTRATIVASGEOLOCATIONSRAGL_ID_GENERATOR")
	@Column(name = "ragl_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reaa_id")
	@ForeignKey(name = "fk_request_admin_auth_geo_locregal_id_request_admin_authreaa_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "reaa_status = 'TRUE'")
	private SolicitudAutorizacionesAdministrativas solicitudAutorizacionesAdministrativas;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "gelo_id")
	@ForeignKey(name = "fk_request_admin_auth_geo_locregal_id_geographical_locationsgelo_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "gelo_status = 'TRUE'")
	private UbicacionesGeografica ubicacionesGeografica;

}
