package de.philippst.alexa.kvb.utils;

import com.amazon.speech.speechlet.Context;
import com.amazon.speech.speechlet.interfaces.display.DisplayInterface;
import com.amazon.speech.speechlet.interfaces.system.SystemInterface;
import com.amazon.speech.speechlet.interfaces.system.SystemState;

public class AlexaSkillKitHelper {

    public static boolean isDisplaySupported(Context context) {
        SystemState systemState = context.getState(SystemInterface.class, SystemState.class);
        return systemState.getDevice().getSupportedInterfaces().isInterfaceSupported(DisplayInterface.class);
    }
}