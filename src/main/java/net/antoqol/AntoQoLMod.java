package net.antoqol;

import net.antoqol.config.ModConfig;
import net.antoqol.events.ClientEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(AntoQoLMod.MOD_ID)
public class AntoQoLMod {

    public static final String MOD_ID = "antoqol";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public AntoQoLMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerConfig(Type.CLIENT, ModConfig.CLIENT_SPEC, "antoqol-client.toml");

        modBus.addListener(ClientEventHandler::onRegisterKeyMappings);

        modBus.addListener(this::clientSetup);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        
        MinecraftForge.EVENT_BUS.register(ClientEventHandler.class);

        LOGGER.info("AntoQoL client setup complete.");
    }
}