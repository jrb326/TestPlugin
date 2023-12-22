package me.x1xx.testplugin;

import com.meteoritepvp.api.MeteoritePlugin;
import me.x1xx.testplugin.command.ColorCommand;

import java.util.Arrays;
import java.util.List;

public final class TestPlugin extends MeteoritePlugin {

    @Override
    public void onRegisterMainCommand(String description) {}

    @Override
    protected void onRegisterCommands(String... aliases) {
        // Register the main default command
        super.onRegisterCommands("colors");
        // Register a /c alias
        super.onRegisterCommands("c");
    }

    @Override
    public void onInit() {
        // Plugin initialization


        // Instantiate the command object
        ColorCommand command = new ColorCommand(this);

        // Register the command
        registerCommandObject(command);


        registerPlaceholderParameter("colors", (sender) -> getColors());

    }


    public List<String> getColors() {
        return Arrays.asList("Black", "White", "Red", "Orange", "Yellow", "Green", "Blue", "Purple", "Pink", "Brown", "Gray", "Light Gray", "Cyan", "Lime", "Magenta", "Light Blue");
    }
}
