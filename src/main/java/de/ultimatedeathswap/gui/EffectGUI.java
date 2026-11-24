package de.ultimatedeathswap.gui;

import de.ultimatedeathswap.UltimateDeathSwap;
import de.ultimatedeathswap.effects.DeathSwapEffect;
import de.ultimatedeathswap.effects.EffectManager;
import de.ultimatedeathswap.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public final class EffectGUI implements Listener {
    private final UltimateDeathSwap plugin;
    private final GameManager game;
    private final EffectManager effects;
    private final NamespacedKey effectKey, targetKey;
    private final Map<UUID, DeathSwapEffect> pending = new HashMap<>();
    public EffectGUI(UltimateDeathSwap plugin, GameManager game, EffectManager effects) {
        this.plugin = plugin; this.game = game; this.effects = effects;
        effectKey = new NamespacedKey(plugin, "effect"); targetKey = new NamespacedKey(plugin, "target");
    }
    public void openEffects(Player player) {
        Inventory inv = Bukkit.createInventory(new MenuHolder(MenuType.EFFECTS), 27, "§5Deine Effekt-Slots");
        for (int slot : new int[]{11, 13, 15}) inv.setItem(slot, named(Material.GRAY_STAINED_GLASS_PANE, "§7Noch kein Effekt", "§8Alle 30 Sekunden bekommst du drei neue Effekte."));
        List<DeathSwapEffect> assigned = effects.getAssigned(player);
        int[] slots = {11, 13, 15};
        for (int i = 0; i < Math.min(3, assigned.size()); i++) inv.setItem(slots[i], effectItem(assigned.get(i)));
        inv.setItem(22, named(Material.BARRIER, "§cSchließen", "§7Schließt dieses Menü."));
        player.openInventory(inv);
    }
    private ItemStack effectItem(DeathSwapEffect effect) {
        ItemStack item = new ItemStack(effect.material()); ItemMeta meta = item.getItemMeta();
        meta.setDisplayName((effect.positive() ? "§a" : "§c") + effect.displayName());
        meta.setLore(List.of("§7" + effect.description(), "", effect.needsTarget() ? "§eRechtsklick: Ziel auswählen" : "§eRechtsklick: Aktivieren"));
        meta.getPersistentDataContainer().set(effectKey, PersistentDataType.STRING, effect.name()); item.setItemMeta(meta); return item;
    }
    private ItemStack named(Material material, String name, String lore) { ItemStack item = new ItemStack(material); ItemMeta meta = item.getItemMeta(); meta.setDisplayName(name); meta.setLore(List.of(lore)); item.setItemMeta(meta); return item; }
    private void openTargets(Player player, DeathSwapEffect effect) {
        pending.put(player.getUniqueId(), effect);
        Inventory inv = Bukkit.createInventory(new MenuHolder(MenuType.TARGETS), 54, "§8Ziel für: " + effect.displayName());
        int slot = 0;
        for (Player target : game.getParticipants()) {
            if (target.equals(player) || !target.isOnline() || !game.isAlive(target.getUniqueId())) continue;
            ItemStack head = new ItemStack(Material.PLAYER_HEAD); SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwningPlayer(target); meta.setDisplayName("§e" + target.getName());
            meta.setLore(List.of("§7Klicken, um diesen Spieler auszuwählen."));
            meta.getPersistentDataContainer().set(targetKey, PersistentDataType.STRING, target.getUniqueId().toString());
            head.setItemMeta(meta); if (slot < 45) inv.setItem(slot++, head);
        }
        inv.setItem(49, named(Material.BARRIER, "§cAbbrechen", "§7Zurück zu deinen Effekten."));
        player.openInventory(inv);
    }
    @EventHandler public void onDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof MenuHolder) event.setCancelled(true);
    }
    @EventHandler public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player) || !(event.getView().getTopInventory().getHolder() instanceof MenuHolder holder)) return;
        event.setCancelled(true);
        if (event.getClickedInventory() == null || event.getClickedInventory() != event.getView().getTopInventory()) return;
        ItemStack clicked = event.getCurrentItem(); if (clicked == null || clicked.getType().isAir()) return;
        if (holder.type == MenuType.EFFECTS) {
            if (clicked.getType() == Material.BARRIER) { player.closeInventory(); return; }
            if (event.getClick().isRightClick()) {
                ItemMeta meta = clicked.getItemMeta();
                if (meta == null) return;
                String id = meta.getPersistentDataContainer().get(effectKey, PersistentDataType.STRING);
                if (id == null) return;
                DeathSwapEffect effect;
                try { effect = DeathSwapEffect.valueOf(id); } catch (IllegalArgumentException ex) { return; }
                if (effect.needsTarget()) openTargets(player, effect);
                else activate(player, player, effect);
            }
        } else {
            if (clicked.getType() == Material.BARRIER) { pending.remove(player.getUniqueId()); openEffects(player); return; }
            String raw = clicked.getItemMeta().getPersistentDataContainer().get(targetKey, PersistentDataType.STRING);
            if (raw == null) return;
            UUID targetId;
            try { targetId = UUID.fromString(raw); } catch (IllegalArgumentException ex) { return; }
            Player target = Bukkit.getPlayer(targetId); DeathSwapEffect effect = pending.remove(player.getUniqueId());
            if (target == null || !target.isOnline() || !game.isAlive(targetId) || effect == null) { player.sendMessage("§cDieses Ziel ist nicht mehr verfügbar."); openEffects(player); return; }
            activate(player, target, effect);
        }
    }
    private void activate(Player player, Player target, DeathSwapEffect effect) {
        if (!effects.consume(player, effect)) { player.sendMessage("§cDieser Effekt wurde bereits benutzt oder ist abgelaufen."); openEffects(player); return; }
        player.closeInventory(); effects.apply(player, target, effect); openEffects(player);
    }
    private enum MenuType { EFFECTS, TARGETS }
    private static final class MenuHolder implements InventoryHolder {
        private final MenuType type;
        private MenuHolder(MenuType type) { this.type = type; }
        @Override public Inventory getInventory() { return null; }
    }
}
