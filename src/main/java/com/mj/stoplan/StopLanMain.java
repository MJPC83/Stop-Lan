package com.mj.stoplan;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = StopLanMain.MODID, name = StopLanMain.NAME, version = StopLanMain.VERSION, acceptableRemoteVersions = "*")
public class StopLanMain {

    public static final String MODID = "stoplan";
    public static final String NAME = "Stop Lan";
    public static final String VERSION = "0.1.b";

    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        logger.info("StopLan is setting up!");
    }

    @EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new StopLanCommand());
    }

}
