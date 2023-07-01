package lt.bongibau.holograms.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLib;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import lt.bongibau.holograms.HologramsPlugin;
import lt.bongibau.holograms.PlaceholderAPReplacements;
import lt.bongibau.holograms.api.Hologram;
import lt.bongibau.holograms.managers.HologramManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EntityMetaDataPacketAdapter extends PacketAdapter {
    public EntityMetaDataPacketAdapter() {
        super(HologramsPlugin.getInstance(), ListenerPriority.HIGH, PacketType.Play.Server.ENTITY_METADATA);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onPacketSending(PacketEvent event) {
        PacketContainer packet = event.getPacket();

        int entityId = packet.getIntegers().read(0);

        Hologram hologram = HologramManager.getInstance().getHologram(entityId);

        if (hologram != null) {
            StructureModifier<List<WrappedDataValue>> watchableAccessor = packet.getDataValueCollectionModifier();
            List<WrappedDataValue> watchableObjects = watchableAccessor.read(0);

            List<WrappedDataValue> copyList = watchableObjects.stream().filter(wrappedDataValue -> {
                return wrappedDataValue.getIndex() != 2;
            }).toList();

            Optional<?> opt = Optional
                    .of(WrappedChatComponent
                            .fromChatMessage(
                                    HologramsPlugin.isIsPlaceholderAPIPresent() ?
                                            PlaceholderAPReplacements.replace(event.getPlayer(), hologram.getText(entityId)) :
                                            hologram.getText(entityId).replace("%player_name%", event.getPlayer().getName())
                            )[0].getHandle());

            List<WrappedDataValue> values = Lists.newArrayList(
                new WrappedDataValue(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true), opt)
            );

            values.addAll(copyList);

            // Insert and read back
            watchableAccessor.write(0, values);
        }
    }

    public static void sendUpdatePackets(Player player) {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

        for (Hologram hologram : HologramManager.getInstance().getAll()) {
            List<ArmorStand> armorStands = hologram.getArmorStands();

            if (hologram.getLocation().distance(player.getLocation()) > 128) {
                continue;
            }

            for (int i = armorStands.size() - 1; i >= 0; i--) {
                PacketContainer container = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
                container.getIntegers().write(0, armorStands.get(i).getEntityId());

                StructureModifier<List<WrappedDataValue>> watchableAccessor = container.getDataValueCollectionModifier();
                Optional<?> opt = Optional
                        .of(WrappedChatComponent
                                .fromChatMessage(
                                        HologramsPlugin.isIsPlaceholderAPIPresent() ?
                                                PlaceholderAPReplacements.replace(player, hologram.getText(armorStands.get(i).getEntityId())) :
                                                hologram.getText(armorStands.get(i).getEntityId()).replace("%player_name%", player.getName())
                                )[0].getHandle());


                List<WrappedDataValue> values = Lists.newArrayList(
                        new WrappedDataValue(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true), opt)
                );

                watchableAccessor.write(0, values);

                protocolManager.sendServerPacket(
                        player,
                        container
                );
            }
        }
    }
}
