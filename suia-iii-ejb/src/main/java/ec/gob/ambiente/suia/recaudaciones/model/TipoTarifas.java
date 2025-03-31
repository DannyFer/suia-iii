package ec.gob.ambiente.suia.recaudaciones.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * <b> Entity que representa la tabla Tarifas. </b>
 * 
 * @author Santiago Flores
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Santiago Flores, Fecha: 29/05/2019]
 *          </p>
 */
@Entity
@Table(name = "rates_type", schema = "payments")
@NamedQueries({
		@NamedQuery(name = TipoTarifas.LISTAR_TODOS, query = "SELECT c FROM TipoTarifas c WHERE c.estado= true") })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "raty_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "raty_date_create")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "raty_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "raty_user_create")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "raty_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "raty_status = 'TRUE'")
public class TipoTarifas extends EntidadAuditable {

	private static final long serialVersionUID = -1756612644167548934L;

	private static final String PAQUETE_CLASE = "ec.gob.ambiente.suia.recaudaciones.model.TipoTarifas";
	public static final String LISTAR_TODOS = PAQUETE_CLASE + "listarTodos";

	@Getter
	@Setter
	@Id
	@Column(name = "raty_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "raty_code")
	private String tipoTasasCodigo;

	@Getter
	@Setter
	@Column(name = "raty_description")
	private String tipoTasasDescripcion;

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TipoTarifas)) {
			return false;
		}
		TipoTarifas other = (TipoTarifas) obj;
		if (((this.id == null) && (other.id != null)) || ((this.id != null) && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return getTipoTasasDescripcion();
	}

}
