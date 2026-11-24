package de.ultimatedeathswap.scoreboard;

import de.ultimatedeathswap.UltimateDeathSwap;
import de.ultimatedeathswap.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public final class ScoreboardManager {
    private final UltimateDeathSwap plugin;
    private int pageIndex = 0;
    public void advancePage(int totalPlayers) { int pages = Math.max(1, (int) Math.ceil(totalPlayers / 13.0)); pageIndex = (pageIndex + 1) % pages; }
    public ScoreboardManager(UltimateDeathSwap plugin) { this.plugin = plugin; }
    public void update(GameManager game) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
            Objective objective = board.registerNewObjective("uds", "dummy", ChatColor.LIGHT_PURPLE + "ULTIMATE DEATHSWAP");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            int line = 15;
            objective.getScore(ChatColor.GRAY + "Spieler: " + ChatColor.WHITE + game.getAliveCount()).setScore(line--);
            java.util.List<Player> participants = game.getParticipants();
            int start = Math.min(pageIndex * 13, Math.max(0, participants.size() - 1));
            int end = Math.min(start + 13, participants.size());
            for (Player participant : participants.subList(start, end)) {
                String status = game.isAlive(participant.getUniqueId()) ? ChatColor.GREEN + "♥ " + game.getLives(participant.getUniqueId()) : ChatColor.RED + "✖";
                String name = participant.getName();
                if (name.length() > 12) name = name.substring(0, 12);
                objective.getScore(status + ChatColor.WHITE + name).setScore(line--);
                if (line < 2) break;
            }
            objective.getScore(ChatColor.YELLOW + "Swap: " + game.getSwapCountdown() + "s").setScore(1);
            player.setScoreboard(board);
        }
    }
}
