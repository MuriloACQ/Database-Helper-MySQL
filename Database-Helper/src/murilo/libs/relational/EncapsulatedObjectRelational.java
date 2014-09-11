package murilo.libs.relational;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class EncapsulatedObjectRelational<T extends ObjectRelational> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final T original;
	private T changeable;

	@SuppressWarnings("unchecked")
	public EncapsulatedObjectRelational(T objectRelational) {
		original = objectRelational;
		try {
			changeable = (T) objectRelational.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

	public T get() {
		return changeable;
	}

	public Map<String, String> export() {
		Map<String, String> changes = new HashMap<String, String>();
		Field[] fields = original.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			try {
				if (!original.getFieldValue(fields[i]).equals(
						changeable.getFieldValue(fields[i]))) {
					changes.put(original.getColumnName(fields[i].getName()),
							changeable.getFieldValueAsString(fields[i]));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return changes;
	}

}
