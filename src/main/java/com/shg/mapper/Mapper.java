package com.shg.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * @author SHG
 */
public final class Mapper {
	public static void map(final Object from, Object to) {
		mapElement(from, to);
	}

	private static void mapElement(final Object from, final Object to) {
		Map<String, Field> fromFields = analyze(from);
		Map<String, Field> toFields = analyze(to);
		fromFields.keySet().retainAll(toFields.keySet());
		for(Entry<String, Field> fromFieldEntry : fromFields.entrySet()) {
			final String name = fromFieldEntry.getKey();
			final Field sourceField = fromFieldEntry.getValue();
			final Field targetField = toFields.get(name);
			if(targetField.getType().isAssignableFrom(sourceField.getType())) {
				sourceField.setAccessible(true);
				if(Modifier.isFinal(targetField.getModifiers()))
					continue;
				targetField.setAccessible(true);
				try {
					targetField.set(to, sourceField.get(from));
				} catch(IllegalAccessException e) {
					throw new IllegalStateException("¡No puede acceder al campo!");
				}
			}
		}
	}

	private static Map<String,Field> analyze(Object o) {
		if(o == null)
			throw new NullPointerException("No se puede realizar el mapeo de un elemento nulo.");

		Map<String, Field> map = new TreeMap();
		Class<?> c = o.getClass();
		for(Field f : c.getDeclaredFields())
			if(!Modifier.isStatic(f.getModifiers()))
				if(!map.containsKey(f.getName()))
					map.put(f.getName(), f);
		return(map);
	}
}