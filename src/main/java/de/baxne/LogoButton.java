package de.baxne;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/** Logo-Button im ESC-Menü und in den Optionen (öffnet das Baxne's-Crosshair-Menü). */
public final class LogoButton {
    private static final Identifier SPRITE = Identifier.fromNamespaceAndPath("baxnes_crosshair", "logo");
    private LogoButton() {}

    /** Fügt ein Widget zum Screen hinzu (die Methode ist protected, daher per Reflection). */
    private static void addWidget(Screen screen, AbstractWidget w) {
        for (String n : new String[]{"addRenderableWidget", "addWidget"}) {
            for (Class<?> c = screen.getClass(); c != null; c = c.getSuperclass()) {
                for (java.lang.reflect.Method m : c.getDeclaredMethods()) {
                    if (m.getName().equals(n) && m.getParameterCount() == 1 && m.getParameterTypes()[0].isInstance(w)) {
                        try { m.setAccessible(true); m.invoke(screen, w); return; } catch (Throwable ignored) {}
                    }
                }
            }
        }
        System.err.println("[Baxne] Logo-Button konnte nicht hinzugefuegt werden");
    }

    public static void register() {
        ScreenEvents.AFTER_INIT.register((client, screen, w, h) -> {
            boolean pause = screen instanceof PauseScreen;
            if (!pause && !(screen instanceof OptionsScreen)) return;

            int x = w - 26, y = 6;
            if (pause) {
                // links neben den "Optionen..."-Button setzen
                String optionsLabel = Component.translatable("menu.options").getString();
                for (Object o : screen.children()) {
                    if (!(o instanceof AbstractWidget b)) continue;
                    if (b.getMessage().getString().equals(optionsLabel)) { x = b.getX() - 24; y = b.getY(); break; }
                }
            }
            SpriteIconButton btn = SpriteIconButton.builder(Component.literal("Baxne's Crosshair"),
                    b -> Compat.setScreen(new MenuScreen(screen)), true)
                    .width(20)
                    .sprite(SPRITE, 16, 16)
                    .build();
            btn.setPosition(x, y);
            addWidget(screen, btn);
        });
    }
}
