package ec.gob.ambiente.suia.exceptions;

import javax.ejb.ApplicationException;

/**
 * <b> Excepcion de servicios generica. </b>
 * 
 * @author rene
 * @version Revision: 1.0
 *          <p>
 *          [Autor: rene, Fecha: Oct 28, 2014]
 *          </p>
 */
@ApplicationException(rollback = true)
public class ServiceException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor por defecto
	 */
	public ServiceException() {
		super();
	}

	/**
	 * Constructor que construye una excepcion a partir de un mensaje de error
	 * 
	 * @param msg
	 *            : mensaje de error
	 */
	public ServiceException(String msg) {
		super(msg);
	}
	
	/**
	 * Constructor que construye una excepcion a partir de un mensaje de error y la causa
	 * 
	 * @param msg
	 *            : mensaje de error
	 */
	public ServiceException(String msg, Throwable t) {
		super(msg, t);
	}

	/**
	 * Constructor que construye una excepcion a partir de otra excepcion
	 * 
	 * @param throwable
	 *            : excepcion
	 */
	public ServiceException(Throwable throwable) {
		super(throwable);
	}

}
