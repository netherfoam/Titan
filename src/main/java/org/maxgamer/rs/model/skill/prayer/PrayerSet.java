package org.maxgamer.rs.model.skill.prayer;

import org.maxgamer.rs.core.tick.Tickable;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.events.mob.persona.PrayerToggleEvent;
import org.maxgamer.rs.model.skill.SkillType;
import org.maxgamer.rs.structure.YMLSerializable;
import org.maxgamer.rs.structure.configs.ConfigSection;
import org.maxgamer.rs.structure.configs.MutableConfig;

import java.util.HashMap;
import java.util.HashSet;

/**
 * @author netherfoam, alva
 */
public class PrayerSet implements YMLSerializable {
    /**
     * The owner of these prayers
     */
    private Persona p;
    /**
     * True if this is ancient prayers, false if it is modern prayers
     */
    private boolean ancientPrayers;
    /**
     * True if the player is using Quick Prayers, false if not
     */
    private boolean usingQuickPrayers;
    /**
     * True if the player is selecting Quick Prayers, false if not
     */
    private boolean selectingQuickPrayers;
    /**
     * The ticker for draining prayers. If this is null, then prayers should be
     * empty. Otherwise, if prayers is not empty, this should not be null.
     */
    private DrainTick drainTicker = null;
    /**
     * The currently active prayers for this Persona
     */
    private HashSet<PrayerType> active = new HashSet<>();
    /**
     * The quick prayers for this Persona
     */
    private HashSet<PrayerType> quickPrayers = new HashSet<>();
    /**
     * The current skill bonuses for the active prayer set. These are cached and
     * are set whenever setEnabled() is called.
     */
    private HashMap<SkillType, Double> bonuses = new HashMap<>(5);

    /**
     * Constructs a new Prayer object for the given Persona
     *
     * @param p the owner of this prayer set
     */
    public PrayerSet(Persona p) {
        if (p == null) {
            throw new NullPointerException("PrayerSet owner may not be null");
        }
        this.p = p;
    }

    /**
     * The owner of this prayer set given in the constructor, not null
     *
     * @return the owner of this prayer set
     */
    public Persona getOwner() {
        return this.p;
    }

    /**
     * The Prayer book currently used.
     *
     * @return ConfigValue 1: ancient prayers; 0: normal prayers
     */
    public int getPrayerBook() {
        return (ancientPrayers ? 1 : 0);
    }

    /**
     * Is the persona using ancient prayers
     *
     * @return using ancient prayers true / false
     */
    public boolean isAncient() {
        return ancientPrayers;
    }

    /**
     * Is the player selecting quick prayers.
     *
     * @return selecting true if not false.
     */
    public boolean isSelectingQuickPrayers() {
        return selectingQuickPrayers;
    }

    /**
     * Checks whether persona has the prayer level required to use the prayer
     * clicked for both books.
     *
     * @param player to send a message if prayer level is too low
     * @param slotid the slotID/prayerID clicked.
     * @return boolean if requirement are met or not.
     */
    public boolean passReqs(Player player, PrayerType key) {
        if (key == null) {
            return false;
        }
        if (p.getSkills().getLevel(SkillType.PRAYER) < key.getLevelReq()) {
            player.sendMessage("You need a prayer level of at least " + key.getLevelReq() + " to use this prayer.");
            return false;
        }
        return checkPrayer();
    }

    /**
     * Fetches the multiplier for the given skill that this PrayerSet creates.
     * The return value is a multiplier, so 0.95 would suggest there is a 5%
     * decrease, and 1.25 would suggest there is a 25% increase.
     *
     * @param skill the skill to get the modifier for
     * @return the multiplier for the skill.
     */
    public double getMultiplier(SkillType skill) {
        if (skill == null) {
            throw new NullPointerException("Skill may not be null");
        }

        Double d = bonuses.get(skill);
        if (d == null) return 1;
        return d.doubleValue() + 1;
    }

