package tb;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

public class TriggerBot implements ClientModInitializer {
    private static KeyBinding menuKey;
    private int shiftTicks = 0;
    private boolean isUnshiftedForAttack = false;

    @Override
    public void onInitializeClient() {
        // Регистрация клавиши открытия меню (Правый Alt) через конструктор с байндингом
        menuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.triggerbot.menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_ALT,
                "category.triggerbot.general"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;

            // Открытие меню по нажатию клавиши
            while (menuKey.wasPressed()) {
                client.setScreen(new TriggerBotScreen(client.currentScreen));
            }

            if (!TriggerBotConfig.enabled) return;

            // Проверяем, кто находится под прицелом
            HitResult hit = client.crosshairTarget;
            if (hit != null && hit.getType() == HitResult.Type.ENTITY) {
                Entity target = ((EntityHitResult) hit).getEntity();

                if (target instanceof LivingEntity && target.isAlive()) {
                    // Логика Smart Shift (сброс шифта перед ударом для макс. урона / отброса)
                    if (TriggerBotConfig.smartShift && client.player.isSneaking()) {
                        client.player.setSneaking(false);
                        isUnshiftedForAttack = true;
                        shiftTicks = 2; // Возвращаем шифт обратно через 2 тика
                    }

                    // Симулируем удар (левый клик)
                    if (client.interactionManager != null && client.player.getAttackCooldownProgress(0.5f) >= 1.0f) {
                        client.interactionManager.attackEntity(client.player, target);
                        client.player.swingHand(net.minecraft.util.Hand.MAIN_HAND);
                    }
                }
            }

            // Возвращаем шифт обратно после удара
            if (isUnshiftedForAttack) {
                if (shiftTicks > 0) {
                    shiftTicks--;
                } else {
                    client.player.setSneaking(true);
                    isUnshiftedForAttack = false;
                }
            }
        });
    }
}
