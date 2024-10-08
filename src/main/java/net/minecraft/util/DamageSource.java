package net.minecraft.util;

import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.world.Explosion;

public class DamageSource {
    public static DamageSource inFire = (new DamageSource("inFire")).setFireDamage();
    public static DamageSource lightningBolt = new DamageSource("lightningBolt");
    public static DamageSource onFire = (new DamageSource("onFire")).setDamageBypassesArmor().setFireDamage();
    public static DamageSource lava = (new DamageSource("lava")).setFireDamage();
    public static DamageSource inWall = (new DamageSource("inWall")).setDamageBypassesArmor();
    public static DamageSource drown = (new DamageSource("drown")).setDamageBypassesArmor();
    public static DamageSource starve = (new DamageSource("starve")).setDamageBypassesArmor().setDamageIsAbsolute();
    public static DamageSource cactus = new DamageSource("cactus");
    public static DamageSource fall = (new DamageSource("fall")).setDamageBypassesArmor();
    public static DamageSource outOfWorld = (new DamageSource("outOfWorld")).setDamageBypassesArmor().setDamageAllowedInCreativeMode();
    public static DamageSource generic = (new DamageSource("generic")).setDamageBypassesArmor();
    public static DamageSource magic = (new DamageSource("magic")).setDamageBypassesArmor().setMagicDamage();
    public static DamageSource wither = (new DamageSource("wither")).setDamageBypassesArmor();
    public static DamageSource anvil = new DamageSource("anvil");
    public static DamageSource fallingBlock = new DamageSource("fallingBlock");
    /**
     * -- GETTER --
     *  Return the name of damage type.
     */
    @Getter
    public String damageType;
    /**
     * This kind of damage can be blocked or not.
     */
    private boolean isUnblockable;
    private boolean isDamageAllowedInCreativeMode;
    /**
     * Whether or not the damage ignores modification by potion effects or enchantments.
     */
    private boolean damageIsAbsolute;
    /**
     * -- GETTER --
     *  How much satiate(food) is consumed by this DamageSource
     */
    @Getter
    private float hungerDamage = 0.3F;
    /**
     * This kind of damage is based on fire or not.
     * -- GETTER --
     *  Returns true if the damage is fire based.

     */
    @Getter
    private boolean fireDamage;
    /**
     * This kind of damage is based on a projectile or not.
     * -- GETTER --
     *  Returns true if the damage is projectile based.

     */
    @Getter
    private boolean projectile;
    /**
     * Whether this damage source will have its damage amount scaled based on the current difficulty.
     * -- GETTER --
     *  Return whether this damage source will have its damage amount scaled based on the current difficulty.

     */
    @Getter
    private boolean difficultyScaled;
    /**
     * Whether the damage is magic based.
     * -- GETTER --
     *  Returns true if the damage is magic based.

     */
    @Getter
    private boolean magicDamage;
    @Getter
    private boolean explosion;

    protected DamageSource(String damageTypeIn) {
        this.damageType = damageTypeIn;
    }

    public static DamageSource causeMobDamage(EntityLivingBase mob) {
        return new EntityDamageSource("mob", mob);
    }

    /**
     * returns an EntityDamageSource of type player
     */
    public static DamageSource causePlayerDamage(EntityPlayer player) {
        return new EntityDamageSource("player", player);
    }

    /**
     * returns EntityDamageSourceIndirect of an arrow
     *
     * @param indirectEntityIn The entity that shoot the arrow
     */
    public static DamageSource causeArrowDamage(EntityArrow arrow, Entity indirectEntityIn) {
        return (new EntityDamageSourceIndirect("arrow", arrow, indirectEntityIn)).setProjectile();
    }

    /**
     * returns EntityDamageSourceIndirect of a fireball
     *
     * @param indirectEntityIn The entity that shoot the fireball
     */
    public static DamageSource causeFireballDamage(EntityFireball fireball, Entity indirectEntityIn) {
        return indirectEntityIn == null ? (new EntityDamageSourceIndirect("onFire", fireball, fireball)).setFireDamage().setProjectile() : (new EntityDamageSourceIndirect("fireball", fireball, indirectEntityIn)).setFireDamage().setProjectile();
    }