    /**
     * Returns true if the given prayer is enabled
     *
     * @param prayer the prayer
     * @return true if it is enabled, false if it is not
     */
    public boolean isEnabled(PrayerType prayer) {
        if (prayer == null) {
            throw new NullPointerException("Prayer may not be null");
        }

        return active.contains(prayer);
    }

    /**
     * Returns true if the given quick prayer is enabled when selecting
     *
     * @param prayer the prayer
     * @return true if it is enabled, false if it is not
     */
    public boolean isQuick(PrayerType prayer) {
        if (prayer == null) {
            throw new NullPointerException("Prayer may not be null");
        }

        return quickPrayers.contains(prayer);
    }

    /**
     * Enables or disables the given quick prayer in the selection interface.
     *
     * @param prayers the quick prayers to toggle on/off
     * @param enable  true if enabling, false if disabling
     */
    public void setQuick(boolean enable, PrayerType... prayers) {
        for (PrayerType prayer : prayers) {
            if (quickPrayers.contains(prayer) == enable) {
                //Prayer already enabled or disabled
                continue;
            }
            if (enable) {
                //Disable all prayers of that type
                for (PrayerGroup group : PrayerGroup.getGroups(prayer)) {
                    setQuick(false, group.getTypes());
                }
                quickPrayers.add(prayer);
            } else {
                quickPrayers.remove(prayer);
            }
        }
        if (p instanceof Player) {//Informing the client about our enabling and disabling.
            Player player = (Player) p;
            int activeBits = 0;
            for (PrayerType p : quickPrayers) {
                if (p.isAncientPrayer() == this.ancientPrayers) activeBits |= PrayerType.getConfigValue(p);
            }
            player.getProtocol().sendConfig(isAncient() ? 1587 : 1397, activeBits);//Currently active prayers bitset
        }
    }

    /**
     * Enables or disables the given prayer. This method fails if the player has
     * no prayer points left and the prayer is being enabled, but the failure is
     * silent.
     *
     * @param prayers the prayers to toggle on/off
     * @param enable  true if enabling, false if disabling
     */
    public void setEnabled(boolean enable, PrayerType... prayers) {
        if (enable) {
            int points = p.getSkills().getLevel(SkillType.PRAYER, true);
            if (points <= 0) {
                return; //No prayer remaining, silent failure
            }
        }
        for (PrayerType prayer : prayers) {
            if (active.contains(prayer) == enable) {
                //Prayer already enabled or disabled
                continue;
            }

            if (enable) {
                //Disable all prayers of that type
                for (PrayerGroup group : PrayerGroup.getGroups(prayer)) {
                    setEnabled(false, group.getTypes());
                }
                active.add(prayer);
            } else {
                active.remove(prayer);
            }
        }
        if (active.isEmpty()) {
            //We have no prayers active, if we're scheduled to drain prayer, cancel the task.
            if (drainTicker != null) {
                //Core.getServer().getTicker().cancel(drainTicker);
                drainTicker.cancel();
                drainTicker = null;
            }
        } else {
            //We have prayers active now, if we're not scheduled to drain prayer, then schedule it.
            if (drainTicker == null) {
                drainTicker = new DrainTick();
                //Core.getServer().getTicker().submit(1, drainTicker);
                drainTicker.queue(1);
            }
        }
        //TODO: This could be optimized a little
        bonuses.clear();
        for (SkillType skill : new SkillType[]{SkillType.ATTACK, SkillType.STRENGTH, SkillType.DEFENCE, SkillType.RANGE, SkillType.MAGIC}) {
            for (PrayerType prayer : active) {
                double v = prayer.getStatBonus(skill);
                if (bonuses.containsKey(skill)) {
                    bonuses.put(skill, bonuses.get(skill) + v);
                } else {
                    bonuses.put(skill, v);
                }
            }
        }
        //Now we inform the client
        if (p instanceof Player) {
            Player player = (Player) p;
            int activeBits = 0;
            int statOffset = 30;
            int statBits = (statOffset + 0) | ((statOffset + 0) << 6) | ((statOffset + 0) << 12) | ((statOffset + 0) << 18) | ((statOffset + 0) << 24);
            for (PrayerType p : active) {
                if (p.isAncientPrayer() == this.ancientPrayers) activeBits |= PrayerType.getConfigValue(p);
            }
            player.getProtocol().sendConfig(isAncient() ? (isSelectingQuickPrayers() ? 1587 : 1582) : (isSelectingQuickPrayers() ? 1397 : 1395), activeBits);//Currently active prayers bitset
            player.getProtocol().sendConfig(1583, statBits);//stat adjustments
            if (this.active.isEmpty()) {//If nothing is activated, then the prayer orb will be deactivated
                player.getProtocol().sendBConfig(182, 0);
                this.usingQuickPrayers = false;
            }
        }
        p.getModel().setPrayerIcon(getPrayerHeadIcon());
    }

