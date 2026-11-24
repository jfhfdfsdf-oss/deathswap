package de.ultimatedeathswap.game;

import de.ultimatedeathswap.UltimateDeathSwap;
import de.ultimatedeathswap.scoreboard.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class GameManager implements Listener {
    private final UltimateDeathSwap plugin;
    private final ScoreboardManager scoreboard;
    private final Set<UUID> participants = new LinkedHashSet<>();
    private final Set<UUID> eliminated = new HashSet<>();
    private final Map<UUID, Integer> lives = new HashMap<>();
    private final Set<UUID> swapProtected = new HashSet<>();
    private BukkitTask ticker;
    private boolean running;
    private int swapInterval, effectInterval, startingLives, secondsToSwap;
    private int secondCounter;

    public GameManager(UltimateDeathSwap plugin, ScoreboardManager scoreboard) { this.plugin = plugin; this.scoreboard = scoreboard; reloadSettings(); }
    public void reloadSettings() { swapInterval = Math.max(10, plugin.getConfig().getInt("swap-interval-seconds", 60)); effectInterval = Math.max(10, plugin.getConfig().getInt("effect-interval-seconds", 30)); startingLives = Math.max(1, plugin.getConfig().getInt("starting-lives", 3)); }
    public void loadData() {
        File file = new File(plugin.getDataFolder(), "players.yml");
        if (!file.exists()) return;
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        for (String key : yml.getKeys(false)) try { lives.put(UUID.fromString(key), yml.getInt(key, startingLives)); } catch (IllegalArgumentException ignored) { }
    }
    public void saveData() {
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
        YamlConfiguration yml = new YamlConfiguration(); lives.forEach((uuid, value) -> yml.set(uuid.toString(), value));
        try { yml.save(new File(plugin.getDataFolder(), "players.yml")); } catch (IOException ex) { plugin.getLogger().severe("Spielerdaten konnten nicht gespeichert werden: " + ex.getMessage()); }
    }
    public boolean join(Player player) {
        if (running) { player.sendMessage("§cDas Spiel läuft bereits."); return false; }
        participants.add(player.getUniqueId()); eliminated.remove(player.getUniqueId()); lives.putIfAbsent(player.getUniqueId(), startingLives); player.setGameMode(GameMode.SURVIVAL); update(); saveData();
        Bukkit.broadcastMessage("§d" + player.getName() + " §7ist DeathSwap beigetreten."); return true;
    }
    public void leave(Player player) {
        participants.remove(player.getUniqueId()); eliminated.remove(player.getUniqueId()); swapProtected.remove(player.getUniqueId()); plugin.effects().clear(player);
        if (running && getAliveCount() <= 1) finishGame(); update(); saveData();
    }
    public void start() {
        if (running) return;
        for (Player p : Bukkit.getOnlinePlayers()) if (participants.contains(p.getUniqueId())) { lives.put(p.getUniqueId(), startingLives); eliminated.remove(p.getUniqueId()); p.setGameMode(GameMode.SURVIVAL); }
        if (getAliveCount() < 2) { Bukkit.broadcastMessage("§cMindestens zwei Spieler müssen teilnehmen."); return; }
        plugin.effects().clearAssignments();
        running = true; secondsToSwap = swapInterval; secondCounter = 0;
        Bukkit.broadcastMessage("§aDeathSwap wurde gestartet!");
        ticker = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 20L, 20L); update();
    }
    public void stop() { if (ticker != null) ticker.cancel(); ticker = null; running = false; plugin.effects().clearAssignments(); Bukkit.broadcastMessage("§cDeathSwap wurde gestoppt."); update(); }
    public void restart() { stop(); for (UUID id : participants) { lives.put(id, startingLives); eliminated.remove(id); Player p = Bukkit.getPlayer(id); if (p != null) p.setGameMode(GameMode.SURVIVAL); } start(); }
    private void tick() {
        if (!running) return;
        secondCounter++;
        secondsToSwap--;
        if (secondsToSwap <= 5 && secondsToSwap >= 1) Bukkit.broadcastMessage("§dSwap in §f" + secondsToSwap + "§d...");
        if (secondsToSwap <= 0) { performSwap(); secondsToSwap = swapInterval; }
        if (secondCounter >= effectInterval) { secondCounter = 0; for (UUID id : getAliveIds()) { Player p = Bukkit.getPlayer(id); if (p != null) plugin.effects().giveThree(p); } }
        scoreboard.advancePage(getParticipants().size());
        update();
    }
    private void performSwap() {
        List<Player> active = new ArrayList<>(); for (UUID id : getAliveIds()) { Player p = Bukkit.getPlayer(id); if (p != null && p.isOnline() && !swapProtected.remove(id)) active.add(p); }
        if (active.size() < 2) return;
        Collections.shuffle(active);
        Map<UUID, Location> destinations = new HashMap<>();
        for (int i = 0; i < active.size(); i++) destinations.put(active.get(i).getUniqueId(), active.get((i + 1) % active.size()).getLocation().clone());
        for (Player p : active) { Location destination = destinations.get(p.getUniqueId()); p.teleportAsync(destination); }
        Bukkit.broadcastMessage("§dDie Spieler wurden getauscht!");
    }
    @EventHandler public void onDeath(PlayerDeathEvent event) {
        Player p = event.getEntity(); UUID id = p.getUniqueId();
        if (!running || !participants.contains(id) || eliminated.contains(id)) return;
        int remaining = lives.getOrDefault(id, startingLives) - 1; lives.put(id, remaining);
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (remaining <= 0) { eliminated.add(id); p.setGameMode(GameMode.SPECTATOR); Bukkit.broadcastMessage("§c" + p.getName() + " ist ausgeschieden!"); }
            else Bukkit.broadcastMessage("§e" + p.getName() + " hat noch " + remaining + " Leben.");
            if (getAliveCount() <= 1) finishGame(); update(); saveData();
        });
    }
    @EventHandler public void onRespawn(PlayerRespawnEvent event) {
        UUID id = event.getPlayer().getUniqueId();
        if (eliminated.contains(id)) Bukkit.getScheduler().runTask(plugin, () -> event.getPlayer().setGameMode(GameMode.SPECTATOR));
    }
    @EventHandler public void onQuit(PlayerQuitEvent event) { if (running && isAlive(event.getPlayer().getUniqueId())) { eliminated.add(event.getPlayer().getUniqueId()); if (getAliveCount() <= 1) finishGame(); update(); saveData(); } }
    private void finishGame() { if (!running) return; running = false; if (ticker != null) ticker.cancel(); ticker = null; List<UUID> winners = getAliveIds(); if (winners.size() == 1) { Player winner = Bukkit.getPlayer(winners.get(0)); Bukkit.broadcastMessage("§6" + (winner == null ? "Ein Spieler" : winner.getName()) + " hat Ultimate DeathSwap gewonnen!"); } else Bukkit.broadcastMessage("§eDas Spiel ist beendet."); }
    public void setLives(Player player, int amount) { lives.put(player.getUniqueId(), Math.max(0, amount)); if (amount <= 0) { eliminated.add(player.getUniqueId()); player.setGameMode(GameMode.SPECTATOR); } else { eliminated.remove(player.getUniqueId()); if (player.getGameMode() == GameMode.SPECTATOR) player.setGameMode(GameMode.SURVIVAL); } if (running && getAliveCount() <= 1) finishGame(); update(); saveData(); }
    public void protectNextSwap(UUID id) { swapProtected.add(id); }
    public boolean isRunning() { return running; }
    public boolean isAlive(UUID id) { return participants.contains(id) && !eliminated.contains(id) && lives.getOrDefault(id, startingLives) > 0; }
    public int getLives(UUID id) { return lives.getOrDefault(id, startingLives); }
    public int getAliveCount() { return (int) participants.stream().filter(this::isAlive).filter(id -> Bukkit.getPlayer(id) != null).count(); }
    public List<UUID> getAliveIds() { return participants.stream().filter(this::isAlive).filter(id -> Bukkit.getPlayer(id) != null).toList(); }
    public List<Player> getParticipants() { return participants.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).toList(); }
    public int getSwapCountdown() { return secondsToSwap; }
    public boolean isParticipant(UUID id) { return participants.contains(id); }
    private void update() { scoreboard.update(this); }
}
