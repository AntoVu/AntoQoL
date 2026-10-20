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

            builder.pop();
        }
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
}