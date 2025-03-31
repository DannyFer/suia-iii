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
@Table(name = "nut_status", schema = "payments")
@NamedQueries({
		@NamedQuery(name = EstadosNut.LISTAR_TODOS, query = "SELECT c FROM EstadosNut c WHERE c.estado= true") })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "nuts_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "nuts_date_create")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "nuts_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "nuts_user_create")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "nuts_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "nuts_status = 'TRUE'")
public class EstadosNut extends EntidadAuditable {

	private static final long serialVersionUID = -1756612644167548934L;

	private static final String PAQUETE_CLASE = "ec.gob.ambiente.suia.recaudaciones.model.EstadosNut";
	public static final String LISTAR_TODOS = PAQUETE_CLASE + "listarTodos";

	@Getter
	@Setter
	@Id
	@Column(name = "nuts_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "nuts_description")
	private String descripcion;

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof EstadosNut)) {
			return false;
		}
		EstadosNut other = (EstadosNut) obj;
		if (((this.id == null) && (other.id != null)) || ((this.id != null) && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return getDescripcion();
	}
	
	public EstadosNut(){
		
	}
	public EstadosNut(Integer id){
		this.id=id;
	}

}
