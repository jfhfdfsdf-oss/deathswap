package de.ultimatedeathswap;

import de.ultimatedeathswap.command.DeathSwapCommand;
import de.ultimatedeathswap.effects.EffectManager;
import de.ultimatedeathswap.game.GameManager;
import de.ultimatedeathswap.gui.EffectGUI;
import de.ultimatedeathswap.scoreboard.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class UltimateDeathSwap extends JavaPlugin {
    private GameManager gameManager;
    private EffectManager effectManager;
    private EffectGUI effectGUI;
    private ScoreboardManager scoreboardManager;

    @Override public void onEnable() {
        saveDefaultConfig();
        this.scoreboardManager = new ScoreboardManager(this);
        this.gameManager = new GameManager(this, scoreboardManager);
        this.effectManager = new EffectManager(this, gameManager);
        this.effectGUI = new EffectGUI(this, gameManager, effectManager);
        getServer().getPluginManager().registerEvents(gameManager, this);
        getServer().getPluginManager().registerEvents(effectManager, this);
        getServer().getPluginManager().registerEvents(effectGUI, this);
        DeathSwapCommand command = new DeathSwapCommand(this, gameManager, effectGUI);
        getCommand("deathswap").setExecutor(command);
        getCommand("deathswap").setTabCompleter(command);
        getCommand("effekte").setExecutor((sender, cmd, label, args) -> {
            if (!(sender instanceof org.bukkit.entity.Player player)) { sender.sendMessage("Nur Spieler können das nutzen."); return true; }
            effectGUI.openEffects(player); return true;
        });
        gameManager.loadData();
        getLogger().info("UltimateDeathSwap wurde aktiviert.");
    }

    @Override public void onDisable() {
        if (gameManager != null) gameManager.saveData();
    }
    public GameManager game() { return gameManager; }
    public EffectManager effects() { return effectManager; }
    public EffectGUI gui() { return effectGUI; }
    public void reloadPluginConfig() { reloadConfig(); gameManager.reloadSettings(); effectManager.reloadSettings(); }
}
