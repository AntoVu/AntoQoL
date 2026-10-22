package net.antoqol.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfigScreen {

    private ConfigScreen() {}

    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Component.literal("AntoQoL — Settings"))
            .setSavingRunnable(ModConfig.CLIENT_SPEC::save);

        ConfigEntryBuilder entry = builder.entryBuilder();

        // Speed
        ConfigCategory speed = builder.getOrCreateCategory(Component.literal("Speed"));

        speed.addEntry(entry
            .startDoubleField(Component.literal("Default Speed Multiplier"), ModConfig.getDefaultSpeed())
            .setDefaultValue(2.0).setMin(0.1).setMax(20.0)
            .setTooltip(
                Component.literal("Movement speed multiplier when Speed is toggled on."),
                Component.literal("1.0 = vanilla. 2.0 = twice as fast."),
                Component.literal("Resets to this value on each toggle.")
            )
            .setSaveConsumer(v -> ModConfig.CLIENT.defaultSpeed.set(v))
            .build());

        speed.addEntry(entry
            .startDoubleField(Component.literal("Speed Scroll Increment"), ModConfig.getSpeedScrollIncrement())
            .setDefaultValue(0.25).setMin(0.01).setMax(5.0)
            .setTooltip(Component.literal("Amount added/subtracted per scroll tick while holding the speed keybind."))
            .setSaveConsumer(v -> ModConfig.CLIENT.speedScrollIncrement.set(v))
            .build());

        // Timer
        ConfigCategory timer = builder.getOrCreateCategory(Component.literal("Timer"));

        timer.addEntry(entry
            .startDoubleField(Component.literal("Default Timer Multiplier"), ModConfig.getDefaultTimer())
            .setDefaultValue(2.0).setMin(0.1).setMax(10.0)
            .setTooltip(
                Component.literal("Client tick rate multiplier when Timer is toggled on."),
                Component.literal("1.0 = normal. 2.0 = twice as fast. 0.5 = slow motion."),
                Component.literal("Affects movement, animations, block breaking, everything client-side."),
                Component.literal("Resets to this value on each toggle.")
            )
            .setSaveConsumer(v -> ModConfig.CLIENT.defaultTimer.set(v))
            .build());

        timer.addEntry(entry
            .startDoubleField(Component.literal("Timer Scroll Increment"), ModConfig.getTimerScrollIncrement())
            .setDefaultValue(0.25).setMin(0.01).setMax(5.0)
            .setTooltip(Component.literal("Amount added/subtracted per scroll tick while holding the timer keybind."))
            .setSaveConsumer(v -> ModConfig.CLIENT.timerScrollIncrement.set(v))
            .build());

        // Accelerator
        ConfigCategory accel = builder.getOrCreateCategory(Component.literal("Accelerator"));

        accel.addEntry(entry
            .startDoubleField(Component.literal("Small Acceleration (blocks/tick)"), ModConfig.getSmallAcceleration())
            .setDefaultValue(3.0).setMin(0.1).setMax(200.0)
            .setTooltip(Component.literal("Velocity applied by the small acceleration keybind."))
            .setSaveConsumer(v -> ModConfig.CLIENT.smallAcceleration.set(v))
            .build());

        accel.addEntry(entry
            .startDoubleField(Component.literal("Large Acceleration (blocks/tick)"), ModConfig.getLargeAcceleration())
            .setDefaultValue(15.0).setMin(0.1).setMax(200.0)
            .setTooltip(Component.literal("Velocity applied by the large acceleration keybind."))
            .setSaveConsumer(v -> ModConfig.CLIENT.largeAcceleration.set(v))
            .build());

        // No Slowdown
        ConfigCategory noSlow = builder.getOrCreateCategory(Component.literal("No Slowdown"));

        noSlow.addEntry(entry
            .startBooleanToggle(Component.literal("No Block Slowdown"), ModConfig.isNoSlowdownBlockEnabled())
            .setDefaultValue(false)
            .setTooltip(
                Component.literal("Prevents slowdown from cobwebs, honey blocks, slime blocks, soul sand, berry bushes."),
                Component.literal("Also works with most modded blocks using vanilla slow mechanisms.")
            )
            .setSaveConsumer(v -> ModConfig.CLIENT.noSlowdownBlock.set(v))
            .build());

        noSlow.addEntry(entry
            .startBooleanToggle(Component.literal("No Item Slowdown"), ModConfig.isNoSlowdownItemEnabled())
            .setDefaultValue(false)
            .setTooltip(Component.literal("Prevents slowdown while eating, drinking, drawing bows/crossbows, blocking with shields, etc."))
            .setSaveConsumer(v -> ModConfig.CLIENT.noSlowdownItem.set(v))
            .build());

        // No Debuff
        ConfigCategory noDebuff = builder.getOrCreateCategory(Component.literal("No Debuff"));

        noDebuff.addEntry(entry
            .startBooleanToggle(Component.literal("No Blindness"), ModConfig.isNoDebuffBlindnessEnabled())
            .setDefaultValue(false)
            .setTooltip(Component.literal("Cancel the Blindness effect's black fog overlay. Effect still shows in inventory."))
            .setSaveConsumer(v -> ModConfig.CLIENT.noDebuffBlindness.set(v))
            .build());

        noDebuff.addEntry(entry
            .startBooleanToggle(Component.literal("No Darkness"), ModConfig.isNoDebuffDarknessEnabled())
            .setDefaultValue(false)
            .setTooltip(Component.literal("Cancel the Darkness effect's pulsing dim overlay from Sculk Shriekers."))
            .setSaveConsumer(v -> ModConfig.CLIENT.noDebuffDarkness.set(v))
            .build());

        noDebuff.addEntry(entry
            .startBooleanToggle(Component.literal("No Nausea"), ModConfig.isNoDebuffNauseaEnabled())
            .setDefaultValue(false)
            .setTooltip(Component.literal("Cancel the Nausea effect's screen wobble and distortion."))
            .setSaveConsumer(v -> ModConfig.CLIENT.noDebuffNausea.set(v))
            .build());

        noDebuff.addEntry(entry
            .startBooleanToggle(Component.literal("Reveal Invisible Entities"), ModConfig.isNoDebuffInvisibilityEnabled())
            .setDefaultValue(false)
            .setTooltip(
                Component.literal("Makes invisible entities fully visible."),
                Component.literal("A white bounding box is drawn around them as a visual indicator.")
            )
            .setSaveConsumer(v -> ModConfig.CLIENT.noDebuffInvisibility.set(v))
            .build());

        noDebuff.addEntry(entry
            .startBooleanToggle(Component.literal("Disable Night Vision"), ModConfig.isNoDebuffNightVisionEnabled())
            .setDefaultValue(false)
            .setTooltip(
                Component.literal("Disables Night Vision effect rendering."),
                Component.literal("Useful if you have permanent night vision and dislike the bright sky."),
                Component.literal("Light renders as if you had no night vision.")
            )
            .setSaveConsumer(v -> ModConfig.CLIENT.noDebuffNightVision.set(v))
            .build());

        // Crossbow Autoclicker
        ConfigCategory crossbow = builder.getOrCreateCategory(Component.literal("Auto Crossbow"));

        crossbow.addEntry(entry
            .startIntField(Component.literal("Shot Delay (ticks)"), ModConfig.getCrossbowShotDelay())
            .setDefaultValue(0).setMin(0).setMax(10)
            .setTooltip(
                Component.literal("Ticks to wait between crossbow shots when Auto Crossbow is active."),
                Component.literal("0 = fire as fast as possible. 10 = half a second between shots.")
            )
            .setSaveConsumer(v -> ModConfig.CLIENT.crossbowShotDelay.set(v))
            .build());

        // Entity Reach
        ConfigCategory reach = builder.getOrCreateCategory(Component.literal("Entity Reach"));

        reach.addEntry(entry
            .startBooleanToggle(Component.literal("Enable Extended Reach"), ModConfig.isEntityReachEnabled())
            .setDefaultValue(false)
            .setTooltip(Component.literal("Enable extended entity interaction and attack reach."))
            .setSaveConsumer(v -> ModConfig.CLIENT.entityReachEnabled.set(v))
            .build());

        reach.addEntry(entry
            .startDoubleField(Component.literal("Reach Distance (blocks)"), ModConfig.getEntityReachDistance())
            .setDefaultValue(3.0).setMin(3.0).setMax(10.0)
            .setTooltip(
                Component.literal("How far you can attack and interact with entities."),
                Component.literal("Vanilla survival default is 3.0 blocks."),
                Component.literal("Cannot be set below vanilla default.")
            )
            .setSaveConsumer(v -> ModConfig.CLIENT.entityReachDistance.set(v))
            .build());

        // Step
        ConfigCategory step = builder.getOrCreateCategory(Component.literal("Step"));

        step.addEntry(entry
            .startDoubleField(Component.literal("Step Height (blocks)"), ModConfig.getStepHeight())
            .setDefaultValue(1.0).setMin(0.6).setMax(10.0)
            .setTooltip(
                Component.literal("Max block height walked up without jumping while Step is on."),
                Component.literal("Vanilla is 0.6 (slabs), horses are 1.0 (full blocks).")
            )
            .setSaveConsumer(v -> ModConfig.CLIENT.stepHeight.set(v))
            .build());

        // Auto Clicker
        ConfigCategory clicker = builder.getOrCreateCategory(Component.literal("Auto Clicker"));

        clicker.addEntry(entry
            .startEnumSelector(Component.literal("Cooldown Mode"), ModConfig.AutoClickerMode.class, ModConfig.getAutoClickerMode())
            .setDefaultValue(ModConfig.AutoClickerMode.CLIENT)
            .setTooltip(
                Component.literal("CLIENT: attack as soon as your cooldown is full."),
                Component.literal("SERVER: also wait for the server's cooldown. Use on laggy servers/realms.")
            )
            .setSaveConsumer(v -> ModConfig.CLIENT.autoClickerMode.set(v))
            .build());

        clicker.addEntry(entry
            .startIntField(Component.literal("Extra Delay (ticks)"), ModConfig.getAutoClickerDelay())
            .setDefaultValue(0).setMin(0).setMax(100)
            .setTooltip(
                Component.literal("Extra ticks to wait after the cooldown is up before clicking."),
                Component.literal("0 = click immediately. 20 ticks = 1 second.")
            )
            .setSaveConsumer(v -> ModConfig.CLIENT.autoClickerDelay.set(v))
            .build());

        // Criticals
        ConfigCategory crits = builder.getOrCreateCategory(Component.literal("Criticals"));

        crits.addEntry(entry
            .startBooleanToggle(Component.literal("Enable Criticals"), ModConfig.isCriticalsEnabled())
            .setDefaultValue(false)
            .setTooltip(
                Component.literal("Make every grounded attack on a living entity a critical hit."),
                Component.literal("Sprinting hits still don't crit.")
            )
            .setSaveConsumer(v -> ModConfig.CLIENT.criticalsEnabled.set(v))
            .build());

        return builder.build();
    }
}