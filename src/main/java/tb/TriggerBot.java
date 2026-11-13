package tb;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import java.util.concurrent.ThreadLocalRandom;

public class TriggerBot implements ClientModInitializer {
    private long nextAttackTime = 0L;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity player = client.player;
            if (player == null || client.interactionManager == null || client.currentScreen != null) return;
            if (!(client.crosshairTarget instanceof EntityHitResult hitResult)
                    || !(hitResult.getEntity() instanceof LivingEntity target) || !target.isAlive()) return;
            long now = System.currentTimeMillis();
            if (now < nextAttackTime) return;
            if (canCrit(player)) {
                client.interactionManager.attackEntity(player, target);
                player.swingHand(Hand.MAIN_HAND);
                nextAttackTime = now + ThreadLocalRandom.current().nextLong(50, 121);
            }
        });
    }

    private boolean canCrit(ClientPlayerEntity player) {
        return player.getAttackCooldownProgress(0.5F) >= 0.9F
                && player.fallDistance > 0.0F
                && !player.isOnGround()
                && !player.isClimbing()
                && !player.isTouchingWater()
                && !player.hasStatusEffect(StatusEffects.BLINDNESS)
                && !player.hasVehicle()
                && !player.isSprinting();
    }
}
