package com.shg.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeMap;

/**
 * @author SHG
 */
public final class Mapper{

	public static void mapList(final List<Object> from, List<Object> to) {
		if (Objects.isNull(from))
			throw new NullPointerException("No se puede realizar el mapeo de una lista nula.");
		to = new ArrayList();

		for (Object o : from) {
			Object toObject = new Object();
			map(o, toObject);
			to.add(toObject);
		}
	}

	public static void map(final Object from, final Object to) {
		Map<String, Field> fromFields = analyze(from);
		Map<String, Field> toFields = analyze(to);
		fromFields.keySet().retainAll(toFields.keySet());
		for(Entry<String, Field> fFieldEntry:fromFields.entrySet()) {
			final String name = fFieldEntry.getKey();
			final Field sourceField = fFieldEntry.getValue();
			final Field targerField = toFields.get(name);
			if(targerField.getType().isAssignableFrom(sourceField.getType())) {
				sourceField.setAccessible(true);
				if(Modifier.isFinal(targerField.getModifiers()))
					continue;
				targerField.setAccessible(true);
				try {
					targerField.set(to, sourceField.get(from));
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
		for(Field f : c.getDeclaredFields()) {
			if(!Modifier.isStatic(f.getModifiers()))
				if(!map.containsKey(f.getName()))
					map.put(f.getName(), f);
		}
		return(map);
	}
}