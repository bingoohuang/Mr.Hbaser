package org.phw.hbaser.valuetype;

import org.phw.core.lang.Clazz;
import org.phw.core.lang.Strings;

public class ValueTypeFactory {
    private static ValueTypable[] vts = new ValueTypable[] {
            new AutoFloatValue(), new AutoNumberValue(),
            new BooleanValue(), new DoubleValue(),
            new FloatValue(), new IntValue(),
            new LongValue(), new ShortValue(),
            new StringValue(), new Base64Value(),
            new MultipleLongValue(), new MultipleIntValue(),
            new IpValue(), new DateValue(), new DayValue(), new MDateValue(),
    };

    private static Class<? extends ValueTypable>[] vtz = new Class[] {
            HexValue.class, Base64Value.class, StringValue.class,
    };

    public static ValueTypable getValueType(String type) {
        String[] subTypes = Strings.split(type, "-", true);
        int subTypesLength = subTypes.length;
        ListedValue listedValue = new ListedValue(type);
        for (String subType : subTypes) {
            ValueTypable vt = matchValueTypeClass(subType);

            if (vt == null) {
                vt = matchValueType(subType);
            }

            if (subTypesLength == 1) {
                return vt;
            }

            if (vt != null) {
                listedValue.addValueTypable(vt);
            }
        }

        listedValue.afterPropertiesSet();
        int size = listedValue.getListSize();
        return size == 0 ? null : size == 1 ? listedValue.get(0) : listedValue;
    }

    private static ValueTypable matchValueType(String type) {
        String valueType = Strings.substringBefore(type, "(");
        for (ValueTypable vt : vts) {
            if (matchValueType(valueType, vt)) {
                return vt;
            }
        }
        return null;
    }

    private static ValueTypable matchValueTypeClass(String type) {
        String valueType = Strings.substringBefore(type, "(");
        String[] args = Strings.split(Strings.substringBetween(type, "(", ")"), ",", false);
        for (Class<? extends ValueTypable> vz : vtz) {
            if (matchValueType(valueType, vz)) {
                ValueTypable vt = Clazz.newInstance(vz);
                if (vt instanceof ArgumentsAppliable) {
                    ((ArgumentsAppliable) vt).apply(args);
                }

                return vt;
            }
        }

        return null;
    }

    private static boolean matchValueType(String valueType, Class<? extends ValueTypable> vz) {
        return vz.getSimpleName().equalsIgnoreCase(valueType + "Value");
    }

    private static boolean matchValueType(String valueType, ValueTypable vt) {
        return matchValueType(valueType, vt.getClass());
    }
}
