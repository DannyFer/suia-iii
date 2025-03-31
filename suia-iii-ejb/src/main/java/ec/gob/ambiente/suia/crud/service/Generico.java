package ec.gob.ambiente.suia.crud.service;

import lombok.Getter;


public abstract class Generico<T> {

	@Getter
	private Class<T> clase;

	public Generico(Class<T> clase) {
		this.clase = clase;
	}
}
