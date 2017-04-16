package org.maxgamer.rs.model.entity.mob.npc;

import org.maxgamer.rs.assets.codec.asset.EncryptedException;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.core.server.WorldFullException;
import org.maxgamer.rs.model.action.WanderAction;
import org.maxgamer.rs.model.entity.Interactable;
import org.maxgamer.rs.model.entity.mob.Bonuses;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.MobModel;
import org.maxgamer.rs.model.entity.mob.MovementUpdate;
import org.maxgamer.rs.model.entity.mob.combat.*;
import org.maxgamer.rs.model.entity.mob.combat.mage.CombatSpell;
import org.maxgamer.rs.model.entity.mob.combat.mage.MagicAttack;
import org.maxgamer.rs.model.entity.mob.npc.loot.Loot;
import org.maxgamer.rs.model.entity.mob.npc.loot.LootItem;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.events.mob.MobMoveEvent;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.ground.GroundItemStack;
import org.maxgamer.rs.model.item.inventory.Equipment;
import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.model.map.path.Direction;
import org.maxgamer.rs.model.map.path.Path;
import org.maxgamer.rs.model.skill.SkillSet;
import org.maxgamer.rs.model.skill.SkillType;
import org.maxgamer.rs.repository.NPCTypeRepository;

import java.util.UUID;

/**
 * @author netherfoam
 */
public class NPC extends Mob implements Interactable {
    private long uuid;

    /**
     * The index in the array of NPC's the server holds, of this NPC
     */
    private short spawnIndex;

    /**
     * The definition for this NPC
     */
    private NPCType definition;

    /**
     * The NPCModel this NPC uses to represent visible information (Head icon,
     * etc)
     */
    private MobModel model;

    /**
     * The location that this NPC spawns at
     */
    private Location spawn;

    private SkillSet skills;

    /**
     * Constructs a new NPC from the given ID. This loads the definition ID from the cache and database. If either fails, a {@link RuntimeException}
     * is thrown. This calls show() on the NPC and sets the location of the NPC to the given location. After calling this constructor, the NPC will be
     * available in the game world, unless the given location is NULL, in which case this will not spawn the NPC into the map. This calls NPC(defId)
     * and then invokes setLocation(). The effect of this constructor is the same as new NPC(defId).teleport(l);.
     *
     * @param defId the definition id for the NPC
     * @throws WorldFullException
     */
    public NPC(int defId, Location l) throws WorldFullException {
        this(defId, (int) UUID.randomUUID().getLeastSignificantBits(), l);
    }