    /**
     * Returns a deep copy of the currently active prayers
     *
     * @return a deep copy of the currently active prayers
     */
    public HashSet<PrayerType> getActivePrayers() {
        return new HashSet<>(active); //Deep copy
    }

    /**
     * Check whether the prayer points are not drained.
     *
     * @return true if not drained, false if drained.
     */
    public boolean checkPrayer() {
        if (p.getSkills().getModifier(SkillType.PRAYER) <= -p.getSkills().getLevel(SkillType.PRAYER)) {
            p.getSkills().setModifier(SkillType.PRAYER, -p.getSkills().getLevel(SkillType.PRAYER));
            if (p instanceof Player) {
                Player player = (Player) p;
                player.sendMessage("You have run out of prayer points. You can recharge at an altar.");
                //TODO: send sound
            }
            return false;
        }
        return true;
    }

    /**
     * Retrieves the value of the prayer headicon according to active prayer(s)
     *
     * @return the headicon value
     */
    public int getPrayerHeadIcon() {
        //TODO: I think this can be done a lot more efficiency & cleanly
        for (PrayerType p : active) {
            int headicon = p.getHeadIcon();
            if (headicon == -1) continue;
            if (!ancientPrayers) {
                //if(PROTECT_FROM_SUMMONING && PROTECT_FROM_???) return PROTECT_FROM_???.getHeadIcon() + 8;
                if (active.contains(PrayerType.PROTECT_FROM_SUMMONING) && active.contains(PrayerType.PROTECT_FROM_MAGIC)) headicon = 10;
                else if (active.contains(PrayerType.PROTECT_FROM_SUMMONING) && active.contains(PrayerType.PROTECT_FROM_MISSILES)) headicon = 9;
                else if (active.contains(PrayerType.PROTECT_FROM_SUMMONING) && active.contains(PrayerType.PROTECT_FROM_MELEE)) headicon = 8;
            } else {
                //if(PROTECT_FROM_SUMMONING && PROTECT_FROM_???) return PROTECT_FROM_???.getHeadIcon() + 4;
                if (active.contains(PrayerType.DEFLECT_SUMMONING) && active.contains(PrayerType.DEFLECT_MAGIC)) headicon = 18;
                else if (active.contains(PrayerType.DEFLECT_SUMMONING) && active.contains(PrayerType.DEFLECT_MISSILES)) headicon = 17;
                else if (active.contains(PrayerType.DEFLECT_SUMMONING) && active.contains(PrayerType.DEFLECT_MELEE)) headicon = 16;
            }
            return headicon;
        }
        return -1;
    }

    /**
     * Toggling the prayer orb button. This means that we are activating the
     * quick prayers or that we are deactivating all prayers.
     */
    public void switchQuickPrayers() {
        if (this.quickPrayers.isEmpty()) { //No quick prayers selected
            return;
        }

        this.usingQuickPrayers = !this.usingQuickPrayers;
        for (PrayerType prayer : this.usingQuickPrayers ? quickPrayers : active) {
            PrayerToggleEvent e = new PrayerToggleEvent(getOwner(), prayer, this.usingQuickPrayers);
            e.call();
            if (e.isCancelled()) {
                continue;
            }
            setEnabled(this.usingQuickPrayers, prayer);
        }

        if (p instanceof Player) {//Informing client about our toggle
            Player player = (Player) p;
            player.getProtocol().sendBConfig(182, usingQuickPrayers ? 1 : 0);//Prayer Orb 'activated'
        }
    }

