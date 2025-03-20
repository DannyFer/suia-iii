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
 * <b> Entity que representa la tabla Cuentas. </b>
 * 
 * @author Santiago Flores
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Santiago Flores, Fecha: 29/05/2019]
 *          </p>
 */
@Entity
@Table(name = "account", schema = "payments")
@NamedQueries({
		@NamedQuery(name = Cuentas.LISTAR_TODOS, query = "SELECT c FROM Cuentas c WHERE c.estado= true") })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "acco_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "acco_date_create")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "acco_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "acco_user_creator")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "acco_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "acco_status = 'TRUE'")
public class Cuentas extends EntidadAuditable {

	private static final long serialVersionUID = -1756612644167548934L;

	private static final String PAQUETE_CLASE = "ec.gob.ambiente.suia.recaudaciones.model.Cuentas";
	public static final String LISTAR_TODOS = PAQUETE_CLASE + "listarTodos";

	@Getter
	@Setter
	@Id
	@Column(name = "acco_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "acco_institution")
	private String cuentaInstitucion;

	@Getter
	@Setter
	@Column(name = "acco_services_number")
	private String cuentaNumero;

	@Getter
	@Setter
	@Column(name = "acco_services_type")
	private String cuentaTipoServicio;
	
	@Getter
	@Setter
	@Column(name = "acco_development_code")
	private String codigoDesarrollo;
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Cuentas)) {
			return false;
		}
		Cuentas other = (Cuentas) obj;
		if (((this.id == null) && (other.id != null)) || ((this.id != null) && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return getCuentaTipoServicio();
	}
	
	public Cuentas(){
		
	}
	
	public Cuentas(Integer id){
		this.id=id;
	}

}