    /**
     * Constructs a new NPC from the given ID. This loads the definition ID from
     * the cache and database. If either fails, a {@link RuntimeException} is
     * thrown. This does not spawn the NPC in the gameworld, you must use
     * setLocation() for that. Once a location is set, it will be visible to
     * players.
     *
     * @param defId the definition id for the NPC
     * @throws WorldFullException
     */
    public NPC(int defId, long spawnId, Location l) throws WorldFullException {
        super(1, 1);
        try {
            this.definition = Core.getServer().getDatabase().getRepository(NPCTypeRepository.class).find(defId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        this.equipment = new Equipment(this);
        this.combatStats = new NPCCombatStats(this);
        this.model = new NPCModel(this.definition);
        this.mask = new NPCUpdateMask(this, new NPCMovementUpdate());
        this.skills = new SkillSet(this);
        this.spawn = l;

        NPCType d = getDefinition();
        this.skills.setLevel(SkillType.ATTACK, d.getAttackLevel());
        this.skills.setLevel(SkillType.STRENGTH, d.getAttackLevel());
        this.skills.setLevel(SkillType.DEFENCE, d.getAttackLevel());
        this.skills.setLevel(SkillType.MAGIC, d.getAttackLevel());
        this.skills.setLevel(SkillType.RANGE, d.getAttackLevel());
        this.uuid = spawnId;

        this.setHealth(getMaxHealth());
        loadId();
        show(); // Technically we still need to await for a location to be set, but we can do this preemptively without hassle.
        setLocation(l);
    }

    /**
     * The unique identifier representing this NPC.  NPC's which are saved after server restarts will have the same UUID.  NPC's which are created
     *
     * @return the unique string representing this NPC.
     */
    public long getSpawnId() {
        return uuid;
    }

    @Override
    public void onDeath() {
        // Cancel our target if we had one
        if (getTarget() != null) {
            setTarget(null);
        }

        // Spawn the loot for this NPC, it has died.
        Location loc = getCenter();

        Mob[] killers = getDamage().getKillers(DamageType.values());
        Persona killer = null;
        for (int i = 0; i < killers.length; i++) {
            if (killers[i] == null) continue;
            if (!(killers[i] instanceof Persona)) continue;
            killer = (Persona) killers[i];
            break;
        }

        for (LootItem item : getLoot().next()) {
            ItemStack stack = item.getItemStack();
            GroundItemStack ground = new GroundItemStack(stack, killer, 50, 500);
            ground.setLocation(loc);
        }
    }

    public NPCGroup getGroup() {
        return this.getDefinition().getGroup();
    }

    public Loot getLoot() {
        return getGroup().getLoot();
    }

    /**
     * The location this NPC is to respawn at. May be null
     *
     * @return The location this NPC is to respawn at. May be null
     */
    public Location getSpawn() {
        return this.spawn;
    }

    /**
     * Sets the location for this NPC to spawn at. This may be null, and will be
     * null on construction. A NPC who has no spawn location will simply die and
     * respawn, without being moved from its death location.
     *
     * @param l the location to set, may be null
     */
    public void setSpawn(Location l) {
        this.spawn = l;
    }

    /**
     * Loads the SpawnIndex for this NPC
     *
     * @throws WorldFullException if the world is full of NPCs
     */
    private void loadId() throws WorldFullException {
        this.spawnIndex = (short) Core.getServer().getNPCs().add(this);
    }

    @Override
    protected void setLocation(Location l) {
        // Method is protected
        super.setLocation(l);
    }

    /**
     * The definition used by this NPC
     *
     * @return The definition used by this NPC
     */
    public NPCType getDefinition() {
        return definition;
    }

    @Override
    public short getSpawnIndex() {
        return spawnIndex;
    }

    /**
     * The Definition ID for this NPC
     *
     * @return
     */
    public int getId() {
        if (model instanceof NPCModel) {
            return ((NPCModel) model).getModelId();
        }
        return definition.getId();
    }

    @Override
    public MobModel getModel() {
        return this.model;
    }

    public void setModel(MobModel model) {
        if (model == null) return;
        this.model = model;
        model.setChanged(true);
    }

    @Override
    public NPCUpdateMask getUpdateMask() {
        return (NPCUpdateMask) super.getUpdateMask();
    }

    @Override
    public void destroy() {
        // Delete ourselves.
        Core.getServer().getNPCs().remove(spawnIndex);
        super.destroy();
    }

    @Override
    public int getClientIndex() {
        return (getSpawnIndex() + 1);
    }

    @Override
    public Attack nextAttack() {
        NPCType d = getDefinition();
        if (d.isMagic()) {
            return new MagicAttack(this, new CombatSpell(1, d.getStartGraphics(), getCombatStats().getAttackAnimation(), d.getAttackDelay(), d.getEndGraphics(), -1, d.getProjectileId(), 8, d.getMagicLevel() * 4, -1));
        }
        if (d.isRange()) {
            return new RangeAttack(this);
        }
        return new MeleeAttack(this);
    }

    @Override
    public int getMaxHealth() {
        return getDefinition().getMaxHealth();
    }

    @Override
    public Mob respawn() {
        Location l = getSpawn();
        if (l != null) {
            // Set the location to the spawn if it is available.
            setLocation(l);
        }

        return super.respawn();
    }

    @Override
    public int getRespawnTicks() {
        return getDefinition().getRespawnDelayTicks();
    }

    @Override
    public boolean move(Path path) {
        MovementUpdate m = this.getUpdateMask().getMovement();
        if (m.hasTeleported()) {
            // Can't move while teleporting
            return false;
        }

        if (m.hasChanged()) {
            // This should not be triggered, but it is. This is the only section
            // of code
            // where we modify MovementUpdate direction. This means somehow,
            // this run()
            // is being called twice, possibly by two WalkAction?
            // It seems to occur when interacting with a gameobject that is
            // immediately
            // next to the player, and then interacting with one that is further
            // away.
            // Eg queuing an empty path, and then queuing a non-empty path.

            /*
             * Wait no, it occurs when the player has to move 1 tile to target
             * then move n tiles to second target in the same tick
             */
            throw new IllegalStateException("Movement update mask has already changed dir " + m.getDirection() + ", tele " + m.hasTeleported() + ", ActionQueue: " + this.getActions().toString());
        }

        if (path != null && !path.isEmpty()) {
            Direction next = path.next();

            int dx = next.dx;
            int dy = next.dy;

            Location dest;
            MobMoveEvent e;
            // TODO: Check the next point in the path is still valid, as the
            // clip in the
            // game world may change (Eg door opens or, more importantly,
            // closes!)

            m.setWalk(next);
            dest = this.getLocation().add(dx, dy);
            e = new MobMoveEvent(this, dest, false);

            e.call();
            if (e.isCancelled()) {
                m.reset();
                return true; // Our path has been cancelled by an event.
            } else {
                try {
                    this.setLocation(dest);
                } catch (RuntimeException ex) {
                    if (ex.getCause() instanceof EncryptedException) {
                        // So if we failed to set the new location due to map
                        // encryption, we shouldn't inform the player that they
                        // moved.
                        m.reset();
                    }
                }
            }

            // We are done walking
// We have area left to traverse.
            return path.isEmpty();
        }

        return true; // Finished
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + getId() + "," + getDefinition().getName() + ",#" + getSpawnIndex() + "]";
    }

    @Override
    public SkillSet getSkills() {
        return skills;
    }

    @Override
    public boolean isAttackable(Mob src) {
        return getDefinition().hasInteraction("Attack");
    }

    @Override
    public void onIdle() {
        if (this.getDefinition().isAggressive()) {
            Persona target = this.getLocation().getClosest(Persona.class, 16);
            if (target != null && target.isAttackable(this)) this.setTarget(target);
            return;
        }
        // This automatically begins processing actions again by calling
        // ActionQueue.queue()
        if (this.getDefinition().canWalk()) {
            getActions().queue(new WanderAction(NPC.this, getLocation(), 4, 10, 30));
        }
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onUnload() {

    }

    @Override
    public AttackStyle getAttackStyle() {
        if (getDefinition().isMagic()) {
            return new AttackStyle(1, "Magic", Bonuses.ATK_MAGIC);
        }
        if (getDefinition().isRange()) {
            return new AttackStyle(1, "Accurate", Bonuses.ATK_RANGE);
        }

        // Find our best bonus type and use it
        int[] options = new int[]{Bonuses.ATK_STAB, Bonuses.ATK_SLASH, Bonuses.ATK_CRUSH};
        String[] names = new String[]{"Stab", "Slash", "Crush"};

        int max = getDefinition().getBonus(options[0]);
        int bestStyle = 0;

        for (int i = 1; i < options.length; i++) {
            if (getDefinition().getBonus(options[i]) > max) {
                bestStyle = i;
                max = getDefinition().getBonus(options[i]);
            }
        }
        return new AttackStyle(bestStyle + 1, names[bestStyle], bestStyle);
    }

    public String getName() {
        return getDefinition().getName();
    }

    public String getOption(int index) {
        return getDefinition().getOption(index);
    }

    @Override
    public boolean hasOption(String option) {
        return getDefinition().hasInteraction(option);
    }

    @Override
    public String[] getOptions() {
        return getDefinition().getInteractions();
    }

    @Override
    public boolean has(ItemStack... items) {
        return false;
    }
}