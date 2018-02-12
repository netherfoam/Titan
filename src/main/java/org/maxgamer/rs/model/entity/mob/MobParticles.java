package org.maxgamer.rs.model.entity.mob;

import org.maxgamer.rs.structure.YMLSerializable;
import org.maxgamer.rs.structure.configs.ConfigSection;
import org.maxgamer.rs.structure.configs.MutableConfig;

/**
 * @author netherfoam
 */
public class MobParticles implements YMLSerializable {
    private int red;
    private int green;
    private int blue;
    private int ambient;
    private int intensity;

    public MobParticles(int red, int green, int blue, int ambient, int intensity) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.ambient = ambient;
        this.intensity = intensity;
    }

    public MobParticles(ConfigSection contents) {
        this.deserialize(contents);
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    public int getAmbient() {
        return ambient;
    }

    public int getIntensity() {
        return intensity;
    }

    @Override
    public ConfigSection serialize() {
        MutableConfig map = new MutableConfig();

        map.set("red", this.red);
        map.set("green", this.green);
        map.set("blue", this.blue);
        map.set("ambient", this.ambient);
        map.set("intensity", this.intensity);

        return map;
    }

    @Override
    public void deserialize(ConfigSection map) {
        this.red = map.getInt("red", this.red);
        this.green = map.getInt("green", this.green);
        this.blue = map.getInt("blue", this.blue);
        this.ambient = map.getInt("ambient", this.ambient);
        this.intensity = map.getInt("intensity", this.intensity);
    }
}
