package com.yungnickyoung.minecraft.bettercaves.event;

import com.yungnickyoung.minecraft.bettercaves.config.Settings;
import com.yungnickyoung.minecraft.bettercaves.world.MapGenBetterCaves;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.lang.reflect.Field;

/**
 * Replaces vanilla cave generation with Better Caves cave generation.
 * Should be registered to the {@code TERRAIN_GEN_BUS}.
 */
public class EventBetterCaveGen {
    /**
     * Replaces cave gen events with Better Caves cave gen
     * @param event Map generation event
     */
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onCaveEvent(InitMapGenEvent event) {
        // Only replace cave gen if the original gen passed isn't a Better Cave
        if (event.getType() == InitMapGenEvent.EventType.CAVE && !event.getOriginalGen().getClass().equals(MapGenBetterCaves.class)) {
            try {
                int dimensionID = (int) getFieldByType(event.getOriginalGen().getClass(), World.class).get(event.getOriginalGen());
                Settings.LOGGER.info("FOUND DIMENSION ID: " + dimensionID);
            } catch (IllegalAccessException e) {
                Settings.LOGGER.error("ERROR getting dimension ID: " + e);
            } catch (NullPointerException e) {
                Settings.LOGGER.error("ERROR getting dimension ID: " + e);
            }

            event.setNewGen(new MapGenBetterCaves());
        }
    }

    /**
     * Returns the first Field found of a specified type in a class.
     * The field does not have to be public.
     * Also checks all superclasses.
     * @param clazz
     * @param desiredType
     * @return the first Field of the deisred type, or null if no such Field found.
     */
    private Field getFieldByType(Class<?> clazz, Class<?> desiredType) {
        Class<?> currClass = clazz;
        Field[] classFields;

        // Check class and all superclasses until null
        while (currClass != null) {
            classFields = currClass.getDeclaredFields();
            Settings.LOGGER.info("Trying class: " + currClass.getName());
            for (Field field : classFields) {
                Settings.LOGGER.info("-> Found field: " + field.getName() + " - " + field.getType().getName());
                field.setAccessible(true);
                if (field.getType() == desiredType) {
                    Settings.LOGGER.info("Found field of type " + desiredType);
                    return field;
                }
            }
            currClass = currClass.getSuperclass();
        }

        return null;
    }
}
