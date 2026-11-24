package de.ultimatedeathswap.effects;

import org.bukkit.Material;

public enum DeathSwapEffect {
    SCHUTZSCHILD("Schutzschild", Material.WHITE_DYE, "3 Minuten immun gegen negative Effekte.", false, true),
    HOEHENFLUG("Höhenflug", Material.LIGHT_BLUE_DYE, "Starker Sprung- und Langsamfall-Boost.", false, true),
    Y100("Y=100", Material.ORANGE_DYE, "Teleportiert einen ausgewählten Spieler auf Höhe 100.", true, false),
    BLINDHEIT("Blindheit", Material.BLACK_DYE, "Blendet einen ausgewählten Spieler 8 Sekunden.", true, false),
    SWAP_SCHUTZ("Swap-Schutz", Material.GRAY_DYE, "Du überspringst den nächsten Spieler-Swap.", false, true),
    VERLANGSAMUNG("Verlangsamung", Material.BROWN_DYE, "Verlangsamt einen Spieler 12 Sekunden.", true, false),
    HEILUNG("Heilung", Material.PINK_DYE, "Stellt 6 Herzen wieder her.", false, true),
    POSITIONSTAUSCH("Positionswechsel", Material.MAGENTA_DYE, "Tauscht deine Position mit einem Spieler.", true, false),
    SCHWAEche("Schwäche", Material.PURPLE_DYE, "Schwächt den Angriff eines Spielers 15 Sekunden.", true, false),
    GESCHWINDIGKEIT("Geschwindigkeit", Material.LIME_DYE, "Geschwindigkeit II für 20 Sekunden.", false, true),
    REGENERATION("Regeneration", Material.RED_DYE, "Regeneration II für 10 Sekunden.", false, true),
    NACHTSICHT("Nachtsicht", Material.CYAN_DYE, "Nachtsicht für 2 Minuten.", false, true),
    FEUERRESISTENZ("Feuerresistenz", Material.YELLOW_DYE, "Feuerresistenz für 45 Sekunden.", false, true),
    WASSERATMUNG("Wasseratmung", Material.BLUE_DYE, "Wasseratmung für 60 Sekunden.", false, true),
    UNSICHTBARKEIT("Unsichtbarkeit", Material.LIGHT_GRAY_DYE, "Unsichtbarkeit für 12 Sekunden.", false, true),
    SPRUNGKRAFT("Sprungkraft", Material.GREEN_DYE, "Sprungkraft III für 20 Sekunden.", false, true),
    RESISTENZ("Resistenz", Material.IRON_INGOT, "Resistenz II für 12 Sekunden.", false, true),
    ABSORPTION("Absorption", Material.GOLD_INGOT, "Erhält 4 zusätzliche Herzen für 30 Sekunden.", false, true),
    SAETTIGUNG("Sättigung", Material.BREAD, "Füllt Hunger und Sättigung vollständig auf.", false, true),
    ENDERBLINK("Enderblink", Material.ENDER_PEARL, "Teleportiert dich 8 Blöcke nach vorne.", false, true),
    RUECKSTOSS("Rückstoß", Material.PISTON, "Schleudert einen ausgewählten Spieler zurück.", true, false),
    LEVITATION("Levitation", Material.FEATHER, "Lässt einen Spieler 3 Sekunden schweben.", true, false),
    GIFT("Gift", Material.SPIDER_EYE, "Vergiftet einen Spieler 8 Sekunden.", true, false),
    WITHER("Wither", Material.WITHER_ROSE, "Fügt einem Spieler kurz Wither zu.", true, false),
    HUNGER("Hunger", Material.ROTTEN_FLESH, "Verursacht starken Hunger für 15 Sekunden.", true, false),
    ABBREMSEN("Abbremsen", Material.MUD, "Gibt einem Spieler Slowness II und Mining Fatigue.", true, false),
    SCHWERE_BEINE("Schwere Beine", Material.ANVIL, "Verhindert hohe Sprünge für 12 Sekunden.", true, false),
    KURZSICHT("Kurzsicht", Material.GRAY_DYE, "Blindheit I für 4 Sekunden.", true, false),
    PECH("Pech", Material.COAL, "Gibt einem Spieler 10 Sekunden lang Pech: Schwäche und Langsamkeit.", true, false),
    GLUECK("Glück", Material.EMERALD, "Gibt dir Glück und Regeneration.", false, true),
    EXTRA_HERZEN("Extra-Herzen", Material.GOLDEN_APPLE, "Erhält 8 Absorptionsherzen für 20 Sekunden.", false, true),
    SCHNELLER_ABBAU("Schneller Abbau", Material.GOLDEN_PICKAXE, "Hast II für 18 Sekunden.", false, true),
    ABBAUFLUCH("Abbaufluch", Material.COBWEB, "Mining Fatigue III für 12 Sekunden.", true, false),
    WOLKENSPRUNG("Wolkensprung", Material.SLIME_BALL, "Springt dich hoch und fängt den Fall ab.", false, true),
    RUECKZUG("Rückzug", Material.POPPED_CHORUS_FRUIT, "Teleportiert dich 12 Blöcke rückwärts.", false, true),
    TAUSCHFALLE("Tauschfalle", Material.TRIPWIRE_HOOK, "Zieht einen Spieler zu deiner Position.", true, false),
    KNOCKBACK_IMMUNITAET("Standfest", Material.OBSIDIAN, "Du bist 15 Sekunden gegen Rückstoß geschützt.", false, true),
    EISFUESSE("Eisfüße", Material.PACKED_ICE, "Ein Spieler wird kurz verlangsamt und festgesetzt.", true, false),
    FEUERFLUCH("Feuerfluch", Material.BLAZE_POWDER, "Setzt einen Spieler 4 Sekunden in Brand.", true, false),
    ENDERFALLE("Enderfalle", Material.ENDER_EYE, "Teleportiert einen Spieler 10 Blöcke nach oben.", true, false),
    SCHUTZREGEN("Schutzregen", Material.SHIELD, "Resistenz I und Regeneration I für 15 Sekunden.", false, true),
    ADRENALIN("Adrenalin", Material.SUGAR, "Speed II und Haste I für 12 Sekunden.", false, true),
    ERSCHOEPFUNG("Erschöpfung", Material.KELP, "Gibt einem Spieler Hunger und Slowness.", true, false),
    SCHWEBESCHUTZ("Schwebeschutz", Material.HONEY_BOTTLE, "Slow Falling für 30 Sekunden.", false, true),
    NACHTMAHR("Nachtmahr", Material.INK_SAC, "Blindheit und Darkness für 6 Sekunden.", true, false),
    VERWIRRUNG("Verwirrung", Material.PUFFERFISH, "Nausea für 10 Sekunden.", true, false),
    HEILBLOCK("Heilimpuls", Material.GHAST_TEAR, "Heilt dich vollständig.", false, true),
    SCHADENSIMPULS("Schadensimpuls", Material.FIRE_CHARGE, "Zieht einem Spieler 3 Herzen ab.", true, false),
    TELEPORT_ZURUECK("Zurück zum Start", Material.COMPASS, "Teleportiert dich zu deinem Welt-Spawn.", false, true),
    TOTEMGLUECK("Totemglück", Material.TOTEM_OF_UNDYING, "Gibt dir 8 Sekunden Regeneration und Resistenz.", false, true);

    private final String displayName;
    private final Material material;
    private final String description;
    private final boolean target;
    private final boolean positive;
    DeathSwapEffect(String displayName, Material material, String description, boolean target, boolean positive) {
        this.displayName = displayName; this.material = material; this.description = description; this.target = target; this.positive = positive;
    }
    public String displayName() { return displayName; }
    public Material material() { return material; }
    public String description() { return description; }
    public boolean needsTarget() { return target; }
    public boolean positive() { return positive; }
}