    public static DamageSource causeThrownDamage(Entity source, Entity indirectEntityIn) {
        return (new EntityDamageSourceIndirect("thrown", source, indirectEntityIn)).setProjectile();
    }

    public static DamageSource causeIndirectMagicDamage(Entity source, Entity indirectEntityIn) {
        return (new EntityDamageSourceIndirect("indirectMagic", source, indirectEntityIn)).setDamageBypassesArmor().setMagicDamage();
    }

    /**
     * Returns the EntityDamageSource of the Thorns enchantment
     *
     * @param source The Entity that wears the armor with thorn
     */
    public static DamageSource causeThornsDamage(Entity source) {
        return (new EntityDamageSource("thorns", source)).setIsThornsDamage().setMagicDamage();
    }

    public static DamageSource setExplosionSource(Explosion explosionIn) {
        return explosionIn != null && explosionIn.getExplosivePlacedBy() != null ? (new EntityDamageSource("explosion.player", explosionIn.getExplosivePlacedBy())).setDifficultyScaled().setExplosion() : (new DamageSource("explosion")).setDifficultyScaled().setExplosion();
    }

    /**
     * Define the damage type as projectile based.
     */
    public DamageSource setProjectile() {
        this.projectile = true;
        return this;
    }

    public DamageSource setExplosion() {
        this.explosion = true;
        return this;
    }

    public boolean isUnblockable() {
        return this.isUnblockable;
    }

    public boolean canHarmInCreative() {
        return this.isDamageAllowedInCreativeMode;
    }

    /**
     * Whether or not the damage ignores modification by potion effects or enchantments.
     */
    public boolean isDamageAbsolute() {
        return this.damageIsAbsolute;
    }

    public Entity getSourceOfDamage() {
        return this.getEntity();
    }

    public Entity getEntity() {
        return null;
    }

    protected DamageSource setDamageBypassesArmor() {
        this.isUnblockable = true;
        this.hungerDamage = 0.0F;
        return this;
    }

    protected DamageSource setDamageAllowedInCreativeMode() {
        this.isDamageAllowedInCreativeMode = true;
        return this;
    }

    /**
     * Sets a value indicating whether the damage is absolute (ignores modification by potion effects or enchantments),
     * and also clears out hunger damage.
     */
    protected DamageSource setDamageIsAbsolute() {
        this.damageIsAbsolute = true;
        this.hungerDamage = 0.0F;
        return this;
    }

    /**
     * Define the damage type as fire based.
     */
    protected DamageSource setFireDamage() {
        this.fireDamage = true;
        return this;
    }

    /**
     * Gets the death message that is displayed when the player dies
     *
     * @param entityLivingBaseIn The EntityLivingBase that died
     */
    public IChatComponent getDeathMessage(EntityLivingBase entityLivingBaseIn) {
        EntityLivingBase entitylivingbase = entityLivingBaseIn.getAttackingEntity();
        String s = "death.attack." + this.damageType;
        String s1 = s + ".player";
        return entitylivingbase != null && StatCollector.canTranslate(s1) ? new ChatComponentTranslation(s1, entityLivingBaseIn.getDisplayName(), entitylivingbase.getDisplayName()) : new ChatComponentTranslation(s, entityLivingBaseIn.getDisplayName());
    }

    /**
     * Set whether this damage source will have its damage amount scaled based on the current difficulty.
     */
    public DamageSource setDifficultyScaled() {
        this.difficultyScaled = true;
        return this;
    }

    /**
     * Define the damage type as magic based.
     */
    public DamageSource setMagicDamage() {
        this.magicDamage = true;
        return this;
    }

    public boolean isCreativePlayer() {
        Entity entity = this.getEntity();
        return entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isCreativeMode;
    }
}
