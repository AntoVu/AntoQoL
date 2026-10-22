package net.antoqol.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModConfig {

    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final Client CLIENT;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        CLIENT = new Client(builder);
        CLIENT_SPEC = builder.build();
    }

    public enum AutoClickerMode { CLIENT, SERVER }

    public static class Client {

        // Speed
        public final ForgeConfigSpec.DoubleValue defaultSpeed;
        public final ForgeConfigSpec.DoubleValue speedScrollIncrement;

        // Timer
        public final ForgeConfigSpec.DoubleValue defaultTimer;
        public final ForgeConfigSpec.DoubleValue timerScrollIncrement;

        // Accelerator
        public final ForgeConfigSpec.DoubleValue smallAcceleration;
        public final ForgeConfigSpec.DoubleValue largeAcceleration;

        // No Slowdown
        public final ForgeConfigSpec.BooleanValue noSlowdownBlock;
        public final ForgeConfigSpec.BooleanValue noSlowdownItem;

        // No Debuff
        public final ForgeConfigSpec.BooleanValue noDebuffBlindness;
        public final ForgeConfigSpec.BooleanValue noDebuffDarkness;
        public final ForgeConfigSpec.BooleanValue noDebuffNausea;
        public final ForgeConfigSpec.BooleanValue noDebuffInvisibility;
        public final ForgeConfigSpec.BooleanValue noDebuffNightVision;

        // Crossbow Autoclicker
        public final ForgeConfigSpec.IntValue crossbowShotDelay;

        // Entity Reach
        public final ForgeConfigSpec.BooleanValue entityReachEnabled;
        public final ForgeConfigSpec.DoubleValue entityReachDistance;

        // Step
        public final ForgeConfigSpec.DoubleValue stepHeight;

        // Auto Clicker
        public final ForgeConfigSpec.EnumValue<AutoClickerMode> autoClickerMode;
        public final ForgeConfigSpec.IntValue autoClickerDelay;

        // Criticals
        public final ForgeConfigSpec.BooleanValue criticalsEnabled;

        // No Fall
        public final ForgeConfigSpec.BooleanValue noFallEnabled;

        // Freecam
        public final ForgeConfigSpec.DoubleValue freecamSpeed;

        // ESP
        public final ForgeConfigSpec.BooleanValue espEnabled;
        public final ForgeConfigSpec.BooleanValue espPlayers;
        public final ForgeConfigSpec.ConfigValue<String> espPlayerColor;
        public final ForgeConfigSpec.ConfigValue<String> espEntities;
        public final ForgeConfigSpec.ConfigValue<String> espEntityColor;
        public final ForgeConfigSpec.BooleanValue espTracers;

        // Fullbright
        public final ForgeConfigSpec.BooleanValue fullbrightEnabled;

        public Client(ForgeConfigSpec.Builder builder) {

            // Speed
            builder.comment("Speed Settings").push("speed");

            defaultSpeed = builder
                .comment("Movement speed multiplier when Speed is toggled on. 1.0 = vanilla.")
                .defineInRange("defaultSpeed", 2.0, 0.1, 20.0);

            speedScrollIncrement = builder
                .comment("How much each scroll tick changes the speed value while holding the speed keybind.")
                .defineInRange("speedScrollIncrement", 0.25, 0.01, 5.0);

            builder.pop();

            // Timer
            builder.comment("Timer Settings").push("timer");

            defaultTimer = builder
                .comment("Game tick rate multiplier when Timer is toggled on. 1.0 = normal. 2.0 = twice as fast.")
                .defineInRange("defaultTimer", 2.0, 0.1, 10.0);

            timerScrollIncrement = builder
                .comment("How much each scroll tick changes the timer while holding the timer keybind.")
                .defineInRange("timerScrollIncrement", 0.25, 0.01, 5.0);

            builder.pop();

            // Accelerator
            builder.comment("Accelerator Settings").push("accelerator");

            smallAcceleration = builder
                .comment("Velocity (blocks/tick) applied by the small acceleration keybind.")
                .defineInRange("smallAcceleration", 3.0, 0.1, 200.0);

            largeAcceleration = builder
                .comment("Velocity (blocks/tick) applied by the large acceleration keybind.")
                .defineInRange("largeAcceleration", 15.0, 0.1, 200.0);

            builder.pop();

            // No Slowdown
            builder.comment("No Slowdown Settings — prevents various sources of movement slow.").push("noSlowdown");

            noSlowdownBlock = builder
                .comment(
                    "Prevent movement slowdown from blocks (cobweb, honey block, slime block, soul sand, berry bush, etc).",
                    "Also works with most modded blocks that use vanilla slow mechanisms."
                )
                .define("noSlowdownBlock", false);

            noSlowdownItem = builder
                .comment("Prevent movement slowdown while using items: eating, drinking, bows, crossbows, shields, tridents, spyglass.")
                .define("noSlowdownItem", false);

            builder.pop();

            // No Debuff
            builder.comment("No Debuff Settings — disables visual/gameplay debuffs client-side.").push("noDebuff");

            noDebuffBlindness = builder
                .comment("Cancel the Blindness effect's black fog overlay.")
                .define("noDebuffBlindness", false);

            noDebuffDarkness = builder
                .comment("Cancel the Darkness effect's dim pulsing overlay (from Sculk Shriekers).")
                .define("noDebuffDarkness", false);

            noDebuffNausea = builder
                .comment("Cancel the Nausea effect's screen wobble/distortion.")
                .define("noDebuffNausea", false);

            noDebuffInvisibility = builder
                .comment(
                    "Reveal invisible entities — they render as fully visible.",
                    "A white bounding box is also drawn around them as a visual indicator."
                )
                .define("noDebuffInvisibility", false);

            noDebuffNightVision = builder
                .comment(
                    "Disable Night Vision effect rendering.",
                    "Useful if you have permanent night vision and find the bright sky/colors unpleasant.",
                    "Light levels render as if you have no night vision."
                )
                .define("noDebuffNightVision", false);

            builder.pop();

            // Crossbow Autoclicker
            builder.comment("Auto Crossbow Settings").push("autoCrossbow");

            crossbowShotDelay = builder
                .comment(
                    "Ticks to wait between shots when Auto Crossbow is active.",
                    "0 = fire as fast as physically possible.",
                    "Range: 0-10."
                )
                .defineInRange("crossbowShotDelay", 0, 0, 10);

            builder.pop();

            // Entity Reach
            builder.comment("Entity Reach Settings").push("entityReach");

            entityReachEnabled = builder
                .comment("Enable extended entity interaction/attack reach.")
                .define("entityReachEnabled", false);

            entityReachDistance = builder
                .comment(
                    "Reach distance in blocks for entity interactions and attacks.",
                    "Vanilla survival default is 3.0. Cannot be set below 3.0.",
                    "Max 10.0. The server ignores hits beyond ~6 blocks."
                )
                .defineInRange("entityReachDistance", 3.0, 3.0, 10.0);

            builder.pop();

            // Step
            builder.comment("Step Settings").push("step");

            stepHeight = builder
                .comment(
                    "Max block height walked up without jumping while Step is toggled on.",
                    "Vanilla is 0.6 (slabs), horses are 1.0 (full blocks)."
                )
                .defineInRange("stepHeight", 1.0, 0.6, 10.0);

            builder.pop();

            // Auto Clicker
            builder.comment("Auto Clicker Settings").push("autoClicker");

            autoClickerMode = builder
                .comment(
                    "When to attack while Auto Clicker is on.",
                    "CLIENT: as soon as your attack cooldown is full.",
                    "SERVER: also waits for the server's cooldown, measured from its TPS. Use on laggy servers/realms."
                )
                .defineEnum("autoClickerMode", AutoClickerMode.CLIENT);

            autoClickerDelay = builder
                .comment(
                    "Extra ticks to wait after the cooldown is up before clicking. 0 = click immediately.",
                    "20 ticks = 1 second."
                )
                .defineInRange("autoClickerDelay", 0, 0, 100);

            builder.pop();

            // Criticals
            builder.comment("Criticals Settings").push("criticals");

            criticalsEnabled = builder
                .comment(
                    "Make every grounded attack on a living entity a critical hit.",
                    "Sends a tiny hop to the server before each attack. Sprinting hits still don't crit."
                )
                .define("criticalsEnabled", false);

            builder.pop();

            // No Fall
            builder.comment("No Fall Settings").push("noFall");

            noFallEnabled = builder
                .comment(
                    "Prevent fall damage. While falling, tells the server you are on the ground.",
                    "Elytra flight is not affected."
                )
                .define("noFallEnabled", false);

            builder.pop();

            // Freecam
            builder.comment("Freecam Settings").push("freecam");

            freecamSpeed = builder
                .comment("Freecam flying speed in blocks per tick. 1.0 = 20 blocks per second.")
                .defineInRange("freecamSpeed", 1.0, 0.1, 10.0);

            builder.pop();

            // ESP
            builder.comment("ESP Settings: glowing outlines (visible through walls) and tracers.").push("esp");

            espEnabled = builder
                .comment("Master switch for ESP. Off disables all ESP; on enables whichever ESP options below are on.")
                .define("espEnabled", true);

            espPlayers = builder
                .comment("Outline other players.")
                .define("espPlayers", false);

            espPlayerColor = builder
                .comment("Player outline color as a hex code, e.g. #FF0000.")
                .define("espPlayerColor", "#FF0000", ModConfig::isHexColor);

            espEntities = builder
                .comment(
                    "Entity IDs to outline, separated by spaces. Modded entities work too. Empty = off.",
                    "Example: minecraft:pig mod1:modded_entity (the minecraft: prefix is optional)."
                )
                .define("espEntities", "", o -> o instanceof String);

            espEntityColor = builder
                .comment("Outline color for the listed entities as a hex code, e.g. #00FF00.")
                .define("espEntityColor", "#00FF00", ModConfig::isHexColor);

            espTracers = builder
                .comment("Draw a line from the crosshair to every outlined entity, in its outline color.")
                .define("espTracers", false);

            builder.pop();

            // Fullbright
            builder.comment("Fullbright Settings").push("fullbright");

            fullbrightEnabled = builder
                .comment("See in the dark, exactly like having Night Vision.")
                .define("fullbrightEnabled", false);

            builder.pop();
        }
    }

    private static boolean isHexColor(Object o) {
        return o instanceof String s && s.matches("#?[0-9a-fA-F]{6}");
    }

    // "#RRGGBB" -> 0xRRGGBB; the spec validator guarantees the format
    private static int parseColor(String s) {
        return Integer.parseInt(s.startsWith("#") ? s.substring(1) : s, 16);
    }

    // Speed
    public static double getDefaultSpeed()         { return CLIENT.defaultSpeed.get(); }
    public static double getSpeedScrollIncrement() { return CLIENT.speedScrollIncrement.get(); }

    // Timer
    public static double getDefaultTimer()         { return CLIENT.defaultTimer.get(); }
    public static double getTimerScrollIncrement() { return CLIENT.timerScrollIncrement.get(); }

    // Accelerator
    public static double getSmallAcceleration()    { return CLIENT.smallAcceleration.get(); }
    public static double getLargeAcceleration()    { return CLIENT.largeAcceleration.get(); }

    // No Slowdown
    public static boolean isNoSlowdownBlockEnabled()  { return CLIENT.noSlowdownBlock.get(); }
    public static boolean isNoSlowdownItemEnabled()   { return CLIENT.noSlowdownItem.get(); }

    // No Debuff
    public static boolean isNoDebuffBlindnessEnabled()    { return CLIENT.noDebuffBlindness.get(); }
    public static boolean isNoDebuffDarknessEnabled()     { return CLIENT.noDebuffDarkness.get(); }
    public static boolean isNoDebuffNauseaEnabled()       { return CLIENT.noDebuffNausea.get(); }
    public static boolean isNoDebuffInvisibilityEnabled() { return CLIENT.noDebuffInvisibility.get(); }
    public static boolean isNoDebuffNightVisionEnabled()  { return CLIENT.noDebuffNightVision.get(); }

    // Crossbow Autoclicker
    public static int getCrossbowShotDelay() { return CLIENT.crossbowShotDelay.get(); }

    // Entity Reach
    public static boolean isEntityReachEnabled()    { return CLIENT.entityReachEnabled.get(); }
    public static double getEntityReachDistance()   { return CLIENT.entityReachDistance.get(); }

    // Step
    public static double getStepHeight()            { return CLIENT.stepHeight.get(); }

    // Auto Clicker
    public static AutoClickerMode getAutoClickerMode() { return CLIENT.autoClickerMode.get(); }
    public static int getAutoClickerDelay()            { return CLIENT.autoClickerDelay.get(); }

    // Criticals
    public static boolean isCriticalsEnabled()     { return CLIENT.criticalsEnabled.get(); }

    // No Fall
    public static boolean isNoFallEnabled()        { return CLIENT.noFallEnabled.get(); }

    // Freecam
    public static double getFreecamSpeed()         { return CLIENT.freecamSpeed.get(); }

    // ESP
    public static boolean isEspEnabled()           { return CLIENT.espEnabled.get(); }
    public static boolean isEspPlayersEnabled()   { return CLIENT.espPlayers.get(); }
    public static int getEspPlayerColor()          { return parseColor(CLIENT.espPlayerColor.get()); }
    public static String getEspEntities()          { return CLIENT.espEntities.get(); }
    public static int getEspEntityColor()          { return parseColor(CLIENT.espEntityColor.get()); }
    public static boolean isEspTracersEnabled()    { return CLIENT.espTracers.get(); }

    // Fullbright
    public static boolean isFullbrightEnabled()    { return CLIENT.fullbrightEnabled.get(); }
}