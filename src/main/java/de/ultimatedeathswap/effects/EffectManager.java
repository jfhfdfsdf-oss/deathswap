package de.ultimatedeathswap.effects;

import de.ultimatedeathswap.UltimateDeathSwap;
import de.ultimatedeathswap.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import io.papermc.paper.event.entity.EntityKnockbackEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

public final class EffectManager implements Listener {
    private final UltimateDeathSwap plugin;
    private final GameManager game;
    private final Map<UUID, List<DeathSwapEffect>> assigned = new HashMap<>();
    private final Map<UUID, Long> shieldUntil = new HashMap<>();
    private final Map<UUID, Long> knockbackImmuneUntil = new HashMap<>();
    private final Random random = new Random();
    public EffectManager(UltimateDeathSwap plugin, GameManager game) { this.plugin = plugin; this.game = game; }
    public void reloadSettings() { }
    public void giveThree(Player player) {
        List<DeathSwapEffect> pool = new ArrayList<>(Arrays.asList(DeathSwapEffect.values()));
        Collections.shuffle(pool, random);
        assigned.put(player.getUniqueId(), new ArrayList<>(pool.subList(0, 3)));
        player.sendMessage("§dDu hast 3 neue Effekt-Items erhalten. §7/effekte zum Öffnen.");
    }
    public List<DeathSwapEffect> getAssigned(Player player) { return assigned.getOrDefault(player.getUniqueId(), List.of()); }
    public void clearAssignments() { assigned.clear(); }
    public void clear(Player player) { assigned.remove(player.getUniqueId()); }
    public boolean consume(Player player, DeathSwapEffect effect) {
        List<DeathSwapEffect> list = assigned.get(player.getUniqueId());
        if (list == null || !list.remove(effect)) return false;
        return true;
    }
    public void apply(Player source, Player target, DeathSwapEffect effect) {
        Player self = source;
        Player victim = target == null ? source : target;
        if (effect.needsTarget() && target == null) return;
        if (effect.needsTarget() && isShielded(victim)) { source.sendMessage("§cDer Schutzschild blockiert diesen Effekt!"); return; }
        int seconds;
        switch (effect) {
            case SCHUTZSCHILD -> { shieldUntil.put(self.getUniqueId(), System.currentTimeMillis() + 180_000L); self.sendMessage("§aSchutzschild aktiv: 3 Minuten."); }
            case HOEHENFLUG -> { add(self, PotionEffectType.JUMP_BOOST, 2, 20); add(self, PotionEffectType.SLOW_FALLING, 0, 20); }
            case Y100 -> { Location l = victim.getLocation().clone(); l.setY(Math.min(100, l.getWorld().getMaxHeight() - 2)); victim.teleportAsync(l); }
            case BLINDHEIT -> add(victim, PotionEffectType.BLINDNESS, 0, 8);
            case SWAP_SCHUTZ -> { game.protectNextSwap(self.getUniqueId()); self.sendMessage("§aDu bist beim nächsten Swap geschützt."); }
            case VERLANGSAMUNG -> add(victim, PotionEffectType.SLOWNESS, 2, 12);
            case HEILUNG -> heal(self, 12.0);
            case POSITIONSTAUSCH -> { Location a = self.getLocation().clone(), b = victim.getLocation().clone(); self.teleportAsync(b); victim.teleportAsync(a); }
            case SCHWAEche -> add(victim, PotionEffectType.WEAKNESS, 1, 15);
            case GESCHWINDIGKEIT -> add(self, PotionEffectType.SPEED, 1, 20);
            case REGENERATION -> add(self, PotionEffectType.REGENERATION, 1, 10);
            case NACHTSICHT -> add(self, PotionEffectType.NIGHT_VISION, 0, 120);
            case FEUERRESISTENZ -> add(self, PotionEffectType.FIRE_RESISTANCE, 0, 45);
            case WASSERATMUNG -> add(self, PotionEffectType.WATER_BREATHING, 0, 60);
            case UNSICHTBARKEIT -> add(self, PotionEffectType.INVISIBILITY, 0, 12);
            case SPRUNGKRAFT -> add(self, PotionEffectType.JUMP_BOOST, 2, 20);
            case RESISTENZ -> add(self, PotionEffectType.RESISTANCE, 1, 12);
            case ABSORPTION -> add(self, PotionEffectType.ABSORPTION, 1, 30);
            case SAETTIGUNG -> { self.setFoodLevel(20); self.setSaturation(20); }
            case ENDERBLINK -> teleportForward(self, 8);
            case RUECKSTOSS -> victim.setVelocity(victim.getLocation().getDirection().multiply(-2.2).setY(0.8));
            case LEVITATION -> add(victim, PotionEffectType.LEVITATION, 0, 3);
            case GIFT -> add(victim, PotionEffectType.POISON, 1, 8);
            case WITHER -> add(victim, PotionEffectType.WITHER, 0, 6);
            case HUNGER -> add(victim, PotionEffectType.HUNGER, 2, 15);
            case ABBREMSEN -> { add(victim, PotionEffectType.SLOWNESS, 1, 12); add(victim, PotionEffectType.MINING_FATIGUE, 1, 12); }
            case SCHWERE_BEINE -> add(victim, PotionEffectType.SLOWNESS, 2, 12);
            case KURZSICHT -> add(victim, PotionEffectType.BLINDNESS, 0, 4);
            case PECH -> { add(victim, PotionEffectType.WEAKNESS, 0, 10); add(victim, PotionEffectType.SLOWNESS, 0, 10); }
            case GLUECK -> { add(self, PotionEffectType.LUCK, 1, 45); add(self, PotionEffectType.REGENERATION, 0, 8); }
            case EXTRA_HERZEN -> add(self, PotionEffectType.ABSORPTION, 3, 20);
            case SCHNELLER_ABBAU -> add(self, PotionEffectType.HASTE, 1, 18);
            case ABBAUFLUCH -> add(victim, PotionEffectType.MINING_FATIGUE, 2, 12);
            case WOLKENSPRUNG -> { self.setVelocity(new Vector(0, 1.25, 0)); add(self, PotionEffectType.SLOW_FALLING, 0, 8); }
            case RUECKZUG -> teleportBackward(self, 12);
            case TAUSCHFALLE -> victim.teleportAsync(self.getLocation().clone());
            case KNOCKBACK_IMMUNITAET -> { knockbackImmuneUntil.put(self.getUniqueId(), System.currentTimeMillis() + 15_000L); add(self, PotionEffectType.RESISTANCE, 0, 15); }
            case EISFUESSE -> { add(victim, PotionEffectType.SLOWNESS, 5, 5); victim.setFreezeTicks(100); }
            case FEUERFLUCH -> victim.setFireTicks(80);
            case ENDERFALLE -> { Location l = victim.getLocation().clone(); l.setY(Math.min(l.getY() + 10, l.getWorld().getMaxHeight() - 2)); victim.teleportAsync(l); }
            case SCHUTZREGEN -> { add(self, PotionEffectType.RESISTANCE, 0, 15); add(self, PotionEffectType.REGENERATION, 0, 15); }
            case ADRENALIN -> { add(self, PotionEffectType.SPEED, 1, 12); add(self, PotionEffectType.HASTE, 0, 12); }
            case ERSCHOEPFUNG -> { add(victim, PotionEffectType.HUNGER, 1, 12); add(victim, PotionEffectType.SLOWNESS, 0, 12); }
            case SCHWEBESCHUTZ -> add(self, PotionEffectType.SLOW_FALLING, 0, 30);
            case NACHTMAHR -> { add(victim, PotionEffectType.BLINDNESS, 0, 6); add(victim, PotionEffectType.DARKNESS, 0, 6); }
            case VERWIRRUNG -> add(victim, PotionEffectType.NAUSEA, 0, 10);
            case HEILBLOCK -> heal(self, 999);
            case SCHADENSIMPULS -> victim.damage(6.0, self);
            case TELEPORT_ZURUECK -> { if (self.getWorld().getSpawnLocation() != null) self.teleportAsync(self.getWorld().getSpawnLocation()); }
            case TOTEMGLUECK -> { add(self, PotionEffectType.REGENERATION, 1, 8); add(self, PotionEffectType.RESISTANCE, 1, 8); }
        }
        source.sendMessage("§aEffekt aktiviert: §f" + effect.displayName());
        if (effect.needsTarget() && target != null) target.sendMessage("§cDu wurdest von " + source.getName() + " mit " + effect.displayName() + " getroffen.");
    }
    private void add(Player player, PotionEffectType type, int amplifier, int seconds) { player.addPotionEffect(new PotionEffect(type, seconds * 20, amplifier, false, true, true)); }
    private void heal(Player player, double amount) { double max = Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).getValue(); player.setHealth(Math.min(max, player.getHealth() + amount)); }
    private void teleportForward(Player p, double distance) { Location l = p.getLocation().clone().add(p.getLocation().getDirection().normalize().multiply(distance)); l.setY(p.getLocation().getY()); p.teleportAsync(l); }
    private void teleportBackward(Player p, double distance) { Location l = p.getLocation().clone().subtract(p.getLocation().getDirection().normalize().multiply(distance)); l.setY(p.getLocation().getY()); p.teleportAsync(l); }
    public boolean isShielded(Player player) { return shieldUntil.getOrDefault(player.getUniqueId(), 0L) > System.currentTimeMillis(); }
    public boolean isKnockbackImmune(Player player) { return knockbackImmuneUntil.getOrDefault(player.getUniqueId(), 0L) > System.currentTimeMillis(); }
    @EventHandler public void onKnockback(EntityKnockbackEvent event) {
        if (event.getEntity() instanceof Player player && isKnockbackImmune(player)) event.setCancelled(true);
    }
    @EventHandler public void onPotion(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player player) || !isShielded(player) || event.getNewEffect() == null) return;
        if (event.getNewEffect().getType().getCategory() == org.bukkit.potion.PotionEffectTypeCategory.HARMFUL) event.setCancelled(true);
    }
}
