package tb;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class TriggerBotScreen extends Screen {
    private final Screen parent;

    public TriggerBotScreen(Screen parent) {
        super(Text.literal("TriggerBot Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Кнопка включения/выключения TriggerBot
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("TriggerBot: " + (TriggerBotConfig.enabled ? "§aON" : "§cOFF")),
                button -> {
                    TriggerBotConfig.enabled = !TriggerBotConfig.enabled;
                    button.setMessage(Text.literal("TriggerBot: " + (TriggerBotConfig.enabled ? "§aON" : "§cOFF")));
                })
                .dimensions(centerX - 100, centerY - 40, 200, 20)
                .build());

        // Кнопка настройки W-Tap (сброс шифта перед ударом)
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Smart Shift Reset: " + (TriggerBotConfig.smartShift ? "§aON" : "§cOFF")),
                button -> {
                    TriggerBotConfig.smartShift = !TriggerBotConfig.smartShift;
                    button.setMessage(Text.literal("Smart Shift Reset: " + (TriggerBotConfig.smartShift ? "§aON" : "§cOFF")));
                })
                .dimensions(centerX - 100, centerY - 10, 200, 20)
                .build());

        // Кнопка закрытия / возврата
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Done"),
                button -> this.client.setScreen(this.parent))
                .dimensions(centerX - 100, centerY + 30, 200, 20)
                .build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 70, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }
}
