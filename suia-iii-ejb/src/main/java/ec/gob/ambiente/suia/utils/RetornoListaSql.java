/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.utils;

import ec.gob.ambiente.suia.exceptions.ServiceException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author christian
 */
public class RetornoListaSql {

    private RetornoListaSql() {
    }

    /**
     * metodo que retorna una lista con datos a traves de un native query para
     * el pojo resultante se debe tener un constructor de tipo String
     *
     * @param listaSql
     * @param clase
     * @throws ServiceException
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static List retornar(List<Object[]> listaSql, Class<?> clase) throws ServiceException {
        List listaRetorno = new ArrayList();
        for (Object[] o : listaSql) {
            Object[] oAux = new Object[o.length];
            Class<?>[] clases = new Class[o.length];
            for (int i = 0; i < o.length; i++) {
                clases[i] = String.class;
                String elemento = "";
                if (o[i] != null) {
                    elemento = String.valueOf(o[i]);
                }
                oAux[i] = String.valueOf(elemento.trim());
            }
            try {
                Constructor<?> c = clase.getConstructor(clases);
                Object s = c.newInstance(oAux);
                listaRetorno.add(s);
            } catch (NoSuchMethodException e) {
                throw new ServiceException(e);
            } catch (InstantiationException e) {
                throw new ServiceException(e);
            } catch (IllegalAccessException e) {
                throw new ServiceException(e);
            } catch (IllegalArgumentException e) {
                throw new ServiceException(e);
            } catch (InvocationTargetException e) {
                throw new ServiceException(e);
            }
        }
        return listaRetorno;
    }
}
