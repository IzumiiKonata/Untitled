package net.optifine.shaders.config;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.src.Config;
import net.optifine.shaders.Shaders;
import net.optifine.util.StrUtils;

import java.util.Arrays;
import java.util.List;

public abstract class ShaderOption {
    public static final String COLOR_GREEN = "\u00a7a";
    public static final String COLOR_RED = "\u00a7c";
    public static final String COLOR_BLUE = "\u00a79";
    @Getter
    private String name = null;
    @Getter
    @Setter
    private String description = null;
    @Getter
    private String value = null;
    private String[] values = null;
    @Getter
    private String valueDefault = null;
    @Getter
    private String[] paths = null;
    @Getter
    @Setter
    private boolean enabled = true;
    @Getter
    @Setter
    private boolean visible = true;

    public ShaderOption(String name, String description, String value, String[] values, String valueDefault, String path) {
        this.name = name;
        this.description = description;
        this.value = value;
        this.values = values;
        this.valueDefault = valueDefault;

        if (path != null) {
            this.paths = new String[]{path};
        }
    }

    private static int getIndex(String str, String[] strs) {
        for (int i = 0; i < strs.length; ++i) {
            String s = strs[i];

            if (s.equals(str)) {
                return i;
            }
        }

        return -1;
    }

    public String getDescriptionText() {
        String s = Config.normalize(this.description);
        s = StrUtils.removePrefix(s, "//");
        s = Shaders.translate("option." + this.getName() + ".comment", s);
        return s;
    }

    public boolean setValue(String value) {
        int i = getIndex(value, this.values);

        if (i < 0) {
            return false;
        } else {
            this.value = value;
            return true;
        }
    }

    public void resetValue() {
        this.value = this.valueDefault;
    }

    public void nextValue() {
        int i = getIndex(this.value, this.values);

        if (i >= 0) {
            i = (i + 1) % this.values.length;
            this.value = this.values[i];
        }
    }

    public void prevValue() {
        int i = getIndex(this.value, this.values);

        if (i >= 0) {
            i = (i - 1 + this.values.length) % this.values.length;
            this.value = this.values[i];
        }
    }

    public void addPaths(String[] newPaths) {
        List<String> list = Arrays.asList(this.paths);

        for (String s : newPaths) {
            if (!list.contains(s)) {
                this.paths = (String[]) Config.addObjectToArray(this.paths, s);
            }
        }
    }

    public boolean isChanged() {
        return !Config.equals(this.value, this.valueDefault);
    }

    public boolean isValidValue(String val) {
        return getIndex(val, this.values) >= 0;
    }

    public String getNameText() {
        return Shaders.translate("option." + this.name, this.name);
    }

    public String getValueText(String val) {
        return Shaders.translate("value." + this.name + "." + val, val);
    }

    public String getValueColor(String val) {
        return "";
    }

    public boolean matchesLine(String line) {
        return false;
    }

    public boolean checkUsed() {
        return false;
    }

    public boolean isUsedInLine(String line) {
        return false;
    }

    public String getSourceLine() {
        return null;
    }

    public String[] getValues() {
        return this.values.clone();
    }

    public float getIndexNormalized() {
        if (this.values.length <= 1) {
            return 0.0F;
        } else {
            int i = getIndex(this.value, this.values);

            if (i < 0) {
                return 0.0F;
            } else {
                float f = (float) i / ((float) this.values.length - 1.0F);
                return f;
            }
        }
    }

    public void setIndexNormalized(float f) {
        if (this.values.length > 1) {
            f = Config.limit(f, 0.0F, 1.0F);
            int i = Math.round(f * (float) (this.values.length - 1));
            this.value = this.values[i];
        }
    }

    public String toString() {
        return this.name + ", value: " + this.value + ", valueDefault: " + this.valueDefault + ", paths: " + Config.arrayToString(this.paths);
    }
}
