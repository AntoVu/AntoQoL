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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.EventPriority;
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

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        keySpeed = new KeyMapping("key.antoqol.speed", GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
        keyTimer = new KeyMapping("key.antoqol.timer", GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
        keySmallAccel = new KeyMapping("key.antoqol.small_accel", GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
        keyLargeAccel = new KeyMapping("key.antoqol.large_accel", GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
        keyFreeFly = new KeyMapping("key.antoqol.free_fly", GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
        keyAutoCrossbow = new KeyMapping("key.antoqol.auto_crossbow", GLFW.GLFW_KEY_UNKNOWN, CATEGORY);

        event.register(keySpeed);
        event.register(keyTimer);
        event.register(keySmallAccel);
        event.register(keyLargeAccel);
        event.register(keyFreeFly);
        event.register(keyAutoCrossbow);
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

    // Toggle key edge-detection state
    private static boolean speedKeyWasDown = false;
    private static boolean timerKeyWasDown = false;
    private static boolean freeFlyKeyWasDown = false;
    private static boolean crossbowKeyWasDown = false;

    // Main client tick
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        if (mc.screen != null) return;
        LocalPlayer player = mc.player;

        boolean speedKeyDown     = keySpeed != null && keySpeed.isDown();
        boolean timerKeyDown     = keyTimer != null && keyTimer.isDown();
        boolean freeFlyKeyDown   = keyFreeFly != null && keyFreeFly.isDown();
        boolean crossbowKeyDown  = keyAutoCrossbow != null && keyAutoCrossbow.isDown();

        if (speedKeyDown && !speedKeyWasDown) {
            while (keySpeed.consumeClick()) {}
            if (speedActive) disableSpeed(player); else enableSpeed(player);
            showHUD(mc);
        }
        if (timerKeyDown && !timerKeyWasDown) {
            while (keyTimer.consumeClick()) {}
            if (timerActive) disableTimer(); else enableTimer();
            showHUD(mc);
        }
        if (freeFlyKeyDown && !freeFlyKeyWasDown) {
            while (keyFreeFly.consumeClick()) {}
            if (freeFlyActive) disableFreeFly(player); else enableFreeFly(player);
            showHUD(mc);
        }
        if (crossbowKeyDown && !crossbowKeyWasDown) {
            while (keyAutoCrossbow.consumeClick()) {}
            toggleAutoCrossbow(player, mc);
            showHUD(mc);
        }

        speedKeyWasDown    = speedKeyDown;
        timerKeyWasDown    = timerKeyDown;
        freeFlyKeyWasDown  = freeFlyKeyDown;
        crossbowKeyWasDown = crossbowKeyDown;

        handleSpeedTick(player);
        handleAcceleratorTick(player);
        handleFreeFlyTick(player);
        handleAutoCrossbowTick(player, mc);
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
            autoCrossbowActive = false;
        } else {
            ItemStack main = player.getMainHandItem();
            ItemStack off  = player.getOffhandItem();
            boolean holdingCrossbow =
                (!main.isEmpty() && main.getItem().getUseAnimation(main) == net.minecraft.world.item.UseAnim.CROSSBOW) ||
                (!off.isEmpty()  && off.getItem().getUseAnimation(off)   == net.minecraft.world.item.UseAnim.CROSSBOW);

            if (holdingCrossbow) {
                autoCrossbowActive = true;
                crossbowTickCounter = 0;
            }
        }
    }

    private static void handleAutoCrossbowTick(LocalPlayer player, Minecraft mc) {
        if (!autoCrossbowActive) return;

        // Auto disable if no crossbow in either hand
        ItemStack main = player.getMainHandItem();
        ItemStack off  = player.getOffhandItem();
        boolean holdingCrossbow =
            (!main.isEmpty() && main.getItem().getUseAnimation(main) == net.minecraft.world.item.UseAnim.CROSSBOW) ||
            (!off.isEmpty()  && off.getItem().getUseAnimation(off)   == net.minecraft.world.item.UseAnim.CROSSBOW);

        if (!holdingCrossbow) {
            autoCrossbowActive = false;
            crossbowTickCounter = 0;
            showHUD(mc);
            return;
        }

        if (crossbowTickCounter <= 0) {
            net.minecraft.world.InteractionHand hand =
                (!main.isEmpty() && main.getItem().getUseAnimation(main) == net.minecraft.world.item.UseAnim.CROSSBOW)
                    ? net.minecraft.world.InteractionHand.MAIN_HAND
                    : net.minecraft.world.InteractionHand.OFF_HAND;

            mc.gameMode.useItem(player, hand);
            crossbowTickCounter = ModConfig.getCrossbowShotDelay(); // your 1-10 tick config value
        } else {
            crossbowTickCounter--;
        }
    }

    // Entity Reach
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onAttackEntity(AttackEntityEvent event) {
        // Already handled by vanilla reach — nothing to do
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onMouseClick(InputEvent.InteractionKeyMappingTriggered event) {
        if (!ModConfig.isEntityReachEnabled()) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        LocalPlayer player = mc.player;

        double reach = ModConfig.getEntityReachDistance();
        Vec3 eyePos = player.getEyePosition(1.0f);
        Vec3 endPos = eyePos.add(player.getLookAngle().scale(reach));

        net.minecraft.world.phys.EntityHitResult hit =
            net.minecraft.world.entity.projectile.ProjectileUtil.getEntityHitResult(
                player, eyePos, endPos,
                new AABB(eyePos, endPos).inflate(1.0),
                e -> !e.isSpectator() && e != player,
                reach * reach
            );

        if (hit == null || hit.getEntity() == null) return;

        Entity target = hit.getEntity();

        if (event.isAttack()) {
            mc.gameMode.attack(player, target);
            event.setCanceled(true);
        } else if (event.isUseItem()) {
            mc.gameMode.interact(player, target, event.getHand());
            event.setCanceled(true);
        }
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
        boolean handled = false;

        if (keySpeed != null && keySpeed.isDown() && speedActive) {
            currentSpeed += delta * ModConfig.getSpeedScrollIncrement();
            if (currentSpeed < 0.1) currentSpeed = 0.1;
            event.setCanceled(true);
            handled = true;
        }

        if (keyTimer != null && keyTimer.isDown() && timerActive) {
            currentTimer += delta * ModConfig.getTimerScrollIncrement();
            if (currentTimer < 0.1) currentTimer = 0.1;
            event.setCanceled(true);
            handled = true;
        }

        if (handled) showHUD(mc);
    }

    // HUD
    private static void showHUD(Minecraft mc) {
        if (mc.player == null) return;

        String speedStr   = speedActive
            ? String.format("§aSpeed §f%.2fx", currentSpeed) : "§7Speed §cOFF";
        String timerStr   = timerActive
            ? String.format("§aTimer §f%.2fx", currentTimer) : "§7Timer §cOFF";
        String flyStr     = freeFlyActive
            ? "§aFreeFly §fON" : "§7FreeFly §cOFF";
        String crossStr   = autoCrossbowActive
            ? "§aAutoCrossbow §fON" : "§7AutoCrossbow §cOFF";

        mc.player.displayClientMessage(
            net.minecraft.network.chat.Component.literal(
                speedStr + "  " + timerStr + "  " + flyStr + "  " + crossStr),
            true);
    }
}