package de.ultimatedeathswap.command;

import de.ultimatedeathswap.UltimateDeathSwap;
import de.ultimatedeathswap.game.GameManager;
import de.ultimatedeathswap.gui.EffectGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class DeathSwapCommand implements CommandExecutor, TabCompleter {
    private final UltimateDeathSwap plugin;
    private final GameManager game;
    private final EffectGUI gui;
    public DeathSwapCommand(UltimateDeathSwap plugin, GameManager game, EffectGUI gui) { this.plugin = plugin; this.game = game; this.gui = gui; }
    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) { sender.sendMessage("§d/deathswap <start|stop|restart|join|leave|setlives|reload>"); return true; }
        String sub = args[0].toLowerCase();
        if (sub.equals("join")) {
            if (!(sender instanceof Player player)) { sender.sendMessage("Nur Spieler können beitreten."); return true; }
            game.join(player); return true;
        }
        if (sub.equals("leave")) {
            if (!(sender instanceof Player player)) { sender.sendMessage("Nur Spieler können das Spiel verlassen."); return true; }
            game.leave(player); player.sendMessage("§eDu hast das DeathSwap-Spiel verlassen."); return true;
        }
        if (!sender.hasPermission("ultimatedeathswap.admin")) { sender.sendMessage("§cDafür benötigst du OP-Rechte."); return true; }
        switch (sub) {
            case "start" -> game.start();
            case "stop" -> game.stop();
            case "restart" -> game.restart();
            case "reload" -> { plugin.reloadPluginConfig(); sender.sendMessage("§aUltimateDeathSwap-Konfiguration neu geladen."); }
            case "setlives" -> {
                if (args.length != 3) { sender.sendMessage("§cVerwendung: /deathswap setlives <spieler> <anzahl>"); return true; }
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) { sender.sendMessage("§cDieser Spieler ist nicht online."); return true; }
                try { int amount = Integer.parseInt(args[2]); if (amount < 0 || amount > plugin.getConfig().getInt("max-lives", 20)) throw new NumberFormatException(); game.setLives(target, amount); sender.sendMessage("§aLeben von " + target.getName() + " auf " + amount + " gesetzt."); }
                catch (NumberFormatException ex) { sender.sendMessage("§cBitte gib eine Zahl zwischen 0 und " + plugin.getConfig().getInt("max-lives", 20) + " ein."); }
            }
            default -> sender.sendMessage("§cUnbekannter Unterbefehl. /deathswap <start|stop|restart|join|leave|setlives|reload>");
        }
        return true;
    }
    @Override public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) return List.of("start", "stop", "restart", "join", "leave", "setlives", "reload").stream().filter(s -> s.startsWith(args[0].toLowerCase())).toList();
        if (args.length == 2 && args[0].equalsIgnoreCase("setlives")) return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(n -> n.toLowerCase().startsWith(args[1].toLowerCase())).toList();
        if (args.length == 3 && args[0].equalsIgnoreCase("setlives")) return List.of("0", "1", "2", "3", "5", "10");
        return new ArrayList<>();
    }
}
