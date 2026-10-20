package net.antoqol.events;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.antoqol.config.ModConfig;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.ArrayList;

@OnlyIn(Dist.CLIENT)
public class ClientEventHandler {

    // Keybind category

    private static final String CATEGORY = "key.categories.antoqol";

    // Keybinds

    public static KeyMapping keySpeed;
    public static KeyMapping keyTimer;
    public static KeyMapping keySmallAccel;
    public static KeyMapping keyLargeAccel;
    public static KeyMapping keyFreeFly;
    public static KeyMapping keyAutoCrossbow;
    public static KeyMapping keyStep;
    public static KeyMapping keyAutoClicker;

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        keySpeed = new KeyMapping("key.antoqol.speed", GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
        keyTimer = new KeyMapping("key.antoqol.timer", GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
        keySmallAccel = new KeyMapping("key.antoqol.small_accel", GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
        keyLargeAccel = new KeyMapping("key.antoqol.large_accel", GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
        keyFreeFly = new KeyMapping("key.antoqol.free_fly", GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
        keyAutoCrossbow = new KeyMapping("key.antoqol.auto_crossbow", GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
        keyStep = new KeyMapping("key.antoqol.step", GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
        keyAutoClicker = new KeyMapping("key.antoqol.auto_clicker", GLFW.GLFW_KEY_UNKNOWN, CATEGORY);

        event.register(keySpeed);
        event.register(keyTimer);
        event.register(keySmallAccel);
        event.register(keyLargeAccel);
        event.register(keyFreeFly);
        event.register(keyAutoCrossbow);
        event.register(keyStep);
        event.register(keyAutoClicker);
    }

    // Speed state
    private static boolean speedActive = false;
    private static double currentSpeed = 1.0;
    private static double vanillaWalkSpeed = 0.1;

    // Timer state
    private static boolean timerActive = false;
    private static double currentTimer = 1.0;

    public static boolean isTimerActive() { return timerActive; }
    public static double getCurrentTimer() { return currentTimer; }

    // Free Fly state
    private static boolean freeFlyActive = false;
    private static final double FLY_SPEED = 0.2;

    // Crossbow Autoclicker state
    private static boolean autoCrossbowActive = false;
    private static int crossbowChargeNeeded  = 0;

    // Step state
    private static boolean stepActive = false;

    // Auto Clicker state
    private static boolean autoClickerActive = false;
    private static long lastClickNanos = 0;
    private static long lastServerTime = -1, lastServerTimeNanos = 0;
    private static double serverTps = 20.0;

    // Toggle key edge-detection state
    private static boolean speedKeyWasDown = false;
    private static boolean timerKeyWasDown = false;
    private static boolean freeFlyKeyWasDown = false;
    private static boolean crossbowKeyWasDown = false;
    private static boolean stepKeyWasDown = false;
    private static boolean autoClickerKeyWasDown = false;

    // Main client tick
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        LocalPlayer player = mc.player;

        // Keep running with a screen open so you don't drop out of the air in chat/inventory
        handleSpeedTick(player);
        handleFreeFlyTick(player);
        handleReachTick(player);
        handleStepTick(player);

        if (mc.screen != null) return;

        boolean speedKeyDown     = keySpeed != null && keySpeed.isDown();
        boolean timerKeyDown     = keyTimer != null && keyTimer.isDown();
        boolean freeFlyKeyDown   = keyFreeFly != null && keyFreeFly.isDown();
        boolean crossbowKeyDown  = keyAutoCrossbow != null && keyAutoCrossbow.isDown();
        boolean stepKeyDown      = keyStep != null && keyStep.isDown();
        boolean clickerKeyDown   = keyAutoClicker != null && keyAutoClicker.isDown();

        if (speedKeyDown && !speedKeyWasDown) {
            while (keySpeed.consumeClick()) {}
            if (speedActive) disableSpeed(player); else enableSpeed(player);
            showSpeed(mc);
        }
        if (timerKeyDown && !timerKeyWasDown) {
            while (keyTimer.consumeClick()) {}
            if (timerActive) disableTimer(); else enableTimer();
            showTimer(mc);
        }
        if (freeFlyKeyDown && !freeFlyKeyWasDown) {
            while (keyFreeFly.consumeClick()) {}
            if (freeFlyActive) disableFreeFly(player); else enableFreeFly(player);
            showStatus(mc, "FreeFly", freeFlyActive, "ON");
        }
        if (crossbowKeyDown && !crossbowKeyWasDown) {
            while (keyAutoCrossbow.consumeClick()) {}
            toggleAutoCrossbow(player, mc);
            showStatus(mc, "AutoCrossbow", autoCrossbowActive, "ON");
        }
        if (stepKeyDown && !stepKeyWasDown) {
            while (keyStep.consumeClick()) {}
            stepActive = !stepActive;
            showStatus(mc, "Step", stepActive, String.format("%.1f blocks", ModConfig.getStepHeight()));
        }
        if (clickerKeyDown && !autoClickerKeyWasDown) {
            while (keyAutoClicker.consumeClick()) {}
            autoClickerActive = !autoClickerActive;
            showStatus(mc, "AutoClicker", autoClickerActive, ModConfig.getAutoClickerMode().name());
        }

        speedKeyWasDown    = speedKeyDown;
        timerKeyWasDown    = timerKeyDown;
        freeFlyKeyWasDown  = freeFlyKeyDown;
        crossbowKeyWasDown = crossbowKeyDown;
        stepKeyWasDown     = stepKeyDown;
        autoClickerKeyWasDown = clickerKeyDown;

        handleAcceleratorTick(player);
        handleAutoCrossbowTick(player, mc);
        handleAutoClickerTick(player, mc);
    }

    // Speed
    private static void handleSpeedTick(LocalPlayer player) {
        if (!speedActive) return;
        var attr = player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED);
        if (attr == null) return;
        attr.setBaseValue(vanillaWalkSpeed * currentSpeed);
    }

    private static void enableSpeed(LocalPlayer player) {
        vanillaWalkSpeed = 0.1;
        currentSpeed = ModConfig.getDefaultSpeed();
        speedActive = true;
    }

    private static void disableSpeed(LocalPlayer player) {
        speedActive = false;
        var attr = player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED);
        if (attr != null) attr.setBaseValue(vanillaWalkSpeed);
    }

    // Timer
    private static void enableTimer() {
        currentTimer = ModConfig.getDefaultTimer();
        timerActive = true;
    }

    private static void disableTimer() {
        timerActive = false;
    }

    // Accelerator
    private static void handleAcceleratorTick(LocalPlayer player) {
        if (keySmallAccel != null && keySmallAccel.consumeClick())
            applyAcceleration(player, ModConfig.getSmallAcceleration());
        if (keyLargeAccel != null && keyLargeAccel.consumeClick())
            applyAcceleration(player, ModConfig.getLargeAcceleration());
    }

    private static void applyAcceleration(LocalPlayer player, double speed) {
        player.setDeltaMovement(player.getLookAngle().scale(speed));
        player.hurtMarked = true;
    }

    // Free Fly
    private static void handleFreeFlyTick(LocalPlayer player) {
        if (!freeFlyActive) return;
        Minecraft mc = Minecraft.getInstance();

        double newY = 0.0;
        if (mc.options.keyJump.isDown())  newY =  FLY_SPEED;
        else if (mc.options.keyShift.isDown()) newY = -FLY_SPEED;

        float yawRad = (float) Math.toRadians(player.getYRot());
        double fwdX = -Math.sin(yawRad), fwdZ = Math.cos(yawRad);
        double strafeX = Math.cos(yawRad), strafeZ = Math.sin(yawRad);
        double newX = 0, newZ = 0;

        if (mc.options.keyUp.isDown())    { newX += fwdX * FLY_SPEED;    newZ += fwdZ * FLY_SPEED; }
        if (mc.options.keyDown.isDown())  { newX -= fwdX * FLY_SPEED;    newZ -= fwdZ * FLY_SPEED; }
        if (mc.options.keyLeft.isDown())  { newX += strafeX * FLY_SPEED; newZ += strafeZ * FLY_SPEED; }
        if (mc.options.keyRight.isDown()) { newX -= strafeX * FLY_SPEED; newZ -= strafeZ * FLY_SPEED; }

        player.setDeltaMovement(newX, newY, newZ);
        player.fallDistance = 0.0f;
        player.hurtMarked = true;
        // Fall damage is server-side: report on-ground so the server resets its fall distance
        player.connection.send(new ServerboundMovePlayerPacket.StatusOnly(true));
    }

    private static void enableFreeFly(LocalPlayer player) {
        freeFlyActive = true;
        player.setDeltaMovement(0, 0, 0);
    }

    private static void disableFreeFly(LocalPlayer player) {
        freeFlyActive = false;
    }

    // Crossbow Autoclicker
    private static int crossbowTickCounter = 0;

    private static void toggleAutoCrossbow(LocalPlayer player, Minecraft mc) {
        if (autoCrossbowActive) {
            stopAutoCrossbow(mc);
        } else if (crossbowHand(player) != null) {
            autoCrossbowActive = true;
            crossbowTickCounter = 0;
        }
    }

    private static void stopAutoCrossbow(Minecraft mc) {
        autoCrossbowActive = false;
        crossbowTickCounter = 0;
        mc.options.keyUse.setDown(false);
    }

    private static InteractionHand crossbowHand(LocalPlayer player) {
        if (player.getMainHandItem().getItem() instanceof CrossbowItem) return InteractionHand.MAIN_HAND;
        if (player.getOffhandItem().getItem() instanceof CrossbowItem) return InteractionHand.OFF_HAND;
        return null;
    }

    // Loop: start charging -> release once fully charged (loads) -> fire after the shot delay
    private static void handleAutoCrossbowTick(LocalPlayer player, Minecraft mc) {
        if (!autoCrossbowActive) return;

        InteractionHand hand = crossbowHand(player);
        if (hand == null) {
            stopAutoCrossbow(mc);
            showStatus(mc, "AutoCrossbow", false, "");
            return;
        }
        ItemStack stack = player.getItemInHand(hand);

        if (CrossbowItem.isCharged(stack)) {
            if (crossbowTickCounter <= 0) {
                mc.gameMode.useItem(player, hand);
                crossbowTickCounter = ModConfig.getCrossbowShotDelay();
            } else {
                crossbowTickCounter--;
            }
        } else if (!player.isUsingItem()) {
            mc.gameMode.useItem(player, hand);
        } else if (player.getTicksUsingItem() > CrossbowItem.getChargeDuration(stack)) {
            // ponytail: one tick past full charge so the server's count also reaches it; raise if loads fail
            mc.gameMode.releaseUsingItem(player);
        }

        // Vanilla releases the item whenever the use key is up, so hold it while charging
        mc.options.keyUse.setDown(player.isUsingItem());
    }

    // Auto Clicker: attacks the entity under the crosshair once the cooldown is full.
    // SERVER mode also waits until the server, running at its measured TPS, has ticked a full cooldown since
    // the last click, so hits on a lagging server aren't sent early and weakened.
    private static void handleAutoClickerTick(LocalPlayer player, Minecraft mc) {
        if (!autoClickerActive || mc.hitResult == null || mc.hitResult.getType() != HitResult.Type.ENTITY) return;
        if (player.getAttackStrengthScale(0.0f) < 1.0f) return;
        if (ModConfig.getAutoClickerMode() == ModConfig.AutoClickerMode.SERVER) {
            double serverTicks = (System.nanoTime() - lastClickNanos) / 1e9 * serverTps;
            if (serverTicks < player.getCurrentItemAttackStrengthDelay()) return;
        }
        // Queued click: vanilla's keybind handling performs the attack next tick, same as a real click
        KeyMapping.click(mc.options.keyAttack.getKey());
        lastClickNanos = System.nanoTime();
    }

    // Called by MixinServerTime on each time sync (every 20 server ticks); measures the server's real TPS
    public static void onServerTime(long gameTime) {
        long now = System.nanoTime();
        if (lastServerTime >= 0 && gameTime > lastServerTime) {
            double tps = (gameTime - lastServerTime) / ((now - lastServerTimeNanos) / 1e9);
            serverTps = Math.max(1.0, Math.min(20.0, tps));
        }
        lastServerTime = gameTime;
        lastServerTimeNanos = now;
    }

    // Entity Reach: Forge's reach attribute drives vanilla targeting, so walls and invalid targets are respected.
    // The server still rejects hits beyond ~6 blocks to the hitbox.
    private static void handleReachTick(LocalPlayer player) {
        var attr = player.getAttribute(ForgeMod.ENTITY_REACH.get());
        if (attr == null) return;
        attr.setBaseValue(ModConfig.isEntityReachEnabled() ? ModConfig.getEntityReachDistance() : 3.0);
    }

    // Step: Forge's step height attribute adds to vanilla's 0.6, so blocks are walked up like a horse, no jump
    private static void handleStepTick(LocalPlayer player) {
        var attr = player.getAttribute(ForgeMod.STEP_HEIGHT_ADDITION.get());
        if (attr == null) return;
        attr.setBaseValue(stepActive ? ModConfig.getStepHeight() - player.maxUpStep() : 0.0);
    }

    // Invisible entity bounding box rendering
    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;
        if (!ModConfig.isNoDebuffInvisibilityEnabled()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        // Get all entities in the level
        List<Entity> entities = new ArrayList<>();
        for (Entity e : mc.level.entitiesForRendering()) {
            if (e.isInvisible() && e != mc.player) {
                entities.add(e);
            }
        }

        if (entities.isEmpty()) return;

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource =
            mc.renderBuffers().bufferSource();

        Vec3 cam = event.getCamera().getPosition();

        VertexConsumer lines = bufferSource.getBuffer(RenderType.lines());

        for (Entity entity : entities) {
            AABB box = entity.getBoundingBox();
            double x = box.minX - cam.x;
            double y = box.minY - cam.y;
            double z = box.minZ - cam.z;
            double maxX = box.maxX - cam.x;
            double maxY = box.maxY - cam.y;
            double maxZ = box.maxZ - cam.z;

            LevelRenderer.renderLineBox(
                poseStack,
                lines,
                box.move(-cam.x, -cam.y, -cam.z),
                1.0F, 1.0F, 1.0F, 1.0F
            );
        }

        bufferSource.endBatch(RenderType.lines());
    }

    // Scroll wheel
    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;

        double delta = event.getScrollDelta();
        if (keySpeed != null && keySpeed.isDown() && speedActive) {
            currentSpeed += delta * ModConfig.getSpeedScrollIncrement();
            if (currentSpeed < 0.1) currentSpeed = 0.1;
            event.setCanceled(true);
            showSpeed(mc);
        }

        if (keyTimer != null && keyTimer.isDown() && timerActive) {
            currentTimer += delta * ModConfig.getTimerScrollIncrement();
            if (currentTimer < 0.1) currentTimer = 0.1;
            event.setCanceled(true);
            showTimer(mc);
        }
    }

    // Action bar: shows only the module that just changed
    private static void showSpeed(Minecraft mc) {
        showStatus(mc, "Speed", speedActive, String.format("%.2fx", currentSpeed));
    }

    private static void showTimer(Minecraft mc) {
        showStatus(mc, "Timer", timerActive, String.format("%.2fx", currentTimer));
    }

    private static void showStatus(Minecraft mc, String name, boolean on, String detail) {
        if (mc.player == null) return;
        mc.player.displayClientMessage(
            net.minecraft.network.chat.Component.literal(on ? "§a" + name + " §f" + detail : "§7" + name + " §cOFF"),
            true);
    }
}