    /**
     * This is used to enable the quick prayer editing or disable the quick
     * prayer editing.
     *
     * @param on true if enabling editing, false if disabling.
     */
    public void setQuickPrayerEditing(boolean on) {
        if (p instanceof Player) {
            Player player = (Player) p;
            player.getPrayer().selectingQuickPrayers = !player.getPrayer().selectingQuickPrayers;
            player.getProtocol().sendBConfig(181, on ? 1 : 0);//Editing 'interface'is displayed or normal prayerinterface is displayed.
            player.getProtocol().sendBConfig(on ? 168 : 149, 6);//No clue
        }
    }

    /**
     * Switching between normal prayers and ancient prayers/curses.
     */
    public void swapPrayerBook() {
        PrayerType[] prayers = active.toArray(new PrayerType[active.size()]);//Disabling all active prayers before going for the switch.
        setEnabled(false, prayers);//Disabling all the active prayers.
        this.quickPrayers.clear();//We don't want the player to be able to use normal (quick prayers) when on curses book.
        this.ancientPrayers = !this.ancientPrayers;//Here we go, switching the boolean.
        if (p instanceof Player) {//Informing the client about our switch.
            Player player = (Player) p;
            player.getProtocol().sendConfig(1584, getPrayerBook());//Actually switching the interface.
        }
    }

    @Override
    public MutableConfig serialize() {
        //The main config section
        MutableConfig map = new MutableConfig();

        //QuickPrayer subsection
        MutableConfig quick = new MutableConfig(); // Separate section for quick prayers

        //Now we save quick prayers to the subsection. I'm not saving them
        //straight to the main section because we will probably want to save
        //other kinds of data to the main section, and mixing that data with
        //quick prayers would seem weird
        int i = 0;
        for (PrayerType type : this.quickPrayers) {
            quick.set("" + i, type.toString());
            i++;
        }
        //Now we put the sub section in the main section
        map.set("quick", quick);

        //Our values are now stored as something like:
        //player.prayers.quick.##: NAME_OF_PRAYER

        //And we return the main section.
        return map;
    }

    @Override
    public void deserialize(MutableConfig map) {
        //Map is the main config section we created in serialize() first.

        //QuickPrayer subsection, as created in serialize()
        ConfigSection quick = map.getSection("quick");

        //Now we load quick prayers from the subsection. We use
        //PrayerType.valueOf(String) to retrieve the prayer by name.
        //We are effectively loading the section player.prayers.quick.*
        for (String key : quick.keys()) {
            //Note that this will also send packets to the player
            this.setQuick(true, PrayerType.valueOf(quick.getString(key).toUpperCase()));
        }
        //Now we are done!
    }

    private class DrainTick extends Tickable {
        @Override
        public void tick() {
            if (p.isDestroyed()) {
                drainTicker = null;
                return; //Player disconnected without toggling prayers off
            }

            int points = p.getSkills().getLevel(SkillType.PRAYER, true);
            if (points <= 0) {
                //Out of prayer, disable all prayers
                PrayerType[] prayers = active.toArray(new PrayerType[active.size()]);
                setEnabled(false, prayers);

                if (p instanceof Player) { //Out of prayer message
                    p.sendMessage("You have run out of prayer points. You can recharge at an altar.");
                }
                drainTicker = null; //Cancelled task.
            } else {
                double drain = 0;
                for (PrayerType p : active) {
                    drain += p.getDrainRate();
                }
                drain = drain * 0.6;

                if (drain > points) {
                    drain = points; //Minimum of 0 points
                }

                p.getSkills().setModifier(SkillType.PRAYER, p.getSkills().getModifier(SkillType.PRAYER) - drain);

                //We're still draining. keep ticking!
                //Core.getServer().getTicker().submit(1, this);
                this.queue(1);
            }
        }
    }
}
