package com.jme3.input.lwjgl;

import com.jme3.input.RawInputListenerAdapter;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.system.lwjgl.LwjglWindow;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.lwjgl.sdl.SDL_Event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.lwjgl.sdl.SDLEvents.SDL_EVENT_KEY_DOWN;
import static org.lwjgl.sdl.SDLKeyboard.SDL_GetKeyFromScancode;
import static org.lwjgl.sdl.SDLKeycode.SDL_KMOD_CAPS;
import static org.lwjgl.sdl.SDLKeycode.SDL_KMOD_LSHIFT;
import static org.lwjgl.sdl.SDLKeycode.SDL_KMOD_NONE;
import static org.lwjgl.sdl.SDLScancode.SDL_SCANCODE_1;
import static org.lwjgl.sdl.SDLScancode.SDL_SCANCODE_A;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SdlKeyInputTest {

    @Test
    public void shouldResolveKeyCharsUsingEventModifiers() {
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

        assertKeyChar(keyInput, events, SDL_SCANCODE_A, (short) SDL_KMOD_NONE);
        assertKeyChar(keyInput, events, SDL_SCANCODE_A, (short) SDL_KMOD_LSHIFT);
        assertKeyChar(keyInput, events, SDL_SCANCODE_A, (short) SDL_KMOD_CAPS);
        assertKeyChar(keyInput, events, SDL_SCANCODE_A, (short) (SDL_KMOD_LSHIFT | SDL_KMOD_CAPS));
        assertKeyChar(keyInput, events, SDL_SCANCODE_1, (short) SDL_KMOD_LSHIFT);
    }

    private static void assertKeyChar(SdlKeyInput keyInput, List<KeyInputEvent> events,
                                      int scancode, short modifiers) {
        int expectedKey = SDL_GetKeyFromScancode(scancode, modifiers, false);
        assertTrue(expectedKey > 0 && expectedKey <= Character.MAX_VALUE);
        assertFalse(Character.isISOControl((char) expectedKey));

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
        assertEquals(SdlKeyMap.toJmeKeyCode(scancode), actual.getKeyCode());
        assertEquals((char) expectedKey, actual.getKeyChar());
        assertTrue(actual.isPressed());
    }
}
