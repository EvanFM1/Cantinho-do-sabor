package org.example.ui.bindings.key;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

/**
 * Builder para associação de atalhos de teclado
 * via InputMap e ActionMap.
 */
public final class KeyBinder {
    /**
     * Componente associado aos atalhos.
     */
    private final JComponent component;

    /**
     * Construtor.
     *
     * @param component componente alvo
     */
    public KeyBinder(JComponent component) {
        this.component = component;
    }

    /**
     * Associa uma tecla a uma ação.
     *
     * Ex:
     * - "ENTER"
     * - "ctrl S"
     * - "ESCAPE"
     *
     * @param key tecla
     * @param handler ação
     * @return KeyBinder
     */
    public KeyBinder on(
            String key,
            Consumer<ActionEvent> handler
    ) {
        if (handler == null) {
            return this;
        }

        KeyStroke keyStroke =
                KeyStroke.getKeyStroke(key);
        if (keyStroke == null) {
            throw new IllegalArgumentException(
                    "KeyStroke inválido: " + key
            );
        }
        String id = normalizeKey(key);

        /*
         * INPUT MAP
         */
        component
                .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(keyStroke, id);

        /*
         * ACTION MAP
         */
        component
                .getActionMap()
                .put(id, new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        /*
                         * IGNORA EVENTOS
                         * QUANDO COMPONENTE
                         * NÃO ESTIVER VISÍVEL
                         */
                        if (!component.isShowing()) {
                            return;
                        }

                        /*
                         * IGNORA EVENTOS
                         * QUANDO JANELA
                         * NÃO ESTIVER FOCADA
                         */
                        if (!component.isFocusOwner()
                                &&
                                SwingUtilities.getWindowAncestor(component)
                                        != KeyboardFocusManager
                                        .getCurrentKeyboardFocusManager()
                                        .getActiveWindow()) {
                            return;
                        }
                        handler.accept(event);
                    }
                });
        return this;
    }

    /**
     * Overload para Runnable.
     *
     * @param key tecla
     * @param handler ação
     * @return KeyBinder
     */
    public KeyBinder on(
            String key,
            Runnable handler
    ) {

        if (handler == null) {
            return this;
        }
        return on(key, event -> handler.run());
    }

    /**
     * Remove um atalho.
     *
     * @param key tecla
     * @return KeyBinder
     */
    public KeyBinder off(String key) {
        KeyStroke keyStroke =
                KeyStroke.getKeyStroke(key);

        if (keyStroke == null) {
            return this;
        }
        String id = normalizeKey(key);
        component
                .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .remove(keyStroke);
        component
                .getActionMap()
                .remove(id);
        return this;
    }

    /**
     * Normaliza identificador interno.
     *
     * @param key tecla
     * @return id normalizado
     */
    private String normalizeKey(String key) {
        return "KEYBIND@" +
                key.toUpperCase().trim();
    }
}