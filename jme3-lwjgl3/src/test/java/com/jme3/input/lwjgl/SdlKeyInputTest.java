package com.jme3.input.lwjgl;

import com.jme3.input.KeyInput;
import com.jme3.input.RawInputListenerAdapter;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.system.lwjgl.LwjglWindow;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.lwjgl.sdl.SDL_Event;
import org.lwjgl.system.Platform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.lwjgl.sdl.SDLEvents.SDL_EVENT_KEY_DOWN;
import static org.lwjgl.sdl.SDLKeycode.SDL_KMOD_NONE;
import static org.lwjgl.sdl.SDLKeycode.SDL_KMOD_NUM;
import static org.lwjgl.sdl.SDLScancode.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SdlKeyInputTest {

    @Test
    public void shouldResolveNumericKeypadCharacters() {
        LwjglWindow context = mock(LwjglWindow.class);
        when(context.getWindowId()).thenReturn(7);
        when(context.isRenderable()).thenReturn(true);

        List<KeyInputEvent> events = new ArrayList<>();
        SdlKeyInput keyInput = new SdlKeyInput(context);
        keyInput.setInputListener(new RawInputListenerAdapter() {
            @Override
            public void onKeyEvent(KeyInputEvent event) {
                events.add(event);
            }
        });

        int[] digitScancodes = {
            SDL_SCANCODE_KP_0,
            SDL_SCANCODE_KP_1,
            SDL_SCANCODE_KP_2,
            SDL_SCANCODE_KP_3,
            SDL_SCANCODE_KP_4,
            SDL_SCANCODE_KP_5,
            SDL_SCANCODE_KP_6,
            SDL_SCANCODE_KP_7,
            SDL_SCANCODE_KP_8,
            SDL_SCANCODE_KP_9
        };
        int[] digitKeyCodes = {
            KeyInput.KEY_NUMPAD0,
            KeyInput.KEY_NUMPAD1,
            KeyInput.KEY_NUMPAD2,
            KeyInput.KEY_NUMPAD3,
            KeyInput.KEY_NUMPAD4,
            KeyInput.KEY_NUMPAD5,
            KeyInput.KEY_NUMPAD6,
            KeyInput.KEY_NUMPAD7,
            KeyInput.KEY_NUMPAD8,
            KeyInput.KEY_NUMPAD9
        };
        for (int i = 0; i < digitScancodes.length; i++) {
            assertKeyEvent(keyInput, events, digitScancodes[i], (short) SDL_KMOD_NUM,
                    digitKeyCodes[i], (char) ('0' + i));
        }

        char numLockOffChar = Platform.get() == Platform.MACOSX ? '7' : '\0';
        assertKeyEvent(keyInput, events, SDL_SCANCODE_KP_7, (short) SDL_KMOD_NONE,
                KeyInput.KEY_NUMPAD7, numLockOffChar);
        assertKeyEvent(keyInput, events, SDL_SCANCODE_KP_PERIOD, (short) SDL_KMOD_NUM,
                KeyInput.KEY_DECIMAL, '.');
        assertKeyEvent(keyInput, events, SDL_SCANCODE_KP_DIVIDE, (short) SDL_KMOD_NONE,
                KeyInput.KEY_DIVIDE, '/');
        assertKeyEvent(keyInput, events, SDL_SCANCODE_KP_MULTIPLY, (short) SDL_KMOD_NONE,
                KeyInput.KEY_MULTIPLY, '*');
        assertKeyEvent(keyInput, events, SDL_SCANCODE_KP_MINUS, (short) SDL_KMOD_NONE,
                KeyInput.KEY_SUBTRACT, '-');
        assertKeyEvent(keyInput, events, SDL_SCANCODE_KP_PLUS, (short) SDL_KMOD_NONE,
                KeyInput.KEY_ADD, '+');
        assertKeyEvent(keyInput, events, SDL_SCANCODE_KP_EQUALS, (short) SDL_KMOD_NONE,
                KeyInput.KEY_NUMPADEQUALS, '=');
        assertKeyEvent(keyInput, events, SDL_SCANCODE_KP_COMMA, (short) SDL_KMOD_NONE,
                KeyInput.KEY_NUMPADCOMMA, ',');
    }

    private static void assertKeyEvent(SdlKeyInput keyInput, List<KeyInputEvent> events,
                                       int scancode, short modifiers, int expectedKeyCode,
                                       char expectedKeyChar) {
        SDL_Event event = SDL_Event.calloc();
        try {
            event.type(SDL_EVENT_KEY_DOWN);
            event.key()
                    .timestamp(123)
                    .windowID(7)
                    .scancode(scancode)
                    .mod(modifiers)
                    .down(true)
                    .repeat(false);

            keyInput.onSDLEvent(event);
            keyInput.update();
        } finally {
            event.free();
        }

        KeyInputEvent actual = events.remove(0);
        assertEquals(expectedKeyCode, actual.getKeyCode());
        assertEquals(expectedKeyChar, actual.getKeyChar());
    }
}
